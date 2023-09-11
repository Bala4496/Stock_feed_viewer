package ua.bala.stock_feed_viewer.adapters.in;

import org.springframework.stereotype.Component;
import ua.bala.stock_feed_viewer.domain.model.Company;
import ua.bala.stock_feed_viewer.ports.in.CompanyFetcher;

import java.util.List;

@Component
public class HttpCompanyFetcher implements CompanyFetcher {

    @Override
    public List<Company> fetchCompanies() {
        return null;
    }
}
