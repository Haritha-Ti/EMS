package com.EMS.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.service.DashBoardService;
import com.EMS.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value = "/dashboard")
public class DashBoardController {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private DashBoardService dashboardService;

	@Autowired
	private UserService userService;

	// Api for dashboard view
	@PostMapping(value = "/getBenchProjectResourcesCount")
	public ObjectNode getCountOfResourcesInBenchProject(@RequestBody ObjectNode requestdata,
			HttpServletResponse httpstatus) {

		ObjectNode resData = objectMapper.createObjectNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		Long userId = null;
		Long regionId = null;

		if (requestdata.get("sessionId") != null && (!requestdata.get("sessionId").asText().trim().isEmpty())) {
			userId = requestdata.get("sessionId").asLong();
		}

		if (userId == null) {
			resData = dashboardService.getCountOfResourcesInBenchProject();
		} else {
			regionId = userService.getUserdetailsbyId(userId).getRegion().getId();
			resData = dashboardService.getCountOfResourcesInBenchProject(regionId);
		}

		jsonDataRes.set("data", resData);
		jsonDataRes.put("status", "success");
		jsonDataRes.put("code", httpstatus.getStatus());
		jsonDataRes.put("message", "success ");

		return jsonDataRes;
	}
}
