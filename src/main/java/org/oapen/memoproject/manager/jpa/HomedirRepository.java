package org.oapen.memoproject.manager.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Homedir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HomedirRepository extends JpaRepository<Homedir, UUID> {
		
	@Query(value = ""
		+ "SELECT * FROM homedir "
		+ "ORDER BY lower(name) ASC", nativeQuery = true)
	List<Homedir> findAllByOrderByNameAsc();
		
	List<Homedir> findAllByOrderByUsernameAsc();

	Optional<Homedir> findByUsername(String username);

}
