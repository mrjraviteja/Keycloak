package com.mrj.keycloak.multitenancy.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mrj.keycloak.multitenancy.security.JwtAuthConverter;

@RestController
@RequestMapping("/cust")
public class MultitenancyController {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthConverter.class);

	@GetMapping("/user")
	public ResponseEntity<Object> getUser(Principal principal)
	{
		logger.info("In getUser in MultitenancyController");
		JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
		String userName = (String) token.getTokenAttributes().get("name");
		String userEmail = (String) token.getTokenAttributes().get("email");
		String user = "Hello user "+userName + ", your email is: "+userEmail;
		return ResponseEntity.status(HttpStatus.OK).body(user);
	}
}
