package org.oapen.memoproject.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 
 * @author acdhirr
 *
 */

@RestController
@Validated
@RequestMapping("/api")
public class UserController {
	
	/**
	 * Shows your user credentials (accessible to anyone)
	 * 
	 * @return The signed in user
	 */
    @GetMapping("/user")
    public UserDetails user() {

    	UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	return user;
    }
    
    /**
     * Returns OK if the user still has a session
     * 
     * @return
     */
    @GetMapping("/session")
    public String session() {
    	
    	return "OK";
    }
    
}
