package com.EMS.service;

import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;

import com.EMS.model.TaskTrackApproval;
import com.EMS.model.TaskTrackApprovalFinal;

public interface AuditService {


	List<JSONObject> getAuditByUserIdForFinal(Long projectId,Long userId, Date fromDate, Date toDate);

	List<JSONObject> getAuditByUserId(Long projectId, Long userId, Date fromDate, Date toDate);
	
	List<JSONObject> getAuditUserDetailsById(Long userId);

	List<JSONObject> getAuditUserDetailsByDateRange(Long userId, Date fromDate, Date toDate);
	
	List<JSONObject> getProjectAuditDataByProjectId(Long projectId);

	List<JSONObject> getProjectAuditDataByProjectIdAndDateRange(Long projectId, Date startDate, Date endDate);

}
