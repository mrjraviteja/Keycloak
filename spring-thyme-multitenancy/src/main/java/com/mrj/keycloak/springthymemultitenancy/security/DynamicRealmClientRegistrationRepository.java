package com.mrj.keycloak.springthymemultitenancy.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DynamicRealmClientRegistrationRepository implements ClientRegistrationRepository{

    private final String authorizationEndpointBaseUri;
    private final Map<String, String> clientCredentials = new HashMap<>();

    public DynamicRealmClientRegistrationRepository(String authorizationEndpointBaseUri, Map<String, String> clientCredentials) {
        this.authorizationEndpointBaseUri = authorizationEndpointBaseUri;
        this.clientCredentials.putAll(clientCredentials);
    }

    @Override
    public ClientRegistration findByRegistrationId(String clientRegistrationId) {
        String realmName = extractRealmFromRequest(); 
        String clientId = clientCredentials.get(realmName);
        String authUrl = authorizationEndpointBaseUri + "/" + realmName + "/protocol/openid-connect/auth";

        return ClientRegistration.withRegistrationId(clientRegistrationId)
                .clientId(clientId)
                .authorizationUri(authUrl) 
                .build();
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
