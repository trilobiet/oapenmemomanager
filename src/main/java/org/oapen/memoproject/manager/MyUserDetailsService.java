package org.oapen.memoproject.manager;

import java.util.Collection;
import java.util.Optional;

import org.oapen.memoproject.manager.entities.Setting;
import org.oapen.memoproject.manager.jpa.SettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);
	private static final String PASSWORD_KEY = ".admin.password"; 
	
	@Autowired
	SettingRepository settingRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		return new UserDetails() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isEnabled() {
				// Key must be present in table 'setting'
				return settingRepository.findByKey(PASSWORD_KEY).isPresent(); 
			}
			
			@Override
			public String getPassword() {
				
				Optional<Setting> op = settingRepository.findByKey(PASSWORD_KEY);
				if (op.isPresent()) return op.get().getValue();
				else {
					logger.error("No admin defined - provide key '.admin.password' in table 'setting'");
					throw new RuntimeException("No admin defined");
				}
			}

			@Override
			public String getUsername() {
				return "administrator";
			}

			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() { return null; }
			
			@Override
			public boolean isAccountNonExpired()  { return true; }
			
			@Override
			public boolean isAccountNonLocked() {  return true; }

			@Override
			public boolean isCredentialsNonExpired() { return true; }
		};

	}

}		