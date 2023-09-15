package ua.bala.stock_feed_viewer.adapters.out.http;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.bala.stock_feed_viewer.domain.dto.CompanyDTO;
import ua.bala.stock_feed_viewer.domain.mapper.CompanyMapper;
import ua.bala.stock_feed_viewer.domain.service.CompanyService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompaniesController {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;

    @GetMapping
    public List<CompanyDTO> getCompanies() {
        return companyService.getCompanies().stream()
                .map(companyMapper::map)
                .toList();
    }
}
