package org.oapen.memoproject.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.oapen.memoproject.manager.controller.ScriptController;
import org.oapen.memoproject.manager.entities.Script;
import org.oapen.memoproject.manager.entities.Script.ScriptType;
import org.oapen.memoproject.manager.jpa.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper; 

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,classes = Application.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude=SecurityAutoConfiguration.class)
public class ScriptControllerTests {
	
	@Autowired
	private MockMvc mvc;

	@Autowired
	private ScriptRepository scriptRepository;
	
	@InjectMocks
	private ScriptController scriptController;

	@Test 
	void givenScripts_whenSaveAllAndGet_thenSortedByName() throws Exception {
		
		Script script1 = new Script("a", ScriptType.SNIP);
		Script script2 = new Script("b", ScriptType.SNIP);
		Script script3 = new Script("c", ScriptType.MAIN);
		Script script4 = new Script("d", ScriptType.SNIP);
		
		scriptRepository.save(script3);
		scriptRepository.save(script4);
		scriptRepository.save(script2);
		scriptRepository.save(script1);
		
		ResultActions resultActions = mvc.perform(
			get("/api/script/snippets")
		);
		
		MvcResult res = resultActions.andReturn();
		
		ObjectMapper om = new ObjectMapper();
		// https://stackoverflow.com/questions/6349421/how-to-use-jackson-to-deserialise-an-array-of-objects
		List<Script> lst = om.readValue(res.getResponse().getContentAsString(), new TypeReference<List<Script>>(){});
		
		assertEquals(lst.size(), 3);
		
		String q = lst.stream().map(script -> script.getName()).collect(Collectors.joining());
		assertEquals(q, "abd");
	}
	
}
