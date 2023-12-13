package org.oapen.memoproject.manager.jpa;

import java.util.List;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Query;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueryRepository extends JpaRepository<Query, UUID> {
	
	List<Query> findAllByOrderByNameAsc();
	
	List<Query> findByIsLibraryTrue(Sort sort);

}
