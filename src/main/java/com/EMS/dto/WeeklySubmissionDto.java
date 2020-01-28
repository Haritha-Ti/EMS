package com.EMS.dto;

import java.util.Date;

public class WeeklySubmissionDto implements Submission {

	
    private String trxDate;

	private String usrInAction;

	private Double d1 ;

	private Double d2 ;

	private Double d3 ;

	private Double d4 ;

	private Double d5 ;

	private Double d6 ;

	private Double d7 ;

	private String role;

	private String status;

	private String comments;

	public String getTrxDate() {
		return trxDate;
	}

	public void setTrxDate(String trxDate) {
		this.trxDate = trxDate;
	}

	public String getUsrInAction() {
		return usrInAction;
	}

	public void setUsrInAction(String usrInAction) {
		this.usrInAction = usrInAction;
	}

	public Double getD1() {
		return d1 == null ? 0d :d1;
	}

	public void setD1(Double d1) {
		this.d1 = d1;
	}

	public Double getD2() {
		return d2 == null ? 0d :d2;
	}

	public void setD2(Double d2) {
		this.d2 = d2;
	}

	public Double getD3() {
		return d3 == null ? 0d :d3;
	}

	public void setD3(Double d3) {
		this.d3 = d3;
	}

	public Double getD4() {
		return d4 == null ? 0d :d4;
	}

	public void setD4(Double d4) {
		this.d4 = d4;
	}

	public Double getD5() {
		return d5 == null ? 0d :d5;
	}

	public void setD5(Double d5) {
		this.d5 = d5;
	}

	public Double getD6() {
		return d6 == null ? 0d :d6;
	}

	public void setD6(Double d6) {
		this.d6 = d6;
	}

	public Double getD7() {
		return d7 == null ? 0d :d7;
	}

	public void setD7(Double d7) {
		this.d7 = d7;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}




	

}