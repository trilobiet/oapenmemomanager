package org.oapen.memoproject.manager.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.entities.Task;
import org.oapen.memoproject.manager.jpa.TaskRepository;
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
@RequestMapping("/api/task")
public class TaskController {

	@Autowired
	TaskRepository taskRepository;

	@GetMapping("")
	@ResponseBody
    public List<Task> listAll(){
		
		return taskRepository.findAll(); 
	}
	
	@GetMapping("/homedir/{id}")
	@ResponseBody
    public List<Task> listForHomedir(
    	@PathVariable(required=true) String id
    ){
		
		Homedir homedir = new Homedir();
		homedir.setId(UUID.fromString(id));
		return taskRepository.findByHomedir(homedir);
	}
	
	@GetMapping("/{id}")
	@ResponseBody
    public Task getById(
    	@PathVariable(required=true) String id
    ){
		Optional<Task> task = taskRepository.findById(UUID.fromString(id));
		
		if (task.isPresent()) 
			return task.get(); 
		else 
			throw new EntityNotFoundException(id);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public Error taskNotFound(EntityNotFoundException e) {
		
		return new Error(404,"Task [" + e.getId() + "] not found");
	}
	
}
