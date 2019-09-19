package com.EMS.service;

import java.util.List;

import org.json.simple.JSONObject;

import com.EMS.model.Technology;
import com.EMS.model.UserModel;
import com.EMS.model.UserTechnology;
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
}
