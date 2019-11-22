package com.EMS.repository;


import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.UserModel;
import com.fasterxml.jackson.databind.JsonNode;

public interface UserRepository extends JpaRepository<UserModel, Long>{
		
	@Query("SELECT u FROM UserModel u WHERE u.userName=?1 AND u.password=?2 AND u.active = true") //Query for checking username and password are matching or not
//	@Query("SELECT u FROM UserModel u WHERE u.userName=?1 AND u.active = true") 
	UserModel getUserdetails(String userName, String password);

	@Query("SELECT u FROM UserModel u WHERE u.role in (11,8)")//for getting user details with role as owner by providing role
	List<UserModel> getProjectOwners();

	@Query("SELECT u FROM UserModel u WHERE u.role in(2,3,4,5) AND u.active = true order by firstName")
	List<UserModel> getUser();
	
	@Query("SELECT u FROM UserModel u WHERE u.userName=?1")
	UserModel getUserDetailsByName(String userName);

	@Query("SELECT u FROM UserModel u WHERE u.department.departmentId = ?1 AND u.active = true AND u.role = 3 order by firstName")
	List<UserModel> findByDeptId(Long deptId);

	@Query("SELECT u FROM UserModel u WHERE u.userId = ?1 and u.department.departmentId = ?2 AND u.active = true order by firstName")
	UserModel getUser(Long userId,Long deptId);

	@Query("SELECT u FROM UserModel u WHERE u.userId = ?1 AND u.active = true")
	UserModel getActiveUser(Long id);
	
	//authentication based query
	@Query("SELECT u FROM UserModel u WHERE u.userName=:username")
	UserModel getUser(String username);

	@Query("SELECT u FROM UserModel u WHERE u.userId=?1 AND u.password=?2") 
	UserModel getUserByUserId(Long userId, String password);
	
//	@Query(value = "SELECT user_id FROM EMS.user LIMIT ?2,?1",nativeQuery = true)
//	List<Object[]> getUserIdLists(Long pageSize, Long startingIndex);
	
	@Query("select u.userId from UserModel u")
	List<Object[]> getUserIdLists();

	@Query(value = "SELECT last_name,first_name FROM user where user.user_id = ?1",nativeQuery = true)
	String getUserName(Long id);

	@Query(value = "SELECT COUNT(user_id) FROM user",nativeQuery = true)
	Long getUserCount();

	@Query("select count(*)>0 from UserModel u where u.userName = ?1")
	Boolean checkExistanceOfUserName(String userName);

	@Query("select u from UserModel u where u.userId = ?1")
	Object getUserById(Long userId);

	@Query("select count(*)>0 from UserModel u where u.empId = ?1")
	Boolean checkExistanceOfEmpId(long empId);

	@Query(value="SELECT joining_date,first_name,last_name,cpp_level,emp_category,recruiter,referred_by,active,user_id FROM user where year(joining_date)=:year AND month(joining_date) between :startmonth AND :endmonth",nativeQuery=true)
	List<Object[]> findnewHire(int startmonth,int endmonth, int year);

//	@Query("select u from UserModel u where u.userName = ?1")
	Optional<UserModel> findByUserName(String username);

	@Query(value = "SELECT user_id,first_name,last_name,joining_date,termination_date,c.level_name FROM user u INNER JOIN cpp_level  c on (u.cpplevels_id = c.id) where role_role_id in('2','3','5') and  department_department_id in('1','2','3','4','8') and (termination_date >= ?1 or termination_date IS NULL) and joining_date<=?2 order by first_name",nativeQuery = true)
	List<Object[]> getUserList(Date startDate, Date endDate);

	@Query("SELECT u FROM UserModel u WHERE u.role NOT IN(1) order by firstName")
	List<UserModel> getAllUsers();
	
	@Query("SELECT u FROM UserModel u WHERE u.role in (11,8)")
	List<UserModel> getOnsiteLeads();

	@Query("SELECT u FROM UserModel u WHERE u.role in(2,3,5) AND department_department_id in('1','2','3','4','8','6','5','7')   order by u.lastName")
	List<UserModel> getUserLists();
	
	@Query(value="SELECT count(*) FROM user where active=1 AND termination_date IS NULL",nativeQuery=true)
	int getAllActiveUsers(String datestring);
	@Query("SELECT u FROM UserModel u WHERE u.region.id = ?1 AND u.active = true AND u.role = 3 order by firstName")
	List<UserModel> getUserlistByregion(Long regionId);

	@Query("SELECT u FROM UserModel u WHERE u.userId = ?1 and u.department.departmentId = ?2  and u.region.id = ?3 AND u.active = true order by firstName")
	UserModel getUserlistByregionDeptuser(Long deptId, Long userId, Long regionId);

	@Query("SELECT u FROM UserModel u WHERE u.userId = ?2 and u.region.id = ?1  AND u.active = true order by firstName")
	UserModel getUserByRegion(Long regionId, Long userId);
	
	//Bala
	@Query("SELECT u FROM UserModel u WHERE u.role NOT IN(1)  and u.region.id=?1    order by firstName")
	List<UserModel> getAllUsersByRegion(Long regionId);
	//Bala
	
	//Renjith
	@Query("SELECT u FROM UserModel u WHERE u.region.id = ?1 AND u.active = true AND u.role not in('1') and department_department_id not in('5','6','7','11') order by firstName")
	List<UserModel> getUserlistByregionAndDepartment(Long regionId);
	
	@Query(value="SELECT count(*) FROM user where active=1 AND termination_date IS NULL AND region_id=?1",nativeQuery=true)
	int getAllActiveUsersByRegion(Long  regionId);
	//Renjith

	//Nisha
	@Query(value = "SELECT user_id,RTRIM(LTRIM(first_name)),RTRIM(LTRIM(last_name)),joining_date,termination_date,c.level_name FROM user u INNER JOIN cpp_level  c on (u.cpplevels_id = c.id) where role_role_id in('2','3','5','11') and  department_department_id in('1','2','3','4','8') and region_id = ?3 and (termination_date >= ?1 or termination_date IS NULL) and joining_date<=?2 order by first_name",nativeQuery = true)
	List<Object[]> getUserListByRegion(Date startDate, Date endDate,Long regionId);

	@Query("SELECT u FROM UserModel u WHERE  u.role.roleId in('2','3','5','11','9') and  department.departmentId in('1','2','3','4','8') and region.id = ?3 and (terminationDate >= ?1 or terminationDate IS NULL) and joiningDate<=?2 order by firstName")
	List<UserModel> getUserlistByregion(Date startDate, Date endDate,Long regionId);
	//Nisha
	
	//drishya
	@Query("SELECT u FROM UserModel u WHERE u.userId = ?1")
	UserModel getNonActiveUser(Long id);

}
