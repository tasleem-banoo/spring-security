package com.spring.security.repository;

import java.util.Optional;

import com.spring.security.entity.User;

public interface UserRepository //extends JpaRepository<User, Integer>
{
	
	Optional<User> findByUserName(String userName);

}
