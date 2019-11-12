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
@Table(name = "userLeaveSummary")
public class UserLeaveSummary  extends Auditable<String> {

    @Id
    @Column(name = "userLeaveSummaryId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long leaveSummaryId;

    @ManyToOne
    private UserModel user;

    private Date leaveDate;

    private String leaveType;
   /* private int month;
    private int year;*/


    public long getLeaveSummaryId() {
        return leaveSummaryId;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

  /*  public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
*/

}
