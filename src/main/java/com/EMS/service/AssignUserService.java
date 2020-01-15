package com.EMS.service;

import java.util.Date;

import com.EMS.model.ApprovalUserAsignModel;
import com.EMS.model.StatusResponse;

public interface AssignUserService {
	
	public StatusResponse saveAssignUser(ApprovalUserAsignModel  user);
	
	public StatusResponse  deleteAssignedUser(Long assignId);
	
	public StatusResponse  getAllAssignedUser();
	
	public StatusResponse  getAssignedUserById(Long assignId);
	
	public StatusResponse  getAssignUserByProjectAndDate(Long projectId, Date startDate, Date endDate);

}
