package com.EMS.dto.tasktrackapproval2.response;

import com.EMS.utility.Constants;

public class SemiMonthlyData {

	
	public SemiMonthlyData(double firstHalfHour, double secondHalfHour, String firstHalfRejectionRemark,String secondHalfRejectionRemark,
			String firstHalfFinalStatus,String secondHalfFinalStatus) {
		super();
		this.firstHalfHour = firstHalfHour;
		this.secondHalfHour = secondHalfHour;
		this.firstHalfRejectionRemark = firstHalfRejectionRemark;
		this.secondHalfRejectionRemark = secondHalfRejectionRemark;
		this.firstHalfFinalStatus = firstHalfFinalStatus;
		this.secondHalfFinalStatus = secondHalfFinalStatus;
		
	}

	private double firstHalfHour;
	
	private double secondHalfHour;
	
	private String firstHalfRejectionRemark;
	
	private String secondHalfRejectionRemark;
	
	private String firstHalfFinalStatus ;
	
	private String secondHalfFinalStatus ;


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

	public String getFirstHalfFinalStatus() {
		return firstHalfFinalStatus;
	}

	public void setFirstHalfFinalStatus(String firstHalfFinalStatus) {
		this.firstHalfFinalStatus = firstHalfFinalStatus;
	}

	public String getSecondHalfFinalStatus() {
		return secondHalfFinalStatus;
	}

	public void setSecondHalfFinalStatus(String secondHalfFinalStatus) {
		this.secondHalfFinalStatus = secondHalfFinalStatus;
	}

	

	
}
