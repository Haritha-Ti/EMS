package com.EMS.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.EMS.model.TaskTrackApprovalFinal;

@Repository
public interface TaskTrackApprovalFinalRepository extends JpaRepository<TaskTrackApprovalFinal, Long> {
	
	@Query("SELECT finalApproval FROM TaskTrackApprovalFinal finalApproval "
			+ "where finalApproval.month = ?3 and finalApproval.year = ?4 and finalApproval.project.projectId = ?2 and finalApproval.user.userId = ?1")
	List<TaskTrackApprovalFinal> getUserFinalApprovalList(Long userId, Long projectId, Integer monthIndex, Integer yearIndex);

	/*********Report **********/

	@Query(value ="SELECT user_user_id as id,(sum(COALESCE(f.day1,0))+sum(COALESCE(f.day2,0))+sum(COALESCE(f.day3,0))+sum(COALESCE(f.day4,0))+sum(COALESCE(f.day5,0))+sum(COALESCE(f.day6,0))+sum(COALESCE(f.day7,0))+\n" +
			"          sum(COALESCE(f.day8,0))+sum(COALESCE(f.day9,0))+sum(COALESCE(f.day10,0))+sum(COALESCE(f.day11,0))+sum(COALESCE(f.day12,0))+sum(COALESCE(f.day13,0))+sum(COALESCE(f.day14,0))+\n" +
			"          sum(COALESCE(f.day15,0))+sum(COALESCE(f.day16,0))+sum(COALESCE(f.day17,0))+sum(COALESCE(f.day18,0))+sum(COALESCE(f.day19,0))+sum(COALESCE(f.day20,0))+sum(COALESCE(f.day21,0))+\n" +
			"          sum(COALESCE(f.day22,0))+sum(COALESCE(f.day23,0))+sum(COALESCE(f.day24,0))+sum(COALESCE(f.day25,0))+sum(COALESCE(f.day26,0))+sum(COALESCE(f.day27,0))+sum(COALESCE(f.day28,0))+\n" +
			"          sum(COALESCE(f.day29,0))+sum(COALESCE(f.day30,0))+sum(COALESCE(f.day31,0))) as totalhour FROM tasktrack_approval_final f " +
			" INNER JOIN project p ON ( p.project_id = f.project_project_id) "
			+ "where f.user_user_id=?3 and f.month=?1 and f.year=?2 and f.project_type in('Billable','Overtime','Non-Billable')  "
			/*+ " and (CASE WHEN ?4 = 1 " +
			"          THEN p.project_type = 1 " +
			"          WHEN ?4 = 0 " +
			"          THEN p.project_type = 0 " +
			"          ELSE p.project_id != 0 END) "*/,nativeQuery = true)
	List<Object[]> getTimeTrackApprovalDataByUserId(Integer monthIndex,Integer yearIndex,Long id, int projectType);

	@Query(value ="SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+" +
			"sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+" +
			"sum(COALESCE(day15,0))) as totalhour FROM tasktrack_approval_final f INNER JOIN project p ON ( p.project_id = f.project_project_id) where user_user_id=?3 and month=?1 and year=?2 and f.project_type in('Billable','Overtime','Non-Billable') "
			/*+" and   (CASE WHEN ?4 = 1 " +
            " THEN p.project_type = 1 " +
            " WHEN ?4 = 0  " +
            "  THEN p.project_type = 0 " +
            "  ELSE p.project_id != 0 END) "*/,nativeQuery = true)
	List<Object[]> getTimeTrackApprovalDataByUserIdMidMonth(Integer monthIndex,Integer yearIndex,Long id,int projectType);

	@Query(value ="SELECT user_user_id as id,(sum(COALESCE(f.day1,0))+sum(COALESCE(f.day2,0))+sum(COALESCE(f.day3,0))+sum(COALESCE(f.day4,0))+sum(COALESCE(f.day5,0))+sum(COALESCE(f.day6,0))+sum(COALESCE(f.day7,0))+\n" +
			"          sum(COALESCE(f.day8,0))+sum(COALESCE(f.day9,0))+sum(COALESCE(f.day10,0))+sum(COALESCE(f.day11,0))+sum(COALESCE(f.day12,0))+sum(COALESCE(f.day13,0))+sum(COALESCE(f.day14,0))+\n" +
			"          sum(COALESCE(f.day15,0))+sum(COALESCE(f.day16,0))+sum(COALESCE(f.day17,0))+sum(COALESCE(f.day18,0))+sum(COALESCE(f.day19,0))+sum(COALESCE(f.day20,0))+sum(COALESCE(f.day21,0))+\n" +
			"          sum(COALESCE(f.day22,0))+sum(COALESCE(f.day23,0))+sum(COALESCE(f.day24,0))+sum(COALESCE(f.day25,0))+sum(COALESCE(f.day26,0))+sum(COALESCE(f.day27,0))+sum(COALESCE(f.day28,0))+\n" +
			"          sum(COALESCE(f.day29,0))+sum(COALESCE(f.day30,0))+sum(COALESCE(f.day31,0))) as billablehour FROM tasktrack_approval_final f " +
			" INNER JOIN project p ON ( p.project_id = f.project_project_id) "
			+ "where f.user_user_id=?3 and f.month=?1 and f.year=?2 and f.project_type in('Billable')  "
			/*+ " and (CASE WHEN ?4 = 1 " +
            "          THEN p.project_type = 1 " +
            "          WHEN ?4 = 0 " +
            "          THEN p.project_type = 0 " +
            "          ELSE p.project_id != 0 END) " */,nativeQuery = true)
	List<Object[]> getBillableDataByUserId(Integer monthIndex,Integer yearIndex,Long id, int projectType);

	@Query(value ="SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+" +
			"sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+" +
			"sum(COALESCE(day15,0))) as billablehour FROM tasktrack_approval_final f  INNER JOIN project p ON ( p.project_id = f.project_project_id) where user_user_id=?3 and month=?1 and year=?2 and f.project_type in('Billable') "
			/*+" and   (CASE WHEN ?4 = 1 " +
            "          THEN p.project_type = 1 " +
            "          WHEN ?4 = 0 " +
            "          THEN p.project_type = 0 " +
            "          ELSE p.project_id != 0 END) "*/,nativeQuery = true)
	List<Object[]> getBillableDataByUserIdMidMonth(Integer monthIndex,Integer yearIndex,Long id,int projectType);

	@Query(value ="SELECT user_user_id as id,(sum(COALESCE(f.day1,0))+sum(COALESCE(f.day2,0))+sum(COALESCE(f.day3,0))+sum(COALESCE(f.day4,0))+sum(COALESCE(f.day5,0))+sum(COALESCE(f.day6,0))+sum(COALESCE(f.day7,0))+\n" +
			"          sum(COALESCE(f.day8,0))+sum(COALESCE(f.day9,0))+sum(COALESCE(f.day10,0))+sum(COALESCE(f.day11,0))+sum(COALESCE(f.day12,0))+sum(COALESCE(f.day13,0))+sum(COALESCE(f.day14,0))+\n" +
			"          sum(COALESCE(f.day15,0))+sum(COALESCE(f.day16,0))+sum(COALESCE(f.day17,0))+sum(COALESCE(f.day18,0))+sum(COALESCE(f.day19,0))+sum(COALESCE(f.day20,0))+sum(COALESCE(f.day21,0))+\n" +
			"          sum(COALESCE(f.day22,0))+sum(COALESCE(f.day23,0))+sum(COALESCE(f.day24,0))+sum(COALESCE(f.day25,0))+sum(COALESCE(f.day26,0))+sum(COALESCE(f.day27,0))+sum(COALESCE(f.day28,0))+\n" +
			"          sum(COALESCE(f.day29,0))+sum(COALESCE(f.day30,0))+sum(COALESCE(f.day31,0))) as billablehour FROM tasktrack_approval_final f" +
			" INNER JOIN project p ON ( p.project_id = f.project_project_id) "
			+ "where f.user_user_id=?3 and f.month=?1 and f.year=?2 and f.project_type in('Non-Billable') "
			/* + "  and (CASE WHEN ?4 = 1 " +
             "          THEN p.project_type = 1 " +
             "          WHEN ?4 = 0 " +
             "          THEN p.project_type = 0 " +
             "          ELSE p.project_id != 0 END) "*/
			,nativeQuery = true)
	List<Object[]> getNonBillableDataByUserId(Integer monthIndex,Integer yearIndex,Long id,int projectType);

	@Query(value ="SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+" +
			"sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+" +
			"sum(COALESCE(day15,0))) as billablehour FROM tasktrack_approval_final f INNER JOIN project p ON ( p.project_id = f.project_project_id) where user_user_id=?3 and month=?1 and year=?2 and f.project_type in('Non-Billable') "
			/*+"and   (CASE WHEN ?4 = 1 " +
            "          THEN p.project_type = 1 " +
            "          WHEN ?4 = 0 " +
            "          THEN p.project_type = 0 " +
            "          ELSE p.project_id != 0 END)"*/,nativeQuery = true)
	List<Object[]> getNonBillableDataByUserIdMidMonth(Integer monthIndex,Integer yearIndex,Long id,int projectType);

	@Query(value ="SELECT user_user_id as id,(sum(COALESCE(f.day1,0))+sum(COALESCE(f.day2,0))+sum(COALESCE(f.day3,0))+sum(COALESCE(f.day4,0))+sum(COALESCE(f.day5,0))+sum(COALESCE(f.day6,0))+sum(COALESCE(f.day7,0))+\n" +
			"          sum(COALESCE(f.day8,0))+sum(COALESCE(f.day9,0))+sum(COALESCE(f.day10,0))+sum(COALESCE(f.day11,0))+sum(COALESCE(f.day12,0))+sum(COALESCE(f.day13,0))+sum(COALESCE(f.day14,0))+\n" +
			"          sum(COALESCE(f.day15,0))+sum(COALESCE(f.day16,0))+sum(COALESCE(f.day17,0))+sum(COALESCE(f.day18,0))+sum(COALESCE(f.day19,0))+sum(COALESCE(f.day20,0))+sum(COALESCE(f.day21,0))+\n" +
			"          sum(COALESCE(f.day22,0))+sum(COALESCE(f.day23,0))+sum(COALESCE(f.day24,0))+sum(COALESCE(f.day25,0))+sum(COALESCE(f.day26,0))+sum(COALESCE(f.day27,0))+sum(COALESCE(f.day28,0))+\n" +
			"          sum(COALESCE(f.day29,0))+sum(COALESCE(f.day30,0))+sum(COALESCE(f.day31,0))) as billablehour FROM tasktrack_approval_final f " +
			" INNER JOIN project p ON ( p.project_id = f.project_project_id) "
			+ "where f.user_user_id=?3 and f.month=?1 and f.year=?2 and f.project_type in('Overtime')  "
			/* + " and (CASE WHEN ?4 = 1 " +
             "          THEN p.project_type = 1 " +
             "          WHEN ?4 = 0 " +
             "          THEN p.project_type = 0 " +
             "          ELSE p.project_id != 0 END) "*/,nativeQuery = true)
	List<Object[]> getOvertimeDataByUserId(Integer monthIndex,Integer yearIndex,Long id,int projectType);

	@Query(value ="SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+" +
			"sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+" +
			"sum(COALESCE(day15,0))) as billablehour FROM tasktrack_approval_final f INNER JOIN project p ON ( p.project_id = f.project_project_id) where user_user_id=?3 and month=?1 and year=?2 and f.project_type in('Overtime') "
			/*+" and   (CASE WHEN ?4 = 1 " +
            "          THEN p.project_type = 1 " +
            "          WHEN ?4 = 0 " +
            "          THEN p.project_type = 0 " +
            "          ELSE p.project_id != 0 END)"*/,nativeQuery = true)
	List<Object[]> getOvertimeDataByUserIdMidMonth(Integer monthIndex,Integer yearIndex,Long id,int projectType);

	@Query(value = "SELECT user_user_id as id,first_name,last_name, sum(COALESCE(day1,0)) as day1,sum(COALESCE(day2,0)) as day2,sum(COALESCE(day3,0)) as day3,sum(COALESCE(day4,0)) as day4,"+
			"sum(COALESCE(day5,0)) as day5,sum(COALESCE(day6,0)) as day6,sum(COALESCE(day7,0)) as day7,sum(COALESCE(day8,0)) as day8,sum(COALESCE(day9,0)) as day9," +
			"sum(COALESCE(day10,0)) as day10,sum(COALESCE(day11,0)) as day11,sum(COALESCE(day12,0)) as day12,sum(COALESCE(day13,0)) as day13,sum(COALESCE(day14,0)) as day14," +
			"sum(COALESCE(day15,0)) as day15,sum(COALESCE(day16,0)) as day16,sum(COALESCE(day17,0)) as day17,sum(COALESCE(day18,0)) as day18,sum(COALESCE(day19,0)) as day19," +
			"sum(COALESCE(day20,0)) as day20,sum(COALESCE(day21,0)) as day21,sum(COALESCE(day22,0)) as day22,sum(COALESCE(day23,0)) as day23,sum(COALESCE(day24,0)) as day24," +
			"sum(COALESCE(day25,0)) as day25,sum(COALESCE(day26,0)) as day26,sum(COALESCE(day27,0)) as day27,sum(COALESCE(day28,0)) as day28,sum(COALESCE(day29,0)) as day29," +
			"sum(COALESCE(day30,0)) as day30,sum(COALESCE(day31,0)) as day31 FROM tasktrack_approval_final ta  inner join user u ON u.user_id = ta.user_user_id where " +
			"month=?1 and year=?2 and project_project_id=?3 and project_type in('Billable','Overtime') group by user_user_id",nativeQuery = true)
	List<Object[]> getFinanceDataByProject(Integer monthIndex, Integer yearIndex, Long projectId);

	//Renjith

	@Query(value = "SELECT p.project_name,first_name,last_name,sum(COALESCE(day1,0)) as day1,sum(COALESCE(day2,0)) as day2,sum(COALESCE(day3,0)) as day3,sum(COALESCE(day4,0)) as day4,"+
			"sum(COALESCE(day5,0)) as day5,sum(COALESCE(day6,0)) as day6,sum(COALESCE(day7,0)) as day7,sum(COALESCE(day8,0)) as day8,sum(COALESCE(day9,0)) as day9," +
			"sum(COALESCE(day10,0)) as day10,sum(COALESCE(day11,0)) as day11,sum(COALESCE(day12,0)) as day12,sum(COALESCE(day13,0)) as day13,sum(COALESCE(day14,0)) as day14," +
			"sum(COALESCE(day15,0)) as day15,sum(COALESCE(day16,0)) as day16,sum(COALESCE(day17,0)) as day17,sum(COALESCE(day18,0)) as day18,sum(COALESCE(day19,0)) as day19," +
			"sum(COALESCE(day20,0)) as day20,sum(COALESCE(day21,0)) as day21,sum(COALESCE(day22,0)) as day22,sum(COALESCE(day23,0)) as day23,sum(COALESCE(day24,0)) as day24," +
			"sum(COALESCE(day25,0)) as day25,sum(COALESCE(day26,0)) as day26,sum(COALESCE(day27,0)) as day27,sum(COALESCE(day28,0)) as day28,sum(COALESCE(day29,0)) as day29," +
			"sum(COALESCE(day30,0)) as day30,sum(COALESCE(day31,0)) as day31 FROM tasktrack_approval_final ta  inner join user u ON u.user_id = ta.user_user_id"
			+ " inner join project p on  p.project_id= ta.project_project_id "
			+ " where " +
			"month=?1 and year=?2 and project_project_id IN (?3)  and ta.project_type in('Billable','Overtime') group by user_user_id,p.project_name",nativeQuery = true)
	List<Object[]> getFinanceDataByProjectSet(Integer monthIndex, Integer yearIndex, Set<Long> ids);

	//Renjith

	@Query(value = "SELECT project_project_id as id,project_name, sum(COALESCE(day1,0)) as day1,sum(COALESCE(day2,0)) as day2,sum(COALESCE(day3,0)) as day3,sum(COALESCE(day4,0)) as day4," +
			"sum(COALESCE(day5,0)) as day5,sum(COALESCE(day6,0)) as day6,sum(COALESCE(day7,0)) as day7,sum(COALESCE(day8,0)) as day8,sum(COALESCE(day9,0)) as day9," +
			"sum(COALESCE(day10,0)) as day10,sum(COALESCE(day11,0)) as day11,sum(COALESCE(day12,0)) as day12,sum(COALESCE(day13,0)) as day13,sum(COALESCE(day14,0)) as day14," +
			"sum(COALESCE(day15,0)) as day15,sum(COALESCE(day16,0)) as day16,sum(COALESCE(day17,0)) as day17,sum(COALESCE(day18,0)) as day18,sum(COALESCE(day19,0)) as day19," +
			"sum(COALESCE(day20,0)) as day20,sum(COALESCE(day21,0)) as day21,sum(COALESCE(day22,0)) as day22,sum(COALESCE(day23,0)) as day23,sum(COALESCE(day24,0)) as day24," +
			"sum(COALESCE(day25,0)) as day25,sum(COALESCE(day26,0)) as day26,sum(COALESCE(day27,0)) as day27,sum(COALESCE(day28,0)) as day28,sum(COALESCE(day29,0)) as day29," +
			"sum(COALESCE(day30,0)) as day30,sum(COALESCE(day31,0)) as day31 FROM tasktrack_approval_final ta  inner join project p ON p.project_id = ta.project_project_id where " +
			"month=?1 and year=?2 and user_user_id=?3 and ta.project_type in('Billable','Overtime') group by project_project_id",nativeQuery = true)
	List<Object[]> getFinanceDataByUser( Integer monthIndex, Integer yearIndex, Long userId);

	@Query(value = "SELECT project_project_id as projectid,project_name,user_user_id as userid,first_name,last_name, sum(COALESCE(day1,0)) as day1,sum(COALESCE(day2,0)) as day2,sum(COALESCE(day3,0)) as day3,sum(COALESCE(day4,0)) as day4," +
			"sum(COALESCE(day5,0)) as day5,sum(COALESCE(day6,0)) as day6,sum(COALESCE(day7,0)) as day7,sum(COALESCE(day8,0)) as day8,sum(COALESCE(day9,0)) as day9," +
			"sum(COALESCE(day10,0)) as day10,sum(COALESCE(day11,0)) as day11,sum(COALESCE(day12,0)) as day12,sum(COALESCE(day13,0)) as day13,sum(COALESCE(day14,0)) as day14," +
			"sum(COALESCE(day15,0)) as day15,sum(COALESCE(day16,0)) as day16,sum(COALESCE(day17,0)) as day17,sum(COALESCE(day18,0)) as day18,sum(COALESCE(day19,0)) as day19," +
			"sum(COALESCE(day20,0)) as day20,sum(COALESCE(day21,0)) as day21,sum(COALESCE(day22,0)) as day22,sum(COALESCE(day23,0)) as day23,sum(COALESCE(day24,0)) as day24," +
			"sum(COALESCE(day25,0)) as day25,sum(COALESCE(day26,0)) as day26,sum(COALESCE(day27,0)) as day27,sum(COALESCE(day28,0)) as day28,sum(COALESCE(day29,0)) as day29," +
			"sum(COALESCE(day30,0)) as day30,sum(COALESCE(day31,0)) as day31 FROM tasktrack_approval_final ta  inner join project p ON p.project_id = ta.project_project_id inner join user u ON u.user_id = ta.user_user_id where " +
			"month=?1 and year=?2 and user_user_id=?3 and project_project_id=?4  and ta.project_type in('Billable','Overtime') group by project_project_id",nativeQuery = true)
	List<Object[]> getFinanceDataByUserAndProject( Integer monthIndex, Integer yearIndex, Long userId,Long projectId);

	@Query(value = "SELECT user_user_id as id,first_name,last_name,project.project_name, sum(COALESCE(day1,0)) as day1,sum(COALESCE(day2,0)) as day2,sum(COALESCE(day3,0)) as day3,sum(COALESCE(day4,0)) as day4,"+
			"sum(COALESCE(day5,0)) as day5,sum(COALESCE(day6,0)) as day6,sum(COALESCE(day7,0)) as day7,sum(COALESCE(day8,0)) as day8,sum(COALESCE(day9,0)) as day9," +
			"sum(COALESCE(day10,0)) as day10,sum(COALESCE(day11,0)) as day11,sum(COALESCE(day12,0)) as day12,sum(COALESCE(day13,0)) as day13,sum(COALESCE(day14,0)) as day14," +
			"sum(COALESCE(day15,0)) as day15,sum(COALESCE(day16,0)) as day16,sum(COALESCE(day17,0)) as day17,sum(COALESCE(day18,0)) as day18,sum(COALESCE(day19,0)) as day19," +
			"sum(COALESCE(day20,0)) as day20,sum(COALESCE(day21,0)) as day21,sum(COALESCE(day22,0)) as day22,sum(COALESCE(day23,0)) as day23,sum(COALESCE(day24,0)) as day24," +
			"sum(COALESCE(day25,0)) as day25,sum(COALESCE(day26,0)) as day26,sum(COALESCE(day27,0)) as day27,sum(COALESCE(day28,0)) as day28,sum(COALESCE(day29,0)) as day29," +
			"sum(COALESCE(day30,0)) as day30,sum(COALESCE(day31,0)) as day31 FROM tasktrack_approval_finance ta  inner join user u ON u.user_id = ta.user_user_id " +
			" inner join project ON (project.project_id = ta.project_project_id) where " +
			"month=?1 and year=?2 and ta.project_type in('Billable','Overtime') group by user_user_id,project.project_name",nativeQuery = true)
	List<Object[]> getFinanceDataByMonthYear(int month, int year);

	@Query(value ="SELECT user_user_id as id,first_name,last_name,project.project_name,project.project_id,r.region_name,ta.trx_date,ta.first_halfsubmitted_by_user_id,ta.second_halfsubmitted_by_user_id,first_half_status,second_half_status,sum(COALESCE(day1,0)) as day1,sum(COALESCE(day2,0)) as day2,sum(COALESCE(day3,0)) as day3,sum(COALESCE(day4,0)) as day4,\n" + 
			"			sum(COALESCE(day5,0)) as day5,sum(COALESCE(day6,0)) as day6,sum(COALESCE(day7,0)) as day7,sum(COALESCE(day8,0)) as day8,sum(COALESCE(day9,0)) as day9,\n" + 
			"			sum(COALESCE(day10,0)) as day10,sum(COALESCE(day11,0)) as day11,sum(COALESCE(day12,0)) as day12,sum(COALESCE(day13,0)) as day13,sum(COALESCE(day14,0)) as day14,\n" + 
			"			sum(COALESCE(day15,0)) as day15,sum(COALESCE(day16,0)) as day16,sum(COALESCE(day17,0)) as day17,sum(COALESCE(day18,0)) as day18,sum(COALESCE(day19,0)) as day19,\n" + 
			"			sum(COALESCE(day20,0)) as day20,sum(COALESCE(day21,0)) as day21,sum(COALESCE(day22,0)) as day22,sum(COALESCE(day23,0)) as day23,sum(COALESCE(day24,0)) as day24,\n" + 
			"			sum(COALESCE(day25,0)) as day25,sum(COALESCE(day26,0)) as day26,sum(COALESCE(day27,0)) as day27,sum(COALESCE(day28,0)) as day28,sum(COALESCE(day29,0)) as day29,\n" + 
			"			sum(COALESCE(day30,0)) as day30,sum(COALESCE(day31,0)) as day31 FROM tasktrack_approval_final ta  \n" + 
			"            inner join user u ON u.user_id = ta.user_user_id \n" + 
			"			inner join project ON (project.project_id = ta.project_project_id) \n" + 
			"			INNER JOIN project_region pr ON (pr.project_id_project_id = ta.project_project_id)\n" + 
			 "          LEFT join region r on (r.id = pr.region_id_id) " +
			"			WHERE (CASE WHEN ?3 != 0 \n" + 
			"     		THEN ta.project_project_id = ?3 \n" + 
			"      		ELSE ta.project_project_id != 0 END) and (CASE WHEN ?4 != 0\n" + 
			"      		THEN ta.user_user_id = ?4 \n" + 
			"      		ELSE ta.user_user_id != 0 END) and (CASE WHEN ?5 != 0\n" + 
			"      		THEN pr.region_id_id = ?5 \n" + 
			"      		ELSE pr.region_id_id != 0 END) and ta.year = ?2 and ta.month = ?1 and ta.project_type in('Billable','Overtime')\n" + 
			"            and ((ta.first_half_status = 'SUBMITTED' or ta.first_half_status = 'CORRECTION_SAVED') or ( ta.second_half_status = 'SUBMITTED' or ta.second_half_status = 'CORRECTION_SAVED' ))\n" + 
			"            group by user_user_id,project.project_name,r.region_name,project.project_id,ta.trx_date,ta.first_halfsubmitted_by_user_id,ta.second_halfsubmitted_by_user_id,first_half_status,second_half_status",nativeQuery = true)
	List<Object[]> getProjectWiseSubmissionDetails(int month, int year, long projectId, long userId,long regionId);
	
	@Query(value ="SELECT trx_date FROM `tasktrack_approval_final_aud` WHERE  project_project_id = ?1 and user_user_id = ?2 and ((first_half_status = 'SUBMITTED' or first_half_status = 'CORRECTION_SAVED')) and month = ?3 and year = ?4 " + 
			" order by trx_date " + 
			" limit 1 ",nativeQuery = true)
	Object[] getSubmittedDateFromAudit(Long projectId, Long userId, int month, int year);
	
	@Query(value ="SELECT trx_date FROM `tasktrack_approval_aud` WHERE  project_project_id = ?1 and user_user_id = ?2 and (first_half_status = 'SUBMITTED' and second_half_status = 'SUBMITTED') and month = ?3 and year = ?4 " + 
			" order by trx_date " + 
			" limit 1 ",nativeQuery = true)
	Object[] getSubmittedDateFromAuditSecondHalf(Long projectId, Long userId, int month, int year);



}
