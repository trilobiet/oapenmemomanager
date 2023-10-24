package org.oapen.memoproject.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.oapen.memoproject.manager.entities.Query;
import org.oapen.memoproject.manager.jpa.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class InMemoryQueryEntityTests {
    
	@Autowired
    private QueryRepository queryRepository;
    
    @Test
    public void givenQuery_whenInsert_thenValidUUID() {
    	
    	Query qNew = new Query("test-query");
    	Query qSaved = queryRepository.save(qNew);

        assertEquals(UUID.fromString(qSaved.getId().toString()), qSaved.getId());
    }

	@Test
    public void givenQuery_whenSave_thenGetOk() {
    	
    	String NAME = "test-query";
    	String BODY = "Some sql statement";
    	
    	Query qNew = new Query(NAME);
    	qNew.setBody(BODY);
    	Query qSaved = queryRepository.save(qNew);
    	
    	Optional<Query> qCheck = queryRepository.findById(qSaved.getId());
    	
        assertEquals(qCheck.get().getName(), NAME);
        assertEquals(qCheck.get().getBody(), BODY);
    }
	
	
	@Test
    public void givenQuery_whenUpdate_thenIdDoesNotChange() {
    	
    	String NAME = "test-query";
    	String BODY = "Some sql statement";
    	String NEWNAME = "test-query2";
    	String NEWBODY = "Another sql statement";
    	
    	Query qNew = new Query();
    	qNew.setName(NAME);
    	qNew.setBody(BODY);
    	Query qSaved = queryRepository.save(qNew);
    	
    	Query qUpdatable = queryRepository.findById(qSaved.getId()).get();
    	qUpdatable.setName(NEWNAME);
    	qUpdatable.setBody(NEWBODY);
    	
    	Query qUpdated = queryRepository.save(qUpdatable);
    	Query qCheck = queryRepository.findById(qUpdated.getId()).get();
    	
        assertEquals(qCheck.getId(),qSaved.getId());
    }
	
	
}
