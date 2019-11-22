package com.EMS.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "email_notification")
public class MailDomainModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long mailDomainId;
	private String mailContent;
	private String mailTo;
	private String cc;
	private String bcc;
	private Date mail_timestamp;
	private int status;
	private String mailFrom;
	
	



	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public Date getMail_timestamp() {
		return mail_timestamp;
	}

	public void setMail_timestamp(Date mail_timestamp) {
		this.mail_timestamp = mail_timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getMailDomainId() {
		return mailDomainId;
	}

	public void setMailDomainId(long mailDomainId) {
		this.mailDomainId = mailDomainId;
	}

	public String getMailContent() {
		return mailContent;
	}

	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	public String getMailTo() {
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

}
