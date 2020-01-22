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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.dto.SemiMonthlyTaskTrackRequestDTO;
import com.EMS.model.AllocationModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.StatusResponse;
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
				if (taskStatusList.contains(approverOneFirstHalfStatus)
						|| taskStatusList.contains(approverTwoFirstHalfStatus)
						|| taskStatusList.contains(financeFirstHalfStatus)) {
					response.put("enabled", false);
				} else {
					response.put("enabled", true);
				}

				String firstHalfApprover1 = null;
				Date firstHalfSubmittedDate = null;
				if (approvalSemiMonthly.getFirstHalfApproverOneId() != null) {
					firstHalfApprover1 =  approvalSemiMonthly.getFirstHalfApproverOneId().getFirstName() + " "
									+ approvalSemiMonthly.getFirstHalfApproverOneId().getLastName();

					firstHalfSubmittedDate = approvalSemiMonthly.getUserFirstHalfSubmittedDate();
				}
				
			
				
				if (null == firstHalfApprover1 || firstHalfApprover1.equals("")) {
					firstHalfApprover1 = null !=  projectModel.getProjectOwner()? projectModel.getProjectOwner().getFirstName() + " "
							+ projectModel.getProjectOwner().getLastName() :"";

				}
				
				String firstHalfApprover2 = null;
				Date firstHalfSubmittedDate2 = null;
				if (approvalSemiMonthly.getFirstHalfApproverTwoId() != null) {
					firstHalfApprover2 =  approvalSemiMonthly.getFirstHalfApproverTwoId().getFirstName() + " "
									+ approvalSemiMonthly.getFirstHalfApproverTwoId().getLastName();

					firstHalfSubmittedDate2 = approvalSemiMonthly.getUserFirstHalfSubmittedDate();
				}

				if (null == firstHalfApprover2 || firstHalfApprover2.equals("")) {
					firstHalfApprover2 = null !=  projectModel.getOnsite_lead()? projectModel.getOnsite_lead().getFirstName() + " "
							+ projectModel.getOnsite_lead().getLastName() :"";

				}
				
				JSONObject approver1 = new JSONObject();
				approver1.put("approver", firstHalfApprover1);
				approver1.put("date", (firstHalfSubmittedDate != null) ? sdf.format(firstHalfSubmittedDate) : "");
				approver1.put("status", approverOneFirstHalfStatus);
				response.put("approver1", approver1);
				
				JSONObject approver2 = new JSONObject();
				approver2.put("approver", firstHalfApprover2);
				approver2.put("date", (firstHalfSubmittedDate2 != null) ? sdf.format(firstHalfSubmittedDate2) : "");
				approver2.put("status", approverTwoFirstHalfStatus);
				response.put("approver2", approver2);
				
				JSONObject user = new JSONObject();
				user.put("status", approvalSemiMonthly.getUserFirstHalfStatus());
				user.put("date", approvalSemiMonthly.getUserFirstHalfSubmittedDate()!=null? approvalSemiMonthly.getUserFirstHalfSubmittedDate():"");
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

					if (taskStatusList.contains(approverOneSecondHalfStatus)
							|| taskStatusList.contains(approverTwoSecondHalfStatus)
							|| taskStatusList.contains(financeSecondHalfStatus)) {

						response.put("enabled", false);
					} else {
						response.put("enabled", true);
					}

	
					
					String secondHalfApprover1 = null;
					Date secondHalfSubmittedDate = null;
					if (approvalSemiMonthly.getSecondHalfApproverOneId() != null) {
						secondHalfApprover1 =  approvalSemiMonthly.getSecondHalfApproverOneId().getFirstName() + " "
										+ approvalSemiMonthly.getSecondHalfApproverOneId().getLastName();

						secondHalfSubmittedDate = approvalSemiMonthly.getUserSecondHalfSubmittedDate();
					}
					
					
					if (null == secondHalfApprover1 || secondHalfApprover1.equals("")) {
						secondHalfApprover1 = null !=  projectModel.getProjectOwner()? projectModel.getProjectOwner().getFirstName() + " "
								+ projectModel.getProjectOwner().getLastName() :"";

					}
					
					String secondHalfApprover2 = null;
					Date secondHalfSubmittedDate2 = null;
					if (approvalSemiMonthly.getSecondHalfApproverTwoId() != null) {
						secondHalfApprover2 =  approvalSemiMonthly.getSecondHalfApproverTwoId().getFirstName() + " "
										+ approvalSemiMonthly.getSecondHalfApproverTwoId().getLastName();

						secondHalfSubmittedDate2 = approvalSemiMonthly.getUserSecondHalfSubmittedDate();
					}

					if (null == secondHalfApprover2 || secondHalfApprover2.equals("")) {
						secondHalfApprover2 = null !=  projectModel.getOnsite_lead()? projectModel.getOnsite_lead().getFirstName() + " "
								+ projectModel.getOnsite_lead().getLastName() :"";

					}
					
					JSONObject approver1 = new JSONObject();
					approver1.put("approver", secondHalfApprover1);
					approver1.put("date", (secondHalfSubmittedDate != null) ? sdf.format(secondHalfSubmittedDate) : "");
					approver1.put("status", approverOneSecondHalfStatus);
					response.put("approver1", approver1);
					
					JSONObject approver2 = new JSONObject();
					approver2.put("approver", secondHalfApprover2);
					approver2.put("date", (secondHalfSubmittedDate2 != null) ? sdf.format(secondHalfSubmittedDate2) : "");
					approver2.put("status", approverTwoSecondHalfStatus);
					response.put("approver2", approver2);
					
					JSONObject user = new JSONObject();
					user.put("status", approvalSemiMonthly.getUserSecondHalfStatus());
					user.put("date", approvalSemiMonthly.getUserSecondHalfSubmittedDate()!=null?approvalSemiMonthly.getUserSecondHalfSubmittedDate():"");
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
			approver1Obj.put("date", "");
			approver1Obj.put("status", Constants.Approver1.TASKTRACK_OPEN);
			response.put("approver1",approver1Obj);

			String approver2 = null != projectModel.getOnsite_lead()
					? projectModel.getOnsite_lead().getFirstName() + " " + projectModel.getOnsite_lead().getLastName()
					: "";

			JSONObject approver2Obj = new JSONObject();
			approver2Obj.put("approver", approver2);
			approver2Obj.put("date", "");
			approver2Obj.put("status", Constants.Approver2.TASKTRACK_OPEN);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private StatusResponse saveOrSubmitSemiMonthlyTaskTrackApproval(JSONObject requestData,Boolean isSave) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int requeststatus = 0;
		@SuppressWarnings("rawtypes")
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
				&& (semiMonthlyApproval.getFirstHalfFinalStatus().equals(Constants.Approver1.TASKTRACK_FORWARDED_TO_LEVEL2)
				|| semiMonthlyApproval.getFirstHalfFinalStatus().endsWith(Constants.Finance.TASKTRACK_APPROVED)))
				
				|| (!isFirstHalf && semiMonthlyApproval.getSecondHalfFinalStatus() != null
				&& (semiMonthlyApproval.getSecondHalfFinalStatus().equals(Constants.Approver1.TASKTRACK_FORWARDED_TO_LEVEL2)
				|| semiMonthlyApproval.getSecondHalfFinalStatus().endsWith(Constants.Finance.TASKTRACK_APPROVED)))) {
			return new StatusResponse("success", 200, "Timetrack has already been approved.");
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

		if (!isFirstHalf) {
			if(isSave) {
				semiMonthlyApproval.setUserSecondHalfStatus(Constants.UserStatus.TASKTRACK_SAVED);
				semiMonthlyApproval.setSecondHalfFinalStatus(Constants.UserStatus.TASKTRACK_SAVED);
			}
			else {
				semiMonthlyApproval.setUserSecondHalfStatus(Constants.UserStatus.TASKTRACK_SUBMIT);
				semiMonthlyApproval.setSecondHalfFinalStatus(Constants.UserStatus.TASKTRACK_SUBMIT);
			}
			semiMonthlyApproval.setUserSecondHalfSubmittedDate(new Date());
		
		} else if(isFirstHalf) {
			if(isSave) {
				semiMonthlyApproval.setUserFirstHalfStatus(Constants.UserStatus.TASKTRACK_SAVED);
				semiMonthlyApproval.setFirstHalfFinalStatus(Constants.UserStatus.TASKTRACK_SAVED);
			}
			else {
				semiMonthlyApproval.setUserFirstHalfStatus(Constants.UserStatus.TASKTRACK_SUBMIT);
				semiMonthlyApproval.setFirstHalfFinalStatus(Constants.UserStatus.TASKTRACK_SUBMIT);
			}
			semiMonthlyApproval.setUserFirstHalfSubmittedDate(new Date());
		}
		if (requeststatus == 0) {
			semiMonthlyRepository.save(semiMonthlyApproval);
			response = new StatusResponse("success", 200, 
					isSave ? "Semi monthly data saved successfully." : "Semi monthly data submitted successfully.");
		} else
			response = new StatusResponse("success", 200, 
					isSave ? "Semi monthly data insertion failed due to invalid data" : "Semi monthly data submission failed due to invalid data");

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
			semiMonthlytasksubmission.setUserFirstHalfStatus(Constants.UserStatus.TASKTRACK_SUBMIT);
		} else {

			semiMonthlytasksubmission.setUserSecondHalfSubmittedDate(new Date());
			semiMonthlytasksubmission.setUserSecondHalfStatus(Constants.UserStatus.TASKTRACK_SUBMIT);
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
	public StatusResponse getSemiMonthlyTasktrackWithTask(SemiMonthlyTaskTrackRequestDTO requestData) throws Exception {
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

		Calendar c = Calendar.getInstance();
		c.setTime(startDate);

		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);

		TasktrackApprovalSemiMonthly tasktrackStatus = semiMonthlyRepository
				.findByUserUserIdAndProjectProjectIdInAndMonthEqualsAndYearEquals(userId, projectId, month, year);

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

		String[] taskStatusArray = {
				Constants.TaskTrackSemiMonthlyApproval.TASKTRACK_SEMI_MONTHLY_APPROVER_STATUS_APPROVED };
		List<String> taskStatusList = Arrays.asList(taskStatusArray);

		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(userId,
				projectId);

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
				if (taskStatusList.contains(approverOneFirstHalfStatus)
						|| taskStatusList.contains(approverTwoFirstHalfStatus)
						|| taskStatusList.contains(financeFirstHalfStatus)) {
					for (AllocationModel al : userProjAllocations) {
						List<Date> allocatedDates = DateUtil.getDatesBetweenTwo(al.getStartDate(), al.getEndDate());
						if (allocatedDates.contains(startDate) || allocatedDates.contains(endDate)) {
							jsonObject.put("enabled", false);
						}
					}

				}

				else {
					jsonObject.put("enabled", true);
				}

			} else if (date == 16) {
				if (taskStatusList.contains(approverOneSecondHalfStatus)
						|| taskStatusList.contains(approverTwoSecondHalfStatus)
						|| taskStatusList.contains(financeSecondHalfStatus)) {
					for (AllocationModel al : userProjAllocations) {
						List<Date> allocatedDates = DateUtil.getDatesBetweenTwo(al.getStartDate(), al.getEndDate());
						if (allocatedDates.contains(startDate) || allocatedDates.contains(endDate)) {
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
