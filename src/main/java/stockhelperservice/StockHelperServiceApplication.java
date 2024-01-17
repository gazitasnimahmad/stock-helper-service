package stockhelperservice;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Properties;

@SpringBootApplication
//@ComponentScan(basePackages = { "" })
public class StockHelperServiceApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(StockHelperServiceApplication.class).properties(getProperties()).build().run(args);
	}

	static Properties getProperties(){
		var props = new Properties();
		props.put("spring.config", "C:\\");
		return props;
	}

}
