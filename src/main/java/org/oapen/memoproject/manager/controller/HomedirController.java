package org.oapen.memoproject.manager.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.jpa.HomedirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/homedir")
public class HomedirController {

	@Autowired
	HomedirRepository homedirRepository;
	
	@GetMapping()
	@ResponseBody
    public List<Homedir> listAll() {
		
		return homedirRepository.findAllByOrderByNameAsc();
		//return homedirRepository.findAll(Sort.by("username").ascending());
	}
	
	@GetMapping("/{id}")
	@ResponseBody
    public Homedir getById(
    	@PathVariable(required=true) String id
    ){
		Optional<Homedir> homedir = homedirRepository.findById(UUID.fromString(id));
		
		if (homedir.isPresent()) 
			return homedir.get(); 
		else 
			throw new EntityNotFoundException(id);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public Error homedirNotFound(EntityNotFoundException e) {
		
		return new Error(404,"Homedir [" + e.getId() + "] not found");
	}
	
}
