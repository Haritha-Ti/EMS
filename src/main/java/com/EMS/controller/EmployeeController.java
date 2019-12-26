package com.EMS.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.model.Technology;
import com.EMS.model.UserModel;
import com.EMS.model.UserTechnology;
import com.EMS.service.LoginService;
import com.EMS.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value = "/employee")
public class EmployeeController {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	LoginService login_service;

	@Autowired
	private UserService userService;

	@PostMapping(value = "/addUserTechnology")
	public JsonNode addUserTechnology(@RequestBody ObjectNode requestBody, HttpServletResponse servletresponse) {
		int responseflag = 0;
		ObjectNode responsedata = objectMapper.createObjectNode();
		ArrayNode skillsArray = (ArrayNode) requestBody.get("data");
		for (int i = 0; i < skillsArray.size(); i++) {

			ObjectNode requestdata = objectMapper.createObjectNode();
			requestdata = (ObjectNode) skillsArray.get(i);

			UserTechnology usertech = new UserTechnology();

			Long techId = requestdata.get("technology").asLong();
			Long userId = requestdata.get("userId").asLong();
			int skill = requestdata.get("skilllevel").asInt();

			double exp = requestdata.get("experience").asDouble();

			Technology technology = null;

			if (techId != null)
				technology = login_service.findtechnology(techId);

			if (technology != null)
				usertech.setTechnology(technology);
			else {
				responseflag = 1;
				responsedata.put("message", "User technology insertion failed due to missing technology value");
			}
			UserModel userdata = userService.getUserdetailsbyId(userId);
			usertech.setUser(userdata);
			usertech.setSkill_level(skill);
			usertech.setExperience(exp);

			if (technology.getTechnologyName().equals("Others")) {
				String comment = requestdata.get("comment").asText();
				usertech.setComment(comment);
			}

			int userTechnology = login_service.addusertechnology(usertech);
			// System.out.println("userTechnology
			// :"+userTechnology);
			if (userTechnology == 0)
				responseflag = 1;
		}

		if (responseflag == 0) {
			responsedata.put("status", "success");
			responsedata.put("message", "User added successfully");
			responsedata.put("code", servletresponse.getStatus());
		} else {
			responsedata.put("status", "Failed");
			responsedata.put("code", servletresponse.getStatus());
		}

		return responsedata;
	}

	@SuppressWarnings({ "unchecked", "unused", "null" })
	@PostMapping(value = "/getUserSkills")
	public JSONObject getUserSkills(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {

		JSONObject response = null;
		Long userId = null;
		try {

			userId = requestdata.get("userId").asLong();
			if (userId == null || userId == 0L) {
				response = new JSONObject();
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			response = userService.getSkillsByUserId(userId);

			if (response != null) {
				response.put("Status", "Success");
				response.put("Code", 200);

			} else {
				response = new JSONObject();
				response.put("Status", "Success");
				response.put("Code", 204);
				response.put("Message", "No Content");
			}

		} catch (Exception e) {
			response = new JSONObject();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
		}
		return response;
	}

	@PostMapping(value = "/editUserTechnology")
	public JsonNode editUserTechnology(@RequestBody ObjectNode requestBody, HttpServletResponse servletresponse) {

		int responseflag = 0;
		ObjectNode responsedata = objectMapper.createObjectNode();
		ArrayNode skillsArray = (ArrayNode) requestBody.get("data");
		ArrayNode deletedUserTechId = (ArrayNode) requestBody.get("deletedIds");

		for (int i = 0; i < deletedUserTechId.size(); i++) {
			ObjectNode deletedObj = (ObjectNode) deletedUserTechId.get(i);
			Long userTechnologyId = deletedObj.get("userTechnology").asLong();

			UserTechnology usertechobj = login_service.getUserTechnology(userTechnologyId);
			if (usertechobj.equals(null)) {
				responsedata.put("message", "Invalid User technology Id for deletion");
			} else {

				int result = login_service.deleteUserTechnology(userTechnologyId);
				if (result <= 0)
					responseflag = 1;
			}
		}

		for (int i = 0; i < skillsArray.size(); i++) {

			ObjectNode requestdata = (ObjectNode) skillsArray.get(i);
			Long userTechId = requestdata.get("userTechnologyId").asLong();

			UserTechnology userTechnology = login_service.getUserTechnology(userTechId);
			Long techId = requestdata.get("technology").asLong();
			Long userId = requestdata.get("userId").asLong();
			int skill = requestdata.get("skilllevel").asInt();
			double exp = requestdata.get("experience").asDouble();

			Technology technology = null;

			if (techId != null)
				technology = login_service.findtechnology(techId);

			if (technology != null)
				userTechnology.setTechnology(technology);
			else {
				responseflag = 1;
				responsedata.put("message", "User technology insertion failed due to missing technology value");
			}
			UserModel userdata = userService.getUserdetailsbyId(userId);
			userTechnology.setUser(userdata);
			userTechnology.setSkill_level(skill);
			userTechnology.setExperience(exp);

			if (technology.getTechnologyName().equals("Others")) {
				String comment = requestdata.get("comment").asText();
				userTechnology.setComment(comment);
			}

			if (userTechnology.equals(null)) {
				responsedata.put("message", "Invalid details");
			} else {
				int userTechno = login_service.updateusertechnology(userTechnology);
				if (userTechno == 0)
					responseflag = 1;

			}
		}

		if (responseflag == 0) {
			responsedata.put("status", "success");
			responsedata.put("message", "User added successfully");
			responsedata.put("code", servletresponse.getStatus());
		} else {
			responsedata.put("status", "Failed");
			responsedata.put("code", servletresponse.getStatus());
		}

		return responsedata;
	}

}
