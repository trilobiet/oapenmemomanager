package org.oapen.memoproject.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.entities.RunLog;
import org.oapen.memoproject.manager.entities.Task;
import org.oapen.memoproject.manager.jpa.HomedirRepository;
import org.oapen.memoproject.manager.jpa.RunLogRepository;
import org.oapen.memoproject.manager.jpa.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class InMemoryRunLogTests {
	
	@Autowired
    private RunLogRepository runLogRepository;
	@Autowired
	private HomedirRepository homedirRepository;
	@Autowired
	private TaskRepository taskRepository;
	
	@Test
    public void givenScript_whenAddTask_whenSave_thenOk() {
		
    	String NAME = "test name";
    	String USERNAME = RandomStringUtils.randomAlphabetic(10);
    	int SIZE = 20;

    	// We need a homedir to serve as owner of the tasks
    	Homedir hNew = new Homedir(USERNAME, NAME);
    	homedirRepository.save(hNew);
    	
    	Task tNew = new Task("File name","ext",hNew);
    	Task tSaved = taskRepository.save(tNew);
		
		for (int i=0; i<SIZE; i++) {
		
			LocalDate date = date();
			RunLog rl = new RunLog();
			rl.setDate(date);
			rl.setTask(tSaved);
			runLogRepository.save(rl);
		}
		
		List<RunLog> lstAll = runLogRepository.findByTaskOrderByDateDesc(tSaved);
		List<RunLog> lstTop10 = runLogRepository.findTop10ByTaskOrderByDateDesc(tSaved);
		
		assertEquals(SIZE, lstAll.size());
		assertEquals(10, lstTop10.size());

	}	
	
	private static LocalDate date() {
	    int hundredYears = 100 * 365;
	    return LocalDate.ofEpochDay(ThreadLocalRandom
	      .current().nextInt(-hundredYears, hundredYears));
	}
	

}

