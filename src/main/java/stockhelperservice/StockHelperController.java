package stockhelperservice;

import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("stockhelper")
@CrossOrigin(origins = {"http://192.168.1.2:3000","http://192.168.1.2:8080"}, originPatterns = {"https://*.vercel.app"})
public class StockHelperController {

    @Autowired
    private StockHelperService stockHelperService;


    @PostMapping("/upload")
    public StockMainResponse handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException, CsvException {
        LocalDateTime startingTime = LocalDateTime.now();
        log.info("started uploading nse data to db. Current time {}", startingTime);
        return stockHelperService.processFile(file, startingTime);
//        return ResponseEntity.ok("File uploaded and processed successfully!");
    }

    @GetMapping("/insights")
    public MasterResponse getInsights(@RequestParam("symbol") String symbol) throws IOException, CsvException {
        log.info("started getting insights for symbol: {}", symbol);
        return stockHelperService.getInsights(symbol.toUpperCase());
    }

    @GetMapping("/hello")
    public String getHello()  {
//        return ResponseEntity.ok("File uploaded and processed successfully!");
        return "hello World";
    }
}
