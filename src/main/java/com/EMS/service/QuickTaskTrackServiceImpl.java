package com.EMS.service;

import static org.hamcrest.CoreMatchers.nullValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.EMS.model.*;
import com.EMS.repository.*;
import com.EMS.utility.Constants;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuickTaskTrackServiceImpl implements QuickTaskTrackService {

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	TasktrackRepository tasktrackRepository;

	@Autowired
	ProjectAllocationService projectAllocationService;

	@Override
	public ProjectModel getProjectModelById(long projectId) {
		return tasktrackRepository.getProjectById(projectId);
	}

	@Override
	public Tasktrack saveTaskDetails(Tasktrack task) {
		return tasktrackRepository.save(task);
	}

	// bala
	@Override
	public Task getTaskByName(String taskName) {
		return taskRepository.getTaskByName(taskName);
	}

	// bala
	@Override
	public Boolean updateTaskByName(Tasktrack task) {
		boolean result = false;
		try {
			tasktrackRepository.updateTaskByName(task.getId(), task.getHours(), task.getProject(), task.getTask());
			result = true;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getQuickTimeTrack(JsonNode request) throws Exception {
		JSONObject response = new JSONObject();

		Long userId = null;
		Date fromDate = null;
		Date toDate = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (request.get("userId") != null && request.get("userId").asText() != "") {
			userId = request.get("userId").asLong();
		}
		if (request.get("fromDate") != null && request.get("fromDate").asText() != null) {
			fromDate = sdf.parse(request.get("fromDate").asText());
		}
		if (request.get("toDate") != null && request.get("toDate").asText() != null) {
			toDate = sdf.parse(request.get("toDate").asText());
		}

		Calendar calender = Calendar.getInstance();
		calender.setTime(fromDate);
		int month = (calender.get(Calendar.MONTH) + 1);
		int year = calender.get(Calendar.YEAR);

		calender.setTime(toDate);
		int lastDay = calender.get(Calendar.DATE);

		List<Object[]> projectAllocList = projectAllocationService.getAllocationProjectForUserId(userId, fromDate,
				toDate);
		Map<Long, List<String>> projectClientMap = new HashMap<Long, List<String>>();
		Map<Long, List<String>> projectDateMap = new HashMap<Long, List<String>>();

		if (projectAllocList != null) {
			for (Object[] allocation : projectAllocList) {
				Long projectId = Long.valueOf(allocation[0].toString());
				String projectName = allocation[1].toString();
				String clientName = allocation[2] == null ? "" : allocation[2].toString();
				List<String> projectDateList = new ArrayList<String>();

				projectClientMap.put(projectId, Arrays.asList(projectName, clientName));

				Date projectStart = (Date) allocation[3];
				Date projectEnd = (Date) allocation[4];

				Calendar startDate = Calendar.getInstance();
				Calendar endDate = Calendar.getInstance();

				if (projectStart.before(fromDate)) {
					startDate.setTime(fromDate);
				} else {
					startDate.setTime(projectStart);
				}
				if (projectEnd.before(toDate)) {
					endDate.setTime(projectEnd);
				} else {
					endDate.setTime(toDate);
				}

				while (startDate.before(endDate) || startDate.equals(endDate)) {
					Date result = startDate.getTime();
					String date = sdf.format(result);
					projectDateList.add(date);
					startDate.add(Calendar.DATE, 1);
				}
				projectDateMap.put(projectId, projectDateList);
			}
		}
		boolean containsBeach = false;

		Map<Long, JSONObject> responseMap = new HashMap<Long, JSONObject>();
		Map<Long, Map<String, JSONObject>> taskListMap = new HashMap<Long, Map<String, JSONObject>>();

		List<Object[]> taskTrackList = tasktrackRepository.getTasksFortimeTrack(userId, fromDate, toDate);
		if (taskTrackList != null && taskTrackList.size() > 0) {
			for (Object[] taskTrack : taskTrackList) {
				Long projectId = Long.valueOf(taskTrack[0].toString());
				if(projectId == Constants.BEACH_PROJECT_ID) {
					containsBeach = true;
				}
				String taskTrackDate = taskTrack[2].toString();
				Map<String, JSONObject> taskDetailsMap = new HashMap<String, JSONObject>();
				if (projectClientMap.containsKey(projectId)) {
					projectClientMap.remove(projectId);
				}

				JSONObject projectObject = new JSONObject();
				if (responseMap.containsKey(projectId)) {
					taskDetailsMap = taskListMap.get(projectId);
					JSONObject task = new JSONObject();
					task.put("date", taskTrackDate);
					task.put("qTrackId", taskTrack[3]);
					task.put("hour", taskTrack[5]);
					task.put("enable", checkDateEnabled(projectDateMap, projectId, taskTrackDate));
					taskDetailsMap.put(taskTrackDate, task);
				} else {
					projectObject.put("projectId", taskTrack[0]);
					projectObject.put("projectName", taskTrack[1]);
					projectObject.put("clientName", taskTrack[4]);

					JSONObject task = new JSONObject();
					task.put("date", taskTrackDate);
					task.put("qTrackId", taskTrack[3]);
					task.put("hour", taskTrack[5]);
					task.put("enable", checkDateEnabled(projectDateMap, projectId, taskTrackDate));

					taskDetailsMap.put(taskTrackDate, task);
					taskListMap.put(projectId, taskDetailsMap);

					responseMap.put(projectId, projectObject);
				}
			}
		}

		if (projectClientMap.size() > 0) {
			for (Map.Entry<Long, List<String>> map : projectClientMap.entrySet()) {
				JSONObject projectObject = new JSONObject();
				projectObject.put("projectId", map.getKey());
				projectObject.put("projectName", map.getValue().get(0));
				projectObject.put("clientName", map.getValue().get(1));

				taskListMap.put(map.getKey(), new HashMap<String, JSONObject>());
				responseMap.put(map.getKey(), projectObject);
			}
		}

		List<JSONObject> projectList = new ArrayList<JSONObject>();
		for (Map.Entry<Long, JSONObject> map : responseMap.entrySet()) {
			List<JSONObject> taskList = new ArrayList<JSONObject>();
			JSONObject projectObject = map.getValue();
			Map<String, JSONObject> taskMap = taskListMap.get(map.getKey());
			for (int day = 1; day <= lastDay; day++) {
				String taskTrackDate = year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day;
				if (!taskMap.containsKey(taskTrackDate)) {
					JSONObject task = new JSONObject();
					task.put("date", taskTrackDate);
					task.put("qTrackId", null);
					task.put("hour", 0.0);
					task.put("enable", checkDateEnabled(projectDateMap, map.getKey(), taskTrackDate));
					taskList.add(task);
				} else {
					taskList.add(taskMap.get(taskTrackDate));
				}
			}
			projectObject.put("taskDetails", taskList);
			projectList.add(projectObject);
		}

		if (!containsBeach) {
			projectList.add(getBeachQuickTimeTrack(userId, fromDate, toDate));
		}

		JSONObject projectResponse = new JSONObject();
		projectResponse.put("projectList", projectList);
		response.put("data", projectResponse);
		response.put("status", "success");

		return response;
	}

	private Boolean checkDateEnabled(Map<Long, List<String>> projectDateMap, Long projectId, String date) {
		List<String> projectDateList = projectDateMap.get(projectId);
		if (projectDateList != null && projectDateList.contains(date)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getBeachQuickTimeTrack(Long userId, Date fromDate, Date toDate) throws Exception {

		long projectId = Constants.BEACH_PROJECT_ID;

		Calendar calender = Calendar.getInstance();
		calender.setTime(fromDate);
		int month = (calender.get(Calendar.MONTH) + 1);
		int year = calender.get(Calendar.YEAR);

		calender.setTime(toDate);
		int lastDay = calender.get(Calendar.DATE);

		Map<String, JSONObject> taskDetailsMap = new HashMap<String, JSONObject>();

		List<Object[]> beachTasksFortimeTrack = tasktrackRepository.getBeachTasksFortimeTrack(userId, fromDate, toDate);

		for (Object[] beachTask : beachTasksFortimeTrack) {
			String taskTrackDate = beachTask[0].toString();
			JSONObject task = new JSONObject();
			task.put("date", taskTrackDate);
			task.put("qTrackId", beachTask[1]);
			task.put("hour", beachTask[2]);
			task.put("enable", true);
			taskDetailsMap.put(taskTrackDate, task);

		}

		List<JSONObject> taskList = new ArrayList<JSONObject>();

		for (int day = 1; day <= lastDay; day++) {
			String taskTrackDate = year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day;
			if (!taskDetailsMap.containsKey(taskTrackDate)) {
				JSONObject task = new JSONObject();
				task.put("date", taskTrackDate);
				task.put("qTrackId", null);
				task.put("hour", 0.0);
				task.put("enable", true);
				taskList.add(task);
			} else {
				taskList.add(taskDetailsMap.get(taskTrackDate));
			}
		}
		ProjectModel projectModel = tasktrackRepository.getProjectById(projectId);

		JSONObject projectObject = new JSONObject();
		projectObject.put("projectId", projectId);
		projectObject.put("projectName", projectModel.getProjectName());
		projectObject.put("clientName",
				projectModel.getClientName() == null ? "" : projectModel.getClientName().getClientName());
		projectObject.put("taskDetails", taskList);
		System.out.println("projectObject : " + projectObject);
		return projectObject;

	}

}
