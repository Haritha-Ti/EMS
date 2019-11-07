package com.EMS.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tasktrack_correction")
public class TaskTrackCorrection extends BaseEntity {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	private TaskTrackApprovalFinal finalApprovalId;
	
	@Column(name = "DAY")
	private int  day;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TaskTrackApprovalFinal getFinalApprovalId() {
		return finalApprovalId;
	}

	public void setFinalApprovalId(TaskTrackApprovalFinal finalApprovalId) {
		this.finalApprovalId = finalApprovalId;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}
}
