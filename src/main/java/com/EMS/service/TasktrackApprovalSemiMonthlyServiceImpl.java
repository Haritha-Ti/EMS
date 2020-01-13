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
import com.EMS.model.Tasktrack;
import com.EMS.model.TasktrackApprovalSemiMonthly;
import com.EMS.model.UserModel;
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

	@SuppressWarnings({ "unchecked" })
	@Override
	public JSONObject getSemiMonthlyTasktrack(JSONObject requestData) throws Exception {

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
		TasktrackApprovalSemiMonthly approvalSemiMonthly = (TasktrackApprovalSemiMonthly) semiMonthlyRepository.getSemiMonthlyTasktrack(startDate, userId, projectId);

		if (approvalSemiMonthly != null) {

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
				JSONArray array = new JSONArray();
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay1(), dateRanges.get(0));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay2(), dateRanges.get(1));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay3(), dateRanges.get(2));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay4(), dateRanges.get(3));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay5(), dateRanges.get(4));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay6(), dateRanges.get(5));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay7(), dateRanges.get(6));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay8(), dateRanges.get(7));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay9(), dateRanges.get(8));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay10(), dateRanges.get(9));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay11(), dateRanges.get(10));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay12(), dateRanges.get(11));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay13(), dateRanges.get(12));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay14(), dateRanges.get(13));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay15(), dateRanges.get(14));
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
				JSONArray array = new JSONArray();
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay16(), dateRanges.get(0));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay17(), dateRanges.get(1));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay18(), dateRanges.get(2));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay19(), dateRanges.get(3));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay20(), dateRanges.get(4));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay21(), dateRanges.get(5));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay22(), dateRanges.get(6));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay23(), dateRanges.get(7));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay24(), dateRanges.get(8));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay25(), dateRanges.get(9));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay26(), dateRanges.get(10));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay27(), dateRanges.get(11));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay28(), dateRanges.get(12));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay29(), dateRanges.get(13));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay30(), dateRanges.get(14));
				array = addHoursandDaytoArray(array, approvalSemiMonthly.getDay31(), dateRanges.get(15));
				response.put("taskList", array);

				if (taskStatusList.contains(approverOneSecondHalfStatus)
						|| taskStatusList.contains(approverTwoSecondHalfStatus)
						|| taskStatusList.contains(financeSecondHalfStatus)) {
					response.put("enabled", false);
				} else {
					response.put("enabled", true);
				}
			}
		} else {

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
	public int submitForSemiMonthlyApproval(JSONObject requestData) {
		TasktrackApprovalSemiMonthly semiMonthlyApproval = new TasktrackApprovalSemiMonthly();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int requeststatus = 0;

		try {

			semiMonthlyApproval.setDay1(Double.parseDouble(requestData.get("day1").toString()));
			semiMonthlyApproval.setDay2(Double.parseDouble(requestData.get("day2").toString()));
			semiMonthlyApproval.setDay3(Double.parseDouble(requestData.get("day3").toString()));
			semiMonthlyApproval.setDay4(Double.parseDouble(requestData.get("day4").toString()));
			semiMonthlyApproval.setDay5(Double.parseDouble(requestData.get("day5").toString()));
			semiMonthlyApproval.setDay6(Double.parseDouble(requestData.get("day6").toString()));
			semiMonthlyApproval.setDay7(Double.parseDouble(requestData.get("day7").toString()));
			semiMonthlyApproval.setDay8(Double.parseDouble(requestData.get("day8").toString()));
			semiMonthlyApproval.setDay9(Double.parseDouble(requestData.get("day9").toString()));
			semiMonthlyApproval.setDay10(Double.parseDouble(requestData.get("day10").toString()));
			semiMonthlyApproval.setDay11(Double.parseDouble(requestData.get("day11").toString()));
			semiMonthlyApproval.setDay12(Double.parseDouble(requestData.get("day12").toString()));
			semiMonthlyApproval.setDay13(Double.parseDouble(requestData.get("day13").toString()));
			semiMonthlyApproval.setDay14(Double.parseDouble(requestData.get("day14").toString()));
			semiMonthlyApproval.setDay15(Double.parseDouble(requestData.get("day15").toString()));
			semiMonthlyApproval.setDay16(Double.parseDouble(requestData.get("day16").toString()));
			semiMonthlyApproval.setDay17(Double.parseDouble(requestData.get("day17").toString()));
			semiMonthlyApproval.setDay18(Double.parseDouble(requestData.get("day18").toString()));
			semiMonthlyApproval.setDay19(Double.parseDouble(requestData.get("day19").toString()));
			semiMonthlyApproval.setDay20(Double.parseDouble(requestData.get("day20").toString()));
			semiMonthlyApproval.setDay21(Double.parseDouble(requestData.get("day21").toString()));
			semiMonthlyApproval.setDay22(Double.parseDouble(requestData.get("day22").toString()));
			semiMonthlyApproval.setDay23(Double.parseDouble(requestData.get("day23").toString()));
			semiMonthlyApproval.setDay24(Double.parseDouble(requestData.get("day24").toString()));
			semiMonthlyApproval.setDay25(Double.parseDouble(requestData.get("day25").toString()));
			semiMonthlyApproval.setDay26(Double.parseDouble(requestData.get("day26").toString()));
			semiMonthlyApproval.setDay27(Double.parseDouble(requestData.get("day27").toString()));
			semiMonthlyApproval.setDay28(Double.parseDouble(requestData.get("day28").toString()));
			semiMonthlyApproval.setDay29(Double.parseDouble(requestData.get("day29").toString()));
			semiMonthlyApproval.setDay31(Double.parseDouble(requestData.get("day31").toString()));
			semiMonthlyApproval.setDay30(Double.parseDouble(requestData.get("day30").toString()));
			semiMonthlyApproval.setYear(Integer.parseInt(requestData.get("year").toString()));
			semiMonthlyApproval.setMonth(Integer.parseInt(requestData.get("month").toString()));

			if ((!semiMonthlyApproval.getDay1().equals(null)) && (!semiMonthlyApproval.getDay2().equals(null))
					&& (!semiMonthlyApproval.getDay3().equals(null)) && (!semiMonthlyApproval.getDay4().equals(null))
					&& (!semiMonthlyApproval.getDay5().equals(null)) && (!semiMonthlyApproval.getDay6().equals(null))
					&& (!semiMonthlyApproval.getDay7().equals(null)) && (!semiMonthlyApproval.getYear().equals(null))
					&& (!semiMonthlyApproval.getDay8().equals(null)) && (!semiMonthlyApproval.getDay9().equals(null))
					&& (!semiMonthlyApproval.getDay10().equals(null)) && (!semiMonthlyApproval.getDay11().equals(null))
					&& (!semiMonthlyApproval.getDay12().equals(null)) && (!semiMonthlyApproval.getDay13().equals(null))
					&& (!semiMonthlyApproval.getDay14().equals(null)) && (!semiMonthlyApproval.getDay15().equals(null))
					&& (!semiMonthlyApproval.getDay16().equals(null)) && (!semiMonthlyApproval.getDay17().equals(null))
					&& (!semiMonthlyApproval.getDay18().equals(null)) && (!semiMonthlyApproval.getDay19().equals(null))
					&& (!semiMonthlyApproval.getDay20().equals(null)) && (!semiMonthlyApproval.getDay21().equals(null))
					&& (!semiMonthlyApproval.getDay22().equals(null)) && (!semiMonthlyApproval.getDay23().equals(null))
					&& (!semiMonthlyApproval.getDay24().equals(null)) && (!semiMonthlyApproval.getDay25().equals(null))
					&& (!semiMonthlyApproval.getDay26().equals(null)) && (!semiMonthlyApproval.getDay27().equals(null))
					&& (!semiMonthlyApproval.getDay28().equals(null)) && (!semiMonthlyApproval.getDay29().equals(null))
					&& (!semiMonthlyApproval.getDay30().equals(null)) && (!semiMonthlyApproval.getDay31().equals(null))
					&& (!semiMonthlyApproval.getMonth().equals(null))) {

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
			} else
				requeststatus = 1;

			Long userId = Long.parseLong(requestData.get("userId").toString());
			UserModel userInfo = userservice.getUserdetailsbyId(userId);

			if (!userInfo.equals(null))
				semiMonthlyApproval.setUser(userInfo);
			else
				requeststatus = 1;

			Long projectId = Long.parseLong(requestData.get("projectId").toString());
			ProjectModel projectInfo = projectservice.findById(projectId);

			if (!projectInfo.equals(null))
				semiMonthlyApproval.setProject(projectInfo);
			else
				requeststatus = 1;

			if (requestData.get("userFirstHalfStatus").toString().equals(null)
					|| requestData.get("userFirstHalfStatus").toString().equals(" ")
					|| requestData.get("userSecondHalfStatus").toString().equals(null)
					|| requestData.get("userSecondHalfStatus").toString().equals(" ")
					|| requestData.get("approverOneFirstHalfStatus").toString().equals(null)
					|| requestData.get("approverOneFirstHalfStatus").toString().equals(" ")
					|| requestData.get("approverOneSecondHalfStatus").toString().equals(null)
					|| requestData.get("approverOneSecondHalfStatus").toString().equals(" ")
					|| requestData.get("approverTwoFirstHalfStatus").toString().equals(null)
					|| requestData.get("approverTwoFirstHalfStatus").toString().equals(" ")
					|| requestData.get("approverTwoSecondHalfStatus").toString().equals(null)
					|| requestData.get("approverTwoSecondHalfStatus").toString().equals(" ")
					|| requestData.get("financeFirstHalfStatus").toString().equals(null)
					|| requestData.get("financeFirstHalfStatus").toString().equals(" ")
					|| requestData.get("financeSecondHalfStatus").toString().equals(null)
					|| requestData.get("financeSecondHalfStatus").toString().equals(" ")) {
				requeststatus = 1;

			} else {

				semiMonthlyApproval.setUserFirstHalfStatus(requestData.get("userFirstHalfStatus").toString());

				semiMonthlyApproval.setUserSecondHalfStatus(requestData.get("userSecondHalfStatus").toString());
				semiMonthlyApproval
						.setApproverOneFirstHalfStatus(requestData.get("approverOneFirstHalfStatus").toString());
				semiMonthlyApproval
						.setApproverOneSecondHalfStatus(requestData.get("approverOneSecondHalfStatus").toString());
				semiMonthlyApproval
						.setApproverTwoFirstHalfStatus(requestData.get("approverTwoFirstHalfStatus").toString());
				semiMonthlyApproval
						.setApproverTwoSecondHalfStatus(requestData.get("approverTwoSecondHalfStatus").toString());
				semiMonthlyApproval.setFinanceFirstHalfStatus(requestData.get("financeFirstHalfStatus").toString());
				semiMonthlyApproval.setFinanceSecondHalfStatus(requestData.get("financeSecondHalfStatus").toString());
			}

			if ((requestData.get("userFirstHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setUserFirstHalfSubmittedDate(
						sdf.parse(requestData.get("userFirstHalfSubmittedDate").toString()));

			}

			if ((requestData.get("userSecondHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setUserSecondHalfSubmittedDate(
						sdf.parse(requestData.get("userSecondHalfSubmittedDate").toString()));

			}

			if (requestData.get("approverOneId") != null) {
				Long approver1Id = Long.parseLong(requestData.get("approverOneId").toString());
				UserModel approver1Info = userservice.getUserdetailsbyId(approver1Id);

				// if (!approver1Info.equals(null))
				// semiMonthlyApproval.set(approver1Info);
			}

			if ((requestData.get("approverOneFirstHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setApproverOneFirstHalfSubmittedDate(
						sdf.parse(requestData.get("approverOneFirstHalfSubmittedDate").toString()));

			}

			if ((requestData.get("approverOneSecondHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setApproverOneSecondHalfSubmittedDate(
						sdf.parse(requestData.get("approverOneSecondHalfSubmittedDate").toString()));

			}

			if (requestData.get("approverTwoId") != null) {

				Long approver2Id = Long.parseLong(requestData.get("approverTwoId").toString());
				UserModel approver2Info = userservice.getUserdetailsbyId(approver2Id);

				// if (!approver2Info.equals(null))
				// semiMonthlyApproval.setApproverTwoId(approver2Info);
			}

			if ((requestData.get("approverTwoFirstHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setApproverTwoFirstHalfSubmittedDate(
						sdf.parse(requestData.get("approverTwoFirstHalfSubmittedDate").toString()));

			}

			if ((requestData.get("approverTwoSecondHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setApproverTwoSecondHalfSubmittedDate(
						sdf.parse(requestData.get("approverTwoSecondHalfSubmittedDate").toString()));

			}

			if (requestData.get("financeUser") != null) {

				Long financeUserId = Long.parseLong(requestData.get("financeUser").toString());
				UserModel financeUser = userservice.getUserdetailsbyId(financeUserId);

				if (!financeUser.equals(null))
					semiMonthlyApproval.setFinanceId(financeUser);
			}

			if ((requestData.get("financeFirstHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setFinanceFirstHalfSubmittedDate(
						sdf.parse(requestData.get("financeFirstHalfSubmittedDate").toString()));

			}

			if ((requestData.get("financeSecondHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setFinanceSecondHalfSubmittedDate(
						sdf.parse(requestData.get("financeSecondHalfSubmittedDate").toString()));

			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (requeststatus == 0) {

			int count = semiMonthlyRepository.getsemiMonthlyRecord(semiMonthlyApproval.getUser().getUserId(),
					semiMonthlyApproval.getMonth(), semiMonthlyApproval.getYear());
			if (count == 0) {
				semiMonthlyRepository.save(semiMonthlyApproval);
				return 0;
			} else
				return 1;
		} else
			return 1;
	}

	@Override
	public int saveSemiMonthlyTaskTrackApproval(JSONObject requestData) {

		TasktrackApprovalSemiMonthly semiMonthlyApproval = new TasktrackApprovalSemiMonthly();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int requeststatus = 0;

		try {

			Double day1 = Double.parseDouble(requestData.get("day1").toString());
			if ((day1 != null) && ((day1 >= 0) || (day1 <= 24)))
				semiMonthlyApproval.setDay1(day1);
			Double day2 = Double.parseDouble(requestData.get("day2").toString());
			if ((day2 != null) && ((day2 >= 0) || (day2 <= 24)))
				semiMonthlyApproval.setDay2(day2);
			Double day3 = Double.parseDouble(requestData.get("day3").toString());
			if ((day3 != null) && ((day3 >= 0) || (day3 <= 24)))
				semiMonthlyApproval.setDay3(day3);
			Double day4 = Double.parseDouble(requestData.get("day4").toString());
			if ((day4 != null) && ((day4 >= 0) || (day4 <= 24)))
				semiMonthlyApproval.setDay4(day4);
			Double day5 = Double.parseDouble(requestData.get("day5").toString());
			if ((day5 != null) && ((day5 >= 0) || (day5 <= 24)))
				semiMonthlyApproval.setDay5(day5);
			Double day6 = Double.parseDouble(requestData.get("day6").toString());
			if ((day6 != null) && ((day6 >= 0) || (day6 <= 24)))
				semiMonthlyApproval.setDay6(day6);
			Double day7 = Double.parseDouble(requestData.get("day7").toString());
			if ((day7 != null) && ((day7 >= 0) || (day7 <= 24)))
				semiMonthlyApproval.setDay7(day7);
			Double day8 = Double.parseDouble(requestData.get("day8").toString());
			if ((day8 != null) && ((day8 >= 0) || (day8 <= 24)))
				semiMonthlyApproval.setDay8(day8);
			Double day9 = Double.parseDouble(requestData.get("day9").toString());
			if ((day9 != null) && ((day9 >= 0) || (day9 <= 24)))
				semiMonthlyApproval.setDay9(day9);
			Double day10 = Double.parseDouble(requestData.get("day10").toString());
			if ((day10 != null) && ((day10 >= 0) || (day10 <= 24)))
				semiMonthlyApproval.setDay10(day10);
			Double day11 = Double.parseDouble(requestData.get("day11").toString());
			if ((day11 != null) && ((day11 >= 0) || (day11 <= 24)))
				semiMonthlyApproval.setDay11(day11);
			Double day12 = Double.parseDouble(requestData.get("day12").toString());
			if ((day12 != null) && ((day12 >= 0) || (day12 <= 24)))
				semiMonthlyApproval.setDay12(day12);
			Double day13 = Double.parseDouble(requestData.get("day13").toString());
			if ((day13 != null) && ((day13 >= 0) || (day13 <= 24)))
				semiMonthlyApproval.setDay13(day13);
			Double day14 = Double.parseDouble(requestData.get("day14").toString());
			if ((day14 != null) && ((day14 >= 0) || (day14 <= 24)))
				semiMonthlyApproval.setDay14(day14);
			Double day15 = Double.parseDouble(requestData.get("day15").toString());
			if ((day15 != null) && ((day15 >= 0) || (day15 <= 24)))
				semiMonthlyApproval.setDay15(day15);
			Double day16 = Double.parseDouble(requestData.get("day16").toString());
			if ((day16 != null) && ((day16 >= 0) || (day16 <= 24)))
				semiMonthlyApproval.setDay16(day16);
			Double day17 = Double.parseDouble(requestData.get("day17").toString());
			if ((day17 != null) && ((day17 >= 0) || (day17 <= 24)))
				semiMonthlyApproval.setDay17(day17);
			Double day18 = Double.parseDouble(requestData.get("day18").toString());
			if ((day18 != null) && ((day18 >= 0) || (day18 <= 24)))
				semiMonthlyApproval.setDay18(day18);
			Double day19 = Double.parseDouble(requestData.get("day19").toString());
			if ((day19 != null) && ((day19 >= 0) || (day19 <= 24)))
				semiMonthlyApproval.setDay19(day19);
			Double day20 = Double.parseDouble(requestData.get("day20").toString());
			if ((day20 != null) && ((day20 >= 0) || (day20 <= 24)))
				semiMonthlyApproval.setDay20(day20);
			Double day21 = Double.parseDouble(requestData.get("day21").toString());
			if ((day21 != null) && ((day21 >= 0) || (day21 <= 24)))
				semiMonthlyApproval.setDay21(day21);
			Double day22 = Double.parseDouble(requestData.get("day22").toString());
			if ((day22 != null) && ((day22 >= 0) || (day22 <= 24)))
				semiMonthlyApproval.setDay22(day22);
			Double day23 = Double.parseDouble(requestData.get("day23").toString());
			if ((day23 != null) && ((day23 >= 0) || (day23 <= 24)))
				semiMonthlyApproval.setDay23(day23);
			Double day24 = Double.parseDouble(requestData.get("day24").toString());
			if ((day24 != null) && ((day24 >= 0) || (day24 <= 24)))
				semiMonthlyApproval.setDay24(day24);
			Double day25 = Double.parseDouble(requestData.get("day25").toString());
			if ((day25 != null) && ((day25 >= 0) || (day25 <= 24)))
				semiMonthlyApproval.setDay25(day25);
			Double day26 = Double.parseDouble(requestData.get("day26").toString());
			if ((day26 != null) && ((day26 >= 0) || (day26 <= 24)))
				semiMonthlyApproval.setDay26(day26);
			Double day27 = Double.parseDouble(requestData.get("day27").toString());
			if ((day27 != null) && ((day27 >= 0) || (day27 <= 24)))
				semiMonthlyApproval.setDay27(day27);
			Double day28 = Double.parseDouble(requestData.get("day28").toString());
			if ((day28 != null) && ((day28 >= 0) || (day28 <= 24)))
				semiMonthlyApproval.setDay28(day28);
			Double day29 = Double.parseDouble(requestData.get("day29").toString());
			if ((day29 != null) && ((day29 >= 0) || (day29 <= 24)))
				semiMonthlyApproval.setDay29(day29);
			Double day30 = Double.parseDouble(requestData.get("day30").toString());
			if ((day30 != null) && ((day30 >= 0) || (day30 <= 24)))
				semiMonthlyApproval.setDay30(day30);
			Double day31 = Double.parseDouble(requestData.get("day31").toString());
			if ((day31 != null) && ((day31 >= 0) || (day31 <= 24)))
				semiMonthlyApproval.setDay31(day31);

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

			Long projectId = Long.parseLong(requestData.get("projectId").toString());
			ProjectModel projectInfo = projectservice.findById(projectId);

			if (!projectInfo.equals(null))
				semiMonthlyApproval.setProject(projectInfo);
			else
				requeststatus = 1;

			if ((requestData.get("userFirstHalfStatus") != null))
				semiMonthlyApproval.setUserFirstHalfStatus(requestData.get("userFirstHalfStatus").toString());

			if ((requestData.get("userSecondHalfStatus") != null))
				semiMonthlyApproval.setUserFirstHalfStatus(requestData.get("userSecondHalfStatus").toString());

			if ((requestData.get("approverOneFirstHalfStatus") != null))
				semiMonthlyApproval.setUserFirstHalfStatus(requestData.get("approverOneFirstHalfStatus").toString());

			if ((requestData.get("approverOneSecondHalfStatus") != null))
				semiMonthlyApproval.setUserFirstHalfStatus(requestData.get("approverOneSecondHalfStatus").toString());

			if ((requestData.get("approverTwoFirstHalfStatus") != null))
				semiMonthlyApproval.setUserFirstHalfStatus(requestData.get("approverTwoFirstHalfStatus").toString());

			if ((requestData.get("approverTwoSecondHalfStatus") != null))
				semiMonthlyApproval.setUserFirstHalfStatus(requestData.get("approverTwoSecondHalfStatus").toString());

			if ((requestData.get("financeFirstHalfStatus") != null))
				semiMonthlyApproval.setUserFirstHalfStatus(requestData.get("financeFirstHalfStatus").toString());

			if ((requestData.get("financeSecondHalfStatus") != null))
				semiMonthlyApproval.setUserFirstHalfStatus(requestData.get("financeSecondHalfStatus").toString());

			if ((requestData.get("userFirstHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setUserFirstHalfSubmittedDate(
						sdf.parse(requestData.get("userFirstHalfSubmittedDate").toString()));

			}

			if ((requestData.get("userSecondHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setUserSecondHalfSubmittedDate(
						sdf.parse(requestData.get("userSecondHalfSubmittedDate").toString()));

			}

			if (requestData.get("approverOneId") != null) {
				Long approver1Id = Long.parseLong(requestData.get("approverOneId").toString());
				UserModel approver1Info = userservice.getUserdetailsbyId(approver1Id);

				// if (!approver1Info.equals(null))
				// semiMonthlyApproval.setApproverOneId(approver1Info);
			}

			if ((requestData.get("approverOneFirstHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setApproverOneFirstHalfSubmittedDate(
						sdf.parse(requestData.get("approverOneFirstHalfSubmittedDate").toString()));

			}

			if ((requestData.get("approverOneSecondHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setApproverOneSecondHalfSubmittedDate(
						sdf.parse(requestData.get("approverOneSecondHalfSubmittedDate").toString()));

			}

			if (requestData.get("approverTwoId") != null) {
				Long approver2Id = Long.parseLong(requestData.get("approverTwoId").toString());
				UserModel approver2Info = userservice.getUserdetailsbyId(approver2Id);

				// if (!approver2Info.equals(null))
				// semiMonthlyApproval.setApproverTwoId(approver2Info);
			}

			if ((requestData.get("approverTwoFirstHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setApproverTwoFirstHalfSubmittedDate(
						sdf.parse(requestData.get("approverTwoFirstHalfSubmittedDate").toString()));

			}

			if ((requestData.get("approverTwoSecondHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setApproverTwoSecondHalfSubmittedDate(
						sdf.parse(requestData.get("approverTwoSecondHalfSubmittedDate").toString()));

			}

			if (requestData.get("financeUser") != null) {
				Long financeUserId = Long.parseLong(requestData.get("financeUser").toString());
				UserModel financeUser = userservice.getUserdetailsbyId(financeUserId);

				if (!financeUser.equals(null))
					semiMonthlyApproval.setFinanceId(financeUser);
			}

			if ((requestData.get("financeFirstHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setFinanceFirstHalfSubmittedDate(
						sdf.parse(requestData.get("financeFirstHalfSubmittedDate").toString()));

			}

			if ((requestData.get("financeSecondHalfSubmittedDate") != null)) {
				semiMonthlyApproval.setFinanceSecondHalfSubmittedDate(
						sdf.parse(requestData.get("financeSecondHalfSubmittedDate").toString()));

			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (requeststatus == 0) {
			int count = semiMonthlyRepository.checkduplicationForsemiMonthlyTaskTrack(
					semiMonthlyApproval.getUser().getUserId(), semiMonthlyApproval.getMonth(),
					semiMonthlyApproval.getYear());
			if (count == 0) {
				semiMonthlyRepository.save(semiMonthlyApproval);
				return 0;
			} else
				return 1;
		} else
			return 1;
	}

	@Override
	public int getSemiMonthlyTasksForSubmission(JsonNode requestData) {
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

		TasktrackApprovalSemiMonthly semiMonthlytasksubmission = new TasktrackApprovalSemiMonthly();
		UserModel userDetails = userservice.getUserdetailsbyId(userId);
		ProjectModel project = projectservice.findById(projectId);

		semiMonthlytasksubmission.setUser(userDetails);
		semiMonthlytasksubmission.setProject(project);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		semiMonthlytasksubmission.setYear(calendar.get(Calendar.YEAR));

		semiMonthlytasksubmission.setMonth(calendar.get(Calendar.MONTH) + 1);
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

			try {
				date1 = sdf.parse(stgdate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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

		int count = semiMonthlyRepository.getsemiMonthlyRecord(userId, semiMonthlytasksubmission.getYear(),
				semiMonthlytasksubmission.getMonth());

		if (count > 0) {
			return 0;
		} else {

			semiMonthlyRepository.save(semiMonthlytasksubmission);
			return 1;
		}

	}

}
