package com.EMS.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.ProjectModel;
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
	public int submitWeeklyApproval(JSONObject requestData) {

		TaskTrackWeeklyApproval weeklyApproval = new TaskTrackWeeklyApproval();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int requeststatus = 0;

		try {
			if (((!requestData.get("startDate").toString().equals(null))
					|| (!requestData.get("startDate").toString().equals(" ")))
					&& ((!requestData.get("endDate").toString().equals(null))
							|| (!requestData.get("endDate").toString().equals(" ")))) {
				weeklyApproval.setStartDate(sdf.parse(requestData.get("startDate").toString()));
				weeklyApproval.setEndDate(sdf.parse(requestData.get("endDate").toString()));

			} else
				requeststatus = 1;

			weeklyApproval.setDay1(Double.parseDouble(requestData.get("day1").toString()));
			weeklyApproval.setDay2(Double.parseDouble(requestData.get("day2").toString()));
			weeklyApproval.setDay3(Double.parseDouble(requestData.get("day3").toString()));
			weeklyApproval.setDay4(Double.parseDouble(requestData.get("day4").toString()));
			weeklyApproval.setDay5(Double.parseDouble(requestData.get("day5").toString()));
			weeklyApproval.setDay6(Double.parseDouble(requestData.get("day6").toString()));
			weeklyApproval.setDay7(Double.parseDouble(requestData.get("day7").toString()));
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

			if (((!requestData.get("userSubmittedDate").toString().equals(null))
					|| (!requestData.get("userSubmittedDate").toString().equals(" ")))) {
				weeklyApproval.setUserSubmittedDate(sdf.parse(requestData.get("userSubmittedDate").toString()));

			} else
				requeststatus = 1;

			Long userId = Long.parseLong(requestData.get("userId").toString());
			UserModel userInfo = userservice.getUserdetailsbyId(userId);

			if (!userInfo.equals(null))
				weeklyApproval.setUser(userInfo);
			else
				requeststatus = 1;

			Long projectId = Long.parseLong(requestData.get("projectId").toString());
			ProjectModel projectInfo = projectservice.findById(projectId);

			if (!projectInfo.equals(null))
				weeklyApproval.setProject(projectInfo);
			else
				requeststatus = 1;

			Long approver1Id = Long.parseLong(requestData.get("approver1Id").toString());
			UserModel approver1Info = userservice.getUserdetailsbyId(approver1Id);

			if (!approver1Info.equals(null))
				weeklyApproval.setApprover1Id(approver1Info);
//			else
//				requeststatus = 1;

			Long approver2Id = Long.parseLong(requestData.get("approver2Id").toString());
			UserModel approver2Info = userservice.getUserdetailsbyId(approver2Id);

			if (!approver2Info.equals(null))
				weeklyApproval.setApprover2Id(approver2Info);
//			else
//				requeststatus = 1;

			Long financeUserId = Long.parseLong(requestData.get("financeUser").toString());
			UserModel financeUser = userservice.getUserdetailsbyId(financeUserId);

			if (!financeUser.equals(null))
				weeklyApproval.setFinanceUser(financeUser);
//			else
//				requeststatus = 1;

			if (requestData.get("timetrackStatus").toString().equals(null)
					|| requestData.get("timetrackStatus").toString().equals(" ")
					|| requestData.get("approver1_status").toString().equals(null)
					|| requestData.get("approver1_status").toString().equals(" ")
					|| requestData.get("approver2_status").toString().equals(null)
					|| requestData.get("approver2_status").toString().equals(" ")
					|| requestData.get("financeStatus").toString().equals(null)
					|| requestData.get("financeStatus").toString().equals(" ")) {
				requeststatus = 1;
			} else {

				weeklyApproval.setTimetrackStatus(requestData.get("timetrackStatus").toString());
				weeklyApproval.setApprover1Status(requestData.get("approver1_status").toString());
				weeklyApproval.setApprover2Status(requestData.get("approver2_status").toString());
				weeklyApproval.setFinanceStatus(requestData.get("financeStatus").toString());

			}

			if (requestData.get("approver1SubmittedDate") != null)
				weeklyApproval
						.setApprover1SubmittedDate(sdf.parse(requestData.get("approver1SubmittedDate").toString()));

			if (requestData.get("approver2SubmittedDate") != null)
				weeklyApproval
						.setApprover2SubmittedDate(sdf.parse(requestData.get("approver2SubmittedDate").toString()));

			if (requestData.get("financeSubmittedDate") != null)
				weeklyApproval.setFinanceSubmittedDate(sdf.parse(requestData.get("financeSubmittedDate").toString()));

			if (requestData.get("rejectionTime") != null)
				weeklyApproval.setRejectionTime(sdf.parse(requestData.get("rejectionTime").toString()));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (requeststatus == 0) {

			int count = taskWeeklyApprovalRepository.getduplicateentrycount(weeklyApproval.getStartDate(),
					weeklyApproval.getEndDate(), weeklyApproval.getUser().getUserId());
			if (count == 0) {
				taskWeeklyApprovalRepository.save(weeklyApproval);
				return 0;
			} else
				return 1;
		} else
			return 1;

	}

	@Override
	public int saveWeeklyApproval(JSONObject requestData) {

		TaskTrackWeeklyApproval weeklyApproval = new TaskTrackWeeklyApproval();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int requeststatus = 0;

		try {
			if (((!requestData.get("startDate").toString().equals(null))
					|| (!requestData.get("startDate").toString().equals(" ")))
					&& ((!requestData.get("endDate").toString().equals(null))
							|| (!requestData.get("endDate").toString().equals(" ")))) {
				weeklyApproval.setStartDate(sdf.parse(requestData.get("startDate").toString()));
				weeklyApproval.setEndDate(sdf.parse(requestData.get("endDate").toString()));

			} else
				requeststatus = 1;

			weeklyApproval.setDay1(Double.parseDouble(requestData.get("day1").toString()));
			weeklyApproval.setDay2(Double.parseDouble(requestData.get("day2").toString()));
			weeklyApproval.setDay3(Double.parseDouble(requestData.get("day3").toString()));
			weeklyApproval.setDay4(Double.parseDouble(requestData.get("day4").toString()));
			weeklyApproval.setDay5(Double.parseDouble(requestData.get("day5").toString()));
			weeklyApproval.setDay6(Double.parseDouble(requestData.get("day6").toString()));
			weeklyApproval.setDay7(Double.parseDouble(requestData.get("day7").toString()));
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

			Long userId = Long.parseLong(requestData.get("userId").toString());
			UserModel userInfo = userservice.getUserdetailsbyId(userId);

			if (!userInfo.equals(null))
				weeklyApproval.setUser(userInfo);
			else
				requeststatus = 1;

			Long projectId = Long.parseLong(requestData.get("projectId").toString());
			ProjectModel projectInfo = projectservice.findById(projectId);

			if (!projectInfo.equals(null))
				weeklyApproval.setProject(projectInfo);
			else
				requeststatus = 1;

			if (requestData.get("timetrackStatus").toString().equals(null)
					|| requestData.get("timetrackStatus").toString().equals(" ")) {
				requeststatus = 1;
			} else {

				weeklyApproval.setTimetrackStatus(requestData.get("timetrackStatus").toString());
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (requeststatus == 0) {

			int count = taskWeeklyApprovalRepository.getduplicateentrycount(weeklyApproval.getStartDate(),
					weeklyApproval.getEndDate(), weeklyApproval.getUser().getUserId());
			if (count == 0) {
				taskWeeklyApprovalRepository.save(weeklyApproval);
				return 0;
			} else
				return 1;
		} else
			return 1;

	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getWeeklyTasktrack(JSONObject requestData) throws Exception, ParseException {
		JSONObject response = new JSONObject();
		Long userId = null;
		Long projectId = null;
		Date startDate = null;
		Date endDate = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (requestData.get("uId") != null && requestData.get("uId").toString() != "") {
			userId = Long.parseLong(requestData.get("uId").toString());
		}
		if (requestData.get("projectId") != null && requestData.get("projectId").toString() != "") {
			projectId = Long.parseLong(requestData.get("projectId").toString());
		}
		if (requestData.get("startDate") != null && requestData.get("startDate").toString() != null) {
			startDate = sdf.parse(requestData.get("startDate").toString());
		}
		if (requestData.get("endDate") != null && requestData.get("endDate").toString() != null) {
			endDate = sdf.parse(requestData.get("endDate").toString());
		}

		String[] taskStatusArray = { Constants.TaskTrackWeeklyApproval.TASKTRACK_WEEKLY_APPROVER_STATUS_APPROVED };
		List<String> taskStatusList = Arrays.asList(taskStatusArray);
		TaskTrackWeeklyApproval weeklyTasktrack = taskWeeklyApprovalRepository.getWeeklyTasktrack(startDate, endDate,userId, projectId);

		if (weeklyTasktrack != null) {
			String approver1Status = weeklyTasktrack.getApprover1Status();
			String approver2Status = weeklyTasktrack.getApprover2Status();
			String financeStatus = weeklyTasktrack.getFinanceStatus();

			if (taskStatusList.contains(approver1Status) || taskStatusList.contains(approver2Status)|| taskStatusList.contains(financeStatus)) {

				response.put("enabled", false);
			} else {
				response.put("enabled", true);
			}
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
		}else {
			
		}
		return response;
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
	
	@Override
	public JSONObject getWeeklyTasktrackWithTask(JSONObject requestData) {
		
		return null;
	}

	@Override
	public int getWeeklyTasksForSubmission(JsonNode requestData) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String start = requestData.get("startDate").asText();
		String end = requestData.get("endDate").asText();
		Long userId = requestData.get("userId").asLong();

		Date endDate = null, startDate = null;
		try {
			startDate = sdf.parse(start);
			endDate = sdf.parse(end);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<Tasktrack> tasklist = tasktrackRepository.getsavedTaskslist(startDate, endDate, userId);
		Map<Date, Double> dailyhours = new HashMap<Date, Double>();
		Long projectId = null;

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

		TaskTrackWeeklyApproval weeklytasksubmission = new TaskTrackWeeklyApproval();
		UserModel userDetails = userservice.getUserdetailsbyId(userId);
		ProjectModel project = projectservice.findById(projectId);

		weeklytasksubmission.setUser(userDetails);
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
			try {
				date1 = sdf.parse(stgdate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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

		
		
		int countt= taskWeeklyApprovalRepository.getduplicateentrycount(weeklytasksubmission.getStartDate(),
				weeklytasksubmission.getEndDate(), weeklytasksubmission.getUser().getUserId());
		if (countt == 0) {
			taskWeeklyApprovalRepository.save(weeklytasksubmission);
			return 0;
		} else
			return 1;
	}

}