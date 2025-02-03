package com.thegoalgrid.goalgrid;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@SpringBootApplication
public class GridgoalApplication {

	@PostConstruct
	public void init() {
		// Set JVM default time zone to UTC
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		SpringApplication.run(GridgoalApplication.class, args);
	}
}
