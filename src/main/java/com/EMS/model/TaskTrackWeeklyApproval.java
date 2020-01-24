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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weekly_approval_id")
    private long weeklyApprovalId;
    private Date startDate;
    private Date endDate;
    private Double day1, day2, day3, day4, day5, day6, day7;
    
  
    @ManyToOne
    private UserModel user;
    @ManyToOne
    private ProjectModel project;
    
    private String timetrackFinalStatus;
    @ManyToOne
    private UserModel approver1;
    @ManyToOne
    private UserModel approver2;
    
    @ManyToOne
    private UserModel financeUser;

    public long getWeeklyApprovalId() {
		return weeklyApprovalId;
	}
	public void setWeeklyApprovalId(long weeklyApprovalId) {
		this.weeklyApprovalId = weeklyApprovalId;
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
    
    public UserModel getApprover1() {
		return approver1;
	}
	public void setApprover1(UserModel approver1) {
		this.approver1 = approver1;
	}
	public UserModel getApprover2() {
		return approver2;
	}
	public void setApprover2(UserModel approver2) {
		this.approver2 = approver2;
	}
	public UserModel getFinanceUser() {
        return financeUser;
    }
    public void setFinanceUser(UserModel financeUser) {
        this.financeUser = financeUser;
    }
  
	public String getTimetrackFinalStatus() {
		if(timetrackFinalStatus == null)
			timetrackFinalStatus = Constants.FinalStatus.TASKTRACK_OPEN;
		return timetrackFinalStatus;
	}
	public void setTimetrackFinalStatus(String timetrackFinalStatus) {
		this.timetrackFinalStatus = timetrackFinalStatus;
	}
    
	
}





