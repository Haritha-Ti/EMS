package com.EMS.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mail_domain_model")
public class MailDomainModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long mailDomainId;
	private String mailContent;
	private String mailTo;
	private String cc;
	private String bcc;

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
