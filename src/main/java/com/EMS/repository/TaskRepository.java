package com.EMS.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.Task;
import com.EMS.model.TaskCategory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface TaskRepository extends JpaRepository<Task, Long> {
	
	@Query("SELECT t.id,t.taskName FROM Task t")
	List<Object[]> getTaskNameId();

	
	@Query(value = "SELECT user.first_name,user.last_name,tasktrack.hours,Date(tasktrack.date) FROM tasktrack JOIN user ON user.user_id = tasktrack.user_user_id where (tasktrack.date <=?3 and tasktrack.date >= ?2) and (tasktrack.user_user_id =?1)",nativeQuery = true)
	List<Object[]> getUserList(Long userId, Date startDate, Date endDate);

	
	
//	@Query("SELECT u.firstName,u.lastName,t.hours,t.date FROM Tasktrack t JOIN UserModel u ON u.userId = t.user.userId where (t.date <= ?3 and t.date >= ?2) and (t.user.userId = ?1)")
//	List<Object[]> getUserList(Long userId, Date startDate, Date endDate);

	@Query("SELECT u.firstName,u.lastName,t.hours,t.date FROM Tasktrack t JOIN UserModel u ON u.userId = t.user.userId where (t.date <= ?3 and t.date >= ?2) and (t.project.projectId = ?1)")
	List<Object[]> getUserListByProjectId(Long projectId, Date startDate, Date endDate);


	@Query("SELECT u.firstName,u.lastName,t.hours,t.date FROM Tasktrack t JOIN UserModel u ON u.userId = t.user.userId where (t.date <= ?2 and t.date >= ?1)")
	List<Object[]> getUserListByDate(Date startDate, Date endDate);

	@Query(value = "SELECT user.first_name,user.last_name,tasktrack.hours,tasktrack.date FROM tasktrack JOIN user ON user.user_id = tasktrack.user_user_id where (tasktrack.date <=?3 and tasktrack.date >= ?2) and (tasktrack.user_user_id =?1)LIMIT ?5,?4",nativeQuery = true)
	List<Object[]> getUserListNew(Long userId, Date startDate, Date endDate, Long pageSize, Long startingIndex);


	@Query(value = "select sum(tasktrack.hours),tasktrack.task_id,concat(user.first_name,' ',user.last_name),task_master.task_name from tasktrack JOIN user JOIN task_master ON (tasktrack.user_user_id = user.user_id and tasktrack.task_id = task_master.id) where (tasktrack.user_user_id = ?1) and (tasktrack.date >= ?2 and tasktrack.date <= ?3) and tasktrack.project_project_id = ?4 group by tasktrack.task_id",nativeQuery = true)
	List<Object[]> getUserTaskList(Long id, Date startDate, Date endDate, Long projectId);

	//@Query(value = "SELECT EMS.user.first_name,EMS.user.last_name,EMS.tasktrack.hours,Date(EMS.tasktrack.date) FROM EMS.tasktrack JOIN EMS.user ON EMS.user.user_id = EMS.tasktrack.user_user_id where (date(EMS.tasktrack.date) <=?3 and date(EMS.tasktrack.date) >= ?2) and (EMS.tasktrack.user_user_id =?1) and (EMS.tasktrack.project_project_id = ?4) order by EMS.tasktrack.date DESC",nativeQuery = true)
	@Query(value = "SELECT user.first_name,user.last_name,tasktrack.hours,Date(tasktrack.date) FROM tasktrack JOIN user ON user.user_id = tasktrack.user_user_id where (date(tasktrack.date) <=date(?3) and date(tasktrack.date) >= date(?2)) and (tasktrack.user_user_id =?1) and (tasktrack.project_project_id = ?4) order by tasktrack.date ASC",nativeQuery = true)
	List<Object[]> getUserListByProject(Long id, Date startDate, Date endDate, Long projectId);

	@Query(value = "SELECT DISTINCT(tasktrack.project_project_id),project.project_name FROM tasktrack JOIN project ON project.project_id = tasktrack.project_project_id where (tasktrack.date <=?3 and tasktrack.date >=?2 ) and (tasktrack.user_user_id =?1)",nativeQuery = true)
	List<Object[]> getProjectListByUserAndDate(Long id, Date startDate, Date endDate);

	@Query(value = "SELECT user.first_name,user.last_name,tasktrack.hours,Date(tasktrack.date) FROM tasktrack JOIN user ON user.user_id = tasktrack.user_user_id where (tasktrack.date <=?4 and tasktrack.date >=?3 ) and (tasktrack.user_user_id =?1) and (tasktrack.project_project_id =?2)",nativeQuery = true)
	List<Object[]> getUserTaskByProjectAndDate(Long userId, Long projectId, Date startDate, Date endDate);

	@Query("SELECT tasks FROM TaskCategory tasks where tasks.id = ?1")
	TaskCategory findByTaskId(Long taskId);

	@Query("SELECT t from Task t where taskName = ?1 ")
	Task getTaskByName(String taskName);


	
}

