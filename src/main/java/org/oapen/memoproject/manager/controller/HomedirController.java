package org.oapen.memoproject.manager.controller;

import java.util.List;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.jpa.HomedirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomedirController {

	@Autowired
	HomedirRepository homedirRepository;
	
	@GetMapping("/homedir")
	@ResponseBody
    public List<Homedir> listAll() {
		
		return homedirRepository.findAllByOrderByNameAsc();
		//return homedirRepository.findAll(Sort.by("username").ascending());
	}
	
	@GetMapping("/homedir/{id}")
	@ResponseBody
    public Homedir getById(
    	@PathVariable(required=true) String id
    ){

		return homedirRepository.findById(UUID.fromString(id))
			.orElseThrow(() -> new EntityNotFoundException("Homedir not found [id=" + id + "]"));
	}
	
}
