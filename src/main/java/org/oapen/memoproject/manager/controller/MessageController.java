package org.oapen.memoproject.manager.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.entities.RunLog;
import org.oapen.memoproject.manager.jpa.HomedirRepository;
import org.oapen.memoproject.manager.jpa.QueryRepository;
import org.oapen.memoproject.manager.jpa.RunLogRepository;
import org.oapen.memoproject.manager.jpa.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
	
	@Autowired
	HomedirRepository homedirRepository;

	@Autowired
	RunLogRepository runLogRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	QueryRepository queryRepository;
	
	@GetMapping("/hello")
    public String hello() {
    	
    	UUID id = UUID.fromString("546f5a85-a903-11ec-a033-9e82efd8bbef");
    	
    	System.out.println(id);
    	
    	Optional<Homedir> h; // = homedirRepository.findById(id);
    	
    	h = homedirRepository.findByUsername("comics-grid");
    	
    	Homedir homedir = h.get();
    	
    	System.out.println(homedir);
    	
    	taskRepository.findByHomedir(homedir).stream().forEach(task -> {
    		
    		List<RunLog> r = runLogRepository.findByTask(task);
    		
    		System.out.println("\n--------------------------------");
    		r.forEach(System.out::print);
    	});
    	
    	
        return "Full Stack Java with Spring Boot & VueJS!";
    }

}