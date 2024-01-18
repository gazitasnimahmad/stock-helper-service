package stockhelperservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stockhelperservice.entities.MasterRatio;
import stockhelperservice.entities.StockRatioAndFactor;

@Repository
public interface MasterRatioRepository extends JpaRepository<MasterRatio, Integer> {
}
