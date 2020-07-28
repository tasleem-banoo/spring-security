package com.spring.security.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.spring.security.filter.JwtRequestFilter;

import ch.qos.logback.core.net.server.Client;

/*Five Spring Security Concepts :
	1. Authentication - Who is user? (knowledge based (password/secret ques) or possesion based(key card/text msg) authentication)
	2. Authorization - Is he allowed to do operations?
	3. Principal - Logged in user
	4. Granted Authority - allowed authority to user
	5. Role - Group of authorities (eg. admin/user)*/

//We will deal with authentication manager builder which created authentication manager for us
@EnableWebSecurity //tell spring that this is web security configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	/*
	 * @Autowired DataSource dataSource;
	 */
	
	@Autowired
	MyUserDetailsService userDetailsService;
	
	@Autowired
	JwtRequestFilter jwtRequestFiler;
	
	/*Oauth is an authorization mechanism where services can authorize again each other on behalf of user once permission is given
	this is often referred to as delegating access.
	Terminologies in Oauth:
	*1. Resource - Protected resource on some other app e.g google drive has photos
	*2. Resource Owner - An entity capable of granting access to protected resource
	*3. Resource Server - Server that holding protected resource
	*4. Client - An application making protected resource req on behalf of the resource owner and with its authorization
	* Whose responsibility is to see that things are secure - its resource owner responsibility that they are providing right authorization. Resource server has coupled with it auth server which is responsible for right authorization
	*5. Authorization Server - The server issuing access tokens to the client
	*/
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//Set your configuration to this auth builder
		/*auth.inMemoryAuthentication()
		.withUser("Tasleem")
		.password("Banoo") //always deal with hashed password
		.roles("USER") 
		.and() //return an object that is of same state that in memory authentication return so that we can add more users.
		.withUser("Admin")
		.password("admin")
		.roles("ADMIN") ;*/
		
		//using in memory database with default schema that spring security creates - https://docs.spring.io/spring-security/site/docs/current/reference/html5/#user-schema
		/*auth.jdbcAuthentication().dataSource(dataSource)
		.withDefaultSchema()
		.withUser(
				User.withUsername("user")
				.password("user").roles("USER")
		)
		.withUser(
				User.withUsername("admin")
				.password("admin").roles("ADMIN")
		);*/
		
		//it will use default schema and default tables provided by spring which we have overridden. now we need to use different table
		//auth.jdbcAuthentication().dataSource(dataSource);
		
		//in this we can override default table names
		/*auth.jdbcAuthentication().dataSource(dataSource)
		.usersByUsernameQuery("Select username,password,enabled from users"
				+" where username=?")
		.authoritiesByUsernameQuery("Select username,authority from authorities"
				+ " where username=?");*/
		
		//using jpa authentication with my sql. auth provider talks to user details service which has one method which get user details by user name.
		//spring security has a way by which we can provide user details service and give it to security from which it can lookup user. 
		//in order to work spring security with jpa we need to create an instance of user details service
		//spring security call this service using jpa, it doesn't matter how this service is implemented. it can be hardcoded, text file or from db can return user.
		auth.userDetailsService(userDetailsService);
		
		//with ldap - lightweight directory access protocol 
		/*auth.ldapAuthentication().userDnPatterns("uid={0},ou=people")
		.groupSearchBase("ou=groups")
		.contextSource()
		.url("ldap://localhost:8870/dc=springframework,dc=org")
		.and()
		.passwordCompare()
		.passwordEncoder(new LdapShaPasswordEncoder())
		.passwordAttribute("userPassword");*/
		
	}
	
	@SuppressWarnings("deprecation")
	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return NoOpPasswordEncoder.getInstance();//It will say i am ok with clear text password. This is not recommended
	}
	
	//"/logout" is default logout url
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().
		authorizeRequests()
		.antMatchers("/authenticate").permitAll()
		.anyRequest().authenticated()
		.and().sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		//.antMatchers("/**").hasRole("ADMIN") //have access to all url
		//sequence of roles should be form most restrictive to least restrictive
		/*.antMatchers("/admin").hasRole("ADMIN")
		.antMatchers("/user").hasAnyRole("USER","ADMIN")
		.antMatchers("/","static/css","static/js").permitAll()
		.and().formLogin();*/
		http.addFilterBefore(jwtRequestFiler,UsernamePasswordAuthenticationFilter.class);
	}
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	/*We can have multiple authentication provider in application i.e oauth/ldap etc.
	Authentication manager will call supported authentication provider. auth provider lets it know that it support which type of authentication
	Auth provider use user details for authentication. 
	
	process of authentication :
	auth filter intercept auth request, it creates auth object with credentials and pass it to auth manager
	auth manager will find right auth provider based on support method and call authenticate method on that auth provider and pass that auth object
	auth provider looks up corresponding user in system by using user details service. this service return user details instance
	which auth provider verifies and authentication happen if it is successful it return auth object with principal and authorities filled in.
	this object is saved in thread context for further use. security context is associated to current thread.
	filter manages user session. this filter takes object from context and save it to thread local object so application can use it.
	then it returns all the way back to auth filter from where it started. if not successful then exception is thrown.
	*/
	
	/*Oauth flows :
		
	1. Authorization code flow - RO(Resource owner) tells client to get my resouurce from server(RS). client knows that i need to access
	RS. I know this RS . It will not allow me to directly access it has oauth implemented. I know there is authorization Server (AS), so he will directly go to AS and tells that my user wants to access this resource from your RS.
	AS says that ok my RO might have asked you to contact me . I am not sure let me talk to RO. It will tell RO that this client want to access this resource.Do you want me to allow it. If RO confirms, AS will give auth token to client which is short lived.
	client uses this token and contact AS to get a second token which is acess token. now client can use this access token to contact RS and access Resource. RS will verify that token by any means i.e by himself or can contact  to AS and provides resource.
	This is the best and safest flow in oauth.
	
	2. Implicit Flow - RO(Resource owner) tells client to get my resouurce from server(RS). client knows that i need to access
	RS. I know this RS . It will not allow me to directly access it has oauth implemented. I know there is authorization Server (AS), so he will directly go to AS and tells that my user wants to access this resource from your RS.
	AS says that ok my RO might have asked you to contact me . I am not sure let me talk to RO. It will tell RO that this client want to access this resource.Do you want me to allow it. If RO confirms, AS will give access token directly to client instead of auth token which is short lived.
	Now client can use this access token to contact RS and access Resource. RS will verify that token by any means i.e by himself or can contact  to AS and provides resource.
	Drawback - Client has Access Token(AT) which it can use anytime to contact RS. If someone else can has AT then he can access resources. In first flow also client has access token,
	but in that exchange of AT is done more securely. second flow is useful only with certain kind of application specifically JS application where the AT is not really you would assume that it is secure.
	We have JS application running on Client we know that AT will be exchanged over the wire and AT gonna to be sit on browser. that's why second simplified flow will be used here.
	
	3. Client Credential flow - When the client is well trusted (confidential clients). e.g client which is written by ourselves is trustworthy. 
	The Big Use Case - oauth we know is authorization between services. oauth is perfect for authorization between micro services.
	Now suppose micro service(MS)1 want to call an api in MS2. MS2 has access to database. MS2 has auth server to make sure that whoever is accessing this service cam get data only they need.
	MS1 first call AS. AS will return certain special key or client id(AT) to MS1. MS1 will call MS2 with access token It will give only allowed information to MS1.
	
	*Oauth is not meant for authentication, it is only for authorization then how can some app say that login with FB or Google. how oauth can be used for authentication
	*this is a new developed way where people found a way to use oauth for authentication
	*We can either build authentication by ourselves or there is choice where we can make oauth call to google to get user profile info. if google verifies it , it will return email id/info of that user and 
	*our app trust google authentication and don't do authentication himself. It will store that user profile info in security context to simulate logged in user.
	*/
	

}
