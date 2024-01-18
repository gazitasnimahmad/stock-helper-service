package stockhelperservice.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stockhelperservice.entities.StockRatioAndFactor;
import stockhelperservice.model.ErrorInfo;
import stockhelperservice.model.StockFileMap;
import stockhelperservice.model.StockMainResponse;
import stockhelperservice.repositories.StockRatioRepository;
import stockhelperservice.utility.CommonUtility;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;


@Service
public class StockHelperServiceImpl implements StockHelperService {
    private StockRatioRepository stockRatioRepository;

    @Autowired
    private CommonUtility commonUtility;

    @Autowired
    public void setStockRatioRepository(StockRatioRepository stockRatioRepository) {
        this.stockRatioRepository = stockRatioRepository;
    }

    @Override
    public StockMainResponse processFile(MultipartFile file) throws IOException, CsvException {
        List<StockFileMap> stockList = new ArrayList<>();
        StockMainResponse stockMainResponse = new StockMainResponse();
        List<StockRatioAndFactor> stockResponse = new ArrayList<>();
        String date = "";
        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            stockList = csvReader.readAll().stream().map(data ->
                    new StockFileMap(data[0], data[1], data[2], data[3], data[4], data[5], data[6],
                            data[7], data[8], data[9], data[10], data[11], data[12], data[13], data[14]))
                    .collect(Collectors.toList());

            stockList = stockList.stream()
                    .filter(not (item -> item.getDeliveryQnty().contains("-")))
                    .filter(not (item -> item.getNumberOfTrades().contains("-")))
                    .filter(not (item -> item.getClosePrice().contains("-")))
                    .filter(not (item -> item.getOpenPrice().contains("-")))
                    .filter(not (item -> item.getDeliveryPer().contains("-"))).toList();

            date = commonUtility.getDate(date, stockList);
            Optional<StockRatioAndFactor> stockAvailable = stockRatioRepository.findByDate(date);
            if(stockAvailable.isEmpty()){
                savingDataToStockRatioDB(stockList,stockResponse);
            }

        } catch (Exception ex) {
            ErrorInfo errorInfo = new ErrorInfo();
            errorInfo.setErrorMessage(ex.getMessage());
            errorInfo.setErrorDescription(ex.getCause().toString());
            stockMainResponse.setErrorInfo(errorInfo);
            ex.printStackTrace();
        }
        stockMainResponse.setStockFileMap(stockResponse);
        return stockMainResponse;
    }

    private void savingDataToStockRatioDB(List<StockFileMap> stockList, List<StockRatioAndFactor> stockResponse) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        for (StockFileMap stock : stockList) {
            StockRatioAndFactor stockData = new StockRatioAndFactor();
            stockData.setSymbol(stock.getSymbol().equalsIgnoreCase("-") ? "NA" : stock.getSymbol());
            stockData.setDate(stock.getDate().equalsIgnoreCase("-") ? "NA" : stock.getDate());
            int factor = (Double.parseDouble(stock.getClosePrice()) < Double.parseDouble(stock.getOpenPrice()) || Double.parseDouble(stock.getDeliveryPer()) < 30) ? -1 : 1;
            stockData.setFactor(factor);
            stockData.setAvg_close_price(stock.getClosePrice());
            stockData.setSum_delivery_qnty(stock.getDeliveryQnty());
            stockData.setAvg_open_price(stock.getOpenPrice());
            double dqByNt = Double.parseDouble(stock.getDeliveryQnty()) / Double.parseDouble(stock.getNumberOfTrades());
            dqByNt = (factor == 1 || factor == -1) ? dqByNt * factor : dqByNt;
            stockData.setDq_by_nt(Double.valueOf(decimalFormat.format(dqByNt)));
            stockData.setSum_of_trades(stock.getNumberOfTrades());
            stockRatioRepository.save(stockData);
            stockResponse.add(stockData);
        }
    }
}
