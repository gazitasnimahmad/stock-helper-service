package stockhelperservice;

import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stockhelperservice.model.StockFileMap;
import stockhelperservice.service.StockHelperService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("stockhelper")
@CrossOrigin(origins = "http://localhost:3000")
public class StockHelperController {

    @Autowired
    private StockHelperService stockHelperService;


    @PostMapping("/upload")
    public List<StockFileMap> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException, CsvException {
        List<StockFileMap> stockData= stockHelperService.processFile(file);
        return stockData;
//        return ResponseEntity.ok("File uploaded and processed successfully!");
    }
}
