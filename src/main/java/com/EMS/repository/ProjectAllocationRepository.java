package com.EMS.repository;

import java.awt.print.Pageable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.EMS.model.AllocationModel;
import com.EMS.model.UserModel;

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

	//# Updated by Rinu 26-09-2019
	@Query(value = "SELECT s FROM AllocationModel s WHERE s.user.userId =:userId and s.startDate <:date2 and s.endDate >:date1 order by user.firstName")
	List<AllocationModel> findUsers(@Param("userId")long userId,@Param("date1") Date date1,@Param("date2") Date date2);

	@Query("SELECT s.allocId FROM AllocationModel s WHERE s.user.userId = ?2 and s.project.projectId = ?1")
	Long getAllocationId(long projectId, long userId);
	

//	@Query("select u.user.userId from AllocationModel u where u.project.projectId = ?1")
//	List<Object[]> getUserIdByProject(Long projectId, Long pageSize, Long limit);
	
//	@Query(value ="SELECT EMS.allocation.user_user_id FROM EMS.allocation where EMS.allocation.project_project_id = ?1 LIMIT ?3,?2",nativeQuery = true)
//	List<Object[]> getUserIdByProject(Long projectId, Long pageSize, Long limit);
	
	@Query("select u.user.userId from AllocationModel u where u.project.projectId = ?1 and u.user.active = true and u.active = true")
	List<Object[]> getUserIdByProject(Long projectId);

	//@Query("select u.user.userId from AllocationModel u where u.project.projectId = ?1 and ((u.startDate between ?2 and ?3) OR (u.endDate between ?2 and ?3)) and u.user.active = true and u.active = true")
	@Query("select u.user.userId from AllocationModel u where u.project.projectId = ?1 and u.startDate <=?3 and u.endDate >=?2 and u.active = true")
	List<Object[]> getUserIdByProjectAndDate(Long projectId,Date startDate, Date endDate);

	@Query(value = "SELECT DISTINCT(tasktrack.project_project_id),project.project_name FROM tasktrack JOIN project ON project.project_id = tasktrack.project_project_id where (tasktrack.date <=?3 and tasktrack.date >=?2 ) and (tasktrack.user_user_id =?1) and project.is_billable=1",nativeQuery = true)
	List<Object[]> getProjectListByUserAndDate(Long id, Date startDate, Date endDate);

	@Query(value = "SELECT COUNT(allocation.user_user_id) FROM allocation where allocation.project_project_id = ?1",nativeQuery = true)
	Long getUserCount(Long projectId);
	
	@Query("SELECT a.isBillable FROM AllocationModel a where a.user.userId = ?1 and a.project.projectId = ?2")
	Boolean getIsBillable(Long id, Long projectId);

	@Query(value="SELECT * FROM allocation where project_project_id=:projectId AND date(start_date)>=:startDate AND date(end_date)<=:endDate",nativeQuery=true)
	List<AllocationModel> getProjectDatewiseLists(long projectId, LocalDate startDate, LocalDate endDate);

	@Query("select u from AllocationModel u where u.project.projectId = ?1 and u.startDate <=?3 and u.endDate >=?2  and u.user.active = true and u.active = true")
	List<AllocationModel> getUserDataByProjectAndDate(Long projectId,Date startDate, Date endDate);
//	@Query(value = "SELECT * FROM EMS.alloc where  EMS.alloc.user_user_id = ?1 and EMS.alloc.end_date < ?3 or EMS.alloc.start_date > ?1", nativeQuery = true)
//	List<Alloc> findUsers(long userId, Date date1, Date date2);
	
	@Query(value="SELECT * FROM `allocation` as alloc Join project as pro ON alloc.project_project_id=pro.project_id where pro.project_category=2 AND alloc.start_date<=?1 AND alloc.end_date>=?1",nativeQuery=true)
	List<AllocationModel> getBenchResources(String datestring);

	     //Renjith
		@Query("select Distinct(u.user) from AllocationModel u where u.project.projectId = ?1 order by u.user.firstName Asc ")
		List<UserModel>  getUsersByProjectId(Long projectId );
		//Renjith

	
	@Query(value = "SELECT (100 - SUM(allocation.allocated_perce )) FROM allocation " + 
			" WHERE allocation.start_date <= ?2 AND allocation.user_user_id = ?1 ",nativeQuery = true)
	Object[] getAvailableAlloc(long userId,Date current);

	@Query(value ="SELECT (100 - SUM(allocation.allocated_perce)) FROM allocation WHERE allocation.user_user_id  = ?1 AND allocation.start_date <= ?2 AND allocation.end_date >= ?3",nativeQuery = true)
	Object[] getFreeAlloc(Long userId, Date fromDate, Date toDate);

	@Query(value = "SELECT allocation.alloc_id FROM allocation WHERE allocation.project_project_id = ?1 " + 
			" and allocation.user_user_id = ?2 " + 
			" order by allocation.end_date desc " + 
			" LIMIT 1",nativeQuery = true)
	BigInteger getAllocationContinousDateRange(Long projectId, Long userId, Date startDate, Date endDate);



	
}
