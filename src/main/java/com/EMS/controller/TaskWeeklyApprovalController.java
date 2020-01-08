package com.EMS.controller;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.service.TaskWeeklyApprovalService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value = { "/tasktrack" })
public class TaskWeeklyApprovalController {
	
	@Autowired
	private TaskWeeklyApprovalService weeklyApprovalService;
	
	@Autowired
	private ObjectMapper objectmapper;
	
	@PostMapping(value = "/submit_weekly_approval")
	public JsonNode submitWeeklyApproval(@RequestBody JSONObject requestData,HttpServletResponse httpstatus) {
		
		int status=weeklyApprovalService.submitWeeklyApproval(requestData);
		
		ObjectNode responseData=objectmapper.createObjectNode();
		if(status==0) {
			responseData.put("status", "success");
			responseData.put("message", "success. ");
			responseData.put("code", httpstatus.getStatus());
		}else {
			responseData.put("status", "success");
			responseData.put("message", "Failed due to invalid credentials ");
			responseData.put("code", httpstatus.getStatus());
		}
		
		return responseData;
	}
	
	@PostMapping(value = "/save_weekly_approval")
	public JsonNode saveWeeklyApproval(@RequestBody JSONObject requestData,HttpServletResponse httpstatus) {
		
		int status=weeklyApprovalService.saveWeeklyApproval(requestData);
		
		
		ObjectNode responseData=objectmapper.createObjectNode();
		if(status==0) {
			responseData.put("status", "success");
			responseData.put("message", "success. ");
			responseData.put("code", httpstatus.getStatus());
		}else {
			responseData.put("status", "success");
			responseData.put("message", "Failed due to invalid credentials ");
			responseData.put("code", httpstatus.getStatus());
		}
		
		return responseData;
	}
	
	@GetMapping(value = "/get/weekly_tasktrack")
	public JSONObject getWeeklyTasktrack(@RequestBody JSONObject requestData) {
		return weeklyApprovalService.getWeeklyTasktrack(requestData);
	}
	
}
