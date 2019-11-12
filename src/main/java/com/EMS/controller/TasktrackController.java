package com.EMS.controller;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.EMS.model.*;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.dto.Taskdetails;
import com.EMS.exceptions.DuplicateEntryException;
import com.EMS.repository.TaskRepository;
import com.EMS.repository.TaskTrackApprovalLevel2Repository;
import com.EMS.service.ProjectAllocationService;
import com.EMS.service.ProjectRegionService;
import com.EMS.service.ProjectService;
import com.EMS.service.RegionService;
import com.EMS.service.TaskTrackFinalService;
import com.EMS.service.TasktrackApprovalService;
import com.EMS.service.TasktrackService;
import com.EMS.service.TasktrackServiceImpl;
import com.EMS.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.EMS.repository.TasktrackRepository;
import com.EMS.repository.TimeTrackApprovalJPARepository;

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

	@Autowired
	TasktrackApprovalService tasktrackApprovalService;

	@Autowired
	TaskTrackFinalService taskTrackFinalService;

	@Autowired
	TaskTrackApprovalLevel2Repository taskTrackApprovalLevel2Repository;

	@Autowired
	TimeTrackApprovalJPARepository timeTrackApprovalJPARepository;

	@Autowired
	private ProjectRegionService projectRegionService;

	@Autowired
	private ProjectService projectservice;

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
		String currentyear = requestData.get("currentyear").asText();
		int uId = requestData.get("uid").asInt();
		/*
		 * Calendar cal = Calendar.getInstance(); int lastDate =
		 * cal.getActualMaximum(Calendar.DATE); return lastDate;
		 */
		String fromdate = currentyear + "-" + currentmonth + "-01";
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
		LocalDate date = LocalDate.parse(fromdate, dateFormat);
		ValueRange range = date.range(ChronoField.DAY_OF_MONTH);
		Long max = range.getMaximum();
		String todate = String.format("%s-%s-%d", currentyear, currentmonth, max);
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startdate = outputFormat.parse(fromdate);
		Date enddate = outputFormat.parse(todate);
		// LocalDate newDate = date.withDayOfMonth(max.intValue());
		// return enddate;
		ArrayNode projectTitle = objectMapper.createArrayNode();
		try {
			for (Object[] alloc : tasktrackRepository.getProjectNamesByMonths(uId, startdate, enddate)) {

				ObjectNode node = objectMapper.createObjectNode();
				node.put("id", (Long) alloc[0]);
				node.put("value", (String) alloc[1]);
				projectTitle.add(node);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			dataResponse.put("message", "Exception : " + e);
			System.out.println("Exception " + e);
		}

		return dataResponse;
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/getTaskTrackData")
	public JSONObject getTaskTrackData(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus)
			throws ParseException {

		JSONObject response = new JSONObject();
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
			Integer firstHalfDay = 15;//Month split from day 15
			JSONArray taskTrackArray = new JSONArray();
			if (startDate != null && endDate != null) {
				Integer projectTier = requestdata.get("projectTier").asInt();
				List<Object[]> userIdList = projectAllocationService.getUserIdByProjectAndDate(projectId, startDate, endDate);
				taskTrackArray = getUserDataForReport(userIdList, startDate, endDate, projectId,projectTier,firstHalfDay);
			}

			response.put("data", taskTrackArray);
			response.put("status", "success");
			response.put("message", "success. ");
			response.put("code", httpstatus.getStatus());
		} 
		catch (Exception e) {
			response.put("status", "failure");
			response.put("code", httpstatus.getStatus());
			response.put("message", "failed. " + e);
		}
		return response;
	}

	@PostMapping("/getTaskTrackDataByProjectorUser")
	public JSONObject getTaskTrackDataByProjectorUser(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus)
			throws ParseException {

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
				// userIdList =
				// projectAllocationService.getUserIdByProject(projectId);
				userIdList = projectAllocationService.getUserIdByProjectAndDate(projectId, startDate, endDate);
				getUserDataForReportByProject(userIdList, startDate, endDate, jsonDataRes, timeTrackJSONData,
						loggedJsonArray, billableJsonArray, nonBillableJsonArray, projectId);
			} else if (startDate != null && endDate != null && projectId == null && userId != null) {
				// userIdList =
				// projectAllocationService.getUserIdByProject(projectId);
				// projectIdList =
				// projectAllocationService.getProjectIdByUserAndDate(userId,startDate,endDate);
				projectList = projectAllocationService.getProjectListByUserAndDate(userId, startDate, endDate);
				getProjectDataForReport(projectList, startDate, endDate, jsonDataRes, timeTrackJSONData,
						loggedJsonArray, billableJsonArray, nonBillableJsonArray, userId);
			} else if (startDate != null && endDate != null && projectId != null && userId != null) {
				// userIdList =
				// projectAllocationService.getUserIdByProject(projectId);
				// List<Object[]> projectList = null;
				String projectName = null;
				Boolean isExist = tasktrackApprovalService.checkIsUserExists(userId);
				// Data From Time track
				projectList = taskRepository.getUserListByProject(userId, startDate, endDate, projectId);
				if (projectList != null && projectList.size() > 0) {
					timeTrackJsonData = tasktrackApprovalService.getTimeTrackUserProjectTaskDetails(projectId,
							projectName, startDate, endDate, projectList, loggedJsonArray, billableJsonArray,
							nonBillableJsonArray, timeTrackJSONData, isExist, userId);
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

	@SuppressWarnings("unchecked")
	private JSONArray getUserDataForReport(List<Object[]> userIdList, Date startDate, Date endDate,
		Long projectId, Integer projectTier, Integer firstHalfDay) throws ParseException {
		
		JSONArray taskTrackArray = new JSONArray();
		for (Object userItem : userIdList) {
			Long userId = (Long) userItem;
			Boolean isExist = tasktrackApprovalService.checkIsUserExists(userId);
			JSONObject taskTrackObject = tasktrackApprovalService.
					getTimeTrackUserTaskDetails(userId, startDate, endDate, isExist,projectId, projectTier,firstHalfDay);
			taskTrackArray.add(taskTrackObject);
		}
		return taskTrackArray;
	}

	private void getUserDataForReportByProjectandUser(List<Object[]> userIdList, Date startDate, Date endDate,
			JSONObject jsonDataRes, List<JSONObject> timeTrackJSONData, List<JSONObject> loggedJsonArray,
			List<JSONObject> billableJsonArray, List<JSONObject> nonBillableJsonArray, Long projectId) {

		JSONObject resultData = new JSONObject();
		List<JSONObject> timeTrackJsonData = new ArrayList<>();
		List<JSONObject> approvalJsonData = new ArrayList<>();
		for (Object userItem : userIdList) {

			Long id = (Long) userItem;
			List<Object[]> userList = null;
			Boolean isExist = tasktrackApprovalService.checkIsUserExists(id);
			// Data From Time trackgetTaskTrackDataByUserId
			timeTrackJsonData = tasktrackApprovalService.getTimeTrackUserTaskDetailsByProject(id, startDate, endDate,
					userList, loggedJsonArray, billableJsonArray, nonBillableJsonArray, timeTrackJSONData, isExist,
					projectId);
		}

	}

	private void getUserDataForReportByProject(List<Object[]> userIdList, Date startDate, Date endDate,
			JSONObject jsonDataRes, List<JSONObject> timeTrackJSONData, List<JSONObject> loggedJsonArray,
			List<JSONObject> billableJsonArray, List<JSONObject> nonBillableJsonArray, Long projectId) {

		JSONObject resultData = new JSONObject();
		List<JSONObject> timeTrackJsonData = new ArrayList<>();
		List<JSONObject> approvalJsonData = new ArrayList<>();
		for (Object userItem : userIdList) {

			Long id = (Long) userItem;
			List<Object[]> userList = null;
			Boolean isExist = tasktrackApprovalService.checkIsUserExists(id);
			// Data From Time track
			timeTrackJsonData = tasktrackApprovalService.getTimeTrackUserTaskDetailsByProject(id, startDate, endDate,
					userList, loggedJsonArray, billableJsonArray, nonBillableJsonArray, timeTrackJSONData, isExist,
					projectId);
		}

	}

	private void getProjectDataForReport(List<Object[]> projectIdList, Date startDate, Date endDate,
			JSONObject jsonDataRes, List<JSONObject> timeTrackJSONData, List<JSONObject> loggedJsonArray,
			List<JSONObject> billableJsonArray, List<JSONObject> nonBillableJsonArray, Long userId) {

		JSONObject resultData = new JSONObject();
		List<JSONObject> timeTrackJsonData = new ArrayList<>();
		List<JSONObject> approvalJsonData = new ArrayList<>();
		Long projectId = null;
		String projectName = null;
		for (Object[] projectItem : projectIdList) {

			projectId = ((BigInteger) projectItem[0]).longValue();
			projectName = (String) projectItem[1];
			// System.out.println(projectId);
			// System.out.println(projectName);
			List<Object[]> projectList = null;
			Boolean isExist = tasktrackApprovalService.checkIsUserExists(userId);
			// Data From Time track
			timeTrackJsonData = tasktrackApprovalService.getTimeTrackUserProjectTaskDetails(projectId, projectName,
					startDate, endDate, projectList, loggedJsonArray, billableJsonArray, nonBillableJsonArray,
					timeTrackJSONData, isExist, userId);
		}

	}

	@SuppressWarnings("unchecked")
	@PostMapping("/getTaskTrackDataByUserId")
	public JSONObject getTaskTrackDataByUserId(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus)
			throws ParseException {

		JSONObject response = new JSONObject();
		try {
			Long userId = null;
			Long projectId = null;
			if (requestdata.get("projectId") != null && requestdata.get("projectId").asText() != "") {
				projectId = requestdata.get("projectId").asLong();
			}
			if (requestdata.get("userId") != null && requestdata.get("userId").asText() != "") {
				userId = requestdata.get("userId").asLong();
			}
			String date1 = requestdata.get("startDate").asText();
			String date2 = requestdata.get("endDate").asText();

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = null;
			Date endDate = null;
			if (!date1.isEmpty() && !date2.isEmpty()) {
				startDate = outputFormat.parse(date1);
				endDate = outputFormat.parse(date2);
			}
			
			Integer firstHalfDay = 15;//Month split from day 15
			JSONObject approvaldata = new JSONObject();
			if(startDate != null && endDate != null) {
				Integer projectTier = requestdata.get("projectTier").asInt();
				approvaldata = getUserDataForApproval(userId, startDate, endDate, null, null, null, null, projectId, projectTier,firstHalfDay);
			}
			response.put("data", approvaldata);
			response.put("status", "success");
			response.put("message", "success. ");
			response.put("code", httpstatus.getStatus());
		} catch (Exception e) {
			response.put("status", "failure");
			response.put("code", httpstatus.getStatus());
			response.put("message", "failed. " + e);
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getUserDataForApproval(Long userId, Date startDate, Date endDate, JSONObject jsonDataRes,
			List<JSONObject> timeTrackJSONData, List<JSONObject> approvalJSONData, List<JSONObject> jsonArray,
			Long projectId, Integer projectTier, Integer firstHalfDay) {
		
		JSONObject response = new JSONObject();
		Boolean isExist = tasktrackApprovalService.checkIsUserExists(userId);
		JSONObject approvalData = tasktrackApprovalService.getApprovedUserTaskDetails(userId, startDate, endDate,isExist, projectId, projectTier,firstHalfDay);
		response.put("ApprovedData", approvalData);
		return response;
	}

	/**
	 * @author sreejith.j
	 * 
	 * @param requestData
	 * @param httpstatus
	 * @return
	 */
	@PostMapping("/saveApprovedHours")
	public ResponseEntity<Object> saveApprovedHours(@RequestBody JSONObject requestData,
			HttpServletResponse httpstatus) {
		ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		try {
			tasktrackApprovalService.saveApprovedHours(requestData);
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.OK);
		} catch (DuplicateEntryException e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Failure");
			jsonDataRes.put("code", HttpServletResponse.SC_BAD_REQUEST);
			jsonDataRes.put("message", "failed. " + e);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Failure");
			jsonDataRes.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			jsonDataRes.put("message", "failed. " + e);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	/**
	 * @author sreejith.j
	 * 
	 * @param requestData
	 * @param httpstatus
	 * @return
	 */
	@PostMapping("/submitFirstHalfHoursForApproval")
	public ResponseEntity<Object> submitFirstHalfHoursForApproval(@RequestBody JSONObject requestData,
			HttpServletResponse httpstatus) {
		ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
		ObjectNode jsonDataRes = objectMapper.createObjectNode();

		try {
			tasktrackApprovalService.submitFirstHalfHoursForApproval(requestData);
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.OK);
		} catch (DuplicateEntryException e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Failure");
			jsonDataRes.put("code", HttpServletResponse.SC_BAD_REQUEST);
			jsonDataRes.put("message", "failed. " + e);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Failure");
			jsonDataRes.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			jsonDataRes.put("message", "failed. " + e);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	/**
	 * @author sreejith.j
	 * 
	 * @param requestData
	 * @param httpstatus
	 * @return
	 */
	@PostMapping("/submitSecondHalfHoursForApproval")
	public ResponseEntity<Object> submitSecondHalfHoursForApproval(@RequestBody JSONObject requestData,
			HttpServletResponse httpstatus) {
		ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
		ObjectNode jsonDataRes = objectMapper.createObjectNode();

		try {
			tasktrackApprovalService.submitSecondHalfHoursForApproval(requestData);
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.OK);
		} catch (DuplicateEntryException e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Failure");
			jsonDataRes.put("code", HttpServletResponse.SC_BAD_REQUEST);
			jsonDataRes.put("message", "failed. " + e);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Failure");
			jsonDataRes.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			jsonDataRes.put("message", "failed. " + e);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	/**
	 * @author sreejith.j
	 * 
	 * @param requestData
	 * @param httpstatus
	 * @return
	 */
	@PostMapping("/saveFinalHours")
	public ResponseEntity<Object> saveFinalHours(@RequestBody JSONObject requestData, HttpServletResponse httpstatus) {
		ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		try {
			taskTrackFinalService.saveFinalHours(requestData);
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.OK);
		} catch (DuplicateEntryException e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Failure");
			jsonDataRes.put("code", HttpServletResponse.SC_BAD_REQUEST);
			jsonDataRes.put("message", "failed. " + e);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Failure");
			jsonDataRes.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			jsonDataRes.put("message", "failed. " + e);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	/**
	 * @author sreejith.j
	 * 
	 * @param requestData
	 * @param httpstatus
	 * @return
	 */
	@PostMapping("/submitFirstHalfHoursAsFinal")
	public ResponseEntity<Object> submitFirstHalfHoursAsFinal(@RequestBody JSONObject requestData,
			HttpServletResponse httpstatus) {
		ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
		ObjectNode jsonDataRes = objectMapper.createObjectNode();

		try {
			taskTrackFinalService.submitFirstHalfHoursAsFinal(requestData);
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.OK);
		} catch (DuplicateEntryException e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Failure");
			jsonDataRes.put("code", HttpServletResponse.SC_BAD_REQUEST);
			jsonDataRes.put("message", "failed. " + e);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Failure");
			jsonDataRes.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			jsonDataRes.put("message", "failed. " + e);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	/**
	 * @author sreejith.j
	 * 
	 * @param requestData
	 * @param httpstatus
	 * @return
	 */
	@PostMapping("/submitSecondHalfHoursAsFinal")
	public ResponseEntity<Object> submitSecondHalfHoursAsFinal(@RequestBody JSONObject requestData,
			HttpServletResponse httpstatus) {
		ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
		ObjectNode jsonDataRes = objectMapper.createObjectNode();

		try {
			taskTrackFinalService.submitSecondHalfHoursAsFinal(requestData);
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.OK);
		} catch (DuplicateEntryException e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Failure");
			jsonDataRes.put("code", HttpServletResponse.SC_BAD_REQUEST);
			jsonDataRes.put("message", "failed. " + e);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Failure");
			jsonDataRes.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			jsonDataRes.put("message", "failed. " + e);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	@PostMapping("/rejectFirstHalfHours")
	public ObjectNode rejectFirstHalfHours(@RequestBody JSONObject requestData,
											HttpServletResponse httpstatus) {

		ObjectNode jsonDataRes = objectMapper.createObjectNode();

		try {
			tasktrackApprovalService.submitForRejection(requestData);
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Failure");
			jsonDataRes.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;
	}
	@PostMapping("/rejectSecondHalfHours")
	public ObjectNode rejectSecondHalfHours(@RequestBody JSONObject requestData,
										   HttpServletResponse httpstatus) {

		ObjectNode jsonDataRes = objectMapper.createObjectNode();

		try {
			tasktrackApprovalService.submitForSecondHalfRejection(requestData);
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Failure");
			jsonDataRes.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;
	}

	@GetMapping("/getProjectNamesForApproval")
	public JsonNode getProjectNamesForApproval(@RequestParam("uId") Long uId) throws Exception {
		ArrayNode projectTitle = objectMapper.createArrayNode();

		UserModel user = userService.getUserDetailsById(uId);

		if (user.getRole().getroleName().equals("FINANCE") || user.getRole().getroleName().equals("ADMIN")) {// Finance
			for (ProjectModel alloc : tasktrackServiceImpl.getProjectNamesForApproval()) {

				ObjectNode node = objectMapper.createObjectNode();
				node.put("id", alloc.getProjectId());
				node.put("value", alloc.getProjectName());
				node.put("tier", alloc.getProjectTier());
				// get region list
				List<ProjectRegion> regions = projectservice.getregionlist(alloc.getProjectId());
				ArrayNode regionsArray = objectMapper.createArrayNode();
				ArrayList<Integer> regionArraylist = new ArrayList<Integer>();
				if (regions.isEmpty()) {
					node.set("projectRegion", regionsArray);
				} else {

					for (ProjectRegion regioneach : regions) {
						ObjectNode resource = objectMapper.createObjectNode();
						regionsArray.add((int) regioneach.getRegion_Id().getId());

					}
					node.set("projectRegion", regionsArray);
				}
				//

				projectTitle.add(node);
			}
		} else {
			List<Object[]> projectList = null;
			if (user.getRole().getroleId() == 7) {
				// System.out.println("_________________________________________APPROVER_LEVEL_2
				// " +uId );
				projectList = tasktrackRepository.getProjectNamesForApprovalLevel2(uId);
			} else if (user.getRole().getroleId() == 2) {
				// System.out.println("_________________________________________APPROVER_LEVEL_1/Lead
				// "+uId);
				projectList = tasktrackRepository.getProjectNamesForApprovalLevel1(uId);
			} else if (user.getRole().getroleId() == 11) {
				projectList = tasktrackRepository.getProjectNamesForApproverOnly(uId);
			}
			// Renjith
			else if (user.getRole().getroleId() == 5) {
				// System.out.println("_________________________________________Sub Admin
				// "+uId);
				projectList = projectRegionService.getObjProjectsByRegionId(user.getRegion().getId());
			}

			else {
				System.out.println("_________________________________________Other" + uId);
				projectList = tasktrackRepository.getProjectNamesForApprovalnew(uId);
			}

			for (Object[] alloc : projectList) {
				try {
					ObjectNode node = objectMapper.createObjectNode();
					node.put("id", (Long) alloc[1]);
					node.put("value", (String) alloc[0]);
					node.put("tier", (Integer) alloc[2]);
					// get region list
					List<ProjectRegion> regions = projectservice.getregionlist((Long) alloc[1]);
					ArrayNode regionsArray = objectMapper.createArrayNode();
					ArrayList<Integer> regionArraylist = new ArrayList<Integer>();
					if (regions.isEmpty()) {
						node.set("projectRegion", regionsArray);
					} else {

						for (ProjectRegion regioneach : regions) {
							ObjectNode resource = objectMapper.createObjectNode();
							regionsArray.add((int) regioneach.getRegion_Id().getId());

						}
						node.set("projectRegion", regionsArray);
					}
					//
					projectTitle.add(node);
				} catch (Exception e) {
					e.printStackTrace();
				}

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
		// System.out.println("Here____________________________");
		return projectService.getProjectDetails(projectId);
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
	public JSONObject getTaskTrackDataByUserIdForLevel2(@RequestBody JsonNode requestdata,
			HttpServletResponse httpstatus) throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		JSONObject returnJsonData = new JSONObject();
		JSONObject returnJsonDatalevel2 = new JSONObject();
		JSONObject jsonDataMessageDetails = new JSONObject();
		List<JSONObject> timeTrackJSONData = new ArrayList<>();
		List<JSONObject> approvalJSONData = new ArrayList<>();
		List<JSONObject> jsonArray = new ArrayList<>();
		Long userId = null, projectId = null;

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

			if (projectId != null) {
				projectdetails = getProjectDetails(projectId);
			}
			if (projectdetails != null) {

				if (projectdetails.getProjectOwner() != null)
					jsonDataProjectDetails.put("approver_level_1", projectdetails.getProjectOwner().getUserId());

				if (projectdetails.getOnsite_lead() != null)
					jsonDataProjectDetails.put("approver_level_2", projectdetails.getOnsite_lead().getUserId());
				else
					jsonDataProjectDetails.put("approver_level_2", "");
				// timeTrackJSONData.add(jsonDataProjectDetails);

				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);

				int intMonth = 0, intday = 0;
				intMonth = (cal.get(Calendar.MONTH) + 1);
				int yearIndex = cal.get(Calendar.YEAR);
				// System.out.println("Month"+intMonth+"Year"+yearIndex);
				String frowardedDate = "";
				String frowardedDateLevel2 = "";
				String pattern = "yyyy-MM-dd";
				DateFormat df = new SimpleDateFormat(pattern);
				/*
				 * ArrayList<TaskTrackApproval> level1 =
				 * tasktrackApprovalService.getForwardedDate(projectId,userId, intMonth);
				 * 
				 * 
				 * if(!level1.isEmpty()) { for(TaskTrackApproval item : level1) {
				 * if(item.getForwarded_date()!= null) fDate = item.getForwarded_date();
				 * System.out.println("ForwardedDates_________"+item. getForwarded_date()); }
				 * 
				 * frowardedDate = df.format(fDate);
				 * System.out.println("ForwardedDates_ String ________" +frowardedDate); }
				 * System.out.println("ForwardedDates_________"+level1);
				 */

				// getb finance status of the current project added on 11/10

				String finance_status_message = "Timesheet not yet submitted to finance";
				Object[] finance_status = tasktrackApprovalService.getFinanceStatusOfCurrentProject(projectId, userId,
						intMonth, yearIndex);

				if (finance_status != null) {

					if (finance_status.length > 0) {

						if (finance_status[0].equals("HM")) {

							finance_status_message = "Submitted mid report";
						} else if (finance_status[0].equals("FM")) {

							finance_status_message = "Submitted Final report";
						}

					}
				}
				jsonDataMessageDetails.put("Status", finance_status_message);
				//
				List<Object> level2 = tasktrackApprovalService.getForwardedDateLevel2(projectId, userId, intMonth,
						yearIndex);

				if (!level2.isEmpty()) {

					// System.out.println("forwarded_date"+level2.get(0));
					if (level2.get(0) != null) {

						Date fdate = (Date) level2.get(0);
						frowardedDateLevel2 = df.format(fdate);
					} else {

						frowardedDateLevel2 = "";
					}
				}

				List<Object> level1 = tasktrackApprovalService.getForwardedDate(projectId, userId, intMonth, yearIndex);
				if (!level2.isEmpty()) {

					// System.out.println("forwarded_date"+level1.get(0));
					if (level1.get(0) != null) {

						Date fdate = (Date) level1.get(0);
						frowardedDate = df.format(fdate);
						// System.out.println("frowardedDate___________"+frowardedDate);
					} else {

						frowardedDate = "";
					}
				}
				jsonDataProjectDetails.put("forwarded_date", frowardedDate);
				jsonDataProjectDetails.put("forwarded_date_finance", frowardedDateLevel2);
			}

			List<Object[]> userIdList = null;
			Long count = null;

			if (startDate != null && endDate != null) {
				userIdList = projectAllocationService.getUserIdByProject(projectId);

				returnJsonDatalevel2 = getUserDataForApprovalForLevel2(userId, startDate, endDate, jsonDataRes,
						timeTrackJSONData, approvalJSONData, jsonArray, projectId);
			}
			jsonDataRes.put("data", returnJsonDatalevel2);
			jsonDataRes.put("details", jsonDataProjectDetails);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("message", "success. ");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message_details", jsonDataMessageDetails);
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;
	}

	private JSONObject getUserDataForApprovalForLevel2(Long userId, Date startDate, Date endDate,
			JSONObject jsonDataRes, List<JSONObject> timeTrackJSONData, List<JSONObject> approvalJSONData,
			List<JSONObject> jsonArray, Long projectId) {

		JSONObject resultData = new JSONObject();
		List<JSONObject> timeTrackJsonData = new ArrayList<>();
		JSONObject approvalJsonData = new JSONObject();
		JSONObject approvalJsonDataLevel2 = new JSONObject();
		List<TaskTrackApproval> userList = null;
		Boolean isExist = tasktrackApprovalService.checkIsUserExists(userId);
		// Data From Approval table
		approvalJsonData = getUserDataForApproval(userId, startDate, endDate, jsonDataRes, timeTrackJSONData,
				approvalJSONData, jsonArray, projectId,null,null);
		// approvalJsonDataLevel2 =
		// tasktrackApprovalService.getApprovedUserTaskDetailsForLevel2(userId,
		// startDate, endDate,userList,jsonArray, approvalJSONData,
		// isExist,projectId);
		approvalJsonDataLevel2 = tasktrackApprovalService.getApproveddatalevel2(userId, startDate, endDate, userList,
				jsonArray, approvalJSONData, isExist, projectId);
		// #Commented By Rinu 25-09-2019
		// resultData.put("ApprovedData", approvalJsonData);
		// #New Line By Rinu 25-09-2019
		resultData.putAll(getUserDataForApproval(userId, startDate, endDate, jsonDataRes, timeTrackJSONData,
				approvalJSONData, jsonArray, projectId,null,null));
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
		ObjectNode ids = objectMapper.createObjectNode();
		ObjectNode jsonDataMessageDetails = objectMapper.createObjectNode();
		boolean timesheet_button = false;
		try {
			// Obtain the data from request data
			Long billableId = null, nonbillableId = null, beachId = null, overtimeId = null, projectId = null,
					userId = null, updatedBy = null;
			Integer year = Integer.parseInt((String) requestdata.get("year"));
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
			if (requestdata.get("beachId") != null && requestdata.get("beachId") != "") {
				beachId = Long.valueOf(requestdata.get("beachId").toString());
			}
			if (requestdata.get("overtimeId") != null && requestdata.get("overtimeId") != "") {
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

			if (requestdata.get("billable") != null && requestdata.get("billable") != "") {
				billableArray = (HashMap<String, Object>) (requestdata.get("billable"));
			}
			if (requestdata.get("nonBillable") != null && requestdata.get("nonBillable") != "") {
				nonbillableArray = (HashMap<String, Object>) requestdata.get("nonBillable");
			}
			if (requestdata.get("beach") != null && requestdata.get("beach") != "") {
				beachArray = (HashMap<String, Object>) requestdata.get("beach");
			}
			if (requestdata.get("overtime") != null && requestdata.get("overtime") != "") {
				overtimeArray = (HashMap<String, Object>) requestdata.get("overtime");
			}

			if (billableArray.size() > 0) {// Billable

				Calendar cal = Calendar.getInstance();

				int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
				int intMonth = 0, intday = 0;
				cal.setTime(startDate);
				double hours = 0;

				if (billableId != null) {
					TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService.findById2(billableId);
					taskTrackApproval.setUpdatedBy(updatedBy);
					taskTrackApproval.setApproved_date(endDate);
					if (taskTrackApproval != null) {

						for (int i = 0; i < diffInDays; i++) {

							intMonth = (cal.get(Calendar.MONTH) + 1);
							intday = cal.get(Calendar.DAY_OF_MONTH);
							String dateString = cal.get(Calendar.YEAR) + "-"
									+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
									+ ((intday < 10) ? "0" + intday : "" + intday);

							if (billableArray.get(dateString) != null) {
								hours = Double.valueOf(billableArray.get(dateString).toString());

								if (i == 0) {
									taskTrackApproval.setDay1(hours);
								} else if (i == 1) {
									taskTrackApproval.setDay2(hours);
								} else if (i == 2) {
									taskTrackApproval.setDay3(hours);
								} else if (i == 3) {
									taskTrackApproval.setDay4(hours);
								} else if (i == 4) {
									taskTrackApproval.setDay5(hours);
								} else if (i == 5) {
									taskTrackApproval.setDay6(hours);
								} else if (i == 6) {
									taskTrackApproval.setDay7(hours);
								} else if (i == 7) {
									taskTrackApproval.setDay8(hours);
								} else if (i == 8) {
									taskTrackApproval.setDay9(hours);
								} else if (i == 9) {
									taskTrackApproval.setDay10(hours);
								} else if (i == 10) {
									taskTrackApproval.setDay11(hours);
								} else if (i == 11) {
									taskTrackApproval.setDay12(hours);
								} else if (i == 12) {
									taskTrackApproval.setDay13(hours);
								} else if (i == 13) {
									taskTrackApproval.setDay14(hours);
								} else if (i == 14) {
									taskTrackApproval.setDay15(hours);
								} else if (i == 15) {
									taskTrackApproval.setDay16(hours);
								} else if (i == 16) {
									taskTrackApproval.setDay17(hours);
								} else if (i == 17) {
									taskTrackApproval.setDay18(hours);
								} else if (i == 18) {
									taskTrackApproval.setDay19(hours);
								} else if (i == 19) {
									taskTrackApproval.setDay20(hours);
								} else if (i == 20) {
									taskTrackApproval.setDay21(hours);
								} else if (i == 21) {
									taskTrackApproval.setDay22(hours);
								} else if (i == 22) {
									taskTrackApproval.setDay23(hours);
								} else if (i == 23) {
									taskTrackApproval.setDay24(hours);
								} else if (i == 24) {
									taskTrackApproval.setDay25(hours);
								} else if (i == 25) {
									taskTrackApproval.setDay26(hours);
								} else if (i == 26) {
									taskTrackApproval.setDay27(hours);
								} else if (i == 27) {
									taskTrackApproval.setDay28(hours);
								} else if (i == 28) {
									taskTrackApproval.setDay29(hours);
								} else if (i == 29) {
									taskTrackApproval.setDay30(hours);
								} else if (i == 30) {
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

			if (nonbillableArray.size() > 0) {// Non-Billable

				Calendar cal = Calendar.getInstance();

				int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
				int intMonth = 0, intday = 0;
				cal.setTime(startDate);
				double hours = 0;

				if (nonbillableId != null) {
					TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService.findById2(nonbillableId);

					taskTrackApproval.setUpdatedBy(updatedBy);
					taskTrackApproval.setApproved_date(endDate);
					if (taskTrackApproval != null) {

						for (int i = 0; i < diffInDays; i++) {

							intMonth = (cal.get(Calendar.MONTH) + 1);
							intday = cal.get(Calendar.DAY_OF_MONTH);
							String dateString = cal.get(Calendar.YEAR) + "-"
									+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
									+ ((intday < 10) ? "0" + intday : "" + intday);

							if (nonbillableArray.get(dateString) != null) {
								hours = Double.valueOf(nonbillableArray.get(dateString).toString());

								if (i == 0) {
									taskTrackApproval.setDay1(hours);
								} else if (i == 1) {
									taskTrackApproval.setDay2(hours);
								} else if (i == 2) {
									taskTrackApproval.setDay3(hours);
								} else if (i == 3) {
									taskTrackApproval.setDay4(hours);
								} else if (i == 4) {
									taskTrackApproval.setDay5(hours);
								} else if (i == 5) {
									taskTrackApproval.setDay6(hours);
								} else if (i == 6) {
									taskTrackApproval.setDay7(hours);
								} else if (i == 7) {
									taskTrackApproval.setDay8(hours);
								} else if (i == 8) {
									taskTrackApproval.setDay9(hours);
								} else if (i == 9) {
									taskTrackApproval.setDay10(hours);
								} else if (i == 10) {
									taskTrackApproval.setDay11(hours);
								} else if (i == 11) {
									taskTrackApproval.setDay12(hours);
								} else if (i == 12) {
									taskTrackApproval.setDay13(hours);
								} else if (i == 13) {
									taskTrackApproval.setDay14(hours);
								} else if (i == 14) {
									taskTrackApproval.setDay15(hours);
								} else if (i == 15) {
									taskTrackApproval.setDay16(hours);
								} else if (i == 16) {
									taskTrackApproval.setDay17(hours);
								} else if (i == 17) {
									taskTrackApproval.setDay18(hours);
								} else if (i == 18) {
									taskTrackApproval.setDay19(hours);
								} else if (i == 19) {
									taskTrackApproval.setDay20(hours);
								} else if (i == 20) {
									taskTrackApproval.setDay21(hours);
								} else if (i == 21) {
									taskTrackApproval.setDay22(hours);
								} else if (i == 22) {
									taskTrackApproval.setDay23(hours);
								} else if (i == 23) {
									taskTrackApproval.setDay24(hours);
								} else if (i == 24) {
									taskTrackApproval.setDay25(hours);
								} else if (i == 25) {
									taskTrackApproval.setDay26(hours);
								} else if (i == 26) {
									taskTrackApproval.setDay27(hours);
								} else if (i == 27) {
									taskTrackApproval.setDay28(hours);
								} else if (i == 28) {
									taskTrackApproval.setDay29(hours);
								} else if (i == 29) {
									taskTrackApproval.setDay30(hours);
								} else if (i == 30) {
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

			if (beachArray.size() > 0) {// Beach

				Calendar cal = Calendar.getInstance();

				int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
				int intMonth = 0, intday = 0;
				cal.setTime(startDate);
				double hours = 0;

				if (beachId != null) {
					TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService.findById2(beachId);
					taskTrackApproval.setUpdatedBy(updatedBy);
					taskTrackApproval.setApproved_date(endDate);
					if (taskTrackApproval != null) {

						for (int i = 0; i < diffInDays; i++) {

							intMonth = (cal.get(Calendar.MONTH) + 1);
							intday = cal.get(Calendar.DAY_OF_MONTH);
							String dateString = cal.get(Calendar.YEAR) + "-"
									+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
									+ ((intday < 10) ? "0" + intday : "" + intday);

							if (beachArray.get(dateString) != null) {
								hours = Double.valueOf(beachArray.get(dateString).toString());

								if (i == 0) {
									taskTrackApproval.setDay1(hours);
								} else if (i == 1) {
									taskTrackApproval.setDay2(hours);
								} else if (i == 2) {
									taskTrackApproval.setDay3(hours);
								} else if (i == 3) {
									taskTrackApproval.setDay4(hours);
								} else if (i == 4) {
									taskTrackApproval.setDay5(hours);
								} else if (i == 5) {
									taskTrackApproval.setDay6(hours);
								} else if (i == 6) {
									taskTrackApproval.setDay7(hours);
								} else if (i == 7) {
									taskTrackApproval.setDay8(hours);
								} else if (i == 8) {
									taskTrackApproval.setDay9(hours);
								} else if (i == 9) {
									taskTrackApproval.setDay10(hours);
								} else if (i == 10) {
									taskTrackApproval.setDay11(hours);
								} else if (i == 11) {
									taskTrackApproval.setDay12(hours);
								} else if (i == 12) {
									taskTrackApproval.setDay13(hours);
								} else if (i == 13) {
									taskTrackApproval.setDay14(hours);
								} else if (i == 14) {
									taskTrackApproval.setDay15(hours);
								} else if (i == 15) {
									taskTrackApproval.setDay16(hours);
								} else if (i == 16) {
									taskTrackApproval.setDay17(hours);
								} else if (i == 17) {
									taskTrackApproval.setDay18(hours);
								} else if (i == 18) {
									taskTrackApproval.setDay19(hours);
								} else if (i == 19) {
									taskTrackApproval.setDay20(hours);
								} else if (i == 20) {
									taskTrackApproval.setDay21(hours);
								} else if (i == 21) {
									taskTrackApproval.setDay22(hours);
								} else if (i == 22) {
									taskTrackApproval.setDay23(hours);
								} else if (i == 23) {
									taskTrackApproval.setDay24(hours);
								} else if (i == 24) {
									taskTrackApproval.setDay25(hours);
								} else if (i == 25) {
									taskTrackApproval.setDay26(hours);
								} else if (i == 26) {
									taskTrackApproval.setDay27(hours);
								} else if (i == 27) {
									taskTrackApproval.setDay28(hours);
								} else if (i == 28) {
									taskTrackApproval.setDay29(hours);
								} else if (i == 29) {
									taskTrackApproval.setDay30(hours);
								} else if (i == 30) {
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

			if (overtimeArray.size() > 0) {// OverTime

				Calendar cal = Calendar.getInstance();

				int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
				int intMonth = 0, intday = 0;
				cal.setTime(startDate);
				double hours = 0;

				if (overtimeId != null) {
					TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService.findById2(overtimeId);
					taskTrackApproval.setUpdatedBy(updatedBy);
					taskTrackApproval.setApproved_date(endDate);
					if (taskTrackApproval != null) {

						for (int i = 0; i < diffInDays; i++) {

							intMonth = (cal.get(Calendar.MONTH) + 1);
							intday = cal.get(Calendar.DAY_OF_MONTH);
							String dateString = cal.get(Calendar.YEAR) + "-"
									+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
									+ ((intday < 10) ? "0" + intday : "" + intday);

							if (overtimeArray.get(dateString) != null) {
								hours = Double.valueOf(overtimeArray.get(dateString).toString());

								if (i == 0) {
									taskTrackApproval.setDay1(hours);
								} else if (i == 1) {
									taskTrackApproval.setDay2(hours);
								} else if (i == 2) {
									taskTrackApproval.setDay3(hours);
								} else if (i == 3) {
									taskTrackApproval.setDay4(hours);
								} else if (i == 4) {
									taskTrackApproval.setDay5(hours);
								} else if (i == 5) {
									taskTrackApproval.setDay6(hours);
								} else if (i == 6) {
									taskTrackApproval.setDay7(hours);
								} else if (i == 7) {
									taskTrackApproval.setDay8(hours);
								} else if (i == 8) {
									taskTrackApproval.setDay9(hours);
								} else if (i == 9) {
									taskTrackApproval.setDay10(hours);
								} else if (i == 10) {
									taskTrackApproval.setDay11(hours);
								} else if (i == 11) {
									taskTrackApproval.setDay12(hours);
								} else if (i == 12) {
									taskTrackApproval.setDay13(hours);
								} else if (i == 13) {
									taskTrackApproval.setDay14(hours);
								} else if (i == 14) {
									taskTrackApproval.setDay15(hours);
								} else if (i == 15) {
									taskTrackApproval.setDay16(hours);
								} else if (i == 16) {
									taskTrackApproval.setDay17(hours);
								} else if (i == 17) {
									taskTrackApproval.setDay18(hours);
								} else if (i == 18) {
									taskTrackApproval.setDay19(hours);
								} else if (i == 19) {
									taskTrackApproval.setDay20(hours);
								} else if (i == 20) {
									taskTrackApproval.setDay21(hours);
								} else if (i == 21) {
									taskTrackApproval.setDay22(hours);
								} else if (i == 22) {
									taskTrackApproval.setDay23(hours);
								} else if (i == 23) {
									taskTrackApproval.setDay24(hours);
								} else if (i == 24) {
									taskTrackApproval.setDay25(hours);
								} else if (i == 25) {
									taskTrackApproval.setDay26(hours);
								} else if (i == 26) {
									taskTrackApproval.setDay27(hours);
								} else if (i == 27) {
									taskTrackApproval.setDay28(hours);
								} else if (i == 28) {
									taskTrackApproval.setDay29(hours);
								} else if (i == 29) {
									taskTrackApproval.setDay30(hours);
								} else if (i == 30) {
									taskTrackApproval.setDay31(hours);
								}
							}
							cal.add(Calendar.DATE, 1);
						}
						tasktrackApprovalService.updateDatas(taskTrackApproval);
					}
				}
			}

			int approved_dayindex = 0;
			int prev_approved_date = 0;
			Date prev_approved_date_date = null;
			Object[] forward_status = timeTrackApprovalJPARepository.getapprovedStatus(month, year, projectId, userId);

			YearMonth yearMonthObject = YearMonth.of(year, month);
			int totaldays = yearMonthObject.lengthOfMonth();

			Calendar cal = Calendar.getInstance();
			cal.setTime(endDate);
			approved_dayindex = cal.get(Calendar.DAY_OF_MONTH);

			System.out.println("approved_dayindex" + approved_dayindex);
			System.out.println("totaldays" + totaldays);
			if (forward_status.length != 0) {
				if ((approved_dayindex >= 15) && (((String) forward_status[0]).equalsIgnoreCase(""))) {

					timesheet_button = true;
				} else if ((approved_dayindex >= totaldays) && (((String) forward_status[0]).equalsIgnoreCase("HM"))) {

					timesheet_button = true;
				}
			} else {

				if ((approved_dayindex >= 15)) {

					timesheet_button = true;
				} else if ((approved_dayindex >= totaldays)) {

					timesheet_button = true;
				}

			}
			// for showing status
			// Level 2 approver name

			ProjectModel projectdetails = null;
			boolean flaglevel2 = true;
			if (projectId != null) {
				// System.out.println("Here____________________________");
				projectdetails = getProjectDetails(projectId);
			}
			if (projectdetails != null) {
				if (projectdetails.getOnsite_lead() != null)

				{
					System.out.println("------------------------------------------------1");

					jsonDataMessageDetails.put("Level2_Approvar_Name", projectdetails.getOnsite_lead().getFirstName()
							+ " " + projectdetails.getOnsite_lead().getLastName());
				} else {

					flaglevel2 = false;
					jsonDataMessageDetails.put("Level2_Approvar_Name", "");
				}

				// level2 forwarded
				String frowardedDate = "";
				String frowardedDateLevel2 = "";
				String finance_status_message = "Timesheet not yet submitted to finance";
				String forwarded_ToLevel2_Status = "";
				if (flaglevel2) {
					forwarded_ToLevel2_Status = "Timesheet not yet forwarded to Level2";
				}
				// getb finance status of the current project added on 11/10

				Object[] finance_status = tasktrackApprovalService.getFinanceStatusOfCurrentProject(projectId, userId,
						month, year);

				if (finance_status != null) {
					System.out.println("---------------------------------------0");
					if (finance_status.length > 0) {
						System.out.println("---------------------------------------1");
						if (finance_status[0].equals("HM")) {
							System.out.println("---------------------------------------2");
							finance_status_message = "Submitted mid report";
						} else if (finance_status[0].equals("FM")) {
							System.out.println("---------------------------------------3");
							finance_status_message = "Submitted Final report";
						}

					}
				}
				jsonDataMessageDetails.put("Status", finance_status_message);
				//
				// level2 forwarded

				List<Object[]> level1 = tasktrackApprovalService.getForwardedDates(projectId, userId, month, year);
				if (!level1.isEmpty()) {
					// System.out.println("forwarded_date"+level1.get(0));
					if (level1 != null) {
						for (Object[] fl : level1) {
							if (fl != null) {
								if (fl[0] != null) {
									Date fdate = (Date) fl[0];
									System.out.println("---------------------------------------4");
									String pattern1 = "MM-dd-yyyy";
									DateFormat df1 = new SimpleDateFormat(pattern1);
									String forw = df1.format(fdate);
									forwarded_ToLevel2_Status = "Data upto " + forw + " has been forwarded to Level2";
								}
								if (fl[1] != null) {
									Date fdates = (Date) fl[1];

								}
							}
						}
					} // System.out.println("frowardedDate___________"+frowardedDate);

				}
				jsonDataMessageDetails.put("Forwarded_status", forwarded_ToLevel2_Status);
			}

			// end

			ids.put("billableId", billableId);
			ids.put("nonBillableId", nonbillableId);
			ids.put("beachId", beachId);
			ids.put("overtimeId", overtimeId);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "successfully saved. ");
			jsonDataRes.set("ids", ids);
			jsonDataRes.put("timesheet_button", timesheet_button);
			jsonDataRes.put("message_details", jsonDataMessageDetails);
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
			jsonDataRes.set("ids", ids);
			jsonDataRes.put("timesheet_button", timesheet_button);
		}
		return jsonDataRes;
	}

	@PostMapping("/getTaskTrackDataLevel2")
	public JSONObject getTaskTrackDataLevel2(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus)
			throws ParseException {

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

			if (startDate != null && endDate != null) {
				// userIdList =
				// projectAllocationService.getUserIdByProject(projectId);
				// userIdList =
				// projectAllocationService.getUserIdByProjectAndDate(projectId,startDate,endDate);
				userIdList = tasktrackApprovalService.getUserIdByProjectAndDateForLevel2(projectId, startDate, endDate);
				getUserDataForReportLevel(userIdList, startDate, endDate, jsonDataRes, timeTrackJSONData,
						loggedJsonArray, billableJsonArray, projectId);
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

	private void getUserDataForReportLevel(List<TaskTrackApprovalLevel2> userIdList, Date startDate, Date endDate,
			JSONObject jsonDataRes, List<JSONObject> timeTrackJSONData, List<JSONObject> loggedJsonArray,
			List<JSONObject> billableJsonArray, Long projectId) {

		JSONObject resultData = new JSONObject();
		List<JSONObject> timeTrackJsonData = new ArrayList<>();
		List<JSONObject> approvalJsonData = new ArrayList<>();
		for (TaskTrackApprovalLevel2 userItem : userIdList) {

			Long id = userItem.getUser().getUserId();
			List<Object[]> userList = null;
			Boolean isExist = tasktrackApprovalService.checkIsUserExists(id);
			// Data From Time track
			timeTrackJsonData = tasktrackApprovalService.getTimeTrackUserTaskDetailsLevel2(id, startDate, endDate,
					userList, loggedJsonArray, billableJsonArray, timeTrackJSONData, isExist, projectId);
		}

	}

	@PostMapping("/getFinanceData")
	public JSONObject getFinanceData(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus)
			throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		Long projectId = null;
		Long userId = null;
		int month = 0;
		int year = 0;

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
				if (month != 0 && year != 0 && projectId != null && userId == null) {

					resultData = tasktrackApprovalService.getFinanceDataByProject(month, year, projectId);
					node.put("timeTracks", resultData);
					node.put("month", month);
					node.put("year", year);
					node1.add(node);

				} else if (month != 0 && year != 0 && projectId == null && userId != null) {

					resultData = tasktrackApprovalService.getFinanceDataByUser(month, year, userId);
					node.put("timeTracks", resultData);
					node.put("month", month);
					node.put("year", year);
					node1.add(node);
				} else if (month != 0 && year != 0 && projectId != null && userId != null) {

					resultData = tasktrackApprovalService.getFinanceDataByUserAndProject(month, year, userId,
							projectId);
					node.put("timeTracks", resultData);
					node.put("month", month);
					node.put("year", year);
					node1.add(node);

				} else if (month != 0 && year != 0 && projectId == null && userId == null) {

					resultData = tasktrackApprovalService.getFinanceDataByMonthAndYear(month, year);
					node.put("timeTracks", resultData);
					node.put("month", month);
					node.put("year", year);
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

	// Renjith
	@PostMapping("/getFinanceDataConsolidated")
	public JSONObject getFinanceDataByProjectSet(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus)
			throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		Long projectId = null;
		Long userId = null;
		int month = 0;
		int year = 0;
		Long roleId = null;
		Long regionId = null;
		Long regionId_selected = null;
		boolean isLevels = true;
		Set<Long> projSet = null;
		List<ProjectModel> projectList = new ArrayList<ProjectModel>();
		try {

			if (requestdata.get("sessionId") != null && requestdata.get("sessionId").asText() != "") {
				userId = requestdata.get("sessionId").asLong();
			}
			if (requestdata.get("regionId") != null && requestdata.get("regionId").asText() != "") {
				regionId_selected = requestdata.get("regionId").asLong();
			}
			ArrayNode range = (ArrayNode) requestdata.get("range");

			JSONObject outputdata = new JSONObject();

			ArrayList<JSONObject> resultData = new ArrayList<JSONObject>();
			ArrayList<JSONObject> node1 = new ArrayList<JSONObject>();
			roleId = userService.getUserDetailsById(userId).getRole().getroleId();
			regionId = userService.getUserdetailsbyId(userId).getRegion().getId();

			if (roleId == 1 | roleId == 10) {
				if (regionId_selected != null) {

					projectList = projectRegionService.getProjectsByRegionId(regionId_selected);

				} else {
					projectList = projectService.getProjectList();
				}
				isLevels = false;
			}

			if (roleId == 4 | roleId == 5 | roleId == 6 | roleId == 9) {
				projectList = projectRegionService.getProjectsByRegionId(regionId);
				isLevels = false;
			}
			if (!(projectList).isEmpty() && projectList.size() > 0 && !isLevels) {
				projSet = new HashSet<Long>();
				for (ProjectModel pModel : projectList) {
					projSet.add(pModel.getProjectId());
				}
				for (JsonNode rangenode : range) {
					JSONObject node = new JSONObject();
					month = Integer.parseInt(rangenode.get("month").toString());
					year = Integer.parseInt(rangenode.get("year").toString());
					if (month != 0 && year != 0 && projSet != null && projSet.size() != 0) {

						// resultData =
						// tasktrackApprovalService.getFinanceDataByProject(month,
						// year, projectId);
						resultData = tasktrackApprovalService.getFinanceDataByProjectSet(month, year, projSet);
						node.put("timeTracks", resultData);
						node.put("month", month);
						node.put("year", year);
						node1.add(node);

					}

				}

				jsonDataRes.put("data", node1);
				jsonDataRes.put("status", "success");
				jsonDataRes.put("message", "success. ");
				jsonDataRes.put("code", httpstatus.getStatus());
				return jsonDataRes;
			}

			// Level 1
			projectList = projectService.getProjectListByLevel1(userId);
			// Level 2
			projectList.addAll(projectService.getProjectListByLevel2(userId));

			// Remove duplicates if any

			projectList = projectList.stream().distinct().collect(Collectors.toList());
			if (!(projectList).isEmpty() && projectList.size() > 0 && isLevels) {
				projSet = new HashSet<Long>();
				for (ProjectModel pModel : projectList) {
					projSet.add(pModel.getProjectId());
				}
				for (JsonNode rangenode : range) {
					JSONObject node = new JSONObject();
					month = Integer.parseInt(rangenode.get("month").toString());
					year = Integer.parseInt(rangenode.get("year").toString());
					if (month != 0 && year != 0 && projSet != null && projSet.size() != 0) {

						// resultData =
						// tasktrackApprovalService.getFinanceDataByProject(month,
						// year, projectId);
						resultData = tasktrackApprovalService.getFinanceDataByProjectSet(month, year, projSet);
						node.put("timeTracks", resultData);
						node.put("month", month);
						node.put("year", year);
						node1.add(node);

					}

				}

				jsonDataRes.put("data", node1);
				jsonDataRes.put("status", "success");
				jsonDataRes.put("message", "success. ");
				jsonDataRes.put("code", httpstatus.getStatus());
			}

		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;

	}

	// Renjith
	@PostMapping("/pendingLogCheck")
	public JSONObject checkPreviousTimeSheetsareClosed(@RequestBody JsonNode requestdata,
			HttpServletResponse httpstatus) throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		Long projectId = null;
		Long userId = null;
		int month = 0;
		int year = 0;

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

			if (month != 0 && year != 0 && projectId != null && userId != null) {

				jsonDataRes = tasktrackApprovalService.checkPreviousTimeSheetsareClosed(month, year, projectId, userId);

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
	public JSONObject halfCycleCheck(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus)
			throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		Long projectId = null;
		Long approverId = null;
		Long userId = null;
		Date endDate = null;

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

			/*
			 * if (requestdata.get("month") != null && requestdata.get("month").asText() !=
			 * "") { month = Integer.parseInt(requestdata.get("month").toString()); }
			 * 
			 * if (requestdata.get("year") != null && requestdata.get("year").asText() !=
			 * "") { year = Integer.parseInt(requestdata.get("year").toString()); }
			 */
			if (requestdata.get("endDate") != null && requestdata.get("endDate").asText() != "") {
				SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
				endDate = outputFormat.parse(requestdata.get("endDate").asText());

			}

			if (projectId != null && userId != null && approverId != null) {

				jsonDataRes = tasktrackApprovalService.halfCycleCheck(projectId, userId, approverId, endDate);

			}

			jsonDataRes.put("code", httpstatus.getStatus());
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;
	}

	/**
	 * @description reapproved datas save of level1
	 */
	@PostMapping("/reapprovelevel1")
	public ObjectNode ReapproveDatasofLevel1(@RequestBody JSONObject requestdata, HttpServletResponse httpstatus) {

		Long projectId = null;
		Long userId = null;
		Long logUser = null;
		Integer month = 0;
		Integer year = 0;
		Long billableId = null, nonbillableId = null, beachId = null, overtimeId = null, updatedBy = null;
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		year = Integer.parseInt((String) requestdata.get("year"));
		month = (Integer) requestdata.get("month");
		if (requestdata.get("projectId") != null && requestdata.get("projectId") != "") {
			projectId = Long.valueOf(requestdata.get("projectId").toString());
		}
		if (requestdata.get("userId") != null && requestdata.get("userId") != "") {
			userId = Long.valueOf(requestdata.get("userId").toString());
		}
		if (requestdata.get("updatedBy") != null && requestdata.get("updatedBy") != "") {
			logUser = Long.valueOf(requestdata.get("updatedBy").toString());
		}
		if (requestdata.get("billableId") != null && requestdata.get("billableId") != "") {
			billableId = Long.valueOf(requestdata.get("billableId").toString());
		}
		if (requestdata.get("nonBillableId") != null && requestdata.get("nonBillableId") != "") {
			nonbillableId = Long.valueOf(requestdata.get("nonBillableId").toString());
		}
		if (requestdata.get("beachId") != null && requestdata.get("beachId") != "") {
			beachId = Long.valueOf(requestdata.get("beachId").toString());
		}
		if (requestdata.get("overtimeId") != null && requestdata.get("overtimeId") != "") {
			overtimeId = Long.valueOf(requestdata.get("overtimeId").toString());
		}

		HashMap<String, Object> billableArray = new JSONObject();
		HashMap<String, Object> nonbillableArray = new JSONObject();
		HashMap<String, Object> beachArray = new JSONObject();
		HashMap<String, Object> overtimeArray = new JSONObject();

		UserModel user = userService.getUserDetailsById(userId);
		ProjectModel project = projectService.getProjectId(projectId);

		if (requestdata.get("billable") != null && requestdata.get("billable") != "") {
			billableArray = (HashMap<String, Object>) (requestdata.get("billable"));
		}
		if (requestdata.get("nonBillable") != null && requestdata.get("nonBillable") != "") {
			nonbillableArray = (HashMap<String, Object>) requestdata.get("nonBillable");
		}
		if (requestdata.get("beach") != null && requestdata.get("beach") != "") {
			beachArray = (HashMap<String, Object>) requestdata.get("beach");
		}
		if (requestdata.get("overtime") != null && requestdata.get("overtime") != "") {
			overtimeArray = (HashMap<String, Object>) requestdata.get("overtime");
		}

		try {
			jsonDataRes = tasktrackApprovalService.reApproveDatasofLevel1(projectId, userId, month, year, billableArray,
					nonbillableArray, beachArray, overtimeArray, billableId, nonbillableId, overtimeId, beachId,
					logUser);

		} catch (Exception e) {
			e.printStackTrace();

		}
		return jsonDataRes;

	}

	/**
	 * @description reapproved datas save of level2
	 */
	@PostMapping("/reapprovelevel2")
	public ObjectNode ReapproveDatasofLevel2(@RequestBody JSONObject requestdata, HttpServletResponse httpstatus) {

		Long projectId = null;
		Long userId = null;
		Long logUser = null;
		Integer month = 0;
		Integer year = 0;
		Long billableId = null, nonbillableId = null, beachId = null, overtimeId = null, updatedBy = null;
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		year = Integer.parseInt((String) requestdata.get("year"));
		month = (Integer) requestdata.get("month");
		if (requestdata.get("projectId") != null && requestdata.get("projectId") != "") {
			projectId = Long.valueOf(requestdata.get("projectId").toString());
		}
		if (requestdata.get("userId") != null && requestdata.get("userId") != "") {
			userId = Long.valueOf(requestdata.get("userId").toString());
		}
		if (requestdata.get("updatedBy") != null && requestdata.get("updatedBy") != "") {
			logUser = Long.valueOf(requestdata.get("updatedBy").toString());
		}
		if (requestdata.get("billableId") != null && requestdata.get("billableId") != "") {
			billableId = Long.valueOf(requestdata.get("billableId").toString());
		}
		if (requestdata.get("nonBillableId") != null && requestdata.get("nonBillableId") != "") {
			nonbillableId = Long.valueOf(requestdata.get("nonBillableId").toString());
		}
		if (requestdata.get("beachId") != null && requestdata.get("beachId") != "") {
			beachId = Long.valueOf(requestdata.get("beachId").toString());
		}
		if (requestdata.get("overtimeId") != null && requestdata.get("overtimeId") != "") {
			overtimeId = Long.valueOf(requestdata.get("overtimeId").toString());
		}

		HashMap<String, Object> billableArray = new JSONObject();
		HashMap<String, Object> nonbillableArray = new JSONObject();
		HashMap<String, Object> beachArray = new JSONObject();
		HashMap<String, Object> overtimeArray = new JSONObject();

		UserModel user = userService.getUserDetailsById(userId);
		ProjectModel project = projectService.getProjectId(projectId);

		if (requestdata.get("billable") != null && requestdata.get("billable") != "") {
			billableArray = (HashMap<String, Object>) (requestdata.get("billable"));
		}
		if (requestdata.get("nonBillable") != null && requestdata.get("nonBillable") != "") {
			nonbillableArray = (HashMap<String, Object>) requestdata.get("nonBillable");
		}
		if (requestdata.get("beach") != null && requestdata.get("beach") != "") {
			beachArray = (HashMap<String, Object>) requestdata.get("beach");
		}
		if (requestdata.get("overtime") != null && requestdata.get("overtime") != "") {
			overtimeArray = (HashMap<String, Object>) requestdata.get("overtime");
		}

		try {
			jsonDataRes = tasktrackApprovalService.reApproveDatasofLevel2(projectId, userId, month, year, billableArray,
					nonbillableArray, beachArray, overtimeArray, billableId, nonbillableId, overtimeId, beachId,
					logUser);

		} catch (Exception e) {
			e.printStackTrace();

		}
		return jsonDataRes;

	}

	@PostMapping("/rejectTimesheet")
	public ObjectNode rejectTimesheet(@RequestBody JSONObject requestdata, HttpServletResponse httpstatus) {

		ObjectNode responsedata = objectMapper.createObjectNode();

		String message = null;
		Long projectId = null;
		Long userId = null;
		Long logUser = null;
		Long month = null;
		Long year = null;
		if (requestdata.get("message") != null) {

			message = (String) requestdata.get("message");
		}
		if (requestdata.get("projectId") != null && requestdata.get("projectId") != "") {
			projectId = Long.valueOf(requestdata.get("projectId").toString());
		}
		if (requestdata.get("userId") != null && requestdata.get("userId") != "") {
			userId = Long.valueOf(requestdata.get("userId").toString());
		}
		if (requestdata.get("month") != null && requestdata.get("month") != "") {
			month = Long.valueOf(requestdata.get("month").toString());
		}
		if (requestdata.get("year") != null && requestdata.get("year") != "") {
			year = Long.valueOf(requestdata.get("year").toString());
		}

		responsedata = tasktrackApprovalService.mailRejectTimesheetDetailstoLevel1andClear(projectId, userId, month,
				year, message);

		return responsedata;
	}

	/***
	 * @des check if the role of joined approver in selected project is as level1 or
	 *      level2
	 * @param requestdata
	 * @param httpstatus
	 * @return primary-role:
	 *         joined_approver,secondary-role:level1approver/level2approver
	 */

	@PostMapping("/checkApproveLevel")
	public ObjectNode checkApproveLevel(@RequestBody JSONObject requestdata, HttpServletResponse httpstatus) {

		ObjectNode responsedata = objectMapper.createObjectNode();
		Long project_Id = null;
		Long logUser = null;

		if (requestdata.get("projectId") != null && requestdata.get("projectId") != "") {
			project_Id = Long.valueOf(requestdata.get("projectId").toString());
		}
		if (requestdata.get("logUser") != null && requestdata.get("logUser") != "") {
			logUser = Long.valueOf(requestdata.get("logUser").toString());
		}

		responsedata = tasktrackService.checkApproveLevel(project_Id, logUser);

		return responsedata;
	}

	// Renjith

	@SuppressWarnings("unchecked")
	@PostMapping("/bulkApprovalLevel2")
	public ObjectNode bulkApprovalLevel2(@RequestBody JSONObject requestdata, HttpServletResponse httpstatus) {

		int currentMonth = 0;
		int currentDay = 0;
		int currentYear = 0;
		int monthIndex = 0;
		int yearIndex = 0;
		Long projectId = null;
		Calendar cal = null;
		SimpleDateFormat dateFormat = null;
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		cal = Calendar.getInstance();
		currentMonth = (cal.get(Calendar.MONTH) + 1);
		currentDay = cal.get(Calendar.DAY_OF_MONTH);
		currentYear = cal.get(Calendar.YEAR);

		int today = 0;
		if (currentDay > 1) {
			today = currentDay - 1;
		} else if (currentDay == 1) {
			today = currentDay;
		}

		if (requestdata.get("projectId") != null && requestdata.get("projectId") != "") {
			projectId = Long.valueOf(requestdata.get("projectId").toString());

		}

		if (requestdata.get("month") != null && requestdata.get("month") != "" && requestdata.get("year") != null
				&& requestdata.get("year") != "") {
			monthIndex = (int) requestdata.get("month");
			yearIndex = (int) requestdata.get("year");
		}

		List<TaskTrackApprovalLevel2> ls = tasktrackApprovalService.getNotApprovedData(monthIndex, yearIndex,
				projectId);

		if (ls.size() == 0) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", " No data  found ! ");
			return jsonDataRes;
		}

		for (TaskTrackApprovalLevel2 trackApprovalLevel2 : ls) {
			try {

				if (trackApprovalLevel2.getYear() == currentYear && trackApprovalLevel2.getMonth() == currentMonth) {

					trackApprovalLevel2
							.setApproved_date(dateFormat.parse(currentYear + "-" + currentMonth + "-" + today));
				} else {
					YearMonth yearMonthObject = YearMonth.of(trackApprovalLevel2.getYear(),
							trackApprovalLevel2.getMonth());
					int daysInMonth = yearMonthObject.lengthOfMonth();
					trackApprovalLevel2.setApproved_date(dateFormat.parse(
							trackApprovalLevel2.getYear() + "-" + trackApprovalLevel2.getMonth() + "-" + daysInMonth));
				}
				taskTrackApprovalLevel2Repository.save(trackApprovalLevel2);

			} catch (Exception e) {
				e.printStackTrace();

			}

		}

		jsonDataRes.put("status", "success");
		jsonDataRes.put("code", httpstatus.getStatus());
		jsonDataRes.put("message", "successfully saved. ");

		return jsonDataRes;
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/closeTimesheet")
	public ObjectNode closeTimesheet(@RequestBody JSONObject requestdata, HttpServletResponse httpstatus) {
		int currentMonth = 0;
		int currentDay = 0;
		int currentYear = 0;
		int monthIndex = 0;
		int yearIndex = 0;
		Long projectId = null;
		Calendar cal = null;
		SimpleDateFormat dateFormat = null;
		ProjectModel project = null;
		TaskTrackApprovalFinance taskTrackApprovalFinance = null;
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		cal = Calendar.getInstance();
		currentMonth = (cal.get(Calendar.MONTH) + 1);
		currentDay = cal.get(Calendar.DAY_OF_MONTH);
		currentYear = cal.get(Calendar.YEAR);
		if (requestdata.get("month") != null && requestdata.get("month") != "" && requestdata.get("year") != null
				&& requestdata.get("year") != "") {
			monthIndex = (int) requestdata.get("month");
			yearIndex = (int) requestdata.get("year");
		}

		if (requestdata.get("projectId") != null && requestdata.get("projectId") != "") {
			projectId = Long.valueOf(requestdata.get("projectId").toString());

		}

		if (yearIndex > currentYear) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("message", " Date Validation failed - hint future date !");

			return jsonDataRes;
		}

		if (yearIndex == currentYear && monthIndex == currentMonth) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("message", " Date Validation failed - hint current Month !");

			return jsonDataRes;
		}

		List<TaskTrackApprovalLevel2> ls = tasktrackApprovalService.getHalfMonthApprovedData(monthIndex, yearIndex,
				projectId);

		if (ls.size() == 0) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", " No data  found ! ");
			return jsonDataRes;
		}

		for (TaskTrackApprovalLevel2 trackApprovalLevel2 : ls) {

			try {
				trackApprovalLevel2.setStatus("FM");
				YearMonth yearMonthObject = YearMonth.of(trackApprovalLevel2.getYear(), trackApprovalLevel2.getMonth());
				int daysInMonth = yearMonthObject.lengthOfMonth();
				trackApprovalLevel2.setForwarded_date(dateFormat.parse(
						trackApprovalLevel2.getYear() + "-" + trackApprovalLevel2.getMonth() + "-" + daysInMonth));
				taskTrackApprovalFinance = new TaskTrackApprovalFinance();
				taskTrackApprovalLevel2Repository.save(trackApprovalLevel2);
				taskTrackApprovalFinance.setDay1(trackApprovalLevel2.getDay1());
				taskTrackApprovalFinance.setDay2(trackApprovalLevel2.getDay2());
				taskTrackApprovalFinance.setDay3(trackApprovalLevel2.getDay3());
				taskTrackApprovalFinance.setDay4(trackApprovalLevel2.getDay4());
				taskTrackApprovalFinance.setDay5(trackApprovalLevel2.getDay5());
				taskTrackApprovalFinance.setDay6(trackApprovalLevel2.getDay6());
				taskTrackApprovalFinance.setDay7(trackApprovalLevel2.getDay7());
				taskTrackApprovalFinance.setDay8(trackApprovalLevel2.getDay8());
				taskTrackApprovalFinance.setDay9(trackApprovalLevel2.getDay9());
				taskTrackApprovalFinance.setDay10(trackApprovalLevel2.getDay10());
				taskTrackApprovalFinance.setDay11(trackApprovalLevel2.getDay11());
				taskTrackApprovalFinance.setDay12(trackApprovalLevel2.getDay12());
				taskTrackApprovalFinance.setDay13(trackApprovalLevel2.getDay13());
				taskTrackApprovalFinance.setDay14(trackApprovalLevel2.getDay14());
				taskTrackApprovalFinance.setDay15(trackApprovalLevel2.getDay15());
				taskTrackApprovalFinance.setDay16(trackApprovalLevel2.getDay16());
				taskTrackApprovalFinance.setDay17(trackApprovalLevel2.getDay17());
				taskTrackApprovalFinance.setDay18(trackApprovalLevel2.getDay18());
				taskTrackApprovalFinance.setDay19(trackApprovalLevel2.getDay19());
				taskTrackApprovalFinance.setDay20(trackApprovalLevel2.getDay20());
				taskTrackApprovalFinance.setDay21(trackApprovalLevel2.getDay21());
				taskTrackApprovalFinance.setDay22(trackApprovalLevel2.getDay22());
				taskTrackApprovalFinance.setDay23(trackApprovalLevel2.getDay23());
				taskTrackApprovalFinance.setDay24(trackApprovalLevel2.getDay24());
				taskTrackApprovalFinance.setDay25(trackApprovalLevel2.getDay25());
				taskTrackApprovalFinance.setDay26(trackApprovalLevel2.getDay26());
				taskTrackApprovalFinance.setDay27(trackApprovalLevel2.getDay27());
				taskTrackApprovalFinance.setDay28(trackApprovalLevel2.getDay28());
				taskTrackApprovalFinance.setDay29(trackApprovalLevel2.getDay29());
				taskTrackApprovalFinance.setDay30(trackApprovalLevel2.getDay30());
				taskTrackApprovalFinance.setDay31(trackApprovalLevel2.getDay31());
				taskTrackApprovalFinance.setFirstName(trackApprovalLevel2.getFirstName());
				taskTrackApprovalFinance.setLastName(trackApprovalLevel2.getLastName());
				taskTrackApprovalFinance.setMonth(trackApprovalLevel2.getMonth());
				taskTrackApprovalFinance.setProject(trackApprovalLevel2.getProject());
				taskTrackApprovalFinance.setProjectType(trackApprovalLevel2.getProjectType());
				taskTrackApprovalFinance.setStatus(trackApprovalLevel2.getStatus());
				taskTrackApprovalFinance.setUser(trackApprovalLevel2.getUser());
				taskTrackApprovalFinance.setYear(trackApprovalLevel2.getYear());
				tasktrackApprovalService.saveLevel3(taskTrackApprovalFinance);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		jsonDataRes.put("status", "success");
		jsonDataRes.put("code", httpstatus.getStatus());
		jsonDataRes.put("message", "successfully saved. ");
		return jsonDataRes;
	}

	// Renjith

	@SuppressWarnings("unchecked")
	@PostMapping("/midMonth")
	public ObjectNode midMonth(@RequestBody JSONObject requestdata, HttpServletResponse httpstatus) {
		int currentMonth = 0;
		int currentDay = 0;
		int currentYear = 0;
		int monthIndex = 0;
		int yearIndex = 0;
		Long projectId = null;
		TaskTrackApprovalFinance taskTrackApprovalFinance = null;
		Calendar cal = null;
		SimpleDateFormat dateFormat = null;
		ProjectModel project = null;
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		cal = Calendar.getInstance();
		currentMonth = (cal.get(Calendar.MONTH) + 1);
		currentDay = cal.get(Calendar.DAY_OF_MONTH);
		currentYear = cal.get(Calendar.YEAR);
		if (requestdata.get("projectId") != null && requestdata.get("projectId") != "") {
			projectId = Long.valueOf(requestdata.get("projectId").toString());
		}
		if (requestdata.get("month") != null && requestdata.get("month") != "" && requestdata.get("year") != null
				&& requestdata.get("year") != "") {
			monthIndex = (int) requestdata.get("month");
			yearIndex = (int) requestdata.get("year");
		}

		if (yearIndex == currentYear && monthIndex == currentMonth && currentDay < 15) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("message", " Given date is less than mid month!");

			return jsonDataRes;
		}

		if (yearIndex > currentYear && monthIndex > currentMonth) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("message", " Date entered is beyond current date !");

			return jsonDataRes;
		}
		System.out.println(monthIndex + ", " + yearIndex + ", " + projectId);
		List<TaskTrackApprovalLevel2> ls = tasktrackApprovalService.getMidMonthApprovedData(monthIndex, yearIndex,
				projectId);
		System.out.println(ls.size());
		for (TaskTrackApprovalLevel2 trackApprovalLevel2 : ls) {
			try {
				trackApprovalLevel2.setStatus("HM");
				YearMonth yearMonthObject = YearMonth.of(trackApprovalLevel2.getYear(), trackApprovalLevel2.getMonth());
				int daysInMonth = yearMonthObject.lengthOfMonth();
				trackApprovalLevel2.setForwarded_date(dateFormat
						.parse(trackApprovalLevel2.getYear() + "-" + trackApprovalLevel2.getMonth() + "-" + "15"));
				taskTrackApprovalFinance = new TaskTrackApprovalFinance();
				taskTrackApprovalLevel2Repository.save(trackApprovalLevel2);
				taskTrackApprovalFinance.setDay1(trackApprovalLevel2.getDay1());
				taskTrackApprovalFinance.setDay2(trackApprovalLevel2.getDay2());
				taskTrackApprovalFinance.setDay3(trackApprovalLevel2.getDay3());
				taskTrackApprovalFinance.setDay4(trackApprovalLevel2.getDay4());
				taskTrackApprovalFinance.setDay5(trackApprovalLevel2.getDay5());
				taskTrackApprovalFinance.setDay6(trackApprovalLevel2.getDay6());
				taskTrackApprovalFinance.setDay7(trackApprovalLevel2.getDay7());
				taskTrackApprovalFinance.setDay8(trackApprovalLevel2.getDay8());
				taskTrackApprovalFinance.setDay9(trackApprovalLevel2.getDay9());
				taskTrackApprovalFinance.setDay10(trackApprovalLevel2.getDay10());
				taskTrackApprovalFinance.setDay11(trackApprovalLevel2.getDay11());
				taskTrackApprovalFinance.setDay12(trackApprovalLevel2.getDay12());
				taskTrackApprovalFinance.setDay13(trackApprovalLevel2.getDay13());
				taskTrackApprovalFinance.setDay14(trackApprovalLevel2.getDay14());
				taskTrackApprovalFinance.setDay15(trackApprovalLevel2.getDay15());
				taskTrackApprovalFinance.setDay16(trackApprovalLevel2.getDay16());
				taskTrackApprovalFinance.setDay17(trackApprovalLevel2.getDay17());
				taskTrackApprovalFinance.setDay18(trackApprovalLevel2.getDay18());
				taskTrackApprovalFinance.setDay19(trackApprovalLevel2.getDay19());
				taskTrackApprovalFinance.setDay20(trackApprovalLevel2.getDay20());
				taskTrackApprovalFinance.setDay21(trackApprovalLevel2.getDay21());
				taskTrackApprovalFinance.setDay22(trackApprovalLevel2.getDay22());
				taskTrackApprovalFinance.setDay23(trackApprovalLevel2.getDay23());
				taskTrackApprovalFinance.setDay24(trackApprovalLevel2.getDay24());
				taskTrackApprovalFinance.setDay25(trackApprovalLevel2.getDay25());
				taskTrackApprovalFinance.setDay26(trackApprovalLevel2.getDay26());
				taskTrackApprovalFinance.setDay27(trackApprovalLevel2.getDay27());
				taskTrackApprovalFinance.setDay28(trackApprovalLevel2.getDay28());
				taskTrackApprovalFinance.setDay29(trackApprovalLevel2.getDay29());
				taskTrackApprovalFinance.setDay30(trackApprovalLevel2.getDay30());
				taskTrackApprovalFinance.setDay31(trackApprovalLevel2.getDay31());
				taskTrackApprovalFinance.setFirstName(trackApprovalLevel2.getFirstName());
				taskTrackApprovalFinance.setLastName(trackApprovalLevel2.getLastName());
				taskTrackApprovalFinance.setMonth(trackApprovalLevel2.getMonth());
				taskTrackApprovalFinance.setProject(trackApprovalLevel2.getProject());
				taskTrackApprovalFinance.setProjectType(trackApprovalLevel2.getProjectType());
				taskTrackApprovalFinance.setStatus(trackApprovalLevel2.getStatus());
				taskTrackApprovalFinance.setUser(trackApprovalLevel2.getUser());
				taskTrackApprovalFinance.setYear(trackApprovalLevel2.getYear());
				tasktrackApprovalService.saveLevel3(taskTrackApprovalFinance);
			} catch (Exception e) {
				e.printStackTrace();
				jsonDataRes.put("status", "Failure");
				jsonDataRes.put("code", httpstatus.getStatus());
				jsonDataRes.put("message", "Exception occured. ");
			}
		}
		jsonDataRes.put("status", "success");
		jsonDataRes.put("code", httpstatus.getStatus());
		jsonDataRes.put("message", "successfully saved. ");
		return jsonDataRes;
	}

//Nisha
	@PostMapping("/getProjectNamesForApproval")
	public JsonNode getProjectNamesForApproval(@RequestBody JSONObject requestdata, HttpServletResponse httpstatus)
			throws Exception {
		ArrayNode projectTitle = objectMapper.createArrayNode();
		long uId = Long.valueOf(requestdata.get("uId").toString());
		int month = Integer.parseInt(requestdata.get("month").toString());
		int year = Integer.parseInt(requestdata.get("year").toString());
		UserModel user = userService.getUserDetailsById(uId);

		if (user.getRole().getroleId() == 6 || user.getRole().getroleId() == 1) {// Finance
			for (ProjectModel alloc : tasktrackRepository.getProjectNamesForApproval(month, year)) {

				ObjectNode node = objectMapper.createObjectNode();
				node.put("id", alloc.getProjectId());
				node.put("value", alloc.getProjectName());
				node.put("tier", alloc.getProjectTier());
				// get region list
				List<ProjectRegion> regions = projectservice.getregionlist(alloc.getProjectId());
				ArrayNode regionsArray = objectMapper.createArrayNode();
				ArrayList<Integer> regionArraylist = new ArrayList<Integer>();
				if (regions.isEmpty()) {
					node.set("projectRegion", regionsArray);
				} else {

					for (ProjectRegion regioneach : regions) {
						ObjectNode resource = objectMapper.createObjectNode();
						regionsArray.add((int) regioneach.getRegion_Id().getId());

					}
					node.set("projectRegion", regionsArray);
				}
				//

				projectTitle.add(node);
			}
		} else {
			List<Object[]> projectList = null;
			if (user.getRole().getroleId() == 7) {
				// System.out.println("_________________________________________APPROVER_LEVEL_2
				// " +uId );
				projectList = tasktrackRepository.getProjectNamesForApprovalLevel2(uId, month, year);
			} else if (user.getRole().getroleId() == 2) {
				// System.out.println("_________________________________________APPROVER_LEVEL_1/Lead
				// "+uId);
				projectList = tasktrackRepository.getProjectNamesForApprovalLevel1(uId, month, year);
			}
			// Renjith
			else if (user.getRole().getroleId() == 5) {
				// System.out.println("_________________________________________Sub Admin
				// "+uId);
				projectList = projectRegionService.getObjProjectsByRegionId(user.getRegion().getId());
			} else if (user.getRole().getroleId() == 11) {
				// System.out.println("_________________________________________Approver
				// "+uId);
				projectList = tasktrackRepository.getProjectNamesForApprover(uId, month, year);
			}

			else {
				// System.out.println("_________________________________________Other"+uId);
				projectList = tasktrackRepository.getProjectNamesForApprovalnew(uId);
			}

			for (Object[] alloc : projectList) {
				try {
					ObjectNode node = objectMapper.createObjectNode();
					node.put("id", (Long) alloc[1]);
					node.put("value", (String) alloc[0]);
					node.put("tier", (Integer) alloc[2]);
					// get region list
					List<ProjectRegion> regions = projectservice.getregionlist((Long) alloc[1]);
					ArrayNode regionsArray = objectMapper.createArrayNode();
					ArrayList<Integer> regionArraylist = new ArrayList<Integer>();
					if (regions.isEmpty()) {
						node.set("projectRegion", regionsArray);
					} else {

						for (ProjectRegion regioneach : regions) {
							ObjectNode resource = objectMapper.createObjectNode();
							regionsArray.add((int) regioneach.getRegion_Id().getId());

						}
						node.set("projectRegion", regionsArray);
					}
					//
					projectTitle.add(node);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		ObjectNode dataNode = objectMapper.createObjectNode();
		dataNode.set("projectTitle", projectTitle);

		ObjectNode node = objectMapper.createObjectNode();
		node.put("status", "success");
		node.set("data", dataNode);
		return node;

	}

	// hashir
	@PostMapping("/submissionday/save")
	public void saveSubmissionDays(@RequestBody List<TaskTrackDaySubmissionModel> taskTrackDaySubmissionList,
			HttpServletResponse httpstatus) {
		ObjectNode response = objectMapper.createObjectNode();
		try {
			tasktrackService.saveSubmissionDays(taskTrackDaySubmissionList);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("status", "Failure");
			response.put("code", httpstatus.getStatus());
			response.put("message", "Exception occured.");
		}
		response.put("status", "success");
		response.put("code", httpstatus.getStatus());
		response.put("message", "successfully saved. ");
	}

	@GetMapping("/submissionday/{month}")

	public ObjectNode getSubmissionDay(@PathVariable("month") int month, HttpServletResponse httpstatus) {
		ObjectNode response = objectMapper.createObjectNode();
		try {
			TaskTrackDaySubmissionModel data = tasktrackService.getSubmissionDayByMonth(month);
			JsonNode node = objectMapper.convertValue(data, JsonNode.class);
			response.put("status", "success");
			response.put("code", httpstatus.getStatus());
			response.set("payload", node);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("status", "Failure");
			response.put("code", httpstatus.getStatus());
			response.put("message", "Exception occured.");
		}
		return response;
	}

	// bala
	@PostMapping(value = "/addQuickTimeTrack", headers = "Accept=application/json")
	public JsonNode addQuickTimeTrack(@RequestBody JsonNode taskData, HttpServletResponse status)
			throws JSONException, ParseException {
		ObjectNode dataResponse = objectMapper.createObjectNode();

		try {
			Long uId = taskData.get("uId").asLong();
			Boolean saveFailed = false;

			if (!uId.equals(null)) {

				ArrayNode arrayNode = (ArrayNode) taskData.get("taskDetails");
				UserModel user = userService.getUserDetailsById(uId);

				if (!user.equals(null)) {

					for (JsonNode node : arrayNode) {

						double hours = node.get("hours").asDouble();
						/*
						 * if(hours==0) continue;
						 */

						Long taskId = node.get("taskId").asLong();
						if (taskId != 0) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							sdf.setTimeZone(TimeZone.getDefault());
							ProjectModel projectModel = tasktrackServiceImpl
									.getProjectModelById(taskData.get("projectId").asLong());
							Task taskCategory = tasktrackService.getTaskByName("Quick Time Track");
							Tasktrack tasktrack = new Tasktrack();
							tasktrack.setTask(taskCategory);
							tasktrack.setProject(projectModel);
							tasktrack.setId(node.get("taskId").asLong());
							tasktrack.setHours(node.get("hours").asDouble());
							tasktrack.setDate(sdf.parse(node.get("date").asText()));
							if (tasktrackServiceImpl.updateTaskByName(tasktrack)) {
								dataResponse.put("status", "success");
							} else {
								dataResponse.put("status", "failure");
							}
							continue;
						}

						Tasktrack tasktrack = new Tasktrack();
						tasktrack.setUser(user);

						Task task = tasktrackService.getTaskByName("Quick Time Track");
						if (task != null)
							tasktrack.setTask(task);
						else {
							saveFailed = true;
							dataResponse.put("message", "Process failed due to invalid task Id");
						}

						tasktrack.setHours(hours);

						Long projectId = taskData.get("projectId").asLong();
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

						tasktrack.setDescription("Quick Time Track");

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
			dataResponse.put("message", "Exception : " + e);
			System.out.println("Exception " + e);
		}

		return dataResponse;
	}

	@PostMapping("/getQuicktimetrack")
	public JSONObject getQuickTimeTrack(@RequestBody JsonNode requestData) throws Exception {
		Long userId = null;
		Date fromDate = null, toDate = null;
		String vl = null;
		JSONObject object = new JSONObject();
		JSONObject obj = new JSONObject();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (requestData.get("userId") != null && requestData.get("userId").asText() != "") {
			userId = requestData.get("userId").asLong();
		}
		if (requestData.get("fromDate") != null && requestData.get("fromDate").asText() != null) {
			fromDate = sdf.parse(requestData.get("fromDate").asText());
		}
		if (requestData.get("toDate") != null && requestData.get("toDate").asText() != null) {
			toDate = sdf.parse(requestData.get("toDate").asText());
		}
		try {
			List<Object[]> taskTypesList = null;
			taskTypesList = tasktrackService.getTasksForTimeTrack(userId, fromDate, toDate);
			ArrayList projectList = new ArrayList();
			ArrayList TaskDetailstList = new JSONArray();
			JSONObject projectObject = new JSONObject();
			// JSONObject singleprojectObject = new JSONObject();
			JSONObject TaskDetailsObject = new JSONObject();
			JSONObject singleprojectObject = new JSONObject();
			String project = "";
			if (!taskTypesList.isEmpty()) {
				if (taskTypesList != null) {
					for (Object[] fl : taskTypesList) {
						if (fl != null) {
							projectObject = new JSONObject();
							project = (String) fl[1];
							Date dat = (Date) fl[2];
							if (singleprojectObject.get(project) == null) {
								if (TaskDetailstList.size() > 0) {
									TaskDetailstList = new JSONArray();
								}
								projectObject.put("id", fl[0]);
								projectObject.put("name", project);
								TaskDetailsObject = new JSONObject();
								TaskDetailsObject.put("date", sdf.format(dat));
								TaskDetailsObject.put("taskId", fl[3]);
								TaskDetailsObject.put("hours", fl[4]);
								TaskDetailstList.add(TaskDetailsObject);

								projectObject.put("TaskDetails", TaskDetailstList);
								singleprojectObject = new JSONObject();
								singleprojectObject.put(project, projectObject);
								projectList.add(singleprojectObject);
							} else {

								projectObject = (JSONObject) singleprojectObject.get(project);
								TaskDetailstList = (ArrayList) projectObject.get("TaskDetails");
								TaskDetailsObject = new JSONObject();
								TaskDetailsObject.put("date", sdf.format(dat));
								TaskDetailsObject.put("taskId", fl[3]);
								TaskDetailsObject.put("hours", fl[4]);
								TaskDetailstList.add(TaskDetailsObject);
								projectObject.put("TaskDetails", TaskDetailstList);
							}
						}
					}
				}

			}
			obj.put("uId", userId);
			obj.put("projectList", projectList);
			object.put("data", obj);
			object.put("status", "success");
		} catch (Exception e) {
			e.printStackTrace();
			object.put("status", "failure");
			object.put("data", obj);

		}
		return object;

	}
	// bala

	//Nisha
	@PostMapping(value = "/createCorrection")
	public JsonNode createCorrection(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {
		ObjectNode responsedata = objectMapper.createObjectNode();
		responsedata = tasktrackService.createCorrection(requestdata);
		responsedata.put("code", servletresponse.getStatus());
		return responsedata;
	}
}
