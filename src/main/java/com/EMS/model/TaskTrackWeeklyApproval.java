package com.EMS.model;
import com.EMS.listener.ModelListener;
import com.EMS.utility.Constants;
import org.hibernate.envers.Audited;
import javax.persistence.*;
import java.util.Date;
@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name = "TASKTRACK_WEEKLY_APPROVAL")
public class TaskTrackWeeklyApproval extends Auditable<Long> {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Date startDate;
    private Date endDate;
    private Double day1, day2, day3, day4, day5, day6, day7;
    
    private Integer year;
    @ManyToOne
    private UserModel user;
    private Date userSubmittedDate;
    @ManyToOne
    private ProjectModel project;
    @Column(name = "timetrack_status", length = 25)
    private String timetrackStatus;
    @ManyToOne
    private UserModel approver1Id;
    @ManyToOne
    private UserModel approver2Id;
    
    @ManyToOne
    private UserModel financeUser;
    @Column(name = "approver1_status", length = 25)
    private String approver1Status;
    @Column(name = "approver2_status", length = 25)
    private String approver2Status;
    
    @Column(name = "financeStatus", length = 25)
    private String financeStatus;
        
    private Date approver1SubmittedDate;
    private Date approver2SubmittedDate;
    
    private Date financeSubmittedDate;
    
    private Date rejectionTime;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
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
    public Date getUserSubmittedDate() {
        return userSubmittedDate;
    }
    public void setUserSubmittedDate(Date userSubmittedDate) {
        this.userSubmittedDate = userSubmittedDate;
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
    public Date getApprover1SubmittedDate() {
        return approver1SubmittedDate;
    }
    public void setApprover1SubmittedDate(Date approver1SubmittedDate) {
        this.approver1SubmittedDate = approver1SubmittedDate;
    }
    public Date getApprover2SubmittedDate() {
        return approver2SubmittedDate;
    }
    public void setApprover2SubmittedDate(Date approver2SubmittedDate) {
        this.approver2SubmittedDate = approver2SubmittedDate;
    }
    public UserModel getFinanceUser() {
        return financeUser;
    }
    public void setFinanceUser(UserModel financeUser) {
        this.financeUser = financeUser;
    }
    public Date getFinanceSubmittedDate() {
        return financeSubmittedDate;
    }
    public void setFinanceSubmittedDate(Date financeSubmittedDate) {
        this.financeSubmittedDate = financeSubmittedDate;
    }
  
    public Date getRejectionTime() {
        return rejectionTime;
    }
    
    public void setRejectionTime(Date rejectionTime) {
        this.rejectionTime = rejectionTime;
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
	public String getFinanceStatus() {
		return financeStatus;
	}
	public void setFinanceStatus(String financeStatus) {
		this.financeStatus = financeStatus;
	}
    
}





