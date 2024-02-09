package com.mrj.keycloaksample.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class KeycloakController {
	
	@GetMapping("/Tester")
	public ResponseEntity<String> getTesterDetails()
	{
		return ResponseEntity.ok("Hello Tester");
	}
	
	@GetMapping("/User")
	public ResponseEntity<String> getUserDetails()
	{
		return ResponseEntity.ok("Hello User");
	}
}
