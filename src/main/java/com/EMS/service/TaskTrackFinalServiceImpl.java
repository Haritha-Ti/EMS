package com.EMS.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ValueRange;
import java.util.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.time.YearMonth;

import com.EMS.model.*;
import com.EMS.repository.*;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.EMS.dto.MailDomainDto;
import com.EMS.exceptions.DuplicateEntryException;
import com.EMS.utility.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class TaskTrackFinalServiceImpl implements TaskTrackFinalService {

	@Autowired
	TaskTrackApprovalFinalRepository taskTrackApprovalFinalRepository;

	@Autowired
	TasktrackRepository tasktrackRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	@Autowired
	TimeTrackApprovalRepository timeTrackApprovalRepository;

	@Autowired
	TaskTrackFinalJPARepository taskTrackFinalJPARepository;
//	For Task track Model

	@Autowired
	TaskTrackApprovalLevel2Repository timeTrackApprovalLevel2;

	@Autowired
	TaskTrackFinanceRepository taskTrackFinanceRepository;

	@Autowired
	TasktrackApprovalService tasktrackApprovalService;

	@Autowired
	ProjectService projectService;

	@Autowired
	ProjectAllocationRepository projectAllocationRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProjectRepository project_repositary;

	@Autowired
	ActivityLogRepository activitylogrepository;

	@Autowired
	TimeTrackApprovalJPARepository timeTrackApprovalJPARepository;

	@Autowired
	TaskTrackCorrectionRepository taskTrackCorrectionRepository;

	@Autowired
	TaskTrackRejectionRepository taskTrackRejectionRepository;

	@Autowired
    private Configuration freemarkerConfig;
	
	@Autowired
	private EmailNotificationService emailNotificationService;
	
	@Value("${FINANCE_MAIL}")
	private String financeMail;

	@Override
	public Boolean checkIsUserExists(Long id) {
		Boolean exist = taskTrackFinalJPARepository.existsByUser(id);
		return exist;
	}

	private List<Object[]> getUserListByProject(Long id, Date startDate, Date endDate, Long projectId) {
		List<Object[]> userTaskList = taskRepository.getUserListByProject(id, startDate, endDate, projectId);
		return userTaskList;
	}

	/*
	 * public List<TaskTrackApprovalLevel2> getUserListForApprovalLevel2(Long
	 * id,Long projectId,Integer monthIndex,Integer yearIndex) {
	 *
	 * List<TaskTrackApprovalLevel2> userList =
	 * timeTrackApprovalRepository.getUserListForApprovalLevel2(id,projectId,
	 * monthIndex,yearIndex);
	 *
	 * return userList; }
	 */
	@Override
	public TaskTrackApprovalFinal findById(Long billableId) {
		TaskTrackApprovalFinal taskTrackApproval = taskTrackFinalJPARepository.getOne(billableId);
		return taskTrackApproval;
	}

	@Override
	public TaskTrackApprovalFinal updateData(TaskTrackApprovalFinal taskTrackApproval) {
		return taskTrackFinalJPARepository.save(taskTrackApproval);

	}

	@Override
	public TaskTrackApprovalFinal save(TaskTrackApprovalFinal taskTrackApproval) {
		return taskTrackFinalJPARepository.save(taskTrackApproval);
	}

	@Override
	public List<Object> getForwardedDate(Long projectId, Long userId, int intMonth, int years) {
		// TODO Auto-generated method stub
		return taskTrackFinalJPARepository.getForwardedDate(projectId, userId, intMonth, years);
	}

	@Override
	public List<Object> getForwardedDateLevel2(Long projectId, Long userId, int intMonth, int year) {
		// TODO Auto-generated method stub
		return timeTrackApprovalLevel2.getForwardedDateLevel2(projectId, userId, intMonth, year);
	}

	public ArrayList<JSONObject> getFinanceDataByProject(int month, int year, Long projectId) {

		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		ArrayList<JSONObject> resultData = new ArrayList<JSONObject>();
		List<Object[]> financeData = taskTrackApprovalFinalRepository.getFinanceDataByProject(month, year, projectId);
		String intmonth;
		if (month < 10) {
			intmonth = "0" + month;
		} else {
			intmonth = String.valueOf(month);
		}
		for (Object[] item : financeData) {
			JSONObject node = new JSONObject();
			List<JSONObject> billableArray = new ArrayList<>();
			List<JSONObject> userArray = new ArrayList<>();

			node.put("userId", item[0]);
			node.put("firstName", item[1]);
			node.put("lastName", item[2]);
			node.put("status", item[3]);
			for (int i = 1; i <= daysInMonth; i++) {
				String j;
				if (i < 10) {
					j = "0" + i;
				} else {
					j = String.valueOf(i);
				}
				JSONObject billableNode = new JSONObject();
				billableNode.put(year + "-" + intmonth + "-" + j, item[i + 3]);
				billableArray.add(billableNode);
			}
			node.put("billable", billableArray);
			resultData.add(node);
		}

		return resultData;
	}

	public ArrayList<JSONObject> getFinanceDataByUser(int month, int year, Long userId) {

		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		ArrayList<JSONObject> resultData = new ArrayList<JSONObject>();
		List<Object[]> financeData = taskTrackApprovalFinalRepository.getFinanceDataByUser(month, year, userId);
		String intmonth;
		if (month < 10) {
			intmonth = "0" + month;
		} else {
			intmonth = String.valueOf(month);
		}
		for (Object[] item : financeData) {
			JSONObject node = new JSONObject();
			List<JSONObject> billableArray = new ArrayList<>();
			node.put("projectId", item[0]);
			node.put("projectName", item[1]);
			node.put("status", item[2]);
			for (int i = 1; i <= daysInMonth; i++) {
				String j;
				if (i < 10) {
					j = "0" + i;
				} else {
					j = String.valueOf(i);
				}
				JSONObject billableNode = new JSONObject();
				billableNode.put(year + "-" + intmonth + "-" + j, item[i + 2]);
				billableArray.add(billableNode);
			}
			node.put("billable", billableArray);
			resultData.add(node);
		}

		return resultData;
	}

	public ArrayList<JSONObject> getFinanceDataByUserAndProject(int month, int year, Long userId, Long projectId) {

		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		ArrayList<JSONObject> resultData = new ArrayList<JSONObject>();
		List<Object[]> financeData = taskTrackApprovalFinalRepository.getFinanceDataByUserAndProject(month, year, userId,
				projectId);
		String intmonth;
		if (month < 10) {
			intmonth = "0" + month;
		} else {
			intmonth = String.valueOf(month);
		}
		for (Object[] item : financeData) {
			JSONObject node = new JSONObject();
			List<JSONObject> billableArray = new ArrayList<>();
			node.put("projectId", item[0]);
			node.put("projectName", item[1]);
			node.put("userId", item[2]);
			node.put("firstName", item[3]);
			node.put("lastName", item[4]);
			node.put("status", item[5]);
			for (int i = 1; i <= daysInMonth; i++) {
				String j;
				if (i < 10) {
					j = "0" + i;
				} else {
					j = String.valueOf(i);
				}
				JSONObject billableNode = new JSONObject();
				billableNode.put(year + "-" + intmonth + "-" + j, item[i + 5]);
				billableArray.add(billableNode);
			}
			node.put("billable", billableArray);
			resultData.add(node);
		}

		return resultData;
	}

	@Override
	public List<Object[]> getForwardedDates(Long projectId, Long userId, int intMonth, int yearIndex) {
		// TODO Auto-generated method stub
		return taskTrackFinalJPARepository.getForwardedDates(projectId, userId, intMonth, yearIndex);
	}

	public JSONObject checkPreviousTimeSheetsareClosed(int month, int year, Long projectId, Long approverId) {

		UserModel user = userRepository.getOne(approverId);
		JSONObject jsonDataRes = new JSONObject();
		String message = null;
		Boolean status = true;

		Long role = user.getRole().getroleId();
		// int prevmonth = month-1;
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		calendar.add(Calendar.MONTH, -1);
		int lastday = calendar.getActualMaximum(Calendar.DATE);
		calendar.set(Calendar.DATE, lastday);
		Date lastDate = calendar.getTime();
		Integer prevMonth = calendar.get(Calendar.MONTH);
		Integer prevMonthYear = calendar.get(Calendar.YEAR);
		Long approverrowcount = null;
		String fromdate = prevMonthYear + "-" + ((prevMonth < 10) ? "0" + prevMonth : "" + prevMonth) + "-01";
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
		LocalDate date = LocalDate.parse(fromdate, dateFormat);
		ValueRange range = date.range(ChronoField.DAY_OF_MONTH);
		Long max = range.getMaximum();
		String todate = String.format("%s-%s-%d", prevMonthYear, prevMonth, max);
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null;
		Date endDate = null;

		try {
			startDate = outputFormat.parse(fromdate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			endDate = outputFormat.parse(todate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int allocated = tasktrackRepository.checkprojectallocated(projectId, startDate, endDate);
		System.out.println("projectId" + projectId + "startdate" + startDate + "enddate" + endDate);
		if (allocated > 0) {
			int expectedRows = allocated * 4;
			System.out.println("expected rows" + expectedRows);
			System.out.println("allocated rows" + allocated);
			if (role == 2)

			{
				ProjectModel projectData = projectRepository.getProjectDetails(projectId);
				approverrowcount = taskTrackFinalJPARepository.getCountOfRows(prevMonth, prevMonthYear, projectId);
				if (expectedRows == approverrowcount) {
					if (projectData.getOnsite_lead() == null) {
						if (expectedRows == taskTrackFinanceRepository.getCountOfRowsHM(prevMonth, prevMonthYear,
								projectId)) {
							if (expectedRows == taskTrackFinanceRepository.getCountOfRowsFM(prevMonth, prevMonthYear,
									projectId)) {
								message = "No pending timesheet found";
								status = true;

							} else {
								message = "Pending previous full month timesheet found";
								status = false;
							}
						} else {
							message = "Pending previous half month timesheet found";
							status = false;
						}
					} else {
						if (expectedRows == timeTrackApprovalLevel2.getCountOfRowsHM(prevMonth, prevMonthYear,
								projectId)) {
							if (expectedRows == timeTrackApprovalLevel2.getCountOfRowsFM(prevMonth, prevMonthYear,
									projectId)) {
								message = "No pending timesheet found";
								status = true;
							} else {
								message = "Pending previous full month timesheet found";
								status = false;
							}
						} else {
							message = "Pending previous half month timesheet found";
							status = false;
						}

					}
				} else {
					message = "Pending previous month timesheet approval found";
					status = false;
				}
			} else if (role == 7) {
				approverrowcount = timeTrackApprovalLevel2.getCountOfRowsHM(prevMonth, prevMonthYear, projectId);
				if (expectedRows == approverrowcount) {
					if (approverrowcount == taskTrackFinanceRepository.getCountOfRowsHM(prevMonth, prevMonthYear,
							projectId)) {
						if (approverrowcount == taskTrackFinanceRepository.getCountOfRowsFM(prevMonth, prevMonthYear,
								projectId)) {
							message = "No pending timesheet found";
							status = true;
						} else {
							message = "Pending previous full month timesheet found";
							status = false;
						}
					} else {
						message = "Pending previous half month timesheet found";
						status = false;
					}

				} else {
					message = "Pending previous month timesheet approval found";
					status = false;
				}
			}
		} else {
			message = "No pending timesheet found";
			status = true;

		}
		status = true;
		message = null;
		jsonDataRes.put("data", status);
		jsonDataRes.put("status", "success");
		jsonDataRes.put("message", message);
		jsonDataRes.put("month", prevMonth);
		jsonDataRes.put("year", prevMonthYear);

		return jsonDataRes;
	}

	public ObjectNode mailRejectMessageToLevel1(Long projectId, String message) {

		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		// find level1 approver

		ProjectModel project = projectRepository.getProjectDetails(projectId);

		try {
			String mail = sendMailTolevel1(message, project.getProjectOwner(), project.getOnsite_lead());
			// System.out.println("------------>"+mail);
			jsonDataRes.put("status", "success");
			// jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "Cleared Data");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jsonDataRes.put("status", "failed");
			// jsonDataRes.put("code", e.printStackTrace());
			jsonDataRes.put("message", "Mail sent successfully");
		}

		return jsonDataRes;
	}

	public String sendMailTolevel1(String messages, UserModel user, UserModel lead)
			throws AddressException, MessagingException {

		String subject = "Rejected Timesheet";
		StringBuilder mailBody = new StringBuilder("Hi " + user.getFirstName() + " " + user.getLastName() + ",");
		mailBody.append("<br/><br/>The time sheet submitted recently has been rejected by level2 approver "
				+ lead.getFirstName() + " " + lead.getLastName() + " please find the below comments from them:");
		mailBody.append("<br/><br/>" + messages + "</a>");
		// mailBody.append("<br/><br/>This link will expire in
		// "+Constants.EMAIL_TOKEN_EXP_DUR+" minutes");

		String to = user.getEmail();
		String from = "noreply@titechnologies.in";
		String host = "smtp.gmail.com";
		final String username = "noreply@titechnologies.in";
		final String password = "Noreply!@#";

		System.out.println("TLS Email Start");

		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);
		// SSL Port
		properties.put("mail.smtp.port", "465");
		// enable authentication
		properties.put("mail.smtp.auth", "true");
		// SSL Factory
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		// creating Session instance referenced to
		// Authenticator object to pass in
		// Session.getInstance argument
		Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			// override the getPasswordAuthentication
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		// javax.mail.internet.MimeMessage class is mostly
		// used for abstraction.
		MimeMessage message = new MimeMessage(session);

		// header field of the header.
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		// message.setText(mailBody);
		message.setContent(mailBody.toString(), "text/html");

		// Send message
		Transport.send(message);

		String msg = "Verification link has been successfully sent to your email \"" + to + "\"";
		System.out.println(msg);
		return msg;
	}

	public List<TaskTrackApprovalLevel2> getHalfMonthApprovedData(int monthIndex, int yearIndex, Long projectId) {
		return timeTrackApprovalLevel2.getHalfMonthApprovedData(monthIndex, yearIndex, projectId);
	}

	private ProjectModel getProjectDetails(Long projectId) {
		// TODO Auto-generated method stub
		// System.out.println("Here____________________________");
		return projectService.getProjectDetails(projectId);
	}

	/**
	 * @author sreejith.j
	 */
	@Override
	public void saveFinalHours(JSONObject requestData) throws Exception {

		// Obtain the data from request data
		Long billableId = null, nonBillableId = null, overtimeId = null, beachId = null, projectId = null, userId = null;
		Integer year = Integer.parseInt((String) requestData.get("year"));
		Integer month = (Integer) requestData.get("month");
		if (requestData.get("projectId") != null && requestData.get("projectId") != "") {
			projectId = Long.valueOf(requestData.get("projectId").toString());
		}
		if (requestData.get("userId") != null && requestData.get("userId") != "") {
			userId = Long.valueOf(requestData.get("userId").toString());
		}
		if (requestData.get("billableId") != null && requestData.get("billableId") != "") {
			billableId = Long.valueOf(requestData.get("billableId").toString());
		}
		if (requestData.get("nonBillableId") != null && requestData.get("nonBillableId") != "") {
			nonBillableId = Long.valueOf(requestData.get("nonBillableId").toString());
		}
		if (requestData.get("overtimeId") != null && requestData.get("overtimeId") != "") {
			overtimeId = Long.valueOf(requestData.get("overtimeId").toString());
		}
		if (requestData.get("beachId") != null && requestData.get("beachId") != "") {
			beachId = Long.valueOf(requestData.get("beachId").toString());
		}
		String date1 = (String) requestData.get("startDate");
		String date2 = (String) requestData.get("endDate");

		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		if (!date1.isEmpty()) {
			startDate = outputFormat.parse(date1);
		}
		if (!date2.isEmpty()) {
			endDate = outputFormat.parse(date2);
		}

		HashMap<String, Object> billableArray = new JSONObject();
		HashMap<String, Object> nonbillableArray = new JSONObject();
		HashMap<String, Object> overtimeArray = new JSONObject();
		HashMap<String, Object> beachArray = new JSONObject();

		UserModel user = userService.getUserDetailsById(userId);
		ProjectModel project = projectService.getProjectId(projectId);

		if (requestData.get("billable") != null && requestData.get("billable") != "") {
			billableArray = (HashMap<String, Object>) (requestData.get("billable"));
		}
		if (requestData.get("nonBillable") != null && requestData.get("nonBillable") != "") {
			nonbillableArray = (HashMap<String, Object>) requestData.get("nonBillable");
		}
		if (requestData.get("overtime") != null && requestData.get("overtime") != "") {
			overtimeArray = (HashMap<String, Object>) requestData.get("overtime");
		}
		if (requestData.get("beach") != null && requestData.get("beach") != "") {
			beachArray = (HashMap<String, Object>) requestData.get("beach");
		}

		Long billable_id = null;
		Long nonbillable_id = null;
		Long overtime_id = null;
		Long beach_id = null;

		Date current_date = new Date();
		Calendar current = Calendar.getInstance();
		current.setTime(current_date);
		int intCurrentMonth = 0;
		intCurrentMonth = (current.get(Calendar.MONTH) + 1);

		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		endCal.add(Calendar.DATE, -1);

		if (billableArray.size() > 0) {// Billable

			Calendar startCal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			startCal.setTime(startDate);
			double hours = 0;
			intMonth = (startCal.get(Calendar.MONTH) + 1);

			if (billableArray.size() > 0 && billableId == null) {
				billableId = taskTrackFinalJPARepository.getBillableIdForAUserForAProject(month, year, projectId,
						userId);
				if (billableId != null) {
					throw new DuplicateEntryException("Duplicate entry for billable.");
				}
			}
			if (nonbillableArray.size() > 0 && nonBillableId == null) {
				nonBillableId = taskTrackFinalJPARepository.getNonBillableIdForAUserForAProject(month, year, projectId,
						userId);
				if (nonBillableId != null) {
					throw new DuplicateEntryException("Duplicate entry for NonBillable.");
				}
			}
			if (overtimeArray.size() > 0 && overtimeId == null) {
				overtimeId = taskTrackFinalJPARepository.getOvertimeIdForAUserForAProject(month, year, projectId,
						userId);
				if (overtimeId != null) {
					throw new DuplicateEntryException("Duplicate entry for Overtime.");
				}
			}
			if (beachArray.size() > 0 && beachId == null) {
				beachId = taskTrackFinalJPARepository.getBeachIdForAUserForAProject(month, year, projectId,
						userId);
				if (beachId != null) {
					throw new DuplicateEntryException("Duplicate entry for Beach.");
				}
			}

			if (billableId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(billableId);

//					taskTrackApproval.setApprovedDate(endDate);
				if (taskTrackApproval != null) {

					int startDayOfMonth = startCal.get(Calendar.DATE);
					if (startCal.get(Calendar.DATE) < 16) {
						if (taskTrackApproval.getFirstHalfStatus()
								.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
								|| taskTrackApproval.getFirstHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
							taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
						} else {
							taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
						}
					}
					if (endCal.get(Calendar.DATE) > 15) {
						if (taskTrackApproval.getSecondHalfStatus()
								.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
								|| taskTrackApproval.getSecondHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
							taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
						} else {
							taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
						}
					}

					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = startCal.get(Calendar.DATE);
						String dateString = startCal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (billableArray.get(dateString) != null) {
							hours = Double.valueOf(billableArray.get(dateString).toString());
							setDayInCorrespondingModel(taskTrackApproval, i, hours);

						}
						startCal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					billable_id = taskTrackApproval.getId();
				} else {
					throw new Exception("TaskTrack data not found for given billable id.");
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Billable");

				if (startCal.get(Calendar.DATE) < 16) {
					if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
							|| taskTrackApproval.getFirstHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
					} else {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
					}
				}
				if (endCal.get(Calendar.DATE) > 15) {
					if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
							|| taskTrackApproval.getSecondHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
					} else {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
					}
				}

				taskTrackApproval.setProject(project);

				int startDayOfMonth = startCal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (startCal.get(Calendar.MONTH) + 1);
					intday = startCal.get(Calendar.DATE);
					String dateString = startCal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (billableArray.get(dateString) != null) {
						hours = Double.valueOf(billableArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);
					}
					startCal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal billable = save(taskTrackApproval);
				billable_id = billable.getId();

			}
		}

		/**************************************************************/

		if (nonbillableArray.size() > 0) {// Non-Billable

			Calendar startCal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			startCal.setTime(startDate);
			double hours = 0;
			intMonth = (startCal.get(Calendar.MONTH) + 1);

			if (nonBillableId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(nonBillableId);

//					taskTrackApproval.setApprovedDate(endDate);

				if (startCal.get(Calendar.DATE) < 16) {
					if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
							|| taskTrackApproval.getFirstHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
					} else {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
					}
				}
				if (endCal.get(Calendar.DATE) > 15) {
					if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
							|| taskTrackApproval.getSecondHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
					} else {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
					}
				}
				if (taskTrackApproval != null) {

					int startDayOfMonth = startCal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = startCal.get(Calendar.DATE);
						String dateString = startCal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (nonbillableArray.get(dateString) != null) {
							hours = Double.valueOf(nonbillableArray.get(dateString).toString());

							setDayInCorrespondingModel(taskTrackApproval, i, hours);

						}
						startCal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					nonbillable_id = taskTrackApproval.getId();
				} else {
					throw new Exception("TaskTrack data not found for given non-billable id.");
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Non-Billable");

				if (startCal.get(Calendar.DATE) < 16) {
					if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
							|| taskTrackApproval.getFirstHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
					} else {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
					}
				}
				if (endCal.get(Calendar.DATE) > 15) {
					if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
							|| taskTrackApproval.getSecondHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
					} else {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
					}
				}

				taskTrackApproval.setProject(project);

				int startDayOfMonth = startCal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (startCal.get(Calendar.MONTH) + 1);
					intday = startCal.get(Calendar.DATE);
					String dateString = startCal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (nonbillableArray.get(dateString) != null) {
						hours = Double.valueOf(nonbillableArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);

					}
					startCal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal nonbillable = save(taskTrackApproval);
				nonbillable_id = nonbillable.getId();

			}

		}
		/****************************************************************************************/

		/*****************************************************************************************/

		if (overtimeArray.size() > 0) {// OverTime

			Calendar startCal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			startCal.setTime(startDate);
			double hours = 0;
			intMonth = (startCal.get(Calendar.MONTH) + 1);
			if (overtimeId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(overtimeId);

//					taskTrackApproval.setApprovedDate(endDate);

				if (startCal.get(Calendar.DATE) < 16) {
					if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
							|| taskTrackApproval.getFirstHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
					} else {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
					}
				}
				if (endCal.get(Calendar.DATE) > 15) {
					if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
							|| taskTrackApproval.getSecondHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
					} else {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
					}
				}
				if (taskTrackApproval != null) {

					int startDayOfMonth = startCal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = startCal.get(Calendar.DATE);
						String dateString = startCal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (overtimeArray.get(dateString) != null) {
							hours = Double.valueOf(overtimeArray.get(dateString).toString());

							setDayInCorrespondingModel(taskTrackApproval, i, hours);
						}
						startCal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					overtime_id = taskTrackApproval.getId();
				} else {
					throw new Exception("TaskTrack data not found for given overtime id.");
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Overtime");

				if (startCal.get(Calendar.DATE) < 16) {
					if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
							|| taskTrackApproval.getFirstHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
					} else {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
					}
				}
				if (endCal.get(Calendar.DATE) > 15) {
					if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
							|| taskTrackApproval.getSecondHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
					} else {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
					}
				}

				taskTrackApproval.setProject(project);

				int startDayOfMonth = startCal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (startCal.get(Calendar.MONTH) + 1);
					intday = startCal.get(Calendar.DATE);
					String dateString = startCal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (overtimeArray.get(dateString) != null) {
						hours = Double.valueOf(overtimeArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);

					}
					startCal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal overtime = save(taskTrackApproval);
				overtime_id = overtime.getId();

			}
		}
		/*****************************************************************************************/

		if (beachArray.size() > 0) {// Beach

			Calendar startCal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			startCal.setTime(startDate);
			double hours = 0;
			intMonth = (startCal.get(Calendar.MONTH) + 1);
			if (beachId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(beachId);

//					taskTrackApproval.setApprovedDate(endDate);

				if (startCal.get(Calendar.DATE) < 16) {
					if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
							|| taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
					} else {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
					}
				}
				if (endCal.get(Calendar.DATE) > 15) {
					if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
							|| taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
					} else {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
					}
				}
				if (taskTrackApproval != null) {

					int startDayOfMonth = startCal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = startCal.get(Calendar.DATE);
						String dateString = startCal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (beachArray.get(dateString) != null) {
							hours = Double.valueOf(beachArray.get(dateString).toString());

							setDayInCorrespondingModel(taskTrackApproval, i, hours);
						}
						startCal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					beach_id = taskTrackApproval.getId();
				} else {
					throw new Exception("TaskTrack data not found for given beach id.");
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Beach");

				if (startCal.get(Calendar.DATE) < 16) {
					if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
							|| taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
					} else {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
					}
				}
				if (endCal.get(Calendar.DATE) > 15) {
					if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
							|| taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED);
					} else {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_OPEN);
					}
				}

				taskTrackApproval.setProject(project);

				int startDayOfMonth = startCal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (startCal.get(Calendar.MONTH) + 1);
					intday = startCal.get(Calendar.DATE);
					String dateString = startCal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (beachArray.get(dateString) != null) {
						hours = Double.valueOf(beachArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);

					}
					startCal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal beach = save(taskTrackApproval);
				beach_id = beach.getId();

			}
		}
	}

	/**
	 * @author sreejith.j
	 */
	@Override
	public void submitFirstHalfHoursAsFinal(JSONObject requestData) throws Exception {

		// Obtain the data from request data
		Long billableId = null, nonBillableId = null, overtimeId = null, beachId = null, projectId = null, userId = null;
		Integer year = Integer.parseInt((String) requestData.get("year"));
		Integer month = (Integer) requestData.get("month");
		if (requestData.get("projectId") != null && requestData.get("projectId") != "") {
			projectId = Long.valueOf(requestData.get("projectId").toString());
		}
		if (requestData.get("userId") != null && requestData.get("userId") != "") {
			userId = Long.valueOf(requestData.get("userId").toString());
		}
		if (requestData.get("billableId") != null && requestData.get("billableId") != "") {
			billableId = Long.valueOf(requestData.get("billableId").toString());
		}
		if (requestData.get("nonBillableId") != null && requestData.get("nonBillableId") != "") {
			nonBillableId = Long.valueOf(requestData.get("nonBillableId").toString());
		}
		if (requestData.get("overtimeId") != null && requestData.get("overtimeId") != "") {
			overtimeId = Long.valueOf(requestData.get("overtimeId").toString());
		}
		if (requestData.get("beachId") != null && requestData.get("beachId") != "") {
			beachId = Long.valueOf(requestData.get("beachId").toString());
		}
		String date1 = (String) requestData.get("startDate");
		String date2 = (String) requestData.get("endDate");

		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		if (!date1.isEmpty()) {
			startDate = outputFormat.parse(date1);
		}
		if (!date2.isEmpty()) {
			endDate = outputFormat.parse(date2);
		}

		HashMap<String, Object> billableArray = new JSONObject();
		HashMap<String, Object> nonbillableArray = new JSONObject();
		HashMap<String, Object> overtimeArray = new JSONObject();
		HashMap<String, Object> beachArray = new JSONObject();

		UserModel user = userService.getUserDetailsById(userId);
		ProjectModel project = projectService.getProjectId(projectId);

		if (requestData.get("billable") != null && requestData.get("billable") != "") {
			billableArray = (HashMap<String, Object>) (requestData.get("billable"));
		}
		if (requestData.get("nonBillable") != null && requestData.get("nonBillable") != "") {
			nonbillableArray = (HashMap<String, Object>) requestData.get("nonBillable");
		}
		if (requestData.get("overtime") != null && requestData.get("overtime") != "") {
			overtimeArray = (HashMap<String, Object>) requestData.get("overtime");
		}
		if (requestData.get("beach") != null && requestData.get("beach") != "") {
			beachArray = (HashMap<String, Object>) requestData.get("beach");
		}

		Long billable_id = null;
		Long nonbillable_id = null;
		Long overtime_id = null;
		Long beach_id = null;

		Date current_date = new Date();
		Calendar current = Calendar.getInstance();
		current.setTime(current_date);
		int intCurrentMonth = 0;
		intCurrentMonth = (current.get(Calendar.MONTH) + 1);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.add(Calendar.DATE, -1);

		if (billableArray.size() > 0 && billableId == null) {
			billableId = taskTrackFinalJPARepository.getBillableIdForAUserForAProject(month, year, projectId, userId);
			if (billableId != null) {
				throw new DuplicateEntryException("Duplicate entry for billable.");
			}
		}
		if (nonbillableArray.size() > 0 && nonBillableId == null) {
			nonBillableId = taskTrackFinalJPARepository.getNonBillableIdForAUserForAProject(month, year, projectId,
					userId);
			if (nonBillableId != null) {
				throw new DuplicateEntryException("Duplicate entry for NonBillable.");
			}
		}
		if (overtimeArray.size() > 0 && overtimeId == null) {
			overtimeId = taskTrackFinalJPARepository.getOvertimeIdForAUserForAProject(month, year, projectId, userId);
			if (overtimeId != null) {
				throw new DuplicateEntryException("Duplicate entry for Overtime.");
			}
		}
		if (beachArray.size() > 0 && beachId == null) {
			beachId = taskTrackFinalJPARepository.getBeachIdForAUserForAProject(month, year, projectId, userId);
			if (beachId != null) {
				throw new DuplicateEntryException("Duplicate entry for Beach.");
			}
		}

		if (billableArray.size() > 0) {// Billable

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);

			if (billableId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(billableId);
				if (taskTrackApproval != null) {

					if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

						int startDay = 0, endDay = 0;
						startDay = cal.get(Calendar.DATE);
						endDay = calendar.get(Calendar.DATE);
						List<TaskTrackCorrection> correctionList = taskTrackCorrectionRepository
								.findCorrectionDays(userId, projectId, month, year, startDay, endDay);
						for (TaskTrackCorrection correctionObj : correctionList) {
							correctionObj.setStatus(Constants.TASKTRACK_CORRECTION_STATUS_CLOSED);
						}

						taskTrackCorrectionRepository.saveAll(correctionList);

					}
					taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

					int startDayOfMonth = cal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DATE);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (billableArray.get(dateString) != null) {
							hours = Double.valueOf(billableArray.get(dateString).toString());
							setDayInCorrespondingModel(taskTrackApproval, i, hours);

						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					billable_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Billable");
				taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);

				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DATE);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (billableArray.get(dateString) != null) {
						hours = Double.valueOf(billableArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);
					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal billable = save(taskTrackApproval);
				billable_id = billable.getId();

			}
		}

		/**************************************************************/

		if (nonbillableArray.size() > 0) {// Non-Billable

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);

			if (nonBillableId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(nonBillableId);

				if (taskTrackApproval != null) {

					if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

						int startDay = 0, endDay = 0;
						startDay = cal.get(Calendar.DATE);
						endDay = calendar.get(Calendar.DATE);
						List<TaskTrackCorrection> correctionList = taskTrackCorrectionRepository
								.findCorrectionDays(userId, projectId, month, year, startDay, endDay);
						for (TaskTrackCorrection correctionObj : correctionList) {
							correctionObj.setStatus(Constants.TASKTRACK_CORRECTION_STATUS_CLOSED);
						}

						taskTrackCorrectionRepository.saveAll(correctionList);

					}
					taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

					int startDayOfMonth = cal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DATE);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (nonbillableArray.get(dateString) != null) {
							hours = Double.valueOf(nonbillableArray.get(dateString).toString());

							setDayInCorrespondingModel(taskTrackApproval, i, hours);

						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					nonbillable_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Non-Billable");
				taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);

				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DATE);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (nonbillableArray.get(dateString) != null) {
						hours = Double.valueOf(nonbillableArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);

					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal nonbillable = save(taskTrackApproval);
				nonbillable_id = nonbillable.getId();

			}

		}
		/****************************************************************************************/

		/*****************************************************************************************/

		if (overtimeArray.size() > 0) {// OverTime

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);
			if (overtimeId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(overtimeId);

				if (taskTrackApproval != null) {

					if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

						int startDay = 0, endDay = 0;
						startDay = cal.get(Calendar.DATE);
						endDay = calendar.get(Calendar.DATE);
						List<TaskTrackCorrection> correctionList = taskTrackCorrectionRepository
								.findCorrectionDays(userId, projectId, month, year, startDay, endDay);
						for (TaskTrackCorrection correctionObj : correctionList) {
							correctionObj.setStatus(Constants.TASKTRACK_CORRECTION_STATUS_CLOSED);
						}

						taskTrackCorrectionRepository.saveAll(correctionList);

					}
					taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

					int startDayOfMonth = cal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DATE);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (overtimeArray.get(dateString) != null) {
							hours = Double.valueOf(overtimeArray.get(dateString).toString());

							setDayInCorrespondingModel(taskTrackApproval, i, hours);
						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					overtime_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Overtime");
				taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);

				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DATE);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (overtimeArray.get(dateString) != null) {
						hours = Double.valueOf(overtimeArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);

					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal overtime = save(taskTrackApproval);
				overtime_id = overtime.getId();

			}
		}
		/*****************************************************************************************/

		if (beachArray.size() > 0) {// Beach

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);
			if (beachId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(beachId);

				if (taskTrackApproval != null) {

					if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

						int startDay = 0, endDay = 0;
						startDay = cal.get(Calendar.DATE);
						endDay = calendar.get(Calendar.DATE);
						List<TaskTrackCorrection> correctionList = taskTrackCorrectionRepository
								.findCorrectionDays(userId, projectId, month, year, startDay, endDay);
						for (TaskTrackCorrection correctionObj : correctionList) {
							correctionObj.setStatus(Constants.TASKTRACK_CORRECTION_STATUS_CLOSED);
						}

						taskTrackCorrectionRepository.saveAll(correctionList);

					}
					taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

					int startDayOfMonth = cal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DATE);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (beachArray.get(dateString) != null) {
							hours = Double.valueOf(beachArray.get(dateString).toString());

							setDayInCorrespondingModel(taskTrackApproval, i, hours);
						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					beach_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Beach");
				taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);

				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DATE);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (beachArray.get(dateString) != null) {
						hours = Double.valueOf(beachArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);

					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal beach = save(taskTrackApproval);
				beach_id = beach.getId();

			}
		}
		try {
			String sendTo="",sendCC="",subject="",emailReceiver="",resource="",approverOne="";
			subject = "RCG Time Sheet- First half time sheet Submitted";
			resource = user.getLastName().concat(" "+user.getFirstName());
			approverOne = project.getProjectOwner().getLastName().concat(" "+project.getProjectOwner().getFirstName());
			sendCC = project.getProjectOwner().getEmail();
			sendTo = financeMail;
			emailReceiver = "Finance Team,";

			StringBuilder mailBody = new StringBuilder("Hi "+ emailReceiver);
			mailBody.append("<br/><br/>Project Name : "+project.getProjectName());
			mailBody.append("<br/>Resource Name : "+resource);
			mailBody.append("<br/><br/>Timesheet for "+Month.of(month).name()+" 1-15 days has been Approved & Submitted for the resource.");
			mailBody.append("<br/><br/>Approved by : "+approverOne);

			sendMail(sendTo,sendCC,subject,mailBody);
		}
		catch(Exception e){

		}
	}

	/**
	 * @author sreejith.j
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void submitSecondHalfHoursAsFinal(JSONObject requestData) throws Exception {

		// Obtain the data from request data
		Long billableId = null, nonBillableId = null, overtimeId = null, beachId = null, projectId = null, userId = null;
		Integer year = Integer.parseInt((String) requestData.get("year"));
		Integer month = (Integer) requestData.get("month");
		if (requestData.get("projectId") != null && requestData.get("projectId") != "") {
			projectId = Long.valueOf(requestData.get("projectId").toString());
		}
		if (requestData.get("userId") != null && requestData.get("userId") != "") {
			userId = Long.valueOf(requestData.get("userId").toString());
		}
		if (requestData.get("billableId") != null && requestData.get("billableId") != "") {
			billableId = Long.valueOf(requestData.get("billableId").toString());
		}
		if (requestData.get("nonBillableId") != null && requestData.get("nonBillableId") != "") {
			nonBillableId = Long.valueOf(requestData.get("nonBillableId").toString());
		}
		if (requestData.get("overtimeId") != null && requestData.get("overtimeId") != "") {
			overtimeId = Long.valueOf(requestData.get("overtimeId").toString());
		}
		if (requestData.get("beachId") != null && requestData.get("beachId") != "") {
			beachId = Long.valueOf(requestData.get("beachId").toString());
		}
		String date1 = (String) requestData.get("startDate");
		String date2 = (String) requestData.get("endDate");

		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		if (!date1.isEmpty()) {
			startDate = outputFormat.parse(date1);
		}
		if (!date2.isEmpty()) {
			endDate = outputFormat.parse(date2);
		}

		HashMap<String, Object> billableArray = new JSONObject();
		HashMap<String, Object> nonbillableArray = new JSONObject();
		HashMap<String, Object> overtimeArray = new JSONObject();
		HashMap<String, Object> beachArray = new JSONObject();

		UserModel user = userService.getUserDetailsById(userId);
		ProjectModel project = projectService.getProjectId(projectId);

		if (requestData.get("billable") != null && requestData.get("billable") != "") {
			billableArray = (HashMap<String, Object>) (requestData.get("billable"));
		}
		if (requestData.get("nonBillable") != null && requestData.get("nonBillable") != "") {
			nonbillableArray = (HashMap<String, Object>) requestData.get("nonBillable");
		}
		if (requestData.get("overtime") != null && requestData.get("overtime") != "") {
			overtimeArray = (HashMap<String, Object>) requestData.get("overtime");
		}
		if (requestData.get("beach") != null && requestData.get("beach") != "") {
			beachArray = (HashMap<String, Object>) requestData.get("beach");
		}

		Long billable_id = null;
		Long nonbillable_id = null;
		Long overtime_id = null;
		Long beach_id = null;

		Date current_date = new Date();
		Calendar current = Calendar.getInstance();
		current.setTime(current_date);
		int intCurrentMonth = 0;
		intCurrentMonth = (current.get(Calendar.MONTH) + 1);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.add(Calendar.DATE, -1);

		if (billableArray.size() > 0 && billableId == null) {
			billableId = taskTrackFinalJPARepository.getBillableIdForAUserForAProject(month, year, projectId, userId);
			if (billableId != null) {
				throw new DuplicateEntryException("Duplicate entry for billable.");
			}
		}
		if (nonbillableArray.size() > 0 && nonBillableId == null) {
			nonBillableId = taskTrackFinalJPARepository.getNonBillableIdForAUserForAProject(month, year, projectId,
					userId);
			if (nonBillableId != null) {
				throw new DuplicateEntryException("Duplicate entry for NonBillable.");
			}
		}
		if (overtimeArray.size() > 0 && overtimeId == null) {
			overtimeId = taskTrackFinalJPARepository.getOvertimeIdForAUserForAProject(month, year, projectId, userId);
			if (overtimeId != null) {
				throw new DuplicateEntryException("Duplicate entry for Overtime.");
			}
		}
		if (beachArray.size() > 0 && beachId == null) {
			beachId = taskTrackFinalJPARepository.getBeachIdForAUserForAProject(month, year, projectId, userId);
			if (beachId != null) {
				throw new DuplicateEntryException("Duplicate entry for Beach.");
			}
		}

		if (billableArray.size() > 0) {// Billable

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);

			if (billableId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(billableId);
				if (taskTrackApproval != null) {

					if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

						int startDay = 0, endDay = 0;
						startDay = cal.get(Calendar.DATE);
						endDay = calendar.get(Calendar.DATE);
						List<TaskTrackCorrection> correctionList = taskTrackCorrectionRepository
								.findCorrectionDays(userId, projectId, month, year, startDay, endDay);
						for (TaskTrackCorrection correctionObj : correctionList) {
							correctionObj.setStatus(Constants.TASKTRACK_CORRECTION_STATUS_CLOSED);
						}

						taskTrackCorrectionRepository.saveAll(correctionList);

					}
					taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

					int startDayOfMonth = cal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DATE);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (billableArray.get(dateString) != null) {
							hours = Double.valueOf(billableArray.get(dateString).toString());
							setDayInCorrespondingModel(taskTrackApproval, i, hours);

						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					billable_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Billable");
				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);

				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DATE);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (billableArray.get(dateString) != null) {
						hours = Double.valueOf(billableArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);
					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal billable = save(taskTrackApproval);
				billable_id = billable.getId();

			}
		}

		/**************************************************************/

		if (nonbillableArray.size() > 0) {// Non-Billable

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);

			if (nonBillableId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(nonBillableId);

				if (taskTrackApproval != null) {

					if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

						int startDay = 0, endDay = 0;
						startDay = cal.get(Calendar.DATE);
						endDay = calendar.get(Calendar.DATE);
						List<TaskTrackCorrection> correctionList = taskTrackCorrectionRepository
								.findCorrectionDays(userId, projectId, month, year, startDay, endDay);
						for (TaskTrackCorrection correctionObj : correctionList) {
							correctionObj.setStatus(Constants.TASKTRACK_CORRECTION_STATUS_CLOSED);
						}

						taskTrackCorrectionRepository.saveAll(correctionList);

					}
					taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

					int startDayOfMonth = cal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DATE);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (nonbillableArray.get(dateString) != null) {
							hours = Double.valueOf(nonbillableArray.get(dateString).toString());

							setDayInCorrespondingModel(taskTrackApproval, i, hours);

						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					nonbillable_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Non-Billable");
				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);

				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DATE);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (nonbillableArray.get(dateString) != null) {
						hours = Double.valueOf(nonbillableArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);

					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal nonbillable = save(taskTrackApproval);
				nonbillable_id = nonbillable.getId();

			}

		}
		/****************************************************************************************/

		/*****************************************************************************************/

		if (overtimeArray.size() > 0) {// OverTime

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);
			if (overtimeId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(overtimeId);

				if (taskTrackApproval != null) {

					if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

						int startDay = 0, endDay = 0;
						startDay = cal.get(Calendar.DATE);
						endDay = calendar.get(Calendar.DATE);
						List<TaskTrackCorrection> correctionList = taskTrackCorrectionRepository
								.findCorrectionDays(userId, projectId, month, year, startDay, endDay);
						for (TaskTrackCorrection correctionObj : correctionList) {
							correctionObj.setStatus(Constants.TASKTRACK_CORRECTION_STATUS_CLOSED);
						}

						taskTrackCorrectionRepository.saveAll(correctionList);

					}
					taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

					int startDayOfMonth = cal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DATE);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (overtimeArray.get(dateString) != null) {
							hours = Double.valueOf(overtimeArray.get(dateString).toString());

							setDayInCorrespondingModel(taskTrackApproval, i, hours);
						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					overtime_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Overtime");
				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);

				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DATE);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (overtimeArray.get(dateString) != null) {
						hours = Double.valueOf(overtimeArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);

					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal overtime = save(taskTrackApproval);
				overtime_id = overtime.getId();

			}
		}
		/*****************************************************************************************/

		if (beachArray.size() > 0) {// Beach

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);
			if (beachId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(beachId);

				if (taskTrackApproval != null) {

					if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

						int startDay = 0, endDay = 0;
						startDay = cal.get(Calendar.DATE);
						endDay = calendar.get(Calendar.DATE);
						List<TaskTrackCorrection> correctionList = taskTrackCorrectionRepository
								.findCorrectionDays(userId, projectId, month, year, startDay, endDay);
						for (TaskTrackCorrection correctionObj : correctionList) {
							correctionObj.setStatus(Constants.TASKTRACK_CORRECTION_STATUS_CLOSED);
						}

						taskTrackCorrectionRepository.saveAll(correctionList);

					}
					taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

					int startDayOfMonth = cal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DATE);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (beachArray.get(dateString) != null) {
							hours = Double.valueOf(beachArray.get(dateString).toString());

							setDayInCorrespondingModel(taskTrackApproval, i, hours);
						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					beach_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Beach");
				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);

				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DATE);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (beachArray.get(dateString) != null) {
						hours = Double.valueOf(beachArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);

					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal beach = save(taskTrackApproval);
				beach_id = beach.getId();

			}
		}
		try {
			String sendTo="",sendCC="",subject="",emailReceiver="",resource="",approverOne="";
			subject = "RCG Time Sheet- Second half time sheet Submitted";
			resource = user.getLastName().concat(" "+user.getFirstName());
			approverOne = project.getProjectOwner().getLastName().concat(" "+project.getProjectOwner().getFirstName());
			sendCC = project.getProjectOwner().getEmail();
			sendTo = financeMail;
			emailReceiver = "Finance Team,";

			StringBuilder mailBody = new StringBuilder("Hi "+ emailReceiver);
			mailBody.append("<br/><br/>Project Name : "+project.getProjectName());
			mailBody.append("<br/>Resource Name : "+resource);
			mailBody.append("<br/><br/>Timesheet for "+Month.of(month).name()+" 16-31 days has been Approved & Submitted for the resource.");
			mailBody.append("<br/><br/>Approved by : "+approverOne);

			sendMail(sendTo,sendCC,subject,mailBody);
		}
		catch(Exception e){

		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void submitFirstHalfHoursForApproval2(JSONObject requestData) throws Exception {
		// Obtain the data from request data
		Long billableId = null, nonBillableId = null, overtimeId = null, beachId = null, projectId = null, userId = null;
		Integer year = Integer.parseInt((String) requestData.get("year"));
		Integer month = (Integer) requestData.get("month");
		if (requestData.get("projectId") != null && requestData.get("projectId") != "") {
			projectId = Long.valueOf(requestData.get("projectId").toString());
		}
		if (requestData.get("userId") != null && requestData.get("userId") != "") {
			userId = Long.valueOf(requestData.get("userId").toString());
		}
		if (requestData.get("billableId") != null && requestData.get("billableId") != "") {
			billableId = Long.valueOf(requestData.get("billableId").toString());
		}
		if (requestData.get("nonBillableId") != null && requestData.get("nonBillableId") != "") {
			nonBillableId = Long.valueOf(requestData.get("nonBillableId").toString());
		}
		if (requestData.get("overtimeId") != null && requestData.get("overtimeId") != "") {
			overtimeId = Long.valueOf(requestData.get("overtimeId").toString());
		}
		if (requestData.get("beachId") != null && requestData.get("beachId") != "") {
			overtimeId = Long.valueOf(requestData.get("beachId").toString());
		}
		String date1 = (String) requestData.get("startDate");
		String date2 = (String) requestData.get("endDate");

		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		if (!date1.isEmpty()) {
			startDate = outputFormat.parse(date1);
		}
		if (!date2.isEmpty()) {
			endDate = outputFormat.parse(date2);
		}

		HashMap<String, Object> billableArray = new JSONObject();
		HashMap<String, Object> nonbillableArray = new JSONObject();
		HashMap<String, Object> overtimeArray = new JSONObject();
		HashMap<String, Object> beachArray = new JSONObject();

		UserModel user = userService.getUserDetailsById(userId);
		ProjectModel project = projectService.getProjectId(projectId);

		if (requestData.get("billable") != null && requestData.get("billable") != "") {
			billableArray = (HashMap<String, Object>) (requestData.get("billable"));
		}
		if (requestData.get("nonBillable") != null && requestData.get("nonBillable") != "") {
			nonbillableArray = (HashMap<String, Object>) requestData.get("nonBillable");
		}
		if (requestData.get("overtime") != null && requestData.get("overtime") != "") {
			overtimeArray = (HashMap<String, Object>) requestData.get("overtime");
		}
		if (requestData.get("beach") != null && requestData.get("beach") != "") {
			beachArray = (HashMap<String, Object>) requestData.get("beach");
		}

		Long billable_id = null;
		Long nonbillable_id = null;
		Long overtime_id = null;
		Long beach_id = null;

		Date current_date = new Date();
		Calendar current = Calendar.getInstance();
		current.setTime(current_date);
		int intCurrentMonth = 0;
		intCurrentMonth = (current.get(Calendar.MONTH) + 1);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.add(Calendar.DATE, -1);

		if (billableArray.size() > 0 && billableId == null) {
			billableId = taskTrackFinalJPARepository.getBillableIdForAUserForAProject(month, year, projectId, userId);
			if (billableId != null) {
				throw new DuplicateEntryException("Duplicate entry for billable.");
			}
		}
		if (nonbillableArray.size() > 0 && nonBillableId == null) {
			nonBillableId = taskTrackFinalJPARepository.getNonBillableIdForAUserForAProject(month, year, projectId,
					userId);
			if (nonBillableId != null) {
				throw new DuplicateEntryException("Duplicate entry for NonBillable.");
			}
		}
		if (overtimeArray.size() > 0 && overtimeId == null) {
			overtimeId = taskTrackFinalJPARepository.getOvertimeIdForAUserForAProject(month, year, projectId, userId);
			if (overtimeId != null) {
				throw new DuplicateEntryException("Duplicate entry for Overtime.");
			}
		}
		if (beachArray.size() > 0 && beachId == null) {
			beachId = taskTrackFinalJPARepository.getBeachIdForAUserForAProject(month, year, projectId, userId);
			if (beachId != null) {
				throw new DuplicateEntryException("Duplicate entry for Beach.");
			}
		}

		String statusBeforeSubmit = null;
		if (billableArray.size() > 0) {// Billable

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);

			if (billableId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(billableId);
				statusBeforeSubmit = taskTrackApproval.getFirstHalfStatus();
				taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);
				if (taskTrackApproval != null) {
					int startDayOfMonth = cal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DAY_OF_MONTH);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (billableArray.get(dateString) != null) {
							hours = Double.valueOf(billableArray.get(dateString).toString());
							setDayInCorrespondingModel(taskTrackApproval, i, hours);

						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					billable_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Billable");
				taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);
				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DAY_OF_MONTH);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (billableArray.get(dateString) != null) {
						hours = Double.valueOf(billableArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);
					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal billable = save(taskTrackApproval);
				billable_id = billable.getId();

			}
		}

		/**************************************************************/
		if (nonbillableArray.size() > 0) {// Non-Billable

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);

			if (nonBillableId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(nonBillableId);

				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				if (taskTrackApproval != null) {
					int startDayOfMonth = cal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DAY_OF_MONTH);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (nonbillableArray.get(dateString) != null) {
							hours = Double.valueOf(nonbillableArray.get(dateString).toString());

							setDayInCorrespondingModel(taskTrackApproval, i, hours);

						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					nonbillable_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Non-Billable");
				taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);
				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DAY_OF_MONTH);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (nonbillableArray.get(dateString) != null) {
						hours = Double.valueOf(nonbillableArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);

					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal nonbillable = save(taskTrackApproval);
				nonbillable_id = nonbillable.getId();

			}

		}
		/****************************************************************************************/

		/*****************************************************************************************/

		if (overtimeArray.size() > 0) {// OverTime

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);
			if (overtimeId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(overtimeId);

				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);
				if (taskTrackApproval != null) {
					int startDayOfMonth = cal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DAY_OF_MONTH);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (overtimeArray.get(dateString) != null) {
							hours = Double.valueOf(overtimeArray.get(dateString).toString());

							setDayInCorrespondingModel(taskTrackApproval, i, hours);
						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					overtime_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Overtime");
				taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);
				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DAY_OF_MONTH);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (overtimeArray.get(dateString) != null) {
						hours = Double.valueOf(overtimeArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);

					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal overtime = save(taskTrackApproval);
				overtime_id = overtime.getId();

			}
		}
		/*****************************************************************************************/

		if (beachArray.size() > 0) {// Beach

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);
			if (beachId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(beachId);

				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);
				if (taskTrackApproval != null) {
					int startDayOfMonth = cal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DAY_OF_MONTH);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (beachArray.get(dateString) != null) {
							hours = Double.valueOf(beachArray.get(dateString).toString());

							setDayInCorrespondingModel(taskTrackApproval, i, hours);
						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					beach_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Beach");
				taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);
				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DAY_OF_MONTH);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (beachArray.get(dateString) != null) {
						hours = Double.valueOf(beachArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);

					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal beach = save(taskTrackApproval);
				beach_id = beach.getId();

			}
		}
		List<TaskTrackApproval> taskTrackApproval = timeTrackApprovalJPARepository
				.upadateTaskTrackApprovalStatus(projectId, month, year, userId);
		for (TaskTrackApproval approval : taskTrackApproval) {
			approval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_LOCK);
		}
		timeTrackApprovalJPARepository.saveAll(taskTrackApproval);

		if (statusBeforeSubmit != null && statusBeforeSubmit.equals(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int startDay = cal.get(Calendar.DATE);
			int endDay = calendar.get(Calendar.DATE);
			List<TaskTrackCorrection> correctionList = taskTrackCorrectionRepository.findCorrectionDays(userId,
					projectId, month, year, startDay, endDay);
			if (correctionList.size() > 0) {
				for (TaskTrackCorrection correctionObj : correctionList) {
					correctionObj.setStatus(Constants.TASKTRACK_CORRECTION_STATUS_CLOSED);
				}
				taskTrackCorrectionRepository.saveAll(correctionList);
			}
		}

		List<TaskTrackRejection> rejectionList = taskTrackRejectionRepository
				.findOpenRejectionForCycleForUserForProject(userId, projectId, month, year,
						Constants.TASKTRACK_REJECTION_SECOND_HALF_CYCLE);
		if (rejectionList.size() > 0) {
			for (TaskTrackRejection rejectionObj : rejectionList) {
				rejectionObj.setStatus(Constants.TASKTRACK_REJECTION_STATUS_CLOSED);
			}
			taskTrackRejectionRepository.saveAll(rejectionList);
		}
		try {
			String sendTo="",sendCC="",subject="",emailReceiver="",resource="",approverOne="";
			subject = "RCG Time Sheet- First half time sheet Submitted";
			resource = user.getLastName().concat(" "+user.getFirstName());
			approverOne = project.getProjectOwner().getLastName().concat(" "+project.getProjectOwner().getFirstName());
			sendCC = project.getOnsite_lead().getEmail();
			sendTo = financeMail;
			emailReceiver = "Finance Team,";

			StringBuilder mailBody = new StringBuilder("Hi "+ emailReceiver);
			mailBody.append("<br/><br/>Project Name : "+project.getProjectName());
			mailBody.append("<br/>Resource Name : "+resource);
			mailBody.append("<br/><br/>Timesheet for "+Month.of(month).name()+" 1-15 days has been Approved & Submitted for the resource.");
			mailBody.append("<br/><br/>Approved by : "+approverOne);

			sendMail(sendTo,sendCC,subject,mailBody);
		}
		catch(Exception e){

		}
	}

	@Override
	public void submitSecondHalfHoursForApproval2(JSONObject requestData) throws Exception {
		Long billableId = null, nonBillableId = null, overtimeId = null, beachId = null, projectId = null, userId = null;
		Integer year = Integer.parseInt((String) requestData.get("year"));
		Integer month = (Integer) requestData.get("month");
		if (requestData.get("projectId") != null && requestData.get("projectId") != "") {
			projectId = Long.valueOf(requestData.get("projectId").toString());
		}
		if (requestData.get("userId") != null && requestData.get("userId") != "") {
			userId = Long.valueOf(requestData.get("userId").toString());
		}
		if (requestData.get("billableId") != null && requestData.get("billableId") != "") {
			billableId = Long.valueOf(requestData.get("billableId").toString());
		}
		if (requestData.get("nonBillableId") != null && requestData.get("nonBillableId") != "") {
			nonBillableId = Long.valueOf(requestData.get("nonBillableId").toString());
		}
		if (requestData.get("overtimeId") != null && requestData.get("overtimeId") != "") {
			overtimeId = Long.valueOf(requestData.get("overtimeId").toString());
		}
		if (requestData.get("beachId") != null && requestData.get("beachId") != "") {
			beachId = Long.valueOf(requestData.get("beachId").toString());
		}
		String date1 = (String) requestData.get("startDate");
		String date2 = (String) requestData.get("endDate");

		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		if (!date1.isEmpty()) {
			startDate = outputFormat.parse(date1);
		}
		if (!date2.isEmpty()) {
			endDate = outputFormat.parse(date2);
		}

		HashMap<String, Object> billableArray = new JSONObject();
		HashMap<String, Object> nonbillableArray = new JSONObject();
		HashMap<String, Object> overtimeArray = new JSONObject();
		HashMap<String, Object> beachArray = new JSONObject();

		UserModel user = userService.getUserDetailsById(userId);
		ProjectModel project = projectService.getProjectId(projectId);

		if (requestData.get("billable") != null && requestData.get("billable") != "") {
			billableArray = (HashMap<String, Object>) (requestData.get("billable"));
		}
		if (requestData.get("nonBillable") != null && requestData.get("nonBillable") != "") {
			nonbillableArray = (HashMap<String, Object>) requestData.get("nonBillable");
		}
		if (requestData.get("overtime") != null && requestData.get("overtime") != "") {
			overtimeArray = (HashMap<String, Object>) requestData.get("overtime");
		}
		if (requestData.get("beach") != null && requestData.get("beach") != "") {
			beachArray = (HashMap<String, Object>) requestData.get("beach");
		}

		Long billable_id = null;
		Long nonbillable_id = null;
		Long overtime_id = null;
		Long beach_id = null;

		Date current_date = new Date();
		Calendar current = Calendar.getInstance();
		current.setTime(current_date);
		int intCurrentMonth = 0;
		intCurrentMonth = (current.get(Calendar.MONTH) + 1);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.add(Calendar.DATE, -1);

		if (billableArray.size() > 0 && billableId == null) {
			billableId = taskTrackFinalJPARepository.getBillableIdForAUserForAProject(month, year, projectId, userId);
			if (billableId != null) {
				throw new DuplicateEntryException("Duplicate entry for billable.");
			}
		}
		if (nonbillableArray.size() > 0 && nonBillableId == null) {
			nonBillableId = taskTrackFinalJPARepository.getNonBillableIdForAUserForAProject(month, year, projectId,
					userId);
			if (nonBillableId != null) {
				throw new DuplicateEntryException("Duplicate entry for NonBillable.");
			}
		}
		if (overtimeArray.size() > 0 && overtimeId == null) {
			overtimeId = taskTrackFinalJPARepository.getOvertimeIdForAUserForAProject(month, year, projectId, userId);
			if (overtimeId != null) {
				throw new DuplicateEntryException("Duplicate entry for Overtime.");
			}
		}
		if (beachArray.size() > 0 && beachId == null) {
			beachId = taskTrackFinalJPARepository.getBeachIdForAUserForAProject(month, year, projectId, userId);
			if (beachId != null) {
				throw new DuplicateEntryException("Duplicate entry for Beach.");
			}
		}

		String statusBeforeSubmit = null;
		if (billableArray.size() > 0) {// Billable

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);

			if (billableId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(billableId);
				statusBeforeSubmit = taskTrackApproval.getSecondHalfStatus();
				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);
				if (taskTrackApproval != null) {
					int startDayOfMonth = cal.get(Calendar.DATE);

					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DAY_OF_MONTH);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (billableArray.get(dateString) != null) {
							hours = Double.valueOf(billableArray.get(dateString).toString());
							setDayInCorrespondingModel(taskTrackApproval, i, hours);

						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					billable_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Billable");
				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);
				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DAY_OF_MONTH);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (billableArray.get(dateString) != null) {
						hours = Double.valueOf(billableArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);
					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal billable = save(taskTrackApproval);
				billable_id = billable.getId();

			}
		}

		/**************************************************************/

		if (nonbillableArray.size() > 0) {// Non-Billable

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);

			if (nonBillableId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(nonBillableId);

				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				if (taskTrackApproval != null) {
					int startDayOfMonth = cal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DAY_OF_MONTH);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (nonbillableArray.get(dateString) != null) {
							hours = Double.valueOf(nonbillableArray.get(dateString).toString());

							setDayInCorrespondingModel(taskTrackApproval, i, hours);

						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					nonbillable_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Non-Billable");
				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);
				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DAY_OF_MONTH);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (nonbillableArray.get(dateString) != null) {
						hours = Double.valueOf(nonbillableArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);

					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal nonbillable = save(taskTrackApproval);
				nonbillable_id = nonbillable.getId();

			}

		}
		/****************************************************************************************/

		/*****************************************************************************************/

		if (overtimeArray.size() > 0) {// OverTime

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);
			if (overtimeId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(overtimeId);

				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);
				if (taskTrackApproval != null) {

					int startDayOfMonth = cal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DAY_OF_MONTH);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (overtimeArray.get(dateString) != null) {
							hours = Double.valueOf(overtimeArray.get(dateString).toString());

							setDayInCorrespondingModel(taskTrackApproval, i, hours);
						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					overtime_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Overtime");
				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);
				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DAY_OF_MONTH);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (overtimeArray.get(dateString) != null) {
						hours = Double.valueOf(overtimeArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);

					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal overtime = save(taskTrackApproval);
				overtime_id = overtime.getId();

			}
		}
		/*****************************************************************************************/

		if (beachArray.size() > 0) {// Beach

			Calendar cal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			cal.setTime(startDate);
			double hours = 0;
			intMonth = (cal.get(Calendar.MONTH) + 1);
			if (beachId != null) {
				TaskTrackApprovalFinal taskTrackApproval = findById(beachId);

				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);
				if (taskTrackApproval != null) {

					int startDayOfMonth = cal.get(Calendar.DATE);
					for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

						intday = cal.get(Calendar.DAY_OF_MONTH);
						String dateString = cal.get(Calendar.YEAR) + "-"
								+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if (beachArray.get(dateString) != null) {
							hours = Double.valueOf(beachArray.get(dateString).toString());

							setDayInCorrespondingModel(taskTrackApproval, i, hours);
						}
						cal.add(Calendar.DATE, 1);
					}
					updateData(taskTrackApproval);
					beach_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApprovalFinal taskTrackApproval = new TaskTrackApprovalFinal();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Beach");
				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);

				taskTrackApproval.setProject(project);
				int startDayOfMonth = cal.get(Calendar.DATE);
				for (int i = startDayOfMonth - 1; i < diffInDays + startDayOfMonth; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DAY_OF_MONTH);
					String dateString = cal.get(Calendar.YEAR) + "-"
							+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (beachArray.get(dateString) != null) {
						hours = Double.valueOf(beachArray.get(dateString).toString());

						setDayInCorrespondingModel(taskTrackApproval, i, hours);

					}
					cal.add(Calendar.DATE, 1);
				}

				TaskTrackApprovalFinal beach = save(taskTrackApproval);
				beach_id = beach.getId();

			}
		}
		List<TaskTrackApproval> taskTrackApproval = timeTrackApprovalJPARepository
				.upadateTaskTrackApprovalStatus(projectId, month, year, userId);
		for (TaskTrackApproval approval : taskTrackApproval) {
			approval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_LOCK);
		}
		timeTrackApprovalJPARepository.saveAll(taskTrackApproval);

		if (statusBeforeSubmit != null && statusBeforeSubmit.equals(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int startDay = cal.get(Calendar.DATE);
			int endDay = calendar.get(Calendar.DATE);
			List<TaskTrackCorrection> correctionList = taskTrackCorrectionRepository.findCorrectionDays(userId,
					projectId, month, year, startDay, endDay);
			if (correctionList.size() > 0) {
				for (TaskTrackCorrection correctionObj : correctionList) {
					correctionObj.setStatus(Constants.TASKTRACK_CORRECTION_STATUS_CLOSED);
				}
				taskTrackCorrectionRepository.saveAll(correctionList);
			}
		}

		List<TaskTrackRejection> rejectionList = taskTrackRejectionRepository
				.findOpenRejectionForCycleForUserForProject(userId, projectId, month, year,
						Constants.TASKTRACK_REJECTION_SECOND_HALF_CYCLE);
		if (rejectionList.size() > 0) {
			for (TaskTrackRejection rejectionObj : rejectionList) {
				rejectionObj.setStatus(Constants.TASKTRACK_REJECTION_STATUS_CLOSED);
			}
			taskTrackRejectionRepository.saveAll(rejectionList);
		}
		try {
			String sendTo="",sendCC="",subject="",emailReceiver="",resource="",approverOne="";
			subject = "RCG Time Sheet- Second half time sheet Submitted";
			resource = user.getLastName().concat(" "+user.getFirstName());
			approverOne = project.getProjectOwner().getLastName().concat(" "+project.getProjectOwner().getFirstName());
			sendCC = project.getOnsite_lead().getEmail();
			sendTo = financeMail;
			emailReceiver = "Finance Team,";

			StringBuilder mailBody = new StringBuilder("Hi "+ emailReceiver);
			mailBody.append("<br/><br/>Project Name : "+project.getProjectName());
			mailBody.append("<br/>Resource Name : "+resource);
			mailBody.append("<br/><br/>Timesheet for "+Month.of(month).name()+" 16-31 days has been Approved & Submitted for the resource.");
			mailBody.append("<br/><br/>Approved by : "+approverOne);

			sendMail(sendTo,sendCC,subject,mailBody);
		}
		catch(Exception e){

		}
	}

	/**
	 * 
	 * @author sreejith.j
	 * @param taskTrackApproval
	 * @param i
	 * @param hours
	 */
	private void setDayInCorrespondingModel(TaskTrackApprovalFinal taskTrackApproval, int i, double hours)
			throws Exception {
		System.out.println("test");
		if (i == 0) {
			taskTrackApproval.setDay1(hours);
		} else if (i == 1) {
			taskTrackApproval.setDay2(hours);
		} else if (i == 2) {
			taskTrackApproval.setDay3(hours);
		} else if (i == 3) {
			taskTrackApproval.setDay4(hours);
		} else if (i == 4) {
			taskTrackApproval.setDay5(hours);
		} else if (i == 5) {
			taskTrackApproval.setDay6(hours);
		} else if (i == 6) {
			taskTrackApproval.setDay7(hours);
		} else if (i == 7) {
			taskTrackApproval.setDay8(hours);
		} else if (i == 8) {
			taskTrackApproval.setDay9(hours);
		} else if (i == 9) {
			taskTrackApproval.setDay10(hours);
		} else if (i == 10) {
			taskTrackApproval.setDay11(hours);
		} else if (i == 11) {
			taskTrackApproval.setDay12(hours);
		} else if (i == 12) {
			taskTrackApproval.setDay13(hours);
		} else if (i == 13) {
			taskTrackApproval.setDay14(hours);
		} else if (i == 14) {
			taskTrackApproval.setDay15(hours);
		} else if (i == 15) {
			taskTrackApproval.setDay16(hours);
		} else if (i == 16) {
			taskTrackApproval.setDay17(hours);
		} else if (i == 17) {
			taskTrackApproval.setDay18(hours);
		} else if (i == 18) {
			taskTrackApproval.setDay19(hours);
		} else if (i == 19) {
			taskTrackApproval.setDay20(hours);
		} else if (i == 20) {
			taskTrackApproval.setDay21(hours);
		} else if (i == 21) {
			taskTrackApproval.setDay22(hours);
		} else if (i == 22) {
			taskTrackApproval.setDay23(hours);
		} else if (i == 23) {
			taskTrackApproval.setDay24(hours);
		} else if (i == 24) {
			taskTrackApproval.setDay25(hours);
		} else if (i == 25) {
			taskTrackApproval.setDay26(hours);
		} else if (i == 26) {
			taskTrackApproval.setDay27(hours);
		} else if (i == 27) {
			taskTrackApproval.setDay28(hours);
		} else if (i == 28) {
			taskTrackApproval.setDay29(hours);
		} else if (i == 29) {
			taskTrackApproval.setDay30(hours);
		} else if (i == 30) {
			taskTrackApproval.setDay31(hours);
		}
	}

	//@author  Jinu Shaji
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void bulkApproveLevel2(JSONObject requestData) throws Exception {

		Long projectId = null;
		List<Integer> userIds = new ArrayList<Integer>();
		Integer monthPeriod=null,userId = null;
		String date1=null,date2=null;
		if (requestData.get("projectId") != null && requestData.get("projectId") != "") {
			projectId = Long.valueOf(requestData.get("projectId").toString());
		}
		if (requestData.get("userId") != null && requestData.get("userId") != "") {
			userIds = (List<Integer>) requestData.get("userId");
		}
		if (requestData.get("monthPeriod") != null) {
			monthPeriod = (Integer) requestData.get("monthPeriod");
		}
		if (requestData.get("startDate") != null && !requestData.get("startDate").equals("")) {
			date1 = (String) requestData.get("startDate");
		}
		if (requestData.get("endDate") != null && !requestData.get("endDate").equals("")) {
			date2 = (String) requestData.get("endDate");
		}

		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		if (date1!=null) {
			startDate = outputFormat.parse(date1);
		}
		if (date2!=null) {
			endDate = outputFormat.parse(date2);
		}
		
		JSONObject userLevelInfo = new JSONObject();

		for (int i = 0; i < userIds.size(); i++) {
			
			HashMap<String, Object> billableArray = new JSONObject();
			HashMap<String, Object> nonbillableArray = new JSONObject();
			HashMap<String, Object> overtimeArray = new JSONObject();
			
			userId = userIds.get(i);
			userLevelInfo = tasktrackApprovalService.getDataForApprovalLevelTwo(new Long(userId), startDate, endDate, projectId, 15);
			if(userLevelInfo!=null) {
				String approverOneFirstHalfStatus = (String) userLevelInfo.get("approverOneFirstHalfStatus");
				String approverOneSecondHalfStatus = (String) userLevelInfo.get("approverOneSecodHalfStatus");
				if( ( monthPeriod == 1 && (approverOneFirstHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)
						||approverOneFirstHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED)
						||approverOneFirstHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED)))
					
					||	(  monthPeriod == 2 && (approverOneSecondHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)
								||approverOneSecondHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED)
								||approverOneSecondHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED)))) 
					{
					JSONObject approverOneData = (JSONObject) userLevelInfo.get("approverOneData");
					JSONObject approverTwoData = (JSONObject) userLevelInfo.get("approverTwoData");
					JSONObject dataToSave = new JSONObject();
					dataToSave.put("projectId", projectId);
					dataToSave.put("year", requestData.get("year").toString());
					dataToSave.put("month", requestData.get("month"));
					dataToSave.put("userId", userId);
					dataToSave.put("startDate", requestData.get("startDate"));
					dataToSave.put("endDate", requestData.get("endDate"));
					dataToSave.put("billableId", approverTwoData.get("billableId"));
					dataToSave.put("nonBillableId", approverTwoData.get("nonBillableId"));
					dataToSave.put("overtimeId", approverTwoData.get("overtimeId"));
					
					ArrayList<JSONObject> billable = new ArrayList<JSONObject>();
					billable = (ArrayList<JSONObject>) approverOneData.get("billable");
					JSONObject billableObj = billable.get(0);
					billableObj.keySet().forEach(keyStr ->
				    {
				        Object keyvalue = billableObj.get(keyStr);
				        billableArray.put((String) keyStr, keyvalue);
				    });
					
					ArrayList<JSONObject> nonBillable = new ArrayList<JSONObject>();
					nonBillable = (ArrayList<JSONObject>) approverOneData.get("nonBillable");
					JSONObject nonBillableObj = nonBillable.get(0);
					nonBillableObj.keySet().forEach(keyStr ->
				    {
				        Object keyvalue = nonBillableObj.get(keyStr);
				        nonbillableArray.put((String) keyStr, keyvalue);
				    });
					
					ArrayList<JSONObject> overTime = new ArrayList<JSONObject>();
					overTime = (ArrayList<JSONObject>) approverOneData.get("overTime");
					JSONObject overTimeObj = overTime.get(0);
					overTimeObj.keySet().forEach(keyStr ->
				    {
				        Object keyvalue = overTimeObj.get(keyStr);
				        overtimeArray.put((String) keyStr, keyvalue);
				    });
							
					dataToSave.put("billable", billableArray);
					dataToSave.put("nonBillable", nonbillableArray);
					dataToSave.put("overtime", overtimeArray);

					if(monthPeriod == 1)
						submitFirstHalfHoursForApproval2(dataToSave);
					else
						submitSecondHalfHoursForApproval2(dataToSave);
				}
			}		
		}
	}
	
	//@author  Jinu Shaji
	private void sendMail(String sendTo, String cc,String subject , StringBuilder mailBody) throws Exception{
		
		try {
		MailDomainDto mailDomainDto = new MailDomainDto();
		mailDomainDto.setSubject(subject);
		mailDomainDto.setCc(cc);
		mailDomainDto.setContent(mailBody.toString());
		
		Template t = freemarkerConfig.getTemplate("email_template.ftl");
        String html = (FreeMarkerTemplateUtils.processTemplateIntoString(t, mailDomainDto)).replace("MAIL_BODY", mailBody).replace("Title", "");

		mailDomainDto.setMailBody(html);
		mailDomainDto.setTo(sendTo);
	    String token = UUID.randomUUID().toString();
		emailNotificationService.sendMail(token, mailDomainDto,true);
		}
		catch (Exception e) {
		}
	}
}
