// src/main/java/stockhelperservice/utility/CommonUtility.java
package stockhelperservice.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stockhelperservice.entities.MasterRatio;
import stockhelperservice.model.StockFileMap;
import stockhelperservice.repositories.MasterRatioRepository;
import stockhelperservice.repositories.StockRatioRepository;
import stockhelperservice.repositories.MasterStockRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class CommonUtility {
    @Autowired
    private StockRatioRepository stockRatioRepository;

    @Autowired
    private MasterRatioRepository masterRatioRepository;

    @Autowired
    private MasterStockRepository masterStockRepository;

    private static final DateTimeFormatter CSV_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy").withLocale(Locale.ENGLISH);

    public LocalDate getDate(LocalDate date, List<StockFileMap> stockList) {
        if (stockList == null || stockList.isEmpty()) {
            return null;
        }
        for (StockFileMap stock : stockList) {
            String raw = stock.getDate();
            if (raw == null) {
                continue;
            }
            String trimmed = raw.trim();
            if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("-")) {
                continue;
            }
            try {
                return LocalDate.parse(trimmed, CSV_DATE_FORMATTER);
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Unable to parse date: " + raw, ex);
            }
        }
        return null;
    }

    public Optional<Double> findSumOfThreeMonthRatio(String symbol) {
        return masterRatioRepository.findSumOfThreeMonthRatio(symbol);
    }

    public Double avgOfTenDays(String symbol) {
        Double sum = 0.00;
        Optional<List<MasterRatio>> lastTenDaysRatio = masterRatioRepository.findLastTenDaysRatio(symbol);
        if (lastTenDaysRatio.isPresent()) {
            for (MasterRatio ratio : lastTenDaysRatio.get()) {
                sum = ratio.getDq_by_nt() != null ? sum + ratio.getDq_by_nt() : sum + 0.00;
            }
        }
        return sum / 10;
    }

    public Double avgOfTwoDays(String symbol) {
        Double sum = 0.00;
        Optional<List<MasterRatio>> lastTwoDaysRatio = masterRatioRepository.findLastTwoDaysRatio(symbol);
        if (lastTwoDaysRatio.isPresent()) {
            for (MasterRatio ratio : lastTwoDaysRatio.get()) {
                sum = ratio.getDq_by_nt() != null ? sum + ratio.getDq_by_nt() : sum + 0.00;
            }
        }
        return sum / 2;
    }
}
