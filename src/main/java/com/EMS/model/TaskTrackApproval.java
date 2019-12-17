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
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;
import com.EMS.utility.Constants;

@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name = "TASKTRACK_APPROVAL")
public class TaskTrackApproval extends Auditable<Long> {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Transient
	private String firstName, lastName;

	private Double day1, day2, day3, day4, day5, day6, day7, day8, day9, day10, day11, day12, day13, day14, day15,
			day16, day17, day18, day19, day20, day21, day22, day23, day24, day25, day26, day27, day28, day29, day30,
			day31 = 0.0d;

	@ManyToOne
	private UserModel user;

	@ManyToOne
	private ProjectModel project;

	private String projectType;

	private Integer year;

	private Integer month;

	@Column(name = "first_half_status", length = 25)
	private String firstHalfStatus;

	@Column(name = "second_half_status", length = 25)
	private String secondHalfStatus;

	// private Date approvedDate;

	private Long updatedBy;

	@ManyToOne
	private UserModel firstHalfsubmittedBy;

	@ManyToOne
	private UserModel secondHalfsubmittedBy;

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public Double getDay8() {
		if (day8 == null) {
			day8 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day8);
	}

	public void setDay8(Double day8) {
		this.day8 = Constants.roundToDefaultPrecision(day8);
	}

	public Double getDay9() {
		if (day9 == null) {
			day9 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day9);
	}

	public void setDay9(Double day9) {
		this.day9 = Constants.roundToDefaultPrecision(day9);
	}

	public Double getDay10() {
		if (day10 == null) {
			day10 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day10);
	}

	public void setDay10(Double day10) {
		this.day10 = Constants.roundToDefaultPrecision(day10);
	}

	public Double getDay11() {
		if (day11 == null) {
			day11 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day11);
	}

	public void setDay11(Double day11) {
		this.day11 = Constants.roundToDefaultPrecision(day11);
	}

	public Double getDay12() {
		if (day12 == null) {
			day12 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day12);
	}

	public void setDay12(Double day12) {
		this.day12 = Constants.roundToDefaultPrecision(day12);
	}

	public Double getDay13() {
		if (day13 == null) {
			day13 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day13);
	}

	public void setDay13(Double day13) {
		this.day13 = Constants.roundToDefaultPrecision(day13);
	}

	public Double getDay14() {
		if (day14 == null) {
			day14 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day14);
	}

	public void setDay14(Double day14) {
		this.day14 = Constants.roundToDefaultPrecision(day14);
	}

	public Double getDay15() {
		if (day15 == null) {
			day15 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day15);
	}

	public void setDay15(Double day15) {
		this.day15 = Constants.roundToDefaultPrecision(day15);
	}

	public Double getDay16() {
		if (day16 == null) {
			day16 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day16);
	}

	public void setDay16(Double day16) {
		this.day16 = Constants.roundToDefaultPrecision(day16);
	}

	public Double getDay17() {
		if (day17 == null) {
			day17 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day17);
	}

	public void setDay17(Double day17) {
		this.day17 = Constants.roundToDefaultPrecision(day17);
	}

	public Double getDay18() {
		if (day18 == null) {
			day18 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day18);
	}

	public void setDay18(Double day18) {
		this.day18 = Constants.roundToDefaultPrecision(day18);
	}

	public Double getDay19() {
		if (day19 == null) {
			day19 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day19);
	}

	public void setDay19(Double day19) {
		this.day19 = Constants.roundToDefaultPrecision(day19);
	}

	public Double getDay20() {
		if (day20 == null) {
			day20 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day20);
	}

	public void setDay20(Double day20) {
		this.day20 = Constants.roundToDefaultPrecision(day20);
	}

	public Double getDay21() {
		if (day21 == null) {
			day21 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day21);
	}

	public void setDay21(Double day21) {
		this.day21 = Constants.roundToDefaultPrecision(day21);
	}

	public Double getDay22() {
		if (day22 == null) {
			day22 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day22);
	}

	public void setDay22(Double day22) {
		this.day22 = Constants.roundToDefaultPrecision(day22);
	}

	public Double getDay23() {
		if (day23 == null) {
			day23 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day23);
	}

	public void setDay23(Double day23) {
		this.day23 = Constants.roundToDefaultPrecision(day23);
	}

	public Double getDay24() {
		if (day24 == null) {
			day24 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day24);
	}

	public void setDay24(Double day24) {
		this.day24 = Constants.roundToDefaultPrecision(day24);
	}

	public Double getDay25() {
		if (day25 == null) {
			day25 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day25);
	}

	public void setDay25(Double day25) {
		this.day25 = Constants.roundToDefaultPrecision(day25);
	}

	public Double getDay26() {
		if (day26 == null) {
			day26 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day26);
	}

	public void setDay26(Double day26) {
		this.day26 = Constants.roundToDefaultPrecision(day26);
	}

	public Double getDay27() {
		if (day27 == null) {
			day27 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day27);
	}

	public void setDay27(Double day27) {
		this.day27 = Constants.roundToDefaultPrecision(day27);
	}

	public Double getDay28() {
		if (day28 == null) {
			day28 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day28);
	}

	public void setDay28(Double day28) {
		this.day28 = Constants.roundToDefaultPrecision(day28);
	}

	public Double getDay29() {
		if (day29 == null) {
			day29 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day29);
	}

	public void setDay29(Double day29) {
		this.day29 = Constants.roundToDefaultPrecision(day29);
	}

	public Double getDay30() {
		if (day30 == null) {
			day30 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day30);
	}

	public void setDay30(Double day30) {
		this.day30 = Constants.roundToDefaultPrecision(day30);
	}

	public Double getDay31() {
		if (day31 == null) {
			day31 = 0.0d;
		}
		return Constants.roundToDefaultPrecision(day31);
	}

	public void setDay31(Double day31) {
		this.day31 = Constants.roundToDefaultPrecision(day31);
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
		return firstHalfStatus;
	}

	public void setFirstHalfStatus(String firstHalfStatus) {
		this.firstHalfStatus = firstHalfStatus;
	}

	public String getSecondHalfStatus() {
		return secondHalfStatus;
	}

	public void setSecondHalfStatus(String secondHalfStatus) {
		this.secondHalfStatus = secondHalfStatus;
	}

	/*
	 * public Date getApprovedDate() { return approvedDate; }
	 * 
	 * public void setApprovedDate(Date approvedDate) { this.approvedDate =
	 * approvedDate;
	 * 
	 * }
	 */

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
