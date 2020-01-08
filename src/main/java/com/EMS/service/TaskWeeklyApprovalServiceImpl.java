package com.EMS.service;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.TaskTrackWeeklyApproval;
import com.EMS.repository.TaskWeeklyApprovalRepository;
import com.EMS.utility.Constants;
import com.EMS.utility.DateUtil;

@Service
public class TaskWeeklyApprovalServiceImpl implements TaskWeeklyApprovalService {

	@Autowired
	private TaskWeeklyApprovalRepository taskWeeklyApprovalRepository;

	@Override
	public void submitWeeklyApproval(JSONObject requestData) {
		
			TaskTrackWeeklyApproval weeklyApproval = new TaskTrackWeeklyApproval();			
			taskWeeklyApprovalRepository.save(weeklyApproval);	
			
	}

	@Override
	public void saveWeeklyApproval(JSONObject requestData) {
		
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
		   			
			response.put(sdf.format(datesInRange.get(0)), weeklyTasktrack.getDay1());
			response.put(sdf.format(datesInRange.get(1)), weeklyTasktrack.getDay2());
			response.put(sdf.format(datesInRange.get(2)), weeklyTasktrack.getDay3());
			response.put(sdf.format(datesInRange.get(3)), weeklyTasktrack.getDay4());
			response.put(sdf.format(datesInRange.get(4)), weeklyTasktrack.getDay5());
			response.put(sdf.format(datesInRange.get(5)), weeklyTasktrack.getDay6());
			response.put(sdf.format(datesInRange.get(6)), weeklyTasktrack.getDay7());
		
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return response;
	}
}