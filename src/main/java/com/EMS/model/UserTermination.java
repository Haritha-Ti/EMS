package com.EMS.model;

import java.util.Date;

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


@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name="userTermination")
public class UserTermination  extends Auditable<Long> {
	
	@Id
	@Column(name = "termId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long termId;
	
	Date terminationDate;
	String termType,termReason;
	
	@ManyToOne
	private UserModel consultant;
	

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public String getTermType() {
		return termType;
	}

	public void setTermType(String termType) {
		this.termType = termType;
	}

	public String getTermReason() {
		return termReason;
	}

	public void setTermReason(String termReason) {
		this.termReason = termReason;
	}

	public UserModel getConsultant() {
		return consultant;
	}

	public void setConsultant(UserModel consultant) {
		this.consultant = consultant;
	}

	public UserTermination(Date terminationDate, String termType, String termReason, UserModel consultant) {
		super();
		this.terminationDate = terminationDate;
		this.termType = termType;
		this.termReason = termReason;
		this.consultant = consultant;
	}
	public UserTermination() {
		
	}

	public long getTermId() {
		return termId;
	}

	public void setTermId(long termId) {
		this.termId = termId;
	}
	
	
	
	
}
