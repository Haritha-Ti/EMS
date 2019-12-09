package com.EMS.service;

import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.EMS.model.Technology;
import com.EMS.model.UserModel;
import com.EMS.model.UserTechnology;
import com.EMS.model.CppLevelModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.EmploymentDetailsModel;
import com.EMS.model.PayrollModel;
import com.EMS.model.PerformanceMangementModel;
import com.EMS.model.SkillsModel;
import com.EMS.model.TaskCategory;
import com.EMS.model.UserTaskCategory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public interface UserService {
	
	UserModel getUserDetailsById(Long uId);

	TaskCategory getTaskDetailsById(Long taskid);

	public void updateUserTaskCategory(UserTaskCategory usertask);

	List<UserModel> getUserByDeptId(Long deptId);

	UserModel getUser(Long deptId, Long userId);

	List<UserModel> getprojectOwner();

	List<UserModel> getAllUsers();

//	List<Object[]> getUserIdLists(Long pageSize, Long startingIndex);
	List<Object[]> getUserIdLists();

	String getUserName(Long id);

	Long getCount();

	 JsonNode getUserList();

	JsonNode getUserdetails(Long userId);

	UserModel updateUser(UserModel user);

	int deleteTechnology(Long userId);

	Boolean checkExistanceOfUserId(Long userId);

	List<Object[]> getUserTechnologyList(Long userId);

	List<Object[]> getnewHire(int startmonth,int endmonth,int year);

	List<Technology> getprimarySkills(long userId);

	UserModel getUserdetailsbyId(long userId);

	UserModel getUserByUserName(String userName);

	JsonNode getAllUserList();
	
	List<UserModel> getOnsiteLead();

	ArrayNode getCppLevel();

	CppLevelModel findCppLevelById(Long cpp_level_id);

	List<UserModel> getUserByRegion(Long regionId);

	UserModel getUserBydeptRegion(Long deptId, Long userId, Long regionId);

	UserModel getUserByRegion(Long regionId, Long userId);
	
	//Bala
	public JsonNode getAllUsersByRegion(Long regionId);
	//Bala
	
	//Renjith
	public List<UserModel> getUserByRegionAndDepartment(Long regionId);
	//Renjith

	//Nisha
	List<UserModel> getUserByRegion(Date startDate, Date endDate, Long regionId);
	//nisha


	List<UserModel> getUsesrsBasedOnMonthYearRegion(Long regionId, int month, int year);

	List<UserModel> getUsesrsBasedOnMonthYearRegion(int month, int year);

	List<ProjectModel> getProjectNamesBasedOnMonthAndYearAndUser(Long regionId, int month, int year, Long userId);

	List<ProjectModel> getProjectNamesBasedOnMonthAndYearAndUser(int month, int year, Long userId);

	
	SkillsModel addSkills(SkillsModel skill);
	
	SkillsModel updateSkills(SkillsModel skill);
	
	JSONObject   getSkillsByUserId(Long userId);
	
	
	JSONObject   getSkillsId(Long userId);
	
	SkillsModel  getSkillModelByUserId(Long userID);
	
	PerformanceMangementModel addAppraisal(PerformanceMangementModel  model);
	
	PerformanceMangementModel updateAppraisal(PerformanceMangementModel  model);
	
	JSONObject   getAppraisalDetailsByUserId(Long userId);
	
	PerformanceMangementModel  getPerformanceManagementModelByUserId(Long userID);
	
	
   EmploymentDetailsModel addEmploymentDetails(EmploymentDetailsModel  model);
	
   EmploymentDetailsModel updateEmploymentDetailsModel(EmploymentDetailsModel  model);
	
	JSONObject   getEmploymentDetailsByUserId(Long userId);
	
	EmploymentDetailsModel  getEmploymentDetailsModelByUserId(Long userID);
	
	
	    PayrollModel addPayroll(PayrollModel  model);
	
	    PayrollModel updatePayroll(PayrollModel  model);
		
	    JSONObject  getPayrollByUserId(Long userID);
	    
	    PayrollModel  PayrollByUserId(Long userID);

		List<UserModel> getUsersByRegionAndDate(Long regionId, Date startDate, Date endDate);
	
	
	
	
	
	
	  

}
