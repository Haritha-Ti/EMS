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

import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;

@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name = "tasktrack_approval_semimonthly")
public class TasktrackApprovalSemiMonthly extends Auditable<Long> {

	@Id
	@Column(name = "monthly_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private Double day1, day2, day3, day4, day5, day6, day7, day8, day9, day10, day11, day12, day13, day14, day15,
			day16, day17, day18, day19, day20, day21, day22, day23, day24, day25, day26, day27, day28, day29, day30,
			day31 = 0.0d;

	@ManyToOne
	private UserModel user;

	@ManyToOne
	private ProjectModel project;

	private Integer year;

	private Integer month;

	private String userFirstHalfStatus;

	private Date userFirstHalfSubmittedDate;

	private String userSecondHalfStatus;

	private Date userSecondHalfSubmittedDate;

	@ManyToOne
	private UserModel firstHalfApproverOneId;

	@ManyToOne
	private UserModel secondHalfApproverOneId;

	private String approverOneFirstHalfStatus;

	private Date approverOneFirstHalfSubmittedDate;

	private String approverOneSecondHalfStatus;

	private Date approverOneSecondHalfSubmittedDate;

	@ManyToOne
	private UserModel firstHalfAapproverTwoId;

	@ManyToOne
	private UserModel secondHalfAapproverTwoId;

	private String approverTwoFirstHalfStatus;

	private Date approverTwoFirstHalfSubmittedDate;

	private String approverTwoSecondHalfStatus;

	private Date approverTwoSecondHalfSubmittedDate;

	@ManyToOne
	private UserModel financeId;

	private String financeFirstHalfStatus;

	private Date financeFirstHalfSubmittedDate;

	private String financeSecondHalfStatus;

	private Date financeSecondHalfSubmittedDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Double getDay1() {
		return day1 == null ? 0d : day1;
	}

	public void setDay1(Double day1) {
		this.day1 = day1;
	}

	public Double getDay2() {
		return day2 == null ? 0d : day2;
	}

	public void setDay2(Double day2) {
		this.day2 = day2;
	}

	public Double getDay3() {
		return day3 == null ? 0d : day3;
	}

	public void setDay3(Double day3) {
		this.day3 = day3;
	}

	public Double getDay4() {
		return day4 == null ? 0d : day4;
	}

	public void setDay4(Double day4) {
		this.day4 = day4;
	}

	public Double getDay5() {
		return day5 == null ? 0d : day5;
	}

	public void setDay5(Double day5) {
		this.day5 = day5;
	}

	public Double getDay6() {
		return day6 == null ? 0d : day6;
	}

	public void setDay6(Double day6) {
		this.day6 = day6;
	}

	public Double getDay7() {
		return day7 == null ? 0d : day7;
	}

	public void setDay7(Double day7) {
		this.day7 = day7;
	}

	public Double getDay8() {
		return day8 == null ? 0d : day8;
	}

	public void setDay8(Double day8) {
		this.day8 = day8;
	}

	public Double getDay9() {
		return day9 == null ? 0d : day9;
	}

	public void setDay9(Double day9) {
		this.day9 = day9;
	}

	public Double getDay10() {
		return day10 == null ? 0d : day10;
	}

	public void setDay10(Double day10) {
		this.day10 = day10;
	}

	public Double getDay11() {
		return day11 == null ? 0d : day11;
	}

	public void setDay11(Double day11) {
		this.day11 = day11;
	}

	public Double getDay12() {
		return day12 == null ? 0d : day12;
	}

	public void setDay12(Double day12) {
		this.day12 = day12;
	}

	public Double getDay13() {
		return day13 == null ? 0d : day13;
	}

	public void setDay13(Double day13) {
		this.day13 = day13;
	}

	public Double getDay14() {
		return day14 == null ? 0d : day14;
	}

	public void setDay14(Double day14) {
		this.day14 = day14;
	}

	public Double getDay15() {
		return day15 == null ? 0d : day15;
	}

	public void setDay15(Double day15) {
		this.day15 = day15;
	}

	public Double getDay16() {
		return day16 == null ? 0d : day16;
	}

	public void setDay16(Double day16) {
		this.day16 = day16;
	}

	public Double getDay17() {
		return day17 == null ? 0d : day17;
	}

	public void setDay17(Double day17) {
		this.day17 = day17;
	}

	public Double getDay18() {
		return day18 == null ? 0d : day18;
	}

	public void setDay18(Double day18) {
		this.day18 = day18;
	}

	public Double getDay19() {
		return day19 == null ? 0d : day19;
	}

	public void setDay19(Double day19) {
		this.day19 = day19;
	}

	public Double getDay20() {
		return day20 == null ? 0d : day20;
	}

	public void setDay20(Double day20) {
		this.day20 = day20;
	}

	public Double getDay21() {
		return day21 == null ? 0d : day21;
	}

	public void setDay21(Double day21) {
		this.day21 = day21;
	}

	public Double getDay22() {
		return day22 == null ? 0d : day22;
	}

	public void setDay22(Double day22) {
		this.day22 = day22;
	}

	public Double getDay23() {
		return day23 == null ? 0d : day23;
	}

	public void setDay23(Double day23) {
		this.day23 = day23;
	}

	public Double getDay24() {
		return day24 == null ? 0d : day24;
	}

	public void setDay24(Double day24) {
		this.day24 = day24;
	}

	public Double getDay25() {
		return day25 == null ? 0d : day25;
	}

	public void setDay25(Double day25) {
		this.day25 = day25;
	}

	public Double getDay26() {
		return day26 == null ? 0d : day26;
	}

	public void setDay26(Double day26) {
		this.day26 = day26;
	}

	public Double getDay27() {
		return day27 == null ? 0d : day27;
	}

	public void setDay27(Double day27) {
		this.day27 = day27;
	}

	public Double getDay28() {
		return day28 == null ? 0d : day28;
	}

	public void setDay28(Double day28) {
		this.day28 = day28;
	}

	public Double getDay29() {
		return day29 == null ? 0d : day29;
	}

	public void setDay29(Double day29) {
		this.day29 = day29;
	}

	public Double getDay30() {
		return day30 == null ? 0d : day30;
	}

	public void setDay30(Double day30) {
		this.day30 = day30;
	}

	public Double getDay31() {
		return day31 == null ? 0d : day31;
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

	public String getUserFirstHalfStatus() {
		return userFirstHalfStatus;
	}

	public void setUserFirstHalfStatus(String userFirstHalfStatus) {
		this.userFirstHalfStatus = userFirstHalfStatus;
	}

	public Date getUserFirstHalfSubmittedDate() {
		return userFirstHalfSubmittedDate;
	}

	public void setUserFirstHalfSubmittedDate(Date userFirstHalfSubmittedDate) {
		this.userFirstHalfSubmittedDate = userFirstHalfSubmittedDate;
	}

	public String getUserSecondHalfStatus() {
		return userSecondHalfStatus;
	}

	public void setUserSecondHalfStatus(String userSecondHalfStatus) {
		this.userSecondHalfStatus = userSecondHalfStatus;
	}

	public Date getUserSecondHalfSubmittedDate() {
		return userSecondHalfSubmittedDate;
	}

	public void setUserSecondHalfSubmittedDate(Date userSecondHalfSubmittedDate) {
		this.userSecondHalfSubmittedDate = userSecondHalfSubmittedDate;
	}

	public UserModel getFirstHalfApproverOneId() {
		return firstHalfApproverOneId;
	}

	public void setFirstHalfApproverOneId(UserModel firstHalfApproverOneId) {
		this.firstHalfApproverOneId = firstHalfApproverOneId;
	}

	public UserModel getSecondHalfApproverOneId() {
		return secondHalfApproverOneId;
	}

	public void setSecondHalfApproverOneId(UserModel secondHalfApproverOneId) {
		this.secondHalfApproverOneId = secondHalfApproverOneId;
	}

	public String getApproverOneFirstHalfStatus() {
		return approverOneFirstHalfStatus;
	}

	public void setApproverOneFirstHalfStatus(String approverOneFirstHalfStatus) {
		this.approverOneFirstHalfStatus = approverOneFirstHalfStatus;
	}

	public Date getApproverOneFirstHalfSubmittedDate() {
		return approverOneFirstHalfSubmittedDate;
	}

	public void setApproverOneFirstHalfSubmittedDate(Date approverOneFirstHalfSubmittedDate) {
		this.approverOneFirstHalfSubmittedDate = approverOneFirstHalfSubmittedDate;
	}

	public String getApproverOneSecondHalfStatus() {
		return approverOneSecondHalfStatus;
	}

	public void setApproverOneSecondHalfStatus(String approverOneSecondHalfStatus) {
		this.approverOneSecondHalfStatus = approverOneSecondHalfStatus;
	}

	public Date getApproverOneSecondHalfSubmittedDate() {
		return approverOneSecondHalfSubmittedDate;
	}

	public void setApproverOneSecondHalfSubmittedDate(Date approverOneSecondHalfSubmittedDate) {
		this.approverOneSecondHalfSubmittedDate = approverOneSecondHalfSubmittedDate;
	}

	public UserModel getFirstHalfApproverTwoId() {
		return firstHalfAapproverTwoId;
	}

	public void setFirstHalfApproverTwoId(UserModel firstHalfAapproverTwoId) {
		this.firstHalfAapproverTwoId = firstHalfAapproverTwoId;
	}

	public UserModel getSecondHalfApproverTwoId() {
		return secondHalfAapproverTwoId;
	}

	public void setSecondHalfApproverTwoId(UserModel secondHalfAapproverTwoId) {
		this.secondHalfAapproverTwoId = secondHalfAapproverTwoId;
	}

	public String getApproverTwoFirstHalfStatus() {
		return approverTwoFirstHalfStatus;
	}

	public void setApproverTwoFirstHalfStatus(String approverTwoFirstHalfStatus) {
		this.approverTwoFirstHalfStatus = approverTwoFirstHalfStatus;
	}

	public Date getApproverTwoFirstHalfSubmittedDate() {
		return approverTwoFirstHalfSubmittedDate;
	}

	public void setApproverTwoFirstHalfSubmittedDate(Date approverTwoFirstHalfSubmittedDate) {
		this.approverTwoFirstHalfSubmittedDate = approverTwoFirstHalfSubmittedDate;
	}

	public String getApproverTwoSecondHalfStatus() {
		return approverTwoSecondHalfStatus;
	}

	public void setApproverTwoSecondHalfStatus(String approverTwoSecondHalfStatus) {
		this.approverTwoSecondHalfStatus = approverTwoSecondHalfStatus;
	}

	public Date getApproverTwoSecondHalfSubmittedDate() {
		return approverTwoSecondHalfSubmittedDate;
	}

	public void setApproverTwoSecondHalfSubmittedDate(Date approverTwoSecondHalfSubmittedDate) {
		this.approverTwoSecondHalfSubmittedDate = approverTwoSecondHalfSubmittedDate;
	}

	public UserModel getFinanceId() {
		return financeId;
	}

	public void setFinanceId(UserModel financeId) {
		this.financeId = financeId;
	}

	public String getFinanceFirstHalfStatus() {
		return financeFirstHalfStatus;
	}

	public void setFinanceFirstHalfStatus(String financeFirstHalfStatus) {
		this.financeFirstHalfStatus = financeFirstHalfStatus;
	}

	public Date getFinanceFirstHalfSubmittedDate() {
		return financeFirstHalfSubmittedDate;
	}

	public void setFinanceFirstHalfSubmittedDate(Date financeFirstHalfSubmittedDate) {
		this.financeFirstHalfSubmittedDate = financeFirstHalfSubmittedDate;
	}

	public String getFinanceSecondHalfStatus() {
		return financeSecondHalfStatus;
	}

	public void setFinanceSecondHalfStatus(String financeSecondHalfStatus) {
		this.financeSecondHalfStatus = financeSecondHalfStatus;
	}

	public Date getFinanceSecondHalfSubmittedDate() {
		return financeSecondHalfSubmittedDate;
	}

	public void setFinanceSecondHalfSubmittedDate(Date financeSecondHalfSubmittedDate) {
		this.financeSecondHalfSubmittedDate = financeSecondHalfSubmittedDate;
	}

}
