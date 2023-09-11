package ua.bala.stock_feed_viewer.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.bala.stock_feed_viewer.domain.model.Company;
import ua.bala.stock_feed_viewer.domain.repository.CompanyRepository;
import ua.bala.stock_feed_viewer.domain.repository.QuoteRepository;
import ua.bala.stock_feed_viewer.domain.service.report.QuoteReport;
import ua.bala.stock_feed_viewer.ports.out.QuotePresenter;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final CompanyRepository companyRepository;
    private final QuoteRepository quoteRepository;
    private final QuotePresenter quotePresenter;
    private final List<QuoteReport> quoteReports;

    @Scheduled(fixedDelayString = "${displaying.delay}")
    public void processQuotes() {
        var quotes = companyRepository.findAll().stream().parallel()
                .map(Company::getCode)
                .map(quoteRepository::findLatestQuoteOrderedByCompanyCode)
                .flatMap(Optional::stream)
                .toList();

        quoteReports.stream()
                .map(quoteReport -> quoteReport.buildReport(quotes))
                .forEach(quotePresenter::presentQuotes);
    }
}
