package org.oapen.memoproject.manager.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Script;
import org.oapen.memoproject.manager.entities.Script.ScriptType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScriptRepository extends JpaRepository<Script, UUID> {
	
	Optional<Script> findByName(String name);
	
	List<Script> findByType(ScriptType type, Sort sort);

}
	