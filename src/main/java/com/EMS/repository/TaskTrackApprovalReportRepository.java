package com.EMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.TasktrackApprovalReporting;

public interface TaskTrackApprovalReportRepository extends JpaRepository<TasktrackApprovalReporting, Long>{

	@Query("FROM TasktrackApprovalReporting report "
			+ "WHERE report.user.userId = ?1 "
			+ "AND report.project.projectId = ?2 "
			+ "AND report.month = ?3 and report.year = ?4")
	TasktrackApprovalReporting findMonthReport(Long userId, Long projectId,Integer month, Integer year);
	
	@Query("FROM TasktrackApprovalReporting report "
			+ "WHERE report.user.userId = ?1 "
			+ "AND report.project.projectId = ?2 "
			+ "AND ((report.month = ?3 and report.year = ?4) OR (report.month = ?5 and report.year = ?6))")
	List<TasktrackApprovalReporting> findWeekIntersectingMonthResport(Long userId, Long projectId,Integer month1, Integer year1, Integer month2, Integer year2);
}
