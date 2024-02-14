package com.mrj.keycloak.springthymemultitenancy.config;

import java.net.URL;

import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.representations.adapters.config.AdapterConfig;

public class PathBasedKeycloakConfigResolver extends KeycloakSpringBootConfigResolver{
	
	public PathBasedKeycloakConfigResolver()
	{
		super();
	}
	
	@Override
	public KeycloakDeployment resolve(HttpFacade.Request facade)
	{
		String path = facade.getURI();
		String realm = extractRealmFromDomain(path);
		
		if(realm != null)
		{
			KeycloakDeployment deployment = new KeycloakDeployment();
			deployment.setRealm(realm);
			AdapterConfig adapterConfig = new AdapterConfig();
			adapterConfig.setAuthServerUrl("http://localhost:8080/auth");
			adapterConfig.setRealm(realm);
			deployment.setAuthServerBaseUrl(adapterConfig);
			return deployment;
		}
		
		return super.resolve(facade);
	}

	private String extractRealmFromDomain(String url) {
	    try {
	        URL parsedUrl = new URL(url);
	        String host = parsedUrl.getHost();

	        String[] parts = host.split("\\.");
	        if (parts.length > 0) {
	            return parts[0];
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null;
	}
}

