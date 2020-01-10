package com.EMS.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EMS.model.TaskTrackWeeklyApproval;

public interface TaskWeeklyApprovalRepository extends JpaRepository<TaskTrackWeeklyApproval, Long> {
	
	@Query(value = "SELECT ta FROM TaskTrackWeeklyApproval AS ta \n" + 
			" WHERE ta.startDate = :startDate and ta.endDate = :endDate and ta.user.userId = :userId and ta.project.projectId = :projectId")
	public TaskTrackWeeklyApproval getWeeklyTasktrack(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("userId") long userId, @Param("projectId") long projectId) throws Exception;
	
	@Query(value = "select ta.task_name, tt.date, tt.hours, tt.description \n" + 
			"from task_master ta left join tasktrack tt\n" + 
			"on ta.id = tt.task_id\n" + 
			"", nativeQuery = true)
	public List<Object[]> getWeeklyTasktrackWithTask();

	@Query(value="SELECT count(*) FROM tasktrack_weekly_approval where start_date=?1 AND end_date=?2 AND user_user_id=?3",nativeQuery=true)
	public int getduplicateentrycount(Date startDate, Date endDate, long userId);
	
}