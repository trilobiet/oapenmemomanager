package org.oapen.memoproject.manager.controller;

import java.util.List;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.RunLog;
import org.oapen.memoproject.manager.entities.Task;
import org.oapen.memoproject.manager.jpa.RunLogRepository;
import org.oapen.memoproject.manager.jpa.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RunLogController {

	@Autowired
	RunLogRepository runLogRepository;

	@Autowired
	TaskRepository taskRepository;

	@GetMapping("/task/{id}/runlog")
	@ResponseBody
    public List<RunLog> listForTask(
    	@PathVariable(required=true) UUID id
    ){
		
		Task task = new Task();
		task.setId(id);
		return runLogRepository.findByTaskOrderByDateDesc(task); 
	}

	@GetMapping("/task/{id}/runlog/top")
	@ResponseBody
    public List<RunLog> top10ForTask(
    	@PathVariable(required=true) UUID id
    ){
		
		Task task = new Task();
		task.setId(id);
		return runLogRepository.findTop10ByTaskOrderByDateDesc(task); 
	}
	
}
