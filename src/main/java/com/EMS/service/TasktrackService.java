package com.EMS.service;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.EMS.model.AllocationModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.Task;
import com.EMS.model.TaskTrackApproval;
import com.EMS.model.TaskTrackDaySubmissionModel;
import com.EMS.model.Tasktrack;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface TasktrackService {

//	For Task track Model	
	List<Tasktrack> getByDate(Date startDate, Date toDate, Long uId);

	Tasktrack saveTaskDetails(Tasktrack task);

//	For Task Model
	List<Object[]> getTaskList();

	Task getTaskById(Long taskId);

	public List<Tasktrack> getTasks();

	List<Object[]> getUserList(Long userId, Date startDate, Date endDate);

	List<Object[]> getUserListByProjectId(Long projectId, Date startDate, Date endDate);

	List<Object[]> getUserListByDate(Date startDate, Date endDate);

	List<Object[]> getUserListNew(Long userId, Date startDate, Date endDate, Long pageSize, Long startingIndex);

	Boolean checkIsUserExists(Long id);

	Object getUserName(Long id);

	Boolean checkExistanceOfUser(Long projectId, Long userId);

	List<JSONObject> getUserTaskDetails(Long id, Date startDate, Date endDate, List<Object[]> userList,
			List<JSONObject> jsonArray, List<JSONObject> jsonDataRes1, Boolean isExist, Long projectId);

	List<JSONObject> getUserTaskDetailsByuser(Long id, Date startDate, Date endDate, List<Object[]> userList,
			List<JSONObject> jsonArray, List<JSONObject> jsonDataRes1, Boolean isExist, Long projectId,
			String projectName);

	List<Object[]> getProjectListByUserAndDate(Long id, Date startDate, Date endDate);

	List<Object[]> getUserTaskList(Long id, Date startDate, Date endDate, Long projectId);

	ObjectNode checkApproveLevel(Long project_Id, Long logUser);

	List<TaskTrackDaySubmissionModel> saveSubmissionDays(List<TaskTrackDaySubmissionModel> taskTrackDaySubmissionList);

	TaskTrackDaySubmissionModel getSubmissionDayByMonth(int month);

	Task getTaskByName(String taskName);

	List<Object[]> getTasksForTimeTrack(Long userId, Date fromDate, Date toDate);

	// nisha
	ObjectNode createCorrection(ObjectNode requestdata, Boolean isReCorrection);
}
