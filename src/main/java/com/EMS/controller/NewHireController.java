package com.EMS.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.EMS.service.NewHireEmployeeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import freemarker.template.TemplateException;

@RestController
@RequestMapping(value = "/newhire")
public class NewHireController {

	@Autowired
	private NewHireEmployeeService newHireEmployeeService;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Value("${HRT_API_URL}")
	private String  url;


	@SuppressWarnings("unchecked")
	@PostMapping(value = "/sendmail")
	public JSONObject sendMail(@RequestBody ObjectNode requestBody, HttpServletResponse servletresponse) {
		String email = null;
		String msg = null;
		String name = null;
		String lastName =null;
		Long regionId = null;
		JSONObject apiResponse = null;
		JSONObject response = new JSONObject();
		String uniqueId = UUID.randomUUID().toString();
		email = org.springframework.util.StringUtils.isEmpty(requestBody.get("email").asText()) ? null
				: requestBody.get("email").asText();
		name = org.springframework.util.StringUtils.isEmpty(requestBody.get("name").asText()) ? null
				: requestBody.get("name").asText();
		lastName = org.springframework.util.StringUtils.isEmpty(requestBody.get("lastName").asText()) ? null
				: requestBody.get("lastName").asText();
		regionId = org.springframework.util.StringUtils.isEmpty(requestBody.get("regionId").asLong()) ? null
				: requestBody.get("regionId").asLong();
		if (email == null || name == null || regionId == null || regionId == 0L) {

			response.put("Status", "failure");
			response.put("Code", 422);
			response.put("Message", "Invalid Input");
			return response;

		}
		RestTemplate restTemplate = new RestTemplate();
		JSONObject apiRequestbody = new JSONObject();
		apiRequestbody.put("name", name);
		apiRequestbody.put("lastName", lastName);
		apiRequestbody.put("regionId", regionId);
		apiRequestbody.put("mailId", email);
		apiRequestbody.put("uId", uniqueId);
		apiRequestbody.put("status", "OPEN");
		try {
			/*
			 * apiResponse = restTemplate.postForObject(
			 * "http://192.168.11.72:8081/hrt/saveNewHireUniqueId",
			 * apiRequestbody, JSONObject.class);
			 */
			apiResponse = restTemplate.postForObject(url+"/hrt/saveNewHireUniqueId", apiRequestbody,
					JSONObject.class);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
			return response;
		}

		// System.out.println(apiResponse.get("code"));
		if (!apiResponse.get("code").equals(new Integer(200))) {
			response.put("Status", "Failure");
			response.put("Code", 400);
			response.put("Message", "New Hire Save failed");
			return response;
		}
		try {
			msg = newHireEmployeeService.sendMail(uniqueId, email,name);
			if (msg == "Success") {
				response.put("Status", "Sucess");
				response.put("Code", 201);
				response.put("Message", "Mail Send Successfully");
			}
			if (msg == "Failure") {
				response.put("Status", "Failure");
				response.put("Code", 400);
				response.put("Message", "Mail Sending Failed");
			}
		} catch (IOException | TemplateException e) {
			e.printStackTrace();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
		}
		return response;
	}

	@PostMapping(value = "/getNewHireDetailsByRegno")
	public JsonNode getNewHireDetailsByRegno(@RequestBody ObjectNode requestBody, HttpServletResponse servletresponse) {
		Long regNo = org.springframework.util.StringUtils.isEmpty(requestBody.get("regNo").asText()) ? null
				: requestBody.get("regNo").asLong();
		RestTemplate restTemplate = new RestTemplate();
		JsonNode node = null;
		ObjectNode response = objectMapper.createObjectNode();
		try {
			node = restTemplate.getForObject(url+"/newHire/getNewEmployeeData/" + regNo,
					JsonNode.class);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
			return response;
		}
		return node;

	}
	
	
	@PostMapping(value = "/getNewHireDetailsByCredentialsId")
	public JsonNode getNewHireDetailsByCredentialsId(@RequestBody ObjectNode requestBody, HttpServletResponse servletresponse) {
		Long credentialsId = org.springframework.util.StringUtils.isEmpty(requestBody.get("credentialsId").asText()) ? null
				: requestBody.get("credentialsId").asLong();
		RestTemplate restTemplate = new RestTemplate();
		JsonNode node = null;
		ObjectNode response = objectMapper.createObjectNode();
		try {
			node = restTemplate.getForObject(url+"/newHire/getNewEmployeeDataByCredentialsId/" + credentialsId,
					JsonNode.class);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
			return response;
		}
		return node;

	}
	
	
	
	@SuppressWarnings("unchecked")
	@PutMapping(value = "/editNewHireDetailsById")
	public JSONObject editNewHireDetailsById(@RequestBody ObjectNode requestBody, HttpServletResponse servletresponse) {
		
		JSONObject apiResponse = null;
		JSONObject response = new JSONObject();
		RestTemplate restTemplate = new RestTemplate();
		//JSONObject apiRequestbody = new JSONObject();
		ObjectNode  apiRequestbody=new ObjectMapper().createObjectNode();
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		ResponseEntity<ObjectNode>  responseEntity=null;
		try {
        apiRequestbody.put("newEmployeeId", org.springframework.util.StringUtils.isEmpty(requestBody.get("newEmployeeId").asLong()) ? null : requestBody.get("newEmployeeId").asLong());
		apiRequestbody.put("contactNumber", org.springframework.util.StringUtils.isEmpty(requestBody.get("contactNumber").asLong()) ? null : requestBody.get("contactNumber").asLong());
		apiRequestbody.put("taxId", org.springframework.util.StringUtils.isEmpty(requestBody.get("taxId").asLong()) ? null : requestBody.get("taxId").asLong());
		apiRequestbody.put("ssn", org.springframework.util.StringUtils.isEmpty(requestBody.get("ssn").asLong()) ? null : requestBody.get("ssn").asLong());
		apiRequestbody.put("emergencyContactInformation", org.springframework.util.StringUtils.isEmpty(requestBody.get("emergencyContactInformation").asText()) ? null : requestBody.get("emergencyContactInformation").asText());
		apiRequestbody.put("name", org.springframework.util.StringUtils.isEmpty(requestBody.get("name").asText()) ? null : requestBody.get("name").asText());
		apiRequestbody.put("nickName", org.springframework.util.StringUtils.isEmpty(requestBody.get("nickName").asText()) ? null : requestBody.get("nickName").asText());
		apiRequestbody.put("gender", org.springframework.util.StringUtils.isEmpty(requestBody.get("gender").asInt()) ? null : requestBody.get("gender").asInt());
		apiRequestbody.put("birthDate", org.springframework.util.StringUtils.isEmpty(requestBody.get("birthDate").asText()) ? null : requestBody.get("birthDate").asText());
		apiRequestbody.put("maritalStatus", org.springframework.util.StringUtils.isEmpty(requestBody.get("maritalStatus").asText()) ? null : requestBody.get("maritalStatus").asText());
		apiRequestbody.put("registeredAddress", org.springframework.util.StringUtils.isEmpty(requestBody.get("registeredAddress").asText()) ? null : requestBody.get("registeredAddress").asText());
		apiRequestbody.put("localAddress", org.springframework.util.StringUtils.isEmpty(requestBody.get("localAddress").asText()) ? null : requestBody.get("localAddress").asText());
		apiRequestbody.put("foreignAddress", org.springframework.util.StringUtils.isEmpty(requestBody.get("foreignAddress").asText()) ? null : requestBody.get("foreignAddress").asText());
		apiRequestbody.put("taxCode", org.springframework.util.StringUtils.isEmpty(requestBody.get("taxCode").asText()) ? null : requestBody.get("taxCode").asText());
		apiRequestbody.put("nameOfDependent", org.springframework.util.StringUtils.isEmpty(requestBody.get("nameOfDependent").asText()) ? null : requestBody.get("nameOfDependent").asText());
		apiRequestbody.put("nationality", org.springframework.util.StringUtils.isEmpty(requestBody.get("nationality").asText()) ? null : requestBody.get("nationality").asText());
		apiRequestbody.put("passPort", org.springframework.util.StringUtils.isEmpty(requestBody.get("passPort").asText()) ? null : requestBody.get("passPort").asText());
		apiRequestbody.put("passportExpirationDate", org.springframework.util.StringUtils.isEmpty(requestBody.get("passportExpirationDate").asText()) ? null : requestBody.get("passportExpirationDate").asText());
		apiRequestbody.put("spouseInformation", org.springframework.util.StringUtils.isEmpty(requestBody.get("spouseInformation").asText()) ? null : requestBody.get("spouseInformation").asText());
		apiRequestbody.put("personalEmailAddress", org.springframework.util.StringUtils.isEmpty(requestBody.get("personalEmailAddress").asText()) ? null : requestBody.get("personalEmailAddress").asText());
		apiRequestbody.put("payslipPassword", org.springframework.util.StringUtils.isEmpty(requestBody.get("payslipPassword").asText()) ? null : requestBody.get("payslipPassword").asText());
		apiRequestbody.put("mostRecentEmployerName", org.springframework.util.StringUtils.isEmpty(requestBody.get("mostRecentEmployerName").asText()) ? null : requestBody.get("mostRecentEmployerName").asText());
		apiRequestbody.put("mostRecentEmploymentPosition", org.springframework.util.StringUtils.isEmpty(requestBody.get("mostRecentEmploymentPosition").asText()) ? null : requestBody.get("mostRecentEmploymentPosition").asText());
		//restTemplate.put("http://192.168.11.72:8081/newHire/editNewEmployeeData", null,apiRequestbody);
		//
		HttpEntity<ObjectNode> requestEntity = new HttpEntity<ObjectNode>(apiRequestbody);
		//responseEntity = restTemplate.exchange("http://192.168.11.72:8081/newHire/editNewEmployeeData", HttpMethod.PUT, requestEntity, ObjectNode.class );
		
		 responseEntity = restTemplate.exchange(url+"/newHire/editNewEmployeeData", HttpMethod.PUT, requestEntity, ObjectNode.class );
		//
		} catch (Exception e) {
			e.printStackTrace();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
			return response;
		}
/*
		// System.out.println(apiResponse.get("code"));
		if (!apiResponse.get("code").equals(new Integer(200))) {
			response.put("Status", "Failure");
			response.put("Code", 400);
			response.put("Message", "New Hire Updation failed");
			return response;
		}*/
		response.put("Status", responseEntity.getBody().get("status"));
		response.put("Code", responseEntity.getBody().get("code"));
		response.put("Message", responseEntity.getBody().get("message"));
		
		return  response;
	}
		
	@GetMapping(value = "/getNewEmployeeList")
	public JsonNode getNewEmployeeList( HttpServletResponse servletresponse) {
		RestTemplate restTemplate = new RestTemplate();
		JsonNode node = null;
		ObjectNode response = objectMapper.createObjectNode();
		try {
			node = restTemplate.getForObject(url+"/newHire/getNewEmployeeList",
					JsonNode.class);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
			return response;
		}
		return node;

	}
	
	
	@GetMapping(value = "/getNationalities")
	public JsonNode getNationalities( HttpServletResponse servletresponse) {
		RestTemplate restTemplate = new RestTemplate();
		JsonNode node = null;
		ObjectNode response = objectMapper.createObjectNode();
		try {
			node = restTemplate.getForObject(url+"/newHire/getNationalities",
					JsonNode.class);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
			return response;
		}
		return node;

	}
	
	
	@GetMapping(value = "/getCountries")
	public JsonNode getCountries( HttpServletResponse servletresponse) {
		RestTemplate restTemplate = new RestTemplate();
		JsonNode node = null;
		ObjectNode response = objectMapper.createObjectNode();
		try {
			node = restTemplate.getForObject(url+"/newHire/getCountries",
					JsonNode.class);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
			return response;
		}
		return node;

	}
	
	
	@GetMapping(value = "/getCountries/{countryId}")
	public JsonNode getStatesByCountry(@PathVariable("countryId") Long countryId, HttpServletResponse servletresponse) {
		RestTemplate restTemplate = new RestTemplate();
		JsonNode node = null;
		ObjectNode response = objectMapper.createObjectNode();
		try {
			node = restTemplate.getForObject(url+"/newHire/getStatesByCountry/"+countryId,
					JsonNode.class);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
			return response;
		}
		return node;

	}
	
	@GetMapping(value = "/getCitiesByState/{stateId}")
	public JsonNode getCitiesByState(@PathVariable("stateId") Long stateId, HttpServletResponse servletresponse) {
		RestTemplate restTemplate = new RestTemplate();
		JsonNode node = null;
		ObjectNode response = objectMapper.createObjectNode();
		try {
			node = restTemplate.getForObject(url+"/newHire/getCitiesByState/"+stateId,
					JsonNode.class);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
			return response;
		}
		return node;

	}
	
	
	
	
	@GetMapping(value = "/getNewEmployeeListStatus")
	public JsonNode getNewEmployeeListStatus( HttpServletResponse servletresponse) {
		RestTemplate restTemplate = new RestTemplate();
		JsonNode node = null;
		ObjectNode response = objectMapper.createObjectNode();
		try {
			node = restTemplate.getForObject(url+"/newHire/getNewEmployeeListBystatus",
					JsonNode.class);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
			return response;
		}
		return node;

	}
	
	
	@SuppressWarnings("unchecked")
	@PutMapping(value = "/editNewHireDetailsStatus")
	public JSONObject editNewHireDetailsStatus(@RequestBody ObjectNode requestBody, HttpServletResponse servletresponse) {
		
		JSONObject apiResponse = null;
		JSONObject response = new JSONObject();
		RestTemplate restTemplate = new RestTemplate();
		//JSONObject apiRequestbody = new JSONObject();
		ObjectNode  apiRequestbody=new ObjectMapper().createObjectNode();
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		ResponseEntity<ObjectNode>  responseEntity=null;
		try {
        apiRequestbody.put("newEmployeeId", org.springframework.util.StringUtils.isEmpty(requestBody.get("newEmployeeId").asLong()) ? null : requestBody.get("newEmployeeId").asLong());
        apiRequestbody.put("status", org.springframework.util.StringUtils.isEmpty(requestBody.get("status").asText()) ? null : requestBody.get("status").asText());
		HttpEntity<ObjectNode> requestEntity = new HttpEntity<ObjectNode>(apiRequestbody);
		responseEntity = restTemplate.exchange(url+"/newHire/editNewEmployeeStatus", HttpMethod.PUT, requestEntity, ObjectNode.class );
		
		} catch (Exception e) {
			e.printStackTrace();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
			return response;
		}

		response.put("Status", responseEntity.getBody().get("status"));
		response.put("Code", responseEntity.getBody().get("code"));
		response.put("Message", responseEntity.getBody().get("message"));
		
		return  response;
	}

}
