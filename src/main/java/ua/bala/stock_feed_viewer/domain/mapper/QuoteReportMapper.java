package ua.bala.stock_feed_viewer.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bala.stock_feed_viewer.domain.dto.QuoteReportDTO;
import ua.bala.stock_feed_viewer.domain.model.QuoteReport;

@Mapper(componentModel = "spring")
public interface QuoteReportMapper {

    @Mapping(target = "quotes", source = "quotes")
    QuoteReportDTO map(QuoteReport quoteReport);
}
