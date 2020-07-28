package com.spring.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.security.config.MyUserDetailsService;
import com.spring.security.entity.AuthenticationRequest;
import com.spring.security.entity.AuthenticationResponse;
import com.spring.security.entity.JwtUtil;

@RestController
public class HomeResourceController {

	@Autowired
	AuthenticationManager authManager;
	
	@Autowired
	MyUserDetailsService userDetailsService;
	
	@Autowired
	JwtUtil jwtUtil;
	
	@GetMapping("/")
	public String home() {
		return ("<h1>Welcome</h1>");
	}
	
	@GetMapping("/user")
	public String user() {
		return ("<h1>Welcome User</h1>");
	}
	
	@GetMapping("/admin")
	public String admin() {
		return ("<h1>Welcome Admin</h1>");
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authRequest) throws Exception{
	 try {
		authManager.authenticate(new UsernamePasswordAuthenticationToken
				(authRequest.getUsername(), authRequest.getPassword()));
	 } catch (BadCredentialsException e) {
		// throw new Exception("Incorrect username or password "+e);
		 throw e;
	 }
	 
	 final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
	 
	 final String jwt = jwtUtil.generateToken(userDetails);
	 
	 return ResponseEntity.ok(new AuthenticationResponse(jwt));
	 
	}
}
