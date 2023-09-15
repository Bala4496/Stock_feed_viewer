package ua.bala.stock_feed_viewer.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QuoteReport {

    private String title;
    private List<Quote> quotes;
}
