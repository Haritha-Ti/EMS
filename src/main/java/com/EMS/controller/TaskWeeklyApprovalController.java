package com.EMS.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.service.TaskWeeklyApprovalService;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping(value = { "/tasktrack" })
public class TaskWeeklyApprovalController {
	
	@Autowired
	private TaskWeeklyApprovalService weeklyApprovalService;
	
	@PostMapping(value = "/submit_weekly_approval")
	public JsonNode submitWeeklyApproval(@RequestBody JSONObject requestData) {
		
		weeklyApprovalService.submitWeeklyApproval(requestData);
		
		return null;
	}
	
	@PostMapping(value = "/save_weekly_approval")
	public JsonNode saveWeeklyApproval(@RequestBody JSONObject requestData) {
		weeklyApprovalService.saveWeeklyApproval(requestData);
		return null;
	}
	
	@GetMapping(value = "/get/weekly_tasktrack")
	public JSONObject getWeeklyTasktrack(@RequestBody JSONObject requestData) {
		return weeklyApprovalService.getWeeklyTasktrack(requestData);
	}
	
}
