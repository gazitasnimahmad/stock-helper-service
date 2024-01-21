package stockhelperservice.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stockhelperservice.entities.MasterRatio;
import stockhelperservice.model.StockFileMap;
import stockhelperservice.repositories.MasterRatioRepository;
import stockhelperservice.repositories.StockRatioRepository;

import java.util.List;
import java.util.Optional;

@Component
public class CommonUtility {
    @Autowired
    private StockRatioRepository stockRatioRepository;

    @Autowired
    private MasterRatioRepository masterRatioRepository;

    @Autowired
    private stockhelperservice.repositories.MasterStockRepository MasterStockRepository;

    public String getDate(String date, List<StockFileMap> stockList) {
        for (StockFileMap stock: stockList){
            date = stock.getDate();
            break;
        }
        return date;
    }

    public Optional<Double> findSumOfThreeMonthRatio(String symbol) {
        return masterRatioRepository.findSumOfThreeMonthRatio(symbol);
    }

    public Double avgOfTenDays(String symbol) {
        Double sum = 0.00;
        Optional<List<MasterRatio>> lastTwoDaysRatio = masterRatioRepository.findLastTenDaysRatio(symbol);
        if(lastTwoDaysRatio.isPresent()){
            for (MasterRatio ratio : lastTwoDaysRatio.get()){
                sum = ratio.getDq_by_nt() != null ? sum + ratio.getDq_by_nt() : sum + 0.00;
            }
        }
        return sum/10;
    }

    public Double avgOfTwoDays(String symbol) {
        Double sum = 0.00;
        Optional<List<MasterRatio>> lastTwoDaysRatio = masterRatioRepository.findLastTwoDaysRatio(symbol);
        if(lastTwoDaysRatio.isPresent()){
            for (MasterRatio ratio : lastTwoDaysRatio.get()){
                sum = ratio.getDq_by_nt() != null ? sum + ratio.getDq_by_nt() : sum + 0.00;
            }
        }
        return sum/2;
    }
}
