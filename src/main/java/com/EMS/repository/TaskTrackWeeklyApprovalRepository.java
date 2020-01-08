package com.EMS.repository;

import com.EMS.model.TaskTrackWeeklyApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface TaskTrackWeeklyApprovalRepository extends JpaRepository<TaskTrackWeeklyApproval, Long> {

    @Query("SELECT a FROM TaskTrackWeeklyApproval a WHERE month(a.startDate)=?2 OR month(a.endDate)=?2 AND a.project.projectId = ?1 AND year(a.startDate)=?3 OR year(a.endDate)=?3 AND a.timetrackStatus='SUBMITTED'")
	List<TaskTrackWeeklyApproval> getWeeklyData(Long projectId,int month,int year);

    @Query("SELECT a FROM TaskTrackWeeklyApproval a WHERE a.startDate=?2 AND a.endDate=?3 AND a.project.projectId = ?1 AND a.user.userId=?4  AND a.timetrackStatus='SUBMITTED'")
    TaskTrackWeeklyApproval getWeeklyUserData(Long projectId, Date weekStart, Date weekEnd, Long userId);
}
