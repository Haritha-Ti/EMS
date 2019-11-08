package com.EMS.service;

import java.util.Calendar;
import java.util.List;
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
import com.EMS.model.PasswordResetModel;
import com.EMS.model.UserModel;
import com.EMS.repository.PasswordResetRepository;
import com.EMS.utility.Constants;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class PasswordResetServiceImpl implements PasswordResetService{
	
	@Autowired
	private PasswordResetRepository passwordResetRepository;
	
	@Autowired
	EmailNotificationService emailNotificationService;
	
	@Autowired
    private Configuration freemarkerConfig;

	@Value("${CONTEXT_PATH}")
	private String CONTEXT_PATH;
	
	@Override
	public void createPasswordResetTokenForUser(UserModel user, String token) throws Exception{
	    Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.MINUTE, Constants.EMAIL_TOKEN_EXP_DUR);
	    List<PasswordResetModel> passResetModelList = passwordResetRepository.findByUserId(user.getUserId());
	    if(!passResetModelList.isEmpty()) {
	    	PasswordResetModel passResetModel = passResetModelList.get(0);
	    	passResetModel.setExpiryDate(cal.getTime());
			passResetModel.setToken(token);
			passwordResetRepository.save(passResetModel);
	    }
		else {
			PasswordResetModel myToken = new PasswordResetModel(token, user,cal.getTime());
			passwordResetRepository.save(myToken);
		}
	}

	@Override
	public void deletePasswordResetToken(PasswordResetModel passwordResetModel) throws Exception {
		if(passwordResetModel != null & passwordResetModel.getId() != 0) {
			passwordResetRepository.delete(passwordResetModel);
		}
	}
	
	@Override
	public String validatePasswordResetToken(long id, String token) throws Exception{
		PasswordResetModel passToken = validateToken(id,token);
	    if(passToken.getStatus() != null) {
	    	return passToken.getStatus();
	    }
	    return null;
	}
	
	@Override
	public PasswordResetModel validateBeforeResetPassword(long id, String token) throws Exception {
		PasswordResetModel passToken = validateToken(id,token);
		return passToken;
	}
	
	private PasswordResetModel validateToken(long id, String token) {
		PasswordResetModel passToken = null;
		List<PasswordResetModel> passTokenList = passwordResetRepository.findByToken(token);
		if(passTokenList.size() > 0) {
			passToken = passwordResetRepository.findByToken(token).get(0);
		}
		if ((passToken == null) || (passToken.getUser().getUserId() != id)) {
			passToken = new PasswordResetModel();
			passToken.setStatus("Invalid Token");
	        return passToken;
	    }
		Calendar cal = Calendar.getInstance();
	    if ((passToken.getExpiryDate()
	        .getTime() - cal.getTime()
	        .getTime()) <= 0) {
	    	passToken.setStatus("Token Expired");
	        return passToken;
	    }
		return passToken;
	}
	
	@Override
	public String sendMail(String token, UserModel user)  throws Exception{
		
		String msg = "Failure";
		MailDomainDto mailDomainDto = new MailDomainDto();

		String url = CONTEXT_PATH+"/pwdVerify?token=" + token + "&userId="+user.getUserId();
		String subject = "Reset Password";
		StringBuilder mailBody = new StringBuilder("Hi "+user.getFirstName()+" "+user.getLastName()+",");
		mailBody.append("<br/><br/>To reset your password click the link below:");
		mailBody.append("<br/><br/> <a href='"+url+"'>Reset password</a>");
		mailBody.append("<br/><br/>This link will expire in "+Constants.EMAIL_TOKEN_EXP_DUR+" minutes<br/><br/>");
		
		Template t = freemarkerConfig.getTemplate("email_template.ftl");
        String html = (FreeMarkerTemplateUtils.processTemplateIntoString(t, mailDomainDto)).replace("MAIL_BODY", mailBody);
		
		mailDomainDto.setSubject(subject);
		mailDomainDto.setMailBody(html);
		mailDomainDto.setTo((user.getEmail() == null)?null:user.getEmail());
		mailDomainDto.setBcc(null);
		mailDomainDto.setCc(null);
		
		String result = emailNotificationService.sendMail(token, mailDomainDto);
		
		if(result == "Success")
            msg = "Verification link has been successfully sent to your email \""+user.getEmail()+"\"";
		else
			msg = "Unable to send mail";
	    System.out.println(msg); 
		return msg;
	}

}
