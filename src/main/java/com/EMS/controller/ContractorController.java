package com.EMS.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.model.EmployeeContractors;
import com.EMS.model.Technology;
import com.EMS.service.ContractorService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value = "/contractor")
public class ContractorController {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ContractorService contractorService;

	@PostMapping(value = "/createEmployeeContractor")
	public ObjectNode createEmployeeContractor(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) {

		ObjectNode responsedata = objectMapper.createObjectNode();
		int responseflag = 0;
		try {

			EmployeeContractors employeeContractors = new EmployeeContractors();
			employeeContractors.setContractorName(requestdata.get("contractorName").asText());

			if ((employeeContractors.getContractorName() != null)
					&& (!employeeContractors.getContractorName().equals(" "))
					&& (employeeContractors.getContractorName().length() > 0)) {

				int result1 = contractorService.duplicationchecking(employeeContractors.getContractorName());
				if (result1 == 0) {

					EmployeeContractors employee = contractorService.save_contractor_record(employeeContractors);
					if (employee == null) {
						responseflag = 1;
						responsedata.put("message", "Contractor record creation failed");
					}

				} else
					responseflag = 1;
			} else
				responseflag = 1;
			// setting values on response json
			if (responseflag == 0) {
				responsedata.put("status", "success");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.put("message", "Record Inserted");
				responsedata.put("payload", "");
			} else {
				responsedata.put("status", "Failed");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.put("payload", "");
			}

		} catch (Exception e) {
			System.out.println("Exception : " + e);
			responsedata.put("status", "Failed");
			responsedata.put("code", httpstatus.getStatus());
			responsedata.put("message", "Exception " + e);
			responsedata.put("payload", "");
		}
		return responsedata;
	}
	
	
//	Api for getting all technologies
	@GetMapping(value = "/getAllContractors")
	public JsonNode getAllContractors(HttpServletResponse statusResponse) {
		ObjectNode responsedata = objectMapper.createObjectNode();
		ArrayNode contractorArray = objectMapper.createArrayNode();

		try {
			// Getting all projects list to arraylist
			ArrayList<EmployeeContractors> contractorList = contractorService.getContractorList();

			// checking for project arraylist is empty or not
			if (contractorList.isEmpty()) {
				responsedata.put("status", "success");
				responsedata.put("message", "No Records Available");
				responsedata.put("code", statusResponse.getStatus());
				responsedata.put("payload", "");
			} else {

				// loop for getting projectwise details
				for (EmployeeContractors obj : contractorList) {

					// storing projects details in json object
					ObjectNode jsonobj = objectMapper.createObjectNode();
					jsonobj.put("contractorId", obj.getContractorId());
					jsonobj.put("contractorName", obj.getContractorName());
					contractorArray.add(jsonobj);
				}
				responsedata.put("status", "success");
				responsedata.put("message", "success");
				responsedata.put("code", statusResponse.getStatus());
				responsedata.set("payload", contractorArray);

			}

		} catch (Exception e) {
			System.out.println("Exception : " + e);
			responsedata.put("status", "failed");
			responsedata.put("message", "Exception : " + e);
			responsedata.put("code", statusResponse.getStatus());
		}

		return responsedata;
	}
	
	
	@PostMapping(value = "/editEmployeeContractor")
	public ObjectNode editEmployeeContractor(@RequestBody JsonNode requestdata,HttpServletResponse httpstatus) {


		ObjectNode responsedata = objectMapper.createObjectNode();
		int responseflag = 0;
		try {
			Long contractId=requestdata.get("contractorId").asLong();
		EmployeeContractors contractors=contractorService.getContractordata(contractId);
				
		contractors.setContractorName(requestdata.get("contractorName").asText());;	
			
			if ((contractors.getContractorName() != null) && (!contractors.getContractorName().equals(" "))
					&& (contractors.getContractorName().length() > 0)) {
				// method invocation for checking duplicate entry for project name
				int result1 = contractorService.duplicationchecking(contractors.getContractorName());
				if (result1 <=1) {
					// Method invocation for creating new project record

					EmployeeContractors model = contractorService.save_contractor_record(contractors);
					// method invocation for storing resouces of project created

					if (model == null) {
						responseflag = 1;
						responsedata.put("message", "Technology record creation failed");
					}
						
				}
			}
			// setting values on response json
			if (responseflag == 0) {
				responsedata.put("status", "success");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.put("message", "Record updated");
				responsedata.put("payload", "");
			} else {
				responsedata.put("status", "Failed");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.put("payload", "");
			}

		} catch (Exception e) {
			System.out.println("Exception : " + e);
			responsedata.put("status", "Failed");
			responsedata.put("code", httpstatus.getStatus());
			responsedata.put("message", "Exception " + e);
			responsedata.put("payload", "");
		}
		return responsedata;
	}
}
