package com.mrj.keycloak.multitenancy.security;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import com.mrj.keycloak.multitenancy.security.MultitenancyProperties.RealmConfig;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthConverter.class);

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    
    private final MultitenancyProperties multiTenancyProperties;
    
    public JwtAuthConverter(MultitenancyProperties multiTenancyProperties)
    {
    	this.multiTenancyProperties = multiTenancyProperties;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
    	logger.info("In covert in JwtAuthConverter");
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()).collect(Collectors.toSet());
        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
    }

    private String getPrincipalClaimName(Jwt jwt) {
    	logger.info("In getPrincipalClaimName in JwtAuthConverter");
        String claimName = JwtClaimNames.SUB;
        String iss = jwt.getClaimAsString(JwtClaimNames.ISS);
        Optional<RealmConfig> optrealm = multiTenancyProperties.getRealms().stream().filter(pred -> pred.getIssuerUri().equalsIgnoreCase(iss)).findAny();
        RealmConfig realm = optrealm.get();
        if (realm.getPrincipalAttribute() != null) {
            claimName = realm.getPrincipalAttribute();
        }
        return jwt.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
    	logger.info("In extractResourceRoles in JwtAuthConverter");
    	String iss = jwt.getClaimAsString(JwtClaimNames.ISS);
    	Optional<RealmConfig> optrealm = multiTenancyProperties.getRealms().stream().filter(pred -> pred.getIssuerUri().equalsIgnoreCase(iss)).findAny();
        RealmConfig realm = optrealm.get();
    	String resourceId = realm.getResourceId();
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        Map<String, Object> resource;
        Collection<String> resourceRoles;
        if (resourceAccess == null
                || (resource = (Map<String, Object>) resourceAccess.get(resourceId)) == null
                || (resourceRoles = (Collection<String>) resource.get("roles")) == null) {
            return Set.of();
        }
        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}