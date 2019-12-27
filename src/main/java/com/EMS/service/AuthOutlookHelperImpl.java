package com.EMS.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AuthOutlookHelperImpl implements AuthOutlookHelper {

	  @Value("${appId}")
	  private  String appId;
	  @Value("${appPassword}")
	  private   String appPassword ;
	  @Value("${redirectUrl}")
	  private  String redirectUrl;
	  
	 private static final String authority = "https://login.microsoftonline.com";
	 private static final String authorizeUrl = authority + "/common/oauth2/v2.0/authorize";

	  private static String[] scopes = {
	    "openid",
	    "offline_access",
	    "profile",
	    "User.Read"
	  };


	@Override
	public String getScopes() {
		StringBuilder sb = new StringBuilder();
		for (String scope : scopes) {
			sb.append(scope + " ");
		}
		return sb.toString().trim();
	}

	@Override
	public String getLoginUrl(String state, String nonce) {
		
		UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(authorizeUrl);
		urlBuilder.queryParam("client_id", appId);
		urlBuilder.queryParam("redirect_uri", redirectUrl);
		urlBuilder.queryParam("response_type", "code id_token");
		urlBuilder.queryParam("scope", getScopes());
		urlBuilder.queryParam("state", state);
		urlBuilder.queryParam("nonce", nonce);
		urlBuilder.queryParam("response_mode", "form_post");

		System.out.println("urlBuilder: "+ urlBuilder.toString());
		return urlBuilder.toUriString();
	}
}
