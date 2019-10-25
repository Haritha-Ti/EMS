package com.EMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.ProjectModel;
import com.EMS.model.ProjectRegion;

public interface ProjectRegionRepository extends JpaRepository<ProjectRegion, Long>{

	@Query("SELECT r FROM ProjectRegion r WHERE r.project_Id.projectId=?1")
	List<ProjectRegion> getRegionList(long projectId);
	
	@Query("SELECT r.project_Id  FROM ProjectRegion r WHERE r.region_Id.id=?1 order by r.project_Id.projectName Asc ")
	List<ProjectModel>   getProjectsByRegionId(Long regionId);



}
