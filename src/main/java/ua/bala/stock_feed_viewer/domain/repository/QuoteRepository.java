package ua.bala.stock_feed_viewer.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.bala.stock_feed_viewer.domain.model.Quote;

import java.util.List;
import java.util.Optional;

public interface QuoteRepository extends JpaRepository<Quote, Long> {


    @Query("""
            SELECT q 
            FROM Quote q 
            WHERE q.companyCode = :companyCode AND q.createdAt = (
                SELECT MAX(q2.createdAt) 
                FROM Quote q2 
                WHERE q2.companyCode = :companyCode
                )
            """)
    Optional<Quote> findLatestQuoteByCompanyCode(String companyCode);

//    @Query("""
//            SELECT q
//            FROM Quote q
//            WHERE q.createdAt = (
//                SELECT MAX(q2.createdAt)
//                FROM Quote q2
//                WHERE q2.companyCode = q.companyCode
//                )
//            """)
    @Query(value = """
            SELECT id, company_code, price, gap_percentage, created_at 
            FROM (
                SELECT id, company_code, price, gap_percentage, created_at, ROW_NUMBER() 
                    OVER (PARTITION BY company_code ORDER BY created_at DESC) AS row_num 
                FROM quotes
            ) ranked 
            WHERE row_num = 1
            """,
            nativeQuery = true)
    List<Quote> findLatestQuotesByCompanyCode();
}
