package com.EMS.service;

import java.util.Date;
import java.util.List;

import com.EMS.model.ProjectRegion;
import org.springframework.stereotype.Service;

import com.EMS.model.ProjectModel;

public interface ProjectRegionService {

	List<ProjectModel> getProjectsByRegionId(Long regionId);

	List<Object[]> getObjProjectsByRegionId(Long regionId, Date startDate, Date endDate);

	List<ProjectRegion> getRegionListByProject(Long projectId);
}
