package ua.bala.stock_feed_viewer.domain.service;

import ua.bala.stock_feed_viewer.domain.model.Company;

import java.util.List;

public interface CompanyService {
    List<Company> getCompanies();
}
