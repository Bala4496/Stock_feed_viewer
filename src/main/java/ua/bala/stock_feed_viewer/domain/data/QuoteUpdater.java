package ua.bala.stock_feed_viewer.domain.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ua.bala.stock_feed_viewer.domain.model.Quote;
import ua.bala.stock_feed_viewer.domain.repository.CompanyRepository;
import ua.bala.stock_feed_viewer.domain.repository.QuoteRepository;
import ua.bala.stock_feed_viewer.ports.in.QuoteFetcher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuoteUpdater implements DataUpdater {

    private final CompanyRepository companyRepository;
    private final QuoteFetcher quoteFetcher;
    private final QuoteRepository quoteRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void update() {
        var thread = new Thread(new UpdateQuotesTask(), "UpdateQuotes");
        thread.setDaemon(true);
        thread.start();
    }

    class UpdateQuotesTask implements Runnable {

        @Override
        public void run() {
            log.info("Thread - {} : started", Thread.currentThread().getName());
            var executorService = Executors.newCachedThreadPool();
            while (true) {
                var futures = companyRepository.findAll().stream()
                        .map(company -> CompletableFuture.runAsync(() -> {
                            String companyCode = company.getCode();
                            var savedQuoteFuture = CompletableFuture.supplyAsync(() -> quoteRepository.findLatestQuoteByCompanyCode(companyCode).orElse(null), executorService);
                            var fetchedQuoteFuture = CompletableFuture.supplyAsync(() -> quoteFetcher.fetchQuoteByCompanyCode(companyCode).orElse(null), executorService);

                            var combinedFuture = savedQuoteFuture.thenCombineAsync(
                                    fetchedQuoteFuture,
                                    (savedQuote, fetchedQuote) -> {
                                        if (Objects.nonNull(fetchedQuote) && !fetchedQuote.equals(savedQuote)) {
                                            fetchedQuote.setGapPercentage(calculateGapPercentage(savedQuote, fetchedQuote));
                                            return quoteRepository.save(fetchedQuote);
                                        }
                                        return savedQuote;
                                    }, executorService
                            );

                            combinedFuture.join();
                        }, executorService))
                        .toList();

                try {
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
                } catch (Exception e) {
                    log.error("Error while updating quotes", e);
                } finally {
                    executorService.shutdown();
                }
            }
        }

        private BigDecimal calculateGapPercentage(Quote savedQuote, Quote fetchedQuote) {
            var fetchedQuotePrice = fetchedQuote.getPrice();
            if (Objects.isNull(savedQuote)) {
                return fetchedQuotePrice;
            }
            var savedQuotePrice = savedQuote.getPrice();
            return fetchedQuotePrice.subtract(savedQuotePrice)
                    .divide(savedQuotePrice, 5, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
        }
    }
}
