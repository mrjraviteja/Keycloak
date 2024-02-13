package com.mrj.keycloak.multitenancy.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthConverter.class);
	
    public static final String USER = "user";
    private final JwtAuthConverter jwtAuthConverter;
    
    AuthenticationManager authenticationManager = JwtIssuerAuthenticationManagerResolver
    	    .fromTrustedIssuers("http://localhost:8080/realms/customer1", "http://localhost:8080/realms/customer2","http://localhost:8080/realms/customer3").resolve(null);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	logger.info("In securityFilterChain in WebSecurityConfig");
        http.authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET, "/cust/user").hasRole("user")
                .anyRequest().authenticated();
        http.oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthConverter)
                .authenticationManager(authenticationManager);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        logger.info("In securityFilterChain in WebSecurityConfig - 2");
        return http.build();
    }

}