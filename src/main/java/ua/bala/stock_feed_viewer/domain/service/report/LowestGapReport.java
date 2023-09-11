package ua.bala.stock_feed_viewer.domain.service.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.bala.stock_feed_viewer.domain.model.Quote;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LowestGapReport implements QuoteReport {

    private int limit = 5;
    private List<Quote> quotesList;

    public LowestGapReport(int limit) {
        this.limit = limit;
    }

    @Override
    public QuoteReport buildReport(List<Quote> quotes) {
        quotesList = quotes.stream()
                .sorted(Comparator.comparing(Quote::getGapPercentage))
                .limit(limit)
                .toList();
        return this;
    }

    @Override
    public List<Quote> getReport() {
        return quotesList;
    }

    @Override
    public String getTitle() {
        return QuoteReportType.LOWGAP.getTitle();
    }
}
