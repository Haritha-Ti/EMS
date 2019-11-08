package com.EMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.EMS.model.TaskTrackApprovalFinal;

@Repository
public interface TaskTrackApprovalFinalRepository extends JpaRepository<TaskTrackApprovalFinal, Long> {
	
	@Query("SELECT finalApproval FROM TaskTrackApprovalFinal finalApproval "
			+ "where finalApproval.month = ?3 and finalApproval.year = ?4 and finalApproval.project.projectId = ?2 and finalApproval.user.userId = ?1")
	List<TaskTrackApprovalFinal> getUserFinalApprovalList(Long userId, Long projectId, Integer monthIndex, Integer yearIndex);
}
