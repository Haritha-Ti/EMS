package com.EMS.model;

import java.util.Date;

public class ProjectReportModel {

	private String userName;
	private int isBillable;
	private String  allocatedPerce;
	private String projectName ;
	private int regionId;

	public int getRegionId() {
		return regionId;
	}
	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getIsBillable() {
		return isBillable;
	}
	public void setIsBillable(int isBillable) {
		this.isBillable = isBillable;
	}
	public String getAllocatedPerce() {
		return allocatedPerce;
	}
	public void setAllocatedPerce(String allocatedPerce) {
		this.allocatedPerce = allocatedPerce;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	
}

