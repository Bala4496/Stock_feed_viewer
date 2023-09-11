package ua.bala.stock_feed_viewer.domain.service.report;

import lombok.Getter;

@Getter
public enum QuoteReportType {
    EXPENS("Top 5 expensive quotes"),
    LOWGAP("Top 5 low-gap quotes");

    private final String title;

    QuoteReportType(String title) {
        this.title = title;
    }
}
