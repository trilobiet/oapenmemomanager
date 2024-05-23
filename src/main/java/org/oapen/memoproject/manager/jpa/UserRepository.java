package org.oapen.memoproject.manager.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

	@Query(value = ""
		+ "SELECT * FROM user "
		+ "WHERE username = ?1", nativeQuery = true)
	Optional<User> findByUsername(String itemId);
	
	List<User> findAllByOrderByUsernameAsc();

}
