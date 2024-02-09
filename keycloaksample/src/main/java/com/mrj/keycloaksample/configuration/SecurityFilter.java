package com.mrj.keycloaksample.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityFilter {
	
	private JwtAuthConverter jwtAuthConverter;
	
	@Bean
	public SecurityFilterChain filter(HttpSecurity http) throws Exception
	{
		http
			.authorizeHttpRequests()
			.requestMatchers(HttpMethod.GET,"/api/Tester").hasRole("tester")
			.requestMatchers(HttpMethod.GET,"/api/User").hasRole("user")
			.anyRequest()
			.authenticated();
			
		http
			.oauth2ResourceServer()
			.jwt()
			.jwtAuthenticationConverter(jwtAuthConverter);
		
		http
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		return http.build();
	}
}
