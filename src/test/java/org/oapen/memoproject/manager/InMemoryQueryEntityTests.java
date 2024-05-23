package org.oapen.memoproject.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.oapen.memoproject.manager.entities.Query;
import org.oapen.memoproject.manager.entities.Script;
import org.oapen.memoproject.manager.entities.Script.ScriptType;
import org.oapen.memoproject.manager.jpa.QueryRepository;
import org.oapen.memoproject.manager.jpa.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@WithMockUser(username = "test", password = "test")
public class InMemoryQueryEntityTests {
    
	@Autowired
    private QueryRepository queryRepository;

	@Autowired
    private ScriptRepository scriptRepository;
	
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
    	
    	Query qNew = new Query(NAME);
    	qNew.setBody(BODY);
    	Query qSaved = queryRepository.save(qNew);
    	
    	Query qUpdatable = queryRepository.findById(qSaved.getId()).get();
    	qUpdatable.setName(NEWNAME);
    	qUpdatable.setBody(NEWBODY);
    	
    	Query qUpdated = queryRepository.save(qUpdatable);
    	Query qCheck = queryRepository.findById(qUpdated.getId()).get();
    	
        assertEquals(qCheck.getId(),qSaved.getId());
    }

	
	@Test
	public void givenScriptsWithReference_thenQueryShouldCountReferences() {
		
		String SCRIPTNAME1 = RandomStringUtils.randomAlphabetic(10);
		String SCRIPTNAME2 = RandomStringUtils.randomAlphabetic(10);
		String BODY1pre = RandomStringUtils.randomAlphabetic(50);
		String BODY1post = RandomStringUtils.randomAlphabetic(50);
		String BODY2pre = RandomStringUtils.randomAlphabetic(50);
		String BODY2post = RandomStringUtils.randomAlphabetic(50);
		String QUERYNAME = RandomStringUtils.randomAlphabetic(10);
		
		Query q = new Query(QUERYNAME);
		queryRepository.save(q);
		
		// Two scripts with QUERYNAME in their body fields
		Script s1 = new Script(SCRIPTNAME1,ScriptType.MAIN);
		s1.setBody(BODY1pre + QUERYNAME + BODY1post);
		scriptRepository.save(s1);
		
		Script s2 = new Script(SCRIPTNAME2,ScriptType.MAIN);
		s2.setBody(BODY2pre + QUERYNAME + BODY2post);
		scriptRepository.save(s2);
		
		Optional<Query> savedQuery = queryRepository.findById(q.getId());
		
		assertEquals(2, savedQuery.get().getReferences());
	}
	
}
