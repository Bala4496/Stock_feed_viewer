package ua.bala.stock_feed_viewer.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.bala.stock_feed_viewer.domain.model.Quote;
import ua.bala.stock_feed_viewer.domain.model.QuoteReport;
import ua.bala.stock_feed_viewer.domain.service.report.QuoteReportService;
import ua.bala.stock_feed_viewer.ports.out.QuotePresenter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final QuoteService quoteService;
    private final List<QuotePresenter> quotePresenters;
    private final List<QuoteReportService> quoteReportServices;

    @Scheduled(fixedDelayString = "${displaying.delay}")
    public void processQuotes() {
        buildQuoteReports(quoteService.getQuotes()).forEach(quoteReport -> quotePresenters
                .forEach(quotePresenter -> quotePresenter.presentQuotes(quoteReport))
        );
    }

    @Override
    public List<QuoteReport> buildQuoteReports(List<Quote> getLatestQuotes) {
        return quoteReportServices.stream()
                .map(quoteReport -> quoteReport.buildQuoteReport(getLatestQuotes))
                .toList();
    }
}
