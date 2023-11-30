package org.oapen.memoproject.manager.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.jpa.HomedirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomedirController {

	@Autowired
	HomedirRepository homedirRepository;
	
    /**
     * Returns OK if the user still has a session
     * 
     * @return
     */
    @GetMapping("/session")
    public String session() {
    	return "OK";
    }
	
	@GetMapping("/homedir")
	@ResponseBody
    public List<Homedir> list() {
		
		return homedirRepository.findAllByOrderByNameAsc();
		//return homedirRepository.findAll(Sort.by("username").ascending());
	}
	
	@GetMapping("/homedir/{id}")
	@ResponseBody
    public Homedir get(
    	@PathVariable(required=true) String id
    ){

		return homedirRepository.findById(UUID.fromString(id))
			.orElseThrow(() -> new EntityNotFoundException("Homedir not found [id=" + id + "]"));
	}
	
	@DeleteMapping("/homedir/{id}")
	@ResponseBody
    public void delete(
    	@PathVariable(required=true) UUID id
    ){
		homedirRepository.deleteById(id);
	}

	// TODO Write a test
    @PostMapping("/homedir")
	public Homedir save(@RequestBody Homedir homedir) {
    	
    	// Password cannot be empty. 
    	// When it is not edited it must stay the same and therefore is not included in the form,
    	// in that case fetch the encrypted password from the database and put it back in the user.
    	String newPw = homedir.getPassword(); 
    	if (newPw == null || newPw.isBlank()) {
    		
    		String username = homedir.getUsername();
    		Optional<String> oldPw = homedirRepository.findByUsername(username).map(u -> u.getPassword());
    		if (oldPw.isPresent()) homedir.setPassword(oldPw.get());
    	}
    	// But when a new password is sent with the form, then encrypt it and put it in the user.
    	else {
    		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
    		homedir.setPassword(enc.encode(newPw));
    	}
    	
    	homedir = homedirRepository.save(homedir);
    	return homedir;
	}

}
