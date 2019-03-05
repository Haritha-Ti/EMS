package com.EMS.repository;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.Tasktrack;


public interface TasktrackRepository extends JpaRepository<Tasktrack, Long> {
	
	@Query("SELECT t.id,t.taskName FROM Tasktrack t")
	List<Object[]> getTaskNameId();
	
	@Query("SELECT t.taskName FROM Tasktrack t WHERE t.user=?1")
	List<String> getTaskByUser(Long id);
	
	
	@Query("SELECT t FROM Tasktrack t WHERE t.date =?1 and t.user.id=?2")
	List<Tasktrack> getByDate(Date currentDate,Long id);

}
