package stockhelperservice.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "MASTER_STOCK_TABLE")
public class MasterStock {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="id", nullable = false)
    private int id;
    @Column(name ="symbol", nullable = false)
    private String symbol;
    @Column(name ="mcap", nullable = true)
    private String mCap;
    @Column(name ="capital_group", nullable = true)
    private String capitalGroup;
    @Column(name ="sector", nullable = true)
    private String sector;
    @Column(name ="sum", nullable = true)
    private Double sum;
    @Column(name ="good_to_go", nullable = true)
    private String goodToGo;
}
