package ua.bala.stock_feed_viewer.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bala.stock_feed_viewer.domain.dto.QuoteDTO;
import ua.bala.stock_feed_viewer.domain.model.Quote;

@Mapper(componentModel = "spring")
public interface QuoteMapper {

    @Mapping(target = "id", ignore = true)
    Quote map(QuoteDTO quoteDTO);
}
