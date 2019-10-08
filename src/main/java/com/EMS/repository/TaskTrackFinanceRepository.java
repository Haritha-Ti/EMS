package com.EMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.TaskTrackApprovalFinance;
import com.EMS.model.TaskTrackApprovalLevel2;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskTrackFinanceRepository extends JpaRepository<TaskTrackApprovalFinance, Long>{


    @Query(value = "SELECT user_user_id as id,first_name,last_name,status, sum(COALESCE(day1,0)) as day1,sum(COALESCE(day2,0)) as day2,sum(COALESCE(day3,0)) as day3,sum(COALESCE(day4,0)) as day4,"+
            "sum(COALESCE(day5,0)) as day5,sum(COALESCE(day6,0)) as day6,sum(COALESCE(day7,0)) as day7,sum(COALESCE(day8,0)) as day8,sum(COALESCE(day9,0)) as day9," +
            "sum(COALESCE(day10,0)) as day10,sum(COALESCE(day11,0)) as day11,sum(COALESCE(day12,0)) as day12,sum(COALESCE(day13,0)) as day13,sum(COALESCE(day14,0)) as day14," +
            "sum(COALESCE(day15,0)) as day15,sum(COALESCE(day16,0)) as day16,sum(COALESCE(day17,0)) as day17,sum(COALESCE(day18,0)) as day18,sum(COALESCE(day19,0)) as day19," +
            "sum(COALESCE(day20,0)) as day20,sum(COALESCE(day21,0)) as day21,sum(COALESCE(day22,0)) as day22,sum(COALESCE(day23,0)) as day23,sum(COALESCE(day24,0)) as day24," +
            "sum(COALESCE(day25,0)) as day25,sum(COALESCE(day26,0)) as day26,sum(COALESCE(day27,0)) as day27,sum(COALESCE(day28,0)) as day28,sum(COALESCE(day29,0)) as day29," +
            "sum(COALESCE(day30,0)) as day30,sum(COALESCE(day31,0)) as day31 FROM tasktrack_approval_finance ta  inner join user u ON u.user_id = ta.user_user_id where " +
            "month=?1 and year=?2 and project_project_id=?3 and project_type in('Billable','Overtime') group by user_user_id,status",nativeQuery = true)
    List<Object[]> getFinanceDataByProject(Integer monthIndex, Integer yearIndex, Long projectId);

    @Query(value = "SELECT project_project_id as id,project_name,status, sum(COALESCE(day1,0)) as day1,sum(COALESCE(day2,0)) as day2,sum(COALESCE(day3,0)) as day3,sum(COALESCE(day4,0)) as day4," +
            "sum(COALESCE(day5,0)) as day5,sum(COALESCE(day6,0)) as day6,sum(COALESCE(day7,0)) as day7,sum(COALESCE(day8,0)) as day8,sum(COALESCE(day9,0)) as day9," +
            "sum(COALESCE(day10,0)) as day10,sum(COALESCE(day11,0)) as day11,sum(COALESCE(day12,0)) as day12,sum(COALESCE(day13,0)) as day13,sum(COALESCE(day14,0)) as day14," +
            "sum(COALESCE(day15,0)) as day15,sum(COALESCE(day16,0)) as day16,sum(COALESCE(day17,0)) as day17,sum(COALESCE(day18,0)) as day18,sum(COALESCE(day19,0)) as day19," +
            "sum(COALESCE(day20,0)) as day20,sum(COALESCE(day21,0)) as day21,sum(COALESCE(day22,0)) as day22,sum(COALESCE(day23,0)) as day23,sum(COALESCE(day24,0)) as day24," +
            "sum(COALESCE(day25,0)) as day25,sum(COALESCE(day26,0)) as day26,sum(COALESCE(day27,0)) as day27,sum(COALESCE(day28,0)) as day28,sum(COALESCE(day29,0)) as day29," +
            "sum(COALESCE(day30,0)) as day30,sum(COALESCE(day31,0)) as day31 FROM tasktrack_approval_finance ta  inner join project p ON p.project_id = ta.project_project_id where " +
            "month=?1 and year=?2 and user_user_id=?3 and ta.project_type in('Billable','Overtime') group by project_project_id,status",nativeQuery = true)
    List<Object[]> getFinanceDataByUser( Integer monthIndex, Integer yearIndex, Long userId);

    @Query(value = "SELECT project_project_id as projectid,project_name,user_user_id as userid,first_name,last_name,status, sum(COALESCE(day1,0)) as day1,sum(COALESCE(day2,0)) as day2,sum(COALESCE(day3,0)) as day3,sum(COALESCE(day4,0)) as day4," +
            "sum(COALESCE(day5,0)) as day5,sum(COALESCE(day6,0)) as day6,sum(COALESCE(day7,0)) as day7,sum(COALESCE(day8,0)) as day8,sum(COALESCE(day9,0)) as day9," +
            "sum(COALESCE(day10,0)) as day10,sum(COALESCE(day11,0)) as day11,sum(COALESCE(day12,0)) as day12,sum(COALESCE(day13,0)) as day13,sum(COALESCE(day14,0)) as day14," +
            "sum(COALESCE(day15,0)) as day15,sum(COALESCE(day16,0)) as day16,sum(COALESCE(day17,0)) as day17,sum(COALESCE(day18,0)) as day18,sum(COALESCE(day19,0)) as day19," +
            "sum(COALESCE(day20,0)) as day20,sum(COALESCE(day21,0)) as day21,sum(COALESCE(day22,0)) as day22,sum(COALESCE(day23,0)) as day23,sum(COALESCE(day24,0)) as day24," +
            "sum(COALESCE(day25,0)) as day25,sum(COALESCE(day26,0)) as day26,sum(COALESCE(day27,0)) as day27,sum(COALESCE(day28,0)) as day28,sum(COALESCE(day29,0)) as day29," +
            "sum(COALESCE(day30,0)) as day30,sum(COALESCE(day31,0)) as day31 FROM tasktrack_approval_finance ta  inner join project p ON p.project_id = ta.project_project_id inner join user u ON u.user_id = ta.user_user_id where " +
            "month=?1 and year=?2 and user_user_id=?3 and project_project_id=?4  and ta.project_type in('Billable','Overtime') group by project_project_id,status",nativeQuery = true)
    List<Object[]> getFinanceDataByUserAndProject( Integer monthIndex, Integer yearIndex, Long userId,Long projectId);

	@Query("SELECT  f FROM TaskTrackApprovalFinance f where f.user.userId = ?1 and f.month = ?2 and f.year =?3 and f.project.projectId = ?4")
	List<TaskTrackApprovalFinance> getDatas(Long userId, int monthIndex, int yearIndex, Long projectId);

    @Query(value = "SELECT count(*) as totalrow FROM tasktrack_approval_finance WHERE month=?1 and  year=?2 and project_project_id=?3",nativeQuery = true)
    Long getCountOfRowsHM(int month,int year,Long projectId);

    @Query(value = "SELECT count(*) as totalrow FROM tasktrack_approval_finance WHERE month=?1 and  year=?2 and project_project_id=?3 and status='FM'",nativeQuery = true)
    Long getCountOfRowsFM(int month,int year,Long projectId);

    @Query(value = "SELECT count(*) as totalrow FROM tasktrack_approval_finance WHERE month=?1 and  year=?2 and project_project_id=?3 and user_user_id=?4",nativeQuery = true)
    Long getCountOfRowsHMByUser(int month,int year,Long projectId,Long userId);

    @Query(value = "SELECT count(*) as totalrow FROM tasktrack_approval_finance WHERE month=?1 and  year=?2 and project_project_id=?3 and user_user_id=?4 and status='FM'",nativeQuery = true)
    Long getCountOfRowsFMByUser(int month,int year,Long projectId,Long userId);


  /*********Report **********/

  @Query(value ="SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+" +
          "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+" +
          "sum(COALESCE(day15,0))+sum(COALESCE(day16,0))+sum(COALESCE(day17,0))+sum(COALESCE(day18,0))+sum(COALESCE(day19,0))+sum(COALESCE(day20,0))+sum(COALESCE(day21,0))+" +
          "sum(COALESCE(day22,0))+sum(COALESCE(day23,0))+sum(COALESCE(day24,0))+sum(COALESCE(day25,0))+sum(COALESCE(day26,0))+sum(COALESCE(day27,0))+sum(COALESCE(day28,0))+" +
          "sum(COALESCE(day29,0))+sum(COALESCE(day30,0))+sum(COALESCE(day31,0))) as totalhour FROM tasktrack_approval_finance where user_user_id=?3 and month=?1 and year=?2 and project_type in('Billable','Overtime','Non-Billable')",nativeQuery = true)
  List<Object[]> getTimeTrackApprovalDataByUserId(Integer monthIndex,Integer yearIndex,Long id);

    @Query(value ="SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+" +
            "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+" +
            "sum(COALESCE(day15,0))) as totalhour FROM tasktrack_approval_finance where user_user_id=?3 and month=?1 and year=?2 and project_type in('Billable','Overtime','Non-Billable')",nativeQuery = true)
    List<Object[]> getTimeTrackApprovalDataByUserIdMidMonth(Integer monthIndex,Integer yearIndex,Long id);

    @Query(value ="SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+" +
            "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+" +
            "sum(COALESCE(day15,0))+sum(COALESCE(day16,0))+sum(COALESCE(day17,0))+sum(COALESCE(day18,0))+sum(COALESCE(day19,0))+sum(COALESCE(day20,0))+sum(COALESCE(day21,0))+" +
            "sum(COALESCE(day22,0))+sum(COALESCE(day23,0))+sum(COALESCE(day24,0))+sum(COALESCE(day25,0))+sum(COALESCE(day26,0))+sum(COALESCE(day27,0))+sum(COALESCE(day28,0))+" +
            "sum(COALESCE(day29,0))+sum(COALESCE(day30,0))+sum(COALESCE(day31,0))) as billablehour FROM tasktrack_approval_finance where user_user_id=?3 and month=?1 and year=?2 and project_type in('Billable')",nativeQuery = true)
    List<Object[]> getBillableDataByUserId(Integer monthIndex,Integer yearIndex,Long id);

    @Query(value ="SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+" +
            "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+" +
            "sum(COALESCE(day15,0))) as billablehour FROM tasktrack_approval_finance where user_user_id=?3 and month=?1 and year=?2 and project_type in('Billable')",nativeQuery = true)
    List<Object[]> getBillableDataByUserIdMidMonth(Integer monthIndex,Integer yearIndex,Long id);

    @Query(value ="SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+" +
            "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+" +
            "sum(COALESCE(day15,0))+sum(COALESCE(day16,0))+sum(COALESCE(day17,0))+sum(COALESCE(day18,0))+sum(COALESCE(day19,0))+sum(COALESCE(day20,0))+sum(COALESCE(day21,0))+" +
            "sum(COALESCE(day22,0))+sum(COALESCE(day23,0))+sum(COALESCE(day24,0))+sum(COALESCE(day25,0))+sum(COALESCE(day26,0))+sum(COALESCE(day27,0))+sum(COALESCE(day28,0))+" +
            "sum(COALESCE(day29,0))+sum(COALESCE(day30,0))+sum(COALESCE(day31,0))) as billablehour FROM tasktrack_approval_finance where user_user_id=?3 and month=?1 and year=?2 and project_type in('Non-Billable')",nativeQuery = true)
    List<Object[]> getNonBillableDataByUserId(Integer monthIndex,Integer yearIndex,Long id);

    @Query(value ="SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+" +
            "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+" +
            "sum(COALESCE(day15,0))) as billablehour FROM tasktrack_approval_finance where user_user_id=?3 and month=?1 and year=?2 and project_type in('Non-Billable')",nativeQuery = true)
    List<Object[]> getNonBillableDataByUserIdMidMonth(Integer monthIndex,Integer yearIndex,Long id);

    @Query(value ="SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+" +
            "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+" +
            "sum(COALESCE(day15,0))+sum(COALESCE(day16,0))+sum(COALESCE(day17,0))+sum(COALESCE(day18,0))+sum(COALESCE(day19,0))+sum(COALESCE(day20,0))+sum(COALESCE(day21,0))+" +
            "sum(COALESCE(day22,0))+sum(COALESCE(day23,0))+sum(COALESCE(day24,0))+sum(COALESCE(day25,0))+sum(COALESCE(day26,0))+sum(COALESCE(day27,0))+sum(COALESCE(day28,0))+" +
            "sum(COALESCE(day29,0))+sum(COALESCE(day30,0))+sum(COALESCE(day31,0))) as billablehour FROM tasktrack_approval_finance where user_user_id=?3 and month=?1 and year=?2 and project_type in('Overtime')",nativeQuery = true)
    List<Object[]> getOvertimeDataByUserId(Integer monthIndex,Integer yearIndex,Long id);

    @Query(value ="SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+" +
            "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+" +
            "sum(COALESCE(day15,0))) as billablehour FROM tasktrack_approval_finance where user_user_id=?3 and month=?1 and year=?2 and project_type in('Overtime')",nativeQuery = true)
    List<Object[]> getOvertimeDataByUserIdMidMonth(Integer monthIndex,Integer yearIndex,Long id);



}
