package stockhelperservice.service;

import com.opencsv.exceptions.CsvException;
import org.springframework.web.multipart.MultipartFile;
import stockhelperservice.model.MasterResponse;
import stockhelperservice.model.StockMainResponse;

import java.io.IOException;
import java.time.LocalDateTime;

public interface StockHelperService {
    StockMainResponse processFile(MultipartFile file, LocalDateTime startingTime) throws IOException, CsvException;
    MasterResponse getInsights(String symbol);
}
