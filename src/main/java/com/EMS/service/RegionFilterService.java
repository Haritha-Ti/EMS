package com.EMS.service;

import java.util.List;

import com.EMS.model.ProjectModel;

public interface RegionFilterService {
	
	List<ProjectModel>  getAllProjectByLoginUserRegion(Long userId);

}
