package org.oapen.memoproject.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.jpa.HomedirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@WithMockUser(username = "test", password = "test")
public class InMemoryHomedirTests {
	
	@Autowired
    private HomedirRepository homedirRepository;
	
    @Test
    public void givenQuery_whenInsert_thenValidUUID() {
    	
    	Homedir hNew = new Homedir(RandomStringUtils.randomAlphabetic(10), "Client Whatshisname");
    	hNew.setAccessKey(RandomStringUtils.randomAlphabetic(10));
    	Homedir qSaved = homedirRepository.save(hNew);

        assertEquals(UUID.fromString(qSaved.getId().toString()), qSaved.getId());
    }
    
	@Test
    public void givenHomedir_whenSave_thenGetOk() {
    	
    	String NAME = "Client Whatshisname";
    	String USERNAME = RandomStringUtils.randomAlphabetic(10);
    	String ACCESSKEY = RandomStringUtils.randomAlphabetic(10);
    	
    	Homedir hNew = new Homedir(USERNAME, NAME);
    	hNew.setAccessKey(ACCESSKEY);
    	Homedir hSaved = homedirRepository.save(hNew);
    	
    	Optional<Homedir> hCheck = homedirRepository.findById(hSaved.getId());
    	
        assertEquals(hCheck.get().getName(), NAME);
        assertEquals(hCheck.get().getUsername(), USERNAME);
    }

	@Test
    public void givenHomedirHavingNonUniqueUsername_whenSave_thenError() {
    	
    	String NAME = "Client Whatshisname";
    	String USERNAME = RandomStringUtils.randomAlphabetic(10);
    	String ACCESSKEY1 = RandomStringUtils.randomAlphabetic(10);
    	String ACCESSKEY2 = RandomStringUtils.randomAlphabetic(10);
    	
    	Homedir hNew1 = new Homedir(USERNAME, NAME);
    	hNew1.setAccessKey(ACCESSKEY1);
    	Homedir hNew2 = new Homedir(USERNAME, NAME);
    	hNew2.setAccessKey(ACCESSKEY2);
    	
    	assertThrows(DataIntegrityViolationException.class, () -> {
    		
        	homedirRepository.save(hNew1);
        	homedirRepository.save(hNew2);
    	});
    }
	
	@Test
    public void givenHomedirHavingNonUniqueAccesKey_whenSave_thenError() {
    	
    	String NAME = "Client Whatshisname";
    	String USERNAME1 = RandomStringUtils.randomAlphabetic(10);
    	String USERNAME2 = RandomStringUtils.randomAlphabetic(10);
    	String ACCESSKEY = RandomStringUtils.randomAlphabetic(10);
    	
    	Homedir hNew1 = new Homedir(USERNAME1, NAME);
    	hNew1.setAccessKey(ACCESSKEY);

    	Homedir hNew2 = new Homedir(USERNAME2, NAME);
    	hNew2.setAccessKey(ACCESSKEY);
    	
    	assertThrows(DataIntegrityViolationException.class, () -> {
    		
        	homedirRepository.save(hNew1);
        	homedirRepository.save(hNew2);
    	});
    }
	
}
