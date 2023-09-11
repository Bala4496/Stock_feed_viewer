package ua.bala.stock_feed_viewer.ports.in;

import ua.bala.stock_feed_viewer.domain.model.Quote;

public interface QuoteFetcher {

    Quote fetchQuoteByCompanyCode(String companyCode);
}
