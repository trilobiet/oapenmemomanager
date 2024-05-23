package org.oapen.memoproject.manager.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.oapen.memoproject.manager.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/download")
public class DownloadController {
	
	@Autowired
	FileService fileService;
	
    @GetMapping(value = "/{dirName}/{fileName}")
    @ResponseBody
    public ResponseEntity<?> getFile(
    		@PathVariable String dirName,
    		@PathVariable String fileName
    	) throws IOException {
    	
    	
    	Optional<Pair<InputStream, String>> oExport = fileService.getExport(dirName,fileName);
    	
    	if (oExport.isPresent()) {
    		
    		Pair<InputStream, String> export = oExport.get();
    		
			BodyBuilder bb = ResponseEntity.ok()
				.header("Content-Type", export.getSecond() + ";charset=utf-8")
				.header("Cache-Control", "no-store")
				.header("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

				// No need to close, Spring handles this
				InputStreamResource res = new InputStreamResource(export.getFirst());
				
			return bb.body(res);
    	} 
    	else {
    		// NOT FOUND 
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("RESOURCE NOT FOUND");
    	}
    }

}
