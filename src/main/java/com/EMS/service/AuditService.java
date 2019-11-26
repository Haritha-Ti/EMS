package com.EMS.service;

import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.simple.JSONObject;

import com.EMS.model.TaskTrackApproval;
import com.EMS.model.TaskTrackApprovalFinal;

public interface AuditService {


	JSONObject getAuditByUserIdForFinal(Long projectId,Long userId, Date fromDate, Date toDate);

	JSONObject getAuditByUserId(Long projectId, Long userId, Date fromDate, Date toDate);
	
	JSONObject getAuditUserDetailsById(Long userId);

	JSONObject getAuditUserDetailsByDateRange(Long userId, Date fromDate, Date toDate);
	
	JSONObject getProjectAuditDataByProjectId(Long projectId);

	JSONObject getProjectAuditDataByProjectIdAndDateRange(Long projectId, Date startDate, Date endDate);
	
	void getProjectAuditeReport(Long projectId, Date startDate, Date endDate,Workbook workbook,Sheet sheet);
	
	void getUserAuditDataReport(Long userId, Date fromDate, Date toDate,Workbook workbook,Sheet sheet);
	
	void getAuditDataReport(Long userId, Long projectId ,Date fromDate, Date toDate,Workbook workbook,Sheet sheet);
	
	void getAuditDataFinalReport(Long userId, Long projectId ,Date fromDate, Date toDate,Workbook workbook,Sheet sheet);

}
