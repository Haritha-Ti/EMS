package com.EMS.service;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import com.EMS.dto.MailDomainDto;
import com.EMS.model.*;
import com.EMS.repository.*;
import com.EMS.utility.Constants;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class TasktrackServiceImpl implements TasktrackService {

	@Autowired
	TimeTrackApprovalJPARepository timeTrackApprovalJPARepository;

	@Autowired
	TaskTrackFinalJPARepository taskTrackFinalJPARepository;

	@Autowired
	TasktrackRepository tasktrackRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	AllocationRepository allocationRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	UserService userService;

	@Autowired
	ProjectService projectService;

	@Autowired
	ProjectReportsRepository projectReportsRepository;

	@Autowired
	TimeTrackApprovalRepository timeTrackApprovalRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	TaskTrackDaySubmissionRepository taskTrackDaySubmissionRepository;
	// For Task track Model
	@Autowired
	TaskTrackCorrectionRepository taskTrackCorrectionRepository;

	@Autowired
	TaskTrackApprovalSemiMonthlyRepository taskTrackApprovalSemiMonthlyRepository;

	@Autowired
	TaskTrackWeeklyApprovalRepository taskTrackWeeklyApprovalRepository;

	@Autowired
	private Configuration freemarkerConfig;

	@Autowired
	private EmailNotificationService emailNotificationService;

	@Value("${FINANCE_MAIL}")
	private String financeMail;

	@Value("${CONTEXT_PATH}")
	private String CONTEXT_PATH;

	@Override
	public List<Tasktrack> getByDate(Date startDate, Date endDate, Long uId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Tasktrack> taskTrackList = new ArrayList<>();

		List<Object[]> projectTierList = tasktrackRepository.getProjectTierForTaskTrack(uId, startDate, endDate);

		List<Long> projectIdsTire1 = new ArrayList<Long>();
		List<Long> projectIdsTire2 = new ArrayList<Long>();

		for (Object[] projectTier : projectTierList) {
			if (Integer.parseInt(projectTier[1].toString()) == 2) {
				projectIdsTire2.add(Long.parseLong(projectTier[0].toString()));

			} else if (Integer.parseInt(projectTier[1].toString()) == 1) {
				projectIdsTire1.add(Long.parseLong(projectTier[0].toString()));

			}
		}

		List<Object[]> taskTrackObjArr = new ArrayList<Object[]>();

		if (projectIdsTire1.size() > 0) {
			taskTrackObjArr.addAll(tasktrackRepository.getTrackTaskListTire1(uId, projectIdsTire1, startDate, endDate));

		}

		if (projectIdsTire2.size() > 0) {
			taskTrackObjArr.addAll(tasktrackRepository.getTrackTaskListTire2(uId, projectIdsTire2, startDate, endDate));

		}

		for (Object[] obj : taskTrackObjArr) {
			Tasktrack tasktrack = new Tasktrack();
			tasktrack.setId(Long.parseLong(obj[0].toString()));
			Task task = new Task();
			task.setTaskName(obj[3].toString());
			tasktrack.setTask(task);
			try {
				tasktrack.setDate(sdf.parse(obj[1].toString()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			ProjectModel projectModel = new ProjectModel();
			projectModel.setProjectName(obj[2].toString());
			projectModel.setProjectId(Long.parseLong(obj[7].toString()));
			projectModel.setProjectTier(Integer.parseInt(obj[8].toString()));
			tasktrack.setProject(projectModel);
			tasktrack.setDescription(obj[4].toString());
			tasktrack.setHours(Double.parseDouble(obj[5].toString()));
			tasktrack.setApprovalStatus(obj[6].toString());

			taskTrackList.add(tasktrack);

		}

		LocalDate localStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate localEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		for (LocalDate date = localStartDate; date.isBefore(localEndDate)
				| date.isEqual(localEndDate); date = date.plusDays(1)) {
			LocalDate locaDate = date;
			Tasktrack obj = taskTrackList.stream()
					.filter(taskTrack -> locaDate
							.equals(taskTrack.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))
					.findAny().orElse(null);
			if (obj == null) {
				Tasktrack tasktrack = new Tasktrack();
				tasktrack.setDate(Date.from(locaDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
				taskTrackList.add(tasktrack);
			}
		}

		Collections.sort(taskTrackList);

		return taskTrackList;

	}

	@Override
	public Tasktrack saveTaskDetails(Tasktrack task) {
		return tasktrackRepository.save(task);

	}
//	For Task Model

	@Override
	public List<Object[]> getTaskList() {
		List<Object[]> taskList = taskRepository.getTaskNameId();
		return taskList;
	}

	@Override
	public Task getTaskById(Long taskId) {
		Task task = taskRepository.getOne(taskId);
		return task;
	}

	@Override
	public List<Tasktrack> getTasks() {
		// return tasktrackRepository.getTasks();
		return new ArrayList<Tasktrack>();
	}

	public boolean updateTaskById(Tasktrack task) {
		boolean result = false;
		try {
			tasktrackRepository.updateTaskById(task.getDescription(), task.getId(), task.getDate(), task.getHours(),
					task.getProject(), task.getTask());
			result = true;
		} catch (Exception exc) {
			exc.printStackTrace();
		}

		return result;
	}

	public JsonNode deleteTaskById(long taskId, long projectId, long userId, Date currentDate) {
		ObjectNode node = objectMapper.createObjectNode();

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(TimeZone.getDefault());
			ProjectModel projectModel = getProjectModelById(projectId);

			int projectTier = projectModel.getProjectTier();

			List<Long> projectIds = Arrays.asList(projectId);

			List<Object[]> taskApprovalStatusArr = new ArrayList<Object[]>();

			if (projectTier == 1) {

				taskApprovalStatusArr.addAll(
						tasktrackRepository.getTaskApprovalStatusForProjectsTire1(userId, currentDate, projectIds));

			}

			else if (projectTier == 2) {

				taskApprovalStatusArr.addAll(
						tasktrackRepository.getTaskApprovalStatusForProjectsTire2(userId, currentDate, projectIds));

			}

			boolean isBlocked = false;

			if (taskApprovalStatusArr != null && !taskApprovalStatusArr.isEmpty()) {

				isBlocked = isTaskTrackApproved(projectTier, taskApprovalStatusArr.get(0)[0].toString());

			}

			if (!isBlocked) {

				tasktrackRepository.deleteTaskById(taskId);

			}

			node.put("isBlocked", isBlocked);

		} catch (Exception exc) {

			exc.printStackTrace();

		}

		return node;

	}

	public boolean createTask(Tasktrack task) {
		boolean result = false;
		try {
			tasktrackRepository.createTask(task);
			result = true;
		} catch (Exception exc) {
		}

		return result;
	}

	public List<AllocationModel> getProjectNames(long uId) {
		try {
			return tasktrackRepository.getProjectNames(uId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<AllocationModel>();
		}
	}

	public List<AllocationModel> getProjectNamesByMonth(long uId, Date startdate, Date enddate) {
		try {
			return tasktrackRepository.getProjectNamesByMonth(uId, startdate, enddate);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<AllocationModel>();
		}
	}

	public List<AllocationModel> getProjectNamesForApproval(long uId) {
		try {
			return tasktrackRepository.getProjectNamesForApproval(uId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<AllocationModel>();
		}
	}

	public List<ProjectModel> getProjectNamesForApproval() {
		try {
			return tasktrackRepository.getProjectNamesForApproval();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ProjectModel>();
		}
	}

	public List<ProjectModel> getProjectNamesForApproval(Date startDate, Date endDate) {
		try {
			return tasktrackRepository.getProjectNamesForApproval(startDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ProjectModel>();
		}
	}

	public List<Task> getTaskCategory(long uId) {
		try {
			return tasktrackRepository.getTaskCategories(uId);
		} catch (Exception exc) {
			exc.printStackTrace();
			return new ArrayList<Task>();
		}
	}

	public ProjectModel getProjectModelById(long id) {
		return tasktrackRepository.getProjectById(id);
	}

	@Override
	public List<Object[]> getUserList(Long userId, Date startDate, Date endDate) {

		List<Object[]> userList = taskRepository.getUserList(userId, startDate, endDate);

		return userList;
	}

	@Override
	public List<Object[]> getUserListByProjectId(Long projectId, Date startDate, Date endDate) {
		List<Object[]> userList = taskRepository.getUserListByProjectId(projectId, startDate, endDate);
		return userList;
	}

	@Override
	public List<Object[]> getUserListByDate(Date startDate, Date endDate) {
		List<Object[]> userList = taskRepository.getUserListByDate(startDate, endDate);
		return userList;
	}

	@Override
	public List<Object[]> getUserListNew(Long userId, Date startDate, Date endDate, Long pageSize, Long startingIndex) {
		List<Object[]> userList = taskRepository.getUserListNew(userId, startDate, endDate, pageSize, startingIndex);
		return userList;
	}

	@Override
	public Boolean checkIsUserExists(Long id) {
		Boolean exist = tasktrackRepository.existsByUser(id);
		return exist;
	}

	@Override
	public Object getUserName(Long id) {
		Object userName = userRepository.getUserName(id);
		return userName;
	}

	@Override
	public Boolean checkExistanceOfUser(Long projectId, Long userId) {
		Boolean exist = tasktrackRepository.checkExistanceOfUser(projectId, userId);
		return exist;
	}

	@Override
	public List<JSONObject> getUserTaskDetails(Long id, Date startDate, Date endDate, List<Object[]> userList,
			List<JSONObject> jsonArray, List<JSONObject> jsonDataRes1, Boolean isExist, Long projectId) {
		if (isExist) {
			JSONObject userListObject = new JSONObject();

			JSONObject userObject = new JSONObject();
			if (projectId == null) {
				userList = getUserList(id, startDate, endDate);
			} else
				userList = getUserListByProject(id, startDate, endDate, projectId);

			jsonArray = new ArrayList<>();

			String name = null;
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			for (int i = 0; i < diffInDays; i++) {

				int intMonth = (cal.get(Calendar.MONTH) + 1);
				int intday = cal.get(Calendar.DAY_OF_MONTH);
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
					jsonArray.add(jsonObject);

				}

				else {
					JSONObject jsonObject = new JSONObject();
					String uName = userService.getUserName(id);
					name = String.valueOf(uName).replace(",", " ");
					jsonObject.put(vl, 0);
					cal.add(Calendar.DATE, 1);
					jsonArray.add(jsonObject);
				}

			}
			userListObject.put("userName", name);
			userListObject.put("date", jsonArray);

			jsonDataRes1.add(userListObject);

		} else {
			jsonArray = new ArrayList<>();
			JSONObject userListObject = new JSONObject();

			String uName = userService.getUserName(id);
			String name = String.valueOf(uName).replace(",", " ");

			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			for (int i = 0; i < diffInDays; i++) {
				JSONObject jsonObject = new JSONObject();

				int intMonth = (cal.get(Calendar.MONTH) + 1);
				int intday = cal.get(Calendar.DAY_OF_MONTH);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				jsonObject.put(vl, 0);
				cal.add(Calendar.DATE, 1);
				jsonArray.add(jsonObject);
			}
			userListObject.put("userName", name);
			System.out.println("name : " + name);
			userListObject.put("date", jsonArray);

			jsonDataRes1.add(userListObject);
		}
		return jsonDataRes1;
	}

	@Override
	public List<JSONObject> getUserTaskDetailsByuser(Long id, Date startDate, Date endDate, List<Object[]> userList,
			List<JSONObject> jsonArray, List<JSONObject> jsonDataRes1, Boolean isExist, Long projectId,
			String projectName) {

		if (isExist) {
			JSONObject userListObject = new JSONObject();

			JSONObject userObject = new JSONObject();

			jsonArray = new ArrayList<>();

			String name = null;
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			for (int i = 0; i < diffInDays; i++) {

				int intMonth = (cal.get(Calendar.MONTH) + 1);
				int intday = cal.get(Calendar.DAY_OF_MONTH);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				Double hours = 0.0;

				userList = taskRepository.getUserTaskByProjectAndDate(id, projectId, startDate, endDate);
				if (userList != null && userList.size() > 0) {
					JSONObject jsonObject = new JSONObject();
					for (Object[] items : userList) {
						String st = String.valueOf(items[3]);

						if (st.equals(vl)) {
							hours = hours + (Double) items[2];

						}
						name = (String) items[0] + " " + items[1];
					}
					userListObject.put("date", jsonArray);
					userListObject.put("projectName", projectName);

					jsonObject.put(vl, hours);
					cal.add(Calendar.DATE, 1);
					jsonArray.add(jsonObject);

				}

				else {
					JSONObject jsonObject = new JSONObject();
					String uName = userService.getUserName(id);
					name = String.valueOf(uName).replace(",", " ");
					jsonObject.put(vl, 0);
					cal.add(Calendar.DATE, 1);
					jsonArray.add(jsonObject);
				}

			}
			userListObject.put("userName", name);
			userListObject.put("date", jsonArray);

			jsonDataRes1.add(userListObject);

		} else {
			System.out.println("caseee 22");
			jsonArray = new ArrayList<>();
			JSONObject userListObject = new JSONObject();

			String uName = userService.getUserName(id);
			String name = String.valueOf(uName).replace(",", " ");

			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			for (int i = 0; i < diffInDays; i++) {
				JSONObject jsonObject = new JSONObject();

				int intMonth = (cal.get(Calendar.MONTH) + 1);
				int intday = cal.get(Calendar.DAY_OF_MONTH);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				jsonObject.put(vl, 0);
				cal.add(Calendar.DATE, 1);
				jsonArray.add(jsonObject);
			}
			userListObject.put("userName", name);
			System.out.println("name : " + name);
			userListObject.put("date", jsonArray);

			jsonDataRes1.add(userListObject);
		}
		return jsonDataRes1;
	}

	private List<Object[]> getUserListByProject(Long id, Date startDate, Date endDate, Long projectId) {
		List<Object[]> userTaskList = taskRepository.getUserListByProject(id, startDate, endDate, projectId);
		return userTaskList;
	}

	@Override
	public List<Object[]> getUserTaskList(Long id, Date startDate, Date endDate, Long projectId) {
		List<Object[]> userTaskList = taskRepository.getUserTaskList(id, startDate, endDate, projectId);
		return userTaskList;
	}

	public List<Object[]> getProjectListByUserAndDate(Long id, Date startDate, Date endDate) {
		List<Object[]> projectList = taskRepository.getProjectListByUserAndDate(id, startDate, endDate);
		return projectList;

	}

	@Override
	public ObjectNode checkApproveLevel(Long project_Id, Long logUser) {
		// TODO Auto-generated method stub
		ObjectNode responsedata = objectMapper.createObjectNode();
		ObjectNode node = objectMapper.createObjectNode();
		try {

			Object[] project = projectRepository.getApproveLevelByprojecIdAndLeadsId(project_Id, logUser);
			Object[] role = projectRepository.getRoleOftheLoguser(logUser);
			String primary_role = null;

			if (project != null) {
				if (project[0] != null)
					node.put("secondary_role", project[0].toString());

				if (role != null) {
					primary_role = role[0].toString();
				}
			}
			node.put("primary_role", primary_role);
			responsedata.put("status", "success");
			responsedata.put("payload", "");
			responsedata.set("data", node);
		} catch (Exception e) {
			responsedata.put("status", "Failed");
			responsedata.put("message", "Exception " + e);
			responsedata.put("payload", "");
		}
		return responsedata;
	}

	// hashir

	@Override
	public List<TaskTrackDaySubmissionModel> saveSubmissionDays(
			List<TaskTrackDaySubmissionModel> taskTrackDaySubmissionList) {
		return taskTrackDaySubmissionRepository.saveAll(taskTrackDaySubmissionList);
	}

	@Override
	public TaskTrackDaySubmissionModel getSubmissionDayByMonth(int month) {
		return taskTrackDaySubmissionRepository.findByMonth(month).orElse(null);
	}

	// Nisha
	@Override
	public ObjectNode createCorrection(ObjectNode requestdata, Boolean isRecorrection) {
		ObjectNode responsedata = objectMapper.createObjectNode();
		try {
			int month = 0, year = 0;
			Long userId = requestdata.get("userId").asLong();
			Long projectId = requestdata.get("projectId").asLong();
			UserModel user = userService.getUserDetailsById(userId);
			ProjectModel project = projectService.getProjectDetails(projectId);
			String comment = requestdata.get("comment").asText();
			String status = requestdata.get("status").asText();
			ArrayNode days = (ArrayNode) requestdata.get("days");
			ArrayNode removedDays = (ArrayNode) requestdata.get("removedDays");
			if (days.size() == 0 && removedDays.size() == 0) {
				responsedata.put("message", "no days selected");
				responsedata.put("status", "success");
				return requestdata;
			}
			String inputDate1 = null;
			if (days.size() != 0) {
				JsonNode node1 = days.get(0);
				inputDate1 = node1.asText();
			} else if (removedDays.size() != 0) {
				JsonNode node1 = removedDays.get(0);
				inputDate1 = node1.asText();
			}
			SimpleDateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			Date correctionDate1 = outputFormat1.parse(inputDate1);
			Calendar calender = Calendar.getInstance();
			calender.setTime(correctionDate1);
			month = (calender.get(Calendar.MONTH) + 1);
			year = calender.get(Calendar.YEAR);
			int firstDay = 0;
			int lastDay = 0;
			int updateFlag = 0;
			int monthIndex = 0, yearIndex = 0, day = 0;
			if (isRecorrection == Boolean.TRUE) {
				if (status.equalsIgnoreCase("firstHalf")) {
					firstDay = 1;
					lastDay = 15;
				} else {
					firstDay = 16;
					lastDay = 31;
				}
				List<TaskTrackCorrection> taskTrackCorrections = taskTrackCorrectionRepository
						.findCorrectionDays(userId, projectId, month, year, firstDay, lastDay);
				if (taskTrackCorrections.size() > 0) {
					for (TaskTrackCorrection correction : taskTrackCorrections) {
						correction.setStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION);
					}
					taskTrackCorrectionRepository.saveAll(taskTrackCorrections);
				}
			}
			// remove days
			if (removedDays.size() != 0) {
				for (JsonNode node : removedDays) {
					String inputDate = node.asText();
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date correctionDate = outputFormat.parse(inputDate);
					Calendar cal = Calendar.getInstance();
					cal.setTime(correctionDate);
					monthIndex = (cal.get(Calendar.MONTH) + 1);
					yearIndex = cal.get(Calendar.YEAR);
					day = cal.get(Calendar.DAY_OF_MONTH);
					List<TaskTrackCorrection> correctionData = taskTrackCorrectionRepository.getTaskCorrectData(userId,
							projectId, monthIndex, yearIndex, day);
					for (TaskTrackCorrection correction : correctionData) {
						correction.setStatus(Constants.TASKTRACK_CORRECTION_STATUS_CLOSED);
					}
					taskTrackCorrectionRepository.saveAll(correctionData);
				}
				if (days.size() == 0) {
					int projectTier = project.getProjectTier();
					if (projectTier == 2) {
						List<TaskTrackApproval> taskTrackApproval = timeTrackApprovalJPARepository
								.upadateTaskTrackApprovalStatus(projectId, monthIndex, yearIndex, userId);
						for (TaskTrackApproval approval : taskTrackApproval) {
							if (status.equalsIgnoreCase("firstHalf")) {
								approval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_LOCK);
							} else if (status.equalsIgnoreCase("secondHalf")) {
								approval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_LOCK);
							}
						}
						timeTrackApprovalJPARepository.saveAll(taskTrackApproval);
					}
					List<TaskTrackApprovalFinal> taskTrackApprovalFinal = taskTrackFinalJPARepository
							.upadateTaskTrackApprovalFinalStatus(projectId, monthIndex, yearIndex, userId);

					for (TaskTrackApprovalFinal approvalFinal : taskTrackApprovalFinal) {
						if (status.equalsIgnoreCase("firstHalf")) {
							approvalFinal.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);
						} else if (status.equalsIgnoreCase("secondHalf")) {
							approvalFinal.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);
						}
					}
					taskTrackFinalJPARepository.saveAll(taskTrackApprovalFinal);
				}
			}
			if (!days.equals(null) && days.size() != 0) {
				for (JsonNode node : days) {
					TaskTrackCorrection taskTrackCorrection = new TaskTrackCorrection();
					String inputDate = node.asText();
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date correctionDate = outputFormat.parse(inputDate);
					Calendar cal = Calendar.getInstance();
					cal.setTime(correctionDate);
					monthIndex = (cal.get(Calendar.MONTH) + 1);
					yearIndex = cal.get(Calendar.YEAR);
					day = cal.get(Calendar.DAY_OF_MONTH);
					int data = taskTrackCorrectionRepository.checkExist(userId, projectId, monthIndex, yearIndex, day);
					if (data > 0) {

					} else {
						updateFlag = 1;
						taskTrackCorrection.setDay(day);
						taskTrackCorrection.setMonth(monthIndex);
						taskTrackCorrection.setYear(yearIndex);
						taskTrackCorrection.setUser(user);
						taskTrackCorrection.setProject(project);
						taskTrackCorrection.setComment(comment);
						taskTrackCorrection.setStatus(Constants.TASKTRACK_CORRECTION_STATUS_OPEN);
						taskTrackCorrection.setType(isRecorrection ? Constants.TASKTRACK_CORRECTION_TYPE_RECORRECTION
								: Constants.TASKTRACK_CORRECTION_TYPE_CORRECTION);
						taskTrackCorrectionRepository.save(taskTrackCorrection);
					}
				}
				if (updateFlag == 1) {
					int projectTier = project.getProjectTier();
					if (projectTier == 2) {
						List<TaskTrackApproval> taskTrackApproval = timeTrackApprovalJPARepository
								.upadateTaskTrackApprovalStatus(projectId, monthIndex, yearIndex, userId);
						for (TaskTrackApproval approval : taskTrackApproval) {
							if (status.equalsIgnoreCase("firstHalf")) {
								approval.setFirstHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION);
							} else if (status.equalsIgnoreCase("secondHalf")) {
								approval.setSecondHalfStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION);
							}
						}
						timeTrackApprovalJPARepository.saveAll(taskTrackApproval);
					}
					List<TaskTrackApprovalFinal> taskTrackApprovalFinal = taskTrackFinalJPARepository
							.upadateTaskTrackApprovalFinalStatus(projectId, monthIndex, yearIndex, userId);

					for (TaskTrackApprovalFinal approvalFinal : taskTrackApprovalFinal) {
						if (status.equalsIgnoreCase("firstHalf")) {
							approvalFinal.setFirstHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION);
						} else if (status.equalsIgnoreCase("secondHalf")) {
							approvalFinal.setSecondHalfStatus(Constants.TASKTRACK_FINAL_STATUS_CORRECTION);
						}
					}
					taskTrackFinalJPARepository.saveAll(taskTrackApprovalFinal);
					responsedata.put("message", "Correction requested successfully");
					/*
					 * Sending mail to approver 1
					 */

					String sendTo = "", sendCC = "", subject = "", emailReceiver = "", resource = "", approverTwo = "";

					if (status.equalsIgnoreCase("firstHalf")) {
						subject = "RCG Time Sheet- First half time sheet needs Correction";
						resource = user.getLastName().concat(" " + user.getFirstName());

						if (projectTier == 1) {
							sendCC = financeMail;
						} else {
							sendCC = project.getOnsite_lead().getEmail();
							approverTwo = project.getOnsite_lead().getLastName()
									.concat(" " + project.getOnsite_lead().getFirstName());
						}
						sendTo = project.getProjectOwner().getEmail();
						emailReceiver = project.getProjectOwner().getLastName()
								.concat(" " + project.getProjectOwner().getFirstName()) + ",";
						StringBuilder mailBody = new StringBuilder("Hi " + emailReceiver + "<br/>");
						mailBody.append("<br/><br/>Project Name : " + project.getProjectName());
						mailBody.append("<br/>Resource Name : " + resource);
						mailBody.append("<br/><br/>Timesheet for " + Month.of(month).name()
								+ " 1-15 days requires correction.");
						mailBody.append(
								"<br/><a href=" + CONTEXT_PATH + "/approve-log>Click here to Re-Submit timesheet</a>");
						mailBody.append("<br/><br/>Comments : " + comment);
						if (isRecorrection && projectTier == 2)
							mailBody.append("<br/><br/>Correction Requested by : " + approverTwo);
						else
							mailBody.append("<br/><br/>Correction Requested by : Finance Team");

						sendMail(sendTo, sendCC, subject, mailBody);
					} else {
						subject = "RCG Time Sheet- Second half time sheet needs Correction";
						resource = user.getLastName().concat(" " + user.getFirstName());
						if (projectTier == 1) {
							sendCC = financeMail;
						} else {
							sendCC = project.getOnsite_lead().getEmail();
							approverTwo = project.getOnsite_lead().getLastName()
									.concat(" " + project.getOnsite_lead().getFirstName());
						}
						sendTo = project.getProjectOwner().getEmail();
						emailReceiver = project.getProjectOwner().getLastName()
								.concat(" " + project.getProjectOwner().getFirstName()) + ",";
						StringBuilder mailBody = new StringBuilder("Hi " + emailReceiver + "<br/>");
						mailBody.append("<br/><br/>Project Name : " + project.getProjectName());
						mailBody.append("<br/>Resource Name : " + resource);
						mailBody.append("<br/><br/>Timesheet for " + Month.of(month).name()
								+ " 16-31 days requires correction.");
						mailBody.append("<br/><br/>Comments : " + comment);
						mailBody.append(
								"<br/><a href=" + CONTEXT_PATH + "/approve-log>Click here to Re-Submit timesheet</a>");
						if (isRecorrection && projectTier == 2)
							mailBody.append("<br/><br/>Correction Requested by : " + approverTwo);
						else
							mailBody.append("<br/><br/>Correction Requested by : Finance Team");

						sendMail(sendTo, sendCC, subject, mailBody);
					}

				} else {
					responsedata.put("message", "Already exist");
				}
				responsedata.put("status", "success");

			} else {
				responsedata.put("status", "failed");
				responsedata.put("message", "Correction requested failed:select dates for correction");
			}
		} catch (Exception e) {
			responsedata.put("status", "Failed");
			responsedata.put("message", "Exception : " + e);

		}
		return responsedata;
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
	public List<Object[]> getProjectTierForTaskTrack(Long userId, Date startDate, Date endDate) {

		return tasktrackRepository.getProjectTierForTaskTrack(userId, startDate, endDate);
	}

	private boolean isTaskTrackApproved(int projectTier, String status) {
		if (projectTier == 2) {
			List<String> tireTwoStatus = Arrays.asList(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT,
					Constants.TASKTRACK_APPROVER_STATUS_LOCK, Constants.TASKTRACK_APPROVER_STATUS_CORRECTED,
					Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED);
			return tireTwoStatus.contains(status);
		}

		else if (projectTier == 1) {
			List<String> tireOneStatus = Arrays.asList(Constants.TASKTRACK_FINAL_STATUS_SUBMIT);
			return tireOneStatus.contains(status);
		}
		return false;

	}

	/**
	 * @author sreejith.j
	 */
	@Override
	public StatusResponse getTimeTrackData(Long userId, Integer month, Integer year) throws Exception {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		SimpleDateFormat dateFrmt = new SimpleDateFormat("yyyy-MM-dd");
		Calendar monthStartCal = Calendar.getInstance();
		monthStartCal.setTime(dateFrmt.parse(year + "-" + month + "-" + "01"));

		Map<Long, HashMap<String, Object>> projectsMap = new HashMap<Long, HashMap<String, Object>>();

		// For semi monthly projects without daily tasks
		List<Long> workflow1Projects = new ArrayList<Long>();

		// For semi monthly projects with daily tasks
		List<Long> workflow2Projects = new ArrayList<Long>();

		// For Weekly projects without daily tasks
		List<Long> workflow3Projects = new ArrayList<Long>();

		// For Weekly projects with daily tasks
		List<Long> workflow4Projects = new ArrayList<Long>();

		Calendar monthEndCal = Calendar.getInstance();
		monthEndCal.setTime(
				dateFrmt.parse(year + "-" + month + "-" + monthStartCal.getActualMaximum(Calendar.DAY_OF_MONTH)));

		Calendar firstWeekStartDay = Calendar.getInstance();
		Calendar lastWeekEndDay = Calendar.getInstance();
		firstWeekStartDay = (Calendar) monthStartCal.clone();
		lastWeekEndDay = (Calendar) monthEndCal.clone();
		firstWeekStartDay.add(Calendar.DAY_OF_WEEK,
				-(monthStartCal.get(Calendar.DAY_OF_WEEK) - Constants.WEEK_START_DAY));
		lastWeekEndDay.add(Calendar.DAY_OF_WEEK,
				+(6 - monthEndCal.get(Calendar.DAY_OF_WEEK) + Constants.WEEK_START_DAY));

		// List all the projects for the user based on the month
		List<AllocationModel> allocationModelList = allocationRepository
				.findByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, monthEndCal.getTime(),
						monthStartCal.getTime());

		for (AllocationModel allocationModel : allocationModelList) {
			ProjectModel project = allocationModel.getproject();
			HashMap<String, Object> projObj = new HashMap<String, Object>();
			List<Map<String, Object>> periodsArray = new ArrayList<Map<String, Object>>();

			projObj.put("projectId", project.getProjectId());
			projObj.put("projectName", project.getProjectName());
			projObj.put("clientId", project.getClientName().getClientId());
			projObj.put("clientName", project.getClientName().getClientName());
			projObj.put("workflow", project.getWorkflowType());
			projObj.put("periods", periodsArray);

			projObj.put("allocStartDate", allocationModel.getStartDate());
			projObj.put("allocEndDate", allocationModel.getEndDate());

			projectsMap.put(project.getProjectId(), projObj);

			switch (project.getWorkflowType()) {
			case 1:
				workflow1Projects.add(project.getProjectId());
				break;
			case 2:
				workflow2Projects.add(project.getProjectId());
				break;
			case 3:
				workflow3Projects.add(project.getProjectId());
				break;
			case 4:
				workflow4Projects.add(project.getProjectId());
				break;
			default:
				break;
			}
		}
		// based on the workflow call the appropriate service for fetching data
//		StatusResponse response =new StatusResponse();
		if (!workflow1Projects.isEmpty()) {
			// Fetch data from the monthly submission table
			List<TasktrackApprovalSemiMonthly> semiMonthlyList = taskTrackApprovalSemiMonthlyRepository
					.findByUserUserIdAndProjectProjectIdInAndMonthAndYear(userId, workflow1Projects, month, year);

			Map<String, HashMap<String, Object>> projHalfs = new HashMap<String, HashMap<String, Object>>();
			for (Long projectId : workflow1Projects) {
				HashMap<String, Object> firstHalfObj = new HashMap<String, Object>();
				HashMap<String, Object> secondHalfObj = new HashMap<String, Object>();

				HashMap<String, Object> projObj = projectsMap.get(projectId);

				Date allocStartDate = (Date) projObj.get("allocStartDate");
				Date allocEndDate = (Date) projObj.get("allocEndDate");

				if (!allocStartDate.after(dateFrmt.parse(year + "-" + month + "-" + "15"))
						|| !allocEndDate.after(dateFrmt.parse(year + "-" + month + "-" + "15"))) {
					firstHalfObj.put("hours", 0);
					firstHalfObj.put("status", "OPEN");
					firstHalfObj.put("startDay", dateFrmt.format(monthStartCal.getTime()));
					firstHalfObj.put("endDay", year + "-" + month + "-" + "15");
					projHalfs.put(String.valueOf(projectId) + "F", firstHalfObj);
				}
				if (!allocStartDate.before(dateFrmt.parse(year + "-" + month + "-" + "15"))
						|| !allocEndDate.before(dateFrmt.parse(year + "-" + month + "-" + "15"))) {
					secondHalfObj.put("hours", 0);
					secondHalfObj.put("status", "OPEN");
					secondHalfObj.put("startDay", year + "-" + month + "-" + "16");
					secondHalfObj.put("endDay", dateFrmt.format(monthEndCal.getTime()));
					projHalfs.put(String.valueOf(projectId) + "S", secondHalfObj);
				}
				projObj.remove("allocStartDate");
				projObj.remove("allocEndDate");
			}

			for (TasktrackApprovalSemiMonthly modelObj : semiMonthlyList) {

				Map<String, Object> firstHalfObj = new HashMap<String, Object>();
				Map<String, Object> secondHalfObj = new HashMap<String, Object>();

				HashMap<String, Object> projObj = projectsMap.get(modelObj.getProject().getProjectId());
				Double firstHalfHours = modelObj.getDay1() + modelObj.getDay2() + modelObj.getDay3()
						+ modelObj.getDay4() + modelObj.getDay5() + modelObj.getDay6() + modelObj.getDay7()
						+ modelObj.getDay8() + modelObj.getDay9() + modelObj.getDay10() + modelObj.getDay11()
						+ modelObj.getDay12() + modelObj.getDay13() + modelObj.getDay14() + modelObj.getDay15();
				String firstHalfFinalStatus = modelObj.getFirstHalfFinalStatus();
				Double secondHalfHours = modelObj.getDay16() + modelObj.getDay17() + modelObj.getDay18()
						+ modelObj.getDay19() + modelObj.getDay20() + modelObj.getDay21() + modelObj.getDay22()
						+ modelObj.getDay23() + modelObj.getDay24() + modelObj.getDay25() + modelObj.getDay26()
						+ modelObj.getDay27() + modelObj.getDay28() + modelObj.getDay29() + modelObj.getDay30()
						+ modelObj.getDay31();
				String secondHalfFinalStatus = modelObj.getSecondHalfFinalStatus();
				List<Map<String, Object>> periodsArray = new ArrayList<Map<String, Object>>();

				if (projHalfs.containsKey(String.valueOf(modelObj.getProject().getProjectId()) + "F")) {
					firstHalfObj.put("hours", firstHalfHours);
					firstHalfObj.put("status", firstHalfFinalStatus);
					firstHalfObj.put("startDay", dateFrmt.format(monthStartCal.getTime()));
					firstHalfObj.put("endDay", year + "-" + month + "-" + "15");
					periodsArray.add(firstHalfObj);
					projHalfs.remove(String.valueOf(modelObj.getProject().getProjectId()) + "F");
				}
				if (projHalfs.containsKey(String.valueOf(modelObj.getProject().getProjectId()) + "S")) {
					secondHalfObj.put("hours", secondHalfHours);
					secondHalfObj.put("status", secondHalfFinalStatus);
					secondHalfObj.put("startDay", year + "-" + month + "-" + "16");
					secondHalfObj.put("endDay", dateFrmt.format(monthEndCal.getTime()));
					periodsArray.add(secondHalfObj);
					projHalfs.remove(String.valueOf(modelObj.getProject().getProjectId()) + "S");
				}
				projObj.put("periods", periodsArray);
			}
			for (String projectIdStr : projHalfs.keySet()) {
				Long projectId = Long.parseLong(projectIdStr.substring(0, projectIdStr.length() - 1));
				HashMap<String, Object> projObj = projectsMap.get(projectId);
				List<Map<String, Object>> periodsArray = (List<Map<String, Object>>) projObj.get("periods");
				// if hoursProjectObj doesnt contain the project id- Error: submission table
				// contain data not in timetrack
				periodsArray.add(projHalfs.get(projectIdStr));
				projObj.put("periods", periodsArray);
			}
		}
		if (!workflow2Projects.isEmpty()) {
			// Fetch data from the timetrack table and status from the submission
			// table
			List<Tasktrack> tasktrackList = tasktrackRepository.findByUserUserIdAndProjectProjectIdInAndDateBetween(
					userId, workflow2Projects, monthStartCal.getTime(), monthEndCal.getTime());

			List<TasktrackApprovalSemiMonthly> semiMonthlyList = taskTrackApprovalSemiMonthlyRepository
					.findByUserUserIdAndProjectProjectIdInAndMonthAndYear(userId, workflow2Projects, month, year);

			Map<Long, HashMap<String, Double>> hoursProjectObj = new HashMap<Long, HashMap<String, Double>>();

			for (Long projectId : workflow2Projects) {
				HashMap<String, Double> hoursObj = new HashMap<String, Double>();
				hoursProjectObj.put(projectId, hoursObj);

				HashMap<String, Object> firstHalfObj = new HashMap<String, Object>();
				HashMap<String, Object> secondHalfObj = new HashMap<String, Object>();

				HashMap<String, Object> projObj = projectsMap.get(projectId);

				Date allocStartDate = (Date) projObj.get("allocStartDate");
				Date allocEndDate = (Date) projObj.get("allocEndDate");

				if (!allocStartDate.after(dateFrmt.parse(year + "-" + month + "-" + "15"))
						|| !allocEndDate.after(dateFrmt.parse(year + "-" + month + "-" + "15"))) {
					firstHalfObj.put("hours", 0);
					firstHalfObj.put("status", "OPEN");
					firstHalfObj.put("startDay", dateFrmt.format(monthStartCal.getTime()));
					firstHalfObj.put("endDay", year + "-" + month + "-" + "15");
					hoursObj.put("firstHalfHours", 0d);
				}
				if (!allocStartDate.before(dateFrmt.parse(year + "-" + month + "-" + "15"))
						|| !allocEndDate.before(dateFrmt.parse(year + "-" + month + "-" + "15"))) {
					secondHalfObj.put("hours", 0);
					secondHalfObj.put("status", "OPEN");
					secondHalfObj.put("startDay", year + "-" + month + "-" + "16");
					secondHalfObj.put("endDay", dateFrmt.format(monthEndCal.getTime()));
					hoursObj.put("secondHalfHours", 0d);
				}
				projObj.remove("allocStartDate");
				projObj.remove("allocEndDate");
			}

			for (Tasktrack modelObj : tasktrackList) {
				HashMap<String, Double> hoursObj = new HashMap<String, Double>();
				if (hoursProjectObj.containsKey(modelObj.getProject().getProjectId())) {
					hoursObj = hoursProjectObj.get(modelObj.getProject().getProjectId());
				}
				if (modelObj.getDate().before(dateFrmt.parse(year + "-" + month + "-" + "16"))
						&& hoursObj.containsKey("firstHalfHours")) {
					hoursObj.put("firstHalfHours", hoursObj.get("firstHalfHours") + modelObj.getHours());
				} else if (hoursObj.containsKey("secondHalfHours")) {
					hoursObj.put("secondHalfHours", hoursObj.get("secondHalfHours") + modelObj.getHours());
				}
			}

			// submitted projects
			for (TasktrackApprovalSemiMonthly modelObj : semiMonthlyList) {
				HashMap<String, Object> projObj = projectsMap.get(modelObj.getProject().getProjectId());
				List<Map<String, Object>> periodsArray = new ArrayList<Map<String, Object>>();
				// if hoursProjectObj doesnt contain the project id- Error: submission table
				// contain data not in timetrack
				HashMap<String, Double> hoursObj = hoursProjectObj.get(modelObj.getProject().getProjectId());

				Map<String, Object> firstHalfObj = new HashMap<String, Object>();
				Map<String, Object> secondHalfObj = new HashMap<String, Object>();

				if (hoursObj.containsKey("firstHalfHours")) {
					firstHalfObj.put("hours", hoursObj.get("firstHalfHours"));
					firstHalfObj.put("status", modelObj.getFirstHalfFinalStatus());
					firstHalfObj.put("startDay", dateFrmt.format(monthStartCal.getTime()));
					firstHalfObj.put("endDay", year + "-" + month + "-" + "15");
					periodsArray.add(firstHalfObj);
				}
				if (hoursObj.containsKey("secondHalfHours")) {
					secondHalfObj.put("hours", hoursObj.get("secondHalfHours"));
					secondHalfObj.put("status", modelObj.getSecondHalfFinalStatus());
					secondHalfObj.put("startDay", year + "-" + month + "-" + "16");
					secondHalfObj.put("endDay", dateFrmt.format(monthEndCal.getTime()));
					periodsArray.add(secondHalfObj);
				}
				projObj.put("periods", periodsArray);
				hoursProjectObj.remove(modelObj.getProject().getProjectId());
			}

			// non submitted projects
			for (Long projectId : hoursProjectObj.keySet()) {
				HashMap<String, Object> projObj = projectsMap.get(projectId);
				List<Map<String, Object>> periodsArray = new ArrayList<Map<String, Object>>();
				// if hoursProjectObj doesnt contain the project id- Error: submission table
				// contain data not in timetrack
				HashMap<String, Double> hoursObj = hoursProjectObj.get(projectId);

				Map<String, Object> firstHalfObj = new HashMap<String, Object>();
				Map<String, Object> secondHalfObj = new HashMap<String, Object>();

				if (hoursObj.containsKey("firstHalfHours")) {
					firstHalfObj.put("hours", hoursObj.get("firstHalfHours"));
					firstHalfObj.put("status", "OPEN");
					firstHalfObj.put("startDay", dateFrmt.format(monthStartCal.getTime()));
					firstHalfObj.put("endDay", year + "-" + month + "-" + "15");
					periodsArray.add(firstHalfObj);
				}
				if (hoursObj.containsKey("secondHalfHours")) {
					secondHalfObj.put("hours", hoursObj.get("secondHalfHours"));
					secondHalfObj.put("status", "OPEN");
					secondHalfObj.put("startDay", year + "-" + month + "-" + "16");
					secondHalfObj.put("endDay", dateFrmt.format(monthEndCal.getTime()));
					periodsArray.add(secondHalfObj);
				}
				projObj.put("periods", periodsArray);
			}

		}
		if (!workflow3Projects.isEmpty()) {
			// Fetch data from the weekly submission table
			List<TaskTrackWeeklyApproval> weeklyList = taskTrackWeeklyApprovalRepository
					.findByUserUserIdAndProjectProjectIdInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId,
							workflow3Projects, monthEndCal.getTime(), monthStartCal.getTime());

			HashMap<Long, HashMap<String, Object>> projWeeksMap = new HashMap<Long, HashMap<String, Object>>();
			HashMap<String, Object> weeksMap = new HashMap<String, Object>();

			for (Long projectId : workflow3Projects) {
				Calendar weekStartDay = (Calendar) firstWeekStartDay.clone();
				Calendar weekEndDay = (Calendar) weekStartDay.clone();
				weekEndDay.add(Calendar.DATE, 6);
				HashMap<String, Object> projObj = projectsMap.get(projectId);
				Date allocStartDate = (Date) projObj.get("allocStartDate");
				Date allocEndDate = (Date) projObj.get("allocEndDate");

				// Looping through the projects and adding the actual weeks according to the
				// allocation
				while (weekStartDay.getTime().before(lastWeekEndDay.getTime())) {
					if (!allocStartDate.after(weekEndDay.getTime()) && !allocEndDate.before(weekStartDay.getTime())) {
						if (!projWeeksMap.containsKey(projectId)) {
							weeksMap = new HashMap<String, Object>();
							projWeeksMap.put(projectId, weeksMap);
						} else {
							weeksMap = projWeeksMap.get(projectId);
						}
						weeksMap.put(dateFrmt.format(weekStartDay.getTime()), dateFrmt.format(weekEndDay.getTime()));
					}
					weekStartDay.add(Calendar.DATE, 7);
					weekEndDay.add(Calendar.DATE, 7);
				}
				projObj.remove("allocStartDate");
				projObj.remove("allocEndDate");
			}

			for (TaskTrackWeeklyApproval modelObj : weeklyList) {
				HashMap<String, Object> projObj = projectsMap.get(modelObj.getProject().getProjectId());
				Double hours = modelObj.getDay1() + modelObj.getDay2() + modelObj.getDay3() + modelObj.getDay4()
						+ modelObj.getDay5() + modelObj.getDay6() + modelObj.getDay7();
				String status = modelObj.getTimetrackFinalStatus();

				List<Map<String, Object>> periodsArray = (List<Map<String, Object>>) projObj.get("periods");

//				Date allocStartDate = (Date) projObj.get("allocStartDate");
//				Date allocEndDate = (Date) projObj.get("allocEndDate");
				weeksMap = projWeeksMap.get(modelObj.getProject().getProjectId());
				if (weeksMap.containsKey(dateFrmt.format(modelObj.getStartDate()))) {
					Map<String, Object> weekObj = new HashMap<String, Object>();
					weekObj.put("hours", hours);
					weekObj.put("status", status == null ? "OPEN" : status.toUpperCase());
					weekObj.put("startDay", dateFrmt.format(modelObj.getStartDate()));
					weekObj.put("endDay", dateFrmt.format(modelObj.getEndDate()));
					periodsArray.add(weekObj);
					projObj.put("periods", periodsArray);
					weeksMap.remove(dateFrmt.format(modelObj.getStartDate()));
				}
				if (weeksMap.isEmpty()) {
					projWeeksMap.remove(modelObj.getProject().getProjectId());
				}
			}
			for (Long projectId : projWeeksMap.keySet()) {
				HashMap<String, Object> projObj = projectsMap.get(projectId);
				List<Map<String, Object>> periodsArray = (List<Map<String, Object>>) projObj.get("periods");
				weeksMap = projWeeksMap.get(projectId);
				for (String startDay : weeksMap.keySet()) {
					Map<String, Object> weekObj = new HashMap<String, Object>();
					weekObj.put("hours", 0);
					weekObj.put("status", "OPEN");
					weekObj.put("startDay", startDay);
					weekObj.put("endDay", weeksMap.get(startDay));
					periodsArray.add(weekObj);
					projObj.put("periods", periodsArray);
				}
			}

		}
		if (!workflow4Projects.isEmpty()) {
			// Fetch data from the timetrack table and status from the submission
			// table

			List<Tasktrack> tasktrackList = tasktrackRepository.findByUserUserIdAndProjectProjectIdInAndDateBetween(
					userId, workflow4Projects, firstWeekStartDay.getTime(), lastWeekEndDay.getTime());

			List<TaskTrackWeeklyApproval> weeklyList = taskTrackWeeklyApprovalRepository
					.findByUserUserIdAndProjectProjectIdInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId,
							workflow4Projects, lastWeekEndDay.getTime(), firstWeekStartDay.getTime());

			Map<Long, HashMap<String, Double>> hoursProjectObj = new HashMap<Long, HashMap<String, Double>>();

			for (Long projectId : workflow4Projects) {
				Calendar weekStartDay = (Calendar) firstWeekStartDay.clone();
				Calendar weekEndDay = (Calendar) weekStartDay.clone();
				weekEndDay.add(Calendar.DATE, 6);

				HashMap<String, Object> projObj = projectsMap.get(projectId);

				Date allocStartDate = (Date) projObj.get("allocStartDate");
				Date allocEndDate = (Date) projObj.get("allocEndDate");
				HashMap<String, Double> hoursObj = new HashMap<String, Double>();
				for (int idx = 0; idx < 7; idx++) {
					if (!allocStartDate.after(weekEndDay.getTime()) && !allocEndDate.before(weekStartDay.getTime())) {
						hoursObj.put("week" + idx + "hours", 0d);
					}
					weekStartDay.add(Calendar.DATE, 7);
					weekEndDay.add(Calendar.DATE, 7);
				}
				hoursProjectObj.put(projectId, hoursObj);
				projObj.remove("allocStartDate");
				projObj.remove("allocEndDate");
			}

			for (Tasktrack modelObj : tasktrackList) {

				HashMap<String, Double> hoursObj = new HashMap<String, Double>();
				if (hoursProjectObj.containsKey(modelObj.getProject().getProjectId())) {
					hoursObj = hoursProjectObj.get(modelObj.getProject().getProjectId());
				} else {
					throw new Exception("Internal error occurred.");
				}
				int idx = 1;
				Calendar weekStartDay = (Calendar) firstWeekStartDay.clone();
				Calendar weekEndDay = (Calendar) weekStartDay.clone();
				weekEndDay.add(Calendar.DATE, 6);
				while (weekStartDay.getTime().before(lastWeekEndDay.getTime())) {
					if (!modelObj.getDate().before(weekStartDay.getTime())
							&& !modelObj.getDate().after(weekEndDay.getTime())) {
						hoursObj.put("week" + idx + "hours",
								hoursObj.get("week" + (idx) + "hours") + modelObj.getHours());
					}
					idx++;
					weekStartDay.add(Calendar.DATE, 7);
					weekEndDay.add(Calendar.DATE, 7);
				}
			}

			// submitted projects
			for (TaskTrackWeeklyApproval modelObj : weeklyList) {

				Calendar weekStartDay = (Calendar) firstWeekStartDay.clone();
				Calendar weekEndDay = (Calendar) weekStartDay.clone();
				weekEndDay.add(Calendar.DATE, 6);

				HashMap<String, Object> projObj = projectsMap.get(modelObj.getProject().getProjectId());

				List<Map<String, Object>> periodsArray = (List<Map<String, Object>>) projObj.get("periods");

				// if hoursProjectObj doesnt contain the project id- Error: submission table
				// contain data not in timetrack
				HashMap<String, Double> hoursObj = hoursProjectObj.get(modelObj.getProject().getProjectId());

				String status = modelObj.getTimetrackFinalStatus();

				int idx = 1;
				while (weekStartDay.getTime().before(lastWeekEndDay.getTime())) {
					if (modelObj.getStartDate().equals(weekStartDay.getTime())) {
						if (hoursObj.containsKey(("week" + idx + "hours"))) {
							Map<String, Object> weekObj = new HashMap<String, Object>();
							weekObj.put("hours", hoursObj.get("week" + idx + "hours"));
							weekObj.put("status", status == null ? "OPEN" : status.toUpperCase());
							weekObj.put("startDay", dateFrmt.format(weekStartDay.getTime()));
							weekObj.put("endDay", dateFrmt.format(weekEndDay.getTime()));
							periodsArray.add(weekObj);
							projObj.put("periods", periodsArray);
							hoursObj.remove("week" + (idx) + "hours");
						}
					}
					weekStartDay.add(Calendar.DATE, 7);
					weekEndDay.add(Calendar.DATE, 7);
					if (hoursProjectObj.get(modelObj.getProject().getProjectId()).isEmpty()) {
						hoursProjectObj.remove(modelObj.getProject().getProjectId());
					}
					idx++;
				}

			}

			// non submitted projects
			for (Long projectId : hoursProjectObj.keySet()) {

				Calendar weekStartDay = (Calendar) firstWeekStartDay.clone();
				Calendar weekEndDay = (Calendar) weekStartDay.clone();
				weekEndDay.add(Calendar.DATE, 6);

				HashMap<String, Object> projObj = projectsMap.get(projectId);
				List<Map<String, Object>> periodsArray = (List<Map<String, Object>>) projObj.get("periods");
				// if hoursProjectObj doesnt contain the project id- Error: submission table
				// contain data not in timetrack
				HashMap<String, Double> hoursObj = hoursProjectObj.get(projectId);

				int idx = 1;
				while (weekStartDay.getTime().before(lastWeekEndDay.getTime())) {
					Map<String, Object> weekObj = new HashMap<String, Object>();
					weekObj.put("hours", hoursObj.get("week" + idx + "hours"));
					weekObj.put("status", "OPEN");
					weekObj.put("startDay", dateFrmt.format(weekStartDay.getTime()));
					weekObj.put("endDay", dateFrmt.format(weekEndDay.getTime()));
					periodsArray.add(weekObj);
					projObj.remove("allocStartDate");
					projObj.remove("allocEndDate");
					projObj.put("periods", periodsArray);
					weekStartDay.add(Calendar.DATE, 7);
					weekEndDay.add(Calendar.DATE, 7);
					idx++;
				}
			}

		}

		Collection<HashMap<String, Object>> resultCollection = projectsMap.values();
		result = new ArrayList<>(resultCollection);
		StatusResponse response;
		if (!result.isEmpty()) {
			response = new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, result);
		} else {
			response = new StatusResponse(Constants.FAILURE, Constants.ERROR_CODE, "No data available");
		}
		return response;
	}

}