package com.mrj.learningportal.configuration;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken>{
	
	private LearningPortalConfiguration properties;
	
	public JwtAuthConverter(LearningPortalConfiguration properties)
	{
		this.properties = properties;
	}
	
	private JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

	@Override
	public AbstractAuthenticationToken convert(Jwt source) {
		Collection<GrantedAuthority> authorities = Stream.concat(jwtGrantedAuthoritiesConverter.convert(source).stream(), extractRoles(source).stream()).collect(Collectors.toSet());
		
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
				(resource = (Map<String, Object>) resourceAccess.get(properties.getResourceId())) == null ||
				(resourceRoles = (Collection<String>) resource.get("roles")) == null)
		{
			return Set.of();
		}
		return resourceRoles.stream().map(r -> new SimpleGrantedAuthority("ROLE_"+r)).collect(Collectors.toSet());
	}
}
