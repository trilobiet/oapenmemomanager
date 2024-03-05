package org.oapen.memoproject.manager.jpa;

import java.util.Optional;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Export;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExportRepository extends JpaRepository<Export, UUID> {
	
	Optional<Export> findByIdTask(String id);
}
