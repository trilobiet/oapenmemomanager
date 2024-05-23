package org.oapen.memoproject.manager.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.User;
import org.oapen.memoproject.manager.jpa.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * 
 * @author acdhirr
 *
 */

@RestController
//@Validated
@RequestMapping("/api")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	
    /**
	 * Shows your user credentials (accessible to anyone)
	 * 
	 * @return The signed in user
	 */
    @GetMapping("/signedinuser")
    public UserDetails signedInUser() {

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

	
	/**
	 * List all users
	 */
    @GetMapping("/user")
    @PreAuthorize("hasAuthority('admin')")
    public List<User> list() {
    	
    	return userRepository.findAllByOrderByUsernameAsc();
    }

    
	@GetMapping("/user/{id}")
	@PreAuthorize("hasAuthority('admin')")
	@ResponseBody
    public User get(
    	@PathVariable(required=true) UUID id
    ){
		return userRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Query not found [id=" + id + "]"));
	}
	
	
	@DeleteMapping("/user/{id}")
	@PreAuthorize("hasAuthority('admin')")
	@ResponseBody
    public void delete(
    	@PathVariable(required=true) UUID id
    ){
		if (isEditableUser(id)) userRepository.deleteById(id);
	}

	
    @PostMapping("/user")
    @PreAuthorize("hasAuthority('admin')")
    @ResponseBody
	public User save(@RequestBody User user) {

    	if (isEditableUser(user.getId())) { // Non-editable user is never saved (but do show this in the UI)
    		
    		System.out.println("Editable user " + user.getFullname());
    		
	    	// Password cannot be empty. 
	    	// When it is not edited it must stay the same and therefore is not included in the form,
	    	// in that case fetch the encrypted password from the database and put it back in the user.
	    	String newPw = user.getPassword(); 
	    	if (newPw == null || newPw.isBlank()) {
	    		
	    		String username = user.getUsername();
	    		Optional<String> oldPw = userRepository.findByUsername(username).map(u -> u.getPassword());
	    		if (oldPw.isPresent()) user.setPassword(oldPw.get());
	    	}
	    	// But when a new password is sent with the form, then encrypt it and put it in the user.
	    	else {
	    		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
	    		user.setPassword(enc.encode(newPw));
	    	}
			
	    	user = userRepository.save(user);
    	}
    	
    	return user;
    	
	}
    
    private boolean isEditableUser(UUID id) {
    	
    	// A new user
    	if (id == null) return true;
    	
    	// only return false for existing users that are set not-editable (admin)
    	return userRepository.findById(id).map(u -> u.isEditable()).orElse(true);
    }
    
    
}
