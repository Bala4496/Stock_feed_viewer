package ua.bala.stock_feed_viewer.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.bala.stock_feed_viewer.domain.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
