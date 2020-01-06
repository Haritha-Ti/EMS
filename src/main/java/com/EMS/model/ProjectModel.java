package com.EMS.model;

import java.security.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.EMS.listener.ModelListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Audited
@EntityListeners(ModelListener.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "Project")
public class ProjectModel extends Auditable<Long> {

	@Id
	@Column(name = "projectId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Cascade(CascadeType.ALL)
	private long projectId;
	private int projectCategory;
	private String projectName, projectDetails;
	private int estimatedHours;
	private Date startDate, endDate, releasingDate;
	private int isBillable;
	private String projectCode;
	private int projectType, isPOC, projectStatus;
	private String clientPointOfContact;

	private long parentProjectId;

	private int projectTier; // 1-level1 project ,2level2 project

	// 1-Semi - monthly submission Type without Daily Tasks
	// 2-Semi - monthly submission Type with Daily Tasks
	// 3-Weekly - Submission Type without Daily Tasks
	// 4-Weekly - Submission Type with Daily Tasks
	private int workflowType;

	private String project_refId;

	@ManyToOne
	private ClientModel clientName;

	@ManyToOne
	private UserModel projectOwner; // approver_level_1

	@ManyToOne
	private ContractModel contract;

	@ManyToOne
	private UserModel onsite_lead; // approver_level_2

	/*
	 * @CreatedBy
	 * 
	 * @ManyToOne private UserModel createdBy;
	 * 
	 * @LastModifiedBy
	 * 
	 * @ManyToOne private UserModel modifiedBy;
	 * 
	 * @CreatedDate private Date createdDate;
	 * 
	 * @LastModifiedDate private Date modifiedDate;
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * public UserModel getCreatedBy() { return createdBy; }
	 * 
	 * 
	 * public void setCreatedBy(UserModel createdBy) { this.createdBy = createdBy; }
	 * 
	 * 
	 * public UserModel getModifiedBy() { return modifiedBy; }
	 * 
	 * 
	 * public void setModifiedBy(UserModel modifiedBy) { this.modifiedBy =
	 * modifiedBy; }
	 * 
	 * 
	 * public Date getCreatedDate() { return createdDate; }
	 * 
	 * 
	 * public void setCreatedDate(Date createdDate) { this.createdDate =
	 * createdDate; }
	 * 
	 * 
	 * public Date getModifiedDate() { return modifiedDate; }
	 * 
	 * 
	 * public void setModifiedDate(Date modifiedDate) { this.modifiedDate =
	 * modifiedDate; }
	 */

	public ProjectModel(long projectId, String projectName, String projectDetails, int estimatedHours, Date startDate,
			Date endDate, int isBillable, String projectCode, int projectType, UserModel projectOwner,
			long parentProjectId, ContractModel contract) {
		super();
		this.projectId = projectId;
		this.parentProjectId = parentProjectId;
		this.projectName = projectName;
		this.projectDetails = projectDetails;
		this.estimatedHours = estimatedHours;
		this.startDate = startDate;
		this.endDate = endDate;
		this.isBillable = isBillable;
		this.projectCode = projectCode;
		this.projectType = projectType;
		this.projectOwner = projectOwner;
		this.contract = contract;
	}

	public long getParentProjectId() {
		return parentProjectId;
	}

	public void setParentProjectId(long parentProjectId) {
		this.parentProjectId = parentProjectId;
	}

	public int getProjectCategory() {
		return projectCategory;
	}

	public void setProjectCategory(int projectCategory) {
		this.projectCategory = projectCategory;
	}

	public String getClientPointOfContact() {
		return clientPointOfContact;
	}

	public void setClientPointOfContact(String clientPointOfContact) { // In case of external projects only
		this.clientPointOfContact = clientPointOfContact;
	}

	public ClientModel getClientName() {
		return clientName;
	}

	public void setClientName(ClientModel clientName) {
		this.clientName = clientName;
	}

	public Date getReleasingDate() {
		return releasingDate;
	}

	public void setReleasingDate(Date releasingDate) {
		this.releasingDate = releasingDate;
	}

	public int getisPOC() {
		return isPOC;
	}

	public void setisPOC(int isPOC) {
		this.isPOC = isPOC;
	}

	public int getprojectStatus() {
		return projectStatus;
	}

	public void setprojectStatus(int projectStatus) {
		this.projectStatus = projectStatus;
	}

	public ProjectModel() {

	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectDetails() {
		return projectDetails;
	}

	public void setProjectDetails(String projectDetails) {
		this.projectDetails = projectDetails;
	}

	public int getEstimatedHours() {
		return estimatedHours;
	}

	public void setEstimatedHours(int estimatedHours) {
		this.estimatedHours = estimatedHours;
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

	public int getisBillable() {
		return isBillable;
	}

	public void setisBillable(int isBillable) {
		this.isBillable = isBillable;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public int getprojectType() {
		return projectType;
	}

	public void setprojectType(int projectType) {
		this.projectType = projectType;
	}

	public UserModel getProjectOwner() {
		return projectOwner;
	}

	public void setProjectOwner(UserModel projectOwner) {
		this.projectOwner = projectOwner;
	}

	public ContractModel getContract() {
		return contract;
	}

	public void setContract(ContractModel contract) {
		this.contract = contract;
	}

	public UserModel getOnsite_lead() {
		return onsite_lead;
	}

	public void setOnsite_lead(UserModel onsite_lead) {
		this.onsite_lead = onsite_lead;
	}

	public String getProject_refId() {
		return project_refId;
	}

	public void setProject_refId(String project_refId) {
		this.project_refId = project_refId;
	}

	public int getProjectTier() {
		return projectTier;
	}

	public void setProjectTier(int projectTier) {
		this.projectTier = projectTier;
	}

	public int getWorkflowType() {
		return workflowType;
	}

	public void setWorkflowType(int workflowType) {
		this.workflowType = workflowType;
	}

}
