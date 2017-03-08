package io.swagger.configuration.security;



import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.swagger.configuration.security.jwt.JWTAuthenticationFilter;


import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
     
    	// disable caching
        http.headers().cacheControl();
        
        http.csrf().disable() // disable csrf for our requests.
    	 .authorizeRequests()
    	   .anyRequest().authenticated()
    	   .and()
    	  // And filter other requests to check the presence of JWT in header
           .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
    	
    }

  
}