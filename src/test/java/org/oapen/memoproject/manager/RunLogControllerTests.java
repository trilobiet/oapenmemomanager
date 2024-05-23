package org.oapen.memoproject.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.oapen.memoproject.manager.controller.RunLogController;
import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.entities.RunLog;
import org.oapen.memoproject.manager.entities.Task;
import org.oapen.memoproject.manager.jpa.HomedirRepository;
import org.oapen.memoproject.manager.jpa.RunLogRepository;
import org.oapen.memoproject.manager.jpa.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; 

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,classes = Application.class)
@AutoConfigureMockMvc(addFilters = false /* bypass security */)
@EnableAutoConfiguration(exclude=SecurityAutoConfiguration.class)
@WithMockUser(username = "test", password = "test")
public class RunLogControllerTests {

	@Autowired
	private MockMvc mvc;

	@InjectMocks
	private RunLogController runLogController;

	@Autowired
	private HomedirRepository homedirRepository;
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private RunLogRepository runLogRepository;
	
	@Test
    public void givenRunLogs_whenSaveAndGetTop10_thenOk() throws Exception {
		
    	String NAME = "test name";
    	String USERNAME = RandomStringUtils.randomAlphabetic(10);
    	String ACCESSKEY = RandomStringUtils.randomAlphabetic(10);
    	
    	int SIZE = 20;

    	// We need a homedir to serve as owner of the tasks
    	Homedir hNew = new Homedir(USERNAME, NAME);
    	hNew.setAccessKey(ACCESSKEY);
    	homedirRepository.save(hNew);
    	
    	Task tNew = new Task("File name","ext",hNew);
    	Task tSaved = taskRepository.save(tNew);
		
		for (int i=0; i<SIZE; i++) {
		
			LocalDate date = date();
			RunLog rl = new RunLog();
			rl.setDate(date.atStartOfDay());
			rl.setTask(tSaved);
			runLogRepository.save(rl);
			//System.out.println(rl);
		}
		
		ResultActions resultActions = mvc.perform(
			get("/api/task/" + tSaved.getId() + "/runlog/top")
		);
		
		MvcResult res = resultActions.andReturn();
		
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
		// https://stackoverflow.com/questions/6349421/how-to-use-jackson-to-deserialise-an-array-of-objects
		List<RunLog> lst = om.readValue(res.getResponse().getContentAsString(), new TypeReference<List<RunLog>>(){});
		
		System.out.println(lst);
		
		assertEquals(10, lst.size());

	}	
	
	
	private static LocalDate date() {
	    int hundredYears = 100 * 365;
	    return LocalDate.ofEpochDay(ThreadLocalRandom
	      .current().nextInt(-hundredYears, hundredYears));
	}
		
	
}
