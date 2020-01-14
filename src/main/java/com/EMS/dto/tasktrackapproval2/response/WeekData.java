package com.EMS.dto.tasktrackapproval2.response;

import java.util.Date;

public class WeekData {

	private double totalHour;
	
	private Date weekStart;
	
	private Date weekEnd;
	
	private String approverSatus;

	public double getTotalHour() {
		return totalHour;
	}

	public void setTotalHour(double totalHour) {
		this.totalHour = totalHour;
	}

	public Date getWeekStart() {
		return weekStart;
	}

	public void setWeekStart(Date weekStart) {
		this.weekStart = weekStart;
	}

	public Date getWeekEnd() {
		return weekEnd;
	}

	public void setWeekEnd(Date weekEnd) {
		this.weekEnd = weekEnd;
	}

	public String getApproverSatus() {
		return approverSatus;
	}

	public void setApproverSatus(String approverSatus) {
		this.approverSatus = approverSatus;
	}
	
	public WeekData(double totalhours,Date weekStart,Date weekEnd,String approverSatus) {
		this.totalHour = totalHour;
		this.weekStart = weekStart;
		this.weekEnd = weekEnd;
		this.approverSatus = approverSatus;
	}
	
	
	
	
}
