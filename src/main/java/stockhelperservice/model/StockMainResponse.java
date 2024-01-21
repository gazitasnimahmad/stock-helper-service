package stockhelperservice.model;

import lombok.Data;
import stockhelperservice.entities.StockRatioAndFactor;

import java.util.List;

@Data
public class StockMainResponse {
    private List<StockRatioAndFactor> stockFileMap;
    private ErrorInfo errorInfo;
}
