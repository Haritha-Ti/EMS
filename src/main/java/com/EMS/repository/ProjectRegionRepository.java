package com.EMS.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.ProjectModel;
import com.EMS.model.ProjectRegion;

public interface ProjectRegionRepository extends JpaRepository<ProjectRegion, Long> {

	@Query("SELECT r FROM ProjectRegion r WHERE r.project_Id.projectId=?1")
	List<ProjectRegion> getRegionList(long projectId);

	@Query("SELECT r.project_Id  FROM ProjectRegion r WHERE r.region_Id.id=?1  AND r.project_Id.projectStatus=1 AND r.project_Id.parentProjectId !=0 group by 1 order by r.project_Id.projectName Asc ")
	List<ProjectModel> getProjectsByRegionId(Long regionId);

	@Query("SELECT r.project_Id.projectName,r.project_Id.projectId,r.project_Id.projectTier  FROM ProjectRegion r WHERE r.region_Id.id=?1 AND r.project_Id.startDate<=?3 and r.project_Id.endDate>=?2 and r.project_Id.projectCategory=1 and r.project_Id.isBillable=1 order by r.project_Id.projectName Asc ")
	List<Object[]> getObjProjectsByRegionId(Long regionId, Date startDate, Date endDate);

}
