package com.EMS.service;

import java.util.List;

import org.json.simple.JSONObject;

import com.EMS.model.TaskTrackApprovalFinal;

public interface TaskTrackFinalService {

	Boolean checkIsUserExists(Long id);

	TaskTrackApprovalFinal findById(Long billableId);

	public TaskTrackApprovalFinal updateData(TaskTrackApprovalFinal taskTrackApproval);

	public TaskTrackApprovalFinal save(TaskTrackApprovalFinal taskTrackApproval);

	List<Object> getForwardedDate(Long projectId, Long userId, int intMonth, int year);

	List<Object> getForwardedDateLevel2(Long projectId, Long userId, int intMonth, int year);

	List<Object[]> getForwardedDates(Long projectId, Long userId, int intMonth, int yearIndex);

	void saveFinalHours(JSONObject requestData) throws Exception;
	
	void submitFirstHalfHoursAsFinal(JSONObject requestData) throws Exception;
	
	void submitSecondHalfHoursAsFinal(JSONObject requestData) throws Exception;

	void submitFirstHalfHoursForApproval2(JSONObject requestData) throws Exception;

	void submitSecondHalfHoursForApproval2(JSONObject requestData) throws Exception;

}

