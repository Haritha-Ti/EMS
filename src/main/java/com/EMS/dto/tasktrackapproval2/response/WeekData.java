package com.EMS.dto.tasktrackapproval2.response;

import java.util.Date;

public class WeekData {

	private double totalHour;
	
	private String weekStart;
	
	private String weekEnd;
	
	private String approverSatus;
	
	private String rejectionRemark;
	
	private String approver2Status;
	
	private String finalStatus;

	public double getTotalHour() {
		return totalHour;
	}

	public void setTotalHour(double totalHour) {
		this.totalHour = totalHour;
	}

	public String getWeekStart() {
		return weekStart;
	}

	public void setWeekStart(String weekStart) {
		this.weekStart = weekStart;
	}

	public String getWeekEnd() {
		return weekEnd;
	}

	public void setWeekEnd(String weekEnd) {
		this.weekEnd = weekEnd;
	}

	public String getApproverSatus() {
		return approverSatus;
	}

	public void setApproverSatus(String approverSatus) {
		this.approverSatus = approverSatus;
	}
	
	public WeekData(double totalhours,String weekStart,String weekEnd,String approverSatus,String rejectionRemark,String approver2Status,String finalStatus) {
		this.totalHour = totalhours;
		this.weekStart = weekStart;
		this.weekEnd = weekEnd;
		this.approverSatus = approverSatus;
		this.rejectionRemark = rejectionRemark;
		this.approver2Status = approver2Status;
		this.finalStatus = finalStatus;
	}

	public String getRejectionRemark() {
		return rejectionRemark;
	}

	public void setRejectionRemark(String rejectionRemark) {
		this.rejectionRemark = rejectionRemark;
	}

	public String getApprover2Status() {
		return approver2Status;
	}

	public void setApprover2Status(String approver2Status) {
		this.approver2Status = approver2Status;
	}

	public String getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(String finalStatus) {
		this.finalStatus = finalStatus;
	}
	
	
}
