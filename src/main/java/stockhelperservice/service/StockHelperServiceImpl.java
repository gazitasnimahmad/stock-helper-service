package stockhelperservice.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stockhelperservice.entities.StockRatioAndFactor;
import stockhelperservice.model.StockFileMap;
import stockhelperservice.repositories.StockRatioRepository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class StockHelperServiceImpl implements StockHelperService{

    @Autowired
    private StockRatioRepository stockRatioRepository;

    @Override
    public List<StockFileMap> processFile(MultipartFile file) throws IOException, CsvException {
        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            var csvData = csvReader.readAll().stream()
                    .map(data -> new StockFileMap(data[0], data[1],data[2],data[3],data[4],data[5],data[6],data[7],data[8],data[9],data[10],data[11],data[12],data[13],data[14]))
                    .collect(Collectors.toList());

            for(StockFileMap stock : csvData){
                StockRatioAndFactor stockData = new StockRatioAndFactor();
                stockData.setSymbol(stock.getSymbol());
                stockData.setDate(stock.getDate());
                int factor = (Double.parseDouble(stock.getClosePrice()) < Double.parseDouble(stock.getOpenPrice()) || Double.parseDouble(stock.getDeliveryPer()) < 30 ) ? -1 : 1;
                stockData.setFactor(factor);
                stockData.setAvg_close_price(stock.getClosePrice());
                stockData.setSum_delivery_qnty(stock.getDeliveryQnty());
                stockData.setAvg_open_price(stock.getOpenPrice());
                double dqByNt = Double.parseDouble(stock.getDeliveryQnty())  / Double.parseDouble(stock.getNumberOfTrades());
                stockData.setDq_by_nt(dqByNt);
                stockData.setSum_of_trades(stock.getNumberOfTrades());
                stockRatioRepository.save(stockData);
            }
            return csvData;
        }
    }
}
