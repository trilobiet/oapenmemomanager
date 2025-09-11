package org.oapen.memoproject.manager.controller;

import static java.util.Map.entry;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author acdhirr
 *
 */

@RestController
@RequestMapping("/api")
public class ConfigController {
	
	@Value("${path.customresources}") 
	private String filesRoot; 

	@Value("${path.clientsurl}") 
	private String clientsUrl; 
	
    @GetMapping("/clientconfig")
    public Map<String,String> getClientConfig() {
    	    
    	Map<String, String> map = Map.ofEntries(
    	    entry("customResources", filesRoot),
    	    entry("clientsUrl", clientsUrl)
    	);    	

    	return map;
    }

}
