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
@Table(name = "user_leave")
public class LeaveModel extends  Auditable<String>{
	
	@Id
	@Column(name = "userLeaveId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long leaveId;
	
	@ManyToOne
	private UserModel user;
	

	private Double leaveCount;
	private String leaveType;
	private Date leaveFrom;
	private Date leaveTo;
	private String leaveReason;
	private String status;
	private Date appliedDate;
	private Date approvedDate;
	
	
	public long getLeaveId() {
		return leaveId;
	}
	
	public Double getLeaveCount() {
		return leaveCount;
	}

	public void setLeaveCount(Double leaveCount) {
		this.leaveCount = leaveCount;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public void setLeaveId(long leaveId) {
		this.leaveId = leaveId;
	}
	
	public UserModel getUser() {
		return user;
	}
	
	public void setUser(UserModel user) {
		this.user = user;
	}
	
	public Date getLeaveFrom() {
		return leaveFrom;
	}
	
	public void setLeaveFrom(Date leaveFrom) {
		this.leaveFrom = leaveFrom;
	}
	
	public Date getLeaveTo() {
		return leaveTo;
	}
	
	public void setLeaveTo(Date leaveTo) {
		this.leaveTo = leaveTo;
	}

	public String getLeaveReason() {
		return leaveReason;
	}

	public void setLeaveReason(String leaveReason) {
		this.leaveReason = leaveReason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getAppliedDate() {
		return appliedDate;
	}

	public void setAppliedDate(Date appliedDate) {
		this.appliedDate = appliedDate;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}
	
	
	
	
}
