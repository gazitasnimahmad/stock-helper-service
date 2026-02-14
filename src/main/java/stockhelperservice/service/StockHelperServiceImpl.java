package stockhelperservice.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stockhelperservice.entities.MasterRatio;
import stockhelperservice.entities.MasterStock;
import stockhelperservice.entities.StockRatioAndFactor;
import stockhelperservice.model.ErrorInfo;
import stockhelperservice.model.MasterResponse;
import stockhelperservice.model.StockFileMap;
import stockhelperservice.model.StockMainResponse;
import stockhelperservice.repositories.MasterRatioRepository;
import stockhelperservice.repositories.MasterStockRepository;
import stockhelperservice.repositories.StockRatioRepository;
import stockhelperservice.utility.CommonUtility;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Slf4j
@Service
public class StockHelperServiceImpl implements StockHelperService {
    private static final int BATCH_SIZE = 50; // tune as needed
    private static final DateTimeFormatter CSV_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy").withLocale(Locale.ENGLISH); // matches '01-Jan-2026'

    @Autowired
    private StockRatioRepository stockRatioRepository;

    @Autowired
    private MasterRatioRepository masterRatioRepository;

    @Autowired
    private MasterStockRepository MasterStockRepository;

    @Autowired
    private CommonUtility commonUtility;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public StockMainResponse processFile(MultipartFile file, LocalDateTime startingTime) throws IOException, CsvException {
        List<StockFileMap> stockList = new ArrayList<>();
        StockMainResponse stockMainResponse = new StockMainResponse();
        List<StockRatioAndFactor> stockResponse = new ArrayList<>();
        LocalDate date = null;
        try {
            log.info("started processing nse data to db. Current time {}", LocalDateTime.now());

            Reader reader = new InputStreamReader(file.getInputStream());
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            stockList = csvReader.readAll().stream().map(data ->
                            new StockFileMap(data[0], data[1], data[2], data[3], data[4], data[5], data[6],
                                    data[7], data[8], data[9], data[10], data[11], data[12], data[13], data[14]))
                    .collect(Collectors.toList());
            log.info("ready to filter data to upload. Current time: {}", LocalDateTime.now());

            stockList = stockList.stream()
                    .filter(not(item -> item.getDeliveryQnty().contains("-")))
                    .filter(not(item -> item.getNumberOfTrades().contains("-")))
                    .filter(not(item -> item.getClosePrice().contains("-")))
                    .filter(not(item -> item.getOpenPrice().contains("-")))
                    .filter(not(item -> item.getDeliveryPer().contains("-"))).toList();
            log.info("Filtering completed. Current time: {}", LocalDateTime.now());

            date = commonUtility.getDate(date, stockList);
            Optional<StockRatioAndFactor> stockAvailable = stockRatioRepository.findByDate(date);
            if (stockAvailable.isEmpty()) {
                savingDataToStockRatioDB(stockList, stockResponse);
                log.info("All NSE Data saved to Stock Ratio table");

                savingDataToMasterRatio(stockResponse);
                log.info("All NSE Data saved to Master Ratio table");

                log.info("NSE Data uploaded successfully. Current time: {}", LocalDateTime.now());
                long seconds = Duration.between(startingTime, LocalDateTime.now()).getSeconds();
                log.info("Total time taken to upload data: {} seconds", seconds);
            } else {
                stockMainResponse.setErrorInfo(new ErrorInfo("Metrics Already available in the database for this date: " + date + ". Please try again for other dates.",
                        "Metrics already stored in the database."));
            }

        } catch (Exception ex) {
            ErrorInfo errorInfo = new ErrorInfo();
            errorInfo.setErrorMessage(ex.getMessage());
            errorInfo.setErrorDescription(ex.getCause() != null ? ex.getCause().toString() : ex.toString());
            stockMainResponse.setErrorInfo(errorInfo);
            log.error("Failed to upload: {}", ex.toString(), ex);
        }
        stockMainResponse.setStockFileMap(stockResponse);
        return stockMainResponse;
    }

    private MasterStock savingDataToMasterStockTable(String symbol) {
        Double avgOfTwoDays = commonUtility.avgOfTwoDays(symbol);
        Double avgOfTenDays = commonUtility.avgOfTenDays(symbol);
        MasterStock masterStock = new MasterStock();
        masterStock.setSymbol(symbol);
        Optional<Double> sum = commonUtility.findSumOfThreeMonthRatio(symbol);
        masterStock.setSum(sum.orElse(0.00));
        masterStock.setGoodToGo((avgOfTwoDays > avgOfTenDays) ? "yes" : "no");
        MasterStockRepository.save(masterStock);
        return masterStock;
    }

    /**
     * Batch-save master ratios created from stockResponse to reduce DB round-trips.
     */
    private void savingDataToMasterRatio(List<StockRatioAndFactor> stockResponse) {
        if (stockResponse == null || stockResponse.isEmpty()) return;

        List<MasterRatio> batch = new ArrayList<>(BATCH_SIZE);
        for (StockRatioAndFactor stock : stockResponse) {
            MasterRatio masterRatio = new MasterRatio();
            masterRatio.setDate(stock.getDate());
            masterRatio.setSymbol(stock.getSymbol());
            masterRatio.setDq_by_nt(stock.getDq_by_nt());
            batch.add(masterRatio);

            if (batch.size() >= BATCH_SIZE) {
                masterRatioRepository.saveAll(batch);
                entityManager.flush();
                entityManager.clear();
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            masterRatioRepository.saveAll(batch);
            entityManager.flush();
            entityManager.clear();
            batch.clear();
        }
    }

    /**
     * Batch insert StockRatioAndFactor entities. Uses saveAll with periodic flush/clear to keep
     * the persistence context small and reduce DB round-trips.
     */
    private void savingDataToStockRatioDB(List<StockFileMap> stockList, List<StockRatioAndFactor> stockResponse) {
        if (stockList == null || stockList.isEmpty()) return;

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        List<StockRatioAndFactor> batch = new ArrayList<>(BATCH_SIZE);

        for (StockFileMap stock : stockList) {
            StockRatioAndFactor stockData = new StockRatioAndFactor();
            stockData.setSymbol(stock.getSymbol().equalsIgnoreCase("-") ? "NA" : stock.getSymbol());

            String raw = stock.getDate();
            LocalDate parsedDate = null;
            if (raw != null) {
                String trimmed = raw.trim();
                if (!trimmed.isEmpty() && !trimmed.equalsIgnoreCase("-")) {
                    parsedDate = LocalDate.parse(trimmed, CSV_DATE_FORMATTER);
                }
            }
            stockData.setDate(parsedDate);

            double close = Double.parseDouble(stock.getClosePrice());
            double open = Double.parseDouble(stock.getOpenPrice());
            double deliveryPer = Double.parseDouble(stock.getDeliveryPer());
            double deliveryQnty = Double.parseDouble(stock.getDeliveryQnty());
            double numberOfTrades = Double.parseDouble(stock.getNumberOfTrades());

            int factor = (close < open || deliveryPer < 30) ? -1 : 1;
            stockData.setFactor(factor);
            stockData.setAvg_close_price(stock.getClosePrice());
            stockData.setSum_delivery_qnty(stock.getDeliveryQnty());
            stockData.setAvg_open_price(stock.getOpenPrice());

            double dqByNt = (numberOfTrades == 0) ? 0.0 : (deliveryQnty / numberOfTrades) * factor;
            stockData.setDq_by_nt(Double.valueOf(decimalFormat.format(dqByNt)));
            stockData.setSum_of_trades(stock.getNumberOfTrades());

            batch.add(stockData);
            stockResponse.add(stockData);

            if (batch.size() >= BATCH_SIZE) {
                stockRatioRepository.saveAll(batch);
                entityManager.flush();
                entityManager.clear();
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            stockRatioRepository.saveAll(batch);
            entityManager.flush();
            entityManager.clear();
            batch.clear();
        }
    }

    @Override
    @Transactional
    public MasterResponse getInsights(String symbol) {
        MasterResponse masterResponse = new MasterResponse();

        if (symbol == null || symbol.trim().isEmpty()) {
            ErrorInfo errorInfo = new ErrorInfo("Symbol is required", "Provided symbol is null or empty");
            masterResponse.setErrorInfo(errorInfo);
            return masterResponse;
        }

        String sym = symbol.trim();
        long overallStart = System.currentTimeMillis();

        try {
            // save/update master stock (this performs calculations)
            MasterStock saved = savingDataToMasterStockTable(sym);
            masterResponse.setMasterStock(saved);
            log.info("saved data to master stock table for: {}", sym);

            // fetch master ratios
            long t0 = System.currentTimeMillis();
            masterRatioRepository.getInsightsForSymbol(sym).ifPresent(masterResponse::setMasterRatios);
            log.info("fetched master ratios for {} in {} ms", sym, System.currentTimeMillis() - t0);

            // fetch stock ratios
            t0 = System.currentTimeMillis();
            stockRatioRepository.getStock(sym).ifPresent(masterResponse::setStockRatioAndFactor);
            log.info("fetched stock ratios for {} in {} ms", sym, System.currentTimeMillis() - t0);

            log.info("getInsights completed for {} in {} ms", sym, System.currentTimeMillis() - overallStart);

        } catch (Exception ex) {
            ErrorInfo errorInfo = new ErrorInfo();
            errorInfo.setErrorMessage(ex.getMessage());
            errorInfo.setErrorDescription(ex.getCause() != null ? ex.getCause().toString() : ex.toString());
            masterResponse.setErrorInfo(errorInfo);
            log.error("failed to get insights for {}: {}", sym, ex.toString(), ex);
        }
        return masterResponse;
    }
}
