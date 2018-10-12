package com.unb.beforeigo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BeforeIGoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeforeIGoApplication.class, args);
	}
}
