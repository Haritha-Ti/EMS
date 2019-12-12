package com.EMS.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.EMS.model.UserModel;
import com.EMS.security.IdToken;
import com.EMS.service.AuthOutlookHelper;
import com.EMS.service.LoginService;
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

	@Autowired
	private LoginService loginService;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

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
	public JsonNode authorize(@RequestParam("code") String code, @RequestParam("id_token") String idToken,
			@RequestParam("state") UUID state, HttpServletRequest request, HttpServletResponse httpResponse)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectNode response = objectMapper.createObjectNode();
		UserModel userModel = null;

		try {
			String expectedState = request.getSession().getAttribute("expected_state").toString();
			String expectedNonce = request.getSession().getAttribute("expected_nonce").toString();

			if (!state.toString().equals(expectedState)) {		
				response.put("status", "Failed");
				response.put("message", "Invalid request.");
				return response;
			}

			IdToken idTokenObj = IdToken.parseEncodedToken(idToken, expectedNonce.toString());

			userModel = userService.getUserByEmail(idTokenObj.getPreferredUsername());
			if (userModel == null) {
				LOGGER.info("Invalid User");
				response.put("status", "Failed");
				response.put("code", httpResponse.getStatus());
				response.put("message", "Invalid User");
				response.put("payload", "");
				return response;
			}
			 response = loginService.adminLogin(userModel, httpResponse);
			 
		} catch (Exception e) {
			LOGGER.info("Exception in adminLogin Method");
			response.put("status", "Failed");
			response.put("code", httpResponse.getStatus());
			response.put("message", "Exception : " + e);
			response.put("payload", "");
		}
		return response;

	}

}
