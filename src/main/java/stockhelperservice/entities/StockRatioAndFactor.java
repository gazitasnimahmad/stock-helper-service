package stockhelperservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "stock_ratio_and_factor")
public class StockRatioAndFactor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="id", nullable = false)
    private int id;
    @Column(name ="symbol", nullable = true)
    private String symbol;
    @Column(name ="date", nullable = false)
    private LocalDate date;
    @Column(name ="avg_close_price", nullable = true)
    private String avg_close_price;
    @Column(name ="avg_open_price", nullable = true)
    private String avg_open_price;
    @Column(name ="sum_delivery_qnty", nullable = true)
    private String sum_delivery_qnty;
    @Column(name ="sum_of_trades", nullable = true)
    private String sum_of_trades;
    @Column(name ="factor", nullable = true)
    private int factor;
    @Column(name ="dq_by_nt", nullable = true)
    private Double dq_by_nt;


}
