package ua.bala.stock_feed_viewer.domain.service;

import ua.bala.stock_feed_viewer.domain.model.Quote;
import ua.bala.stock_feed_viewer.domain.model.QuoteReport;

import java.util.List;

public interface ReportService {
    List<QuoteReport> buildQuoteReports(List<Quote> getLatestQuotes);
}
