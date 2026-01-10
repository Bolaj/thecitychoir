package com.portfolio.thecitychoir;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ThecitychoirApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThecitychoirApplication.class, args);
	}

}
