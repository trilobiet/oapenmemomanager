package org.oapen.memoproject.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Optional;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
public class FileService {
	
	//@Autowired
	@Value("${path.exports}") 
	private String filesRoot; 

	
	public Optional<Pair<InputStream, String>> getExport(String dirName, String fileName) {
		
		File file = new File(filesRoot + "/" + dirName + "/" + fileName);
		FileInputStream fis;

		try {
			fis = new FileInputStream(file);
			String mimeType = getMimeType(fileName, fis);
			return Optional.of(Pair.of(fis, mimeType));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return Optional.empty();
		}
	}
	
	
    private String getMimeType(String fileName, InputStream is) {
    	
    	/* Tika detects RSS as "application/rss+xml"
    	   which is not recognized by most browsers. */
    	if (fileName.toLowerCase().endsWith(".rss")) 
    		return "text/xml"; 
    	
		Tika tika = new Tika();
		
		try {
			FileInputStream fis = (FileInputStream) is;
			// Use this channel to reset the inputstream
			// after tika inspected it 
			// https://stackoverflow.com/questions/1094703/java-file-input-with-rewind-reset-capability
			FileChannel ch = fis.getChannel(); 
			String mt = tika.detect(is);
			ch.position(0);
			return mt;
		} catch (IOException e) {
			return "application/octet-stream";
		}
    }

    
}
