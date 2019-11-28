package com.EMS.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;
import com.EMS.utility.Constants;



@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name = "TASKTRACK_APPROVAL_FINAL")
public class TaskTrackApprovalFinal extends  Auditable<Long>{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private Double day1,day2,day3,day4,day5,day6,day7,day8,day9,day10,
	day11,day12,day13,day14,day15,day16,day17,day18,day19,day20,
	day21,day22,day23,day24,day25,day26,day27,day28,day29,day30,day31 = 0.0d;

	@ManyToOne
	private UserModel user;

	@OneToOne
	private TaskTrackApproval approvalId;

	public TaskTrackApproval getApprovalId() {
		return approvalId;
	}

	public void setApprovalId(TaskTrackApproval approvalId) {
		this.approvalId = approvalId;
	}

	@ManyToOne
	private ProjectModel project;

	private String projectType;

	private Integer year;

	private Integer month;

	@Column(name = "first_half_status", length = 25)
	private String firstHalfStatus;

	@Column(name = "second_half_status", length = 25)
	private String secondHalfStatus;
	
	@ManyToOne
	private UserModel firstHalfsubmittedBy;
	
	@ManyToOne
	private UserModel secondHalfsubmittedBy;

	//private Date approvedDate;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Double getDay1() {
		return day1;
	}

	public void setDay1(Double day1) {
		this.day1 = day1;
	}

	public Double getDay2() {
		return day2;
	}

	public void setDay2(Double day2) {
		this.day2 = day2;
	}

	public Double getDay3() {
		return day3;
	}

	public void setDay3(Double day3) {
		this.day3 = day3;
	}

	public Double getDay4() {
		return day4;
	}

	public void setDay4(Double day4) {
		this.day4 = day4;
	}

	public Double getDay5() {
		return day5;
	}

	public void setDay5(Double day5) {
		this.day5 = day5;
	}

	public Double getDay6() {
		return day6;
	}

	public void setDay6(Double day6) {
		this.day6 = day6;
	}

	public Double getDay7() {
		return day7;
	}

	public void setDay7(Double day7) {
		this.day7 = day7;
	}

	public Double getDay8() {
		return day8;
	}

	public void setDay8(Double day8) {
		this.day8 = day8;
	}

	public Double getDay9() {
		return day9;
	}

	public void setDay9(Double day9) {
		this.day9 = day9;
	}

	public Double getDay10() {
		return day10;
	}

	public void setDay10(Double day10) {
		this.day10 = day10;
	}

	public Double getDay11() {
		return day11;
	}

	public void setDay11(Double day11) {
		this.day11 = day11;
	}

	public Double getDay12() {
		return day12;
	}

	public void setDay12(Double day12) {
		this.day12 = day12;
	}

	public Double getDay13() {
		return day13;
	}

	public void setDay13(Double day13) {
		this.day13 = day13;
	}

	public Double getDay14() {
		return day14;
	}

	public void setDay14(Double day14) {
		this.day14 = day14;
	}

	public Double getDay15() {
		return day15;
	}

	public void setDay15(Double day15) {
		this.day15 = day15;
	}

	public Double getDay16() {
		return day16;
	}

	public void setDay16(Double day16) {
		this.day16 = day16;
	}

	public Double getDay17() {
		return day17;
	}

	public void setDay17(Double day17) {
		this.day17 = day17;
	}

	public Double getDay18() {
		return day18;
	}

	public void setDay18(Double day18) {
		this.day18 = day18;
	}

	public Double getDay19() {
		return day19;
	}

	public void setDay19(Double day19) {
		this.day19 = day19;
	}

	public Double getDay20() {
		return day20;
	}

	public void setDay20(Double day20) {
		this.day20 = day20;
	}

	public Double getDay21() {
		return day21;
	}

	public void setDay21(Double day21) {
		this.day21 = day21;
	}

	public Double getDay22() {
		return day22;
	}

	public void setDay22(Double day22) {
		this.day22 = day22;
	}

	public Double getDay23() {
		return day23;
	}

	public void setDay23(Double day23) {
		this.day23 = day23;
	}

	public Double getDay24() {
		return day24;
	}

	public void setDay24(Double day24) {
		this.day24 = day24;
	}

	public Double getDay25() {
		return day25;
	}

	public void setDay25(Double day25) {
		this.day25 = day25;
	}

	public Double getDay26() {
		return day26;
	}

	public void setDay26(Double day26) {
		this.day26 = day26;
	}

	public Double getDay27() {
		return day27;
	}

	public void setDay27(Double day27) {
		this.day27 = day27;
	}

	public Double getDay28() {
		return day28;
	}

	public void setDay28(Double day28) {
		this.day28 = day28;
	}

	public Double getDay29() {
		return day29;
	}

	public void setDay29(Double day29) {
		this.day29 = day29;
	}

	public Double getDay30() {
		return day30;
	}

	public void setDay30(Double day30) {
		this.day30 = day30;
	}

	public Double getDay31() {
		return day31;
	}

	public void setDay31(Double day31) {
		this.day31 = day31;
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

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public String getFirstHalfStatus() {
		if(firstHalfStatus == null) {
			firstHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
		}
		return firstHalfStatus;
	}

	public void setFirstHalfStatus(String firstHalfStatus) {
		this.firstHalfStatus = firstHalfStatus;
	}

	public String getSecondHalfStatus() {
		if(secondHalfStatus == null) {
			secondHalfStatus = Constants.TASKTRACK_APPROVER_STATUS_OPEN;
		}
		return secondHalfStatus;
	}
	public void setSecondHalfStatus(String secondHalfStatus) {
		this.secondHalfStatus = secondHalfStatus;
	}

	/*public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}*/

	public UserModel getFirstHalfsubmittedBy() {
		return firstHalfsubmittedBy;
	}

	public void setFirstHalfsubmittedBy(UserModel firstHalfsubmittedBy) {
		this.firstHalfsubmittedBy = firstHalfsubmittedBy;
	}

	public UserModel getSecondHalfsubmittedBy() {
		return secondHalfsubmittedBy;
	}

	public void setSecondHalfsubmittedBy(UserModel secondHalfsubmittedBy) {
		this.secondHalfsubmittedBy = secondHalfsubmittedBy;
	}

	
}
