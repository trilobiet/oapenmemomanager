package org.oapen.memoproject.manager;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.oapen.memoproject.manager.controller.HomedirController;
import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.jpa.HomedirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
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
@WithMockUser(username = "test", password = "test")
public class HomedirControllerTests {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
    private HomedirRepository homedirRepository;

	@InjectMocks
	private HomedirController homedirController;
	
	private ObjectMapper om;

	@BeforeEach
	private void setup(){
		
		om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
	}
	
	
	@Test 
	void givenHomedirAndPassword_whenSaveEncryptedAndGet_thenPasswordsMatch() throws Exception {
		
		String USERNAME = RandomStringUtils.randomAlphabetic(10);
		String PASSWORD = RandomStringUtils.randomAlphabetic(10); 		
		String ACCESSKEY = RandomStringUtils.randomAlphabetic(10);
		
		// Json represents a task on homedir together with a script
		String json = "{\"username\":\"" + USERNAME + "\",\"name\":\"Pietje\",\"accessKey\":\"" + ACCESSKEY + "\","
			+ "\"password\":\"" + PASSWORD + "\",\"notes\":\"blablabla\" }";
		
		ResultActions resultActions = mvc.perform(
			post("/api/homedir").contentType(MediaType.APPLICATION_JSON)
			.content(json)
		);
		
		MvcResult w = resultActions.andReturn();
		Homedir homedir = om.readValue(w.getResponse().getContentAsString(), Homedir.class);
		
		Optional<Homedir> hSaved = homedirRepository.findById(homedir.getId());
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		assertTrue(enc.matches(PASSWORD, hSaved.get().getPassword()));
	}

	
}
