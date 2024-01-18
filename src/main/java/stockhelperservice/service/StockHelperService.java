package stockhelperservice.service;

import com.opencsv.exceptions.CsvException;
import org.springframework.web.multipart.MultipartFile;
import stockhelperservice.model.StockFileMap;
import stockhelperservice.model.StockMainResponse;

import java.io.IOException;
import java.util.List;

public interface StockHelperService {
    StockMainResponse processFile(MultipartFile file) throws IOException, CsvException;
}
