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

@PostMapping(value="/getAuditDataForApproval")
public JSONObject getAuditData(@RequestBody JsonNode givenData) throws Exception{
	Long userId=null,projectId=null;
	Date fromDate=null,toDate=null;
	JSONObject node=new JSONObject();
	JSONObject nodeJson=new JSONObject();
	List<JSONObject> taskTrackApproval=null;
	SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
	fromDate=simpleDateFormat.parse(givenData.get("fromDate").asText());
	if(givenData.get("userId")!=null && givenData.get("userId").asText()!=" ") {
	userId=givenData.get("userId").asLong();
	}
	if(givenData.get("projectId")!=null && givenData.get("projectId").asText()!=" ") {
	projectId=givenData.get("projectId").asLong();
	}
	if(givenData.get("fromDate")!=null && givenData.get("fromDate").asText()!=" ") {
	fromDate=simpleDateFormat.parse(givenData.get("fromDate").asText());
	}
	if(givenData.get("toDate")!=null && givenData.get("toDate").asText()!=" ") {
		toDate=simpleDateFormat.parse(givenData.get("toDate").asText());
	}
	try {
	taskTrackApproval=auditService.getAuditByUserId(projectId,userId,fromDate,toDate);
	
	
	}catch(Exception e) {
		
		node.put("status", "failure");
		node.put("message", "failed. " + e);
	}
	if (taskTrackApproval == null || taskTrackApproval.isEmpty()) {
		node.put("staus", "failure");
		node.put("message", "No Content");
		return node;
	}
	nodeJson.put("rows",taskTrackApproval);
	node.put("status", "Success");
    node.put("data",nodeJson);
	return node;
}

@PostMapping(value="/getAuditDataForApprovalFinal")
public JSONObject  getAuditDataForFinal(@RequestBody JsonNode givenData) throws Exception{
	Long userId=null,projectId=null;
	List<JSONObject> taskTrackApprovalFinal=null;
	JSONObject node=new JSONObject();
	JSONObject nodeJson=new JSONObject();
	Date fromDate=null,toDate=null;
	SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
	if(givenData.get("userId")!=null && givenData.get("userId").asText()!=" ") {
	userId=givenData.get("userId").asLong();
	}
	if(givenData.get("projectId")!=null && givenData.get("projectId").asText()!=" ") {
	projectId=givenData.get("projectId").asLong();
	}
	if(givenData.get("fromDate")!=null && givenData.get("fromDate").asText()!=" ") {
	fromDate=simpleDateFormat.parse(givenData.get("fromDate").asText());
	}
	if(givenData.get("toDate")!=null && givenData.get("toDate").asText()!=" ") {
		toDate=simpleDateFormat.parse(givenData.get("toDate").asText());
	}
	
	try {
		taskTrackApprovalFinal=auditService.getAuditByUserIdForFinal(projectId,userId,fromDate,toDate);
		
		
		}catch(Exception e) {
			
			node.put("status", "failure");
			node.put("message", "failed. " + e);
		}
	if (taskTrackApprovalFinal == null || taskTrackApprovalFinal.isEmpty()) {
		node.put("staus", "failure");
		node.put("message", "No Content");
		return node;
	}
	nodeJson.put("rows",taskTrackApprovalFinal);
	node.put("status", "Success");
    node.put("data",nodeJson);
return node;
}

@PostMapping(value="/getUserAuditData")
public JSONObject getUserAuditData(@RequestBody JsonNode givenData) throws Exception{
	Long userId=null;
	List<JSONObject> userAuditData=null;
	Date fromDate=null,toDate=null;
	JSONObject node=new JSONObject();
	JSONObject nodeJson=new JSONObject();
	SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
	if(givenData.get("userId")!=null && givenData.get("userId").asText()!=" ") {
		userId=givenData.get("userId").asLong();		
	}
	if(givenData.get("fromDate")!=null && givenData.get("fromDate").asText()!=" ") {
		fromDate=simpleDateFormat.parse(givenData.get("fromDate").asText());
	}
	if(givenData.get("toDate")!=null && givenData.get("toDate").asText()!= " ") {
		toDate=simpleDateFormat.parse(givenData.get("toDate").asText());
	}
	try {
		if (fromDate == null && toDate == null)
			userAuditData=auditService.getAuditUserDetailsById(userId);
		else
			userAuditData=auditService.getAuditUserDetailsByDateRange(userId,fromDate,toDate);
		
	}catch(Exception e) {
		node.put("status", "failure");
		node.put("message", "failed. " + e);
	}
	if (userAuditData == null || userAuditData.isEmpty()) {
		node.put("status", "failure");
		node.put("message", "No Content");
		return node;
	}
	nodeJson.put("rows",userAuditData);
	node.put("status","Success");
	node.put("data",nodeJson);
	return node;
}

@SuppressWarnings("unchecked")
@PostMapping(value = "/getProjectAuditData")
public JSONObject getProjectAuditData(@RequestBody JsonNode givenData) throws Exception {

	Long projectId = null;
	Date fromDate = null;
	Date toDate = null;
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	JSONObject response = new JSONObject();
	List<JSONObject> node = null;
	if (givenData.get("projectId") != null && givenData.get("projectId").asText() != " ") {
		projectId = givenData.get("projectId").asLong();
	}
	if (givenData.get("fromDate") != null && givenData.get("fromDate").asText() != " ") {
		fromDate = simpleDateFormat.parse(givenData.get("fromDate").asText());
	}
	if (givenData.get("toDate") != null && givenData.get("toDate").asText() != " ") {
		toDate = simpleDateFormat.parse(givenData.get("toDate").asText());
	}

	try {

		if (projectId == 0L || projectId == null) {
			response.put("staus", "failure");
			response.put("message", "ProjectId is mandtory");
			return response;
		}

		if (fromDate == null && toDate == null)
			node = auditService.getProjectAuditDataByProjectId(projectId);
		else
			node = auditService.getProjectAuditDataByProjectIdAndDateRange(projectId, fromDate, toDate);
	} catch (Exception e) {
		response.put("staus", "failure");
		response.put("message", e.getMessage());
		return response;
	}
	if (node == null || node.isEmpty()) {
		response.put("staus", "failure");
		response.put("message", "No Content");
		return response;
	}
	response.put("data", node);
	response.put("staus", "Success");
	return response;

}
}
