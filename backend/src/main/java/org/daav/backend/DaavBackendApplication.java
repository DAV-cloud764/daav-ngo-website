package org.daav.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DaavBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DaavBackendApplication.class, args);
	}

}
