package com.EMS.service;

import java.util.*;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.CppLevelModel;
import com.EMS.model.EmploymentDetailsModel;
import com.EMS.model.PasswordResetModel;
import com.EMS.model.PayrollModel;
import com.EMS.model.PerformanceMangementModel;
import com.EMS.model.SkillsModel;
import com.EMS.model.Technology;
import com.EMS.model.UserModel;
import com.EMS.model.TaskCategory;
import com.EMS.model.UserTaskCategory;
import com.EMS.model.UserTechnology;
import com.EMS.repository.CppLevelRepository;
import com.EMS.repository.EmploymentDetailsRepository;
import com.EMS.repository.PasswordResetRepository;
import com.EMS.repository.PayrollRepository;
import com.EMS.repository.PerformanceMangementRepository;
import com.EMS.repository.SkillsRepository;
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
	
	@Autowired
	private  SkillsRepository  skillsRepository;
	
	@Autowired
	private PerformanceMangementRepository  performanceMangementRepository;
	
	@Autowired
	private EmploymentDetailsRepository  employmentDetailsRepository;
	
	@Autowired
	private PayrollRepository payrollRepository;
	
	@Override
	public UserModel getUserDetailsById(Long id) {
		return userRepository.getNonActiveUser(id);

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

	@Override
	public UserModel getUserByRegion(Long regionId, Long userId) {
		// TODO Auto-generated method stub
		
		UserModel model = userRepository.getUserByRegion(regionId,userId);
		return model;
	}
	
	//Bala
	@Override
	public JsonNode getAllUsersByRegion(Long regionId){
		
		JsonNode node = objectMapper.createObjectNode();
		node = objectMapper.convertValue(userRepository.getAllUsersByRegion(regionId), JsonNode.class);
		return node;
		
	}
	//Bala
	
	//Renjith
	@Override
	public List<UserModel> getUserByRegionAndDepartment(Long regionId) {
		// TODO Auto-generated method stub
		List<UserModel> modles = userRepository.getUserlistByregionAndDepartment(regionId);
		return modles;
	}
	//Renjith
	//Nisha
	@Override
	public List<UserModel> getUserByRegion(Date startDate, Date endDate, Long regionId) {
		// TODO Auto-generated method stub
		List<UserModel> modles = userRepository.getUserlistByregion(startDate,endDate,regionId);
		return modles;
	}
	//Nisha

	@Override
	public SkillsModel addSkills(SkillsModel skill) {
		SkillsModel skillModel=null;
		try {
			skillModel = skillsRepository.save(skill);
		} catch (Exception e) {
			System.out.println("Exception"+e);
		}
		
		return skillModel;
	}

	@Override
	public SkillsModel updateSkills(SkillsModel skill) {
		SkillsModel skillModel=null;
		try {
			skillModel = skillsRepository.save(skill);
		} catch (Exception e) {
			System.out.println("Exception"+e);
		}
		
		return skillModel;
	}

	@Override
	public JSONObject getSkillsByUserId(Long userId) {
		List<JSONObject> ls=skillsRepository.getSkillsByUserId(userId);
		if(ls.isEmpty())
			return null;
		JSONObject  node = new JSONObject();
		node.put("Skills", ls);
		return node;
	}

	@Override
	public JSONObject getSkillsId(Long userId) {
		//SkillsModel ls=skillsRepository.findByuserId(userId);
		JSONObject  node = new JSONObject();
		node.put("Skills", null);
		return node;
	}

	@Override
	public SkillsModel getSkillModelByUserId(Long userID) {
		
		return skillsRepository.getAllSkillsByUserId(userID).get(0);
	}

	@Override
	public PerformanceMangementModel addAppraisal(PerformanceMangementModel model) {
		

		PerformanceMangementModel pmObj=null;
		try {
			pmObj = performanceMangementRepository.save(model);
		} catch (Exception e) {
			System.out.println("Exception"+e);
		}
		
		return pmObj;
	
	}
	
	@Override
	public PerformanceMangementModel updateAppraisal(PerformanceMangementModel model) {
		PerformanceMangementModel pmObj=null;
		try {
			pmObj = performanceMangementRepository.save(model);
		} catch (Exception e) {
			System.out.println("Exception"+e);
		}
		
		return pmObj;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject   getAppraisalDetailsByUserId(Long userId){

		JSONObject  pmObject=performanceMangementRepository.getAppraisalDetailsByUserId(userId);
		if(pmObject==null)
			return null;
		JSONObject  node = new JSONObject();
		node.put("appraisal", pmObject);
		return node;
	
		
	}

	@Override
	public PerformanceMangementModel getPerformanceManagementModelByUserId(Long userID) {
		return  performanceMangementRepository.getPerformanceManagementModelByUserId(userID);
	}

	@Override
	public EmploymentDetailsModel addEmploymentDetails(EmploymentDetailsModel model) {
		EmploymentDetailsModel empDetailsObj=null;
		try {
			empDetailsObj = employmentDetailsRepository.save(model);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception"+e);
		}
		
		return empDetailsObj;
	}

	@Override
	public EmploymentDetailsModel updateEmploymentDetailsModel(EmploymentDetailsModel model) {
		EmploymentDetailsModel empDetailsObj=null;
		try {
			empDetailsObj = employmentDetailsRepository.save(model);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception"+e);
		}
		
		return empDetailsObj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getEmploymentDetailsByUserId(Long userId) {

		JSONObject  empDetailsObj=employmentDetailsRepository.getEmploymentDetailsByUserId(userId);
		if(empDetailsObj==null)
			return null;
		JSONObject  node = new JSONObject();
		node.put("employmentdetails", empDetailsObj);
		return node;
	
		
	}

	@Override
	public EmploymentDetailsModel getEmploymentDetailsModelByUserId(Long userID) {
		return  employmentDetailsRepository.EmploymentDetailsByUserId(userID);
	}

	@Override
	public PayrollModel addPayroll(PayrollModel model) {
		PayrollModel payroll=null;
		try {
			payroll = payrollRepository.save(model);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception"+e);
		}
		
		return payroll;
	}

	@Override
	public PayrollModel updatePayroll(PayrollModel model) {
		PayrollModel payroll=null;
		try {
			payroll = payrollRepository.save(model);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception"+e);
		}
		
		return payroll;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getPayrollByUserId(Long userID) {


		JSONObject  payroll=payrollRepository.getPayrollByUserId(userID);
		if(payroll==null)
			return null;
		JSONObject  node = new JSONObject();
		node.put("payroll", payroll);
		return node;
	
		
	
	}

	@Override
	public PayrollModel PayrollByUserId(Long userID) {
		
		return payrollRepository.PayrollByUserId(userID);
	}

}
