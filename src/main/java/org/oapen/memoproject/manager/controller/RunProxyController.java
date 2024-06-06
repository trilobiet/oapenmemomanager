package org.oapen.memoproject.manager.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * Forwards all local /run/ or /dryrun/ requests to another application (oapenmemotaskrunner)
 * 
 */

@RestController
@RequestMapping("/runproxy")
public class RunProxyController {

	@Autowired
	private Environment env;
	
	private static final Logger logger = 
		LoggerFactory.getLogger(RunProxyController.class);
	
	
	@GetMapping(value = "/run/{taskId}")
	@ResponseBody
    @Transactional(timeout = 300_000)
	private ResponseEntity<?> run(
			@PathVariable UUID taskId,
			@RequestParam(name = "dry", required = false, defaultValue = "false") boolean isDry
		) throws URISyntaxException {
		
		// "http://localhost:8080/";
		String baseUrl = env.getProperty("taskrunner.url");
		StringBuilder taskrunnerUri = new StringBuilder(baseUrl);
		if(!baseUrl.endsWith("/")) taskrunnerUri.append("/");
		if (isDry) taskrunnerUri.append("dryruntask/");
		else taskrunnerUri.append("runtask/");
		taskrunnerUri.append(taskId);
		
		RestTemplate restTemplate = new RestTemplate();
		
		URI uri = new URI(taskrunnerUri.toString());
		// System.out.println(uri);
		logger.info("Calling taskrunner from manager " + uri.toString());
		
		try {
			ResponseEntity<?> responseEntity = restTemplate.getForEntity(uri, String.class);
			return responseEntity;
			
		} catch (HttpStatusCodeException e) {

			ResponseEntity<String> responseEntity = 
				ResponseEntity.status(e.getRawStatusCode())
				.header("Content-type", "application/json").body(e.getResponseBodyAsString());

			return responseEntity;
			
		} catch (Exception e) { // proxied server may be unreachable
			
			Map<String,String> error = new HashMap<>();
			error.put("message", e.getMessage());
			
			return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	

}
