package com.EMS.dto;

import java.util.Map;

public class PreviousMonthMailDto {
	
	String email;
	Map<String, StringBuilder> mailContent;
	String approverFullName;
	

	
	public String getApproverFullName() {
		return approverFullName;
	}

	public void setApproverFullName(String approverFullName) {
		this.approverFullName = approverFullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Map<String, StringBuilder> getMailContent() {
		return mailContent;
	}

	public void setMailContent(Map<String, StringBuilder> mailContent) {
		this.mailContent = mailContent;
	}

	

}
