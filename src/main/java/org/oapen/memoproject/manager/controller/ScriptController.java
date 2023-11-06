package org.oapen.memoproject.manager.controller;

import java.util.List;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Script;
import org.oapen.memoproject.manager.entities.Script.ScriptType;
import org.oapen.memoproject.manager.jpa.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
public class ScriptController {

	@Autowired
	ScriptRepository scriptRepository;

	@GetMapping("/script")
	@ResponseBody
    public List<Script> list(){
		
		return scriptRepository.findAll(); 
	}

	@GetMapping("/script/snippets")
	@ResponseBody
    public List<Script> listSnippets(){
		
		return scriptRepository.findByType(ScriptType.SNIP, Sort.by("name").ascending());
	}
	
	@GetMapping("/script/{id}")
	@ResponseBody
    public Script get(
    	@PathVariable(required=true) UUID id
    ){
		return scriptRepository.findById(id)
		.orElseThrow(() -> new EntityNotFoundException("Script not found [id=" + id + "]"));
	}

	
	@DeleteMapping("/script/{id}")
	@ResponseBody
    public void delete(
    	@PathVariable(required=true) UUID id
    ){
		scriptRepository.deleteById(id);
	}
	
	
    @PostMapping("/script")
	public Script save(
		@PathVariable("id") UUID hId,
		@RequestBody final Script script 
	){
    	
    	// Content-type: application/json
   		return scriptRepository.save(script);
	}
	
}
