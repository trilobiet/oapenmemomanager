package org.oapen.memoproject.manager.config;

import java.util.Arrays;

import org.oapen.memoproject.manager.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity( // need this to use @PreAuthorize on methods
  prePostEnabled = true, 
  securedEnabled = true, 
  jsr250Enabled = true
)
public class SecurityConfig {
	
	/*
	 * @Bean public WebSecurityCustomizer webSecurityCustomizer() { // configure Web
	 * security... }
	 */
	
	// Use to generate test passwords https://bcrypt-generator.com/

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		
		return new MyUserDetailsService();
		
	}
	
	// configure SecurityFilterChain
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http
			.cors().and()
			.csrf().disable() 
			.authorizeRequests()
			.antMatchers("/assets/**","/file/**","/favicon.ico")
            	.permitAll()
			.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
			.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
				.logoutSuccessUrl("/login")
			.and()	
				.rememberMe()
				.key("somethingextremelysecretandutterlyincomprehensible")
				.tokenValiditySeconds(86400) // 1 day, default = two weeks validity
			; 
		
		return http.build();
	}
	
	
	@Bean
	// Enable cors() in configure method to use
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// Enable cors for localhost (development uses another domain/port)
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000","http://localhost:8080"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
			
	
}
