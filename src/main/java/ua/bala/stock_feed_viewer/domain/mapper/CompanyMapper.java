package ua.bala.stock_feed_viewer.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bala.stock_feed_viewer.domain.dto.CompanyDTO;
import ua.bala.stock_feed_viewer.domain.model.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "id", ignore = true)
    Company map(CompanyDTO userDto);
}
