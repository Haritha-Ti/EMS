package com.EMS.repository;

import java.util.Date;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EMS.model.TaskTrackWeeklyApproval;
import com.EMS.model.TasktrackApprovalSemiMonthly;

public interface TaskTrackApprovalSemiMonthlyRepository extends JpaRepository<TasktrackApprovalSemiMonthly, Long> {

	@Query(value = "FROM TasktrackApprovalSemiMonthly AS ta where ta.user.userId = :userId and ta.project.projectId = :projectId and ta.month = month(:startDate) and ta.year = year(:startDate)")
	public TasktrackApprovalSemiMonthly getSemiMonthlyTasktrack(@Param("startDate") Date startDate, @Param("userId") long userId, @Param("projectId") long projectId) throws Exception;

	public List<TasktrackApprovalSemiMonthly> findByUserUserIdAndProjectProjectIdInAndMonthAndYear(Long userId,
			List<Long> projectIdList, Integer month, Integer year);
	

	TasktrackApprovalSemiMonthly findByUserUserIdAndProjectProjectIdAndMonthAndYear(Long userId,Long projectId,int month,int year);


//	@Query(value="SELECT count(*) FROM tasktrack_approval_semimonthly where year=?2 AND month=?3 AND user_user_id=?1 AND user_first_half_status is NOT NUll AND user_second_half_status is NOT NULL",nativeQuery=true)
//	public int getsemiMonthlyRecord(Long userId, Integer year, Integer month);

	@Query(value="SELECT * FROM tasktrack_approval_semimonthly where year=?3 AND month=?2 AND user_user_id=?1",nativeQuery=true)
	public TasktrackApprovalSemiMonthly checkduplicationForsemiMonthlyTaskTrack(long userId, Integer month, Integer year);

	public TasktrackApprovalSemiMonthly findByUserUserIdAndProjectProjectIdInAndMonthEqualsAndYearEquals(
            Long userId, Long projectId, int month, int year);
}
