package com.EMS.dto.tasktrackapproval2.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Data {

	private String projectWorkFlowType;
	
	private ArrayList<UserData> userData = new ArrayList<>();
	
	private ArrayList<String> weeks = new ArrayList<>();

	public String getProjectWorkFlowType() {
		return projectWorkFlowType;
	}

	public void setProjectWorkFlowType(String projectWorkFlowType) {
		this.projectWorkFlowType = projectWorkFlowType;
	}

	public ArrayList<UserData> getUserData() {
		return userData;
	}

	public void setUserData(ArrayList<UserData> userData) {
		this.userData = userData;
	}

	

	public ArrayList<String> getWeeks() {
		return weeks;
	}

	public void setWeeks(ArrayList<String> weeks) {
		this.weeks = weeks;
	}

	public Data(String projectWorkFlowType, ArrayList<UserData> userData, ArrayList<String> weeks) {
		super();
		this.projectWorkFlowType = projectWorkFlowType;
		this.userData = userData;
		this.weeks = weeks;
	}
	
	
}
