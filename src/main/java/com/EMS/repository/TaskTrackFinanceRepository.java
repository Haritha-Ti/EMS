package com.EMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.TaskTrackApprovalFinance;
import com.EMS.model.TaskTrackApprovalLevel2;

public interface TaskTrackFinanceRepository extends JpaRepository<TaskTrackApprovalFinance, Long>{

	@Query("SELECT  f FROM TaskTrackApprovalFinance f where f.user.userId = ?1 and f.month = ?2 and f.year =?3 and f.project.projectId = ?4")
	List<TaskTrackApprovalFinance> getDatas(Long userId, int monthIndex, int yearIndex, Long projectId);

}
