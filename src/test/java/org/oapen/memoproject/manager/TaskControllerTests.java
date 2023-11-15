package org.oapen.memoproject.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.oapen.memoproject.manager.controller.TaskController;
import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.entities.Task;
import org.oapen.memoproject.manager.jpa.HomedirRepository;
import org.oapen.memoproject.manager.jpa.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; 

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,classes = Application.class)
@AutoConfigureMockMvc(addFilters = false /* bypass security */)
@EnableAutoConfiguration(exclude=SecurityAutoConfiguration.class)
public class TaskControllerTests {
	
	@Autowired
	private MockMvc mvc;

	@InjectMocks
	private TaskController taskController;
	
	@Autowired
    private HomedirRepository homedirRepository;
	
	@Autowired
    private TaskRepository taskRepository;

	private ObjectMapper om;
	private Homedir homedir;
	
	@BeforeEach
	private void setup(){
		
		om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
		
		homedir = new Homedir(UUID.randomUUID().toString(),"Test");
		homedirRepository.save(homedir);
	}

	@Test 
	void givenJson_whenInsertTask_thenOk() throws Exception {
		
		// Json represents a task on homedir together with a script
		String json = "{\"fileName\":\"my_export\",\"extension\":\"xml\","
			+ "\"startDate\":[2023,10,31],\"frequency\":\"M\","
			+ "\"script\":{\"name\":\"script\",\"type\":\"MAIN\"},"
			+ "\"public\":false,\"path\":\"pietje/my_export\",\"active\":false,"
			+ "\"frequencyAsText\":\"monthly\"}";
		
		ResultActions resultActions = mvc.perform(
			post("/api/homedir/" + homedir.getId() + "/task")
			.contentType(MediaType.APPLICATION_JSON)
			.content(json)
		);
		
		MvcResult w = resultActions.andReturn();
		
		Task task = om.readValue(w.getResponse().getContentAsString(), Task.class);
		
		UUID taskId = task.getId();
		UUID scriptId = task.getScript().getId();
		
		Optional<Task> tSaved = taskRepository.findById(taskId);
		
		assertTrue(tSaved.isPresent());
		assertEquals(tSaved.get().getId(), taskId);
		assertEquals(tSaved.get().getScript().getId(), scriptId);
		assertEquals(tSaved.get().getHomedir().getId(), homedir.getId());
		
	}
	
	
	
}
