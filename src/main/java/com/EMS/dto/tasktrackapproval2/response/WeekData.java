package com.EMS.dto.tasktrackapproval2.response;

import java.util.Date;

public class WeekData {

	private double totalHour;
	
	private String weekStart;
	
	private String weekEnd;
	
	private String rejectionRemark;
	
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
	
	public WeekData(double totalhours,String weekStart,String weekEnd,String rejectionRemark,String finalStatus) {
		this.totalHour = totalhours;
		this.weekStart = weekStart;
		this.weekEnd = weekEnd;
		this.rejectionRemark = rejectionRemark;
		this.finalStatus = finalStatus;
	}

	public String getRejectionRemark() {
		return rejectionRemark;
	}

	public void setRejectionRemark(String rejectionRemark) {
		this.rejectionRemark = rejectionRemark;
	}

	public String getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(String finalStatus) {
		this.finalStatus = finalStatus;
	}
	
	
}
