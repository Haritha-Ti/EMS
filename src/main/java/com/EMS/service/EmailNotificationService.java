package com.EMS.service;

import com.EMS.dto.MailDomainDto;

public interface EmailNotificationService {
	
	String sendMail(String token, MailDomainDto mailDomainDto) throws Exception;


}
