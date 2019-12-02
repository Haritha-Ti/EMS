package com.EMS.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;

@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name = "Employment_Details")
public class EmploymentDetailsModel extends Auditable<Long> {
	
	@Id
	@Column(name = "employment_detailsId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long employmentDetailsId;

	@ManyToOne
	@JoinColumn(name = "userId" , unique=true)
	private UserModel user;

	@Column(name = "contract_startdate")
	private Date  contractStartdate;
	
	@Column(name = "contract_enddate")
	private Date  contractEnddate;
	
	@Column(name = "regularization_date")
	private Date regularizationDate;
	
	@Column(name = "newcpplevel")
	private String newCppLevel;
	
	@Column(name = "newcpplevel_eff_date")
	private Date newCppLevelEffDate;
	
	@Column(name = "visastatus")
	private String visaStatus;
	
	@Column(name = "visatype")
	private String visaType;
	
	@Column(name = "visa_expdate")
	private Date visaExpDate;
	
	@Column(name = "source")
	private String source;
	
	@Column(name = "referredby")
	private String referredby;
	
	@Column(name = "termtype")
	private String termtype;
	
	
	@Column(name = "lastwork_date")
	private Date lastWorkDate;
	
	@Column(name = "paythru_date")
	private Date payThruDate;
	
	@Column(name = "termination_date")
	private Date terminationDate;
	
	@Column(name = "termination_reason")
	private String terminationReason;

	public long getEmploymentDetailsId() {
		return employmentDetailsId;
	}

	public void setEmploymentDetailsId(long employmentDetailsId) {
		this.employmentDetailsId = employmentDetailsId;
	}

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public Date getContractStartdate() {
		return contractStartdate;
	}

	public void setContractStartdate(Date contractStartdate) {
		this.contractStartdate = contractStartdate;
	}

	public Date getContractEnddate() {
		return contractEnddate;
	}

	public void setContractEnddate(Date contractEnddate) {
		this.contractEnddate = contractEnddate;
	}

	public Date getRegularizationDate() {
		return regularizationDate;
	}

	public void setRegularizationDate(Date regularizationDate) {
		this.regularizationDate = regularizationDate;
	}

	public String getNewCppLevel() {
		return newCppLevel;
	}

	public void setNewCppLevel(String newCppLevel) {
		this.newCppLevel = newCppLevel;
	}

	public Date getNewCppLevelEffDate() {
		return newCppLevelEffDate;
	}

	public void setNewCppLevelEffDate(Date newCppLevelEffDate) {
		this.newCppLevelEffDate = newCppLevelEffDate;
	}

	public String getVisaStatus() {
		return visaStatus;
	}

	public void setVisaStatus(String visaStatus) {
		this.visaStatus = visaStatus;
	}

	public String getVisaType() {
		return visaType;
	}

	public void setVisaType(String visaType) {
		this.visaType = visaType;
	}

	public Date getVisaExpDate() {
		return visaExpDate;
	}

	public void setVisaExpDate(Date visaExpDate) {
		this.visaExpDate = visaExpDate;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getReferredby() {
		return referredby;
	}

	public void setReferredby(String referredby) {
		this.referredby = referredby;
	}

	public String getTermtype() {
		return termtype;
	}

	public void setTermtype(String termtype) {
		this.termtype = termtype;
	}

	public Date getLastWorkDate() {
		return lastWorkDate;
	}

	public void setLastWorkDate(Date lastWorkDate) {
		this.lastWorkDate = lastWorkDate;
	}

	public Date getPayThruDate() {
		return payThruDate;
	}

	public void setPayThruDate(Date payThruDate) {
		this.payThruDate = payThruDate;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public String getTerminationReason() {
		return terminationReason;
	}

	public void setTerminationReason(String terminationReason) {
		this.terminationReason = terminationReason;
	}
	 
	 

}
