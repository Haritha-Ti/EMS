package com.EMS.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.EMS.model.*;
import com.EMS.repository.UserRepository;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.dto.ProjectSubmissionDataDTO;
import com.EMS.dto.Taskdetails;
import com.EMS.exceptions.BadInputException;
import com.EMS.service.ProjectAllocationService;
import com.EMS.service.ProjectExportService;
import com.EMS.service.ProjectRegionService;
import com.EMS.service.ReportService;
import com.EMS.service.ReportServiceImpl;
import com.EMS.service.TasktrackApprovalService;
import com.EMS.service.TasktrackService;
import com.EMS.service.UserService;
import com.EMS.service.ProjectService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value = { "/report" })
public class ReportController {

	@Autowired
	ReportService reportService;
	@Autowired
	ReportServiceImpl reportServiceImpl;

	@Autowired
	TasktrackService taskTrackService;

	@Autowired
	UserService userService;

	@Autowired
	ProjectAllocationService projectAllocationService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	ProjectExportService projectExportService;

	@Autowired
	ProjectService projectService;

	@Autowired
	private ProjectRegionService projectRegionService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private TasktrackApprovalService tasktrackApprovalService;

	@PostMapping("/getProjectReport")
	public JsonNode getProjectReport(@RequestBody Taskdetails requestdata) {
		ArrayNode projectReport = null;
		Long regionId_selected = null;
		if (requestdata.getRegionId() != null && requestdata.getRegionId() != 0) {
			regionId_selected = requestdata.getRegionId();
		}

		if (regionId_selected != null) {
			projectReport = reportServiceImpl.getProjectReportDetailsByRegions(requestdata.getProjectId(),
					requestdata.getFromDate(), requestdata.getToDate(), regionId_selected);
		} else {
			projectReport = reportServiceImpl.getProjectReportDetails(requestdata.getProjectId(),
					requestdata.getFromDate(), requestdata.getToDate());
		}

		ObjectNode dataNode = objectMapper.createObjectNode();
		dataNode.set("projectReport", projectReport);

		ObjectNode node = objectMapper.createObjectNode();
		node.put("status", "success");
		node.set("data", dataNode);
		return node;
	}

	@PostMapping("/getBenchProjectReport")
	public JsonNode getBenchProjectReport(@RequestBody Taskdetails requestdata) {

		ArrayNode benchProjectReport = null;
		System.out.println("requestdata.getuId()=" + requestdata.getuId());
		if (requestdata.getuId() != null) { // System.out.println("if");
			benchProjectReport = reportServiceImpl.getBenchProjectReportDetails(requestdata.getuId(),
					requestdata.getFromDate(), requestdata.getToDate());
		} else if (requestdata.getRegionId() != null) {
			benchProjectReport = reportServiceImpl.getBenchProjectReportDetailsReports(requestdata.getuId(),
					requestdata.getFromDate(), requestdata.getToDate(), requestdata.getRegionId());
		} else { // System.out.println("else");
			benchProjectReport = reportServiceImpl.getBenchProjectReportDetails(requestdata.getFromDate(),
					requestdata.getToDate());
		}
		ObjectNode dataNode = objectMapper.createObjectNode();
		dataNode.set("benchProjectReport", benchProjectReport);

		ObjectNode node = objectMapper.createObjectNode();
		node.put("status", "success");
		node.set("data", dataNode);
		return node;
	}

	@PostMapping("/getUserReport")
	public JSONObject getUserreport(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus)
			throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		List<JSONObject> jsonDataRes1 = new ArrayList<>();
		List<JSONObject> jsonArray = new ArrayList<>();
		Long userId = null, projectId = null;
		// Long pageSize = 50L;
		// Long pageIndex = null;

		try {
			if (requestdata.get("userId") != null && requestdata.get("userId").asText() != "") {
				userId = requestdata.get("userId").asLong();
			}

			if (requestdata.get("projectId") != null && requestdata.get("projectId").asText() != "") {
				projectId = requestdata.get("projectId").asLong();
			}

			// if (requestdata.get("pageIndex") != null &&
			// requestdata.get("pageIndex").asText() != "") {
			// pageIndex = requestdata.get("pageIndex").asLong();
			// }
			// Long startingIndex = (pageSize*pageIndex)+1;
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
			Long count = null;
			if (startDate != null && endDate != null && projectId == null && userId == null) {
				count = userService.getCount();
				// jsonDataRes.put("userCount", count);
				// userIdList =
				// userService.getUserIdLists(pageSize,startingIndex);
				userIdList = userService.getUserIdLists();
				getUserDataForReport(userIdList, startDate, endDate, jsonDataRes, jsonDataRes1, jsonArray, projectId);

			}

			else if (startDate != null && endDate != null && projectId != null && userId == null) {
				count = projectAllocationService.getUserCount(projectId);
				// jsonDataRes.put("userCount", count);
				// userIdList =
				// projectAllocationService.getUserIdByProject(projectId,pageSize,startingIndex);
				// userIdList =
				// projectAllocationService.getUserIdByProject(projectId);
				userIdList = projectAllocationService.getUserIdByProjectAndDate(projectId, startDate, endDate);

				getUserDataForReport(userIdList, startDate, endDate, jsonDataRes, jsonDataRes1, jsonArray, projectId);
			}

			else if (startDate != null && endDate != null && projectId == null && userId != null) {
				List<Object[]> userList = null;
				// jsonDataRes.put("userCount", "1");
				Boolean isExist = taskTrackService.checkIsUserExists(userId);
				List<JSONObject> jsonDataRes11 = taskTrackService.getUserTaskDetails(userId, startDate, endDate,
						userList, jsonArray, jsonDataRes1, isExist, projectId);
			}

			else if (startDate != null && endDate != null && projectId != null && userId != null) {
				List<Object[]> userList = null;
				// jsonDataRes.put("userCount", "1");
				Boolean isExist = taskTrackService.checkExistanceOfUser(projectId, userId);
				List<JSONObject> jsonDataRes11 = taskTrackService.getUserTaskDetails(userId, startDate, endDate,
						userList, jsonArray, jsonDataRes1, isExist, projectId);

			}
			jsonDataRes.put("data", jsonDataRes1);
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

	@PostMapping("/getUserReportByUserIdorProject")
	public JSONObject getUserreportByUserIdorProject(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus)
			throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		List<JSONObject> jsonDataRes1 = new ArrayList<>();
		List<JSONObject> jsonArray = new ArrayList<>();
		Long userId = null, projectId = null;
		// Long pageSize = 50L;
		// Long pageIndex = null;

		try {
			if (requestdata.get("userId") != null && requestdata.get("userId").asText() != "") {
				userId = requestdata.get("userId").asLong();
			}

			if (requestdata.get("projectId") != null && requestdata.get("projectId").asText() != "") {
				projectId = requestdata.get("projectId").asLong();
			}

			// if (requestdata.get("pageIndex") != null &&
			// requestdata.get("pageIndex").asText() != "") {
			// pageIndex = requestdata.get("pageIndex").asLong();
			// }
			// Long startingIndex = (pageSize*pageIndex)+1;
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
			Long count = null;
			if (startDate != null && endDate != null && projectId == null && userId == null) {
				count = userService.getCount();
				// jsonDataRes.put("userCount", count);
				// userIdList =
				// userService.getUserIdLists(pageSize,startingIndex);
				userIdList = userService.getUserIdLists();
				getUserDataForReport(userIdList, startDate, endDate, jsonDataRes, jsonDataRes1, jsonArray, projectId);

			}

			else if (startDate != null && endDate != null && projectId != null && userId == null) {
				count = projectAllocationService.getUserCount(projectId);
				// jsonDataRes.put("userCount", count);
				// userIdList =
				// projectAllocationService.getUserIdByProject(projectId,pageSize,startingIndex);
				// userIdList =
				// projectAllocationService.getUserIdByProject(projectId);
				userIdList = projectAllocationService.getUserIdByProjectAndDate(projectId, startDate, endDate);

				getUserDataForReport(userIdList, startDate, endDate, jsonDataRes, jsonDataRes1, jsonArray, projectId);
			}

			else if (startDate != null && endDate != null && projectId == null && userId != null) {
				List<Object[]> userList = null;
				// jsonDataRes.put("userCount", "1");
				Boolean isExist = taskTrackService.checkIsUserExists(userId);
				System.out.println("aaaaa");
				/*
				 * List<JSONObject> jsonDataRes11 = taskTrackService.getUserTaskDetails(userId,
				 * startDate, endDate, userList, jsonArray, jsonDataRes1, isExist,projectId);
				 */
				/*
				 * List<JSONObject> jsonDataRes11 =
				 * taskTrackService.getUserTaskDetailsByuser(userId, startDate, endDate,
				 * userList, jsonArray, jsonDataRes1, isExist,projectId);
				 */
				projectList = taskTrackService.getProjectListByUserAndDate(userId, startDate, endDate);
				getUserDataForReportByProject(projectList, startDate, endDate, jsonDataRes, jsonDataRes1, jsonArray,
						userId);
				/*
				 * List<JSONObject> jsonDataRes11 =
				 * taskTrackService.getUserTaskDetailsByuser(userId, startDate, endDate,
				 * userList, jsonArray, jsonDataRes1, isExist,projectId);
				 */
			}

			else if (startDate != null && endDate != null && projectId != null && userId != null) {
				List<Object[]> userList = null;
				// jsonDataRes.put("userCount", "1");
				Boolean isExist = taskTrackService.checkExistanceOfUser(projectId, userId);
				List<JSONObject> jsonDataRes11 = taskTrackService.getUserTaskDetails(userId, startDate, endDate,
						userList, jsonArray, jsonDataRes1, isExist, projectId);

			}
			jsonDataRes.put("data", jsonDataRes1);
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

	private void getUserDataForReportByProject(List<Object[]> projectList, Date startDate, Date endDate,
			JSONObject jsonDataRes, List<JSONObject> jsonDataRes1, List<JSONObject> jsonArray, Long userId) {
		Long projectId = null;
		String projectName = null;
		for (Object[] item : projectList) {

			projectId = ((BigInteger) item[0]).longValue();
			projectName = (String) item[1];

			List<Object[]> userList = null;
			Boolean isExist = taskTrackService.checkIsUserExists(userId);
			List<JSONObject> jsonDataRes11 = taskTrackService.getUserTaskDetailsByuser(userId, startDate, endDate,
					userList, jsonArray, jsonDataRes1, isExist, projectId, projectName);

		}
	}

	private void getUserDataForReport(List<Object[]> userIdList, Date startDate, Date endDate, JSONObject jsonDataRes,
			List<JSONObject> jsonDataRes1, List<JSONObject> jsonArray, Long projectId) {

		for (Object userItem : userIdList) {

			// Long id = Long.valueOf(((BigInteger) userItem).longValue());;
			Long id = (Long) userItem;

			List<Object[]> userList = null;
			Boolean isExist = taskTrackService.checkIsUserExists(id);
			List<JSONObject> jsonDataRes11 = taskTrackService.getUserTaskDetails(id, startDate, endDate, userList,
					jsonArray, jsonDataRes1, isExist, projectId);

		}
	}

	@PostMapping("/getUserTaskReport")
	public ObjectNode getUserTaskReport(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus)
			throws ParseException {

		Long projectId = null;
		List<Object[]> userIdList = null;
		Long pageSize = 50L;
		Long pageIndex = null;
		ArrayNode jsonArray = objectMapper.createArrayNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();

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

			// if (requestdata.get("pageIndex") != null &&
			// requestdata.get("pageIndex").asText() != "") {
			// pageIndex = requestdata.get("pageIndex").asLong();
			// }
			// Long startingIndex = (pageSize*pageIndex)+1;

			// Long count = projectAllocationService.getUserCount(projectId);

			// userIdList =
			// projectAllocationService.getUserIdByProject(projectId,pageSize,startingIndex);
			// userIdList =
			// projectAllocationService.getUserIdByProject(projectId);
			userIdList = projectAllocationService.getUserIdByProjectAndDate(projectId, startDate, endDate);

			System.out.println("userIdList size : " + userIdList.size());

			List<Object[]> taskList = taskTrackService.getTaskList();
			System.out.println("taskList size : " + taskList.size());

			for (Object userItem : userIdList) {
				String billable = null;
				Boolean isBillable = false;
				Long id = (Long) userItem;
				System.out.println("id : " + id);

				isBillable = projectAllocationService.getIsBillable(id, projectId);
				if (isBillable)
					billable = "YES";
				else
					billable = "NO";

				List<Object[]> userList = null;
				Boolean isExist = taskTrackService.checkIsUserExists(id);

				if (isExist) {
					userList = taskTrackService.getUserTaskList(id, startDate, endDate, projectId);
				}

				if (userList != null && userList.size() > 0) {
					for (Object[] listItem : userList) {
						ObjectNode taskItemObject = objectMapper.createObjectNode();

						String name = (String) listItem[2];
						Double totalHours = (Double) listItem[0];
						String taskName = (String) listItem[3];

						taskItemObject.put("User", name);
						taskItemObject.put("Billable", billable);
						taskItemObject.put("Hours", totalHours);
						taskItemObject.put("Task Type", taskName);
						jsonArray.add(taskItemObject);

					}
				}

			}

			// jsonDataRes.put("userCount", count);
			jsonDataRes.set("data", jsonArray);
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

	@PostMapping(value = "/exportProjctTaskRpt")
	public ResponseEntity exportProjctAllocationRpt(@RequestBody JSONObject requestData, HttpServletResponse response) {
		ObjectNode jsonData = objectMapper.createObjectNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		Date fromDate = null, toDate = null;
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		try {
			if (!requestData.get("startDate").toString().isEmpty() && requestData.get("startDate").toString() != null) {
				String startDate = requestData.get("startDate").toString();
				if (!startDate.isEmpty()) {
					fromDate = outputFormat.parse(startDate);

				}
			}
			if (!requestData.get("endDate").toString().isEmpty() && requestData.get("endDate").toString() != null) {
				String endDate = requestData.get("endDate").toString();
				if (!endDate.isEmpty()) {
					toDate = outputFormat.parse(endDate);

				}
			}
			Long projectId = Long.parseLong(requestData.get("projectId").toString());
			List<ExportProjectTaskReportModel> exportData = reportService.getProjectTaskReportDetails(fromDate, toDate,
					projectId);
			projectExportService.exportProjectTaskReport(exportData, response);

		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("/getApprovalTimeLogReport")
	public JSONObject getApprovalTimeLogReport(@RequestBody JSONObject requestData) {
		JSONArray approvalReport = new JSONArray();
		try {
			String date1 = (String) requestData.get("startDate");
			String date2 = (String) requestData.get("endDate");

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
			int startdateIndex = cal.get(Calendar.DAY_OF_MONTH);
			int startmonthIndex = (cal.get(Calendar.MONTH) + 1);
			int startyearIndex = cal.get(Calendar.YEAR);

			cal.setTime(endDate);
			int enddateIndex = cal.get(Calendar.DAY_OF_MONTH);
			int endmonthIndex = (cal.get(Calendar.MONTH) + 1);
			int endyearIndex = cal.get(Calendar.YEAR);

			int startDateOfMonth = startdateIndex;
			int endDateOfMonth = enddateIndex;
			int month = startmonthIndex - 1;
			int endMonth = endmonthIndex;

			Map billableHourMap = new HashMap();
			List projectList = new ArrayList();
			for (int i = startmonthIndex; i <= endmonthIndex; i++) {

				if (endmonthIndex > i) {// Multiple Month
					System.out.println("Multiple Month");
					if (i == startmonthIndex)
						startDateOfMonth = startdateIndex;
					else
						startDateOfMonth = 1;
					endDateOfMonth = 31;
					month++;
				} else {
					if (i == startmonthIndex) {// Within One Month
						System.out.println("Within One Month");
						startDateOfMonth = startdateIndex;
						endDateOfMonth = enddateIndex;
						month = startmonthIndex;
					} else {// Multiple Month - Last Month
						System.out.println("Multiple Month -Last Month");
						startDateOfMonth = 1;
						endDateOfMonth = enddateIndex;
						month = endMonth;
					}
				}

				List<ApprovalTimeTrackReportModel> data = reportServiceImpl.getApprovalStatusReport(startDate, endDate,
						startDateOfMonth, endDateOfMonth, month, startyearIndex);
				JSONObject jsonData = new JSONObject();
				double actualHours = 0.0;
				for (ApprovalTimeTrackReportModel obj : data) {
					long projectId = obj.getProjectId();
					if (projectId != 35) {
						actualHours = reportService.getActualHours(projectId, startDate, endDate);

						if (projectList.contains(obj.getProjectName())) {
							for (int k = 0; k < approvalReport.size(); k++) {
								JSONObject objects = (JSONObject) approvalReport.get(k);

								if (objects.get("projectName").equals(obj.getProjectName())) {
									double hr = (double) objects.get("BillableHours");
									hr += obj.getBillableHours();
									objects.remove("BillableHours");
									objects.put("BillableHours", hr);
								}
							}
						} else {
							jsonData = new JSONObject();
							billableHourMap.put(obj.getProjectName(),
									obj.getBillableHours() != null ? obj.getBillableHours() : 0);
							if (!projectList.contains(obj.getProjectName()))
								projectList.add(obj.getProjectName());

							jsonData.put("projectName", obj.getProjectName());
							jsonData.put("BillableHours", obj.getBillableHours() != null ? obj.getBillableHours() : 0);
							// jsonData.put("LoggedHours", obj.getLoggedHours() != null ?
							// obj.getLoggedHours() : 0);
							jsonData.put("LoggedHours", actualHours);
							approvalReport.add(jsonData);
						}
					}
				}
			}
			JSONObject dataNode = new JSONObject();
			dataNode.put("approvalReport", approvalReport);

			JSONObject node = new JSONObject();
			node.put("status", "success");
			node.put("data", dataNode);
			return node;
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject node = new JSONObject();
			node.put("status", "failure");
			node.put("message", "failed. " + e);
			return node;
		}

	}

	@PostMapping("/getAllocationofTechnology")
	public ObjectNode getAllocationofTechnology(@RequestBody JSONObject requestData) {
		ObjectNode jsonData = objectMapper.createObjectNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		try {

			Long techId = Long.valueOf(requestData.get("techId").toString());
			String date1 = requestData.get("fromDate").toString();
			String date2 = requestData.get("toDate").toString();
			Long userId = null;
			Long regionId_selected = null;
			Long regionId = null;
			UserModel user = null;

			if (requestData.get("sessionId") != null && requestData.get("sessionId").toString() != "") {
				userId = Long.valueOf(requestData.get("sessionId").toString());
				user = userRepository.getOne(userId);
				regionId = user.getRegion().getId();
			}
			if (requestData.get("regionId") != null && requestData.get("regionId").toString() != "") {
				regionId_selected = Long.valueOf(requestData.get("regionId").toString());
				if (user.getRole().getroleId() == 1 || user.getRole().getroleId() == 10) {
					regionId = regionId_selected;
				}
			}

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date fromDate = null, toDate = null;
			if (!date1.isEmpty()) {
				fromDate = outputFormat.parse(date1);
			}
			if (!date2.isEmpty()) {
				toDate = outputFormat.parse(date2);
			}

			// List<Object[]> allocReport =
			// reportServiceImpl.getAllocationDetailsTechWise(techId, fromDate, toDate);
			List<Object[]> allocReport = reportService.getAllocationDetailsTechWiseRegionwise(techId, fromDate, toDate,
					regionId);
			ArrayNode jsonArray = objectMapper.createArrayNode();
			for (Object[] item : allocReport) {
				userId = Long.valueOf((String.valueOf(item[0])));
				ObjectNode jsonObject = objectMapper.createObjectNode();
				jsonObject.put("userId", userId);
				jsonObject.put("userName", (String) item[1]);
				jsonObject.put("techName", (String) item[2]);

				ArrayNode projectarray = objectMapper.createArrayNode();
				List<Object[]> projectList = reportServiceImpl.getAllocatedProjectByUserId(userId, fromDate, toDate);

				Date minStartDate = null;
				Date maxEndDate = null;
				Date startDate;
				Date endDate;
				String projectNames = "";
				SimpleDateFormat outputFormats = new SimpleDateFormat("dd-MMM-yyyy");
				int projectCount = projectList.size();
				for (Object[] projects : projectList) {
					startDate = (Date) projects[2];
					endDate = (Date) projects[3];
					if (minStartDate == null) {
						minStartDate = startDate;
					} else {
						if ((startDate.compareTo(minStartDate) < 0)) {
							minStartDate = startDate;
						}
					}
					if (maxEndDate == null) {
						maxEndDate = endDate;
					} else {
						if ((endDate.compareTo(maxEndDate) > 0)) {
							maxEndDate = endDate;
						}
					}

					projectNames += projects[4] + " " + outputFormats.format(projects[2]) + "-"
							+ outputFormats.format(projects[3]) + " | ";

					ObjectNode responseData = objectMapper.createObjectNode();
					responseData.put("projectName", (String) projects[4]);
					responseData.put("allocPercent", (double) projects[1]);
					responseData.put("billable", (boolean) projects[0]);
					responseData.put("startDate", (String.valueOf(projects[2])));
					responseData.put("endDate", (String.valueOf(projects[3])));
					projectarray.add(responseData);

				}
				jsonObject.set("projectList", projectarray);
				jsonObject.put("projectData", projectNames);
				jsonObject.put("startDate", String.valueOf(minStartDate));
				jsonObject.put("endDate", String.valueOf(maxEndDate));
				jsonArray.add(jsonObject);

			}

			jsonDataRes.set("data", jsonArray);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("message", "success");
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("message", "failed. " + e);

		}
		return jsonDataRes;
	}

	@PostMapping(value = "/exportFinanceData")
	public ResponseEntity exportFinanceData(@RequestBody JsonNode requestdata, HttpServletResponse response) {
		ObjectNode jsonData = objectMapper.createObjectNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		Date fromDate = null, toDate = null;
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
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
			int i = 0;
			Workbook workrbook = new XSSFWorkbook();
			for (JsonNode rangenode : range) {
				JSONObject node = new JSONObject();
				month = Integer.parseInt(rangenode.get("month").toString());
				year = Integer.parseInt(rangenode.get("year").toString());
				String monthName = Month.of(month).name();

				if (month != 0 && year != 0 && projectId != null && userId == null) {

					String projectName = projectService.getProjectName(projectId);
					Sheet sheet = workrbook.createSheet(monthName + "-" + year);
					String nameofReport = "Report Of project " + projectName;
					projectExportService.exportFinanceDataByProject(workrbook, sheet, nameofReport, month, year,
							projectId, projectName);

				} else if (month != 0 && year != 0 && projectId == null && userId != null) {

					String name = userService.getUserName(userId);
					String userName = String.valueOf(name).replace(",", " ");
					Sheet sheet = workrbook.createSheet(monthName + "-" + year);
					String nameofReport = "Report Of user " + userName;
					projectExportService.exportFinanceDataByUser(workrbook, sheet, nameofReport, month, year, userId,
							userName);

				} else if (month != 0 && year != 0 && projectId != null && userId != null) {

					String name = userService.getUserName(userId);
					String userName = String.valueOf(name).replace(",", " ");
					String projectName = projectService.getProjectName(projectId);
					Sheet sheet = workrbook.createSheet(monthName + "-" + year);
					String nameofReport = "Report Of user " + userName + " of" + " project " + projectName;
					projectExportService.exportFinanceDataByUserAndProject(workrbook, sheet, nameofReport, month, year,
							userId, projectId);

				} else if (month != 0 && year != 0 && projectId == null && userId == null) {
					System.out.println("financeData=------------------>");
					Sheet sheet = workrbook.createSheet(monthName + "-" + year);
					String nameofReport = "Report of " + monthName + "-" + year;
					projectExportService.exportFinanceDataByMonthAndYear(workrbook, sheet, nameofReport, month, year);

				}
				// outputdata.put(i, node);
				// i = i + 1;

			}
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
			response.setHeader("Content-Disposition", "filename=\"" + "FinanceData.xlsx" + "\"");
			workrbook.write(response.getOutputStream());
			workrbook.close();

		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity(HttpStatus.OK);
	}

	// Renjith
	@PostMapping(value = "/exportFinanceDataConsolidated")
	public ResponseEntity exportFinanceDataConsolidated(@RequestBody JsonNode requestdata,
			HttpServletResponse response) {
		ObjectNode jsonData = objectMapper.createObjectNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		Date fromDate = null, toDate = null;
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		Long projectId = null;
		Long userId = null;
		int month = 0;
		int year = 0;
		Long roleId = null;
		Long regionId = null;
		boolean isLevels = true;
		Set<Long> projSet = null;
		List<ProjectModel> projectList = new ArrayList<ProjectModel>();
		try {

			if (requestdata.get("sessionId") != null && requestdata.get("sessionId").asText() != "") {
				userId = requestdata.get("sessionId").asLong();
			}

			ArrayNode range = (ArrayNode) requestdata.get("range");
			int i = 0;
			Workbook workrbook = new XSSFWorkbook();
			roleId = userService.getUserDetailsById(userId).getRole().getroleId();
			regionId = userService.getUserdetailsbyId(userId).getRegion().getId();
			if (roleId == 1) {

				projectList = projectService.getProjectList();
				isLevels = false;
			}

			if (roleId == 4 | roleId == 5 | roleId == 6 | roleId == 9 | roleId == 10) {
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
					String monthName = Month.of(month).name();

					if (month != 0 && year != 0 && projSet != null && projSet.size() != 0) {

						String projectName = projectService.getProjectName(projectId);
						Sheet sheet = workrbook.createSheet(monthName + "-" + year);
						String nameofReport = "Project Report  Consolidated";
						projectExportService.exportFinanceDataByProjectSet(workrbook, sheet, nameofReport, month, year,
								projSet);

					}

				}
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
				response.setHeader("Content-Disposition", "filename=\"" + "FinanceData.xlsx" + "\"");
				workrbook.write(response.getOutputStream());
				workrbook.close();
				return new ResponseEntity(HttpStatus.OK);

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
					String monthName = Month.of(month).name();

					if (month != 0 && year != 0 && projSet != null && projSet.size() != 0) {

						String projectName = projectService.getProjectName(projectId);
						Sheet sheet = workrbook.createSheet(monthName + "-" + year);
						String nameofReport = "Project Report  Consolidated";
						projectExportService.exportFinanceDataByProjectSet(workrbook, sheet, nameofReport, month, year,
								projSet);

					}

				}
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
				response.setHeader("Content-Disposition", "filename=\"" + "FinanceData.xlsx" + "\"");
				workrbook.write(response.getOutputStream());
				workrbook.close();
			}

		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity(HttpStatus.OK);
	}

	// Renjith

	/***
	 * @des export bench report
	 * @param requestdata
	 * @param httpstatus
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	@PostMapping("/exportBenchReport")
	public void exportBenchReport(@RequestBody JsonNode requestdata, HttpServletResponse response)
			throws IOException, ParseException {

		String startdate = requestdata.get("fromDate").asText();
		String enddate = requestdata.get("toDate").asText();
		Date fromDate = null, toDate = null;
		Long regionId = null;
		Long userId = null;
		if (requestdata.get("regionId").asText() != null && requestdata.get("regionId").asText() != "") {
			regionId = requestdata.get("regionId").asLong();
		}
		if (requestdata.get("uId").asText() != null && requestdata.get("uId").asText() != "") {
			userId = requestdata.get("uId").asLong();
		}
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (!startdate.isEmpty()) {
			fromDate = outputFormat.parse(startdate);
		}
		if (!enddate.isEmpty()) {
			toDate = outputFormat.parse(enddate);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);

		int monthIndex = (cal.get(Calendar.MONTH) + 1);
		int yearIndex = cal.get(Calendar.YEAR);
		List<BenchProjectReportModel> benchProjectReport = null;
		if (userId != null && regionId == null) {
			System.out.println("if");
			benchProjectReport = reportServiceImpl.getBenchProjectReportDetailsReport(userId, fromDate, toDate);
		} else if (userId == null && regionId != null) {
			System.out.println("else if");
			benchProjectReport = reportServiceImpl.getBenchProjectReportDetailsReport(userId, fromDate, toDate,
					regionId);
		} else {
			System.out.println("else");
			benchProjectReport = reportServiceImpl.getBenchProjectReportDetailsReport(fromDate, toDate);
		}
		Workbook workrbook = new XSSFWorkbook();
		Sheet sheet = workrbook.createSheet("Bench Report");
		String nameofReport = "BENCH REPORT";
		projectExportService.exportBenchReport(workrbook, sheet, nameofReport, benchProjectReport);
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
		response.setHeader("Content-Disposition", "filename=\"" + "Bench Report.xlsx" + "\"");
		workrbook.write(response.getOutputStream());
		workrbook.close();

	}

	/**
	 * @author sreejith.j
	 * @param month
	 * @param year
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value = "getSubmissionDataForFinance")
	public ResponseEntity<Object> getSubmissionDataForFinance(@RequestParam("month") Integer month,
			@RequestParam("year") Integer year, @RequestParam(name = "regionId", required = false) Integer regionId,
			@RequestParam("sessionId") Integer sessionId) {
		ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
		JSONObject jsonDataRes = new JSONObject();
		try {
			List<ProjectSubmissionDataDTO> submittedDataList = new ArrayList<ProjectSubmissionDataDTO>();
			if (regionId == null || regionId == 0) {
				UserModel user = userRepository.getOne(Long.valueOf(sessionId));
				regionId = (int) user.getRegion().getId();
			}
			if (regionId == null || regionId == 0) {
				throw new BadInputException("RegionId not found for the login user.");
			}
			submittedDataList = reportService.getProjectSubmissionDetails(month, year, Long.valueOf(regionId));
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
			jsonDataRes.put("data", submittedDataList);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.OK);
		} catch (BadInputException e) {
			// TODO: handle exception
			e.printStackTrace();
			jsonDataRes.put("status", "Error");
			jsonDataRes.put("code", HttpServletResponse.SC_BAD_REQUEST);
			jsonDataRes.put("message", e.getMessage());
			response = new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			jsonDataRes.put("status", "Error");
			jsonDataRes.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			jsonDataRes.put("message", e.getMessage());
			response = new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	/**
	 * @author sreejith.j
	 * @param month
	 * @param year
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value = "getUsersSubmissionDataForFinance")
	public ResponseEntity<Object> getUsersSubmissionDataForFinance(@RequestParam("pId") Long projectId,
			@RequestParam("pTyre") Integer projectTyre, @RequestParam("uId") Long userId,
			@RequestParam("month") Integer month, @RequestParam("year") Integer year,
			@RequestParam("ses") String session) {
		ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
		JSONObject jsonDataRes = new JSONObject();
		try {
			HashMap<String, Object> submittedDataList = reportService.getUsersProjectSubmissionDetails(projectId,
					projectTyre, userId, month, year, session);
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
			jsonDataRes.put("data", submittedDataList);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
			jsonDataRes.put("message", e.getMessage());
			response = new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	/***
	 * @author drishya dinesh
	 * @param requestdata
	 * @param response
	 * @des export projectwise submission data
	 * @return
	 */
	@PostMapping(value = "/exportProjectWiseSubmissionData")
	public ResponseEntity exportProjectWiseSubmissionData(@RequestBody JsonNode requestdata,
			HttpServletResponse response) {
		JSONObject jsonDataRes = new JSONObject();
		long projectId = 0;
		long regionId = 1;
		long userId = 0;
		long sessionId = 0;
		int month = 0;
		int year = 0;

		try {

			if (requestdata.get("projectId") != null && requestdata.get("projectId").asText() != "") {
				projectId = requestdata.get("projectId").asLong();
			}
			if (requestdata.get("userId") != null && requestdata.get("userId").asText() != "") {
				userId = requestdata.get("userId").asLong();
			}
			if (requestdata.get("regionId") != null && requestdata.get("regionId").asText() != "") {
				regionId = 1;//requestdata.get("regionId").asLong();
			}
			if (requestdata.get("sessionId") != null && requestdata.get("sessionId").asText() != "") {
				sessionId = requestdata.get("sessionId").asLong();
			}

			UserModel loggedUser = userService.getUserdetailsbyId(sessionId);

			if (loggedUser.getRole().getroleId() == 6) {

				regionId = loggedUser.getRegion().getId();
			}
			ArrayNode range = (ArrayNode) requestdata.get("range");

			JSONObject outputdata = new JSONObject();

			ArrayList<JSONObject> resultData = new ArrayList<JSONObject>();
			ArrayList<JSONObject> node1 = new ArrayList<JSONObject>();

			List<Object[]> result = new ArrayList<Object[]>();

			ProjectModel project = projectService.findById(projectId);
			String nameofReport = "Report Of project " + project.getProjectName();
			Workbook workrbook = new XSSFWorkbook();
			if (project.getProjectTier() == 1) {

				for (JsonNode rangenode : range) {
					JSONObject node = new JSONObject();
					month = Integer.parseInt(rangenode.get("month").toString());
					year = Integer.parseInt(rangenode.get("year").toString());
					String monthName = Month.of(month).name();

					if (month != 0 && year != 0) {
						Sheet sheet = workrbook.createSheet(monthName + "-" + year);
						result = tasktrackApprovalService.getProjectWiseSubmissionDetailsTierOne(month, year, projectId,
								userId, regionId);
						projectExportService.exportBillingProjectWise(workrbook, sheet, nameofReport, month, year,
								result);
						node.put("timeTracks", resultData);
						node.put("month", month);
						node.put("year", year);
						node1.add(node);

					}

				}
			} else if (project.getProjectTier() == 2) {
				for (JsonNode rangenode : range) {
					JSONObject node = new JSONObject();
					month = Integer.parseInt(rangenode.get("month").toString());
					year = Integer.parseInt(rangenode.get("year").toString());
					String monthName = Month.of(month).name();

					if (month != 0 && year != 0) {
						Sheet sheet = workrbook.createSheet(monthName + "-" + year);
						result = tasktrackApprovalService.getProjectWiseSubmissionDetailsTierTwo(month, year, projectId,
								userId, regionId);
						projectExportService.exportBillingProjectWise(workrbook, sheet, nameofReport, month, year,
								result);
						node.put("timeTracks", resultData);
						node.put("month", month);
						node.put("year", year);
						node1.add(node);

					}

				}
			}

			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
			response.setHeader("Content-Disposition", "filename=\"" + "BillingProjectWise.xlsx" + "\"");
			workrbook.write(response.getOutputStream());
			workrbook.close();

		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
			// e.printStackTrace();
		}
		return new ResponseEntity(HttpStatus.OK);
	}

	/***
	 * @author drishya dinesh
	 * @param requestdata
	 * @param response
	 * @des export projectwise submission data
	 * @return
	 */
	@PostMapping(value = "/exportUserWiseSubmissionData")
	public ResponseEntity exportUserWiseSubmissionData(@RequestBody JsonNode requestdata,
			HttpServletResponse response) {
		JSONObject jsonDataRes = new JSONObject();
		long projectId = 0;
		long regionId = 0;
		long sessionId = 0;
		long userId = 0;
		int month = 0;
		int year = 0;

		try {

			if (requestdata.get("projectId") != null && requestdata.get("projectId").asText() != "") {
				projectId = requestdata.get("projectId").asLong();
			}
			if (requestdata.get("userId") != null && requestdata.get("userId").asText() != "") {
				userId = requestdata.get("userId").asLong();
			}
			if (requestdata.get("regionId") != null && requestdata.get("regionId").asText() != "") {
				regionId = requestdata.get("regionId").asLong();
			}
			if (requestdata.get("sessionId") != null && requestdata.get("sessionId").asText() != "") {
				sessionId = requestdata.get("sessionId").asLong();
			}

			UserModel loggedUser = userService.getUserdetailsbyId(sessionId);

			if (loggedUser.getRole().getroleId() == 6) {

				regionId = loggedUser.getRegion().getId();
			}
			ArrayNode range = (ArrayNode) requestdata.get("range");

			JSONObject outputdata = new JSONObject();

			ArrayList<JSONObject> resultData = new ArrayList<JSONObject>();
			ArrayList<JSONObject> node1 = new ArrayList<JSONObject>();

			List<Object[]> result = new ArrayList<Object[]>();

			UserModel user = userService.getUserDetailsById(userId);
			String nameofReport = "Report Of User " + user.getLastName() + " " + user.getFirstName();
			Workbook workrbook = new XSSFWorkbook();
			for (JsonNode rangenode : range) {
				JSONObject node = new JSONObject();
				month = Integer.parseInt(rangenode.get("month").toString());
				year = Integer.parseInt(rangenode.get("year").toString());
				String monthName = Month.of(month).name();

				if (month != 0 && year != 0) {
					Sheet sheet = workrbook.createSheet(monthName + "-" + year);
					result = tasktrackApprovalService.getUserWiseSubmissionDetailsExport(month, year, projectId, userId,
							regionId);
					projectExportService.exportBillingProjectWise(workrbook, sheet, nameofReport, month, year, result);
					node.put("timeTracks", resultData);
					node.put("month", month);
					node.put("year", year);
					node1.add(node);

				}

			}
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
			response.setHeader("Content-Disposition", "filename=\"" + "BillingUserWise.xlsx" + "\"");
			workrbook.write(response.getOutputStream());
			workrbook.close();

		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);

		}
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping(value = "getFreeAllocationReport")
	public ResponseEntity<Object> getFreeAllocationReport(@RequestBody Taskdetails requestdata,
			HttpServletResponse httpstatus) {
		ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
		JSONObject jsonDataRes = new JSONObject();
		JSONObject freeAllocationData = new JSONObject();
		try {
			Date fromDate = null, toDate = null;
			Long regionId_selected = null;
			fromDate = requestdata.getFromDate();
			toDate = requestdata.getToDate();

			if (requestdata.getRegionId() != null && requestdata.getRegionId() != 0) {
				regionId_selected = requestdata.getRegionId();
			}
			freeAllocationData = reportService.getFreeAllocationReport(regionId_selected, fromDate, toDate);
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
			jsonDataRes.put("data", freeAllocationData);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
			jsonDataRes.put("message", e.getMessage());
			response = new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	/**
	 * @author sreejith.j
	 * @param requestData
	 * @return
	 */
	@PostMapping(value = "getRegionLeaveReport")
	public ResponseEntity<Object> getRegionLeaveReport(@RequestBody JSONObject requestData) {
		ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
		JSONObject responseObj = new JSONObject();
		try {
			Integer month = Integer.parseInt(requestData.get("month").toString());
			Integer year = Integer.parseInt(requestData.get("year").toString());
			Integer regionId = Integer.parseInt(requestData.get("region").toString());

			List<HashMap<String, Object>> regionLeaves = reportService.getRegionLeaves(regionId, month, year);

			responseObj.put("status", "Success");
			responseObj.put("code", HttpServletResponse.SC_OK);
			responseObj.put("data", regionLeaves);
			response = new ResponseEntity<Object>(responseObj, HttpStatus.OK);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			responseObj.put("status", "Error");
			responseObj.put("code", HttpServletResponse.SC_BAD_REQUEST);
			responseObj.put("message", e.getMessage());
			response = new ResponseEntity<Object>(responseObj, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.put("status", "Error");
			responseObj.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			responseObj.put("message", e.getMessage());
			response = new ResponseEntity<Object>(responseObj, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	/**
	 * @author sreejith.j
	 * @param projectId
	 * @param projectTyre
	 * @param userId
	 * @param month
	 * @param year
	 * @param session
	 * @return
	 */
	@GetMapping(value = "getFullReportSubmissionForFinance")
	public ResponseEntity<Object> getFullReportSubmissionForFinance(
			@RequestParam(value = "pId", required = false) Long projectId,
			@RequestParam(value = "pTyre", required = false) Integer projectTyre,
			@RequestParam(value = "uId", required = false) Long userId, @RequestParam("month") Integer month,
			@RequestParam("year") Integer year) {
		ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
		JSONObject jsonDataRes = new JSONObject();
		try {
			List<Map<String, Object>> submittedDataList = reportService
					.getSubmissionDetailsForFinanceFullReport(projectId, projectTyre, userId, month, year, "FULL");
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
			jsonDataRes.put("data", submittedDataList);
			response = new ResponseEntity<Object>(jsonDataRes, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "Success");
			jsonDataRes.put("code", HttpServletResponse.SC_OK);
			jsonDataRes.put("message", e.getMessage());
			response = new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

}
