package org.oapen.memoproject.manager.controller;

import java.util.List;

import org.oapen.memoproject.manager.entities.Setting;
import org.oapen.memoproject.manager.jpa.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasAuthority('admin')")
public class SettingController {

	@Autowired
	SettingRepository settingRepository;
	
	@GetMapping("/setting")
	@ResponseBody
    public List<Setting> list() {
		
		return settingRepository.findAllByOrderByKeyAsc();
		//return homedirRepository.findAll(Sort.by("username").ascending());
	}
	
	@GetMapping("/setting/{key}")
	@ResponseBody
    public Setting get(
    	@PathVariable(required=true) String key
    ){
		return settingRepository.findById(key)
		.orElseThrow(() -> new EntityNotFoundException("Setting not found [key=" + key + "]"));
	}
	
	@DeleteMapping("/setting/{key}")
	@ResponseBody
    public void delete(
    	@PathVariable(required=true) String key
    ){
		settingRepository.deleteById(key);
	}
	
    @PostMapping("/setting")
    @ResponseBody
	public Setting save(@RequestBody Setting setting) {
    	
    	// Content-type: application/json
    	// Sample request body:
    	// {"key":"a","value":"b"}
    	
    	return settingRepository.save(setting);
	}
	
}
