package com.EMS.service;

import java.util.UUID;

public interface AuthOutlookHelper {

	String getScopes();
	String getLoginUrl(String state, String nonce);
}