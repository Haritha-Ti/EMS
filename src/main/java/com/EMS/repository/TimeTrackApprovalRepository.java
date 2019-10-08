package com.EMS.repository;

import java.util.Date;
import java.util.List;

import com.EMS.model.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.EMS.utility.ApprovalReportRowMapper;
import com.EMS.utility.ApprovalTimeTrackReportRowMapper;
import com.EMS.utility.BenchReportRowMapper;
import com.EMS.utility.DbConnectionUtility;
import com.EMS.utility.ExportProjectTaskReportRowMapper;
import com.EMS.utility.JsonNodeRowMapper;
import com.EMS.utility.ReportRowMapper;
import com.EMS.utility.TimeTrackApprovalRowMapper;
import com.EMS.utility.TimeTrackApproverLevel2Mapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class TimeTrackApprovalRepository extends DbConnectionUtility {


	public List<TaskTrackApproval> getUserListForApproval (Long id,Long projectId,Integer monthIndex,Integer yearIndex){
	
		List<TaskTrackApproval> list = null;
				
		String sql = "SELECT id,u.first_name as firstName,u.last_name as lastName,TA.project_type as projectType,updated_by as updatedBy,day1,day2,day3,day4,day5,day6,day7,day8,day9,day10,day11,day12,"+
		"day13,day14,day15,day16,day17,day18,day19,day20,day21,day22,day23,day24,day25,day26,day27,day28,day29,day30,day31"+
		" FROM tasktrack_approval TA LEFT JOIN user u ON u.user_id = TA.user_user_id where TA.month =? and TA.year=? and TA.project_project_id = ? and TA.user_user_id=?";
	
		list = jdbcTemplate.query(sql, new TimeTrackApprovalRowMapper(), new Object[] {monthIndex,yearIndex,projectId,id});	
		
		return list;
	}
	
	public List<ExportApprovalReportModel> getApprovalReportData (Integer monthIndex,Integer yearIndex){
		
		List<ExportApprovalReportModel> list = null;
				
		String sql = "SELECT u.user_id as id,u.last_name as LastName,u.first_name as FirstName,p.project_name AS ProjectName,sum(COALESCE(day1,0)) as day1,sum(COALESCE(day2,0)) as day2,sum(COALESCE(day3,0)) as day3,sum(COALESCE(day4,0)) as day4,sum(COALESCE(day5,0)) as day5,sum(COALESCE(day6,0)) as day6,sum(COALESCE(day7,0)) as day7,sum(COALESCE(day8,0)) as day8,sum(COALESCE(day9,0)) as day9,sum(COALESCE(day10,0)) as day10,sum(COALESCE(day11,0)) as day11,sum(COALESCE(day12,0)) as day12,sum(COALESCE(day13,0)) as day13,sum(COALESCE(day14,0)) as day14,sum(COALESCE(day15,0)) as day15,sum(COALESCE(day16,0)) as day16,sum(COALESCE(day17,0)) as day17,sum(COALESCE(day18,0)) as day18,sum(COALESCE(day19,0)) as day19,sum(COALESCE(day20,0)) as day20,sum(COALESCE(day21,0)) as day21,sum(COALESCE(day22,0)) as day22 ,sum(COALESCE(day23,0)) as day23,sum(COALESCE(day24,0)) as day24,sum(COALESCE(day25,0)) as day25,sum(COALESCE(day26,0)) as day26,sum(COALESCE(day27,0)) as day27,sum(COALESCE(day28,0)) as day28,sum(COALESCE(day29,0)) as day29,sum(COALESCE(day30,0)) as day30,sum(COALESCE(day31,0)) as day31 " + 
				" FROM tasktrack_approval ta LEFT JOIN `user` u ON u.user_id = ta.user_user_id LEFT JOIN project p ON p.project_id = ta.project_project_id WHERE  p.is_billable=1 and ta.month=? AND ta.year=?  AND ta.project_type in('Billable','Overtime') group by 1,2,3,4 order by 4";
		list = jdbcTemplate.query(sql, new ApprovalReportRowMapper(), new Object[] {monthIndex,yearIndex});	
		
		return list;
	}
	public List<ExportApprovalReportModel> getNonApprovalReportData (Integer monthIndex,Integer yearIndex){

		List<ExportApprovalReportModel> list = null;

		String sql = "SELECT u.user_id as id,u.last_name as LastName,u.first_name as FirstName,p.project_name AS ProjectName,sum(COALESCE(day1,0)) as day1,sum(COALESCE(day2,0)) as day2,sum(COALESCE(day3,0)) as day3,sum(COALESCE(day4,0)) as day4,sum(COALESCE(day5,0)) as day5,sum(COALESCE(day6,0)) as day6,sum(COALESCE(day7,0)) as day7,sum(COALESCE(day8,0)) as day8,sum(COALESCE(day9,0)) as day9,sum(COALESCE(day10,0)) as day10,sum(COALESCE(day11,0)) as day11,sum(COALESCE(day12,0)) as day12,sum(COALESCE(day13,0)) as day13,sum(COALESCE(day14,0)) as day14,sum(COALESCE(day15,0)) as day15,sum(COALESCE(day16,0)) as day16,sum(COALESCE(day17,0)) as day17,sum(COALESCE(day18,0)) as day18,sum(COALESCE(day19,0)) as day19,sum(COALESCE(day20,0)) as day20,sum(COALESCE(day21,0)) as day21,sum(COALESCE(day22,0)) as day22 ,sum(COALESCE(day23,0)) as day23,sum(COALESCE(day24,0)) as day24,sum(COALESCE(day25,0)) as day25,sum(COALESCE(day26,0)) as day26,sum(COALESCE(day27,0)) as day27,sum(COALESCE(day28,0)) as day28,sum(COALESCE(day29,0)) as day29,sum(COALESCE(day30,0)) as day30,sum(COALESCE(day31,0)) as day31 " +
				" FROM tasktrack_approval ta LEFT JOIN `user` u ON u.user_id = ta.user_user_id LEFT JOIN project p ON p.project_id = ta.project_project_id WHERE  p.is_billable=1 and ta.month=? AND ta.year=?  AND ta.project_type in('Non-Billable') group by 1,2,3,4 order by 4";
		list = jdbcTemplate.query(sql, new ApprovalReportRowMapper(), new Object[] {monthIndex,yearIndex});

		return list;
	}


	public List<ExportApprovalReportModel> getApprovalReportDataFinance (Integer monthIndex,Integer yearIndex){

		List<ExportApprovalReportModel> list = null;

		String sql = "SELECT u.user_id as id,u.last_name as LastName,u.first_name as FirstName,p.project_name AS ProjectName,sum(COALESCE(day1,0)) as day1,sum(COALESCE(day2,0)) as day2,sum(COALESCE(day3,0)) as day3,sum(COALESCE(day4,0)) as day4,sum(COALESCE(day5,0)) as day5,sum(COALESCE(day6,0)) as day6,sum(COALESCE(day7,0)) as day7,sum(COALESCE(day8,0)) as day8,sum(COALESCE(day9,0)) as day9,sum(COALESCE(day10,0)) as day10,sum(COALESCE(day11,0)) as day11,sum(COALESCE(day12,0)) as day12,sum(COALESCE(day13,0)) as day13,sum(COALESCE(day14,0)) as day14,sum(COALESCE(day15,0)) as day15,sum(COALESCE(day16,0)) as day16,sum(COALESCE(day17,0)) as day17,sum(COALESCE(day18,0)) as day18,sum(COALESCE(day19,0)) as day19,sum(COALESCE(day20,0)) as day20,sum(COALESCE(day21,0)) as day21,sum(COALESCE(day22,0)) as day22 ,sum(COALESCE(day23,0)) as day23,sum(COALESCE(day24,0)) as day24,sum(COALESCE(day25,0)) as day25,sum(COALESCE(day26,0)) as day26,sum(COALESCE(day27,0)) as day27,sum(COALESCE(day28,0)) as day28,sum(COALESCE(day29,0)) as day29,sum(COALESCE(day30,0)) as day30,sum(COALESCE(day31,0)) as day31 " +
				" FROM tasktrack_approval_finance ta LEFT JOIN `user` u ON u.user_id = ta.user_user_id LEFT JOIN project p ON p.project_id = ta.project_project_id WHERE  p.is_billable=1 and ta.month=? AND ta.year=?  AND ta.project_type in('Billable','Overtime') group by 1,2,3,4 order by 4";
		list = jdbcTemplate.query(sql, new ApprovalReportRowMapper(), new Object[] {monthIndex,yearIndex});

		return list;
	}
	public List<ExportApprovalReportModel> getNonApprovalReportDataFinance (Integer monthIndex,Integer yearIndex){

		List<ExportApprovalReportModel> list = null;

		String sql = "SELECT u.user_id as id,u.last_name as LastName,u.first_name as FirstName,p.project_name AS ProjectName,sum(COALESCE(day1,0)) as day1,sum(COALESCE(day2,0)) as day2,sum(COALESCE(day3,0)) as day3,sum(COALESCE(day4,0)) as day4,sum(COALESCE(day5,0)) as day5,sum(COALESCE(day6,0)) as day6,sum(COALESCE(day7,0)) as day7,sum(COALESCE(day8,0)) as day8,sum(COALESCE(day9,0)) as day9,sum(COALESCE(day10,0)) as day10,sum(COALESCE(day11,0)) as day11,sum(COALESCE(day12,0)) as day12,sum(COALESCE(day13,0)) as day13,sum(COALESCE(day14,0)) as day14,sum(COALESCE(day15,0)) as day15,sum(COALESCE(day16,0)) as day16,sum(COALESCE(day17,0)) as day17,sum(COALESCE(day18,0)) as day18,sum(COALESCE(day19,0)) as day19,sum(COALESCE(day20,0)) as day20,sum(COALESCE(day21,0)) as day21,sum(COALESCE(day22,0)) as day22 ,sum(COALESCE(day23,0)) as day23,sum(COALESCE(day24,0)) as day24,sum(COALESCE(day25,0)) as day25,sum(COALESCE(day26,0)) as day26,sum(COALESCE(day27,0)) as day27,sum(COALESCE(day28,0)) as day28,sum(COALESCE(day29,0)) as day29,sum(COALESCE(day30,0)) as day30,sum(COALESCE(day31,0)) as day31 " +
				" FROM tasktrack_approval_finance ta LEFT JOIN `user` u ON u.user_id = ta.user_user_id LEFT JOIN project p ON p.project_id = ta.project_project_id WHERE  p.is_billable=1 and ta.month=? AND ta.year=?  AND ta.project_type in('Non-Billable') group by 1,2,3,4 order by 4";
		list = jdbcTemplate.query(sql, new ApprovalReportRowMapper(), new Object[] {monthIndex,yearIndex});

		return list;
	}

	/*
	 * public List<TaskTrackApprovalLevel2> getUserListForApprovalLevel2(Long id,
	 * Long projectId, Integer monthIndex, Integer yearIndex) {
	 * List<TaskTrackApprovalLevel2> list = null;
	 * 
	 * String sql =
	 * "SELECT id,u.first_name as firstName,u.last_name as lastName,TA.project_type as projectType,updated_by as updatedBy,day1,day2,day3,day4,day5,day6,day7,day8,day9,day10,day11,day12,"
	 * +
	 * "day13,day14,day15,day16,day17,day18,day19,day20,day21,day22,day23,day24,day25,day26,day27,day28,day29,day30,day31"+
	 * " FROM tasktrack_approval_level2 TA LEFT JOIN user u ON u.user_id = TA.user_user_id where TA.month =? and TA.year=? and TA.project_project_id = ? and TA.user_user_id=?"
	 * ;
	 * 
	 * list = jdbcTemplate.query(sql, new TimeTrackApproverLevel2Mapper(), new
	 * Object[] {monthIndex,yearIndex,projectId,id});
	 * 
	 * return list; }
	 */
}
