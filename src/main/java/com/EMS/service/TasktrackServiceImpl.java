package com.EMS.service;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.EMS.model.*;
import com.EMS.repository.*;
import com.EMS.utility.Constants;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
//	For Task track Model
	@Autowired
	TaskTrackCorrectionRepository taskTrackCorrectionRepository;

	@Override
	public List<Tasktrack> getByDate(Date startDate, Date endDate, Long uId) {
		List<Tasktrack> list = tasktrackRepository.getByDate(startDate, endDate, uId);

		LocalDate localStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate localEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		for (LocalDate date = localStartDate; date.isBefore(localEndDate)
				| date.isEqual(localEndDate); date = date.plusDays(1)) {
			LocalDate locaDate = date;
			Tasktrack obj = list.stream()
					.filter(taskTrack -> locaDate
							.equals(taskTrack.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))
					.findAny().orElse(null);
			if (obj == null) {
				Tasktrack tasktrack = new Tasktrack();
				tasktrack.setDate(Date.from(locaDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
				list.add(tasktrack);
			}
		}

		Collections.sort(list);

		return list;

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

	public boolean deleteTaskById(long id) {
		boolean result = false;
		try {
			tasktrackRepository.deleteTaskById(id);
			result = true;
		} catch (Exception exc) {
			exc.printStackTrace();
		}

		return result;
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
						name = (String) item[0] + " " + item[1];
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

	// bala

	@Override
	public Task getTaskByName(String taskName) {
		// TODO Auto-generated method stub
		return taskRepository.getTaskByName(taskName);
	}

	public boolean updateTaskByName(Tasktrack task) {
		boolean result = false;
		try {
			tasktrackRepository.updateTaskByName(task.getId(), task.getHours(), task.getProject(), task.getTask());
			result = true;
		} catch (Exception exc) {
			exc.printStackTrace();
		}

		return result;
	}

	@Override
	public List<Object[]> getTasksForTimeTrack(Long userId, Date fromDate, Date toDate) {
		List<Object[]> ls = null;
		try {
			ls = tasktrackRepository.getTasksFortimeTrack(userId, fromDate, toDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ls;

	}
	// bala

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
			JsonNode node1 = days.get(0);
			String inputDate1 = node1.asText();
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
				if(taskTrackCorrections.size() > 0) {
					for (TaskTrackCorrection correction : taskTrackCorrections) {
						correction.setStatus(Constants.TASKTRACK_APPROVER_STATUS_CORRECTION);
					}
					taskTrackCorrectionRepository.saveAll(taskTrackCorrections);
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
						taskTrackCorrection.setType(isRecorrection ? Constants.TASKTRACK_CORRECTION_TYPE_RECORRECTION : Constants.TASKTRACK_CORRECTION_TYPE_CORRECTION);
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

}
