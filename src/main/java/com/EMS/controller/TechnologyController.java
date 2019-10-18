package com.EMS.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.model.ProjectModel;
import com.EMS.model.Technology;
import com.EMS.service.TechnologyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value="/technology")
public class TechnologyController {

	
	@Autowired
	private ObjectMapper objectMapper;
	

	@Autowired
	TechnologyService technologyService;
	
	@PostMapping(value = "/createTechnology")
	public ObjectNode createTechnology(@RequestBody JsonNode requestdata,HttpServletResponse httpstatus) {


		ObjectNode responsedata = objectMapper.createObjectNode();
		int responseflag = 0;
		try {

		Technology technology=new Technology();
		technology.setTechnologyName(requestdata.get("technologyName").asText());
			
			if ((technology.getTechnologyName() != null) && (!technology.getTechnologyName().equals(" "))
					&& (technology.getTechnologyName().length() > 0)) {
				// method invocation for checking duplicate entry for project name
				int result1 = technologyService.duplicationchecking(technology.getTechnologyName());
				if (result1 == 0) {
					// Method invocation for creating new project record

					Technology techmodel = technologyService.save_technology_record(technology);
					// method invocation for storing resouces of project created

					if (techmodel == null) {
						responseflag = 1;
						responsedata.put("message", "Technology record creation failed");
					}
						
				}
			}
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
	
	@PostMapping(value = "/editTechnology")
	public ObjectNode editTechnology(@RequestBody JsonNode requestdata,HttpServletResponse httpstatus) {


		ObjectNode responsedata = objectMapper.createObjectNode();
		int responseflag = 0;
		try {
			Long techId=requestdata.get("technologyId").asLong();
		Technology technology=technologyService.getTechnologydata(techId);
				
		technology.setTechnologyName(requestdata.get("technologyName").asText());	
			
			if ((technology.getTechnologyName() != null) && (!technology.getTechnologyName().equals(" "))
					&& (technology.getTechnologyName().length() > 0)) {
				// method invocation for checking duplicate entry for project name
				int result1 = technologyService.duplicationchecking(technology.getTechnologyName());
				if (result1 <=1) {
					// Method invocation for creating new project record

					Technology techmodel = technologyService.save_technology_record(technology);
					// method invocation for storing resouces of project created

					if (techmodel == null) {
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
	
//	Api for getting all technologies
	@GetMapping(value = "/getTechnologies")
	public JsonNode getTechnologies(HttpServletResponse statusResponse) {
		ObjectNode responsedata = objectMapper.createObjectNode();
		ArrayNode technologyArray = objectMapper.createArrayNode();

		try {
			// Getting all projects list to arraylist
			ArrayList<Technology> technologyList = technologyService.getTechnologyList();

			// checking for project arraylist is empty or not
			if (technologyList.isEmpty()) {
				responsedata.put("status", "success");
				responsedata.put("message", "No Records Available");
				responsedata.put("code", statusResponse.getStatus());
				responsedata.put("payload", "");
			} else {

				// loop for getting projectwise details
				for (Technology obj : technologyList) {

					// storing projects details in json object
					ObjectNode jsonobj = objectMapper.createObjectNode();
					jsonobj.put("technologyId", obj.getTechnologyId());
					jsonobj.put("technologyName", obj.getTechnologyName());
					technologyArray.add(jsonobj);
				}
				responsedata.put("status", "success");
				responsedata.put("message", "success");
				responsedata.put("code", statusResponse.getStatus());
				responsedata.set("payload", technologyArray);

			}

		} catch (Exception e) {
			System.out.println("Exception : " + e);
			responsedata.put("status", "failed");
			responsedata.put("message", "Exception : " + e);
			responsedata.put("code", statusResponse.getStatus());
		}

		return responsedata;
	}

}
