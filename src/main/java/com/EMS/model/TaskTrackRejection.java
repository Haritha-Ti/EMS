package com.EMS.model;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table (name="tasktrack_rejection")

public class TaskTrackRejection {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne
	private UserModel user;

	@ManyToOne
	private ProjectModel project;

	@ManyToOne
	private UserModel rejectedBy;
	
	private Integer rejectionLevel;

	private Integer month;

	private Integer year;

	private Date startDate;

	private Date endDate;

	private Date rejectionTime;

	private String remark;

	private String status;

	private String cycle;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getRejectionLevel() {
		return rejectionLevel;
	}

	public void setRejectionLevel(Integer rejectionLevel) {
		this.rejectionLevel = rejectionLevel;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public ProjectModel getProject() {
		return project;
	}

	public void setProject(ProjectModel project) {
		this.project = project;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
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

	public UserModel getRejectedBy() {
		return rejectedBy;
	}

	public void setRejectedBy(UserModel rejectedBy) {
		this.rejectedBy = rejectedBy;
	}
	
	public Date getRejectionTime() {
		return rejectionTime;
	}

	public void setRejectionTime(Date rejectionTime) {
		this.rejectionTime = rejectionTime;
	}
}
