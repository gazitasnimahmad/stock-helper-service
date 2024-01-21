package stockhelperservice.service;

import com.opencsv.exceptions.CsvException;
import org.springframework.web.multipart.MultipartFile;
import stockhelperservice.model.MasterResponse;
import stockhelperservice.model.StockMainResponse;

import java.io.IOException;

public interface StockHelperService {
    StockMainResponse processFile(MultipartFile file) throws IOException, CsvException;
    MasterResponse getInsights(String symbol);
}
