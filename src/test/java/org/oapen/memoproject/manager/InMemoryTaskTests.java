package org.oapen.memoproject.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.entities.Script;
import org.oapen.memoproject.manager.entities.Task;
import org.oapen.memoproject.manager.jpa.HomedirRepository;
import org.oapen.memoproject.manager.jpa.ScriptRepository;
import org.oapen.memoproject.manager.jpa.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class InMemoryTaskTests {
	
	@Autowired
    private HomedirRepository homedirRepository;
	
	@Autowired
    private TaskRepository taskRepository;
	
	@Autowired
    private ScriptRepository scriptRepository;

	@Test
    public void givenTaskAndHomedir_whenSave_thenGetOk() {
    	
    	String NAME = "test name";
    	String USERNAME = RandomStringUtils.randomAlphabetic(10);

    	// We need a homedir to serve as owner of the tasks
    	Homedir hNew = new Homedir(USERNAME, NAME);
    	homedirRepository.save(hNew);
    	
    	Task tNew = new Task("File name","ext",hNew);
    	
		taskRepository.save(tNew);
    	
    	List<Task> tasks = taskRepository.findByHomedir(hNew);
    	
    	assertTrue(tasks.size() == 1);
    	assertEquals(hNew, tasks.get(0).getHomedir());
    }
	
	
	@Test
    public void givenTaskAndHomedirAndScript_whenSave_thenGetOk() {
    	
    	String NAME = "test name";
    	String USERNAME = RandomStringUtils.randomAlphabetic(10);

    	// We need a homedir to serve as owner of the tasks
    	Homedir hNew = new Homedir(USERNAME, NAME);
    	homedirRepository.save(hNew);
    	
    	Task tNew = new Task("File Name","ext",hNew);
	
    	Script sNew = new Script("A Name","MAIN");
    	
    	scriptRepository.save(sNew);
    	
    	tNew.setScript(sNew);
    	
    	taskRepository.save(tNew);
    	
    	System.out.println(tNew);
    	
    	List<Task> tasks = taskRepository.findByHomedir(hNew);
    	
    	System.out.println(tasks);
    	assertTrue(tasks.size() == 1);
    	assertEquals(sNew, tasks.get(0).getScript());
    	
	}	
	

}
