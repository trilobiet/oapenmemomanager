package org.oapen.memoproject.manager.jpa;

import java.util.List;
import java.util.UUID;

import org.oapen.memoproject.manager.entities.Homedir;
import org.oapen.memoproject.manager.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, UUID> {

	List<Task> findByHomedir(Homedir homedir);
	
	// Serves to see whether a given fileName is already in use for a homedir
	List<Task> findByHomedirAndFileName(Homedir homedir, String fileName);
}
	