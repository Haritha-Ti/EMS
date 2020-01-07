package com.EMS.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EMS.model.TaskTrackWeeklyApproval;

public interface TaskTrackWeeklyApprovalRepository extends JpaRepository<TaskTrackWeeklyApproval, Long> {

	public List<TaskTrackWeeklyApproval> findByUserUserIdAndProjectProjectIdInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
			Long userId, List<Long> projectIdList, Date endDate, Date startDate);
}
