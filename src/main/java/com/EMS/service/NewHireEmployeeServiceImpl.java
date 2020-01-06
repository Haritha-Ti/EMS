package com.EMS.service;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.EMS.dto.MailDomainDto;
import com.EMS.utility.Constants;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

@Service
public class NewHireEmployeeServiceImpl implements NewHireEmployeeService{
	@Value("${EMAIL_FROM}")
	private String from;

	@Value("${EMAIL_USERNAME}")
	private String username;

	@Value("${EMAIL_PASSWORD}")
	private String password;
	@Autowired
    private Configuration freemarkerConfig;
	
	@Value("${HRT_URL}")
	private String CONTEXT_PATH;
	
	
	@Override
	public String sendMail(String uId, String mailId,String name) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		
		MailDomainDto mailDomainDto = new MailDomainDto();
		//String CONTEXT_PATH ="http://192.168.11.22:4201";
		String url = CONTEXT_PATH+"/welcome/" + uId;
		String subject = "New Employee Registration";
		StringBuilder mailBody = new StringBuilder("Hi "+name+" ,");
		mailBody.append("<br/><br/>Please click below link for registration:");
		mailBody.append("<br/><br/> <a href='"+url+"'>New Employee Registration</a>");
		Template t = freemarkerConfig.getTemplate("email_template.ftl");
        String html = (FreeMarkerTemplateUtils.processTemplateIntoString(t, mailDomainDto)).replace("MAIL_BODY", mailBody).replace("Title", "New Employee Registration");
		
		mailDomainDto.setSubject(subject);
		mailDomainDto.setMailBody(html);
		mailDomainDto.setTo((mailId == null)?null:mailId);
		mailDomainDto.setBcc(null);
		mailDomainDto.setCc(null);
		
		String msg = null;
		try {
			msg = send(uId, mailDomainDto,false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return msg;
	}
	
	
	
	private String send(String token, MailDomainDto mailDomainDto, Boolean isSaveMailContent) throws Exception {
		String msg = "Failure";

		try {
			String host = "smtp.gmail.com";
			System.out.println("TLS Email Start");
			Properties properties = System.getProperties();
			properties.setProperty("mail.smtp.host", host);
			properties.put("mail.smtp.port", "465");
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

			Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(from));

			if (mailDomainDto.getTo() != null) {
				InternetAddress[] to = InternetAddress.parse(mailDomainDto.getTo());
				message.addRecipients(Message.RecipientType.TO, to);
			}

			if (mailDomainDto.getCc() != null) {
				InternetAddress[] cc = InternetAddress.parse(mailDomainDto.getCc());
				message.addRecipients(Message.RecipientType.CC, cc);
			}
			if (mailDomainDto.getBcc() != null) {
				InternetAddress[] bcc = InternetAddress.parse(mailDomainDto.getBcc());
				message.addRecipients(Message.RecipientType.BCC, bcc);
			}

			message.setSubject(mailDomainDto.getSubject());
			message.setText(mailDomainDto.getMailBody());
			message.setContent(mailDomainDto.getMailBody(), "text/html");

			Transport.send(message);

			msg = "Success";

		} catch (Exception e) {
			msg = "Failure";
			
		}
		System.out.println(msg);
		return msg;
	}


	

}
