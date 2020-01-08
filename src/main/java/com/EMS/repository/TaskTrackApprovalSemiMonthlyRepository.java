package com.EMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EMS.model.TasktrackApprovalSemiMonthly;

public interface TaskTrackApprovalSemiMonthlyRepository extends JpaRepository<TasktrackApprovalSemiMonthly, Long> {

	public List<TasktrackApprovalSemiMonthly> findByUserUserIdAndProjectProjectIdInAndMonthAndYear(Long userId,
			List<Long> projectIdList, Integer month, Integer year);

}
