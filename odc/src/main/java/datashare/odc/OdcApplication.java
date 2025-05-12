package datashare.odc;

import datashare.odc.filtering.FilteringRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OdcApplication {

	public static void main(String[] args) {
		SpringApplication.run(OdcApplication.class, args);
	}

}
