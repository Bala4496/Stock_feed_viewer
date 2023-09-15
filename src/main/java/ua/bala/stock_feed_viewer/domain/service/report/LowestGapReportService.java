package ua.bala.stock_feed_viewer.domain.service.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.bala.stock_feed_viewer.domain.model.Quote;
import ua.bala.stock_feed_viewer.domain.model.QuoteReport;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LowestGapReportService implements QuoteReportService {

    private int limit = 5;

    public LowestGapReportService(int limit) {
        this.limit = limit;
    }

    @Override
    public QuoteReport buildQuoteReport(List<Quote> quotes) {
        var quotesList = quotes.stream().parallel()
                .sorted(Comparator.comparing(quote -> quote.getGapPercentage().abs()))
                .limit(limit)
                .toList();
        return new QuoteReport(getReportTitle(), quotesList);
    }

    @Override
    public String getReportTitle() {
        return QuoteReportType.LOWGAP.getTitle();
    }
}
