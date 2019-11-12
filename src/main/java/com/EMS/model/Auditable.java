package com.EMS.model;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Audited
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable<U> {
	
	@LastModifiedBy
	@Column(name="user_in_action")
	private U user_in_action;
	
	@LastModifiedDate
	@Column(name="trx_date")
	private U trx_date;

	public U getUser_in_action() {
		return user_in_action;
	}

	public void setUser_in_action(U user_in_action) {
		this.user_in_action = user_in_action;
	}

	public U getTrx_date() {
		return trx_date;
	}

	public void setTrx_date(U trx_date) {
		this.trx_date = trx_date;
	}

    
	
	
	

}
