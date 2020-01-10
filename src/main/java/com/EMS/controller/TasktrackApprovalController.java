package com.EMS.controller;

import com.EMS.model.*;
import com.EMS.service.*;
import com.EMS.utility.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

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
}
