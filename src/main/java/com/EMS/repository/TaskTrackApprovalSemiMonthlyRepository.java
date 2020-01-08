package com.EMS.repository;

import java.util.Date;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EMS.model.TasktrackApprovalSemiMonthly;

public interface TaskTrackApprovalSemiMonthlyRepository extends JpaRepository<TasktrackApprovalSemiMonthly, Long> {

	@Query(value = "FROM TasktrackApprovalSemiMonthly AS ta where ta.user.userId = :userId and ta.project.projectId = :projectId and ta.month = month(:startDate) and ta.year = year(:startDate)")
	public TasktrackApprovalSemiMonthly getSemiMonthlyTasktrack(@Param("startDate") Date startDate, @Param("userId") long userId, @Param("projectId") long projectId) throws Exception;

	public List<TasktrackApprovalSemiMonthly> findByUserUserIdAndProjectProjectIdInAndMonthAndYear(Long userId,
			List<Long> projectIdList, Integer month, Integer year);
	

	TasktrackApprovalSemiMonthly findByUserUserIdAndProjectProjectIdAndMonthAndYear(Long userId,Long projectId,int month,int year);

}
