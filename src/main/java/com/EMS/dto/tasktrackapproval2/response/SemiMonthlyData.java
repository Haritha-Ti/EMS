package com.EMS.dto.tasktrackapproval2.response;

public class SemiMonthlyData {

	
	public SemiMonthlyData(double firstHalfHour, double secondHalfHour, String firstHalfStatus,
			String secondHalfStatus, String firstHalfRejectionRemark,String secondHalfRejectionRemark) {
		super();
		this.firstHalfHour = firstHalfHour;
		this.secondHalfHour = secondHalfHour;
		this.firstHalfStatus = firstHalfStatus;
		this.secondHalfStatus = secondHalfStatus;
		this.firstHalfRejectionRemark = firstHalfRejectionRemark;
		this.secondHalfRejectionRemark = secondHalfRejectionRemark;
	}

	private double firstHalfHour;
	
	private double secondHalfHour;
	
	private String firstHalfStatus;
	
	private String secondHalfStatus;
	
	private String firstHalfRejectionRemark;
	
	private String secondHalfRejectionRemark;

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

	public String getFirstHalfRejectionRemark() {
		return firstHalfRejectionRemark;
	}

	public void setFirstHalfRejectionRemark(String firstHalfRejectionRemark) {
		this.firstHalfRejectionRemark = firstHalfRejectionRemark;
	}

	public String getSecondHalfRejectionRemark() {
		return secondHalfRejectionRemark;
	}

	public void setSecondHalfRejectionRemark(String secondHalfRejectionRemark) {
		this.secondHalfRejectionRemark = secondHalfRejectionRemark;
	}
	
}
