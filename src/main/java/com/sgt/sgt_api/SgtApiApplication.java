package com.sgt.sgt_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SgtApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SgtApiApplication.class, args);
	}

}
