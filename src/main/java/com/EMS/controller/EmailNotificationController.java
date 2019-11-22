package com.EMS.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.model.MailDomainModel;
import com.EMS.model.Technology;
import com.EMS.model.UserModel;
import com.EMS.service.EmailNotificationService;
import com.EMS.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value = "/EmailNotification")
public class EmailNotificationController {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserService userservice;

	@Autowired
	private EmailNotificationService emailNotificationService;

	@PostMapping(value = "/viewEmailNotification")
	public ObjectNode viewEmailNotification(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) {

		ObjectNode responsedata = objectMapper.createObjectNode();
		ArrayNode emailArray = objectMapper.createArrayNode();

		try {

			Long userId = requestdata.get("userId").asLong();
			UserModel user = userservice.getUserdetailsbyId(userId);
			System.out.println("user email : "+user.getEmail());
			List<MailDomainModel> mailnotificationList = emailNotificationService.getUnReadEmails(user.getEmail());
			System.out.println("Email list size :"+mailnotificationList.size());
			if (mailnotificationList.isEmpty()) {
				responsedata.put("status", "success");
				responsedata.put("message", "No Records Available");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.put("payload", "");
			} else {

				// loop for getting projectwise details
				for (MailDomainModel obj : mailnotificationList) {

					// storing projects details in json object
					ObjectNode jsonobj = objectMapper.createObjectNode();
					jsonobj.put("mailDomaiId", obj.getMailDomainId());
					jsonobj.put("mailContent", obj.getMailContent());
					jsonobj.put("mailTo", obj.getMailTo());
					jsonobj.put("mailFrom", obj.getMailFrom());
					jsonobj.put("mailTimeStamp", obj.getMail_timestamp().toString());
					emailArray.add(jsonobj);
				}
				responsedata.put("status", "success");
				responsedata.put("message", "success");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.set("payload", emailArray);

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
	
	@PostMapping(value = "/updateEmailNotificationStatus")
	public ObjectNode toUpdateEmailStatus(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) {

		ObjectNode responsedata = objectMapper.createObjectNode();
		
		try {

//			Long userId = requestdata.get("userId").asLong();
			Long mailDomainId = requestdata.get("mailDomainId").asLong();
			int mailcount = emailNotificationService.getEmailCount(mailDomainId);
		
			if(mailcount==0) {
				responsedata.put("status", "success");
				responsedata.put("message", "No Matching Records Available");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.put("payload", "");
			}else {
				
				int result=emailNotificationService.updateEmailStatus(mailDomainId);
//				if(result>0) {
					responsedata.put("status", "success");
					responsedata.put("code", httpstatus.getStatus());
					responsedata.put("message", "Record Status Updated");
					responsedata.put("payload", "");
//				}else {
//					responsedata.put("status", "Failed");
//					responsedata.put("code", httpstatus.getStatus());
//					responsedata.put("payload", "");
//				}
				
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
