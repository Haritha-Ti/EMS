package com.EMS.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.EMS.dto.MailDomainDto;
import com.EMS.model.UserModel;
import com.EMS.utility.Constants;

@Service
public class EmailNotificationServiceImpl implements EmailNotificationService{
	
	@Value("${EMAIL_FROM}")
	private String from;
	
	@Value("${EMAIL_USERNAME}")
	private String username;
	
	@Value("${EMAIL_PASSWORD}")
	private String password;
	

	@Override
	public String sendMail(String token, MailDomainDto mailDomainDto) throws Exception {
	    String msg = "Failure";

		try {
		        String host = "smtp.gmail.com"; 
		        System.out.println("TLS Email Start"); 
		        Properties properties = System.getProperties();  
		        properties.setProperty("mail.smtp.host", host); 
		        properties.put("mail.smtp.port", "465");  
		        properties.put("mail.smtp.auth", "true");  
		        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");   

		        Session session = Session.getDefaultInstance(properties, 
		        new javax.mail.Authenticator() { 
		            protected PasswordAuthentication  
		                    getPasswordAuthentication() { 
		                return new PasswordAuthentication(username, password); 
		            } 
		        }); 

			    MimeMessage message = new MimeMessage(session);  
			      
			    message.setFrom(new InternetAddress(from)); 

			    if(mailDomainDto.getTo() != null ) {
			    	InternetAddress[] to = InternetAddress.parse(mailDomainDto.getTo()); 
			    	message.addRecipients(Message.RecipientType.TO, to);
			    }
			    
			    if(mailDomainDto.getCc() != null ) {
			    	InternetAddress[] cc = InternetAddress.parse(mailDomainDto.getCc()); 
			    	message.addRecipients(Message.RecipientType.CC, cc);
			    }
			    if(mailDomainDto.getBcc() != null ) {
			    	InternetAddress[] bcc = InternetAddress.parse(mailDomainDto.getBcc()); 
			    	message.addRecipients(Message.RecipientType.BCC, bcc);	
			    }
			    
			    message.setSubject(mailDomainDto.getSubject()); 
			    message.setText(mailDomainDto.getMailBody());
			    message.setContent(mailDomainDto.getMailBody(),"text/html");
			  
			    Transport.send(message); 
			    
			    msg = "Success";

		}catch (Exception e) {
			msg = "Failure";
		}
	    System.out.println(msg); 
		return msg;
	}

}
