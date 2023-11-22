package org.oapen.memoproject.manager.controller;

import java.util.List;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.entities.Task;
import org.oapen.memoproject.manager.jpa.HomedirRepository;
import org.oapen.memoproject.manager.jpa.TaskRepository;
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
public class TaskController {

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	HomedirRepository homedirRepository;
	
	
	@GetMapping("/task")
	@ResponseBody
    public List<Task> list(){
		
		return taskRepository.findAll(); 
	}
	
	@GetMapping("/homedir/{id}/task")
	@ResponseBody
    public List<Task> listForHomedir(
    	@PathVariable(required=true) String id
    ){
		
		Homedir homedir = new Homedir();
		homedir.setId(UUID.fromString(id));
		return taskRepository.findByHomedir(homedir);
	}
	
	@GetMapping("/task/{id}")
	@ResponseBody
    public Task get(
    	@PathVariable(required=true) UUID id
    ){
		return taskRepository.findById(id)
		.orElseThrow(() -> new EntityNotFoundException("Task not found [id=" + id + "]"));
	}

	
	@DeleteMapping("/task/{id}")
	@ResponseBody
    public void delete(
    	@PathVariable(required=true) UUID id
    ){
		taskRepository.deleteById(id);
	}

	// Save task to a homedir
    @PostMapping("/task")
	public Task save(
		@RequestBody final Task task 
	){
    	
    	// Content-type: application/json
    	// Sample request body:
    	// {"id":"8f6f600f-7837-497b-8a28-999736be531c","fileName":"my_export","extension":"xml",
    	// "startDate":"2023-10-31","frequency":"M","public":false,"active":false}    	
    	
   		return taskRepository.save(task);
	}
	
	// Save task to a homedir
    @PostMapping("/homedir/{id}/task")
	public Task saveNew(
		@PathVariable("id") UUID hId,
		@RequestBody final Task task 
	){
    	
    	return homedirRepository.findById(hId).map(homedir -> {
    		task.setHomedir(homedir);
    		return taskRepository.save(task);
    	})
    	.orElseThrow(() -> new EntityNotFoundException("Homedir not found [id=" + hId + "]"));
	}
	
}
