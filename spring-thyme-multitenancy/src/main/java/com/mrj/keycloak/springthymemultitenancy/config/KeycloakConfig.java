package com.mrj.keycloak.springthymemultitenancy.config;


import org.keycloak.adapters.springsecurity.config.KeycloakSpringConfigResolverWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {
	
	@Bean
	public KeycloakSpringConfigResolverWrapper keycloakConfigResolver()
	{
		return new KeycloakSpringConfigResolverWrapper(null);
	}
}
