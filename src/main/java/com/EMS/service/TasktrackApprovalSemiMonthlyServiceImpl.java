package com.EMS.service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.TasktrackApprovalSemiMonthly;
import com.EMS.repository.TaskTrackApprovalSemiMonthlyRepository;
import com.EMS.utility.Constants;
import com.EMS.utility.DateUtil;

@Service
public class TasktrackApprovalSemiMonthlyServiceImpl implements TasktrackApprovalSemiMonthlyService {

	@Autowired
	private TaskTrackApprovalSemiMonthlyRepository semiMonthlyRepository;

	@SuppressWarnings({ "unchecked" })
	@Override
	public JSONObject getSemiMonthlyTasktrack(JSONObject requestData) {

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

			TasktrackApprovalSemiMonthly approvalSemiMonthly = (TasktrackApprovalSemiMonthly) semiMonthlyRepository
					.getSemiMonthlyTasktrack(startDate, userId, projectId);

			String approverOneFirstHalfStatus = approvalSemiMonthly.getApproverOneFirstHalfStatus();
			String approverTwoFirstHalfStatus = approvalSemiMonthly.getApproverTwoFirstHalfStatus();
			String financeFirstHalfStatus = approvalSemiMonthly.getFinanceFirstHalfStatus();

			String approverOneSecondHalfStatus = approvalSemiMonthly.getApproverOneSecondHalfStatus();
			String approverTwoSecondHalfStatus = approvalSemiMonthly.getApproverTwoSecondHalfStatus();
			String financeSecondHalfStatus = approvalSemiMonthly.getFinanceSecondHalfStatus();

			List<Date> dateRanges = DateUtil.getDatesBetweenTwo(startDate, endDate);
			dateRanges.add(endDate);
			Calendar c = Calendar.getInstance();
			c.setTime(startDate);

			int date = c.get(Calendar.DATE);
			if (date == 1) {
				response.put(sdf.format(dateRanges.get(0)), approvalSemiMonthly.getDay1());
				response.put(sdf.format(dateRanges.get(1)), approvalSemiMonthly.getDay2());
				response.put(sdf.format(dateRanges.get(2)), approvalSemiMonthly.getDay3());
				response.put(sdf.format(dateRanges.get(3)), approvalSemiMonthly.getDay4());
				response.put(sdf.format(dateRanges.get(4)), approvalSemiMonthly.getDay5());
				response.put(sdf.format(dateRanges.get(5)), approvalSemiMonthly.getDay6());
				response.put(sdf.format(dateRanges.get(6)), approvalSemiMonthly.getDay7());
				response.put(sdf.format(dateRanges.get(7)), approvalSemiMonthly.getDay8());
				response.put(sdf.format(dateRanges.get(8)), approvalSemiMonthly.getDay9());
				response.put(sdf.format(dateRanges.get(9)), approvalSemiMonthly.getDay10());
				response.put(sdf.format(dateRanges.get(10)), approvalSemiMonthly.getDay11());
				response.put(sdf.format(dateRanges.get(11)), approvalSemiMonthly.getDay12());
				response.put(sdf.format(dateRanges.get(12)), approvalSemiMonthly.getDay13());
				response.put(sdf.format(dateRanges.get(13)), approvalSemiMonthly.getDay14());
				response.put(sdf.format(dateRanges.get(14)), approvalSemiMonthly.getDay15());

				if (taskStatusList.contains(approverOneFirstHalfStatus)
						|| taskStatusList.contains(approverTwoFirstHalfStatus)
						|| taskStatusList.contains(financeFirstHalfStatus)) {
					response.put("enabled", false);
				} else {
					response.put("enabled", true);
				}

			}

			if (date == 16) {
				response.put(sdf.format(dateRanges.get(0)), approvalSemiMonthly.getDay16());
				response.put(sdf.format(dateRanges.get(1)), approvalSemiMonthly.getDay17());
				response.put(sdf.format(dateRanges.get(2)), approvalSemiMonthly.getDay18());
				response.put(sdf.format(dateRanges.get(3)), approvalSemiMonthly.getDay19());
				response.put(sdf.format(dateRanges.get(4)), approvalSemiMonthly.getDay20());
				response.put(sdf.format(dateRanges.get(5)), approvalSemiMonthly.getDay21());
				response.put(sdf.format(dateRanges.get(6)), approvalSemiMonthly.getDay22());
				response.put(sdf.format(dateRanges.get(7)), approvalSemiMonthly.getDay23());
				response.put(sdf.format(dateRanges.get(8)), approvalSemiMonthly.getDay24());
				response.put(sdf.format(dateRanges.get(9)), approvalSemiMonthly.getDay25());
				response.put(sdf.format(dateRanges.get(10)), approvalSemiMonthly.getDay26());
				response.put(sdf.format(dateRanges.get(11)), approvalSemiMonthly.getDay27());
				response.put(sdf.format(dateRanges.get(12)), approvalSemiMonthly.getDay28());
				response.put(sdf.format(dateRanges.get(13)), approvalSemiMonthly.getDay29());
				response.put(sdf.format(dateRanges.get(14)), approvalSemiMonthly.getDay30());
				response.put(sdf.format(dateRanges.get(15)), approvalSemiMonthly.getDay31());

				if (taskStatusList.contains(approverOneSecondHalfStatus)
						|| taskStatusList.contains(approverTwoSecondHalfStatus)
						|| taskStatusList.contains(financeSecondHalfStatus)) {
					response.put("enabled", false);
				} else {
					response.put("enabled", true);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;

	}

}
