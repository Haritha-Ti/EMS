package com.EMS.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletResponse;

import com.EMS.model.*;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.repository.UserRepository;
import com.EMS.security.jwt.JwtTokenProvider;
import com.EMS.service.LoginService;
import com.EMS.service.PageRuleService;
import com.EMS.service.RegionService;
import com.EMS.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


@RestController
@RequestMapping(value = "/login")
public class LoginController {

	@Autowired
	LoginService login_service;

	@Autowired
	PageRuleService pageruleService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;
	
    @Autowired 
    UserRepository userRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Autowired
	private RegionService regionservice;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
	@PostMapping(value = "/getLoginCredentials")
	@ResponseBody
	public JsonNode adminLogin(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus) {
		
		ObjectNode response = objectMapper.createObjectNode();
		ObjectNode data = objectMapper.createObjectNode();

		String username = requestdata.get("username").asText();
		String password = requestdata.get("password").asText();
		
		try {
			
			if ((username != null) && (username.length() > 0) && (!username.equals(" ")) && (password != null)
					&& (password.length() > 0)) {

				authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	            String token = jwtTokenProvider.createToken(username, 
	            		this.userRepository.findByUserName(username)
	            		.orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found"))
	            		.getRole().getroleName());

				UserModel usercheck = login_service.login_authentication(username);

				if (usercheck == null) {
					LOGGER.info("User Authentication Failed");
					response.put("status", "Failed");
					response.put("code", httpstatus.getStatus());
					response.put("message", "Invalid user");
					response.put("payload", "");
				} else {
					LOGGER.info("User Authentication Success");
					response.put("status", "success");
					response.put("code", httpstatus.getStatus());
					response.put("message", "Valid user");
					data.put("username", usercheck.getUserName());
					data.put("userId", usercheck.getUserId());
					data.put("roleId", usercheck.getRole().getroleId());
					data.put("roleName", usercheck.getRole().getroleName());
						
					ObjectMapper mapper = new ObjectMapper();
					ArrayNode array = getBlockedPageList(usercheck.getRole().getroleId());
					data.putArray("allowedPages").addAll(array);
					data.put("token", token);
					
					response.set("payload", data);
				}

			} else {
				LOGGER.info("Invalid credientials");
				response.put("status", "Failed");
				response.put("code", httpstatus.getStatus());
				response.put("message", "Invalid credientials");
				response.put("payload", "");
			}

		}
		catch(BadCredentialsException b) {
			LOGGER.info("Exception in adminLogin Method");
			b.printStackTrace();
			// Setting status on json object
			response.put("status", "Failed");
			response.put("code", "Invalid credientials");
			response.put("message", "Exception " + b);
			response.put("payload", "");
		}
		catch (Exception e) {
			LOGGER.info("Exception in adminLogin Method");
			e.printStackTrace();
			// Setting status on json object
			response.put("status", "Failed");
			response.put("code", httpstatus.getStatus());
			response.put("message", "Exception " + e);
			response.put("payload", "");
		}
		return response;
	}

//	//api call for registering new user
	@PostMapping(value = "/adduser")
	public JsonNode adduser(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {

		ObjectNode responsedata = objectMapper.createObjectNode();
		int responseflag = 0;
		// setting values to usermodel
		UserModel user = new UserModel();

		try {

			user.setFirstName(requestdata.get("firstName").asText());
			user.setLastName(requestdata.get("lastName").asText());
			user.setContact(requestdata.get("contact").asLong());
			user.setUserName(requestdata.get("userName").asText());
			user.setBloodGroup(requestdata.get("bloodGroup").asText());
			user.setGender(requestdata.get("gender").asInt());
			user.setEmploymentType(requestdata.get("employment").asText());
			user.setActive(requestdata.get("active").asBoolean());

			Long departId = requestdata.get("department").asLong();
			DepartmentModel department = null;
			if (departId != null)
				department = login_service.getDepartment(departId);

			if (department != null)
				user.setDepartment(department);
			else
				responseflag = 1;

			Long roleId = requestdata.get("role").asLong();
			RoleModel role = null;
			if (roleId != null)
				role = login_service.getRole(roleId);
			if (role != null)
				user.setRole(role);

			//region add 
			Long regionId = null;
			if(requestdata.get("regionId") != null)
			{
				regionId = requestdata.get("regionId").asLong();
			}
			Region region = null;
			if(regionId != null) {
				
				region = regionservice.getregion(regionId);
			}
			
			if(region != null)
				user.setRegion(region);
			
			//add cpp level 
			
			Long cpp_level_id = requestdata.get("levelId").asLong();
			CppLevelModel cpplevel = null;
			if(cpp_level_id != null) {
				
				cpplevel = userService.findCppLevelById(cpp_level_id);
			}
			
			if(cpplevel != null)
				user.setCpplevels(cpplevel);
			
			
			
			
			// add timezone
			
			Long timezoneId = requestdata.get("timezoneId").asLong();
			TimeZoneModel zone = null;
			if(timezoneId != null) {
				
				zone = regionservice.getZone(timezoneId);
			}
			if(zone != null)
				user.setTimezone(zone);
			
			Long contractorId = requestdata.get("contractors").asLong();
			EmployeeContractors contractor = null;
			if ( contractorId != 0 )
				contractor = login_service.getContractor(contractorId);
			if (contractor != null)
				user.setContractor(contractor);

			String dob = requestdata.get("dob").asText();
			String joindate = requestdata.get("joiningDate").asText();

			// Formatting the dates before storing
			DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			Date date1 = null, date2 = null;

			if (!dob.isEmpty()) {
				date2 = formatter.parse(dob);
				user.setDob(date2);

			}
			if (!joindate.isEmpty()) {
				date1 = formatter.parse(joindate);
				user.setJoiningDate(date1);

			}

			user.setEmpId(requestdata.get("empId").asLong());
			user.setEmail(requestdata.get("email").asText());
			
			 String password = requestdata.get("password").toString().replace("\"", "");
			 /*
			 MessageDigest md = MessageDigest.getInstance("MD5");
			 md.update(password.getBytes());
			 byte[] digest = md.digest();
			 String encPassword = DatatypeConverter.printHexBinary(digest).toUpperCase();
			 */
			 String encPassword = this.passwordEncoder.encode(password);
			 user.setPassword(encPassword);  
			
			user.setQualification(requestdata.get("qualification").asText());
			Boolean isUsernameExist = login_service.checkUsernameDuplication(requestdata.get("userName").asText());
            Boolean isEmpIdExist = login_service.checkEmpIDDuplication(requestdata.get("empId").asLong());
			
			if(!isUsernameExist) {
				if(!isEmpIdExist) {
				UserModel userdata = login_service.adduser(user);
				if (userdata == null) {
					responseflag = 1;
					responsedata.put("message", "User record insertion failed");
				}
				else {

					//inserting task category against user during user creation
					Long user_id = user.getUserId();

					long[] taskArray;

					if(user_id !=null) {

						if (departId == 1) {            //department - production

							taskArray= new long[]{1,7};

						}else if(departId == 2) {      //department - Design and Graphics

							taskArray= new long[]{10,7};

						}else if(departId == 3) {       //department - Mobile

							taskArray= new long[]{1,7};

						}else if(departId == 4 ) {      //department - Testing
							taskArray= new long[]{4,7};

						}else if(departId == 5) {       //department - Sales and marketing

							taskArray= new long[]{2,7};

						}else if(departId == 6) {       //department - Human Resource

							taskArray= new long[]{3,7};

						}else if(departId == 7) {       //department - Administration

							taskArray= new long[]{7};

						}else if(departId == 8){       //department - Quality Assurance

							taskArray= new long[]{4,7};

						}else if(departId == 11) {    // department - House Keeping Association

							taskArray= new long[]{7};

						}else {

							taskArray = null;
						}

						if(taskArray != null) {

							for(long tasks : taskArray) {

								UserTaskCategory usertask   = new UserTaskCategory();

								UserModel newuser           = userService.getUserDetailsById(user_id);
								TaskCategory taskcategory   = userService.getTaskDetailsById(tasks);

								usertask.setTaskCategory(taskcategory);
								usertask.setUser(newuser);

								//adding details in user task category based on department
								userService.updateUserTaskCategory(usertask);
							}

						}

					}

					// adding details in user technology
					ArrayNode usertechnology = (ArrayNode) requestdata.get("userTechnology");

					if (!usertechnology.equals(null)) {


//						responseflag = 1;
//						responsedata.put("message", "Technology insertion failed");
//					} else {
						for (JsonNode node : usertechnology) {

							// checking for technology using ID

							Long techId = node.get("technology").asLong();
							Technology technology = null;


							if (techId != null)
								technology = login_service.findtechnology(techId);

							UserTechnology usertech = new UserTechnology();
							if (technology != null)
								usertech.setTechnology(technology);
							else {
								responseflag = 1;
								responsedata.put("message",
										"User technology insertion failed due to missing technology value");
							}
							usertech.setUser(userdata);
							usertech.setExperience(node.get("experience").asDouble());
							int userTechnology = login_service.addusertechnology(usertech);
							//System.out.println("userTechnology :"+userTechnology);
							if (userTechnology == 0)
								responseflag = 1;
						}

					}
				}
				}
				else {
					responseflag = 1;
					responsedata.put("message", "Employee ID already exist.");
				}
			}
			else {
				responseflag = 1;
				responsedata.put("message", "Username already exist.");
			}

			 

			if (responseflag == 0) {
				responsedata.put("status", "success");
				responsedata.put("message", "User added successfully");
				responsedata.put("code", servletresponse.getStatus());
			} else {
				responsedata.put("status", "Failed");
				responsedata.put("code", servletresponse.getStatus());
			}

		} catch (Exception e) {
			responsedata.put("status", "Failed");
			responsedata.put("message", "Exception : " + e);
			responsedata.put("code", servletresponse.getStatus());
		}

		return responsedata;
	}
	
	
	@PostMapping(value="/change_password")
	public JSONObject changePassword(@RequestBody JSONObject requestdata, HttpServletResponse httpstatus) throws NoSuchAlgorithmException {
		JSONObject responsedata = new JSONObject();
		
		try {
			
			String userId = requestdata.get("userId").toString();		
			String userName = requestdata.get("userName").toString();
			String password = requestdata.get("password").toString();		
			String newPassword = requestdata.get("newPassword").toString();		
			/*		
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());
			byte[] digest = md.digest();
			*/
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));

			UserModel usercheck = login_service.login_authentication(userName);
					
			if (usercheck!= null) {			
				
				String encPassword = this.passwordEncoder.encode(newPassword);
				usercheck.setPassword(encPassword);
				login_service.adduser(usercheck);
				responsedata.put("status", "success");
				responsedata.put("message", "Password Updated");
				responsedata.put("code", httpstatus.getStatus());
			}
			else {
				responsedata.put("status", "Failed");
				responsedata.put("message", "Password Not Matching");
				responsedata.put("code", httpstatus.getStatus());
			}			
		}
		catch(BadCredentialsException b) {
			LOGGER.info("Exception in adminLogin Method");
			b.printStackTrace();
			// Setting status on json object
			responsedata.put("status", "Failed");
			responsedata.put("code", "Invalid credientials");
			responsedata.put("message", "Exception " + b);
			responsedata.put("payload", "");
		}
		catch (Exception e) {
			responsedata.put("status", "Failed");
			responsedata.put("message", "Exception : " + e);
			responsedata.put("code", httpstatus.getStatus());
		}
		return responsedata;
	}
	private ArrayNode getBlockedPageList(long roleid) {

		ArrayNode blockedPageList = pageruleService.getBlockedPageList(roleid);
			return blockedPageList;		
	}

	
	
	
	@GetMapping("/getUserList")
	public JsonNode getUserList(HttpServletResponse httpstatus) throws ParseException {

		ObjectNode dataNode = objectMapper.createObjectNode();
		ObjectNode node = objectMapper.createObjectNode();
		ArrayNode userarray=objectMapper.createArrayNode();


		try {
			JsonNode userList = userService.getUserList();
			for(JsonNode nodeItem : userList) {
				ArrayNode techarray=objectMapper.createArrayNode();

				List<Object[]> technologyList = userService.getUserTechnologyList(nodeItem.get("userId").asLong());
				for(Object[] item : technologyList) {
					ObjectNode responseData=objectMapper.createObjectNode();
					String experience = String.valueOf(item[0]);
					String id = String.valueOf(item[1]);
					responseData.put("id", id);
					responseData.put("experience", experience);
					techarray.add(responseData);
				}
				((ObjectNode) nodeItem).set("technologyList", techarray);
				userarray.add(nodeItem);
			}
			
			dataNode.set("userList", userarray);

			node.put("status", "success");
			node.set("data", dataNode);

		} catch (Exception e) {
			System.out.println("Exception " + e);
			node.put("status", "failure");
			node.set("data", dataNode);
		}

		return node;

	}
	
	@GetMapping("/getUserDetails/{userId}")
	public JsonNode getUserDetails(@PathVariable("userId") Long userId,HttpServletResponse httpstatus) throws ParseException {

		ObjectNode userNode = objectMapper.createObjectNode();
		ObjectNode node = objectMapper.createObjectNode();
		ArrayNode techarray=objectMapper.createArrayNode();

		try {
			JsonNode userData = userService.getUserdetails(userId);
			String terminationType = login_service.getUserTerminationType(userId);
			((ObjectNode) userData).put("terminationType", terminationType);
			userNode.set("userList", userData);
			List<Object[]> technologyList = userService.getUserTechnologyList(userId);
			for(Object[] item : technologyList) {
				ObjectNode responseData=objectMapper.createObjectNode();
				String experience = String.valueOf(item[0]);
				String id = String.valueOf(item[1]);
				responseData.put("id", id);
				responseData.put("experience", experience);
				techarray.add(responseData);
			}
			((ObjectNode) userData).set("technologyList", techarray);

			node.put("status", "success");
			node.set("data", userNode);

		} catch (Exception e) {
			node.put("status", "failure");
			node.set("data", userNode);
		}

		return node;

	}

	@GetMapping("/technology")
	public JsonNode getTechnology(HttpServletResponse httpstatus) {
		ObjectNode responseData=objectMapper.createObjectNode();
		
		List<Technology> techList=login_service.getTechnology();
		ArrayNode techarray=objectMapper.createArrayNode();
		
		if (!techList.isEmpty()) {
		
			for (Technology tech : techList) {
				ObjectNode clientobj = objectMapper.createObjectNode();
				clientobj.put("technologyId", tech.getTechnologyId());
				clientobj.put("technologyName", tech.getTechnologyName());
				techarray.add(clientobj);
			}
		}
		responseData.put("status", "success");
		responseData.put("message", "success");
		responseData.put("code", httpstatus.getStatus());
		responseData.set("payload", techarray);
		
		return responseData;
		
	}

	@PutMapping(value = "/editUserDetails")
	public JsonNode setuserData(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus) {
		ObjectNode responseData = objectMapper.createObjectNode();
		try {
			
			Long userId  = requestdata.get("userId").asLong();
			UserModel user = userService.getUserDetailsById(userId);
			if ( user != null) {
				user.setFirstName(requestdata.get("firstName").asText());
				user.setLastName(requestdata.get("lastName").asText());
				user.setContact(requestdata.get("contact").asLong());
				user.setUserName(requestdata.get("userName").asText());
				user.setBloodGroup(requestdata.get("bloodGroup").asText());
				user.setGender(requestdata.get("gender").asInt());
				user.setEmploymentType(requestdata.get("employment").asText());
				user.setActive(requestdata.get("active").asBoolean());

				Long departId = requestdata.get("department").asLong();
				DepartmentModel department = null;
				if (departId != null)
					department = login_service.getDepartment(departId);

				if (department != null)
					user.setDepartment(department);
				
				Long roleId = requestdata.get("role").asLong();
				RoleModel role = null;
				if (roleId != null)
					role = login_service.getRole(roleId);
				if (role != null)
					user.setRole(role);


				Long contractorId = requestdata.get("contractor").asLong();
				EmployeeContractors contractor = null;
				if (contractorId != null)
					contractor = login_service.getContractor(contractorId);
				if (contractor != null)
					user.setContractor(contractor);

				String dob = requestdata.get("dob").asText();
				String joindate = requestdata.get("joiningDate").asText();

				// Formatting the dates before storing
				DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
				Date date1 = null, date2 = null;

				if (!dob.isEmpty()) {
					date2 = formatter.parse(dob);
					user.setDob(date2);

				}
				if (!joindate.isEmpty()) {
					date1 = formatter.parse(joindate);
					user.setJoiningDate(date1);

				}

				user.setEmpId(requestdata.get("empId").asLong());
				user.setEmail(requestdata.get("email").asText());
				user.setQualification(requestdata.get("qualification").asText());
    
				UserModel userModel = userService.updateUser(user);
				int  result = 1;
				Boolean isExist = userService.checkExistanceOfUserId(userId);

				if(isExist) {
					result = userService.deleteTechnology(userId);
				}
				

				if(result != 0) {
					ArrayNode usertechnology = (ArrayNode) requestdata.get("userTechnology");
					if (!usertechnology.equals(null)) {

						for (JsonNode node : usertechnology) {

							// checking for technology using ID

							Long techId = node.get("technology").asLong();
							Technology technology = null;

							if (techId != null)
								technology = login_service.findtechnology(techId);

							UserTechnology usertech = new UserTechnology();
							if (technology != null)
								usertech.setTechnology(technology);
							else {
								responseData.put("message",
										"User technology insertion failed due to missing technology value");
							}
							usertech.setUser(userModel);
							usertech.setExperience(node.get("experience").asDouble());
							int userTechnology = login_service.addusertechnology(usertech);
							
						}

					}
				}
				
			
				responseData.put("message", "User updated successfully");

				
				
			}
			else {
				responseData.put("message", "User does not exist");
			}
			responseData.put("status", "success");
			responseData.put("code", httpstatus.getStatus());
			
			
		}
		catch (Exception e) {
			responseData.put("status", "failure");
			responseData.put("code", httpstatus.getStatus());
		}
		return responseData;
		
	}

	@PutMapping(value = "v2/editUserDetails")
	public JsonNode setuserDataV2(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus) {
		ObjectNode responseData = objectMapper.createObjectNode();
		try {

			Long userId  = requestdata.get("userId").asLong();
			//UserModel user = userService.getUserDetailsById(userId);
			UserModel user = userService.getUserdetailsbyId(userId);
			if ( user != null) {
				user.setFirstName(requestdata.get("firstName").asText());
				user.setLastName(requestdata.get("lastName").asText());
				user.setContact(requestdata.get("contact").asLong());
				user.setUserName(requestdata.get("userName").asText());
				user.setBloodGroup(requestdata.get("bloodGroup").asText());
				user.setGender(requestdata.get("gender").asInt());
				user.setEmploymentType(requestdata.get("employment").asText());
				user.setActive(requestdata.get("active").asBoolean());

				Long departId = requestdata.get("department").asLong();
				DepartmentModel department = null;
				if (departId != null)
					department = login_service.getDepartment(departId);

				if (department != null)
					user.setDepartment(department);

				Long roleId = requestdata.get("role").asLong();
				RoleModel role = null;
				if (roleId != null)
					role = login_service.getRole(roleId);
				if (role != null)
					user.setRole(role);

				Long contractorId = requestdata.get("contractors").asLong();
				EmployeeContractors contractor = null;
				System.out.println("contractorId "+contractorId);
				if(contractorId ==0)
					user.setContractor(null);
				if (contractorId !=0 )
					contractor = login_service.getContractor(contractorId);
				if (contractor != null)
					user.setContractor(contractor);
				
				
				//region add 
				
				Long regionId = requestdata.get("regionId").asLong();
				Region region = null;
				if(regionId != null) {
					
					region = regionservice.getregion(regionId);
				}
				
				if(region != null)
					user.setRegion(region);
				
				//add cpp level 
				Long cpplevel_Id = requestdata.get("levelId").asLong();
				
				CppLevelModel cpplevel =null;
				if(cpplevel_Id != null) {
					cpplevel = userService.findCppLevelById(cpplevel_Id);
					
				}
				if(cpplevel != null)
				{
					user.setCpplevels(cpplevel);
				}
				
				// add timezone
				
				Long timezoneId = requestdata.get("timezoneId").asLong();
				TimeZoneModel zone = null;
				if(timezoneId != null) {
					
					zone = regionservice.getZone(timezoneId);
				}
				if(zone != null)
					user.setTimezone(zone);

				String dob = requestdata.get("dob").asText();
				String joindate = requestdata.get("joiningDate").asText();

				// Formatting the dates before storing
				DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
				Date date1 = null, date2 = null;

				if (!dob.isEmpty()) {
					date2 = formatter.parse(dob);
					user.setDob(date2);

				}
				if (!joindate.isEmpty()) {
					date1 = formatter.parse(joindate);
					user.setJoiningDate(date1);

				}

				user.setEmpId(requestdata.get("empId").asLong());
				user.setEmail(requestdata.get("email").asText());
				user.setQualification(requestdata.get("qualification").asText());
				String newpassword = requestdata.get("newPassword").asText();
				if(!newpassword.isEmpty()) {
					String encPassword = this.passwordEncoder.encode(newpassword);
					user.setPassword(encPassword);
				}
				String terminationDate = requestdata.get("terminationDate").asText();
				Date date3 = null;
				if (!terminationDate.isEmpty()) {
					date3 = formatter.parse(terminationDate);

				}
				user.setTerminationDate(date3);
				UserModel userModel = userService.updateUser(user);
				int  result = 1;
				Boolean isExist = userService.checkExistanceOfUserId(userId);

				if(isExist) {
					result = userService.deleteTechnology(userId);
				}


				if(result != 0) {
					ArrayNode usertechnology = (ArrayNode) requestdata.get("userTechnology");
					if (!usertechnology.equals(null)) {

						for (JsonNode node : usertechnology) {

							// checking for technology using ID

							Long techId = node.get("technology").asLong();
							Technology technology = null;

							if (techId != null)
								technology = login_service.findtechnology(techId);

							UserTechnology usertech = new UserTechnology();
							if (technology != null)
								usertech.setTechnology(technology);
							else {
								responseData.put("message",
										"user technology insertion failed due to missing technology value");
							}
							usertech.setUser(userModel);
							usertech.setExperience(node.get("experience").asDouble());
							int userTechnology = login_service.addusertechnology(usertech);

						}

					}

				}

				String terminationType = requestdata.get("terminationType").asText();
				Boolean isExist1 = login_service.checkExistanceOfUserIdInTermination(userId);
				if(isExist1)
				{
					login_service.updateUserTerm(terminationType,date3,userId);
				}
				else {
					if (!terminationType.isEmpty() && terminationType != "null") {
						UserTermination userterm = new UserTermination();
						userterm.setTermType(terminationType);
						if (!terminationDate.isEmpty()) {
							userterm.setTerminationDate(date3);
						}
						userterm.setConsultant(userModel);
						UserTermination userTermination = login_service.addusertermination(userterm);
					}


				}


				responseData.put("message", "User updated successfully");



			}
			else {
				responseData.put("message", "User not exist");
			}
			responseData.put("status", "success");
			responseData.put("code", httpstatus.getStatus());


		}
		catch (Exception e) {
			responseData.put("status", "failure");
			responseData.put("code", httpstatus.getStatus());
		}
		return responseData;

	}

	@GetMapping("/getAllUserList")
	public JsonNode getAllUserList(HttpServletResponse httpstatus) throws ParseException {

		ObjectNode dataNode = objectMapper.createObjectNode();
		ObjectNode node = objectMapper.createObjectNode();
		ArrayNode userarray=objectMapper.createArrayNode();


		try {
			JsonNode userList = userService.getAllUserList();
			for(JsonNode nodeItem : userList) {
				ArrayNode techarray=objectMapper.createArrayNode();

				List<Object[]> technologyList = userService.getUserTechnologyList(nodeItem.get("userId").asLong());
				for(Object[] item : technologyList) {
					ObjectNode responseData=objectMapper.createObjectNode();
					String experience = String.valueOf(item[0]);
					String id = String.valueOf(item[1]);
					responseData.put("id", id);
					responseData.put("experience", experience);
					techarray.add(responseData);
				}
				((ObjectNode) nodeItem).set("technologyList", techarray);
				userarray.add(nodeItem);
			}

			dataNode.set("userList", userarray);

			node.put("status", "success");
			node.set("data", dataNode);

		} catch (Exception e) {
			System.out.println("Exception " + e);
			node.put("status", "failure");
			node.set("data", dataNode);
		}

		return node;

	}
	  //Bala
	@PostMapping("/getAllUserListByRegion")
	public JsonNode getAllUserListByRegion(@RequestBody JsonNode requestdata,HttpServletResponse httpstatus) throws ParseException {


		ObjectNode dataNode = objectMapper.createObjectNode();
		ObjectNode node = objectMapper.createObjectNode();
		ArrayNode userarray=objectMapper.createArrayNode();
		Long userId = null;
        Long regionId=null;
		try {
			if (requestdata.get("sessionId") != null && requestdata.get("sessionId").asText() != "") {
				userId = requestdata.get("sessionId").asLong();
			}
			regionId = userService.getUserdetailsbyId(userId).getRegion().getId();
			JsonNode userList = userService.getAllUsersByRegion(regionId);
			for(JsonNode nodeItem : userList) {
				ArrayNode techarray=objectMapper.createArrayNode();

				List<Object[]> technologyList = userService.getUserTechnologyList(nodeItem.get("userId").asLong());
				for(Object[] item : technologyList) {
					ObjectNode responseData=objectMapper.createObjectNode();
					String experience = String.valueOf(item[0]);
					String id = String.valueOf(item[1]);
					responseData.put("id", id);
					responseData.put("experience", experience);
					techarray.add(responseData);
				}
				((ObjectNode) nodeItem).set("technologyList", techarray);
				userarray.add(nodeItem);
			}

			dataNode.set("userList", userarray);

			node.put("status", "success");
			node.set("data", dataNode);

		} catch (Exception e) {
			System.out.println("Exception " + e);
			node.put("status", "failure");
			node.set("data", dataNode);
		}

		return node;

	
		
	}
	//Bala
	@GetMapping("/getCppLevelList")
	public JsonNode getCppLevelList(HttpServletResponse httpstatus) throws ParseException {
		
		ObjectNode dataNode = objectMapper.createObjectNode();
		ObjectNode node = objectMapper.createObjectNode();
		ArrayNode cpplevelarray = objectMapper.createArrayNode();
		try {
		cpplevelarray = userService.getCppLevel();
		dataNode.set("cpplevels", cpplevelarray);
		node.put("status", "success");
		node.set("data", dataNode);
		}	
		catch (Exception e) {
			System.out.println("Exception " + e);
			node.put("status", "failure");
			node.set("data", dataNode);
		}
		return node;
	}
}
