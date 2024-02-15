package com.mrj.keycloak.springthymemultitenancy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private DynamicRealmClientRegistrationRepository dynamicRealmClientRegistrationRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests()
                .requestMatchers("/login", "/public").permitAll()
                .requestMatchers("/protected").authenticated()
                .and()
            .oauth2ResourceServer()
                .jwt()
                .jwkSetUri("http://localhost:8080/auth/realms/{" + extractRealmFromRequest() + "}");
        return http.build();
    }
    
    private String extractRealmFromRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String host = request.getServerName();
        if (host != null && host.contains(".")) {
            String[] parts = host.split("\\.", 2); 
            return parts[0]; 
        } else {
            return "customer1";
        }
    }

}
