package org.oapen.memoproject.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.entities.Script;
import org.oapen.memoproject.manager.entities.Script.ScriptType;
import org.oapen.memoproject.manager.entities.Task;
import org.oapen.memoproject.manager.jpa.HomedirRepository;
import org.oapen.memoproject.manager.jpa.ScriptRepository;
import org.oapen.memoproject.manager.jpa.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class InMemoryScriptTests {
	
	@Autowired
    private HomedirRepository homedirRepository;
	
	@Autowired
    private TaskRepository taskRepository;
	
	@Autowired
    private ScriptRepository scriptRepository;
	
	@Test
    public void givenScript_whenSave_thenOk() {
		
		String NAME = RandomStringUtils.randomAlphabetic(10);
    	
    	Script sNew = new Script(NAME,ScriptType.MAIN);
    	
    	// Task holds a reference to script, so save the script first
    	scriptRepository.save(sNew);
    	
    	Optional<Script> script = scriptRepository.findByName(NAME);
    	
    	assertTrue(script.isPresent());
    	assertEquals(NAME, script.get().getName());
	}	
	

	@Test
    public void givenNameNotUnique_whenSave_thenError() {
		
		String NAME = RandomStringUtils.randomAlphabetic(10);
    	
    	Script sNew1 = new Script(NAME,ScriptType.MAIN);
    	Script sNew2 = new Script(NAME,ScriptType.SNIP);
    	
    	assertThrows(DataIntegrityViolationException.class, () -> {
    		scriptRepository.save(sNew1);
    		scriptRepository.save(sNew2);
    	});
    	
	}	
	
	
	@Test
    public void givenScript_whenAddTask_whenSave_thenOk() {
    	
    	String NAME = "test name";
    	String USERNAME = RandomStringUtils.randomAlphabetic(10);
    	
    	// We need a homedir to serve as owner of the tasks
    	Homedir hNew = new Homedir(USERNAME, NAME);
    	hNew = homedirRepository.save(hNew);
    	
    	Task tNew = new Task("File Name","ext",hNew);
    	Script sNew = new Script("A Name",ScriptType.MAIN);
    	tNew.setScript(sNew);
    	
    	taskRepository.save(tNew);
    	
    	List<Task> tasks = taskRepository.findByHomedir(hNew);
    	
    	assertTrue(tasks.size() == 1);
    	assertEquals(sNew, tasks.get(0).getScript());
    	
    	System.out.println(tasks);
	}	
	

}
