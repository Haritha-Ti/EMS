package com.EMS.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.TaskTrackApproval;
import com.EMS.model.TaskTrackApprovalFinal;
import com.EMS.repository.AuditRepository;

@Service
public class AuditServiceImpl implements AuditService {
@Autowired
AuditRepository auditRepository;
@Autowired
ProjectAllocationService projectAllocationService;
	@Override
	public List<JSONObject> getAuditByUserId(Long projectId,Long userId, Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
	
		List<JSONObject> taskTrackApproval=new ArrayList<JSONObject>();
		taskTrackApproval=auditRepository.getAuditDataByUserId(projectId,userId,fromDate,toDate);
	
		return taskTrackApproval;
		
	}
	@Override
	public List<JSONObject> getAuditByUserIdForFinal(Long projectId,Long userId, Date fromDate, Date toDate) {
		
		List<JSONObject> taskTrackApprovalFinal=new ArrayList<JSONObject>();
		taskTrackApprovalFinal=auditRepository.getAuditDataByUserIdForFinal(projectId,userId,fromDate,toDate);
		return taskTrackApprovalFinal;
	}
	
	@Override
	public List<JSONObject> getAuditUserDetailsById(Long userId) {
		// TODO Auto-generated method stub
		return auditRepository.getAuditUserDetailsById(userId);
	}

	@Override
	public List<JSONObject> getAuditUserDetailsByDateRange(Long userId, Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		return auditRepository.getAuditUserDetailsByDateRange(userId,fromDate,toDate);
	}
	
	public List<JSONObject> getProjectAuditDataByProjectId(Long projectId) {
		List<JSONObject> projectAuditData = auditRepository.getProjectAuditDataByProjectId(projectId);
		return projectAuditData;
	}

	public List<JSONObject> getProjectAuditDataByProjectIdAndDateRange(Long projectId, Date startDate, Date endDate) {
		List<JSONObject> projectAuditData = auditRepository.getProjectAuditDataByProjectIdAndDateRange(projectId,
				startDate, endDate);
		return projectAuditData;
	}

}
