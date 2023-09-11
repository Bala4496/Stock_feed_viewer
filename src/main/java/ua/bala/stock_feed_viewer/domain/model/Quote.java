package ua.bala.stock_feed_viewer.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("quotes")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Accessors(chain = true)
public class Quote {

    @Id
    private Long id;
    @Column("company_code")
    @EqualsAndHashCode.Include
    private String companyCode;
    @EqualsAndHashCode.Include
    private BigDecimal price;
    @Column("gap_percentage")
    private float gapPercentage;
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
}