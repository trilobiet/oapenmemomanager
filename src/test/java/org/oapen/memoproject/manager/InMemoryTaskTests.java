package org.oapen.memoproject.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.entities.Query;
import org.oapen.memoproject.manager.entities.RunLog;
import org.oapen.memoproject.manager.entities.Script;
import org.oapen.memoproject.manager.entities.Script.ScriptType;
import org.oapen.memoproject.manager.entities.Task;
import org.oapen.memoproject.manager.entities.Task.TaskFrequency;
import org.oapen.memoproject.manager.jpa.HomedirRepository;
import org.oapen.memoproject.manager.jpa.RunLogRepository;
import org.oapen.memoproject.manager.jpa.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@WithMockUser(username = "test", password = "test")
public class InMemoryTaskTests {
	
	@Autowired
    private HomedirRepository homedirRepository;
	
	@Autowired
    private TaskRepository taskRepository;
	
	@Autowired
    private RunLogRepository runLogRepository;

	@Test
    public void givenHomedir_whenAddTask_thenOk() {
    	
    	String NAME = "test name";
    	String USERNAME = RandomStringUtils.randomAlphabetic(10);
    	String ACCESSKEY = RandomStringUtils.randomAlphabetic(10);

    	// We need a homedir to serve as owner of the tasks
    	Homedir hNew = new Homedir(USERNAME, NAME);
    	hNew.setAccessKey(ACCESSKEY);
    	homedirRepository.save(hNew);
    	
    	Task tNew = new Task("File name","ext",hNew);
    	tNew.setFrequency(TaskFrequency.D);
    	
		taskRepository.save(tNew);
    	
    	List<Task> tasks = taskRepository.findByHomedir(hNew);
    	
    	assertTrue(tasks.size() == 1);
    	assertEquals(hNew, tasks.get(0).getHomedir());
    	assertEquals("daily", tasks.get(0).getFrequencyAsText());
    }
	
	@Test
    public void givenHomedir_whenAddTasks_thenCountMatches() {
    	
    	String NAME = "test name";
    	String USERNAME = RandomStringUtils.randomAlphabetic(10);
    	String ACCESSKEY = RandomStringUtils.randomAlphabetic(10);

    	// We need a homedir to serve as owner of the tasks
    	Homedir hNew = new Homedir(USERNAME, NAME);
    	hNew.setAccessKey(ACCESSKEY);
    	UUID id = homedirRepository.save(hNew).getId();
    	
    	Task t1 = new Task("File1","ext",hNew);
    	Task t2 = new Task("File2","ext",hNew);
    	Task t3 = new Task("File3","ext",hNew);
    	
		taskRepository.save(t1);
		taskRepository.save(t2);
		taskRepository.save(t3);
    	
		Optional<Homedir> hSaved = homedirRepository.findById(id);
   	
    	assertTrue(hSaved.isPresent());
    	assertEquals(3, hSaved.get().getTaskCount());
    }
	
	@Test
    public void givenHomedirAndTaskname_whenFind_thenResult() {
    	
    	String NAME = "test name";
    	String USERNAME = RandomStringUtils.randomAlphabetic(10);
    	String ACCESSKEY = RandomStringUtils.randomAlphabetic(10);
    	String FILENAME = RandomStringUtils.randomAlphabetic(10);

    	// We need a homedir to serve as owner of the tasks
    	Homedir hNew = new Homedir(USERNAME, NAME);
    	hNew.setAccessKey(ACCESSKEY);
    	homedirRepository.save(hNew);
    	
    	Task tNew = new Task(FILENAME,"ext",hNew);
    	tNew.setFrequency(TaskFrequency.D);
    	
		taskRepository.save(tNew);
    	
    	List<Task> tasks = taskRepository.findByHomedirAndFileName(hNew,FILENAME);
    	
    	//System.out.println(tasks.get(0));
    	
    	assertTrue(tasks.size() == 1);
    }
	
	@Test
    public void givenTask_whenFindRunLog_thenGetList() {
		
    	String NAME = "test name";
    	String USERNAME = RandomStringUtils.randomAlphabetic(10);
    	String ACCESSKEY = RandomStringUtils.randomAlphabetic(10);

    	// We need a homedir to serve as owner of the tasks
    	Homedir hNew = new Homedir(USERNAME, NAME);
    	hNew.setAccessKey(ACCESSKEY);
    	homedirRepository.save(hNew);
    	
    	Task tNew = new Task("File name","ext",hNew);
    	
		taskRepository.save(tNew);
		
		List<RunLog> q = runLogRepository.findTop10ByTaskOrderByDateDesc(tNew);
		
		assertTrue(q.isEmpty());
	}


	@Test
    public void givenTask_whenAddScriptAndQuery_thenOk() {
		
    	String HOMEDIRNAME = RandomStringUtils.randomAlphabetic(10);
    	String USERNAME = RandomStringUtils.randomAlphabetic(10);
    	String ACCESSKEY = RandomStringUtils.randomAlphabetic(10);
    	String TASK = RandomStringUtils.randomAlphabetic(10);
    	String SCRIPT = RandomStringUtils.randomAlphabetic(10);
    	String QUERY = RandomStringUtils.randomAlphabetic(10);

    	// We need a homedir to serve as owner of the tasks
    	Homedir hNew = new Homedir(USERNAME, HOMEDIRNAME);
    	hNew.setAccessKey(ACCESSKEY);
    	homedirRepository.save(hNew);
    	
    	Task tNew = new Task(TASK, "ext", hNew);
    	Script script = new Script(SCRIPT, ScriptType.MAIN);
    	Query q = new Query(QUERY);
    	script.setQuery(q);
    	tNew.setScript(script);
    	
		Task t = taskRepository.save(tNew);
		
		Optional<Task> tf = taskRepository.findById(t.getId());
		
		// System.out.println(tf);
		
		assertTrue(tf.isPresent());
		assertEquals(tf.get().getScript().getName(),SCRIPT);
		assertEquals(tf.get().getScript().getQuery().getName(),QUERY);
	}

	
	
}
