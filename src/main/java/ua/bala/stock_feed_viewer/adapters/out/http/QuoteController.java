package ua.bala.stock_feed_viewer.adapters.out.http;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.bala.stock_feed_viewer.domain.dto.QuoteDTO;
import ua.bala.stock_feed_viewer.domain.dto.QuoteReportDTO;
import ua.bala.stock_feed_viewer.domain.mapper.QuoteMapper;
import ua.bala.stock_feed_viewer.domain.mapper.QuoteReportMapper;
import ua.bala.stock_feed_viewer.domain.model.QuoteReport;
import ua.bala.stock_feed_viewer.domain.service.QuoteService;
import ua.bala.stock_feed_viewer.domain.service.report.QuoteReportService;
import ua.bala.stock_feed_viewer.ports.out.QuotePresenter;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class QuoteController implements QuotePresenter {

    private final QuoteService quoteService;
    private final QuoteMapper quoteMapper;
    private final List<QuoteReportService> quoteReportServices;
    private final QuoteReportMapper quoteReportMapper;
    private List<QuoteReport> quoteReports;

    @GetMapping("/quotes")
    public List<QuoteDTO> getQuotes() {
        return quoteService.getQuotes().stream()
                .map(quoteMapper::map)
                .toList();
    }

    @GetMapping("/{code}/quote")
    public QuoteDTO getQuotesByCompanyCode(@PathVariable String code) {
        return quoteMapper.map(quoteService.getQuoteByCompanyCode(code));
    }

    @GetMapping("/report")
    public List<QuoteReportDTO> getQuoteReport() {
        return quoteReports.stream()
                .map(quoteReportMapper::map)
                .toList();
    }

    @Override
    public void presentQuotes(QuoteReport quoteReport) {
        quoteReports = quoteReportServices.stream()
                .map(quoteReportService -> quoteReportService.buildQuoteReport(quoteService.getQuotes()))
                .toList();
    }
}
