package com.EMS.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
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

import com.EMS.dto.DailyTasktrackDto;
import com.EMS.dto.DateBasedTaskDto;
import com.EMS.dto.DateBasedTaskTrackDto;
import com.EMS.dto.SaveSemiMonthlyWithTasksRequestDto;
import com.EMS.dto.SemiMonthlyTaskTrackRequestDTO;
import com.EMS.dto.SemiMonthlyTaskTrackWithTaskResponse;
import com.EMS.dto.SemiMonthlyTaskTrackWithTaskResponseDTO;
import com.EMS.dto.SubmitSemiMonthlyTasktrackWithTaskRequestDto;
import com.EMS.dto.TasktrackDto;
import com.EMS.dto.WeeklyTaskTrackWithTaskResponse;
import com.EMS.dto.WeeklyTaskTrackWithTaskResponseDTO;
import com.EMS.model.AllocationModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.StatusResponse;
import com.EMS.model.Tasktrack;
import com.EMS.model.TasktrackApprovalSemiMonthly;
import com.EMS.model.UserModel;
import com.EMS.repository.AllocationRepository;
import com.EMS.repository.SemiMonthlyTasktrackRepository;
import com.EMS.repository.TasktrackRepository;
import com.EMS.utility.Constants;
import com.EMS.utility.DateUtil;
import com.EMS.utility.ProjectAllocationUtil;
import com.EMS.utility.Constants.UserStatus;
import com.fasterxml.jackson.databind.JsonNode;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Service
public class SemiMonthlyTasktrackServiceImpl implements SemiMonthlyTasktrackService {

	@Autowired
	private SemiMonthlyTasktrackRepository semiMonthlyRepository;

	@Autowired
	UserService userservice;

	@Autowired
	private TasktrackRepository tasktrackRepository;

	@Autowired
	TasktrackService tasktrackService;

	@Autowired
	ProjectService projectservice;

	@Autowired
	private AllocationRepository allocationRepository;

	
	@Override
	public StatusResponse getSemiMonthlyTasktrack(SemiMonthlyTaskTrackRequestDTO requestData) throws Exception {
		StatusResponse result = new StatusResponse();
		JSONObject response = new JSONObject();
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

		ProjectModel projectModel = projectservice.findById(projectId);
		
		String[] taskStatusArray = { Constants.TaskTrackWeeklyApproval.TASKTRACK_WEEKLY_APPROVER_STATUS_APPROVED,Constants.Approver1.TASKTRACK_FORWARDED_TO_LEVEL2 };
		List<String> taskStatusList = Arrays.asList(taskStatusArray);
		TasktrackApprovalSemiMonthly approvalSemiMonthly = (TasktrackApprovalSemiMonthly) semiMonthlyRepository
				.getSemiMonthlyTasktrack(startDate, userId, projectId);

		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(userId,
				projectId);
		List<Date> dateRanges = DateUtil.getDatesBetweenTwo(startDate, endDate);
		dateRanges.add(endDate);
		if (approvalSemiMonthly != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(startDate);

			int date = c.get(Calendar.DATE);
			if (date == 1) {
				JSONArray array = new JSONArray();
				for (AllocationModel al : userProjAllocations) {
					
					List<Date> allocatedDates = DateUtil.getDatesBetweenTwo(al.getStartDate(), al.getEndDate());

					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay1(), dateRanges.get(0)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay2(), dateRanges.get(1)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay3(), dateRanges.get(2)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay4(), dateRanges.get(3)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay5(), dateRanges.get(4)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay6(), dateRanges.get(5)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay7(), dateRanges.get(6)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay8(), dateRanges.get(7)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay9(), dateRanges.get(8)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay10(), dateRanges.get(9)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay11(), dateRanges.get(10)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay12(), dateRanges.get(11)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay13(), dateRanges.get(12)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay14(), dateRanges.get(13)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay15(), dateRanges.get(14)));
				
					response.put("taskList", array);
				}
				
				response.put("enabled", taskStatusList.contains(approvalSemiMonthly.getFirstHalfFinalStatus())? Boolean.TRUE: Boolean.FALSE);
			
				String firstHalfApprover1 = null;
				if (approvalSemiMonthly.getFirstHalfApproverOne() != null) {
					firstHalfApprover1 =  approvalSemiMonthly.getFirstHalfApproverOne().getFirstName() + " "
									+ approvalSemiMonthly.getFirstHalfApproverOne().getLastName();
					
				}
								
				if (null == firstHalfApprover1 || firstHalfApprover1.equals("")) {
					firstHalfApprover1 = null !=  projectModel.getProjectOwner()? projectModel.getProjectOwner().getFirstName() + " "
							+ projectModel.getProjectOwner().getLastName() :"";
				}
				
				String firstHalfApprover2 = null;
				if (approvalSemiMonthly.getFirstHalfApproverTwo() != null) {
					firstHalfApprover2 =  approvalSemiMonthly.getFirstHalfApproverTwo().getFirstName() + " "
									+ approvalSemiMonthly.getFirstHalfApproverTwo().getLastName();

				}

				if (null == firstHalfApprover2 || firstHalfApprover2.equals("")) {
					firstHalfApprover2 = null !=  projectModel.getOnsite_lead()? projectModel.getOnsite_lead().getFirstName() + " "
							+ projectModel.getOnsite_lead().getLastName() :"";

				}
				
				JSONObject approver1 = new JSONObject();
				approver1.put("approver", firstHalfApprover1);		
				response.put("approver1", approver1);
				
				JSONObject approver2 = new JSONObject();
				approver2.put("approver", firstHalfApprover2);
				response.put("approver2", approver2);
				
				JSONObject user = new JSONObject();
				response.put("user", user);
				
			}
			if (date == 16) {

				for (AllocationModel al : userProjAllocations) {

					List<Date> allocatedDates = DateUtil.getDatesBetweenTwo(al.getStartDate(), al.getEndDate());

					JSONArray array = new JSONArray();
					
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay16(), dateRanges.get(0)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay17(), dateRanges.get(1)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay18(), dateRanges.get(2)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay19(), dateRanges.get(3)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay20(), dateRanges.get(4)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay21(), dateRanges.get(5)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay22(), dateRanges.get(6)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay23(), dateRanges.get(7)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay24(), dateRanges.get(8)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay25(), dateRanges.get(9)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay26(), dateRanges.get(10)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay27(), dateRanges.get(11)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay28(), dateRanges.get(12)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay29(), dateRanges.get(13)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay30(), dateRanges.get(14)));
					array.add(addHoursandDaytoArray(sdf, allocatedDates,  approvalSemiMonthly.getDay31(), dateRanges.get(15)));
					
					response.put("taskList", array);

					response.put("enabled", taskStatusList.contains(approvalSemiMonthly.getSecondHalfFinalStatus())? Boolean.TRUE : Boolean.FALSE);
				
					String secondHalfApprover1 = null;
					if (approvalSemiMonthly.getSecondHalfApproverOne() != null) {
						secondHalfApprover1 =  approvalSemiMonthly.getSecondHalfApproverOne().getFirstName() + " "
										+ approvalSemiMonthly.getSecondHalfApproverOne().getLastName();

					}
					
					
					if (null == secondHalfApprover1 || secondHalfApprover1.equals("")) {
						secondHalfApprover1 = null !=  projectModel.getProjectOwner()? projectModel.getProjectOwner().getFirstName() + " "
								+ projectModel.getProjectOwner().getLastName() :"";

					}
					
					String secondHalfApprover2 = null;
					if (approvalSemiMonthly.getSecondHalfApproverTwo() != null) {
						secondHalfApprover2 =  approvalSemiMonthly.getSecondHalfApproverTwo().getFirstName() + " "
										+ approvalSemiMonthly.getSecondHalfApproverTwo().getLastName();

					}

					if (null == secondHalfApprover2 || secondHalfApprover2.equals("")) {
						secondHalfApprover2 = null !=  projectModel.getOnsite_lead()? projectModel.getOnsite_lead().getFirstName() + " "
								+ projectModel.getOnsite_lead().getLastName() :"";

					}
					
					JSONObject approver1 = new JSONObject();
					approver1.put("approver", secondHalfApprover1);
					response.put("approver1", approver1);
					
					JSONObject approver2 = new JSONObject();
					approver2.put("approver", secondHalfApprover2);				
					response.put("approver2", approver2);
					
					JSONObject user = new JSONObject();
					response.put("user", user);
				}
			}
			result = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, response);

		} else {
			JSONArray array = new JSONArray();
			for (AllocationModel al : userProjAllocations) {
				List<Date> allocatedDates = DateUtil.getDatesBetweenTwo(al.getStartDate(), al.getEndDate());
				for (Date date : dateRanges) {					
						array.add(addHoursandDaytoArray(sdf, allocatedDates, null, date));																																																																																																																			
				}
			}
			JSONObject user = new JSONObject();
			user.put("status", Constants.UserStatus.TASKTRACK_OPEN);
			user.put("date", "");
			response.put("user", user);
			
			String approver1 = null != projectModel.getProjectOwner()
					? projectModel.getProjectOwner().getFirstName() + " " + projectModel.getProjectOwner().getLastName()
					: "";

			JSONObject approver1Obj = new JSONObject();
			approver1Obj.put("approver", approver1);
			response.put("approver1",approver1Obj);

			String approver2 = null != projectModel.getOnsite_lead()
					? projectModel.getOnsite_lead().getFirstName() + " " + projectModel.getOnsite_lead().getLastName()
					: "";

			JSONObject approver2Obj = new JSONObject();
			approver2Obj.put("approver", approver2);
			response.put("approver2", approver2Obj);
			
			response.put("taskList", array);
			response.put("enabled",true);
			result = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, response);
		}

		return result;

	}

	private JSONObject addHoursandDaytoArray(SimpleDateFormat sdf, List<Date> allocatedDates, Double hour, Date date) {
		JSONObject response = new JSONObject();
		JSONObject dayResponse = new JSONObject();
		dayResponse.put("hour", hour);
		dayResponse.put("enabled",allocatedDates.contains(date));
		response.put(sdf.format(date), dayResponse);
		return response;
	}

	@Override
	public StatusResponse submitForSemiMonthlyApproval(JSONObject requestData) throws ParseException {
		return saveOrSubmitSemiMonthlyTaskTrackApproval(requestData,Boolean.FALSE);
	}

	@Override
	public StatusResponse saveSemiMonthlyTaskTrackApproval(JSONObject requestData) throws ParseException {
		return saveOrSubmitSemiMonthlyTaskTrackApproval(requestData,Boolean.TRUE);	
	}

	private StatusResponse saveOrSubmitSemiMonthlyTaskTrackApproval(JSONObject requestData,Boolean isSave) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int requeststatus = 0;

		StatusResponse response = new StatusResponse();

		Map<String, Object> timetrack = (Map<String, Object>) requestData.get("timetrack");
		
		Date startDate = sdf.parse(requestData.get("startDate").toString());
		Date endDate = sdf.parse(requestData.get("endDate").toString());
		int year = Integer.parseInt(requestData.get("year").toString());
		int month = Integer.parseInt(requestData.get("month").toString());
		Long userId = Long.parseLong(requestData.get("userId").toString());
		
		Boolean isFirstHalf = Boolean.TRUE;
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		if (cal.get(Calendar.DAY_OF_MONTH) > 15) {
			isFirstHalf = Boolean.FALSE;
		} 
		
		UserModel userInfo = userservice.getUserdetailsbyId(userId);

		TasktrackApprovalSemiMonthly semiMonthlyApproval = semiMonthlyRepository.checkduplicationForsemiMonthlyTaskTrack(userId, month, year);
		
		if (null == semiMonthlyApproval) {
			semiMonthlyApproval = new TasktrackApprovalSemiMonthly();
		}
		if((isFirstHalf && semiMonthlyApproval.getFirstHalfFinalStatus() != null 
				&& (semiMonthlyApproval.getFirstHalfFinalStatus().equals(Constants.FinalStatus.TASKTRACK_FORWARDED_TO_LEVEL2)
				|| semiMonthlyApproval.getFirstHalfFinalStatus().endsWith(Constants.FinalStatus.TASKTRACK_APPROVED)))
				
				|| (!isFirstHalf && semiMonthlyApproval.getSecondHalfFinalStatus() != null
				&& (semiMonthlyApproval.getSecondHalfFinalStatus().equals(Constants.Approver1.TASKTRACK_FORWARDED_TO_LEVEL2)
				|| semiMonthlyApproval.getSecondHalfFinalStatus().endsWith(Constants.Finance.TASKTRACK_APPROVED)))) {
			return new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, "Timetrack has already been approved.");
		}
		semiMonthlyApproval.setYear(year);
		semiMonthlyApproval.setMonth(month);

		if (!userInfo.equals(null)) {
			semiMonthlyApproval.setUser(userInfo);
		}
		
		Long projectId = Long.parseLong(requestData.get("projectId").toString());
		ProjectModel projectInfo = projectservice.findById(projectId);

		if (!projectInfo.equals(null)) {
			semiMonthlyApproval.setProject(projectInfo);
		}
		
		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(userId,
				projectId);

		List<Date> projectDateList = new ArrayList<Date>();

		for (AllocationModel al : userProjAllocations) {
			Date allocStartDate = al.getStartDate();
			Date allocEndDate = al.getEndDate();

			Calendar fromDate = Calendar.getInstance();
			Calendar toDate = Calendar.getInstance();
		
			if (allocStartDate.before(startDate )) {
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
				Date date = fromDate.getTime();
				projectDateList.add(date);
				fromDate.add(Calendar.DATE, 1);
			}
		}
		
		for(Date date : projectDateList) {
			cal.setTime(date);
			int day = cal.get(Calendar.DATE);
			Object hr = timetrack.get(sdf.format(date));
			if(hr != null) {
			Double hour = Double.parseDouble(hr.toString());
				if(hour < 0 || hour > 24) {
					requeststatus = 1;
					break;
				}
				switch(day) {
					case 1: {
						semiMonthlyApproval.setDay1(hour);
						break;
					}
					case 2: {
						semiMonthlyApproval.setDay2(hour);
						break;
					}
					case 3: {
						semiMonthlyApproval.setDay3(hour);
						break;
					}
					case 4: {
						semiMonthlyApproval.setDay4(hour);
						break;
					}
					case 5: {
						semiMonthlyApproval.setDay5(hour);
						break;
					}
					case 6: {
						semiMonthlyApproval.setDay6(hour);
						break;
					}
					case 7: {
						semiMonthlyApproval.setDay7(hour);
						break;
					}
					case 8: {
						semiMonthlyApproval.setDay8(hour);
						break;
					}
					case 9: {
						semiMonthlyApproval.setDay9(hour);
						break;
					}
					case 10: {
						semiMonthlyApproval.setDay10(hour);
						break;
					}
					case 11: {
						semiMonthlyApproval.setDay11(hour);
						break;
					}
					case 12: {
						semiMonthlyApproval.setDay12(hour);
						break;
					}
					case 13: {
						semiMonthlyApproval.setDay13(hour);
						break;
					}
					case 14: {
						semiMonthlyApproval.setDay14(hour);
						break;
					}
					case 15: {
						semiMonthlyApproval.setDay15(hour);
						break;
					}
					case 16: {
						semiMonthlyApproval.setDay16(hour);
						break;
					}
					case 17: {
						semiMonthlyApproval.setDay17(hour);
						break;
					}
					case 18: {
						semiMonthlyApproval.setDay18(hour);
						break;
					}
					case 19: {
						semiMonthlyApproval.setDay19(hour);
						break;
					}
					case 20: {
						semiMonthlyApproval.setDay20(hour);
						break;
					}
					case 21: {
						semiMonthlyApproval.setDay21(hour);
						break;
					}
					case 22: {
						semiMonthlyApproval.setDay22(hour);
						break;
					}
					case 23: {
						semiMonthlyApproval.setDay23(hour);
						break;
					}
					case 24: {
						semiMonthlyApproval.setDay24(hour);
						break;
					}
					case 25: {
						semiMonthlyApproval.setDay25(hour);
						break;
					}
					case 26: {
						semiMonthlyApproval.setDay26(hour);
						break;
					}
					case 27: {
						semiMonthlyApproval.setDay27(hour);
						break;
					}
					case 28: {
						semiMonthlyApproval.setDay28(hour);
						break;
					}
					case 29: {
						semiMonthlyApproval.setDay29(hour);
						break;
					}
					case 30: {
						semiMonthlyApproval.setDay30(hour);
						break;
					}
					case 31: {
						semiMonthlyApproval.setDay31(hour);
						break;
					}
				}
			}
		}

		if (isFirstHalf) {

			semiMonthlyApproval
					.setFirstHalfFinalStatus(isSave ? UserStatus.TASKTRACK_SAVED : UserStatus.TASKTRACK_SUBMIT);
			semiMonthlyApproval.setSubmissionPeriod(Constants.UserStatus.TASKTRACK_FIRSTHALF_PERIOD_STATUS);

		} else {

			semiMonthlyApproval
					.setSecondHalfFinalStatus(isSave ? UserStatus.TASKTRACK_SAVED : UserStatus.TASKTRACK_SUBMIT);
			semiMonthlyApproval.setSubmissionPeriod(Constants.UserStatus.TASKTRACK_SECONDHALF_PERIOD_STATUS);

		}
	
		if (requeststatus == 0) {
			semiMonthlyRepository.save(semiMonthlyApproval);
			response = new StatusResponse("success", 200, 
					isSave ? "Semi monthly data saved successfully." : "Semi monthly data submitted successfully.");
		} else {
			response = new StatusResponse("success", 200, 
					isSave ? "Semi monthly data insertion failed due to invalid data" : "Semi monthly data submission failed due to invalid data");
		}

		return response;
	}
	
	@Override
	public StatusResponse getSemiMonthlyTasksForSubmission(JsonNode requestData) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String start = requestData.get("startDate").asText();
		String end = requestData.get("endDate").asText();
		Long userId = requestData.get("userId").asLong();
		StatusResponse response = new StatusResponse();
		Date endDate = null, startDate = null;
		TasktrackApprovalSemiMonthly semiMonthlytasksubmission = new TasktrackApprovalSemiMonthly();

		startDate = sdf.parse(start);
		endDate = sdf.parse(end);

		ArrayList<Tasktrack> tasklist = tasktrackRepository.getsavedTaskslist(startDate, endDate, userId);
		Map<Date, Double> dailyhours = new HashMap<Date, Double>();
		Long projectId = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);

		UserModel userDetails = userservice.getUserdetailsbyId(userId);
		semiMonthlytasksubmission.setUser(userDetails);

		semiMonthlytasksubmission.setYear(calendar.get(Calendar.YEAR));

		semiMonthlytasksubmission.setMonth(calendar.get(Calendar.MONTH) + 1);

		semiMonthlytasksubmission = semiMonthlyRepository.checkduplicationForsemiMonthlyTaskTrack(userId,
				semiMonthlytasksubmission.getMonth(), semiMonthlytasksubmission.getYear());

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
		semiMonthlytasksubmission.setProject(project);

		Map<Object, Object> result = dailyhours.entrySet().stream().sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
						LinkedHashMap::new));

		LocalDate localstartdate = LocalDate.parse(start);
		LocalDate localenddate = LocalDate.parse(end);

		while (!localstartdate.isAfter(localenddate)) {
			String stgdate = String.valueOf(localstartdate);
			Date date1 = null;

			date1 = sdf.parse(stgdate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date1);

			Double hours = 0.0;
			if (result.get(date1) != null)
				hours = Double.parseDouble(result.get(date1).toString());

			if (cal.get(Calendar.DAY_OF_MONTH) == 1)
				semiMonthlytasksubmission.setDay1(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 2)
				semiMonthlytasksubmission.setDay2(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 3)
				semiMonthlytasksubmission.setDay3(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 4)
				semiMonthlytasksubmission.setDay4(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 5)
				semiMonthlytasksubmission.setDay5(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 6)
				semiMonthlytasksubmission.setDay6(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 7)
				semiMonthlytasksubmission.setDay7(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 8)
				semiMonthlytasksubmission.setDay8(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 9)
				semiMonthlytasksubmission.setDay9(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 10)
				semiMonthlytasksubmission.setDay10(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 11)
				semiMonthlytasksubmission.setDay11(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 12)
				semiMonthlytasksubmission.setDay12(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 13)
				semiMonthlytasksubmission.setDay13(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 14)
				semiMonthlytasksubmission.setDay14(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 15)
				semiMonthlytasksubmission.setDay15(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 16)
				semiMonthlytasksubmission.setDay16(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 17)
				semiMonthlytasksubmission.setDay17(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 18)
				semiMonthlytasksubmission.setDay18(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 19)
				semiMonthlytasksubmission.setDay19(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 20)
				semiMonthlytasksubmission.setDay20(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 21)
				semiMonthlytasksubmission.setDay21(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 22)
				semiMonthlytasksubmission.setDay22(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 23)
				semiMonthlytasksubmission.setDay23(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 24)
				semiMonthlytasksubmission.setDay24(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 25)
				semiMonthlytasksubmission.setDay25(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 26)
				semiMonthlytasksubmission.setDay26(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 27)
				semiMonthlytasksubmission.setDay27(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 28)
				semiMonthlytasksubmission.setDay28(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 29)
				semiMonthlytasksubmission.setDay29(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 30)
				semiMonthlytasksubmission.setDay30(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 31)
				semiMonthlytasksubmission.setDay31(hours);

			localstartdate = localstartdate.plusDays(1);

		}

		semiMonthlyRepository.save(semiMonthlytasksubmission);
		response = new StatusResponse("success", 200, "Semi monthly data submission completed");

		return response;
	}


	@Override
	public SemiMonthlyTaskTrackWithTaskResponseDTO getSemiMonthlyTasktrackWithTask(SemiMonthlyTaskTrackRequestDTO requestData) throws Exception {
		
		SemiMonthlyTaskTrackWithTaskResponseDTO semiMonthlyTaskTrackWithTaskResponseDTO = new SemiMonthlyTaskTrackWithTaskResponseDTO();
		List<SemiMonthlyTaskTrackWithTaskResponse> taskTrackResponseList = new ArrayList<>();
	
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

		Boolean isFirstHalf = Boolean.TRUE;
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		if (cal.get(Calendar.DAY_OF_MONTH) > 15) {
			isFirstHalf = Boolean.FALSE;
		} 
		
		List<Tasktrack> tasktrackList = tasktrackRepository
				.findByUserUserIdAndProjectProjectIdAndDateBetweenOrderByDateAsc(userId, projectId, startDate, endDate);

		Calendar c = Calendar.getInstance();
		c.setTime(startDate);

		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);

		TasktrackApprovalSemiMonthly tasktrackStatus = semiMonthlyRepository
				.findByUserUserIdAndProjectProjectIdInAndMonthEqualsAndYearEquals(userId, projectId, month, year);
		
		List<String> projectDateList = ProjectAllocationUtil.findAllocatedDates(allocationRepository, startDate, endDate, sdf, userId, projectId);

		HashSet<Date> trackdate = new HashSet<Date>();

		List<Date> reqDateRange = DateUtil.getDatesBetweenTwo(startDate, endDate);
		
		
		if (!tasktrackList.isEmpty()) {

			for (Date date : reqDateRange) {
				if (projectDateList.contains(sdf.format(date))) {

					for (Tasktrack tasktrackOuter : tasktrackList) {
						if (!trackdate.contains(tasktrackOuter.getDate())) {							
							trackdate.add(tasktrackOuter.getDate());
							String intialDate = sdf.format(tasktrackOuter.getDate());
							SemiMonthlyTaskTrackWithTaskResponse taskTrackResponse = new SemiMonthlyTaskTrackWithTaskResponse();
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
										
					semiMonthlyTaskTrackWithTaskResponseDTO.setTasktrackList(taskTrackResponseList);

				} else {
					SemiMonthlyTaskTrackWithTaskResponse taskTrackResponse = new SemiMonthlyTaskTrackWithTaskResponse();
					taskTrackResponse.setTaskList(new ArrayList<>());
					taskTrackResponse.setEnabled(false);
					taskTrackResponse.setDate(sdf.format(date));
					taskTrackResponse.setFinalHour(0.0);
					taskTrackResponseList.add(taskTrackResponse);
					semiMonthlyTaskTrackWithTaskResponseDTO.setTasktrackList(taskTrackResponseList);
				}
				
			}
			semiMonthlyTaskTrackWithTaskResponseDTO.setTasktrackList(addMissingDate(reqDateRange,taskTrackResponseList));
		}
		
		if (tasktrackList.isEmpty()) {

			for (Date reqDate : reqDateRange) {

				SemiMonthlyTaskTrackWithTaskResponse taskTrackResponse = new SemiMonthlyTaskTrackWithTaskResponse();
				taskTrackResponse.setTaskList(new ArrayList<>());
				taskTrackResponse.setDate(sdf.format(reqDate));
				taskTrackResponse.setFinalHour(0.0);
				taskTrackResponse.setEnabled(projectDateList.contains(sdf.format(reqDate)) ? true : false);
				taskTrackResponseList.add(taskTrackResponse);
				semiMonthlyTaskTrackWithTaskResponseDTO.setTasktrackList(taskTrackResponseList);
			}
		}
		 
		if (tasktrackStatus != null) {
			String[] taskStatusArray = { Constants.TaskTrackWeeklyApproval.TASKTRACK_WEEKLY_APPROVER_STATUS_APPROVED };
			List<String> taskStatusList = Arrays.asList(taskStatusArray);

			
			if(isFirstHalf) {
				semiMonthlyTaskTrackWithTaskResponseDTO.setEnabled(taskStatusList.contains(tasktrackStatus.getFirstHalfFinalStatus()) ? Boolean.FALSE : Boolean.TRUE);
			}else
				semiMonthlyTaskTrackWithTaskResponseDTO.setEnabled(taskStatusList.contains(tasktrackStatus.getSecondHalfFinalStatus()) ? Boolean.FALSE : Boolean.TRUE);

			ProjectModel projectModel = projectservice.findById(projectId);

			String	approver1 = null != projectModel.getProjectOwner() ? projectModel.getProjectOwner().getFirstName() + " "
						+ projectModel.getProjectOwner().getLastName() : "";



			JSONObject approver1Obj = new JSONObject();
			approver1Obj.put("approver", approver1);

			semiMonthlyTaskTrackWithTaskResponseDTO.setApprover1(approver1Obj);

				String approver2 = null != projectModel.getOnsite_lead() ? projectModel.getOnsite_lead().getFirstName() + " "
						+ projectModel.getOnsite_lead().getLastName() : "";


			JSONObject approver2Obj = new JSONObject();
			approver2Obj.put("approver", approver2);

			semiMonthlyTaskTrackWithTaskResponseDTO.setApprover2(approver2Obj);

			JSONObject user = new JSONObject();
			semiMonthlyTaskTrackWithTaskResponseDTO.setUser(user);
		}
		
		return semiMonthlyTaskTrackWithTaskResponseDTO;
		
	}
	
	

	@Override
	public StatusResponse saveSemiMonthlyTasktrackWithTask(SaveSemiMonthlyWithTasksRequestDto dateBasedTaskTrackDto)
			throws Exception {
		return saveSemiMonthlyTasktrackWithTaskData(dateBasedTaskTrackDto);
	}

	/*
	 * @Author haritha
	 * 
	 * 
	 */
	private StatusResponse saveSemiMonthlyTasktrackWithTaskData(
			SaveSemiMonthlyWithTasksRequestDto dateBasedTaskTrackDto) throws Exception {

		StatusResponse response = new StatusResponse();
		int requeststatus = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Long projectId = dateBasedTaskTrackDto.getProjectId();
		Long uId = dateBasedTaskTrackDto.getuId();

		ProjectModel projectModel =projectservice.findById(projectId);
	
		UserModel userModel = userservice.getUserdetailsbyId(uId);
	
		Date startDate = sdf.parse(dateBasedTaskTrackDto.getStartDate());
		Date endDate = sdf.parse(dateBasedTaskTrackDto.getEndDate());

		Boolean isFirstHalf = Boolean.TRUE;
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		if (cal.get(Calendar.DAY_OF_MONTH) > 15) {
			isFirstHalf = Boolean.FALSE;
		}

		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(
				dateBasedTaskTrackDto.getuId(), dateBasedTaskTrackDto.getProjectId());
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

		if (projectDateList.contains(dateBasedTaskTrackDto.getTask().getDate())) {
			Tasktrack tasktrack = new Tasktrack();
			tasktrack.setDate(sdf.parse(dateBasedTaskTrackDto.getTask().getDate()));
			tasktrack.setHours(dateBasedTaskTrackDto.getTask().getHour());
			tasktrack.setDescription(dateBasedTaskTrackDto.getTask().getDescription());
			tasktrack.setTask(tasktrackService.getTaskById(dateBasedTaskTrackDto.getTask().getTaskTypeId()));
			tasktrack.setProject(projectModel);
			tasktrack.setUser(userModel);
			tasktrackRepository.save(tasktrack);

			Date currentdate = sdf.parse(dateBasedTaskTrackDto.getTask().getDate());
			Calendar calender = Calendar.getInstance();
			calender.setTime(currentdate);

			int month = calender.get(Calendar.MONTH) + 1;
			int year = calender.get(Calendar.YEAR);

			TasktrackApprovalSemiMonthly semiMonthlyApproval = semiMonthlyRepository
					.checkduplicationForsemiMonthlyTaskTrack(uId, month, year);

			if (null == semiMonthlyApproval) {
				semiMonthlyApproval = new TasktrackApprovalSemiMonthly();
			}
			if ((isFirstHalf && semiMonthlyApproval.getFirstHalfFinalStatus() != null && (semiMonthlyApproval
					.getFirstHalfFinalStatus().equals(Constants.Approver1.TASKTRACK_FORWARDED_TO_LEVEL2)
					|| semiMonthlyApproval.getFirstHalfFinalStatus().endsWith(Constants.Finance.TASKTRACK_APPROVED)))

					|| (!isFirstHalf && semiMonthlyApproval.getSecondHalfFinalStatus() != null
							&& (semiMonthlyApproval.getSecondHalfFinalStatus()
									.equals(Constants.Approver1.TASKTRACK_FORWARDED_TO_LEVEL2)
									|| semiMonthlyApproval.getSecondHalfFinalStatus()
											.endsWith(Constants.Finance.TASKTRACK_APPROVED)))) {
				return new StatusResponse("success", 200, "Timetrack has already been approved.");
			}

			semiMonthlyApproval.setYear(year);
			semiMonthlyApproval.setMonth(month);
			semiMonthlyApproval.setProject(projectModel);
			semiMonthlyApproval.setUser(userModel);
			if (!isFirstHalf) {
				semiMonthlyApproval.setSecondHalfFinalStatus(Constants.UserStatus.TASKTRACK_SAVED);
				semiMonthlyApproval.setSubmissionPeriod(Constants.UserStatus.TASKTRACK_SECONDHALF_PERIOD_STATUS);
			} else if (isFirstHalf) {
				semiMonthlyApproval.setFirstHalfFinalStatus(Constants.UserStatus.TASKTRACK_SAVED);
				semiMonthlyApproval.setSubmissionPeriod(Constants.UserStatus.TASKTRACK_FIRSTHALF_PERIOD_STATUS);
			}

			if (requeststatus == 0) {
				semiMonthlyRepository.save(semiMonthlyApproval);
				response = new StatusResponse("success", 200, "Semi monthly data saved successfully.");
			} else
				response = new StatusResponse("success", 200, "Semi monthly data insertion failed due to invalid data");

		} else {
			response = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, "You are not allocated.");
		}

		return response;
	}
	
	
	@Override
	public StatusResponse submitSemiMonthlyTasktrackWithTask(SubmitSemiMonthlyTasktrackWithTaskRequestDto dateBasedTaskTrackDto) throws Exception {
		return submitSemiMonthlyTasktrackWithTaskData(dateBasedTaskTrackDto);
	}

	/*
	 * @Author Haritha
	 * 
	 * @version 1.0
	 * 
	 * @Since 24-01-2020
	 * 
	 */

	private StatusResponse submitSemiMonthlyTasktrackWithTaskData(SubmitSemiMonthlyTasktrackWithTaskRequestDto dateBasedTaskTrackDto) throws Exception {

		StatusResponse response = new StatusResponse();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DecimalFormat decimalFormat = new DecimalFormat("0.0");

		Date startDate = sdf.parse(dateBasedTaskTrackDto.getStartDate());
		Date endDate = sdf.parse(dateBasedTaskTrackDto.getEndDate());

		Long projectId = dateBasedTaskTrackDto.getProjectId();
		Long uId = dateBasedTaskTrackDto.getuId();

		ProjectModel projectModel = projectservice.findById(projectId);
		UserModel userModel = userservice.getUserdetailsbyId(uId);

		Boolean isFirstHalf = Boolean.TRUE;
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		if (cal.get(Calendar.DAY_OF_MONTH) > 15) {
			isFirstHalf = Boolean.FALSE;
		}

		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(
				dateBasedTaskTrackDto.getuId(), dateBasedTaskTrackDto.getProjectId());
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

		Calendar calender = Calendar.getInstance();
		calender.setTime(startDate);

		int month = calender.get(Calendar.MONTH) + 1;
		int year = calender.get(Calendar.YEAR);

		List<Tasktrack> taskList = tasktrackRepository
				.findByUserUserIdAndProjectProjectIdAndDateBetweenOrderByDateAsc(uId, projectId, startDate, endDate);

		HashMap<Date, Double> dateHourMap = new HashMap<>();
		for (Tasktrack task : taskList) {
			dateHourMap.put(task.getDate(), task.getHours());
		}

		TasktrackApprovalSemiMonthly semiMonthlyApproval = semiMonthlyRepository
				.checkduplicationForsemiMonthlyTaskTrack(uId, month, year);

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
				semiMonthlyApproval.setDay1(hour);
				break;
			}
			case 2: {
				semiMonthlyApproval.setDay2(hour);
				break;
			}
			case 3: {
				semiMonthlyApproval.setDay3(hour);
				break;
			}
			case 4: {
				semiMonthlyApproval.setDay4(hour);
				break;
			}
			case 5: {
				semiMonthlyApproval.setDay5(hour);
				break;
			}
			case 6: {
				semiMonthlyApproval.setDay6(hour);
				break;
			}
			case 7: {
				semiMonthlyApproval.setDay7(hour);
				break;
			}
			case 8: {
				semiMonthlyApproval.setDay8(hour);
				break;
			}
			case 9: {
				semiMonthlyApproval.setDay9(hour);
				break;
			}
			case 10: {
				semiMonthlyApproval.setDay10(hour);
				break;
			}
			case 11: {
				semiMonthlyApproval.setDay11(hour);
				break;
			}
			case 12: {
				semiMonthlyApproval.setDay12(hour);
				break;
			}
			case 13: {
				semiMonthlyApproval.setDay13(hour);
				break;
			}
			case 14: {
				semiMonthlyApproval.setDay14(hour);
				break;
			}
			case 15: {
				semiMonthlyApproval.setDay15(hour);
				break;
			}
			case 16: {
				semiMonthlyApproval.setDay16(hour);
				break;
			}
			case 17: {
				semiMonthlyApproval.setDay17(hour);
				break;
			}
			case 18: {
				semiMonthlyApproval.setDay18(hour);
				break;
			}
			case 19: {
				semiMonthlyApproval.setDay19(hour);
				break;
			}
			case 20: {
				semiMonthlyApproval.setDay20(hour);
				break;
			}
			case 21: {
				semiMonthlyApproval.setDay21(hour);
				break;
			}
			case 22: {
				semiMonthlyApproval.setDay22(hour);
				break;
			}
			case 23: {
				semiMonthlyApproval.setDay23(hour);
				break;
			}
			case 24: {
				semiMonthlyApproval.setDay24(hour);
				break;
			}
			case 25: {
				semiMonthlyApproval.setDay25(hour);
				break;
			}
			case 26: {
				semiMonthlyApproval.setDay26(hour);
				break;
			}
			case 27: {
				semiMonthlyApproval.setDay27(hour);
				break;
			}
			case 28: {
				semiMonthlyApproval.setDay28(hour);
				break;
			}
			case 29: {
				semiMonthlyApproval.setDay29(hour);
				break;
			}
			case 30: {
				semiMonthlyApproval.setDay30(hour);
				break;
			}
			case 31: {
				semiMonthlyApproval.setDay31(hour);
				break;
			}
			}
			indx++;
		}

		if (null == semiMonthlyApproval) {
			semiMonthlyApproval = new TasktrackApprovalSemiMonthly();
		}

		if ((isFirstHalf && semiMonthlyApproval.getFirstHalfFinalStatus() != null && (semiMonthlyApproval
				.getFirstHalfFinalStatus().equals(Constants.Approver1.TASKTRACK_FORWARDED_TO_LEVEL2)
				|| semiMonthlyApproval.getFirstHalfFinalStatus().endsWith(Constants.Finance.TASKTRACK_APPROVED)))

				|| (!isFirstHalf && semiMonthlyApproval.getSecondHalfFinalStatus() != null
						&& (semiMonthlyApproval.getSecondHalfFinalStatus()
								.equals(Constants.Approver1.TASKTRACK_FORWARDED_TO_LEVEL2)
								|| semiMonthlyApproval.getSecondHalfFinalStatus()
										.endsWith(Constants.Finance.TASKTRACK_APPROVED)))) {
			return new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, "Timetrack has already been approved.");
		}

		if ((!semiMonthlyApproval.getDay1().equals(null)) && (!semiMonthlyApproval.getDay2().equals(null))
				&& (!semiMonthlyApproval.getDay3().equals(null)) && (!semiMonthlyApproval.getDay4().equals(null))
				&& (!semiMonthlyApproval.getDay5().equals(null)) && (!semiMonthlyApproval.getDay6().equals(null))
				&& (!semiMonthlyApproval.getDay7().equals(null)) && (!semiMonthlyApproval.getDay8().equals(null))
				&& (!semiMonthlyApproval.getDay9().equals(null)) && (!semiMonthlyApproval.getDay10().equals(null))
				&& (!semiMonthlyApproval.getDay11().equals(null)) && (!semiMonthlyApproval.getDay12().equals(null))
				&& (!semiMonthlyApproval.getDay13().equals(null)) && (!semiMonthlyApproval.getDay14().equals(null))
				&& (!semiMonthlyApproval.getDay15().equals(null)) && (!semiMonthlyApproval.getDay16().equals(null))
				&& (!semiMonthlyApproval.getDay17().equals(null)) && (!semiMonthlyApproval.getDay18().equals(null))
				&& (!semiMonthlyApproval.getDay19().equals(null)) && (!semiMonthlyApproval.getDay20().equals(null))
				&& (!semiMonthlyApproval.getDay21().equals(null)) && (!semiMonthlyApproval.getDay22().equals(null))
				&& (!semiMonthlyApproval.getDay23().equals(null)) && (!semiMonthlyApproval.getDay24().equals(null))
				&& (!semiMonthlyApproval.getDay25().equals(null)) && (!semiMonthlyApproval.getDay26().equals(null))
				&& (!semiMonthlyApproval.getDay27().equals(null)) && (!semiMonthlyApproval.getDay28().equals(null))
				&& (!semiMonthlyApproval.getDay29().equals(null)) && (!semiMonthlyApproval.getDay30().equals(null))
				&& (!semiMonthlyApproval.getDay31().equals(null))) {

			if ((semiMonthlyApproval.getDay1() < 0) || (semiMonthlyApproval.getDay1() > 24)
					|| (semiMonthlyApproval.getDay2() < 0) || (semiMonthlyApproval.getDay2() > 24)
					|| (semiMonthlyApproval.getDay3() < 0) || (semiMonthlyApproval.getDay3() > 24)
					|| (semiMonthlyApproval.getDay4() < 0) || (semiMonthlyApproval.getDay4() > 24)
					|| (semiMonthlyApproval.getDay5() < 0) || (semiMonthlyApproval.getDay5() > 24)
					|| (semiMonthlyApproval.getDay6() < 0) || (semiMonthlyApproval.getDay6() > 24)
					|| (semiMonthlyApproval.getDay7() < 0) || (semiMonthlyApproval.getDay7() > 24)
					|| (semiMonthlyApproval.getDay8() < 0) || (semiMonthlyApproval.getDay8() > 24)
					|| (semiMonthlyApproval.getDay9() < 0) || (semiMonthlyApproval.getDay9() > 24)
					|| (semiMonthlyApproval.getDay10() < 0) || (semiMonthlyApproval.getDay10() > 24)
					|| (semiMonthlyApproval.getDay11() < 0) || (semiMonthlyApproval.getDay11() > 24)
					|| (semiMonthlyApproval.getDay12() < 0) || (semiMonthlyApproval.getDay12() > 24)
					|| (semiMonthlyApproval.getDay13() < 0) || (semiMonthlyApproval.getDay13() > 24)
					|| (semiMonthlyApproval.getDay14() < 0) || (semiMonthlyApproval.getDay14() > 24)
					|| (semiMonthlyApproval.getDay15() < 0) || (semiMonthlyApproval.getDay15() > 24)
					|| (semiMonthlyApproval.getDay16() < 0) || (semiMonthlyApproval.getDay16() > 24)
					|| (semiMonthlyApproval.getDay17() < 0) || (semiMonthlyApproval.getDay17() > 24)
					|| (semiMonthlyApproval.getDay18() < 0) || (semiMonthlyApproval.getDay18() > 24)
					|| (semiMonthlyApproval.getDay19() < 0) || (semiMonthlyApproval.getDay19() > 24)
					|| (semiMonthlyApproval.getDay20() < 0) || (semiMonthlyApproval.getDay20() > 24)
					|| (semiMonthlyApproval.getDay21() < 0) || (semiMonthlyApproval.getDay21() > 24)
					|| (semiMonthlyApproval.getDay22() < 0) || (semiMonthlyApproval.getDay22() > 24)
					|| (semiMonthlyApproval.getDay23() < 0) || (semiMonthlyApproval.getDay23() > 24)
					|| (semiMonthlyApproval.getDay24() < 0) || (semiMonthlyApproval.getDay24() > 24)
					|| (semiMonthlyApproval.getDay25() < 0) || (semiMonthlyApproval.getDay25() > 24)
					|| (semiMonthlyApproval.getDay26() < 0) || (semiMonthlyApproval.getDay26() > 24)
					|| (semiMonthlyApproval.getDay27() < 0) || (semiMonthlyApproval.getDay27() > 24)
					|| (semiMonthlyApproval.getDay28() < 0) || (semiMonthlyApproval.getDay28() > 24)
					|| (semiMonthlyApproval.getDay29() < 0) || (semiMonthlyApproval.getDay29() > 24)
					|| (semiMonthlyApproval.getDay30() < 0) || (semiMonthlyApproval.getDay30() > 24)
					|| (semiMonthlyApproval.getDay31() < 0) || (semiMonthlyApproval.getDay31() > 24))
				return new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE,
						"Hour should not be more than 24 and less than 0.");
		}

		semiMonthlyApproval.setMonth(month);
		semiMonthlyApproval.setYear(year);
		semiMonthlyApproval.setProject(projectModel);
		semiMonthlyApproval.setUser(userModel);

		if (!isFirstHalf) {

			semiMonthlyApproval.setSecondHalfFinalStatus(Constants.UserStatus.TASKTRACK_SUBMIT);
			semiMonthlyApproval.setSubmissionPeriod(Constants.UserStatus.TASKTRACK_SECONDHALF_PERIOD_STATUS);

		} else if (isFirstHalf) {
			semiMonthlyApproval.setFirstHalfFinalStatus(Constants.UserStatus.TASKTRACK_SUBMIT);
			semiMonthlyApproval.setSubmissionPeriod(Constants.UserStatus.TASKTRACK_FIRSTHALF_PERIOD_STATUS);
		}

		semiMonthlyRepository.save(semiMonthlyApproval);
		response = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, "Submitted successfully");

		return response;
	}
	
	private List<SemiMonthlyTaskTrackWithTaskResponse> addMissingDate(List<Date> reqDateRange,
			List<SemiMonthlyTaskTrackWithTaskResponse> taskTrackResponseList) {

		reqDateRange.forEach(date -> {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String requestDate = sdf.format(date);
			boolean isDatePresent = false;
			for (SemiMonthlyTaskTrackWithTaskResponse taskTrackResp : taskTrackResponseList) {
				if (taskTrackResp.getDate().equals(requestDate)) {
					isDatePresent = true;
					 break;
				}
				
			}
			if(!isDatePresent) {
				SemiMonthlyTaskTrackWithTaskResponse taskTrackWithTaskResponse = new SemiMonthlyTaskTrackWithTaskResponse();
				taskTrackWithTaskResponse.setDate(sdf.format(date));
				taskTrackWithTaskResponse.setEnabled(true);
				taskTrackWithTaskResponse.setFinalHour(0.0);
				taskTrackWithTaskResponse.setTaskList(new ArrayList<>());	
				taskTrackResponseList.add(taskTrackWithTaskResponse);
			}

		});
		Collections.sort(taskTrackResponseList, (s1, s2) -> s1.getDate().
	            compareTo(s2.getDate()));

		return taskTrackResponseList;
	}
    	
}
