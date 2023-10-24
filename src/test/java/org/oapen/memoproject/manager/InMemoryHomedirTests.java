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
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class InMemoryHomedirTests {
	
	@Autowired
    private HomedirRepository homedirRepository;
	
    @Test
    public void givenQuery_whenInsert_thenValidUUID() {
    	
    	Homedir hNew = new Homedir(RandomStringUtils.randomAlphabetic(10), "Client Whatshisname");
    	Homedir qSaved = homedirRepository.save(hNew);

        assertEquals(UUID.fromString(qSaved.getId().toString()), qSaved.getId());
    }
    
    @Test
    public void givenQueryHavingEmptyNonNullableFields_whenSave_thenError() {
    	
    	assertThrows(DataIntegrityViolationException.class, () -> {
	    	Homedir qNew = new Homedir();
	    	homedirRepository.save(qNew);
    	});
    }
    
	@Test
    public void givenHomedir_whenSave_thenGetOk() {
    	
    	String NAME = "Client Whatshisname";
    	String USERNAME = RandomStringUtils.randomAlphabetic(10);
    	
    	Homedir hNew = new Homedir(USERNAME, NAME);
    	Homedir hSaved = homedirRepository.save(hNew);
    	
    	Optional<Homedir> hCheck = homedirRepository.findById(hSaved.getId());
    	
        assertEquals(hCheck.get().getName(), NAME);
        assertEquals(hCheck.get().getUsername(), USERNAME);
    }

	@Test
    public void givenHomedirHavingNonUniqueUsername_whenSave_thenError() {
    	
    	String NAME = "Client Whatshisname";
    	String USERNAME = RandomStringUtils.randomAlphabetic(10);
    	
    	Homedir hNew1 = new Homedir(USERNAME, NAME);
    	Homedir hNew2 = new Homedir(USERNAME, NAME);
    	
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
    	String ACCESSKEY = "12345";
    	
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
