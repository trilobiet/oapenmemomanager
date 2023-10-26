package org.oapen.memoproject.manager.controller;

import java.util.List;
import java.util.Optional;

import org.oapen.memoproject.manager.entities.Setting;
import org.oapen.memoproject.manager.jpa.SettingRepository;
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
public class SettingController {

	@Autowired
	SettingRepository settingRepository;
	
	@GetMapping()
	@ResponseBody
    public List<Setting> listAll() {
		
		return settingRepository.findAllByOrderByKeyAsc();
		//return homedirRepository.findAll(Sort.by("username").ascending());
	}
	
	@GetMapping("/{key}")
	@ResponseBody
    public Setting getByKey(
    	@PathVariable(required=true) String key
    ){
		Optional<Setting> setting = settingRepository.findById(key);
		
		if (setting.isPresent()) 
			return setting.get(); 
		else 
			throw new EntityNotFoundException(key);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public Error settingNotFound(EntityNotFoundException e) {
		
		return new Error(404,"Setting [" + e.getId() + "] not found");
	}
	
}
