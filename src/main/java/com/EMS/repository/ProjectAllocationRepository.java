package com.EMS.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.EMS.model.AllocationModel;

import javax.transaction.Transactional;


public interface ProjectAllocationRepository extends JpaRepository<AllocationModel, Long> {


	@Query("SELECT s FROM AllocationModel s WHERE s.project.projectId = ?1")
	List<AllocationModel> getProjectLists(Long projectId);
//	public default List<Alloc> getProjectLists(Long projectId){
//	getJdbcTemplate().query("select user_name as userName", new RowMapper());
//	}
	
	public default void a() {
		
	}

	@Query("SELECT count(*) > 0 FROM AllocationModel s WHERE s.user.userId = ?1")
	Boolean isExists(long userId);

	@Query("SELECT s FROM AllocationModel s WHERE s.user.userId = ?1")
	List<AllocationModel> findByUserId(long userId);

	@Query(value = "SELECT s FROM AllocationModel s WHERE s.user.userId =:userId and s.startDate <:date2 and s.endDate >:date1 order by user.firstName")
	List<AllocationModel> findUsers(long userId, Date date1, Date date2);

	@Query("SELECT s.allocId FROM AllocationModel s WHERE s.user.userId = ?2 and s.project.projectId = ?1")
	Long getAllocationId(long projectId, long userId);
	

//	@Query("select u.user.userId from AllocationModel u where u.project.projectId = ?1")
//	List<Object[]> getUserIdByProject(Long projectId, Long pageSize, Long limit);
	
//	@Query(value ="SELECT EMS.allocation.user_user_id FROM EMS.allocation where EMS.allocation.project_project_id = ?1 LIMIT ?3,?2",nativeQuery = true)
//	List<Object[]> getUserIdByProject(Long projectId, Long pageSize, Long limit);
	
	@Query("select u.user.userId from AllocationModel u where u.project.projectId = ?1 and u.user.active = true and u.active = true")
	List<Object[]> getUserIdByProject(Long projectId);

	//@Query("select u.user.userId from AllocationModel u where u.project.projectId = ?1 and ((u.startDate between ?2 and ?3) OR (u.endDate between ?2 and ?3)) and u.user.active = true and u.active = true")
	@Query("select u.user.userId from AllocationModel u where u.project.projectId = ?1 and u.startDate >=?2 and u.endDate <=?3  and u.user.active = true and u.active = true")
	List<Object[]> getUserIdByProjectAndDate(Long projectId,Date startDate, Date endDate);

	@Query(value = "SELECT DISTINCT(tasktrack.project_project_id),project.project_name FROM tasktrack JOIN project ON project.project_id = tasktrack.project_project_id where (tasktrack.date <=?3 and tasktrack.date >=?2 ) and (tasktrack.user_user_id =?1) and project.is_billable=1",nativeQuery = true)
	List<Object[]> getProjectListByUserAndDate(Long id, Date startDate, Date endDate);

	@Query(value = "SELECT COUNT(allocation.user_user_id) FROM allocation where allocation.project_project_id = ?1",nativeQuery = true)
	Long getUserCount(Long projectId);
	
	@Query("SELECT a.isBillable FROM AllocationModel a where a.user.userId = ?1 and a.project.projectId = ?2")
	Boolean getIsBillable(Long id, Long projectId);

	@Query(value="SELECT * FROM allocation where project_project_id=:projectId AND date(start_date)>=:startDate AND date(end_date)<=:endDate",nativeQuery=true)
	List<AllocationModel> getProjectDatewiseLists(long projectId, LocalDate startDate, LocalDate endDate);




//	@Query(value = "SELECT * FROM EMS.alloc where  EMS.alloc.user_user_id = ?1 and EMS.alloc.end_date < ?3 or EMS.alloc.start_date > ?1", nativeQuery = true)
//	List<Alloc> findUsers(long userId, Date date1, Date date2);



	
}
