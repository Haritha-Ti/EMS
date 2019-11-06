package com.EMS.service;

import java.util.List;

import com.EMS.model.ProjectRegion;
import org.springframework.stereotype.Service;

import com.EMS.model.ProjectModel;


public interface ProjectRegionService {
	
	List<ProjectModel>   getProjectsByRegionId(Long regionId);
	List<Object[]>   getObjProjectsByRegionId(Long regionId);


	List<ProjectRegion> getRegionListByProject(Long projectId);
}
