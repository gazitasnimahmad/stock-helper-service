package stockhelperservice.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "MASTER_DQ_NT_TABLE")
public class MasterRatio {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="id", nullable = false)
    private int id;
    @Column(name ="symbol", nullable = true)
    private String symbol;
    @Column(name ="date", nullable = false)
    private String date;
    @Column(name ="dq_by_nt", nullable = true)
    private Double dq_by_nt;
}
