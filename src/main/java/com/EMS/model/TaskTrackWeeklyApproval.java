package com.EMS.model;

import com.EMS.listener.ModelListener;
import com.EMS.utility.Constants;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name = "TASKTRACK_WEEKLY_APPROVAL")
public class TaskTrackWeeklyApproval extends Auditable<Long> {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long weekNo;

	private Double day1, day2, day3, day4, day5, day6, day7 = 0.0d;

	private Integer year;

	@ManyToOne
	private UserModel user;

	@ManyToOne
	private ProjectModel project;

	@Column(name = "timetrack_status", length = 25)
	private String timetrackStatus;

	@ManyToOne
	private UserModel approver1Id;

	@ManyToOne
	private UserModel approver2Id;

	@Column(name = "approver1_status", length = 25)
	private String approver1Status;

	@Column(name = "approver2_status", length = 25)
	private String approver2Status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getWeekNo() {
		return weekNo;
	}

	public void setWeekNo(long weekNo) {
		this.weekNo = weekNo;
	}

	public Double getDay1() {
		if (day1 == null) {
			day1 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day1);
	}

	public void setDay1(Double day1) {
		this.day1 = Constants.roundToDefaultPrecision(day1);
	}

	public Double getDay2() {
		if (day2 == null) {
			day2 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day2);
	}

	public void setDay2(Double day2) {
		this.day2 = Constants.roundToDefaultPrecision(day2);
	}

	public Double getDay3() {
		if (day3 == null) {
			day3 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day3);
	}

	public void setDay3(Double day3) {
		this.day3 = Constants.roundToDefaultPrecision(day3);
	}

	public Double getDay4() {
		if (day4 == null) {
			day4 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day4);
	}

	public void setDay4(Double day4) {
		this.day4 = Constants.roundToDefaultPrecision(day4);
	}

	public Double getDay5() {
		if (day5 == null) {
			day5 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day5);
	}

	public void setDay5(Double day5) {
		this.day5 = Constants.roundToDefaultPrecision(day5);
	}

	public Double getDay6() {
		if (day6 == null) {
			day6 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day6);
	}

	public void setDay6(Double day6) {
		this.day6 = Constants.roundToDefaultPrecision(day6);
	}

	public Double getDay7() {
		if (day7 == null) {
			day7 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day7);
	}

	public void setDay7(Double day7) {
		this.day7 = Constants.roundToDefaultPrecision(day7);
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
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

	public String getTimetrackStatus() {
		return timetrackStatus;
	}

	public void setTimetrackStatus(String timetrackStatus) {
		this.timetrackStatus = timetrackStatus;
	}
	public UserModel getApprover1Id() {
		return approver1Id;
	}

	public void setApprover1Id(UserModel approver1Id) {
		this.approver1Id = approver1Id;
	}

	public UserModel getApprover2Id() {
		return approver2Id;
	}

	public void setApprover2Id(UserModel approver2Id) {
		this.approver2Id = approver2Id;
	}
	public String getApprover1Status() {
		return approver1Status;
	}

	public void setApprover1Status(String approver1Status) {
		this.approver1Status = approver1Status;
	}

	public String getApprover2Status() {
		return approver2Status;
	}

	public void setApprover2Status(String approver2Status) {
		this.approver2Status = approver2Status;
	}



}
