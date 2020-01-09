package com.EMS.controller;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.service.TasktrackApprovalSemiMonthlyService;

@RestController
@RequestMapping(value = { "/tasktrack" })
public class TasktrackApprovalSemiMonthlyController {

	@Autowired
	private TasktrackApprovalSemiMonthlyService approvalSemiMonthlyService;
	
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/get/semi_monthly_tasktrack")
	public ResponseEntity<Object> getWeeklyTasktrack(@RequestBody JSONObject requestData) {
		ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
		JSONObject jsonResp = new JSONObject();
		try {
			JSONObject semiMonthlyTasktrack = approvalSemiMonthlyService.getSemiMonthlyTasktrack(requestData);
			jsonResp.put("status", "Success");
			jsonResp.put("code", HttpServletResponse.SC_OK);
			jsonResp.put("data", semiMonthlyTasktrack);
			response = new ResponseEntity<Object>(jsonResp, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			jsonResp.put("status", "Error");
			jsonResp.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			jsonResp.put("message", e.getMessage());
			response = new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return response;
	}
	
}
