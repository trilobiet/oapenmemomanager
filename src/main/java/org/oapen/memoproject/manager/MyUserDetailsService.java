package org.oapen.memoproject.manager;

import java.io.Serializable;
import java.util.Optional;

import org.oapen.memoproject.manager.entities.User;
import org.oapen.memoproject.manager.jpa.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * Current implementation supports a single user 'admin' whose password is set 
 * in application.properties (Bcrypt)
 * https://bcrypt-generator.com/
 */
public class MyUserDetailsService implements UserDetailsService, Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) {
		
		Optional<User> user = userRepository.findByUsername(username);
		logger.info("User found by username " + username + ": " + user + " (when username is found, login may still fail when password is incorrect)");
		return user.orElseThrow(() ->  new UsernameNotFoundException("User '" + username + "' not found"));
	}

}		