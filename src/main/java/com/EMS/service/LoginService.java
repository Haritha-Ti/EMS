package com.EMS.service;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.EMS.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface LoginService {

	

//		abstract method declaration for user login authentication
	UserModel login_authentication(String userName);

	UserModel adduser(UserModel requestdata);

	//abstract method declaration for add user technology data
	int addusertechnology(UserTechnology usertech);
	
	//find technology by given ID
	Technology findtechnology(Long parseInt);
	
	//find department by given ID
	DepartmentModel getDepartment(long parseLong);
	
	//find role by given ID
	RoleModel getRole(long parseLong);

	UserModel changePasswordAuthentication(long parseLong, String oldPassword);

	Boolean checkUsernameDuplication(String userName);

	List<Technology> getTechnology();

	Boolean checkEmpIDDuplication(long empId);

	UserTermination addusertermination(UserTermination requestdata);

	String getUserTerminationType(long userId);

	Boolean checkExistanceOfUserIdInTermination(Long userId);

	void updateUserTerm(String terminationType, Date date3, long userId);

	public EmployeeContractors getContractor(long contractorId);
	
	ObjectNode adminLogin(UserModel usercheck, HttpServletResponse httpstatus);
	
	ArrayNode getBlockedPageList(long roleid);
	
	UserTechnology getUserTechnology(Long userTechId);

	int deleteUserTechnology(Long userTechnologyId);

	int updateusertechnology(UserTechnology userTechnology);

}
