package stockhelperservice.model;

import lombok.Data;
import stockhelperservice.entities.MasterRatio;
import stockhelperservice.entities.MasterStock;
import stockhelperservice.entities.StockRatioAndFactor;

import java.util.List;
@Data
public class MasterResponse {
    private StockRatioAndFactor stockRatioAndFactor;
    private List<MasterRatio> masterRatios;
    private MasterStock masterStock;
    private ErrorInfo errorInfo;
}
