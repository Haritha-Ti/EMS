package com.EMS.service;

import java.util.Date;
import java.util.List;

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

}
