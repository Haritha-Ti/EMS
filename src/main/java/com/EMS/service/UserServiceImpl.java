package com.EMS.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.CppLevelModel;
import com.EMS.model.PasswordResetModel;
import com.EMS.model.Technology;
import com.EMS.model.UserModel;
import com.EMS.model.TaskCategory;
import com.EMS.model.UserTaskCategory;
import com.EMS.model.UserTechnology;
import com.EMS.repository.CppLevelRepository;
import com.EMS.repository.PasswordResetRepository;
import com.EMS.repository.TechnologyRepository;
import com.EMS.repository.UserRepository;
import com.EMS.repository.UserTechnologyRepository;
import com.EMS.repository.UserTaskCategoryRepository;
import com.EMS.repository.TaskRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserTechnologyRepository userTechnologyRepository;
	
	@Autowired
	private TechnologyRepository technologyRepository;

	@Autowired
	private TaskRepository TaskRepository;

	@Autowired
	private UserTaskCategoryRepository UserTaskCategoryRepository;
	
	@Autowired
	private CppLevelRepository cppLevelRepository;
	
	@Override
	public UserModel getUserDetailsById(Long id) {
		return userRepository.getActiveUser(id);

	}

	//@Override
	public TaskCategory getTaskDetailsById(Long taskid) {

		TaskCategory task =TaskRepository.findByTaskId(taskid);
		/*TaskCategory taskcat = new TaskCategory();
		//taskcat.setId(Long.parseLong(task[0].toString()));
		taskcat.setId(Long.parseLong(task[0].toString()));
		taskcat.setDescription(task[1].toString());
        taskcat.setName(task[2].toString());*/

		return task;

	}

	@Override
	public List<UserModel> getUserByDeptId(Long deptId) {
		List<UserModel> userList = userRepository.findByDeptId(deptId);
		return userList;
	}

	@Override
	public UserModel getUser(Long deptId, Long userId) {
		UserModel user = userRepository.getUser(userId, deptId);
		return user;
	}

	@Override
	public List<UserModel> getprojectOwner() {
		List<UserModel> user_owner = new ArrayList<UserModel>();
		user_owner = userRepository.getProjectOwners();

		return user_owner;
	}

	@Override
	public List<UserModel> getAllUsers() {
		List<UserModel> users = userRepository.getUser();
		return users;
	}


	@Override
	public String getUserName(Long id) {
		String name = userRepository.getUserName(id);
		return name;
	}

//	@Override
//	public List<Object[]> getUserIdLists(Long pageSize, Long startingIndex) {
//		List<Object[]> userIdList = userRepository.getUserIdLists(pageSize,startingIndex);
//		return userIdList;
//	}
	@Override
	public List<Object[]> getUserIdLists() {
		List<Object[]> userIdList = userRepository.getUserIdLists();
		return userIdList;
	}

	@Override
	public Long getCount() {
		Long count = userRepository.getUserCount();
		return count;
	}

	@Override
	public JsonNode getUserList() {
		JsonNode node = objectMapper.createObjectNode();
		node = objectMapper.convertValue(userRepository.getUser(), JsonNode.class);
		return node;
	}

	@Override
	public JsonNode getUserdetails(Long userId) {
		JsonNode node = objectMapper.createObjectNode();
		node = objectMapper.convertValue(userRepository.getUserById(userId), JsonNode.class);
		return node;
	}

	@Override
	public UserModel updateUser(UserModel user) {
		UserModel userModel = userRepository.save(user);
		return userModel;
	}

	@Override
	public int deleteTechnology(Long userId) {
		System.out.println("userId: "+userId);

		int userTechnology = userTechnologyRepository.deleteByUserId(userId);
		System.out.println("userId 1 : "+userId);

		return userTechnology;
	}

	@Override
	public Boolean checkExistanceOfUserId(Long userId) {
		Boolean isExist = userTechnologyRepository.checkExistanceOfUserId(userId);
		return isExist;
	}

	@Override
	public List<Object[]> getUserTechnologyList(Long userId) {
		
		List<Object[]> list = technologyRepository.getUserTechnologyList(userId);
		return list;
		
	}

	@Override
	public List<Object[]> getnewHire(int startmonth,int endmonth,int year) {
		System.out.println("month :"+startmonth+" endmonth:"+endmonth);
		
		List<Object[]> list=userRepository.findnewHire(startmonth,endmonth,year);

		return list;
	}

	@Override
	public List<Technology> getprimarySkills(long userId) {
		List<Technology> list=userTechnologyRepository.getPrimarySkills(userId);
		return list;
	}

	@Override
	public UserModel getUserdetailsbyId(long userId) {
		UserModel user=userRepository.getOne(userId);
		return user;
	}
	
	@Override
	public UserModel getUserByUserName(String userName) {
		UserModel user = null;
		Optional<UserModel> userList =  userRepository.findByUserName(userName);
		if(userList.isPresent()) {
			user = userList.get();
		}
		return user;
	}

	public void updateUserTaskCategory(UserTaskCategory usertask) {

		UserTaskCategoryRepository.save(usertask);

	}

	@Override
	public JsonNode getAllUserList() {
		JsonNode node = objectMapper.createObjectNode();
		node = objectMapper.convertValue(userRepository.getAllUsers(), JsonNode.class);
		return node;
	}
	
	@Override
	public List<UserModel> getOnsiteLead() {
		// TODO Auto-generated method stub
		List<UserModel> onsite_lead = new ArrayList<UserModel>();
		onsite_lead = userRepository.getOnsiteLeads();
		return onsite_lead;

	}

	@Override
	public ArrayNode getCppLevel() {
		// TODO Auto-generated method stub
		ArrayNode cpplevels = objectMapper.createArrayNode();
		List<CppLevelModel> cpplevelsdata = cppLevelRepository.findAll();
		
		for(CppLevelModel data : cpplevelsdata) {
			ObjectNode node = objectMapper.createObjectNode();
			node.put("levelId", data.getLevelId());
			node.put("levelName", data.getLevelName());
			cpplevels.add(node);
		}
		
		return cpplevels;
	}

	@Override
	public CppLevelModel findCppLevelById(Long cpp_level_id) {
		// TODO Auto-generated method stub
		return cppLevelRepository.getOne(cpp_level_id);
	}

	@Override
	public List<UserModel> getUserByRegion(Long regionId) {
		// TODO Auto-generated method stub
		List<UserModel> modles = userRepository.getUserlistByregion(regionId);
		return modles;
	}

	@Override
	public UserModel getUserBydeptRegion(Long deptId, Long userId, Long regionId) {
		// TODO Auto-generated method stub
		UserModel modles = userRepository.getUserlistByregionDeptuser(deptId,userId,regionId);
		return modles;
	}
}
