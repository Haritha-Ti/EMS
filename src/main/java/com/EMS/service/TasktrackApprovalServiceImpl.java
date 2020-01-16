package com.EMS.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ValueRange;
import java.util.*;
import java.util.stream.Collectors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.joda.time.DateTimeConstants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.EMS.dto.ApproverOneDto;
import com.EMS.dto.ApproverTwoDto;
import com.EMS.dto.MailDomainDto;
import com.EMS.dto.MonthlySubmissionDto;
import com.EMS.dto.Submission;
import com.EMS.dto.WeeklySubmissionDto;
import com.EMS.dto.tasktrackapproval2.request.ApproveHoursRequest;
import com.EMS.dto.tasktrackapproval2.request.GetTaskTrackData;
import com.EMS.dto.tasktrackapproval2.response.Data;
import com.EMS.dto.tasktrackapproval2.response.SemiMonthlyData;
import com.EMS.dto.tasktrackapproval2.response.UserData;
import com.EMS.dto.tasktrackapproval2.response.WeekData;
import com.EMS.exception.DuplicateEntryException;
import com.EMS.model.ActivityLog;
import com.EMS.model.AllocationModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.StatusResponse;
import com.EMS.model.TaskTrackApproval;
import com.EMS.model.TaskTrackApprovalFinal;
import com.EMS.model.TaskTrackApprovalFinance;
import com.EMS.model.TaskTrackApprovalLevel2;
import com.EMS.model.TaskTrackCorrection;
import com.EMS.model.TaskTrackDaySubmissionModel;
import com.EMS.model.TaskTrackRejection;
import com.EMS.model.TaskTrackWeeklyApproval;
import com.EMS.model.Tasktrack;
import com.EMS.model.TasktrackApprovalSemiMonthly;
import com.EMS.model.UserModel;
import com.EMS.repository.ActivityLogRepository;
import com.EMS.repository.AllocationRepository;
import com.EMS.repository.AuditRepository;
import com.EMS.repository.ProjectAllocationRepository;
import com.EMS.repository.ProjectRepository;
import com.EMS.repository.TaskRepository;
import com.EMS.repository.TaskTrackApprovalFinalRepository;
import com.EMS.repository.TaskTrackApprovalLevel2Repository;
import com.EMS.repository.TaskTrackApprovalSemiMonthlyRepository;
import com.EMS.repository.TaskTrackCorrectionRepository;
import com.EMS.repository.TaskTrackDaySubmissionRepository;
import com.EMS.repository.TaskTrackFinalJPARepository;
import com.EMS.repository.TaskTrackFinanceRepository;
import com.EMS.repository.TaskTrackRejectionRepository;
import com.EMS.repository.TaskTrackWeeklyApprovalRepository;
import com.EMS.repository.TasktrackRepository;
import com.EMS.repository.TimeTrackApprovalJPARepository;
import com.EMS.repository.TimeTrackApprovalRepository;
import com.EMS.repository.UserRepository;
import com.EMS.utility.Constants;
import com.EMS.utility.TaskTrackApproverConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class TasktrackApprovalServiceImpl implements TasktrackApprovalService {

	@Autowired
	TaskTrackApprovalSemiMonthlyRepository taskTrackApprovalSemiMonthlyRepository;

	@Autowired
	TaskTrackWeeklyApprovalRepository taskTrackWeeklyApprovalRepository;

	@Autowired
	TasktrackRepository tasktrackRepository;

	@Autowired
	TaskTrackDaySubmissionRepository taskTrackDaySubmissionRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	@Autowired
	AllocationRepository allocationRepository;

	@Autowired
	TimeTrackApprovalRepository timeTrackApprovalRepository;

	@Autowired
	TimeTrackApprovalJPARepository timeTrackApprovalJPARepository;

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
	TaskTrackApprovalFinalRepository taskTrackApprovalFinalRepository;

	@Autowired
	TaskTrackCorrectionRepository taskTrackCorrectionRepository;

	@Autowired
	TaskTrackRejectionRepository taskTrackRejectionRepository;

	@Autowired
	private Configuration freemarkerConfig;

	@Autowired
	private EmailNotificationService emailNotificationService;

	@Autowired
	private ProjectAllocationService projectAllocationService;
	
	@Autowired
	private AuditRepository auditRepository;

	@Value("${FINANCE_MAIL}")
	private String financeMail;

	@Value("${CONTEXT_PATH}")
	private String CONTEXT_PATH;

	@Override
	public Boolean checkIsUserExists(Long id) {
		Boolean exist = tasktrackRepository.existsByUser(id);
		return exist;
	}

	@Override
	public Boolean checkIsUserExistsInApproval(Long id) {
		Boolean exist = timeTrackApprovalJPARepository.existsByUser(id);
		return exist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getTimeTrackUserTaskDetails(Long userId, Date startDate, Date endDate, Boolean isExist,
			Long projectId, Integer projectTier, Integer firstHalfDay) throws ParseException {

		JSONObject response = new JSONObject();

		String firstHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
		String secondHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;

		String firstHalfRemarks = null;
		String secondHalfRemarks = null;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		int month = (cal.get(Calendar.MONTH) + 1);
		int year = cal.get(Calendar.YEAR);

		TaskTrackDaySubmissionModel submissionDates = taskTrackDaySubmissionRepository.findOneByMonth(month);

		Double firstHalfHour = 0.0;
		Double secondHalfHour = 0.0;

		if (isExist) {
			String userName = null;

			// For Logged Details
			List<Object[]> loggedList = getUserListByProject(userId, startDate, endDate, projectId);
			if (loggedList != null && loggedList.size() > 0) {
				Object[] user = loggedList.get(0);
				userName = user[1] + " " + user[0];
				for (Object[] logged : loggedList) {
					Date date = dateFormat.parse(String.valueOf(logged[3]));
					cal.setTime(date);
					int day = cal.get(Calendar.DATE);
					Double hour = Constants.roundToDefaultPrecision(Double.valueOf(logged[2].toString()));
					if (day <= firstHalfDay) {
						firstHalfHour += hour;
					} else {
						secondHalfHour += hour;
					}
				}
			}

			JSONObject logged = new JSONObject();
			logged.put("firstHalfTotal", Constants.roundToDefaultPrecision(firstHalfHour));
			logged.put("secondHalfTotal", Constants.roundToDefaultPrecision(secondHalfHour));

			// For Billable Details
			List<TaskTrackApproval> approvalUserList = new ArrayList<TaskTrackApproval>();
			if (projectTier == 1) {
				List<TaskTrackApprovalFinal> approvalFinalList = taskTrackApprovalFinalRepository
						.getUserFinalApprovalList(userId, projectId, month, year);
				for (TaskTrackApprovalFinal taskTrackFinal : approvalFinalList) {
					approvalUserList.add(TaskTrackApproverConverter.finalApproverToApprover(taskTrackFinal));
				}
			} else {
				approvalUserList = timeTrackApprovalJPARepository.getUserListForApproval(userId, projectId, month,
						year);
			}

			firstHalfHour = 0.0;
			secondHalfHour = 0.0;

			if (approvalUserList != null && approvalUserList.size() > 0) {
				TaskTrackApproval billableData = new TaskTrackApproval();
				TaskTrackApproval overtimeData = new TaskTrackApproval();
				for (TaskTrackApproval task : approvalUserList) {
					if (task.getProjectType().equalsIgnoreCase("Billable")) {
						firstHalfStatus = task.getFirstHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getFirstHalfStatus();
						secondHalfStatus = task.getSecondHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getSecondHalfStatus();

						if (firstHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
								|| firstHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED)
								|| secondHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
								|| secondHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED)) {
							List<TaskTrackRejection> rejections = taskTrackRejectionRepository
									.findOpenRejectionForUserForProject(userId, projectId, month, year);
							for (TaskTrackRejection rejection : rejections) {
								if (rejection.getCycle().equals(Constants.TASKTRACK_REJECTION_FIRST_HALF_CYCLE)) {
									firstHalfRemarks = rejection.getRemark();
								} else if (rejection.getCycle()
										.equals(Constants.TASKTRACK_REJECTION_SECOND_HALF_CYCLE)) {
									secondHalfRemarks = rejection.getRemark();
								}
							}
						} else if (firstHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION)
								|| firstHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED)
								|| secondHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION)
								|| secondHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED)) {
							List<TaskTrackCorrection> corrections = taskTrackCorrectionRepository
									.findCorrectionDays(userId, projectId, month, year, 1, 31);
							for (TaskTrackCorrection correction : corrections) {
								if (firstHalfRemarks == null && correction.getDay() <= firstHalfDay) {
									firstHalfRemarks = correction.getComment();
									continue;
								}
								if (secondHalfRemarks == null && correction.getDay() > firstHalfDay) {
									secondHalfRemarks = correction.getComment();
								}
							}
						}
						billableData = task;
					} else if (task.getProjectType().equalsIgnoreCase("Overtime")) {
						overtimeData = task;
					}
				}

				List<Double> completeHourList = getApprovalTotalBillableHour(billableData, overtimeData);
				for (int i = 0; i < completeHourList.size(); i++) {
					if (i < firstHalfDay) {
						firstHalfHour += Constants.roundToDefaultPrecision(completeHourList.get(i));
					} else {
						secondHalfHour += Constants.roundToDefaultPrecision(completeHourList.get(i));
					}
				}
			}

			JSONObject billable = new JSONObject();
			billable.put("firstHalfTotal", Constants.roundToDefaultPrecision(firstHalfHour));
			billable.put("secondHalfTotal", Constants.roundToDefaultPrecision(secondHalfHour));

			if (userName == null || userName.isEmpty()) {
				String uName = userService.getUserName(userId);
				userName = String.valueOf(uName).replace(",", " ");
			}

			response.put("userId", userId);
			response.put("userName", userName);
			response.put("month", month);
			response.put("logged", logged);
			response.put("billable", billable);
			response.put("firstHalfStatus", firstHalfStatus);
			response.put("secondHalfStatus", secondHalfStatus);
		} else {
			response.put("userId", userId);
			String uName = userService.getUserName(userId);
			String userName = String.valueOf(uName).replace(",", " ");
			response.put("userName", userName);
			response.put("month", month);

			JSONObject totalHour = new JSONObject();
			totalHour.put("firstHalfTotal", firstHalfHour);
			totalHour.put("secondHalfTotal", secondHalfHour);

			response.put("logged", totalHour);
			response.put("billable", totalHour);
			response.put("firstHalfStatus", firstHalfStatus);
			response.put("secondHalfStatus", secondHalfStatus);
		}
		if (submissionDates != null) {
			response.put("firstHalfSubmissionDate", cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" : "") + month + "-"
					+ (submissionDates.getFirstApprovalDay() < 10 ? "0" : "") + submissionDates.getFirstApprovalDay());
			response.put("secondHalfSubmissionDate",
					cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" : "") + month + "-"
							+ (submissionDates.getSecondApprovalDay() < 10 ? "0" : "")
							+ submissionDates.getSecondApprovalDay());
		}

		response.put("firstHalfRemarks", firstHalfRemarks);
		response.put("secondHalfRemarks", secondHalfRemarks);
		return response;
	}

	private List<Double> getApprovalTotalBillableHour(TaskTrackApproval billableData, TaskTrackApproval overtimeData) {
		List<Double> completeHourList = new ArrayList<Double>();
		completeHourList.add(billableData.getDay1() + overtimeData.getDay1());
		completeHourList.add(billableData.getDay2() + overtimeData.getDay2());
		completeHourList.add(billableData.getDay3() + overtimeData.getDay3());
		completeHourList.add(billableData.getDay4() + overtimeData.getDay4());
		completeHourList.add(billableData.getDay5() + overtimeData.getDay5());
		completeHourList.add(billableData.getDay6() + overtimeData.getDay6());
		completeHourList.add(billableData.getDay7() + overtimeData.getDay7());
		completeHourList.add(billableData.getDay8() + overtimeData.getDay8());
		completeHourList.add(billableData.getDay9() + overtimeData.getDay9());
		completeHourList.add(billableData.getDay10() + overtimeData.getDay10());
		completeHourList.add(billableData.getDay11() + overtimeData.getDay11());
		completeHourList.add(billableData.getDay12() + overtimeData.getDay12());
		completeHourList.add(billableData.getDay13() + overtimeData.getDay13());
		completeHourList.add(billableData.getDay14() + overtimeData.getDay14());
		completeHourList.add(billableData.getDay15() + overtimeData.getDay15());
		completeHourList.add(billableData.getDay16() + overtimeData.getDay16());
		completeHourList.add(billableData.getDay17() + overtimeData.getDay17());
		completeHourList.add(billableData.getDay18() + overtimeData.getDay18());
		completeHourList.add(billableData.getDay19() + overtimeData.getDay19());
		completeHourList.add(billableData.getDay20() + overtimeData.getDay20());
		completeHourList.add(billableData.getDay21() + overtimeData.getDay21());
		completeHourList.add(billableData.getDay22() + overtimeData.getDay22());
		completeHourList.add(billableData.getDay23() + overtimeData.getDay23());
		completeHourList.add(billableData.getDay24() + overtimeData.getDay24());
		completeHourList.add(billableData.getDay25() + overtimeData.getDay25());
		completeHourList.add(billableData.getDay26() + overtimeData.getDay26());
		completeHourList.add(billableData.getDay27() + overtimeData.getDay27());
		completeHourList.add(billableData.getDay28() + overtimeData.getDay28());
		completeHourList.add(billableData.getDay29() + overtimeData.getDay29());
		completeHourList.add(billableData.getDay30() + overtimeData.getDay30());
		completeHourList.add(billableData.getDay31() + overtimeData.getDay31());
		return completeHourList;
	}

	@Override
	public List<JSONObject> getTimeTrackUserTaskDetailsByProject(Long id, Date startDate, Date endDate,
			List<Object[]> userList, List<JSONObject> loggedJsonArray, List<JSONObject> billableJsonArray,
			List<JSONObject> nonBillableJsonArray, List<JSONObject> timeTrackJSONData, Boolean isExist,
			Long projectId) {
		if (isExist) {
			JSONObject userListObject = new JSONObject();

			userList = getUserListByProject(id, startDate, endDate, projectId);

			loggedJsonArray = new ArrayList<>();

			String name = null;
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			for (int i = 0; i < diffInDays; i++) {
				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DATE);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				Double hours = 0.0;
				if (userList != null && userList.size() > 0) {
					JSONObject jsonObject = new JSONObject();
					for (Object[] item : userList) {

						String st = String.valueOf(item[3]);

						if (st.equals(vl)) {
							hours = hours + (Double) item[2];

						}
						name = (String) item[0] + " " + item[1];
					}
					jsonObject.put(vl, hours);
					cal.add(Calendar.DATE, 1);
					loggedJsonArray.add(jsonObject);

				}

				else {
					JSONObject jsonObject = new JSONObject();
					String uName = userService.getUserName(id);
					name = String.valueOf(uName).replace(",", " ");
					jsonObject.put(vl, 0);
					cal.add(Calendar.DATE, 1);
					loggedJsonArray.add(jsonObject);
				}

			}
			userListObject.put("userName", name);
			userListObject.put("userId", id);
			userListObject.put("month", intMonth);
			userListObject.put("logged", loggedJsonArray);

			name = null;
			cal.setTime(startDate);
			int monthIndex = (cal.get(Calendar.MONTH) + 1);
			int yearIndex = cal.get(Calendar.YEAR);

			List<TaskTrackApproval> approvalUserList = getUserListForApproval(id, projectId, monthIndex, yearIndex);
			billableJsonArray = new ArrayList<>();
			nonBillableJsonArray = new ArrayList<>();

			diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			intMonth = 0;
			intday = 0;
			Double hours = 0.0;
			if (approvalUserList != null && approvalUserList.size() > 0) {
				JSONObject jsonObject = new JSONObject();

				for (TaskTrackApproval item : approvalUserList) {
					cal.setTime(startDate);

					for (int i = 0; i < diffInDays; i++) {

						intMonth = (cal.get(Calendar.MONTH) + 1);
						intday = cal.get(Calendar.DATE);
						String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth)
								+ "-" + ((intday < 10) ? "0" + intday : "" + intday);

						if (i == 0)
							hours = (Double) item.getDay1();
						else if (i == 1)
							hours = (Double) item.getDay2();
						else if (i == 2)
							hours = (Double) item.getDay3();
						else if (i == 3)
							hours = (Double) item.getDay4();
						else if (i == 4)
							hours = (Double) item.getDay5();
						else if (i == 5)
							hours = (Double) item.getDay6();
						else if (i == 6)
							hours = (Double) item.getDay7();
						else if (i == 7)
							hours = (Double) item.getDay8();
						else if (i == 8)
							hours = (Double) item.getDay9();
						else if (i == 9)
							hours = (Double) item.getDay10();
						else if (i == 10)
							hours = (Double) item.getDay11();
						else if (i == 11)
							hours = (Double) item.getDay12();
						else if (i == 12)
							hours = (Double) item.getDay13();
						else if (i == 13)
							hours = (Double) item.getDay14();
						else if (i == 14)
							hours = (Double) item.getDay15();
						else if (i == 15)
							hours = (Double) item.getDay16();
						else if (i == 16)
							hours = (Double) item.getDay17();
						else if (i == 17)
							hours = (Double) item.getDay18();
						else if (i == 18)
							hours = (Double) item.getDay19();
						else if (i == 19)
							hours = (Double) item.getDay20();
						else if (i == 20)
							hours = (Double) item.getDay21();
						else if (i == 21)
							hours = (Double) item.getDay22();
						else if (i == 22)
							hours = (Double) item.getDay23();
						else if (i == 23)
							hours = (Double) item.getDay24();
						else if (i == 24)
							hours = (Double) item.getDay25();
						else if (i == 25)
							hours = (Double) item.getDay26();
						else if (i == 26)
							hours = (Double) item.getDay27();
						else if (i == 27)
							hours = (Double) item.getDay28();
						else if (i == 28)
							hours = (Double) item.getDay29();
						else if (i == 29)
							hours = (Double) item.getDay30();
						else if (i == 30)
							hours = (Double) item.getDay31();

						name = (String) item.getFirstName() + " " + item.getLastName();

						if (item.getProjectType().equals("Billable")) {
							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							billableJsonArray.add(jsonObject);
						}
						if (item.getProjectType().equals("Non-Billable")) {
							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							nonBillableJsonArray.add(jsonObject);
						}

						cal.add(Calendar.DATE, 1);

					}
				}
			} else {
				cal.setTime(startDate);
				for (int i = 0; i < diffInDays; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DATE);
					String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					JSONObject jsonObject = new JSONObject();
					jsonObject.put(vl, 0);
					billableJsonArray.add(jsonObject);
					nonBillableJsonArray.add(jsonObject);

					cal.add(Calendar.DATE, 1);

				}
			}
			userListObject.put("billable", billableJsonArray);
			userListObject.put("nonbillable", nonBillableJsonArray);

			timeTrackJSONData.add(userListObject);

		} else {
			loggedJsonArray = new ArrayList<>();
			billableJsonArray = new ArrayList<>();
			JSONObject userListObject = new JSONObject();

			String uName = userService.getUserName(id);
			String name = String.valueOf(uName).replace(",", " ");

			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			for (int i = 0; i < diffInDays; i++) {
				JSONObject jsonObject = new JSONObject();

				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DATE);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				jsonObject.put(vl, 0);
				cal.add(Calendar.DATE, 1);
				loggedJsonArray.add(jsonObject);
				billableJsonArray.add(jsonObject);
				nonBillableJsonArray.add(jsonObject);
			}
			userListObject.put("userName", name);
			userListObject.put("userId", id);
			userListObject.put("month", intMonth);
			userListObject.put("logged", loggedJsonArray);
			userListObject.put("billable", billableJsonArray);
			userListObject.put("nonbillable", nonBillableJsonArray);

			timeTrackJSONData.add(userListObject);
		}
		return timeTrackJSONData;
	}

	@Override
	public List<JSONObject> getTimeTrackUserProjectTaskDetails(Long projectId, String projectName, Date startDate,
			Date endDate, List<Object[]> projectList, List<JSONObject> loggedJsonArray,
			List<JSONObject> billableJsonArray, List<JSONObject> nonBillableJsonArray,
			List<JSONObject> timeTrackJSONData, Boolean isExist, Long userId) {
		if (isExist) {

			JSONObject userListObject = new JSONObject();

			projectList = getUserListByProject(userId, startDate, endDate, projectId);

			loggedJsonArray = new ArrayList<>();

			String name = null;
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			for (int i = 0; i < diffInDays; i++) {
				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DATE);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				Double hours = 0.0;
				if (projectList != null && projectList.size() > 0) {
					JSONObject jsonObject = new JSONObject();
					for (Object[] item : projectList) {

						String st = String.valueOf(item[3]);

						if (st.equals(vl)) {
							hours = hours + Constants.roundToDefaultPrecision((Double) item[2]);

						}
						name = (String) item[0] + " " + item[1];
					}
					jsonObject.put(vl, Constants.roundToDefaultPrecision(hours));
					cal.add(Calendar.DATE, 1);
					loggedJsonArray.add(jsonObject);

				}

				else {
					JSONObject jsonObject = new JSONObject();
					String uName = userService.getUserName(userId);
					name = String.valueOf(uName).replace(",", " ");
					jsonObject.put(vl, 0);
					cal.add(Calendar.DATE, 1);
					loggedJsonArray.add(jsonObject);
				}

			}
			if (projectName != null) {
				userListObject.put("projectName", projectName);
			} else {
				userListObject.put("userName", name);
			}
			userListObject.put("userId", userId);
			userListObject.put("month", intMonth);
			userListObject.put("logged", loggedJsonArray);

			name = null;
			cal.setTime(startDate);
			int monthIndex = (cal.get(Calendar.MONTH) + 1);
			int yearIndex = cal.get(Calendar.YEAR);

			List<TaskTrackApproval> approvalUserList = getUserListForApproval(userId, projectId, monthIndex, yearIndex);
			billableJsonArray = new ArrayList<>();
			nonBillableJsonArray = new ArrayList<>();

			diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			intMonth = 0;
			intday = 0;
			Double hours = 0.0;
			if (approvalUserList != null && approvalUserList.size() > 0) {
				JSONObject jsonObject = new JSONObject();

				for (TaskTrackApproval item : approvalUserList) {
					cal.setTime(startDate);

					for (int i = 0; i < diffInDays; i++) {

						intMonth = (cal.get(Calendar.MONTH) + 1);
						intday = cal.get(Calendar.DATE);
						String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth)
								+ "-" + ((intday < 10) ? "0" + intday : "" + intday);

						if (i == 0)
							hours = (Double) item.getDay1();
						else if (i == 1)
							hours = (Double) item.getDay2();
						else if (i == 2)
							hours = (Double) item.getDay3();
						else if (i == 3)
							hours = (Double) item.getDay4();
						else if (i == 4)
							hours = (Double) item.getDay5();
						else if (i == 5)
							hours = (Double) item.getDay6();
						else if (i == 6)
							hours = (Double) item.getDay7();
						else if (i == 7)
							hours = (Double) item.getDay8();
						else if (i == 8)
							hours = (Double) item.getDay9();
						else if (i == 9)
							hours = (Double) item.getDay10();
						else if (i == 10)
							hours = (Double) item.getDay11();
						else if (i == 11)
							hours = (Double) item.getDay12();
						else if (i == 12)
							hours = (Double) item.getDay13();
						else if (i == 13)
							hours = (Double) item.getDay14();
						else if (i == 14)
							hours = (Double) item.getDay15();
						else if (i == 15)
							hours = (Double) item.getDay16();
						else if (i == 16)
							hours = (Double) item.getDay17();
						else if (i == 17)
							hours = (Double) item.getDay18();
						else if (i == 18)
							hours = (Double) item.getDay19();
						else if (i == 19)
							hours = (Double) item.getDay20();
						else if (i == 20)
							hours = (Double) item.getDay21();
						else if (i == 21)
							hours = (Double) item.getDay22();
						else if (i == 22)
							hours = (Double) item.getDay23();
						else if (i == 23)
							hours = (Double) item.getDay24();
						else if (i == 24)
							hours = (Double) item.getDay25();
						else if (i == 25)
							hours = (Double) item.getDay26();
						else if (i == 26)
							hours = (Double) item.getDay27();
						else if (i == 27)
							hours = (Double) item.getDay28();
						else if (i == 28)
							hours = (Double) item.getDay29();
						else if (i == 29)
							hours = (Double) item.getDay30();
						else if (i == 30)
							hours = (Double) item.getDay31();

						name = (String) item.getFirstName() + " " + item.getLastName();

						if (item.getProjectType().equals("Billable")) {
							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							billableJsonArray.add(jsonObject);
						}
						if (item.getProjectType().equals("Non-Billable")) {
							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							nonBillableJsonArray.add(jsonObject);
						}
						cal.add(Calendar.DATE, 1);

					}
				}
			} else {
				cal.setTime(startDate);
				for (int i = 0; i < diffInDays; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DATE);
					String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					JSONObject jsonObject = new JSONObject();
					jsonObject.put(vl, 0);
					billableJsonArray.add(jsonObject);
					nonBillableJsonArray.add(jsonObject);
					cal.add(Calendar.DATE, 1);

				}
			}
			userListObject.put("billable", billableJsonArray);
			userListObject.put("nonbillable", nonBillableJsonArray);

			timeTrackJSONData.add(userListObject);

		} else {

			loggedJsonArray = new ArrayList<>();
			billableJsonArray = new ArrayList<>();
			JSONObject userListObject = new JSONObject();

			String uName = userService.getUserName(userId);
			String name = String.valueOf(uName).replace(",", " ");

			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			for (int i = 0; i < diffInDays; i++) {
				JSONObject jsonObject = new JSONObject();

				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DATE);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				jsonObject.put(vl, 0);
				cal.add(Calendar.DATE, 1);
				loggedJsonArray.add(jsonObject);
				billableJsonArray.add(jsonObject);
				nonBillableJsonArray.add(jsonObject);
			}
			userListObject.put("userName", name);
			userListObject.put("userId", userId);
			userListObject.put("month", intMonth);
			userListObject.put("logged", loggedJsonArray);
			userListObject.put("billable", billableJsonArray);
			userListObject.put("nonbillable", nonBillableJsonArray);

			timeTrackJSONData.add(userListObject);
		}
		return timeTrackJSONData;
	}

	private List<Object[]> getUserListByProject(Long id, Date startDate, Date endDate, Long projectId) {
		List<Object[]> userTaskList = taskRepository.getUserListByProject(id, startDate, endDate, projectId);
		return userTaskList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getApprovedUserTaskDetails(Long userId, Date startDate, Date endDate, Boolean isExist,
			Long projectId, Integer projectTier, Integer firstHalfDay) {
		JSONObject response = new JSONObject();

		List<JSONObject> loggedArray = new ArrayList<>();
		List<JSONObject> billableArray = new ArrayList<>();
		List<JSONObject> overTimeArray = new ArrayList<>();
		List<JSONObject> nonbillableArray = new ArrayList<>();
		List<JSONObject> beachArray = new ArrayList<>();
		List<Integer> correctionDays = new ArrayList<Integer>();

		String approvalStatus = null;
		String name = null;
		Long billableId = null;
		Long nonBillableId = null;
		Long overtimeId = null;
		Long beachId = null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		int endDay = cal.get(Calendar.DATE);
		cal.setTime(startDate);
		int monthIndex = (cal.get(Calendar.MONTH) + 1);
		int yearIndex = cal.get(Calendar.YEAR);
		int startDay = cal.get(Calendar.DATE);

		AllocationModel allocationObj = allocationRepository
				.findOneByProjectProjectIdAndUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIsBillableOrderByEndDateDesc(
						projectId, userId, endDate, startDate, true);

		Boolean isBillable = allocationObj != null ? true : false;

		if (isExist) {
			JSONObject temporaryObject = new JSONObject();
			// Logged Details
			List<Object[]> loggedList = getUserListByProject(userId, startDate, endDate, projectId);
			if (loggedList != null && loggedList.size() > 0) {
				Object[] user = loggedList.get(0);
				name = user[1] + " " + user[0];
			}
			temporaryObject = new JSONObject();
			for (int i = startDay; i <= endDay; i++) {
				int month = (cal.get(Calendar.MONTH) + 1);
				int day = cal.get(Calendar.DATE);
				String taskDate = cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : "" + month) + "-"
						+ (day < 10 ? "0" + day : "" + day);

				Double hours = 0.0;
				Boolean found = false;
				if (loggedList != null && loggedList.size() > 0) {
					for (Object[] item : loggedList) {
						String loggedDate = String.valueOf(item[3]);
						if (loggedDate.equals(taskDate)) {
							found = true;
							hours = hours + (Double) item[2];
							temporaryObject.put(taskDate, hours);
						}
					}
				}
				if (!found) {
					temporaryObject.put(taskDate, 0);
				}
				cal.add(Calendar.DATE, 1);
			}
			loggedArray.add(temporaryObject);

			// Approved Details
			List<TaskTrackApproval> approvalUserList = new ArrayList<TaskTrackApproval>();
			if (projectTier == 1) {
				List<TaskTrackApprovalFinal> approvalFinalList = taskTrackApprovalFinalRepository
						.getUserFinalApprovalList(userId, projectId, monthIndex, yearIndex);
				for (TaskTrackApprovalFinal taskTrackFinal : approvalFinalList) {
					approvalUserList.add(TaskTrackApproverConverter.finalApproverToApprover(taskTrackFinal));
				}
			} else {
				approvalUserList = timeTrackApprovalJPARepository.getUserListForApproval(userId, projectId, monthIndex,
						yearIndex);
			}

			Double hours = 0.0;
			if (approvalUserList != null && approvalUserList.size() > 0) {
				if (name == null || name.isEmpty()) {
					name = approvalUserList.get(0).getFirstName() + " " + approvalUserList.get(0).getLastName();
				}
				for (TaskTrackApproval item : approvalUserList) {
					cal.setTime(startDate);
					for (int i = startDay; i <= endDay; i++) {
						int month = (cal.get(Calendar.MONTH) + 1);
						int day = cal.get(Calendar.DATE);
						String taskDate = cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : "" + month) + "-"
								+ (day < 10 ? "0" + day : "" + day);

						switch (i) {
						case 1:
							hours = (Double) item.getDay1();
							break;
						case 2:
							hours = (Double) item.getDay2();
							break;
						case 3:
							hours = (Double) item.getDay3();
							break;
						case 4:
							hours = (Double) item.getDay4();
							break;
						case 5:
							hours = (Double) item.getDay5();
							break;
						case 6:
							hours = (Double) item.getDay6();
							break;
						case 7:
							hours = (Double) item.getDay7();
							break;
						case 8:
							hours = (Double) item.getDay8();
							break;
						case 9:
							hours = (Double) item.getDay9();
							break;
						case 10:
							hours = (Double) item.getDay10();
							break;
						case 11:
							hours = (Double) item.getDay11();
							break;
						case 12:
							hours = (Double) item.getDay12();
							break;
						case 13:
							hours = (Double) item.getDay13();
							break;
						case 14:
							hours = (Double) item.getDay14();
							break;
						case 15:
							hours = (Double) item.getDay15();
							break;
						case 16:
							hours = (Double) item.getDay16();
							break;
						case 17:
							hours = (Double) item.getDay17();
							break;
						case 18:
							hours = (Double) item.getDay18();
							break;
						case 19:
							hours = (Double) item.getDay19();
							break;
						case 20:
							hours = (Double) item.getDay20();
							break;
						case 21:
							hours = (Double) item.getDay21();
							break;
						case 22:
							hours = (Double) item.getDay22();
							break;
						case 23:
							hours = (Double) item.getDay23();
							break;
						case 24:
							hours = (Double) item.getDay24();
							break;
						case 25:
							hours = (Double) item.getDay25();
							break;
						case 26:
							hours = (Double) item.getDay26();
							break;
						case 27:
							hours = (Double) item.getDay27();
							break;
						case 28:
							hours = (Double) item.getDay28();
							break;
						case 29:
							hours = (Double) item.getDay29();
							break;
						case 30:
							hours = (Double) item.getDay30();
							break;
						case 31:
							hours = (Double) item.getDay31();
							break;
						}

						if (item.getProjectType().equals("Billable")) {
							temporaryObject = new JSONObject();
							temporaryObject.put(taskDate, hours);
							billableArray.add(temporaryObject);
							billableId = item.getId();
						} else if (item.getProjectType().equals("Non-Billable")) {
							temporaryObject = new JSONObject();
							temporaryObject.put(taskDate, hours);
							nonbillableArray.add(temporaryObject);
							nonBillableId = item.getId();
						} else if (item.getProjectType().equals("Overtime")) {
							temporaryObject = new JSONObject();
							temporaryObject.put(taskDate, hours);
							overTimeArray.add(temporaryObject);
							overtimeId = item.getId();
						} else if (item.getProjectType().equals("Beach")) {
							temporaryObject = new JSONObject();
							temporaryObject.put(taskDate, hours);
							beachArray.add(temporaryObject);
							beachId = item.getId();
						}
						cal.add(Calendar.DATE, 1);
					}
					if (item.getProjectType().equalsIgnoreCase("Billable")) {
						if (startDay <= firstHalfDay) {
							approvalStatus = item.getFirstHalfStatus();
						} else {
							approvalStatus = item.getSecondHalfStatus();
						}
						if (approvalStatus != null
								&& (approvalStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION)
										|| approvalStatus.equalsIgnoreCase(
												Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED))) {
							List<TaskTrackCorrection> corrections = taskTrackCorrectionRepository.findCorrectionDays(
									item.getUser().getUserId(), item.getProject().getProjectId(), item.getMonth(),
									item.getYear(), startDay, endDay);
							System.out.println("size" + corrections);
							for (TaskTrackCorrection correction : corrections) {
								correctionDays.add(correction.getDay());
							}
						}
					}
				}
			} else {
				cal.setTime(startDate);
				for (int i = startDay; i <= endDay; i++) {
					int month = (cal.get(Calendar.MONTH) + 1);
					int day = cal.get(Calendar.DATE);
					String taskDate = cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : "" + month) + "-"
							+ (day < 10 ? "0" + day : "" + day);

					JSONObject jsonObject = new JSONObject();
					jsonObject.put(taskDate, 0);
					billableArray.add(jsonObject);
					nonbillableArray.add(jsonObject);
					overTimeArray.add(jsonObject);
					beachArray.add(jsonObject);

					cal.add(Calendar.DATE, 1);
				}
			}
			response.put("logged", loggedArray);
			response.put("billable", billableArray);
			response.put("nonBillable", nonbillableArray);
			response.put("overtime", overTimeArray);
			response.put("beach", beachArray);
			response.put("billableId", billableId);
			response.put("nonBillableId", nonBillableId);
			response.put("overtimeId", overtimeId);
			response.put("beachId", beachId);
			response.put("updatedBy", null);
		} else {
			cal.setTime(startDate);
			for (int i = startDay; i <= endDay; i++) {
				JSONObject temporaryObject = new JSONObject();
				int month = (cal.get(Calendar.MONTH) + 1);
				int day = cal.get(Calendar.DATE);
				String vl = cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : "" + month) + "-"
						+ (day < 10 ? "0" + day : "" + day);

				temporaryObject.put(vl, 0);
				loggedArray.add(temporaryObject);
				billableArray.add(temporaryObject);
				nonbillableArray.add(temporaryObject);
				overTimeArray.add(temporaryObject);
				beachArray.add(temporaryObject);

				cal.add(Calendar.DATE, 1);
			}

			response.put("logged", loggedArray);
			response.put("billable", billableArray);
			;
			response.put("nonBillable", nonbillableArray);
			response.put("overtime", overTimeArray);
			response.put("beach", beachArray);
			response.put("billableId", null);
			response.put("nonBillableId", null);
			response.put("overtimeId", null);
			response.put("beachId", null);
			response.put("updatedBy", null);
		}
		if (name == null || name.isEmpty()) {
			String uName = userService.getUserName(userId);
			name = String.valueOf(uName).replace(",", " ");
		}
		response.put("userName", name);
		response.put("userId", userId);
		response.put("month", monthIndex);
		response.put("isBillable", isBillable);
		response.put("approvalStatus", approvalStatus);
		response.put("correctionDays", correctionDays);
		return response;
	}

	public List<TaskTrackApproval> getUserListForApproval(Long id, Long projectId, Integer monthIndex,
			Integer yearIndex) {

		List<TaskTrackApproval> userList = timeTrackApprovalRepository.getUserListForApproval(id, projectId, monthIndex,
				yearIndex);

		return userList;
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
	public TaskTrackApproval findById(Long billableId) {
		TaskTrackApproval taskTrackApproval = timeTrackApprovalJPARepository.getOne(billableId);
		return taskTrackApproval;
	}

	@Override
	public TaskTrackApproval updateData(TaskTrackApproval taskTrackApproval) {
		return timeTrackApprovalJPARepository.save(taskTrackApproval);

	}

	@Override
	public TaskTrackApproval save(TaskTrackApproval taskTrackApproval) {
		return timeTrackApprovalJPARepository.save(taskTrackApproval);
	}

	@Override
	public TaskTrackApprovalLevel2 findById2(Long billableId) {
		// TODO Auto-generated method stub
		TaskTrackApprovalLevel2 taskTrackApproval = timeTrackApprovalLevel2.getOne(billableId);
		return taskTrackApproval;
	}

	@Override
	public TaskTrackApprovalLevel2 updateDatas(TaskTrackApprovalLevel2 taskTrackApproval) {
		// TODO Auto-generated method stub
		return timeTrackApprovalLevel2.save(taskTrackApproval);
	}

	@Override
	public TaskTrackApprovalLevel2 saveLevel2(TaskTrackApprovalLevel2 taskTrackApproval) {
		// TODO Auto-generated method stub
		return timeTrackApprovalLevel2.save(taskTrackApproval);
	}

	@Override
	public JSONObject getApproveddatalevel2(Long userId, Date startDate, Date endDate, List<TaskTrackApproval> userList,
			List<JSONObject> jsonArray, List<JSONObject> approvalJSONData, Boolean isExist, Long projectId) {

		List<JSONObject> billableArray = new ArrayList<>();
		List<JSONObject> overTimeArray = new ArrayList<>();
		List<JSONObject> nonbillableArray = new ArrayList<>();
		List<JSONObject> beachArray = new ArrayList<>();
		JSONObject userListObject = new JSONObject();
		List<JSONObject> jsonDataRes1 = new ArrayList<JSONObject>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		int monthIndex = (cal.get(Calendar.MONTH) + 1);
		int yearIndex = cal.get(Calendar.YEAR);
		List<TaskTrackApprovalLevel2> approvedData = timeTrackApprovalLevel2.getApprovedData(userId, monthIndex,
				yearIndex, projectId);
		jsonArray = new ArrayList<>();

		String name = null;
		Long billableId = null, nonBillableId = null, beachId = null, overtimeId = null, updatedBy = null;

		int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
		int intMonth = 0, intday = 0;

		Double hours = 0.0;
		if (approvedData != null && approvedData.size() > 0) {
			JSONObject jsonObject = new JSONObject();

			for (TaskTrackApprovalLevel2 item : approvedData) {
				cal.setTime(startDate);

				for (int i = 0; i < diffInDays; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DATE);
					String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if (i == 0)
						hours = (Double) item.getDay1();
					else if (i == 1)
						hours = (Double) item.getDay2();
					else if (i == 2)
						hours = (Double) item.getDay3();
					else if (i == 3)
						hours = (Double) item.getDay4();
					else if (i == 4)
						hours = (Double) item.getDay5();
					else if (i == 5)
						hours = (Double) item.getDay6();
					else if (i == 6)
						hours = (Double) item.getDay7();
					else if (i == 7)
						hours = (Double) item.getDay8();
					else if (i == 8)
						hours = (Double) item.getDay9();
					else if (i == 9)
						hours = (Double) item.getDay10();
					else if (i == 10)
						hours = (Double) item.getDay11();
					else if (i == 11)
						hours = (Double) item.getDay12();
					else if (i == 12)
						hours = (Double) item.getDay13();
					else if (i == 13)
						hours = (Double) item.getDay14();
					else if (i == 14)
						hours = (Double) item.getDay15();
					else if (i == 15)
						hours = (Double) item.getDay16();
					else if (i == 16)
						hours = (Double) item.getDay17();
					else if (i == 17)
						hours = (Double) item.getDay18();
					else if (i == 18)
						hours = (Double) item.getDay19();
					else if (i == 19)
						hours = (Double) item.getDay20();
					else if (i == 20)
						hours = (Double) item.getDay21();
					else if (i == 21)
						hours = (Double) item.getDay22();
					else if (i == 22)
						hours = (Double) item.getDay23();
					else if (i == 23)
						hours = (Double) item.getDay24();
					else if (i == 24)
						hours = (Double) item.getDay25();
					else if (i == 25)
						hours = (Double) item.getDay26();
					else if (i == 26)
						hours = (Double) item.getDay27();
					else if (i == 27)
						hours = (Double) item.getDay28();
					else if (i == 28)
						hours = (Double) item.getDay29();
					else if (i == 29)
						hours = (Double) item.getDay30();
					else if (i == 30)
						hours = (Double) item.getDay31();

					name = (String) item.getUser().getFirstName() + " " + item.getUser().getLastName();
					updatedBy = item.getUpdatedBy();

					if (item.getProjectType().equals("Billable")) {
						jsonObject = new JSONObject();
						jsonObject.put(vl, hours);
						billableArray.add(jsonObject);
						billableId = item.getId();
					} else if (item.getProjectType().equals("Non-Billable")) {
						jsonObject = new JSONObject();
						jsonObject.put(vl, hours);
						nonbillableArray.add(jsonObject);
						nonBillableId = item.getId();
					} else if (item.getProjectType().equals("Beach")) {
						jsonObject = new JSONObject();
						jsonObject.put(vl, hours);
						beachArray.add(jsonObject);
						beachId = item.getId();
					} else if (item.getProjectType().equals("Overtime")) {
						jsonObject = new JSONObject();
						jsonObject.put(vl, hours);
						overTimeArray.add(jsonObject);
						overtimeId = item.getId();
					}
					cal.add(Calendar.DATE, 1);

				}
			}
		} else {
			cal.setTime(startDate);
			for (int i = 0; i < diffInDays; i++) {

				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DATE);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				JSONObject jsonObject = new JSONObject();
				jsonObject.put(vl, 0);
				billableArray.add(jsonObject);
				nonbillableArray.add(jsonObject);
				beachArray.add(jsonObject);
				overTimeArray.add(jsonObject);

				cal.add(Calendar.DATE, 1);

			}
		}

		userListObject.put("userName", name);
		userListObject.put("userId", userId);
		userListObject.put("month", intMonth);
		userListObject.put("billable", billableArray);
		;
		userListObject.put("nonBillable", nonbillableArray);
		userListObject.put("beach", beachArray);
		userListObject.put("overtime", overTimeArray);
		userListObject.put("billableId", billableId);
		userListObject.put("nonBillableId", nonBillableId);
		userListObject.put("beachId", beachId);
		userListObject.put("overtimeId", overtimeId);
		userListObject.put("updatedBy", updatedBy);
		jsonDataRes1.add(userListObject);

		return userListObject;
		// return null;
	}

	@Override
	public void saveLevel3(TaskTrackApprovalFinance taskTrackApproval) {
		// TODO Auto-generated method stub
		taskTrackFinanceRepository.save(taskTrackApproval);
	}

	@Override
	public JSONObject getApproveddatalevel2toFinance(Long userId, Long logUser, int monthIndex, int yearIndex,
			Long projectId) {
		// TODO Auto-generated method stub

		int flagExist = 0;
		JSONObject userListObject = new JSONObject();
		JSONObject testValidation = new JSONObject();
		JSONObject jsonDataMessageDetails = new JSONObject();
		boolean timesheet_button = false;
		testValidation = checkPreviousTimeSheetsareClosed(monthIndex, yearIndex, projectId, logUser);

		String status = null;
		if ((boolean) testValidation.get("data")) {

			List<TaskTrackApprovalLevel2> approvedData = timeTrackApprovalLevel2.getApprovedData(userId, monthIndex,
					yearIndex, projectId);

			List<TaskTrackApprovalFinance> data = taskTrackFinanceRepository.getDatas(userId, monthIndex, yearIndex,
					projectId);
			if (!data.isEmpty()) {

				flagExist = 1;

			}

			YearMonth yearMonthObject = YearMonth.of(yearIndex, monthIndex);
			int totaldays = yearMonthObject.lengthOfMonth();
			// Date fdate = new Date();
			String forwarded_date = null;
			Date date1 = null;
			if (approvedData != null && approvedData.size() > 0) {

				if (flagExist == 0) {
					status = "HM";
					forwarded_date = yearIndex + "-" + monthIndex + "-" + "15";
					try {
						date1 = new SimpleDateFormat("yyyy-MM-dd").parse(forwarded_date);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for (TaskTrackApprovalLevel2 item : approvedData) {

						TaskTrackApprovalFinance finance = new TaskTrackApprovalFinance();
						TaskTrackApprovalLevel2 level2 = tasktrackApprovalService.findById2(item.getId());
						level2.setForwarded_date(date1);
						// level2.setStatus("");
						UserModel user = userService.getUserDetailsById(userId);
						ProjectModel project = projectService.getProjectId(projectId);
						finance.setProject(project);
						finance.setProjectType(item.getProjectType());
						finance.setApprover_level2(level2);
						finance.setStatus(status);
						finance.setMonth(monthIndex);
						finance.setYear(yearIndex);
						finance.setDay1(item.getDay1());
						finance.setDay2(item.getDay2());
						finance.setDay3(item.getDay3());
						finance.setDay4(item.getDay4());
						finance.setDay5(item.getDay5());
						finance.setDay6(item.getDay6());
						finance.setDay7(item.getDay7());
						finance.setDay8(item.getDay8());
						finance.setDay9(item.getDay9());
						finance.setDay10(item.getDay10());
						finance.setDay11(item.getDay11());
						finance.setDay12(item.getDay12());
						finance.setDay13(item.getDay13());
						finance.setDay14(item.getDay14());
						finance.setDay15(item.getDay15());
						finance.setUser(user);
						// cal.setTime(startDate);
						taskTrackFinanceRepository.save(finance);
						timeTrackApprovalLevel2.save(level2);
					}
				} else {

					status = "FM";
					for (TaskTrackApprovalFinance eachdata : data) {
						forwarded_date = yearIndex + "-" + monthIndex + "-" + totaldays;
						try {
							date1 = new SimpleDateFormat("yyyy-MM-dd").parse(forwarded_date);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (eachdata.getStatus().equalsIgnoreCase("HM")) {

							for (TaskTrackApprovalLevel2 item : approvedData) {

								if (eachdata.getProjectType().equalsIgnoreCase("Non-Billable")
										&& item.getProjectType().equalsIgnoreCase("Non-Billable")) {

									TaskTrackApprovalLevel2 level2 = tasktrackApprovalService.findById2(item.getId());
									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);
										level2.setForwarded_date(date1);
										timeTrackApprovalLevel2.save(level2);

									}
								}
								if (eachdata.getProjectType().equalsIgnoreCase("Billable")
										&& item.getProjectType().equalsIgnoreCase("Billable")) {
									TaskTrackApprovalLevel2 level2 = tasktrackApprovalService.findById2(item.getId());
									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);
										level2.setForwarded_date(date1);
										timeTrackApprovalLevel2.save(level2);

									}
								}
								if (eachdata.getProjectType().equalsIgnoreCase("Overtime")
										&& item.getProjectType().equalsIgnoreCase("Overtime")) {
									TaskTrackApprovalLevel2 level2 = tasktrackApprovalService.findById2(item.getId());
									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);
										level2.setForwarded_date(date1);
										timeTrackApprovalLevel2.save(level2);

									}
								}
								if (eachdata.getProjectType().equalsIgnoreCase("Beach")
										&& item.getProjectType().equalsIgnoreCase("Beach")) {

									TaskTrackApprovalLevel2 level2 = tasktrackApprovalService.findById2(item.getId());
									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);
										eachdata.setStatus(status);
										level2.setForwarded_date(date1);
									}
								}
								taskTrackFinanceRepository.save(eachdata);
							}
						}
					}

				}
			}

			Object[] approved_date = timeTrackApprovalLevel2.getapprovedDates(monthIndex, yearIndex, projectId, userId);
			// adding closetime button drishya

			int approved_dayindex = 0;
			if (approved_date != null) {
				if (approved_date[0] != null) {
					Date approved_date_2 = (Date) approved_date[0];
					Calendar cal = Calendar.getInstance();
					cal.setTime(approved_date_2);
					approved_dayindex = cal.get(Calendar.DATE);
					if ((approved_dayindex >= 15) && (status.equalsIgnoreCase(""))) {

						timesheet_button = true;
					} else if ((approved_dayindex >= totaldays) && (status.equalsIgnoreCase("HM"))) {

						timesheet_button = true;
					}
				}
			}

			// for showing status
			// Level 2 approver name

			ProjectModel projectdetails = null;
			boolean flaglevel2 = true;
			if (projectId != null) {
				// System.out.println("Here____________________________");
				projectdetails = getProjectDetails(projectId);
			}
			if (projectdetails != null) {
				if (projectdetails.getOnsite_lead() != null)

				{
					System.out.println("------------------------------------------------1");

					jsonDataMessageDetails.put("Level2_Approvar_Name", projectdetails.getOnsite_lead().getFirstName()
							+ " " + projectdetails.getOnsite_lead().getLastName());
				} else {

					flaglevel2 = false;
					jsonDataMessageDetails.put("Level2_Approvar_Name", "");
				}

				// level2 forwarded
				String frowardedDate = "";
				String frowardedDateLevel2 = "";
				String finance_status_message = "Timesheet not yet submitted to finance";
				String forwarded_ToLevel2_Status = "";
				if (flaglevel2) {
					forwarded_ToLevel2_Status = "Timesheet not yet forwarded to Level2";
				}
				// getb finance status of the current project added on 11/10

				Object[] finance_status = tasktrackApprovalService.getFinanceStatusOfCurrentProject(projectId, userId,
						monthIndex, yearIndex);

				if (finance_status != null) {
					System.out.println("---------------------------------------0");
					if (finance_status.length > 0) {
						System.out.println("---------------------------------------1");
						if (finance_status[0].equals("HM")) {
							System.out.println("---------------------------------------2");
							finance_status_message = "Submitted mid report";
						} else if (finance_status[0].equals("FM")) {
							System.out.println("---------------------------------------3");
							finance_status_message = "Submitted Final report";
						}

					}
				}
				jsonDataMessageDetails.put("Status", finance_status_message);
				//
				// level2 forwarded

				List<Object[]> level1 = tasktrackApprovalService.getForwardedDates(projectId, userId, monthIndex,
						yearIndex);
				if (!level1.isEmpty()) {
					// System.out.println("forwarded_date"+level1.get(0));
					if (level1 != null) {
						for (Object[] fl : level1) {
							if (fl != null) {
								if (fl[0] != null) {
									Date fdate = (Date) fl[0];
									System.out.println("---------------------------------------4");
									String pattern1 = "MM-dd-yyyy";
									DateFormat df1 = new SimpleDateFormat(pattern1);
									String forw = df1.format(fdate);
									forwarded_ToLevel2_Status = "Data upto " + forw + " has been forwarded to Level2";
								}
								if (fl[1] != null) {
									Date fdates = (Date) fl[1];

								}
							}
						}
					} // System.out.println("frowardedDate___________"+frowardedDate);

				}
				jsonDataMessageDetails.put("Forwarded_status", forwarded_ToLevel2_Status);
			}

			// end

			//
			String displyDay = null;
			String mon;

			// adding 0 before month if it is less than 10th month
			if (monthIndex < 10) {
				mon = "0" + monthIndex;
			} else {
				mon = "" + monthIndex;
			}

			// return 15 if mhalf month
			if (status.equals("HM")) {

				displyDay = yearIndex + "-" + mon + "-15";
			} else if (status.equals("FM")) // return month end if Full Month
			{
				displyDay = yearIndex + "-" + mon + "-" + totaldays;
			}
			userListObject.put("data", "success");
			userListObject.put("status", "success");
			userListObject.put("message", "forwarded to finance");
			userListObject.put("forwadedDate", displyDay);
			userListObject.put("buttonStatus", status);
			userListObject.put("timesheet_button", timesheet_button);
		} else {
			userListObject.put("data", "failed");
			userListObject.put("status", "success");
			userListObject.put("message", testValidation.get("message"));
			userListObject.put("buttonStatus", status);
			userListObject.put("timesheet_button", timesheet_button);
		}

		return userListObject;
	}

	@Override
	public JSONObject getApproveddatalevel1toFinance(Long userId, Long logUser, int monthIndex, int yearIndex,
			Long projectId) {
		// TODO Auto-generated method stub

		JSONObject testValidation = new JSONObject();
		JSONObject jsonDataMessageDetails = new JSONObject();
		String status = null;
		boolean timesheet_button = false;
		testValidation = checkPreviousTimeSheetsareClosed(monthIndex, yearIndex, projectId, logUser);
		JSONObject userListObject = new JSONObject();
		if ((boolean) testValidation.get("data")) {
			int flagExist = 0;
			List<TaskTrackApproval> approvedData = tasktrackRepository.getApprovedData(userId, monthIndex, yearIndex,
					projectId);

			List<TaskTrackApprovalFinance> data = taskTrackFinanceRepository.getDatas(userId, monthIndex, yearIndex,
					projectId);
			if (!data.isEmpty()) {

				flagExist = 1;

			}

			YearMonth yearMonthObject = YearMonth.of(yearIndex, monthIndex);
			int totaldays = yearMonthObject.lengthOfMonth();
			// Date fdate = new Date();
			String forwarded_date = null;
			Date date1 = null;

			if (approvedData != null && approvedData.size() > 0) {

				if (flagExist == 0) {
					status = "HM";
					forwarded_date = yearIndex + "-" + monthIndex + "-" + "15";
					try {
						date1 = new SimpleDateFormat("yyyy-MM-dd").parse(forwarded_date);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					for (TaskTrackApproval item : approvedData) {
						TaskTrackApprovalFinance finance = new TaskTrackApprovalFinance();
						TaskTrackApproval level1 = tasktrackApprovalService.findById(item.getId());
						// level1.setForwarded_date(yesterday);
						// level1.setForwarded_finance(date1);
						UserModel user = userService.getUserDetailsById(userId);
						ProjectModel project = projectService.getProjectId(projectId);
						finance.setProject(project);
						finance.setProjectType(item.getProjectType());
						finance.setApprover_level1(level1);
						finance.setStatus(status);
						finance.setMonth(monthIndex);
						finance.setYear(yearIndex);
						finance.setDay1(item.getDay1());
						finance.setDay2(item.getDay2());
						finance.setDay3(item.getDay3());
						finance.setDay4(item.getDay4());
						finance.setDay5(item.getDay5());
						finance.setDay6(item.getDay6());
						finance.setDay7(item.getDay7());
						finance.setDay8(item.getDay8());
						finance.setDay9(item.getDay9());
						finance.setDay10(item.getDay10());
						finance.setDay11(item.getDay11());
						finance.setDay12(item.getDay12());
						finance.setDay13(item.getDay13());
						finance.setDay14(item.getDay14());
						finance.setDay15(item.getDay15());
						finance.setUser(user);
						// cal.setTime(startDate);
						taskTrackFinanceRepository.save(finance);
						timeTrackApprovalJPARepository.save(level1);
					}
				} else {
					status = "FM";

					for (TaskTrackApprovalFinance eachdata : data) {
						if (eachdata.getStatus().equalsIgnoreCase("HM")) {

							forwarded_date = yearIndex + "-" + monthIndex + "-" + totaldays;
							try {
								date1 = new SimpleDateFormat("yyyy-MM-dd").parse(forwarded_date);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							;

							for (TaskTrackApproval item : approvedData) {
								if (eachdata.getProjectType().equalsIgnoreCase("Non-Billable")
										&& item.getProjectType().equalsIgnoreCase("Non-Billable")) {

									TaskTrackApproval level1 = tasktrackApprovalService.findById(item.getId());
									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);
										// level1.setForwarded_finance(date1);
										timeTrackApprovalJPARepository.save(level1);

									}
								}
								if (eachdata.getProjectType().equalsIgnoreCase("Billable")
										&& item.getProjectType().equalsIgnoreCase("Billable")) {

									TaskTrackApproval level1 = tasktrackApprovalService.findById(item.getId());
									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);
										// level1.setForwarded_finance(date1);
										timeTrackApprovalJPARepository.save(level1);

									}
								}
								if (eachdata.getProjectType().equalsIgnoreCase("Overtime")
										&& item.getProjectType().equalsIgnoreCase("Overtime")) {

									TaskTrackApproval level1 = tasktrackApprovalService.findById(item.getId());
									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);
										// level1.setForwarded_finance(date1);
										timeTrackApprovalJPARepository.save(level1);

									}
								}
								if (eachdata.getProjectType().equalsIgnoreCase("Beach")
										&& item.getProjectType().equalsIgnoreCase("Beach")) {

									TaskTrackApproval level1 = tasktrackApprovalService.findById(item.getId());
									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);
										// level1.setForwarded_finance(date1);
										timeTrackApprovalJPARepository.save(level1);

									}
								}
								taskTrackFinanceRepository.save(eachdata);
							}
						}
					}
				}

				// add timesheet_button drishya

				int approved_dayindex = 0;
				Object[] approvedDate = timeTrackApprovalJPARepository.getapprovedDates(monthIndex, yearIndex,
						projectId, userId);

				if (approvedDate != null) {
					if (approvedDate[0] != null) {
						Date approved_date_1 = (Date) approvedDate[0];

						Calendar cal = Calendar.getInstance();
						cal.setTime(approved_date_1);
						approved_dayindex = cal.get(Calendar.DATE);
						if ((approved_dayindex >= 15) && (status.equalsIgnoreCase(""))) {

							timesheet_button = true;
						} else if ((approved_dayindex >= totaldays) && (status.equalsIgnoreCase("HM"))) {

							timesheet_button = true;
						}
					}
				}

				// for showing status
				// Level 2 approver name

				ProjectModel projectdetails = null;
				boolean flaglevel2 = true;
				if (projectId != null) {
					// System.out.println("Here____________________________");
					projectdetails = getProjectDetails(projectId);
				}
				if (projectdetails != null) {
					if (projectdetails.getOnsite_lead() != null)

					{
						System.out.println("------------------------------------------------1");

						jsonDataMessageDetails.put("Level2_Approvar_Name",
								projectdetails.getOnsite_lead().getFirstName() + " "
										+ projectdetails.getOnsite_lead().getLastName());
					} else {

						flaglevel2 = false;
						jsonDataMessageDetails.put("Level2_Approvar_Name", "");
					}

					// level2 forwarded
					String frowardedDate = "";
					String frowardedDateLevel2 = "";
					String finance_status_message = "Timesheet not yet submitted to finance";
					String forwarded_ToLevel2_Status = "";
					if (flaglevel2) {
						forwarded_ToLevel2_Status = "Timesheet not yet forwarded to Level2";
					}
					// getb finance status of the current project added on 11/10

					Object[] finance_status = tasktrackApprovalService.getFinanceStatusOfCurrentProject(projectId,
							userId, monthIndex, yearIndex);

					if (finance_status != null) {
						System.out.println("---------------------------------------0");
						if (finance_status.length > 0) {
							System.out.println("---------------------------------------1");
							if (finance_status[0].equals("HM")) {
								System.out.println("---------------------------------------2");
								finance_status_message = "Submitted mid report";
							} else if (finance_status[0].equals("FM")) {
								System.out.println("---------------------------------------3");
								finance_status_message = "Submitted Final report";
							}

						}
					}
					jsonDataMessageDetails.put("Status", finance_status_message);
					//
					// level2 forwarded

					List<Object[]> level1 = tasktrackApprovalService.getForwardedDates(projectId, userId, monthIndex,
							yearIndex);
					if (!level1.isEmpty()) {
						// System.out.println("forwarded_date"+level1.get(0));
						if (level1 != null) {
							for (Object[] fl : level1) {
								if (fl != null) {
									if (fl[0] != null) {
										Date fdate = (Date) fl[0];
										System.out.println("---------------------------------------4");
										String pattern1 = "MM-dd-yyyy";
										DateFormat df1 = new SimpleDateFormat(pattern1);
										String forw = df1.format(fdate);
										forwarded_ToLevel2_Status = "Data upto " + forw
												+ " has been forwarded to Level2";
									}
									if (fl[1] != null) {
										Date fdates = (Date) fl[1];

									}
								}
							}
						} // System.out.println("frowardedDate___________"+frowardedDate);

					}
					jsonDataMessageDetails.put("Forwarded_status", forwarded_ToLevel2_Status);
				}

				// end

				//
				String displyDay = null;
				String mon;

				// adding 0 before month if it is less than 10th month
				if (monthIndex < 10) {
					mon = "0" + monthIndex;
				} else {
					mon = "" + monthIndex;
				}

				// return 15 if mhalf month

				if (status != null && status.equals("HM")) {

					displyDay = yearIndex + "-" + mon + "-15";
				} else if (status != null && status.equals("FM")) // return month end if Full Month
				{
					displyDay = yearIndex + "-" + mon + "-" + totaldays;
				}

				userListObject.put("data", "success");
				userListObject.put("status", "success");
				userListObject.put("message", "forwarded to finance");
				userListObject.put("forwadedDate", displyDay);
				userListObject.put("buttonStatus", status);
				userListObject.put("timesheet_button", timesheet_button);
				userListObject.put("message_details", jsonDataMessageDetails);
			} // ---Rinu--//
			else // no data found
			{
				String displyDay = null;
				String mon;

				// adding 0 before month if it is less than 10th month
				if (monthIndex < 10) {
					mon = "0" + monthIndex;
				} else {
					mon = "" + monthIndex;
				}

				displyDay = yearIndex + "-" + mon + "-15";

				userListObject.put("data", "failed");
				userListObject.put("status", "success");
				userListObject.put("message", "No Data Found in Approval section!!");
				userListObject.put("forwadedDate", displyDay);
				userListObject.put("buttonStatus", status);
				userListObject.put("timesheet_button", timesheet_button);
				return userListObject;

			}
		} else {
			userListObject.put("data", "failed");
			userListObject.put("status", "success");
			userListObject.put("message", testValidation.get("message"));
			userListObject.put("buttonStatus", status);
		}

		return userListObject;
	}

	@Override
	public List<JSONObject> getTimeTrackUserTaskDetailsLevel2(Long id, Date startDate, Date endDate,
			List<Object[]> userList, List<JSONObject> loggedJsonArray, List<JSONObject> billableJsonArrayLogged,
			List<JSONObject> timeTrackJSONData, Boolean isExist, Long projectId) {

		List<JSONObject> billableJsonArray;
		List<JSONObject> overTimeArray;

		// TODO Auto-generated method stub
		if (isExist) {
			JSONObject userListObject = new JSONObject();

			userList = getUserListByProject(id, startDate, endDate, projectId);

			loggedJsonArray = new ArrayList<>();

			String name = null;
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			for (int i = 0; i < diffInDays; i++) {
				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DATE);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				Double hours = 0.0;
				if (userList != null && userList.size() > 0) {
					JSONObject jsonObject = new JSONObject();
					for (Object[] item : userList) {

						String st = String.valueOf(item[3]);

						if (st.equals(vl)) {
							hours = hours + (Double) item[2];

						}
						name = (String) item[1] + " " + item[0];
					}
					jsonObject.put(vl, hours);
					cal.add(Calendar.DATE, 1);
					loggedJsonArray.add(jsonObject);

				}

				else {
					JSONObject jsonObject = new JSONObject();
					String uName = userService.getUserName(id);
					name = String.valueOf(uName).replace(",", " ");
					jsonObject.put(vl, 0);
					cal.add(Calendar.DATE, 1);
					loggedJsonArray.add(jsonObject);
				}

			}
			userListObject.put("userName", name);
			userListObject.put("userId", id);
			userListObject.put("month", intMonth);
			userListObject.put("logged", loggedJsonArray);
			// System.out.println("logged has data : "+loggedJsonArray);
			name = null;
			cal.setTime(startDate);
			int monthIndex = (cal.get(Calendar.MONTH) + 1);
			int yearIndex = cal.get(Calendar.YEAR);

			List<TaskTrackApprovalLevel2> approvalUserList = getUserListForApprovalLevel2(id, projectId, monthIndex,
					yearIndex);
			billableJsonArray = new ArrayList<>();
			billableJsonArrayLogged = new ArrayList<>();
			overTimeArray = new ArrayList<>();

			diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			intMonth = 0;
			intday = 0;
			Double hours = 0.0;

			if (approvalUserList != null && approvalUserList.size() > 0) {
				JSONObject jsonObject = new JSONObject();

				for (TaskTrackApprovalLevel2 item : approvalUserList) {
					cal.setTime(startDate);

					for (int i = 0; i < diffInDays; i++) {

						intMonth = (cal.get(Calendar.MONTH) + 1);
						intday = cal.get(Calendar.DATE);
						String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth)
								+ "-" + ((intday < 10) ? "0" + intday : "" + intday);

						if (i == 0)
							hours = (Double) item.getDay1();
						else if (i == 1)
							hours = (Double) item.getDay2();
						else if (i == 2)
							hours = (Double) item.getDay3();
						else if (i == 3)
							hours = (Double) item.getDay4();
						else if (i == 4)
							hours = (Double) item.getDay5();
						else if (i == 5)
							hours = (Double) item.getDay6();
						else if (i == 6)
							hours = (Double) item.getDay7();
						else if (i == 7)
							hours = (Double) item.getDay8();
						else if (i == 8)
							hours = (Double) item.getDay9();
						else if (i == 9)
							hours = (Double) item.getDay10();
						else if (i == 10)
							hours = (Double) item.getDay11();
						else if (i == 11)
							hours = (Double) item.getDay12();
						else if (i == 12)
							hours = (Double) item.getDay13();
						else if (i == 13)
							hours = (Double) item.getDay14();
						else if (i == 14)
							hours = (Double) item.getDay15();
						else if (i == 15)
							hours = (Double) item.getDay16();
						else if (i == 16)
							hours = (Double) item.getDay17();
						else if (i == 17)
							hours = (Double) item.getDay18();
						else if (i == 18)
							hours = (Double) item.getDay19();
						else if (i == 19)
							hours = (Double) item.getDay20();
						else if (i == 20)
							hours = (Double) item.getDay21();
						else if (i == 21)
							hours = (Double) item.getDay22();
						else if (i == 22)
							hours = (Double) item.getDay23();
						else if (i == 23)
							hours = (Double) item.getDay24();
						else if (i == 24)
							hours = (Double) item.getDay25();
						else if (i == 25)
							hours = (Double) item.getDay26();
						else if (i == 26)
							hours = (Double) item.getDay27();
						else if (i == 27)
							hours = (Double) item.getDay28();
						else if (i == 28)
							hours = (Double) item.getDay29();
						else if (i == 29)
							hours = (Double) item.getDay30();
						else if (i == 30)
							hours = (Double) item.getDay31();

						name = (String) item.getFirstName() + " " + item.getLastName();

						if (item.getProjectType().equals("Billable")) {
							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							billableJsonArrayLogged.add(jsonObject);
						}

						if (item.getProjectType().equals("Overtime")) {

							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							overTimeArray.add(jsonObject);

						}

						cal.add(Calendar.DATE, 1);

					}

					/*-------------------------*/

					if (!overTimeArray.isEmpty() && !billableJsonArrayLogged.isEmpty()
							&& billableJsonArray.size() < diffInDays) {
						cal.setTime(startDate);
						for (int i = 0; i < diffInDays; i++) {
							JSONObject jsonOverTime = new JSONObject();
							JSONObject jsonBillable = new JSONObject();
							JSONObject jsonTotal = new JSONObject();

							Double billable = 0.0;
							Double overTime = 0.0;

							intMonth = (cal.get(Calendar.MONTH) + 1);
							intday = cal.get(Calendar.DATE);
							String vl = cal.get(Calendar.YEAR) + "-"
									+ ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
									+ ((intday < 10) ? "0" + intday : "" + intday);

							jsonBillable = billableJsonArrayLogged.get(i);
							if (jsonBillable.get(vl) != null) {
								billable = (Double) jsonBillable.get(vl);
							}

							jsonOverTime = overTimeArray.get(i);
							if (jsonOverTime.get(vl) != null) {
								overTime = (Double) jsonOverTime.get(vl);
							}

							Double totalTime = billable + overTime;
							jsonTotal.put(vl, totalTime);
							billableJsonArray.add(jsonTotal);

							cal.add(Calendar.DATE, 1);
						}
					}
					/*-------------------------*/
				}
			} else {
				cal.setTime(startDate);
				for (int i = 0; i < diffInDays; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DATE);
					String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					JSONObject jsonObject = new JSONObject();
					jsonObject.put(vl, 0);
					billableJsonArray.add(jsonObject);

					cal.add(Calendar.DATE, 1);

				}
			}
			userListObject.put("billable", billableJsonArray);

			timeTrackJSONData.add(userListObject);

		} else {
			loggedJsonArray = new ArrayList<>();
			billableJsonArray = new ArrayList<>();
			JSONObject userListObject = new JSONObject();

			String uName = userService.getUserName(id);
			String name = String.valueOf(uName).replace(",", " ");

			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			for (int i = 0; i < diffInDays; i++) {
				JSONObject jsonObject = new JSONObject();

				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DATE);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				jsonObject.put(vl, 0);
				cal.add(Calendar.DATE, 1);
				loggedJsonArray.add(jsonObject);
				billableJsonArray.add(jsonObject);
			}
			userListObject.put("userName", name);
			userListObject.put("userId", id);
			userListObject.put("month", intMonth);
			userListObject.put("logged", loggedJsonArray);
			userListObject.put("billable", billableJsonArray);
			// System.out.println("logged is empty : "+loggedJsonArray);
			timeTrackJSONData.add(userListObject);
		}
		return timeTrackJSONData;
	}

	public List<TaskTrackApprovalLevel2> getUserListForApprovalLevel2(Long id, Long projectId, Integer monthIndex,
			Integer yearIndex) {

		List<TaskTrackApprovalLevel2> userList = timeTrackApprovalLevel2.getUserListForApproval(id, projectId,
				monthIndex, yearIndex);

		return userList;
	}

	@Override
	public List<Object> getForwardedDate(Long projectId, Long userId, int intMonth, int years) {
		// TODO Auto-generated method stub
		return timeTrackApprovalJPARepository.getForwardedDate(projectId, userId, intMonth, years);
	}

	@Override
	public List<Object> getForwardedDateLevel2(Long projectId, Long userId, int intMonth, int year) {
		// TODO Auto-generated method stub
		return timeTrackApprovalLevel2.getForwardedDateLevel2(projectId, userId, intMonth, year);
	}

	@Override
	public ObjectNode saveLevel2FromLevel1(Long projectId, Long userId, Long logUser, Date startDate, Date endDate) {
		// TODO Auto-generated method stub

		TaskTrackApprovalLevel2 tta2 = new TaskTrackApprovalLevel2();
		Calendar cal = Calendar.getInstance();
		ObjectNode response = objectMapper.createObjectNode();
		ObjectNode ids = objectMapper.createObjectNode();
		ObjectNode jsonDataMessageDetails = objectMapper.createObjectNode();
		cal.setTime(startDate);
		int intMonth = 0, intday = 0;
		intMonth = (cal.get(Calendar.MONTH) + 1);
		int yearIndex = cal.get(Calendar.YEAR);
		intday = cal.get(Calendar.DATE);

		Long billable_id = null;
		Long nonbillable_id = null;
		Long beach_id = null;
		Long overtime_id = null;

		Date current_date = new Date();
		Calendar current = Calendar.getInstance();
		current.setTime(current_date);
		int intCurrentMonth = 0;
		intCurrentMonth = (current.get(Calendar.MONTH) + 1);
		// System.out.println("currentMonth
		// ------------------------------------>"+(current.get(Calendar.MONTH) + 1));

		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		int day = 0;
		day = c.get(Calendar.DAY_OF_MONTH);
		int totaldays = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
		// System.out.println("total days"+totaldays);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();

		Date dateobj = new Date();
		String status = "";
		int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;

		if (intCurrentMonth > intMonth) {

			diffInDays = diffInDays + 1;
			status = "FM";
		} else {

			status = "HM";
			endDate = yesterday;
		}

		JSONObject testValidation = new JSONObject();
		testValidation = checkPreviousTimeSheetsareClosed(intMonth, yearIndex, projectId, logUser);

		if ((boolean) testValidation.get("data")) {
			List<TaskTrackApproval> approvedData = tasktrackRepository.getApprovedData(userId, intMonth, yearIndex,
					projectId);
			int fflag = 1;
			// int eflag = 0;

			if (approvedData.size() > 0) {

				if (!timeTrackApprovalLevel2.getApprovedData(userId, intMonth, yearIndex, projectId).isEmpty()) {
					fflag = 2;
				}

				for (TaskTrackApproval item : approvedData) {
					if (fflag == 1) {
						TaskTrackApprovalLevel2 level2 = new TaskTrackApprovalLevel2();
						TaskTrackApproval level1 = tasktrackApprovalService.findById(item.getId());
						System.out.println("------------------------------------->" + endDate);
						// level1.setForwarded_date(endDate);
						// level1.setStatus(status);
						// System.out.println("Current_Date"+dateobj);
						// level1.setApproved_date(endDate);
						UserModel user = userService.getUserDetailsById(userId);
						ProjectModel project = projectService.getProjectId(projectId);
						level2.setProject(project);
						level2.setProjectType(item.getProjectType());
						level2.setTasktrack_level1_Id(level1);
						level2.setStatus(status);

						// level2.setForwarded_date(yesterday);
						level2.setMonth(intMonth);
						level2.setYear(yearIndex);
						for (int i = 0; i < diffInDays - 1; i++) {
							if (i == 0)
								level2.setDay1(item.getDay1());
							else if (i == 1)
								level2.setDay2(item.getDay2());
							else if (i == 2)
								level2.setDay3(item.getDay3());
							else if (i == 3)
								level2.setDay4(item.getDay4());
							else if (i == 4)
								level2.setDay5(item.getDay5());
							else if (i == 5)
								level2.setDay6(item.getDay6());
							else if (i == 6)
								level2.setDay7(item.getDay7());
							else if (i == 7)
								level2.setDay8(item.getDay8());
							else if (i == 8)
								level2.setDay9(item.getDay9());
							else if (i == 9)
								level2.setDay10(item.getDay10());
							else if (i == 10)
								level2.setDay11(item.getDay11());
							else if (i == 11)
								level2.setDay12(item.getDay12());
							else if (i == 12)
								level2.setDay13(item.getDay13());
							else if (i == 13)
								level2.setDay14(item.getDay14());
							else if (i == 14)
								level2.setDay15(item.getDay15());
							else if (i == 15)
								level2.setDay16(item.getDay16());
							else if (i == 16)
								level2.setDay17(item.getDay17());
							else if (i == 17)
								level2.setDay18(item.getDay18());
							else if (i == 18)
								level2.setDay19(item.getDay19());
							else if (i == 19)
								level2.setDay20(item.getDay20());
							else if (i == 20)
								level2.setDay21(item.getDay21());
							else if (i == 21)
								level2.setDay22(item.getDay22());
							else if (i == 22)
								level2.setDay23(item.getDay23());
							else if (i == 23)
								level2.setDay24(item.getDay24());
							else if (i == 24)
								level2.setDay25(item.getDay25());
							else if (i == 25)
								level2.setDay26(item.getDay26());
							else if (i == 26)
								level2.setDay27(item.getDay27());
							else if (i == 27)
								level2.setDay28(item.getDay28());
							else if (i == 28)
								level2.setDay29(item.getDay29());
							else if (i == 29)
								level2.setDay30(item.getDay30());
							else if (i == 30)
								level2.setDay31(item.getDay31());
						}
						level2.setUser(user);
						cal.setTime(startDate);
						tta2 = timeTrackApprovalLevel2.save(level2);
						if (tta2.getProjectType().equalsIgnoreCase("Billable")) {
							billable_id = tta2.getId();
						} else if (tta2.getProjectType().equalsIgnoreCase("Non-Billable")) {
							nonbillable_id = tta2.getId();
						}

						else if (tta2.getProjectType().equalsIgnoreCase("Beach")) {

							beach_id = tta2.getId();
						} else if (tta2.getProjectType().equalsIgnoreCase("Overtime")) {

							overtime_id = tta2.getId();
						}
					}

					else if (fflag == 2) {

						List<TaskTrackApprovalLevel2> level2data = timeTrackApprovalLevel2.getApprovedData(userId,
								intMonth, yearIndex, projectId);

						if (level2data != null && level2data.size() > 0) {

							for (TaskTrackApprovalLevel2 item1 : level2data) {

								// Date previous_forwardedDate =
								// item1.getTasktrack_level1_Id().getForwarded_date();
								Calendar caldays = Calendar.getInstance();
								/*
								 * if(previous_forwardedDate != null) {
								 * 
								 * caldays.setTime(previous_forwardedDate); // changed on 17/10/19
								 * 
								 * }
								 */

								Calendar caldayss = Calendar.getInstance();
								caldays.setTime(endDate);
								int dayf = 0;
								int daypf = 0;
								daypf = cal.get(Calendar.DATE);
								dayf = caldayss.get(Calendar.DAY_OF_MONTH);
								if (item.getProjectType().equalsIgnoreCase("Billable")
										&& item1.getProjectType().equalsIgnoreCase("Billable")) {
									for (int i = daypf; i < dayf; i++) {
										if (i == 1)
											item1.setDay1(item.getDay1());
										else if (i == 2)
											item1.setDay2(item.getDay2());
										else if (i == 3)
											item1.setDay3(item.getDay3());
										else if (i == 4)
											item1.setDay4(item.getDay4());
										else if (i == 5)
											item1.setDay5(item.getDay5());
										else if (i == 6)
											item1.setDay6(item.getDay6());
										else if (i == 7)
											item1.setDay7(item.getDay7());
										else if (i == 8)
											item1.setDay8(item.getDay8());
										else if (i == 9)
											item1.setDay9(item.getDay9());
										else if (i == 10)
											item1.setDay10(item.getDay10());
										else if (i == 11)
											item1.setDay11(item.getDay11());
										else if (i == 12)
											item1.setDay12(item.getDay12());
										else if (i == 13)
											item1.setDay13(item.getDay13());
										else if (i == 14)
											item1.setDay14(item.getDay14());
										else if (i == 15)
											item1.setDay15(item.getDay15());
										else if (i == 16)
											item1.setDay16(item.getDay16());
										else if (i == 17)
											item1.setDay17(item.getDay17());
										else if (i == 18)
											item1.setDay18(item.getDay18());
										else if (i == 19)
											item1.setDay19(item.getDay19());
										else if (i == 20)
											item1.setDay20(item.getDay20());
										else if (i == 21)
											item1.setDay21(item.getDay21());
										else if (i == 22)
											item1.setDay22(item.getDay22());
										else if (i == 23)
											item1.setDay23(item.getDay23());
										else if (i == 24)
											item1.setDay24(item.getDay24());
										else if (i == 25)
											item1.setDay25(item.getDay25());
										else if (i == 26)
											item1.setDay26(item.getDay26());
										else if (i == 27)
											item1.setDay27(item.getDay27());
										else if (i == 28)
											item1.setDay28(item.getDay28());
										else if (i == 29)
											item1.setDay29(item.getDay29());
										else if (i == 30)
											item1.setDay30(item.getDay30());
										else if (i == 31)
											item1.setDay31(item.getDay31());
										tta2 = timeTrackApprovalLevel2.save(item1);
										billable_id = tta2.getId();
									}
								}

								if (item.getProjectType().equalsIgnoreCase("Non-Billable")
										&& item1.getProjectType().equalsIgnoreCase("Non-Billable")) {
									for (int i = daypf; i < dayf; i++) {
										if (i == 1)
											item1.setDay1(item.getDay1());
										else if (i == 2)
											item1.setDay2(item.getDay2());
										else if (i == 3)
											item1.setDay3(item.getDay3());
										else if (i == 4)
											item1.setDay4(item.getDay4());
										else if (i == 5)
											item1.setDay5(item.getDay5());
										else if (i == 6)
											item1.setDay6(item.getDay6());
										else if (i == 7)
											item1.setDay7(item.getDay7());
										else if (i == 8)
											item1.setDay8(item.getDay8());
										else if (i == 9)
											item1.setDay9(item.getDay9());
										else if (i == 10)
											item1.setDay10(item.getDay10());
										else if (i == 11)
											item1.setDay11(item.getDay11());
										else if (i == 12)
											item1.setDay12(item.getDay12());
										else if (i == 13)
											item1.setDay13(item.getDay13());
										else if (i == 14)
											item1.setDay14(item.getDay14());
										else if (i == 15)
											item1.setDay15(item.getDay15());
										else if (i == 16)
											item1.setDay16(item.getDay16());
										else if (i == 17)
											item1.setDay17(item.getDay17());
										else if (i == 18)
											item1.setDay18(item.getDay18());
										else if (i == 19)
											item1.setDay19(item.getDay19());
										else if (i == 20)
											item1.setDay20(item.getDay20());
										else if (i == 21)
											item1.setDay21(item.getDay21());
										else if (i == 22)
											item1.setDay22(item.getDay22());
										else if (i == 23)
											item1.setDay23(item.getDay23());
										else if (i == 24)
											item1.setDay24(item.getDay24());
										else if (i == 25)
											item1.setDay25(item.getDay25());
										else if (i == 26)
											item1.setDay26(item.getDay26());
										else if (i == 27)
											item1.setDay27(item.getDay27());
										else if (i == 28)
											item1.setDay28(item.getDay28());
										else if (i == 29)
											item1.setDay29(item.getDay29());
										else if (i == 30)
											item1.setDay30(item.getDay30());
										else if (i == 31)
											item1.setDay31(item.getDay31());
										tta2 = timeTrackApprovalLevel2.save(item1);
										nonbillable_id = tta2.getId();
									}
								}
								if (item.getProjectType().equalsIgnoreCase("Beach")
										&& item1.getProjectType().equalsIgnoreCase("Beach")) {
									for (int i = daypf; i < dayf; i++) {
										if (i == 1)
											item1.setDay1(item.getDay1());
										else if (i == 2)
											item1.setDay2(item.getDay2());
										else if (i == 3)
											item1.setDay3(item.getDay3());
										else if (i == 4)
											item1.setDay4(item.getDay4());
										else if (i == 5)
											item1.setDay5(item.getDay5());
										else if (i == 6)
											item1.setDay6(item.getDay6());
										else if (i == 7)
											item1.setDay7(item.getDay7());
										else if (i == 8)
											item1.setDay8(item.getDay8());
										else if (i == 9)
											item1.setDay9(item.getDay9());
										else if (i == 10)
											item1.setDay10(item.getDay10());
										else if (i == 11)
											item1.setDay11(item.getDay11());
										else if (i == 12)
											item1.setDay12(item.getDay12());
										else if (i == 13)
											item1.setDay13(item.getDay13());
										else if (i == 14)
											item1.setDay14(item.getDay14());
										else if (i == 15)
											item1.setDay15(item.getDay15());
										else if (i == 16)
											item1.setDay16(item.getDay16());
										else if (i == 17)
											item1.setDay17(item.getDay17());
										else if (i == 18)
											item1.setDay18(item.getDay18());
										else if (i == 19)
											item1.setDay19(item.getDay19());
										else if (i == 20)
											item1.setDay20(item.getDay20());
										else if (i == 21)
											item1.setDay21(item.getDay21());
										else if (i == 22)
											item1.setDay22(item.getDay22());
										else if (i == 23)
											item1.setDay23(item.getDay23());
										else if (i == 24)
											item1.setDay24(item.getDay24());
										else if (i == 25)
											item1.setDay25(item.getDay25());
										else if (i == 26)
											item1.setDay26(item.getDay26());
										else if (i == 27)
											item1.setDay27(item.getDay27());
										else if (i == 28)
											item1.setDay28(item.getDay28());
										else if (i == 29)
											item1.setDay29(item.getDay29());
										else if (i == 30)
											item1.setDay30(item.getDay30());
										else if (i == 31)
											item1.setDay31(item.getDay31());
										tta2 = timeTrackApprovalLevel2.save(item1);
										beach_id = tta2.getId();
									}
								}
								if (item.getProjectType().equalsIgnoreCase("Overtime")
										&& item1.getProjectType().equalsIgnoreCase("Overtime")) {
									for (int i = daypf; i < dayf; i++) {
										if (i == 1)
											item1.setDay1(item.getDay1());
										else if (i == 2)
											item1.setDay2(item.getDay2());
										else if (i == 3)
											item1.setDay3(item.getDay3());
										else if (i == 4)
											item1.setDay4(item.getDay4());
										else if (i == 5)
											item1.setDay5(item.getDay5());
										else if (i == 6)
											item1.setDay6(item.getDay6());
										else if (i == 7)
											item1.setDay7(item.getDay7());
										else if (i == 8)
											item1.setDay8(item.getDay8());
										else if (i == 9)
											item1.setDay9(item.getDay9());
										else if (i == 10)
											item1.setDay10(item.getDay10());
										else if (i == 11)
											item1.setDay11(item.getDay11());
										else if (i == 12)
											item1.setDay12(item.getDay12());
										else if (i == 13)
											item1.setDay13(item.getDay13());
										else if (i == 14)
											item1.setDay14(item.getDay14());
										else if (i == 15)
											item1.setDay15(item.getDay15());
										else if (i == 16)
											item1.setDay16(item.getDay16());
										else if (i == 17)
											item1.setDay17(item.getDay17());
										else if (i == 18)
											item1.setDay18(item.getDay18());
										else if (i == 19)
											item1.setDay19(item.getDay19());
										else if (i == 20)
											item1.setDay20(item.getDay20());
										else if (i == 21)
											item1.setDay21(item.getDay21());
										else if (i == 22)
											item1.setDay22(item.getDay22());
										else if (i == 23)
											item1.setDay23(item.getDay23());
										else if (i == 24)
											item1.setDay24(item.getDay24());
										else if (i == 25)
											item1.setDay25(item.getDay25());
										else if (i == 26)
											item1.setDay26(item.getDay26());
										else if (i == 27)
											item1.setDay27(item.getDay27());
										else if (i == 28)
											item1.setDay28(item.getDay28());
										else if (i == 29)
											item1.setDay29(item.getDay29());
										else if (i == 30)
											item1.setDay30(item.getDay30());
										else if (i == 31)
											item1.setDay31(item.getDay31());

										tta2 = timeTrackApprovalLevel2.save(item1);
										overtime_id = tta2.getId();
										// System.out.println("Overtime"+overtime_id);
									}
								}

							}

						}
					}
				}

			}
			// forward button and approve button status
			boolean approve_button = true;
			boolean forward_button = false;

			TaskTrackApproval forward_approved = timeTrackApprovalJPARepository.getapprovedDates2(intMonth, yearIndex,
					projectId, userId);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			if (forward_approved != null) {
				// System.out.println("Forwarded---------------"+forward_approved.getApproved_date());
				// System.out.println("approve---------------"+forward_approved.getForwarded_date());
				/*
				 * if(forward_approved.getForwarded_date() != null) {
				 * System.out.println(" 1------------------------->"); Calendar cl =
				 * Calendar.getInstance(); // passing month-1 because 0-->jan, 1-->feb...
				 * 11-->dec cl.set(yearIndex, intMonth - 1, 1); cl.set(Calendar.DATE,
				 * cl.getActualMaximum(Calendar.DATE)); Date monthend_date = cl.getTime(); Date
				 * forwarded_date = (Date) forward_approved.getApproved_date();
				 * System.out.println("Month end------------------>"+monthend_date);
				 * 
				 * String forwarded_date_s = dateFormat.format(forwarded_date); String
				 * monthend_date_s = dateFormat.format(monthend_date);
				 * System.out.println(forwarded_date_s+"-----------"+monthend_date_s);
				 * System.out.println("Month end------------------>"+monthend_date);
				 * if(forwarded_date_s.equals(monthend_date_s)) { approve_button = false; } else
				 * if (forwarded_date.after(monthend_date) ) {
				 * 
				 * System.out.println("approve_button ------------------------->"+forwarded_date
				 * .compareTo(monthend_date)); approve_button = false; }
				 * 
				 * } else {
				 * 
				 * if(forward_approved.getApproved_date() != null) {
				 * 
				 * forward_button = true; } }
				 */
				/*
				 * if(forward_approved.getApproved_date() != null &&
				 * forward_approved.getForwarded_date() != null) { Date forwarded_date = (Date)
				 * forward_approved.getApproved_date(); String forwarded_date_s =
				 * dateFormat.format(forwarded_date); Date approved_date = (Date)
				 * forward_approved.getApproved_date(); String approved_date_s =
				 * dateFormat.format(approved_date);
				 * System.out.println(approved_date+"Dates------------------"+forwarded_date);
				 * if(forwarded_date_s.equals(approved_date_s)) {
				 * 
				 * forward_button = false; } else if (forwarded_date.before(approved_date)) {
				 * System.out.println("forward button ------------------------->"+forward_button
				 * ); forward_button = true; }
				 * 
				 * }
				 */

			}
			//

			// for showing status
			// Level 2 approver name

			ProjectModel projectdetails = null;
			boolean flaglevel2 = true;
			if (projectId != null) {
				// System.out.println("Here____________________________");
				projectdetails = getProjectDetails(projectId);
			}
			if (projectdetails != null) {
				if (projectdetails.getOnsite_lead() != null)

				{
					System.out.println("------------------------------------------------1");

					jsonDataMessageDetails.put("Level2_Approvar_Name", projectdetails.getOnsite_lead().getFirstName()
							+ " " + projectdetails.getOnsite_lead().getLastName());
				} else {

					flaglevel2 = false;
					jsonDataMessageDetails.put("Level2_Approvar_Name", "");
				}

				// level2 forwarded
				String frowardedDate = "";
				String frowardedDateLevel2 = "";
				String finance_status_message = "Timesheet not yet submitted to finance";
				String forwarded_ToLevel2_Status = "";
				if (flaglevel2) {
					forwarded_ToLevel2_Status = "Timesheet not yet forwarded to Level2";
				}
				// getb finance status of the current project added on 11/10

				Object[] finance_status = tasktrackApprovalService.getFinanceStatusOfCurrentProject(projectId, userId,
						intMonth, yearIndex);

				if (finance_status != null) {
					System.out.println("---------------------------------------0");
					if (finance_status.length > 0) {
						System.out.println("---------------------------------------1");
						if (finance_status[0].equals("HM")) {
							System.out.println("---------------------------------------2");
							finance_status_message = "Submitted mid report";
						} else if (finance_status[0].equals("FM")) {
							System.out.println("---------------------------------------3");
							finance_status_message = "Submitted Final report";
						}

					}
				}
				jsonDataMessageDetails.put("Status", finance_status_message);
				//
				// level2 forwarded

				List<Object[]> level1 = tasktrackApprovalService.getForwardedDates(projectId, userId, intMonth,
						yearIndex);
				if (!level1.isEmpty()) {
					// System.out.println("forwarded_date"+level1.get(0));
					if (level1 != null) {
						for (Object[] fl : level1) {
							if (fl != null) {
								if (fl[0] != null) {
									Date fdate = (Date) fl[0];
									System.out.println("---------------------------------------4");
									String pattern1 = "MM-dd-yyyy";
									DateFormat df1 = new SimpleDateFormat(pattern1);
									String forw = df1.format(fdate);
									forwarded_ToLevel2_Status = "Data upto " + forw + " has been forwarded to Level2";
								}
								if (fl[1] != null) {
									Date fdates = (Date) fl[1];

								}
							}
						}
					} // System.out.println("frowardedDate___________"+frowardedDate);

				}
				jsonDataMessageDetails.put("Forwarded_status", forwarded_ToLevel2_Status);
			}

			// end

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			ids.put("billable_id", billable_id);
			ids.put("nonbillable_id", nonbillable_id);
			ids.put("beach_id", beach_id);
			ids.put("overtime_id", overtime_id);
			response.put("data", "");
			response.put("status", "success");
			response.put("message", "forwarded to level2");
			response.put("forwardedDate", (outputFormat.format(endDate)).toString());
			response.set("ids", ids);
			response.put("approve_button", approve_button);
			response.put("forward_button", forward_button);
			response.put("message_details", jsonDataMessageDetails);
		} else {
			response.put("data", "");
			response.put("status", "failed");
			response.put("message", (String) testValidation.get("message"));
		}
		return response;
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
		if (financeData.size() != 0) {
			for (Object[] item : financeData) {
				JSONObject node = new JSONObject();
				List<JSONObject> billableArray = new ArrayList<>();
				List<JSONObject> userArray = new ArrayList<>();

				node.put("userId", item[0]);
				node.put("firstName", item[1]);
				node.put("lastName", item[2]);
				// node.put("status", item[3]);
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
		if (financeData.size() != 0) {
			for (Object[] item : financeData) {
				JSONObject node = new JSONObject();
				List<JSONObject> billableArray = new ArrayList<>();
				node.put("projectId", item[0]);
				node.put("projectName", item[1]);
				// node.put("status", item[2]);
				for (int i = 1; i <= daysInMonth; i++) {
					String j;
					if (i < 10) {
						j = "0" + i;
					} else {
						j = String.valueOf(i);
					}
					JSONObject billableNode = new JSONObject();
					billableNode.put(year + "-" + intmonth + "-" + j, item[i + 1]);
					billableArray.add(billableNode);
				}
				node.put("billable", billableArray);
				resultData.add(node);
			}
		}

		return resultData;
	}

	public ArrayList<JSONObject> getFinanceDataByUserAndProject(int month, int year, Long userId, Long projectId) {

		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		ArrayList<JSONObject> resultData = new ArrayList<JSONObject>();
		List<Object[]> financeData = taskTrackApprovalFinalRepository.getFinanceDataByUserAndProject(month, year,
				userId, projectId);
		String intmonth;
		if (month < 10) {
			intmonth = "0" + month;
		} else {
			intmonth = String.valueOf(month);
		}
		if (financeData.size() != 0) {
			for (Object[] item : financeData) {
				JSONObject node = new JSONObject();
				List<JSONObject> billableArray = new ArrayList<>();
				node.put("projectId", item[0]);
				node.put("projectName", item[1]);
				node.put("userId", item[2]);
				node.put("firstName", item[3]);
				node.put("lastName", item[4]);
				// node.put("status", item[5]);
				for (int i = 1; i <= daysInMonth; i++) {
					String j;
					if (i < 10) {
						j = "0" + i;
					} else {
						j = String.valueOf(i);
					}
					JSONObject billableNode = new JSONObject();
					billableNode.put(year + "-" + intmonth + "-" + j, item[i + 4]);
					billableArray.add(billableNode);
				}
				node.put("billable", billableArray);
				resultData.add(node);
			}
		}

		return resultData;
	}

	@Override
	public List<Object[]> getForwardedDates(Long projectId, Long userId, int intMonth, int yearIndex) {
		// TODO Auto-generated method stub
		return timeTrackApprovalJPARepository.getForwardedDates(projectId, userId, intMonth, yearIndex);
	}

	@Override
	public List<TaskTrackApprovalLevel2> getUserIdByProjectAndDateForLevel2(Long projectId, Date startDate,
			Date endDate) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		int intMonth = 0, intday = 0;
		intMonth = (cal.get(Calendar.MONTH) + 1);
		int yearIndex = cal.get(Calendar.YEAR);
		return timeTrackApprovalLevel2.getUserIdByProjectAndDateForLevel2(projectId, intMonth, yearIndex);
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
				approverrowcount = timeTrackApprovalJPARepository.getCountOfRows(prevMonth, prevMonthYear, projectId);
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

	public JSONObject halfCycleCheck(Long projectId, Long userId, Long approverId, Date endDate) throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		UserModel user = userRepository.getOne(approverId);
		String message = null;
		Boolean status = true;
		Long approverrowcount = null;
		boolean timesheet_button = false;
		boolean approve_button = true;
		boolean forward_button = false;
		String approved_till_date = "";
		Long role = user.getRole().getroleId();
		DateFormat df;
		df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = (calendar.get(Calendar.MONTH) + 1);
		int year = calendar.get(Calendar.YEAR);
		String fromdate = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-01";
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startdDate = outputFormat.parse(fromdate);
		List<Tasktrack> logData = tasktrackRepository.getByDate(startdDate, endDate, userId);
		String financeStatus = "";

		Calendar cal = Calendar.getInstance();
		int approved_dayindex = 0;
		if (day > 15) {
			if (!logData.isEmpty()) {
				if (role == 2) {

					ProjectModel projectData = projectRepository.getProjectDetails(projectId);
					approverrowcount = timeTrackApprovalJPARepository.getCountOfRowsByUser(month, year, projectId,
							userId);
					Object[] approvedDate = timeTrackApprovalJPARepository.getapprovedDates(month, year, projectId,
							userId);

					Date approved_date = null;
					YearMonth yearMonthObject = YearMonth.of(year, month);
					int totaldays = yearMonthObject.lengthOfMonth();

					if (projectData.getOnsite_lead() == null) {

						if (approverrowcount == 0) {
							financeStatus = "";
							message = "Log not approved yet";
							status = false;
						} else if (approverrowcount == taskTrackFinanceRepository.getCountOfRowsHMByUser(month, year,
								projectId, userId)) {

							if (approverrowcount == taskTrackFinanceRepository.getCountOfRowsFMByUser(month, year,
									projectId, userId)) {
								financeStatus = "FM";
								message = "No pending logs found";
								status = true;
							} else {
								financeStatus = "HM";
								message = "Pending full month logs found ";
								status = true;
							}
						} else {
							financeStatus = "";
							message = "Pending half month logs found";
							status = false;
						}
					} else {
						if (approverrowcount == timeTrackApprovalLevel2.getCountOfRowsHMByUser(month, year, projectId,
								userId)) {
							financeStatus = "";
							message = "No pending logs found";
							status = true;
						} else {
							financeStatus = "";
							message = "Pending half month logs found";
							status = false;
						}

					}

					if (approvedDate.length != 0) {
						if (approvedDate[0] != null) {
							approved_date = (Date) approvedDate[0];
							cal.setTime(approved_date);
							approved_dayindex = cal.get(Calendar.DATE);
							if ((approved_dayindex >= 15) && (financeStatus.equalsIgnoreCase(""))) {

								timesheet_button = true;
							} else if ((approved_dayindex >= totaldays) && (financeStatus.equalsIgnoreCase("HM"))) {

								timesheet_button = true;
							}

						}
					}

					TaskTrackApproval forward_approved = timeTrackApprovalJPARepository.getapprovedDates2(month, year,
							projectId, userId);

					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					if (forward_approved != null) {

						/*
						 * if(forward_approved.getApproved_date() != null) {
						 * 
						 * 
						 * approved_till_date = dateFormat.format(forward_approved.getApproved_date());
						 * }
						 */

						/*
						 * System.out.println("Forwarded---------------"+forward_approved.
						 * getApproved_date());
						 * System.out.println("approve---------------"+forward_approved.
						 * getForwarded_date()); if(forward_approved.getForwarded_date() != null) {
						 * System.out.println(" 1------------------------->"); Calendar cl =
						 * Calendar.getInstance(); // passing month-1 because 0-->jan, 1-->feb...
						 * 11-->dec cl.set(year, month - 1, 1); cl.set(Calendar.DATE,
						 * cl.getActualMaximum(Calendar.DATE)); Date monthend_date = cl.getTime(); Date
						 * forwarded_date = (Date) forward_approved.getApproved_date();
						 * 
						 * 
						 * String forwarded_date_s = dateFormat.format(forwarded_date); String
						 * monthend_date_s = dateFormat.format(monthend_date);
						 * System.out.println(forwarded_date_s+"-----------"+monthend_date_s);
						 * 
						 * System.out.println("Month end------------------>"+monthend_date);
						 * 
						 * 
						 * 
						 * 
						 * if(forwarded_date_s.equals(monthend_date_s)) { approve_button = false; } else
						 * if (forwarded_date.after(monthend_date) ) {
						 * 
						 * System.out.println("approve_button ------------------------->"+forwarded_date
						 * .compareTo(monthend_date)); approve_button = false; }
						 * 
						 * } else {
						 * 
						 * if(forward_approved.getApproved_date() != null) {
						 * 
						 * forward_button = true; } }
						 */

						/*
						 * if(forward_approved.getApproved_date() != null &&
						 * forward_approved.getForwarded_date() != null) {
						 * 
						 * Date approved_dates = (Date) forward_approved.getApproved_date(); Date
						 * forwarded_date = (Date) forward_approved.getForwarded_date(); if
						 * (approved_dates.before(forwarded_date)) {
						 * System.out.println("forward button ------------------------->"+forward_button
						 * ); forward_button = true; } }
						 */

					}

				} else if (role == 7) {

					approverrowcount = timeTrackApprovalLevel2.getCountOfRowsHMByUser(month, year, projectId, userId);
					Object[] approvedDate = timeTrackApprovalLevel2.getapprovedDates(month, year, projectId, userId);
					Date approved_date = null;
					YearMonth yearMonthObject = YearMonth.of(year, month);
					int totaldays = yearMonthObject.lengthOfMonth();
					if (approverrowcount == taskTrackFinanceRepository.getCountOfRowsHMByUser(month, year, projectId,
							userId)) {
						if (approverrowcount == 0) {
							financeStatus = "";
							message = "Log not approved yet";
							status = false;
						} else if (approverrowcount == taskTrackFinanceRepository.getCountOfRowsFMByUser(month, year,
								projectId, userId)) {
							financeStatus = "FM";
							message = "No pending logs found";
							status = true;
						} else {
							financeStatus = "HM";
							message = "Pending full month logs found ";
							status = true;
						}
					} else {
						financeStatus = "";
						message = "Pending half month logs found";
						status = false;
					}

					if (approvedDate.length != 0) {
						if (approvedDate[0] != null) {
							approved_date = (Date) approvedDate[0];
							cal.setTime(approved_date);
							approved_dayindex = cal.get(Calendar.DATE);
							if ((approved_dayindex >= 15) && (financeStatus.equalsIgnoreCase(""))) {

								timesheet_button = true;
							} else if ((approved_dayindex >= totaldays) && (financeStatus.equalsIgnoreCase("HM"))) {

								timesheet_button = true;
							}

						}
					}

					TaskTrackApprovalLevel2 forward_approved = timeTrackApprovalLevel2.getapprovedDates2(month, year,
							projectId, userId);
					if (forward_approved != null) {

						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						if (forward_approved.getApproved_date() != null) {

							approved_till_date = dateFormat.format(forward_approved.getApproved_date());
						}

						if (forward_approved.getForwarded_date() != null) {
							System.out.println(" 1------------------------->");
							Calendar cl = Calendar.getInstance();
							// passing month-1 because 0-->jan, 1-->feb... 11-->dec
							cl.set(year, month - 1, 1);
							cl.set(Calendar.DATE, cl.getActualMaximum(Calendar.DATE));
							Date monthend_date = cl.getTime();
							Date forwarded_date = (Date) forward_approved.getApproved_date();

							String forwarded_date_s = dateFormat.format(forwarded_date);
							String monthend_date_s = dateFormat.format(monthend_date);
							System.out.println(forwarded_date_s + "-----------" + monthend_date_s);

							System.out.println("Month end------------------>" + monthend_date);

							if (forwarded_date_s.equals(monthend_date_s)) {
								approve_button = false;
							} else if (forwarded_date.after(monthend_date)) {

								System.out.println("approve_button ------------------------->"
										+ forwarded_date.compareTo(monthend_date));
								approve_button = false;
							}

						}

					}

				}
			} else {
				financeStatus = "";
				message = "Pending half month logs found";
				status = false;
			}

		}
		// List<AllocationModel> =

		jsonDataRes.put("data", status);
		jsonDataRes.put("status", "success");
		jsonDataRes.put("message", message);
		jsonDataRes.put("financeStatus", financeStatus);
		jsonDataRes.put("timesheet_button", timesheet_button);
		jsonDataRes.put("approve_button", approve_button);
		jsonDataRes.put("forward_button", forward_button);
		jsonDataRes.put("approved_till_date", approved_till_date);

		return jsonDataRes;

	}

	@Override
	public Object[] getFinanceStatusOfCurrentProject(Long projectId, Long userId, int intMonth, int yearIndex) {
		// TODO Auto-generated method stub
		return taskTrackFinanceRepository.getFinanceStatusOfCurrentProject(projectId, userId, intMonth, yearIndex);
	}

	@Override
	public ObjectNode reApproveDatasofLevel1(Long projectId, Long userId, int month, int year,
			HashMap<String, Object> billableArray, HashMap<String, Object> nonbillableArray,
			HashMap<String, Object> beachArray, HashMap<String, Object> overtimeArray, Long billableId,
			Long nonbillableId, Long overtimeId, Long beachId, Long logUser) {
		// TODO Auto-generated method stub
		System.out.println("------" + projectId + "----" + userId + "------" + month + "---------" + year);
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		double hours = 0;
		Date from_date = null;
		Date to_date = null;
		String message = "successfully saved";
		String status = "success";
		// find approved date and forwarded date to decide editable columns
		List<Object[]> dates = timeTrackApprovalJPARepository.getForwardedDates(projectId, userId, month, year);
		System.out.println("outside data size greater-----------------------------" + dates.size());
		try {
			if (dates.size() > 0) {
				System.out.println("inside data size greater-----------------------------");
				for (Object[] date : dates) {
					if ((date[2] != null && date[1] != null) || (date[0] != null && date[2] != null)) {
						// if forwarded nd approved dates not null
						System.out.println(
								"inside  both forwarded finance  and aproved datesr-----------------------------");
						Date approved_Date = (Date) date[2];
						Date forwarded_Date = null;
						if (date[0] != null) {
							forwarded_Date = (Date) date[0];
						} else if (date[1] != null) {

							forwarded_Date = (Date) date[1];
						}
						from_date = forwarded_Date;
						to_date = approved_Date;
						Calendar approved = Calendar.getInstance();
						approved.setTime(approved_Date);
						Calendar frowarded = Calendar.getInstance();
						frowarded.setTime(forwarded_Date);
						int forwardeddate = frowarded.get(Calendar.DAY_OF_MONTH);
						int approveddate = approved.get(Calendar.DAY_OF_MONTH);
						System.out.println("approved_Date-------------------------" + approveddate
								+ "forwardeddate----------------" + forwardeddate);
						if (approveddate > forwardeddate) {

							System.out.println("approved greater than forwarded------------>");

							if (billableId != null) {
								TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(billableId);
								for (int i = forwardeddate + 1; i <= approveddate; i++) {
									String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
											+ ((i < 10) ? "0" + i : "" + i);
									System.out.println("dates--------->" + dateString);
									if (billableArray.get(dateString) != null) {
										hours = Double.valueOf(billableArray.get(dateString).toString());
										if (i == 1) {
											taskTrackApproval.setDay1(hours);
										} else if (i == 2) {
											taskTrackApproval.setDay2(hours);
										} else if (i == 3) {
											taskTrackApproval.setDay3(hours);
										} else if (i == 4) {
											taskTrackApproval.setDay4(hours);
										} else if (i == 5) {
											taskTrackApproval.setDay5(hours);
										} else if (i == 6) {
											taskTrackApproval.setDay6(hours);
										} else if (i == 7) {
											taskTrackApproval.setDay7(hours);
										} else if (i == 8) {
											taskTrackApproval.setDay8(hours);
										} else if (i == 9) {
											taskTrackApproval.setDay9(hours);
										} else if (i == 10) {
											taskTrackApproval.setDay10(hours);
										} else if (i == 11) {
											taskTrackApproval.setDay11(hours);
										} else if (i == 12) {
											taskTrackApproval.setDay12(hours);
										} else if (i == 13) {
											taskTrackApproval.setDay13(hours);
										} else if (i == 14) {
											taskTrackApproval.setDay14(hours);
										} else if (i == 15) {
											taskTrackApproval.setDay15(hours);
										} else if (i == 16) {
											taskTrackApproval.setDay16(hours);
										} else if (i == 17) {
											taskTrackApproval.setDay17(hours);
										} else if (i == 18) {
											taskTrackApproval.setDay18(hours);
										} else if (i == 19) {
											taskTrackApproval.setDay19(hours);
										} else if (i == 20) {
											taskTrackApproval.setDay20(hours);
										} else if (i == 21) {
											taskTrackApproval.setDay21(hours);
										} else if (i == 22) {
											taskTrackApproval.setDay22(hours);
										} else if (i == 23) {
											taskTrackApproval.setDay23(hours);
										} else if (i == 24) {
											taskTrackApproval.setDay24(hours);
										} else if (i == 25) {
											taskTrackApproval.setDay25(hours);
										} else if (i == 26) {
											taskTrackApproval.setDay26(hours);
										} else if (i == 27) {
											taskTrackApproval.setDay27(hours);
										} else if (i == 28) {
											taskTrackApproval.setDay28(hours);
										} else if (i == 29) {
											taskTrackApproval.setDay29(hours);
										} else if (i == 30) {
											taskTrackApproval.setDay30(hours);
										} else if (i == 31) {
											taskTrackApproval.setDay31(hours);
										}
									}
								}
								tasktrackApprovalService.updateData(taskTrackApproval);
							}
							if (nonbillableId != null) {
								TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(nonbillableId);
								for (int i = forwardeddate + 1; i <= approveddate; i++) {
									String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
											+ ((i < 10) ? "0" + i : "" + i);
									if (nonbillableArray.get(dateString) != null) {
										hours = Double.valueOf(nonbillableArray.get(dateString).toString());
										if (i == 1) {
											taskTrackApproval.setDay1(hours);
										} else if (i == 2) {
											taskTrackApproval.setDay2(hours);
										} else if (i == 3) {
											taskTrackApproval.setDay3(hours);
										} else if (i == 4) {
											taskTrackApproval.setDay4(hours);
										} else if (i == 5) {
											taskTrackApproval.setDay5(hours);
										} else if (i == 6) {
											taskTrackApproval.setDay6(hours);
										} else if (i == 7) {
											taskTrackApproval.setDay7(hours);
										} else if (i == 8) {
											taskTrackApproval.setDay8(hours);
										} else if (i == 9) {
											taskTrackApproval.setDay9(hours);
										} else if (i == 10) {
											taskTrackApproval.setDay10(hours);
										} else if (i == 11) {
											taskTrackApproval.setDay11(hours);
										} else if (i == 12) {
											taskTrackApproval.setDay12(hours);
										} else if (i == 13) {
											taskTrackApproval.setDay13(hours);
										} else if (i == 14) {
											taskTrackApproval.setDay14(hours);
										} else if (i == 15) {
											taskTrackApproval.setDay15(hours);
										} else if (i == 16) {
											taskTrackApproval.setDay16(hours);
										} else if (i == 17) {
											taskTrackApproval.setDay17(hours);
										} else if (i == 18) {
											taskTrackApproval.setDay18(hours);
										} else if (i == 19) {
											taskTrackApproval.setDay19(hours);
										} else if (i == 20) {
											taskTrackApproval.setDay20(hours);
										} else if (i == 21) {
											taskTrackApproval.setDay21(hours);
										} else if (i == 22) {
											taskTrackApproval.setDay22(hours);
										} else if (i == 23) {
											taskTrackApproval.setDay23(hours);
										} else if (i == 24) {
											taskTrackApproval.setDay24(hours);
										} else if (i == 25) {
											taskTrackApproval.setDay25(hours);
										} else if (i == 26) {
											taskTrackApproval.setDay26(hours);
										} else if (i == 27) {
											taskTrackApproval.setDay27(hours);
										} else if (i == 28) {
											taskTrackApproval.setDay28(hours);
										} else if (i == 29) {
											taskTrackApproval.setDay29(hours);
										} else if (i == 30) {
											taskTrackApproval.setDay30(hours);
										} else if (i == 31) {
											taskTrackApproval.setDay31(hours);
										}
									}
								}
								tasktrackApprovalService.updateData(taskTrackApproval);
							}
							if (overtimeId != null) {
								TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(overtimeId);
								for (int i = forwardeddate + 1; i <= approveddate; i++) {
									String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
											+ ((i < 10) ? "0" + i : "" + i);
									if (overtimeArray.get(dateString) != null) {
										hours = Double.valueOf(overtimeArray.get(dateString).toString());
										if (i == 1) {
											taskTrackApproval.setDay1(hours);
										} else if (i == 2) {
											taskTrackApproval.setDay2(hours);
										} else if (i == 3) {
											taskTrackApproval.setDay3(hours);
										} else if (i == 4) {
											taskTrackApproval.setDay4(hours);
										} else if (i == 5) {
											taskTrackApproval.setDay5(hours);
										} else if (i == 6) {
											taskTrackApproval.setDay6(hours);
										} else if (i == 7) {
											taskTrackApproval.setDay7(hours);
										} else if (i == 8) {
											taskTrackApproval.setDay8(hours);
										} else if (i == 9) {
											taskTrackApproval.setDay9(hours);
										} else if (i == 10) {
											taskTrackApproval.setDay10(hours);
										} else if (i == 11) {
											taskTrackApproval.setDay11(hours);
										} else if (i == 12) {
											taskTrackApproval.setDay12(hours);
										} else if (i == 13) {
											taskTrackApproval.setDay13(hours);
										} else if (i == 14) {
											taskTrackApproval.setDay14(hours);
										} else if (i == 15) {
											taskTrackApproval.setDay15(hours);
										} else if (i == 16) {
											taskTrackApproval.setDay16(hours);
										} else if (i == 17) {
											taskTrackApproval.setDay17(hours);
										} else if (i == 18) {
											taskTrackApproval.setDay18(hours);
										} else if (i == 19) {
											taskTrackApproval.setDay19(hours);
										} else if (i == 20) {
											taskTrackApproval.setDay20(hours);
										} else if (i == 21) {
											taskTrackApproval.setDay21(hours);
										} else if (i == 22) {
											taskTrackApproval.setDay22(hours);
										} else if (i == 23) {
											taskTrackApproval.setDay23(hours);
										} else if (i == 24) {
											taskTrackApproval.setDay24(hours);
										} else if (i == 25) {
											taskTrackApproval.setDay25(hours);
										} else if (i == 26) {
											taskTrackApproval.setDay26(hours);
										} else if (i == 27) {
											taskTrackApproval.setDay27(hours);
										} else if (i == 28) {
											taskTrackApproval.setDay28(hours);
										} else if (i == 29) {
											taskTrackApproval.setDay29(hours);
										} else if (i == 30) {
											taskTrackApproval.setDay30(hours);
										} else if (i == 31) {
											taskTrackApproval.setDay31(hours);
										}
									}
								}
								tasktrackApprovalService.updateData(taskTrackApproval);
							}
							if (beachId != null) {
								TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(beachId);
								for (int i = forwardeddate + 1; i <= approveddate; i++) {
									String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
											+ ((i < 10) ? "0" + i : "" + i);
									if (beachArray.get(dateString) != null) {
										hours = Double.valueOf(beachArray.get(dateString).toString());
										if (i == 1) {
											taskTrackApproval.setDay1(hours);
										} else if (i == 2) {
											taskTrackApproval.setDay2(hours);
										} else if (i == 3) {
											taskTrackApproval.setDay3(hours);
										} else if (i == 4) {
											taskTrackApproval.setDay4(hours);
										} else if (i == 5) {
											taskTrackApproval.setDay5(hours);
										} else if (i == 6) {
											taskTrackApproval.setDay6(hours);
										} else if (i == 7) {
											taskTrackApproval.setDay7(hours);
										} else if (i == 8) {
											taskTrackApproval.setDay8(hours);
										} else if (i == 9) {
											taskTrackApproval.setDay9(hours);
										} else if (i == 10) {
											taskTrackApproval.setDay10(hours);
										} else if (i == 11) {
											taskTrackApproval.setDay11(hours);
										} else if (i == 12) {
											taskTrackApproval.setDay12(hours);
										} else if (i == 13) {
											taskTrackApproval.setDay13(hours);
										} else if (i == 14) {
											taskTrackApproval.setDay14(hours);
										} else if (i == 15) {
											taskTrackApproval.setDay15(hours);
										} else if (i == 16) {
											taskTrackApproval.setDay16(hours);
										} else if (i == 17) {
											taskTrackApproval.setDay17(hours);
										} else if (i == 18) {
											taskTrackApproval.setDay18(hours);
										} else if (i == 19) {
											taskTrackApproval.setDay19(hours);
										} else if (i == 20) {
											taskTrackApproval.setDay20(hours);
										} else if (i == 21) {
											taskTrackApproval.setDay21(hours);
										} else if (i == 22) {
											taskTrackApproval.setDay22(hours);
										} else if (i == 23) {
											taskTrackApproval.setDay23(hours);
										} else if (i == 24) {
											taskTrackApproval.setDay24(hours);
										} else if (i == 25) {
											taskTrackApproval.setDay25(hours);
										} else if (i == 26) {
											taskTrackApproval.setDay26(hours);
										} else if (i == 27) {
											taskTrackApproval.setDay27(hours);
										} else if (i == 28) {
											taskTrackApproval.setDay28(hours);
										} else if (i == 29) {
											taskTrackApproval.setDay29(hours);
										} else if (i == 30) {
											taskTrackApproval.setDay30(hours);
										} else if (i == 31) {
											taskTrackApproval.setDay31(hours);
										}
									}
								}
								tasktrackApprovalService.updateData(taskTrackApproval);
							}

						}

					}

					else if (date[2] != null) {
						System.out.println("inside  aproved datesr only-----------------------------");
						Date approved_Date = (Date) date[2];
						from_date = new SimpleDateFormat("dd-MM-yyyy").parse("01-" + month + "-" + year);
						to_date = approved_Date;
						Calendar approved = Calendar.getInstance();
						approved.setTime(approved_Date);
						int approveddate = approved.get(Calendar.DAY_OF_MONTH);
						// approved date not null
						if (billableId != null) {
							TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(billableId);
							for (int i = 1; i <= approveddate; i++) {
								String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
										+ ((i < 10) ? "0" + i : "" + i);
								System.out.println("----------" + dateString);
								if (billableArray.get(dateString) != null) {
									hours = Double.valueOf(billableArray.get(dateString).toString());
									if (i == 1) {
										taskTrackApproval.setDay1(hours);
									} else if (i == 2) {
										taskTrackApproval.setDay2(hours);
									} else if (i == 3) {
										taskTrackApproval.setDay3(hours);
									} else if (i == 4) {
										taskTrackApproval.setDay4(hours);
									} else if (i == 5) {
										taskTrackApproval.setDay5(hours);
									} else if (i == 6) {
										taskTrackApproval.setDay6(hours);
									} else if (i == 7) {
										taskTrackApproval.setDay7(hours);
									} else if (i == 8) {
										taskTrackApproval.setDay8(hours);
									} else if (i == 9) {
										taskTrackApproval.setDay9(hours);
									} else if (i == 10) {
										taskTrackApproval.setDay10(hours);
									} else if (i == 11) {
										taskTrackApproval.setDay11(hours);
									} else if (i == 12) {
										taskTrackApproval.setDay12(hours);
									} else if (i == 13) {
										taskTrackApproval.setDay13(hours);
									} else if (i == 14) {
										taskTrackApproval.setDay14(hours);
									} else if (i == 15) {
										taskTrackApproval.setDay15(hours);
									} else if (i == 16) {
										taskTrackApproval.setDay16(hours);
									} else if (i == 17) {
										taskTrackApproval.setDay17(hours);
									} else if (i == 18) {
										taskTrackApproval.setDay18(hours);
									} else if (i == 19) {
										taskTrackApproval.setDay19(hours);
									} else if (i == 20) {
										taskTrackApproval.setDay20(hours);
									} else if (i == 21) {
										taskTrackApproval.setDay21(hours);
									} else if (i == 22) {
										taskTrackApproval.setDay22(hours);
									} else if (i == 23) {
										taskTrackApproval.setDay23(hours);
									} else if (i == 24) {
										taskTrackApproval.setDay24(hours);
									} else if (i == 25) {
										taskTrackApproval.setDay25(hours);
									} else if (i == 26) {
										taskTrackApproval.setDay26(hours);
									} else if (i == 27) {
										taskTrackApproval.setDay27(hours);
									} else if (i == 28) {
										taskTrackApproval.setDay28(hours);
									} else if (i == 29) {
										taskTrackApproval.setDay29(hours);
									} else if (i == 30) {
										taskTrackApproval.setDay30(hours);
									} else if (i == 31) {
										taskTrackApproval.setDay31(hours);
									}
								}
							}
							tasktrackApprovalService.updateData(taskTrackApproval);
						}
						if (nonbillableId != null) {
							TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(nonbillableId);
							for (int i = 1; i <= approveddate; i++) {
								String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
										+ ((i < 10) ? "0" + i : "" + i);
								if (nonbillableArray.get(dateString) != null) {
									hours = Double.valueOf(nonbillableArray.get(dateString).toString());
									if (i == 1) {
										taskTrackApproval.setDay1(hours);
									} else if (i == 2) {
										taskTrackApproval.setDay2(hours);
									} else if (i == 3) {
										taskTrackApproval.setDay3(hours);
									} else if (i == 4) {
										taskTrackApproval.setDay4(hours);
									} else if (i == 5) {
										taskTrackApproval.setDay5(hours);
									} else if (i == 6) {
										taskTrackApproval.setDay6(hours);
									} else if (i == 7) {
										taskTrackApproval.setDay7(hours);
									} else if (i == 8) {
										taskTrackApproval.setDay8(hours);
									} else if (i == 9) {
										taskTrackApproval.setDay9(hours);
									} else if (i == 10) {
										taskTrackApproval.setDay10(hours);
									} else if (i == 11) {
										taskTrackApproval.setDay11(hours);
									} else if (i == 12) {
										taskTrackApproval.setDay12(hours);
									} else if (i == 13) {
										taskTrackApproval.setDay13(hours);
									} else if (i == 14) {
										taskTrackApproval.setDay14(hours);
									} else if (i == 15) {
										taskTrackApproval.setDay15(hours);
									} else if (i == 16) {
										taskTrackApproval.setDay16(hours);
									} else if (i == 17) {
										taskTrackApproval.setDay17(hours);
									} else if (i == 18) {
										taskTrackApproval.setDay18(hours);
									} else if (i == 19) {
										taskTrackApproval.setDay19(hours);
									} else if (i == 20) {
										taskTrackApproval.setDay20(hours);
									} else if (i == 21) {
										taskTrackApproval.setDay21(hours);
									} else if (i == 22) {
										taskTrackApproval.setDay22(hours);
									} else if (i == 23) {
										taskTrackApproval.setDay23(hours);
									} else if (i == 24) {
										taskTrackApproval.setDay24(hours);
									} else if (i == 25) {
										taskTrackApproval.setDay25(hours);
									} else if (i == 26) {
										taskTrackApproval.setDay26(hours);
									} else if (i == 27) {
										taskTrackApproval.setDay27(hours);
									} else if (i == 28) {
										taskTrackApproval.setDay28(hours);
									} else if (i == 29) {
										taskTrackApproval.setDay29(hours);
									} else if (i == 30) {
										taskTrackApproval.setDay30(hours);
									} else if (i == 31) {
										taskTrackApproval.setDay31(hours);
									}
								}
							}
							tasktrackApprovalService.updateData(taskTrackApproval);
						}
						if (overtimeId != null) {
							TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(overtimeId);
							for (int i = 1; i <= approveddate; i++) {
								String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
										+ ((i < 10) ? "0" + i : "" + i);
								if (overtimeArray.get(dateString) != null) {
									hours = Double.valueOf(overtimeArray.get(dateString).toString());
									if (i == 1) {
										taskTrackApproval.setDay1(hours);
									} else if (i == 2) {
										taskTrackApproval.setDay2(hours);
									} else if (i == 3) {
										taskTrackApproval.setDay3(hours);
									} else if (i == 4) {
										taskTrackApproval.setDay4(hours);
									} else if (i == 5) {
										taskTrackApproval.setDay5(hours);
									} else if (i == 6) {
										taskTrackApproval.setDay6(hours);
									} else if (i == 7) {
										taskTrackApproval.setDay7(hours);
									} else if (i == 8) {
										taskTrackApproval.setDay8(hours);
									} else if (i == 9) {
										taskTrackApproval.setDay9(hours);
									} else if (i == 10) {
										taskTrackApproval.setDay10(hours);
									} else if (i == 11) {
										taskTrackApproval.setDay11(hours);
									} else if (i == 12) {
										taskTrackApproval.setDay12(hours);
									} else if (i == 13) {
										taskTrackApproval.setDay13(hours);
									} else if (i == 14) {
										taskTrackApproval.setDay14(hours);
									} else if (i == 15) {
										taskTrackApproval.setDay15(hours);
									} else if (i == 16) {
										taskTrackApproval.setDay16(hours);
									} else if (i == 17) {
										taskTrackApproval.setDay17(hours);
									} else if (i == 18) {
										taskTrackApproval.setDay18(hours);
									} else if (i == 19) {
										taskTrackApproval.setDay19(hours);
									} else if (i == 20) {
										taskTrackApproval.setDay20(hours);
									} else if (i == 21) {
										taskTrackApproval.setDay21(hours);
									} else if (i == 22) {
										taskTrackApproval.setDay22(hours);
									} else if (i == 23) {
										taskTrackApproval.setDay23(hours);
									} else if (i == 24) {
										taskTrackApproval.setDay24(hours);
									} else if (i == 25) {
										taskTrackApproval.setDay25(hours);
									} else if (i == 26) {
										taskTrackApproval.setDay26(hours);
									} else if (i == 27) {
										taskTrackApproval.setDay27(hours);
									} else if (i == 28) {
										taskTrackApproval.setDay28(hours);
									} else if (i == 29) {
										taskTrackApproval.setDay29(hours);
									} else if (i == 30) {
										taskTrackApproval.setDay30(hours);
									} else if (i == 31) {
										taskTrackApproval.setDay31(hours);
									}
								}
							}
							tasktrackApprovalService.updateData(taskTrackApproval);

						}
						if (beachId != null) {
							TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(beachId);
							for (int i = 1; i <= approveddate; i++) {
								String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
										+ ((i < 10) ? "0" + i : "" + i);
								if (beachArray.get(dateString) != null) {
									hours = Double.valueOf(beachArray.get(dateString).toString());
									if (i == 1) {
										taskTrackApproval.setDay1(hours);
									} else if (i == 2) {
										taskTrackApproval.setDay2(hours);
									} else if (i == 3) {
										taskTrackApproval.setDay3(hours);
									} else if (i == 4) {
										taskTrackApproval.setDay4(hours);
									} else if (i == 5) {
										taskTrackApproval.setDay5(hours);
									} else if (i == 6) {
										taskTrackApproval.setDay6(hours);
									} else if (i == 7) {
										taskTrackApproval.setDay7(hours);
									} else if (i == 8) {
										taskTrackApproval.setDay8(hours);
									} else if (i == 9) {
										taskTrackApproval.setDay9(hours);
									} else if (i == 10) {
										taskTrackApproval.setDay10(hours);
									} else if (i == 11) {
										taskTrackApproval.setDay11(hours);
									} else if (i == 12) {
										taskTrackApproval.setDay12(hours);
									} else if (i == 13) {
										taskTrackApproval.setDay13(hours);
									} else if (i == 14) {
										taskTrackApproval.setDay14(hours);
									} else if (i == 15) {
										taskTrackApproval.setDay15(hours);
									} else if (i == 16) {
										taskTrackApproval.setDay16(hours);
									} else if (i == 17) {
										taskTrackApproval.setDay17(hours);
									} else if (i == 18) {
										taskTrackApproval.setDay18(hours);
									} else if (i == 19) {
										taskTrackApproval.setDay19(hours);
									} else if (i == 20) {
										taskTrackApproval.setDay20(hours);
									} else if (i == 21) {
										taskTrackApproval.setDay21(hours);
									} else if (i == 22) {
										taskTrackApproval.setDay22(hours);
									} else if (i == 23) {
										taskTrackApproval.setDay23(hours);
									} else if (i == 24) {
										taskTrackApproval.setDay24(hours);
									} else if (i == 25) {
										taskTrackApproval.setDay25(hours);
									} else if (i == 26) {
										taskTrackApproval.setDay26(hours);
									} else if (i == 27) {
										taskTrackApproval.setDay27(hours);
									} else if (i == 28) {
										taskTrackApproval.setDay28(hours);
									} else if (i == 29) {
										taskTrackApproval.setDay29(hours);
									} else if (i == 30) {
										taskTrackApproval.setDay30(hours);
									} else if (i == 31) {
										taskTrackApproval.setDay31(hours);
									}
								}
							}
							tasktrackApprovalService.updateData(taskTrackApproval);
						}

					} else {
						message = "Not yet approved";
						status = "failed";
					}

				}

			} else {
				message = "Not yet approved";
				status = "failed";
			}

			// data insertion to activity log
			Date current = new Date();
			ActivityLog activity = new ActivityLog();
			activity.setAction("Reapproved time sheet");
			UserModel user = userRepository.getActiveUser(logUser);
			UserModel user2 = userRepository.getActiveUser(userId);
			activity.setUser(user2);
			activity.setAction_by(user);
			activity.setAction_on(current);
			activity.setFrom_date(from_date);
			activity.setTo_date(to_date);
			activity.setMonth(month);
			ProjectModel project = project_repositary.getOne(projectId);
			activity.setProject(project);
			activity.setYear(year);
			activitylogrepository.save(activity);

			jsonDataRes.put("status", status);
			// jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", message);
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "failure");
			// jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}

		return jsonDataRes;
	}

	@Override
	public ObjectNode reApproveDatasofLevel2(Long projectId, Long userId, Integer month, Integer year,
			HashMap<String, Object> billableArray, HashMap<String, Object> nonbillableArray,
			HashMap<String, Object> beachArray, HashMap<String, Object> overtimeArray, Long billableId,
			Long nonbillableId, Long overtimeId, Long beachId, Long logUser) {
		// TODO Auto-generated method stub
		int month1 = month;
		int year1 = year;

		System.out.println("------" + projectId + "----" + userId + "------" + month + "---------" + year);
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		double hours = 0;
		Date from_date = null;
		Date to_date = null;
		String message = "successfully saved";
		// find approved date and forwarded date to decide editable columns
		TaskTrackApprovalLevel2 dates = timeTrackApprovalLevel2.getapprovedDates2(month1, year1, projectId, userId);
		// System.out.println("outside data size
		// greater-----------------------------"+dates.size());
		try {
			if (dates != null) {
				System.out.println("inside data size greater-----------------------------");
				if (dates.getApproved_date() != null && dates.getForwarded_date() != null) {
					// if forwarded nd approved dates not null
					System.out
							.println("inside  both forwarded finance  and aproved datesr-----------------------------");
					Date approved_Date = (Date) dates.getApproved_date();
					Date forwarded_Date = null;

					forwarded_Date = (Date) dates.getForwarded_date();

					from_date = forwarded_Date;
					to_date = approved_Date;
					Calendar approved = Calendar.getInstance();
					approved.setTime(approved_Date);
					Calendar frowarded = Calendar.getInstance();
					frowarded.setTime(forwarded_Date);
					int forwardeddate = frowarded.get(Calendar.DAY_OF_MONTH);
					int approveddate = approved.get(Calendar.DAY_OF_MONTH);
					System.out.println("approved_Date-------------------------" + approveddate
							+ "forwardeddate----------------" + forwardeddate);
					if (approveddate > forwardeddate) {

						System.out.println("approved greater than forwarded------------>");

						if (billableId != null) {
							TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService.findById2(billableId);
							for (int i = forwardeddate + 1; i < approveddate; i++) {
								String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
										+ ((i < 10) ? "0" + i : "" + i);
								System.out.println("dates--------->" + dateString);
								if (billableArray.get(dateString) != null) {
									hours = Double.valueOf(billableArray.get(dateString).toString());
									if (i == 1) {
										taskTrackApproval.setDay1(hours);
									} else if (i == 2) {
										taskTrackApproval.setDay2(hours);
									} else if (i == 3) {
										taskTrackApproval.setDay3(hours);
									} else if (i == 4) {
										taskTrackApproval.setDay4(hours);
									} else if (i == 5) {
										taskTrackApproval.setDay5(hours);
									} else if (i == 6) {
										taskTrackApproval.setDay6(hours);
									} else if (i == 7) {
										taskTrackApproval.setDay7(hours);
									} else if (i == 8) {
										taskTrackApproval.setDay8(hours);
									} else if (i == 9) {
										taskTrackApproval.setDay9(hours);
									} else if (i == 10) {
										taskTrackApproval.setDay10(hours);
									} else if (i == 11) {
										taskTrackApproval.setDay11(hours);
									} else if (i == 12) {
										taskTrackApproval.setDay12(hours);
									} else if (i == 13) {
										taskTrackApproval.setDay13(hours);
									} else if (i == 14) {
										taskTrackApproval.setDay14(hours);
									} else if (i == 15) {
										taskTrackApproval.setDay15(hours);
									} else if (i == 16) {
										taskTrackApproval.setDay16(hours);
									} else if (i == 17) {
										taskTrackApproval.setDay17(hours);
									} else if (i == 18) {
										taskTrackApproval.setDay18(hours);
									} else if (i == 19) {
										taskTrackApproval.setDay19(hours);
									} else if (i == 20) {
										taskTrackApproval.setDay20(hours);
									} else if (i == 21) {
										taskTrackApproval.setDay21(hours);
									} else if (i == 22) {
										taskTrackApproval.setDay22(hours);
									} else if (i == 23) {
										taskTrackApproval.setDay23(hours);
									} else if (i == 24) {
										taskTrackApproval.setDay24(hours);
									} else if (i == 25) {
										taskTrackApproval.setDay25(hours);
									} else if (i == 26) {
										taskTrackApproval.setDay26(hours);
									} else if (i == 27) {
										taskTrackApproval.setDay27(hours);
									} else if (i == 28) {
										taskTrackApproval.setDay28(hours);
									} else if (i == 29) {
										taskTrackApproval.setDay29(hours);
									} else if (i == 30) {
										taskTrackApproval.setDay30(hours);
									} else if (i == 31) {
										taskTrackApproval.setDay31(hours);
									}
								}
							}
							tasktrackApprovalService.updateDatas(taskTrackApproval);
						}
						if (nonbillableId != null) {
							TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService
									.findById2(nonbillableId);
							for (int i = forwardeddate + 1; i < approveddate; i++) {
								String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
										+ ((i < 10) ? "0" + i : "" + i);
								if (nonbillableArray.get(dateString) != null) {
									hours = Double.valueOf(nonbillableArray.get(dateString).toString());
									if (i == 1) {
										taskTrackApproval.setDay1(hours);
									} else if (i == 2) {
										taskTrackApproval.setDay2(hours);
									} else if (i == 3) {
										taskTrackApproval.setDay3(hours);
									} else if (i == 4) {
										taskTrackApproval.setDay4(hours);
									} else if (i == 5) {
										taskTrackApproval.setDay5(hours);
									} else if (i == 6) {
										taskTrackApproval.setDay6(hours);
									} else if (i == 7) {
										taskTrackApproval.setDay7(hours);
									} else if (i == 8) {
										taskTrackApproval.setDay8(hours);
									} else if (i == 9) {
										taskTrackApproval.setDay9(hours);
									} else if (i == 10) {
										taskTrackApproval.setDay10(hours);
									} else if (i == 11) {
										taskTrackApproval.setDay11(hours);
									} else if (i == 12) {
										taskTrackApproval.setDay12(hours);
									} else if (i == 13) {
										taskTrackApproval.setDay13(hours);
									} else if (i == 14) {
										taskTrackApproval.setDay14(hours);
									} else if (i == 15) {
										taskTrackApproval.setDay15(hours);
									} else if (i == 16) {
										taskTrackApproval.setDay16(hours);
									} else if (i == 17) {
										taskTrackApproval.setDay17(hours);
									} else if (i == 18) {
										taskTrackApproval.setDay18(hours);
									} else if (i == 19) {
										taskTrackApproval.setDay19(hours);
									} else if (i == 20) {
										taskTrackApproval.setDay20(hours);
									} else if (i == 21) {
										taskTrackApproval.setDay21(hours);
									} else if (i == 22) {
										taskTrackApproval.setDay22(hours);
									} else if (i == 23) {
										taskTrackApproval.setDay23(hours);
									} else if (i == 24) {
										taskTrackApproval.setDay24(hours);
									} else if (i == 25) {
										taskTrackApproval.setDay25(hours);
									} else if (i == 26) {
										taskTrackApproval.setDay26(hours);
									} else if (i == 27) {
										taskTrackApproval.setDay27(hours);
									} else if (i == 28) {
										taskTrackApproval.setDay28(hours);
									} else if (i == 29) {
										taskTrackApproval.setDay29(hours);
									} else if (i == 30) {
										taskTrackApproval.setDay30(hours);
									} else if (i == 31) {
										taskTrackApproval.setDay31(hours);
									}
								}
							}
							tasktrackApprovalService.updateDatas(taskTrackApproval);
						}
						if (overtimeId != null) {
							TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService.findById2(overtimeId);
							for (int i = forwardeddate + 1; i < approveddate; i++) {
								String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
										+ ((i < 10) ? "0" + i : "" + i);
								if (overtimeArray.get(dateString) != null) {
									hours = Double.valueOf(overtimeArray.get(dateString).toString());
									if (i == 1) {
										taskTrackApproval.setDay1(hours);
									} else if (i == 2) {
										taskTrackApproval.setDay2(hours);
									} else if (i == 3) {
										taskTrackApproval.setDay3(hours);
									} else if (i == 4) {
										taskTrackApproval.setDay4(hours);
									} else if (i == 5) {
										taskTrackApproval.setDay5(hours);
									} else if (i == 6) {
										taskTrackApproval.setDay6(hours);
									} else if (i == 7) {
										taskTrackApproval.setDay7(hours);
									} else if (i == 8) {
										taskTrackApproval.setDay8(hours);
									} else if (i == 9) {
										taskTrackApproval.setDay9(hours);
									} else if (i == 10) {
										taskTrackApproval.setDay10(hours);
									} else if (i == 11) {
										taskTrackApproval.setDay11(hours);
									} else if (i == 12) {
										taskTrackApproval.setDay12(hours);
									} else if (i == 13) {
										taskTrackApproval.setDay13(hours);
									} else if (i == 14) {
										taskTrackApproval.setDay14(hours);
									} else if (i == 15) {
										taskTrackApproval.setDay15(hours);
									} else if (i == 16) {
										taskTrackApproval.setDay16(hours);
									} else if (i == 17) {
										taskTrackApproval.setDay17(hours);
									} else if (i == 18) {
										taskTrackApproval.setDay18(hours);
									} else if (i == 19) {
										taskTrackApproval.setDay19(hours);
									} else if (i == 20) {
										taskTrackApproval.setDay20(hours);
									} else if (i == 21) {
										taskTrackApproval.setDay21(hours);
									} else if (i == 22) {
										taskTrackApproval.setDay22(hours);
									} else if (i == 23) {
										taskTrackApproval.setDay23(hours);
									} else if (i == 24) {
										taskTrackApproval.setDay24(hours);
									} else if (i == 25) {
										taskTrackApproval.setDay25(hours);
									} else if (i == 26) {
										taskTrackApproval.setDay26(hours);
									} else if (i == 27) {
										taskTrackApproval.setDay27(hours);
									} else if (i == 28) {
										taskTrackApproval.setDay28(hours);
									} else if (i == 29) {
										taskTrackApproval.setDay29(hours);
									} else if (i == 30) {
										taskTrackApproval.setDay30(hours);
									} else if (i == 31) {
										taskTrackApproval.setDay31(hours);
									}
								}
							}
							tasktrackApprovalService.updateDatas(taskTrackApproval);
						}
						if (beachId != null) {
							TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService.findById2(beachId);
							for (int i = forwardeddate + 1; i < approveddate; i++) {
								String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
										+ ((i < 10) ? "0" + i : "" + i);
								if (beachArray.get(dateString) != null) {
									hours = Double.valueOf(beachArray.get(dateString).toString());
									if (i == 1) {
										taskTrackApproval.setDay1(hours);
									} else if (i == 2) {
										taskTrackApproval.setDay2(hours);
									} else if (i == 3) {
										taskTrackApproval.setDay3(hours);
									} else if (i == 4) {
										taskTrackApproval.setDay4(hours);
									} else if (i == 5) {
										taskTrackApproval.setDay5(hours);
									} else if (i == 6) {
										taskTrackApproval.setDay6(hours);
									} else if (i == 7) {
										taskTrackApproval.setDay7(hours);
									} else if (i == 8) {
										taskTrackApproval.setDay8(hours);
									} else if (i == 9) {
										taskTrackApproval.setDay9(hours);
									} else if (i == 10) {
										taskTrackApproval.setDay10(hours);
									} else if (i == 11) {
										taskTrackApproval.setDay11(hours);
									} else if (i == 12) {
										taskTrackApproval.setDay12(hours);
									} else if (i == 13) {
										taskTrackApproval.setDay13(hours);
									} else if (i == 14) {
										taskTrackApproval.setDay14(hours);
									} else if (i == 15) {
										taskTrackApproval.setDay15(hours);
									} else if (i == 16) {
										taskTrackApproval.setDay16(hours);
									} else if (i == 17) {
										taskTrackApproval.setDay17(hours);
									} else if (i == 18) {
										taskTrackApproval.setDay18(hours);
									} else if (i == 19) {
										taskTrackApproval.setDay19(hours);
									} else if (i == 20) {
										taskTrackApproval.setDay20(hours);
									} else if (i == 21) {
										taskTrackApproval.setDay21(hours);
									} else if (i == 22) {
										taskTrackApproval.setDay22(hours);
									} else if (i == 23) {
										taskTrackApproval.setDay23(hours);
									} else if (i == 24) {
										taskTrackApproval.setDay24(hours);
									} else if (i == 25) {
										taskTrackApproval.setDay25(hours);
									} else if (i == 26) {
										taskTrackApproval.setDay26(hours);
									} else if (i == 27) {
										taskTrackApproval.setDay27(hours);
									} else if (i == 28) {
										taskTrackApproval.setDay28(hours);
									} else if (i == 29) {
										taskTrackApproval.setDay29(hours);
									} else if (i == 30) {
										taskTrackApproval.setDay30(hours);
									} else if (i == 31) {
										taskTrackApproval.setDay31(hours);
									}
								}
							}
							tasktrackApprovalService.updateDatas(taskTrackApproval);
						}

					}

				}

				else if (dates.getApproved_date() != null) {
					System.out.println("inside  aproved datesr only-----------------------------");
					Date approved_Date = (Date) dates.getApproved_date();
					from_date = new SimpleDateFormat("dd-MM-yyyy").parse("01-" + month + "-" + year);
					to_date = approved_Date;
					Calendar approved = Calendar.getInstance();
					approved.setTime(approved_Date);
					int approveddate = approved.get(Calendar.DAY_OF_MONTH);
					// approved date not null
					if (billableId != null) {
						TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService.findById2(billableId);
						for (int i = 1; i < approveddate; i++) {
							String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
									+ ((i < 10) ? "0" + i : "" + i);
							System.out.println("----------" + dateString);
							if (billableArray.get(dateString) != null) {
								hours = Double.valueOf(billableArray.get(dateString).toString());
								if (i == 1) {
									taskTrackApproval.setDay1(hours);
								} else if (i == 2) {
									taskTrackApproval.setDay2(hours);
								} else if (i == 3) {
									taskTrackApproval.setDay3(hours);
								} else if (i == 4) {
									taskTrackApproval.setDay4(hours);
								} else if (i == 5) {
									taskTrackApproval.setDay5(hours);
								} else if (i == 6) {
									taskTrackApproval.setDay6(hours);
								} else if (i == 7) {
									taskTrackApproval.setDay7(hours);
								} else if (i == 8) {
									taskTrackApproval.setDay8(hours);
								} else if (i == 9) {
									taskTrackApproval.setDay9(hours);
								} else if (i == 10) {
									taskTrackApproval.setDay10(hours);
								} else if (i == 11) {
									taskTrackApproval.setDay11(hours);
								} else if (i == 12) {
									taskTrackApproval.setDay12(hours);
								} else if (i == 13) {
									taskTrackApproval.setDay13(hours);
								} else if (i == 14) {
									taskTrackApproval.setDay14(hours);
								} else if (i == 15) {
									taskTrackApproval.setDay15(hours);
								} else if (i == 16) {
									taskTrackApproval.setDay16(hours);
								} else if (i == 17) {
									taskTrackApproval.setDay17(hours);
								} else if (i == 18) {
									taskTrackApproval.setDay18(hours);
								} else if (i == 19) {
									taskTrackApproval.setDay19(hours);
								} else if (i == 20) {
									taskTrackApproval.setDay20(hours);
								} else if (i == 21) {
									taskTrackApproval.setDay21(hours);
								} else if (i == 22) {
									taskTrackApproval.setDay22(hours);
								} else if (i == 23) {
									taskTrackApproval.setDay23(hours);
								} else if (i == 24) {
									taskTrackApproval.setDay24(hours);
								} else if (i == 25) {
									taskTrackApproval.setDay25(hours);
								} else if (i == 26) {
									taskTrackApproval.setDay26(hours);
								} else if (i == 27) {
									taskTrackApproval.setDay27(hours);
								} else if (i == 28) {
									taskTrackApproval.setDay28(hours);
								} else if (i == 29) {
									taskTrackApproval.setDay29(hours);
								} else if (i == 30) {
									taskTrackApproval.setDay30(hours);
								} else if (i == 31) {
									taskTrackApproval.setDay31(hours);
								}
							}
						}
						tasktrackApprovalService.updateDatas(taskTrackApproval);
					}
					if (nonbillableId != null) {
						TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService.findById2(nonbillableId);
						for (int i = 1; i < approveddate; i++) {
							String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
									+ ((i < 10) ? "0" + i : "" + i);
							if (nonbillableArray.get(dateString) != null) {
								hours = Double.valueOf(nonbillableArray.get(dateString).toString());
								if (i == 1) {
									taskTrackApproval.setDay1(hours);
								} else if (i == 2) {
									taskTrackApproval.setDay2(hours);
								} else if (i == 3) {
									taskTrackApproval.setDay3(hours);
								} else if (i == 4) {
									taskTrackApproval.setDay4(hours);
								} else if (i == 5) {
									taskTrackApproval.setDay5(hours);
								} else if (i == 6) {
									taskTrackApproval.setDay6(hours);
								} else if (i == 7) {
									taskTrackApproval.setDay7(hours);
								} else if (i == 8) {
									taskTrackApproval.setDay8(hours);
								} else if (i == 9) {
									taskTrackApproval.setDay9(hours);
								} else if (i == 10) {
									taskTrackApproval.setDay10(hours);
								} else if (i == 11) {
									taskTrackApproval.setDay11(hours);
								} else if (i == 12) {
									taskTrackApproval.setDay12(hours);
								} else if (i == 13) {
									taskTrackApproval.setDay13(hours);
								} else if (i == 14) {
									taskTrackApproval.setDay14(hours);
								} else if (i == 15) {
									taskTrackApproval.setDay15(hours);
								} else if (i == 16) {
									taskTrackApproval.setDay16(hours);
								} else if (i == 17) {
									taskTrackApproval.setDay17(hours);
								} else if (i == 18) {
									taskTrackApproval.setDay18(hours);
								} else if (i == 19) {
									taskTrackApproval.setDay19(hours);
								} else if (i == 20) {
									taskTrackApproval.setDay20(hours);
								} else if (i == 21) {
									taskTrackApproval.setDay21(hours);
								} else if (i == 22) {
									taskTrackApproval.setDay22(hours);
								} else if (i == 23) {
									taskTrackApproval.setDay23(hours);
								} else if (i == 24) {
									taskTrackApproval.setDay24(hours);
								} else if (i == 25) {
									taskTrackApproval.setDay25(hours);
								} else if (i == 26) {
									taskTrackApproval.setDay26(hours);
								} else if (i == 27) {
									taskTrackApproval.setDay27(hours);
								} else if (i == 28) {
									taskTrackApproval.setDay28(hours);
								} else if (i == 29) {
									taskTrackApproval.setDay29(hours);
								} else if (i == 30) {
									taskTrackApproval.setDay30(hours);
								} else if (i == 31) {
									taskTrackApproval.setDay31(hours);
								}
							}
						}
						tasktrackApprovalService.updateDatas(taskTrackApproval);
					}
					if (overtimeId != null) {
						TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService.findById2(overtimeId);
						for (int i = 1; i < approveddate; i++) {
							String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
									+ ((i < 10) ? "0" + i : "" + i);
							if (overtimeArray.get(dateString) != null) {
								hours = Double.valueOf(overtimeArray.get(dateString).toString());
								if (i == 1) {
									taskTrackApproval.setDay1(hours);
								} else if (i == 2) {
									taskTrackApproval.setDay2(hours);
								} else if (i == 3) {
									taskTrackApproval.setDay3(hours);
								} else if (i == 4) {
									taskTrackApproval.setDay4(hours);
								} else if (i == 5) {
									taskTrackApproval.setDay5(hours);
								} else if (i == 6) {
									taskTrackApproval.setDay6(hours);
								} else if (i == 7) {
									taskTrackApproval.setDay7(hours);
								} else if (i == 8) {
									taskTrackApproval.setDay8(hours);
								} else if (i == 9) {
									taskTrackApproval.setDay9(hours);
								} else if (i == 10) {
									taskTrackApproval.setDay10(hours);
								} else if (i == 11) {
									taskTrackApproval.setDay11(hours);
								} else if (i == 12) {
									taskTrackApproval.setDay12(hours);
								} else if (i == 13) {
									taskTrackApproval.setDay13(hours);
								} else if (i == 14) {
									taskTrackApproval.setDay14(hours);
								} else if (i == 15) {
									taskTrackApproval.setDay15(hours);
								} else if (i == 16) {
									taskTrackApproval.setDay16(hours);
								} else if (i == 17) {
									taskTrackApproval.setDay17(hours);
								} else if (i == 18) {
									taskTrackApproval.setDay18(hours);
								} else if (i == 19) {
									taskTrackApproval.setDay19(hours);
								} else if (i == 20) {
									taskTrackApproval.setDay20(hours);
								} else if (i == 21) {
									taskTrackApproval.setDay21(hours);
								} else if (i == 22) {
									taskTrackApproval.setDay22(hours);
								} else if (i == 23) {
									taskTrackApproval.setDay23(hours);
								} else if (i == 24) {
									taskTrackApproval.setDay24(hours);
								} else if (i == 25) {
									taskTrackApproval.setDay25(hours);
								} else if (i == 26) {
									taskTrackApproval.setDay26(hours);
								} else if (i == 27) {
									taskTrackApproval.setDay27(hours);
								} else if (i == 28) {
									taskTrackApproval.setDay28(hours);
								} else if (i == 29) {
									taskTrackApproval.setDay29(hours);
								} else if (i == 30) {
									taskTrackApproval.setDay30(hours);
								} else if (i == 31) {
									taskTrackApproval.setDay31(hours);
								}
							}
						}
						tasktrackApprovalService.updateDatas(taskTrackApproval);

					}
					if (beachId != null) {
						TaskTrackApprovalLevel2 taskTrackApproval = tasktrackApprovalService.findById2(beachId);
						for (int i = 1; i < approveddate; i++) {
							String dateString = year + "-" + ((month < 10) ? "0" + month : "" + month) + "-"
									+ ((i < 10) ? "0" + i : "" + i);
							if (beachArray.get(dateString) != null) {
								hours = Double.valueOf(beachArray.get(dateString).toString());
								if (i == 1) {
									taskTrackApproval.setDay1(hours);
								} else if (i == 2) {
									taskTrackApproval.setDay2(hours);
								} else if (i == 3) {
									taskTrackApproval.setDay3(hours);
								} else if (i == 4) {
									taskTrackApproval.setDay4(hours);
								} else if (i == 5) {
									taskTrackApproval.setDay5(hours);
								} else if (i == 6) {
									taskTrackApproval.setDay6(hours);
								} else if (i == 7) {
									taskTrackApproval.setDay7(hours);
								} else if (i == 8) {
									taskTrackApproval.setDay8(hours);
								} else if (i == 9) {
									taskTrackApproval.setDay9(hours);
								} else if (i == 10) {
									taskTrackApproval.setDay10(hours);
								} else if (i == 11) {
									taskTrackApproval.setDay11(hours);
								} else if (i == 12) {
									taskTrackApproval.setDay12(hours);
								} else if (i == 13) {
									taskTrackApproval.setDay13(hours);
								} else if (i == 14) {
									taskTrackApproval.setDay14(hours);
								} else if (i == 15) {
									taskTrackApproval.setDay15(hours);
								} else if (i == 16) {
									taskTrackApproval.setDay16(hours);
								} else if (i == 17) {
									taskTrackApproval.setDay17(hours);
								} else if (i == 18) {
									taskTrackApproval.setDay18(hours);
								} else if (i == 19) {
									taskTrackApproval.setDay19(hours);
								} else if (i == 20) {
									taskTrackApproval.setDay20(hours);
								} else if (i == 21) {
									taskTrackApproval.setDay21(hours);
								} else if (i == 22) {
									taskTrackApproval.setDay22(hours);
								} else if (i == 23) {
									taskTrackApproval.setDay23(hours);
								} else if (i == 24) {
									taskTrackApproval.setDay24(hours);
								} else if (i == 25) {
									taskTrackApproval.setDay25(hours);
								} else if (i == 26) {
									taskTrackApproval.setDay26(hours);
								} else if (i == 27) {
									taskTrackApproval.setDay27(hours);
								} else if (i == 28) {
									taskTrackApproval.setDay28(hours);
								} else if (i == 29) {
									taskTrackApproval.setDay29(hours);
								} else if (i == 30) {
									taskTrackApproval.setDay30(hours);
								} else if (i == 31) {
									taskTrackApproval.setDay31(hours);
								}
							}
						}
						tasktrackApprovalService.updateDatas(taskTrackApproval);
					}

				} else {
					message = "Not yet approved";

				}

			}

			// data insertion to activity log
			Date current = new Date();
			ActivityLog activity = new ActivityLog();
			activity.setAction("Reapproved time sheet");
			UserModel user = userRepository.getActiveUser(logUser);
			UserModel user2 = userRepository.getActiveUser(userId);
			activity.setUser(user2);
			activity.setAction_by(user);
			activity.setAction_on(current);
			activity.setFrom_date(from_date);
			activity.setTo_date(to_date);
			activity.setMonth(month);
			ProjectModel project = project_repositary.getOne(projectId);
			activity.setProject(project);
			activity.setYear(year);
			activitylogrepository.save(activity);

			jsonDataRes.put("status", "success");
			// jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", message);
		} catch (Exception e) {
			e.printStackTrace();
			jsonDataRes.put("status", "failure");
			// jsonDataRes.put("code", httpstatus.getStatus());
			jsonDataRes.put("message", "failed. " + e);
		}
		return jsonDataRes;
	}

	@Override
	public ObjectNode mailRejectTimesheetDetailstoLevel1andClear(Long projectId, Long userId, Long month, Long year,
			String message) {
		// TODO Auto-generated method stub

		// int month1 = month.intValue();
		// int year1 = year.intValue();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		// 1.clear recently added data.
		// List<TaskTrackApprovalLevel2> approvedData =
		// timeTrackApprovalLevel2.getApprovedData(userId,month1,year1,projectId);
		// TaskTrackApproval forwardtolevel2 =
		// timeTrackApprovalJPARepository.getapprovedDates2(month1, year1, projectId,
		// userId);
		// TaskTrackApprovalLevel2 dates =
		// timeTrackApprovalLevel2.getapprovedDates2(month1, year1,projectId, userId);

		/*
		 * if(forwardtolevel2 != null && dates != null) {
		 * 
		 * if(dates.getApproved_date() != null && forwardtolevel2.getForwarded_date() !=
		 * null) {
		 * if(dates.getApproved_date().before(forwardtolevel2.getForwarded_date())) {
		 * 
		 * //do clear the rejected datas Calendar approved = Calendar.getInstance();
		 * approved.setTime(dates.getApproved_date()); int approved_date =
		 * approved.get(Calendar.DAY_OF_MONTH);
		 * 
		 * Calendar forwarded_level1 = Calendar.getInstance();
		 * forwarded_level1.setTime(forwardtolevel2.getForwarded_date()); int forwarded
		 * = forwarded_level1.get(Calendar.DAY_OF_MONTH);
		 * System.out.println("forwarded-------------->"+forwarded+
		 * "approved----------->"+approved_date);
		 * 
		 * if(approvedData.size() > 0) { for(TaskTrackApprovalLevel2 data: approvedData)
		 * { System.out.println("Here-------------------------------------------->1");
		 * TaskTrackApprovalLevel2 taskTrackApproval =
		 * timeTrackApprovalLevel2.getOne(data.getId()); TaskTrackApproval level1 =
		 * tasktrackApprovalService.findById(taskTrackApproval.getTasktrack_level1_Id().
		 * getId()); level1.setForwarded_date(dates.getApproved_date());
		 * timeTrackApprovalJPARepository.save(level1); for(int i = approved_date+1; i<=
		 * forwarded ; i++) {
		 * 
		 * if(i==1) { taskTrackApproval.setDay1(null); } else if(i==2) {
		 * taskTrackApproval.setDay2(null); } else if(i==3) {
		 * taskTrackApproval.setDay3(null); } else if(i==4) {
		 * taskTrackApproval.setDay4(null); } else if(i==5) {
		 * taskTrackApproval.setDay5(null); } else if(i==6) {
		 * taskTrackApproval.setDay6(null); } else if(i==7) {
		 * taskTrackApproval.setDay7(null); } else if(i==8) {
		 * taskTrackApproval.setDay8(null); } else if(i==9) {
		 * taskTrackApproval.setDay9(null); } else if(i==10) {
		 * taskTrackApproval.setDay10(null); } else if(i==11) {
		 * taskTrackApproval.setDay11(null); } else if(i==12) {
		 * taskTrackApproval.setDay12(null); } else if(i==13) {
		 * taskTrackApproval.setDay13(null); } else if(i==14) {
		 * taskTrackApproval.setDay14(null); } else if(i==15) {
		 * taskTrackApproval.setDay15(null); } else if(i==16) {
		 * taskTrackApproval.setDay16(null); } else if(i==17) {
		 * taskTrackApproval.setDay17(null); } else if(i==18) {
		 * taskTrackApproval.setDay18(null); } else if(i==19) {
		 * taskTrackApproval.setDay19(null); } else if(i==20) {
		 * taskTrackApproval.setDay20(null); } else if(i==21) {
		 * taskTrackApproval.setDay21(null); } else if(i==22) {
		 * taskTrackApproval.setDay22(null); } else if(i==23) {
		 * taskTrackApproval.setDay23(null); } else if(i==24) {
		 * taskTrackApproval.setDay24(null); } else if(i==25) {
		 * taskTrackApproval.setDay25(null); } else if(i==26) {
		 * taskTrackApproval.setDay26(null); } else if(i==27) {
		 * taskTrackApproval.setDay27(null); } else if(i==28) {
		 * taskTrackApproval.setDay28(null); } else if(i==29) {
		 * taskTrackApproval.setDay29(null); } else if(i==30) {
		 * taskTrackApproval.setDay30(null); } else if(i==31) {
		 * taskTrackApproval.setDay31(null); }
		 * 
		 * }
		 * 
		 * tasktrackApprovalService.updateDatas(taskTrackApproval); } }
		 * jsonDataRes.put("status", "success"); //jsonDataRes.put("code",
		 * httpstatus.getStatus()); jsonDataRes.put("message", "Cleared Data"); }
		 * 
		 * else { jsonDataRes.put("status", "failure"); //jsonDataRes.put("code",
		 * httpstatus.getStatus()); jsonDataRes.put("message", "failed. " +
		 * "Cannot Reject data"); // set cannot reject data } }
		 */
		/*
		 * else if(dates.getApproved_date() == null &&
		 * forwardtolevel2.getForwarded_date() != null) {
		 * System.out.println("Here-------------------------------------------->2");
		 * Calendar forwarded_level1 = Calendar.getInstance();
		 * forwarded_level1.setTime(forwardtolevel2.getForwarded_date()); int forwarded
		 * = forwarded_level1.get(Calendar.DAY_OF_MONTH);
		 * 
		 * 
		 * if(approvedData.size() > 0) {
		 * System.out.println("Here-------------------------------------------->3");
		 * for(TaskTrackApprovalLevel2 data: approvedData) { TaskTrackApprovalLevel2
		 * taskTrackApproval = timeTrackApprovalLevel2.getOne(data.getId());
		 * TaskTrackApproval level1 =
		 * tasktrackApprovalService.findById(taskTrackApproval.getTasktrack_level1_Id().
		 * getId()); level1.setForwarded_date(dates.getApproved_date()); for(int i = 1;
		 * i< forwarded ; i++) {
		 * 
		 * if(i==1) { taskTrackApproval.setDay1(null); } else if(i==2) {
		 * taskTrackApproval.setDay2(null); } else if(i==3) {
		 * taskTrackApproval.setDay3(null); } else if(i==4) {
		 * taskTrackApproval.setDay4(null); } else if(i==5) {
		 * taskTrackApproval.setDay5(null); } else if(i==6) {
		 * taskTrackApproval.setDay6(null); } else if(i==7) {
		 * taskTrackApproval.setDay7(null); } else if(i==8) {
		 * taskTrackApproval.setDay8(null); } else if(i==9) {
		 * taskTrackApproval.setDay9(null); } else if(i==10) {
		 * taskTrackApproval.setDay10(null); } else if(i==11) {
		 * taskTrackApproval.setDay11(null); } else if(i==12) {
		 * taskTrackApproval.setDay12(null); } else if(i==13) {
		 * taskTrackApproval.setDay13(null); } else if(i==14) {
		 * taskTrackApproval.setDay14(null); } else if(i==15) {
		 * taskTrackApproval.setDay15(null); } else if(i==16) {
		 * taskTrackApproval.setDay16(null); } else if(i==17) {
		 * taskTrackApproval.setDay17(null); } else if(i==18) {
		 * taskTrackApproval.setDay18(null); } else if(i==19) {
		 * taskTrackApproval.setDay19(null); } else if(i==20) {
		 * taskTrackApproval.setDay20(null); } else if(i==21) {
		 * taskTrackApproval.setDay21(null); } else if(i==22) {
		 * taskTrackApproval.setDay22(null); } else if(i==23) {
		 * taskTrackApproval.setDay23(null); } else if(i==24) {
		 * taskTrackApproval.setDay24(null); } else if(i==25) {
		 * taskTrackApproval.setDay25(null); } else if(i==26) {
		 * taskTrackApproval.setDay26(null); } else if(i==27) {
		 * taskTrackApproval.setDay27(null); } else if(i==28) {
		 * taskTrackApproval.setDay28(null); } else if(i==29) {
		 * taskTrackApproval.setDay29(null); } else if(i==30) {
		 * taskTrackApproval.setDay30(null); } else if(i==31) {
		 * taskTrackApproval.setDay31(null); }
		 * 
		 * }
		 * 
		 * tasktrackApprovalService.updateDatas(taskTrackApproval); } }
		 * jsonDataRes.put("status", "success"); //jsonDataRes.put("code",
		 * httpstatus.getStatus()); jsonDataRes.put("message", "Cleared Data"); }
		 */
		// }

		// 2.mail reject message to level1 approver.
		// ObjectNode mailtolevel1 = mailRejectMessageToLevel1(projectId,message);

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

	@Override
	public ArrayList<JSONObject> getFinanceDataByMonthAndYear(int month, int year) {
		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		ArrayList<JSONObject> resultData = new ArrayList<JSONObject>();
		List<Object[]> financeData = taskTrackApprovalFinalRepository.getFinanceDataByMonthYear(month, year);
		String intmonth;
		if (month < 10) {
			intmonth = "0" + month;
		} else {
			intmonth = String.valueOf(month);
		}
		if (financeData.size() != 0) {
			for (Object[] item : financeData) {
				JSONObject node = new JSONObject();
				List<JSONObject> billableArray = new ArrayList<>();
				List<JSONObject> userArray = new ArrayList<>();

				node.put("userId", item[0]);
				node.put("firstName", item[1]);
				node.put("lastName", item[2]);
				// node.put("status", item[3]);
				node.put("projectName", item[3]);
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
		}

		return resultData;
	}

	// Renjith
	@Override
	public List<TaskTrackApprovalLevel2> getNotApprovedData(int monthIndex, int yearIndex, Long projectId) {
		return timeTrackApprovalLevel2.getNotApprovedData(monthIndex, yearIndex, projectId);
	}

	public List<TaskTrackApprovalLevel2> getHalfMonthApprovedData(int monthIndex, int yearIndex, Long projectId) {
		return timeTrackApprovalLevel2.getHalfMonthApprovedData(monthIndex, yearIndex, projectId);
	}

	// Renjith
	@Override
	public List<TaskTrackApprovalLevel2> getMidMonthApprovedData(int monthIndex, int yearIndex, Long projectId) {
		return timeTrackApprovalLevel2.getMidMonthData(monthIndex, yearIndex, projectId);
	}

	private ProjectModel getProjectDetails(Long projectId) {
		// TODO Auto-generated method stub
		// System.out.println("Here____________________________");
		return projectService.getProjectDetails(projectId);
	}

	// Renjith
	public ArrayList<JSONObject> getFinanceDataByProjectSet(int month, int year, Set<Long> id) {

		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		ArrayList<JSONObject> resultData = new ArrayList<JSONObject>();
		List<Object[]> financeData = taskTrackApprovalFinalRepository.getFinanceDataByProjectSet(month, year, id);
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
			// node.put("status", item[3]);
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

	/**
	 * @author sreejith.j
	 */
	@Override
	public void saveApprovedHours(JSONObject requestData) throws Exception {

		// Obtain the data from request data
		Long billableId = null, nonBillableId = null, overtimeId = null, beachId = null, projectId = null,
				userId = null;
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

		if (billableArray.size() > 0 && billableId == null) {
			billableId = timeTrackApprovalJPARepository.getBillableIdForAUserForAProject(month, year, projectId,
					userId);
			if (billableId != null) {
				throw new DuplicateEntryException("Duplicate entry for billable.");
			}
		}
		if (nonbillableArray.size() > 0 && nonBillableId == null) {
			nonBillableId = timeTrackApprovalJPARepository.getNonBillableIdForAUserForAProject(month, year, projectId,
					userId);
			if (nonBillableId != null) {
				throw new DuplicateEntryException("Duplicate entry for NonBillable.");
			}
		}
		if (overtimeArray.size() > 0 && overtimeId == null) {
			overtimeId = timeTrackApprovalJPARepository.getOvertimeIdForAUserForAProject(month, year, projectId,
					userId);
			if (overtimeId != null) {
				throw new DuplicateEntryException("Duplicate entry for Overtime.");
			}
		}
		if (beachArray.size() > 0 && beachId == null) {
			beachId = timeTrackApprovalJPARepository.getBeachIdForAUserForAProject(month, year, projectId, userId);
			if (beachId != null) {
				throw new DuplicateEntryException("Duplicate entry for Beach.");
			}
		}

		if (billableArray.size() > 0) {// Billable

			Calendar startCal = Calendar.getInstance();

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0, intday = 0;
			startCal.setTime(startDate);
			double hours = 0;
			intMonth = (startCal.get(Calendar.MONTH) + 1);

			if (billableId != null) {
				TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(billableId);
				List<TaskTrackRejection> rejectionList = new ArrayList<TaskTrackRejection>();
//					taskTrackApproval.setApprovedDate(endDate);
				if (taskTrackApproval != null) {

					if (startCal.get(Calendar.DATE) < 16) {
						if (taskTrackApproval.getFirstHalfStatus() != null && (taskTrackApproval.getFirstHalfStatus()
								.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION)
								|| taskTrackApproval.getFirstHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED))) {
							taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED);
						} else if (taskTrackApproval.getFirstHalfStatus() != null && (taskTrackApproval
								.getFirstHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
								|| taskTrackApproval.getFirstHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED))) {
							List<TaskTrackRejection> rejectionEntryList = taskTrackRejectionRepository
									.findOpenRejectionForCycleForUserForProject(userId, projectId, month, year,
											Constants.TASKTRACK_REJECTION_FIRST_HALF_CYCLE);
							TaskTrackRejection rejectionObj = new TaskTrackRejection();
							if (rejectionEntryList.size() > 0) {
								rejectionObj = rejectionEntryList.get(0);
								rejectionObj.setStatus(Constants.TASKTRACK_REJECTION_STATUS_CLOSED);
								rejectionList.add(rejectionObj);
							}
							taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED);
						} else {
							taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						}

					}
					if (endCal.get(Calendar.DATE) > 15) {
						if (taskTrackApproval.getSecondHalfStatus() != null && (taskTrackApproval.getSecondHalfStatus()
								.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION))) {
							taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED);
						} else if (taskTrackApproval.getSecondHalfStatus() != null
								&& (taskTrackApproval.getSecondHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION))) {
							List<TaskTrackRejection> rejectionEntryList = taskTrackRejectionRepository
									.findOpenRejectionForCycleForUserForProject(userId, projectId, month, year,
											Constants.TASKTRACK_REJECTION_SECOND_HALF_CYCLE);
							TaskTrackRejection rejectionObj = new TaskTrackRejection();
							if (rejectionEntryList.size() > 0) {
								rejectionObj = rejectionEntryList.get(0);
								rejectionObj.setStatus(Constants.TASKTRACK_REJECTION_STATUS_CLOSED);
								rejectionList.add(rejectionObj);
							}
							taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED);
						} else {
							taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						}
					}
					int startDayOfMonth = startCal.get(Calendar.DATE);
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

					tasktrackApprovalService.updateData(taskTrackApproval);
					billable_id = taskTrackApproval.getId();
					taskTrackRejectionRepository.saveAll(rejectionList);
				} else {
					throw new Exception("TaskTrack data not found for given billable id.");
				}
			} else {

				TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Billable");

				if (startCal.get(Calendar.DATE) < 16) {
					taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
				}
				if (endCal.get(Calendar.DATE) > 15) {
					taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
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

				TaskTrackApproval billable = tasktrackApprovalService.save(taskTrackApproval);
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
				TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(nonBillableId);

				if (taskTrackApproval != null) {
					if (startCal.get(Calendar.DATE) < 16) {
						if (taskTrackApproval.getFirstHalfStatus() != null && (taskTrackApproval.getFirstHalfStatus()
								.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION)
								|| taskTrackApproval.getFirstHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED))) {
							taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED);
						} else if (taskTrackApproval.getFirstHalfStatus() != null && (taskTrackApproval
								.getFirstHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
								|| taskTrackApproval.getFirstHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED))) {
							taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED);
						} else {
							taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						}
					}
					if (endCal.get(Calendar.DATE) > 15) {
						if (taskTrackApproval.getSecondHalfStatus() != null && (taskTrackApproval.getSecondHalfStatus()
								.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION)
								|| taskTrackApproval.getSecondHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED))) {
							taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED);
						} else if (taskTrackApproval.getSecondHalfStatus() != null && (taskTrackApproval
								.getSecondHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
								|| taskTrackApproval.getSecondHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED))) {
							taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED);
						} else {
							taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						}
					}
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
					tasktrackApprovalService.updateData(taskTrackApproval);
					nonbillable_id = taskTrackApproval.getId();
				} else {
					throw new Exception("TaskTrack data not found for given non-billable id.");
				}
			} else {

				TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Non-Billable");

				if (startCal.get(Calendar.DATE) < 16) {
					taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
				}
				if (endCal.get(Calendar.DATE) > 15) {
					taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
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

				TaskTrackApproval nonbillable = tasktrackApprovalService.save(taskTrackApproval);
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
				TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(overtimeId);

				if (taskTrackApproval != null) {
					if (startCal.get(Calendar.DATE) < 16) {
						if (taskTrackApproval.getFirstHalfStatus() != null && (taskTrackApproval.getFirstHalfStatus()
								.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION)
								|| taskTrackApproval.getFirstHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED))) {
							taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED);
						} else if (taskTrackApproval.getFirstHalfStatus() != null && (taskTrackApproval
								.getFirstHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
								|| taskTrackApproval.getFirstHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED))) {
							taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED);
						} else {
							taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						}
					}
					if (endCal.get(Calendar.DATE) > 15) {
						if (taskTrackApproval.getSecondHalfStatus() != null && (taskTrackApproval.getSecondHalfStatus()
								.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION)
								|| taskTrackApproval.getSecondHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED))) {
							taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED);
						} else if (taskTrackApproval.getSecondHalfStatus() != null && (taskTrackApproval
								.getSecondHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
								|| taskTrackApproval.getSecondHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED))) {
							taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED);
						} else {
							taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						}
					}
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
					tasktrackApprovalService.updateData(taskTrackApproval);
					overtime_id = taskTrackApproval.getId();
				} else {
					throw new Exception("TaskTrack data not found for given overtime id.");
				}
			} else {

				TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Overtime");

				if (startCal.get(Calendar.DATE) < 16) {
					taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
				}
				if (endCal.get(Calendar.DATE) > 15) {
					taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
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

				TaskTrackApproval overtime = tasktrackApprovalService.save(taskTrackApproval);
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
				TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(beachId);

				if (taskTrackApproval != null) {

					if (startCal.get(Calendar.DATE) < 16) {
						if (taskTrackApproval.getFirstHalfStatus() != null && (taskTrackApproval.getFirstHalfStatus()
								.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION)
								|| taskTrackApproval.getFirstHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED))) {
							taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED);
						} else if (taskTrackApproval.getFirstHalfStatus() != null && (taskTrackApproval
								.getFirstHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
								|| taskTrackApproval.getFirstHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED))) {
							taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED);
						} else {
							taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						}
					}
					if (endCal.get(Calendar.DATE) > 15) {
						if (taskTrackApproval.getSecondHalfStatus() != null && (taskTrackApproval.getSecondHalfStatus()
								.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION)
								|| taskTrackApproval.getSecondHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED))) {
							taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED);
						} else if (taskTrackApproval.getSecondHalfStatus() != null && (taskTrackApproval
								.getSecondHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
								|| taskTrackApproval.getSecondHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED))) {
							taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED);
						} else {
							taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						}
					}

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
					tasktrackApprovalService.updateData(taskTrackApproval);
					beach_id = taskTrackApproval.getId();
				} else {
					throw new Exception("TaskTrack data not found for given beach id.");
				}
			} else {

				TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Beach");

				if (startCal.get(Calendar.DATE) < 16) {
					taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
				}
				if (endCal.get(Calendar.DATE) > 15) {
					taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_OPEN);
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

				TaskTrackApproval beach = tasktrackApprovalService.save(taskTrackApproval);
				beach_id = beach.getId();

			}
		}
	}

	/**
	 * @author sreejith.j
	 */
	@Override
	public void submitFirstHalfHoursForApproval(JSONObject requestData) throws Exception {

		// Obtain the data from request data
		Long billableId = null, nonBillableId = null, overtimeId = null, beachId = null, projectId = null,
				userId = null;
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

		if (billableArray.size() > 0 && billableId == null) {
			billableId = timeTrackApprovalJPARepository.getBillableIdForAUserForAProject(month, year, projectId,
					userId);
			if (billableId != null) {
				throw new DuplicateEntryException("Duplicate entry for billable.");
			}
		}
		if (nonbillableArray.size() > 0 && nonBillableId == null) {
			nonBillableId = timeTrackApprovalJPARepository.getNonBillableIdForAUserForAProject(month, year, projectId,
					userId);
			if (nonBillableId != null) {
				throw new DuplicateEntryException("Duplicate entry for NonBillable.");
			}
		}
		if (overtimeArray.size() > 0 && overtimeId == null) {
			overtimeId = timeTrackApprovalJPARepository.getOvertimeIdForAUserForAProject(month, year, projectId,
					userId);
			if (overtimeId != null) {
				throw new DuplicateEntryException("Duplicate entry for Overtime.");
			}
		}
		if (beachArray.size() > 0 && beachId == null) {
			beachId = timeTrackApprovalJPARepository.getBeachIdForAUserForAProject(month, year, projectId, userId);
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
				TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(billableId);

				if (taskTrackApproval != null) {

					taskTrackApproval.setFirstHalfStatus(
							taskTrackApproval.getFirstHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: taskTrackApproval.getFirstHalfStatus());

					if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED)
							|| taskTrackApproval.getFirstHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED)) {

						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED);

					} else if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED)
							|| taskTrackApproval.getFirstHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED)) {

						int startDay = 0, endDay = 0;
						startDay = cal.get(Calendar.DATE);
						endDay = calendar.get(Calendar.DATE);
						List<TaskTrackRejection> rejectionList = taskTrackRejectionRepository
								.findOpenRejectionForCycleForUserForProject(userId, projectId, month, year,
										Constants.TASKTRACK_REJECTION_FIRST_HALF_CYCLE);
						for (TaskTrackRejection rejectionObj : rejectionList) {
							rejectionObj.setStatus(Constants.TASKTRACK_REJECTION_STATUS_CLOSED);
						}

						taskTrackRejectionRepository.saveAll(rejectionList);
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED);
					} else {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
					}
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
					tasktrackApprovalService.updateData(taskTrackApproval);
					billable_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Billable");
				taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);

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

				TaskTrackApproval billable = tasktrackApprovalService.save(taskTrackApproval);
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
				TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(nonBillableId);

				if (taskTrackApproval != null) {

					taskTrackApproval.setFirstHalfStatus(
							taskTrackApproval.getFirstHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: taskTrackApproval.getFirstHalfStatus());
					if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED)
							|| taskTrackApproval.getFirstHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED);

					} else if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED)
							|| taskTrackApproval.getFirstHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED);
					} else {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
					}
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
					tasktrackApprovalService.updateData(taskTrackApproval);
					nonbillable_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Non-Billable");
				taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);

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

				TaskTrackApproval nonbillable = tasktrackApprovalService.save(taskTrackApproval);
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
				TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(overtimeId);

				if (taskTrackApproval != null) {

					taskTrackApproval.setFirstHalfStatus(
							taskTrackApproval.getFirstHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: taskTrackApproval.getFirstHalfStatus());

					if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED)
							|| taskTrackApproval.getFirstHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED);

					} else if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED)
							|| taskTrackApproval.getFirstHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED);
					} else {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
					}

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
					tasktrackApprovalService.updateData(taskTrackApproval);
					overtime_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Overtime");
				taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);

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

				TaskTrackApproval overtime = tasktrackApprovalService.save(taskTrackApproval);
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
			if (overtimeId != null) {
				TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(beachId);

				if (taskTrackApproval != null) {

					taskTrackApproval.setFirstHalfStatus(
							taskTrackApproval.getFirstHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: taskTrackApproval.getFirstHalfStatus());

					if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED)
							|| taskTrackApproval.getFirstHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED);

					} else if (taskTrackApproval.getFirstHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED)
							|| taskTrackApproval.getFirstHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED)) {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED);
					} else {
						taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
					}

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
					tasktrackApprovalService.updateData(taskTrackApproval);
					beach_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Beach");
				taskTrackApproval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);

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

				TaskTrackApproval beach = tasktrackApprovalService.save(taskTrackApproval);
				beach_id = beach.getId();

			}
		}
		try {
			String sendTo = "", sendCC = "", subject = "", emailReceiver = "", resource = "", approverOne = "";
			subject = "RCG Time Sheet- First half time sheet Forwarded";
			resource = user.getLastName().concat(" " + user.getFirstName());
			approverOne = project.getProjectOwner().getLastName()
					.concat(" " + project.getProjectOwner().getFirstName());
			sendCC = project.getProjectOwner().getEmail();
			sendTo = project.getOnsite_lead().getEmail().toString();
			emailReceiver = project.getOnsite_lead().getLastName().concat(" " + project.getOnsite_lead().getFirstName())
					+ ",";
			StringBuilder mailBody = new StringBuilder("Hi " + emailReceiver);
			mailBody.append("<br/><br/>Project Name : " + project.getProjectName());
			mailBody.append("<br/>Resource Name : " + resource);
			mailBody.append("<br/><br/>Timesheet for " + Month.of(month).name()
					+ " 1-15 days has been Forwarded for Level 2 Approval");
			mailBody.append("<br/><a href=" + CONTEXT_PATH + "/approve-log-two>Click here to Review and Submit</a>");
			mailBody.append("<br/><br/>Forwarded by : " + approverOne);

			sendMail(sendTo, sendCC, subject, mailBody);
		} catch (Exception e) {
		}
	}

	/**
	 * @author sreejith.j
	 */
	@Override
	public void submitSecondHalfHoursForApproval(JSONObject requestData) throws Exception {

		// Obtain the data from request data
		Long billableId = null, nonBillableId = null, overtimeId = null, beachId = null, projectId = null,
				userId = null;
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
			billableId = timeTrackApprovalJPARepository.getBillableIdForAUserForAProject(month, year, projectId,
					userId);
			if (billableId != null) {
				throw new DuplicateEntryException("Duplicate entry for billable.");
			}
		}
		if (nonbillableArray.size() > 0 && nonBillableId == null) {
			nonBillableId = timeTrackApprovalJPARepository.getNonBillableIdForAUserForAProject(month, year, projectId,
					userId);
			if (nonBillableId != null) {
				throw new DuplicateEntryException("Duplicate entry for NonBillable.");
			}
		}
		if (overtimeArray.size() > 0 && overtimeId == null) {
			overtimeId = timeTrackApprovalJPARepository.getOvertimeIdForAUserForAProject(month, year, projectId,
					userId);
			if (overtimeId != null) {
				throw new DuplicateEntryException("Duplicate entry for Overtime.");
			}
		}
		if (beachArray.size() > 0 && beachId == null) {
			beachId = timeTrackApprovalJPARepository.getOvertimeIdForAUserForAProject(month, year, projectId, userId);
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
				TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(billableId);

				if (taskTrackApproval != null) {
					taskTrackApproval.setSecondHalfStatus(
							taskTrackApproval.getSecondHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: taskTrackApproval.getSecondHalfStatus());
					if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED);

					} else if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED)
							|| taskTrackApproval.getSecondHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED)) {

						int startDay = 0, endDay = 0;
						startDay = cal.get(Calendar.DATE);
						endDay = calendar.get(Calendar.DATE);
						List<TaskTrackRejection> rejectionList = taskTrackRejectionRepository
								.findOpenRejectionForCycleForUserForProject(userId, projectId, month, year,
										Constants.TASKTRACK_REJECTION_SECOND_HALF_CYCLE);
						for (TaskTrackRejection rejectionObj : rejectionList) {
							rejectionObj.setStatus(Constants.TASKTRACK_REJECTION_STATUS_CLOSED);
						}

						taskTrackRejectionRepository.saveAll(rejectionList);
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED);
					} else {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
					}

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
					tasktrackApprovalService.updateData(taskTrackApproval);
					billable_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Billable");
				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);

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

				TaskTrackApproval billable = tasktrackApprovalService.save(taskTrackApproval);
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
				TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(nonBillableId);

				if (taskTrackApproval != null) {

					taskTrackApproval.setSecondHalfStatus(
							taskTrackApproval.getSecondHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: taskTrackApproval.getSecondHalfStatus());

					if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED)
							|| taskTrackApproval.getSecondHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED);

					} else if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED)
							|| taskTrackApproval.getSecondHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED);
					} else {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
					}

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
					tasktrackApprovalService.updateData(taskTrackApproval);
					nonbillable_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Non-Billable");
				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);

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

				TaskTrackApproval nonbillable = tasktrackApprovalService.save(taskTrackApproval);
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
				TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(overtimeId);

				if (taskTrackApproval != null) {

					taskTrackApproval.setSecondHalfStatus(
							taskTrackApproval.getSecondHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: taskTrackApproval.getSecondHalfStatus());

					if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED)
							|| taskTrackApproval.getSecondHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED);

					} else if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED)
							|| taskTrackApproval.getSecondHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED);
					} else {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
					}

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
					tasktrackApprovalService.updateData(taskTrackApproval);
					overtime_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Overtime");
				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);

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

				TaskTrackApproval overtime = tasktrackApprovalService.save(taskTrackApproval);
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
				TaskTrackApproval taskTrackApproval = tasktrackApprovalService.findById(beachId);

				if (taskTrackApproval != null) {

					taskTrackApproval.setSecondHalfStatus(
							taskTrackApproval.getSecondHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: taskTrackApproval.getSecondHalfStatus());

					if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED)
							|| taskTrackApproval.getSecondHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED);

					} else if (taskTrackApproval.getSecondHalfStatus()
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED)
							|| taskTrackApproval.getSecondHalfStatus()
									.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED)) {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED);
					} else {
						taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
					}

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
					tasktrackApprovalService.updateData(taskTrackApproval);
					beach_id = taskTrackApproval.getId();
				}
			} else {

				TaskTrackApproval taskTrackApproval = new TaskTrackApproval();
				taskTrackApproval.setMonth(month);
				taskTrackApproval.setYear(year);
				taskTrackApproval.setUser(user);
				taskTrackApproval.setProjectType("Beach");
				taskTrackApproval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);

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

				TaskTrackApproval beach = tasktrackApprovalService.save(taskTrackApproval);
				beach_id = beach.getId();

			}
		}
		try {
			String sendTo = "", sendCC = "", subject = "", emailReceiver = "", resource = "", approverOne = "";
			subject = "RCG Time Sheet- Second half time sheet Forwarded";
			resource = user.getLastName().concat(" " + user.getFirstName());
			approverOne = project.getProjectOwner().getLastName()
					.concat(" " + project.getProjectOwner().getFirstName());
			sendCC = project.getProjectOwner().getEmail();
			sendTo = project.getOnsite_lead().getEmail().toString();
			emailReceiver = project.getOnsite_lead().getLastName().concat(" " + project.getOnsite_lead().getFirstName())
					+ ",";
			StringBuilder mailBody = new StringBuilder("Hi " + emailReceiver);
			mailBody.append("<br/><br/>Project Name : " + project.getProjectName());
			mailBody.append("<br/>Resource Name : " + resource);
			mailBody.append("<br/><br/>Timesheet for " + Month.of(month).name()
					+ " 16-31 days has been Forwarded for Level 2 Approval");
			mailBody.append("<br/><a href=" + CONTEXT_PATH + "/approve-log-two>Click here to Review and Submit</a>");
			mailBody.append("<br/><br/>Forwarded by : " + approverOne);

			sendMail(sendTo, sendCC, subject, mailBody);
		} catch (Exception e) {

		}
	}

	@Override
	public void submitForRejection(JSONObject requestData) throws Exception {

		Long projectId = Long.valueOf(requestData.get("projectId").toString());
		int month = Integer.valueOf(requestData.get("month").toString());
		int year = Integer.valueOf(requestData.get("year").toString());
		Long userId = Long.valueOf(requestData.get("userId").toString());
		String remarks = requestData.get("remarks").toString();

		List<TaskTrackApproval> taskTrackApproval = timeTrackApprovalJPARepository
				.upadateTaskTrackApprovalStatus(projectId, month, year, userId);
		for (TaskTrackApproval approval : taskTrackApproval) {
			approval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION);
		}
		timeTrackApprovalJPARepository.saveAll(taskTrackApproval);
		TaskTrackRejection taskTrackRejection = new TaskTrackRejection();
		Optional<UserModel> user = userRepository.findById(userId);
		Optional<ProjectModel> project = projectRepository.findById(projectId);
		taskTrackRejection.setUser(user.get());
		taskTrackRejection.setProject(project.get());
		taskTrackRejection.setMonth(month);
		taskTrackRejection.setYear(year);
		taskTrackRejection.setRemark(remarks);
		taskTrackRejection.setStatus(Constants.TASKTRACK_REJECTION_STATUS_OPEN);
		taskTrackRejection.setCycle(Constants.TASKTRACK_REJECTION_FIRST_HALF_CYCLE);
		taskTrackRejectionRepository.save(taskTrackRejection);

		try {
			String sendTo = "", sendCC = "", subject = "", emailReceiver = "", resource = "", approverTwo = "";
			subject = "RCG Time Sheet- First half time sheet Rejected";
			resource = user.get().getLastName().concat(" " + user.get().getFirstName());
			approverTwo = project.get().getOnsite_lead().getLastName()
					.concat(" " + project.get().getOnsite_lead().getFirstName());
			sendCC = project.get().getOnsite_lead().getEmail();
			sendTo = project.get().getProjectOwner().getEmail();
			emailReceiver = project.get().getProjectOwner().getLastName()
					.concat(" " + project.get().getProjectOwner().getFirstName()) + ",";

			StringBuilder mailBody = new StringBuilder("Hi " + emailReceiver);
			mailBody.append("<br/><br/>Project Name : " + project.get().getProjectName());
			mailBody.append("<br/>Resource Name : " + resource);
			mailBody.append("<br/><br/>Timesheet for " + Month.of(month).name() + " 1-15 days has been Rejected.");
			mailBody.append("<br/>Comments : " + remarks);
			mailBody.append("<br/><a href=" + CONTEXT_PATH + "/approve-log>Click here to Re-Submit timesheet</a>");
			mailBody.append("<br/><br/>Rejected by : " + approverTwo);

			sendMail(sendTo, sendCC, subject, mailBody);
		} catch (Exception e) {

		}
	}

	@Override
	public void submitForSecondHalfRejection(JSONObject requestData) throws Exception {
		Long projectId = Long.valueOf(requestData.get("projectId").toString());
		int month = Integer.valueOf(requestData.get("month").toString());
		int year = Integer.valueOf(requestData.get("year").toString());
		Long userId = Long.valueOf(requestData.get("userId").toString());
		String remarks = requestData.get("remarks").toString();

		List<TaskTrackApproval> taskTrackApproval = timeTrackApprovalJPARepository
				.upadateTaskTrackApprovalStatus(projectId, month, year, userId);
		for (TaskTrackApproval approval : taskTrackApproval) {
			approval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION);
		}
		timeTrackApprovalJPARepository.saveAll(taskTrackApproval);
		TaskTrackRejection taskTrackRejection = new TaskTrackRejection();
		Optional<UserModel> user = userRepository.findById(userId);
		Optional<ProjectModel> project = projectRepository.findById(projectId);
		taskTrackRejection.setUser(user.get());
		taskTrackRejection.setProject(project.get());
		taskTrackRejection.setMonth(month);
		taskTrackRejection.setYear(year);
		taskTrackRejection.setRemark(remarks);
		taskTrackRejection.setStatus(Constants.TASKTRACK_REJECTION_STATUS_OPEN);
		taskTrackRejection.setCycle(Constants.TASKTRACK_REJECTION_SECOND_HALF_CYCLE);
		taskTrackRejectionRepository.save(taskTrackRejection);

		try {
			String sendTo = "", sendCC = "", subject = "", emailReceiver = "", resource = "", approverTwo = "";
			subject = "RCG Time Sheet- Second half time sheet Rejected";
			resource = user.get().getLastName().concat(" " + user.get().getFirstName());
			approverTwo = project.get().getOnsite_lead().getLastName()
					.concat(" " + project.get().getOnsite_lead().getFirstName());
			sendCC = project.get().getOnsite_lead().getEmail();
			sendTo = project.get().getProjectOwner().getEmail();
			emailReceiver = project.get().getProjectOwner().getLastName()
					.concat(" " + project.get().getProjectOwner().getFirstName()) + ",";

			StringBuilder mailBody = new StringBuilder("Hi " + emailReceiver);
			mailBody.append("<br/><br/>Project Name : " + project.get().getProjectName());
			mailBody.append("<br/>Resource Name : " + resource);
			mailBody.append("<br/><br/>Timesheet for " + Month.of(month).name() + " 16-31 days has been Rejected.");
			mailBody.append("<br/>Comments : " + remarks);
			mailBody.append("<br/><a href=" + CONTEXT_PATH + "/approve-log>Click here to Re-Submit timesheet</a>");
			mailBody.append("<br/><br/>Rejected by : " + approverTwo);

			sendMail(sendTo, sendCC, subject, mailBody);
		} catch (Exception e) {

		}
	}

	/**
	 * 
	 * @author sreejith.j
	 * @param taskTrackApproval
	 * @param i
	 * @param hours
	 */
	private void setDayInCorrespondingModel(TaskTrackApproval taskTrackApproval, int i, double hours) throws Exception {

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

	public JSONObject getInfoForApprovalLevelTwo(Long userId, Date startDate, Date endDate, Boolean isExist,
			Long projectId, Integer firstHalfDay) throws ParseException {
		JSONObject response = new JSONObject();
		String approverOneFirstHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
		String approverOneSecodHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
		String approverTwoFirstHalfStatus = Constants.TASKTRACK_FINAL_STATUS_OPEN;
		String approverTwoSecodHalfStatus = Constants.TASKTRACK_FINAL_STATUS_OPEN;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		int month = (cal.get(Calendar.MONTH) + 1);
		int year = cal.get(Calendar.YEAR);
		Double firstHalfHour = 0.0;
		Double secondHalfHour = 0.0;
		if (isExist) {
			String userName = null;
			// For Approver 1 Data
			List<TaskTrackApproval> approverOneData = new ArrayList<TaskTrackApproval>();
			approverOneData = getUserListForApproval(userId, projectId, month, year);
			firstHalfHour = 0.0;
			secondHalfHour = 0.0;
			if (approverOneData != null && approverOneData.size() > 0) {
				TaskTrackApproval billableData = new TaskTrackApproval();
				TaskTrackApproval overtimeData = new TaskTrackApproval();
				for (TaskTrackApproval task : approverOneData) {
					if (task.getProjectType().equalsIgnoreCase("Billable")) {
						approverOneFirstHalfStatus = task.getFirstHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getFirstHalfStatus();
						approverOneSecodHalfStatus = task.getSecondHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getSecondHalfStatus();
						billableData = task;
					} else if (task.getProjectType().equalsIgnoreCase("Overtime")) {
						overtimeData = task;
					}
				}

				List<Double> approverOneHourList = getApprovalTotalBillableHour(billableData, overtimeData);
				for (int i = 0; i < approverOneHourList.size(); i++) {
					if (i < firstHalfDay) {
						if (approverOneFirstHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)
								|| approverOneFirstHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED)
								|| approverOneFirstHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED)
								|| approverOneFirstHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_LOCK))
							firstHalfHour += approverOneHourList.get(i);
					} else {
						if (approverOneSecodHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)
								|| approverOneSecodHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED)
								|| approverOneSecodHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED)
								|| approverOneSecodHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_LOCK))
							secondHalfHour += approverOneHourList.get(i);
					}
				}
			} else {
				approverOneFirstHalfStatus = Constants.NOT_SUBMITTED;
				approverOneSecodHalfStatus = Constants.NOT_SUBMITTED;
			}
			JSONObject userHours = new JSONObject();
			userHours.put("firstHalfTotal", firstHalfHour);
			userHours.put("secondHalfTotal", secondHalfHour);
			List<TaskTrackApproval> approverTwoData = new ArrayList<TaskTrackApproval>();
			List<TaskTrackApprovalFinal> approvalFinalList = taskTrackApprovalFinalRepository
					.getUserFinalApprovalList(userId, projectId, month, year);
			for (TaskTrackApprovalFinal taskTrackFinal : approvalFinalList) {
				approverTwoData.add(TaskTrackApproverConverter.finalApproverToApprover(taskTrackFinal));
			}
			firstHalfHour = 0.0;
			secondHalfHour = 0.0;
			if (approverTwoData != null && approverTwoData.size() > 0) {
				TaskTrackApproval billableData = new TaskTrackApproval();
				TaskTrackApproval overtimeData = new TaskTrackApproval();
				for (TaskTrackApproval task : approverTwoData) {
					if (task.getProjectType().equalsIgnoreCase("Billable")) {
						approverTwoFirstHalfStatus = task.getFirstHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getFirstHalfStatus();
						approverTwoSecodHalfStatus = task.getSecondHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getSecondHalfStatus();
						billableData = task;
					} else if (task.getProjectType().equalsIgnoreCase("Overtime")) {
						overtimeData = task;
					}
				}

				List<Double> approverTwoHourList = getApprovalTotalBillableHour(billableData, overtimeData);
				for (int i = 0; i < approverTwoHourList.size(); i++) {
					if (i < firstHalfDay) {
						if (approverTwoFirstHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)
								|| approverTwoFirstHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
								|| approverTwoFirstHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED))
							firstHalfHour += approverTwoHourList.get(i);
					} else {
						if (approverTwoSecodHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)
								|| approverTwoSecodHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
								|| approverTwoFirstHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED))
							secondHalfHour += approverTwoHourList.get(i);
					}
				}
			} else {
				approverTwoFirstHalfStatus = Constants.NOT_SUBMITTED;
				approverTwoSecodHalfStatus = Constants.NOT_SUBMITTED;
			}

			/*
			 * Status of approver2 should be REJECTED if status in in approver1 is either
			 * REJECTED, REJECTION_SAVED or REJECTION_SUBMITTED
			 * 
			 */

			approverTwoFirstHalfStatus = (approverOneFirstHalfStatus
					.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
					|| approverOneFirstHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED)
					|| approverOneFirstHalfStatus
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED))
									? Constants.TASKTRACK_APPROVER_STATUS_REJECTION
									: approverTwoFirstHalfStatus;

			approverTwoSecodHalfStatus = (approverOneSecodHalfStatus
					.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
					|| approverOneSecodHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED)
					|| approverOneSecodHalfStatus
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED))
									? Constants.TASKTRACK_APPROVER_STATUS_REJECTION
									: approverTwoSecodHalfStatus;

			JSONObject savedHour = new JSONObject();
			savedHour.put("firstHalfTotal", firstHalfHour);
			savedHour.put("secondHalfTotal", secondHalfHour);
			if (userName == null || userName.isEmpty()) {
				String uName = userService.getUserName(userId);
				userName = String.valueOf(uName).replace(",", " ");
			}

			// Rejection messages from REJECTION Table

			List<TaskTrackRejection> rejectionFirstHalf = taskTrackRejectionRepository
					.findOpenRejectionForCycleForUserForProject(userId, projectId, month, year,
							Constants.TASKTRACK_REJECTION_FIRST_HALF_CYCLE);
			List<TaskTrackRejection> rejectionSecondHalf = taskTrackRejectionRepository
					.findOpenRejectionForCycleForUserForProject(userId, projectId, month, year,
							Constants.TASKTRACK_REJECTION_SECOND_HALF_CYCLE);

			String firstHalfRemark = "", secondHalfRemark = "";
			for (TaskTrackRejection rejectionObj : rejectionFirstHalf) {
				firstHalfRemark = rejectionObj.getRemark();
			}
			for (TaskTrackRejection rejectionObj : rejectionSecondHalf) {
				secondHalfRemark = rejectionObj.getRemark();
			}

			response.put("userId", userId);
			response.put("userName", userName);
			response.put("month", month);
			response.put("approvalOneHours", userHours);
			response.put("approvalTwoHours", savedHour);
			response.put("approverOneFirstHalfStatus", approverOneFirstHalfStatus);
			response.put("approverOneSecodHalfStatus", approverOneSecodHalfStatus);
			response.put("approverTwoFirstHalfStatus", approverTwoFirstHalfStatus);
			response.put("approverTwoSecodHalfStatus", approverTwoSecodHalfStatus);
			response.put("firstHalfRemark", firstHalfRemark);
			response.put("secondHalfRemark", secondHalfRemark);
		} else {
			response.put("userId", userId);
			String uName = userService.getUserName(userId);
			String userName = String.valueOf(uName).replace(",", " ");
			response.put("userName", userName);
			response.put("month", month);
			JSONObject totalHour = new JSONObject();
			totalHour.put("firstHalfTotal", firstHalfHour);
			totalHour.put("secondHalfTotal", secondHalfHour);
			response.put("approvalOneHours", totalHour);
			response.put("approvalTwoHours", totalHour);
			response.put("approverOneFirstHalfStatus", approverOneFirstHalfStatus);
			response.put("approverOneSecodHalfStatus", approverOneSecodHalfStatus);
			response.put("approverTwoFirstHalfStatus", approverTwoFirstHalfStatus);
			response.put("approverTwoSecodHalfStatus", approverTwoSecodHalfStatus);
			response.put("firstHalfRemark", "");
			response.put("secondHalfRemark", "");
		}
		return response;
	}

	@Override
	public JSONObject getDataForApprovalLevelTwo(Long userId, Date startDate, Date endDate, Long projectId,
			Integer firstHalfDay) {
		// TODO Auto-generated method stub

		JSONObject response = new JSONObject();
		JSONObject billableHours;
		JSONObject billableHours2;
		JSONObject overtimeHours2;
		JSONObject nonBillableHours2;
		JSONObject beachHours2;
		JSONObject billableHoursOneCopy;
		JSONObject nonBillableHoursOneCopy;
		JSONObject overTimeHoursOneCopy;
		JSONObject beachHoursOneCopy;
		List<Integer> correctionDays = null;
		List<JSONObject> billableArrayOne = new ArrayList<>();
		List<JSONObject> overTimeArrayOne = new ArrayList<>();
		List<JSONObject> billableArrayOneCopy = new ArrayList<>();
		List<JSONObject> overTimeArrayOneCopy = new ArrayList<>();
		List<JSONObject> nonBillableArrayOneCopy = new ArrayList<>();
		List<JSONObject> beachArrayOneCopy = new ArrayList<>();
		List<JSONObject> billableArray = new ArrayList<>();
		List<JSONObject> overTimeArray = new ArrayList<>();
		List<JSONObject> nonbillableArray = new ArrayList<>();
		List<JSONObject> beachArray = new ArrayList<>();
		ArrayList<JSONObject> nonbillableArray2 = new ArrayList<JSONObject>();
		ArrayList<JSONObject> beachArray2 = new ArrayList<JSONObject>();
		JSONObject approverOneDatas = new JSONObject();
		JSONObject approverTwoDatas = new JSONObject();
		String approvalStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
		String approverOneFirstHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
		String approverOneSecodHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
		String approverTwoFirstHalfStatus = Constants.TASKTRACK_FINAL_STATUS_OPEN;
		String approverTwoSecodHalfStatus = Constants.TASKTRACK_FINAL_STATUS_OPEN;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		int month = (cal.get(Calendar.MONTH) + 1);
		int year = cal.get(Calendar.YEAR);
		int startDay = cal.get(Calendar.DATE);
		cal.setTime(endDate);
		int endDay = cal.get(Calendar.DATE);

		Double firstHalfHour = 0.0;
		Double secondHalfHour = 0.0;

		Long billableId = null;
		Long overtimeId = null;
		Long nonbillableId = null;
		Long beachId = null;

		String userName = null;
		String projectName = null;
		Long projectIdOne = null;
		// For Approver 1 Data
		List<TaskTrackApproval> approverOneData = new ArrayList<TaskTrackApproval>();
		// approverOneData = getUserListForApproval(userId, projectId, month, year);
		approverOneData = timeTrackApprovalJPARepository.getUserListForApprovalApproverOne(userId, projectId, month,
				year);
		firstHalfHour = 0.0;
		secondHalfHour = 0.0;
		if (approverOneData != null && approverOneData.size() > 0) {
			billableHours = new JSONObject();
			JSONObject overtimeHours = new JSONObject();
			JSONObject nonBillableHours = new JSONObject();
			JSONObject beachHours = new JSONObject();
			for (TaskTrackApproval task : approverOneData) {
				cal.setTime(startDate);
				Double hours = 0.0;

				for (int i = startDay; i <= endDay; i++) {
					int day = cal.get(Calendar.DATE);
					String taskDate = cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : "" + month) + "-"
							+ (day < 10 ? "0" + day : "" + day);

					switch (i) {
					case 1:
						hours = (Double) task.getDay1();
						break;
					case 2:
						hours = (Double) task.getDay2();
						break;
					case 3:
						hours = (Double) task.getDay3();
						break;
					case 4:
						hours = (Double) task.getDay4();
						break;
					case 5:
						hours = (Double) task.getDay5();
						break;
					case 6:
						hours = (Double) task.getDay6();
						break;
					case 7:
						hours = (Double) task.getDay7();
						break;
					case 8:
						hours = (Double) task.getDay8();
						break;
					case 9:
						hours = (Double) task.getDay9();
						break;
					case 10:
						hours = (Double) task.getDay10();
						break;
					case 11:
						hours = (Double) task.getDay11();
						break;
					case 12:
						hours = (Double) task.getDay12();
						break;
					case 13:
						hours = (Double) task.getDay13();
						break;
					case 14:
						hours = (Double) task.getDay14();
						break;
					case 15:
						hours = (Double) task.getDay15();
						break;
					case 16:
						hours = (Double) task.getDay16();
						break;
					case 17:
						hours = (Double) task.getDay17();
						break;
					case 18:
						hours = (Double) task.getDay18();
						break;
					case 19:
						hours = (Double) task.getDay19();
						break;
					case 20:
						hours = (Double) task.getDay20();
						break;
					case 21:
						hours = (Double) task.getDay21();
						break;
					case 22:
						hours = (Double) task.getDay22();
						break;
					case 23:
						hours = (Double) task.getDay23();
						break;
					case 24:
						hours = (Double) task.getDay24();
						break;
					case 25:
						hours = (Double) task.getDay25();
						break;
					case 26:
						hours = (Double) task.getDay26();
						break;
					case 27:
						hours = (Double) task.getDay27();
						break;
					case 28:
						hours = (Double) task.getDay28();
						break;
					case 29:
						hours = (Double) task.getDay29();
						break;
					case 30:
						hours = (Double) task.getDay30();
						break;
					case 31:
						hours = (Double) task.getDay31();
						break;
					}

					if (task.getProjectType().equalsIgnoreCase("Billable")) {

						billableHours.put(taskDate, hours);
						projectIdOne = task.getProject().getProjectId();
						projectName = task.getProject().getProjectName();
						approverOneFirstHalfStatus = task.getFirstHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getFirstHalfStatus();
						approverOneSecodHalfStatus = task.getSecondHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getSecondHalfStatus();
						billableHoursOneCopy = new JSONObject();
						billableHoursOneCopy.put(taskDate, hours);
						billableArrayOneCopy.add(billableHoursOneCopy);

					} else if (task.getProjectType().equalsIgnoreCase("Overtime")) {

						overTimeHoursOneCopy = new JSONObject();
						overTimeHoursOneCopy.put(taskDate, hours);
						overTimeArrayOneCopy.add(overTimeHoursOneCopy);
						overtimeHours.put(taskDate, hours);

					} else if (task.getProjectType().equalsIgnoreCase("Non-Billable")) {
						nonBillableHoursOneCopy = new JSONObject();
						nonBillableHoursOneCopy.put(taskDate, hours);
						nonBillableArrayOneCopy.add(nonBillableHoursOneCopy);
						// temporaryObject = new JSONObject();
						// nonBillableHours2 = new JSONObject();
						nonBillableHours.put(taskDate, hours);
						// nonBillableHours2.put(taskDate, hours);
						// nonbillableArray2.add(nonBillableHours2);
						// nonbillableId = task.getId();
					} else if (task.getProjectType().equalsIgnoreCase("Beach")) {
						beachHoursOneCopy = new JSONObject();
						beachHoursOneCopy.put(taskDate, hours);
						beachArrayOneCopy.add(beachHoursOneCopy);
						// temporaryObject = new JSONObject();
						// beachHours2 = new JSONObject();
						beachHours.put(taskDate, hours);
						// beachHours2.put(taskDate, hours);
						// beachArray2.add(beachHours2);
						// nonbillableId = task.getId();
					}
					cal.add(Calendar.DATE, 1);
				}
			}
			billableArray.add(billableHours);
			overTimeArray.add(overtimeHours);
			nonbillableArray.add(nonBillableHours);
			beachArray.add(beachHours);

		}

		else {
			cal.setTime(startDate);
			for (int i = startDay; i <= endDay; i++) {

				int day = cal.get(Calendar.DATE);
				String taskDate = cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : "" + month) + "-"
						+ (day < 10 ? "0" + day : "" + day);

				JSONObject jsonObject = new JSONObject();
				jsonObject.put(taskDate, 0);
				billableArray.add(jsonObject);
				overTimeArray.add(jsonObject);
				nonbillableArray.add(jsonObject);
				beachArray.add(jsonObject);

				cal.add(Calendar.DATE, 1);
			}
		}
		approverOneDatas.put("billable", billableArray);
		approverOneDatas.put("overTime", overTimeArray);
		approverOneDatas.put("nonBillable", nonbillableArray);
		approverOneDatas.put("beach", beachArray);
		JSONObject userHours = new JSONObject();
		userHours.put("firstHalfTotal", firstHalfHour);
		userHours.put("secondHalfTotal", secondHalfHour);
		List<TaskTrackApproval> approverTwoData = new ArrayList<TaskTrackApproval>();
		List<TaskTrackApprovalFinal> approvalFinalList = taskTrackApprovalFinalRepository
				.getUserFinalApprovalList(userId, projectId, month, year);
		for (TaskTrackApprovalFinal taskTrackFinal : approvalFinalList) {
			approverTwoData.add(TaskTrackApproverConverter.finalApproverToApprover(taskTrackFinal));
		}
		firstHalfHour = 0.0;
		secondHalfHour = 0.0;
		ArrayList<JSONObject> billableArray2 = new ArrayList<JSONObject>();

		ArrayList<JSONObject> overTimeArray2 = new ArrayList<JSONObject>();
		ArrayList<JSONObject> nonbillableArray3 = new ArrayList<JSONObject>();
		ArrayList<JSONObject> beachArray3 = new ArrayList<JSONObject>();
		JSONObject nonBillableHours3;
		JSONObject beachHours3;
		if (approverTwoData != null && approverTwoData.size() > 0) {
			if (((firstHalfDay >= startDay) && approverTwoData.get(0).getFirstHalfStatus() != null)
					|| ((firstHalfDay <= startDay) && approverTwoData.get(0).getSecondHalfStatus() != null)) {
				for (TaskTrackApproval task : approverTwoData) {

					cal.setTime(startDate);
					Double hours = 0.0;
					for (int i = startDay; i <= endDay; i++) {
						int day = cal.get(Calendar.DATE);
						String taskDate = cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : "" + month) + "-"
								+ (day < 10 ? "0" + day : "" + day);

						switch (i) {
						case 1:
							hours = (Double) task.getDay1();
							break;
						case 2:
							hours = (Double) task.getDay2();
							break;
						case 3:
							hours = (Double) task.getDay3();
							break;
						case 4:
							hours = (Double) task.getDay4();
							break;
						case 5:
							hours = (Double) task.getDay5();
							break;
						case 6:
							hours = (Double) task.getDay6();
							break;
						case 7:
							hours = (Double) task.getDay7();
							break;
						case 8:
							hours = (Double) task.getDay8();
							break;
						case 9:
							hours = (Double) task.getDay9();
							break;
						case 10:
							hours = (Double) task.getDay10();
							break;
						case 11:
							hours = (Double) task.getDay11();
							break;
						case 12:
							hours = (Double) task.getDay12();
							break;
						case 13:
							hours = (Double) task.getDay13();
							break;
						case 14:
							hours = (Double) task.getDay14();
							break;
						case 15:
							hours = (Double) task.getDay15();
							break;
						case 16:
							hours = (Double) task.getDay16();
							break;
						case 17:
							hours = (Double) task.getDay17();
							break;
						case 18:
							hours = (Double) task.getDay18();
							break;
						case 19:
							hours = (Double) task.getDay19();
							break;
						case 20:
							hours = (Double) task.getDay20();
							break;
						case 21:
							hours = (Double) task.getDay21();
							break;
						case 22:
							hours = (Double) task.getDay22();
							break;
						case 23:
							hours = (Double) task.getDay23();
							break;
						case 24:
							hours = (Double) task.getDay24();
							break;
						case 25:
							hours = (Double) task.getDay25();
							break;
						case 26:
							hours = (Double) task.getDay26();
							break;
						case 27:
							hours = (Double) task.getDay27();
							break;
						case 28:
							hours = (Double) task.getDay28();
							break;
						case 29:
							hours = (Double) task.getDay29();
							break;
						case 30:
							hours = (Double) task.getDay30();
							break;
						case 31:
							hours = (Double) task.getDay31();
							break;
						}
						if (task.getProjectType().equalsIgnoreCase("Billable")) {
							billableHours2 = new JSONObject();
							billableHours2.put(taskDate, hours);
							billableArray2.add(billableHours2);
							billableId = task.getId();
							approverTwoFirstHalfStatus = task.getFirstHalfStatus() == null
									? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: task.getFirstHalfStatus();
							approverTwoSecodHalfStatus = task.getSecondHalfStatus() == null
									? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: task.getSecondHalfStatus();
						} else if (task.getProjectType().equalsIgnoreCase("Overtime")) {
							overtimeHours2 = new JSONObject();
							overtimeHours2.put(taskDate, hours);
							overTimeArray2.add(overtimeHours2);
							overtimeId = task.getId();
						} else if (task.getProjectType().equalsIgnoreCase("Non-Billable")) {
							nonBillableHours3 = new JSONObject();
							nonBillableHours3.put(taskDate, hours);
							nonbillableArray3.add(nonBillableHours3);
							nonbillableId = task.getId();
						} else if (task.getProjectType().equalsIgnoreCase("Beach")) {
							beachHours3 = new JSONObject();
							beachHours3.put(taskDate, hours);
							beachArray3.add(beachHours3);
							beachId = task.getId();
						}

						if (task.getProjectType().equalsIgnoreCase("Billable")) {
							if (startDay <= firstHalfDay) {
								approvalStatus = task.getFirstHalfStatus() == null
										? Constants.TASKTRACK_APPROVER_STATUS_OPEN
										: task.getFirstHalfStatus();
							} else {
								approvalStatus = task.getSecondHalfStatus() == null
										? Constants.TASKTRACK_APPROVER_STATUS_OPEN
										: task.getSecondHalfStatus();
							}
							if (approvalStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION)
									|| approvalStatus
											.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED)) {
								List<TaskTrackCorrection> corrections = taskTrackCorrectionRepository
										.findCorrectionDays(task.getUser().getUserId(),
												task.getProject().getProjectId(), task.getMonth(), task.getYear(),
												startDay, endDay);
								correctionDays = new ArrayList<Integer>();
								for (TaskTrackCorrection correction : corrections) {
									correctionDays.add(correction.getDay());
								}
							}
						}
						cal.add(Calendar.DATE, 1);
					}

				}
				approverTwoDatas.put("billable", billableArray2);
				approverTwoDatas.put("overTime", overTimeArray2);
				approverTwoDatas.put("nonBillable", nonbillableArray3);
				approverTwoDatas.put("beach", beachArray3);
				approverTwoDatas.put("billableId", billableId);
				approverTwoDatas.put("nonBillableId", nonbillableId);
				approverTwoDatas.put("overtimeId", overtimeId);
				approverTwoDatas.put("beachId", beachId);
			} else {
				// first table data
				for (TaskTrackApproval task : approverTwoData) {
					if (task.getProjectType().equalsIgnoreCase("Billable")) {

						billableId = task.getId();
						approverTwoFirstHalfStatus = task.getFirstHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getFirstHalfStatus();
						approverTwoSecodHalfStatus = task.getSecondHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getSecondHalfStatus();
					} else if (task.getProjectType().equalsIgnoreCase("Overtime")) {

						overtimeId = task.getId();
					} else if (task.getProjectType().equalsIgnoreCase("Non-Billable")) {
						nonbillableId = task.getId();
					} else if (task.getProjectType().equalsIgnoreCase("Beach")) {
						beachId = task.getId();
					}
				}
				approverTwoDatas.put("billable", billableArrayOneCopy);
				approverTwoDatas.put("overTime", overTimeArrayOneCopy);
				approverTwoDatas.put("nonBillable", nonBillableArrayOneCopy);
				approverTwoDatas.put("beach", beachArrayOneCopy);
				approverTwoDatas.put("billableId", billableId);
				approverTwoDatas.put("nonBillableId", nonbillableId);
				approverTwoDatas.put("overtimeId", overtimeId);
				approverTwoDatas.put("beachId", beachId);
			}
		} else {
			// first table data
			if (approverOneData != null && approverOneData.size() > 0) {
				approverTwoDatas.put("billable", billableArrayOneCopy);
				approverTwoDatas.put("overTime", overTimeArrayOneCopy);
				approverTwoDatas.put("nonBillable", nonBillableArrayOneCopy);
				approverTwoDatas.put("beach", beachArrayOneCopy);
				approverTwoDatas.put("billableId", billableId);
				approverTwoDatas.put("nonBillableId", nonbillableId);
				approverTwoDatas.put("overtimeId", overtimeId);
				approverTwoDatas.put("beachId", beachId);
			} else {
				cal.setTime(startDate);
				for (int i = startDay; i <= endDay; i++) {

					int day = cal.get(Calendar.DATE);
					String taskDate = cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : "" + month) + "-"
							+ (day < 10 ? "0" + day : "" + day);

					JSONObject jsonObject = new JSONObject();
					jsonObject.put(taskDate, 0);
					billableArray2.add(jsonObject);
					overTimeArray2.add(jsonObject);
					nonbillableArray2.add(jsonObject);
					beachArray2.add(jsonObject);

					cal.add(Calendar.DATE, 1);
				}
				approverTwoDatas.put("billable", billableArray2);
				approverTwoDatas.put("overTime", overTimeArray2);
				approverTwoDatas.put("nonBillable", nonbillableArray2);
				approverTwoDatas.put("beach", beachArray2);
				approverTwoDatas.put("billableId", billableId);
				approverTwoDatas.put("nonBillableId", nonbillableId);
				approverTwoDatas.put("overtimeId", overtimeId);
				approverTwoDatas.put("beachId", beachId);
			}

		}

		if (userName == null || userName.isEmpty()) {
			String uName = userService.getUserName(userId);
			userName = String.valueOf(uName).replace(",", " ");
		}

		response.put("projectName", projectName);
		response.put("userId", userId);
		response.put("userName", userName);
		response.put("month", month);
		response.put("approverOneData", approverOneDatas);
		response.put("approverTwoData", approverTwoDatas);
		response.put("approverOneFirstHalfStatus", approverOneFirstHalfStatus);
		response.put("approverOneSecodHalfStatus", approverOneSecodHalfStatus);
		response.put("approverTwoFirstHalfStatus", approverTwoFirstHalfStatus);
		response.put("approverTwoSecodHalfStatus", approverTwoSecodHalfStatus);
		response.put("correctionDays", correctionDays);

		return response;
	}

	@Override
	public JSONObject getDataForApprovalFinance(Long userId, Date startDate, Date endDate, Long projectId,
			Integer firstHalfDay) {
		// TODO Auto-generated method stub

		JSONObject response = new JSONObject();

		JSONObject billableHours2;
		JSONObject overtimeHours2;
		List<Integer> correctionDays = null;
		JSONObject approverTwoDatas = new JSONObject();
		String approvalStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
		String approverTwoFirstHalfStatus = Constants.TASKTRACK_FINAL_STATUS_OPEN;
		String approverTwoSecodHalfStatus = Constants.TASKTRACK_FINAL_STATUS_OPEN;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		int month = (cal.get(Calendar.MONTH) + 1);
		int year = cal.get(Calendar.YEAR);
		int startDay = cal.get(Calendar.DATE);
		cal.setTime(endDate);
		int endDay = cal.get(Calendar.DATE);

		Double firstHalfHour = 0.0;
		Double secondHalfHour = 0.0;

		Long billableId = null;
		Long overtimeId = null;

		String userName = null;
		String projectName = null;
		Long projectIdOne = null;

		List<TaskTrackApproval> approverTwoData = new ArrayList<TaskTrackApproval>();
		List<TaskTrackApprovalFinal> approvalFinalList = taskTrackApprovalFinalRepository
				.getUserFinalApprovalList(userId, projectId, month, year);
		for (TaskTrackApprovalFinal taskTrackFinal : approvalFinalList) {
			approverTwoData.add(TaskTrackApproverConverter.finalApproverToApprover(taskTrackFinal));
		}

		ArrayList<JSONObject> billableArray2 = new ArrayList<JSONObject>();
		ArrayList<JSONObject> overTimeArray2 = new ArrayList<JSONObject>();
		if (approverTwoData != null && approverTwoData.size() > 0) {
			billableHours2 = new JSONObject();
			overtimeHours2 = new JSONObject();
			for (TaskTrackApproval task : approverTwoData) {
				projectName = task.getProject().getProjectName();
				cal.setTime(startDate);
				Double hours = 0.0;

				for (int i = startDay; i <= endDay; i++) {
					int day = cal.get(Calendar.DATE);
					String taskDate = cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : "" + month) + "-"
							+ (day < 10 ? "0" + day : "" + day);

					switch (i) {
					case 1:
						hours = (Double) task.getDay1();
						break;
					case 2:
						hours = (Double) task.getDay2();
						break;
					case 3:
						hours = (Double) task.getDay3();
						break;
					case 4:
						hours = (Double) task.getDay4();
						break;
					case 5:
						hours = (Double) task.getDay5();
						break;
					case 6:
						hours = (Double) task.getDay6();
						break;
					case 7:
						hours = (Double) task.getDay7();
						break;
					case 8:
						hours = (Double) task.getDay8();
						break;
					case 9:
						hours = (Double) task.getDay9();
						break;
					case 10:
						hours = (Double) task.getDay10();
						break;
					case 11:
						hours = (Double) task.getDay11();
						break;
					case 12:
						hours = (Double) task.getDay12();
						break;
					case 13:
						hours = (Double) task.getDay13();
						break;
					case 14:
						hours = (Double) task.getDay14();
						break;
					case 15:
						hours = (Double) task.getDay15();
						break;
					case 16:
						hours = (Double) task.getDay16();
						break;
					case 17:
						hours = (Double) task.getDay17();
						break;
					case 18:
						hours = (Double) task.getDay18();
						break;
					case 19:
						hours = (Double) task.getDay19();
						break;
					case 20:
						hours = (Double) task.getDay20();
						break;
					case 21:
						hours = (Double) task.getDay21();
						break;
					case 22:
						hours = (Double) task.getDay22();
						break;
					case 23:
						hours = (Double) task.getDay23();
						break;
					case 24:
						hours = (Double) task.getDay24();
						break;
					case 25:
						hours = (Double) task.getDay25();
						break;
					case 26:
						hours = (Double) task.getDay26();
						break;
					case 27:
						hours = (Double) task.getDay27();
						break;
					case 28:
						hours = (Double) task.getDay28();
						break;
					case 29:
						hours = (Double) task.getDay29();
						break;
					case 30:
						hours = (Double) task.getDay30();
						break;
					case 31:
						hours = (Double) task.getDay31();
						break;
					}
					if (task.getProjectType().equalsIgnoreCase("Billable")) {

						billableHours2.put(taskDate, hours);

						billableId = task.getId();
						approverTwoFirstHalfStatus = task.getFirstHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getFirstHalfStatus();
						approverTwoSecodHalfStatus = task.getSecondHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getSecondHalfStatus();
					} else if (task.getProjectType().equalsIgnoreCase("Overtime")) {

						overtimeHours2.put(taskDate, hours);

						overtimeId = task.getId();
					}

					if (task.getProjectType().equalsIgnoreCase("Billable")) {
						if (startDay <= firstHalfDay) {
							approvalStatus = task.getFirstHalfStatus() == null
									? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: task.getFirstHalfStatus();
						} else {
							approvalStatus = task.getSecondHalfStatus() == null
									? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: task.getSecondHalfStatus();
						}
						if (approvalStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION)
								|| approvalStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED)) {
							List<TaskTrackCorrection> corrections = taskTrackCorrectionRepository.findCorrectionDays(
									task.getUser().getUserId(), task.getProject().getProjectId(), task.getMonth(),
									task.getYear(), startDay, endDay);
							correctionDays = new ArrayList<Integer>();
							for (TaskTrackCorrection correction : corrections) {
								correctionDays.add(correction.getDay());
							}
						}
					}
					cal.add(Calendar.DATE, 1);
				}

			}
			billableArray2.add(billableHours2);
			overTimeArray2.add(overtimeHours2);
			approverTwoDatas.put("billable", billableArray2);
			approverTwoDatas.put("overTime", overTimeArray2);
			approverTwoDatas.put("billableId", billableId);
			approverTwoDatas.put("overtimeId", overtimeId);
		} else {
			cal.setTime(startDate);
			JSONObject jsonObject = new JSONObject();
			for (int i = startDay; i <= endDay; i++) {

				int day = cal.get(Calendar.DATE);
				String taskDate = cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : "" + month) + "-"
						+ (day < 10 ? "0" + day : "" + day);

				jsonObject.put(taskDate, 0);

				cal.add(Calendar.DATE, 1);
			}
			billableArray2.add(jsonObject);
			overTimeArray2.add(jsonObject);
			approverTwoDatas.put("billable", billableArray2);
			approverTwoDatas.put("overTime", overTimeArray2);
			approverTwoDatas.put("billableId", billableId);
			approverTwoDatas.put("overtimeId", overtimeId);
		}

		if (userName == null || userName.isEmpty()) {
			String uName = userService.getUserName(userId);
			userName = String.valueOf(uName).replace(",", " ");
		}

		response.put("projectName", projectName);
		response.put("userId", userId);
		response.put("userName", userName);
		response.put("month", month);
		response.put("approverTwoData", approverTwoDatas);
		response.put("approverTwoFirstHalfStatus", approverTwoFirstHalfStatus);
		response.put("approverTwoSecondHalfStatus", approverTwoSecodHalfStatus);
		response.put("correctionDays", correctionDays);

		return response;
	}

	public JSONObject getInfoForFinance(Long userId, Date startDate, Date endDate, Boolean isExist, Long projectId,
			Integer firstHalfDay) throws ParseException {
		JSONObject response = new JSONObject();
		String approverOneFirstHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
		String approverOneSecodHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
		String approverTwoFirstHalfStatus = Constants.TASKTRACK_FINAL_STATUS_OPEN;
		String approverTwoSecodHalfStatus = Constants.TASKTRACK_FINAL_STATUS_OPEN;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		int month = (cal.get(Calendar.MONTH) + 1);
		int year = cal.get(Calendar.YEAR);
		Double firstHalfHour = 0.0;
		Double secondHalfHour = 0.0;
		if (isExist) {
			String userName = null;
			// For Approver 1 Data
			List<TaskTrackApproval> approverOneData = new ArrayList<TaskTrackApproval>();
			approverOneData = getUserListForApproval(userId, projectId, month, year);
			firstHalfHour = 0.0;
			secondHalfHour = 0.0;
			if (approverOneData != null && approverOneData.size() > 0) {
				TaskTrackApproval billableData = new TaskTrackApproval();
				TaskTrackApproval overtimeData = new TaskTrackApproval();
				for (TaskTrackApproval task : approverOneData) {
					if (task.getProjectType().equalsIgnoreCase("Billable")) {
						approverOneFirstHalfStatus = task.getFirstHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getFirstHalfStatus();
						approverOneSecodHalfStatus = task.getSecondHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getSecondHalfStatus();
						billableData = task;
					} else if (task.getProjectType().equalsIgnoreCase("Overtime")) {
						overtimeData = task;
					}
				}

				List<Double> approverOneHourList = getApprovalTotalBillableHour(billableData, overtimeData);
				for (int i = 0; i < approverOneHourList.size(); i++) {
					if (i < firstHalfDay) {
						if (approverOneFirstHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)
								|| approverOneFirstHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED)
								|| approverOneFirstHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED)
								|| approverOneFirstHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_LOCK))
							firstHalfHour += approverOneHourList.get(i);
					} else {
						if (approverOneSecodHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)
								|| approverOneSecodHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED)
								|| approverOneSecodHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED)
								|| approverOneFirstHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_LOCK))
							secondHalfHour += approverOneHourList.get(i);
					}
				}
			} else {
				approverOneFirstHalfStatus = Constants.NOT_SUBMITTED;
				approverOneSecodHalfStatus = Constants.NOT_SUBMITTED;
			}
			JSONObject userHours = new JSONObject();
			userHours.put("firstHalfTotal", firstHalfHour);
			userHours.put("secondHalfTotal", secondHalfHour);
			List<TaskTrackApproval> approverTwoData = new ArrayList<TaskTrackApproval>();
			List<TaskTrackApprovalFinal> approvalFinalList = taskTrackApprovalFinalRepository
					.getUserFinalApprovalList(userId, projectId, month, year);
			for (TaskTrackApprovalFinal taskTrackFinal : approvalFinalList) {
				approverTwoData.add(TaskTrackApproverConverter.finalApproverToApprover(taskTrackFinal));
			}
			firstHalfHour = 0.0;
			secondHalfHour = 0.0;
			if (approverTwoData != null && approverTwoData.size() > 0) {
				TaskTrackApproval billableData = new TaskTrackApproval();
				TaskTrackApproval overtimeData = new TaskTrackApproval();
				for (TaskTrackApproval task : approverTwoData) {
					if (task.getProjectType().equalsIgnoreCase("Billable")) {
						approverTwoFirstHalfStatus = task.getFirstHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getFirstHalfStatus();
						approverTwoSecodHalfStatus = task.getSecondHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: task.getSecondHalfStatus();
						billableData = task;
					} else if (task.getProjectType().equalsIgnoreCase("Overtime")) {
						overtimeData = task;
					}
				}

				List<Double> approverTwoHourList = getApprovalTotalBillableHour(billableData, overtimeData);
				for (int i = 0; i < approverTwoHourList.size(); i++) {
					if (i < firstHalfDay) {
						if (approverTwoFirstHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)
								|| approverTwoFirstHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
								|| approverTwoFirstHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED))
							firstHalfHour += approverTwoHourList.get(i);
					} else {
						if (approverTwoSecodHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)
								|| approverTwoFirstHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION)
								|| approverTwoFirstHalfStatus
										.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_CORRECTION_SAVED))
							secondHalfHour += approverTwoHourList.get(i);
					}
				}
			} else {
				approverTwoFirstHalfStatus = Constants.NOT_SUBMITTED;
				approverTwoSecodHalfStatus = Constants.NOT_SUBMITTED;
			}

			/*
			 * Status of approver2 should be REJECTED if status in in approver1 is either
			 * REJECTED, REJECTION_SAVED or REJECTION_SUBMITTED
			 *
			 */

			approverTwoFirstHalfStatus = (approverOneFirstHalfStatus
					.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
					|| approverOneFirstHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED)
					|| approverOneFirstHalfStatus
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED))
									? Constants.TASKTRACK_APPROVER_STATUS_REJECTION
									: approverTwoFirstHalfStatus;

			approverTwoSecodHalfStatus = (approverOneSecodHalfStatus
					.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
					|| approverOneSecodHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SAVED)
					|| approverOneSecodHalfStatus
							.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED))
									? Constants.TASKTRACK_APPROVER_STATUS_REJECTION
									: approverTwoSecodHalfStatus;

			JSONObject savedHour = new JSONObject();
			savedHour.put("firstHalfTotal", firstHalfHour);
			savedHour.put("secondHalfTotal", secondHalfHour);
			if (userName == null || userName.isEmpty()) {
				String uName = userService.getUserName(userId);
				userName = String.valueOf(uName).replace(",", " ");
			}

			// Rejection messages from REJECTION Table

			List<TaskTrackRejection> rejectionFirstHalf = taskTrackRejectionRepository
					.findOpenRejectionForCycleForUserForProject(userId, projectId, month, year,
							Constants.TASKTRACK_REJECTION_FIRST_HALF_CYCLE);
			List<TaskTrackRejection> rejectionSecondHalf = taskTrackRejectionRepository
					.findOpenRejectionForCycleForUserForProject(userId, projectId, month, year,
							Constants.TASKTRACK_REJECTION_SECOND_HALF_CYCLE);

			String firstHalfRemark = "", secondHalfRemark = "";
			for (TaskTrackRejection rejectionObj : rejectionFirstHalf) {
				firstHalfRemark = rejectionObj.getRemark();
			}
			for (TaskTrackRejection rejectionObj : rejectionSecondHalf) {
				secondHalfRemark = rejectionObj.getRemark();
			}
			approverTwoFirstHalfStatus = approverTwoFirstHalfStatus
					.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_OPEN)
					|| approverTwoFirstHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
							? Constants.NOT_SUBMITTED
							: approverTwoFirstHalfStatus;
			approverTwoSecodHalfStatus = approverTwoSecodHalfStatus
					.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_OPEN)
					|| approverTwoSecodHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
							? Constants.NOT_SUBMITTED
							: approverTwoSecodHalfStatus;
			response.put("userId", userId);
			response.put("userName", userName);
			response.put("month", month);
			response.put("approvalOneHours", userHours);
			response.put("approvalTwoHours", savedHour);
			response.put("approverOneFirstHalfStatus", approverOneFirstHalfStatus);
			response.put("approverOneSecondHalfStatus", approverOneSecodHalfStatus);
			response.put("approverTwoFirstHalfStatus", approverTwoFirstHalfStatus);
			response.put("approverTwoSecondHalfStatus", approverTwoSecodHalfStatus);
			response.put("firstHalfRemark", firstHalfRemark);
			response.put("secondHalfRemark", secondHalfRemark);
		} else {
			response.put("userId", userId);
			String uName = userService.getUserName(userId);
			String userName = String.valueOf(uName).replace(",", " ");
			response.put("userName", userName);
			response.put("month", month);
			JSONObject totalHour = new JSONObject();
			approverTwoFirstHalfStatus = approverTwoFirstHalfStatus
					.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_OPEN)
					|| approverTwoFirstHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
							? Constants.NOT_SUBMITTED
							: approverTwoFirstHalfStatus;
			approverTwoSecodHalfStatus = approverTwoSecodHalfStatus
					.equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_OPEN)
					|| approverTwoSecodHalfStatus.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)
							? Constants.NOT_SUBMITTED
							: approverTwoSecodHalfStatus;
			totalHour.put("firstHalfTotal", firstHalfHour);
			totalHour.put("secondHalfTotal", secondHalfHour);
			response.put("approvalOneHours", totalHour);
			response.put("approvalTwoHours", totalHour);
			response.put("approverOneFirstHalfStatus", approverOneFirstHalfStatus);
			response.put("approverOneSecondHalfStatus", approverOneSecodHalfStatus);
			response.put("approverTwoFirstHalfStatus", approverTwoFirstHalfStatus);
			response.put("approverTwoSecondHalfStatus", approverTwoSecodHalfStatus);
			response.put("firstHalfRemark", "");
			response.put("secondHalfRemark", "");
		}
		return response;
	}

	private void sendMail(String sendTo, String cc, String subject, StringBuilder mailBody) throws Exception {
		try {

			MailDomainDto mailDomainDto = new MailDomainDto();
			mailDomainDto.setSubject(subject);
			mailDomainDto.setCc(cc);
			mailDomainDto.setContent(mailBody.toString());

			Template t = freemarkerConfig.getTemplate("email_template.ftl");
			String html = (FreeMarkerTemplateUtils.processTemplateIntoString(t, mailDomainDto))
					.replace("MAIL_BODY", mailBody).replace("Title", "");

			mailDomainDto.setMailBody(html);
			mailDomainDto.setTo(sendTo);
			String token = UUID.randomUUID().toString();
			emailNotificationService.sendMail(token, mailDomainDto, true);
		} catch (Exception e) {
		}
	}

	@Override
	public ArrayList<JSONObject> getProjectWiseSubmissionDetailsTier1(int month, int year, long projectId, long userId,
			long regionId) {
		// TODO Auto-generated method stub
		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		ArrayList<JSONObject> data = new ArrayList<>();
		// ArrayList<JSONObject> datas = new ArrayList<>();
		Double firstHalfHour = 0.0;
		Double secondHalfHour = 0.0;
		Date startDate = null;
		Date endDate = null;
		TaskTrackApproval submissionDetails = new TaskTrackApproval();
		int firstHalfDay = 15;
		try {
			String start = year + "-" + month + "-01";
			startDate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			int total = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			String end = year + "-" + month + "-" + total;
			endDate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (startDate != null && endDate != null) {
				// System.out.println("datas------------>" + userIdList.size());
				JSONObject eachUserData = new JSONObject();
				List<Object[]> details = taskTrackApprovalFinalRepository.getProjectWiseSubmissionDetails(month, year,
						projectId, userId, regionId);
				if (details != null && details.size() > 0) {
					for (Object[] item : details) {
						JSONObject node = new JSONObject();
						List<JSONObject> billableArray = new ArrayList<>();
						List<JSONObject> userArray = new ArrayList<>();
						node.put("userId", item[0]);
						node.put("firstName", item[1]);
						node.put("lastName", item[2]);
						// node.put("status", item[3]);
						int halforfull = daysInMonth;
						if (item[9] != null && item[10] == null) {
							if (item[9].toString().equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)) {
								halforfull = 15;
							}
						}
						if (item[9] != null && item[10] != null) {
							if (item[9].toString().equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)
									&& !item[10].toString().equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)) {
								halforfull = 15;
							}
						}
						if (item[9] == null && item[10] != null) {

							halforfull = 1;

						}
						for (int i = 1; i <= daysInMonth; i++) {
							String j;
							if (i < 10) {
								j = "0" + i;
							} else {
								j = String.valueOf(i);
							}
							JSONObject billableNode = new JSONObject();
							billableNode.put(year + "-" + month + "-" + j, item[i + 10]);
							billableArray.add(billableNode);
						}
						Long user_id = Long.parseLong(item[0].toString());
						Long project_id = Long.parseLong(item[4].toString());
						// odc details
						JSONObject odcDetails = new JSONObject();
						UserModel userDetails = userService.getUserDetailsById(user_id);
						odcDetails.put("odcName", userDetails.getRegion().getRegion_name());
						// project details
						JSONObject projectDetails = new JSONObject();
						ProjectModel project = projectService.getProjectDetails(project_id);
						projectDetails.put("projectName", project.getProjectName());
						if (project.getClientName().getClientName() != null) {
							projectDetails.put("clientName", project.getClientName().getClientName());
						} else {
							projectDetails.put("clientName", "");
						}
						projectDetails.put("projectTier", project.getProjectTier());
						projectDetails.put("projectType", project.getprojectType());
						// submitted details level2
						SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy");
						// first half submitted date
						Object[] firstHalfDates = taskTrackApprovalFinalRepository.getSubmittedDateFromAudit(project_id,
								user_id, month, year);
						Date firstHalfDateOne = null;
						String firstHalfDateOnes = null;
						// System.out.println("---------------->" + firstHalfDates.length);
						if (firstHalfDates.length > 0) {
							// System.out.println("here--------------------------->");
							if (firstHalfDates != null) {
								firstHalfDateOne = (Date) firstHalfDates[0];
								firstHalfDateOnes = ft.format(firstHalfDateOne);
							}
						} else {
							firstHalfDateOnes = "-";
						}
						JSONObject submissionDetail = new JSONObject();
						String firstHalfDate = null;
						if (firstHalfDateOnes != null) {
							submissionDetail.put("firstHalfsubmittedDate", firstHalfDateOnes);
						} else {
							submissionDetail.put("firstHalfsubmittedDate", "-");
						}
						if (item[7] != null) {
							Long approver21 = Long.parseLong(item[7].toString());
							UserModel approverDetails = userService.getUserdetailsbyId(approver21);
							submissionDetail.put("firstHalfsubmittedPerson",
									(approverDetails.getLastName() + " " + approverDetails.getFirstName()));
						} else {
							submissionDetail.put("firstHalfsubmittedPerson", "-");
						}
						if (halforfull != 15) {
							if (item[8] != null && item[6] != null) {
								firstHalfDate = ft.format(item[6]);
								Long approver2 = Long.parseLong(item[8].toString());
								UserModel approverD = userService.getUserdetailsbyId(approver2);
								submissionDetail.put("secondHalfsubmittedPerson",
										(approverD.getLastName() + " " + approverD.getFirstName()));
								submissionDetail.put("secondHalfsubmittedDate", firstHalfDate);
							} else {
								submissionDetail.put("secondHalfsubmittedPerson", "-");
								submissionDetail.put("secondHalfsubmittedDate", "-");
							}
						} else if (halforfull == 1) {
							submissionDetail.put("firstHalfsubmittedDate", "");
							submissionDetail.put("firstHalfsubmittedPerson", "");
						} else {
							submissionDetail.put("secondHalfsubmittedPerson", "");
							submissionDetail.put("secondHalfsubmittedDate", "");
						}
						node.put("billable", billableArray);
						node.put("odcDetails", odcDetails);
						node.put("projectDetails", projectDetails);
						node.put("submissionDetails", submissionDetail);
						node.put("approveLevel", "level1");
						data.add(node);
					}
					// data.add(eachUserData);
				}
				/*
				 * for(JSONObject eachNode : data) {
				 *
				 * if(!eachNode.isEmpty()) { datas.add(eachNode); } }
				 */
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return data;
	}

	@Override
	public ArrayList<JSONObject> getProjectWiseSubmissionDetailsTier2(int month, int year, long projectId, long userId,
			long regionId) {
		// TODO Auto-generated method stub
		// approver level2 submitted details
		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		ArrayList<JSONObject> data = new ArrayList<>();
//		ArrayList<JSONObject> datas = new ArrayList<>();
		Double firstHalfHour = 0.0;
		Double secondHalfHour = 0.0;
		Date startDate = null;
		Date endDate = null;
		TaskTrackApproval submissionDetails = new TaskTrackApproval();
		int firstHalfDay = 15;
		try {
			String start = year + "-" + month + "-01";
			startDate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			int total = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			String end = year + "-" + month + "-" + total;
			endDate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (startDate != null && endDate != null) {
				// approver level1 submitted details
				List<Object[]> details = taskTrackApprovalFinalRepository.getProjectWiseSubmissionDetails(month, year,
						projectId, userId, regionId);
				if (details != null && details.size() > 0) {
					for (Object[] item : details) {
						JSONObject eachUserData = new JSONObject();
						JSONObject node = new JSONObject();
						List<JSONObject> billableArray = new ArrayList<>();
						List<JSONObject> userArray = new ArrayList<>();
						node.put("userId", item[0]);
						node.put("firstName", item[1]);
						node.put("lastName", item[2]);
						// node.put("status", item[3]);
						int halforfull = daysInMonth;
						if (item[9] != null && item[10] == null) {
							if (item[9].toString().equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)) {
								halforfull = 15;
							}
						}
						if (item[9] != null && item[10] != null) {
							if (item[9].toString().equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)
									&& !item[10].toString().equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)) {
								halforfull = 15;
							}
						}
						for (int i = 1; i <= daysInMonth; i++) {
							String j;
							if (i < 10) {
								j = "0" + i;
							} else {
								j = String.valueOf(i);
							}
							JSONObject billableNode = new JSONObject();
							billableNode.put(year + "-" + month + "-" + j, item[i + 10]);
							billableArray.add(billableNode);
						}
						Long user_id = Long.parseLong(item[0].toString());
						Long project_id = Long.parseLong(item[4].toString());
						// odc details
						JSONObject odcDetails = new JSONObject();
						UserModel userDetails = userService.getUserDetailsById(user_id);
						odcDetails.put("odcName", userDetails.getRegion().getRegion_name());
						// project details
						JSONObject projectDetails = new JSONObject();
						ProjectModel project = projectService.getProjectDetails(project_id);
						projectDetails.put("projectName", project.getProjectName());
						if (project.getClientName().getClientName() != null) {
							projectDetails.put("clientName", project.getClientName().getClientName());
						} else {
							projectDetails.put("clientName", "");
						}
						projectDetails.put("projectTier", project.getProjectTier());
						projectDetails.put("projectType", project.getprojectType());
						// submitted details level2
						SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy");
						// first half submitted date
						Object[] firstHalfDates = taskTrackApprovalFinalRepository.getSubmittedDateFromAudit(project_id,
								user_id, month, year);
						Date firstHalfDateOne = null;
						String firstHalfDateOnes = null;
						// System.out.println("---------------->" + firstHalfDates.length);
						if (firstHalfDates.length > 0) {
							if (firstHalfDates != null) {
								firstHalfDateOne = (Date) firstHalfDates[0];
								firstHalfDateOnes = ft.format(firstHalfDateOne);
							}
						} else {
							firstHalfDateOnes = "-";
						}
						JSONObject submissionDetail = new JSONObject();
						String firstHalfDate = null;
						if (firstHalfDateOnes != null) {
							submissionDetail.put("firstHalfsubmittedDate", firstHalfDateOnes);
						} else {
							submissionDetail.put("firstHalfsubmittedDate", "-");
						}
						if (item[7] != null) {
							Long approver21 = Long.parseLong(item[7].toString());
							UserModel approverDetails = userService.getUserdetailsbyId(approver21);
							submissionDetail.put("firstHalfsubmittedPerson",
									(approverDetails.getLastName() + " " + approverDetails.getFirstName()));
						} else {
							submissionDetail.put("firstHalfsubmittedPerson", "-");
						}
						if (halforfull != 15) {
							if (item[8] != null && item[6] != null) {
								firstHalfDate = ft.format(item[6]);
								Long approver2 = Long.parseLong(item[8].toString());
								UserModel approverD = userService.getUserdetailsbyId(approver2);
								submissionDetail.put("secondHalfsubmittedPerson",
										(approverD.getLastName() + " " + approverD.getFirstName()));
								submissionDetail.put("secondHalfsubmittedDate", firstHalfDate);
							} else {
								submissionDetail.put("secondHalfsubmittedPerson", "-");
								submissionDetail.put("secondHalfsubmittedDate", "-");
							}
						} else {
							submissionDetail.put("secondHalfsubmittedPerson", "");
							submissionDetail.put("secondHalfsubmittedDate", "");
						}
						node.put("billable", billableArray);
						node.put("odcDetails", odcDetails);
						node.put("projectDetails", projectDetails);
						node.put("submissionDetails", submissionDetail);
						node.put("approverLevel", "level2");
						data.add(node);
//							for(JSONObject eachNode : data) {
//								
//								if(!eachNode.isEmpty()) {
//									datas.add(eachNode);
//								}
//							}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return data;
	}

	@Override
	public List<Object[]> getProjectWiseSubmissionDetailsTierOne(int month, int year, long projectId, long userId,
			long regionId) {
		// TODO Auto-generated method stub
		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		ArrayList<Object[]> data = new ArrayList<>();
		// ArrayList<JSONObject> datas = new ArrayList<>();
		List<Object[]> details = new ArrayList<Object[]>();
		Double firstHalfHour = 0.0;
		Double secondHalfHour = 0.0;
		Date startDate = null;
		Date endDate = null;
		TaskTrackApproval submissionDetails = new TaskTrackApproval();
		int firstHalfDay = 15;
		try {
			String start = year + "-" + month + "-01";
			startDate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			int total = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			String end = year + "-" + month + "-" + total;
			endDate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (startDate != null && endDate != null) {
				// System.out.println("datas------------>" + userIdList.size());
				JSONObject eachUserData = new JSONObject();
				details = taskTrackApprovalFinalRepository.getProjectWiseSubmissionDetails(month, year, projectId,
						userId, regionId);
				/*
				 * for(JSONObject eachNode : data) {
				 *
				 * if(!eachNode.isEmpty()) { datas.add(eachNode); } }
				 */
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return details;
	}

	@Override
	public List<Object[]> getProjectWiseSubmissionDetailsTierTwo(int month, int year, long projectId, long userId,
			long regionId) {
		// TODO Auto-generated method stub
		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		ArrayList<JSONObject> data = new ArrayList<>();
//		ArrayList<JSONObject> datas = new ArrayList<>();
		List<Object[]> details = new ArrayList<Object[]>();
		Double firstHalfHour = 0.0;
		Double secondHalfHour = 0.0;
		Date startDate = null;
		Date endDate = null;
		TaskTrackApproval submissionDetails = new TaskTrackApproval();
		int firstHalfDay = 15;
		try {
			String start = year + "-" + month + "-01";
			startDate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			int total = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			String end = year + "-" + month + "-" + total;
			endDate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (startDate != null && endDate != null) {
				// approver level1 submitted details
				details = taskTrackApprovalFinalRepository.getProjectWiseSubmissionDetails(month, year, projectId,
						userId, regionId);
				if (details != null && details.size() > 0) {
					for (Object[] item : details) {
						JSONObject eachUserData = new JSONObject();
						JSONObject node = new JSONObject();
						List<JSONObject> billableArray = new ArrayList<>();
						List<JSONObject> userArray = new ArrayList<>();
						node.put("userId", item[0]);
						node.put("firstName", item[1]);
						node.put("lastName", item[2]);
						// node.put("status", item[3]);
						int halforfull = daysInMonth;
						if (item[9] != null && item[10] == null) {
							if (item[9].toString().equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)) {
								halforfull = 15;
							}
						}
						if (item[9] != null && item[10] != null) {
							if (item[9].toString().equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)
									&& !item[10].toString().equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)) {
								halforfull = 15;
							}
						}
						for (int i = 1; i <= daysInMonth; i++) {
							String j;
							if (i < 10) {
								j = "0" + i;
							} else {
								j = String.valueOf(i);
							}
							JSONObject billableNode = new JSONObject();
							billableNode.put(year + "-" + month + "-" + j, item[i + 10]);
							billableArray.add(billableNode);
						}
						Long user_id = Long.parseLong(item[0].toString());
						Long project_id = Long.parseLong(item[4].toString());
						// odc details
						JSONObject odcDetails = new JSONObject();
						UserModel userDetails = userService.getUserDetailsById(user_id);
						odcDetails.put("odcName", userDetails.getRegion().getRegion_name());
						// project details
						JSONObject projectDetails = new JSONObject();
						ProjectModel project = projectService.getProjectDetails(project_id);
						projectDetails.put("projectName", project.getProjectName());
						if (project.getClientName().getClientName() != null) {
							projectDetails.put("clientName", project.getClientName().getClientName());
						} else {
							projectDetails.put("clientName", "");
						}
						projectDetails.put("projectTier", project.getProjectTier());
						projectDetails.put("projectType", project.getprojectType());
						// submitted details level2
						SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
						// first half submitted date
						Object[] firstHalfDates = taskTrackApprovalFinalRepository.getSubmittedDateFromAudit(project_id,
								user_id, month, year);
						Date firstHalfDateOne = null;
						String firstHalfDateOnes = null;
						// System.out.println("---------------->" + firstHalfDates.length);
						if (firstHalfDates.length > 0) {
							if (firstHalfDates != null) {
								firstHalfDateOne = (Date) firstHalfDates[0];
								firstHalfDateOnes = ft.format(firstHalfDateOne);
							}
						} else {
							firstHalfDateOnes = "-";
						}
						JSONObject submissionDetail = new JSONObject();
						String firstHalfDate = null;
						if (firstHalfDateOnes != null) {
							submissionDetail.put("firstHalfsubmittedDate", firstHalfDateOnes);
						} else {
							submissionDetail.put("firstHalfsubmittedDate", "-");
						}
						if (item[7] != null) {
							Long approver21 = Long.parseLong(item[7].toString());
							UserModel approverDetails = userService.getUserdetailsbyId(approver21);
							submissionDetail.put("firstHalfsubmittedPerson",
									(approverDetails.getLastName() + " " + approverDetails.getFirstName()));
						} else {
							submissionDetail.put("firstHalfsubmittedPerson", "-");
						}
						if (halforfull != 15) {
							if (item[8] != null && item[6] != null) {
								firstHalfDate = ft.format(item[6]);
								Long approver2 = Long.parseLong(item[8].toString());
								UserModel approverD = userService.getUserdetailsbyId(approver2);
								submissionDetail.put("secondHalfsubmittedPerson",
										(approverD.getLastName() + " " + approverD.getFirstName()));
								submissionDetail.put("secondHalfsubmittedDate", firstHalfDate);
							} else {
								submissionDetail.put("secondHalfsubmittedPerson", "-");
								submissionDetail.put("secondHalfsubmittedDate", "-");
							}
						} else {
							submissionDetail.put("secondHalfsubmittedPerson", "-");
							submissionDetail.put("secondHalfsubmittedDate", "-");
						}
						node.put("billable", billableArray);
						node.put("odcDetails", odcDetails);
						node.put("projectDetails", projectDetails);
						node.put("submissionDetails", submissionDetail);
						node.put("approverLevel", "level2");
						data.add(node);
//							for(JSONObject eachNode : data) {
//								
//								if(!eachNode.isEmpty()) {
//									datas.add(eachNode);
//								}
//							}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return details;
	}

	@Override
	public ArrayList<JSONObject> getUserWiseSubmissionDetails(int month, int year, long projectId, long userId,
			long regionId) {
		// TODO Auto-generated method stub
		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		ArrayList<JSONObject> data = new ArrayList<>();
		List<Object[]> details = new ArrayList<Object[]>();
		Double firstHalfHour = 0.0;
		Double secondHalfHour = 0.0;
		Date startDate = null;
		Date endDate = null;
		TaskTrackApproval submissionDetails = new TaskTrackApproval();
		int firstHalfDay = 15;
		try {
			String start = year + "-" + month + "-01";
			startDate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			int total = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			String end = year + "-" + month + "-" + total;
			endDate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (startDate != null && endDate != null) {
				// approver level1 submitted details
				details = taskTrackApprovalFinalRepository.getProjectWiseSubmissionDetails(month, year, projectId,
						userId, regionId);
				if (details != null && details.size() > 0) {
					for (Object[] item : details) {
						JSONObject eachUserData = new JSONObject();
						JSONObject node = new JSONObject();
						List<JSONObject> billableArray = new ArrayList<>();
						List<JSONObject> userArray = new ArrayList<>();
						node.put("projectId", item[4]);
						node.put("ProjectName", item[3]);
						// node.put("lastName", item[2]);
						// node.put("status", item[3]);
						int halforfull = daysInMonth;
						if (item[9] != null && item[10] == null) {
							if (item[9].toString().equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)) {
								halforfull = 15;
							}
						}
						if (item[9] != null && item[10] != null) {
							if (item[9].toString().equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)
									&& !item[10].toString().equalsIgnoreCase(Constants.TASKTRACK_FINAL_STATUS_SUBMIT)) {
								halforfull = 15;
							}
						}
						for (int i = 1; i <= daysInMonth; i++) {
							String j;
							if (i < 10) {
								j = "0" + i;
							} else {
								j = String.valueOf(i);
							}
							JSONObject billableNode = new JSONObject();
							billableNode.put(year + "-" + month + "-" + j, item[i + 10]);
							billableArray.add(billableNode);
						}
						Long user_id = Long.parseLong(item[0].toString());
						Long project_id = Long.parseLong(item[4].toString());
						// odc details
						JSONObject odcDetails = new JSONObject();
						UserModel userDetails = userService.getUserDetailsById(user_id);
						odcDetails.put("odcName", userDetails.getRegion().getRegion_name());
						// project details
						JSONObject projectDetails = new JSONObject();
						ProjectModel project = projectService.getProjectDetails(project_id);
						projectDetails.put("projectName", project.getProjectName());
						if (project.getClientName() != null) {
							if (project.getClientName().getClientName() != null) {
								projectDetails.put("clientName", project.getClientName().getClientName());
							}
						} else {
							projectDetails.put("clientName", "");
						}
						projectDetails.put("projectTier", project.getProjectTier());
						projectDetails.put("projectType", project.getprojectType());
						// submitted details level2
						SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy");
						// first half submitted date
						Object[] firstHalfDates = taskTrackApprovalFinalRepository.getSubmittedDateFromAudit(project_id,
								userId, month, year);
						Date firstHalfDateOne = null;
						String firstHalfDateOnes = null;
						// System.out.println("---------------->" + firstHalfDates.length);
						if (firstHalfDates.length > 0) {
							if (firstHalfDates != null) {
								firstHalfDateOne = (Date) firstHalfDates[0];
								firstHalfDateOnes = ft.format(firstHalfDateOne);
							}
						} else {
							firstHalfDateOnes = "-";
						}
						JSONObject submissionDetail = new JSONObject();
						String firstHalfDate = null;
						if (firstHalfDateOnes != null) {
							submissionDetail.put("firstHalfsubmittedDate", firstHalfDateOnes);
						} else {
							submissionDetail.put("firstHalfsubmittedDate", "-");
						}
						if (item[7] != null) {
							Long approver21 = Long.parseLong(item[7].toString());
							UserModel approverDetails = userService.getUserdetailsbyId(approver21);
							submissionDetail.put("firstHalfsubmittedPerson",
									(approverDetails.getLastName() + " " + approverDetails.getFirstName()));
						} else {
							submissionDetail.put("firstHalfsubmittedPerson", "-");
						}
						if (halforfull != 15) {
							if (item[8] != null && item[6] != null) {
								firstHalfDate = ft.format(item[6]);
								Long approver2 = Long.parseLong(item[8].toString());
								UserModel approverD = userService.getUserdetailsbyId(approver2);
								submissionDetail.put("secondHalfsubmittedPerson",
										(approverD.getLastName() + " " + approverD.getFirstName()));
								submissionDetail.put("secondHalfsubmittedDate", firstHalfDate);
							} else {
								submissionDetail.put("secondHalfsubmittedPerson", "-");
								submissionDetail.put("secondHalfsubmittedDate", "-");
							}
						} else {
							submissionDetail.put("secondHalfsubmittedPerson", "");
							submissionDetail.put("secondHalfsubmittedDate", "");
						}
						node.put("billable", billableArray);
						node.put("odcDetails", odcDetails);
						node.put("projectDetails", projectDetails);
						node.put("submissionDetails", submissionDetail);
						node.put("approverLevel", "level2");
						data.add(node);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return data;
	}

	@Override
	public List<Object[]> getUserWiseSubmissionDetailsExport(int month, int year, long projectId, long userId,
			long regionId) {
		// TODO Auto-generated method stub
		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		ArrayList<JSONObject> data = new ArrayList<>();
		List<Object[]> details = new ArrayList<Object[]>();
		Double firstHalfHour = 0.0;
		Double secondHalfHour = 0.0;
		Date startDate = null;
		Date endDate = null;
		TaskTrackApproval submissionDetails = new TaskTrackApproval();
		int firstHalfDay = 15;
		try {
			String start = year + "-" + month + "-01";
			startDate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			int total = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			String end = year + "-" + month + "-" + total;
			endDate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (startDate != null && endDate != null) {
				// approver level1 submitted details
				details = taskTrackApprovalFinalRepository.getProjectWiseSubmissionDetails(month, year, projectId,
						userId, regionId);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return details;
	}

	// Nisha
	@Override
	public ObjectNode getTaskTrackDataForApprover1(ObjectNode requestdata) throws Exception {
		ObjectNode node = objectMapper.createObjectNode();
		ArrayNode weeksDataArray = objectMapper.createArrayNode();

		ArrayNode userDataArray = objectMapper.createArrayNode();

		JSONArray weeksArray = new JSONArray();
		int year = requestdata.get("year").asInt();
		Long projectId = requestdata.get("projectId").asLong();
		int month = requestdata.get("month").asInt();
		// get weeks of the month
		weeksArray = findWeeks(month, year);
		// get startDate and endDate of the month
		Date startDate;
		Date endDate;
		String start = year + "-" + month + "-01";
		startDate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		int total = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		String end = year + "-" + month + "-" + total;
		endDate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
		// get usersList in the project for the corresponding month
		int projectWorkFlow = 0;
		List<AllocationModel> allocatedUserData = allocationRepository.findByProjectProjectIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIsBillable(projectId,
				endDate,startDate,true);
		//List<AllocationModel> allocatedUserData = projectAllocationRepository.getUserDataByProjectAndDate(projectId,
			//	startDate, endDate);
		if (allocatedUserData.size() >= 1) {
			for (AllocationModel userData : allocatedUserData) {
				ObjectNode userDataResponse = objectMapper.createObjectNode();
				ArrayNode weeklyHourData = objectMapper.createArrayNode();
				// ArrayNode semiMonthlyHourData = objectMapper.createArrayNode();
				Long userId = userData.getuser().getUserId();
				String projectWorkFlowType = null;
				projectWorkFlow = userData.getproject().getWorkflowType();
				userDataResponse.put("userId", userData.getuser().getUserId());
				userDataResponse.put("userName",
						userData.getuser().getLastName() + " " + userData.getuser().getFirstName());
				// WeeklyData
				if (projectWorkFlow == 3 || projectWorkFlow == 4) {
					for (int i = 0; i < weeksArray.size(); i++) {
						ObjectNode weeklyDataResponse = objectMapper.createObjectNode();
						JSONObject objects = (JSONObject) weeksArray.get(i);
						SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
						Date weekStart = (Date) objects.get("startDate");
						Date weekEnd = (Date) objects.get("endDate");
						TaskTrackWeeklyApproval weeklyUserData = taskTrackWeeklyApprovalRepository
								.findByProjectProjectIdAndStartDateAndEndDateAndUserUserId(projectId, weekStart,
										weekEnd, userId);
						double totalhours = 0.0;
						String approverSatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
						if (weeklyUserData != null) {
							totalhours = weeklyUserData.getDay1() + weeklyUserData.getDay2() + weeklyUserData.getDay3()
									+ weeklyUserData.getDay4() + weeklyUserData.getDay5() + weeklyUserData.getDay6()
									+ weeklyUserData.getDay7();
							approverSatus = weeklyUserData.getApprover1Status() == null
									? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: weeklyUserData.getApprover1Status();
						}
						TaskTrackRejection rejection = null;
						if(approverSatus.equals(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)) {
							rejection = taskTrackRejectionRepository.
									findWeeklyRejection(userId, projectId, year, startDate, endDate, Constants.TASKTRACK_REJECTION_STATUS_OPEN);
						}
						weeklyDataResponse.put("totalHour", totalhours);
						weeklyDataResponse.put("weekStart", sdff.format(weekStart));
						weeklyDataResponse.put("weekEnd", sdff.format(weekEnd));
						weeklyDataResponse.put("approverSatus", approverSatus);
						weeklyDataResponse.put("rejectionRemark", rejection == null ? "" : rejection.getRemark());
						weeklyHourData.add(weeklyDataResponse);
					}
					userDataResponse.set("weekData", weeklyHourData);
				} else { // SemiMonthlyData
					ObjectNode semiMonthlyDataResponse = objectMapper.createObjectNode();
					TasktrackApprovalSemiMonthly semiMonthlyUserData = taskTrackApprovalSemiMonthlyRepository
							.findByUserUserIdAndProjectProjectIdAndMonthAndYear(userId, projectId, month, year);
					double firstHalfHour = 0.0;
					double secondHalfHour = 0.0;
					String firstHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
					String secondHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
					
					if (semiMonthlyUserData != null) {
						firstHalfStatus = semiMonthlyUserData.getApproverOneFirstHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: semiMonthlyUserData.getApproverOneFirstHalfStatus();
						secondHalfStatus = semiMonthlyUserData.getApproverOneSecondHalfStatus() == null
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: semiMonthlyUserData.getApproverOneSecondHalfStatus();
						if (semiMonthlyUserData.getUserFirstHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_USER_STATUS_SUBMIT)) {
							firstHalfHour = semiMonthlyUserData.getDay1() + semiMonthlyUserData.getDay2()
									+ semiMonthlyUserData.getDay3() + semiMonthlyUserData.getDay4()
									+ semiMonthlyUserData.getDay5() + semiMonthlyUserData.getDay6()
									+ semiMonthlyUserData.getDay7() + semiMonthlyUserData.getDay8()
									+ semiMonthlyUserData.getDay9() + semiMonthlyUserData.getDay10()
									+ semiMonthlyUserData.getDay11() + semiMonthlyUserData.getDay12()
									+ semiMonthlyUserData.getDay13() + semiMonthlyUserData.getDay14()
									+ semiMonthlyUserData.getDay15();

						}
						if (semiMonthlyUserData.getUserSecondHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_USER_STATUS_SUBMIT)) {
							secondHalfHour = semiMonthlyUserData.getDay16() + semiMonthlyUserData.getDay17()
									+ semiMonthlyUserData.getDay18() + semiMonthlyUserData.getDay19()
									+ semiMonthlyUserData.getDay20() + semiMonthlyUserData.getDay21()
									+ semiMonthlyUserData.getDay22() + semiMonthlyUserData.getDay23()
									+ semiMonthlyUserData.getDay24() + semiMonthlyUserData.getDay25()
									+ semiMonthlyUserData.getDay26() + semiMonthlyUserData.getDay27()
									+ semiMonthlyUserData.getDay28() + semiMonthlyUserData.getDay29()
									+ semiMonthlyUserData.getDay30() + semiMonthlyUserData.getDay31();
						}

					}
					TaskTrackRejection firstHalfRejection = null;
					TaskTrackRejection secondHalfRejection = null;
					if(firstHalfStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)) {
						firstHalfRejection = taskTrackRejectionRepository.findSemiMonthlyRejection(userId, projectId,month,year,
										Constants.TASKTRACK_REJECTION_FIRST_HALF_CYCLE,Constants.TASKTRACK_REJECTION_STATUS_OPEN);
					}
					else if(secondHalfStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)) {
						secondHalfRejection = taskTrackRejectionRepository.findSemiMonthlyRejection(userId, projectId,month,year,
										Constants.TASKTRACK_REJECTION_SECOND_HALF_CYCLE,Constants.TASKTRACK_REJECTION_STATUS_OPEN);
					}
					semiMonthlyDataResponse.put("firstHalfHour", firstHalfHour);
					semiMonthlyDataResponse.put("secondHalfHour", secondHalfHour);
					semiMonthlyDataResponse.put("firstHalfStatus", firstHalfStatus);
					semiMonthlyDataResponse.put("secondHalfStatus", secondHalfStatus);
					semiMonthlyDataResponse.put("firstHalfRejectionRemark", firstHalfRejection == null ? "" : firstHalfRejection.getRemark());
					semiMonthlyDataResponse.put("secondHalfRejectionRemark", secondHalfRejection == null ? "" : secondHalfRejection.getRemark());
					// semiMonthlyHourData.add(semiMonthlyDataResponse);
					userDataResponse.set("semiMonthlyData", semiMonthlyDataResponse);

				}
				userDataArray.add(userDataResponse);
			}
			if (projectWorkFlow == 3 || projectWorkFlow == 4) {
				for (int j = 0; j < weeksArray.size(); j++) {
					JSONObject objects = (JSONObject) weeksArray.get(j);
					Date weekStart = (Date) objects.get("startDate");
					Date weekEnd = (Date) objects.get("endDate");
					SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
					String weekRange = sdf.format(weekStart) + " - " + sdf.format(weekEnd);
					weeksDataArray.add(weekRange);
				}
				node.set("weeks", weeksDataArray);
				node.put("projectWorkFlowType", "weekly");

			} else {
				node.put("projectWorkFlowType", "semiMonthly");
			}
			node.set("userData", userDataArray);
		}
		return node;

	}

	// Nisha
	public JSONArray findWeeks(int curMonth, int curYear) {
		int month = curMonth;
		int year = curYear;
		int dayOfWeek = DateTimeConstants.SUNDAY;
		String weekRange;
		JSONArray weeksArray = new JSONArray();
		org.joda.time.LocalDate firstOfMonth = new org.joda.time.LocalDate(year, month, 1);
		org.joda.time.LocalDate firstOfNextMonth = firstOfMonth.plusMonths(1);
		org.joda.time.LocalDate firstDateInGrid = firstOfMonth.withDayOfWeek(dayOfWeek);
		if (firstDateInGrid.isAfter(firstOfMonth)) { // If getting the next start of week instead of desired week's
														// start, adjust backwards.
			firstDateInGrid = firstDateInGrid.minusWeeks(1);
		}

		org.joda.time.LocalDate weekStart = firstDateInGrid;
		org.joda.time.LocalDate weekStop = null;
		int weekNumber = 0;

		do {
			JSONObject weekDataResponse = new JSONObject();

			weekNumber = weekNumber + 1;
			weekStop = weekStart.plusDays(6);
			// System.out.println( weekNumber + " week: " + weekStart + " --- " + weekStop
			// ); // 1 week: 03-30-2014 --- 04-05-2014
			weekRange = weekStart + " - " + weekStop;

			weekDataResponse.put("startDate", weekStart.toDateTimeAtStartOfDay().toDate());
			weekDataResponse.put("endDate", weekStop.toDateTimeAtStartOfDay().toDate());
			weeksArray.add(weekDataResponse);
			weekStart = weekStop.plusDays(1);
		} while (weekStop.isBefore(firstOfNextMonth));
		return weeksArray;
	}

	@Override
	public ObjectNode getTaskTrackDataByUserId(ObjectNode requestdata) throws Exception {
		ObjectNode node = objectMapper.createObjectNode();
		Long projectId = requestdata.get("projectId").asLong();
		Long userId = requestdata.get("userId").asLong();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dff = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
		Date startDate = df.parse(requestdata.get("startDate").asText());
		Date endDate = df.parse(requestdata.get("endDate").asText());
		ProjectModel projectData = projectRepository.getProjectDetails(projectId);
		Boolean submitButtonStatus = Boolean.FALSE;
		Boolean rejectButtonStatus = Boolean.FALSE;
		if (projectData != null) {
			node.put("clientName",projectData.getClientName().getClientName());
			ArrayNode hourDataNode = objectMapper.createArrayNode();
			// weekly
			if (projectData.getWorkflowType() == 3 || projectData.getWorkflowType() == 4) {
				TaskTrackWeeklyApproval userData = taskTrackWeeklyApprovalRepository
						.findByProjectProjectIdAndStartDateAndEndDateAndUserUserId(projectId, startDate, endDate,
								userId);
				if (userData != null) {
					node.put("userName", userData.getUser().getLastName() + " " + userData.getUser().getFirstName());
					node.put("userId", userData.getUser().getUserId());
					node.put("approver1Status",
							userData.getApprover1Status() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: userData.getApprover1Status());
					node.put("approver1SubmittedDate",userData.getApprover1SubmittedDate()==null?"": dff.format(userData.getApprover1SubmittedDate()));
					node.put("userStatus", userData.getTimetrackStatus());
					node.put("userSubmittedDate", userData.getUserSubmittedDate()==null?"":dff.format(userData.getUserSubmittedDate()));
					if (projectData.getProjectTier() == 2) {

						node.put("approver2Status",
								userData.getApprover2Status() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
										: userData.getApprover2Status());
						node.put("approver2SubmittedDate", userData.getApprover2SubmittedDate()==null?"":dff.format(userData.getApprover2SubmittedDate()));
					} else {
						node.put("approver2Status", "");
						node.put("approver2SubmittedDate", "");
					}
					node.put("loggedId", userData.getId());

					String timeTrackStatus = userData.getTimetrackStatus() == null 
							? Constants.TASKTRACK_USER_STATUS_SAVED : userData.getTimetrackStatus();
					String approverOneStatus = userData.getApprover1Status() == null 
							? Constants.TASKTRACK_APPROVER_STATUS_OPEN : userData.getApprover1Status();
					
					if(timeTrackStatus.equals(Constants.TASKTRACK_USER_STATUS_SUBMIT) 
							&& approverOneStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_OPEN)) {
						submitButtonStatus = true;
						rejectButtonStatus = true;
					}
					else if(timeTrackStatus.equals(Constants.TASKTRACK_USER_STATUS_SUBMIT)
							&& approverOneStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)) {
						submitButtonStatus = true;
						rejectButtonStatus = true;
					}
					if(userData.getFinanceStatus()!=null) {
						if (userData.getFinanceStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)) {
							submitButtonStatus = false;
							rejectButtonStatus = false;
						}
					}

					node.put("submitButtonStatus", submitButtonStatus);
					node.put("rejectButtonStatus", rejectButtonStatus);
					
					ObjectNode hourDataResponse = objectMapper.createObjectNode();
					Calendar cal = Calendar.getInstance();
					cal.setTime(startDate);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay1());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay2());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay3());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay4());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay5());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay6());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay7());
					hourDataNode.add(hourDataResponse);
					node.set("hourData", hourDataNode);
				} else {
					UserModel user = userRepository.findOneByUserId(userId);
					if (user != null) {
						node.put("userName", user.getLastName() + " " + user.getFirstName());
						node.put("userId", user.getUserId());
						node.put("approver1Status", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						node.put("loggedId", "");
						node.put("userStatus", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						node.put("approver1SubmittedDate", "");
						node.put("userSubmittedDate", "");
						if (projectData.getProjectTier() == 2) {

							node.put("approver2Status", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
							node.put("approver2SubmittedDate", "");
						} else {
							node.put("approver2Status", "");
							node.put("approver2SubmittedDate", "");
						}
						ObjectNode hourDataResponse = objectMapper.createObjectNode();
						Calendar cal = Calendar.getInstance();
						cal.setTime(startDate);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						hourDataNode.add(hourDataResponse);
						node.set("hourData", hourDataNode);
						node.put("submitButtonStatus", false);
						node.put("rejectButtonStatus", false);
					}
				}

			} else {
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);

				int month = cal.get(Calendar.MONTH)+1;
				//System.out.println("month----++++++"+month);
				int year = cal.get(Calendar.YEAR);
				Calendar cale = Calendar.getInstance();
				cale.setTime(endDate);
				int day = cale.get(Calendar.DAY_OF_MONTH);
				TasktrackApprovalSemiMonthly userData = taskTrackApprovalSemiMonthlyRepository
						.findByUserUserIdAndProjectProjectIdAndMonthAndYear(userId, projectId, month, year);
				if (userData != null) {
					node.put("userName", userData.getUser().getLastName() + " " + userData.getUser().getFirstName());
					node.put("userId", userData.getUser().getUserId());
					node.put("loggedId", userData.getId());

					ObjectNode hourDataResponse = objectMapper.createObjectNode();
					if (day == 15) {

						hourDataResponse.put(df.format(cal.getTime()), userData.getDay1());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay2());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay3());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay4());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay5());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay6());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay7());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay8());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay9());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay10());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay11());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay12());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay13());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay14());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay15());
						node.put("approver1Status",
								userData.getApproverOneFirstHalfStatus() == null
										? Constants.TASKTRACK_APPROVER_STATUS_OPEN
										: userData.getApproverOneFirstHalfStatus());
						node.put("userStatus", userData.getUserFirstHalfStatus());
						node.put("approver1SubmittedDate",userData.getApproverOneFirstHalfSubmittedDate()==null?"":
								dff.format(userData.getApproverOneFirstHalfSubmittedDate()));
						node.put("userSubmittedDate", userData.getUserFirstHalfSubmittedDate()==null?"":
								dff.format(userData.getUserFirstHalfSubmittedDate()));

						if (projectData.getProjectTier() == 2) {

							node.put("approver2Status",
									userData.getApproverTwoFirstHalfStatus() == null
											? Constants.TASKTRACK_APPROVER_STATUS_OPEN
											: userData.getApproverTwoFirstHalfStatus());
							node.put("approver2SubmittedDate",userData.getApproverTwoFirstHalfSubmittedDate()==null?"":
									dff.format(userData.getApproverTwoFirstHalfSubmittedDate()));
						} else {
							node.put("approver2Status", "");
							node.put("approver2SubmittedDate", "");
						}

						String userFirstHalfStatus = userData.getUserFirstHalfStatus() == null 
								? Constants.TASKTRACK_USER_STATUS_SAVED : userData.getUserFirstHalfStatus();
						String approverOneFirstHalfStatus = userData.getApproverOneFirstHalfStatus() == null 
								? Constants.TASKTRACK_APPROVER_STATUS_OPEN : userData.getApproverOneFirstHalfStatus();
						
						if(userFirstHalfStatus.equals(Constants.TASKTRACK_USER_STATUS_SUBMIT) 
								&& approverOneFirstHalfStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_OPEN)) {
							submitButtonStatus = true;
							rejectButtonStatus = true;
						}
						else if(userFirstHalfStatus.equals(Constants.TASKTRACK_USER_STATUS_SUBMIT)
								&& approverOneFirstHalfStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)) {
							submitButtonStatus = true;
							rejectButtonStatus = true;
						}
						if(userData.getFinanceFirstHalfStatus()!=null) {
							if (userData.getFinanceFirstHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)) {
								submitButtonStatus = false;
								rejectButtonStatus = false;
							}
						}
						node.put("submitButtonStatus", submitButtonStatus);
						node.put("rejectButtonStatus", rejectButtonStatus);

					} else {

						hourDataResponse.put(df.format(cal.getTime()), userData.getDay16());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay17());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay18());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay19());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay20());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay21());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay22());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay23());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay24());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay25());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay26());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay27());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay28());
						if (day > 28) {
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), userData.getDay29());
						}
						if (day > 29) {
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), userData.getDay30());
						}
						if (day > 30) {
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), userData.getDay31());
						}
						node.put("approver1Status",
								userData.getApproverOneSecondHalfStatus() == null
										? Constants.TASKTRACK_APPROVER_STATUS_OPEN
										: userData.getApproverOneFirstHalfStatus());
						node.put("userStatus", userData.getUserSecondHalfStatus());
						node.put("approver1SubmittedDate",
								userData.getApproverOneSecondHalfSubmittedDate()==null?"":dff.format(userData.getApproverOneSecondHalfSubmittedDate()));
						node.put("userSubmittedDate", userData.getUserSecondHalfSubmittedDate()==null?"":dff.format(userData.getUserSecondHalfSubmittedDate()));
						if (projectData.getProjectTier() == 2) {

							node.put("approver2Status",
									userData.getApproverTwoSecondHalfStatus() == null
											? Constants.TASKTRACK_APPROVER_STATUS_OPEN
											: userData.getApproverTwoSecondHalfStatus());
							node.put("approver2SubmittedDate",userData.getApproverTwoSecondHalfSubmittedDate()==null?"":
									dff.format(userData.getApproverTwoSecondHalfSubmittedDate()));
						} else {
							node.put("approver2Status", "");
							node.put("approver2SubmittedDate", "");
						}
					}
					String userSecondHalfStatus = userData.getUserSecondHalfStatus() == null 
							? Constants.TASKTRACK_USER_STATUS_SAVED : userData.getUserSecondHalfStatus();
					String approverOneSecondHalfStatus = userData.getApproverOneSecondHalfStatus() == null 
							? Constants.TASKTRACK_APPROVER_STATUS_OPEN : userData.getApproverOneSecondHalfStatus();
					
					if(userSecondHalfStatus.equals(Constants.TASKTRACK_USER_STATUS_SUBMIT) 
							&& approverOneSecondHalfStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_OPEN)) {
						submitButtonStatus = true;
						rejectButtonStatus = true;
					}
					else if(userSecondHalfStatus.equals(Constants.TASKTRACK_USER_STATUS_SUBMIT)
							&& approverOneSecondHalfStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_REJECTION)) {
						submitButtonStatus = true;
						rejectButtonStatus = true;
					}
					if(userData.getFinanceSecondHalfStatus()!=null) {
						if (userData.getFinanceSecondHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)) {
							submitButtonStatus = false;
							rejectButtonStatus = false;
						}
					}
					node.put("submitButtonStatus", submitButtonStatus);
					node.put("rejectButtonStatus", rejectButtonStatus);
					hourDataNode.add(hourDataResponse);
					node.set("hourData", hourDataNode);
				} else {
					UserModel user = userRepository.findOneByUserId(userId);
					if (user != null) {

						node.put("userName", user.getLastName() + " " + user.getFirstName());
						node.put("userId", user.getUserId());
						node.put("approver1Status", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						node.put("loggedId", "");
						node.put("userStatus", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						node.put("approver1SubmittedDate", "");
						node.put("userSubmittedDate", "");
						if (projectData.getProjectTier() == 2) {

							node.put("approver2Status", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
							node.put("approver2SubmittedDate", "");
						} else {
							node.put("approver2Status", "");
							node.put("approver2SubmittedDate", "");
						}
						ObjectNode hourDataResponse = objectMapper.createObjectNode();
						if (day == 15) {
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);

						} else {
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							if (day > 28) {
								cal.add(Calendar.DATE, 1);
								hourDataResponse.put(df.format(cal.getTime()), 0);
							}
							if (day > 29) {
								cal.add(Calendar.DATE, 1);
								hourDataResponse.put(df.format(cal.getTime()), 0);
							}
							if (day > 30) {
								cal.add(Calendar.DATE, 1);
								hourDataResponse.put(df.format(cal.getTime()), 0);
							}
						}
						hourDataNode.add(hourDataResponse);
						node.set("hourData", hourDataNode);
						node.put("submitButtonStatus", false);
						node.put("rejectButtonStatus", false);
					}
				}
			}
		}
		return node;
	}

	public ObjectNode approveHoursLevel1(ObjectNode requestdata) throws Exception {
		ObjectNode node = objectMapper.createObjectNode();
		Long projectId = requestdata.get("projectId").asLong();
		Long userId = requestdata.get("userId").asLong();
		Long loggedId = requestdata.get("loggedId").asLong();
		Long approverId = requestdata.get("approverId").asLong();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date endDate = df.parse(requestdata.get("endDate").asText());
		Date startDate = df.parse(requestdata.get("startDate").asText());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		Date curDate = formatter.parse(formatter.format(date));
		ProjectModel projectData = projectRepository.getProjectDetails(projectId);
		if (projectData != null) {
			// weekly approval
			if (projectData.getWorkflowType() == 3 || projectData.getWorkflowType() == 4) {
				if (loggedId != null) {
					TaskTrackWeeklyApproval userData = taskTrackWeeklyApprovalRepository
							.findByProjectProjectIdAndStartDateAndEndDateAndUserUserId(projectId, startDate, endDate,
									userId);
					if (userData != null) {
						userData.setApprover1Status(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
						UserModel approver = userRepository.getOne(approverId);
						userData.setApprover1Id(approver);
						userData.setApprover1SubmittedDate(curDate);
						taskTrackWeeklyApprovalRepository.save(userData);
					}
				}

			} else { // semimonthly approval
				if (loggedId != null) {
					Calendar cale = Calendar.getInstance();
					cale.setTime(endDate);
					int day = cale.get(Calendar.DAY_OF_MONTH);
					int month = cale.get(Calendar.MONTH)+1;
					int year = cale.get(Calendar.YEAR);
					TasktrackApprovalSemiMonthly userData = taskTrackApprovalSemiMonthlyRepository
							.findByUserUserIdAndProjectProjectIdAndMonthAndYear(userId, projectId, month, year);
					if (userData != null) {
						UserModel approver = userRepository.getOne(approverId);
						if (day > 15) {
							userData.setApproverOneSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
							userData.setSecondHalfApproverOneId(approver);
							userData.setApproverOneSecondHalfSubmittedDate(curDate);
							taskTrackApprovalSemiMonthlyRepository.save(userData);
						} else {
							userData.setApproverOneFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
							userData.setFirstHalfApproverOneId(approver);
							userData.setApproverOneFirstHalfSubmittedDate(curDate);
							taskTrackApprovalSemiMonthlyRepository.save(userData);
						}
					}
				}
			}
		}
		return node;
	}

	@Override
	public StatusResponse getTaskTrackDataForApprover2(GetTaskTrackData requestdata) throws Exception {
		// TODO Auto-generated method stub
		Data data = null;
		int projectWorkFlow  = 0;
		UserData userResponse = null;
		ArrayList<UserData> userDataArray = new ArrayList<>();
		//HashMap<String, Date> weeks = new HashMap<>();
		ArrayList<String> week = new ArrayList<>();
		JSONArray weeksArray = new JSONArray();
		int year = requestdata.getYear();
		Long projectId = requestdata.getProjectId();
		int month = requestdata.getMonth();
		weeksArray = findWeeks(month, year);
		String projectWorkFlowType = null;
		// get startDate and endDate of the months
		Date startDate;
		Date endDate;
		String start = year + "-" + month + "-01";
		startDate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		int total = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		String end = year + "-" + month + "-" + total;
		endDate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
		// get usersList in the project for the corresponding month
		
		List<AllocationModel> allocatedUserData = projectAllocationRepository.getUserDataByProjectAndDate(projectId,
				startDate, endDate);
		if (allocatedUserData.size() >= 1) {
			for (AllocationModel userData : allocatedUserData) {
				// ObjectNode userDataResponse = objectMapper.createObjectNode();
				ArrayNode weeklyHourData = objectMapper.createArrayNode();
				ArrayNode semiMonthlyHourData = objectMapper.createArrayNode();
				Long userId = userData.getuser().getUserId();
				String userName = userData.getuser().getLastName() + " " + userData.getuser().getFirstName();
				 projectWorkFlow = userData.getproject().getWorkflowType();
				// WeeklyData
				if (projectWorkFlow == 3 || projectWorkFlow == 4) {
					ArrayList<WeekData> weeksDataArray = new ArrayList<>();
					for (int i = 0; i < weeksArray.size(); i++) {
						
						JSONObject objects = (JSONObject) weeksArray.get(i);
						SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
						Date weekStart = (Date) objects.get("startDate");
						Date weekEnd = (Date) objects.get("endDate");
						TaskTrackWeeklyApproval weeklyUserData = taskTrackWeeklyApprovalRepository
								.findByProjectProjectIdAndStartDateAndEndDateAndUserUserId(projectId, weekStart,
										weekEnd, userId);
						double totalhours = 0.0;
						String approverSatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
						if (weeklyUserData != null) {
							if(weeklyUserData.getTimetrackStatus().
									equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT) && weeklyUserData.getApprover1Status().
									equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT))
							totalhours = weeklyUserData.getDay1() + weeklyUserData.getDay2() + weeklyUserData.getDay3()
									+ weeklyUserData.getDay4() + weeklyUserData.getDay5() + weeklyUserData.getDay6()
									+ weeklyUserData.getDay7();
							approverSatus = weeklyUserData.getApprover1Status() == null
									? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: weeklyUserData.getApprover1Status();
						}
						WeekData weekData = new WeekData(totalhours, sdff.format(weekStart), sdff.format(weekEnd), approverSatus);
						weeksDataArray.add(weekData);
						
					}
					userResponse = new UserData(userId, userName, weeksDataArray);
					userDataArray.add(userResponse);
					data = new Data(projectWorkFlowType,userDataArray,week);
					
				} else { // SemiMonthlyData
					ArrayList<WeekData> weeksDataArray = new ArrayList<>();
					ObjectNode semiMonthlyDataResponse = objectMapper.createObjectNode();
					TasktrackApprovalSemiMonthly semiMonthlyUserData = taskTrackApprovalSemiMonthlyRepository
							.findByUserUserIdAndProjectProjectIdAndMonthAndYear(userId, projectId, month, year);
					double firstHalfHour = 0.0;
					double secondHalfHour = 0.0;
					String firstHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
					String secondHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
					if (semiMonthlyUserData != null) {
						if (semiMonthlyUserData.getUserFirstHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_USER_STATUS_SUBMIT)) {
							if(semiMonthlyUserData.getApproverOneFirstHalfStatus()!= null)
								if(semiMonthlyUserData.getApproverOneFirstHalfStatus()
										.equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)) {
							firstHalfHour = semiMonthlyUserData.getDay1() + semiMonthlyUserData.getDay2()
									+ semiMonthlyUserData.getDay3() + semiMonthlyUserData.getDay4()
									+ semiMonthlyUserData.getDay5() + semiMonthlyUserData.getDay6()
									+ semiMonthlyUserData.getDay7() + semiMonthlyUserData.getDay8()
									+ semiMonthlyUserData.getDay9() + semiMonthlyUserData.getDay10()
									+ semiMonthlyUserData.getDay11() + semiMonthlyUserData.getDay12()
									+ semiMonthlyUserData.getDay13() + semiMonthlyUserData.getDay14()
									+ semiMonthlyUserData.getDay15();
							firstHalfStatus = semiMonthlyUserData.getApproverOneFirstHalfStatus() == null
									? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: semiMonthlyUserData.getApproverOneFirstHalfStatus();
						}

						}
						if (semiMonthlyUserData.getUserSecondHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_USER_STATUS_SUBMIT)) {
							secondHalfHour = semiMonthlyUserData.getDay16() + semiMonthlyUserData.getDay17()
									+ semiMonthlyUserData.getDay18() + semiMonthlyUserData.getDay19()
									+ semiMonthlyUserData.getDay20() + semiMonthlyUserData.getDay21()
									+ semiMonthlyUserData.getDay22() + semiMonthlyUserData.getDay23()
									+ semiMonthlyUserData.getDay24() + semiMonthlyUserData.getDay25()
									+ semiMonthlyUserData.getDay26() + semiMonthlyUserData.getDay27()
									+ semiMonthlyUserData.getDay28() + semiMonthlyUserData.getDay29()
									+ semiMonthlyUserData.getDay30() + semiMonthlyUserData.getDay31();
							secondHalfStatus = semiMonthlyUserData.getApproverOneSecondHalfStatus() == null
									? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: semiMonthlyUserData.getApproverOneSecondHalfStatus();
						}

					}
					SemiMonthlyData semiMonthlyData = new SemiMonthlyData(firstHalfHour, secondHalfHour,
							firstHalfStatus, secondHalfStatus);

					userResponse = new UserData(userId, userName, semiMonthlyData);
					userResponse.setWeekData(weeksDataArray);
					userDataArray.add(userResponse);
					data = new Data(projectWorkFlowType,userDataArray,week);
				}
			

		}
			if (projectWorkFlow == 3 || projectWorkFlow == 4) {
				for (int j = 0; j < weeksArray.size(); j++) {
					JSONObject objects = (JSONObject) weeksArray.get(j);
					Date weekStart = (Date) objects.get("startDate");
					Date weekEnd = (Date) objects.get("endDate");
					SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
					String weekRange = sdf.format(weekStart) + " - " + sdf.format(weekEnd);
					week.add(weekRange);
				}
				data.setProjectWorkFlowType("weekly");
			}
			else {
				data.setProjectWorkFlowType("semiMonthly");
			}
		}
		StatusResponse statusResponse = new StatusResponse( Constants.SUCCESS,Constants.SUCCESS_CODE,data);
		return statusResponse;// node;
	}

	@Override
	public ObjectNode getTaskTrackDataForFinance(ObjectNode requestdata) throws Exception {
		ObjectNode node = objectMapper.createObjectNode();
		ArrayNode weeksDataArray = objectMapper.createArrayNode();

		ArrayNode userDataArray = objectMapper.createArrayNode();

		JSONArray weeksArray = new JSONArray();
		int year = requestdata.get("year").asInt();
		Long projectId = requestdata.get("projectId").asLong();
		int month = requestdata.get("month").asInt();
		//get weeks of the month
		weeksArray = findWeeks(month, year);
		//get startDate and endDate of the month
		Date startDate;
		Date endDate;
		String start = year + "-" + month + "-01";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		startDate = df.parse(start);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		int total = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		String end = year + "-" + month + "-" + total;
		endDate = df.parse(end);
		//get usersList in the project for the corresponding month
		int projectWorkFlow = 0;
		//List<AllocationModel> allocatedUserData = projectAllocationRepository.getUserDataByProjectAndDate(projectId, startDate, endDate);
		List<AllocationModel> allocatedUserData = allocationRepository.findByProjectProjectIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIsBillable(projectId,
				endDate,startDate,true);
		if (allocatedUserData.size() >= 1) {
			for (AllocationModel userData : allocatedUserData) {
				ObjectNode userDataResponse = objectMapper.createObjectNode();
				ArrayNode weeklyHourData = objectMapper.createArrayNode();
				ArrayNode semiMonthlyHourData = objectMapper.createArrayNode();
				Long userId = userData.getuser().getUserId();
				String projectWorkFlowType = null;
				projectWorkFlow = userData.getproject().getWorkflowType();
				int projectTier = userData.getproject().getProjectTier();
				userDataResponse.put("userId", userData.getuser().getUserId());
				userDataResponse.put("userName", userData.getuser().getLastName() + " " + userData.getuser().getFirstName());
				//WeeklyData
				if (projectWorkFlow == 3 || projectWorkFlow == 4) {
					for (int i = 0; i < weeksArray.size(); i++) {
						ObjectNode weeklyDataResponse = objectMapper.createObjectNode();
						JSONObject objects = (JSONObject) weeksArray.get(i);
						SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
						Date weekStart = (Date) objects.get("startDate");
						Date weekEnd = (Date) objects.get("endDate");
						TaskTrackWeeklyApproval weeklyUserData = taskTrackWeeklyApprovalRepository.findByProjectProjectIdAndStartDateAndEndDateAndUserUserId(projectId, weekStart, weekEnd, userId);
						double totalhours = 0.0;
						//String userStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
						String approverStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
						/*String approver2Status = " ";
						if (projectTier == 2) {
							approver2Status = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
						}*/
						String financeStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;


						if (weeklyUserData != null) {
							if (weeklyUserData.getTimetrackStatus().equalsIgnoreCase("SUBMITTED")) {
								totalhours = weeklyUserData.getDay1() + weeklyUserData.getDay2() + weeklyUserData.getDay3() + weeklyUserData.getDay4() + weeklyUserData.getDay5() + weeklyUserData.getDay6() + weeklyUserData.getDay7();
							}
							//userStatus = weeklyUserData.getTimetrackStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
							//		: weeklyUserData.getTimetrackStatus();
							approverStatus = weeklyUserData.getApprover1Status() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: weeklyUserData.getApprover1Status();
							if (projectTier == 2) {
								approverStatus = weeklyUserData.getApprover2Status() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
										: weeklyUserData.getApprover2Status();
							}
							financeStatus = weeklyUserData.getFinanceStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: weeklyUserData.getFinanceStatus();

						}

						weeklyDataResponse.put("totalHour", totalhours);
						weeklyDataResponse.put("weekStart", sdff.format(weekStart));
						weeklyDataResponse.put("weekEnd", sdff.format(weekEnd));
						//weeklyDataResponse.put("userStatus", userStatus);
						weeklyDataResponse.put("approverStatus", approverStatus);
						//weeklyDataResponse.put("approver2Status", approver2Status);
						weeklyDataResponse.put("financeStatus", financeStatus);
						weeklyHourData.add(weeklyDataResponse);
					}
					userDataResponse.set("weekData", weeklyHourData);
				} else { //SemiMonthlyData
					ObjectNode semiMonthlyFirstHalfDataResponse = objectMapper.createObjectNode();
					ObjectNode semiMonthlySecondHalfDataResponse = objectMapper.createObjectNode();
					TasktrackApprovalSemiMonthly semiMonthlyUserData = taskTrackApprovalSemiMonthlyRepository.findByUserUserIdAndProjectProjectIdAndMonthAndYear(userId, projectId, month, year);
					double firstHalfHour = 0.0;
					double secondHalfHour = 0.0;
					//String userFirstHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
					//String userSecondHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
					String approverFirstHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
					String approverSecondHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
					//String approver2FirstHalfStatus = "";
					//String approver2SecondHalfStatus = "";
					/*if (projectTier == 2) {
						approver2FirstHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
						approver2SecondHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
					}*/
					String financeFirstHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
					String financeSecondHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
					if (semiMonthlyUserData != null) {
						if (semiMonthlyUserData.getUserFirstHalfStatus().equalsIgnoreCase("SUBMITTED")) {
							firstHalfHour = semiMonthlyUserData.getDay1() + semiMonthlyUserData.getDay2() + semiMonthlyUserData.getDay3() + semiMonthlyUserData.getDay4() + semiMonthlyUserData.getDay5() +
									semiMonthlyUserData.getDay6() + semiMonthlyUserData.getDay7() + semiMonthlyUserData.getDay8() + semiMonthlyUserData.getDay9() + semiMonthlyUserData.getDay10() +
									semiMonthlyUserData.getDay11() + semiMonthlyUserData.getDay12() + semiMonthlyUserData.getDay13() + semiMonthlyUserData.getDay14() + semiMonthlyUserData.getDay15();
							/*userFirstHalfStatus = semiMonthlyUserData.getUserFirstHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: semiMonthlyUserData.getUserFirstHalfStatus();*/
							approverFirstHalfStatus = semiMonthlyUserData.getApproverOneFirstHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: semiMonthlyUserData.getApproverOneFirstHalfStatus();
							if (projectTier == 2) {
								approverFirstHalfStatus = semiMonthlyUserData.getApproverTwoFirstHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
										: semiMonthlyUserData.getApproverTwoFirstHalfStatus();
							}
							financeFirstHalfStatus = semiMonthlyUserData.getFinanceFirstHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: semiMonthlyUserData.getFinanceFirstHalfStatus();

						}
						if (semiMonthlyUserData.getUserSecondHalfStatus().equalsIgnoreCase("SUBMITTED")) {
							secondHalfHour = semiMonthlyUserData.getDay16() + semiMonthlyUserData.getDay17() + semiMonthlyUserData.getDay18() + semiMonthlyUserData.getDay19() + semiMonthlyUserData.getDay20() +
									semiMonthlyUserData.getDay21() + semiMonthlyUserData.getDay22() + semiMonthlyUserData.getDay23() + semiMonthlyUserData.getDay24() + semiMonthlyUserData.getDay25() +
									semiMonthlyUserData.getDay26() + semiMonthlyUserData.getDay27() + semiMonthlyUserData.getDay28() + semiMonthlyUserData.getDay29() + semiMonthlyUserData.getDay30() + semiMonthlyUserData.getDay31();
							/*userSecondHalfStatus = semiMonthlyUserData.getUserSecondHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: semiMonthlyUserData.getUserSecondHalfStatus();*/
							approverSecondHalfStatus = semiMonthlyUserData.getApproverOneSecondHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: semiMonthlyUserData.getApproverOneSecondHalfStatus();
							if (projectTier == 2) {
								approverSecondHalfStatus = semiMonthlyUserData.getApproverTwoSecondHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
										: semiMonthlyUserData.getApproverTwoSecondHalfStatus();
							}
							financeSecondHalfStatus = semiMonthlyUserData.getFinanceSecondHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: semiMonthlyUserData.getFinanceSecondHalfStatus();
						}

					}
					semiMonthlyFirstHalfDataResponse.put("totalHour", firstHalfHour);
					semiMonthlyFirstHalfDataResponse.put("approverStatus", approverFirstHalfStatus);
					semiMonthlyFirstHalfDataResponse.put("financeStatus", financeFirstHalfStatus);
					semiMonthlyFirstHalfDataResponse.put("startDate",df.format(df.parse(year + "-" + month + "-01")));
					semiMonthlyFirstHalfDataResponse.put("endDate",df.format(df.parse(year + "-" + month + "-15")));
					//semiMonthlyDataResponse.put("userFirstHalfStatus", userFirstHalfStatus);
					//semiMonthlyDataResponse.put("userSecondHalfStatus", userSecondHalfStatus);
					semiMonthlySecondHalfDataResponse.put("totalHour", secondHalfHour);
					semiMonthlySecondHalfDataResponse.put("approverStatus", approverSecondHalfStatus);
					semiMonthlySecondHalfDataResponse.put("financeStatus", financeSecondHalfStatus);
					semiMonthlySecondHalfDataResponse.put("startDate",df.format(df.parse(year + "-" + month + "-16")));
					semiMonthlySecondHalfDataResponse.put("endDate",df.format(df.parse(end)));
					//semiMonthlyDataResponse.put("approver2FirstHalfStatus", approver2FirstHalfStatus);
					//semiMonthlyDataResponse.put("approver2SecondHalfStatus", approver2SecondHalfStatus);
					semiMonthlyHourData.add(semiMonthlyFirstHalfDataResponse);
					semiMonthlyHourData.add(semiMonthlySecondHalfDataResponse);
					userDataResponse.set("semiMonthlyData", semiMonthlyHourData);

				}
				userDataArray.add(userDataResponse);
			}
			if (projectWorkFlow == 3 || projectWorkFlow == 4) {
				for (int j = 0; j < weeksArray.size(); j++) {
					JSONObject objects = (JSONObject) weeksArray.get(j);
					Date weekStart = (Date) objects.get("startDate");
					Date weekEnd = (Date) objects.get("endDate");
					SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
					String weekRange = sdf.format(weekStart) + " - " + sdf.format(weekEnd);
					weeksDataArray.add(weekRange);
				}
				node.set("weeks", weeksDataArray);
				node.put("projectWorkFlowType", "weekly");

			} else {
				node.put("projectWorkFlowType", "semiMonthly");
			}
			node.set("userData", userDataArray);
		}
		return node;
	}

	@Override
	public ObjectNode getTaskTrackDataByUserIdForFinance(ObjectNode requestdata) throws Exception {
		ObjectNode node = objectMapper.createObjectNode();
		Long projectId = requestdata.get("projectId").asLong();
		Long userId = requestdata.get("userId").asLong();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = df.parse(requestdata.get("startDate").asText());
		Date endDate = df.parse(requestdata.get("endDate").asText());
		ProjectModel projectData = projectRepository.getProjectDetails(projectId);
		if (projectData != null) {
			ArrayNode hourDataNode = objectMapper.createArrayNode();
			//weekly
			if (projectData.getWorkflowType() == 3 || projectData.getWorkflowType() == 4) {
				TaskTrackWeeklyApproval userData = taskTrackWeeklyApprovalRepository.findByProjectProjectIdAndStartDateAndEndDateAndUserUserId(projectId, startDate, endDate, userId);
				if (userData != null) {
					node.put("userName", userData.getUser().getLastName() + " " + userData.getUser().getFirstName());
					node.put("userId", userData.getUser().getUserId());
					node.put("approver1Name",userData.getApprover1Id()== null ?
							userData.getProject().getProjectOwner().getLastName()+" "+userData.getProject().getProjectOwner().getFirstName():userData.getApprover1Id().getLastName()+" "+userData.getApprover1Id().getFirstName());
					node.put("approver1Status", userData.getApprover1Status() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
							: userData.getApprover1Status());
					node.put("approver1SubmittedDate",userData.getApprover1SubmittedDate()==null ?"":userData.getApprover1SubmittedDate().toString());
					if(projectData.getProjectTier()==2) {
						node.put("approver2Name",userData.getApprover2Id()== null ?
								userData.getProject().getOnsite_lead().getLastName()+" "+userData.getProject().getOnsite_lead().getFirstName():userData.getApprover2Id().getLastName()+" "+userData.getApprover2Id().getFirstName());
						node.put("approver2Status", userData.getApprover2Status() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: userData.getApprover2Status());
						node.put("approver2SubmittedDate",userData.getApprover2SubmittedDate()==null ?"":userData.getApprover2SubmittedDate().toString());

					}else{
						node.put("approver2Status","");
						node.put("approver2SubmittedDate","");
						node.put("approver2Name","");
					}
					node.put("loggedId", userData.getId());
					node.put("userStatus", userData.getTimetrackStatus()== null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
							: userData.getTimetrackStatus());
					node.put("userSubmittedDate",userData.getUserSubmittedDate()==null ?"":userData.getUserSubmittedDate().toString());
					node.put("financeStatus", userData.getFinanceStatus()== null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
							: userData.getFinanceStatus());
					node.put("financeSubmittedDate",userData.getFinanceSubmittedDate()==null ?"":userData.getFinanceSubmittedDate().toString());
					ObjectNode hourDataResponse = objectMapper.createObjectNode();
					Calendar cal = Calendar.getInstance();
					cal.setTime(startDate);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay1());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay2());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay3());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay4());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay5());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay6());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay7());
					hourDataNode.add(hourDataResponse);
					node.set("hourData", hourDataNode);

					JSONObject sublist = new JSONObject();
					sublist.put("history",getSubmissionHistory(userData.getId(),projectId));
					node.setAll(sublist);
				} else {
					UserModel user = userRepository.findOneByUserId(userId);
					if (user != null) {
						node.put("userName", user.getLastName() + " " + user.getFirstName());
						node.put("userId", user.getUserId());
						node.put("userStatus", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						node.put("userSubmittedDate","");
						node.put("approver1Name",projectData.getProjectOwner().getLastName()+" "+projectData.getProjectOwner().getFirstName());
						node.put("approver1Status", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						node.put("approver1SubmittedDate","");
						if(projectData.getProjectTier()==2){
							node.put("approver2Name",projectData.getOnsite_lead().getLastName()+" "+projectData.getOnsite_lead().getFirstName());
							node.put("approver2Status", Constants.TASKTRACK_APPROVER_STATUS_OPEN);}
						else{node.put("approver2Status","");node.put("approver2Name","");}
						node.put("approver2SubmittedDate","");
						node.put("financeStatus", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						node.put("financeSubmittedDate","");
						node.put("loggedId", "");

						ObjectNode hourDataResponse = objectMapper.createObjectNode();
						Calendar cal = Calendar.getInstance();
						cal.setTime(startDate);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						hourDataNode.add(hourDataResponse);
						node.set("hourData", hourDataNode);
						node.put("submissionHistory","");
					}
				}

			} else {
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				int month = cal.get(Calendar.MONTH + 1);
				int year = cal.get(Calendar.YEAR);
				Calendar cale = Calendar.getInstance();
				cale.setTime(endDate);
				int day = cale.get(Calendar.DAY_OF_MONTH);
				TasktrackApprovalSemiMonthly userData = taskTrackApprovalSemiMonthlyRepository.findByUserUserIdAndProjectProjectIdAndMonthAndYear(userId, projectId, month, year);
				if (userData != null) {
					node.put("userName", userData.getUser().getLastName() + " " + userData.getUser().getFirstName());
					node.put("userId", userData.getUser().getUserId());
					node.put("loggedId", userData.getId());

					ObjectNode hourDataResponse = objectMapper.createObjectNode();
					if (day == 15) {

						hourDataResponse.put(df.format(cal.getTime()), userData.getDay1());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay2());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay3());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay4());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay5());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay6());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay7());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay8());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay9());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay10());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay11());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay12());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay13());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay14());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay15());
						node.put("userStatus", userData.getUserFirstHalfStatus());
						node.put("userSubmittedDate", userData.getUserFirstHalfSubmittedDate()==null?"":userData.getUserFirstHalfSubmittedDate().toString());
						node.put("approver1Name",userData.getFirstHalfApproverOneId()== null ?
								userData.getProject().getProjectOwner().getLastName()+" "+userData.getProject().getProjectOwner().getFirstName():userData.getFirstHalfApproverOneId().getLastName()+" "+userData.getFirstHalfApproverOneId().getFirstName());
						node.put("approver1Status", userData.getApproverOneFirstHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: userData.getApproverOneFirstHalfStatus());
						node.put("approver1SubmittedDate",userData.getApproverOneFirstHalfSubmittedDate()==null?"":userData.getApproverOneFirstHalfSubmittedDate().toString());
						if(projectData.getProjectTier()==2){
							node.put("approver2Name",userData.getFirstHalfApproverTwoId()== null ?
									userData.getProject().getOnsite_lead().getLastName()+" "+userData.getProject().getOnsite_lead().getFirstName():userData.getFirstHalfApproverTwoId().getLastName()+" "+userData.getFirstHalfApproverTwoId().getFirstName());
							node.put("approver2Status", userData.getApproverTwoFirstHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: userData.getApproverTwoFirstHalfStatus());
							node.put("approver2SubmittedDate",userData.getApproverTwoFirstHalfSubmittedDate()==null?"":userData.getApproverTwoFirstHalfSubmittedDate().toString());
						}
						else{
							node.put("approver2Name","");
							node.put("approver2Status","");
							node.put("approver2SubmittedDate","");

						}


					} else {

						hourDataResponse.put(df.format(cal.getTime()), userData.getDay16());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay17());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay18());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay19());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay20());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay21());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay22());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay23());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay24());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay25());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay26());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay27());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay28());
						if (day > 28) {
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), userData.getDay29());
						}
						if (day > 29) {
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), userData.getDay30());
						}
						if (day > 30) {
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), userData.getDay31());
						}
						node.put("userStatus", userData.getUserSecondHalfStatus());
						node.put("userSubmittedDate", userData.getUserSecondHalfSubmittedDate()==null?"":userData.getUserSecondHalfSubmittedDate().toString());
						node.put("approver1Name",userData.getSecondHalfApproverOneId()== null ?
								userData.getProject().getProjectOwner().getLastName()+" "+userData.getProject().getProjectOwner().getFirstName():userData.getSecondHalfApproverOneId().getLastName()+" "+userData.getSecondHalfApproverOneId().getFirstName());
						node.put("approver1Status", userData.getApproverOneSecondHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
								: userData.getApproverOneSecondHalfStatus());
						node.put("approver1SubmittedDate",userData.getApproverOneSecondHalfSubmittedDate()==null?"":userData.getApproverOneSecondHalfSubmittedDate().toString());
						if(projectData.getProjectTier()==2){
							node.put("approver2Name",userData.getSecondHalfApproverTwoId()== null ?
									userData.getProject().getOnsite_lead().getLastName()+" "+userData.getProject().getOnsite_lead().getFirstName():userData.getSecondHalfApproverTwoId().getLastName()+" "+userData.getSecondHalfApproverTwoId().getFirstName());
							node.put("approver2Status", userData.getApproverTwoSecondHalfStatus() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: userData.getApproverTwoSecondHalfStatus());
							node.put("approver2SubmittedDate",userData.getApproverTwoSecondHalfSubmittedDate()==null?"":userData.getApproverTwoSecondHalfSubmittedDate().toString());
						}
						else{
							node.put("approver2Name","");
							node.put("approver2Status","");
							node.put("approver2SubmittedDate","");

						}
					}
					hourDataNode.add(hourDataResponse);
					node.set("hourData", hourDataNode);
					JSONObject sublist = new JSONObject();
					sublist.put("history",getSubmissionHistory(userData.getId(),projectId));
					node.setAll(sublist);
				} else {
					UserModel user = userRepository.findOneByUserId(userId);
					if (user != null) {

						node.put("userName", user.getLastName() + " " + user.getFirstName());
						node.put("userId", user.getUserId());
						node.put("userStatus", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						node.put("userSubmittedDate","");
						node.put("approver1Name",projectData.getProjectOwner().getLastName()+" "+projectData.getProjectOwner().getFirstName());
						node.put("approver1Status", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						node.put("approver1SubmittedDate","");
						if(projectData.getProjectTier()==2){
							node.put("approver2Name",projectData.getOnsite_lead().getLastName()+" "+projectData.getOnsite_lead().getFirstName());
							node.put("approver2Status", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						}
						else{
							node.put("approver2Name","");
							node.put("approver2Status", "");
						}
						node.put("approver2SubmittedDate","");
						node.put("loggedId", "");

						ObjectNode hourDataResponse = objectMapper.createObjectNode();
						if (day == 15) {
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);

						} else {
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							if (day > 28) {
								cal.add(Calendar.DATE, 1);
								hourDataResponse.put(df.format(cal.getTime()), 0);
							}
							if (day > 29) {
								cal.add(Calendar.DATE, 1);
								hourDataResponse.put(df.format(cal.getTime()), 0);
							}
							if (day > 30) {
								cal.add(Calendar.DATE, 1);
								hourDataResponse.put(df.format(cal.getTime()), 0);
							}
						}
						hourDataNode.add(hourDataResponse);
						node.set("hourData", hourDataNode);
						node.put("submissionHistory","");
					}
				}
			}

		}
		
		return node;
	}


	public ObjectNode approveHoursFinance(ObjectNode requestdata) throws Exception{
		ObjectNode node = objectMapper.createObjectNode();
		Long projectId = requestdata.get("projectId").asLong();
		Long userId = requestdata.get("userId").asLong();
		Long loggedId = requestdata.get("loggedId").asLong();
		Long approverId = requestdata.get("approverId").asLong();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date endDate = df.parse(requestdata.get("endDate").asText());
		Date startDate = df.parse(requestdata.get("startDate").asText());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		Date curDate = formatter.parse(formatter.format(date));

		ProjectModel projectData = projectRepository.getProjectDetails(projectId);
		TaskTrackWeeklyApproval userDataWeekly = null;
		TasktrackApprovalSemiMonthly userDataSemiMonthly = null;
		ObjectMapper mapper = new ObjectMapper();
		Map<Date, Integer> hourData = mapper.convertValue(requestdata.get("hourData"),Map.class);

		Map<Object, Object> result = hourData.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors
				.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		Iterator iter = result.entrySet().iterator();
		int counting = 0;
		if (projectData != null) {
			// weekly approval
			if (projectData.getWorkflowType() == 3 || projectData.getWorkflowType() == 4) {
				//if (loggedId != null) {
				userDataWeekly = taskTrackWeeklyApprovalRepository
							.findByProjectProjectIdAndStartDateAndEndDateAndUserUserId(projectId, startDate, endDate,
									userId);
                UserModel approver = userRepository.getOne(approverId);
					if (userDataWeekly == null) {
						userDataWeekly = new TaskTrackWeeklyApproval();
						userDataWeekly.setUser(userRepository.getOne(userId));
						userDataWeekly.setProject(projectData);
						userDataWeekly.setStartDate(startDate);
						userDataWeekly.setEndDate(endDate);
                        if(projectData.getProjectTier()==2){
                            userDataWeekly.setApprover2Status(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
                            userDataWeekly.setApprover2Id(approver);
                        }
                        else{
                            userDataWeekly.setApprover1Status(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
                            userDataWeekly.setApprover1Id(approver);
                        }
					}
                if(projectData.getProjectTier()==2){
                    if(userDataWeekly.getApprover2Id()==null || !userDataWeekly.getApprover2Status().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)) {
                        userDataWeekly.setApprover2Status(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
                        userDataWeekly.setApprover2Id(approver);
                    }
                }
                else{
                    if(userDataWeekly.getApprover1Id()==null || !userDataWeekly.getApprover1Status().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)) {
                        userDataWeekly.setApprover1Status(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
                        userDataWeekly.setApprover1Id(approver);
                    }
                }
				userDataWeekly.setFinanceStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
				userDataWeekly.setFinanceUser(approver);
				userDataWeekly.setFinanceSubmittedDate(curDate);
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Double hours = Double.parseDouble(entry.getValue().toString());
					if (counting == 0)
						userDataWeekly.setDay1(hours);
					if (counting == 1)
						userDataWeekly.setDay2(hours);
					if (counting == 2)
						userDataWeekly.setDay3(hours);
					if (counting == 3)
						userDataWeekly.setDay4(hours);
					if (counting == 4)
						userDataWeekly.setDay5(hours);
					if (counting == 5)
						userDataWeekly.setDay6(hours);
					if (counting == 6)
						userDataWeekly.setDay7(hours);
					counting++;
				}
				taskTrackWeeklyApprovalRepository.save(userDataWeekly);
				//}

			} else { // semimonthly approval
				//if (loggedId != null) {
					Calendar cale = Calendar.getInstance();
					cale.setTime(endDate);
					int day = cale.get(Calendar.DAY_OF_MONTH);
					int month = cale.get(Calendar.MONTH)+1;
					int year = cale.get(Calendar.YEAR);
					 userDataSemiMonthly = taskTrackApprovalSemiMonthlyRepository
							.findByUserUserIdAndProjectProjectIdAndMonthAndYear(userId, projectId, month, year);
					if (userDataSemiMonthly == null) {
						userDataSemiMonthly = new TasktrackApprovalSemiMonthly();
						userDataSemiMonthly.setUser(userRepository.getOne(userId));
						userDataSemiMonthly.setProject(projectData);
						userDataSemiMonthly.setMonth(month);
						userDataSemiMonthly.setYear(year);
					}
				UserModel approver = userRepository.getOne(approverId);
				if (day > 15) {
					userDataSemiMonthly.setFinanceSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
					userDataSemiMonthly.setSecondHalfFinanceId(approver);
					userDataSemiMonthly.setFinanceSecondHalfSubmittedDate(curDate);
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						Double hours = Double.parseDouble(entry.getValue().toString());
						if (counting == 0)
							userDataSemiMonthly.setDay16(hours);
						if (counting == 1)
							userDataSemiMonthly.setDay17(hours);
						if (counting == 2)
							userDataSemiMonthly.setDay18(hours);
						if (counting == 3)
							userDataSemiMonthly.setDay19(hours);
						if (counting == 4)
							userDataSemiMonthly.setDay20(hours);
						if (counting == 5)
							userDataSemiMonthly.setDay21(hours);
						if (counting == 6)
							userDataSemiMonthly.setDay22(hours);
						if (counting == 7)
							userDataSemiMonthly.setDay23(hours);
						if (counting == 8)
							userDataSemiMonthly.setDay24(hours);
						if (counting == 9)
							userDataSemiMonthly.setDay25(hours);
						if (counting == 10)
							userDataSemiMonthly.setDay26(hours);
						if (counting == 11)
							userDataSemiMonthly.setDay27(hours);
						if (counting == 12)
							userDataSemiMonthly.setDay28(hours);
						if (counting == 13)
							userDataSemiMonthly.setDay29(hours);
						if (counting == 14)
							userDataSemiMonthly.setDay30(hours);
						if (counting == 15)
							userDataSemiMonthly.setDay31(hours);
						counting++;
					}
				} else {
					userDataSemiMonthly.setFinanceFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
					userDataSemiMonthly.setFirstHalfFinanceId(approver);
					userDataSemiMonthly.setFinanceFirstHalfSubmittedDate(curDate);
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						Double hours = Double.parseDouble(entry.getValue().toString());
						if (counting == 0)
							userDataSemiMonthly.setDay1(hours);
						if (counting == 1)
							userDataSemiMonthly.setDay2(hours);
						if (counting == 2)
							userDataSemiMonthly.setDay3(hours);
						if (counting == 3)
							userDataSemiMonthly.setDay4(hours);
						if (counting == 4)
							userDataSemiMonthly.setDay5(hours);
						if (counting == 5)
							userDataSemiMonthly.setDay6(hours);
						if (counting == 6)
							userDataSemiMonthly.setDay7(hours);
						if (counting == 7)
							userDataSemiMonthly.setDay8(hours);
						if (counting == 8)
							userDataSemiMonthly.setDay9(hours);
						if (counting == 9)
							userDataSemiMonthly.setDay10(hours);
						if (counting == 10)
							userDataSemiMonthly.setDay11(hours);
						if (counting == 11)
							userDataSemiMonthly.setDay12(hours);
						if (counting == 12)
							userDataSemiMonthly.setDay13(hours);
						if (counting == 13)
							userDataSemiMonthly.setDay14(hours);
						if (counting == 14)
							userDataSemiMonthly.setDay15(hours);
						counting++;
					}

				}
				taskTrackApprovalSemiMonthlyRepository.save(userDataSemiMonthly);
				//}
			}
		}
		return node;
	}

	
	/**
	 * @author @Renjith
	 */
	@Override
	public List<Submission> getSubmissionHistory(Long Id, Long projectId) {
		ProjectModel p = projectService.getProjectDetails(projectId);
		int wfType = p.getWorkflowType();
		List<JSONObject> objNodeList = null;
		List<Submission> subList = null;
		if (wfType == 3 || wfType == 4)
			objNodeList = auditRepository.getWeeklySubmission(Id);

		if (objNodeList!=null  && !objNodeList.isEmpty() && (wfType == 3 || wfType == 4) ) {
			subList = new ArrayList<Submission>();
			for (JSONObject objNode : objNodeList) {
				WeeklySubmissionDto weekly = new WeeklySubmissionDto();
				weekly.setApproverOne((String) objNode.get("approver1"));
				weekly.setApproverTwo((String) objNode.get("approver2"));
				weekly.setFinance((String) objNode.get("finance"));
				weekly.setUser((String) objNode.get("user"));
				weekly.setTrxDate((Date) objNode.get("trx_date"));
				weekly.setTrxn((String) objNode.get("trxn"));
				weekly.setUsrInAction((String) objNode.get("user_in_action"));
				weekly.setApproverOneStatus((String) objNode.get("approver1_status"));
				weekly.setApproverTwoStatus((String) objNode.get("approver2_status"));
				weekly.setFinanceStatus((String) objNode.get("finance_status"));
				weekly.setUsrStatus((String) objNode.get("timetrack_status"));
				weekly.setApproverOneSubmtdDate((Date) objNode.get("approver1submitted_date"));
				weekly.setApproverTwoSubmtdDate((Date) objNode.get("approver2submitted_date"));
				weekly.setFinanceSubmtdDate((Date) objNode.get("finance_submitted_date"));
				weekly.setUsrSubmtdDate((Date) objNode.get("user_submitted_date"));
				weekly.setRejectnDate((Date) objNode.get("rejection_time"));
				weekly.setStartDate((Date) objNode.get("start_date"));
				weekly.setEndDate((Date) objNode.get("end_date"));
				weekly.setProject((String) objNode.get("project_name"));
				weekly.setD1((Double) objNode.get("day1"));
				weekly.setD2((Double) objNode.get("day2"));
				weekly.setD3((Double) objNode.get("day3"));
				weekly.setD4((Double) objNode.get("day4"));
				weekly.setD5((Double) objNode.get("day5"));
				weekly.setD6((Double) objNode.get("day6"));
				weekly.setD7((Double) objNode.get("day7"));
				subList.add(weekly);
			}
		}
		if (wfType == 1 || wfType == 2)
		objNodeList = auditRepository.getmonthlySubmission(Id);
		if (objNodeList!=null  && !objNodeList.isEmpty() && (wfType == 1 || wfType == 2) ) {
			subList = new ArrayList<Submission>();
			for (JSONObject objNode : objNodeList) {
				MonthlySubmissionDto monthly = new MonthlySubmissionDto();
				monthly.setApproverOne((String) objNode.get("approver1"));
				monthly.setApproverTwo((String) objNode.get("approver2"));
				monthly.setFinance((String) objNode.get("finance"));
				monthly.setUser((String) objNode.get("user"));
				monthly.setTrxDate((Date) objNode.get("trx_date"));
				monthly.setTrxn((String) objNode.get("trxn"));
				monthly.setUsrInAction((String) objNode.get("user_in_action"));

				monthly.setApproverOneFirstHalfStatus((String) objNode.get("approver_one_first_half_status"));
				monthly.setApproverTwoFirstHalfStatus((String) objNode.get("approver_two_first_half_status"));
				monthly.setFinanceFirstHalfStatus((String) objNode.get("finance_first_half_status"));
				monthly.setUsrFirstHalfStatus((String) objNode.get("user_first_half_status"));

				monthly.setApproverOneSecondHalfStatus((String) objNode.get("approver_one_second_half_status"));
				monthly.setApproverTwoSecondHalfStatus((String) objNode.get("approver_two_second_half_status"));
				monthly.setFinanceSecondHalfStatus((String) objNode.get("finance_second_half_status"));
				monthly.setUsrSecondHalfStatus((String) objNode.get("user_second_half_status"));

				monthly.setApproverOneFirstHalfSubmtdDate((Date) objNode.get("approver_one_first_half_submitted_date"));
				monthly.setApproverTwoFirstHalfSubmtdDate((Date) objNode.get("approver_two_first_half_submitted_date"));
				monthly.setFinanceFirstHalfSubmtdDate((Date) objNode.get("finance_first_half_submitted_date"));
				monthly.setUsrFirstHalfSubmtdDate((Date) objNode.get("user_first_half_submitted_date"));

				monthly.setApproverOneSecondHalfSubmtdDate(
						(Date) objNode.get("approver_one_second_half_submitted_date"));
				monthly.setApproverTwoSecondHalfSubmtdDate(
						(Date) objNode.get("approver_two_second_half_submitted_date"));
				monthly.setFinanceSecondHalfSubmtdDate((Date) objNode.get("finance_second_half_submitted_date"));
				monthly.setUsrSecondHalfSubmtdDate((Date) objNode.get("user_second_half_submitted_date"));

				monthly.setRejectnDate((Date) objNode.get("rejection_time"));
				monthly.setStartDate((Date) objNode.get("start_date"));
				monthly.setEndDate((Date) objNode.get("end_date"));
				monthly.setProject((String) objNode.get("project_name"));

				monthly.setD1((Double) objNode.get("day1"));
				monthly.setD2((Double) objNode.get("day2"));
				monthly.setD3((Double) objNode.get("day3"));
				monthly.setD4((Double) objNode.get("day4"));
				monthly.setD5((Double) objNode.get("day5"));
				monthly.setD6((Double) objNode.get("day6"));
				monthly.setD7((Double) objNode.get("day7"));
				monthly.setD8((Double) objNode.get("day8"));
				monthly.setD9((Double) objNode.get("day9"));
				monthly.setD10((Double) objNode.get("day10"));
				monthly.setD11((Double) objNode.get("day11"));
				monthly.setD12((Double) objNode.get("day12"));
				monthly.setD13((Double) objNode.get("day13"));
				monthly.setD14((Double) objNode.get("day14"));
				monthly.setD15((Double) objNode.get("day15"));
				monthly.setD16((Double) objNode.get("day16"));
				monthly.setD17((Double) objNode.get("day17"));
				monthly.setD18((Double) objNode.get("day18"));
				monthly.setD19((Double) objNode.get("day19"));
				monthly.setD20((Double) objNode.get("day20"));
				monthly.setD21((Double) objNode.get("day21"));
				monthly.setD22((Double) objNode.get("day22"));
				monthly.setD23((Double) objNode.get("day23"));
				monthly.setD24((Double) objNode.get("day24"));
				monthly.setD25((Double) objNode.get("day25"));
				monthly.setD26((Double) objNode.get("day26"));
				monthly.setD27((Double) objNode.get("day27"));
				monthly.setD28((Double) objNode.get("day28"));
				monthly.setD29((Double) objNode.get("day29"));
				monthly.setD30((Double) objNode.get("day30"));
				monthly.setD31((Double) objNode.get("day31"));
				subList.add(monthly);
			}

			
		}

		return subList;

	}
	@Override
	public StatusResponse approveHoursLevel2(ApproveHoursRequest requestdata) throws Exception {
		// TODO Auto-generated method stub
		StatusResponse node = new StatusResponse<>();
		Long projectId = requestdata.getProjectId();
		Long userId = requestdata.getUserId();
		Long loggedId = requestdata.getLoggedId();
		Long approverId = requestdata.getApproverId();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date endDate = requestdata.getEndDate();
		Date startDate = requestdata.getStartDate();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		Date curDate = formatter.parse(formatter.format(date));
		ProjectModel projectData = projectRepository.getProjectDetails(projectId);
		if (projectData != null) {
			// weekly approval
			if (projectData.getWorkflowType() == 3 || projectData.getWorkflowType() == 4) {
				if (loggedId != null) {
					TaskTrackWeeklyApproval userData = taskTrackWeeklyApprovalRepository
							.findByProjectProjectIdAndStartDateAndEndDateAndUserUserId(projectId, startDate, endDate,
									userId);
					if (userData != null) {
						if(userData.getApprover1Status().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)) {
							userData.setApprover2Status(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
							UserModel approver = userRepository.getOne(approverId);
							userData.setApprover2Id(approver);
							userData.setApprover2SubmittedDate(curDate);
							taskTrackWeeklyApprovalRepository.save(userData);
						}
					}
				}
			} else { // semimonthly approval
				if (loggedId != null) {
					Calendar cale = Calendar.getInstance();
					cale.setTime(endDate);
					int day = cale.get(Calendar.DAY_OF_MONTH);
					int month = cale.get(Calendar.MONTH);
					int year = cale.get(Calendar.YEAR);
					TasktrackApprovalSemiMonthly userData = taskTrackApprovalSemiMonthlyRepository
							.findByUserUserIdAndProjectProjectIdAndMonthAndYear(userId, projectId, month, year);
					if (userData != null) {
						UserModel approver = userRepository.getOne(approverId);
						if (day > 15) {
							if(userData.getApproverOneSecondHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)) {
								userData.setApproverTwoSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
								userData.setSecondHalfApproverTwoId(approver);
								userData.setApproverTwoSecondHalfSubmittedDate(curDate);
								taskTrackApprovalSemiMonthlyRepository.save(userData);
							}
						} else {
							if(userData.getApproverOneFirstHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)) {
								userData.setApproverTwoFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
								userData.setFirstHalfApproverTwoId(approver);
								userData.setApproverTwoFirstHalfSubmittedDate(curDate);
								taskTrackApprovalSemiMonthlyRepository.save(userData);
							}
						}
					}
				}
			}
		}
		StatusResponse statusResponse = new StatusResponse( Constants.SUCCESS,Constants.SUCCESS_CODE,"");
		return node;
	}
	
	public StatusResponse bulkApprovalForApproverOne(ApproverOneDto approverOneDto) throws ParseException,Exception{
		Long projectId = approverOneDto.getProjectId();
		List<Long> userIds = approverOneDto.getUserId();
		Long approverId = approverOneDto.getApproverId();
		Date endDate = Constants.dateFormat.parse(Constants.dateFormat.format(approverOneDto.getEndDate()));
		Date startDate = Constants.dateFormat.parse(Constants.dateFormat.format(approverOneDto.getStartDate()));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		Date curDate = formatter.parse(formatter.format(date));
		ProjectModel projectData = projectRepository.getProjectDetails(projectId);
		if (projectData != null) {
			for(Long userId : userIds) {
				if (projectData.getWorkflowType() == 3 || projectData.getWorkflowType() == 4) {// weekly approval
					TaskTrackWeeklyApproval userData = taskTrackWeeklyApprovalRepository
							.findByProjectProjectIdAndStartDateAndEndDateAndUserUserId(projectId, startDate, endDate,
									userId);
					if (userData != null) {
						userData.setApprover1Status(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
						UserModel approver = userRepository.getOne(approverId);
						userData.setApprover1Id(approver);
						userData.setApprover1SubmittedDate(curDate);
						taskTrackWeeklyApprovalRepository.save(userData);
					}
				} else { // semimonthly approval
					Calendar cale = Calendar.getInstance();
					cale.setTime(endDate);
					int day = cale.get(Calendar.DAY_OF_MONTH);
					int month = cale.get(Calendar.MONTH)+1;
					int year = cale.get(Calendar.YEAR);
					TasktrackApprovalSemiMonthly userData = taskTrackApprovalSemiMonthlyRepository
							.findByUserUserIdAndProjectProjectIdAndMonthAndYear(userId, projectId, month, year);
					if (userData != null) {
						UserModel approver = userRepository.getOne(approverId);
						if (day > 15) {
							userData.setApproverOneSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
							userData.setSecondHalfApproverOneId(approver);
							userData.setApproverOneSecondHalfSubmittedDate(curDate);
							taskTrackApprovalSemiMonthlyRepository.save(userData);
						} else {
							userData.setApproverOneFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
							userData.setFirstHalfApproverOneId(approver);
							userData.setApproverOneFirstHalfSubmittedDate(curDate);
							taskTrackApprovalSemiMonthlyRepository.save(userData);
						}
					}
				}
			}
		}
		StatusResponse statusResponse = new StatusResponse( Constants.SUCCESS,Constants.SUCCESS_CODE,"");
		return statusResponse;
	}
	public StatusResponse approveHoursLevel2(ApproverTwoDto approverTwoDto) throws Exception {
		// TODO Auto-generated method stub
		StatusResponse node = new StatusResponse<>();
		Long projectId = approverTwoDto.getProjectId();
		List<Long> userIds = approverTwoDto.getUserId();
		Long approverId = approverTwoDto.getApproverId();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date endDate = df.parse(df.format(approverTwoDto.getEndDate()));
		Date startDate = df.parse(df.format(approverTwoDto.getStartDate()));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		Date curDate = formatter.parse(formatter.format(date));
		ProjectModel projectData = projectRepository.getProjectDetails(projectId);
		if (projectData != null) {
			for(Long userId : userIds) {
				if (projectData.getWorkflowType() == 3 || projectData.getWorkflowType() == 4) {// weekly approval
					TaskTrackWeeklyApproval userData = taskTrackWeeklyApprovalRepository
							.findByProjectProjectIdAndStartDateAndEndDateAndUserUserId(projectId, startDate, endDate,
									userId);
					if (userData != null) {
						if(userData.getApprover1Status().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)) {
							userData.setApprover2Status(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
							UserModel approver = userRepository.getOne(approverId);
							userData.setApprover2Id(approver);
							userData.setApprover2SubmittedDate(curDate);
							taskTrackWeeklyApprovalRepository.save(userData);
						}
					}
				} else { // semimonthly approval
					Calendar cale = Calendar.getInstance();
					cale.setTime(endDate);
					int day = cale.get(Calendar.DAY_OF_MONTH);
					int month = cale.get(Calendar.MONTH);
					int year = cale.get(Calendar.YEAR);
					TasktrackApprovalSemiMonthly userData = taskTrackApprovalSemiMonthlyRepository
							.findByUserUserIdAndProjectProjectIdAndMonthAndYear(userId, projectId, month, year);
					if (userData != null) {
						UserModel approver = userRepository.getOne(approverId);
						if (day > 15) {
							if(userData.getApproverOneSecondHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)) {
								userData.setApproverTwoSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
								userData.setSecondHalfApproverTwoId(approver);
								userData.setApproverTwoSecondHalfSubmittedDate(curDate);
								taskTrackApprovalSemiMonthlyRepository.save(userData);
							}
						} else {
							if(userData.getApproverOneFirstHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)) {
								userData.setApproverTwoFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
								userData.setFirstHalfApproverTwoId(approver);
								userData.setApproverTwoFirstHalfSubmittedDate(curDate);
								taskTrackApprovalSemiMonthlyRepository.save(userData);
							}
						}
					}
				}
			}
		}
		StatusResponse statusResponse = new StatusResponse( Constants.SUCCESS,Constants.SUCCESS_CODE,"");
		return node;
	}
	
	@Override
	@Transactional
	public void rejectionFromApprover(ObjectNode requestdata, Integer approverLevel) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date endDate = df.parse(requestdata.get("endDate").asText());
		Date startDate = df.parse(requestdata.get("startDate").asText());
		Long projectId = requestdata.get("projectId").asLong();
		Long userId = requestdata.get("userId").asLong();
		Long loggedId = requestdata.get("loggedId").asLong();
		Long approverId = requestdata.get("approverId").asLong();
		String remark = requestdata.get("remark").asText();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		Date curDate = formatter.parse(formatter.format(date));
		ProjectModel project = projectRepository.getProjectDetails(projectId);
		if (project == null)
			throw new Exception("Invalid project.");
		
		TaskTrackRejection rejection = new TaskTrackRejection();
		rejection.setProject(project);
		rejection.setRemark(remark);
		rejection.setStatus(Constants.TASKTRACK_REJECTION_STATUS_OPEN);
		
		// weekly approval
		if (project.getWorkflowType() == Constants.ProjectWorkflow.WEEKLY_WITHOUT_DAILY_TASK 
				|| project.getWorkflowType() == Constants.ProjectWorkflow.WEEKLY_WITH_DAILY_TASK) {
			TaskTrackWeeklyApproval userData = taskTrackWeeklyApprovalRepository
					.findByProjectProjectIdAndStartDateAndEndDateAndUserUserId(projectId, startDate, endDate,userId);
			if (userData == null)
				throw new Exception("No record found.");
			
			String approverStatus = approverLevel == 1 ? userData.getApprover1Status() : userData.getApprover2Status();
			if(approverStatus.equals(Constants.TaskTrackWeeklyApproval.TASKTRACK_WEEKLY_APPROVER_STATUS_REJECTED) 
					|| userData.getTimetrackStatus().equals(Constants.TASKTRACK_USER_STATUS_REJECTION)) {
				throw new Exception("This record was already rejected.");
			}
			if(approverLevel == 1) {
				userData.setApprover1Status(Constants.TaskTrackWeeklyApproval.TASKTRACK_WEEKLY_APPROVER_STATUS_REJECTED);
			}
			else {
				userData.setApprover2Status(Constants.TaskTrackWeeklyApproval.TASKTRACK_WEEKLY_APPROVER_STATUS_REJECTED);
			}
			userData.setTimetrackStatus(Constants.TASKTRACK_USER_STATUS_REJECTION);
			userData.setRejectionTime(curDate);
			taskTrackWeeklyApprovalRepository.save(userData);
			
			rejection.setUser(userData.getUser());
			rejection.setYear(userData.getYear());
			rejection.setStartDate(userData.getStartDate());
			rejection.setEndDate(userData.getEndDate());
			taskTrackRejectionRepository.save(rejection);
		} 
		else { 
			// semimonthly approval
			Calendar cale = Calendar.getInstance();
			cale.setTime(endDate);
			int day = cale.get(Calendar.DAY_OF_MONTH);
			int month = cale.get(Calendar.MONTH)+1;
			int year = cale.get(Calendar.YEAR);
			TasktrackApprovalSemiMonthly userData = taskTrackApprovalSemiMonthlyRepository
					.findByUserUserIdAndProjectProjectIdAndMonthAndYear(userId, projectId, month, year);
			if (userData == null)
				throw new Exception("No record found.");
			
			if (day > 15) {
				String approverStatus = approverLevel == 1 ? userData.getApproverOneSecondHalfStatus() : userData.getApproverTwoSecondHalfStatus();
				if(approverStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_REJECTION) 
						|| userData.getUserSecondHalfStatus().equals(Constants.TASKTRACK_USER_STATUS_REJECTION)) {
					throw new Exception("This record was already rejected.");
				}
				if(approverLevel == 1) {
					userData.setApproverOneSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION);
				}
				else {
					userData.setApproverTwoSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION);
				}
				userData.setUserSecondHalfStatus(Constants.TASKTRACK_USER_STATUS_REJECTION);
				rejection.setCycle(Constants.TASKTRACK_REJECTION_SECOND_HALF_CYCLE);
			} 
			else {
				String approverStatus = approverLevel == 1 ? userData.getApproverOneFirstHalfStatus() : userData.getApproverTwoFirstHalfStatus();
				if(approverStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_REJECTION) 
						|| userData.getUserFirstHalfStatus().equals(Constants.TASKTRACK_USER_STATUS_REJECTION)) {
					throw new Exception("This record was already rejected.");
				}
				if(approverLevel == 1) {
					userData.setApproverOneFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION);
				}
				else {
					userData.setApproverTwoFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REJECTION);
				}
				userData.setUserFirstHalfStatus(Constants.TASKTRACK_USER_STATUS_REJECTION);
				rejection.setCycle(Constants.TASKTRACK_REJECTION_FIRST_HALF_CYCLE);
			}
			taskTrackApprovalSemiMonthlyRepository.save(userData);
			
			rejection.setUser(userData.getUser());
			rejection.setMonth(userData.getMonth());
			rejection.setYear(userData.getYear());
			taskTrackRejectionRepository.save(rejection);
		}
		try {
			String sendTo = "", sendCC = "", subject = "", emailReceiver = "", resource = "", approverTwo = "";
			subject = "RCG Time Sheet- Second half time sheet Rejected";
			resource = rejection.getUser().getLastName().concat(" " + rejection.getUser().getFirstName());
			approverTwo = rejection.getProject().getOnsite_lead().getLastName()
					.concat(" " + rejection.getProject().getOnsite_lead().getFirstName());
			sendCC = rejection.getProject().getOnsite_lead().getEmail();
			sendTo = rejection.getProject().getProjectOwner().getEmail();
			emailReceiver = rejection.getProject().getProjectOwner().getLastName()
					.concat(" " + rejection.getProject().getProjectOwner().getFirstName()) + ",";

			StringBuilder mailBody = new StringBuilder("Hi " + emailReceiver);
			mailBody.append("<br/><br/>Project Name : " + rejection.getProject().getProjectName());
			mailBody.append("<br/>Resource Name : " + resource);
			mailBody.append("<br/><br/>Timesheet from "+requestdata.get("startDate").asText()+" to "+requestdata.get("endDate").asText()+" has been Rejected.");
			mailBody.append("<br/>Comments : " + rejection.getRemark());
			mailBody.append("<br/><a href=" + CONTEXT_PATH + "/approve-log>Click here to Re-Submit timesheet</a>");
			mailBody.append("<br/><br/>Rejected by : " + approverTwo);

			sendMail(sendTo, sendCC, subject, mailBody);
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author @Renjith
	 * @throws ParseException 
	 */
	@Override
	public StatusResponse reopenSubmission(Long Id,Long projectId,Long userId, Date startDate, Date endDate) throws ParseException{
		StatusResponse  response = null;
		if(Id==null || Id == 0L)
			return new StatusResponse<String>("failure", 400, "Invalid Submission");
		if(projectId==null || projectId==0L)
			return new StatusResponse<String>("failure", 400, "Invalid Project for Submission");
		if(userId==null || userId == 0L)
			return new StatusResponse<String>("failure", 400, "Invalid User for Submission");
		if(startDate==null )
			return new StatusResponse<String>("failure", 400, "Invalid Start date for Submission");
		if(endDate==null )
			return new StatusResponse<String>("failure", 400, "Invalid End date for Submission");
		ProjectModel p = projectService.getProjectDetails(projectId);
		int wfType = p.getWorkflowType();
		if (wfType == 3 || wfType == 4){
			Calendar cal = Calendar.getInstance();
			Optional<TaskTrackWeeklyApproval>  weeklySubOpt=taskTrackWeeklyApprovalRepository.findById(Id);
			TaskTrackWeeklyApproval  weeklySub=weeklySubOpt.get();
			weeklySub.setFinanceStatus(Constants.TASKTRACK_APPROVER_STATUS_REOPEN);
			weeklySub.setFinanceUser(userRepository.findById(userId).get());
			weeklySub.setFinanceSubmittedDate(cal.getTime());
			taskTrackWeeklyApprovalRepository.save(weeklySub);
			return new StatusResponse<String>("Success", 200, null);
		}
		
		if (wfType == 1 || wfType == 2){
			SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
			//startDate=simpleDateFormat.parse(startDate.toString());
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			Optional<TasktrackApprovalSemiMonthly>  monthlySubOpt=taskTrackApprovalSemiMonthlyRepository.findById(Id);
			TasktrackApprovalSemiMonthly  monthlySub=monthlySubOpt.get();
			cal.setTime(startDate);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			if(day<=15)
			monthlySub.setFinanceFirstHalfStatus("Reopen");
			if(day>15)
				monthlySub.setFinanceSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_REOPEN);
			taskTrackApprovalSemiMonthlyRepository.save(monthlySub);
			return new StatusResponse<String>("Success", 200, null);
		}
			
		
		return response;
	}

	@Override
	public ObjectNode getTaskTrackDataByUserIdForApprover2(ApproveHoursRequest requestdata) {
		// TODO Auto-generated method stub
		ObjectNode node = objectMapper.createObjectNode();
		Long projectId = requestdata.getProjectId();
		Long userId = requestdata.getUserId();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = requestdata.getStartDate();
		Date endDate = requestdata.getEndDate();
		ProjectModel projectData = projectRepository.getProjectDetails(projectId);
		boolean submitButtonStatus = false;
		if (projectData != null) {
			ArrayNode hourDataNode = objectMapper.createArrayNode();
			// weekly
			if (projectData.getWorkflowType() == 3 || projectData.getWorkflowType() == 4) {
				TaskTrackWeeklyApproval userData = taskTrackWeeklyApprovalRepository
						.findByProjectProjectIdAndStartDateAndEndDateAndUserUserId(projectId, startDate, endDate,
								userId);
				if (userData != null) {
					node.put("userName", userData.getUser().getLastName() + " " + userData.getUser().getFirstName());
					node.put("userId", userData.getUser().getUserId());
					node.put("approver1Status",
							userData.getApprover1Status() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
									: userData.getApprover1Status());
					node.put("approver1SubmittedDate", userData.getApprover1SubmittedDate().toString());
					node.put("userSubmittedDate", userData.getUserSubmittedDate().toString());
					if (projectData.getProjectTier() == 2) {

						node.put("approver2Status",
								userData.getApprover2Status() == null ? Constants.TASKTRACK_APPROVER_STATUS_OPEN
										: userData.getApprover2Status());
						node.put("approver2SubmittedDate", userData.getApprover2SubmittedDate().toString());
					} else {
						node.put("approver2Status", "");
						node.put("approver2SubmittedDate", "");
					}
					node.put("loggedId", userData.getId());
					node.put("userStatus", userData.getTimetrackStatus());
					
					if((userData.getApprover1Status() == Constants.TASKTRACK_APPROVER_STATUS_SUBMIT 
							&& userData.getApprover2Status() != Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)){
						submitButtonStatus = true;
					}
					
					node.put("submitButtonStatus", submitButtonStatus);
					ObjectNode hourDataResponse = objectMapper.createObjectNode();
					Calendar cal = Calendar.getInstance();
					cal.setTime(startDate);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay1());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay2());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay3());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay4());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay5());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay6());
					cal.add(Calendar.DATE, 1);
					hourDataResponse.put(df.format(cal.getTime()), userData.getDay7());
					hourDataNode.add(hourDataResponse);
					node.set("hourData", hourDataNode);
				} else {
					UserModel user = userRepository.findOneByUserId(userId);
					if (user != null) {
						node.put("userName", user.getLastName() + " " + user.getFirstName());
						node.put("userId", user.getUserId());
						node.put("approver1Status", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						node.put("loggedId", "");
						node.put("userStatus", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						node.put("approver1SubmittedDate", "");
						node.put("userSubmittedDate", "");
						if (projectData.getProjectTier() == 2) {

							node.put("approver2Status", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
							node.put("approver2SubmittedDate", "");
						} else {
							node.put("approver2Status", "");
							node.put("approver2SubmittedDate", "");
						}
						ObjectNode hourDataResponse = objectMapper.createObjectNode();
						Calendar cal = Calendar.getInstance();
						cal.setTime(startDate);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), 0);
						hourDataNode.add(hourDataResponse);
						node.set("hourData", hourDataNode);
						node.put("submitButtonStatus", false);
					}
				}

			} else {
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				int month = cal.get(Calendar.MONTH + 1);
				int year = cal.get(Calendar.YEAR);
				Calendar cale = Calendar.getInstance();
				cale.setTime(endDate);
				int day = cale.get(Calendar.DAY_OF_MONTH);
				TasktrackApprovalSemiMonthly userData = taskTrackApprovalSemiMonthlyRepository
						.findByUserUserIdAndProjectProjectIdAndMonthAndYear(userId, projectId, month, year);
				if (userData != null) {
					node.put("userName", userData.getUser().getLastName() + " " + userData.getUser().getFirstName());
					node.put("userId", userData.getUser().getUserId());
					node.put("loggedId", userData.getId());

					ObjectNode hourDataResponse = objectMapper.createObjectNode();
					if (day == 15) {

						hourDataResponse.put(df.format(cal.getTime()), userData.getDay1());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay2());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay3());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay4());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay5());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay6());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay7());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay8());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay9());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay10());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay11());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay12());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay13());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay14());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay15());
						node.put("approver1Status",
								userData.getApproverOneFirstHalfStatus() == null
										? Constants.TASKTRACK_APPROVER_STATUS_OPEN
										: userData.getApproverOneFirstHalfStatus());
						node.put("userStatus", userData.getUserFirstHalfStatus());
						if(userData.getApproverOneFirstHalfSubmittedDate() != null)
						node.put("approver1SubmittedDate",
								userData.getApproverOneFirstHalfSubmittedDate().toString());
						else
							node.put("approver1SubmittedDate",
									"");
						if(userData.getUserFirstHalfSubmittedDate()!= null)
						node.put("userSubmittedDate", userData.getUserFirstHalfSubmittedDate().toString());
						else
							node.put("userSubmittedDate", "");	
						if (projectData.getProjectTier() == 2) {

							node.put("approver2Status",
									userData.getApproverTwoFirstHalfStatus() == null
											? Constants.TASKTRACK_APPROVER_STATUS_OPEN
											: userData.getApproverTwoFirstHalfStatus());
							node.put("approver2SubmittedDate",
									userData.getApproverTwoFirstHalfSubmittedDate().toString());
						} else {
							node.put("approver2Status", "");
							node.put("approver2SubmittedDate", "");
						}
						if((userData.getApproverOneFirstHalfStatus() == Constants.TASKTRACK_APPROVER_STATUS_SUBMIT 
								&& userData.getApproverTwoFirstHalfStatus() != Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)){
							submitButtonStatus = true;
						}
						node.put("submitButtonStatus", submitButtonStatus);

					} else {

						hourDataResponse.put(df.format(cal.getTime()), userData.getDay16());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay17());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay18());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay19());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay20());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay21());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay22());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay23());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay24());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay25());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay26());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay27());
						cal.add(Calendar.DATE, 1);
						hourDataResponse.put(df.format(cal.getTime()), userData.getDay28());
						if (day > 28) {
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), userData.getDay29());
						}
						if (day > 29) {
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), userData.getDay30());
						}
						if (day > 30) {
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), userData.getDay31());
						}
						node.put("approver1Status",
								userData.getApproverOneSecondHalfStatus() == null
										? Constants.TASKTRACK_APPROVER_STATUS_OPEN
										: userData.getApproverOneFirstHalfStatus());
						node.put("userStatus", userData.getUserSecondHalfStatus());
						node.put("approver1SubmittedDate",
								userData.getApproverOneSecondHalfStatus().toString());
						node.put("userSubmittedDate", userData.getUserSecondHalfSubmittedDate().toString());
						if (projectData.getProjectTier() == 2) {

							node.put("approver2Status",
									userData.getApproverTwoSecondHalfStatus() == null
											? Constants.TASKTRACK_APPROVER_STATUS_OPEN
											: userData.getApproverTwoSecondHalfStatus());
							node.put("approver2SubmittedDate",
									userData.getApproverTwoSecondHalfSubmittedDate().toString());
						} else {
							node.put("approver2Status", "");
							node.put("approver2SubmittedDate", "");
						}
					}
					if((userData.getApproverOneSecondHalfStatus() == Constants.TASKTRACK_APPROVER_STATUS_SUBMIT 
							&& userData.getApproverTwoSecondHalfStatus() != Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)){
						submitButtonStatus = true;
					}
					node.put("submitButtonStatus", submitButtonStatus);
					hourDataNode.add(hourDataResponse);
					node.set("hourData", hourDataNode);
				} else {
					UserModel user = userRepository.findOneByUserId(userId);
					if (user != null) {

						node.put("userName", user.getLastName() + " " + user.getFirstName());
						node.put("userId", user.getUserId());
						node.put("approver1Status", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						node.put("loggedId", "");
						node.put("userStatus", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
						node.put("approver1SubmittedDate", "");
						node.put("userSubmittedDate", "");
						if (projectData.getProjectTier() == 2) {

							node.put("approver2Status", Constants.TASKTRACK_APPROVER_STATUS_OPEN);
							node.put("approver2SubmittedDate", "");
						} else {
							node.put("approver2Status", "");
							node.put("approver2SubmittedDate", "");
						}
						ObjectNode hourDataResponse = objectMapper.createObjectNode();
						if (day == 15) {
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);

						} else {
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							cal.add(Calendar.DATE, 1);
							hourDataResponse.put(df.format(cal.getTime()), 0);
							if (day > 28) {
								cal.add(Calendar.DATE, 1);
								hourDataResponse.put(df.format(cal.getTime()), 0);
							}
							if (day > 29) {
								cal.add(Calendar.DATE, 1);
								hourDataResponse.put(df.format(cal.getTime()), 0);
							}
							if (day > 30) {
								cal.add(Calendar.DATE, 1);
								hourDataResponse.put(df.format(cal.getTime()), 0);
							}
						}
						hourDataNode.add(hourDataResponse);
						node.set("hourData", hourDataNode);
						node.put("submitButtonStatus", false);
					}
				}
			}

		}
		return node;
	}


	public StatusResponse bulkApprovalForApproverTwo(ApproverTwoDto approverTwoDto) throws ParseException,Exception{
		// TODO Auto-generated method stub
		Long projectId = approverTwoDto.getProjectId();
		List<Long> userIds = approverTwoDto.getUserId();
		Long approverId = approverTwoDto.getApproverId();
		Date endDate = Constants.dateFormat.parse(Constants.dateFormat.format(approverTwoDto.getEndDate()));
		Date startDate = Constants.dateFormat.parse(Constants.dateFormat.format(approverTwoDto.getStartDate()));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		Date curDate = formatter.parse(formatter.format(date));
		ProjectModel projectData = projectRepository.getProjectDetails(projectId);
		if (projectData != null) {
			for(Long userId : userIds) {
				if (projectData.getWorkflowType() == 3 || projectData.getWorkflowType() == 4) {// weekly approval
					TaskTrackWeeklyApproval userData = taskTrackWeeklyApprovalRepository
							.findByProjectProjectIdAndStartDateAndEndDateAndUserUserId(projectId, startDate, endDate,
									userId);
					if (userData != null) {
						if(userData.getApprover1Status().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)) {
							userData.setApprover2Status(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
							UserModel approver = userRepository.getOne(approverId);
							userData.setApprover2Id(approver);
							userData.setApprover2SubmittedDate(curDate);
							taskTrackWeeklyApprovalRepository.save(userData);
						}
					}
				} else { // semimonthly approval
					Calendar cale = Calendar.getInstance();
					cale.setTime(endDate);
					int day = cale.get(Calendar.DAY_OF_MONTH);
					int month = cale.get(Calendar.MONTH);
					int year = cale.get(Calendar.YEAR);
					TasktrackApprovalSemiMonthly userData = taskTrackApprovalSemiMonthlyRepository
							.findByUserUserIdAndProjectProjectIdAndMonthAndYear(userId, projectId, month, year);
					if (userData != null) {
						UserModel approver = userRepository.getOne(approverId);
						if (day > 15) {
							if(userData.getApproverOneSecondHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)) {
								userData.setApproverTwoSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
								userData.setSecondHalfApproverTwoId(approver);
								userData.setApproverTwoSecondHalfSubmittedDate(curDate);
								taskTrackApprovalSemiMonthlyRepository.save(userData);
							}
						} else {
							if(userData.getApproverOneFirstHalfStatus().equalsIgnoreCase(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT)) {
								userData.setApproverTwoFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT);
								userData.setFirstHalfApproverTwoId(approver);
								userData.setApproverTwoFirstHalfSubmittedDate(curDate);
								taskTrackApprovalSemiMonthlyRepository.save(userData);
							}
						}
					}
				}
			}
		}
		StatusResponse statusResponse = new StatusResponse( Constants.SUCCESS,Constants.SUCCESS_CODE,"");
		return statusResponse;
	}

}
