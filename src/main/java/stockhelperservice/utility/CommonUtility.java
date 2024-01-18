package stockhelperservice.utility;

import org.springframework.stereotype.Component;
import stockhelperservice.model.StockFileMap;

import java.util.List;

@Component
public class CommonUtility {
    public String getDate(String date, List<StockFileMap> stockList) {
        for (StockFileMap stock: stockList){
            date = stock.getDate();
            break;
        }
        return date;
    }
}
