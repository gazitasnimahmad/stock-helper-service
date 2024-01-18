package stockhelperservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stockhelperservice.entities.StockRatioAndFactor;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRatioRepository extends JpaRepository<StockRatioAndFactor, Integer> {

    @Query(value = "select * from stock_Ratio_and_factor", nativeQuery = true)
    Optional<StockRatioAndFactor> findAllNtByQt(@Param("symbol") String symbol, @Param("date") String date);

    @Query(value = "select * from stock_Ratio_and_factor where date = ?1 LIMIT 1", nativeQuery = true)
    Optional<StockRatioAndFactor> findByDate(String date);

}
