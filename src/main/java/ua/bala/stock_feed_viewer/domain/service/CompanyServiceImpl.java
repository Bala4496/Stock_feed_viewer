package ua.bala.stock_feed_viewer.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.bala.stock_feed_viewer.domain.model.Company;
import ua.bala.stock_feed_viewer.domain.repository.CompanyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public List<Company> getCompanies() {
        return companyRepository.findAll();
    }
}
