package com.spring.security.entity;

import lombok.Data;

@Data
public class AuthenticationRequest {
	
	private String username;
	private String password;

}
