package com.EMS.dto.tasktrackapproval2.response;

public class SemiMonthlyData {

	
	public SemiMonthlyData(double firstHalfHour, double secondHalfHour, String firstHalfStatus,
			String secondHalfStatus) {
		super();
		this.firstHalfHour = firstHalfHour;
		this.secondHalfHour = secondHalfHour;
		this.firstHalfStatus = firstHalfStatus;
		this.secondHalfStatus = secondHalfStatus;
	}

	private double firstHalfHour;
	
	private double secondHalfHour;
	
	private String firstHalfStatus;
	
	private String secondHalfStatus;

	public double getFirstHalfHour() {
		return firstHalfHour;
	}

	public void setFirstHalfHour(double firstHalfHour) {
		this.firstHalfHour = firstHalfHour;
	}

	public double getSecondHalfHour() {
		return secondHalfHour;
	}

	public void setSecondHalfHour(double secondHalfHour) {
		this.secondHalfHour = secondHalfHour;
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
	
	
	
}
