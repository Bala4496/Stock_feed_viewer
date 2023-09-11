package ua.bala.stock_feed_viewer.ports.out;

import ua.bala.stock_feed_viewer.domain.service.report.QuoteReport;

public interface QuotePresenter {

    void presentQuotes(QuoteReport quoteReport);
}
