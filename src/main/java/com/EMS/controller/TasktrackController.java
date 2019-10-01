package com.EMS.controller;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.dto.Taskdetails;
import com.EMS.model.AllocationModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.Task;
import com.EMS.model.TaskTrackApproval;
import com.EMS.model.TaskTrackApprovalFinance;
import com.EMS.model.TaskTrackApprovalLevel2;
import com.EMS.model.Tasktrack;
import com.EMS.model.UserModel;
import com.EMS.repository.TaskRepository;
import com.EMS.repository.TaskTrackApprovalLevel2Repository;
import com.EMS.service.ProjectAllocationService;
import com.EMS.service.ProjectService;
import com.EMS.service.TasktrackApprovalService;
import com.EMS.service.TasktrackService;
import com.EMS.service.TasktrackServiceImpl;
import com.EMS.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.EMS.repository.TasktrackRepository;
@RestController
@RequestMapping(value = { "/tasktrack" })
public class TasktrackController {

	@Autowired
	ProjectService projectService;
	@Autowired
	TasktrackService tasktrackService;
	@Autowired
	TasktrackServiceImpl tasktrackServiceImpl;
	@Autowired
	UserService userService;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	TasktrackRepository tasktrackRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	ProjectAllocationService projectAllocationService;
	
	@Autowired TasktrackApprovalService tasktrackApprovalService;
	
	@Autowired
	TaskTrackApprovalLevel2Repository taskTrackApprovalLevel2Repository;

	@PostMapping(value = "/getTaskDetails")
	public JsonNode getByDate(@RequestBody Taskdetails requestdata) {
		/*
		 * { "status": "success", "data": { "taskDetails": { "2019-03-11": [ {
		 * "Project": "SAMPLE", "taskType": "SAMPLE", "hours": 3 } ] } } }
		 */
		List<Tasktrack> list = tasktrackService.getByDate(requestdata.getFromDate(), requestdata.getToDate(),
				requestdata.getuId());
		ObjectNode responseNode = objectMapper.createObjectNode();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ObjectNode taskDetails = objectMapper.createObjectNode();
		for (Tasktrack obj : list) {
			if (taskDetails.get(sdf.format(obj.getDate())) != null) {
				ObjectNode objectNode = objectMapper.createObjectNode();
				objectNode.put("taskId", obj.getId());
				objectNode.put("Project",
						(obj.getProject().getProjectName() != null) ? obj.getProject().getProjectName() : null);
				objectNode.put("taskType", (obj.getTask().getTaskName() != null) ? obj.getTask().getTaskName() : null);
				objectNode.put("taskSummary",
						(obj.getDescription() != null) ? obj.getDescription() : obj.getDescription());
				objectNode.put("hours", (obj.getHours() != null) ? obj.getHours() : null);
				ArrayNode arrayNode = (ArrayNode) taskDetails.get(sdf.format(obj.getDate()));
				arrayNode.add(objectNode);
				taskDetails.set(sdf.format(obj.getDate()), arrayNode);
			} else {
				ArrayNode arrayNode = objectMapper.createArrayNode();
				if (obj.getId() != 0) {
					ObjectNode objectNode = objectMapper.createObjectNode();
					objectNode.put("taskId", obj.getId());
					objectNode.put("Project",
							(obj.getProject().getProjectName() != null) ? obj.getProject().getProjectName() : null);
					objectNode.put("taskType",
							(obj.getTask().getTaskName() != null) ? obj.getTask().getTaskName() : null);
					objectNode.put("taskSummary",
							(obj.getDescription() != null) ? obj.getDescription() : obj.getDescription());
					objectNode.put("hours", (obj.getHours() != null) ? obj.getHours() : null);
					arrayNode.add(objectNode);
				}

				taskDetails.set(sdf.format(obj.getDate()), arrayNode);
			}
		}

		ObjectNode dataNode = objectMapper.createObjectNode();
		dataNode.set("taskDetails", taskDetails);
		responseNode.put("status", "success");
		responseNode.set("data", dataNode);

		return responseNode;

	}

	@GetMapping(value = "/getProjectTaskDatas")
	public JSONObject getprojectnameList() {

		List<Object[]> projectTitleList = projectService.getNameId();
		List<Object[]> taskTypesList = tasktrackService.getTaskList();
		JSONObject returnData = new JSONObject();
		JSONObject projectTaskDatas = new JSONObject();
		List<JSONObject> projectIdTitleList = new ArrayList<>();
		List<JSONObject> taskIdTitleList = new ArrayList<>();

		try {
			if (!projectTitleList.isEmpty() && !taskTypesList.isEmpty() && projectTitleList.size() > 0
					&& taskTypesList.size() > 0) {

				for (Object[] itemNew : projectTitleList) {
					JSONObject jsonObjectNew = new JSONObject();
					jsonObjectNew.put("id", itemNew[0]);
					jsonObjectNew.put("value", itemNew[1]);
					projectIdTitleList.add(jsonObjectNew);
				}
				for (Object[] itemNew : taskTypesList) {
					JSONObject jsonObjectNew = new JSONObject();
					jsonObjectNew.put("id", itemNew[0]);
					jsonObjectNew.put("value", itemNew[1]);
					taskIdTitleList.add(jsonObjectNew);
				}
				projectTaskDatas.put("taskTypes", taskIdTitleList);
				projectTaskDatas.put("projectTitle", projectIdTitleList);
				returnData.put("status", "success");
				returnData.put("data", projectTaskDatas);
			}
		} catch (Exception e) {
			returnData.put("status", "failure");
			returnData.put("data", projectTaskDatas);
		}

		return returnData;
	}

	@GetMapping("/getTaskList")
	public ArrayNode getTasks() {
		ArrayNode node = objectMapper.convertValue(tasktrackService.getTasks(), ArrayNode.class);

		return node;
	}

	@PutMapping("/updateTaskById")
	public JsonNode updateTaskById(@RequestBody ObjectNode objectNode) {
		ObjectNode node = objectMapper.createObjectNode();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(TimeZone.getDefault());
			ProjectModel projectModel = tasktrackServiceImpl.getProjectModelById(objectNode.get("projectId").asLong());
			Task taskCategory = tasktrackService.getTaskById(objectNode.get("taskTypeId").asLong());
			Tasktrack tasktrack = new Tasktrack();
			tasktrack.setTask(taskCategory);
			tasktrack.setProject(projectModel);
			tasktrack.setDescription(objectNode.get("taskSummary").asText());
			tasktrack.setId(objectNode.get("taskId").asLong());
			tasktrack.setHours(objectNode.get("hours").asDouble());
			tasktrack.setDate(sdf.parse(objectNode.get("date").asText()));
			if (tasktrackServiceImpl.updateTaskById(tasktrack)) {
				node.put("status", "success");
			} else {
				node.put("status", "failure");
			}
		} catch (Exception e) {
			e.printStackTrace();
			node.put("status", "failure");
		}

		return node;
	}

	@DeleteMapping("/deleteTaskById")
	public JsonNode deleteTaskById(@RequestParam("taskId") int id) {
		ObjectNode node = objectMapper.createObjectNode();

		if (tasktrackServiceImpl.deleteTaskById(id)) {
			node.put("status", "success");
		} else {
			node.put("status", "failure");
		}

		return node;
	}

	@PostMapping("/createTask")
	public JsonNode createTask(@RequestBody Tasktrack task) {
		ObjectNode node = objectMapper.createObjectNode();

		if (tasktrackServiceImpl.createTask(task)) {
			node.put("status", HttpStatus.OK.value());
			node.put("message", "success");
		} else {
			node.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
			node.put("message", "failed to update");
		}

		return node;
	}

	@GetMapping("/getProjectNames")
	public JsonNode getProjectNames(@RequestParam("uId") int uId) {
		ArrayNode projectTitle = objectMapper.createArrayNode();
		for (AllocationModel alloc : tasktrackServiceImpl.getProjectNames(uId)) {

			ObjectNode node = objectMapper.createObjectNode();
			node.put("id", alloc.getproject().getProjectId());
			node.put("value", alloc.getproject().getProjectName());
			projectTitle.add(node);
		}

		ObjectNode dataNode = objectMapper.createObjectNode();
		dataNode.set("projectTitle", projectTitle);

		ObjectNode node = objectMapper.createObjectNode();
		node.put("status", "success");
		node.set("data", dataNode);
		return node;

	}

	@PostMapping("/getProjectNamesByMonth")
	public JsonNode getProjectNamesByMonth(@RequestBody JsonNode requestData) throws ParseException {
		String currentmonth = requestData.get("currentmonth").asText();
		String currentyear  = requestData.get("currentyear").asText();
		int  uId          = requestData.get("uid").asInt();
		/*Calendar cal = Calendar.getInstance();
		int lastDate = cal.getActualMaximum(Calendar.DATE);
		return lastDate;*/
		String fromdate = currentyear+"-"+currentmonth+"-01";
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
		LocalDate date = LocalDate.parse(fromdate, dateFormat);
		ValueRange range = date.range(ChronoField.DAY_OF_MONTH);
		Long max = range.getMaximum();
		String todate = String.format("%s-%s-%d", currentyear, currentmonth, max);
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startdate = outputFormat.parse(fromdate);
		Date enddate = outputFormat.parse(todate);
		//LocalDate newDate = date.withDayOfMonth(max.intValue());
		//return enddate;
		ArrayNode projectTitle = objectMapper.createArrayNode();
		for (AllocationModel alloc : tasktrackServiceImpl.getProjectNamesByMonth(uId,startdate,enddate)) {

			ObjectNode node = objectMapper.createObjectNode();
			node.put("id", alloc.getproject().getProjectId());
			node.put("value", alloc.getproject().getProjectName());
			projectTitle.add(node);
		}

		ObjectNode dataNode = objectMapper.createObjectNode();
		dataNode.set("projectTitle", projectTitle);

		ObjectNode node = objectMapper.createObjectNode();
		node.put("status", "success");
		node.set("data", dataNode);
		return node;

	}

	@GetMapping("/getTaskCategories")
	public JsonNode getTaskCategories(@RequestParam("uId") int uId) {
		ArrayNode taskTypes = objectMapper.createArrayNode();
		for (Task category : tasktrackServiceImpl.getTaskCategory(uId)) {
			ObjectNode node = objectMapper.createObjectNode();
			node.put("id", category.getId());
			node.put("value", category.getTaskName());
			taskTypes.add(node);
		}
		ObjectNode dataNode = objectMapper.createObjectNode();
		dataNode.set("taskTypes", taskTypes);

		ObjectNode node = objectMapper.createObjectNode();
		node.put("status", "success");
		node.set("data", dataNode);
		return node;
	}
	
	

	@PostMapping(value = "/addTask", headers = "Accept=application/json")
	public JsonNode updateData(@RequestBody JsonNode taskData, HttpServletResponse status)
			throws JSONException, ParseException {
		ObjectNode dataResponse = objectMapper.createObjectNode();

		try {
			Long uId = taskData.get("uId").asLong();
			Boolean saveFailed = false;

			if (!uId.equals(null)) {

				ArrayNode arrayNode = (ArrayNode) taskData.get("addTask");
				UserModel user = userService.getUserDetailsById(uId);

				if (!user.equals(null)) {

					for (JsonNode node : arrayNode) {
						Tasktrack tasktrack = new Tasktrack();
						tasktrack.setUser(user);
						Long taskId = node.get("taskTypeId").asLong();
						if (taskId != 0L) {
							Task task = tasktrackService.getTaskById(taskId);
							if (task != null)
								tasktrack.setTask(task);
							else {
								saveFailed = true;
								dataResponse.put("message", "Process failed due to invalid task Id");
							}
						} else {
							saveFailed = true;
							dataResponse.put("message", "Process failed due to empty task Id");
						}

						tasktrack.setHours(node.get("hours").asDouble());

						Long projectId = node.get("projectId").asLong();
						if (projectId != 0L) {
							ProjectModel proj = projectService.findById(projectId);
							if (proj != null)
								tasktrack.setProject(proj);
							else {
								saveFailed = true;
								dataResponse.put("message", "Process failed due to invalid project Id");
							}
						} else {
							saveFailed = true;
							dataResponse.put("message", "Process failed due to empty project Id");
						}

						if (!(node.get("taskSummary").asText().isEmpty()))
							tasktrack.setDescription(node.get("taskSummary").asText());
						else {
							saveFailed = true;
							dataResponse.put("message", "Process failed due to invalid summary ");
						}
						if (!(node.get("date").asText().isEmpty())) {
							String dateNew = node.get("date").asText();
							TimeZone zone = TimeZone.getTimeZone("MST");
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
							outputFormat.setTimeZone(zone);
							Date date1;

							date1 = outputFormat.parse(dateNew);
							if (date1 != null)
								tasktrack.setDate(date1);
							else {
								saveFailed = true;
								dataResponse.put("message", "Process failed due to invalid date ");
							}
						} else {
							saveFailed = true;
							dataResponse.put("message", "Process failed due to empty date value ");
						}
						if (!saveFailed) {
							tasktrackService.saveTaskDetails(tasktrack);
							dataResponse.put("message", "success");

						}

					}

				} else {
					saveFailed = true;
					dataResponse.put("message", "Not a valid user Id");
				}
			} else {
				saveFailed = true;
				dataResponse.put("message", "user id is missing");
			}

			if (saveFailed)
				dataResponse.put("status", "Failed");
			else
				dataResponse.put("status", "success");
			dataResponse.put("code", status.getStatus());

		} catch (Exception e) {
			dataResponse.put("status", "failure");
			dataResponse.put("message", "Exception : "+e);
			System.out.println("Exception " + e);
		}

		return dataResponse;
	}
	
	@PostMapping("/getTaskTrackData")
	public JSONObject getTaskTrackData(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		JSONObject returnJsonData = new JSONObject();
		List<JSONObject> timeTrackJSONData = new ArrayList<>();
		List<JSONObject> loggedJsonArray = new ArrayList<>();
		List<JSONObject> billableJsonArray = new ArrayList<>();
		Long projectId = null;
		
		try {

			if (requestdata.get("projectId") != null && requestdata.get("projectId").asText() != "") {
				projectId = requestdata.get("projectId").asLong();
			}

			String date1 = requestdata.get("startDate").asText();
			String date2 = requestdata.get("endDate").asText();

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = null, endDate = null;
			if (!date1.isEmpty()) {
				startDate = outputFormat.parse(date1);
			}
			if (!date2.isEmpty()) {
				endDate = outputFormat.parse(date2);
			}

			List<Object[]> userIdList = null;
			
			
			if (startDate != null && endDate != null ) {
				//userIdList = projectAllocationService.getUserIdByProject(projectId);
				userIdList = projectAllocationService.getUserIdByProjectAndDate(projectId,startDate,endDate);
				getUserDataForReport(userIdList, startDate, endDate, jsonDataRes, timeTrackJSONData, loggedJsonArray,billableJsonArray,projectId);
			}
			
			jsonDataRes.put("data", timeTrackJSONData);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("message", "success. ");
			jsonDataRes.put("code", httpstatus.getStatus());
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;
	}
	@PostMapping("/getTaskTrackDataByProjectorUser")
	public JSONObject getTaskTrackDataByProjectorUser(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		JSONObject returnJsonData = new JSONObject();
		List<JSONObject> timeTrackJSONData = new ArrayList<>();
		List<JSONObject> loggedJsonArray = new ArrayList<>();
		List<JSONObject> billableJsonArray = new ArrayList<>();
		List<JSONObject> nonBillableJsonArray = new ArrayList<>();
		List<JSONObject> timeTrackJsonData = new ArrayList<>();
		Long projectId = null;
		Long userId = null;

		try {

			if (requestdata.get("projectId") != null && requestdata.get("projectId").asText() != "") {
				projectId = requestdata.get("projectId").asLong();
			}

			if (requestdata.get("userId") != null && requestdata.get("userId").asText() != "") {
				userId = requestdata.get("userId").asLong();
			}

			String date1 = requestdata.get("startDate").asText();
			String date2 = requestdata.get("endDate").asText();

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = null, endDate = null;
			if (!date1.isEmpty()) {
				startDate = outputFormat.parse(date1);
			}
			if (!date2.isEmpty()) {
				endDate = outputFormat.parse(date2);
			}

			List<Object[]> userIdList = null;
			List<Object[]> projectList = null;

			if (startDate != null && endDate != null && projectId != null && userId == null) {
				//userIdList = projectAllocationService.getUserIdByProject(projectId);
				userIdList = projectAllocationService.getUserIdByProjectAndDate(projectId,startDate,endDate);
				getUserDataForReportByProject(userIdList, startDate, endDate, jsonDataRes, timeTrackJSONData, loggedJsonArray,billableJsonArray,nonBillableJsonArray,projectId);
			}
			else if (startDate != null && endDate != null && projectId == null && userId != null) {
				//userIdList = projectAllocationService.getUserIdByProject(projectId);
				//projectIdList = projectAllocationService.getProjectIdByUserAndDate(userId,startDate,endDate);
				projectList = projectAllocationService.getProjectListByUserAndDate(userId, startDate, endDate);
				getProjectDataForReport(projectList, startDate, endDate, jsonDataRes, timeTrackJSONData, loggedJsonArray,billableJsonArray,nonBillableJsonArray,userId);
			}
			else if (startDate != null && endDate != null && projectId != null && userId != null) {
				//userIdList = projectAllocationService.getUserIdByProject(projectId);
			//	List<Object[]> projectList = null;
				String projectName = null;
				Boolean isExist = tasktrackApprovalService.checkIsUserExists(userId);
				//Data From Time track
				projectList =taskRepository.getUserListByProject(userId, startDate, endDate,projectId);
				if (projectList != null && projectList.size() > 0) {
					timeTrackJsonData = tasktrackApprovalService.getTimeTrackUserProjectTaskDetails(projectId, projectName, startDate, endDate, projectList, loggedJsonArray, billableJsonArray, nonBillableJsonArray, timeTrackJSONData, isExist, userId);
				}
			}
			jsonDataRes.put("data", timeTrackJSONData);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("message", "success. ");
			jsonDataRes.put("code", httpstatus.getStatus());
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;
	}
	private void getUserDataForReport(List<Object[]> userIdList, Date startDate, Date endDate, JSONObject jsonDataRes,
			List<JSONObject> timeTrackJSONData, List<JSONObject> loggedJsonArray,List<JSONObject> billableJsonArray,Long projectId) {
		
		JSONObject resultData = new JSONObject();
		List<JSONObject> timeTrackJsonData = new ArrayList<>();
		List<JSONObject> approvalJsonData = new ArrayList<>();
		for (Object userItem : userIdList) {

			Long id = (Long) userItem;
			List<Object[]> userList = null;
			Boolean isExist = tasktrackApprovalService.checkIsUserExists(id);
			//Data From Time track
			timeTrackJsonData = tasktrackApprovalService.getTimeTrackUserTaskDetails(id, startDate, endDate, userList, loggedJsonArray,billableJsonArray, timeTrackJSONData, isExist,projectId);	
		}
		
	}
	private void getUserDataForReportByProjectandUser(List<Object[]> userIdList, Date startDate, Date endDate, JSONObject jsonDataRes,
									  List<JSONObject> timeTrackJSONData, List<JSONObject> loggedJsonArray,List<JSONObject> billableJsonArray,List<JSONObject> nonBillableJsonArray,Long projectId) {

		JSONObject resultData = new JSONObject();
		List<JSONObject> timeTrackJsonData = new ArrayList<>();
		List<JSONObject> approvalJsonData = new ArrayList<>();
		for (Object userItem : userIdList) {

			Long id = (Long) userItem;
			List<Object[]> userList = null;
			Boolean isExist = tasktrackApprovalService.checkIsUserExists(id);
			//Data From Time trackgetTaskTrackDataByUserId
			timeTrackJsonData = tasktrackApprovalService.getTimeTrackUserTaskDetailsByProject(id, startDate, endDate, userList, loggedJsonArray,billableJsonArray,nonBillableJsonArray, timeTrackJSONData, isExist,projectId);
		}

	}
	private void getUserDataForReportByProject(List<Object[]> userIdList, Date startDate, Date endDate, JSONObject jsonDataRes,
											   List<JSONObject> timeTrackJSONData, List<JSONObject> loggedJsonArray,List<JSONObject> billableJsonArray,List<JSONObject> nonBillableJsonArray,Long projectId) {

		JSONObject resultData = new JSONObject();
		List<JSONObject> timeTrackJsonData = new ArrayList<>();
		List<JSONObject> approvalJsonData = new ArrayList<>();
		for (Object userItem : userIdList) {

			Long id = (Long) userItem;
			List<Object[]> userList = null;
			Boolean isExist = tasktrackApprovalService.checkIsUserExists(id);
			//Data From Time track
			timeTrackJsonData = tasktrackApprovalService.getTimeTrackUserTaskDetailsByProject(id, startDate, endDate, userList, loggedJsonArray,billableJsonArray,nonBillableJsonArray, timeTrackJSONData, isExist,projectId);
		}

	}

	private void getProjectDataForReport(List<Object[]> projectIdList, Date startDate, Date endDate, JSONObject jsonDataRes,
									  List<JSONObject> timeTrackJSONData, List<JSONObject> loggedJsonArray,List<JSONObject> billableJsonArray,List<JSONObject> nonBillableJsonArray,Long userId) {

		JSONObject resultData = new JSONObject();
		List<JSONObject> timeTrackJsonData = new ArrayList<>();
		List<JSONObject> approvalJsonData = new ArrayList<>();
		Long projectId = null;
		String projectName = null;
		for (Object[] projectItem : projectIdList) {

			projectId = ((BigInteger) projectItem[0]).longValue();
			projectName = (String)projectItem[1];
			//System.out.println(projectId);
			//System.out.println(projectName);
			List<Object[]> projectList = null;
			Boolean isExist = tasktrackApprovalService.checkIsUserExists(userId);
			//Data From Time track
			timeTrackJsonData = tasktrackApprovalService.getTimeTrackUserProjectTaskDetails(projectId,projectName, startDate, endDate, projectList, loggedJsonArray,billableJsonArray,nonBillableJsonArray,timeTrackJSONData, isExist,userId);
		}

	}
	
	@PostMapping("/getTaskTrackDataByUserId")
	public JSONObject getTaskTrackDataByUserId(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		JSONObject returnJsonData = new JSONObject();
		List<JSONObject> timeTrackJSONData = new ArrayList<>();
		List<JSONObject> approvalJSONData = new ArrayList<>();
		List<JSONObject> jsonArray = new ArrayList<>();
		Long userId=null,projectId = null;
		
		try {

			if (requestdata.get("projectId") != null && requestdata.get("projectId").asText() != "") {
				projectId = requestdata.get("projectId").asLong();
			}
			if (requestdata.get("userId") != null && requestdata.get("userId").asText() != "") {
				userId = requestdata.get("userId").asLong();
			}
			String date1 = requestdata.get("startDate").asText();
			String date2 = requestdata.get("endDate").asText();

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = null, endDate = null;
			if (!date1.isEmpty()) {
				startDate = outputFormat.parse(date1);
			}
			if (!date2.isEmpty()) {
				endDate = outputFormat.parse(date2);
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int intMonth = 0,intday = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);
			intday = cal.get(Calendar.DAY_OF_MONTH);
			String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
					+ ((intday < 10) ? "0" + intday : "" + intday);
			
			JSONObject jsonDataProjectDetails = new JSONObject();
			
			ProjectModel projectdetails = null;
			
			if(projectId != null) {
				//System.out.println("Here____________________________");
				 projectdetails = getProjectDetails(projectId);
				}
			if(projectdetails != null) {
		
				//System.out.println("Here____________________________");
				if(projectdetails.getProjectOwner() != null)
				jsonDataProjectDetails.put("approver_level_1",projectdetails.getProjectOwner().getUserId());
				
					
				if(projectdetails.getOnsite_lead() != null)
				jsonDataProjectDetails.put("approver_level_2",projectdetails.getOnsite_lead().getUserId());	
				else
					jsonDataProjectDetails.put("approver_level_2","");	
				//timeTrackJSONData.add(jsonDataProjectDetails);		
				//TaskTrackApprovalLevel2 forwardeddate = taskTrackApprovalLevel2Repository.getForwardedDate(projectId,userId,intMonth);
				int yearIndex = cal.get(Calendar.YEAR);
				System.out.println("Month"+intMonth+"Year"+yearIndex);
				String frowardedDate = "";
				String frowardedDateLevel2 = "";
				 String pattern = "yyyy-MM-dd"; 
				 DateFormat df = new SimpleDateFormat(pattern);
				
				 List<Object> level2 = tasktrackApprovalService.getForwardedDateLevel2(projectId,userId,intMonth,yearIndex);
				  
				  if(!level2.isEmpty()) {
				  
					 // System.out.println("forwarded_date"+level2.get(0));
					  if(level2.get(0) != null) {
						  
						  Date fdate = (Date) level2.get(0);
						  frowardedDateLevel2 = df.format(fdate);
					  }
					  else {
						
						 frowardedDateLevel2 = ""; 
					  }
				 }
				 
					
				  List<Object[]> level1 = tasktrackApprovalService.getForwardedDates(projectId,userId,intMonth,yearIndex);
				  if(!level1.isEmpty()) {
					 // System.out.println("forwarded_date"+level1.get(0));
					 if(level1 != null) {
						for(Object[] fl : level1) {
							if(fl != null)
							{ 
								if(fl[0] != null) {
								Date fdate = (Date) fl[0];
							  frowardedDate = df.format(fdate);
								}
								if(fl[1] != null) {
								Date fdates = (Date) fl[1];
							  frowardedDateLevel2 = df.format(fdates);
								}
							}
						}
					 }	  						 //System.out.println("frowardedDate___________"+frowardedDate);
 

				 }
				
				jsonDataProjectDetails.put("forwarded_date",frowardedDate);
				jsonDataProjectDetails.put("forwarded_date_finance",frowardedDateLevel2);
			}
			
			List<Object[]> userIdList = null;
			Long count = null;
			
			if (startDate != null && endDate != null ) {
				userIdList = projectAllocationService.getUserIdByProject(projectId);
				returnJsonData = getUserDataForApproval(userId, startDate, endDate, jsonDataRes, timeTrackJSONData,approvalJSONData, jsonArray,projectId);
			}
			
			jsonDataRes.put("data", returnJsonData);
			jsonDataRes.put("details",jsonDataProjectDetails);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("message", "success. ");
			jsonDataRes.put("code", httpstatus.getStatus());
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;
	}
	private JSONObject getUserDataForApproval(Long userId, Date startDate, Date endDate, JSONObject jsonDataRes,
			List<JSONObject> timeTrackJSONData,List<JSONObject> approvalJSONData, List<JSONObject> jsonArray,Long projectId) {
		
		JSONObject resultData = new JSONObject();
		List<JSONObject> timeTrackJsonData = new ArrayList<>();
		JSONObject approvalJsonData = new JSONObject();
		List<TaskTrackApproval>  userList = null;
		Boolean isExist = tasktrackApprovalService.checkIsUserExists(userId);
		//Data From Approval table
		approvalJsonData = tasktrackApprovalService.getApprovedUserTaskDetails(userId, startDate, endDate,userList,jsonArray, approvalJSONData, isExist,projectId);	
		
		resultData.put("ApprovedData", approvalJsonData);
		
		return resultData;
		
	}
	@SuppressWarnings("unchecked")
	@PostMapping("/saveApprovedHours")
	public ObjectNode saveApprovedHours(@RequestBody JSONObject requestdata, HttpServletResponse httpstatus) {

		
		ObjectNode jsonDataRes = objectMapper.createObjectNode();

		try {
			// Obtain the data from request data
			Long billableId =null,nonbillableId=null,beachId=null,overtimeId=null,projectId=null,userId=null,updatedBy=null;
			Integer year = Integer.parseInt((String)requestdata.get("year"));
			Integer month = (Integer) requestdata.get("month");
			if (requestdata.get("projectId") != null && requestdata.get("projectId") != "") {
				projectId = Long.valueOf(requestdata.get("projectId").toString());
			}
			if (requestdata.get("userId") != null && requestdata.get("userId") != "") {
				userId = Long.valueOf(requestdata.get("userId").toString());
			}
			if (requestdata.get("updatedBy") != null && requestdata.get("updatedBy") != "") {
				updatedBy = Long.valueOf(requestdata.get("updatedBy").toString());
			}
			if (requestdata.get("billableId") != null && requestdata.get("billableId") != "") {
				billableId = Long.valueOf(requestdata.get("billableId").toString());
			}
			if (requestdata.get("nonBillableId") != null && requestdata.get("nonBillableId") != "") {
				nonbillableId = Long.valueOf(requestdata.get("nonBillableId").toString());
			}
			if (requestdata.get("beachId") != null && requestdata.get("beachId")!= "") {
				beachId = Long.valueOf(requestdata.get("beachId").toString());
			}
			if (requestdata.get("overtimeId") != null && requestdata.get("overtimeId")!= "") {
				overtimeId = Long.valueOf(requestdata.get("overtimeId").toString());
			}
			String date1 = (String) requestdata.get("startDate");
			String date2 = (String) requestdata.get("endDate");

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = null, endDate = null;
			if (!date1.isEmpty()) {
				startDate = outputFormat.parse(date1);
			}
			if (!date2.isEmpty()) {
				endDate = outputFormat.parse(date2);
			}

			HashMap<String, Object> billableArray = new JSONObject();
			HashMap<String, Object> nonbillableArray = new JSONObject();
			HashMap<String, Object> beachArray = new JSONObject();
			HashMap<String, Object> overtimeArray = new JSONObject();

			UserModel user = userService.getUserDetailsById(userId);
			ProjectModel project = projectService.getProjectId(projectId);

			if (requestdata.get("billable") != null && requestdata.get("billable")!= "") {
				billableArray =(HashMap<String, Object>) (requestdata.get("billable"));
			}
			if (requestdata.get("nonBillable") != null && requestdata.get("nonBillable")!= "") {
				nonbillableArray = (HashMap<String, Object>) requestdata.get("nonBillable");
			}
			if (requestdata.get("beach") != null && requestdata.get("beach")!= "") {
				beachArray = (HashMap<String, Object>) requestdata.get("beach");
			}
			if (requestdata.get("overtime") != null && requestdata.get("overtime")!= "") {
				overtimeArray = (HashMap<String, Object>) requestdata.get("overtime");
			}
			
			if(billableArray.size()>0) {//Billable

				Calendar cal = Calendar.getInstance();

				int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
				int intMonth = 0,intday = 0;
				cal.setTime(startDate);
				double hours =0;

				if(billableId!=null) {
					TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(billableId);
					taskTrackApproval.setUpdatedBy(updatedBy);
					taskTrackApproval.setApproved_date(endDate);
					if(taskTrackApproval!=null) {

						for (int i = 0; i < diffInDays; i++) {

							intMonth = (cal.get(Calendar.MONTH) + 1);
							intday = cal.get(Calendar.DAY_OF_MONTH);
							String dateString = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
									+ ((intday < 10) ? "0" + intday : "" + intday);
							
							if(billableArray.get(dateString)!=null) {						
								hours = Double.valueOf(billableArray.get(dateString).toString());	
												
								if(i==0) {
									taskTrackApproval.setDay1(hours);
								}
								else if(i==1) {
									taskTrackApproval.setDay2(hours);
								}
								else if(i==2) {
									taskTrackApproval.setDay3(hours);
								}
								else if(i==3) {
									taskTrackApproval.setDay4(hours);
								}
								else if(i==4) {
									taskTrackApproval.setDay5(hours);
								}
								else if(i==5) {
									taskTrackApproval.setDay6(hours);
								}
								else if(i==6) {
									taskTrackApproval.setDay7(hours);
								}
								else if(i==7) {
									taskTrackApproval.setDay8(hours);
								}
								else if(i==8) {
									taskTrackApproval.setDay9(hours);
								}
								else if(i==9) {
									taskTrackApproval.setDay10(hours);
								}
								else if(i==10) {
									taskTrackApproval.setDay11(hours);
								}
								else if(i==11) {
									taskTrackApproval.setDay12(hours);
								}
								else if(i==12) {
									taskTrackApproval.setDay13(hours);
								}
								else if(i==13) {
									taskTrackApproval.setDay14(hours);
								}
								else if(i==14) {
									taskTrackApproval.setDay15(hours);
								}
								else if(i==15) {
									taskTrackApproval.setDay16(hours);
								}
								else if(i==16) {
									taskTrackApproval.setDay17(hours);
								}
								else if(i==17) {
									taskTrackApproval.setDay18(hours);
								}
								else if(i==18) {
									taskTrackApproval.setDay19(hours);
								}
								else if(i==19) {
									taskTrackApproval.setDay20(hours);
								}
								else if(i==20) {
									taskTrackApproval.setDay21(hours);
								}
								else if(i==21) {
									taskTrackApproval.setDay22(hours);
								}
								else if(i==22) {
									taskTrackApproval.setDay23(hours);
								}
								else if(i==23) {
									taskTrackApproval.setDay24(hours);
								}
								else if(i==24) {
									taskTrackApproval.setDay25(hours);
								}
								else if(i==25) {
									taskTrackApproval.setDay26(hours);
								}
								else if(i==26) {
									taskTrackApproval.setDay27(hours);
								}
								else if(i==27) {
									taskTrackApproval.setDay28(hours);
								}
								else if(i==28) {
									taskTrackApproval.setDay29(hours);
								}
								else if(i==29) {
									taskTrackApproval.setDay30(hours);
								}
								else if(i==30) {
									taskTrackApproval.setDay31(hours);
								}
							}					
							cal.add(Calendar.DATE, 1);				
						}				
						tasktrackApprovalService.updateData(taskTrackApproval);				
					}
				}
				else {

					TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
					taskTrackApproval.setMonth(month);
					taskTrackApproval.setYear(year);
					taskTrackApproval.setUser(user);
					taskTrackApproval.setApproved_date(endDate);
					taskTrackApproval.setProjectType("Billable");
					taskTrackApproval.setUpdatedBy(updatedBy);
					taskTrackApproval.setProject(project);
					for (int i = 0; i < diffInDays; i++) {

						intMonth = (cal.get(Calendar.MONTH) + 1);
						intday = cal.get(Calendar.DAY_OF_MONTH);
						String dateString = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);
						

						if(billableArray.get(dateString)!=null) {						
							hours = Double.valueOf(billableArray.get(dateString).toString());
											
							if(i==0) {
								taskTrackApproval.setDay1(hours);
							}
							else if(i==1) {
								taskTrackApproval.setDay2(hours);
							}
							else if(i==2) {
								taskTrackApproval.setDay3(hours);
							}
							else if(i==3) {
								taskTrackApproval.setDay4(hours);
							}
							else if(i==4) {
								taskTrackApproval.setDay5(hours);
							}
							else if(i==5) {
								taskTrackApproval.setDay6(hours);
							}
							else if(i==6) {
								taskTrackApproval.setDay7(hours);
							}
							else if(i==7) {
								taskTrackApproval.setDay8(hours);
							}
							else if(i==8) {
								taskTrackApproval.setDay9(hours);
							}
							else if(i==9) {
								taskTrackApproval.setDay10(hours);
							}
							else if(i==10) {
								taskTrackApproval.setDay11(hours);
							}
							else if(i==11) {
								taskTrackApproval.setDay12(hours);
							}
							else if(i==12) {
								taskTrackApproval.setDay13(hours);
							}
							else if(i==13) {
								taskTrackApproval.setDay14(hours);
							}
							else if(i==14) {
								taskTrackApproval.setDay15(hours);
							}
							else if(i==15) {
								taskTrackApproval.setDay16(hours);
							}
							else if(i==16) {
								taskTrackApproval.setDay17(hours);
							}
							else if(i==17) {
								taskTrackApproval.setDay18(hours);
							}
							else if(i==18) {
								taskTrackApproval.setDay19(hours);
							}
							else if(i==19) {
								taskTrackApproval.setDay20(hours);
							}
							else if(i==20) {
								taskTrackApproval.setDay21(hours);
							}
							else if(i==21) {
								taskTrackApproval.setDay22(hours);
							}
							else if(i==22) {
								taskTrackApproval.setDay23(hours);
							}
							else if(i==23) {
								taskTrackApproval.setDay24(hours);
							}
							else if(i==24) {
								taskTrackApproval.setDay25(hours);
							}
							else if(i==25) {
								taskTrackApproval.setDay26(hours);
							}
							else if(i==26) {
								taskTrackApproval.setDay27(hours);
							}
							else if(i==27) {
								taskTrackApproval.setDay28(hours);
							}
							else if(i==28) {
								taskTrackApproval.setDay29(hours);
							}
							else if(i==29) {
								taskTrackApproval.setDay30(hours);
							}
							else if(i==30) {
								taskTrackApproval.setDay31(hours);
							}

						}					
						cal.add(Calendar.DATE, 1);				
					}				

					tasktrackApprovalService.save(taskTrackApproval);
				}
			}

			/**************************************************************/

			if(nonbillableArray.size()>0) {//Non-Billable

				Calendar cal = Calendar.getInstance();

				int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
				int intMonth = 0,intday = 0;
				cal.setTime(startDate);
				double hours =0;

				if(nonbillableId!=null) {
					TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(nonbillableId);
				
					taskTrackApproval.setApproved_date(endDate);
					taskTrackApproval.setUpdatedBy(updatedBy);
					if(taskTrackApproval!=null) {

						for (int i = 0; i < diffInDays; i++) {

							intMonth = (cal.get(Calendar.MONTH) + 1);
							intday = cal.get(Calendar.DAY_OF_MONTH);
							String dateString = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
									+ ((intday < 10) ? "0" + intday : "" + intday);
							

							if(nonbillableArray.get(dateString)!=null) {						
								hours = Double.valueOf(nonbillableArray.get(dateString).toString());	
												
								if(i==0) {
									taskTrackApproval.setDay1(hours);
								}
								else if(i==1) {
									taskTrackApproval.setDay2(hours);
								}
								else if(i==2) {
									taskTrackApproval.setDay3(hours);
								}
								else if(i==3) {
									taskTrackApproval.setDay4(hours);
								}
								else if(i==4) {
									taskTrackApproval.setDay5(hours);
								}
								else if(i==5) {
									taskTrackApproval.setDay6(hours);
								}
								else if(i==6) {
									taskTrackApproval.setDay7(hours);
								}
								else if(i==7) {
									taskTrackApproval.setDay8(hours);
								}
								else if(i==8) {
									taskTrackApproval.setDay9(hours);
								}
								else if(i==9) {
									taskTrackApproval.setDay10(hours);
								}
								else if(i==10) {
									taskTrackApproval.setDay11(hours);
								}
								else if(i==11) {
									taskTrackApproval.setDay12(hours);
								}
								else if(i==12) {
									taskTrackApproval.setDay13(hours);
								}
								else if(i==13) {
									taskTrackApproval.setDay14(hours);
								}
								else if(i==14) {
									taskTrackApproval.setDay15(hours);
								}
								else if(i==15) {
									taskTrackApproval.setDay16(hours);
								}
								else if(i==16) {
									taskTrackApproval.setDay17(hours);
								}
								else if(i==17) {
									taskTrackApproval.setDay18(hours);
								}
								else if(i==18) {
									taskTrackApproval.setDay19(hours);
								}
								else if(i==19) {
									taskTrackApproval.setDay20(hours);
								}
								else if(i==20) {
									taskTrackApproval.setDay21(hours);
								}
								else if(i==21) {
									taskTrackApproval.setDay22(hours);
								}
								else if(i==22) {
									taskTrackApproval.setDay23(hours);
								}
								else if(i==23) {
									taskTrackApproval.setDay24(hours);
								}
								else if(i==24) {
									taskTrackApproval.setDay25(hours);
								}
								else if(i==25) {
									taskTrackApproval.setDay26(hours);
								}
								else if(i==26) {
									taskTrackApproval.setDay27(hours);
								}
								else if(i==27) {
									taskTrackApproval.setDay28(hours);
								}
								else if(i==28) {
									taskTrackApproval.setDay29(hours);
								}
								else if(i==29) {
									taskTrackApproval.setDay30(hours);
								}
								else if(i==30) {
									taskTrackApproval.setDay31(hours);
								}

							}					
							cal.add(Calendar.DATE, 1);				
						}				
						tasktrackApprovalService.updateData(taskTrackApproval);				
					}
				}
				else {

					TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
					taskTrackApproval.setMonth(month);
					taskTrackApproval.setYear(year);
					taskTrackApproval.setUser(user);
					taskTrackApproval.setProjectType("Non-Billable");
					taskTrackApproval.setApproved_date(endDate);
					taskTrackApproval.setUpdatedBy(updatedBy);
					taskTrackApproval.setProject(project);
					for (int i = 0; i < diffInDays; i++) {

						intMonth = (cal.get(Calendar.MONTH) + 1);
						intday = cal.get(Calendar.DAY_OF_MONTH);
						String dateString = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);
						
						if(nonbillableArray.get(dateString)!=null) {						
							hours = Double.valueOf(nonbillableArray.get(dateString).toString());	
											
							if(i==0) {
								taskTrackApproval.setDay1(hours);
							}
							else if(i==1) {
								taskTrackApproval.setDay2(hours);
							}
							else if(i==2) {
								taskTrackApproval.setDay3(hours);
							}
							else if(i==3) {
								taskTrackApproval.setDay4(hours);
							}
							else if(i==4) {
								taskTrackApproval.setDay5(hours);
							}
							else if(i==5) {
								taskTrackApproval.setDay6(hours);
							}
							else if(i==6) {
								taskTrackApproval.setDay7(hours);
							}
							else if(i==7) {
								taskTrackApproval.setDay8(hours);
							}
							else if(i==8) {
								taskTrackApproval.setDay9(hours);
							}
							else if(i==9) {
								taskTrackApproval.setDay10(hours);
							}
							else if(i==10) {
								taskTrackApproval.setDay11(hours);
							}
							else if(i==11) {
								taskTrackApproval.setDay12(hours);
							}
							else if(i==12) {
								taskTrackApproval.setDay13(hours);
							}
							else if(i==13) {
								taskTrackApproval.setDay14(hours);
							}
							else if(i==14) {
								taskTrackApproval.setDay15(hours);
							}
							else if(i==15) {
								taskTrackApproval.setDay16(hours);
							}
							else if(i==16) {
								taskTrackApproval.setDay17(hours);
							}
							else if(i==17) {
								taskTrackApproval.setDay18(hours);
							}
							else if(i==18) {
								taskTrackApproval.setDay19(hours);
							}
							else if(i==19) {
								taskTrackApproval.setDay20(hours);
							}
							else if(i==20) {
								taskTrackApproval.setDay21(hours);
							}
							else if(i==21) {
								taskTrackApproval.setDay22(hours);
							}
							else if(i==22) {
								taskTrackApproval.setDay23(hours);
							}
							else if(i==23) {
								taskTrackApproval.setDay24(hours);
							}
							else if(i==24) {
								taskTrackApproval.setDay25(hours);
							}
							else if(i==25) {
								taskTrackApproval.setDay26(hours);
							}
							else if(i==26) {
								taskTrackApproval.setDay27(hours);
							}
							else if(i==27) {
								taskTrackApproval.setDay28(hours);
							}
							else if(i==28) {
								taskTrackApproval.setDay29(hours);
							}
							else if(i==29) {
								taskTrackApproval.setDay30(hours);
							}
							else if(i==30) {
								taskTrackApproval.setDay31(hours);
							}

						}					
						cal.add(Calendar.DATE, 1);				
					}				

					tasktrackApprovalService.save(taskTrackApproval);
				}



			}
			/****************************************************************************************/

			if(beachArray.size()>0) {//Beach

				Calendar cal = Calendar.getInstance();

				int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
				int intMonth = 0,intday = 0;
				cal.setTime(startDate);
				double hours =0;

				if(beachId!=null) {
					TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(beachId);
					taskTrackApproval.setUpdatedBy(updatedBy);
					taskTrackApproval.setApproved_date(endDate);
					if(taskTrackApproval!=null) {

						for (int i = 0; i < diffInDays; i++) {

							intMonth = (cal.get(Calendar.MONTH) + 1);
							intday = cal.get(Calendar.DAY_OF_MONTH);
							String dateString = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
									+ ((intday < 10) ? "0" + intday : "" + intday);
							

							if(beachArray.get(dateString)!=null) {						
								hours = Double.valueOf(beachArray.get(dateString).toString());		
												
								if(i==0) {
									taskTrackApproval.setDay1(hours);
								}
								else if(i==1) {
									taskTrackApproval.setDay2(hours);
								}
								else if(i==2) {
									taskTrackApproval.setDay3(hours);
								}
								else if(i==3) {
									taskTrackApproval.setDay4(hours);
								}
								else if(i==4) {
									taskTrackApproval.setDay5(hours);
								}
								else if(i==5) {
									taskTrackApproval.setDay6(hours);
								}
								else if(i==6) {
									taskTrackApproval.setDay7(hours);
								}
								else if(i==7) {
									taskTrackApproval.setDay8(hours);
								}
								else if(i==8) {
									taskTrackApproval.setDay9(hours);
								}
								else if(i==9) {
									taskTrackApproval.setDay10(hours);
								}
								else if(i==10) {
									taskTrackApproval.setDay11(hours);
								}
								else if(i==11) {
									taskTrackApproval.setDay12(hours);
								}
								else if(i==12) {
									taskTrackApproval.setDay13(hours);
								}
								else if(i==13) {
									taskTrackApproval.setDay14(hours);
								}
								else if(i==14) {
									taskTrackApproval.setDay15(hours);
								}
								else if(i==15) {
									taskTrackApproval.setDay16(hours);
								}
								else if(i==16) {
									taskTrackApproval.setDay17(hours);
								}
								else if(i==17) {
									taskTrackApproval.setDay18(hours);
								}
								else if(i==18) {
									taskTrackApproval.setDay19(hours);
								}
								else if(i==19) {
									taskTrackApproval.setDay20(hours);
								}
								else if(i==20) {
									taskTrackApproval.setDay21(hours);
								}
								else if(i==21) {
									taskTrackApproval.setDay22(hours);
								}
								else if(i==22) {
									taskTrackApproval.setDay23(hours);
								}
								else if(i==23) {
									taskTrackApproval.setDay24(hours);
								}
								else if(i==24) {
									taskTrackApproval.setDay25(hours);
								}
								else if(i==25) {
									taskTrackApproval.setDay26(hours);
								}
								else if(i==26) {
									taskTrackApproval.setDay27(hours);
								}
								else if(i==27) {
									taskTrackApproval.setDay28(hours);
								}
								else if(i==28) {
									taskTrackApproval.setDay29(hours);
								}
								else if(i==29) {
									taskTrackApproval.setDay30(hours);
								}
								else if(i==30) {
									taskTrackApproval.setDay31(hours);
								}

							}					
							cal.add(Calendar.DATE, 1);				
						}				
						tasktrackApprovalService.updateData(taskTrackApproval);				
					}
				}
				else {

					TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
					taskTrackApproval.setMonth(month);
					taskTrackApproval.setYear(year);
					taskTrackApproval.setUser(user);
					taskTrackApproval.setProjectType("Beach");
					taskTrackApproval.setUpdatedBy(updatedBy);
					taskTrackApproval.setApproved_date(endDate);
					taskTrackApproval.setProject(project);
					for (int i = 0; i < diffInDays; i++) {

						intMonth = (cal.get(Calendar.MONTH) + 1);
						intday = cal.get(Calendar.DAY_OF_MONTH);
						String dateString = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);
						

						if(beachArray.get(dateString)!=null) {						
							hours = Double.valueOf(beachArray.get(dateString).toString());
											
							if(i==0) {
								taskTrackApproval.setDay1(hours);
							}
							else if(i==1) {
								taskTrackApproval.setDay2(hours);
							}
							else if(i==2) {
								taskTrackApproval.setDay3(hours);
							}
							else if(i==3) {
								taskTrackApproval.setDay4(hours);
							}
							else if(i==4) {
								taskTrackApproval.setDay5(hours);
							}
							else if(i==5) {
								taskTrackApproval.setDay6(hours);
							}
							else if(i==6) {
								taskTrackApproval.setDay7(hours);
							}
							else if(i==7) {
								taskTrackApproval.setDay8(hours);
							}
							else if(i==8) {
								taskTrackApproval.setDay9(hours);
							}
							else if(i==9) {
								taskTrackApproval.setDay10(hours);
							}
							else if(i==10) {
								taskTrackApproval.setDay11(hours);
							}
							else if(i==11) {
								taskTrackApproval.setDay12(hours);
							}
							else if(i==12) {
								taskTrackApproval.setDay13(hours);
							}
							else if(i==13) {
								taskTrackApproval.setDay14(hours);
							}
							else if(i==14) {
								taskTrackApproval.setDay15(hours);
							}
							else if(i==15) {
								taskTrackApproval.setDay16(hours);
							}
							else if(i==16) {
								taskTrackApproval.setDay17(hours);
							}
							else if(i==17) {
								taskTrackApproval.setDay18(hours);
							}
							else if(i==18) {
								taskTrackApproval.setDay19(hours);
							}
							else if(i==19) {
								taskTrackApproval.setDay20(hours);
							}
							else if(i==20) {
								taskTrackApproval.setDay21(hours);
							}
							else if(i==21) {
								taskTrackApproval.setDay22(hours);
							}
							else if(i==22) {
								taskTrackApproval.setDay23(hours);
							}
							else if(i==23) {
								taskTrackApproval.setDay24(hours);
							}
							else if(i==24) {
								taskTrackApproval.setDay25(hours);
							}
							else if(i==25) {
								taskTrackApproval.setDay26(hours);
							}
							else if(i==26) {
								taskTrackApproval.setDay27(hours);
							}
							else if(i==27) {
								taskTrackApproval.setDay28(hours);
							}
							else if(i==28) {
								taskTrackApproval.setDay29(hours);
							}
							else if(i==29) {
								taskTrackApproval.setDay30(hours);
							}
							else if(i==30) {
								taskTrackApproval.setDay31(hours);
							}

						}					
						cal.add(Calendar.DATE, 1);				
					}				

					tasktrackApprovalService.save(taskTrackApproval);
				}
			}
			/*****************************************************************************************/
			
			if(overtimeArray.size()>0) {//OverTime

				Calendar cal = Calendar.getInstance();

				int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
				int intMonth = 0,intday = 0;
				cal.setTime(startDate);
				double hours =0;

				if(overtimeId!=null) {
					TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(overtimeId);
					taskTrackApproval.setUpdatedBy(updatedBy);
					taskTrackApproval.setApproved_date(endDate);
					if(taskTrackApproval!=null) {

						for (int i = 0; i < diffInDays; i++) {

							intMonth = (cal.get(Calendar.MONTH) + 1);
							intday = cal.get(Calendar.DAY_OF_MONTH);
							String dateString = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
									+ ((intday < 10) ? "0" + intday : "" + intday);
							
							if(overtimeArray.get(dateString)!=null) {						
								hours = Double.valueOf(overtimeArray.get(dateString).toString());	
												
								if(i==0) {
									taskTrackApproval.setDay1(hours);
								}
								else if(i==1) {
									taskTrackApproval.setDay2(hours);
								}
								else if(i==2) {
									taskTrackApproval.setDay3(hours);
								}
								else if(i==3) {
									taskTrackApproval.setDay4(hours);
								}
								else if(i==4) {
									taskTrackApproval.setDay5(hours);
								}
								else if(i==5) {
									taskTrackApproval.setDay6(hours);
								}
								else if(i==6) {
									taskTrackApproval.setDay7(hours);
								}
								else if(i==7) {
									taskTrackApproval.setDay8(hours);
								}
								else if(i==8) {
									taskTrackApproval.setDay9(hours);
								}
								else if(i==9) {
									taskTrackApproval.setDay10(hours);
								}
								else if(i==10) {
									taskTrackApproval.setDay11(hours);
								}
								else if(i==11) {
									taskTrackApproval.setDay12(hours);
								}
								else if(i==12) {
									taskTrackApproval.setDay13(hours);
								}
								else if(i==13) {
									taskTrackApproval.setDay14(hours);
								}
								else if(i==14) {
									taskTrackApproval.setDay15(hours);
								}
								else if(i==15) {
									taskTrackApproval.setDay16(hours);
								}
								else if(i==16) {
									taskTrackApproval.setDay17(hours);
								}
								else if(i==17) {
									taskTrackApproval.setDay18(hours);
								}
								else if(i==18) {
									taskTrackApproval.setDay19(hours);
								}
								else if(i==19) {
									taskTrackApproval.setDay20(hours);
								}
								else if(i==20) {
									taskTrackApproval.setDay21(hours);
								}
								else if(i==21) {
									taskTrackApproval.setDay22(hours);
								}
								else if(i==22) {
									taskTrackApproval.setDay23(hours);
								}
								else if(i==23) {
									taskTrackApproval.setDay24(hours);
								}
								else if(i==24) {
									taskTrackApproval.setDay25(hours);
								}
								else if(i==25) {
									taskTrackApproval.setDay26(hours);
								}
								else if(i==26) {
									taskTrackApproval.setDay27(hours);
								}
								else if(i==27) {
									taskTrackApproval.setDay28(hours);
								}
								else if(i==28) {
									taskTrackApproval.setDay29(hours);
								}
								else if(i==29) {
									taskTrackApproval.setDay30(hours);
								}
								else if(i==30) {
									taskTrackApproval.setDay31(hours);
								}
							}					
							cal.add(Calendar.DATE, 1);				
						}				
						tasktrackApprovalService.updateData(taskTrackApproval);				
					}
				}
				else {

					TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
					taskTrackApproval.setMonth(month);
					taskTrackApproval.setYear(year);
					taskTrackApproval.setUser(user);
					taskTrackApproval.setProjectType("Overtime");
					taskTrackApproval.setUpdatedBy(updatedBy);
					taskTrackApproval.setApproved_date(endDate);
					taskTrackApproval.setProject(project);
					for (int i = 0; i < diffInDays; i++) {

						intMonth = (cal.get(Calendar.MONTH) + 1);
						intday = cal.get(Calendar.DAY_OF_MONTH);
						String dateString = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);
						

						if(overtimeArray.get(dateString)!=null) {						
							hours = Double.valueOf(overtimeArray.get(dateString).toString());
											
							if(i==0) {
								taskTrackApproval.setDay1(hours);
							}
							else if(i==1) {
								taskTrackApproval.setDay2(hours);
							}
							else if(i==2) {
								taskTrackApproval.setDay3(hours);
							}
							else if(i==3) {
								taskTrackApproval.setDay4(hours);
							}
							else if(i==4) {
								taskTrackApproval.setDay5(hours);
							}
							else if(i==5) {
								taskTrackApproval.setDay6(hours);
							}
							else if(i==6) {
								taskTrackApproval.setDay7(hours);
							}
							else if(i==7) {
								taskTrackApproval.setDay8(hours);
							}
							else if(i==8) {
								taskTrackApproval.setDay9(hours);
							}
							else if(i==9) {
								taskTrackApproval.setDay10(hours);
							}
							else if(i==10) {
								taskTrackApproval.setDay11(hours);
							}
							else if(i==11) {
								taskTrackApproval.setDay12(hours);
							}
							else if(i==12) {
								taskTrackApproval.setDay13(hours);
							}
							else if(i==13) {
								taskTrackApproval.setDay14(hours);
							}
							else if(i==14) {
								taskTrackApproval.setDay15(hours);
							}
							else if(i==15) {
								taskTrackApproval.setDay16(hours);
							}
							else if(i==16) {
								taskTrackApproval.setDay17(hours);
							}
							else if(i==17) {
								taskTrackApproval.setDay18(hours);
							}
							else if(i==18) {
								taskTrackApproval.setDay19(hours);
							}
							else if(i==19) {
								taskTrackApproval.setDay20(hours);
							}
							else if(i==20) {
								taskTrackApproval.setDay21(hours);
							}
							else if(i==21) {
								taskTrackApproval.setDay22(hours);
							}
							else if(i==22) {
								taskTrackApproval.setDay23(hours);
							}
							else if(i==23) {
								taskTrackApproval.setDay24(hours);
							}
							else if(i==24) {
								taskTrackApproval.setDay25(hours);
							}
							else if(i==25) {
								taskTrackApproval.setDay26(hours);
							}
							else if(i==26) {
								taskTrackApproval.setDay27(hours);
							}
							else if(i==27) {
								taskTrackApproval.setDay28(hours);
							}
							else if(i==28) {
								taskTrackApproval.setDay29(hours);
							}
							else if(i==29) {
								taskTrackApproval.setDay30(hours);
							}
							else if(i==30) {
								taskTrackApproval.setDay31(hours);
							}

						}					
						cal.add(Calendar.DATE, 1);				
					}				

					tasktrackApprovalService.save(taskTrackApproval);
				}
			}
			jsonDataRes.put("status", "success");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "successfully saved. ");
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;
	}
	@GetMapping("/getProjectNamesForApproval")
	public JsonNode getProjectNamesForApproval(@RequestParam("uId") Long uId) throws Exception {
		ArrayNode projectTitle = objectMapper.createArrayNode();
		
		UserModel user = userService.getUserDetailsById(uId);

		if(user.getRole().getroleName().equals("FINANCE") || user.getRole().getroleName().equals("ADMIN") ) {//Finance
			for (ProjectModel alloc : tasktrackServiceImpl.getProjectNamesForApproval()) {

				ObjectNode node = objectMapper.createObjectNode();
				node.put("id", alloc.getProjectId());
				node.put("value", alloc.getProjectName());
				projectTitle.add(node);
			}
		}
		else {
			List<Object[]> projectList = null;
			if(user.getRole().getroleName().equals("APPROVER_LEVEL_2")) {
				//System.out.println("_________________________________________APPROVER_LEVEL_2 " +uId );
				projectList = tasktrackRepository.getProjectNamesForApprovalLevel2(uId);
			}
			else if (user.getRole().getroleName().equals("LEAD")) {
				//System.out.println("_________________________________________APPROVER_LEVEL_1 "+uId);
				projectList = tasktrackRepository.getProjectNamesForApprovalLevel1(uId);
			}
			else {
				//System.out.println("_________________________________________Other"+uId);
				projectList = tasktrackRepository.getProjectNamesForApprovalnew(uId);
			}
			
			
			for (Object[] alloc : projectList) {

				ObjectNode node = objectMapper.createObjectNode();
				node.put("id", (Long) alloc[1]);
				node.put("value",(String) alloc[0]);
				projectTitle.add(node);
			}
		}
		ObjectNode dataNode = objectMapper.createObjectNode();
		dataNode.set("projectTitle", projectTitle);

		ObjectNode node = objectMapper.createObjectNode();
		node.put("status", "success");
		node.set("data", dataNode);
		return node;

	}
	private ProjectModel getProjectDetails(Long projectId) {
		// TODO Auto-generated method stub
		//System.out.println("Here____________________________");
		return projectService.getProjectDetails(projectId);
	}
	
	/**
	 * @des the approved datas of level1 populates to level2 table
	 * @param requestdata
	 * @param httpstatus
	 * @return
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/saveApprovedHoursforLevel2")
	public ObjectNode saveApprovedHoursforLevel2(@RequestBody JSONObject requestdata, HttpServletResponse httpstatus) throws ParseException {

		Long projectId = null ;
		Long userId=null;
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		if (requestdata.get("projectId") != null && requestdata.get("projectId") != "") {
			projectId = Long.valueOf(requestdata.get("projectId").toString());
		}
		if (requestdata.get("userId") != null && requestdata.get("userId") != "") {
			userId = Long.valueOf(requestdata.get("userId").toString());
		}
		String date1 = (String) requestdata.get("startDate");
		String date2 = (String) requestdata.get("endDate");

		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		if (!date1.isEmpty()) {
			startDate = outputFormat.parse(date1);
		}
		if (!date2.isEmpty()) {
			endDate = outputFormat.parse(date2);
		}
		try {
			jsonDataRes   = tasktrackApprovalService.saveLevel2FromLevel1(projectId,userId,startDate,endDate);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "successfully saved. ");
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;
	}
	/**
	 * 
	 * @param requestdata
	 * @param httpstatus
	 * @return
	 * @des for getting view data for each user in level2 approval view
	 * @throws ParseException
	 */
	@PostMapping("/getTaskTrackDataByUserIdForLevel2")
	public JSONObject getTaskTrackDataByUserIdForLevel2(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		JSONObject returnJsonData = new JSONObject();
		JSONObject returnJsonDatalevel2 = new JSONObject();
		List<JSONObject> timeTrackJSONData = new ArrayList<>();
		List<JSONObject> approvalJSONData = new ArrayList<>();
		List<JSONObject> jsonArray = new ArrayList<>();
		Long userId=null,projectId = null;
		
		try {

			if (requestdata.get("projectId") != null && requestdata.get("projectId").asText() != "") {
				projectId = requestdata.get("projectId").asLong();
			}
			if (requestdata.get("userId") != null && requestdata.get("userId").asText() != "") {
				userId = requestdata.get("userId").asLong();
			}
			String date1 = requestdata.get("startDate").asText();
			String date2 = requestdata.get("endDate").asText();

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = null, endDate = null;
			if (!date1.isEmpty()) {
				startDate = outputFormat.parse(date1);
			}
			if (!date2.isEmpty()) {
				endDate = outputFormat.parse(date2);
			}

			
			JSONObject jsonDataProjectDetails = new JSONObject();
			
			ProjectModel projectdetails = null;
			
			if(projectId != null) {
				 projectdetails = getProjectDetails(projectId);
				}
			if(projectdetails != null) {
		
				if(projectdetails.getProjectOwner() != null)
				jsonDataProjectDetails.put("approver_level_1",projectdetails.getProjectOwner().getUserId());
				
					
				if(projectdetails.getOnsite_lead() != null)
				jsonDataProjectDetails.put("approver_level_2",projectdetails.getOnsite_lead().getUserId());	
				else
					jsonDataProjectDetails.put("approver_level_2","");	
				//timeTrackJSONData.add(jsonDataProjectDetails);
				
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
			
				int intMonth = 0,intday = 0;
				intMonth = (cal.get(Calendar.MONTH) + 1);
				int yearIndex = cal.get(Calendar.YEAR);
				//System.out.println("Month"+intMonth+"Year"+yearIndex);
				String frowardedDate = "";
				String frowardedDateLevel2 = "";
				 String pattern = "yyyy-MM-dd"; 
				 DateFormat df = new SimpleDateFormat(pattern);
				/*
				 * ArrayList<TaskTrackApproval> level1 =
				 * tasktrackApprovalService.getForwardedDate(projectId,userId,intMonth);
				 * 
				 * 
				 * if(!level1.isEmpty()) {
				 * for(TaskTrackApproval item : level1) { if(item.getForwarded_date()!= null)
				 * fDate = item.getForwarded_date();
				 * System.out.println("ForwardedDates_________"+item.getForwarded_date()); }
				 * 
				 * frowardedDate = df.format(fDate);
				 * System.out.println("ForwardedDates_ String ________"+frowardedDate); }
				 * System.out.println("ForwardedDates_________"+level1);
				 */
				
				  List<Object> level2 = tasktrackApprovalService.getForwardedDateLevel2(projectId,userId,intMonth,yearIndex);
				  
				  if(!level2.isEmpty()) {
				  
					 // System.out.println("forwarded_date"+level2.get(0));
					  if(level2.get(0) != null) {
						  
						  Date fdate = (Date) level2.get(0);
						  frowardedDateLevel2 = df.format(fdate);
					  }
					  else {
						
						 frowardedDateLevel2 = ""; 
					  }
				 }
				 
					
				  List<Object> level1 = tasktrackApprovalService.getForwardedDate(projectId,userId,intMonth,yearIndex);
				  if(!level2.isEmpty()) {
					  
					 // System.out.println("forwarded_date"+level1.get(0));
					  if(level1.get(0) != null) {
						  
						  Date fdate = (Date) level1.get(0);
						  frowardedDate = df.format(fdate);
						 //System.out.println("frowardedDate___________"+frowardedDate);
					  }
					  else {
						
						  frowardedDate = ""; 
					  }
				 }
				jsonDataProjectDetails.put("forwarded_date",frowardedDate);
				jsonDataProjectDetails.put("forwarded_date_finance",frowardedDateLevel2);
			}
			
			List<Object[]> userIdList = null;
			Long count = null;
			
			if (startDate != null && endDate != null ) {
				userIdList = projectAllocationService.getUserIdByProject(projectId);
				
				returnJsonDatalevel2 = getUserDataForApprovalForLevel2(userId, startDate, endDate, jsonDataRes, timeTrackJSONData,approvalJSONData, jsonArray,projectId);
			}
			jsonDataRes.put("data", returnJsonDatalevel2);
			jsonDataRes.put("details",jsonDataProjectDetails);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("message", "success. ");
			jsonDataRes.put("code", httpstatus.getStatus());
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;
	}
	private JSONObject getUserDataForApprovalForLevel2(Long userId, Date startDate, Date endDate, JSONObject jsonDataRes,
			List<JSONObject> timeTrackJSONData,List<JSONObject> approvalJSONData, List<JSONObject> jsonArray,Long projectId) {
		
		JSONObject resultData = new JSONObject();
		List<JSONObject> timeTrackJsonData = new ArrayList<>();
		JSONObject approvalJsonData = new JSONObject();
		JSONObject approvalJsonDataLevel2 = new JSONObject();
		List<TaskTrackApproval>  userList = null;
		Boolean isExist = tasktrackApprovalService.checkIsUserExists(userId);
		//Data From Approval table
		approvalJsonData = getUserDataForApproval(userId, startDate, endDate, jsonDataRes, timeTrackJSONData,approvalJSONData, jsonArray,projectId);
	//	approvalJsonDataLevel2 = tasktrackApprovalService.getApprovedUserTaskDetailsForLevel2(userId, startDate, endDate,userList,jsonArray, approvalJSONData, isExist,projectId);	
		approvalJsonDataLevel2 = tasktrackApprovalService.getApproveddatalevel2(userId, startDate, endDate,userList,jsonArray, approvalJSONData, isExist,projectId);
		//#Commented By Rinu 25-09-2019
		//resultData.put("ApprovedData", approvalJsonData);
		//#New Line By  Rinu 25-09-2019
		resultData.putAll(getUserDataForApproval(userId, startDate, endDate, jsonDataRes, timeTrackJSONData,approvalJSONData, jsonArray,projectId));
		resultData.put("ApprovedData_level2", approvalJsonDataLevel2);
		
		return resultData;
		
	}
	/**
	 * 
	 * @param requestdata
	 * @param httpstatus
	 * @return
	 * @des save approved datas in level2
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/editApprovedHoursLevel2")
	public ObjectNode editApprovedHoursEdit(@RequestBody JSONObject requestdata, HttpServletResponse httpstatus) {
		

		ObjectNode jsonDataRes = objectMapper.createObjectNode();

		try {
			// Obtain the data from request data
			Long billableId =null,nonbillableId=null,beachId=null,overtimeId=null,projectId=null,userId=null,updatedBy=null;
			Integer year = Integer.parseInt((String)requestdata.get("year"));
			Integer month = (Integer) requestdata.get("month");
			if (requestdata.get("projectId") != null && requestdata.get("projectId") != "") {
				projectId = Long.valueOf(requestdata.get("projectId").toString());
			}
			if (requestdata.get("userId") != null && requestdata.get("userId") != "") {
				userId = Long.valueOf(requestdata.get("userId").toString());
			}
			if (requestdata.get("updatedBy") != null && requestdata.get("updatedBy") != "") {
				updatedBy = Long.valueOf(requestdata.get("updatedBy").toString());
			}
			if (requestdata.get("billableId") != null && requestdata.get("billableId") != "") {
				billableId = Long.valueOf(requestdata.get("billableId").toString());
			}
			if (requestdata.get("nonBillableId") != null && requestdata.get("nonBillableId") != "") {
				nonbillableId = Long.valueOf(requestdata.get("nonBillableId").toString());
			}
			if (requestdata.get("beachId") != null && requestdata.get("beachId")!= "") {
				beachId = Long.valueOf(requestdata.get("beachId").toString());
			}
			if (requestdata.get("overtimeId") != null && requestdata.get("overtimeId")!= "") {
				overtimeId = Long.valueOf(requestdata.get("overtimeId").toString());
			}
			String date1 = (String) requestdata.get("startDate");
			String date2 = (String) requestdata.get("endDate");

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = null, endDate = null;
			if (!date1.isEmpty()) {
				startDate = outputFormat.parse(date1);
			}
			if (!date2.isEmpty()) {
				endDate = outputFormat.parse(date2);
			}

			HashMap<String, Object> billableArray = new JSONObject();
			HashMap<String, Object> nonbillableArray = new JSONObject();
			HashMap<String, Object> beachArray = new JSONObject();
			HashMap<String, Object> overtimeArray = new JSONObject();

			UserModel user = userService.getUserDetailsById(userId);
			ProjectModel project = projectService.getProjectId(projectId);

			if (requestdata.get("billable") != null && requestdata.get("billable")!= "") {
				billableArray =(HashMap<String, Object>) (requestdata.get("billable"));
			}
			if (requestdata.get("nonBillable") != null && requestdata.get("nonBillable")!= "") {
				nonbillableArray = (HashMap<String, Object>) requestdata.get("nonBillable");
			}
			if (requestdata.get("beach") != null && requestdata.get("beach")!= "") {
				beachArray = (HashMap<String, Object>) requestdata.get("beach");
			}
			if (requestdata.get("overtime") != null && requestdata.get("overtime")!= "") {
				overtimeArray = (HashMap<String, Object>) requestdata.get("overtime");
			}
			
			if(billableArray.size()>0) {//Billable

				Calendar cal = Calendar.getInstance();

				int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
				int intMonth = 0,intday = 0;
				cal.setTime(startDate);
				double hours =0;

				if(billableId!=null) {
					TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService.findById2(billableId);
					taskTrackApproval.setUpdatedBy(updatedBy);
					taskTrackApproval.setApproved_date(endDate);
					if(taskTrackApproval!=null) {

						for (int i = 0; i < diffInDays; i++) {

							intMonth = (cal.get(Calendar.MONTH) + 1);
							intday = cal.get(Calendar.DAY_OF_MONTH);
							String dateString = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
									+ ((intday < 10) ? "0" + intday : "" + intday);
							
							if(billableArray.get(dateString)!=null) {						
								hours = Double.valueOf(billableArray.get(dateString).toString());	
												
								if(i==0) {
									taskTrackApproval.setDay1(hours);
								}
								else if(i==1) {
									taskTrackApproval.setDay2(hours);
								}
								else if(i==2) {
									taskTrackApproval.setDay3(hours);
								}
								else if(i==3) {
									taskTrackApproval.setDay4(hours);
								}
								else if(i==4) {
									taskTrackApproval.setDay5(hours);
								}
								else if(i==5) {
									taskTrackApproval.setDay6(hours);
								}
								else if(i==6) {
									taskTrackApproval.setDay7(hours);
								}
								else if(i==7) {
									taskTrackApproval.setDay8(hours);
								}
								else if(i==8) {
									taskTrackApproval.setDay9(hours);
								}
								else if(i==9) {
									taskTrackApproval.setDay10(hours);
								}
								else if(i==10) {
									taskTrackApproval.setDay11(hours);
								}
								else if(i==11) {
									taskTrackApproval.setDay12(hours);
								}
								else if(i==12) {
									taskTrackApproval.setDay13(hours);
								}
								else if(i==13) {
									taskTrackApproval.setDay14(hours);
								}
								else if(i==14) {
									taskTrackApproval.setDay15(hours);
								}
								else if(i==15) {
									taskTrackApproval.setDay16(hours);
								}
								else if(i==16) {
									taskTrackApproval.setDay17(hours);
								}
								else if(i==17) {
									taskTrackApproval.setDay18(hours);
								}
								else if(i==18) {
									taskTrackApproval.setDay19(hours);
								}
								else if(i==19) {
									taskTrackApproval.setDay20(hours);
								}
								else if(i==20) {
									taskTrackApproval.setDay21(hours);
								}
								else if(i==21) {
									taskTrackApproval.setDay22(hours);
								}
								else if(i==22) {
									taskTrackApproval.setDay23(hours);
								}
								else if(i==23) {
									taskTrackApproval.setDay24(hours);
								}
								else if(i==24) {
									taskTrackApproval.setDay25(hours);
								}
								else if(i==25) {
									taskTrackApproval.setDay26(hours);
								}
								else if(i==26) {
									taskTrackApproval.setDay27(hours);
								}
								else if(i==27) {
									taskTrackApproval.setDay28(hours);
								}
								else if(i==28) {
									taskTrackApproval.setDay29(hours);
								}
								else if(i==29) {
									taskTrackApproval.setDay30(hours);
								}
								else if(i==30) {
									taskTrackApproval.setDay31(hours);
								}
							}					
							cal.add(Calendar.DATE, 1);				
						}				
						tasktrackApprovalService.updateDatas(taskTrackApproval);				
					}
				}
				
			}

			/**************************************************************/

			if(nonbillableArray.size()>0) {//Non-Billable

				Calendar cal = Calendar.getInstance();

				int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
				int intMonth = 0,intday = 0;
				cal.setTime(startDate);
				double hours =0;

				if(nonbillableId!=null) {
					TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService.findById2(nonbillableId);
				

					taskTrackApproval.setUpdatedBy(updatedBy);
					taskTrackApproval.setApproved_date(endDate);
					if(taskTrackApproval!=null) {

						for (int i = 0; i < diffInDays; i++) {

							intMonth = (cal.get(Calendar.MONTH) + 1);
							intday = cal.get(Calendar.DAY_OF_MONTH);
							String dateString = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
									+ ((intday < 10) ? "0" + intday : "" + intday);
							

							if(nonbillableArray.get(dateString)!=null) {						
								hours = Double.valueOf(nonbillableArray.get(dateString).toString());	
												
								if(i==0) {
									taskTrackApproval.setDay1(hours);
								}
								else if(i==1) {
									taskTrackApproval.setDay2(hours);
								}
								else if(i==2) {
									taskTrackApproval.setDay3(hours);
								}
								else if(i==3) {
									taskTrackApproval.setDay4(hours);
								}
								else if(i==4) {
									taskTrackApproval.setDay5(hours);
								}
								else if(i==5) {
									taskTrackApproval.setDay6(hours);
								}
								else if(i==6) {
									taskTrackApproval.setDay7(hours);
								}
								else if(i==7) {
									taskTrackApproval.setDay8(hours);
								}
								else if(i==8) {
									taskTrackApproval.setDay9(hours);
								}
								else if(i==9) {
									taskTrackApproval.setDay10(hours);
								}
								else if(i==10) {
									taskTrackApproval.setDay11(hours);
								}
								else if(i==11) {
									taskTrackApproval.setDay12(hours);
								}
								else if(i==12) {
									taskTrackApproval.setDay13(hours);
								}
								else if(i==13) {
									taskTrackApproval.setDay14(hours);
								}
								else if(i==14) {
									taskTrackApproval.setDay15(hours);
								}
								else if(i==15) {
									taskTrackApproval.setDay16(hours);
								}
								else if(i==16) {
									taskTrackApproval.setDay17(hours);
								}
								else if(i==17) {
									taskTrackApproval.setDay18(hours);
								}
								else if(i==18) {
									taskTrackApproval.setDay19(hours);
								}
								else if(i==19) {
									taskTrackApproval.setDay20(hours);
								}
								else if(i==20) {
									taskTrackApproval.setDay21(hours);
								}
								else if(i==21) {
									taskTrackApproval.setDay22(hours);
								}
								else if(i==22) {
									taskTrackApproval.setDay23(hours);
								}
								else if(i==23) {
									taskTrackApproval.setDay24(hours);
								}
								else if(i==24) {
									taskTrackApproval.setDay25(hours);
								}
								else if(i==25) {
									taskTrackApproval.setDay26(hours);
								}
								else if(i==26) {
									taskTrackApproval.setDay27(hours);
								}
								else if(i==27) {
									taskTrackApproval.setDay28(hours);
								}
								else if(i==28) {
									taskTrackApproval.setDay29(hours);
								}
								else if(i==29) {
									taskTrackApproval.setDay30(hours);
								}
								else if(i==30) {
									taskTrackApproval.setDay31(hours);
								}

							}					
							cal.add(Calendar.DATE, 1);				
						}				
						tasktrackApprovalService.updateDatas(taskTrackApproval);				
					}
				}
				



			}
			/****************************************************************************************/

			if(beachArray.size()>0) {//Beach

				Calendar cal = Calendar.getInstance();

				int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
				int intMonth = 0,intday = 0;
				cal.setTime(startDate);
				double hours =0;

				if(beachId!=null) {
					TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService.findById2(beachId);
					taskTrackApproval.setUpdatedBy(updatedBy);
					taskTrackApproval.setApproved_date(endDate);
					if(taskTrackApproval!=null) {

						for (int i = 0; i < diffInDays; i++) {

							intMonth = (cal.get(Calendar.MONTH) + 1);
							intday = cal.get(Calendar.DAY_OF_MONTH);
							String dateString = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
									+ ((intday < 10) ? "0" + intday : "" + intday);
							

							if(beachArray.get(dateString)!=null) {						
								hours = Double.valueOf(beachArray.get(dateString).toString());		
												
								if(i==0) {
									taskTrackApproval.setDay1(hours);
								}
								else if(i==1) {
									taskTrackApproval.setDay2(hours);
								}
								else if(i==2) {
									taskTrackApproval.setDay3(hours);
								}
								else if(i==3) {
									taskTrackApproval.setDay4(hours);
								}
								else if(i==4) {
									taskTrackApproval.setDay5(hours);
								}
								else if(i==5) {
									taskTrackApproval.setDay6(hours);
								}
								else if(i==6) {
									taskTrackApproval.setDay7(hours);
								}
								else if(i==7) {
									taskTrackApproval.setDay8(hours);
								}
								else if(i==8) {
									taskTrackApproval.setDay9(hours);
								}
								else if(i==9) {
									taskTrackApproval.setDay10(hours);
								}
								else if(i==10) {
									taskTrackApproval.setDay11(hours);
								}
								else if(i==11) {
									taskTrackApproval.setDay12(hours);
								}
								else if(i==12) {
									taskTrackApproval.setDay13(hours);
								}
								else if(i==13) {
									taskTrackApproval.setDay14(hours);
								}
								else if(i==14) {
									taskTrackApproval.setDay15(hours);
								}
								else if(i==15) {
									taskTrackApproval.setDay16(hours);
								}
								else if(i==16) {
									taskTrackApproval.setDay17(hours);
								}
								else if(i==17) {
									taskTrackApproval.setDay18(hours);
								}
								else if(i==18) {
									taskTrackApproval.setDay19(hours);
								}
								else if(i==19) {
									taskTrackApproval.setDay20(hours);
								}
								else if(i==20) {
									taskTrackApproval.setDay21(hours);
								}
								else if(i==21) {
									taskTrackApproval.setDay22(hours);
								}
								else if(i==22) {
									taskTrackApproval.setDay23(hours);
								}
								else if(i==23) {
									taskTrackApproval.setDay24(hours);
								}
								else if(i==24) {
									taskTrackApproval.setDay25(hours);
								}
								else if(i==25) {
									taskTrackApproval.setDay26(hours);
								}
								else if(i==26) {
									taskTrackApproval.setDay27(hours);
								}
								else if(i==27) {
									taskTrackApproval.setDay28(hours);
								}
								else if(i==28) {
									taskTrackApproval.setDay29(hours);
								}
								else if(i==29) {
									taskTrackApproval.setDay30(hours);
								}
								else if(i==30) {
									taskTrackApproval.setDay31(hours);
								}

							}					
							cal.add(Calendar.DATE, 1);				
						}				
						tasktrackApprovalService.updateDatas(taskTrackApproval);				
					}
				}
			}
			/*****************************************************************************************/
			
			if(overtimeArray.size()>0) {//OverTime

				Calendar cal = Calendar.getInstance();

				int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
				int intMonth = 0,intday = 0;
				cal.setTime(startDate);
				double hours =0;

				if(overtimeId!=null) {
					TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService.findById2(overtimeId);
					taskTrackApproval.setUpdatedBy(updatedBy);
					taskTrackApproval.setApproved_date(endDate);
					if(taskTrackApproval!=null) {

						for (int i = 0; i < diffInDays; i++) {

							intMonth = (cal.get(Calendar.MONTH) + 1);
							intday = cal.get(Calendar.DAY_OF_MONTH);
							String dateString = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
									+ ((intday < 10) ? "0" + intday : "" + intday);
							
							if(overtimeArray.get(dateString)!=null) {						
								hours = Double.valueOf(overtimeArray.get(dateString).toString());	
												
								if(i==0) {
									taskTrackApproval.setDay1(hours);
								}
								else if(i==1) {
									taskTrackApproval.setDay2(hours);
								}
								else if(i==2) {
									taskTrackApproval.setDay3(hours);
								}
								else if(i==3) {
									taskTrackApproval.setDay4(hours);
								}
								else if(i==4) {
									taskTrackApproval.setDay5(hours);
								}
								else if(i==5) {
									taskTrackApproval.setDay6(hours);
								}
								else if(i==6) {
									taskTrackApproval.setDay7(hours);
								}
								else if(i==7) {
									taskTrackApproval.setDay8(hours);
								}
								else if(i==8) {
									taskTrackApproval.setDay9(hours);
								}
								else if(i==9) {
									taskTrackApproval.setDay10(hours);
								}
								else if(i==10) {
									taskTrackApproval.setDay11(hours);
								}
								else if(i==11) {
									taskTrackApproval.setDay12(hours);
								}
								else if(i==12) {
									taskTrackApproval.setDay13(hours);
								}
								else if(i==13) {
									taskTrackApproval.setDay14(hours);
								}
								else if(i==14) {
									taskTrackApproval.setDay15(hours);
								}
								else if(i==15) {
									taskTrackApproval.setDay16(hours);
								}
								else if(i==16) {
									taskTrackApproval.setDay17(hours);
								}
								else if(i==17) {
									taskTrackApproval.setDay18(hours);
								}
								else if(i==18) {
									taskTrackApproval.setDay19(hours);
								}
								else if(i==19) {
									taskTrackApproval.setDay20(hours);
								}
								else if(i==20) {
									taskTrackApproval.setDay21(hours);
								}
								else if(i==21) {
									taskTrackApproval.setDay22(hours);
								}
								else if(i==22) {
									taskTrackApproval.setDay23(hours);
								}
								else if(i==23) {
									taskTrackApproval.setDay24(hours);
								}
								else if(i==24) {
									taskTrackApproval.setDay25(hours);
								}
								else if(i==25) {
									taskTrackApproval.setDay26(hours);
								}
								else if(i==26) {
									taskTrackApproval.setDay27(hours);
								}
								else if(i==27) {
									taskTrackApproval.setDay28(hours);
								}
								else if(i==28) {
									taskTrackApproval.setDay29(hours);
								}
								else if(i==29) {
									taskTrackApproval.setDay30(hours);
								}
								else if(i==30) {
									taskTrackApproval.setDay31(hours);
								}
							}					
							cal.add(Calendar.DATE, 1);				
						}				
						tasktrackApprovalService.updateDatas(taskTrackApproval);				
					}
				}
			}
			jsonDataRes.put("status", "success");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "successfully saved. ");
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;
	}
	/***
	 * @des approved datas from level2 populates to finance table
	 * @param requestdata
	 * @param httpstatus
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/saveApprovedHoursforFinanceFromLevel2")
	public JSONObject saveApprovedHoursforFinance(@RequestBody JSONObject requestdata, HttpServletResponse httpstatus) {

		
		JSONObject jsonDataRes =  new JSONObject();
		JSONObject approvalJsonDataLevel2 = new JSONObject();
		try {
			// Obtain the data from request data
			Long billableId =null,nonbillableId=null,beachId=null,overtimeId=null,projectId=null,userId=null,updatedBy=null;
			//Integer year = Integer.parseInt((String)requestdata.get("year"));
			//Integer month = (Integer) requestdata.get("month");
			
			if (requestdata.get("projectId") != null && requestdata.get("projectId") != "") {
				projectId = Long.valueOf(requestdata.get("projectId").toString());
			}
			if (requestdata.get("userId") != null && requestdata.get("userId") != "") {
				userId = Long.valueOf(requestdata.get("userId").toString());
			}
			//String date1 = "";
			//String date2 = "";
			int monthIndex = 0;
			int yearIndex = 0;
			if (requestdata.get("monthIndex") != null && requestdata.get("monthIndex") != "" 
					&& requestdata.get("yearIndex") != null && requestdata.get("yearIndex") != "") {
			  monthIndex =  (int) requestdata.get("monthIndex");
			  yearIndex =  (int) requestdata.get("yearIndex");
			}
			
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = null, endDate = null;
			/*
			 * if (!date1.isEmpty()) { startDate = outputFormat.parse(date1); } if
			 * (!date2.isEmpty()) { endDate = outputFormat.parse(date2); }
			 */
			/*
			 * Calendar cal1 = Calendar.getInstance(); cal1.setTime(startDate); int month =
			 * (cal1.get(Calendar.MONTH) + 1); int year = cal1.get(Calendar.YEAR);
			 */
			jsonDataRes = tasktrackApprovalService.getApproveddatalevel2toFinance(userId, monthIndex, yearIndex,projectId);
			

			
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;
	}
	
	/***
	 * @des approved datas from level1 populates to finance table
	 * @param requestdata
	 * @param httpstatus
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/saveApprovedHoursforFinanceFromLevel1")
	public JSONObject saveApprovedHoursforFinanceFromLevel1(@RequestBody JSONObject requestdata, HttpServletResponse httpstatus) {

		
		JSONObject jsonDataRes = new JSONObject();
		JSONObject approvalJsonDataLevel2 = new JSONObject();
		try {
			// Obtain the data from request data
			Long billableId =null,nonbillableId=null,beachId=null,overtimeId=null,projectId=null,userId=null,updatedBy=null;
			//Integer year = Integer.parseInt((String)requestdata.get("year"));
			//Integer month = (Integer) requestdata.get("month");
			
			if (requestdata.get("projectId") != null && requestdata.get("projectId") != "") {
				projectId = Long.valueOf(requestdata.get("projectId").toString());
			}
			if (requestdata.get("userId") != null && requestdata.get("userId") != "") {
				userId = Long.valueOf(requestdata.get("userId").toString());
			}
			/*
			 * String date1 = ""; String date2 = "";
			 */
			int monthIndex = 0;
			int yearIndex = 0;
			if (requestdata.get("month") != null && requestdata.get("month") != "" 
					&& requestdata.get("year") != null && requestdata.get("year") != "") {
				monthIndex = (int) requestdata.get("month");
				yearIndex = (int) requestdata.get("year");
			}
			
			/*
			 * SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd"); Date
			 * startDate = null, endDate = null; if (!date1.isEmpty()) { startDate =
			 * outputFormat.parse(date1); } if (!date2.isEmpty()) { endDate =
			 * outputFormat.parse(date2); }
			 */
			/*
			 * Calendar cal1 = Calendar.getInstance(); cal1.setTime(startDate); int month =
			 * (cal1.get(Calendar.MONTH) + 1); int year = cal1.get(Calendar.YEAR);
			 */
			jsonDataRes = tasktrackApprovalService.getApproveddatalevel1toFinance(userId, monthIndex, yearIndex,projectId);
			

			jsonDataRes.put("status", "success");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "successfully saved. ");
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;
	}
	@PostMapping("/getTaskTrackDataLevel2")
	public JSONObject getTaskTrackDataLevel2(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		JSONObject returnJsonData = new JSONObject();
		List<JSONObject> timeTrackJSONData = new ArrayList<>();
		List<JSONObject> loggedJsonArray = new ArrayList<>();
		List<JSONObject> billableJsonArray = new ArrayList<>();
		Long projectId = null;
		
		try {

			if (requestdata.get("projectId") != null && requestdata.get("projectId").asText() != "") {
				projectId = requestdata.get("projectId").asLong();
			}

			String date1 = requestdata.get("startDate").asText();
			String date2 = requestdata.get("endDate").asText();

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = null, endDate = null;
			if (!date1.isEmpty()) {
				startDate = outputFormat.parse(date1);
			}
			if (!date2.isEmpty()) {
				endDate = outputFormat.parse(date2);
			}

			List<TaskTrackApprovalLevel2> userIdList = null;
			
			
			if (startDate != null && endDate != null ) {
				//userIdList = projectAllocationService.getUserIdByProject(projectId);
				//userIdList = projectAllocationService.getUserIdByProjectAndDate(projectId,startDate,endDate);
				userIdList = tasktrackApprovalService.getUserIdByProjectAndDateForLevel2(projectId,startDate,endDate);
				getUserDataForReportLevel(userIdList, startDate, endDate, jsonDataRes, timeTrackJSONData, loggedJsonArray,billableJsonArray,projectId);
			}
			
			jsonDataRes.put("data", timeTrackJSONData);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("message", "success. ");
			jsonDataRes.put("code", httpstatus.getStatus());
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;
	}
	private void getUserDataForReportLevel(List<TaskTrackApprovalLevel2> userIdList, Date startDate, Date endDate, JSONObject jsonDataRes,
			List<JSONObject> timeTrackJSONData, List<JSONObject> loggedJsonArray,List<JSONObject> billableJsonArray,Long projectId) {
		
		JSONObject resultData = new JSONObject();
		List<JSONObject> timeTrackJsonData = new ArrayList<>();
		List<JSONObject> approvalJsonData = new ArrayList<>();
		for (TaskTrackApprovalLevel2 userItem : userIdList) {

			Long id =  userItem.getUser().getUserId();
			List<Object[]> userList = null;
			Boolean isExist = tasktrackApprovalService.checkIsUserExists(id);
			//Data From Time track
			timeTrackJsonData = tasktrackApprovalService.getTimeTrackUserTaskDetailsLevel2(id, startDate, endDate, userList, loggedJsonArray,billableJsonArray, timeTrackJSONData, isExist,projectId);	
		}
		
	}

	@PostMapping("/getFinanceData")
	public JSONObject getFinanceData(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		Long projectId = null;
		Long userId = null;
		int month = 0;
		int year =0;

		try {

			if (requestdata.get("projectId") != null && requestdata.get("projectId").asText() != "") {
				projectId = requestdata.get("projectId").asLong();
			}

			if (requestdata.get("userId") != null && requestdata.get("userId").asText() != "") {
				userId = requestdata.get("userId").asLong();
			}

			ArrayNode range = (ArrayNode) requestdata.get("range");

			JSONObject outputdata = new JSONObject();

			ArrayList<JSONObject> resultData = new ArrayList<JSONObject>();
			ArrayList<JSONObject> node1 = new ArrayList<JSONObject>();

			for (JsonNode rangenode : range) {
				JSONObject node = new JSONObject();
				month = Integer.parseInt(rangenode.get("month").toString());
				year = Integer.parseInt(rangenode.get("year").toString());
				if (month!= 0 && year!= 0 && projectId != null && userId == null) {

					resultData = tasktrackApprovalService.getFinanceDataByProject(month, year, projectId);
					node.put("timeTracks", resultData);
					node.put("month", month);
					node.put("year", year);
					node1.add(node);

				}
				else if (month!= 0 && year!= 0 && projectId == null && userId != null) {

					resultData  = tasktrackApprovalService.getFinanceDataByUser(month, year, userId);
					node.put("timeTracks",resultData);
					node.put("month",month);
					node.put("year",year);
					node1.add(node);
				}
				else if (month!= 0 && year!= 0 && projectId != null && userId != null) {

					resultData  = tasktrackApprovalService.getFinanceDataByUserAndProject(month, year, userId, projectId);
					node.put("timeTracks",resultData);
					node.put("month",month);
					node.put("year",year);
					node1.add(node);

				}

			}


			jsonDataRes.put("data", node1);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("message", "success. ");
			jsonDataRes.put("code", httpstatus.getStatus());
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;
	}

	@PostMapping("/pendingLogCheck")
	public JSONObject checkPreviousTimeSheetsareClosed(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		Long projectId = null;
		Long userId = null;
		int month = 0;
		int year =0;

		try {

			if (requestdata.get("projectId") != null && requestdata.get("projectId").asText() != "") {
				projectId = requestdata.get("projectId").asLong();
			}

			if (requestdata.get("userId") != null && requestdata.get("userId").asText() != "") {
				userId = requestdata.get("userId").asLong();
			}

			if (requestdata.get("month") != null && requestdata.get("month").asText() != "") {
				month = Integer.parseInt(requestdata.get("month").toString());
			}

			if (requestdata.get("year") != null && requestdata.get("year").asText() != "") {
				year = Integer.parseInt(requestdata.get("year").toString());
			}


			String message = "";

				if (month!= 0 && year!= 0 && projectId != null && userId != null) {

					jsonDataRes =	 tasktrackApprovalService.checkPreviousTimeSheetsareClosed(month, year, projectId,userId);

				}

			jsonDataRes.put("code", httpstatus.getStatus());
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;
	}

	@PostMapping("/halfCycleCheck")
	public JSONObject halfCycleCheck(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		Long projectId = null;
		Long approverId = null;
		Long userId = null;
		Date  curDate = null;

		try {

			if (requestdata.get("projectId") != null && requestdata.get("projectId").asText() != "") {
				projectId = requestdata.get("projectId").asLong();
			}

			if (requestdata.get("userId") != null && requestdata.get("userId").asText() != "") {
				userId = requestdata.get("userId").asLong();
			}
			if (requestdata.get("approverId") != null && requestdata.get("approverId").asText() != "") {
				approverId = requestdata.get("approverId").asLong();
			}

			/*if (requestdata.get("month") != null && requestdata.get("month").asText() != "") {
				month = Integer.parseInt(requestdata.get("month").toString());
			}

			if (requestdata.get("year") != null && requestdata.get("year").asText() != "") {
				year = Integer.parseInt(requestdata.get("year").toString());
			}*/
			if(requestdata.get("currentDate") !=null && requestdata.get("currentDate").asText() != "")
			{
				SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
				  curDate =outputFormat.parse(requestdata.get("currentDate").asText());

			}



			if (projectId != null &&  userId !=null && approverId != null ) {

				jsonDataRes =	 tasktrackApprovalService.halfCycleCheck(projectId, userId,approverId,curDate);


			}

			jsonDataRes.put("code", httpstatus.getStatus());
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;
	}
}
