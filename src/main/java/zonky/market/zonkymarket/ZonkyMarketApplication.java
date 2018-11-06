package zonky.market.zonkymarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ZonkyMarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZonkyMarketApplication.class, args);
	}
}
