package ua.bala.stock_feed_viewer.domain.service.report;

import ua.bala.stock_feed_viewer.domain.model.Quote;
import ua.bala.stock_feed_viewer.domain.model.QuoteReport;

import java.util.List;

public interface QuoteReportService {

    QuoteReport buildQuoteReport(List<Quote> quotes);

    String getReportTitle();
}
