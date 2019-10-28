package com.EMS.controller;

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

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.dto.Taskdetails;
import com.EMS.model.ApprovalTimeTrackReportModel;
import com.EMS.model.ExportProjectTaskReportModel;
import com.EMS.model.ProjectModel;
import com.EMS.service.ProjectAllocationService;
import com.EMS.service.ProjectExportService;
import com.EMS.service.ProjectRegionService;
import com.EMS.service.ReportService;
import com.EMS.service.ReportServiceImpl;
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

	@PostMapping("/getProjectReport")
	public JsonNode getProjectReport(@RequestBody Taskdetails requestdata) {

		ArrayNode projectReport = reportServiceImpl.getProjectReportDetails(requestdata.getProjectId(),
				requestdata.getFromDate(), requestdata.getToDate());

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
				 * List<JSONObject> jsonDataRes11 =
				 * taskTrackService.getUserTaskDetails(userId, startDate,
				 * endDate, userList, jsonArray, jsonDataRes1,
				 * isExist,projectId);
				 */
				/*
				 * List<JSONObject> jsonDataRes11 =
				 * taskTrackService.getUserTaskDetailsByuser(userId, startDate,
				 * endDate, userList, jsonArray, jsonDataRes1,
				 * isExist,projectId);
				 */
				projectList = taskTrackService.getProjectListByUserAndDate(userId, startDate, endDate);
				getUserDataForReportByProject(projectList, startDate, endDate, jsonDataRes, jsonDataRes1, jsonArray,
						userId);
				/*
				 * List<JSONObject> jsonDataRes11 =
				 * taskTrackService.getUserTaskDetailsByuser(userId, startDate,
				 * endDate, userList, jsonArray, jsonDataRes1,
				 * isExist,projectId);
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
				for (ApprovalTimeTrackReportModel obj : data) {

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
						jsonData.put("LoggedHours", obj.getLoggedHours() != null ? obj.getLoggedHours() : 0);
						approvalReport.add(jsonData);
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

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date fromDate = null, toDate = null;
			if (!date1.isEmpty()) {
				fromDate = outputFormat.parse(date1);
			}
			if (!date2.isEmpty()) {
				toDate = outputFormat.parse(date2);
			}

			List<Object[]> allocReport = reportServiceImpl.getAllocationDetailsTechWise(techId, fromDate, toDate);
			ArrayNode jsonArray = objectMapper.createArrayNode();
			for (Object[] item : allocReport) {
				Long userId = Long.valueOf((String.valueOf(item[0])));
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
					String monthName = Month.of(month).name();

					if (month != 0 && year != 0 && projSet != null && projSet.size() != 0) {

						String projectName = projectService.getProjectName(projectId);
						Sheet sheet = workrbook.createSheet(monthName + "-" + year);
						String nameofReport = "Report Of project " + projectName;
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
								String nameofReport = "Report Of project " + projectName;
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
}
