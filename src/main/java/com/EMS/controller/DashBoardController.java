package com.EMS.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.service.DashBoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value="/dashboard")
public class DashBoardController {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private DashBoardService dashboardService;
	
//	Api for dashboard view
	@PostMapping(value = "/getBenchProjectResourcesCount")
	public ObjectNode getCountOfResourcesInBenchProject(HttpServletResponse httpstatus) {

		ObjectNode resData = objectMapper.createObjectNode();
		ObjectNode jsonDataRes = objectMapper.createObjectNode();
		resData=dashboardService.getCountOfResourcesInBenchProject();
		
		jsonDataRes.set("data",resData);
		jsonDataRes.put("status", "success");
		jsonDataRes.put("code", httpstatus.getStatus());
		jsonDataRes.put("message", "success ");
		
		return jsonDataRes;
	}
}
