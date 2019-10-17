package com.EMS.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EMS.model.ProjectModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface ProjectRepository extends JpaRepository<ProjectModel, Long> {

	@Query("SELECT s.projectName FROM ProjectModel s")
	List<String> getProjectName();

	@Query("SELECT s.id FROM ProjectModel s where s.projectName=?1")
	Long getProjectId(String projectName);
	
	@Query("SELECT s.id,s.projectName FROM ProjectModel s")
	List<Object[]>getByIdName();

	@Query("SELECT count(p) FROM ProjectModel p WHERE p.projectName=?1")
	int findproject(String getprojectName);

	//@Query(value ="SELECT CONCAT(u.first_name,' ',u.last_name) AS users , a.is_billable, a.allocated_perce, a.start_date, a.end_date, p.project_name,t.technology_name FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id LEFT JOIN user_technology ut ON ut.user_user_id = u.user_id LEFT JOIN technology t ON ut.technology_technology_id = t.technology_id WHERE t.technology_id =?1 AND a.start_date <= ?3 AND a.end_date  >= ?2",nativeQuery = true)
	@Query(value ="SELECT DISTINCT(u.user_id),CONCAT(u.first_name,' ',u.last_name) AS users ,t.technology_name FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN user_technology ut ON ut.user_user_id = u.user_id LEFT JOIN technology t ON ut.technology_technology_id = t.technology_id WHERE t.technology_id =?1 AND a.start_date <= ?3 AND a.end_date  >= ?2",nativeQuery = true)
	public List<Object[]> getAllocationDetailsTechWise(Long techId, Date fromDate, Date toDate) throws Exception;

	@Query(value ="SELECT  a.is_billable, a.allocated_perce, a.start_date, a.end_date, p.project_name FROM allocation a LEFT JOIN project p ON p.project_id = a.project_project_id  WHERE a.user_user_id =?1 AND a.start_date <= ?3 AND a.end_date  >= ?2",nativeQuery = true)
	public List<Object[]> getAllocatedProjectByUserId(Long userId, Date fromDate, Date toDate) throws Exception;

	@Query("SELECT s FROM ProjectModel s WHERE s.projectId = ?1")
	public ProjectModel getProjectDetails(Long projectId);

	@Query("SELECT s.projectName FROM ProjectModel s where s.projectId=?1")
	String getProjectName(Long projectId);

	@Query(value =" SELECT (CASE WHEN project.project_owner_user_id = ?2 " + 
			" THEN 'level1'  " + 
			" WHEN project.onsite_lead_user_id = ?2 " + 
			" THEN 'level2' " + 
			" ELSE 'not assigned this project' END) as secondary_role " + 
			 " FROM pmstaging.project " + 
			" WHERE  project.project_id = ?1",nativeQuery = true)
	Object[] getApproveLevelByprojecIdAndLeadsId(Long project_Id, Long logUser);

	@Query(value = "SELECT role.role_name " + 
			" FROM pmstaging.user " + 
			" INNER JOIN pmstaging.role on (role.role_id = user.role_role_id) " + 
			" where user.user_id = ?1 ",nativeQuery = true)
	Object[] getRoleOftheLoguser(Long logUser);

	
	@Query(value = "SELECT count(*) FROM `project` where start_date<=?1 AND end_date>=?1 AND project_status=1",nativeQuery = true)
	int getActiveProjects(String datestring);

}
