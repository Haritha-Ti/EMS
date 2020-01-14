package com.EMS.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Audited
@EntityListeners(ModelListener.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "client_user")
public class ClientUserModel  extends Auditable<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long clientUserId;
	
	
	@ManyToOne
	private UserModel userId;
	
	@ManyToOne
	private Region region;
	
	private String firstName, lastName;
	private String email;

	private String clientCompany;
	
	private int gender;
	
	private boolean active;
	
	private long contact;
	
	// private Address address;

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getClientCompany() {
		return clientCompany;
	}

	public void setClientCompany(String clientCompany) {
		this.clientCompany = clientCompany;
	}
	
	
	public long getClientUserId() {
		return clientUserId;
	}
	public void setClientUserId(long clientUserId) {
		this.clientUserId = clientUserId;
	}
	public UserModel getUserId() {
		return userId;
	}
	public void setUserId(UserModel userId) {
		this.userId = userId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public long getContact() {
		return contact;
	}

	public void setContact(long contact) {
		this.contact = contact;
	}
	
	
	
}
