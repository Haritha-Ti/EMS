package com.EMS.service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
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

			if (null == approvalSemiMonthly) {
				
			}
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
				
				JSONObject hour1 = new JSONObject();
				hour1.put("hour", approvalSemiMonthly.getDay1());
				JSONObject day1 = new JSONObject();
				day1.put(sdf.format(dateRanges.get(0)), hour1);
				
				JSONObject hour2 = new JSONObject();
				hour2.put("hour", approvalSemiMonthly.getDay2());
				JSONObject day2 = new JSONObject();
				day2.put(sdf.format(dateRanges.get(1)), hour2);
				   
				JSONObject hour3 = new JSONObject();
				hour3.put("hour", approvalSemiMonthly.getDay3());
				JSONObject day3 = new JSONObject();
				day3.put(sdf.format(dateRanges.get(2)), hour3);
				
				JSONObject hour4 = new JSONObject();
				hour4.put("hour", approvalSemiMonthly.getDay4());
				JSONObject day4 = new JSONObject();
				day4.put(sdf.format(dateRanges.get(3)), hour4);
				
				JSONObject hour5 = new JSONObject();
				hour5.put("hour", approvalSemiMonthly.getDay5());
				JSONObject day5 = new JSONObject();
				day5.put(sdf.format(dateRanges.get(4)), hour5);
				
				JSONObject hour6 = new JSONObject();
				hour6.put("hour", approvalSemiMonthly.getDay6());
				JSONObject day6 = new JSONObject();
				day6.put(sdf.format(dateRanges.get(5)), hour6);
				
				JSONObject hour7 = new JSONObject();
				hour7.put("hour", approvalSemiMonthly.getDay7());
				JSONObject day7 = new JSONObject();
				day7.put(sdf.format(dateRanges.get(6)), hour7);
				
				JSONObject hour8 = new JSONObject();
				hour8.put("hour", approvalSemiMonthly.getDay8());
				JSONObject day8 = new JSONObject();
				day8.put(sdf.format(dateRanges.get(7)), hour8);
				
				JSONObject hour9 = new JSONObject();
				hour9.put("hour", approvalSemiMonthly.getDay9());
				JSONObject day9 = new JSONObject();
				day9.put(sdf.format(dateRanges.get(8)), hour9);
				
				JSONObject hour10 = new JSONObject();
				hour10.put("hour", approvalSemiMonthly.getDay10());
				JSONObject day10 = new JSONObject();
				day10.put(sdf.format(dateRanges.get(9)), hour10);
				
				JSONObject hour11 = new JSONObject();
				hour11.put("hour", approvalSemiMonthly.getDay11());
				JSONObject day11 = new JSONObject();
				day11.put(sdf.format(dateRanges.get(10)), hour11);
				
				JSONObject hour12 = new JSONObject();
				hour12.put("hour", approvalSemiMonthly.getDay12());
				JSONObject day12 = new JSONObject();
				day12.put(sdf.format(dateRanges.get(11)), hour12);
				
				JSONObject hour13 = new JSONObject();
				hour13.put("hour", approvalSemiMonthly.getDay13());
				JSONObject day13 = new JSONObject();
				day13.put(sdf.format(dateRanges.get(12)), hour13);
				
				JSONObject hour14 = new JSONObject();
				hour14.put("hour", approvalSemiMonthly.getDay14());
				JSONObject day14 = new JSONObject();
				day14.put(sdf.format(dateRanges.get(13)), hour14);
				
				JSONObject hour15 = new JSONObject();
				hour15.put("hour", approvalSemiMonthly.getDay15());
				JSONObject day15 = new JSONObject();
				day15.put(sdf.format(dateRanges.get(14)), hour15);
				
				JSONArray array = new JSONArray();
								
			    array.add(day1);
			    array.add(day2);
			    array.add(day3);
			    array.add(day4);
			    array.add(day5);
			    array.add(day6);
			    array.add(day7);
			    array.add(day8);
			    array.add(day9);
			    array.add(day10);
			    array.add(day11);
			    array.add(day12);
			    array.add(day13);
			    array.add(day14);
			    array.add(day15);
			    response.put("taskList", array);
		
				if (taskStatusList.contains(approverOneFirstHalfStatus)
						|| taskStatusList.contains(approverTwoFirstHalfStatus)
						|| taskStatusList.contains(financeFirstHalfStatus)) {
					response.put("enabled", false);
				} else {
					response.put("enabled", true);
				}

			}

			if (date == 16) {
				JSONObject hour16 = new JSONObject();
				hour16.put("hour", approvalSemiMonthly.getDay16());
				JSONObject day16 = new JSONObject();
				day16.put(sdf.format(dateRanges.get(0)), hour16);
				
				JSONObject hour17 = new JSONObject();
				hour17.put("hour", approvalSemiMonthly.getDay17());
				JSONObject day17 = new JSONObject();
				day17.put(sdf.format(dateRanges.get(1)), hour17);
				
				JSONObject hour18 = new JSONObject();
				hour18.put("hour", approvalSemiMonthly.getDay18());
				JSONObject day18 = new JSONObject();
				day18.put(sdf.format(dateRanges.get(2)), hour18);
				
				JSONObject hour19 = new JSONObject();
				hour19.put("hour", approvalSemiMonthly.getDay19());
				JSONObject day19 = new JSONObject();
				day19.put(sdf.format(dateRanges.get(3)), hour19);

				JSONObject hour20 = new JSONObject();
				hour20.put("hour", approvalSemiMonthly.getDay20());
				JSONObject day20 = new JSONObject();
				day20.put(sdf.format(dateRanges.get(4)), hour20);
				
				JSONObject hour21 = new JSONObject();
				hour21.put("hour", approvalSemiMonthly.getDay21());
				JSONObject day21 = new JSONObject();
				day21.put(sdf.format(dateRanges.get(5)), hour21);
				
				JSONObject hour22 = new JSONObject();
				hour22.put("hour", approvalSemiMonthly.getDay22());
				JSONObject day22 = new JSONObject();
				day22.put(sdf.format(dateRanges.get(6)), hour22);
				
				JSONObject hour23 = new JSONObject();
				hour23.put("hour", approvalSemiMonthly.getDay23());
				JSONObject day23 = new JSONObject();
				day23.put(sdf.format(dateRanges.get(7)), hour23);
				
				JSONObject hour24 = new JSONObject();
				hour24.put("hour", approvalSemiMonthly.getDay24());
				JSONObject day24 = new JSONObject();
				day24.put(sdf.format(dateRanges.get(8)), hour24);
				
				JSONObject hour25 = new JSONObject();
				hour25.put("hour", approvalSemiMonthly.getDay25());
				JSONObject day25 = new JSONObject();
				day25.put(sdf.format(dateRanges.get(9)), hour25);
				
				JSONObject hour26 = new JSONObject();
				hour26.put("hour", approvalSemiMonthly.getDay26());
				JSONObject day26 = new JSONObject();
				day26.put(sdf.format(dateRanges.get(10)), hour26);
				
				JSONObject hour27 = new JSONObject();
				hour27.put("hour", approvalSemiMonthly.getDay27());
				JSONObject day27 = new JSONObject();
				day27.put(sdf.format(dateRanges.get(11)), hour27);
				
				JSONObject hour28 = new JSONObject();
				hour28.put("hour", approvalSemiMonthly.getDay28());
				JSONObject day28 = new JSONObject();
				day28.put(sdf.format(dateRanges.get(12)), hour28);
				
				JSONObject hour29 = new JSONObject();
				hour29.put("hour", approvalSemiMonthly.getDay29());
				JSONObject day29 = new JSONObject();
				day29.put(sdf.format(dateRanges.get(13)), hour29);
				
				JSONObject hour30 = new JSONObject();
				hour30.put("hour", approvalSemiMonthly.getDay30());
				JSONObject day30 = new JSONObject();
				day30.put(sdf.format(dateRanges.get(14)), hour30);
				
				JSONObject hour31 = new JSONObject();
				hour31.put("hour", approvalSemiMonthly.getDay31());
				JSONObject day31 = new JSONObject();
				day31.put(sdf.format(dateRanges.get(15)), hour31);
				
				JSONArray array = new JSONArray();
				
			    array.add(day16);
			    array.add(day17);
			    array.add(day18);
			    array.add(day19);
			    array.add(day20);
			    array.add(day21);
			    array.add(day22);
			    array.add(day23);
			    array.add(day24);
			    array.add(day25);
			    array.add(day26);
			    array.add(day27);
			    array.add(day28);
			    array.add(day29);
			    array.add(day30);
			    array.add(day31);
			    response.put("taskList", array);
		
			
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
