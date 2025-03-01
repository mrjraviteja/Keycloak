package com.mrj.keycloakcustomjwt.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;


@Component

public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken>{
	
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthConverter.class);

	private KeycloakConfiguration properties;
	
	public JwtAuthConverter(KeycloakConfiguration properties)
	{
		this.properties = properties;
		
	}
	
	private JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
	
	@Override
	public AbstractAuthenticationToken convert(Jwt source) {
//		logger.info("Testing in JwtAuthConverter");
//		logger.info(Arrays.toString(source.getClaims().entrySet().toArray()));
//		logger.info(source.getClaimAsString("userclaim"));
		
		String customClaimValue = source.getClaimAsString("userclaim");

        // Check the value of the custom claim
        if (!"this is a user attribute".equals(customClaimValue)) {
            // Deny access if the custom claim value is not as expected
            throw new BadCredentialsException("Invalid user claim value");
        }
		
		Collection<GrantedAuthority> authorities = Stream.concat(jwtGrantedAuthoritiesConverter.convert(source).stream(), extractRoles(source).stream())
				.collect(Collectors.toSet());
		
		authorities.add(new SimpleGrantedAuthority("USER_CLAIM_" + customClaimValue));
		
		return new JwtAuthenticationToken(source,authorities,getPrincipalName(source));
	}
	
	private String getPrincipalName(Jwt jwt)
	{
		String name = JwtClaimNames.SUB;
		
		if(properties.getPrincipalAttribute() != null)
		{
			name = properties.getPrincipalAttribute();
		}
		return jwt.getClaim(name);
	}
	
	private Collection<? extends GrantedAuthority> extractRoles(Jwt jwt)
	{
		Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
		
		Map<String, Object> resource;
		Collection<String> resourceRoles;
		
		if(resourceAccess == null || 
				(resource = (Map<String,Object>) resourceAccess.get(properties.getResourceId())) == null || 
				(resourceRoles = (Collection<String>) resource.get("roles")) == null)
		{
			return Set.of();
		}
		
		return resourceRoles.stream().map(r -> new SimpleGrantedAuthority("ROLE_"+ r)).collect(Collectors.toSet());
	}
}