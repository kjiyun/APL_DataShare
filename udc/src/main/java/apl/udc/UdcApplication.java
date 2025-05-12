package apl.udc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UdcApplication {

	public static void main(String[] args) {
		SpringApplication.run(UdcApplication.class, args);

		RuleUploader.sendRule();
	}
}
