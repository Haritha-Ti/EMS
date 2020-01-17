package com.EMS.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.dto.WeeklyTaskTrackWithTaskRequestDTO;
import com.EMS.dto.WeeklyTaskTrackWithoutTaskRequestDTO;
import com.EMS.model.AllocationModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.StatusResponse;
import com.EMS.model.TaskTrackWeeklyApproval;
import com.EMS.model.Tasktrack;
import com.EMS.model.UserModel;
import com.EMS.repository.AllocationRepository;
import com.EMS.repository.TaskWeeklyApprovalRepository;
import com.EMS.repository.TasktrackRepository;
import com.EMS.utility.Constants;
import com.EMS.utility.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;

@SuppressWarnings({ "rawtypes", "unchecked" })
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

	@Autowired
	private AllocationRepository allocationRepository;

	@Override
	public StatusResponse submitWeeklyApproval(JSONObject requestData) throws ParseException, Exception {

		Date startDate = null;
		Date endDate = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int requeststatus = 0;
		StatusResponse response = new StatusResponse();

		if (((!requestData.get("startDate").toString().equals(null))
				|| (!requestData.get("startDate").toString().equals(" ")))
				&& ((!requestData.get("endDate").toString().equals(null))
						|| (!requestData.get("endDate").toString().equals(" ")))) {
			startDate = sdf.parse(requestData.get("startDate").toString());
			endDate = sdf.parse(requestData.get("endDate").toString());

		}
		Long userId = Long.parseLong(requestData.get("userId").toString());

		TaskTrackWeeklyApproval weeklyApproval = null;
		weeklyApproval = taskWeeklyApprovalRepository.getDuplicateEntryCount(startDate, endDate, userId);
		if (null == weeklyApproval) {
			weeklyApproval = new TaskTrackWeeklyApproval();
		}

		if (null != weeklyApproval.getTimetrackStatus() &&  weeklyApproval.getTimetrackStatus().equals(Constants.TASKTRACK_USER_STATUS_SUBMIT)) {
			return new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, "Timetrack already submitted.");
		}
		weeklyApproval.setStartDate(startDate);
		weeklyApproval.setEndDate(endDate);
		UserModel userInfo = userservice.getUserdetailsbyId(userId);

		if (!userInfo.equals(null)) {
			weeklyApproval.setUser(userInfo);
		}

		Long projectId = Long.parseLong(requestData.get("projectId").toString());
		ProjectModel projectInfo = projectservice.findById(projectId);

		if (!projectInfo.equals(null)) {
			weeklyApproval.setProject(projectInfo);
		} else {
			requeststatus = 1;
		}

		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(userId,
				projectId);

		if ((!weeklyApproval.getDay1().equals(null)) && (!weeklyApproval.getDay2().equals(null))
				&& (!weeklyApproval.getDay3().equals(null)) && (!weeklyApproval.getDay4().equals(null))
				&& (!weeklyApproval.getDay5().equals(null)) && (!weeklyApproval.getDay6().equals(null))
				&& (!weeklyApproval.getDay7().equals(null))) {

			if ((weeklyApproval.getDay1() < 0) || (weeklyApproval.getDay1() > 24) || (weeklyApproval.getDay2() < 0)
					|| (weeklyApproval.getDay2() > 24) || (weeklyApproval.getDay3() < 0)
					|| (weeklyApproval.getDay3() > 24) || (weeklyApproval.getDay4() < 0)
					|| (weeklyApproval.getDay4() > 24) || (weeklyApproval.getDay5() < 0)
					|| (weeklyApproval.getDay5() > 24) || (weeklyApproval.getDay6() < 0)
					|| (weeklyApproval.getDay6() > 24) || (weeklyApproval.getDay7() < 0)
					|| (weeklyApproval.getDay7() > 24)) {
				requeststatus = 1;
			}
		} else {
			requeststatus = 1;
		}

		Map<Date, Double> timetrack = (Map<Date, Double>) requestData.get("timetrack");

		Map<Object, Object> timetrackRequestData = timetrack.entrySet().stream().sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
						LinkedHashMap::new));

		List<String> projectDateList = new ArrayList<String>();

		for (AllocationModel al : userProjAllocations) {
			Date allocStartDate = al.getStartDate();
			Date allocEndDate = al.getEndDate();

			Calendar fromDate = Calendar.getInstance();
			Calendar toDate = Calendar.getInstance();

			if (allocStartDate.before(startDate)) {
				fromDate.setTime(startDate);
			} else {
				fromDate.setTime(allocStartDate);
			}

			if (allocEndDate.before(endDate)) {
				toDate.setTime(allocEndDate);
			} else {
				toDate.setTime(endDate);
			}

			while (fromDate.before(toDate) || fromDate.equals(toDate)) {
				Date result = fromDate.getTime();
				String date = sdf.format(result);
				projectDateList.add(date);
				fromDate.add(Calendar.DATE, 1);
			}
		}

	
			if (projectDateList.size()>0 && projectDateList.get(0) != null) {
				weeklyApproval.setDay1(Double.parseDouble(timetrackRequestData.get(projectDateList.get(0)).toString()));
			}
			if (projectDateList.size()>1 && projectDateList.get(1) != null) {
				weeklyApproval.setDay2(Double.parseDouble(timetrackRequestData.get(projectDateList.get(1)).toString()));
			}
			if (projectDateList.size()>2 && projectDateList.get(2) != null) {
				weeklyApproval.setDay3(Double.parseDouble(timetrackRequestData.get(projectDateList.get(2)).toString()));
			}
			if (projectDateList.size()>3 && projectDateList.get(3) != null) {
				weeklyApproval.setDay4(Double.parseDouble(timetrackRequestData.get(projectDateList.get(3)).toString()));
			}
			if (projectDateList.size()>4 && projectDateList.get(4) != null) {
				weeklyApproval.setDay5(Double.parseDouble(timetrackRequestData.get(projectDateList.get(4)).toString()));
			}
			if (projectDateList.size()>5 && projectDateList.get(5) != null) {
				weeklyApproval.setDay6(Double.parseDouble(timetrackRequestData.get(projectDateList.get(5)).toString()));
			}
			if (projectDateList.size()>6 && projectDateList.get(6) != null) {
				weeklyApproval.setDay7(Double.parseDouble(timetrackRequestData.get(projectDateList.get(6)).toString()));
			}
	
		weeklyApproval.setUserSubmittedDate(new Date());

		weeklyApproval.setTimetrackStatus(Constants.TASKTRACK_USER_STATUS_SUBMIT);
		weeklyApproval.setTimetrackFinalStatus(Constants.TASKTRACK_USER_STATUS_SUBMIT);

		if (requeststatus == 0) {
			taskWeeklyApprovalRepository.save(weeklyApproval);
			response = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, "Insertion completed");
		} else {
			response = new StatusResponse(Constants.FAILURE, Constants.ERROR_CODE, Constants.NO_DATA_FOUND_MESSAGE);
		}
		return response;

	}

	@Override
	public StatusResponse saveWeeklyApproval(JSONObject requestData) throws ParseException {

		Date startDate = null;
		Date endDate  = null;		
		
		StatusResponse response = new StatusResponse();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int requeststatus = 0;

		if (((!requestData.get("startDate").toString().equals(null))
				|| (!requestData.get("startDate").toString().equals(" ")))
				&& ((!requestData.get("endDate").toString().equals(null))
						|| (!requestData.get("endDate").toString().equals(" ")))) {
			 startDate = sdf.parse(requestData.get("startDate").toString());
			
			 endDate = sdf.parse(requestData.get("endDate").toString());
			
		} 		
		
		Long userId = Long.parseLong(requestData.get("userId").toString());
		
		TaskTrackWeeklyApproval weeklyApproval = null;
		weeklyApproval = taskWeeklyApprovalRepository.getDuplicateEntryCount(startDate,
				endDate, userId);

		if (null == weeklyApproval) {
			weeklyApproval = new TaskTrackWeeklyApproval();
		}
		weeklyApproval.setStartDate(startDate);
		weeklyApproval.setEndDate(endDate);
		
		UserModel userInfo = userservice.getUserdetailsbyId(userId);
		if (!userInfo.equals(null)) {
			weeklyApproval.setUser(userInfo);
		} 
		
		Long projectId = Long.parseLong(requestData.get("projectId").toString());
		ProjectModel projectInfo = projectservice.findById(projectId);
		
		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(userId,
				projectId);
		
		Map<Date, Integer> timetrack = (Map<Date, Integer>) requestData.get("timetrack");

		Map<Object, Object> timetrackRequestData = timetrack.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors
				.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		
		List<String> projectDateList = new ArrayList<String>();
		
		for (AllocationModel al : userProjAllocations) {
			Date allocStartDate = al.getStartDate();
			Date allocEndDate = al.getEndDate();

			Calendar fromDate = Calendar.getInstance();
			Calendar toDate = Calendar.getInstance();

			if (allocStartDate.before(startDate)) {
				fromDate.setTime(startDate);
			} else {
				fromDate.setTime(allocStartDate);
			}

			if (allocEndDate.before(endDate)) {
				toDate.setTime(allocEndDate);
			} else {
				toDate.setTime(endDate);
			}

			while (fromDate.before(toDate) || fromDate.equals(toDate)) {
				Date result = fromDate.getTime();
				String date = sdf.format(result);
				projectDateList.add(date);
				fromDate.add(Calendar.DATE, 1);
			}

		}

		
		
		if (projectDateList.size()>0 && projectDateList.get(0) != null) {
			weeklyApproval.setDay1(Double.parseDouble(timetrackRequestData.get(projectDateList.get(0)).toString()));
		}
		if (projectDateList.size()>1 && projectDateList.get(1) != null) {
			weeklyApproval.setDay2(Double.parseDouble(timetrackRequestData.get(projectDateList.get(1)).toString()));
		}
		if (projectDateList.size()>2 && projectDateList.get(2) != null) {
			weeklyApproval.setDay3(Double.parseDouble(timetrackRequestData.get(projectDateList.get(2)).toString()));
		}
		if (projectDateList.size()>3 && projectDateList.get(3) != null) {
			weeklyApproval.setDay4(Double.parseDouble(timetrackRequestData.get(projectDateList.get(3)).toString()));
		}
		if (projectDateList.size()>4 && projectDateList.get(4) != null) {
			weeklyApproval.setDay5(Double.parseDouble(timetrackRequestData.get(projectDateList.get(4)).toString()));
		}
		if (projectDateList.size()>5 && projectDateList.get(5) != null) {
			weeklyApproval.setDay6(Double.parseDouble(timetrackRequestData.get(projectDateList.get(5)).toString()));
		}
		if (projectDateList.size()>6 && projectDateList.get(6) != null) {
			weeklyApproval.setDay7(Double.parseDouble(timetrackRequestData.get(projectDateList.get(6)).toString()));
		}


		if ((!weeklyApproval.getDay1().equals(null)) && (!weeklyApproval.getDay2().equals(null))
				&& (!weeklyApproval.getDay3().equals(null)) && (!weeklyApproval.getDay4().equals(null))
				&& (!weeklyApproval.getDay5().equals(null)) && (!weeklyApproval.getDay6().equals(null))
				&& (!weeklyApproval.getDay7().equals(null))) {

			if ((weeklyApproval.getDay1() < 0) || (weeklyApproval.getDay1() > 24) || (weeklyApproval.getDay2() < 0)
					|| (weeklyApproval.getDay2() > 24) || (weeklyApproval.getDay3() < 0)
					|| (weeklyApproval.getDay3() > 24) || (weeklyApproval.getDay4() < 0)
					|| (weeklyApproval.getDay4() > 24) || (weeklyApproval.getDay5() < 0)
					|| (weeklyApproval.getDay5() > 24) || (weeklyApproval.getDay6() < 0)
					|| (weeklyApproval.getDay6() > 24) || (weeklyApproval.getDay7() < 0)
					|| (weeklyApproval.getDay7() > 24))
				requeststatus = 1;
		} else
		{
			requeststatus = 1;
		}
	

		if (!projectInfo.equals(null)) {
			weeklyApproval.setProject(projectInfo);
		}
		else {
			requeststatus = 1;
		}
		weeklyApproval.setTimetrackStatus(Constants.TASKTRACK_USER_STATUS_SAVED);
		weeklyApproval.setTimetrackFinalStatus(Constants.TASKTRACK_USER_STATUS_SAVED);

		if (requeststatus == 0) {

		taskWeeklyApprovalRepository.save(weeklyApproval);
		response = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, "Insertion completed");

		} else
		{
			response = new StatusResponse("Success", 200, "Insertion failed due to invalid credientials");
		}
		return response;

	}

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

		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(userId,
				projectId);

		List<Date> datesInRange = DateUtil.getDatesBetweenTwo(startDate, endDate);
		datesInRange.add(endDate);

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

			String approver = weeklyTasktrack.getApprover1Id()!=null? weeklyTasktrack.getApprover1Id().getFirstName() + " "
					+ weeklyTasktrack.getApprover1Id().getLastName():"";
			Date approver1SubmittedDate = weeklyTasktrack.getApprover1SubmittedDate();

			JSONObject approvalObj = new JSONObject();
			approvalObj.put("approver", approver);

			if(null != approver1SubmittedDate){ 
				approvalObj.put("date", sdf.format(approver1SubmittedDate));
			}
			else {
				approvalObj.put("date", "");
			}

			approvalObj.put("status", approver1Status);
			response.put("approval", approvalObj);

			JSONObject user = new JSONObject();
			user.put("status", weeklyTasktrack.getTimetrackStatus());
			user.put("date", weeklyTasktrack.getUserSubmittedDate());
			response.put("user", user);

			
			JSONArray array = new JSONArray();
			for (AllocationModel al : userProjAllocations) {
				List<Date> allocatedDates = DateUtil.getDatesBetweenTwo(al.getStartDate(), al.getEndDate());
				array.add(addHoursandDaytoArray(sdf,allocatedDates,weeklyTasktrack.getDay1(), datesInRange.get(0)));
				array.add(addHoursandDaytoArray(sdf,allocatedDates,weeklyTasktrack.getDay2(), datesInRange.get(1)));
				array.add(addHoursandDaytoArray(sdf,allocatedDates,weeklyTasktrack.getDay3(), datesInRange.get(2)));
				array.add(addHoursandDaytoArray(sdf,allocatedDates,weeklyTasktrack.getDay4(), datesInRange.get(3)));
				array.add(addHoursandDaytoArray(sdf,allocatedDates,weeklyTasktrack.getDay5(), datesInRange.get(4)));
				array.add(addHoursandDaytoArray(sdf,allocatedDates,weeklyTasktrack.getDay6(), datesInRange.get(5)));
				array.add(addHoursandDaytoArray(sdf,allocatedDates,weeklyTasktrack.getDay7(), datesInRange.get(6)));
			}

			response.put("taskList", array);
			responseFinal = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, response);
		} else {
			JSONArray array = new JSONArray();
			for (AllocationModel al : userProjAllocations) {
				List<Date> allocatedDates = DateUtil.getDatesBetweenTwo(al.getStartDate(), al.getEndDate());
				for (Date date : datesInRange) {
					array.add(addHoursandDaytoArray(sdf, allocatedDates,null, date));
				}
			}
			response.put("taskList", array);
			response.put("enabled", true);
			responseFinal = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, response);
		}
		return responseFinal;
	}

	public JSONObject addHoursandDaytoArray(SimpleDateFormat sdf, List<Date> allocatedDates, Double hour, Date date) {
		JSONObject response = new JSONObject();
		JSONObject dayResponse = new JSONObject();
		dayResponse.put("hour", hour);
		dayResponse.put("enabled",allocatedDates.contains(date));
		response.put(sdf.format(date), dayResponse);
		return response;
	}

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

				.findByUserUserIdAndProjectProjectIdInAndStartDateEqualsAndEndDateEquals(userId, projectId, startDate,
						endDate);

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

		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(userId,
				projectId);

		if (tasktrackStatus != null) {

			String approver1Status = tasktrackStatus.getApprover1Status();
			String approver2Status = tasktrackStatus.getApprover2Status();
			String financeStatus = tasktrackStatus.getFinanceStatus();

			String[] taskStatusArray = { Constants.TaskTrackWeeklyApproval.TASKTRACK_WEEKLY_APPROVER_STATUS_APPROVED };
			List<String> taskStatusList = Arrays.asList(taskStatusArray);

			if (taskStatusList.contains(approver1Status) || taskStatusList.contains(approver2Status)
					|| taskStatusList.contains(financeStatus)) {
				for (AllocationModel al : userProjAllocations) {
					List<Date> allocatedDates = DateUtil.getDatesBetweenTwo(al.getStartDate(), al.getEndDate());
					if (allocatedDates.contains(startDate) || allocatedDates.contains(endDate)) {
						jsonObject.put("enabled", false);
					}
				}
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

		weeklytasksubmission = taskWeeklyApprovalRepository.getDuplicateEntryCount(weeklytasksubmission.getStartDate(),
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