package com.EMS.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.EMS.model.ProjectModel;


public interface ProjectRegionService {
	
	List<ProjectModel>   getProjectsByRegionId(Long regionId);

}
