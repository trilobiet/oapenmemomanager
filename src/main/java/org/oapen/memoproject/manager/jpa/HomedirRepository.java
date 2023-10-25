package org.oapen.memoproject.manager.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Homedir;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomedirRepository extends JpaRepository<Homedir, UUID> {
	
	List<Homedir> findAllByOrderByNameAsc();
	List<Homedir> findAllByOrderByUsernameAsc();
	Optional<Homedir> findByUsername(String username);

}
