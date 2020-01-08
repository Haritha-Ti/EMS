package com.EMS.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.service.TasktrackApprovalSemiMonthlyService;

@RestController
@RequestMapping(value = { "/tasktrack" })
public class TasktrackApprovalSemiMonthlyController {

	@Autowired
	private TasktrackApprovalSemiMonthlyService approvalSemiMonthlyService;
	
	@GetMapping(value = "/get/semi_monthly_tasktrack")
	public JSONObject getWeeklyTasktrack(@RequestBody JSONObject requestData) {
		return approvalSemiMonthlyService.getSemiMonthlyTasktrack(requestData);
	}
	
}
