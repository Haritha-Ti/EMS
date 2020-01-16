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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name="ApprovalUserAssign")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ApprovalUserAsignModel  extends  Auditable<Long> {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="assign_user_id")
	private long assignUserId;
	
	
	@ManyToOne
	private UserModel userId;
	
	private Date startDate;
	
	private Date endDate;
	
	private  boolean Status ;
	
	@ManyToOne
	private ProjectModel  projectId;

	public UserModel getUserId() {
		return userId;
	}

	public void setUserId(UserModel userId) {
		this.userId = userId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public ProjectModel getProjectId() {
		return projectId;
	}

	public void setProjectId(ProjectModel projectId) {
		this.projectId = projectId;
	}

	public long getAssignUserId() {
		return assignUserId;
	}

	public void setAssignUserId(long assignUserId) {
		this.assignUserId = assignUserId;
	}

	public boolean isStatus() {
		return Status;
	}

	public void setStatus(boolean status) {
		Status = status;
	}
	
	

}
