package org.oapen.memoproject.manager.controller;

import java.io.IOException;
import java.util.Optional;

import org.oapen.memoproject.manager.entities.Export;
import org.oapen.memoproject.manager.jpa.ExportRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
	ExportRepository exportRepository;
	
    @GetMapping(value = "/{idTask}/{fileName}")
    @ResponseBody
    public ResponseEntity<String> getFile(
    		@PathVariable String idTask,
    		@PathVariable String fileName
    	) throws IOException {
    	
    	Optional<Export> oExport = exportRepository.findByIdTask(idTask);
    	
    	if (oExport.isPresent()) {
    		
    		Export export = oExport.get();
    		
			BodyBuilder bb = ResponseEntity.ok()
				.header("Content-Type", export.getMimetype() + ";charset=utf-8")
				.header("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

			return bb.body(export.getContent());
    	} 
    	else {
    		// NOT FOUND 
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("RESOURCE NOT FOUND");
    	}
    }

}
