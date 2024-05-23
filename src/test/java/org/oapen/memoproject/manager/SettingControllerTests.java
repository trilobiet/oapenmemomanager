package org.oapen.memoproject.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.oapen.memoproject.manager.controller.SettingController;
import org.oapen.memoproject.manager.entities.Setting;
import org.oapen.memoproject.manager.jpa.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc; 

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,classes = Application.class)
@AutoConfigureMockMvc(addFilters = false /* bypass security */)
@EnableAutoConfiguration(exclude=SecurityAutoConfiguration.class)
@WithMockUser(username = "admin", password = "test", authorities = {"admin"})
public class SettingControllerTests {
	
	@Autowired
	private MockMvc mvc;

	@Autowired
	private SettingRepository settingRepository;

	@InjectMocks
	private SettingController SettingController;
	
	@BeforeEach
	private void setup(){
		
	}

	@Test 
	void givenJson_whenInsertSetting_thenOk() throws Exception {
		
		String KEY = RandomStringUtils.randomAlphabetic(10);
		String VALUE = RandomStringUtils.randomAlphabetic(10);
		
		// Json represents a task on homedir together with a script
		String json = "{\"key\":\"" + KEY + "\",\"value\":\"" + VALUE + "\"}";
		
		mvc.perform(
			post("/api/setting/")
			.contentType(MediaType.APPLICATION_JSON)
			.content(json)
		);
		
		Optional<Setting> sSaved = settingRepository.findByKey(KEY);
		
		assertTrue(sSaved.isPresent());
		assertEquals(sSaved.get().getKey(), KEY);
		assertEquals(sSaved.get().getValue(), VALUE);
		
	}
	
	@Test 
	void givenJson_whenDeleteSetting_thenOk() throws Exception {
		
		String KEY = RandomStringUtils.randomAlphabetic(10);
		String VALUE = RandomStringUtils.randomAlphabetic(10);
		
		Setting s = new Setting(KEY,VALUE);
		settingRepository.save(s);
		
		mvc.perform(delete("/api/setting/" + s.getKey()))
			.andExpect(status().is2xxSuccessful());
		
		mvc.perform(get("/api/setting/" + s.getKey()))
			.andExpect(status().isNotFound());
		
		/*
		Optional<Setting> sSaved = settingRepository.findByKey(KEY);
		
		assertTrue(sSaved.isPresent());
		assertEquals(sSaved.get().getKey(), KEY);
		assertEquals(sSaved.get().getValue(), VALUE);
		
		*/
	}
	
	
}
