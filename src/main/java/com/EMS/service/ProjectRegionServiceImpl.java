package com.EMS.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.ProjectModel;
import com.EMS.repository.ProjectRegionRepository;


@Service
public class ProjectRegionServiceImpl implements ProjectRegionService{
	
	@Autowired
	private ProjectRegionRepository projectRegionRepository;

	@Override
	public List<ProjectModel> getProjectsByRegionId(Long regionId) {
		
		return projectRegionRepository.getProjectsByRegionId(regionId);
	}

	@Override
	public List<Object[]> getObjProjectsByRegionId(Long regionId) {
		// TODO Auto-generated method stub
		return projectRegionRepository.getObjProjectsByRegionId(regionId);
	}
	
	
	

}
