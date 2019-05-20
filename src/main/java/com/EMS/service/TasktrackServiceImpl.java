package com.EMS.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.EMS.model.AllocationModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.Task;
import com.EMS.model.TaskTrackApproval;
import com.EMS.model.Tasktrack;
import com.EMS.repository.ProjectReportsRepository;
import com.EMS.repository.TaskRepository;
import com.EMS.repository.TasktrackRepository;
import com.EMS.repository.TimeTrackApprovalRepository;
import com.EMS.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
public class TasktrackServiceImpl implements TasktrackService {

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
	ProjectReportsRepository projectReportsRepository;

	@Autowired
	TimeTrackApprovalRepository timeTrackApprovalRepository;
//	For Task track Model

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

		List<Object[]> userList = taskRepository.getUserList(userId,startDate,endDate);
		
		return userList;
	}

	@Override
	public List<Object[]> getUserListByProjectId(Long projectId, Date startDate, Date endDate) {
		List<Object[]> userList = taskRepository.getUserListByProjectId(projectId,startDate,endDate);
		return userList;
	}

	@Override
	public List<Object[]> getUserListByDate(Date startDate, Date endDate) {
		List<Object[]> userList = taskRepository.getUserListByDate(startDate,endDate);
		return userList;
	}

	@Override
	public List<Object[]> getUserListNew(Long userId, Date startDate, Date endDate, Long pageSize, Long startingIndex) {
		List<Object[]> userList = taskRepository.getUserListNew(userId,startDate,endDate,pageSize,startingIndex);
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
		Boolean exist = tasktrackRepository.checkExistanceOfUser(projectId,userId);
		return exist;
	}

	@Override
	public List<JSONObject> getUserTaskDetails(Long id, Date startDate, Date endDate, List<Object[]> userList,
			List<JSONObject> jsonArray, List<JSONObject> jsonDataRes1, Boolean isExist, Long projectId) {
		if (isExist) {
			JSONObject userListObject = new JSONObject();

			JSONObject userObject = new JSONObject();
            if(projectId == null) {
    			userList =getUserList(id, startDate, endDate);
            }
            else
    			userList =getUserListByProject(id, startDate, endDate,projectId);

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

	private List<Object[]> getUserListByProject(Long id, Date startDate, Date endDate, Long projectId) {
		List<Object[]> userTaskList = taskRepository.getUserListByProject(id,startDate,endDate,projectId);
		return userTaskList;
	}

	@Override
	public List<Object[]> getUserTaskList(Long id, Date startDate, Date endDate, Long projectId) {
		List<Object[]> userTaskList = taskRepository.getUserTaskList(id,startDate,endDate,projectId);
		return userTaskList;
	}
}
