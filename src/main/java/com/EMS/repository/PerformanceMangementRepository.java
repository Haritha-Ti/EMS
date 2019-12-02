package com.EMS.repository;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.PerformanceMangementModel;

public interface PerformanceMangementRepository extends JpaRepository<PerformanceMangementModel,Long>{

	@Query(" SELECT P.type as Type ,DATE_FORMAT(P.appraisal_date,'%Y-%m-%d')  as appraisal_date ,P.rating as rating  FROM PerformanceMangementModel P  WHERE P.user.userId=?1")
	JSONObject  getAppraisalDetailsByUserId(Long userID);
	
	@Query(" SELECT P FROM PerformanceMangementModel P  WHERE P.user.userId=?1")
	PerformanceMangementModel  getPerformanceManagementModelByUserId(Long userID);
}
