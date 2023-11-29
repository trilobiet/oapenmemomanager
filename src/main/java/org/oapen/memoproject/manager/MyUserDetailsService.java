package org.oapen.memoproject.manager;

import java.util.Collection;

import org.oapen.memoproject.manager.jpa.HomedirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Current implementation supports a single user 'admin' whose password is set 
 * in application.properties (Bcrypt)
 * https://bcrypt-generator.com/
 */
public class MyUserDetailsService implements UserDetailsService {
	
	private final String bcryptPassword;
	
	public MyUserDetailsService(String bcryptPassword) {
		this.bcryptPassword = bcryptPassword;
	}

	@Autowired
	HomedirRepository homedirRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		return new UserDetails() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public String getPassword() {
				return bcryptPassword;
			}

			@Override
			public String getUsername() {
				return "admin";
			}

			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() { return null; }
			@Override
			public boolean isAccountNonExpired()  { return true; }
			@Override
			public boolean isAccountNonLocked() {  return true; }
			@Override
			public boolean isCredentialsNonExpired() { return true; }
			@Override
			public boolean isEnabled() { return true; }
		};

	}

}		