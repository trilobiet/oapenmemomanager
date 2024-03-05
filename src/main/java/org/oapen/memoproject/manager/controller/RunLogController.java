package org.oapen.memoproject.manager.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.oapen.memoproject.manager.entities.RunLog;
import org.oapen.memoproject.manager.entities.Task;
import org.oapen.memoproject.manager.jpa.RunLogRepository;
import org.oapen.memoproject.manager.jpa.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

	@GetMapping("/runlog")
	@ResponseBody
    public List<Map<String, Object>> list(){
		
		List<RunLog> runlog = runLogRepository.findAll(Sort.by("date").descending());
		
		return runlog.stream().map(line -> {
			
			Map<String,Object> json = new HashMap<>();
			
			json.put("id", line.getId());
			json.put("message", line.getMessage());
			json.put("shortMessage", line.getShortMessage());
			json.put("date", line.getDate());
			json.put("success", line.isSuccess());
			json.put("client", line.getTask().getHomedir().getName());
			json.put("task", line.getTask().getFileName());
			return json;
		})
		.collect(Collectors.toList());
		
	}

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
