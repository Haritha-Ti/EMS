package com.EMS.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EMS.model.ApprovalUserAsignModel;
import com.EMS.model.Tasktrack;

public interface AssignUserRepository  extends JpaRepository<ApprovalUserAsignModel, Long> {
	
	List<ApprovalUserAsignModel> findByProjectIdProjectId(Long projectId);

}
