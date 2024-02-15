package com.mrj.keycloak.springthymemultitenancy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@SpringBootApplication
public class SpringThymeMultitenancyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringThymeMultitenancyApplication.class, args);
	}

}
