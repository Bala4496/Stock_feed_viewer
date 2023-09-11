package ua.bala.stock_feed_viewer.domain.data;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ua.bala.stock_feed_viewer.domain.repository.CompanyRepository;
import ua.bala.stock_feed_viewer.domain.repository.QuoteRepository;
import ua.bala.stock_feed_viewer.ports.in.QuoteFetcher;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuoteUpdater {

    private final CompanyRepository companyRepository;
    private final QuoteFetcher quoteFetcher;
    private final QuoteRepository quoteRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void updateQuotes() {
        int maxThreads = 10;
        var thread = new Thread(new UpdateQuotesTask(maxThreads), "UpdateQuotesThread");
        thread.setDaemon(true);
        thread.start();
    }

    @AllArgsConstructor
    class UpdateQuotesTask implements Runnable {

        int maxThreads;

        @Override
        public void run() {
            while (true) {
                var executorService = Executors.newFixedThreadPool(maxThreads - 1);

                var futures = companyRepository.findAll().stream()
                        .map(company -> CompletableFuture.runAsync(() -> {
                            String companyCode = company.getCode();
                            var savedQuoteFuture = CompletableFuture.supplyAsync(() -> quoteRepository.findLatestQuoteOrderedByCompanyCode(companyCode).orElse(null), executorService);
                            var fetchedQuoteFuture = CompletableFuture.supplyAsync(() -> quoteFetcher.fetchQuoteByCompanyCode(companyCode), executorService);

                            var combinedFuture = savedQuoteFuture.thenCombineAsync(
                                    fetchedQuoteFuture,
                                    (savedQuote, fetchedQuote) -> {
                                        if (savedQuote != null && fetchedQuote != null && !savedQuote.equals(fetchedQuote)) {
                                            quoteRepository.save(fetchedQuote);
                                        }
                                        return fetchedQuote;
                                    },
                                    executorService
                            );

                            combinedFuture.join();
                        }, executorService))
                        .toList();

                var allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

                try {
                    allOf.get();
                } catch (Exception e) {
                    log.error("Error while updating quotes", e);
                } finally {
                    executorService.shutdown();
                }
            }
        }
    }
}
