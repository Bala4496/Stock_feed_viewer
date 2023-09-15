package ua.bala.stock_feed_viewer.domain.service.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.bala.stock_feed_viewer.domain.model.Quote;
import ua.bala.stock_feed_viewer.domain.model.QuoteReport;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MostExpensiveReportService implements QuoteReportService {

    int limit = 5;

    @Override
    public QuoteReport buildQuoteReport(List<Quote> quotes) {
        var quotesList = quotes.stream()
                .sorted(Comparator.comparing(Quote::getPrice, Comparator.reverseOrder()))
                .limit(limit)
                .toList();
        return new QuoteReport(getReportTitle(), quotesList);
    }

    @Override
    public String getReportTitle() {
        return QuoteReportType.EXPENS.getTitle();
    }
}
