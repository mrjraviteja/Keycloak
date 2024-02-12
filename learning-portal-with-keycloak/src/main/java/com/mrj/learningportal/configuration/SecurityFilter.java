package com.mrj.learningportal.configuration;

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
			.requestMatchers(HttpMethod.GET,"/users").hasRole("admin")
			.requestMatchers(HttpMethod.POST,"/users").hasRole("admin")
			.requestMatchers(HttpMethod.GET,"/users/{id}").hasRole("admin")
			.requestMatchers(HttpMethod.DELETE,"/users/{id}").hasRole("admin")
			.requestMatchers(HttpMethod.POST,"/users/{id}/enroll/{courseid}").hasRole("learner")
			.requestMatchers(HttpMethod.POST,"/users/{id}/favorite/{courseid}").hasRole("learner")
			.requestMatchers(HttpMethod.DELETE,"/users/{id}/favorite/{courseid}").hasRole("learner")
			.requestMatchers(HttpMethod.GET,"/registrations").hasRole("admin")
			.requestMatchers(HttpMethod.GET,"/registrations/{id}").hasRole("admin")
			.requestMatchers(HttpMethod.GET,"/favourites").hasRole("author")
			.requestMatchers(HttpMethod.GET,"/courses").hasAnyRole("learner","author")
			.requestMatchers(HttpMethod.POST,"/courses").hasRole("author")
			.requestMatchers(HttpMethod.PUT,"/courses/{id}").hasRole("author")
			.requestMatchers(HttpMethod.GET,"/courses/{id}").hasAnyRole("author","learner")
			.requestMatchers(HttpMethod.DELETE,"/courses/{id}").hasRole("author")
			.requestMatchers(HttpMethod.GET,"/categories").hasAnyRole("learner","author")
			.requestMatchers(HttpMethod.GET,"/categories/{categoryid}").hasAnyRole("author","learner")
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
