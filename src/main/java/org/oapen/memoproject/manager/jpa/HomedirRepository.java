package org.oapen.memoproject.manager.jpa;

import java.util.Optional;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Homedir;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomedirRepository extends JpaRepository<Homedir, UUID> {
	
	Optional<Homedir> findByUsername(String username);

}
