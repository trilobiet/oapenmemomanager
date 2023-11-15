package org.oapen.memoproject.manager;

import java.util.Optional;

import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.jpa.HomedirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class ClientDetailsService implements UserDetailsService {
	
	@Autowired
	HomedirRepository homedirRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Optional<Homedir> hd = homedirRepository.findByUsername(username);
		return hd.orElseThrow(() ->  new UsernameNotFoundException("User '" + username + "' not found"));
	}

}
