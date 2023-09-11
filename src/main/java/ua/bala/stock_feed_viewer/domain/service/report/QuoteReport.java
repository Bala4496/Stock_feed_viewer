package ua.bala.stock_feed_viewer.domain.service.report;

import ua.bala.stock_feed_viewer.domain.model.Quote;

import java.util.List;

public interface QuoteReport {

    QuoteReport buildReport(List<Quote> quotes);
    List<Quote> getReport();
    String getTitle();
}
