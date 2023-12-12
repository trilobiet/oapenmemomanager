package org.oapen.memoproject.manager.controller;

import java.util.List;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Query;
import org.oapen.memoproject.manager.jpa.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class QueryController {

	@Autowired
	QueryRepository queryRepository;
	
	@GetMapping("/query")
	@ResponseBody
    public List<Query> list() {
		
		return queryRepository.findAllByOrderByNameAsc();
		//return homedirRepository.findAll(Sort.by("username").ascending());
	}
	
	@GetMapping("/query/{id}")
	@ResponseBody
    public Query get(
    	@PathVariable(required=true) String id
    ){
		return queryRepository.findById(UUID.fromString(id))
		.orElseThrow(() -> new EntityNotFoundException("Query not found [id=" + id + "]"));
	}
	
	@DeleteMapping("/query/{id}")
	@ResponseBody
    public void delete(
    	@PathVariable(required=true) UUID id
    ){
		queryRepository.deleteById(id);
	}

    @PostMapping("/query")
	public Query save(@RequestBody Query query) {
    	
    	// Content-type: application/json
    	// Sample request body:
    	// {"id":"6145e100-82b1-11ec-a8a3-0242ac120002","name":"Test","body":"SELECT * from Table","params":"a=b","notes":"how do you do?"}
    	
    	query = queryRepository.save(query);
    	return query;
	}
	
}
