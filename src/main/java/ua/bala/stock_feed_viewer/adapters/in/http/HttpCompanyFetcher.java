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
import ua.bala.stock_feed_viewer.domain.dto.CompanyDTO;
import ua.bala.stock_feed_viewer.domain.mapper.CompanyMapper;
import ua.bala.stock_feed_viewer.domain.model.Company;
import ua.bala.stock_feed_viewer.ports.in.CompanyFetcher;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpCompanyFetcher implements CompanyFetcher {

    @Value("${stock-feed-api.url}")
    private String apiUrl;
    @Value("${stock-feed-api.api-key}")
    private String apiKey;
    private final String API_COMPANIES_URL = "http://%s/api/v1/companies";

    private final RestTemplate restTemplate;
    private final CompanyMapper companyMapper;


    @Override
    public List<Company> fetchCompanies() {
        var body = getCompaniesFromApi().getBody();
        if (Objects.isNull(body)) {
            return Collections.emptyList();
        }
        return Arrays.stream(body).map(companyMapper::map).toList();
    }

    private ResponseEntity<CompanyDTO[]> getCompaniesFromApi() {
        var headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        return restTemplate.exchange(
                API_COMPANIES_URL.formatted(apiUrl),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                CompanyDTO[].class
        );
    }
}
