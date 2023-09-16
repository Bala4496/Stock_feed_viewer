package ua.bala.stock_feed_viewer.domain.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ua.bala.stock_feed_viewer.domain.model.Company;
import ua.bala.stock_feed_viewer.domain.model.Quote;
import ua.bala.stock_feed_viewer.domain.repository.CompanyRepository;
import ua.bala.stock_feed_viewer.domain.repository.QuoteRepository;
import ua.bala.stock_feed_viewer.ports.in.QuoteFetcher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuoteUpdater implements DataUpdater {

    private static final int BATCH_SIZE = 100;
    private final CompanyRepository companyRepository;
    private final QuoteFetcher quoteFetcher;
    private final QuoteRepository quoteRepository;
    private ExecutorService executorService;

    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void update() {
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        var thread = new Thread(new UpdateQuotesTask(), "UpdateQuotes");
        thread.setDaemon(true);
        thread.start();
    }

    private void shutdown() {
        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    class UpdateQuotesTask implements Runnable {

        @Override
        public void run() {
            log.info("Thread - {} : started", Thread.currentThread().getName());
            while (true) {
                processCompaniesInBatches(companyRepository.findAll(), BATCH_SIZE);
            }
        }
    }

    private void processCompaniesInBatches(List<Company> companies, int batchSize) {
        for (int i = 0; i < companies.size(); i += batchSize) {
            List<Company> batch = companies.subList(i, Math.min(i + batchSize, companies.size()));

            List<CompletableFuture<Void>> futures = batch.stream()
                    .map(this::processCompanyAsync)
                    .toList();

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            try {
                allOf.join();
            } catch (Exception e) {
                log.error("Error while updating quotes", e);
            }
        }
    }

    private CompletableFuture<Void> processCompanyAsync(Company company) {
        return CompletableFuture.runAsync(() -> {
            var companyCode = company.getCode();

            var quote = quoteFetcher.fetchQuoteByCompanyCode(companyCode);
            if (quote.isEmpty()) {
                return;
            }

            var fetchedQuote = quote.orElse(null);
            var savedQuote = quoteRepository.findLatestQuoteByCompanyCode(companyCode).orElse(null);
            if (!fetchedQuote.equals(savedQuote)) {
                fetchedQuote.setGapPercentage(calculateGapPercentage(savedQuote, fetchedQuote));
                quoteRepository.save(fetchedQuote);
            }
        }, executorService);
    }

    private BigDecimal calculateGapPercentage(Quote savedQuote, Quote fetchedQuote) {
        var fetchedQuotePrice = fetchedQuote.getPrice();
        if (Objects.isNull(savedQuote)) {
            return fetchedQuotePrice;
        }

        var savedQuotePrice = savedQuote.getPrice();
        if (savedQuotePrice.compareTo(BigDecimal.ZERO) == 0) {
            return new BigDecimal(100);
        }

        return fetchedQuotePrice.subtract(savedQuotePrice)
                .divide(savedQuotePrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
    }
}
