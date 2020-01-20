package com.EMS.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.dto.AssignUserReturnDto;
import com.EMS.model.ApprovalUserAsignModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.StatusResponse;
import com.EMS.model.UserModel;
import com.EMS.repository.AssignUserRepository;
import com.EMS.repository.ProjectRepository;
import com.EMS.repository.UserRepository;


@Service
public class AssignUserServiceImpl implements  AssignUserService{
	
	@Autowired
	private AssignUserRepository  assignUserRepository;
	
	@Autowired
	private UserRepository  userRepo;
	
	@Autowired
	private ProjectRepository  projectRepo;

	@Override
	public StatusResponse saveAssignUser(ApprovalUserAsignModel user) {
		StatusResponse  response =null;
		assignUserRepository.save(user);
		response  =new StatusResponse("Success", 200, null);
		
		return response;
		
		
	}

	@Override
	public StatusResponse deleteAssignedUser(Long assignId) {
		StatusResponse  response =null;
		assignUserRepository.deleteById(assignId);
       response  =new StatusResponse("Success", 200, null);
		return response;
	}

	@Override
	public StatusResponse getAllAssignedUser() {
		StatusResponse  response =null;
		List<ApprovalUserAsignModel>  data=assignUserRepository.findAll();
       response  =new StatusResponse("Success", 200, data);
		return response;
	}

	@Override
	public StatusResponse getAssignedUserById(Long assignId) {
		StatusResponse  response =null;
		ApprovalUserAsignModel data=assignUserRepository.findById(assignId).get();
       response  =new StatusResponse("Success", 200, data);
		return response;
	}

	@Override
	public StatusResponse getAssignUserByProjectAndDate(Long projectId, Date startDate, Date endDate) {
		StatusResponse  response =null;
		List<AssignUserReturnDto>  dtoList =new ArrayList<AssignUserReturnDto>();
		//List<ApprovalUserAsignModel> data=assignUserRepository.findByProjectIdProjectId(projectId);
		List<ApprovalUserAsignModel> data=assignUserRepository.findByProjectIdProjectIdAndStartDateAfterAndEndDateBefore(projectId, startDate, endDate);
		for(ApprovalUserAsignModel m: data ){
			AssignUserReturnDto   dto= new AssignUserReturnDto();
			UserModel  user = userRepo.findById(m.getUserId().getUserId()).get() ;
			ProjectModel project = projectRepo.findById(m.getProjectId().getProjectId()).get() ;
			dto.setProjectId(m.getProjectId().getProjectId());
			dto.setStatus(m.isStatus());
			dto.setAssignUserId(m.getAssignUserId());
			dto.setProjectName(project.getProjectName());
			dto.setUserId((m.getUserId().getUserId()));
			dto.setUserName(user.getFirstName()+" "+user.getLastName());
			dtoList.add(dto);
			
		}
       response  =new StatusResponse("Success", 200, dtoList);
		return response;
	}

}
