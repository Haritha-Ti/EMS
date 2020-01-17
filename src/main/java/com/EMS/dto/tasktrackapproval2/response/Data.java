package com.EMS.dto.tasktrackapproval2.response;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Data {

	
	private String projectWorkFlowType;
	
	private ArrayList<UserData> userData = new ArrayList<>();
	
	private ArrayList<String> weeks = new ArrayList<>();
	
	private ArrayList<Dates> weeksDate = new ArrayList<>();
	
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

	public Data(String projectWorkFlowType, ArrayList<UserData> userData, ArrayList<String> weeks,ArrayList<Dates> weeksDate) {
		super();
		this.projectWorkFlowType = projectWorkFlowType;
		this.userData = userData;
		this.weeks = weeks;
		this.weeksDate = weeksDate;
	}

	public ArrayList<Dates> getWeeksDate() {
		return weeksDate;
	}

	public void setWeeksDate(ArrayList<Dates> weeksDate) {
		this.weeksDate = weeksDate;
	}

	



	
	
	
	
}
