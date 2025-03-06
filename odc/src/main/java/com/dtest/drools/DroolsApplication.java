package com.dtest.drools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DroolsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DroolsApplication.class, args);
	}

}
