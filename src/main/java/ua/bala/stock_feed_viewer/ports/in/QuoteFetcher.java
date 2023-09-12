package ua.bala.stock_feed_viewer.ports.in;

import ua.bala.stock_feed_viewer.domain.model.Quote;

import java.util.Optional;

public interface QuoteFetcher {

    Optional<Quote> fetchQuoteByCompanyCode(String companyCode);
}
