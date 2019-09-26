package com.EMS.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.EMS.model.AllocationModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.Task;
import com.EMS.model.TaskTrackApproval;
import com.EMS.model.TaskTrackApprovalFinance;
import com.EMS.model.TaskTrackApprovalLevel2;
import com.EMS.model.Tasktrack;
import com.fasterxml.jackson.databind.node.ArrayNode;

public interface TasktrackApprovalService {

	Boolean checkIsUserExists(Long id);
	
	List<JSONObject> getTimeTrackUserTaskDetails(Long id, Date startDate, Date endDate, List<Object[]> userList,
			List<JSONObject> loggedJsonArray,List<JSONObject> billableJsonArray,List<JSONObject> timeTrackJSONData, Boolean isExist,Long projectId);
	List<JSONObject> getTimeTrackUserTaskDetailsByProject(Long id, Date startDate, Date endDate, List<Object[]> userList,
												 List<JSONObject> loggedJsonArray,List<JSONObject> billableJsonArray,List<JSONObject> nonBillableJsonArray,List<JSONObject> timeTrackJSONData, Boolean isExist,Long projectId);
	List<JSONObject> getTimeTrackUserProjectTaskDetails(Long projectId,String projectName, Date startDate, Date endDate, List<Object[]> projectList,
												 List<JSONObject> loggedJsonArray,List<JSONObject> billableJsonArray,List<JSONObject> nonBillableJsonArray,List<JSONObject> timeTrackJSONData, Boolean isExist,Long userId);
	
	JSONObject getApprovedUserTaskDetails(Long id, Date startDate, Date endDate, List<TaskTrackApproval> userList,
			List<JSONObject> jsonArray, List<JSONObject> jsonDataRes1, Boolean isExist,Long projectId);
	
	TaskTrackApproval findById(Long billableId);
	
	public TaskTrackApproval updateData(TaskTrackApproval taskTrackApproval);
	
	public void save(TaskTrackApproval taskTrackApproval);

	TaskTrackApprovalLevel2 findById2(Long billableId);

	public TaskTrackApprovalLevel2 updateDatas(TaskTrackApprovalLevel2 taskTrackApproval);

	public TaskTrackApprovalLevel2 saveLevel2(TaskTrackApprovalLevel2 taskTrackApproval);

	JSONObject getApproveddatalevel2(Long userId, Date startDate, Date endDate, List<TaskTrackApproval> userList,
			List<JSONObject> jsonArray, List<JSONObject> approvalJSONData, Boolean isExist, Long projectId);


	void saveLevel3(TaskTrackApprovalFinance taskTrackApproval);

	JSONObject getApproveddatalevel2toFinance(Long userId, Date startDate, Date endDate, Long projectId);

	JSONObject getApproveddatalevel1toFinance(Long userId, Date startDate, Date endDate, Long projectId);

	List<JSONObject> getTimeTrackUserTaskDetailsLevel2(Long id, Date startDate, Date endDate, List<Object[]> userList,
			List<JSONObject> loggedJsonArray, List<JSONObject> billableJsonArray, List<JSONObject> timeTrackJSONData,
			Boolean isExist, Long projectId);

	List<Object> getForwardedDate(Long projectId, Long userId, int intMonth,int year);

	List<Object> getForwardedDateLevel2(Long projectId, Long userId, int intMonth,int year);


	/*
	 * JSONObject getApprovedUserTaskDetailsForLevel2(Long userId, Date startDate,
	 * Date endDate, List<TaskTrackApproval> userList, List<JSONObject> jsonArray,
	 * List<JSONObject> approvalJSONData, Boolean isExist, Long projectId);
	 */
	
}

