package com.EMS.dto;

import java.util.List;

public class DateBasedTaskDto {

	private String taskDate;
	
	private List<DailyTasktrackDto> dailyTasktrackDtos;

	public String getTaskDate() {
		return taskDate;
	}

	public void setTaskDate(String taskDate) {
		this.taskDate = taskDate;
	}

	public List<DailyTasktrackDto> getDailyTasktrackDtos() {
		return dailyTasktrackDtos;
	}

	public void setDailyTasktrackDtos(List<DailyTasktrackDto> dailyTasktrackDtos) {
		this.dailyTasktrackDtos = dailyTasktrackDtos;
	}
	
	
	
}
