package stockhelperservice;

import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stockhelperservice.entities.StockRatioAndFactor;
import stockhelperservice.model.MasterResponse;
import stockhelperservice.model.StockFileMap;
import stockhelperservice.model.StockMainResponse;
import stockhelperservice.repositories.StockRatioRepository;
import stockhelperservice.service.StockHelperService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("stockhelper")
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:8080"}, originPatterns = {"https://*.vercel.app"})
public class StockHelperController {

    @Autowired
    private StockHelperService stockHelperService;


    @PostMapping("/upload")
    public StockMainResponse handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException, CsvException {
        return stockHelperService.processFile(file);
//        return ResponseEntity.ok("File uploaded and processed successfully!");
    }

    @GetMapping("/insights")
    public MasterResponse getInsights(@RequestParam("symbol") String symbol) throws IOException, CsvException {
//        return ResponseEntity.ok("File uploaded and processed successfully!");
        return stockHelperService.getInsights(symbol.toUpperCase());
    }

    @GetMapping("/hello")
    public String getHello()  {
//        return ResponseEntity.ok("File uploaded and processed successfully!");
        return "hello World";
    }
}
