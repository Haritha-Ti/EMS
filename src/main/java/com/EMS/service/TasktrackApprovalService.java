package com.EMS.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;

import com.EMS.model.TaskTrackApproval;
import com.EMS.model.TaskTrackApprovalFinance;
import com.EMS.model.TaskTrackApprovalLevel2;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface TasktrackApprovalService {

	Boolean checkIsUserExists(Long id);
	
	Boolean checkIsUserExistsInApproval(Long id);

	JSONObject getTimeTrackUserTaskDetails(Long userId, Date startDate, Date endDate,Boolean isExist,Long projectId, Integer projectTier,Integer firstHalfDay) throws ParseException;

	List<JSONObject> getTimeTrackUserTaskDetailsByProject(Long id, Date startDate, Date endDate,
			List<Object[]> userList, List<JSONObject> loggedJsonArray, List<JSONObject> billableJsonArray,
			List<JSONObject> nonBillableJsonArray, List<JSONObject> timeTrackJSONData, Boolean isExist, Long projectId);

	List<JSONObject> getTimeTrackUserProjectTaskDetails(Long projectId, String projectName, Date startDate,
			Date endDate, List<Object[]> projectList, List<JSONObject> loggedJsonArray,
			List<JSONObject> billableJsonArray, List<JSONObject> nonBillableJsonArray,
			List<JSONObject> timeTrackJSONData, Boolean isExist, Long userId);

	JSONObject getApprovedUserTaskDetails(Long id, Date startDate, Date endDate, Boolean isExist,Long projectId,Integer projectTier, Integer firstHalfDay);

	TaskTrackApproval findById(Long billableId);

	public TaskTrackApproval updateData(TaskTrackApproval taskTrackApproval);

	public TaskTrackApproval save(TaskTrackApproval taskTrackApproval);

	TaskTrackApprovalLevel2 findById2(Long billableId);

	public TaskTrackApprovalLevel2 updateDatas(TaskTrackApprovalLevel2 taskTrackApproval);

	public TaskTrackApprovalLevel2 saveLevel2(TaskTrackApprovalLevel2 taskTrackApproval);

	JSONObject getApproveddatalevel2(Long userId, Date startDate, Date endDate, List<TaskTrackApproval> userList,
			List<JSONObject> jsonArray, List<JSONObject> approvalJSONData, Boolean isExist, Long projectId);

	void saveLevel3(TaskTrackApprovalFinance taskTrackApproval);

	JSONObject getApproveddatalevel2toFinance(Long userId, Long logUser, int mothIndex, int yearIndex, Long projectId);

	JSONObject getApproveddatalevel1toFinance(Long userId, Long logUser, int monthIndex, int yearIndex, Long projectId);

	List<JSONObject> getTimeTrackUserTaskDetailsLevel2(Long id, Date startDate, Date endDate, List<Object[]> userList,
			List<JSONObject> loggedJsonArray, List<JSONObject> billableJsonArray, List<JSONObject> timeTrackJSONData,
			Boolean isExist, Long projectId);

	List<Object> getForwardedDate(Long projectId, Long userId, int intMonth, int year);

	List<Object> getForwardedDateLevel2(Long projectId, Long userId, int intMonth, int year);

	ObjectNode saveLevel2FromLevel1(Long projectId, Long userId, Long logUser, Date startDate, Date endDate);

	List<Object[]> getForwardedDates(Long projectId, Long userId, int intMonth, int yearIndex);

	List<TaskTrackApprovalLevel2> getUserIdByProjectAndDateForLevel2(Long projectId, Date startDate, Date endDate);

	/*
	 * JSONObject getApprovedUserTaskDetailsForLevel2(Long userId, Date startDate,
	 * Date endDate, List<TaskTrackApproval> userList, List<JSONObject> jsonArray,
	 * List<JSONObject> approvalJSONData, Boolean isExist, Long projectId);
	 */
	public ArrayList<JSONObject> getFinanceDataByProject(int month, int year, Long projectId);

	public ArrayList<JSONObject> getFinanceDataByUser(int month, int year, Long userId);

	public ArrayList<JSONObject> getFinanceDataByUserAndProject(int month, int year, Long userId, Long projectId);

	JSONObject checkPreviousTimeSheetsareClosed(int month, int year, Long projectId, Long userId);

	JSONObject halfCycleCheck(Long projectId, Long userId, Long approverId, Date endDate) throws ParseException;

	Object[] getFinanceStatusOfCurrentProject(Long projectId, Long userId, int intMonth, int yearIndex);

	ObjectNode reApproveDatasofLevel1(Long projectId, Long userId, int month, int year,
			HashMap<String, Object> billableArray, HashMap<String, Object> nonbillableArray,
			HashMap<String, Object> beachArray, HashMap<String, Object> overtimeArray, Long billableId,
			Long nonbillableId, Long overtimeId, Long beachId, Long logUser);

	ObjectNode reApproveDatasofLevel2(Long projectId, Long userId, Integer month, Integer year,
			HashMap<String, Object> billableArray, HashMap<String, Object> nonbillableArray,
			HashMap<String, Object> beachArray, HashMap<String, Object> overtimeArray, Long billableId,
			Long nonbillableId, Long overtimeId, Long beachId, Long logUser);

	ObjectNode mailRejectTimesheetDetailstoLevel1andClear(Long projectId, Long userId, Long month, Long year,
			String message);

	ArrayList<JSONObject> getFinanceDataByMonthAndYear(int month, int year);

	// Renjith
	List<TaskTrackApprovalLevel2> getNotApprovedData(int monthIndex, int yearIndex, Long projectId);

	List<TaskTrackApprovalLevel2> getHalfMonthApprovedData(int monthIndex, int yearIndex, Long projectId);

	public ArrayList<JSONObject> getFinanceDataByProjectSet(int month, int year, Set<Long> id);

	// Renjith
	List<TaskTrackApprovalLevel2> getMidMonthApprovedData(int monthIndex, int yearIndex, Long projectId);

	void saveApprovedHours(JSONObject requestData) throws Exception;

	void submitFirstHalfHoursForApproval(JSONObject requestData) throws Exception;

	void submitSecondHalfHoursForApproval(JSONObject requestData) throws Exception;

	void submitForRejection(JSONObject requestData) throws Exception;

	void submitForSecondHalfRejection(JSONObject requestData) throws Exception;

	JSONObject getInfoForApprovalLevelTwo(Long userId, Date startDate, Date endDate,Boolean isExist,Long projectId,Integer firstHalfDay) throws ParseException;

	JSONObject getDataForApprovalLevelTwo(Long userId, Date startDate, Date endDate, Long projectId,
			Integer firstHalfDay);

	JSONObject getDataForApprovalFinance(Long userId, Date startDate, Date endDate, Long projectId,
										 Integer firstHalfDay);


	JSONObject getInfoForFinance(Long userId, Date startDate, Date endDate, Boolean isExist, Long projectId, Integer firstHalfDay) throws ParseException;

	ArrayList<JSONObject> getProjectWiseSubmissionDetailsTier1(int month, int year, long projectId,long userId, long regionId);

	ArrayList<JSONObject> getProjectWiseSubmissionDetailsTier2(int month, int year, long projectId,long userId, long regionId);

	List<Object[]> getProjectWiseSubmissionDetailsTierOne(int month, int year, long projectId, long userId,
			long regionId);

	List<Object[]> getProjectWiseSubmissionDetailsTierTwo(int month, int year, long projectId, long userId,
			long regionId);

	ArrayList<JSONObject> getUserWiseSubmissionDetails(int month, int year, long projectId, long userId, long regionId);

	List<Object[]> getUserWiseSubmissionDetailsExport(int month, int year, long projectId, long userId, long regionId);
}
