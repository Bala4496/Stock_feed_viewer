package ua.bala.stock_feed_viewer.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotes")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Accessors(chain = true)
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quotes_id_seq")
    @SequenceGenerator(name = "quotes_id_seq", sequenceName = "quotes_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "company_code")
    @EqualsAndHashCode.Include
    private String companyCode;
    @EqualsAndHashCode.Include
    private BigDecimal price;
    @Column(name = "gap_percentage")
    private BigDecimal gapPercentage;
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}