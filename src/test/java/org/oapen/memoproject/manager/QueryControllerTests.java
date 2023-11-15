package org.oapen.memoproject.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

/*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
*/

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.oapen.memoproject.manager.controller.QueryController;
import org.oapen.memoproject.manager.entities.Query;
import org.oapen.memoproject.manager.jpa.QueryRepository;
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
public class QueryControllerTests {

	@Autowired
	private MockMvc mvc;

	@InjectMocks
	private QueryController queryController;
	
	@Autowired 
	private QueryRepository queryRepository;
	
	private ObjectMapper om;

	@BeforeEach
	private void setup(){
		
		om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
	}
	
	
	@Test 
	void givenRandomId_whenGetQuery_then404() throws Exception {
		
		String id = UUID.randomUUID().toString();
		
		mvc.perform(
			get("/api/query/" + id)
		)
		.andExpect(status().isNotFound());
	}

	@Test 
	void givenValidId_whenGetQuery_thenOk() throws Exception {
		
		Query q = queryRepository.save(new Query("qTEST123","blahblah"));
		
		ResultActions resultActions = mvc.perform(get("/api/query/" + q.getId()));
		
		MvcResult w = resultActions.andReturn();
		Query query = om.readValue(w.getResponse().getContentAsString(), Query.class);
		UUID uuid = query.getId();
		
		assertEquals(uuid, q.getId());
	}
	
	@Test 
	void givenJson_whenInsertQuery_thenOk() throws Exception {
		
		String json = "{\"name\":\"qWhatever\",\"body\":\"SELECT WHATEVER FROM TABLE\"}";
		
		mvc.perform(
			post("/api/query").contentType(MediaType.APPLICATION_JSON)
			.content(json)
		);
		
		List<Query> list = queryRepository.findAll();
		
		assertFalse(list.isEmpty());
		assert(list.get(0).getId().toString().length() == 36);
	}


	@Test 
	void givenQuery_whenDeleteQuery_thenNotFound() throws Exception {
		
		Query q = queryRepository.save(new Query("qTEST","blahblah"));

		mvc.perform(get("/api/query/" + q.getId()))
			.andExpect(status().isOk());
		
		mvc.perform(delete("/api/query/" + q.getId()));
		
		mvc.perform(get("/api/query/" + q.getId()))
			.andExpect(status().isNotFound());
	}
	
	
	@Test 
	void givenJson_whenUpdateQuery_thenOk() throws Exception {
		
		// Insert new Query object named 'qTest'
		String jsonInsert = "{\"name\":\"qTest\",\"body\":\"SELECT * FROM TABLE\"}";
		
		ResultActions resultActions = mvc.perform(
			post("/api/query").contentType(MediaType.APPLICATION_JSON)
			.content(jsonInsert)
		);
		
		// Get new Id from returned Query
		MvcResult w = resultActions.andReturn();
		Query query = om.readValue(w.getResponse().getContentAsString(), Query.class);
		UUID uuid = query.getId();

		// Update Query object identified by id, set name to 'qTestUpdated'  
		String jsonUpdate = "{\"id\":\"" + uuid + "\",\"name\":\"qTestUpdated\",\"body\":\"SELECT SOMETHING ELSE FROM TABLE\"}";
		
		mvc.perform(
			post("/api/query").contentType(MediaType.APPLICATION_JSON)
			.content(jsonUpdate)
		);

		Optional<Query> q = queryRepository.findById(uuid);
		
		assertTrue(q.isPresent());
		assertEquals(q.get().getName(), "qTestUpdated");
	}
	
	
	
}
