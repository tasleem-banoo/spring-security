package com.spring.security.config;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.spring.security.entity.MyUserDetails;

@Service
public class MyUserDetailsService implements UserDetailsService {
	
	/*
	 * @Autowired 
	 * UserRepository userRepository;
	 */
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		/*Optional<User> user =userRepository.findByUserName(username);
		user.orElseThrow(()->new UsernameNotFoundException("Not Found: "+username));
		return user.map(MyUserDetails::new).get();*/
		
		return new MyUserDetails(username,"pass",new ArrayList<>());
		
	}

}
