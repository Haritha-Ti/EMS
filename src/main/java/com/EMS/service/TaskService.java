package com.EMS.service;

import java.util.Date;
import java.util.List;

import com.EMS.model.TaskModel;

public interface TaskService {
	
	List<Object[]> getTaskList();
	List<String> getTaskByUserId(Long uId);
	List<TaskModel> getByDate(Date currentDate,Long uId);
	TaskModel saveTaskDetails(TaskModel task);

}
