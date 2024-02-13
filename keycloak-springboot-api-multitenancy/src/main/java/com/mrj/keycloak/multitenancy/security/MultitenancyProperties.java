package com.mrj.keycloak.multitenancy.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "multitenancy")
public class MultitenancyProperties {
	private List<RealmConfig> realms = new ArrayList<>();
	
	@Data
	public static class RealmConfig
	{
		private String name;
		private String issuerUri;
		private String jwkSetUri;
		private String resourceId;
		private String principalAttribute;
	}
}
