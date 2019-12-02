package com.EMS.repository;

import org.json.simple.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.EmploymentDetailsModel;

public interface EmploymentDetailsRepository extends JpaRepository<EmploymentDetailsModel, Long> {

	@Query(" SELECT E FROM EmploymentDetailsModel E  WHERE E.user.userId=?1  ")
	EmploymentDetailsModel EmploymentDetailsByUserId(Long userID);

	@Query(" SELECT  DATE_FORMAT(E.contractStartdate,'%Y-%m-%d')  as contractStartdate ,DATE_FORMAT(E.contractEnddate,'%Y-%m-%d') as contractEnddate , DATE_FORMAT(E.regularizationDate,'%Y-%m-%d')  as regularizationDate, E.newCppLevel as newCppLevel ,"
			+ " DATE_FORMAT(E.newCppLevelEffDate,'%Y-%m-%d')  as  newCppLevelEffDate , "
			+ " E.visaStatus as visaStatus ,E.visaType as visaType ,"
			+ " DATE_FORMAT(E.visaExpDate,'%Y-%m-%d')  as visaExpDate ,E.source as source , "
			+ " E.referredby as referredby ,E.termtype as termtype ,DATE_FORMAT(E.lastWorkDate,'%Y-%m-%d')  as lastWorkDate , DATE_FORMAT(E.payThruDate,'%Y-%m-%d')  as payThruDate, DATE_FORMAT(E.terminationDate,'%Y-%m-%d')  as  terminationDate , "
			+ " E.terminationReason as  terminationReason "
			+ " FROM EmploymentDetailsModel E  WHERE E.user.userId=?1  ")
	JSONObject getEmploymentDetailsByUserId(Long userID);

}
