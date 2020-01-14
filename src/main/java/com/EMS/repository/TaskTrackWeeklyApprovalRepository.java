
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


    TaskTrackWeeklyApproval findByProjectProjectIdAndStartDateAndEndDateAndUserUserId(Long projectId, Date weekStart, Date weekEnd, Long userId);

    public List<TaskTrackWeeklyApproval> findByUserUserIdAndProjectProjectIdInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long userId, List<Long> projectIdList, Date endDate, Date startDate);
    
	@Query(value="SELECT * FROM tasktrack_weekly_approval where start_date=?1 AND end_date=?2 AND user_user_id=?3",nativeQuery=true)
	public TaskTrackWeeklyApproval getduplicateentrycount(Date startDate, Date endDate, long userId);
}

