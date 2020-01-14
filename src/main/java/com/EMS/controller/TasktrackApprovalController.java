package com.EMS.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.dto.tasktrackapproval2.request.GetTaskTrackData;
import com.EMS.model.StatusResponse;
import com.EMS.service.TasktrackApprovalService;
import com.EMS.utility.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value = { "/approval" })
public class TasktrackApprovalController {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private  TasktrackApprovalService tasktrackApprovalService;

	@PostMapping(value = "/taskTrackDataForApprover1")
	public ObjectNode getTaskTrackDataForApprover1(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus) {
		ObjectNode responseData = objectMapper.createObjectNode();
		ObjectNode node = objectMapper.createObjectNode();
		try {
			node = tasktrackApprovalService.getTaskTrackDataForApprover1(requestdata);
            responseData.set("data",node);
			responseData.put("status", "Sucess");
			responseData.put("message", "Sucess ");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
            responseData.set("data",node);
			responseData.put("status", "failure");
			responseData.put("message", "failed. " + e);
		}

		responseData.put("code", httpstatus.getStatus());
		return responseData;
	}

	@PostMapping(value = "/taskTrackDataByUserId")
	public ObjectNode getTaskTrackDataByUserId(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus) {
		ObjectNode responseData = objectMapper.createObjectNode();
		ObjectNode node = objectMapper.createObjectNode();
		try {
			node = tasktrackApprovalService.getTaskTrackDataByUserId(requestdata);
			responseData.set("data",node);
			responseData.put("status", "Sucess");
			responseData.put("message", "Sucess ");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			responseData.set("data",node);
			responseData.put("status", "failure");
			responseData.put("message", "failed. " + e);
		}

		responseData.put("code", httpstatus.getStatus());
		return responseData;
	}
	@PutMapping(value = "/approveHoursLevel1")
	public ObjectNode approveHoursLevel1(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus) {
		ObjectNode responseData = objectMapper.createObjectNode();
		ObjectNode node = objectMapper.createObjectNode();
		try {
			node = tasktrackApprovalService.approveHoursLevel1(requestdata);
			responseData.set("data",node);
			responseData.put("status", "Sucess");
			responseData.put("message", "Sucess ");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			responseData.set("data",node);
			responseData.put("status", "failure");
			responseData.put("message", "failed. " + e);
		}

		responseData.put("code", httpstatus.getStatus());
		return responseData;
	}
	
	@PostMapping(value = "/taskTrackDataForApprover2")
	public StatusResponse getTaskTrackDataForApprover2(@RequestBody GetTaskTrackData requestdata, HttpServletResponse httpstatus) {
		
		StatusResponse node = null;
		try {
			node = tasktrackApprovalService.getTaskTrackDataForApprover2(requestdata);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			node = new StatusResponse(Constants.ERROR,Constants.ERROR_CODE,"");
		}

		return node;
	}

	//Nisha
	@PostMapping(value = "/taskTrackDataForFinance")
	public ObjectNode getTaskTrackDataForFinance(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus) {
		ObjectNode responseData = objectMapper.createObjectNode();
		ObjectNode node = objectMapper.createObjectNode();
		try {
			node = tasktrackApprovalService.getTaskTrackDataForFinance(requestdata);
			responseData.set("data",node);
			responseData.put("status", "Sucess");
			responseData.put("message", "Sucess ");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			responseData.set("data",node);
			responseData.put("status", "failure");
			responseData.put("message", "failed. " + e);
		}

		responseData.put("code", httpstatus.getStatus());
		return responseData;
	}
	//nisha
	@PostMapping(value = "/taskTrackDataByUserIdForFinance")
	public ObjectNode getTaskTrackDataByUserIdForFinance(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus) {
		ObjectNode responseData = objectMapper.createObjectNode();
		ObjectNode node = objectMapper.createObjectNode();
		try {
			node = tasktrackApprovalService.getTaskTrackDataByUserIdForFinance(requestdata);
			responseData.set("data",node);
			responseData.put("status", "Sucess");
			responseData.put("message", "Sucess ");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			responseData.set("data",node);
			responseData.put("status", "failure");
			responseData.put("message", "failed. " + e);
		}

		responseData.put("code", httpstatus.getStatus());
		return responseData;
	}
}
