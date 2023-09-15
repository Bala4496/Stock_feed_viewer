package ua.bala.stock_feed_viewer.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.bala.stock_feed_viewer.domain.model.Quote;
import ua.bala.stock_feed_viewer.domain.repository.QuoteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;

    @Override
    public List<Quote> getQuotes() {
        return quoteRepository.findLatestQuotesByCompanyCode();
    }

    @Override
    public Quote getQuoteByCompanyCode(String companyCode) {
        return quoteRepository.findLatestQuoteByCompanyCode(companyCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock quote by code '%s' not found".formatted(companyCode)));
    }
}
