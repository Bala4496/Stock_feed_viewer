package ua.bala.stock_feed_viewer.adapters.out.console;

import org.springframework.stereotype.Component;
import ua.bala.stock_feed_viewer.domain.model.Quote;
import ua.bala.stock_feed_viewer.domain.model.QuoteReport;
import ua.bala.stock_feed_viewer.ports.out.QuotePresenter;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Component
public class ConsoleQuotePresenter implements QuotePresenter {

    @Override
    public void presentQuotes(QuoteReport quoteReport) {
        var quotes = quoteReport.getQuotes();
        var title = quoteReport.getTitle();
        System.out.printf("-------- %s --------%n", title);
        System.out.println(LocalDateTime.now());
        IntStream.range(0, quotes.size()).forEach(order ->
                System.out.println(parseQuote((order + 1), quotes.get(order)))
        );
        System.out.printf("-------- %s --------%n", title);
    }

    private String parseQuote(int order, Quote quote) {
        return "%d. Company code: %s, Price: %s, Percentage changes: %s%%"
                .formatted(order, quote.getCompanyCode(), quote.getPrice(), quote.getGapPercentage().abs());
    }
}
