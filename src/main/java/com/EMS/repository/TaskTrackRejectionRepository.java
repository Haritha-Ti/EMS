package com.EMS.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.TaskTrackRejection;
import com.EMS.utility.Constants;

public interface TaskTrackRejectionRepository extends JpaRepository<TaskTrackRejection, Long> {

	@Query("SELECT rejection FROM TaskTrackRejection rejection "
			+ "where rejection.user.userId = ?1 and rejection.project.projectId = ?2 "
			+ "and rejection.month = ?3 and rejection.year = ?4 and rejection.cycle = ?5 and rejection.status = '"
			+ Constants.TASKTRACK_REJECTION_STATUS_OPEN + "' order by rejection.id desc")
	List<TaskTrackRejection> findOpenRejectionForCycleForUserForProject(Long userId, Long projectId, Integer month,
			Integer year, String cycle);
	
	@Query("SELECT rejection FROM TaskTrackRejection rejection "
			+ "where rejection.user.userId = ?1 and rejection.project.projectId = ?2 "
			+ "and rejection.month = ?3 and rejection.year = ?4 and rejection.status = '"
			+ Constants.TASKTRACK_REJECTION_STATUS_OPEN + "' order by rejection.id desc")
	List<TaskTrackRejection> findOpenRejectionForUserForProject(Long userId, Long projectId, Integer month,Integer year);

	
	@Query("select rejection FROM TaskTrackRejection rejection "
			+ "where rejection.user.userId = ?1 and rejection.project.projectId = ?2 and rejection.year = ?3 "
			+ "and rejection.startDate = ?4 and rejection.endDate = ?5 and rejection.status = ?6 ")
	TaskTrackRejection findWeeklyRejection(Long userId, Long projectId, Integer year, Date startDate,Date endDate,String status);
	
	@Query("select rejection FROM TaskTrackRejection rejection "
			+ "where rejection.user.userId = ?1 and rejection.project.projectId = ?2 and rejection.month = ?3 "
			+ "and rejection.year = ?4 and rejection.status = ?5 ")
	List<TaskTrackRejection> findSemiMonthlyRejection(Long userId, Long projectId, Integer month, Integer year,String status);
}
