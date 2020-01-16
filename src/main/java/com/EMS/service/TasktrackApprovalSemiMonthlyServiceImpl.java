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

import com.EMS.dto.SemiMonthlyTaskTrackRequestDTO;
import com.EMS.dto.WeeklyTaskTrackWithTaskRequestDTO;
import com.EMS.model.AllocationModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.StatusResponse;
import com.EMS.model.TaskTrackWeeklyApproval;
import com.EMS.model.Tasktrack;
import com.EMS.model.TasktrackApprovalSemiMonthly;
import com.EMS.model.UserModel;
import com.EMS.repository.AllocationRepository;
import com.EMS.repository.TaskTrackApprovalSemiMonthlyRepository;
import com.EMS.repository.TasktrackRepository;
import com.EMS.utility.Constants;
import com.EMS.utility.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class TasktrackApprovalSemiMonthlyServiceImpl implements TasktrackApprovalSemiMonthlyService {

	@Autowired
	private TaskTrackApprovalSemiMonthlyRepository semiMonthlyRepository;

	@Autowired
	UserService userservice;

	@Autowired
	private TasktrackRepository tasktrackRepository;

	@Autowired
	ProjectService projectservice;
	
	@Autowired
	private AllocationRepository allocationRepository;
	

	@SuppressWarnings({ "unchecked" })
	@Override
	public StatusResponse getSemiMonthlyTasktrack(SemiMonthlyTaskTrackRequestDTO requestData) throws Exception {
		StatusResponse result = new StatusResponse();
		JSONObject response = new JSONObject();
		Long userId = null;
		Long projectId = null;
		Date startDate = null;
		Date endDate = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (requestData.getuId() !=null) {
			userId =requestData.getuId();
		}
		if (requestData.getProjectId() !=null) {
			projectId = requestData.getProjectId();
		}
		if (requestData.getStartDate() !=null && !requestData.getStartDate().isEmpty()) {
			startDate = sdf.parse(requestData.getStartDate());
		}

		if (requestData.getEndDate() != null && !requestData.getEndDate().isEmpty()) {
			endDate = sdf.parse(requestData.getEndDate());
		}

		String[] taskStatusArray = { Constants.TaskTrackWeeklyApproval.TASKTRACK_WEEKLY_APPROVER_STATUS_APPROVED };
		List<String> taskStatusList = Arrays.asList(taskStatusArray);
		TasktrackApprovalSemiMonthly approvalSemiMonthly = (TasktrackApprovalSemiMonthly) semiMonthlyRepository.getSemiMonthlyTasktrack(startDate, userId, projectId);

		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(userId,
				projectId);
		List<Date> dateRanges = DateUtil.getDatesBetweenTwo(startDate, endDate);
		dateRanges.add(endDate);
		if (approvalSemiMonthly != null) {

			String approverOneFirstHalfStatus = approvalSemiMonthly.getApproverOneFirstHalfStatus();
			String approverTwoFirstHalfStatus = approvalSemiMonthly.getApproverTwoFirstHalfStatus();
			String financeFirstHalfStatus = approvalSemiMonthly.getFinanceFirstHalfStatus();

			String approverOneSecondHalfStatus = approvalSemiMonthly.getApproverOneSecondHalfStatus();
			String approverTwoSecondHalfStatus = approvalSemiMonthly.getApproverTwoSecondHalfStatus();
			String financeSecondHalfStatus = approvalSemiMonthly.getFinanceSecondHalfStatus();

			
			Calendar c = Calendar.getInstance();
			c.setTime(startDate);

			int date = c.get(Calendar.DATE);
			if (date == 1) {
				JSONArray array = new JSONArray();
				for (AllocationModel al : userProjAllocations) {
					List<Date> allocatedDates = DateUtil.getDatesBetweenTwo(al.getStartDate(), al.getEndDate());	
					
					if (allocatedDates.contains(dateRanges.get(0))) {
						array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay1(), dateRanges.get(0));
					}
					if (allocatedDates.contains(dateRanges.get(1))) {
						array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay2(), dateRanges.get(1));
					}
					if (allocatedDates.contains(dateRanges.get(2))) {
						array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay3(), dateRanges.get(2));
					}
					if (allocatedDates.contains(dateRanges.get(3))) {
						array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay4(), dateRanges.get(3));
					}
					if (allocatedDates.contains(dateRanges.get(4))) {
						array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay5(), dateRanges.get(4));
					}
					if (allocatedDates.contains(dateRanges.get(5))) {
						array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay6(), dateRanges.get(5));
					}
					if (allocatedDates.contains(dateRanges.get(6))) {
						array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay7(), dateRanges.get(6));
					}
					if (allocatedDates.contains(dateRanges.get(7))) {
						array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay8(), dateRanges.get(7));
					}
					if (allocatedDates.contains(dateRanges.get(8))) {
						array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay9(), dateRanges.get(8));
					}
					if (allocatedDates.contains(dateRanges.get(9))) {
						array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay10(), dateRanges.get(9));
					}
					if (allocatedDates.contains(dateRanges.get(10))) {
						array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay11(), dateRanges.get(10));
					}
					if (allocatedDates.contains(dateRanges.get(11))) {
						array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay12(), dateRanges.get(11));
					}
					if (allocatedDates.contains(dateRanges.get(12))) {
						array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay13(), dateRanges.get(12));
					}
					if (allocatedDates.contains(dateRanges.get(13))) {
						array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay14(), dateRanges.get(13));
					}
					if (allocatedDates.contains(dateRanges.get(14))) {
						array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay15(), dateRanges.get(14));
					}
				
				response.put("taskList", array);
				}
				if (taskStatusList.contains(approverOneFirstHalfStatus)
						|| taskStatusList.contains(approverTwoFirstHalfStatus)
						|| taskStatusList.contains(financeFirstHalfStatus)) {
					response.put("enabled", false);
				} else {
					response.put("enabled", true);
				}
				
				String firstHalfApprover = null;
				Date firstHalfSubmittedDate = null;
				if (approvalSemiMonthly.getFirstHalfApproverOneId() != null ) {
					 firstHalfApprover = approvalSemiMonthly.getFirstHalfApproverOneId().getFirstName() + " "
								+ approvalSemiMonthly.getFirstHalfApproverOneId().getLastName();
					 
						 firstHalfSubmittedDate = approvalSemiMonthly.getUserFirstHalfSubmittedDate();
				}

				JSONObject approval = new JSONObject();
				approval.put("approver", firstHalfApprover);
				approval.put("date",(firstHalfSubmittedDate != null)? sdf.format(firstHalfSubmittedDate) : "");
				approval.put("status", approverOneFirstHalfStatus);

				response.put("approval", approval);
			}
			if (date == 16) {
				
				for (AllocationModel al : userProjAllocations) {
				
					List<Date> allocatedDates = DateUtil.getDatesBetweenTwo(al.getStartDate(), al.getEndDate());	
					
				JSONArray array = new JSONArray();
				if (allocatedDates.contains(dateRanges.get(0))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay16(), dateRanges.get(0));
				}
				if (allocatedDates.contains(dateRanges.get(1))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay17(), dateRanges.get(1));
				}
				if (allocatedDates.contains(dateRanges.get(0))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay18(), dateRanges.get(2));
				}
				if (allocatedDates.contains(dateRanges.get(0))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay19(), dateRanges.get(3));
				}
				if (allocatedDates.contains(dateRanges.get(0))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay20(), dateRanges.get(4));
				}
				if (allocatedDates.contains(dateRanges.get(0))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay21(), dateRanges.get(5));
				}
				if (allocatedDates.contains(dateRanges.get(0))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay22(), dateRanges.get(6));
				}
				if (allocatedDates.contains(dateRanges.get(0))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay23(), dateRanges.get(7));
				}
				if (allocatedDates.contains(dateRanges.get(0))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay24(), dateRanges.get(8));
				}
				if (allocatedDates.contains(dateRanges.get(0))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay25(), dateRanges.get(9));
				}
				if (allocatedDates.contains(dateRanges.get(0))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay26(), dateRanges.get(10));
				}
				if (allocatedDates.contains(dateRanges.get(0))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay27(), dateRanges.get(11));
				}
				if (allocatedDates.contains(dateRanges.get(0))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay28(), dateRanges.get(12));
				}
				if (allocatedDates.contains(dateRanges.get(0))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay29(), dateRanges.get(13));
				}
				if (allocatedDates.contains(dateRanges.get(0))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay30(), dateRanges.get(14));
				}
				if (allocatedDates.contains(dateRanges.get(0))) {
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay31(), dateRanges.get(15));
				}
				
				response.put("taskList", array);
	
				if (taskStatusList.contains(approverOneSecondHalfStatus)
						|| taskStatusList.contains(approverTwoSecondHalfStatus)
						|| taskStatusList.contains(financeSecondHalfStatus)) {
					
					response.put("enabled", false);
				} else {
					response.put("enabled", true);
				}
				
				String secondHalfApprover = null;
				Date secondHalfSubmittedDate  = null;
				if (null != approvalSemiMonthly.getSecondHalfApproverOneId()) {
					 secondHalfApprover = approvalSemiMonthly.getSecondHalfApproverOneId().getFirstName() + " "
							+ approvalSemiMonthly.getFirstHalfApproverOneId().getLastName();

					 secondHalfSubmittedDate = approvalSemiMonthly.getUserSecondHalfSubmittedDate();
				}
				JSONObject approval = new JSONObject();
				approval.put("approver", secondHalfApprover);
				approval.put("date", (secondHalfSubmittedDate !=null)?sdf.format(secondHalfSubmittedDate) :"");
				approval.put("status", approverOneSecondHalfStatus);

				response.put("approval", approval);
				
			}
			}
			result = new StatusResponse(Constants.SUCCESS,Constants.SUCCESS_CODE,response);

		}else {
			JSONArray array = new JSONArray();
			for (AllocationModel al : userProjAllocations) {
				List<Date> allocatedDates = DateUtil.getDatesBetweenTwo(al.getStartDate(), al.getEndDate());
				for (Date date : dateRanges) {
					if (allocatedDates.contains(date)) {
						array = addHoursandDaytoArray(array, null, date);
					}
				}
			}
			response.put("taskList", array);
			result = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, response);
		}

		return result;

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
	public StatusResponse submitForSemiMonthlyApproval(JSONObject requestData) throws ParseException {
		TasktrackApprovalSemiMonthly semiMonthlyApproval = new TasktrackApprovalSemiMonthly();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int requeststatus = 0;
		StatusResponse response = new StatusResponse();

		Map<Date, Integer> timetrack = (Map<Date, Integer>) requestData.get("timetrack");

		Map<Object, Object> result = timetrack.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors
				.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		Iterator iter = result.entrySet().iterator();
		Date date1 = null;
		Calendar cal = Calendar.getInstance();
		
		semiMonthlyApproval.setYear(Integer.parseInt(requestData.get("year").toString()));
		semiMonthlyApproval.setMonth(Integer.parseInt(requestData.get("month").toString()));

		Long userId = Long.parseLong(requestData.get("userId").toString());
		UserModel userInfo = userservice.getUserdetailsbyId(userId);

		if (!userInfo.equals(null))
			semiMonthlyApproval.setUser(userInfo);
		else
			requeststatus = 1;
		
		semiMonthlyApproval = semiMonthlyRepository.checkduplicationForsemiMonthlyTaskTrack(
				semiMonthlyApproval.getUser().getUserId(), semiMonthlyApproval.getMonth(),
				semiMonthlyApproval.getYear());
		
		
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Double hours = 0.0;
			if (entry.getValue() != null)
				hours = Double.parseDouble(entry.getValue().toString());

			String startdate = entry.getKey().toString();
			date1 = sdf.parse(startdate);

			cal.setTime(date1);

			if (cal.get(Calendar.DAY_OF_MONTH) == 1)
				semiMonthlyApproval.setDay1(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 2)
				semiMonthlyApproval.setDay2(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 3)
				semiMonthlyApproval.setDay3(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 4)
				semiMonthlyApproval.setDay4(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 5)
				semiMonthlyApproval.setDay5(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 6)
				semiMonthlyApproval.setDay6(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 7)
				semiMonthlyApproval.setDay7(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 8)
				semiMonthlyApproval.setDay8(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 9)
				semiMonthlyApproval.setDay9(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 10)
				semiMonthlyApproval.setDay10(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 11)
				semiMonthlyApproval.setDay11(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 12)
				semiMonthlyApproval.setDay12(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 13)
				semiMonthlyApproval.setDay13(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 14)
				semiMonthlyApproval.setDay14(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 15)
				semiMonthlyApproval.setDay15(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 16)
				semiMonthlyApproval.setDay16(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 17)
				semiMonthlyApproval.setDay17(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 18)
				semiMonthlyApproval.setDay18(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 19)
				semiMonthlyApproval.setDay19(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 20)
				semiMonthlyApproval.setDay20(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 21)
				semiMonthlyApproval.setDay21(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 22)
				semiMonthlyApproval.setDay22(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 23)
				semiMonthlyApproval.setDay23(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 24)
				semiMonthlyApproval.setDay24(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 25)
				semiMonthlyApproval.setDay25(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 26)
				semiMonthlyApproval.setDay26(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 27)
				semiMonthlyApproval.setDay27(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 28)
				semiMonthlyApproval.setDay28(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 29)
				semiMonthlyApproval.setDay29(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 30)
				semiMonthlyApproval.setDay30(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 31)
				semiMonthlyApproval.setDay31(hours);

		}

	

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
				|| (semiMonthlyApproval.getDay20() < 0) || (semiMonthlyApproval.getDay19() > 24)
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
				|| (semiMonthlyApproval.getDay31() < 0) || (semiMonthlyApproval.getDay31() > 24)
				|| semiMonthlyApproval.getYear() <= 0 || semiMonthlyApproval.getMonth() < 0
				|| semiMonthlyApproval.getMonth() >= 12)
			requeststatus = 1;


		Long projectId = Long.parseLong(requestData.get("projectId").toString());
		ProjectModel projectInfo = projectservice.findById(projectId);

		if (!projectInfo.equals(null))
			semiMonthlyApproval.setProject(projectInfo);
		else
			requeststatus = 1;

		if (cal.get(Calendar.DAY_OF_MONTH) > 15) {
			semiMonthlyApproval.setUserSecondHalfStatus(Constants.TASKTRACK_USER_STATUS_SUBMIT);
			semiMonthlyApproval.setUserFirstHalfSubmittedDate(new Date());
		} else {
			semiMonthlyApproval.setUserFirstHalfStatus(Constants.TASKTRACK_USER_STATUS_SUBMIT);
			semiMonthlyApproval.setUserSecondHalfSubmittedDate(new Date());

		}

//		if (requeststatus == 0) {
				semiMonthlyRepository.save(semiMonthlyApproval);
				response = new StatusResponse("success", 200, "Semi monthly data submission completed");
			
//		} else
//			response = new StatusResponse("success", 200, "Semi monthly data submission failed due to invalid data");
		
		return response;
	}

	@Override
	public StatusResponse saveSemiMonthlyTaskTrackApproval(JSONObject requestData) throws ParseException {

		StatusResponse response = new StatusResponse();
		TasktrackApprovalSemiMonthly semiMonthlyApproval = new TasktrackApprovalSemiMonthly();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int requeststatus = 0;

		Map<Date, Integer> timetrack = (Map<Date, Integer>) requestData.get("timetrack");

		Map<Object, Object> result = timetrack.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors
				.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		Iterator iter = result.entrySet().iterator();
		Date date1 = null;
		Calendar cal = Calendar.getInstance();

		semiMonthlyApproval.setYear(Integer.parseInt(requestData.get("year").toString()));
		semiMonthlyApproval.setMonth(Integer.parseInt(requestData.get("month").toString()));

		if ((!semiMonthlyApproval.getYear().equals(null)) && (!semiMonthlyApproval.getMonth().equals(null))) {

			if (semiMonthlyApproval.getYear() <= 0 || semiMonthlyApproval.getMonth() < 0
					|| semiMonthlyApproval.getMonth() >= 12)
				requeststatus = 1;
		} else
			requeststatus = 1;

		Long userId = Long.parseLong(requestData.get("userId").toString());
		UserModel userInfo = userservice.getUserdetailsbyId(userId);

		if (!userInfo.equals(null))
			semiMonthlyApproval.setUser(userInfo);
		else
			requeststatus = 1;

		semiMonthlyApproval = semiMonthlyRepository.checkduplicationForsemiMonthlyTaskTrack(
				semiMonthlyApproval.getUser().getUserId(), semiMonthlyApproval.getMonth(),
				semiMonthlyApproval.getYear());

		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Double hours = 0.0;
			if (entry.getValue() != null)
				hours = Double.parseDouble(entry.getValue().toString());

			String startdate = entry.getKey().toString();
			date1 = sdf.parse(startdate);

			cal.setTime(date1);

			if (cal.get(Calendar.DAY_OF_MONTH) == 1)
				semiMonthlyApproval.setDay1(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 2)
				semiMonthlyApproval.setDay2(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 3)
				semiMonthlyApproval.setDay3(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 4)
				semiMonthlyApproval.setDay4(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 5)
				semiMonthlyApproval.setDay5(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 6)
				semiMonthlyApproval.setDay6(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 7)
				semiMonthlyApproval.setDay7(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 8)
				semiMonthlyApproval.setDay8(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 9)
				semiMonthlyApproval.setDay9(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 10)
				semiMonthlyApproval.setDay10(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 11)
				semiMonthlyApproval.setDay11(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 12)
				semiMonthlyApproval.setDay12(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 13)
				semiMonthlyApproval.setDay13(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 14)
				semiMonthlyApproval.setDay14(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 15)
				semiMonthlyApproval.setDay15(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 16)
				semiMonthlyApproval.setDay16(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 17)
				semiMonthlyApproval.setDay17(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 18)
				semiMonthlyApproval.setDay18(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 19)
				semiMonthlyApproval.setDay19(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 20)
				semiMonthlyApproval.setDay20(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 21)
				semiMonthlyApproval.setDay21(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 22)
				semiMonthlyApproval.setDay22(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 23)
				semiMonthlyApproval.setDay23(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 24)
				semiMonthlyApproval.setDay24(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 25)
				semiMonthlyApproval.setDay25(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 26)
				semiMonthlyApproval.setDay26(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 27)
				semiMonthlyApproval.setDay27(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 28)
				semiMonthlyApproval.setDay28(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 29)
				semiMonthlyApproval.setDay29(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 30)
				semiMonthlyApproval.setDay30(hours);
			if (cal.get(Calendar.DAY_OF_MONTH) == 31)
				semiMonthlyApproval.setDay31(hours);

		}

		Long projectId = Long.parseLong(requestData.get("projectId").toString());
		ProjectModel projectInfo = projectservice.findById(projectId);

		if (!projectInfo.equals(null))
			semiMonthlyApproval.setProject(projectInfo);
		else
			requeststatus = 1;

		if (cal.get(Calendar.DAY_OF_MONTH) > 15) {
			semiMonthlyApproval.setUserSecondHalfStatus(Constants.TASKTRACK_USER_STATUS_SAVED);
			semiMonthlyApproval.setUserFirstHalfSubmittedDate(new Date());
		} else {
			semiMonthlyApproval.setUserFirstHalfStatus(Constants.TASKTRACK_USER_STATUS_SAVED);
			semiMonthlyApproval.setUserSecondHalfSubmittedDate(new Date());

		}

//		if (requeststatus == 0) {
			semiMonthlyRepository.save(semiMonthlyApproval);
			response = new StatusResponse("Success", 200, "Insertion of semimonthly tasktrack completed");
//		} else
//			response = new StatusResponse("Success", 200,
//					"Insertion of semimonthly tasktrack failed due to invalid entry");
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

	
		if (calendar.get(Calendar.DAY_OF_MONTH) < 5) {

			semiMonthlytasksubmission.setUserFirstHalfSubmittedDate(new Date());
			semiMonthlytasksubmission.setUserFirstHalfStatus(Constants.TASKTRACK_USER_STATUS_SUBMIT);
		} else {

			semiMonthlytasksubmission.setUserSecondHalfSubmittedDate(new Date());
			semiMonthlytasksubmission.setUserSecondHalfStatus(Constants.TASKTRACK_USER_STATUS_SUBMIT);
		}

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

	@SuppressWarnings("unchecked")
	@Override
	public StatusResponse getSemiMonthlyTasktrackWithTask(SemiMonthlyTaskTrackRequestDTO requestData)
			throws Exception {
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
		
		List<Tasktrack> tasktrackList = tasktrackRepository.findByUserUserIdAndProjectProjectIdAndDateBetweenOrderByDateAsc(
				userId, projectId, startDate, endDate);

		Calendar c = Calendar.getInstance();
		c.setTime(startDate);

		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		
		TasktrackApprovalSemiMonthly tasktrackStatus = semiMonthlyRepository
				.findByUserUserIdAndProjectProjectIdInAndMonthEqualsAndYearEquals(userId,
						projectId, month, year);
		
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
	
		String[] taskStatusArray = { Constants.TaskTrackSemiMonthlyApproval.TASKTRACK_SEMI_MONTHLY_APPROVER_STATUS_APPROVED };
		List<String> taskStatusList = Arrays.asList(taskStatusArray);
		
		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(userId, projectId);
		
		if (tasktrackStatus != null) {
		
			String approverOneFirstHalfStatus = tasktrackStatus.getApproverOneFirstHalfStatus();
			String approverOneSecondHalfStatus = tasktrackStatus.getApproverOneSecondHalfStatus();
			String approverTwoFirstHalfStatus = tasktrackStatus.getApproverTwoFirstHalfStatus();
			String approverTwoSecondHalfStatus = tasktrackStatus.getApproverTwoSecondHalfStatus();
			String financeFirstHalfStatus = tasktrackStatus.getFinanceFirstHalfStatus();
			String financeSecondHalfStatus = tasktrackStatus.getFinanceSecondHalfStatus();
		
			List<Date> dateRanges = DateUtil.getDatesBetweenTwo(startDate, endDate);
			dateRanges.add(endDate);
			
			int date = c.get(Calendar.DATE);
			if (date == 1) {
				if (taskStatusList.contains(approverOneFirstHalfStatus) || taskStatusList.contains(approverTwoFirstHalfStatus)
						|| taskStatusList.contains(financeFirstHalfStatus)) {
					for (AllocationModel al : userProjAllocations) {
						List<Date> allocatedDates = DateUtil.getDatesBetweenTwo(al.getStartDate(), al.getEndDate());
						if ( allocatedDates.contains(startDate) || allocatedDates.contains(endDate)) {
							jsonObject.put("enabled", false);
						}
					}
					
				}

				else {
					jsonObject.put("enabled", true);
				}
			
			}
			else if (date == 16) {
				if (taskStatusList.contains(approverOneSecondHalfStatus) || taskStatusList.contains(approverTwoSecondHalfStatus)
						|| taskStatusList.contains(financeSecondHalfStatus)) {
					for (AllocationModel al : userProjAllocations) {
						List<Date> allocatedDates = DateUtil.getDatesBetweenTwo(al.getStartDate(), al.getEndDate());
						if ( allocatedDates.contains(startDate) || allocatedDates.contains(endDate)) {
							jsonObject.put("enabled", false);
						}
					}
					
				}

				else {
					jsonObject.put("enabled", true);
				}
			}
		
		}
		
		response.setData(jsonObject);
		response.setStatus(Constants.SUCCESS);
		response.setStatusCode(Constants.SUCCESS_CODE);
		
		return response;
	}

}
