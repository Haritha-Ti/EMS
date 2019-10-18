package com.EMS.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.model.ClientModel;
import com.EMS.service.ClientService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value="/client")
public class ClientController {

	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ClientService clientservice;
	
	@PostMapping(value = "/createClient")
	public ObjectNode createClient(@RequestBody JsonNode requestdata,HttpServletResponse httpstatus) {


		ObjectNode responsedata = objectMapper.createObjectNode();
		int responseflag = 0;
		try {

		ClientModel client=new ClientModel();
			client.setClientName(requestdata.get("clientName").asText());
			
			if ((client.getClientName() != null) && (!client.getClientName().equals(" "))
					&& (client.getClientName().length() > 0)) {
				// method invocation for checking duplicate entry for project name
				int result1 = clientservice.duplicationchecking(client.getClientName());
				if (result1 == 0) {
					// Method invocation for creating new project record

					ClientModel clientmodel = clientservice.save_client_record(client);
					// method invocation for storing resouces of project created

					if (clientmodel == null) {
						responseflag = 1;
						responsedata.put("message", "Client record creation failed");
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
	
	@GetMapping(value = "/getAllClients")
	public JsonNode getAllClients(HttpServletResponse statusResponse) {
		ObjectNode responsedata = objectMapper.createObjectNode();
		ArrayNode technologyArray = objectMapper.createArrayNode();

		try {
			// Getting all projects list to arraylist
			ArrayList<ClientModel> clientList = clientservice.getClientList();

			// checking for project arraylist is empty or not
			if (clientList.isEmpty()) {
				responsedata.put("status", "success");
				responsedata.put("message", "No Records Available");
				responsedata.put("code", statusResponse.getStatus());
				responsedata.put("payload", "");
			} else {

				// loop for getting projectwise details
				for (ClientModel obj : clientList) {

					// storing projects details in json object
					ObjectNode jsonobj = objectMapper.createObjectNode();
					jsonobj.put("clientId", obj.getClientId());
					jsonobj.put("clientName", obj.getClientName());
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
	
	
	
	@PostMapping(value = "/editClient")
	public ObjectNode editClient(@RequestBody JsonNode requestdata,HttpServletResponse httpstatus) {


		ObjectNode responsedata = objectMapper.createObjectNode();
		int responseflag = 0;
		try {

			Long clientId=requestdata.get("clientId").asLong();
			ClientModel client = clientservice.getClientData(clientId);
			client.setClientName(requestdata.get("clientName").asText());
			
			if ((client.getClientName() != null) && (!client.getClientName().equals(" "))
					&& (client.getClientName().length() > 0)) {
				// method invocation for checking duplicate entry for project name
				int result1 = clientservice.duplicationchecking(client.getClientName());
				if (result1 <= 1) {
					// Method invocation for creating new project record

					ClientModel clientmodel = clientservice.save_client_record(client);
					// method invocation for storing resouces of project created

					if (clientmodel == null) {
						responseflag = 1;
						responsedata.put("message", "Client record creation failed");
					}
						
				}
			}
			// setting values on response json
			if (responseflag == 0) {
				responsedata.put("status", "success");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.put("message", "Record Updated");
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
