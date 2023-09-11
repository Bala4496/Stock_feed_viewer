package ua.bala.stock_feed_viewer.ports.in;

import ua.bala.stock_feed_viewer.domain.model.Company;

import java.util.List;

public interface CompanyFetcher {

    List<Company> fetchCompanies();
}
