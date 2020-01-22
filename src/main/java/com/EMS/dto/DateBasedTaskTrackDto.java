package com.EMS.dto;

import java.util.Date;
import java.util.List;

public class DateBasedTaskTrackDto {
	
	private String month;
	
	private String year;

	private String startDate;
	
	private String endDate;
	
	private Long  uId;
	
	private Long projectId;
	
	private List<DateBasedTaskDto> dateBasedTaskDtoList;

	

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Long getuId() {
		return uId;
	}

	public void setuId(Long uId) {
		this.uId = uId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public List<DateBasedTaskDto> getDateBasedTaskDtoList() {
		return dateBasedTaskDtoList;
	}

	public void setDateBasedTaskDtoList(List<DateBasedTaskDto> dateBasedTaskDtoList) {
		this.dateBasedTaskDtoList = dateBasedTaskDtoList;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	
	
	
	
}
