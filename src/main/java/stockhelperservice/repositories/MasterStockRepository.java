package stockhelperservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stockhelperservice.entities.MasterRatio;
import stockhelperservice.entities.MasterStock;

@Repository
public interface MasterStockRepository extends JpaRepository<MasterStock, Integer> {
}
