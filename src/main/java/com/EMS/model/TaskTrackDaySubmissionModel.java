package com.EMS.model;

import javax.persistence.*;

import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;

@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name = "tasktrack_day_submission")
public class TaskTrackDaySubmissionModel  extends Auditable<String> {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "month")
    private int month;
    
    @Column(name = "first_approval_day")
    private int firstApprovalDay;
    
    @Column(name = "second_approval_day")
    private int secondApprovalDay;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getFirstApprovalDay() {
		return firstApprovalDay;
	}

	public void setFirstApprovalDay(int firstApprovalDay) {
		this.firstApprovalDay = firstApprovalDay;
	}

	public int getSecondApprovalDay() {
		return secondApprovalDay;
	}

	public void setSecondApprovalDay(int secondApprovalDay) {
		this.secondApprovalDay = secondApprovalDay;
	}
}


