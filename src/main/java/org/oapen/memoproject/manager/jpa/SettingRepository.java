package org.oapen.memoproject.manager.jpa;

import java.util.Optional;

import org.oapen.memoproject.manager.entities.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, String> {
	
	Optional<Setting> findByKey(String key);

}
	