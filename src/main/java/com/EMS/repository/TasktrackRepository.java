package com.EMS.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.AllocationModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.Task;
import com.EMS.model.TaskTrackApproval;
import com.EMS.model.Tasktrack;

public interface TasktrackRepository extends JpaRepository<Tasktrack, Long> {

	@Query("SELECT t FROM Tasktrack t WHERE t.user.userId=?3 AND CAST(t.date as date) BETWEEN ?1 AND ?2 order by date(t.date) asc")
	List<Tasktrack> getByDate(Date startDate, Date endDate, Long id);

	@Modifying
	@Transactional
	@Query("UPDATE Tasktrack t set t.description=?1,t.date=?3,t.hours=?4,t.project=?5,t.task=?6 where t.id=?2")
	public void updateTaskById(String description, long id, Date date,double hours,ProjectModel projectModel,Task task) throws Exception;

	@Modifying
	@Transactional
	@Query("delete from Tasktrack t where t.id=?1")
	public void deleteTaskById(long id) throws Exception;

	public default void createTask(Tasktrack task) throws Exception {
		try {
			// getSession().save(task);
		} catch (Exception exc) {
			throw new Exception();
		}
	}

	@Query("SELECT a from AllocationModel a where a.user.userId=?1 order by a.project.projectName")
	public List<AllocationModel> getProjectNames(long uId) throws Exception;

	//@Query(value="SELECT a from AllocationModel a where a.user.userId=:uId AND  ((a.startDate BETWEEN :startdate AND :enddate) OR (a.endDate BETWEEN :startdate AND :enddate)) order by a.project.projectName",nativeQuery=true)
	//@Query("SELECT a from AllocationModel a where a.user.userId=?1 and  ((a.startDate between ?2 and ?3) OR (a.endDate between ?2 and ?3)) order by a.project.projectName")
	@Query("SELECT a from AllocationModel a where a.user.userId=?1 and  a.startDate <=?3 and a.endDate >=?2 order by a.project.projectName")
	//@Query(value="SELECT a from AllocationModel a where a.user.userId=?1 AND  (a.startDate >=?2 AND a.endDate <=?3) order by a.project.projectName",nativeQuery=true)
	public List<AllocationModel> getProjectNamesByMonth(long uId,Date startdate,Date enddate) throws Exception;
	
	@Query("SELECT a from AllocationModel a where a.user.userId=?1 and a.project.isBillable =1 order by a.project.projectName")
	public List<AllocationModel> getProjectNamesForApproval(long uId) throws Exception;

	@Query("from ProjectModel p where p.isBillable =1  AND p.projectStatus=1 order by p.projectName")
	public List<ProjectModel> getProjectNamesForApproval() throws Exception;
	
	@Query("SELECT tsk from UserTaskCategory utc inner join utc.taskCategory.task tsk where utc.user.userId = ?1 order by tsk.taskName")
	public List<Task> getTaskCategories(long uId) throws Exception;

	@Query("from ProjectModel where projectId=?1")
	public ProjectModel getProjectById(long id);
	
	@Query("SELECT tt from Tasktrack tt")
	public List<Tasktrack> getTaskList() throws Exception;
	/*
	 * public void getTaskCategory() {
	 * 
	 * }
	 */
	@Query("SELECT count(*) > 0 FROM Tasktrack s WHERE s.user.userId = ?1")
	Boolean existsByUser(Long id);

	@Query("SELECT count(*) > 0 FROM Tasktrack s WHERE s.user.userId = ?2 and s.project.projectId = ?1")
	Boolean checkExistanceOfUser(Long projectId, Long userId);

	@Query("SELECT DISTINCT a.project.projectName,a.project.projectId,a.project.projectTier from AllocationModel a where a.user.userId=?1 and a.project.isBillable =1 and a.project.projectStatus=1 order by a.project.projectName")
	public List<Object[]> getProjectNamesForApprovalnew(long uId) throws Exception;
	
	@Query("SELECT DISTINCT a.projectName,a.projectId from ProjectModel a where a.onsite_lead.userId=?1 and a.isBillable =1 and a.projectStatus=1 order by a.projectName")
	public List<Object[]> getProjectNamesForApprovalLevel2(long uId);

	@Query("SELECT DISTINCT a.projectName,a.projectId from ProjectModel a where a.projectOwner.userId=?1 and a.isBillable =1 and a.projectStatus=1 order by a.projectName")
	public List<Object[]> getProjectNamesForApprovalLevel1(long uId);

	@Query("SELECT a FROM TaskTrackApproval a where a.user.userId = ?1 and a.month = ?2 and a.year = ?3 and a.project.projectId = ?4 ")
	List<TaskTrackApproval> getApprovedData(Long userId, int monthIndex, int yearIndex, Long projectId);

	@Query(value="SELECT count(*) from allocation a where a.project_project_id=?1 and  a.start_date <=?3 and a.end_date >=?2",nativeQuery = true)
	int  checkprojectallocated(long projectId,Date startdate,Date enddate);

	//Nisha
	@Query("from ProjectModel p where p.isBillable =1  AND p.projectStatus=1 AND (month(start_date)<=?1 and year(start_date)<=?2) and ( (month(end_date)>=?1 and year(end_date)>=?2) or  year(end_date)>?2) order by p.projectName")
	public List<ProjectModel> getProjectNamesForApproval(int month,int year) throws Exception;

	@Query("SELECT DISTINCT a.projectName,a.projectId from ProjectModel a where a.onsite_lead.userId=?1 and a.isBillable =1 and a.projectStatus=1  AND (month(start_date)<=?2 and year(start_date)<=?3) and ( (month(end_date)>=?2 and year(end_date)>=?3) or  year(end_date)>?3) order by a.projectName")
	public List<Object[]> getProjectNamesForApprovalLevel2(long uId,int month,int year);

	@Query("SELECT DISTINCT a.projectName,a.projectId from ProjectModel a where a.projectOwner.userId=?1 and a.isBillable =1 and a.projectStatus=1 AND (month(start_date)<=?2 and year(start_date)<=?3) and ( (month(end_date)>=?2 and year(end_date)>=?3) or  year(end_date)>?3) order by a.projectName")
	public List<Object[]> getProjectNamesForApprovalLevel1(long uId,int month,int year);

	@Query("SELECT DISTINCT a.projectName,a.projectId,a.projectTier from ProjectModel a where (a.projectOwner.userId=?1 or a.onsite_lead.userId=?1) and a.isBillable =1 and a.projectStatus=1 AND (month(start_date)<=?2 and year(start_date)<=?3) and ( (month(end_date)>=?2 and year(end_date)>=?3) or  year(end_date)>?3) order by a.projectName")
	public List<Object[]> getProjectNamesForApprover(long uId,int month,int year);

	//Nisha

	@Query("SELECT DISTINCT a.project.projectId,a.project.projectName from AllocationModel a where a.user.userId=?1 and  a.startDate <=?3 and a.endDate >=?2 order by a.project.projectName")
	public List<Object[]> getProjectNamesByMonths(long uId,Date startdate,Date enddate) throws Exception;

	@Query("SELECT DISTINCT a.projectName,a.projectId,a.projectTier from ProjectModel a where (a.projectOwner.userId=?1 or a.onsite_lead.userId=?1) and a.isBillable =1 and a.projectStatus=1 order by a.projectName")
	List<Object[]> getProjectNamesForApproverOnly(Long uId);
	
	
}
