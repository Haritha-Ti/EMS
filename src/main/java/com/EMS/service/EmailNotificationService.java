package com.EMS.service;

import java.util.List;

import com.EMS.dto.MailDomainDto;
import com.EMS.model.MailDomainModel;

public interface EmailNotificationService {
	
	String sendMail(String token, MailDomainDto mailDomainDto ,Boolean isSaveMailContent) throws Exception;

	List<MailDomainModel> getUnReadEmails(String email);

	 

	int getEmailCount(Long mailDomainId);

	int updateEmailStatus(Long mailDomainId);


}
