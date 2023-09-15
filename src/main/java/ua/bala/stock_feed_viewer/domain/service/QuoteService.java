package ua.bala.stock_feed_viewer.domain.service;

import ua.bala.stock_feed_viewer.domain.model.Quote;

import java.util.List;

public interface QuoteService {
    List<Quote> getQuotes();
    Quote getQuoteByCompanyCode(String companyCode);
}
