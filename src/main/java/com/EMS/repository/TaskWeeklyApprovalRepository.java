package com.EMS.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EMS.model.TaskTrackWeeklyApproval;

public interface TaskWeeklyApprovalRepository extends JpaRepository<TaskTrackWeeklyApproval, Long> {
	
	@Query(value = "SELECT ta FROM TaskTrackWeeklyApproval AS ta \n" + 
			" WHERE ta.startDate = :startDate and ta.endDate = :endDate and ta.user.userId = :userId and ta.project.projectId = :projectId")
	public TaskTrackWeeklyApproval getWeeklyTasktrack(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("userId") long userId, @Param("projectId") long projectId) throws Exception;
	
}