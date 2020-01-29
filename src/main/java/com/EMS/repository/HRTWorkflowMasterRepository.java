package com.EMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EMS.model.HRTWorkflowMaster;

public interface HRTWorkflowMasterRepository extends JpaRepository<HRTWorkflowMaster, Long> {

	List<HRTWorkflowMaster> findByRegionId(Long regionId);

}
