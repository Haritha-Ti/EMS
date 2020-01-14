package com.EMS.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.dto.WeeklyTaskTrackWithTaskRequestDTO;
import com.EMS.dto.WeeklyTaskTrackWithoutTaskRequestDTO;
import com.EMS.model.ProjectModel;
import com.EMS.model.StatusResponse;
import com.EMS.model.TaskTrackWeeklyApproval;
import com.EMS.model.Tasktrack;
import com.EMS.model.UserModel;
import com.EMS.repository.TaskWeeklyApprovalRepository;
import com.EMS.repository.TasktrackRepository;
import com.EMS.utility.Constants;
import com.EMS.utility.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class TaskWeeklyApprovalServiceImpl implements TaskWeeklyApprovalService {

	@Autowired
	private TaskWeeklyApprovalRepository taskWeeklyApprovalRepository;

	@Autowired
	private UserService userservice;

	@Autowired
	private TasktrackRepository tasktrackRepository;

	@Autowired
	private ProjectService projectservice;

	@Override
	public StatusResponse submitWeeklyApproval(JSONObject requestData) throws ParseException, Exception {

		TaskTrackWeeklyApproval weeklyApproval = new TaskTrackWeeklyApproval();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int requeststatus = 0;
		StatusResponse response = new StatusResponse();

		if (((!requestData.get("startDate").toString().equals(null))
				|| (!requestData.get("startDate").toString().equals(" ")))
				&& ((!requestData.get("endDate").toString().equals(null))
						|| (!requestData.get("endDate").toString().equals(" ")))) {
			weeklyApproval.setStartDate(sdf.parse(requestData.get("startDate").toString()));
			weeklyApproval.setEndDate(sdf.parse(requestData.get("endDate").toString()));

		} else
			requeststatus = 1;

		Long userId = Long.parseLong(requestData.get("userId").toString());
		UserModel userInfo = userservice.getUserdetailsbyId(userId);

		if (!userInfo.equals(null))
			weeklyApproval.setUser(userInfo);
		else
			requeststatus = 1;

		weeklyApproval = taskWeeklyApprovalRepository.getduplicateentrycount(weeklyApproval.getStartDate(),
				weeklyApproval.getEndDate(), weeklyApproval.getUser().getUserId());

		Map<Date, Integer> timetrack = (Map<Date, Integer>) requestData.get("timetrack");

		Map<Object, Object> result = timetrack.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors
				.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		Iterator iter = result.entrySet().iterator();
		int counting = 0;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Double hours = Double.parseDouble(entry.getValue().toString());
			if (counting == 0)
				weeklyApproval.setDay1(hours);
			if (counting == 1)
				weeklyApproval.setDay2(hours);
			if (counting == 2)
				weeklyApproval.setDay3(hours);
			if (counting == 3)
				weeklyApproval.setDay4(hours);
			if (counting == 4)
				weeklyApproval.setDay5(hours);
			if (counting == 5)
				weeklyApproval.setDay6(hours);
			if (counting == 6)
				weeklyApproval.setDay7(hours);
			counting++;
		}

		weeklyApproval.setYear(Integer.parseInt(requestData.get("year").toString()));

		if ((!weeklyApproval.getDay1().equals(null)) && (!weeklyApproval.getDay2().equals(null))
				&& (!weeklyApproval.getDay3().equals(null)) && (!weeklyApproval.getDay4().equals(null))
				&& (!weeklyApproval.getDay5().equals(null)) && (!weeklyApproval.getDay6().equals(null))
				&& (!weeklyApproval.getDay7().equals(null)) && (!weeklyApproval.getYear().equals(null))) {

			if ((weeklyApproval.getDay1() < 0) || (weeklyApproval.getDay1() > 24) || (weeklyApproval.getDay2() < 0)
					|| (weeklyApproval.getDay2() > 24) || (weeklyApproval.getDay3() < 0)
					|| (weeklyApproval.getDay3() > 24) || (weeklyApproval.getDay4() < 0)
					|| (weeklyApproval.getDay4() > 24) || (weeklyApproval.getDay5() < 0)
					|| (weeklyApproval.getDay5() > 24) || (weeklyApproval.getDay6() < 0)
					|| (weeklyApproval.getDay6() > 24) || (weeklyApproval.getDay7() < 0)
					|| (weeklyApproval.getDay7() > 24) || weeklyApproval.getYear() <= 0)
				requeststatus = 1;
		} else
			requeststatus = 1;

		weeklyApproval.setUserSubmittedDate(new Date());

		Long projectId = Long.parseLong(requestData.get("projectId").toString());
		ProjectModel projectInfo = projectservice.findById(projectId);

		if (!projectInfo.equals(null))
			weeklyApproval.setProject(projectInfo);
		else
			requeststatus = 1;

		weeklyApproval.setTimetrackStatus(Constants.TASKTRACK_USER_STATUS_SUBMIT);

		if (requeststatus == 0) {

			taskWeeklyApprovalRepository.save(weeklyApproval);
			response = new StatusResponse("Success", 200, "Insertion completed");

		} else {
			response = new StatusResponse("Success", 200, "Insertion failed due to invalid credientials");
		}
		return response;

	}

	@Override
	public StatusResponse saveWeeklyApproval(JSONObject requestData) throws ParseException {

		StatusResponse response = new StatusResponse();
		TaskTrackWeeklyApproval weeklyApproval = new TaskTrackWeeklyApproval();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int requeststatus = 0;

		if (((!requestData.get("startDate").toString().equals(null))
				|| (!requestData.get("startDate").toString().equals(" ")))
				&& ((!requestData.get("endDate").toString().equals(null))
						|| (!requestData.get("endDate").toString().equals(" ")))) {
			weeklyApproval.setStartDate(sdf.parse(requestData.get("startDate").toString()));
			weeklyApproval.setEndDate(sdf.parse(requestData.get("endDate").toString()));

		} else
			requeststatus = 1;
		
		Long userId = Long.parseLong(requestData.get("userId").toString());
		UserModel userInfo = userservice.getUserdetailsbyId(userId);

		if (!userInfo.equals(null))
			weeklyApproval.setUser(userInfo);
		else
			requeststatus = 1;
		
		weeklyApproval = taskWeeklyApprovalRepository.getduplicateentrycount(weeklyApproval.getStartDate(),
				weeklyApproval.getEndDate(), weeklyApproval.getUser().getUserId());
		
		Map<Date, Integer> timetrack = (Map<Date, Integer>) requestData.get("timetrack");

		Map<Object, Object> result = timetrack.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors
				.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		Iterator iter = result.entrySet().iterator();
		int counting = 0;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Double hours = Double.parseDouble(entry.getValue().toString());
			if (counting == 0)
				weeklyApproval.setDay1(hours);
			if (counting == 1)
				weeklyApproval.setDay2(hours);
			if (counting == 2)
				weeklyApproval.setDay3(hours);
			if (counting == 3)
				weeklyApproval.setDay4(hours);
			if (counting == 4)
				weeklyApproval.setDay5(hours);
			if (counting == 5)
				weeklyApproval.setDay6(hours);
			if (counting == 6)
				weeklyApproval.setDay7(hours);
			counting++;
		}
		weeklyApproval.setYear(Integer.parseInt(requestData.get("year").toString()));

		if ((!weeklyApproval.getDay1().equals(null)) && (!weeklyApproval.getDay2().equals(null))
				&& (!weeklyApproval.getDay3().equals(null)) && (!weeklyApproval.getDay4().equals(null))
				&& (!weeklyApproval.getDay5().equals(null)) && (!weeklyApproval.getDay6().equals(null))
				&& (!weeklyApproval.getDay7().equals(null)) && (!weeklyApproval.getYear().equals(null))) {

			if ((weeklyApproval.getDay1() < 0) || (weeklyApproval.getDay1() > 24) || (weeklyApproval.getDay2() < 0)
					|| (weeklyApproval.getDay2() > 24) || (weeklyApproval.getDay3() < 0)
					|| (weeklyApproval.getDay3() > 24) || (weeklyApproval.getDay4() < 0)
					|| (weeklyApproval.getDay4() > 24) || (weeklyApproval.getDay5() < 0)
					|| (weeklyApproval.getDay5() > 24) || (weeklyApproval.getDay6() < 0)
					|| (weeklyApproval.getDay6() > 24) || (weeklyApproval.getDay7() < 0)
					|| (weeklyApproval.getDay7() > 24) || weeklyApproval.getYear() <= 0)
				requeststatus = 1;
		} else
			requeststatus = 1;


		Long projectId = Long.parseLong(requestData.get("projectId").toString());
		ProjectModel projectInfo = projectservice.findById(projectId);

		if (!projectInfo.equals(null))
			weeklyApproval.setProject(projectInfo);
		else
			requeststatus = 1;
		weeklyApproval.setTimetrackStatus(Constants.TASKTRACK_USER_STATUS_SAVED);

		if (requeststatus == 0) {

				taskWeeklyApprovalRepository.save(weeklyApproval);
				response = new StatusResponse("Success", 200, "Insertion completed");
		
		} else
			response = new StatusResponse("Success", 200, "Insertion failed due to invalid credientials");

		return response;

	}

	@SuppressWarnings("unchecked")
	@Override
	public StatusResponse getWeeklyTasktrack(WeeklyTaskTrackWithoutTaskRequestDTO requestData)
			throws Exception, ParseException {
		JSONObject response = new JSONObject();
		StatusResponse responseFinal;
		Long userId = null;
		Long projectId = null;
		Date startDate = null;
		Date endDate = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (requestData.getuId() != null) {
			userId = requestData.getuId();
		}
		if (requestData.getProjectId() != null) {
			projectId = requestData.getProjectId();
		}
		if (requestData.getStartDate() != null && !requestData.getStartDate().isEmpty()) {
			startDate = sdf.parse(requestData.getStartDate());
		}
		if (requestData.getEndDate() != null && !requestData.getEndDate().isEmpty()) {
			endDate = sdf.parse(requestData.getEndDate());
		}

		String[] taskStatusArray = { Constants.TaskTrackWeeklyApproval.TASKTRACK_WEEKLY_APPROVER_STATUS_APPROVED };
		List<String> taskStatusList = Arrays.asList(taskStatusArray);
		TaskTrackWeeklyApproval weeklyTasktrack = taskWeeklyApprovalRepository.getWeeklyTasktrack(startDate, endDate,
				userId, projectId);

		if (weeklyTasktrack != null) {
			String approver1Status = weeklyTasktrack.getApprover1Status();
			String approver2Status = weeklyTasktrack.getApprover2Status();
			String financeStatus = weeklyTasktrack.getFinanceStatus();

			if (taskStatusList.contains(approver1Status) || taskStatusList.contains(approver2Status)
					|| taskStatusList.contains(financeStatus)) {

				response.put("enabled", false);
			} else {
				response.put("enabled", true);
			}
			
			String approver = weeklyTasktrack.getApprover1Id().getFirstName()+" "+ weeklyTasktrack.getApprover1Id().getLastName();
			Date approver1SubmittedDate = weeklyTasktrack.getApprover1SubmittedDate();
			
			JSONObject approvalObj = new JSONObject();
			approvalObj.put("approver", approver);
			approvalObj.put("date", sdf.format(approver1SubmittedDate));
			approvalObj.put("status", approver1Status);
			
			response.put("approval", approvalObj);
			
			List<Date> datesInRange = DateUtil.getDatesBetweenTwo(startDate, endDate);
			datesInRange.add(endDate);

			JSONArray array = new JSONArray();
			array = addHoursandDaytoArray(array, weeklyTasktrack.getDay1(), datesInRange.get(0));
			array = addHoursandDaytoArray(array, weeklyTasktrack.getDay2(), datesInRange.get(1));
			array = addHoursandDaytoArray(array, weeklyTasktrack.getDay3(), datesInRange.get(2));
			array = addHoursandDaytoArray(array, weeklyTasktrack.getDay4(), datesInRange.get(3));
			array = addHoursandDaytoArray(array, weeklyTasktrack.getDay5(), datesInRange.get(4));
			array = addHoursandDaytoArray(array, weeklyTasktrack.getDay6(), datesInRange.get(5));
			array = addHoursandDaytoArray(array, weeklyTasktrack.getDay7(), datesInRange.get(6));
			response.put("taskList", array);
			responseFinal = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, response);
		} else {
			responseFinal = new StatusResponse(Constants.ERROR, Constants.ERROR_CODE, "No Data Available");

		}
		return responseFinal;
	}

	public JSONArray addHoursandDaytoArray(JSONArray array, Double day, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		JSONObject hour = new JSONObject();
		hour.put("hour", day);
		JSONObject finalDay = new JSONObject();
		finalDay.put(sdf.format(date), hour);
		array.add(finalDay);
		return array;

	}

	@SuppressWarnings("unchecked")
	@Override
	public StatusResponse getWeeklyTasktrackWithTask(WeeklyTaskTrackWithTaskRequestDTO requestData) throws Exception {
		StatusResponse response = new StatusResponse();
		Long userId = null;
		Long projectId = null;
		Date startDate = null;
		Date endDate = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (requestData.getuId() != null) {
			userId = requestData.getuId();
		}
		if (requestData.getProjectId() != null) {
			projectId = requestData.getProjectId();
		}
		if (requestData.getStartDate() != null && !requestData.getStartDate().isEmpty()) {
			startDate = sdf.parse(requestData.getStartDate());
		}
		if (requestData.getEndDate() != null && !requestData.getEndDate().isEmpty()) {
			endDate = sdf.parse(requestData.getEndDate());
		}

		List<Tasktrack> tasktrackList = tasktrackRepository
				.findByUserUserIdAndProjectProjectIdAndDateBetweenOrderByDateAsc(userId, projectId, startDate, endDate);

		TaskTrackWeeklyApproval tasktrackStatus = taskWeeklyApprovalRepository

				.findByUserUserIdAndProjectProjectIdInAndStartDateEqualsAndEndDateEquals(userId,
						projectId, startDate, endDate);


		JSONArray taskList = new JSONArray();
		HashSet<Date> trackdate = new HashSet<Date>();
		for (Tasktrack tasktrackOuter : tasktrackList) {
			if (!trackdate.contains(tasktrackOuter.getDate())) {
				trackdate.add(tasktrackOuter.getDate());
				String intialDate = sdf.format(tasktrackOuter.getDate());
				JSONArray dateTaskArray = new JSONArray();
				JSONObject dateTaskObj = new JSONObject();
				for (Tasktrack tasktrack : tasktrackList) {
					JSONObject taskObj = new JSONObject();
					taskObj.put("hour", tasktrack.getHours());
					taskObj.put("task_type", tasktrack.getTask().getTaskName());
					taskObj.put("description", tasktrack.getDescription());
					if (intialDate.equals(sdf.format(tasktrack.getDate()))) {
						dateTaskArray.add(taskObj);
					}
				}
				dateTaskObj.put(sdf.format(tasktrackOuter.getDate()), dateTaskArray);
				taskList.add(dateTaskObj);
			}
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("taskList", taskList);

		if (tasktrackStatus != null) {

			String approver1Status = tasktrackStatus.getApprover1Status();
			String approver2Status = tasktrackStatus.getApprover2Status();
			String financeStatus = tasktrackStatus.getFinanceStatus();

			String[] taskStatusArray = { Constants.TaskTrackWeeklyApproval.TASKTRACK_WEEKLY_APPROVER_STATUS_APPROVED };
			List<String> taskStatusList = Arrays.asList(taskStatusArray);

			if (taskStatusList.contains(approver1Status) || taskStatusList.contains(approver2Status)
					|| taskStatusList.contains(financeStatus)) {
				jsonObject.put("enabled", false);
			} else {
				jsonObject.put("enabled", true);
			}

		}
		response.setData(jsonObject);
		response.setStatus(Constants.SUCCESS);
		response.setStatusCode(Constants.SUCCESS_CODE);

		return response;
	}

	@Override
	public StatusResponse getWeeklyTasksForSubmission(JsonNode requestData) throws ParseException {

		StatusResponse response = new StatusResponse();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String start = requestData.get("startDate").asText();
		String end = requestData.get("endDate").asText();
		Long userId = requestData.get("userId").asLong();
		TaskTrackWeeklyApproval weeklytasksubmission = new TaskTrackWeeklyApproval();
		Date endDate = null, startDate = null;

		startDate = sdf.parse(start);
		endDate = sdf.parse(end);

		ArrayList<Tasktrack> tasklist = tasktrackRepository.getsavedTaskslist(startDate, endDate, userId);
		Map<Date, Double> dailyhours = new HashMap<Date, Double>();
		Long projectId = null;
		
		UserModel userDetails = userservice.getUserdetailsbyId(userId);
		weeklytasksubmission.setUser(userDetails);
		
		weeklytasksubmission = taskWeeklyApprovalRepository.getduplicateentrycount(weeklytasksubmission.getStartDate(),
				weeklytasksubmission.getEndDate(), weeklytasksubmission.getUser().getUserId());
		

		if (!tasklist.isEmpty()) {

			for (Tasktrack task : tasklist) {
				Date datevalue = task.getDate();
				projectId = task.getProject().getProjectId();
				if (dailyhours.containsKey(datevalue)) {
					Double hours = dailyhours.get(datevalue);
					hours = hours + task.getHours();
					dailyhours.put(datevalue, hours);
				} else {
					dailyhours.put(datevalue, task.getHours());
				}
			}
		}

		
		
		ProjectModel project = projectservice.findById(projectId);
		weeklytasksubmission.setProject(project);
		weeklytasksubmission.setStartDate(startDate);
		weeklytasksubmission.setEndDate(endDate);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		weeklytasksubmission.setYear(calendar.get(Calendar.YEAR));
		weeklytasksubmission.setUserSubmittedDate(new Date());
		weeklytasksubmission.setTimetrackStatus(Constants.TASKTRACK_USER_STATUS_SUBMIT);

		Map<Object, Object> result = dailyhours.entrySet().stream().sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
						LinkedHashMap::new));

		LocalDate localstartdate = LocalDate.parse(start);
		LocalDate localenddate = LocalDate.parse(end);
		int count = 0;
		while (!localstartdate.isAfter(localenddate)) {
			String stgdate = String.valueOf(localstartdate);
			Date date1 = null;

			date1 = sdf.parse(stgdate);

			Double hours = 0.0;
			if (result.get(date1) != null)
				hours = Double.parseDouble(result.get(date1).toString());

			if (count == 0)
				weeklytasksubmission.setDay1(hours);
			if (count == 1)
				weeklytasksubmission.setDay2(hours);
			if (count == 2)
				weeklytasksubmission.setDay3(hours);
			if (count == 3)
				weeklytasksubmission.setDay4(hours);
			if (count == 4)
				weeklytasksubmission.setDay5(hours);
			if (count == 5)
				weeklytasksubmission.setDay6(hours);
			if (count == 6)
				weeklytasksubmission.setDay7(hours);

			localstartdate = localstartdate.plusDays(1);
			count++;
		}

			taskWeeklyApprovalRepository.save(weeklytasksubmission);
			response = new StatusResponse("Success", 200, "Weekly data submission completed");
		
		return response;
	}

}