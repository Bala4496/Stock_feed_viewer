package ua.bala.stock_feed_viewer.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("companies")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Accessors(chain = true)
public class Company {

    @Id
    private Long id;
    @EqualsAndHashCode.Include
    private String code;
    private String name;
}
