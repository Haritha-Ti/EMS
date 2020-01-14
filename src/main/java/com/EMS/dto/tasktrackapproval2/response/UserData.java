package com.EMS.dto.tasktrackapproval2.response;

import java.util.ArrayList;

public class UserData {

	private long userId;
	
	private String userName;
	
	private SemiMonthlyData semiMonthlyData;

	private ArrayList<WeekData> weekData = new ArrayList<>();
	
	public UserData(long userId,String userName,ArrayList<WeekData> weekData) {
		
		this.userId = userId;
		this.userName = userName;
		this.weekData = weekData;
		
	}
	
	public UserData(long userId,String userName,SemiMonthlyData semiMonthlyData) {
		
		this.userId = userId;
		this.userName = userName;
		this.semiMonthlyData = semiMonthlyData;
		
	}
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public SemiMonthlyData getSemiMonthlyData() {
		return semiMonthlyData;
	}

	public void setSemiMonthlyData(SemiMonthlyData semiMonthlyData) {
		this.semiMonthlyData = semiMonthlyData;
	}

	public ArrayList<WeekData> getWeekData() {
		return weekData;
	}

	public void setWeekData(ArrayList<WeekData> weekData) {
		this.weekData = weekData;
	}


	
	
}
