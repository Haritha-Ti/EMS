package com.EMS.controller;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer.UserInfoEndpointConfig;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.exceptions.BadInputException;
import com.EMS.model.AllocationModel;
import com.EMS.model.DepartmentModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.ProjectRegion;
import com.EMS.model.Region;
import com.EMS.model.UserModel;
import com.EMS.service.AttendanceService;
import com.EMS.service.ProjectAllocationService;
import com.EMS.service.ProjectRegionService;
import com.EMS.service.ProjectService;
import com.EMS.service.RegionService;
import com.EMS.service.ReportService;
import com.EMS.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value = "/project")
public class ProjectAllocationController {

	@Autowired
	ProjectAllocationService projectAllocation;

	@Autowired
	ProjectService projectService;

	@Autowired
	UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	ReportService reportService;

	@Autowired
	AttendanceService attendanceService;

	@Autowired
	private RegionService regionService;

	@Autowired
	private ProjectRegionService projectRegionService;

	@Autowired
	private ProjectService projectservice;

	@Autowired
	private RegionService regionservice;

	// To get user, department and project list

	@GetMapping(value = "/getPreResourceData")
	public ObjectNode getUsernameList(HttpServletResponse httpstatus) {
		ObjectNode jsonData = objectMapper.createObjectNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		try {
			// Method invocation for getting user list
			// List<UserModel> userList = projectAllocation.getUserList();
			List<UserModel> userList = projectAllocation.getUserLists();
			// Method invocation for getting department list
			List<DepartmentModel> departmentList = projectAllocation.getDepartmentList();
			// Method invocation for getting project list
			List<ProjectModel> projectList = projectService.getAllActiveProjectList();

			ArrayNode jsonArray = objectMapper.createArrayNode();
			ArrayNode jsonProjectArray = objectMapper.createArrayNode();
			ArrayNode jsonDepartmentArray = objectMapper.createArrayNode();

			// Add user list to json object
			if (!(userList).isEmpty() && userList.size() > 0) {
				for (UserModel user : userList) {
					ObjectNode jsonObject = objectMapper.createObjectNode();
					jsonObject.put("userId", user.getUserId());
					jsonObject.put("firstName", user.getFirstName());
					jsonObject.put("lastName", user.getLastName());
					jsonObject.put("role", user.getRole().getroleId());
					jsonObject.put("region_id", user.getRegion().getId());
					DepartmentModel departmentModel = user.getDepartment();
					ObjectNode depNode = objectMapper.createObjectNode();
					depNode.put("departmentId", departmentModel.getDepartmentId());
					depNode.put("departmentName", departmentModel.getdepartmentName());

					jsonObject.set("department", depNode);

					LocalDate now = LocalDate.now();
					int quarter = 0;
					int monthNumber = now.getMonthValue();
					int year = now.getYear();

					if (monthNumber >= 1 && monthNumber <= 3)
						quarter = 1;
					else if (monthNumber >= 4 && monthNumber <= 6)
						quarter = 2;
					else if (monthNumber >= 7 && monthNumber <= 9)
						quarter = 3;
					else if (monthNumber >= 10 && monthNumber <= 12)
						quarter = 4;
					ObjectNode leaveBalanceNode = attendanceService.getLeavebalanceData(user.getUserId(), quarter,
							year);
					jsonObject.set("leaveBalance", leaveBalanceNode);
					jsonArray.add(jsonObject);
				}
				jsonData.set("userList", jsonArray);

			}

			// Add project list to json object
			if (!(projectList).isEmpty() && projectList.size() > 0) {
				for (ProjectModel project : projectList) {
					ObjectNode jsonObject = objectMapper.createObjectNode();
					jsonObject.put("projectId", project.getProjectId());
					jsonObject.put("projectName", project.getProjectName());

					// get region list
					List<ProjectRegion> regions = projectservice.getregionlist(project.getProjectId());
					ArrayNode regionsArray = objectMapper.createArrayNode();
					ArrayList<Integer> regionArraylist = new ArrayList<Integer>();
					if (regions.isEmpty()) {
						jsonObject.set("projectRegion", regionsArray);
					} else {

						for (ProjectRegion regioneach : regions) {
							ObjectNode resource = objectMapper.createObjectNode();
							regionsArray.add((int) regioneach.getRegion_Id().getId());

						}
						jsonObject.set("projectRegion", regionsArray);
					}
					//
					jsonProjectArray.add(jsonObject);
				}
				jsonData.set("projectList", jsonProjectArray);
			}

			// Add department list to json object
			if (!(departmentList).isEmpty() && departmentList.size() > 0) {
				for (DepartmentModel department : departmentList) {
					ObjectNode jsonObject = objectMapper.createObjectNode();
					jsonObject.put("departmentId", department.getDepartmentId());
					jsonObject.put("departmentName", department.getdepartmentName());
					jsonDepartmentArray.add(jsonObject);
				}
				jsonData.set("departmentList", jsonDepartmentArray);
			}

			jsonDataRes.set("data", jsonData);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "success");
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;
	}

	// Renjith

	@PostMapping("/getUserDataByRegion")
	public JSONObject getAllocationListByRegion(@RequestBody JSONObject requestData, HttpServletResponse httpstatus) {

		JSONObject jsonData = new JSONObject();
		JSONObject jsonDataRes = new JSONObject();
		List<JSONObject> jsonArrayFiltered = new ArrayList<>();
		TimeZone zone = TimeZone.getTimeZone("MST");
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		outputFormat.setTimeZone(zone);
		java.util.Date date1 = null, date2 = null;
		String uId = null, dId = null, startDate = null, endDate = null;
		String regionId = null;
		Long sessionId = null;
		Long loginUserRoleId = null;
		Long loginUserRegionId = null;
		try {

			if (!requestData.get("startDate").toString().isEmpty() && requestData.get("startDate").toString() != null) {
				startDate = requestData.get("startDate").toString();
				if (!startDate.isEmpty()) {
					date1 = outputFormat.parse(startDate);

				}
			}
			if (!requestData.get("endDate").toString().isEmpty() && requestData.get("endDate").toString() != null) {
				endDate = requestData.get("endDate").toString();
				if (!endDate.isEmpty()) {
					date2 = outputFormat.parse(endDate);

				}
			}

			if (!requestData.get("userId").toString().isEmpty() && requestData.get("userId").toString() != null)
				uId = requestData.get("userId").toString();
			if (!requestData.get("deptId").toString().isEmpty() && requestData.get("deptId").toString() != null)
				dId = requestData.get("deptId").toString();

			if (!requestData.get("regionId").toString().isEmpty() && requestData.get("regionId").toString() != null)
				regionId = requestData.get("regionId").toString();
			System.out.println("-------------------->" + regionId);

			if (!requestData.get("sessionId").toString().isEmpty() && requestData.get("sessionId").toString() != null)
				sessionId = new Long(requestData.get("sessionId").toString());

			if (sessionId == null) {
				jsonDataRes.put("status", "failure");
				jsonDataRes.put("code", httpstatus.getStatus());
				jsonDataRes.put("message", "failed. Invalid user ");
				return jsonDataRes;
			}

			loginUserRoleId = userService.getUserDetailsById(sessionId).getRole().getroleId();

			loginUserRegionId = userService.getUserdetailsbyId(sessionId).getRegion().getId();

			// Obtain the user list if both department id and user id are not
			// available

			if ((uId == null || uId == "") && (dId == null || dId == "") && (regionId == null || regionId == "")) {
				System.out.println("no ids");
				List<UserModel> userList = userService.getUserByRegionAndDepartment(loginUserRegionId);

				// Invoc getUserAllocationList() to findout the allocation
				// details of the user
				if (userList != null) {
					for (UserModel user : userList) {

						// Invoc getUserAllocationList() to findout the
						// allocation details of the user
						getUserAllocationList(user, date1, date2, jsonArrayFiltered);

					}
				}
			}

			// Obtain the user list only if the department id is available and
			// user id is not available or if the user id is 0.

			else if ((dId != null || dId != "") && (uId == null || uId == "" || uId.equals("0"))
					&& (regionId == null || regionId == "")) {
				Long deptId = Long.parseLong(dId);
				System.out.println("depart id");
				// Obtain the user list based on the department
				List<UserModel> userList = userService.getUserByDeptId(deptId);

				if (userList != null) {
					for (UserModel user : userList) {

						// Invoc getUserAllocationList() to findout the
						// allocation details of the user
						getUserAllocationList(user, date1, date2, jsonArrayFiltered);

					}
				}
			}

			// Obtain the user list only if the user id is available and
			// department id is not available

			else if ((uId != null && uId != "" && !uId.equals("0")) && (dId == null || dId == "")
					&& (regionId == null || regionId == "")) {

				Long userId = Long.parseLong(uId);
				UserModel user = userService.getUserDetailsById(userId);
				System.out.println("userId");
				// Invoc getUserAllocationList() to findout the allocation
				// details of the user
				if (user != null) {
					getUserAllocationList(user, date1, date2, jsonArrayFiltered);

				}

			}
			// regionId is there other ids not there
			else if ((uId == null || uId == "") && (dId == null || dId == "") && (regionId != null && regionId != "")) {

				Long regId = Long.parseLong(regionId);
				List<UserModel> userList = userService.getUserByRegion(date1, date2, regId);
				System.out.println("regionId");
				if (userList != null) {
					for (UserModel user : userList) {
						System.out.println("userName----" + user.getFirstName() + " " + user.getLastName());
						// Invoc getUserAllocationList() to findout the
						// allocation details of the user
						getUserAllocationList(user, date1, date2, jsonArrayFiltered);

					}
				}
			}
			// Obtain the user list if both department id and user id are
			// available

			else if ((uId != null && uId != "" && !uId.equals("0")) && (dId != null || dId != "")
					&& (regionId == null || regionId == "")) {
				Long deptId = Long.parseLong(dId);
				Long userId = Long.parseLong(uId);
				System.out.println("userId&deptId");
				UserModel user = userService.getUser(deptId, userId);

				// Invoc getUserAllocationList() to findout the allocation
				// details of the user
				if (user != null) {
					getUserAllocationList(user, date1, date2, jsonArrayFiltered);
				}
			}
			// only user id n regionId
			else if ((uId != null && uId != "") && (dId == null || dId == "") && (regionId != null || regionId != "")) {
				// Long deptId = Long.parseLong(dId);
				Long userId = Long.parseLong(uId);
				Long regId = Long.parseLong(regionId);
				UserModel user = userService.getUserByRegion(regId, userId);
				System.out.println("UserId n RegionId");
				// Invoc getUserAllocationList() to findout the allocation
				// details of the user
				if (user != null) {
					getUserAllocationList(user, date1, date2, jsonArrayFiltered);
				}
			}
			// 3 ids are there
			else if ((uId != null && uId != "" && !uId.equals("0")) && (dId != null || dId != "")
					&& (regionId != null && regionId != "")) {
				System.out.println("--------------------->" + dId);
				System.out.println("-------------------->6" + regionId);
				Long deptId = Long.parseLong(dId);
				Long userId = Long.parseLong(uId);
				System.out.println("Region and user only");
				Long regId = Long.parseLong(regionId);
				UserModel user = userService.getUserBydeptRegion(deptId, userId, regId);

				// Invoc getUserAllocationList() to findout the allocation
				// details of the user
				if (user != null) {
					getUserAllocationList(user, date1, date2, jsonArrayFiltered);
				}
			}

			jsonData.put("user", jsonArrayFiltered);
			jsonDataRes.put("data", jsonData);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "success. ");

		} catch (Exception e) {
			System.out.println("start failed");
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;
	}

	@PostMapping(value = "/getUsersByRegionAndLevels")
	public ObjectNode getUsernameListByRegionAndLevels(@RequestBody ObjectNode requestdata,
			HttpServletResponse httpstatus) {
		ObjectNode jsonData = objectMapper.createObjectNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		try {
			// Method invocation for getting user list
			// List<UserModel> userList = projectAllocation.getUserList();
			List<UserModel> userList = null;
			// Method invocation for getting department list
			List<DepartmentModel> departmentList = projectAllocation.getDepartmentList();
			// Method invocation for getting project list
			List<ProjectModel> projectList = null;

			ArrayNode jsonArray = objectMapper.createArrayNode();
			ArrayNode jsonProjectArray = objectMapper.createArrayNode();
			ArrayNode jsonDepartmentArray = objectMapper.createArrayNode();
			Long userId = null;
			Long roleId = null;
			Long regionId = null;

			if (requestdata.get("sessionId") != null && (!requestdata.get("sessionId").asText().trim().isEmpty())) {
				userId = requestdata.get("sessionId").asLong();
			}

			if (userId == null) {
				jsonDataRes.put("status", "failure");
				jsonDataRes.put("code", httpstatus.getStatus());
				jsonDataRes.put("message", "failed. Invalid user ");
				return jsonDataRes;
			}

			roleId = userService.getUserDetailsById(userId).getRole().getroleId();

			regionId = userService.getUserdetailsbyId(userId).getRegion().getId();

			userList = projectAllocation.getUserListByRegion(regionId);
			projectList = projectRegionService.getProjectsByRegionId(regionId);
			// Add user list to json object
			if (!(userList).isEmpty() && userList.size() > 0) {
				for (UserModel user : userList) {
					ObjectNode jsonObject = objectMapper.createObjectNode();
					jsonObject.put("userId", user.getUserId());
					jsonObject.put("firstName", user.getFirstName());
					jsonObject.put("lastName", user.getLastName());
					jsonObject.put("role", user.getRole().getroleId());
					jsonObject.put("region_id", user.getRegion().getId());
					DepartmentModel departmentModel = user.getDepartment();
					ObjectNode depNode = objectMapper.createObjectNode();
					depNode.put("departmentId", departmentModel.getDepartmentId());
					depNode.put("departmentName", departmentModel.getdepartmentName());

					jsonObject.set("department", depNode);

					LocalDate now = LocalDate.now();
					int quarter = 0;
					int monthNumber = now.getMonthValue();
					int year = now.getYear();

					if (monthNumber >= 1 && monthNumber <= 3)
						quarter = 1;
					else if (monthNumber >= 4 && monthNumber <= 6)
						quarter = 2;
					else if (monthNumber >= 7 && monthNumber <= 9)
						quarter = 3;
					else if (monthNumber >= 10 && monthNumber <= 12)
						quarter = 4;
					ObjectNode leaveBalanceNode = attendanceService.getLeavebalanceData(user.getUserId(), quarter,
							year);
					jsonObject.set("leaveBalance", leaveBalanceNode);
					jsonArray.add(jsonObject);
				}
				jsonData.set("userList", jsonArray);

			}

			// Add project list to json object
			if (!(projectList).isEmpty() && projectList.size() > 0) {
				for (ProjectModel project : projectList) {
					ObjectNode jsonObject = objectMapper.createObjectNode();
					jsonObject.put("projectId", project.getProjectId());
					jsonObject.put("projectName", project.getProjectName());
					jsonProjectArray.add(jsonObject);
				}
				jsonData.set("projectList", jsonProjectArray);
			}

			// Add department list to json object
			if (!(departmentList).isEmpty() && departmentList.size() > 0) {
				for (DepartmentModel department : departmentList) {
					ObjectNode jsonObject = objectMapper.createObjectNode();
					jsonObject.put("departmentId", department.getDepartmentId());
					jsonObject.put("departmentName", department.getdepartmentName());
					jsonDepartmentArray.add(jsonObject);
				}
				jsonData.set("departmentList", jsonDepartmentArray);
			}

			jsonDataRes.set("data", jsonData);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "success");
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;
	}

	@PostMapping(value = "/getPreResourceDataByRegionAndLevels")
	public ObjectNode getUsernameByUserRoleList(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus) {

		ObjectNode jsonData = objectMapper.createObjectNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		Long userId = null;
		Long roleId = null;
		Long regionId = null;
		Long regionId_selected = null;
		boolean isLevels = true;
		ArrayNode jsonProjectArray = objectMapper.createArrayNode();

		if (requestdata.get("regionId") != null && (!requestdata.get("regionId").asText().trim().isEmpty())) {
			regionId_selected = requestdata.get("regionId").asLong();
		}

		if (requestdata.get("sessionId") != null && (!requestdata.get("sessionId").asText().trim().isEmpty())) {
			userId = requestdata.get("sessionId").asLong();
		}

		if (userId == null) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. Invalid user ");
			return jsonDataRes;
		}
		List<ProjectModel> projectList = new ArrayList<ProjectModel>();
		try {

			roleId = userService.getUserDetailsById(userId).getRole().getroleId();

			regionId = userService.getUserdetailsbyId(userId).getRegion().getId();

			if ((roleId == 1) || (roleId == 10)) {
				if (regionId_selected != null) {
					projectList = projectRegionService.getProjectsByRegionId(regionId_selected);
				} else {
					projectList = projectService.getAllActiveProjectList();
				}
				isLevels = false;
			}

			if (roleId == 4 | roleId == 5 | roleId == 6 | roleId == 9) {
				projectList = projectRegionService.getProjectsByRegionId(regionId);
				isLevels = false;
			}

			if ((projectList).isEmpty() && projectList.size() == 0 && !isLevels) {

				jsonDataRes.put("status", "no data");
				jsonDataRes.put("code", httpstatus.SC_NO_CONTENT);
				jsonDataRes.put("message", "no data found !");
				return jsonDataRes;
			}

			if (!(projectList).isEmpty() && projectList.size() > 0 && !isLevels) {
				for (ProjectModel project : projectList) {
					ObjectNode jsonObject = objectMapper.createObjectNode();
					jsonObject.put("projectId", project.getProjectId());
					jsonObject.put("projectName", project.getProjectName());
					jsonProjectArray.add(jsonObject);
				}
				jsonData.set("projectList", jsonProjectArray);
				jsonDataRes.set("data", jsonData);
				jsonDataRes.put("status", "success");
				jsonDataRes.put("code", httpstatus.getStatus());
				jsonDataRes.put("message", "success");
				return jsonDataRes;
			}

			// Level 1
			projectList = projectService.getProjectListByLevel1(userId);
			// Level 2
			projectList.addAll(projectService.getProjectListByLevel2(userId));

			// Remove duplicates if any

			projectList = projectList.stream().distinct().collect(Collectors.toList());

			if ((projectList).isEmpty() && projectList.size() == 0 && isLevels) {
				jsonDataRes.put("status", "no data");
				jsonDataRes.put("code", httpstatus.SC_NO_CONTENT);
				jsonDataRes.put("message", "no data found !");
				return jsonDataRes;
			}

			// Add project list to json object
			if (!(projectList).isEmpty() && projectList.size() > 0 && isLevels) {
				for (ProjectModel project : projectList) {
					ObjectNode jsonObject = objectMapper.createObjectNode();
					jsonObject.put("projectId", project.getProjectId());
					jsonObject.put("projectName", project.getProjectName());
					jsonProjectArray.add(jsonObject);
				}
				jsonData.set("projectList", jsonProjectArray);
			}

			jsonDataRes.set("data", jsonData);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "success");
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;
	}

	@PostMapping(value = "/getUsersByProjectId")
	public ObjectNode getUsersByProjectId(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus) {
		ObjectNode jsonData = objectMapper.createObjectNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		Long projectId = null;
		List<UserModel> userList = new ArrayList<UserModel>();
		ArrayNode jsonArray = objectMapper.createArrayNode();

		if (requestdata.get("projectId") != null && requestdata.get("projectId").asText() != "") {
			projectId = requestdata.get("projectId").asLong();
		}

		if (projectId == null) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. Invalid Project ");
			return jsonDataRes;
		}
		userList = projectAllocation.getUsersByProjectId(projectId);
		try {
			// Add user list to json object
			if (!(userList).isEmpty() && userList.size() > 0) {

				for (UserModel user : userList) {
					ObjectNode jsonObject = objectMapper.createObjectNode();
					jsonObject.put("userId", user.getUserId());
					jsonObject.put("firstName", user.getFirstName());
					jsonObject.put("lastName", user.getLastName());
					jsonObject.put("role", user.getRole().getroleId());
					DepartmentModel departmentModel = user.getDepartment();
					ObjectNode depNode = objectMapper.createObjectNode();
					depNode.put("departmentId", departmentModel.getDepartmentId());
					depNode.put("departmentName", departmentModel.getdepartmentName());

					jsonObject.set("department", depNode);

					LocalDate now = LocalDate.now();
					int quarter = 0;
					int monthNumber = now.getMonthValue();
					int year = now.getYear();

					if (monthNumber >= 1 && monthNumber <= 3)
						quarter = 1;
					else if (monthNumber >= 4 && monthNumber <= 6)
						quarter = 2;
					else if (monthNumber >= 7 && monthNumber <= 9)
						quarter = 3;
					else if (monthNumber >= 10 && monthNumber <= 12)
						quarter = 4;
					ObjectNode leaveBalanceNode = attendanceService.getLeavebalanceData(user.getUserId(), quarter,
							year);
					jsonObject.set("leaveBalance", leaveBalanceNode);
					jsonArray.add(jsonObject);
				}
				jsonData.set("userList", jsonArray);

			}

			jsonDataRes.set("data", jsonData);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "success");

		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;
	}

	// Renjith

	// To update resource allocation data

	@PutMapping(value = "/editAllocation")
	public ObjectNode updateData(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus) {

		ObjectNode jsonDataRes = objectMapper.createObjectNode();

		try {
			Long id = requestdata.get("id").asLong();
			Double allocatedVal = requestdata.get("allocatedPerce").asDouble();
			Boolean isBillable = requestdata.get("isBillable").asBoolean();
			Boolean isActive = true;// requestdata.get("active").asBoolean();

			// Method invocation for getting allocation details
			AllocationModel allocationModel = projectAllocation.findDataById(id);
			if (allocationModel != null) {
				allocationModel.setAllocatedPerce(allocatedVal);
				allocationModel.setIsBillable(isBillable);
				allocationModel.setActive(isActive);

				// Updating allcation details
				projectAllocation.updateData(allocationModel);
				jsonDataRes.put("status", "success");
				jsonDataRes.put("code", httpstatus.getStatus());
				jsonDataRes.put("message", "updated successfully");
			}
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "updation failed. " + e);
		}
		return jsonDataRes;

	}

	// To get the allocation list

	// @GetMapping(value = "/getResourceListBasedonProject/{projectId}")
	// public ObjectNode
	// getAllocationListsBasedonProject(@PathVariable("projectId") Long
	// projectId,HttpServletResponse httpstatus) {
	@PostMapping(value = "/getResourceListBasedonProject")
	public ObjectNode getAllocationListsBasedonProject(@RequestBody ObjectNode requestData,
			HttpServletResponse httpstatus) {

		// Method invocation for getting allocation list based on the project
		Long projectId = requestData.get("projectId").asLong();
		String startDate = requestData.get("startDate").asText();
		String endDate = requestData.get("endDate").asText();
		Date fromDate = null, toDate = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		List<AllocationModel> allocationModel = projectAllocation.getAllocationList(projectId);

		ArrayNode jsonArray = objectMapper.createArrayNode();
		ObjectNode jsonData = objectMapper.createObjectNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		try {
			fromDate = df.parse(startDate);
			toDate = df.parse(endDate);
			if (!(allocationModel.isEmpty() && allocationModel.size() > 0)) {
				for (AllocationModel item : allocationModel) {
					String projectStartDate = df.format(item.getStartDate());
					String projectEndDate = df.format(item.getEndDate());
					if ((fromDate.compareTo(df.parse(projectStartDate)) >= 0
							&& fromDate.compareTo(df.parse(projectStartDate)) <= 0)
							|| (toDate.compareTo(df.parse(projectStartDate)) >= 0
									&& toDate.compareTo(df.parse(projectEndDate)) <= 0)
							|| (fromDate.compareTo(df.parse(projectStartDate)) >= 0
									&& toDate.compareTo(df.parse(projectEndDate)) <= 0)) {

						ObjectNode jsonObject = objectMapper.createObjectNode();
						jsonObject.put("allocationId", item.getAllocId());
						if (item.getproject() != null) {
							jsonObject.put("projectTitle", item.getproject().getProjectName());
							jsonObject.put("projectCategory", item.getproject().getProjectCategory());
						}
						if (item.getuser() != null) {
							jsonObject.put("userId", item.getuser().getUserId());
							jsonObject.put("firstName", item.getuser().getFirstName());
							jsonObject.put("lastName", item.getuser().getLastName());
							jsonObject.put("role", item.getuser().getRole().getroleId());
						}
						jsonObject.put("allocatedVal", item.getAllocatedPerce());
						jsonObject.put("isBillable", item.getIsBillable());

						if (item.getuser() != null && item.getuser().getDepartment() != null)
							jsonObject.put("departmentName", item.getuser().getDepartment().getdepartmentName());
						jsonArray.add(jsonObject);
					}
				}
				jsonData.set("resourceList", jsonArray);
			}
			jsonDataRes.put("status", "success");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "success ");
			jsonDataRes.set("data", jsonData);

		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;

	}

	// saving allocation details

	@PostMapping("/saveAllocation")
	public ObjectNode saveAllocationDetails(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus) {

		AllocationModel allocationModel = new AllocationModel();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();

		try {
			// Obtain the data from request data
			String date1 = requestdata.get("startDate").asText();
			String date2 = requestdata.get("endDate").asText();
			TimeZone zone = TimeZone.getTimeZone("MST");
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			outputFormat.setTimeZone(zone);

			// Formating the date values
			Date startDate = null, endDate = null;
			if (!date1.isEmpty()) {
				startDate = outputFormat.parse(date1);
			}
			if (!date2.isEmpty()) {
				endDate = outputFormat.parse(date2);
			}
			// Setting values to Allocation model object
			Double val = requestdata.get("allocatedPerce").asDouble();
			Long projectId = requestdata.get("projectId").asLong();
			Long userId = requestdata.get("userId").asLong();
			Boolean isBillable = requestdata.get("isBillable").asBoolean();

			ProjectModel project = projectService.findById(projectId);
			UserModel user = userService.getUserDetailsById(userId);

			allocationModel.setproject(project);
			allocationModel.setuser(user);
			allocationModel.setStartDate(startDate);
			allocationModel.setEndDate(endDate);
			allocationModel.setAllocatedPerce(val);
			allocationModel.setIsBillable(isBillable);

			// if the endate is just after the end date of previously added one
			System.out.println("Here---------------------->");
			// List<Object[]> allocationModelnew =
			// projectAllocation.getAllocationContinousDateRange(projectId,userId,startDate,endDate);
			// Object[] allocationModelnew =
			// projectAllocation.getAllocationContinousDateRange(projectId,userId,startDate,endDate);

			BigInteger allocationID = projectAllocation.getAllocationContinousDateRange(projectId, userId, startDate,
					endDate);
			System.out.println("Here---------------------->1" + allocationID);
			Long prim_id = null;
			// long alloc_id = 721;

			if (allocationID != null) {
				if (allocationID.compareTo(BigInteger.ZERO) > 0) {
					prim_id = allocationID.longValue();

					// System.out.println("-------------------->2");
					AllocationModel allocationmodelupdate = projectAllocation.findById(prim_id);
					long difference = startDate.getTime() - allocationmodelupdate.getEndDate().getTime();
					long diff = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
					// System.out.println("-------------------->3"+diff);
					if (diff == 1) {

						allocationmodelupdate.setEndDate(endDate);
						allocationmodelupdate.setAllocatedPerce(val);
						projectAllocation.save(allocationmodelupdate);

					} else {
						projectAllocation.save(allocationModel);
					}

				}
			} else {
				projectAllocation.save(allocationModel);
			}

			// Check whether the user is already allocated to the project.If so
			// update the previous entry of the user otherwise new entry is
			// created.
			/*
			 * Long allocId = projectAllocation.getAllocId(projectId,userId); if(allocId !=
			 * null) { AllocationModel oldAlloc = projectAllocation.findDataById(allocId);
			 * if(oldAlloc != null) {
			 * oldAlloc.setAllocatedPerce(allocationModel.getAllocatedPerce());
			 * oldAlloc.setStartDate(allocationModel.getStartDate());
			 * oldAlloc.setEndDate(allocationModel.getEndDate());
			 * oldAlloc.setIsBillable(allocationModel.getIsBillable());
			 * projectAllocation.updateData(oldAlloc); }
			 * 
			 * } else {
			 */

			// }

			jsonDataRes.put("status", "success");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "successfully saved. ");
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;
	}

	@PostMapping("/getUserData")
	public JSONObject getAllocationList(@RequestBody JSONObject requestData, HttpServletResponse httpstatus) {

		JSONObject jsonData = new JSONObject();
		JSONObject jsonDataRes = new JSONObject();
		List<JSONObject> jsonArrayFiltered = new ArrayList<>();
		TimeZone zone = TimeZone.getTimeZone("MST");
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		outputFormat.setTimeZone(zone);
		java.util.Date date1 = null, date2 = null;
		String uId = null, dId = null, startDate = null, endDate = null;
		String regionId = null;

		try {

			if (!requestData.get("startDate").toString().isEmpty() && requestData.get("startDate").toString() != null) {
				startDate = requestData.get("startDate").toString();
				if (!startDate.isEmpty()) {
					date1 = outputFormat.parse(startDate);

				}
			}
			if (!requestData.get("endDate").toString().isEmpty() && requestData.get("endDate").toString() != null) {
				endDate = requestData.get("endDate").toString();
				if (!endDate.isEmpty()) {
					date2 = outputFormat.parse(endDate);

				}
			}

			if (!requestData.get("userId").toString().isEmpty() && requestData.get("userId").toString() != null)
				uId = requestData.get("userId").toString();
			if (!requestData.get("deptId").toString().isEmpty() && requestData.get("deptId").toString() != null)
				dId = requestData.get("deptId").toString();

			if (!requestData.get("regionId").toString().isEmpty() && requestData.get("regionId").toString() != null)
				regionId = requestData.get("regionId").toString();
			System.out.println("-------------------->" + regionId);

			// Obtain the user list if both department id and user id are not
			// available

			if ((uId == null || uId == "") && (dId == null || dId == "") && (regionId == null || regionId == "")) {
				System.out.println("no ids");
				List<UserModel> userList = userService.getAllUsers();

				// Invoc getUserAllocationList() to findout the allocation
				// details of the user
				if (userList != null) {
					for (UserModel user : userList) {

						// Invoc getUserAllocationList() to findout the
						// allocation details of the user
						getUserAllocationList(user, date1, date2, jsonArrayFiltered);

					}
				}
			}

			// Obtain the user list only if the department id is available and
			// user id is not available or if the user id is 0.

			else if ((dId != null || dId != "") && (uId == null || uId == "" || uId.equals("0"))
					&& (regionId == null || regionId == "")) {
				Long deptId = Long.parseLong(dId);
				System.out.println("depart id");
				// Obtain the user list based on the department
				List<UserModel> userList = userService.getUserByDeptId(deptId);

				if (userList != null) {
					for (UserModel user : userList) {

						// Invoc getUserAllocationList() to findout the
						// allocation details of the user
						getUserAllocationList(user, date1, date2, jsonArrayFiltered);

					}
				}
			}

			// Obtain the user list only if the user id is available and
			// department id is not available

			else if ((uId != null && uId != "" && !uId.equals("0")) && (dId == null || dId == "")
					&& (regionId == null || regionId == "")) {

				Long userId = Long.parseLong(uId);
				UserModel user = userService.getUserDetailsById(userId);
				System.out.println("userId");
				// Invoc getUserAllocationList() to findout the allocation
				// details of the user
				if (user != null) {
					getUserAllocationList(user, date1, date2, jsonArrayFiltered);

				}

			}
			// regionId is there other ids not there
			else if ((uId == null || uId == "") && (dId == null || dId == "") && (regionId != null && regionId != "")) {

				Long regId = Long.parseLong(regionId);
				List<UserModel> userList = userService.getUserByRegion(regId);
				System.out.println("regionId");
				if (userList != null) {
					for (UserModel user : userList) {

						// Invoc getUserAllocationList() to findout the
						// allocation details of the user
						getUserAllocationList(user, date1, date2, jsonArrayFiltered);

					}
				}
			}
			// Obtain the user list if both department id and user id are
			// available

			else if ((uId != null && uId != "" && !uId.equals("0")) && (dId != null || dId != "")
					&& (regionId == null || regionId == "")) {
				Long deptId = Long.parseLong(dId);
				Long userId = Long.parseLong(uId);
				System.out.println("userId&deptId");
				UserModel user = userService.getUser(deptId, userId);

				// Invoc getUserAllocationList() to findout the allocation
				// details of the user
				if (user != null) {
					getUserAllocationList(user, date1, date2, jsonArrayFiltered);
				}
			}
			// only user id n regionId
			else if ((uId != null && uId != "") && (dId == null || dId == "") && (regionId != null || regionId != "")) {
				// Long deptId = Long.parseLong(dId);
				Long userId = Long.parseLong(uId);
				Long regId = Long.parseLong(regionId);
				UserModel user = userService.getUserByRegion(regId, userId);
				System.out.println("UserId n RegionId");
				// Invoc getUserAllocationList() to findout the allocation
				// details of the user
				if (user != null) {
					getUserAllocationList(user, date1, date2, jsonArrayFiltered);
				}
			}
			// 3 ids are there
			else if ((uId != null && uId != "" && !uId.equals("0")) && (dId != null || dId != "")
					&& (regionId != null && regionId != "")) {
				System.out.println("--------------------->" + dId);
				System.out.println("-------------------->6" + regionId);
				Long deptId = Long.parseLong(dId);
				Long userId = Long.parseLong(uId);
				System.out.println("Region and user only");
				Long regId = Long.parseLong(regionId);
				UserModel user = userService.getUserBydeptRegion(deptId, userId, regId);

				// Invoc getUserAllocationList() to findout the allocation
				// details of the user
				if (user != null) {
					getUserAllocationList(user, date1, date2, jsonArrayFiltered);
				}
			}

			jsonData.put("user", jsonArrayFiltered);
			jsonDataRes.put("data", jsonData);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "success. ");

		} catch (Exception e) {
			System.out.println("start failed");
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;
	}

	private void getUserAllocationList(UserModel user, Date date1, Date date2, List<JSONObject> jsonArrayFiltered) {

		// Checks whether the user has an entry on allocation table
		Boolean isExist = projectAllocation.checkIsExist(user.getUserId());
		if (isExist) {

			// find out the allocation details based on the date passed as an
			// argument
			List<AllocationModel> newUserList = projectAllocation.getUsersList(user.getUserId(), date1, date2);

			// Add user and project alocation details to the json object
			if (newUserList != null && newUserList.size() > 0) {
				JSONObject jsonObject = new JSONObject();
				int freeAlloc = 100;
				int totAlloc = 0;
				List<JSONObject> jsonArray = new ArrayList<>();
				jsonObject.put("userId", user.getUserId());
				jsonObject.put("firstName", user.getFirstName());
				jsonObject.put("lastName", user.getLastName());
				jsonObject.put("role", user.getRole().getroleId());
				jsonObject.put("department", user.getDepartment());
				jsonObject.put("region_id", user.getRegion().getId());
				for (AllocationModel item : newUserList) {
					JSONObject jsonObjectData = new JSONObject();
					jsonObjectData.put("allocationId", item.getAllocId());
					jsonObjectData.put("projectId", item.getproject().getProjectId());
					jsonObjectData.put("projectName", item.getproject().getProjectName());
					jsonObjectData.put("projectCategory", item.getproject().getProjectCategory());
					jsonObjectData.put("allocationPercentage", item.getAllocatedPerce());
					jsonObjectData.put("allocationStartDate", item.getStartDate().toString());
					jsonObjectData.put("allocationEndDate", item.getEndDate().toString());
					jsonObjectData.put("isBillable", item.getIsBillable());
					totAlloc += item.getAllocatedPerce();

					if (freeAlloc > 0)
						freeAlloc -= item.getAllocatedPerce();

					jsonArray.add(jsonObjectData);

				}
				jsonObject.put("totalAllocation", totAlloc);
				jsonObject.put("freeAlloc", freeAlloc);
				jsonObject.put("project", jsonArray);
				jsonArrayFiltered.add(jsonObject);
			} else {
				JSONObject jsonObject = new JSONObject();
				List<JSONObject> jsonArray = new ArrayList<>();
				jsonObject.put("userId", user.getUserId());
				jsonObject.put("firstName", user.getFirstName());
				jsonObject.put("lastName", user.getLastName());
				jsonObject.put("role", user.getRole().getroleId());
				jsonObject.put("department", user.getDepartment());
				jsonObject.put("project", jsonArray);
				jsonObject.put("freeAlloc", 100);
				jsonObject.put("totalAllocation", 0);
				jsonArrayFiltered.add(jsonObject);
			}

		}

		// If the user has no entry, then add user detais without project
		// allocation details to the json object
		else {
			JSONObject jsonObject = new JSONObject();
			List<JSONObject> jsonArray = new ArrayList<>();
			jsonObject.put("userId", user.getUserId());
			jsonObject.put("firstName", user.getFirstName());
			jsonObject.put("lastName", user.getLastName());
			jsonObject.put("role", user.getRole().getroleId());
			jsonObject.put("department", user.getDepartment());
			jsonObject.put("project", jsonArray);
			jsonObject.put("freeAlloc", 100);
			jsonObject.put("totalAllocation", 0);
			jsonArrayFiltered.add(jsonObject);
		}

	}

	@PutMapping(value = "/editAllocationstatus")
	public ObjectNode editAllocationstatus(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus) {

		ObjectNode jsonDataRes = objectMapper.createObjectNode();

		try {
			Long id = requestdata.get("id").asLong();
			Boolean isActive = requestdata.get("isActive").asBoolean();

			// Method invocation for getting allocation details
			AllocationModel allocationModel = projectAllocation.findDataById(id);
			if (allocationModel != null) {
				allocationModel.setActive(isActive);

				// Updating allcation details
				projectAllocation.updateData(allocationModel);
				jsonDataRes.put("status", "success");
				jsonDataRes.put("code", httpstatus.getStatus());
				jsonDataRes.put("message", "updated successfully");
			}
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "updation failed. " + e);
		}
		return jsonDataRes;

	}

	@DeleteMapping("/deleteAllocationById")
	public JsonNode deleteTaskById(@RequestParam("allocationId") Long id) {
		ObjectNode node = objectMapper.createObjectNode();

		if (projectAllocation.remove(id)) {
			node.put("status", "success");
		} else {
			node.put("status", "failure");
		}

		return node;
	}

	@PutMapping(value = "/v2/editAllocation")
	public ObjectNode updateDataV2(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus) {

		ObjectNode jsonDataRes = objectMapper.createObjectNode();

		try {
			Long id = requestdata.get("id").asLong();
			String date1 = requestdata.get("startDate").asText();
			String date2 = requestdata.get("endDate").asText();
			TimeZone zone = TimeZone.getTimeZone("MST");
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			outputFormat.setTimeZone(zone);

			// Formating the date values
			Date startDate = null, endDate = null;
			if (!date1.isEmpty()) {
				startDate = outputFormat.parse(date1);
			}
			if (!date2.isEmpty()) {
				endDate = outputFormat.parse(date2);
			}
			Double allocatedVal = requestdata.get("allocatedPerce").asDouble();
			Boolean isBillable = requestdata.get("isBillable").asBoolean();
			Boolean isActive = true;// requestdata.get("active").asBoolean();

			// Method invocation for getting allocation details
			AllocationModel allocationModel = projectAllocation.findDataById(id);
			if (allocationModel != null) {
				allocationModel.setStartDate(startDate);
				allocationModel.setEndDate(endDate);
				allocationModel.setAllocatedPerce(allocatedVal);
				allocationModel.setIsBillable(isBillable);
				allocationModel.setActive(isActive);

				// Updating allcation details
				projectAllocation.updateData(allocationModel);
				jsonDataRes.put("status", "success");
				jsonDataRes.put("code", httpstatus.getStatus());
				jsonDataRes.put("message", "updated successfully");
			}
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "updation failed. " + e);
		}
		return jsonDataRes;

	}

	@PostMapping(value = "/v2/getResourceListBasedonProject")
	public ObjectNode getAllocationListsBasedonProjectV2(@RequestBody ObjectNode requestData,
			HttpServletResponse httpstatus) {

		// Method invocation for getting allocation list based on the project
		Long projectId = requestData.get("projectId").asLong();
		String startDate = requestData.get("startDate").asText();
		String endDate = requestData.get("endDate").asText();
		Date fromDate = null, toDate = null;
		String projectStart = null;
		String projectEnd = null;
		String projectName = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<AllocationModel> allocationModel = projectAllocation.getAllocationList(projectId);

		ArrayNode jsonArray = objectMapper.createArrayNode();
		ArrayNode projectjsonArray = objectMapper.createArrayNode();
		ArrayNode regionjsonArray = objectMapper.createArrayNode();
		ObjectNode jsonData = objectMapper.createObjectNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		ObjectNode jsonprojectObject = objectMapper.createObjectNode();
		try {
			fromDate = df.parse(startDate);
			toDate = df.parse(endDate);
			if ((!allocationModel.isEmpty() && allocationModel.size() > 0)) {

				for (AllocationModel item : allocationModel) {
					if (item != null) {
						String projectStartDate = df.format(item.getStartDate());
						String projectEndDate = df.format(item.getEndDate());
						/*
						 * if ((fromDate.compareTo(df.parse(projectStartDate)) >= 0 &&
						 * fromDate.compareTo(df.parse(projectStartDate)) <= 0) ||
						 * (toDate.compareTo(df.parse(projectStartDate)) >= 0 &&
						 * toDate.compareTo(df.parse(projectEndDate)) <= 0) ||
						 * (fromDate.compareTo(df.parse(projectStartDate)) >= 0 &&
						 * toDate.compareTo(df.parse(projectEndDate)) <= 0)) {
						 */
						// if(fromDate.compareTo(df.parse(projectEndDate)) <= 0 &&
						// (df.parse(projectEndDate)).compareTo(toDate) <= 0){
						if ((df.parse(projectStartDate)).compareTo(toDate) <= 0
								&& (df.parse(projectEndDate)).compareTo(fromDate) >= 0) {

							ObjectNode jsonObject = objectMapper.createObjectNode();

							jsonObject.put("allocationId", item.getAllocId());
							if (item.getproject() != null) {
								jsonObject.put("projectTitle", item.getproject().getProjectName());
								jsonObject.put("projectCategory", item.getproject().getProjectCategory());

							}
							double availableAlloc = 0;
							if (item.getuser() != null) {
								availableAlloc = projectAllocation.getAvailableAlloc(projectId,
										item.getuser().getUserId());
								jsonObject.put("userId", item.getuser().getUserId());

								jsonObject.put("firstName", item.getuser().getFirstName());
								jsonObject.put("lastName", item.getuser().getLastName());
								jsonObject.put("role", item.getuser().getRole().getroleId());
								jsonObject.put("userRegion", item.getuser().getRegion().getId());
							}

							jsonObject.put("startDate", projectStartDate);
							jsonObject.put("endDate", projectEndDate);
							jsonObject.put("allocatedVal", item.getAllocatedPerce());
							jsonObject.put("totalFreeAlloc", (availableAlloc + item.getAllocatedPerce()));
							jsonObject.put("isBillable", item.getIsBillable());

							if (item.getuser() != null && item.getuser().getDepartment() != null)
								jsonObject.put("departmentName", item.getuser().getDepartment().getdepartmentName());

							jsonArray.add(jsonObject);
						}
					}
				}

			}
			// nisha
			ProjectModel project = projectService.getProjectDetails(projectId);
			projectStart = df.format(project.getStartDate());
			projectEnd = df.format(project.getEndDate());
			projectName = project.getProjectName();
			jsonprojectObject.put("projectId", projectId);
			jsonprojectObject.put("projectName", projectName);
			jsonprojectObject.put("projectStartDate", projectStart);
			jsonprojectObject.put("projectEndDate", projectEnd);

			jsonData.set("projectData", jsonprojectObject);
			jsonData.set("resourceList", jsonArray);
			List<Region> projectRegion = regionservice.getlist();

			for (Region region : projectRegion) {

				ObjectNode jsonregionObject = objectMapper.createObjectNode();
				jsonregionObject.put("region_Id", region.getId());
				jsonregionObject.put("region_code", region.getRegion_code());
				jsonregionObject.put("region_name", region.getRegion_name());
				regionjsonArray.add(jsonregionObject);
			}
			jsonData.set("regionList", regionjsonArray);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "success ");
			jsonDataRes.set("data", jsonData);

		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;

	}

	@PostMapping("/getFreeAllocation")
	public JsonNode getFreeAllocation(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) {

		ObjectNode responsedata = objectMapper.createObjectNode();

		Long userId = requestdata.get("userId").asLong();
		String startDate = requestdata.get("startDate").asText();
		String endDate = requestdata.get("endDate").asText();
		Date fromDate = null, toDate = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			fromDate = df.parse(startDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			toDate = df.parse(endDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Object[] freeAlloc = projectAllocation.getFreeAlloc(userId, fromDate, toDate);
			responsedata.put("status", "success");
			responsedata.put("code", httpstatus.getStatus());
			responsedata.put("message", "success ");
			responsedata.put("totalFreeAlloc", (double) freeAlloc[0]);
		} catch (Exception e) {
			// TODO: handle exception
			responsedata.put("status", "failure");
			responsedata.put("code", httpstatus.getStatus());
			responsedata.put("message", "failed. " + e);
		}

		return responsedata;
	}

	@PostMapping(value = "/getProjectNamesBasedOnMonthAndYear")
	public ObjectNode getProjectsBasedOnMonthandYear(@RequestBody ObjectNode requestData,
			HttpServletResponse httpStatus) {
		ObjectNode node = objectMapper.createObjectNode();
		ArrayNode jsonProjectArray = objectMapper.createArrayNode();
		Long regionId = null;
		Long regionIdSelected = null;
		int month = 0;
		int year = 0;
		List<ProjectModel> projectList = new ArrayList<>();
		Set<ProjectModel> allProject = new HashSet<>();
		ArrayList<ProjectModel> newList = new ArrayList<ProjectModel>();
		ArrayNode range = (ArrayNode) requestData.get("range");
		if (requestData.get("regionId") != null && (!requestData.get("regionId").asText().trim().isEmpty())) {
			regionId = requestData.get("regionId").asLong();
		}
		for (JsonNode rangeNode : range) {
			projectList = new ArrayList<ProjectModel>();
			month = Integer.parseInt(rangeNode.get("month").toString());
			year = Integer.parseInt(rangeNode.get("year").toString());
			if (month != 0 && year != 0 && regionId != null) {

				projectList = projectservice.getProjectsBasedOnMonthYearRegion(regionId, month, year);

			} else if ((month != 0 && year != 0 && regionId == null)) {
				projectList = projectservice.getProjectsBasedOnMonthYearRegion(month, year);
			}

			allProject.addAll(projectList);

		}

		if (!(allProject).isEmpty() && allProject.size() > 0) {
			for (ProjectModel project : allProject) {
				ObjectNode jsonObject = objectMapper.createObjectNode();
				jsonObject.put("projectId", project.getProjectId());
				jsonObject.put("projectName", project.getProjectName());
				jsonProjectArray.add(jsonObject);
			}
			node.set("projectList", jsonProjectArray);
			// node.set("data", jsonData);
			node.put("status", "success");
			node.put("code", httpStatus.getStatus());
			node.put("message", "success");

		} else {
			node.set("projectList", null);
			// node.set("data", jsonData);
			node.put("status", "failed");
			node.put("code", httpStatus.getStatus());
			node.put("message", "failed");
		}

		return node;
	}

	@PostMapping(value = "/getUserNamesBasedOnMonthAndYear")
	public ObjectNode getUserNamesBasedOnMonthAndYear(@RequestBody ObjectNode requestData,
			HttpServletResponse httpStatus) {
		ObjectNode node = objectMapper.createObjectNode();
		ArrayNode jsonProjectArray = objectMapper.createArrayNode();
		Long regionId = null;
		Long regionIdSelected = null;
		int month = 0;
		int year = 0;
		List<UserModel> userList = new ArrayList<>();
		Set<UserModel> allUser = new HashSet<>();
		ArrayList<UserModel> newList = new ArrayList<UserModel>();
		ArrayNode range = (ArrayNode) requestData.get("range");
		if (requestData.get("regionId") != null && (!requestData.get("regionId").asText().trim().isEmpty())) {
			regionId = requestData.get("regionId").asLong();
		}
		for (JsonNode rangeNode : range) {
			userList = new ArrayList<UserModel>();
			month = Integer.parseInt(rangeNode.get("month").toString());
			year = Integer.parseInt(rangeNode.get("year").toString());
			if (month != 0 && year != 0 && regionId != null) {

				userList = userService.getUsesrsBasedOnMonthYearRegion(regionId, month, year);

			} else if ((month != 0 && year != 0 && regionId == null)) {
				userList = userService.getUsesrsBasedOnMonthYearRegion(month, year);
			}

			allUser.addAll(userList);

		}

		if (!(allUser).isEmpty() && allUser.size() > 0) {
			for (UserModel user : allUser) {
				ObjectNode jsonObject = objectMapper.createObjectNode();
				jsonObject.put("userId", user.getUserId());
				jsonObject.put("userName", user.getLastName() + " " + user.getFirstName());
				jsonProjectArray.add(jsonObject);
			}
			node.set("userList", jsonProjectArray);
			// node.set("data", jsonData);
			node.put("status", "success");
			node.put("code", httpStatus.getStatus());
			node.put("message", "success");

		} else {
			node.set("userList", null);
			// node.set("data", jsonData);
			node.put("status", "failed");
			node.put("code", httpStatus.getStatus());
			node.put("message", "failed");
		}

		return node;
	}

	@PostMapping(value = "/getProjectNamesBasedOnMonthYearAndUser")
	public ObjectNode getProjectNamesBasedOnMonthAndYearAndUser(@RequestBody ObjectNode requestData,
			HttpServletResponse httpStatus) {
		ObjectNode node = objectMapper.createObjectNode();
		ArrayNode jsonProjectArray = objectMapper.createArrayNode();
		Long regionId = null;
		Long userId = null;
		Long regionIdSelected = null;
		int month = 0;
		int year = 0;
		List<ProjectModel> userList = new ArrayList<>();
		Set<ProjectModel> allUser = new HashSet<>();
		ArrayList<ProjectModel> newList = new ArrayList<ProjectModel>();
		ArrayNode range = (ArrayNode) requestData.get("range");
		if (requestData.get("regionId") != null && (!requestData.get("regionId").asText().trim().isEmpty())) {
			regionId = requestData.get("regionId").asLong();
		}
		if (requestData.get("userId") != null && (!requestData.get("userId").asText().trim().isEmpty())) {
			userId = requestData.get("userId").asLong();
		}
		for (JsonNode rangeNode : range) {
			userList = new ArrayList<ProjectModel>();
			month = Integer.parseInt(rangeNode.get("month").toString());
			year = Integer.parseInt(rangeNode.get("year").toString());
			if (month != 0 && year != 0 && regionId != null) {

				userList = userService.getProjectNamesBasedOnMonthAndYearAndUser(regionId, month, year, userId);

			} else if ((month != 0 && year != 0 && regionId == null)) {
				userList = userService.getProjectNamesBasedOnMonthAndYearAndUser(month, year, userId);
			}

			allUser.addAll(userList);

		}

		if (!(allUser).isEmpty() && allUser.size() > 0) {
			for (ProjectModel user : allUser) {
				ObjectNode jsonObject = objectMapper.createObjectNode();
				jsonObject.put("projectId", user.getProjectId());
				jsonObject.put("projectName", user.getProjectName());
				jsonProjectArray.add(jsonObject);
			}
			node.set("projectList", jsonProjectArray);
			// node.set("data", jsonData);
			node.put("status", "success");
			node.put("code", httpStatus.getStatus());
			node.put("message", "success");

		} else {
			node.set("userList", null);
			// node.set("data", jsonData);
			node.put("status", "success");
			node.put("code", httpStatus.getStatus());
			node.put("message", "failed");
		}

		return node;
	}

	@PostMapping("/v2/saveAllocation")
	public ObjectNode saveAllocations(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus)
			throws ParseException {
		ObjectNode response = objectMapper.createObjectNode();
		ArrayNode arrayNodes = null;
		arrayNodes = (ArrayNode) requestdata.get("projectAllocations");
		List<AllocationModel> allocations = new ArrayList<AllocationModel>();

		try {
			if (arrayNodes != null) {

				for (JsonNode node : arrayNodes) {
					AllocationModel allocation = new AllocationModel();
					allocation.setAllocatedPerce(node.get("allocatedPerce").asDouble());
					allocation.setActive(node.get("active").asBoolean());
					String date1 = node.get("startDate").asText();
					String date2 = node.get("endDate").asText();
					TimeZone zone = TimeZone.getTimeZone("MST");
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
					outputFormat.setTimeZone(zone);
					// Formating the date values
					Date startDate = null, endDate = null;
					if (!date1.isEmpty()) {
						startDate = outputFormat.parse(date1);
					}
					if (!date2.isEmpty()) {
						endDate = outputFormat.parse(date2);
					}
					allocation.setStartDate(startDate);
					allocation.setEndDate(endDate);
					// Setting values to Allocation model object
					Long projectId = node.get("projectId").asLong();
					Long userId = node.get("userId").asLong();
					Boolean isBillable = node.get("isBillable").asBoolean();
					allocation.setIsBillable(isBillable);
					ProjectModel project = projectService.findById(projectId);
					UserModel user = userService.getUserDetailsById(userId);
					allocation.setuser(user);
					allocation.setproject(project);
					allocation.setAllocId(node.get("allocId").asLong());
					allocations.add(allocation);

				}

				List<AllocationModel> allocs = projectAllocation.saveAllocation(allocations);

				response.put("status", "success");
				response.put("code", httpstatus.getStatus());
				response.put("message", "successfully saved. ");
			}
		} catch (Exception e) {
			response.put("status", "failure");
			response.put("code", httpstatus.getStatus());
			response.put("message", "failed. " + e);
		}

		return response;
	}

	/**
	 * 
	 * @throws ParseException
	 * @desc edit the project period from the project allocation page
	 */
	@PostMapping("/modifyProjectPeriod")
	public ObjectNode modifyProjectPeriod(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus)
			throws ParseException {

		Long projectId = null;
		ObjectNode responsedata = objectMapper.createObjectNode();
		String date1 = requestdata.get("startDate").asText();
		String date2 = requestdata.get("endDate").asText();
		TimeZone zone = TimeZone.getTimeZone("MST");
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		outputFormat.setTimeZone(zone);
		// Formating the date values
		Date startDate = null, endDate = null;
		if (!requestdata.get("startDate").toString().isEmpty() && requestdata.get("startDate").toString() != null) {
			date1 = requestdata.get("startDate").toString();
			if (!date1.isEmpty()) {
				startDate = outputFormat.parse(date1);

			}
		}
		if (!requestdata.get("endDate").toString().isEmpty() && requestdata.get("endDate").toString() != null) {
			date2 = requestdata.get("endDate").toString();
			if (!date2.isEmpty()) {
				endDate = outputFormat.parse(date2);

			}
		}
		if (requestdata.get("projectId").toString() != null || requestdata.get("projectId").toString() != "") {
			projectId = requestdata.get("projectId").asLong();
		}
		if (projectId != null) {
			ProjectModel project = projectService.findById(projectId);
			project.setStartDate(startDate);
			project.setEndDate(endDate);
			project.setStartDate(startDate);
			project.setEndDate(endDate);
			projectService.save_project_record(project);
		}
		return responsedata;
	}

	@PostMapping("/checkPreviouslyAllocatedOrNot")
	public ObjectNode checkPreviouslyAllocatedOrNot(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus)
			throws ParseException {

		ObjectNode response = objectMapper.createObjectNode();
		Long userId = null;
		Long projectId = null;
		if (requestdata.get("userId").toString() != null || requestdata.get("userId").toString() != "") {
			userId = requestdata.get("userId").asLong();
		}
		if (requestdata.get("projectId").toString() != null || requestdata.get("projectId").toString() != "") {
			projectId = requestdata.get("projectId").asLong();
		}
		try {

			Boolean deactiveStatus = projectAllocation.checkPreviouslyAllocatedOrNot(userId, projectId);

			if (deactiveStatus) {
				response.put("status", "success");
				response.put("allocationStatus", deactiveStatus);
				response.put("code", httpstatus.getStatus());
				response.put("message", "No exsisting allocation ");
			} else {
				response.put("status", "success");
				response.put("code", httpstatus.getStatus());
				response.put("allocationStatus", deactiveStatus);
				response.put("message", "Deactivate the exsisting allocation");
			}

		} catch (Exception e) {
			// TODO: handle exception
			response.put("status", "failure");
			response.put("code", httpstatus.getStatus());
			response.put("message", "failed. " + e);
		}
		return response;
	}

	/**
	 * @author sreejith.j
	 * @param allocId
	 * @return
	 */
	@DeleteMapping("/deactivateAllocation/{allocId}")
	public ResponseEntity<Object> deactivateAllocation(@PathVariable("allocId") Long allocId) {
		ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.ACCEPTED);
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		try {
			projectAllocation.deactivateAllocation(allocId);
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.OK);
		} catch (BadInputException e) {
			jsonDataRes.put("status", "Error");
			jsonDataRes.put("code", HttpServletResponse.SC_BAD_REQUEST);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Error");
			jsonDataRes.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
}