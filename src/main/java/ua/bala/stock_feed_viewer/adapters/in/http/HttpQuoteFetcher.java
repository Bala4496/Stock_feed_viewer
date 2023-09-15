package ua.bala.stock_feed_viewer.adapters.in.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ua.bala.stock_feed_viewer.domain.dto.QuoteDTO;
import ua.bala.stock_feed_viewer.domain.mapper.QuoteMapper;
import ua.bala.stock_feed_viewer.domain.model.Quote;
import ua.bala.stock_feed_viewer.ports.in.QuoteFetcher;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpQuoteFetcher implements QuoteFetcher {

    @Value("${stock-feed-api.url}")
    private String apiUrl;
    @Value("${stock-feed-api.api-key}")
    private String apiKey;
    private final String API_QUOTES_URL = "http://%s/api/v1/stocks/%s/quote";

    private final RestTemplate restTemplate;
    private final QuoteMapper quoteMapper;

    @Override
    public Optional<Quote> fetchQuoteByCompanyCode(String companyCode) {
        return Optional.ofNullable(getQuoteByCompanyCodeFromApi(companyCode).getBody())
                .map(quoteMapper::map);
    }

    public ResponseEntity<QuoteDTO> getQuoteByCompanyCodeFromApi(String companyCode) {
        var headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        return restTemplate.exchange(
                API_QUOTES_URL.formatted(apiUrl, companyCode),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                QuoteDTO.class
        );
    }
}
