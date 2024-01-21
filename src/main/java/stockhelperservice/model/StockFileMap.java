package stockhelperservice.model;
import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockFileMap {
    @CsvBindByPosition(position = 0)
    private String symbol;
    @CsvBindByPosition(position = 1)
    private String series;
    @CsvBindByPosition(position = 2)
    private String date;
    @CsvBindByPosition(position = 3)
    private String previousClosePrice;
    @CsvBindByPosition(position = 4)
    private String openPrice;
    @CsvBindByPosition(position = 5)
    private String highPrice;
    @CsvBindByPosition(position = 6)
    private String lowPrice;
    @CsvBindByPosition(position = 7)
    private String lastPrice;
    @CsvBindByPosition(position = 8)
    private String closePrice;
    @CsvBindByPosition(position = 9)
    private String avgPrice;
    @CsvBindByPosition(position = 10)
    private String ttlTrdQnty;
    @CsvBindByPosition(position = 11)
    private String turnOverlacs;
    @CsvBindByPosition(position = 12)
    private String numberOfTrades;
    @CsvBindByPosition(position = 13)
    private String deliveryQnty;
    @CsvBindByPosition(position = 14)
    private String deliveryPer;

}
