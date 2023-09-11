package ua.bala.stock_feed_viewer.adapters.in;

import org.springframework.stereotype.Component;
import ua.bala.stock_feed_viewer.domain.model.Quote;
import ua.bala.stock_feed_viewer.ports.in.QuoteFetcher;

@Component
public class HttpQuoteFetcher implements QuoteFetcher {

    @Override
    public Quote fetchQuoteByCompanyCode(String companyCode) {
        return null;
    }
}
