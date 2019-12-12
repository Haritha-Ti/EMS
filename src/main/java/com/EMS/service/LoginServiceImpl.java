package com.EMS.service;


import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.EMS.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.EMS.repository.UserTechnologyRepository;

import com.EMS.repository.DepartmentRepository;
import com.EMS.repository.RoleRepository;
import com.EMS.repository.TechnologyRepository;
import com.EMS.repository.UserRepository;
import com.EMS.repository.UserTerminationRepository;
import com.EMS.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.EMS.repository.EmployeeContractorsRepository;

@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	UserRepository user_repositary;
	
	@Autowired
	UserTechnologyRepository usertechnology_repository;
	
	@Autowired
	TechnologyRepository technology_repository;
	
	@Autowired
	DepartmentRepository department_repository;
	
	@Autowired
	RoleRepository role_repository;

	@Autowired
	UserTerminationRepository userTerminationRepository;

	@Autowired
	EmployeeContractorsRepository employeeContractorsRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private PageRuleService pageruleService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private LoginService login_service;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
//	 Implementation for authenticating user with role

	@Override
	public UserModel login_authentication(String userName) {

//		Initializing usermodel for returning		
		UserModel checkuserid = null;
		
		try {
			System.out.println("una : "+userName);
			//System.out.println("pwd : "+password);

//			calling sql query by passing parameters	
			checkuserid = user_repositary.findByUserName(userName).get();//getUserdetails(userName/*, password*/);

			return checkuserid;
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			return checkuserid;
		}
	}

	
	// method for creating user record
	@Override
	public UserModel adduser(UserModel requestdata) {
		return user_repositary.save(requestdata);
	}
	
	//method for creating records on usertechnology
	@Override
	public int addusertechnology(UserTechnology usertech) {
		
		int usertechno=usertechnology_repository.save(usertech);
		return usertechno;
	}

	//method for finding technology by ID
	@Override
	public Technology findtechnology(Long id) {

		Technology technology=technology_repository.getOne(id);
		return technology;
	}
	
	//method for finding department by ID
	@Override
	public DepartmentModel getDepartment(long id) {

		DepartmentModel department=department_repository.getOne(id);
		return department;
	}
	
	//method for finding department by Id
	@Override
	public RoleModel getRole(long id) {
		
		RoleModel role=role_repository.getOne(id);
		return role;
	}


	@Override
	public UserModel changePasswordAuthentication(long userId, String password) {
		//Initializing usermodel for returning		
				UserModel checkuserid = null;
				try {

				//calling sql query by passing parameters			
					checkuserid = user_repositary.getUserByUserId(userId, password);
					return checkuserid;
				} catch (Exception e) {
					System.out.println("Exception : " + e);
					return checkuserid;
				}
	}


	@Override
	public Boolean checkUsernameDuplication(String userName) {
		Boolean isUsernameExist = user_repositary.checkExistanceOfUserName(userName);
		return isUsernameExist;
	}


	@Override
	public List<Technology> getTechnology() {
		List<Technology> techlist=technology_repository.getTechnologies();
		return techlist;
	}


	@Override
	public Boolean checkEmpIDDuplication(long empId) {
		Boolean isUsernameExist = user_repositary.checkExistanceOfEmpId(empId);
		return isUsernameExist;
	}


	public UserTermination addusertermination(UserTermination requestdata) {
		UserTermination userTermination=null;
		try {

			userTermination=userTerminationRepository.save(requestdata);
			return userTermination;
		}catch(Exception e) {
			System.out.println("Exception : "+e);
			return userTermination;
		}

	}

	public String getUserTerminationType(long userId){
		String userTermType = null;
		try {

			userTermType=userTerminationRepository.getTermType(userId);
			return userTermType;
		}catch(Exception e) {
			System.out.println("Exception : "+e);
			return userTermType;
		}
	}

	public Boolean checkExistanceOfUserIdInTermination(Long userId) {
		int result = 0;
		result = userTerminationRepository.checkExistanceOfUserId(userId);
		System.out.println("result "+result);

		if(result>0)
			return true;
		else
			return false;
	}

	public void updateUserTerm(String terminationType, Date date3, long userId){


		try {
			userTerminationRepository.updateUserTerm(terminationType,date3,userId);

		}catch(Exception e) {
			System.out.println("Exception : "+e);

		}

	}

	//method for finding contractor by Id
	@Override
	public EmployeeContractors getContractor(long contractorId) {

		EmployeeContractors contractor=employeeContractorsRepository.getOne(contractorId);
		return contractor;
	}

	@Override
	public ObjectNode adminLogin(UserModel usercheck, HttpServletResponse httpstatus) {

		ObjectNode response = objectMapper.createObjectNode();
		ObjectNode data = objectMapper.createObjectNode();
	
		try {
				if(!usercheck.isActive()) {
					LOGGER.info("Inactive User");
					response.put("status", "Failed");
					response.put("code", httpstatus.getStatus());
					response.put("message", "User account has been deactivated");
					response.put("payload", "");
					return response;
				}
				
				String token = jwtTokenProvider.createToken(usercheck.getUsername(), usercheck.getRole().getroleName(), usercheck.getRole().getroleId());

				LOGGER.info("User Authentication Success");
				response.put("status", "success");
				response.put("code", httpstatus.getStatus());
				response.put("message", "Valid user");
				data.put("username", usercheck.getUserName());
				data.put("userId", usercheck.getUserId());
				data.put("roleId", usercheck.getRole().getroleId());
				data.put("roleName", usercheck.getRole().getroleName());
				data.put("regionName", usercheck.getRegion().getRegion_name());

				ArrayNode array = getBlockedPageList(usercheck.getRole().getroleId());
				data.putArray("allowedPages").addAll(array);
				data.put("token", token);

				response.set("payload", data);

		} catch (Exception exception) {
			LOGGER.info("Exception in adminLogin Method");
			exception.printStackTrace();
			response.put("status", "Failed");
			response.put("code", httpstatus.getStatus());
			response.put("message", "Exception : " + exception);
			response.put("payload", "");
		}
		return response;
	}
	
	@Override
	public ArrayNode getBlockedPageList(long roleid) {
		ArrayNode blockedPageList = pageruleService.getBlockedPageList(roleid);
		return blockedPageList;
	}
}
