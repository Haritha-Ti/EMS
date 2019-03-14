package com.EMS.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="Project")
public class ProjectModel {
	
	@Id
	@Column(name="projectId")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Cascade(CascadeType.ALL)
	private long projectId;
	
	private String projectName,projectDetails;
	private int estimatedHours;
	private Date startDate,endDate,releasingDate;
	private int isBillable;
	private String projectCode;
	private int projectPhase,projectType,isPOC,projectStatus;
	
	
	
	@ManyToOne
	private UserModel projectOwner;
	
	@ManyToOne
	private ContractModel contract;

	

	public ProjectModel(long projectId, String projectName, String projectDetails, int estimatedHours, Date startDate,
			Date endDate, int isBillable, String projectCode, int projectPhase, int projectType, UserModel projectOwner,
			ContractModel contract) {
		super();
		this.projectId = projectId;
		this.projectName = projectName;
		this.projectDetails = projectDetails;
		this.estimatedHours = estimatedHours;
		this.startDate = startDate;
		this.endDate = endDate;
		this.isBillable = isBillable;
		this.projectCode = projectCode;
		this.projectPhase = projectPhase;
		this.projectType = projectType;
		this.projectOwner = projectOwner;
		this.contract = contract;
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

	public int getprojectPhase() {
		return projectPhase;
	}

	public void setprojectPhase(int projectPhase) {
		this.projectPhase = projectPhase;
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
	


	
}
