package com.spring.security.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class MyUserDetails implements UserDetails{

	private static final long serialVersionUID = 6105670461514304247L;

	private String userName;
	private String password;
	private boolean active=true;
	private List<GrantedAuthority> authority;
	
	
	public MyUserDetails() {
	}
	
	public MyUserDetails(String userName) {
		this.userName=userName;
	}
	
	public MyUserDetails(User user) {
		this.userName=user.getUserName();
		this.password = user.getPassword();
		this.active = user.isActive();
		this.authority= Arrays.stream(user.getRoles().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	public MyUserDetails(String username, String pass, List<GrantedAuthority> roles) {
		this.userName=username;
		this.password = pass;
		this.authority= roles;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authority;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return active;
	}

}
