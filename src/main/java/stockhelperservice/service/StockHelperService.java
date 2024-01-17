package stockhelperservice.service;

import com.opencsv.exceptions.CsvException;
import org.springframework.web.multipart.MultipartFile;
import stockhelperservice.model.StockFileMap;

import java.io.IOException;
import java.util.List;

public interface StockHelperService {
    List<StockFileMap> processFile(MultipartFile file) throws IOException, CsvException;
}
