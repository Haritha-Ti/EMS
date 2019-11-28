package com.EMS.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.EMS.model.BenchProjectReportModel;
import com.EMS.model.ExportProjectTaskReportModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.ProjectReportModel;
import com.EMS.model.TaskTrackApproval;
import com.EMS.model.Technology;
import com.EMS.utility.BenchReportRowMapper;
import com.EMS.utility.DbConnectionUtility;
import com.EMS.utility.ExportProjectTaskReportRowMapper;
import com.EMS.utility.JsonNodeRowMapper;
import com.EMS.utility.ReportRowMapper;
import com.EMS.utility.TimeTrackApprovalRowMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public interface TimeTrackApprovalJPARepository extends JpaRepository<TaskTrackApproval, Long> {

	/*
	 * @Query(value ="SELECT user.user_id as id," +
	 * "sum(COALESCE(day1,0)) as day1,sum(COALESCE(day2,0)) as day2,sum(COALESCE(day3,0)) as day3,sum(COALESCE(day4,0)) as day4,sum(COALESCE(day5,0)) as day5,"
	 * +
	 * "sum(COALESCE(day6,0)) as day6,sum(COALESCE(day7,0)) as day7,sum(COALESCE(day8,0)) as day8,sum(COALESCE(day9,0)) as day9,sum(COALESCE(day10,0)) as day10,"
	 * +
	 * "sum(COALESCE(day11,0)) as day11,sum(COALESCE(day12,0)) as day12,sum(COALESCE(day13,0)) as day13,sum(COALESCE(day14,0)) as day14,sum(COALESCE(day15,0)) as day15,"
	 * +
	 * "sum(COALESCE(day16,0)) as day16,sum(COALESCE(day17,0)) as day17,sum(COALESCE(day18,0)) as day18,sum(COALESCE(day19,0)) as day19,sum(COALESCE(day20,0)) as day20,"
	 * +
	 * "sum(COALESCE(day21,0)) as day21,sum(COALESCE(day22,0)) as day22 ,sum(COALESCE(day23,0)) as day23,sum(COALESCE(day24,0)) as day24,sum(COALESCE(day25,0)) as day25,"
	 * +
	 * "sum(COALESCE(day26,0)) as day26,sum(COALESCE(day27,0)) as day27,sum(COALESCE(day28,0)) as day28,sum(COALESCE(day29,0)) as day29,sum(COALESCE(day30,0)) as day30,"
	 * +
	 * "sum(COALESCE(day31,0)) as day31 FROM tasktrack_approval where user_user_id=?3 and month=?1 and year=?2 and project_type in('Billable','Overtime','Non-Billable')"
	 * ,nativeQuery = true)
	 */
	@Query(value = "SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+"
			+ "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+"
			+ "sum(COALESCE(day15,0))+sum(COALESCE(day16,0))+sum(COALESCE(day17,0))+sum(COALESCE(day18,0))+sum(COALESCE(day19,0))+sum(COALESCE(day20,0))+sum(COALESCE(day21,0))+"
			+ "sum(COALESCE(day22,0))+sum(COALESCE(day23,0))+sum(COALESCE(day24,0))+sum(COALESCE(day25,0))+sum(COALESCE(day26,0))+sum(COALESCE(day27,0))+sum(COALESCE(day28,0))+"
			+ "sum(COALESCE(day29,0))+sum(COALESCE(day30,0))+sum(COALESCE(day31,0))) as totalhour FROM tasktrack_approval f"
			+ " INNER JOIN project p ON ( p.project_id = f.project_project_id) "
			+ " where f.user_user_id=?3 and f.month=?1 and f.year=?2 and f.project_type in('Billable','Overtime','Non-Billable')"
			+ "and  (CASE WHEN ?3 = 1 " + "          THEN p.project_type = 1 " + "          WHEN ?3 = 0 "
			+ "          THEN p.project_type = 0 " + "          ELSE p.project_id != 0 END) ", nativeQuery = true)
	List<Object[]> getTimeTrackApprovalDataByUserId(Integer monthIndex, Integer yearIndex, Long id, int projectType);

	@Query("SELECT count(*) > 0 FROM TaskTrackApproval s WHERE s.user.userId = ?1")
	Boolean existsByUser(Long id);
	
	@Query(value = "SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+"
			+ "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+"
			+ "sum(COALESCE(day15,0))) as totalhour FROM tasktrack_approval where user_user_id=?3 and month=?1 and year=?2 and project_type in('Billable','Overtime','Non-Billable')", nativeQuery = true)
	List<Object[]> getTimeTrackApprovalDataByUserIdMidMonth(Integer monthIndex, Integer yearIndex, Long id);

	@Query(value = "SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+"
			+ "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+"
			+ "sum(COALESCE(day15,0))+sum(COALESCE(day16,0))+sum(COALESCE(day17,0))+sum(COALESCE(day18,0))+sum(COALESCE(day19,0))+sum(COALESCE(day20,0))+sum(COALESCE(day21,0))+"
			+ "sum(COALESCE(day22,0))+sum(COALESCE(day23,0))+sum(COALESCE(day24,0))+sum(COALESCE(day25,0))+sum(COALESCE(day26,0))+sum(COALESCE(day27,0))+sum(COALESCE(day28,0))+"
			+ "sum(COALESCE(day29,0))+sum(COALESCE(day30,0))+sum(COALESCE(day31,0))) as billablehour FROM tasktrack_approval where user_user_id=?3 and month=?1 and year=?2 and project_type in('Billable')", nativeQuery = true)
	List<Object[]> getBillableDataByUserId(Integer monthIndex, Integer yearIndex, Long id);

	@Query(value = "SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+"
			+ "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+"
			+ "sum(COALESCE(day15,0))) as billablehour FROM tasktrack_approval where user_user_id=?3 and month=?1 and year=?2 and project_type in('Billable')", nativeQuery = true)
	List<Object[]> getBillableDataByUserIdMidMonth(Integer monthIndex, Integer yearIndex, Long id);

	@Query(value = "SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+"
			+ "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+"
			+ "sum(COALESCE(day15,0))+sum(COALESCE(day16,0))+sum(COALESCE(day17,0))+sum(COALESCE(day18,0))+sum(COALESCE(day19,0))+sum(COALESCE(day20,0))+sum(COALESCE(day21,0))+"
			+ "sum(COALESCE(day22,0))+sum(COALESCE(day23,0))+sum(COALESCE(day24,0))+sum(COALESCE(day25,0))+sum(COALESCE(day26,0))+sum(COALESCE(day27,0))+sum(COALESCE(day28,0))+"
			+ "sum(COALESCE(day29,0))+sum(COALESCE(day30,0))+sum(COALESCE(day31,0))) as billablehour FROM tasktrack_approval where user_user_id=?3 and month=?1 and year=?2 and project_type in('Non-Billable')", nativeQuery = true)
	List<Object[]> getNonBillableDataByUserId(Integer monthIndex, Integer yearIndex, Long id);

	@Query(value = "SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+"
			+ "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+"
			+ "sum(COALESCE(day15,0))) as billablehour FROM tasktrack_approval where user_user_id=?3 and month=?1 and year=?2 and project_type in('Non-Billable')", nativeQuery = true)
	List<Object[]> getNonBillableDataByUserIdMidMonth(Integer monthIndex, Integer yearIndex, Long id);

	@Query(value = "SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+"
			+ "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+"
			+ "sum(COALESCE(day15,0))+sum(COALESCE(day16,0))+sum(COALESCE(day17,0))+sum(COALESCE(day18,0))+sum(COALESCE(day19,0))+sum(COALESCE(day20,0))+sum(COALESCE(day21,0))+"
			+ "sum(COALESCE(day22,0))+sum(COALESCE(day23,0))+sum(COALESCE(day24,0))+sum(COALESCE(day25,0))+sum(COALESCE(day26,0))+sum(COALESCE(day27,0))+sum(COALESCE(day28,0))+"
			+ "sum(COALESCE(day29,0))+sum(COALESCE(day30,0))+sum(COALESCE(day31,0))) as billablehour FROM tasktrack_approval where user_user_id=?3 and month=?1 and year=?2 and project_type in('Overtime')", nativeQuery = true)
	List<Object[]> getOvertimeDataByUserId(Integer monthIndex, Integer yearIndex, Long id);

	@Query(value = "SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+"
			+ "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+"
			+ "sum(COALESCE(day15,0))) as billablehour FROM tasktrack_approval where user_user_id=?3 and month=?1 and year=?2 and project_type in('Overtime')", nativeQuery = true)
	List<Object[]> getOvertimeDataByUserIdMidMonth(Integer monthIndex, Integer yearIndex, Long id);

	@Query(value = "SELECT s.forwarded_date FROM tasktrack_approval s WHERE s.month = ?3 and s.year = ?4 and s.project_project_id = ?1 and s.user_user_id = ?2 LIMIT 1 ", nativeQuery = true)
	List<Object> getForwardedDate(Long projectId, Long userId, int intMonth, int year);

	@Query(value = "SELECT s.forwarded_date,s.forwarded_finance,s.approved_date FROM tasktrack_approval s WHERE s.month = ?3 and s.year = ?4 and s.project_project_id = ?1 and s.user_user_id = ?2 LIMIT 1 ", nativeQuery = true)
	List<Object[]> getForwardedDates(Long projectId, Long userId, int intMonth, int yearIndex);

	@Query(value = "SELECT count(*) as totalrow FROM tasktrack_approval WHERE month=?1 and  year=?2 and project_project_id=?3 ", nativeQuery = true)
	Long getCountOfRows(int month, int year, Long projectId);

	@Query("SELECT a FROM TaskTrackApproval a  where a.project.projectId = ?1 and a.month = ?2 and a.year = ?3 and a.projectType = 'Billable' ")
	TaskTrackApproval getDataForCheckingForwardedToLevel2(Long projectId, int intMonth, int yearIndex);

	@Query(value = "SELECT count(*) as totalrow FROM tasktrack_approval WHERE month=?1 and  year=?2 and project_project_id=?3 and user_user_id=?4", nativeQuery = true)
	Long getCountOfRowsByUser(int month, int year, Long projectId, Long userId);

	@Query(value = "SELECT  approved_date FROM tasktrack_approval WHERE month=?1 and  year=?2 and project_project_id=?3 and user_user_id=?4 and project_type = 'Billable'", nativeQuery = true)
	Object[] getapprovedDates(int month, int year, Long projectId, Long userId);

	@Query(value = "SELECT distinct(status) FROM tasktrack_approval_finance WHERE month=?1 and  year=?2 and project_project_id=?3 and user_user_id=?4", nativeQuery = true)
	Object[] getapprovedStatus(Integer month, Integer year, Long projectId, Long userId);

	@Query("SELECT  s FROM TaskTrackApproval s  WHERE s.month=?1 and  s.year=?2 and s.project.projectId =?3 and s.user.userId=?4 and s.projectType = 'Billable'")
	TaskTrackApproval getapprovedDates2(int intMonth, int yearIndex, Long projectId, Long userId);

	@Query(value = "SELECT id  FROM tasktrack_approval where project_project_id=?3 and user_user_id=?4 and month=?1 and year=?2 and project_type in('Billable') order by id desc limit 1", nativeQuery = true)
	Long getBillableIdForAUserForAProject(Integer month, Integer year, Long projectId, Long userId);
	
	@Query(value = "SELECT id FROM tasktrack_approval where project_project_id=?3 and user_user_id=?4 and month=?1 and year=?2 and project_type in('Non-Billable') order by id desc limit 1", nativeQuery = true)
	Long getNonBillableIdForAUserForAProject(Integer month, Integer year, Long projectId, Long userId);
	
	@Query(value = "SELECT id  FROM tasktrack_approval where project_project_id=?3 and user_user_id=?4 and month=?1 and year=?2 and project_type in('Overtime') order by id desc limit 1", nativeQuery = true)
	Long getOvertimeIdForAUserForAProject(Integer month, Integer year, Long projectId, Long userId);

	@Query("SELECT a FROM TaskTrackApproval a  where a.project.projectId = ?1 and a.month = ?2 and a.year = ?3 and a.user.userId = ?4 ")
	List<TaskTrackApproval> upadateTaskTrackApprovalStatus(Long projectId, int intMonth, int year,Long userId);

	@Query("SELECT approval FROM TaskTrackApproval approval "
			+ "where approval.month = ?3 and approval.year = ?4 and approval.project.projectId = ?2 and approval.user.userId = ?1")
	List<TaskTrackApproval> getUserListForApproval(Long userId,Long projectId,Integer month,Integer year);

	@Query("SELECT a FROM TaskTrackApproval a  where a.project.projectId = ?2 and a.month = ?3 and a.year = ?4 and user.userId = ?1 ")
	List<TaskTrackApproval> getUserListForApprovalApproverOne(Long userId, Long projectId, int month, int year);
	
	//@Query("SELECT a FROM TaskTrackApproval a  where a.project.projectId = ?2 and a.month = ?3 and a.year = ?4 and user.userId = ?1 ")
	List<TaskTrackApproval> findByUserUserIdAndProjectProjectIdAndMonthAndYear(Long userId, Long projectId, Integer month, Integer year);
	@Query("SELECT a FROM TaskTrackApproval a  where a.project.projectId = ?2 and a.month = ?3 and a.year = ?4 and user.userId = ?1 ")
	List<TaskTrackApproval> getSubmittedDatasOfApproverOne(int month, int year, Long projectId);

	@Query("SELECT a FROM TaskTrackApproval a where a.project.projectId = ?1 and a.user.userId = ?2 and a.month = ?3 and a.year = ?4")
	List<TaskTrackApproval> approverLevelOneHalfDetails(long projectId, long userId, int month, int year);

}
