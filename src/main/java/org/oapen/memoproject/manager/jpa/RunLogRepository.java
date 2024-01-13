package org.oapen.memoproject.manager.jpa;

import java.util.List;

import org.oapen.memoproject.manager.entities.RunLog;
import org.oapen.memoproject.manager.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunLogRepository extends JpaRepository<RunLog, Integer> {
	
	List<RunLog> findByTaskOrderByDateDesc(Task task);
	List<RunLog> findTop10ByTaskOrderByDateDesc(Task task);

}
