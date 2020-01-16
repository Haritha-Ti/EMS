package com.EMS.dto.tasktrackapproval2.response;

import java.util.Date;

public class WeekData {

	private double totalHour;
	
	private String weekStart;
	
	private String weekEnd;
	
	private String approverSatus;
	
	private String rejectionRemark;

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
	
	public WeekData(double totalhours,String weekStart,String weekEnd,String approverSatus,String rejectionRemark) {
		this.totalHour = totalhours;
		this.weekStart = weekStart;
		this.weekEnd = weekEnd;
		this.approverSatus = approverSatus;
		this.rejectionRemark = rejectionRemark;
	}

	public String getRejectionRemark() {
		return rejectionRemark;
	}

	public void setRejectionRemark(String rejectionRemark) {
		this.rejectionRemark = rejectionRemark;
	}
	
}
