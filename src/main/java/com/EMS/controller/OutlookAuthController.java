package com.EMS.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.EMS.model.UserModel;
import com.EMS.security.IdToken;
import com.EMS.service.AuthOutlookHelper;
import com.EMS.service.UserService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


@RestController
public class OutlookAuthController {

	@Autowired
	private AuthOutlookHelper authOutlookHelper;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/outlook_login")
	public JsonNode loginFromOutlook(HttpServletRequest request) {
		UUID state = UUID.randomUUID();
		UUID nonce = UUID.randomUUID();

		String loginUrl = authOutlookHelper.getLoginUrl(state, nonce);
		
		ObjectNode responseNode = objectMapper.createObjectNode();
		responseNode.put("loginUrl", loginUrl);
		
		request.getSession().setAttribute("expected_state", state.toString());
		request.getSession().setAttribute("expected_nonce", nonce.toString());

		return responseNode;
	}
	
	@PostMapping(value = "/authorize")
	public boolean authorize(@RequestParam("code") String code, @RequestParam("id_token") String idToken,
			@RequestParam("state") UUID state, HttpServletRequest request ) throws JsonParseException, JsonMappingException, IOException {
		
		ObjectNode responseNode = objectMapper.createObjectNode();
		boolean isAuthorized = false;
		
		String  expectedState = request.getSession().getAttribute("expected_state").toString();
		String expectedNonce = request.getSession().getAttribute("expected_nonce").toString();
		
		if (state.toString().equals(expectedState)) {
			responseNode.put("authCode", code);
			responseNode.put("idToken", idToken);
			
			isAuthorized = Boolean.TRUE;
		} else {
			responseNode.put("failure", "Unexpected state returned from authority.");
			isAuthorized = Boolean.FALSE;
		}
			
		IdToken idTokenObj = IdToken.parseEncodedToken(idToken, expectedNonce.toString());
		if (idTokenObj != null) {
		
		  System.out.println(idTokenObj.getEmail());
		  System.out.println(idTokenObj.toString());
		} 	
		
		  UserModel userModel = userService.getUserByEmail(idTokenObj.getPreferredUsername()); 
		  String username = userModel.getUsername(); 
		  String password =  userModel.getPassword();
		  
		  System.out.println("username + password "+username + password);
		
		return isAuthorized;
	}
	

	 
}
