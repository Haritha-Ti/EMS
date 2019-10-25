package com.EMS.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.ProjectModel;


@Service
public class RegionFilterServiceImpl  implements RegionFilterService {
	
	@Autowired
	private UserService userService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectRegionService projectRegionService;

	@Override
	public List<ProjectModel> getAllProjectByLoginUserRegion(Long userId) {
		
		Long  roleId=null;
		Long  regionId=null;
		List<ProjectModel> projectList = new ArrayList<ProjectModel>();
		boolean isLevels=true;
		if(userId !=null){
			
			roleId = userService.getUserDetailsById(userId).getRole().getroleId();
			
			regionId = userService.getUserdetailsbyId(userId).getRegion().getId();
		}
		else 
			return null;
		if (roleId == 1){
			
			projectList = projectService.getProjectList();
			isLevels= false;
		}
			

		if (roleId == 4 | roleId == 6 | roleId == 9) {
			projectList = projectRegionService.getProjectsByRegionId(regionId);
			isLevels= false;
		}
		
		if (!(projectList).isEmpty() && projectList.size() > 0 && !isLevels) {
			return projectList;
		}
		
		// Level 1
		projectList = projectService.getProjectListByLevel1(userId);
		// Level 2
		projectList.addAll(projectService.getProjectListByLevel2(userId));

		//Remove duplicates if any
		
		projectList = projectList.stream() 
                .distinct() 
                .collect(Collectors.toList());
		if (!(projectList).isEmpty() && projectList.size() > 0 &&  isLevels) {
			
			return projectList;
		}
		
		return null;
	}

}
