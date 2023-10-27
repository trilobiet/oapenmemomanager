package org.oapen.memoproject.manager.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Query;
import org.oapen.memoproject.manager.jpa.QueryRepository;
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
@RequestMapping("/api/setting")
public class QueryController {

	@Autowired
	QueryRepository queryRepository;
	
	@GetMapping()
	@ResponseBody
    public List<Query> listAll() {
		
		return queryRepository.findAllByOrderByNameAsc();
		//return homedirRepository.findAll(Sort.by("username").ascending());
	}
	
	@GetMapping("/{id}")
	@ResponseBody
    public Query getById(
    	@PathVariable(required=true) String id
    ){
		Optional<Query> query = queryRepository.findById(UUID.fromString(id));
		
		if (query.isPresent()) 
			return query.get(); 
		else 
			throw new EntityNotFoundException(id);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public Error settingNotFound(EntityNotFoundException e) {
		
		return new Error(404,"Query [" + e.getId() + "] not found");
	}
	
}
