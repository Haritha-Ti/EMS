package com.EMS.controller;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import com.EMS.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.service.AttendanceService;
import com.EMS.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.qos.logback.core.pattern.parser.Node;

@RestController
@RequestMapping(value = "/attendance")
public class AttendanceController {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AttendanceService attendanceService;

	@Autowired
	private UserService userservice;

	@GetMapping("/getHolidayList")
	public ObjectNode getHolidayList(HttpServletResponse httpstatus) {

		ArrayNode jsonArray = objectMapper.createArrayNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();

		try {
			List<Object[]> holidayList = attendanceService.getHolidayList();
			System.out.println("holidayList : " + holidayList.size());
//			ArrayNode node = objectMapper.convertValue(attendanceService.getHolidayList(), ArrayNode.class);
			for (Object[] item : holidayList) {
				ObjectNode holidayNode = objectMapper.createObjectNode();
				String holidayId = item[1].toString();
				String date = item[0].toString();
				String day = item[2].toString();
				String holidayName = item[3].toString();
				String holidayType = item[4].toString();
				holidayNode.put("holidayId", holidayId);
				holidayNode.put("date", date);
				holidayNode.put("day", day);
				holidayNode.put("holidayName", holidayName);
				holidayNode.put("holidayType", holidayType);
				holidayNode.put("region_id", item[5].toString());
				holidayNode.put("region_name", item[6].toString());
				jsonArray.add(holidayNode);

			}
			jsonDataRes.set("data", jsonArray);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("message", "success. ");
			jsonDataRes.put("code", httpstatus.getStatus());

		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;

	}

	@PostMapping("/getUserLeaveList")
	public ObjectNode getUserLeaveList(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) {

		ArrayNode jsonArray = objectMapper.createArrayNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		ObjectNode leaveBalanceNode = objectMapper.createObjectNode();

		Long userId = null;
		String type = null, date1 = null, date2 = null;

		LocalDate date = LocalDate.now();
		LocalDate firstDayOfYear = date.with(TemporalAdjusters.firstDayOfYear());
		LocalDate lastDayOfYear = date.with(TemporalAdjusters.lastDayOfYear());

		int quarter = 0;
		int monthNumber = date.getMonthValue();
		int year = date.getYear();

		if (monthNumber >= 1 && monthNumber <= 3)
			quarter = 1;
		else if (monthNumber >= 4 && monthNumber <= 6)
			quarter = 2;
		else if (monthNumber >= 7 && monthNumber <= 9)
			quarter = 3;
		else if (monthNumber >= 10 && monthNumber <= 12)
			quarter = 4;

		try {
			if (requestdata.get("userId") != null && requestdata.get("userId").asText() != "")
				userId = requestdata.get("userId").asLong();
			if (requestdata.get("leaveType") != null && requestdata.get("leaveType").asText() != "")
				type = requestdata.get("leaveType").asText();
			if (requestdata.get("leaveFrom") != null && requestdata.get("leaveFrom").asText() != "")
				date1 = requestdata.get("leaveFrom").asText();
			if (requestdata.get("leaveTo") != null && requestdata.get("leaveTo").asText() != "")
				date2 = requestdata.get("leaveTo").asText();

			LocalDate startDate = null, endDate = null;
			if (date1 != null && !date1.isEmpty()) {
				startDate = LocalDate.parse(date1);
				System.out.println("start date : " + startDate);
			}
			if (date1 != null && !date2.isEmpty()) {
				endDate = LocalDate.parse(date2);
			}

			List<Object[]> leaveModel = null;

			if (userId != null && type == null && startDate == null && endDate == null)
				leaveModel = attendanceService.getUserLeaveList(userId, firstDayOfYear, lastDayOfYear);
			else if (userId != null && type != null && startDate == null && endDate == null)
				leaveModel = attendanceService.getUserLeaveListByLeaveType(userId, type, firstDayOfYear, lastDayOfYear);
			else if (userId != null && type != null && startDate != null && endDate != null)
				leaveModel = attendanceService.getUserLeaveListByLeaveType(userId, type, startDate, endDate);
			else if (userId != null && type == null && startDate != null && endDate != null)
				leaveModel = attendanceService.getUserLeaveList(userId, startDate, endDate);

			if (leaveModel != null) {
				System.out.println("leave list size : " + leaveModel.size());

				for (Object[] leave : leaveModel) {
					ObjectNode leaveObject = objectMapper.createObjectNode();

					String leaveType = leave[4].toString();
					String leaveCount = leave[5].toString();
					String leaveDescription = leave[2].toString();
					String leaveFrom = leave[0].toString();
					String leaveTo = leave[1].toString();
					String status = leave[3].toString();

					leaveObject.put("Leave Type", leaveType);
					leaveObject.put("Leave Count", leaveCount);
					leaveObject.put("Leave Description", leaveDescription);
					leaveObject.put("Start Date", leaveFrom);
					leaveObject.put("End Date", leaveTo);
					leaveObject.put("Status", status);
					jsonArray.add(leaveObject);

				}

				// to obtain user leave balance details
				leaveBalanceNode = attendanceService.getLeavebalanceData(userId, quarter, year);

			}

			jsonDataRes.set("data", jsonArray);
			jsonDataRes.set("leaveBalance", leaveBalanceNode);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("message", "success. ");
			jsonDataRes.put("code", httpstatus.getStatus());
		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;
	}

	@PostMapping("/getWeeklyLeaveList")
	public JsonNode getweeklyLeeveList(@RequestBody JsonNode requestdata, HttpServletResponse response) {
		ObjectNode responseData = objectMapper.createObjectNode();

		String startDate = requestdata.get("startDate").asText();
		String endDate = requestdata.get("endDate").asText();

		try {

			LocalDate start = LocalDate.parse(startDate);
			LocalDate end = LocalDate.parse(endDate);
			ObjectNode datewise = objectMapper.createObjectNode();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//			DateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd");
			Date startdate1 = formatter.parse(startDate);
			Date enddate1 = formatter.parse(endDate);
			List<Object[]> leavelist = attendanceService.getWeeklyLeavelist(startdate1, enddate1);
			System.out.println("list"+leavelist.size());
			while (!start.isAfter(end)) {

				System.out.println("day  :" + start);
//				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
				String stgdate = String.valueOf(start);
				Date date1 = formatter.parse(stgdate);

				ArrayNode leaverecord = objectMapper.createArrayNode();

				for (Object[] leaveobj : leavelist) {

					LeaveModel leave=new LeaveModel();
					long leaveid=Long.parseLong(String.valueOf(leaveobj[0]));
					leave.setLeaveId(leaveid);
					leave.setLeaveFrom((Date)leaveobj[4]);
					leave.setLeaveTo((Date)leaveobj[6]);
					leave.setLeaveCount((double)leaveobj[3]);
					leave.setLeaveType(leaveobj[7].toString());
					leave.setStatus(leaveobj[8].toString());
					leave.setLeaveReason(leaveobj[5].toString());
					UserModel user=new UserModel();
					long userid=Long.parseLong(String.valueOf(leaveobj[9]));
					user.setUserId(userid);
					leave.setUser(user);
					
					ObjectNode node = objectMapper.createObjectNode();
					System.out.println("userid :" + leave.getUser().getUserId());
					String userDetail = userservice.getUserName(leave.getUser().getUserId());
					String username = userDetail.replace(",", " ");
					System.out.println("username:" + username+"leaveid"+leave.getLeaveId());
					node.put("username", username);
					node.put("reason", leave.getLeaveReason().toString());
					node.put("leaveType", leave.getLeaveType().toLowerCase());
//					node.put("count", leave.getLeaveCount());
					System.out.println("sec1");
					if ((leave.getLeaveFrom().compareTo(date1) >= 0) && (leave.getLeaveTo().compareTo(enddate1) <= 0)) {
						System.out.println("if 1 lFrom" + leave.getLeaveFrom() + " lTo:" + leave.getLeaveTo() + " compdate:"
								+ date1+"   leaveid  :"+leave.getLeaveId());

						if ((leave.getLeaveFrom().compareTo(date1)<=0)) {
							System.out.println("leaveid :"+leave.getLeaveId());
							node.put("startDate", leave.getLeaveFrom().toString());
							node.put("endDate", leave.getLeaveTo().toString());
							node.put("count", 1.0);
							leaverecord.add(node);
						}


					}else if ((leave.getLeaveFrom().compareTo(date1) <= 0) && (leave.getLeaveTo().compareTo(enddate1) > 0)) {
						
						System.out.println("sec2");
						System.out.println("if 2  lFrom" + leave.getLeaveFrom() + " lTo:" + leave.getLeaveTo() +"compadate: "+date1+" leaveid:"
								+ leave.getLeaveId());
						if (leave.getLeaveFrom().compareTo(date1) <= 0) {
							System.out.println("leaveid :"+leave.getLeaveId());
							node.put("startDate", leave.getLeaveFrom().toString());
							node.put("endDate", leave.getLeaveTo().toString());
							node.put("count", 1.0);
							leaverecord.add(node);
						}
					}else if ((leave.getLeaveFrom().compareTo(date1) <= 0) && (leave.getLeaveTo().compareTo(enddate1) <= 0)
							&& (leave.getLeaveTo().compareTo(date1) >= 0)) {
						
						System.out.println("sec3");
						System.out.println("if 3  lFrom" + leave.getLeaveFrom() + " lTo:" + leave.getLeaveTo() + "  compadate: "+date1+" leaveid:"
								+ leave.getLeaveId());
						if (leave.getLeaveFrom().compareTo(date1) <= 0) {
							System.out.println("leaveid :"+leave.getLeaveId());
							node.put("startDate", leave.getLeaveFrom().toString());
							node.put("endDate", leave.getLeaveTo().toString());
							node.put("count", 1.0);
							leaverecord.add(node);
						}
					}else if ((leave.getLeaveFrom().compareTo(date1) > 0) && (leave.getLeaveTo().compareTo(enddate1) > 0)
							&& (leave.getLeaveFrom().compareTo(enddate1) < 0)) {
						System.out.println("sec4");
						System.out.println("if 4  lFrom" + leave.getLeaveFrom() + " lTo:" + leave.getLeaveTo() + " compadate: "+date1+" leaveid:"
								+ leave.getLeaveId());
						if (leave.getLeaveFrom().compareTo(date1) <= 0) {
							System.out.println("leaveid :"+leave.getLeaveId());
							node.put("startDate", leave.getLeaveFrom().toString());
							node.put("endDate", leave.getLeaveTo().toString());
							node.put("count", 1.0);
							leaverecord.add(node);
						}
					}else if(leave.getLeaveTo().equals(date1)) {
						System.out.println("sec5");
						System.out.println("leaveid :"+leave.getLeaveId());
						node.put("startDate", leave.getLeaveFrom().toString());
						node.put("endDate", leave.getLeaveTo().toString());
						node.put("count", 1.0);
						leaverecord.add(node);
					}

				}

				datewise.set(stgdate, leaverecord);
				start = start.plusDays(1);
			}
			responseData.put("status", "success");
			responseData.put("code", response.getStatus());
			responseData.put("message", "success");
			responseData.set("payload", datewise);

		} catch (Exception e) {

			responseData.put("status", "Failed");
			responseData.put("code", response.getStatus());
			responseData.put("message", "Exception:" + e);
			e.printStackTrace();
			e.printStackTrace();
		}

		return responseData;
	}

	@PostMapping("/getYearlyLeaveList")
	public JsonNode getYearlyLeeveList(@RequestBody JsonNode requestdata, HttpServletResponse response) {
		ObjectNode responseData = objectMapper.createObjectNode();

		String startDate = requestdata.get("startDate").asText();
		String endDate = requestdata.get("endDate").asText();

		try {

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			Date startDate1 = formatter.parse(startDate);
			Date endDate1 = formatter.parse(endDate);

			ArrayNode userleaverecord = objectMapper.createArrayNode();
			List<UserModel> userlist = userservice.getAllUsers();
			if (!userlist.isEmpty()) {
				System.out.println("array size :" + userlist.size());
				for (UserModel user : userlist) {

					ObjectNode node = objectMapper.createObjectNode();
					node.put("employeeID", user.getEmpId());
					node.put("employeeName", user.getFirstName() + " " + user.getLastName());

					System.out.println("userid :" + user.getUserId());
					List<LeaveModel> leavelist = attendanceService.getYearlyLeavelist(user.getUserId(), startDate1,
							endDate1);
					double clCount = 0, elCount = 0, lopCount = 0, slCount = 0;

					for (LeaveModel leave : leavelist) {

						if (leave.getLeaveType().toLowerCase().equals("cl"))
							clCount = clCount + leave.getLeaveCount();

						if (leave.getLeaveType().toLowerCase().equals("el"))
							elCount = elCount + leave.getLeaveCount();

						if (leave.getLeaveType().toLowerCase().equals("lop"))
							lopCount = lopCount + leave.getLeaveCount();

						if (leave.getLeaveType().toLowerCase().equals("sl"))
							slCount = slCount + leave.getLeaveCount();

					}
					node.put("casualLeave", clCount);
					node.put("earnedLeave", elCount);
					node.put("LOP", lopCount);
					node.put("sickLeave", slCount);

					userleaverecord.add(node);
				}

			}

			responseData.put("status", "success");
			responseData.put("code", response.getStatus());
			responseData.put("message", "success");
			responseData.set("payload", userleaverecord);

		} catch (Exception e) {
			responseData.put("status", "Failed");
			responseData.put("code", response.getStatus());
			responseData.put("message", "Exception:" + e);
			e.printStackTrace();
		}

		return responseData;
	}

	@PostMapping("/leaveMarking")
	public ObjectNode setLeaveMarking(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) {
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		try {

			TimeZone zone = TimeZone.getTimeZone("MST");
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			outputFormat.setTimeZone(zone);

			Long userId = requestdata.get("userId").asLong();
			UserModel user = userservice.getUserDetailsById(userId);

			LocalDate now = LocalDate.now();

			ArrayNode leaveNode = (ArrayNode) requestdata.get("leaveList");
			System.out.println("leaveNode size : " + leaveNode.size());
			for (JsonNode node : leaveNode) {
				LeaveModel leaveModel = new LeaveModel();
				leaveModel.setUser(user);
				leaveModel.setStatus(requestdata.get("status").asText());
				leaveModel.setAppliedDate(outputFormat.parse(now.toString()));
				leaveModel.setLeaveReason(node.get("reason").asText());
				String startDate = node.get("startdate").asText();
				String endDate = node.get("enddate").asText();
				System.out.println("start:" + startDate);
				if (!startDate.isEmpty()) {
					leaveModel.setLeaveFrom(outputFormat.parse(startDate));
				}
				if (!endDate.isEmpty()) {
					leaveModel.setLeaveTo(outputFormat.parse(endDate));
				}
				double leaveCount = node.get("count").asDouble();
				leaveModel.setLeaveType(node.get("leaveType").asText().toUpperCase());
				leaveModel.setLeaveCount(leaveCount);

				LocalDate startdate = LocalDate.parse(startDate);
				LocalDate enddate = LocalDate.parse(endDate);

				// balance check
				LeaveBalanceModel lBalance = new LeaveBalanceModel();
				int year = startdate.getYear();
				lBalance.setYear(year);
				lBalance.setUser(leaveModel.getUser());
				int month = startdate.getMonthValue();
				System.out.println("month :" + month);
				int quarterleave = 0;
				if (month <= 3) {
					quarterleave = 1;
				} else if (month <= 6 && month > 3) {
					quarterleave = 2;
				} else if (month <= 9 && month > 6) {
					quarterleave = 3;
				} else if (month <= 12 && month > 9) {
					quarterleave = 4;

				}

				lBalance.setQuarter(quarterleave);
				System.out.println("quat:" + lBalance.getQuarter() + "yera:" + lBalance.getYear() + " :user"
						+ lBalance.getUser().getUserId());
				LeaveBalanceModel leavebal = attendanceService.getUserLeaveBalance(lBalance.getQuarter(),
						lBalance.getYear(), lBalance.getUser().getUserId());

				System.out.println("sl:" + leavebal.getSlBalance() + " el:" + leavebal.getElBalance() + " cl:"
						+ leavebal.getClBalance());

				double leavebalance = 0;

				if (leaveModel.getLeaveType().equals("CL")) {

					if (leavebal.getClBalance() >= leaveModel.getLeaveCount()) {

						leavebalance = leavebal.getClBalance();
						leavebalance = leavebalance - leaveModel.getLeaveCount();
						System.out.println("new bal:" + leavebalance + " count:" + leaveModel.getLeaveCount());
						leavebal.setClBalance(leavebalance);
					}
				} else if (leaveModel.getLeaveType().equals("SL")) {
					if (leavebal.getSlBalance() >= leaveModel.getLeaveCount()) {
						leavebalance = leavebal.getSlBalance();
						leavebalance = leavebalance - leaveModel.getLeaveCount();
						System.out.println("new bal:" + leavebalance + " count:" + leaveModel.getLeaveCount());
						leavebal.setSlBalance(leavebalance);
					}
				} else if (leaveModel.getLeaveType().equals("EL")) {
					if (leavebal.getElBalance() >= leaveModel.getLeaveCount()) {

						leavebalance = leavebal.getElBalance();
						leavebalance = leavebalance - leaveModel.getLeaveCount();
						leavebal.setElBalance(leavebalance);
					}
					System.out.println("new bal:" + leavebalance + " count:" + leaveModel.getLeaveCount());

				}

				attendanceService.setLeaveBalance(leavebal);
				boolean statusFlag = false;
				Boolean checkvalue = attendanceService.checkLeave(leaveModel);
				if (checkvalue) {
					statusFlag = true;
					attendanceService.saveLeaveMarking(leaveModel);
					jsonDataRes.put("status", "success");
					jsonDataRes.put("message", "successfully saved.");
				} else {
					jsonDataRes.put("status", "success");
					jsonDataRes.put("message", "duplicate entry");
				}
				jsonDataRes.put("flag", statusFlag);
				jsonDataRes.put("code", httpstatus.getStatus());
			}

		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;

	}

	@GetMapping("/getLeaveBalanceList")
	public JsonNode getLeaveBalanceList(HttpServletResponse response) {
		ObjectNode responseData = objectMapper.createObjectNode();

		try {

			LocalDate start = LocalDate.now();

			int year = start.getYear();
			int month = start.getMonthValue();
			System.out.println("month :" + month);
			int quarterleave = 0;
			if (month <= 3) {
				quarterleave = 1;
			} else if (month <= 6 && month > 3) {
				quarterleave = 2;
			} else if (month <= 9 && month > 6) {
				quarterleave = 3;
			} else if (month <= 12 && month > 9) {
				quarterleave = 4;

			}
			LeaveBalanceModel lBalance = new LeaveBalanceModel();
			lBalance.setQuarter(quarterleave);
			lBalance.setYear(year);

			ArrayNode leaverecord = objectMapper.createArrayNode();
			List<Long> userlist = attendanceService.getAllUserId();
			if (!userlist.isEmpty()) {
				System.out.println("array size :" + userlist.size());
				for (int i = 0; i < userlist.size(); i++) {
					ObjectNode node = objectMapper.createObjectNode();
					String data = String.valueOf(userlist.get(i));
					Long userId = Long.parseLong(data);
					System.out.println(data + "user id:" + userId);
					UserModel userdata = userservice.getUserDetailsById(userId);
					System.out.println("userid :" + userlist.get(i));
					ObjectNode leavebalance = attendanceService.getLeavebalanceData(userId, lBalance.getQuarter(),
							lBalance.getYear());
					node.put("empId", userdata.getEmpId());
					node.put("userId", userdata.getUserId());
					node.put("employeeName", userdata.getFirstName() + " " + userdata.getLastName());
					node.put("clBalance", leavebalance.get("CL").asDouble());
					node.put("elBalance", leavebalance.get("EL").asDouble());
					node.put("slBalance", leavebalance.get("SL").asDouble());
					leaverecord.add(node);
					System.out.println("for end");
				}

			}

			responseData.put("status", "success");
			responseData.put("code", response.getStatus());
			responseData.put("message", "success");
			responseData.set("payload", leaverecord);

		} catch (Exception e) {
			responseData.put("status", "Failed");
			responseData.put("code", response.getStatus());
			responseData.put("message", "Exception:" + e);
			e.printStackTrace();
		}

		return responseData;
	}

	@PostMapping("/getLeaveList")
	public JsonNode getLeaveList(@RequestBody JsonNode requestdata, HttpServletResponse response) {
		ObjectNode responseData = objectMapper.createObjectNode();

		String startDate = requestdata.get("startdate").asText();
		String endDate = requestdata.get("endDate").asText();
		String leaveType = requestdata.get("leaveType").asText();

		try {

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			Date startDate1 = formatter.parse(startDate);
			Date endDate1 = formatter.parse(endDate);

			ArrayNode leaverecord = objectMapper.createArrayNode();
			List<LeaveModel> leavelist = new ArrayList<LeaveModel>();
			if (leaveType.equals(null) || leaveType.equals("")) {
				leavelist = attendanceService.getLeavelist(startDate1, endDate1);
				System.out.println("if:" + leavelist.size());
			} else {
				leavelist = attendanceService.getLeavelist(startDate1, endDate1, leaveType.toUpperCase());
				System.out.println("else:" + leavelist.size());
			}

			for (LeaveModel leave : leavelist) {

				ObjectNode leavenode = objectMapper.createObjectNode();
				UserModel user = userservice.getUserDetailsById(leave.getUser().getUserId());
				leavenode.put("empId", user.getEmpId());
				leavenode.put("employeeName", user.getFirstName() + " " + user.getLastName());
				leavenode.put("leaveType", leave.getLeaveType().toLowerCase());
				leavenode.put("leaveCount", leave.getLeaveCount());
				leavenode.put("leaveFrom", leave.getLeaveFrom().toString());
				leavenode.put("leaveTo", leave.getLeaveTo().toString());
				leavenode.put("leaveReason", leave.getLeaveReason());
				leavenode.put("leaveId", leave.getLeaveId());
				leavenode.put("userId", user.getUserId());
				leavenode.put("leaveStatus", leave.getStatus());
				leaverecord.add(leavenode);
			}

			responseData.put("status", "success");
			responseData.put("code", response.getStatus());
			responseData.put("message", "success");
			responseData.set("payload", leaverecord);

		} catch (Exception e) {
			responseData.put("status", "Failed");
			responseData.put("code", response.getStatus());
			responseData.put("message", "Exception:" + e);
			e.printStackTrace();
		}

		return responseData;
	}

//	for editing leave 
	@PostMapping("/editLeaveMarking")
	public ObjectNode setEditLeaveMarking(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) {
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		try {

			TimeZone zone = TimeZone.getTimeZone("MST");
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			outputFormat.setTimeZone(zone);

			Long userId = requestdata.get("userId").asLong();
			UserModel user = userservice.getUserDetailsById(userId);

			LocalDate now = LocalDate.now();

			ArrayNode leaveNode = (ArrayNode) requestdata.get("leaveList");
			System.out.println("leaveNode size : " + leaveNode.size());
			for (JsonNode node : leaveNode) {
				LeaveModel leaveModel = new LeaveModel();
				leaveModel.setLeaveId(node.get("leaveId").asLong());
				leaveModel.setUser(user);
				leaveModel.setStatus(requestdata.get("status").asText());
				leaveModel.setAppliedDate(outputFormat.parse(now.toString()));
				leaveModel.setLeaveReason(node.get("reason").asText());
				double leaveCount = node.get("count").asDouble();
				leaveModel.setLeaveCount(leaveCount);
				leaveModel.setLeaveType(node.get("leaveType").asText().toUpperCase());

				String startDate = node.get("startdate").asText();
				String endDate = node.get("enddate").asText();
				System.out.println("start:" + startDate);
				if (!startDate.isEmpty()) {
					leaveModel.setLeaveFrom(outputFormat.parse(startDate));
				}
				if (!endDate.isEmpty()) {
					leaveModel.setLeaveTo(outputFormat.parse(endDate));
				}

				LocalDate startdate = LocalDate.parse(startDate);
				LocalDate enddate = LocalDate.parse(endDate);

				// balance check
				LeaveBalanceModel lBalance = new LeaveBalanceModel();
				int year = startdate.getYear();
				lBalance.setYear(year);
				lBalance.setUser(leaveModel.getUser());
				int month = startdate.getMonthValue();
				System.out.println("month :" + month);
				int quarterleave = 0;
				if (month <= 3) {
					quarterleave = 1;
				} else if (month <= 6 && month > 3) {
					quarterleave = 2;
				} else if (month <= 9 && month > 6) {
					quarterleave = 3;
				} else if (month <= 12 && month > 9) {
					quarterleave = 4;

				}

				lBalance.setQuarter(quarterleave);
				System.out.println("quat:" + lBalance.getQuarter() + "yera:" + lBalance.getYear() + " :user"
						+ lBalance.getUser().getUserId());
				LeaveBalanceModel leavebal = attendanceService.getUserLeaveBalance(lBalance.getQuarter(),
						lBalance.getYear(), lBalance.getUser().getUserId());
				System.out.println("sl:" + leavebal.getSlBalance() + " el:" + leavebal.getElBalance() + " cl:"
						+ leavebal.getClBalance());

				double leavebalance = 0;
				LeaveModel checkleavevalue = attendanceService.Leavedetails(leaveModel.getLeaveId());
				if (checkleavevalue.getLeaveType().equals("CL"))
					leavebal.setClBalance(leavebal.getClBalance() + checkleavevalue.getLeaveCount());
				else if (checkleavevalue.getLeaveType().equals("EL"))
					leavebal.setElBalance(leavebal.getElBalance() + checkleavevalue.getLeaveCount());
				else if (checkleavevalue.getLeaveType().equals("SL"))
					leavebal.setSlBalance(leavebal.getSlBalance() + checkleavevalue.getLeaveCount());

				if (leaveModel.getLeaveType().equals("CL")) {

					if (leavebal.getClBalance() >= leaveModel.getLeaveCount()) {

						leavebalance = leavebal.getClBalance();
						leavebalance = leavebalance - leaveModel.getLeaveCount();
						System.out.println("new bal:" + leavebalance + " count:" + leaveModel.getLeaveCount());
						leavebal.setClBalance(leavebalance);
					}
				} else if (leaveModel.getLeaveType().equals("SL")) {
					if (leavebal.getSlBalance() >= leaveModel.getLeaveCount()) {
						leavebalance = leavebal.getSlBalance();
						leavebalance = leavebalance - leaveModel.getLeaveCount();
						System.out.println("new bal:" + leavebalance + " count:" + leaveModel.getLeaveCount());
						leavebal.setSlBalance(leavebalance);
					}
				} else if (leaveModel.getLeaveType().equals("EL")) {
					if (leavebal.getElBalance() >= leaveModel.getLeaveCount()) {

						leavebalance = leavebal.getElBalance();
						leavebalance = leavebalance - leaveModel.getLeaveCount();
						leavebal.setElBalance(leavebalance);
					}
					System.out.println("new bal:" + leavebalance + " count:" + leaveModel.getLeaveCount());

				}
				attendanceService.setLeaveBalance(leavebal);

				attendanceService.saveLeaveMarking(leaveModel);
				jsonDataRes.put("status", "success");
				jsonDataRes.put("message", "successfully saved.");
				jsonDataRes.put("code", httpstatus.getStatus());

			}

		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;

	}

//	for calculating balance leave
	@PostMapping("/getleaveBalance")
	public ObjectNode setLeaveBalance(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) {
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		try {

			Long userId = requestdata.get("userId").asLong();
			System.out.println("userID:" + userId);
			UserModel user = userservice.getUserDetailsById(userId);

			if (user != null) {
				LocalDate now = LocalDate.now();
				int year = now.getYear();
				LeaveBalanceModel lBalance = new LeaveBalanceModel();
				lBalance.setYear(year);
				lBalance.setUser(user);
				int month = now.getMonthValue();
				double currEL = requestdata.get("el").asDouble();
				double currSL = requestdata.get("sl").asDouble();
				double currCL = requestdata.get("cl").asDouble();

				System.out.println("month :" + month);

				int quarterleave = 0;
				if (month <= 3) {
					quarterleave = 1;
				} else if (month <= 6 && month > 3) {
					quarterleave = 2;
				} else if (month <= 9 && month > 6) {
					quarterleave = 3;
				} else if (month <= 12 && month > 9) {
					quarterleave = 4;
				}

				lBalance.setQuarter(quarterleave);
				System.out.println("user id" + lBalance.getUser().getUserId());
				System.out.println("quarter:" + lBalance.getQuarter() + "yera:" + lBalance.getYear());

				double clBalance = 0, elBalance = 0, slBalance = 0;
				if (lBalance.getQuarter() > 1) {
					ObjectNode balanceList = attendanceService.getLeavebalanceData(lBalance.getUser().getUserId(),
							lBalance.getQuarter() - 1, lBalance.getYear());
					clBalance = balanceList.get("CL").asDouble();
					elBalance = balanceList.get("SL").asDouble();
					slBalance = balanceList.get("EL").asDouble();
				}

				clBalance = clBalance + currCL;
				elBalance = elBalance + currEL;
				slBalance = slBalance + currSL;

				Boolean checkvalue = attendanceService.checkLeaveBalance(lBalance);
				if (checkvalue) {
					LeaveBalanceModel leavedata = attendanceService.getUserLeaveBalance(lBalance.getQuarter() - 1,
							lBalance.getYear(), lBalance.getUser().getUserId());
					System.out.println("leave id;" + leavedata.getLeavebalanceId());
					leavedata.setClBalance(clBalance);
					leavedata.setElBalance(elBalance);
					leavedata.setSlBalance(slBalance);
					leavedata.setQuarter(lBalance.getQuarter());
					leavedata.setYear(lBalance.getYear());
					attendanceService.setLeaveBalance(leavedata);
//						attendanceService.deleteBalance(lBalance);
					jsonDataRes.put("status", "success");
					jsonDataRes.put("message", "successfully saved.");
					jsonDataRes.put("code", httpstatus.getStatus());
				} else {
					jsonDataRes.put("status", "failure");
					jsonDataRes.put("code", httpstatus.getStatus());
					jsonDataRes.put("message", "duplicate entry");
				}

			} else {
				jsonDataRes.put("status", "failure");
				jsonDataRes.put("code", httpstatus.getStatus());
				jsonDataRes.put("message", "Not an active user");
			}

		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;

	}

//	for cancelling leave
	@PostMapping("/cancelLeaveMarking")
	public ObjectNode setcancelLeaveMarking(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) {
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		boolean statusflag = false;
		try {

			Long userId = requestdata.get("userId").asLong();
			UserModel user = userservice.getUserDetailsById(userId);

			LocalDate now = LocalDate.now();
			LeaveModel leaveModel = new LeaveModel();
			leaveModel.setLeaveId(requestdata.get("leaveId").asLong());
			leaveModel.setUser(user);
			System.out.println("leaveid:" + leaveModel.getLeaveId() + " userId:" + leaveModel.getUser().getUserId());
			LeaveModel leaveDetails = attendanceService.getLeaveDetails(leaveModel.getLeaveId());
			double clCount = 0, elCount = 0, slCount = 0;
			if (leaveDetails.getLeaveType().equals("CL"))
				clCount = clCount + leaveDetails.getLeaveCount();
			else if (leaveDetails.getLeaveType().equals("EL"))
				elCount = elCount + leaveDetails.getLeaveCount();
			else if (leaveDetails.getLeaveType().equals("SL"))
				slCount = slCount + leaveDetails.getLeaveCount();

			System.out.println("CL:" + clCount + "EL:" + elCount + "SL:" + slCount);
			LeaveBalanceModel lBalance = new LeaveBalanceModel();

			int year = now.getYear();
			lBalance.setYear(year);
			lBalance.setUser(leaveModel.getUser());
			int month = now.getMonthValue();
			System.out.println("month :" + month);
			int quarterleave = 0;
			if (month <= 3) {
				quarterleave = 1;
			} else if (month <= 6 && month > 3) {
				quarterleave = 2;
			} else if (month <= 9 && month > 6) {
				quarterleave = 3;
			} else if (month <= 12 && month > 9) {
				quarterleave = 4;

			}

			lBalance.setQuarter(quarterleave);
			System.out.println("quat:" + lBalance.getQuarter() + "yera:" + lBalance.getYear() + " :user"
					+ lBalance.getUser().getUserId());
			LeaveBalanceModel leavebal = attendanceService.getUserLeaveBalance(lBalance.getQuarter(),
					lBalance.getYear(), lBalance.getUser().getUserId());
			System.out.println("balId:" + leavebal.getLeavebalanceId() + " sl:" + leavebal.getSlBalance() + " el:"
					+ leavebal.getElBalance() + " cl:" + leavebal.getClBalance());

			leavebal.setClBalance(leavebal.getClBalance() + clCount);
			leavebal.setElBalance(leavebal.getElBalance() + elCount);
			leavebal.setSlBalance(leavebal.getSlBalance() + slCount);

			attendanceService.setLeaveBalance(leavebal);

			attendanceService.deleteLeaveMarking(leaveModel.getLeaveId());
			statusflag = true;
			jsonDataRes.put("status", "success");
			jsonDataRes.put("message", "successfully deleted");
			jsonDataRes.put("code", httpstatus.getStatus());

		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);

		}
		jsonDataRes.put("statusflag", statusflag);

		return jsonDataRes;

	}

	@PutMapping("/getUserBalanceLeave/{userId}")
	public JsonNode getUserBalanceLeave(@PathVariable Long userId, HttpServletResponse response) {
		ObjectNode responseData = objectMapper.createObjectNode();

		try {
			LocalDate now = LocalDate.now();
			int quarter = 0;
			int monthNumber = now.getMonthValue();
			int year = now.getYear();

			if (monthNumber >= 1 && monthNumber <= 3)
				quarter = 1;
			else if (monthNumber >= 4 && monthNumber <= 6)
				quarter = 2;
			else if (monthNumber >= 7 && monthNumber <= 9)
				quarter = 3;
			else if (monthNumber >= 10 && monthNumber <= 12)
				quarter = 4;
			ObjectNode leaveBalanceNode = attendanceService.getLeavebalanceData(userId, quarter, year);

			ObjectNode leavebal = objectMapper.createObjectNode();
			leavebal.put("cl", leaveBalanceNode.get("CL").asDouble());
			leavebal.put("el", leaveBalanceNode.get("EL").asDouble());
			leavebal.put("sl", leaveBalanceNode.get("SL").asDouble());
			leavebal.put("balanceId", leaveBalanceNode.get("balanceId").asLong());

			responseData.put("status", "success");
			responseData.put("code", response.getStatus());
			responseData.put("message", "success");
			responseData.set("payload", leavebal);

		} catch (Exception e) {
			responseData.put("status", "Failed");
			responseData.put("code", response.getStatus());
			responseData.put("message", "Exception:" + e);
			e.printStackTrace();
		}

		return responseData;
	}

	@PostMapping(value = "/adduserleavesummary")
	public JsonNode adduser(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {

		ObjectNode responsedata = objectMapper.createObjectNode();
		int responseflag = 0;
		// setting values to usermodel
		UserLeaveSummary userLeaveSummary = new UserLeaveSummary();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			long leaveSummaryId = 0;
			long userId = requestdata.get("userId").asLong();
			long userLeaveSummaryId = requestdata.get("userLeaveSummaryId").asLong();
			if(!requestdata.get("leaveDate").asText().isEmpty()) {
				userLeaveSummary.setLeaveDate(df.parse(requestdata.get("leaveDate").asText()));
			}
			if(!requestdata.get("leaveType").asText().isEmpty()) {
				userLeaveSummary.setLeaveType(requestdata.get("leaveType").asText());
			}
			UserModel newuser  = userservice.getUserDetailsById(userId);
			userLeaveSummary.setUser(newuser);
            boolean isExist = attendanceService.isExist(userLeaveSummaryId);
            if(isExist)
			{
				UserLeaveSummary userLeaveData = attendanceService.getLeaveDetailsById(userLeaveSummaryId);
				if(requestdata.get("leaveDate").asText().isEmpty() && requestdata.get("leaveType").asText().isEmpty()){

					attendanceService.removeUserLeaveSummary(userLeaveData);
					responsedata.put("status", "success");
					responsedata.put("message", "record deleted succesfully");
					responsedata.put("leaveSummaryId", userLeaveSummaryId);

				}
				else
				{

					userLeaveData.setLeaveType(requestdata.get("leaveType").asText());
					userLeaveData.setLeaveDate(df.parse(requestdata.get("leaveDate").asText()));
					attendanceService.addUserLeaveSummary(userLeaveData);
					responsedata.put("status", "success");
					responsedata.put("message", "record updated succesfully");
					responsedata.put("leaveSummaryId", userLeaveSummaryId);

				}
			}
            else {
			 leaveSummaryId	= attendanceService.addUserLeaveSummary(userLeaveSummary);
				responsedata.put("status", "success");
				responsedata.put("message", "record insertion completed");
				responsedata.put("leaveSummaryId", leaveSummaryId);
			}


			responsedata.put("code", servletresponse.getStatus());


		} catch (Exception e) {
			responsedata.put("status", "Failed");
			responsedata.put("message", "Exception : " + e);
			responsedata.put("code", servletresponse.getStatus());
		}

		return responsedata;
	}


	@PostMapping(value = "/getUserLeaveSummaryList")
	public ObjectNode getUserLeaveSummaryList(@RequestBody ObjectNode requestData,HttpServletResponse httpstatus) {

		long userId = requestData.get("userId").asLong();
		ArrayNode jsonArray = objectMapper.createArrayNode();
		ObjectNode jsonData = objectMapper.createObjectNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");


		try {

			boolean isUserExist = attendanceService.isUserExist(userId);
			if(isUserExist) {
				List<UserLeaveSummary> userLeaveSummary = attendanceService.getUserLeaveSummaryList(userId);
				String userName = "";

				for (UserLeaveSummary item : userLeaveSummary) {


					ObjectNode jsonObject = objectMapper.createObjectNode();
					userName = item.getUser().getFirstName() + " " + item.getUser().getLastName();
					jsonObject.put("userLeaveSummaryId", item.getLeaveSummaryId());
					//jsonObject.put("userName",item.getUser().getFirstName()+" "+item.getUser().getLastName());
					//System.out.println(item.getLeaveDate());
					jsonObject.put("leaveDate", df.format(item.getLeaveDate()).toString());
					jsonObject.put("leaveType", item.getLeaveType());
					jsonArray.add(jsonObject);
				}
				jsonData.put("userId",userId);
				jsonData.put("userName", userName);
				jsonData.set("leaveSummaryList", jsonArray);

			}
			jsonDataRes.put("status", "success");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "success ");
			jsonDataRes.set("data", jsonData);

		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;

	}
	@PostMapping("/getHolidayListByRegionId")
	public ObjectNode getHolidayListByRegionId(@RequestBody ObjectNode requestdata,HttpServletResponse httpstatus) {

		ArrayNode jsonArray = objectMapper.createArrayNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		Long region_Id = null;
		String month = null;
		String year = null;
		System.out.println("month _________________"+month);
		if (requestdata.get("region_Id") != null && requestdata.get("region_Id").asText() != "") {
			region_Id = Long.valueOf(requestdata.get("region_Id").toString());
			System.out.println("region_Id _________________"+region_Id);
		}
		
		if (requestdata.get("month") != null && requestdata.get("month").asText() != "") {
			month = requestdata.get("month").asText();
			System.out.println("month _________________"+month);
			long m =  Long.parseLong(month);
			 
			if(m < 10) {
			
				month = "0"+month;
				
			 }
			
		}
		if (requestdata.get("year") != null && requestdata.get("year").asText() != "") {
			year = requestdata.get("year").asText();
		}
		System.out.println("region_Id _________________"+region_Id);
		try {
			List<Object[]> holidayList = attendanceService.getHolidayListByRegionId(region_Id,month,year);
			System.out.println("holidayList : " + holidayList.size());
//			ArrayNode node = objectMapper.convertValue(attendanceService.getHolidayList(), ArrayNode.class);
			for (Object[] item : holidayList) {
				ObjectNode holidayNode = objectMapper.createObjectNode();
				String holidayId = item[1].toString();
				String date = item[0].toString();
				String day = item[2].toString();
				String holidayName = item[3].toString();
				String holidayType = item[4].toString();
				holidayNode.put("holidayId", holidayId);
				holidayNode.put("date", date);
				holidayNode.put("day", day);
				holidayNode.put("holidayName", holidayName);
				holidayNode.put("holidayType", holidayType);
				holidayNode.put("region_id", item[5].toString());
				holidayNode.put("region_name", item[6].toString());
				jsonArray.add(holidayNode);

			}
			jsonDataRes.set("data", jsonArray);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("message", "success. ");
			jsonDataRes.put("code", httpstatus.getStatus());

		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;

	}
	
	@GetMapping("/getHolidayList/{holiday_id}")
	public ObjectNode getHolidayList(HttpServletResponse httpstatus,@PathVariable("holiday_id") Long holiday_id) {

		ArrayNode jsonArray = objectMapper.createArrayNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();

		try {
			List<Object[]> holidayList = attendanceService.getHolidayDetails(holiday_id);
			System.out.println("holidayList : " + holidayList.size());
//			ArrayNode node = objectMapper.convertValue(attendanceService.getHolidayList(), ArrayNode.class);
			for (Object[] item : holidayList) {
				ObjectNode holidayNode = objectMapper.createObjectNode();
				String holidayId = item[1].toString();
				String date = item[0].toString();
				String day = item[2].toString();
				String holidayName = item[3].toString();
				String holidayType = item[4].toString();
				holidayNode.put("holidayId", holidayId);
				holidayNode.put("date", date);
				holidayNode.put("day", day);
				holidayNode.put("holidayName", holidayName);
				holidayNode.put("holidayType", holidayType);
				holidayNode.put("region_id", item[5].toString());
				holidayNode.put("region_name", item[6].toString());
				jsonArray.add(holidayNode);

			}
			jsonDataRes.set("data", jsonArray);
			jsonDataRes.put("status", "success");
			jsonDataRes.put("message", "success. ");
			jsonDataRes.put("code", httpstatus.getStatus());

		} catch (Exception e) {
			jsonDataRes.put("status", "failure");
			jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;

	}
}
