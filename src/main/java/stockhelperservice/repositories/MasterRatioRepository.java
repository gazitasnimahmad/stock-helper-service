package stockhelperservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import stockhelperservice.entities.MasterRatio;
import stockhelperservice.entities.StockRatioAndFactor;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterRatioRepository extends JpaRepository<MasterRatio, Integer> {
    @Query(value = "SELECT * FROM master_dq_nt_table WHERE symbol = ?1 ORDER BY date DESC LIMIT 2", nativeQuery = true)
    Optional<List<MasterRatio>> findLastTwoDaysRatio(String symbol);

    @Query(value = "SELECT * FROM master_dq_nt_table WHERE symbol = ?1 ORDER BY date DESC LIMIT 10", nativeQuery = true)
    Optional<List<MasterRatio>> findLastTenDaysRatio(String symbol);

    @Query(value = "SELECT SUM(dq_by_nt) AS total_sum FROM master_dq_nt_table WHERE symbol = ?1", nativeQuery = true)
    Optional<Double> findSumOfThreeMonthRatio(String symbol);

    @Query(value = "SELECT * FROM master_dq_nt_table WHERE symbol = ?1 ORDER BY date DESC", nativeQuery = true)
    Optional<List<MasterRatio>> getInsightsForSymbol(String symbol);
}
