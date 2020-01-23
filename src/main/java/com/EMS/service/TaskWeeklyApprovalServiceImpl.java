package com.EMS.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.EMS.dto.SaveWeeklyTasktrackWithTaskRequestDTO2;
import com.EMS.dto.SubmitWeeklyTasktrackWithTaskRequestDTO2;
import com.EMS.dto.TasktrackDto;
import com.EMS.dto.WeeklyTaskTrackWithTaskRequestDTO;
import com.EMS.dto.WeeklyTaskTrackWithTaskResponse;
import com.EMS.dto.WeeklyTaskTrackWithTaskResponseDTO;
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

@SuppressWarnings({ "rawtypes", "unchecked"})
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

	@Autowired
	TasktrackService tasktrackService;
	
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

		if (null != weeklyApproval.getTimetrackFinalStatus()) {
			if (weeklyApproval.getTimetrackFinalStatus().equals(Constants.FinalStatus.TASKTRACK_APPROVED)
					|| weeklyApproval.getTimetrackFinalStatus()
							.equals(Constants.Approver1.TASKTRACK_FORWARDED_TO_LEVEL2)) {
				return new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE,
						"Timetrack has already been approved.");
			}

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

		Map<Object, Object> timetrackRequestData = (Map<Object, Object>) requestData.get("timetrack");

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

		int indx = 1;
		for (Map.Entry<Object, Object> map : timetrackRequestData.entrySet()) {
			Double hour = null;
			if (projectDateList.contains(map.getKey()) && map.getValue() != null) {
				hour = Double.parseDouble(map.getValue().toString());
			} else {
				indx++;
				continue;
			}
			switch (indx) {
			case 1: {
				weeklyApproval.setDay1(hour);
				break;
			}
			case 2: {
				weeklyApproval.setDay2(hour);
				break;
			}
			case 3: {
				weeklyApproval.setDay3(hour);
				break;
			}
			case 4: {
				weeklyApproval.setDay4(hour);
				break;
			}
			case 5: {
				weeklyApproval.setDay5(hour);
				break;
			}
			case 6: {
				weeklyApproval.setDay6(hour);
				break;
			}
			case 7: {
				weeklyApproval.setDay7(hour);
				break;
			}
			}
			indx++;
		}

		weeklyApproval.setUserSubmittedDate(new Date());

		weeklyApproval.setTimetrackStatus(Constants.UserStatus.TASKTRACK_SUBMIT);
		weeklyApproval.setTimetrackFinalStatus(Constants.UserStatus.TASKTRACK_SUBMIT);

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
		Date endDate = null;

		StatusResponse response = new StatusResponse();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    DecimalFormat decimalFormat = new DecimalFormat("0.0");
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
		weeklyApproval = taskWeeklyApprovalRepository.getDuplicateEntryCount(startDate, endDate, userId);

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

		Map<Object, Object> timetrackRequestData = (Map<Object, Object>) requestData.get("timetrack");

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

		int indx = 1;
		for (Map.Entry<Object, Object> map : timetrackRequestData.entrySet()) {
			Double hour = null;
			if (projectDateList.contains(map.getKey()) && map.getValue() != null) {
				String hourStr = decimalFormat.format(map.getValue());
				hour = Double.parseDouble(hourStr);
				
			} else {
				indx++;
				continue;
			}
			switch (indx) {
			case 1: {
				weeklyApproval.setDay1(hour);
				break;
			}
			case 2: {
				weeklyApproval.setDay2(hour);
				break;
			}
			case 3: {
				weeklyApproval.setDay3(hour);
				break;
			}
			case 4: {
				weeklyApproval.setDay4(hour);
				break;
			}
			case 5: {
				weeklyApproval.setDay5(hour);
				break;
			}
			case 6: {
				weeklyApproval.setDay6(hour);
				break;
			}
			case 7: {
				weeklyApproval.setDay7(hour);
				break;
			}
			}
			indx++;
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
		} else {
			requeststatus = 1;
		}

		if (!projectInfo.equals(null)) {
			weeklyApproval.setProject(projectInfo);
		} else {
			requeststatus = 1;
		}
		weeklyApproval.setTimetrackStatus(Constants.UserStatus.TASKTRACK_SAVED);
		weeklyApproval.setTimetrackFinalStatus(Constants.UserStatus.TASKTRACK_SAVED);

		if (requeststatus == 0) {

			taskWeeklyApprovalRepository.save(weeklyApproval);
			response = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, "Insertion completed");

		} else {
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

		String[] taskStatusArray = { Constants.TaskTrackWeeklyApproval.TASKTRACK_WEEKLY_APPROVER_STATUS_APPROVED,Constants.Approver1.TASKTRACK_FORWARDED_TO_LEVEL2 };
		List<String> taskStatusList = Arrays.asList(taskStatusArray);
		TaskTrackWeeklyApproval weeklyTasktrack = taskWeeklyApprovalRepository.getWeeklyTasktrack(startDate, endDate,
				userId, projectId);

		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(userId,
				projectId);

		List<Date> datesInRange = DateUtil.getDatesBetweenTwo(startDate, endDate);
		
		ProjectModel projectModel = projectservice.findById(projectId);

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

			
			String approver1 = weeklyTasktrack.getApprover1Id() != null
					? weeklyTasktrack.getApprover1Id().getFirstName() + " "
							+ weeklyTasktrack.getApprover1Id().getLastName()
					: "";

			if (null == approver1 || approver1.equals("")) {
				approver1 = null != projectModel.getProjectOwner() ? projectModel.getProjectOwner().getFirstName() + " "
						+ projectModel.getProjectOwner().getLastName() : "";

			}

			Date approver1SubmittedDate = weeklyTasktrack.getApprover1SubmittedDate();

			JSONObject approver1Obj = new JSONObject();
			approver1Obj.put("approver", approver1);

			if (null != approver1SubmittedDate) {
				approver1Obj.put("date", sdf.format(approver1SubmittedDate));
			} else {
				approver1Obj.put("date", "");
			}

			approver1Obj.put("status", approver1Status);
			response.put("approver1", approver1Obj);

			String approver2 = weeklyTasktrack.getApprover2Id() != null
					? weeklyTasktrack.getApprover2Id().getFirstName() + " "
							+ weeklyTasktrack.getApprover2Id().getLastName()
					: "";

			if (null == approver2 || approver2.equals("")) {
				approver2 = null != projectModel.getOnsite_lead() ? projectModel.getOnsite_lead().getFirstName() + " "
						+ projectModel.getOnsite_lead().getLastName() : "";

			}

			Date approver2SubmittedDate = weeklyTasktrack.getApprover2SubmittedDate();

			JSONObject approver2Obj = new JSONObject();
			approver2Obj.put("approver", approver2);

			if (null != approver2SubmittedDate) {
				approver2Obj.put("date", sdf.format(approver2SubmittedDate));
			} else {
				approver2Obj.put("date", "");
			}

			approver2Obj.put("status", approver2Status);
			response.put("approver2", approver2Obj);

			JSONObject user = new JSONObject();
			user.put("status", weeklyTasktrack.getTimetrackStatus());
			user.put("date", weeklyTasktrack.getUserSubmittedDate());
			response.put("user", user);

			JSONArray array = new JSONArray();
			for (AllocationModel al : userProjAllocations) {
				List<Date> allocatedDates = DateUtil.getDatesBetweenTwo(al.getStartDate(), al.getEndDate());
				array.add(addHoursandDaytoArray(sdf, allocatedDates, weeklyTasktrack.getDay1(), datesInRange.get(0)));
				array.add(addHoursandDaytoArray(sdf, allocatedDates, weeklyTasktrack.getDay2(), datesInRange.get(1)));
				array.add(addHoursandDaytoArray(sdf, allocatedDates, weeklyTasktrack.getDay3(), datesInRange.get(2)));
				array.add(addHoursandDaytoArray(sdf, allocatedDates, weeklyTasktrack.getDay4(), datesInRange.get(3)));
				array.add(addHoursandDaytoArray(sdf, allocatedDates, weeklyTasktrack.getDay5(), datesInRange.get(4)));
				array.add(addHoursandDaytoArray(sdf, allocatedDates, weeklyTasktrack.getDay6(), datesInRange.get(5)));
				array.add(addHoursandDaytoArray(sdf, allocatedDates, weeklyTasktrack.getDay7(), datesInRange.get(6)));
			}

			response.put("taskList", array);
			responseFinal = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, response);
		} else {
			JSONArray array = new JSONArray();
			for (AllocationModel al : userProjAllocations) {
				List<Date> allocatedDates = DateUtil.getDatesBetweenTwo(al.getStartDate(), al.getEndDate());
				for (Date date : datesInRange) {
					array.add(addHoursandDaytoArray(sdf, allocatedDates, null, date));
				}
			}
			response.put("taskList", array);
			JSONObject user = new JSONObject();
			user.put("status", Constants.UserStatus.TASKTRACK_OPEN);
			user.put("date", "");
			response.put("user", user);

			String approver1 = null != projectModel.getProjectOwner()
					? projectModel.getProjectOwner().getFirstName() + " " + projectModel.getProjectOwner().getLastName()
					: "";

			JSONObject approver1Obj = new JSONObject();
			approver1Obj.put("approver", approver1);
			approver1Obj.put("date", "");
			approver1Obj.put("status", Constants.Approver1.TASKTRACK_OPEN);
			response.put("approver1", approver1Obj);

			String approver2 = null != projectModel.getOnsite_lead()
					? projectModel.getOnsite_lead().getFirstName() + " " + projectModel.getOnsite_lead().getLastName()
					: "";

			JSONObject approver2Obj = new JSONObject();
			approver2Obj.put("approver", approver2);
			approver2Obj.put("date", "");
			approver2Obj.put("status", Constants.Approver2.TASKTRACK_OPEN);
			response.put("approver2", approver2Obj);

			response.put("enabled", true);
			responseFinal = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, response);
		}
		return responseFinal;
	}

	public JSONObject addHoursandDaytoArray(SimpleDateFormat sdf, List<Date> allocatedDates, Double hour, Date date) {
		JSONObject response = new JSONObject();
		JSONObject dayResponse = new JSONObject();
		dayResponse.put("hour", hour);
		dayResponse.put("enabled", allocatedDates.contains(date));
		response.put(sdf.format(date), dayResponse);
		return response;
	}


	public JSONObject addTaskAandDaytoArray(SimpleDateFormat sdf, List<Date> allocatedDates, Double hour, Date date) {
		JSONObject response = new JSONObject();
		JSONObject dayResponse = new JSONObject();
		dayResponse.put("hour", hour);
		dayResponse.put("enabled", allocatedDates.contains(date));
		response.put(sdf.format(date), dayResponse);
		return response;
	}
	
	@Override
	public WeeklyTaskTrackWithTaskResponseDTO getWeeklyTasktrackWithTask(WeeklyTaskTrackWithTaskRequestDTO requestData) throws Exception {

		WeeklyTaskTrackWithTaskResponseDTO weeklyTaskTrackWithTaskResponseDTO = new WeeklyTaskTrackWithTaskResponseDTO();
		List<WeeklyTaskTrackWithTaskResponse> taskTrackResponseList = new ArrayList<>();
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

		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(userId,
				projectId);

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

		HashSet<Date> trackdate = new HashSet<Date>();

		List<Date> reqDateRange = DateUtil.getDatesBetweenTwo(startDate, endDate);

		if (!tasktrackList.isEmpty()) {

			for (Date date : reqDateRange) {
				if (projectDateList.contains(sdf.format(date))) {

					for (Tasktrack tasktrackOuter : tasktrackList) {
						if (!trackdate.contains(tasktrackOuter.getDate())) {
							trackdate.add(tasktrackOuter.getDate());
							String intialDate = sdf.format(tasktrackOuter.getDate());

							WeeklyTaskTrackWithTaskResponse taskTrackResponse = new WeeklyTaskTrackWithTaskResponse();
							List<TasktrackDto> tasktrackDtoList = new ArrayList<>();
							Double finalHour = 0.0;
							for (Tasktrack tasktrack : tasktrackList) {
								TasktrackDto tasktrackDto = new TasktrackDto();
								tasktrackDto.setHour(tasktrack.getHours());
								tasktrackDto.setTaskType(
										null != tasktrack.getTask() ? tasktrack.getTask().getTaskName() : "");
								tasktrackDto.setTaskTypeId(
										null != tasktrack.getTask() ? tasktrack.getTask().getId() : null);
								tasktrackDto.setDescription(tasktrack.getDescription());
								if (intialDate.equals(sdf.format(tasktrack.getDate()))) {
									finalHour += tasktrack.getHours();
									tasktrackDtoList.add(tasktrackDto);
								}
							}
							taskTrackResponse.setTaskList(tasktrackDtoList);
							taskTrackResponse.setEnabled(true);
							taskTrackResponse.setDate(sdf.format(tasktrackOuter.getDate()));
							taskTrackResponse.setFinalHour(finalHour);
							taskTrackResponseList.add(taskTrackResponse);

						}
					}
					weeklyTaskTrackWithTaskResponseDTO.setTasktrackList(taskTrackResponseList);
				} else {
					WeeklyTaskTrackWithTaskResponse taskTrackResponse = new WeeklyTaskTrackWithTaskResponse();
					taskTrackResponse.setTaskList(new ArrayList<>());
					taskTrackResponse.setEnabled(false);
					taskTrackResponse.setDate(sdf.format(date));
					taskTrackResponse.setFinalHour(0.0);
					taskTrackResponseList.add(taskTrackResponse);
					weeklyTaskTrackWithTaskResponseDTO.setTasktrackList(taskTrackResponseList);
				}

			}
		}
		if (tasktrackList.isEmpty()) {

			for (Date reqDate : reqDateRange) {

				WeeklyTaskTrackWithTaskResponse taskTrackResponse = new WeeklyTaskTrackWithTaskResponse();
				taskTrackResponse.setTaskList(new ArrayList<>());
				taskTrackResponse.setDate(sdf.format(reqDate));
				taskTrackResponse.setFinalHour(0.0);
				taskTrackResponse.setEnabled(projectDateList.contains(sdf.format(reqDate)) ? true : false);
				taskTrackResponseList.add(taskTrackResponse);
				weeklyTaskTrackWithTaskResponseDTO.setTasktrackList(taskTrackResponseList);
			}
		}

		if (tasktrackStatus != null) {

			String approver1Status = tasktrackStatus.getApprover1Status();
			String approver2Status = tasktrackStatus.getApprover2Status();
			String financeStatus = tasktrackStatus.getFinanceStatus();

			String[] taskStatusArray = { Constants.TaskTrackWeeklyApproval.TASKTRACK_WEEKLY_APPROVER_STATUS_APPROVED };
			List<String> taskStatusList = Arrays.asList(taskStatusArray);

			weeklyTaskTrackWithTaskResponseDTO
					.setEnabled((taskStatusList.contains(approver1Status) || taskStatusList.contains(approver2Status)
							|| taskStatusList.contains(financeStatus)) ? false : true);

			ProjectModel projectModel = projectservice.findById(projectId);

			String approver1 = tasktrackStatus.getApprover1Id() != null
					? tasktrackStatus.getApprover1Id().getFirstName() + " "
							+ tasktrackStatus.getApprover1Id().getLastName()
					: "";

			if (null == approver1 || approver1.equals("")) {
				approver1 = null != projectModel.getProjectOwner() ? projectModel.getProjectOwner().getFirstName() + " "
						+ projectModel.getProjectOwner().getLastName() : "";

			}

			Date approver1SubmittedDate = tasktrackStatus.getApprover1SubmittedDate();

			JSONObject approver1Obj = new JSONObject();
			approver1Obj.put("approver", approver1);

			if (null != approver1SubmittedDate) {
				approver1Obj.put("date", sdf.format(approver1SubmittedDate));
			} else {
				approver1Obj.put("date", "");
			}

			approver1Obj.put("status", approver1Status);
			weeklyTaskTrackWithTaskResponseDTO.setApprover1(approver1Obj);

			String approver2 = tasktrackStatus.getApprover1Id() != null
					? tasktrackStatus.getApprover1Id().getFirstName() + " "
							+ tasktrackStatus.getApprover1Id().getLastName()
					: "";

			if (null == approver2 || approver2.equals("")) {
				approver2 = null != projectModel.getOnsite_lead() ? projectModel.getOnsite_lead().getFirstName() + " "
						+ projectModel.getOnsite_lead().getLastName() : "";

			}

			Date approver2SubmittedDate = tasktrackStatus.getApprover2SubmittedDate();

			JSONObject approver2Obj = new JSONObject();
			approver2Obj.put("approver", approver2);

			if (null != approver2SubmittedDate) {
				approver2Obj.put("date", sdf.format(approver2SubmittedDate));
			} else {
				approver2Obj.put("date", "");
			}

			approver2Obj.put("status", approver2Status);
			weeklyTaskTrackWithTaskResponseDTO.setApprover2(approver2Obj);

			JSONObject user = new JSONObject();
			user.put("status", tasktrackStatus.getTimetrackStatus());
			user.put("date", tasktrackStatus.getUserSubmittedDate());
			weeklyTaskTrackWithTaskResponseDTO.setUser(user);
		}
		return weeklyTaskTrackWithTaskResponseDTO;

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
		weeklytasksubmission.setTimetrackStatus(Constants.UserStatus.TASKTRACK_SUBMIT);

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

	@Override
	public StatusResponse saveWeeklyTasktrackWithTask(SaveWeeklyTasktrackWithTaskRequestDTO2 requestData) throws Exception {
		
		StatusResponse response = new StatusResponse();
				
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Long projectId = requestData.getProjectId();
		Long uId = requestData.getuId();
		
		ProjectModel projectModel = projectservice.findById(projectId);
		UserModel userModel = userservice.getUserdetailsbyId(uId);
		
		Date startDate = sdf.parse(requestData.getStartDate());
		Date endDate = sdf.parse(requestData.getEndDate());
		
		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(requestData.getuId(),
				requestData.getProjectId());
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
		
		if (projectDateList.contains(requestData.getTask().getDate())) {
	
		Tasktrack tasktrack = new Tasktrack();
		tasktrack.setDate(sdf.parse(requestData.getTask().getDate()));
		tasktrack.setHours(requestData.getTask().getHour());
		tasktrack.setDescription(requestData.getTask().getDescription());
		tasktrack.setTask(tasktrackService.getTaskById(requestData.getTask().getTaskTypeId()));
		tasktrack.setProject(projectModel);
		tasktrack.setUser(userModel);
		tasktrackRepository.save(tasktrack);
		
		TaskTrackWeeklyApproval weeklyApproval = taskWeeklyApprovalRepository.getDuplicateEntryCount(startDate, endDate, uId);
				
		if (null == weeklyApproval) {
			weeklyApproval= new TaskTrackWeeklyApproval();
		}
			
		if (null != weeklyApproval.getTimetrackFinalStatus()) {
			if (weeklyApproval.getTimetrackFinalStatus().equals(Constants.FinalStatus.TASKTRACK_APPROVED)
					|| weeklyApproval.getTimetrackFinalStatus()
							.equals(Constants.Approver1.TASKTRACK_FORWARDED_TO_LEVEL2)) {
				return new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE,
						"Timetrack has already been approved.");
			}

		}
		
		weeklyApproval.setStartDate(startDate);
		weeklyApproval.setEndDate(endDate);
		weeklyApproval.setProject(projectModel);
		weeklyApproval.setUser(userModel);
		weeklyApproval.setTimetrackStatus(Constants.UserStatus.TASKTRACK_SAVED);
		weeklyApproval.setTimetrackFinalStatus(weeklyApproval.getTimetrackFinalStatus());
		taskWeeklyApprovalRepository.save(weeklyApproval);
		
		response = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, "Insertion completed");
		
		}
		else {
			response = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, "You are not allocated.");
		}
		return response;
	}

	@Override
	public StatusResponse submitWeeklyTasktrackWithTask(SubmitWeeklyTasktrackWithTaskRequestDTO2 requestData) throws Exception {
		
		StatusResponse response = new StatusResponse();
	
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		 
		Date startDate = sdf.parse(requestData.getStartDate());
		Date endDate = sdf.parse(requestData.getEndDate());
	
		Long projectId = requestData.getProjectId();
		Long uId = requestData.getuId();
		
		ProjectModel projectModel = projectservice.findById(projectId);
		UserModel userModel = userservice.getUserdetailsbyId(uId);
	
		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(requestData.getuId(),
				requestData.getProjectId());
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
		List<Tasktrack> taskList = tasktrackRepository.findByUserUserIdAndProjectProjectIdAndDateBetweenOrderByDateAsc(uId, projectId, startDate, endDate);
		
		HashMap<Date, Double> dateHourMap = new HashMap<>();
		for (Tasktrack task : taskList) {
			dateHourMap.put(task.getDate(), task.getHours());
		}
				
		TaskTrackWeeklyApproval weeklyApproval = taskWeeklyApprovalRepository.getDuplicateEntryCount(startDate, endDate, uId);
		
		int indx = 1;
		for (Map.Entry<Date, Double> map : dateHourMap.entrySet()) {
			Double hour = null;
			if (projectDateList.contains(sdf.format(map.getKey())) && map.getValue() != null) {				
				String hourStr = decimalFormat.format(map.getValue());
				hour = Double.parseDouble(hourStr);		
			} else {
				indx++;
				continue;
			}
			switch (indx) {
			case 1: {
				weeklyApproval.setDay1(hour);
				break;
			}
			case 2: {
				weeklyApproval.setDay2(hour);
				break;
			}
			case 3: {
				weeklyApproval.setDay3(hour);
				break;
			}
			case 4: {
				weeklyApproval.setDay4(hour);
				break;
			}
			case 5: {
				weeklyApproval.setDay5(hour);
				break;
			}
			case 6: {
				weeklyApproval.setDay6(hour);
				break;
			}
			case 7: {
				weeklyApproval.setDay7(hour);
				break;
			}
			}
			indx++;
		}
		
		if (null == weeklyApproval) {
			weeklyApproval= new TaskTrackWeeklyApproval();
		}
			
		if (null != weeklyApproval.getTimetrackFinalStatus()) {
			if (weeklyApproval.getTimetrackFinalStatus().equals(Constants.FinalStatus.TASKTRACK_APPROVED)
					|| weeklyApproval.getTimetrackFinalStatus()
							.equals(Constants.Approver1.TASKTRACK_FORWARDED_TO_LEVEL2)) {
				return new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE,
						"Timetrack has already been approved.");
			}

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
				return new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE,
						"Hour should not be more than 24 and less than 0.");
		} 
		
		weeklyApproval.setStartDate(startDate);
		weeklyApproval.setEndDate(endDate);
		weeklyApproval.setProject(projectModel);
		weeklyApproval.setUser(userModel);

		weeklyApproval.setTimetrackStatus(Constants.UserStatus.TASKTRACK_SUBMIT);
		weeklyApproval.setTimetrackFinalStatus(weeklyApproval.getTimetrackStatus());

		taskWeeklyApprovalRepository.save(weeklyApproval);
		response = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, "Submitted successfully");

		return response;
	}

}