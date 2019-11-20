package com.EMS.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.EMS.service.AuditService;
import com.EMS.service.ProjectAllocationService;
import com.EMS.service.ProjectService;
import com.EMS.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping(value="/audit")
public class AuditController {
@Autowired
private UserService userService;
@Autowired
private ProjectService projectService;
@Autowired
private ProjectAllocationService projectAllocationService;
@Autowired
private AuditService auditService;

@SuppressWarnings("unchecked")
@PostMapping(value="/getAuditDataForApproval")
public JSONObject getAuditData(@RequestBody JsonNode givenData) throws Exception{
	Long userId=null,projectId=null;
	Date fromDate=null,toDate=null;
	JSONObject node=new JSONObject();
	JSONObject nodeJson=new JSONObject();
	JSONObject taskTrackApproval=null;
	SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
	fromDate=simpleDateFormat.parse(givenData.get("fromDate").asText());
	if(givenData.get("userId")!=null && givenData.get("userId").asText()!="") {
	userId=givenData.get("userId").asLong();
	}
	if(givenData.get("projectId")!=null && givenData.get("projectId").asText()!="") {
	projectId=givenData.get("projectId").asLong();
	}
	if(givenData.get("fromDate")!=null && givenData.get("fromDate").asText()!="") {
	fromDate=simpleDateFormat.parse(givenData.get("fromDate").asText());
	}
	if(givenData.get("toDate")!=null && givenData.get("toDate").asText()!="") {
		toDate=simpleDateFormat.parse(givenData.get("toDate").asText());
	}
	try {
	taskTrackApproval=auditService.getAuditByUserId(projectId,userId,fromDate,toDate);
		}catch(Exception e) {
		
		node.put("status", "failure");
		node.put("message", "failed. " + e);
	}
	if (taskTrackApproval == null || taskTrackApproval.isEmpty()) {
		node.put("status", "failure");
		node.put("message", "No Content");
		return node;
	}
	
	node.put("status", "Success");
    node.put("data",taskTrackApproval);
	return node;
}

@SuppressWarnings("unchecked")
@PostMapping(value="/getAuditDataForApprovalFinal")
public JSONObject  getAuditDataForFinal(@RequestBody JsonNode givenData) throws Exception{
	Long userId=null,projectId=null;
	JSONObject taskTrackApprovalFinal=null;
	JSONObject node=new JSONObject();
	JSONObject nodeJson=new JSONObject();
	Date fromDate=null,toDate=null;
	SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
	if(givenData.get("userId")!=null && givenData.get("userId").asText()!="") {
	userId=givenData.get("userId").asLong();
	}
	if(givenData.get("projectId")!=null && givenData.get("projectId").asText()!="") {
	projectId=givenData.get("projectId").asLong();
	}
	if(givenData.get("fromDate")!=null && givenData.get("fromDate").asText()!="") {
	fromDate=simpleDateFormat.parse(givenData.get("fromDate").asText());
	}
	if(givenData.get("toDate")!=null && givenData.get("toDate").asText()!="") {
		toDate=simpleDateFormat.parse(givenData.get("toDate").asText());
	}
	
	try {
		taskTrackApprovalFinal=auditService.getAuditByUserIdForFinal(projectId,userId,fromDate,toDate);
		}catch(Exception e) {
			
			node.put("status", "failure");
			node.put("message", "failed. " + e);
		}
	if (taskTrackApprovalFinal == null || taskTrackApprovalFinal.isEmpty()) {
		node.put("status", "failure");
		node.put("message", "No Content");
		return node;
	}
	
	node.put("status", "Success");
    node.put("data",taskTrackApprovalFinal);
return node;
}

@SuppressWarnings("unchecked")
@PostMapping(value="/getUserAuditData")
public JSONObject getUserAuditData(@RequestBody JsonNode givenData) throws Exception{
	Long userId=null;

	JSONObject userAuditDatas=null;
	Date fromDate=null,toDate=null;
	JSONObject node=new JSONObject();
	JSONObject nodeJson=new JSONObject();
	SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
	if(givenData.get("userId")!=null && givenData.get("userId").asText()!="") {
		userId=givenData.get("userId").asLong();		
	}
	if(givenData.get("fromDate")!=null && givenData.get("fromDate").asText()!="") {
		fromDate=simpleDateFormat.parse(givenData.get("fromDate").asText());
	}
	if(givenData.get("toDate")!=null && givenData.get("toDate").asText()!= "") {
		toDate=simpleDateFormat.parse(givenData.get("toDate").asText());
	}
	try {
		if (fromDate == null && toDate == null)
			userAuditDatas=auditService.getAuditUserDetailsById(userId);
		else
			userAuditDatas=auditService.getAuditUserDetailsByDateRange(userId,fromDate,toDate);
		
	}catch(Exception e) {
		node.put("status", "failure");
		node.put("message", "failed. " + e);
	}
	if (userAuditDatas == null || userAuditDatas.isEmpty()) {
		node.put("status", "failure");
		node.put("message", "No Content");
		return node;
	}
	//nodeJson.put("userAuditDetails",userAuditDatas);
	node.put("status","Success");
	node.put("data",userAuditDatas);
	return node;
}


@SuppressWarnings("unchecked")
@PostMapping(value = "/getProjectAuditData")
public JSONObject getProjectAuditData(@RequestBody JsonNode request) throws Exception {

	Long projectId = null;
	Date fromDate = null;
	Date toDate = null;
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	JSONObject response = new JSONObject();
	JSONObject node = null;
	if (request.get("projectId") != null && request.get("projectId").asText() != "") {
		projectId = request.get("projectId").asLong();
	}
	if (request.get("fromDate") != null && request.get("fromDate").asText() != "") {
		fromDate = simpleDateFormat.parse(request.get("fromDate").asText());
	}
	if (request.get("toDate") != null && request.get("toDate").asText() != "") {
		toDate = simpleDateFormat.parse(request.get("toDate").asText());
	}

	try {

		if (projectId == 0L || projectId == null) {
			response.put("status", "failure");
			response.put("message", "ProjectId is mandtory");
			response.put("code", 400);
			return response;
		}

		if (fromDate == null && toDate == null)
			node = auditService.getProjectAuditDataByProjectId(projectId);
		else
			node = auditService.getProjectAuditDataByProjectIdAndDateRange(projectId, fromDate, toDate);
		
	} catch (Exception e) {
		response.put("status", "failure");
		response.put("message", e.getMessage());
		response.put("code", 500);
		return response;
	}
	if (node == null || node.isEmpty()) {
		response.put("status", "failure");
		response.put("message", "No Content");
		response.put("code", 404);
		return response;
	}
	response.put("data", node);
	response.put("status", "Success");
	return response;

	
}

}
