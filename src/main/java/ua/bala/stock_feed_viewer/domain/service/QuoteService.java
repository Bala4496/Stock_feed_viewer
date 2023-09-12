package ua.bala.stock_feed_viewer.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.bala.stock_feed_viewer.domain.repository.QuoteRepository;
import ua.bala.stock_feed_viewer.domain.service.report.QuoteReport;
import ua.bala.stock_feed_viewer.ports.out.QuotePresenter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuotePresenter quotePresenter;
    private final List<QuoteReport> quoteReports;

    @Scheduled(fixedDelayString = "${displaying.delay}")
    public void processQuotes() {
        var latestQuotesByCompanyCode = quoteRepository.findLatestQuotesByCompanyCode();
        quoteReports.stream()
                .map(quoteReport -> quoteReport.buildReport(latestQuotesByCompanyCode))
                .forEach(quotePresenter::presentQuotes);
    }
}
