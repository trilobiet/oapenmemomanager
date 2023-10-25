package org.oapen.memoproject.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.oapen.memoproject.manager.entities.Setting;
import org.oapen.memoproject.manager.jpa.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class InMemorySettingTests {
	
	@Autowired
    private SettingRepository settingRepository;
	
	@Test
    public void givenNonExistingKey_whenFind_thenEmpty() {
		
		String key = RandomStringUtils.randomAlphabetic(10);
		Optional<Setting> sCheck = settingRepository.findById(key);
		assertTrue(sCheck.isEmpty());
	}
	
	//@Test
    public void givenSetting_whenSave_thenOk() {
		
		String key = RandomStringUtils.randomAlphabetic(10);
		String val = RandomStringUtils.randomAlphabetic(10);
		
		Setting sNew = new Setting(key,val);
		
		settingRepository.save(sNew);
		
		Optional<Setting> sCheck = settingRepository.findByKey(key);
		
		assertTrue(sCheck.isPresent());
		assertEquals(sCheck.get().getValue(),val);
	}
	
	//@Test
    public void givenSettingHavingNonUniqueKey_whenSave_thenError() {
		
		String key = RandomStringUtils.randomAlphabetic(10);
		String val1 = RandomStringUtils.randomAlphabetic(10);
		String val2 = RandomStringUtils.randomAlphabetic(10);
		
		assertThrows(DataIntegrityViolationException.class, () -> {
			new Setting(key,val1);
			new Setting(key,val2);
		});
		
	}

}
