package com.EMS.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.model.StatusResponse;
import com.EMS.service.AuditService;
import com.EMS.service.HRTWorkflowMasterService;
import com.EMS.utility.Constants;

@RestController
@RequestMapping(value = "/hrt")
public class HRTWorkFlowController {
	
	@Autowired
	private HRTWorkflowMasterService HRTWorkflowMasterService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("/workflow/{regionId}")
	public StatusResponse workflowBasedonRegionId(@PathVariable("regionId") Long regionId){
		
		StatusResponse response = new StatusResponse();
		try {
			response= new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, HRTWorkflowMasterService.getWorkflowByRegionId(regionId));
		} catch (Exception e) {

		}
		return response;

	}

}
