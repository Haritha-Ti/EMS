package com.EMS.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EMS.model.AllocationModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.Task;
import com.EMS.model.TaskTrackApproval;
import com.EMS.model.TaskTrackDaySubmissionModel;
import com.EMS.model.Tasktrack;
import com.EMS.utility.Constants;

public interface TasktrackRepository extends JpaRepository<Tasktrack, Long> {

	@Query("SELECT t FROM Tasktrack t WHERE t.user.userId=?3 AND CAST(t.date as date) BETWEEN ?1 AND ?2 order by date(t.date) asc")
	List<Tasktrack> getByDate(Date startDate, Date endDate, Long id);

	@Modifying
	@Transactional
	@Query("UPDATE Tasktrack t set t.description=?1,t.date=?3,t.hours=?4,t.project=?5,t.task=?6 where t.id=?2")
	public void updateTaskById(String description, long id, Date date, double hours, ProjectModel projectModel,
			Task task) throws Exception;

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

	// @Query(value="SELECT a from AllocationModel a where a.user.userId=:uId AND
	// ((a.startDate BETWEEN :startdate AND :enddate) OR (a.endDate BETWEEN
	// :startdate AND :enddate)) order by a.project.projectName",nativeQuery=true)
	// @Query("SELECT a from AllocationModel a where a.user.userId=?1 and
	// ((a.startDate between ?2 and ?3) OR (a.endDate between ?2 and ?3)) order by
	// a.project.projectName")
	@Query("SELECT a from AllocationModel a where a.user.userId=?1 and  a.startDate <=?3 and a.endDate >=?2 order by a.project.projectName")
	// @Query(value="SELECT a from AllocationModel a where a.user.userId=?1 AND
	// (a.startDate >=?2 AND a.endDate <=?3) order by
	// a.project.projectName",nativeQuery=true)
	public List<AllocationModel> getProjectNamesByMonth(long uId, Date startdate, Date enddate) throws Exception;

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

	@Query("SELECT DISTINCT a.project.projectName,a.project.projectId,a.project.projectTier from AllocationModel a where a.user.userId=?1 and a.project.startDate<=?3 and a.project.endDate>=?2 and a.project.projectCategory=1 order by a.project.projectName")
	public List<Object[]> getProjectNamesForApprovalnew(long uId, Date startDate, Date endDate) throws Exception;

	@Query("SELECT DISTINCT a.projectName,a.projectId,a.projectTier from ProjectModel a where a.onsite_lead.userId=?1 and a.isBillable =1 and a.startDate<=?3 and a.endDate>=?2 and a.projectCategory=1 order by a.projectName")
	public List<Object[]> getProjectNamesForApprovalLevel2(long uId, Date startDate, Date endDate);

	@Query("SELECT DISTINCT a.projectName,a.projectId,a.projectTier from ProjectModel a where a.projectOwner.userId=?1 and a.isBillable =1 and a.startDate<=?3 and a.endDate>=?2 and a.projectCategory=1 order by a.projectName")
	public List<Object[]> getProjectNamesForApprovalLevel1(long uId, Date startDate, Date endDate);

	@Query("SELECT a FROM TaskTrackApproval a where a.user.userId = ?1 and a.month = ?2 and a.year = ?3 and a.project.projectId = ?4 ")
	List<TaskTrackApproval> getApprovedData(Long userId, int monthIndex, int yearIndex, Long projectId);

	@Query(value = "SELECT count(*) from allocation a where a.project_project_id=?1 and  a.start_date <=?3 and a.end_date >=?2", nativeQuery = true)
	int checkprojectallocated(long projectId, Date startdate, Date enddate);

	// Nisha
	@Query("from ProjectModel p where (month(start_date)<=?1 and year(start_date)<=?2) and ( (month(end_date)>=?1 and year(end_date)>=?2) or  year(end_date)>?2) order by p.projectName")
	public List<ProjectModel> getProjectNamesForApproval(int month, int year) throws Exception;

	@Query("from ProjectModel p where startDate<=?2 and endDate>=?1 and projectCategory=1 order by p.projectName")
	public List<ProjectModel> getProjectNamesForApproval(Date startDate, Date endDate) throws Exception;

	@Query("SELECT DISTINCT a.projectName,a.projectId,a.projectTier from ProjectModel a where a.onsite_lead.userId=?1 AND (?2 BETWEEN start_date and end_date or last_day(?2) BETWEEN start_date and end_date) AND project_category=1  order by a.projectName")
	public List<Object[]> getProjectNamesForApprovalLevel2(long uId, Date startDate, int month, int year);

	// @Query("SELECT DISTINCT a.projectName,a.projectId,a.projectTier from
	// ProjectModel a where a.projectOwner.userId=?1 AND (month(start_date)<=?2 and
	// year(start_date)<=?3) and ( (month(end_date)>=?2 and year(end_date)>=?3) or
	// year(end_date)>?3) order by a.projectName")
	@Query("SELECT DISTINCT a.projectName,a.projectId,a.projectTier from ProjectModel a where a.projectOwner.userId=?1 AND (?2 BETWEEN start_date and end_date or last_day(?2) BETWEEN start_date and end_date) AND project_category=1 order by a.projectName")
	public List<Object[]> getProjectNamesForApprovalLevel1(long uId, Date startDate, int month, int year);

	@Query("SELECT DISTINCT a.projectName,a.projectId,a.projectTier from ProjectModel a where (a.projectTier = 1 or a.projectTier = 2) AND (?1 BETWEEN start_date and end_date or last_day(?1) BETWEEN start_date and end_date) AND project_category=1 order by a.projectName")
	public List<Object[]> getTier1And2ProjectNames(Date startDate);

	@Query("SELECT DISTINCT a.projectName,a.projectId,a.projectTier from ProjectModel a where a.projectTier = 2 AND (?1 BETWEEN start_date and end_date or last_day(?1) BETWEEN start_date and end_date)  AND project_category=1 order by a.projectName")
	public List<Object[]> getTier2ProjectNames(Date startDate);

	@Query("SELECT DISTINCT a.projectName,a.projectId,a.projectTier from ProjectModel a where (a.projectOwner.userId=?1 or a.onsite_lead.userId=?1) AND (month(start_date)<=?2 and year(start_date)<=?3) and ( (month(end_date)>=?2 and year(end_date)>=?3) or  year(end_date)>?3) order by a.projectName")
	public List<Object[]> getProjectNamesForApprover(long uId, int month, int year);

	// Nisha

	@Query("SELECT DISTINCT a.project.projectId,a.project.projectName,a.project.clientName.clientName from AllocationModel a where a.user.userId=?1 and  a.startDate <=?3 and a.endDate >=?2 order by a.project.projectName")
	public List<Object[]> getProjectNamesByMonths(long uId, Date startdate, Date enddate) throws Exception;

	@Query("SELECT DISTINCT a.projectName,a.projectId,a.projectTier from ProjectModel a where (a.projectOwner.userId=?1 or a.onsite_lead.userId=?1) and a.isBillable =1 and a.startDate<=?3 and a.endDate>=?2 and a.projectCategory=1 order by a.projectName")
	List<Object[]> getProjectNamesForApproverOnly(Long uId, Date startDate, Date endDate);

	@Modifying
	@Transactional
	@Query("UPDATE Tasktrack t set t.hours=?2,t.project=?3,t.task=?4 where t.id=?1")
	public void updateTaskByName(long id, double hours, ProjectModel projectModel, Task task) throws Exception;

	@Query(value = "select p.project_id, p.project_name , t.`date`,t.id,c.client_name  ,sum(t.hours), p.project_tier from tasktrack t left join project p on p.project_id = t.project_project_id join `user` u  on u.user_id = t.user_user_id left join `client` c on c.client_id=p.client_name_client_id where u.user_id = ?1 and t.`date`>=?2 and t.`date`<=?3  group by 1,2,3,4,5 order by 1,2", nativeQuery = true)
	public List<Object[]> getTasksFortimeTrack(long id, Date fromDate, Date toDate) throws Exception;

	@Query(value = "SELECT \r\n"
			+ "pms.task_date AS \"Task Date\",COALESCE(pms.projectname,pr.project_name) AS \"Project\",\r\n"
			+ "FullName,Email,COALESCE(pms.end_date,pr.end_date) AS \"End Date\",UserName,IsActive,IsAllocated\r\n"

			+ "FROM\r\n" + "(\r\n" + "    SELECT \r\n" + "     date1 AS task_date,\r\n"
			+ "    prj.project_name AS projectname,\r\n" + "    CONCAT(u.first_name,' ',u.last_name) AS FullName,\r\n"
			+ "    t.hours AS actual_hour,u.email AS Email,prj.end_date AS end_date,u.user_name AS UserName,u.active AS IsActive,alc.active as IsAllocated,\r\n"
			+ "    (CASE \r\n" + "        WHEN prj.project_id IS NULL THEN u.user_id \r\n" + "        ELSE NULL \r\n"
			+ "    END) AS untracked_user,d.department_name   FROM\r\n" + "    `user` u\r\n" + "    LEFT JOIN \r\n"
			+ "    (\r\n" + "        SELECT \r\n" + "        user_user_id,\r\n" + "        project_project_id,\r\n"
			+ "        `date` as date1,\r\n" + "        COALESCE(SUM(hours),0) AS hours \r\n"
			+ "        FROM tasktrack WHERE DATE(`date`) >=?1 && DATE(`date`) <=?2 \r\n" + "        GROUP BY 1,2,3\r\n"
			+ "    ) t ON t.user_user_id = u.user_id\r\n"
			+ "    LEFT JOIN allocation alc ON alc.user_user_id = u.user_id\r\n"
			+ "    LEFT JOIN project prj ON prj.project_id = alc.project_project_id\r\n"
			+ " LEFT JOIN department d on d.department_id = u.department_department_id\r\n" + ")pms\r\n"
			+ "LEFT JOIN allocation al ON al.user_user_id = pms.untracked_user\r\n"
			+ "LEFT JOIN project pr ON pr.project_id = al.project_project_id \r\n"
			+ "where COALESCE(pms.projectname,pr.project_name) is not null  and  pms.department_name = 'Production' \r\n"
			+ "GROUP BY 1,2,3,4,5,6,7,8\r\n" + "ORDER BY 1,2,3;", nativeQuery = true)
	List<Object[]> getTrackTaskList(LocalDate fromDate, LocalDate toDate);

	@Query(value = "select user_id ,t.entryDate,t.user_user_id,user_name,email,concat(last_name,\" \",first_name) AS fullName\r\n"
			+ "from `user` u\r\n" + "left join(\r\n"
			+ "select `date` as entryDate,user_user_id,sum(hours) as hrs from tasktrack where `date`>=?1 and `date`<=?2\r\n"
			+ "group by 1,2) t on u.user_id = t.user_user_id LEFT JOIN department d on d.department_id = u.department_department_id where u.active = 1 \r\n"
			+ "and  d.department_name = 'Production' order by t.user_user_id,t.entryDate", nativeQuery = true)
	List<Object[]> getUserTaskTrackList(LocalDate fromDate, LocalDate toDate);

	@Query(value = "select coalesce(first_half_status,'OPEN') as status,po.user_name approver,po.email approverEmail,po.project_name,\n"
			+ "concat(usr.last_name, \" \",usr.first_name) userName,po.approverFullName\n"
			+ "from tasktrack_approval_final tf\n" + "join\n"
			+ "(select distinct p.project_id,p.project_name,u.user_name,u.email,concat(u.last_name,\" \",u.first_name) AS approverFullName\n"
			+ "from project p\n" + "left join user u on u.user_id = p.onsite_lead_user_id\n"
			+ "where p.project_tier=2) po on po.project_id = tf.project_project_id\n"
			+ "join user usr on usr.user_id = tf.user_user_id\n"
			+ "and project_type='Billable' and tf.month = ?1 LEFT JOIN department d on d.department_id = usr.department_department_id\r\n"
			+ "  where d.department_name = 'Production'", nativeQuery = true)
	List<Object[]> getApproverTwoFirstHalfInfo(Integer month);

	@Query(value = "select coalesce(second_half_status,'OPEN') as status,po.user_name approver,po.email approverEmail,po.project_name,\n"
			+ "concat(usr.last_name, \" \",usr.first_name) userName,po.approverFullName\n"
			+ "from tasktrack_approval_final tf\n" + "join\n"
			+ "(select distinct p.project_id,p.project_name,u.user_name,u.email,concat(u.last_name,\" \",u.first_name) AS approverFullName\n"
			+ "from project p\n" + "left join user u on u.user_id = p.onsite_lead_user_id\n"
			+ "where p.project_tier=2) po on po.project_id = tf.project_project_id\n"
			+ "join user usr on usr.user_id = tf.user_user_id\n"
			+ "and project_type='Billable' and tf.month = ?1 LEFT JOIN department d on d.department_id = usr.department_department_id\r\n"
			+ " where d.department_name = 'Production'", nativeQuery = true)
	List<Object[]> getApproverTwoSecondHalfInfo(Integer month);

	@Query(value = "select coalesce(first_half_status,'OPEN') as status,po.user_name approver,po.email approverEmail,po.project_name,\n"
			+ "concat(usr.last_name, \" \",usr.first_name) userName,po.approverFullName,po.project_tier\n"
			+ "from tasktrack_approval tf\n" + "join\n"
			+ "(select distinct p.project_id,p.project_name,u.user_name,u.email,concat(u.last_name,\" \",u.first_name) AS approverFullName\n"
			+ ",p.project_tier\n" + "from project p\n" + "left join user u on u.user_id = p.project_owner_user_id\n"
			+ "where p.project_tier=2) po on po.project_id = tf.project_project_id\n"
			+ "join user usr on usr.user_id = tf.user_user_id\n" + "and project_type='Billable' and tf.month = ?1 \n"
			+ "LEFT JOIN department d on d.department_id = usr.department_department_id where d.department_name = 'Production' "
			+ "union all\n"
			+ "select coalesce(first_half_status,'OPEN') as status,po.user_name approver,po.email approverEmail,po.project_name,\n"
			+ "concat(usr.last_name, \" \",usr.first_name) userName,po.approverFullName,po.project_tier\n"
			+ "from tasktrack_approval_final tf\n" + "join\n"
			+ "(select distinct p.project_id,p.project_name,u.user_name,u.email,concat(u.last_name,\" \",u.first_name) AS approverFullName\n"
			+ ",p.project_tier\n" + "from project p\n" + "left join user u on u.user_id = p.project_owner_user_id\n"
			+ "where p.project_tier=1) po on po.project_id = tf.project_project_id\n"
			+ "join user usr on usr.user_id = tf.user_user_id and project_type='Billable' and tf.month = ?1 "
			+ "LEFT JOIN department d on d.department_id = usr.department_department_id where d.department_name = 'Production' ", nativeQuery = true)
	List<Object[]> getApproverOneFirstHalfInfo(Integer month);

	@Query(value = "select coalesce(second_half_status,'OPEN') as status,po.user_name approver,po.email approverEmail,po.project_name,\n"
			+ "concat(usr.last_name, \" \",usr.first_name) userName,po.approverFullName,po.project_tier\n"
			+ "from tasktrack_approval tf\n" + "join\n"
			+ "(select distinct p.project_id,p.project_name,u.user_name,u.email,concat(u.last_name,\" \",u.first_name) AS approverFullName\n"
			+ ",p.project_tier\n" + "from project p\n" + "left join user u on u.user_id = p.project_owner_user_id\n"
			+ "where p.project_tier=2) po on po.project_id = tf.project_project_id\n"
			+ "join user usr on usr.user_id = tf.user_user_id\n" + "and project_type='Billable' and tf.month = ?1 \n"
			+ " LEFT JOIN department d on d.department_id = usr.department_department_id where d.department_name = 'Production' "
			+ "\n" + "union all\n" + "\n"
			+ "select coalesce(second_half_status,'OPEN') as status,po.user_name approver,po.email approverEmail,po.project_name,\n"
			+ "concat(usr.last_name, \" \",usr.first_name) userName,po.approverFullName,po.project_tier\n"
			+ "from tasktrack_approval_final tf\n" + "join\n"
			+ "(select distinct p.project_id,p.project_name,u.user_name,u.email,concat(u.last_name,\" \",u.first_name) AS approverFullName\n"
			+ ",p.project_tier\n" + "from project p\n" + "left join user u on u.user_id = p.project_owner_user_id\n"
			+ "where p.project_tier=1) po on po.project_id = tf.project_project_id\n"
			+ "join user usr on usr.user_id = tf.user_user_id\n" + "and project_type='Billable' and tf.month = ?1 "
			+ " LEFT JOIN department d on d.department_id = usr.department_department_id where d.department_name = 'Production' ", nativeQuery = true)
	List<Object[]> getApproverOneSecondHalfInfo(Integer month);

	@Query(value = "select coalesce(first_half_status,'OPEN') as `first_half_status`,coalesce(second_half_status,'OPEN') as `second_half_status`,po.user_name approver,po.email approverEmail,po.project_name,\r\n"
			+ "			concat(usr.last_name, \" \",usr.first_name) userName,po.approverFullName \r\n"
			+ "			from tasktrack_approval_final tf\r\n" + "			join \r\n"
			+ "			(select distinct p.project_id,p.project_name,u.user_name,u.email,concat(u.last_name,\" \",u.first_name) AS approverFullName \r\n"
			+ "			from project p\r\n" + "			left join user u on u.user_id = p.onsite_lead_user_id\r\n"
			+ "			where p.project_tier=2) po on po.project_id = tf.project_project_id\r\n"
			+ "			join user usr on usr.user_id = tf.user_user_id\r\n"
			+ "			and project_type='Billable' and tf.month = ?1", nativeQuery = true)
	List<Object[]> getApproverTwoPreviousMonthData(int month);

	@Query(value = "select coalesce(first_half_status,'OPEN') as `first_half_status`,coalesce(second_half_status,'OPEN') as `second_half_status`,po.user_name approver,po.email approverEmail,po.project_name,\n"
			+ "concat(usr.last_name, \" \",usr.first_name) userName,po.approverFullName,po.project_tier\n"
			+ "from tasktrack_approval tf\n" + "join\n"
			+ "(select distinct p.project_id,p.project_name,u.user_name,u.email,concat(u.last_name,\" \",u.first_name) AS approverFullName\n"
			+ ",p.project_tier\n" + "from project p\n" + "left join user u on u.user_id = p.project_owner_user_id\n"
			+ "where p.project_tier=2) po on po.project_id = tf.project_project_id\n"
			+ "join user usr on usr.user_id = tf.user_user_id\n" + "and project_type='Billable' and tf.month = ?1 \n"
			+ "\n" + "union all\n" + "\n"
			+ "select coalesce(first_half_status,'OPEN') as `first_half_status`,coalesce(second_half_status,'OPEN') as `second_half_status`,po.user_name approver,po.email approverEmail,po.project_name,\n"
			+ "concat(usr.last_name, \" \",usr.first_name) userName,po.approverFullName,po.project_tier\n"
			+ "from tasktrack_approval_final tf\n" + "join\n"
			+ "(select distinct p.project_id,p.project_name,u.user_name,u.email,concat(u.last_name,\" \",u.first_name) AS approverFullName\n"
			+ ",p.project_tier\n" + "from project p\n" + "left join user u on u.user_id = p.project_owner_user_id\n"
			+ "where p.project_tier=1) po on po.project_id = tf.project_project_id\n"
			+ "join user usr on usr.user_id = tf.user_user_id\n" + "and project_type='Billable' and tf.month = ?1 ;\n"
			+ "", nativeQuery = true)
	List<Object[]> getApproverOnePreviousMonthInfo(int month);

	@Query("SELECT t.project.projectId,t.project.projectTier FROM Tasktrack t "
			+ "WHERE t.user.userId=?1 AND CAST(t.date as date) BETWEEN ?2 AND ?3 group by t.project.projectId,t.project.projectTier")
	List<Object[]> getProjectTierForTaskTrack(Long userId, Date startDate, Date endDate);

	@Query(value = "SELECT t.id AS taskTrackId, t.date,\r\n" + "			p.project_name, \r\n"
			+ "			tm.task_name,\r\n" + "			t.description,\r\n" + "			t.hours,\r\n"
			+ "			COALESCE(case when day(t.date) <= 15 then ta.first_half_status else ta.second_half_status end,'OPEN') as approvalStatus, p.project_id as projectId, p.project_tier\r\n"
			+ "			FROM tasktrack t \r\n" + "			LEFT JOIN tasktrack_approval ta\r\n"
			+ "				ON (t.user_user_id = ta.user_user_id  \r\n"
			+ "				AND ta.project_project_id = t.project_project_id\r\n"
			+ "				AND ta.month = month(t.date) \r\n"
			+ "				AND ta.year = year(t.date)  and ta.project_type=\"Billable\") \r\n"
			+ "			INNER JOIN project p \r\n" + "				on p.project_id = t.project_project_id \r\n"
			+ "			INNER JOIN task_master tm \r\n" + "				on tm.id = t.task_id \r\n"
			+ "			WHERE t.user_user_id = :uId \r\n"
			+ "            AND t.project_project_id IN :projectId AND CAST(t.date as date) BETWEEN :fromDate AND :toDate "
			+ "			 ORDER BY date(t.date) ASC ", nativeQuery = true)
	List<Object[]> getTrackTaskListTire2(@Param("uId") long userId, @Param("projectId") List<Long> projectId,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query(value = "SELECT t.id AS taskTrackId, t.date,\r\n" + "			p.project_name, \r\n"
			+ "			tm.task_name,\r\n" + "			t.description,\r\n" + "			t.hours,\r\n"
			+ "			COALESCE(case when day(t.date) <= 15 then ta.first_half_status else ta.second_half_status end,'OPEN') as approvalStatus, p.project_id as projectId, p.project_tier\r\n"
			+ "			FROM tasktrack t \r\n" + "			LEFT JOIN tasktrack_approval_final ta\r\n"
			+ "				ON (t.user_user_id = ta.user_user_id  \r\n"
			+ "				AND ta.project_project_id = t.project_project_id\r\n"
			+ "				AND ta.month = month(t.date) \r\n"
			+ "				AND ta.year = year(t.date)  and ta.project_type=\"Billable\") \r\n"
			+ "			INNER JOIN project p \r\n" + "				on p.project_id = t.project_project_id \r\n"
			+ "			INNER JOIN task_master tm \r\n" + "				on tm.id = t.task_id \r\n"
			+ "			WHERE t.user_user_id = :uId \r\n"
			+ "            AND t.project_project_id IN :projectId AND CAST(t.date as date) BETWEEN :fromDate AND :toDate "
			+ "			 ORDER BY date(t.date) ASC ", nativeQuery = true)
	List<Object[]> getTrackTaskListTire1(@Param("uId") long userId, @Param("projectId") List<Long> projectId,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query("select date(tt.date),tt.hours from Tasktrack tt join tt.user u join tt.project p where u.userId=?1 and p.projectId=?2 and month(tt.date)=?3 and year(tt.date)=?4 and day(tt.date) between ?5 and ?6 order by tt.date")
	List<Object[]> getTaskTrackProjectHoursForUser(Long userId, Long projectId, Integer month, Integer year,
			Integer startDay, Integer endDay);

	// Nisha

	@Query("SELECT DISTINCT a.project.projectId,a.project.projectName,a.project.clientName.clientName, a.project.projectTier from AllocationModel a where a.user.userId=?1 and  a.startDate <= ?2 and a.endDate >= ?2 and a.active = true order by a.project.projectName")
	public List<Object[]> getProjectNamesByAllocation(long uId, Date curdate) throws Exception;

	@Query(value = "select p.project_id, p.project_name , t.`date`,t.id,c.client_name  ,sum(t.hours) "
			+ "from tasktrack t left join project p on p.project_id = t.project_project_id "
			+ "join `user` u  on u.user_id = t.user_user_id "
			+ "left join `client` c on c.client_id=p.client_name_client_id " + "where u.user_id = ?1 and project_id = "
			+ Constants.BEACH_PROJECT_ID
			+ " and t.`date`>=?2 and t.`date`<=?3  group by 1,2,3,4,5 order by 1,2", nativeQuery = true)
	public List<Object[]> getBeachTasksFortimeTrack(@Param("userId") long userId, @Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate) throws Exception;

	@Query("SELECT COALESCE(case when day(:currentDate) <= 15 then ta.firstHalfStatus else ta.secondHalfStatus end,'OPEN'), ta.project.projectId \r\n"
			+ "	FROM TaskTrackApprovalFinal ta 					\r\n" + "	WHERE ta.user.userId = :userId \r\n"
			+ "    AND ta.month = month(:currentDate) AND ta.year = year(:currentDate)  and ta.projectType='Billable'\r\n"
			+ "	AND ta.project.projectId IN :projectIds")
	public List<Object[]> getTaskApprovalStatusForProjectsTire1(@Param("userId") long userId,
			@Param("currentDate") Date currentDate, @Param("projectIds") List<Long> projectIds);

	@Query("SELECT COALESCE(case when day(:currentDate) <= 15 then ta.firstHalfStatus else ta.secondHalfStatus end,'OPEN'), ta.project.projectId \r\n"
			+ "	FROM TaskTrackApproval ta 					\r\n" + "	WHERE ta.user.userId = :userId \r\n"
			+ "    AND ta.month = month(:currentDate) AND ta.year = year(:currentDate)  and ta.projectType='Billable'\r\n"
			+ "	AND ta.project.projectId IN :projectIds")
	public List<Object[]> getTaskApprovalStatusForProjectsTire2(@Param("userId") long userId,
			@Param("currentDate") Date currentDate, @Param("projectIds") List<Long> projectIds);

	@Query("select Date(allocation.startDate),Date(allocation.endDate),allocation.user.userName from AllocationModel allocation where allocation.project.projectName = ?2 and allocation.endDate >= Date(?1)")
	List<Object[]> getAllocationDateList(LocalDate fromDate, String projectName);

	/**
	 * @author sreejith.j
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<Tasktrack> findByUserUserIdAndProjectProjectIdInAndDateBetween(Long userId, List<Long> projectIds, Date startDate,
			Date endDate);

	@Query(value = "SELECT * FROM tasktrack WHERE date BETWEEN :startDate AND :endDate and user_user_id = :userId",nativeQuery=true)
	public ArrayList<Tasktrack> getsavedTaskslist(Date startDate, Date endDate, Long userId);


}
