package com.EMS.model;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;

@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name = "tasktrack_day_submission")
public class TaskTrackDaySubmissionModel extends Auditable<String> {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "month", unique = true)
	private Integer month;

	@Column(name = "first_approval_day")
	private Integer firstApprovalDay;

	@Column(name = "second_approval_day")
	private Integer secondApprovalDay;

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

	public Integer getFirstApprovalDay() {
		return firstApprovalDay;
	}

	public void setFirstApprovalDay(Integer firstApprovalDay) {
		this.firstApprovalDay = firstApprovalDay;
	}

	public Integer getSecondApprovalDay() {
		return secondApprovalDay;
	}

	public void setSecondApprovalDay(Integer secondApprovalDay) {
		this.secondApprovalDay = secondApprovalDay;
	}

}
