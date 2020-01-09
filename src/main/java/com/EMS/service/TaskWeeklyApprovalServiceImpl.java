package com.EMS.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.ProjectModel;
import com.EMS.model.TaskTrackWeeklyApproval;
import com.EMS.model.UserModel;
import com.EMS.repository.TaskWeeklyApprovalRepository;
import com.EMS.utility.Constants;
import com.EMS.utility.DateUtil;

@Service
public class TaskWeeklyApprovalServiceImpl implements TaskWeeklyApprovalService {

	@Autowired
	private TaskWeeklyApprovalRepository taskWeeklyApprovalRepository;
	
	
	@Autowired
	private UserService userservice;

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
				weeklyApproval.setApprover1SubmittedDate(sdf.parse(requestData.get("approver1SubmittedDate").toString()));
			
			
			if (requestData.get("approver2SubmittedDate") != null)
				weeklyApproval.setApprover2SubmittedDate(sdf.parse(requestData.get("approver2SubmittedDate").toString()));
			
			if (requestData.get("financeSubmittedDate") != null)
				weeklyApproval.setFinanceSubmittedDate(sdf.parse(requestData.get("financeSubmittedDate").toString()));

			if (requestData.get("rejectionTime") != null)
				weeklyApproval.setRejectionTime(sdf.parse(requestData.get("rejectionTime").toString()));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (requeststatus == 0) {
			taskWeeklyApprovalRepository.save(weeklyApproval);
			return 0;
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

			if (requestData.get("timetrackStatus").toString().equals(null)|| requestData.get("timetrackStatus").toString().equals(" ")){
				requeststatus = 1;
			} else {

				weeklyApproval.setTimetrackStatus(requestData.get("timetrackStatus").toString());
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (requeststatus == 0) {
			taskWeeklyApprovalRepository.save(weeklyApproval);
			return 0;
		} else
			return 1;

	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getWeeklyTasktrack(JSONObject requestData) {
		JSONObject response = new JSONObject();
		try {
			
			Long userId = null;
			Long projectId = null;
			Date startDate = null;
			Date endDate = null;

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (requestData.get("uId") != null && requestData.get("uId").toString() != "") {
				userId = Long.parseLong(requestData.get("uId").toString());
			}
			if (requestData.get("projectId") !=null && requestData.get("projectId").toString() != "") {
				projectId = Long.parseLong(requestData.get("projectId").toString());
			}
			if (requestData.get("startDate") != null && requestData.get("startDate").toString() != null) {
				startDate = sdf.parse(requestData.get("startDate").toString());
			}
			if (requestData.get("endDate") != null && requestData.get("endDate").toString() != null) {
				endDate = sdf.parse(requestData.get("endDate").toString());
			}
			
			String[] taskStatusArray = {Constants.TaskTrackWeeklyApproval.TASKTRACK_WEEKLY_APPROVER_STATUS_APPROVED};
			List<String> taskStatusList = Arrays.asList(taskStatusArray);
			TaskTrackWeeklyApproval weeklyTasktrack = taskWeeklyApprovalRepository.getWeeklyTasktrack(startDate, endDate, userId, projectId);
			
			
			String approver1Status = weeklyTasktrack.getApprover1Status();
			String approver2Status = weeklyTasktrack.getApprover2Status();
			String financeStatus = weeklyTasktrack.getFinanceStatus();
		
			if (taskStatusList.contains(approver1Status) || taskStatusList.contains(approver2Status)
					|| taskStatusList.contains(financeStatus)) {
				response.put("enabled", false);
			}
			else {
				response.put("enabled", true);
			}
		
			List<Date> datesInRange = DateUtil.getDatesBetweenTwo(startDate, endDate);
		    datesInRange.add(endDate);
		    		    
		    JSONObject hour1 = new JSONObject();
		    hour1.put("hour", weeklyTasktrack.getDay1());		    		    
		    JSONObject day1 = new JSONObject();
		    day1.put(sdf.format(datesInRange.get(0)), hour1);
		    
		    JSONObject hour2 = new JSONObject();
		    hour2.put("hour", weeklyTasktrack.getDay2());		    		    
		    JSONObject day2 = new JSONObject();
		    day2.put(sdf.format(datesInRange.get(1)), hour2);
		    
		    JSONObject hour3 = new JSONObject();
		    hour3.put("hour", weeklyTasktrack.getDay3());		    		    
		    JSONObject day3 = new JSONObject();
		    day3.put(sdf.format(datesInRange.get(2)), hour3);
		    
		    JSONObject hour4 = new JSONObject();
		    hour4.put("hour", weeklyTasktrack.getDay4());		    		    
		    JSONObject day4 = new JSONObject();
		    day4.put(sdf.format(datesInRange.get(3)), hour4);
		    
		    JSONObject hour5 = new JSONObject();
		    hour5.put("hour", weeklyTasktrack.getDay5());		    		    
		    JSONObject day5 = new JSONObject();
		    day5.put(sdf.format(datesInRange.get(4)), hour5);
		    
		    JSONObject hour6 = new JSONObject();
		    hour5.put("hour", weeklyTasktrack.getDay6());		    		    
		    JSONObject day6 = new JSONObject();
		    day6.put(sdf.format(datesInRange.get(5)), hour6);
		    
		    JSONObject hour7 = new JSONObject();
		    hour7.put("hour", weeklyTasktrack.getDay7());		    		    
		    JSONObject day7 = new JSONObject();
		    day7.put(sdf.format(datesInRange.get(6)), hour7);
		    
		    JSONArray array = new JSONArray();
		    array.add(day1);
		    array.add(day2);
		    array.add(day3);
		    array.add(day4);
		    array.add(day5);
		    array.add(day6);
		    array.add(day7);	
		    response.put("taskList", array);
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public JSONObject getWeeklyTasktrackWithTask(JSONObject requestData) {
		// TODO Auto-generated method stub
		return null;
	}
}