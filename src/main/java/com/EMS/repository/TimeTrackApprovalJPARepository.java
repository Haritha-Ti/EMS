package com.EMS.repository;

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

   /* @Query(value ="SELECT user.user_id as id," +
            "sum(COALESCE(day1,0)) as day1,sum(COALESCE(day2,0)) as day2,sum(COALESCE(day3,0)) as day3,sum(COALESCE(day4,0)) as day4,sum(COALESCE(day5,0)) as day5," +
            "sum(COALESCE(day6,0)) as day6,sum(COALESCE(day7,0)) as day7,sum(COALESCE(day8,0)) as day8,sum(COALESCE(day9,0)) as day9,sum(COALESCE(day10,0)) as day10," +
            "sum(COALESCE(day11,0)) as day11,sum(COALESCE(day12,0)) as day12,sum(COALESCE(day13,0)) as day13,sum(COALESCE(day14,0)) as day14,sum(COALESCE(day15,0)) as day15," +
            "sum(COALESCE(day16,0)) as day16,sum(COALESCE(day17,0)) as day17,sum(COALESCE(day18,0)) as day18,sum(COALESCE(day19,0)) as day19,sum(COALESCE(day20,0)) as day20," +
            "sum(COALESCE(day21,0)) as day21,sum(COALESCE(day22,0)) as day22 ,sum(COALESCE(day23,0)) as day23,sum(COALESCE(day24,0)) as day24,sum(COALESCE(day25,0)) as day25," +
            "sum(COALESCE(day26,0)) as day26,sum(COALESCE(day27,0)) as day27,sum(COALESCE(day28,0)) as day28,sum(COALESCE(day29,0)) as day29,sum(COALESCE(day30,0)) as day30," +
            "sum(COALESCE(day31,0)) as day31 FROM tasktrack_approval where user_user_id=?3 and month=?1 and year=?2 and project_type in('Billable','Overtime','Non-Billable')",nativeQuery = true)*/
    @Query(value ="SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+" +
            "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+" +
            "sum(COALESCE(day15,0))+sum(COALESCE(day16,0))+sum(COALESCE(day17,0))+sum(COALESCE(day18,0))+sum(COALESCE(day19,0))+sum(COALESCE(day20,0))+sum(COALESCE(day21,0))+" +
            "sum(COALESCE(day22,0))+sum(COALESCE(day23,0))+sum(COALESCE(day24,0))+sum(COALESCE(day25,0))+sum(COALESCE(day26,0))+sum(COALESCE(day27,0))+sum(COALESCE(day28,0))+" +
            "sum(COALESCE(day29,0))+sum(COALESCE(day30,0))+sum(COALESCE(day31,0))) as totalhour FROM tasktrack_approval where user_user_id=?3 and month=?1 and year=?2 and project_type in('Billable','Overtime','Non-Billable')",nativeQuery = true)
    List<Object[]> getTimeTrackApprovalDataByUserId(Integer monthIndex,Integer yearIndex,Long id);

    @Query(value ="SELECT user_user_id as id,(sum(COALESCE(day1,0))+sum(COALESCE(day2,0))+sum(COALESCE(day3,0))+sum(COALESCE(day4,0))+sum(COALESCE(day5,0))+sum(COALESCE(day6,0))+sum(COALESCE(day7,0))+" +
            "sum(COALESCE(day8,0))+sum(COALESCE(day9,0))+sum(COALESCE(day10,0))+sum(COALESCE(day11,0))+sum(COALESCE(day12,0))+sum(COALESCE(day13,0))+sum(COALESCE(day14,0))+" +
            "sum(COALESCE(day15,0))) as totalhour FROM tasktrack_approval where user_user_id=?3 and month=?1 and year=?2 and project_type in('Billable','Overtime','Non-Billable')",nativeQuery = true)
    List<Object[]> getTimeTrackApprovalDataByUserIdMidMonth(Integer monthIndex,Integer yearIndex,Long id);

}
