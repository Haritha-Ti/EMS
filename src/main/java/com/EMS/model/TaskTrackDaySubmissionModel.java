package com.EMS.model;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "tasktrack_day_submission")
public class TaskTrackDaySubmissionModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "month", unique = true)
    private Integer month;
    
    @Column(name = "first_approval_day")
    private Date firstApprovalDay;
    
    @Column(name = "second_approval_day")
    private Date secondApprovalDay;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Date getFirstApprovalDay() {
		return firstApprovalDay;
	}

	public void setFirstApprovalDay(Date firstApprovalDay) {
		this.firstApprovalDay = firstApprovalDay;
	}

	public Date getSecondApprovalDay() {
		return secondApprovalDay;
	}

	public void setSecondApprovalDay(Date secondApprovalDay) {
		this.secondApprovalDay = secondApprovalDay;
	}
}


