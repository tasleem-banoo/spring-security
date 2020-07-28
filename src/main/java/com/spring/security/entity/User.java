package com.spring.security.entity;

import lombok.Data;

//@Entity
//@Table(name="USER")
@Data
public class User {
	
	//@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String userName;
	private String password;
	private boolean active;
	private String roles;

}
