package com.EMS.repository;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.EMS.model.ApprovalTimeTrackReportModel;
import com.EMS.model.BenchProjectReportModel;
import com.EMS.model.ExportProjectHourReportModel;
import com.EMS.model.ExportProjectTaskReportModel;
import com.EMS.model.FreeAllocationReportModel;
import com.EMS.model.ProjectReportModel;
import com.EMS.utility.ApprovalTimeTrackReportRowMapper;
import com.EMS.utility.BenchReportRowMapper;
import com.EMS.utility.DbConnectionUtility;
import com.EMS.utility.ExportProjectHourReportRowMapper;
import com.EMS.utility.ExportProjectTaskReportRowMapper;
import com.EMS.utility.FreeAllcoationReportRowMapper;
import com.EMS.utility.JsonNodeRowMapper;
import com.EMS.utility.ReportRowMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class ProjectReportsRepository extends DbConnectionUtility {

	/*
	public List<JsonNode> GenerateProjectReports (){
		String sql = "SELECT CONCAT(u.first_name,' ',u.last_name) AS users , u.qualification, a.is_billable, a.allocated_perce, a.start_date, a.end_date, p.project_name FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id WHERE CAST(a.start_date  AS DATE) >= '2019/04/01' AND CAST(a.end_date  AS DATE)  <=  '2019/04/30' ";
		List<JsonNode> list = jdbcTemplate.query(sql, new JsonNodeRowMapper(new ObjectMapper()));
		return list;
	}
	*/
	public List<ProjectReportModel> GenerateProjectReports (long projectId,Date fromDate, Date toDate){
		String sql ="";
		List<ProjectReportModel> list = null;
		if(projectId == 0) 
		{
			//sql = "SELECT CONCAT(u.first_name,' ',u.last_name) AS users ,  a.is_billable, a.allocated_perce, p.project_name FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id WHERE CAST(a.start_date  AS DATE) >= CAST(?  AS DATE) AND CAST(a.end_date  AS DATE)  <=  CAST(?  AS DATE) order by project_name ";
			sql = "SELECT CONCAT(u.last_name,' ',u.first_name) AS users ,  a.is_billable, a.allocated_perce, p.project_name,u.region_id FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id WHERE (date(?) between date(a.start_date) and date(a.end_date) or date(?) between date(a.start_date) and date(a.end_date))  order by project_name";
			list = jdbcTemplate.query(sql, new ReportRowMapper(), new Object[] {fromDate,toDate});
		}
		else 
		{
			//sql = "   SELECT CONCAT(u.first_name,' ',u.last_name) AS users ,  a.is_billable, a.allocated_perce, p.project_name FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id WHERE p.project_id = ? AND CAST(a.start_date  AS DATE) >= CAST(?  AS DATE) AND CAST(a.end_date  AS DATE)  <=  CAST(?  AS DATE) order by project_name";
			
			//Changed by Haritha 21-10-2019//
			//sql = "SELECT CONCAT(u.last_name,' ',u.first_name) AS users ,  a.is_billable, a.allocated_perce, p.project_name FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id WHERE p.project_id = ? AND ((CAST(a.start_date  AS DATE) >= CAST(?  AS DATE) AND CAST(a.start_date  AS DATE)  <=  CAST(?  AS DATE) ) OR (CAST(a.end_date  AS DATE) >= CAST(?  AS DATE) AND CAST(a.end_date  AS DATE)  <=  CAST(?  AS DATE) ) OR (CAST(a.start_date  AS DATE) >= CAST(?  AS DATE) AND CAST(a.end_date  AS DATE)  <=  CAST(?  AS DATE) ) ) order by project_name";
			//list = jdbcTemplate.query(sql, new ReportRowMapper(), new Object[] {projectId,fromDate,toDate,fromDate,toDate,fromDate,toDate});
			//Changed by Haritha 21-10-2019//
			
			sql = "SELECT CONCAT(u.last_name,' ',u.first_name) AS users ,  a.is_billable, a.allocated_perce, p.project_name , u.region_id FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id WHERE p.project_id = ? AND (date(?) between date(a.start_date) and date(a.end_date) or date(?) between date(a.start_date) and date(a.end_date)) order by project_name";
			list = jdbcTemplate.query(sql, new ReportRowMapper(), new Object[] {projectId,fromDate,toDate});
		}
		return list;
	}
	public List<BenchProjectReportModel> GenerateBenchProjectReports (Long uId,Date fromDate, Date toDate){
		String sql ="";
		List<BenchProjectReportModel> list = null;

		//sql = "SELECT CONCAT(u.first_name,' ',u.last_name) AS users ,u.user_id, a.is_billable, a.allocated_perce, p.project_name, d.department_id, d.department_name FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id LEFT JOIN department d ON d.department_id = u.department_department_id WHERE p.project_code = 'BP' AND u.user_id=? AND			 CAST(a.start_date  AS DATE) >= CAST(?  AS DATE) AND CAST(a.end_date  AS DATE)  <=  CAST(?  AS DATE) order by users";
		sql = "SELECT CONCAT(u.last_name,' ',u.first_name) AS users ,u.user_id, a.is_billable, a.allocated_perce, p.project_name, d.department_id, d.department_name ,a.start_date,a.end_date FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id LEFT JOIN department d ON d.department_id = u.department_department_id WHERE p.project_code = 'BP'  AND u.user_id=? AND ((CAST(a.start_date  AS DATE) >= CAST(?  AS DATE) AND CAST(a.start_date  AS DATE)  <=  CAST(?  AS DATE)) OR (CAST(a.end_date  AS DATE) >= CAST(? AS DATE) AND CAST(a.end_date  AS DATE)  <=  CAST(?  AS DATE)) OR (CAST(a.start_date  AS DATE) >= CAST(? AS DATE) AND CAST(a.end_date  AS DATE)  <=  CAST(?  AS DATE) ) ) order by users";
		list = jdbcTemplate.query(sql, new BenchReportRowMapper(), new Object[] {uId,fromDate,toDate,fromDate,toDate,fromDate,toDate});

		return list;
	}
	public List<BenchProjectReportModel> GenerateBenchProjectReports (Date fromDate, Date toDate){
		String sql ="";
		List<BenchProjectReportModel> list = null;

		sql = "SELECT CONCAT(u.last_name,' ',u.first_name) AS users ,u.user_id, a.is_billable, a.allocated_perce, p.project_name, d.department_id, d.department_name ,a.start_date,a.end_date FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id LEFT JOIN department d ON d.department_id = u.department_department_id WHERE p.project_code = 'BP'  AND  ((CAST(a.start_date  AS DATE) >= CAST(?  AS DATE) AND CAST(a.start_date  AS DATE)  <=  CAST(?  AS DATE)) OR (CAST(a.end_date  AS DATE) >= CAST(? AS DATE) AND CAST(a.end_date  AS DATE)  <=  CAST(?  AS DATE)) OR (CAST(a.start_date  AS DATE) >= CAST(? AS DATE) AND CAST(a.end_date  AS DATE)  <=  CAST(?  AS DATE) ) ) order by users";
		list = jdbcTemplate.query(sql, new BenchReportRowMapper(), new Object[] {fromDate,toDate,fromDate,toDate,fromDate,toDate});	
		
		return list;
	}
	public List<ExportProjectTaskReportModel> GenerateProjectTaskReportForExporting (Date fromDate,Date toDate,Long projectId){
		String sql ="";
		List<ExportProjectTaskReportModel> list = null;

		//sql = " SELECT id,CAST(`date`  AS DATE) AS taskDate,CONCAT(u.first_name,' ',u.last_name) AS resourceName,  `description` as taskDescription,hours , p.project_name as projectName,p.project_id as projectId,  (CASE 	WHEN a.is_billable IS NULL THEN '-' 	WHEN a.is_billable = '1' THEN 'Yes' 	ELSE 'No' END) AS billable  from tasktrack t  LEFT JOIN `user` u ON  u.user_id = t. user_user_id  LEFT JOIN allocation a ON a.project_project_id = t.project_project_id and a.user_user_id = t.user_user_id  LEFT JOIN project p on p.project_id = t.project_project_id  where CAST(`date`  AS DATE) BETWEEN ? AND ?  and t.project_project_id = ? order by TaskDate,ResourceName,hours";
		sql = "SELECT DISTINCT CAST(`date`  AS DATE) AS taskDate,CONCAT(u.last_name,'',u.first_name) AS resourceName,tm.task_name as taskCategory,  `description` as taskDescription,hours , p.project_name as projectName,  (CASE 	WHEN a.is_billable IS NULL THEN '-' 	WHEN a.is_billable = '1' THEN 'Yes' 	ELSE 'No' END) AS billable  from tasktrack t  LEFT JOIN `user` u ON  u.user_id = t. user_user_id  LEFT JOIN allocation a ON a.project_project_id = t.project_project_id and a.user_user_id = t.user_user_id  LEFT JOIN project p on p.project_id = t.project_project_id  LEFT JOIN task_master tm on tm.id = t.task_id  where CAST(`date`  AS DATE) >= CAST(?  AS DATE)  AND CAST(`date`  AS DATE)  <=  CAST(?  AS DATE) and t.project_project_id = ? order by TaskDate,ResourceName,hours";
		list = jdbcTemplate.query(sql, new ExportProjectTaskReportRowMapper(), new Object[] {fromDate,toDate,projectId});	

		return list;
	}
	public List<ApprovalTimeTrackReportModel> getApprovalStatusReportDetails (Date startDate,Date endDate,
			int startdateIndex,int enddateIndex,int month,int year){
		String sql ="";
		List<ApprovalTimeTrackReportModel> list = null;
		System.out.println("startdateIndex=="+startdateIndex+"  enddateIndex=="+enddateIndex+"  month=="+month+"  year=="+year);
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("SELECT ems1.projectName,ems1.projectId,COALESCE(SUM(ems1.approved),0) AS 'approved', SUM(ems1.logged) AS 'logged' FROM ( SELECT ems.projectName,ems.projectId,CASE WHEN ems.label = 'approved' THEN hours END AS 'approved', CASE WHEN ems.label = 'logged' THEN hours END AS 'logged' FROM ( SELECT p.project_name AS projectName,SUM(");
		
		if(startdateIndex==1 && enddateIndex>=1) 
			buffer.append("COALESCE(day1,0)+");
		if(startdateIndex<=2 && enddateIndex>=2) 
			buffer.append("COALESCE(day2,0)+");
		if(startdateIndex<=3 && enddateIndex>=3) 
			buffer.append("COALESCE(day3,0)+");	
		if(startdateIndex<=4 && enddateIndex>=4) 
			buffer.append("COALESCE(day4,0)+");	
		if(startdateIndex<=5 && enddateIndex>=5) 
			buffer.append("COALESCE(day5,0)+");	
		if(startdateIndex<=6 && enddateIndex>=6) 
			buffer.append("COALESCE(day6,0)+");
		if(startdateIndex<=7 && enddateIndex>=7) 
			buffer.append("COALESCE(day7,0)+");
		if(startdateIndex<=8 && enddateIndex>=8) 
			buffer.append("COALESCE(day8,0)+");
		if(startdateIndex<=9 && enddateIndex>=9) 
			buffer.append("COALESCE(day9,0)+");
		if(startdateIndex<=10 && enddateIndex>=10) 
			buffer.append("COALESCE(day10,0)+");
		if(startdateIndex<=11 && enddateIndex>=11) 
			buffer.append("COALESCE(day11,0)+");
		if(startdateIndex<=12 && enddateIndex>=12) 
			buffer.append("COALESCE(day12,0)+");
		if(startdateIndex<=13 && enddateIndex>=13) 
			buffer.append("COALESCE(day13,0)+");
		if(startdateIndex<=14 && enddateIndex>=14) 
			buffer.append("COALESCE(day14,0)+");
		if(startdateIndex<=15 && enddateIndex>=15) 
			buffer.append("COALESCE(day15,0)+");
		if(startdateIndex<=16 && enddateIndex>=16) 
			buffer.append("COALESCE(day16,0)+");
		if(startdateIndex<=17 && enddateIndex>=17) 
			buffer.append("COALESCE(day17,0)+");
		if(startdateIndex<=18 && enddateIndex>=18) 
			buffer.append("COALESCE(day18,0)+");
		if(startdateIndex<=19 && enddateIndex>=19) 
			buffer.append("COALESCE(day19,0)+");
		if(startdateIndex<=20 && enddateIndex>=20) 
			buffer.append("COALESCE(day20,0)+");
		if(startdateIndex<=21 && enddateIndex>=21) 
			buffer.append("COALESCE(day21,0)+");
		if(startdateIndex<=22 && enddateIndex>=22) 
			buffer.append("COALESCE(day22,0)+");
		if(startdateIndex<=23 && enddateIndex>=23) 
			buffer.append("COALESCE(day23,0)+");
		if(startdateIndex<=24 && enddateIndex>=24) 
			buffer.append("COALESCE(day24,0)+");
		if(startdateIndex<=25 && enddateIndex>=25) 
			buffer.append("COALESCE(day25,0)+");
		if(startdateIndex<=26 && enddateIndex>=26) 
			buffer.append("COALESCE(day26,0)+");
		if(startdateIndex<=27 && enddateIndex>=27) 
			buffer.append("COALESCE(day27,0)+");
		if(startdateIndex<=28 && enddateIndex>=28) 
			buffer.append("COALESCE(day28,0)+");
		if(startdateIndex<=29 && enddateIndex>=29) 
			buffer.append("COALESCE(day29,0)+");
		if(startdateIndex<=30 && enddateIndex>=30) 
			buffer.append("COALESCE(day30,0)+");
		if(startdateIndex<=31 && enddateIndex==31) 
			buffer.append("COALESCE(day31,0)+");
		
		buffer.append("0) AS hours,");
		
		buffer.append(" 'approved' AS label,p.project_id AS projectId FROM tasktrack_approval ta LEFT JOIN `user` u ON u.user_id = ta.user_user_id LEFT JOIN project p ON p.project_id = ta.project_project_id WHERE ta.month=? AND ta.year=? AND ta.project_type ='Billable' GROUP BY 1,3,4 UNION  SELECT p.project_name AS projectName,SUM(tt.hours) AS hours,'logged' AS label,p.project_id AS projectId FROM tasktrack tt LEFT JOIN `user` u ON u.user_id = tt.user_user_id LEFT JOIN project p ON p.project_id = tt.project_project_id WHERE CAST(tt.`date`  AS DATE) >= CAST(?  AS DATE)  AND CAST(tt.`date`  AS DATE)  <=  CAST(?  AS DATE) GROUP BY 1,3,4 ) ems )ems1 GROUP BY 1,2");

		
		//sql="SELECT ems1.projectName,COALESCE(SUM(ems1.approved),0) AS 'approved', SUM(ems1.logged) AS 'logged' FROM ( SELECT ems.projectName,CASE WHEN ems.label = 'approved' THEN hours END AS 'approved', CASE WHEN ems.label = 'logged' THEN hours END AS 'logged' FROM ( SELECT p.project_name AS projectName,SUM(COALESCE(day1,0)+COALESCE(day2,0)+COALESCE(day3,0)+COALESCE(day4,0)+COALESCE(day5,0)+COALESCE(day6,0)+COALESCE(day7,0)+COALESCE(day8,0)+COALESCE(day9,0)+COALESCE(day10,0) +COALESCE(day11,0)+COALESCE(day12,0)+COALESCE(day13,0)+COALESCE(day14,0)+COALESCE(day15,0)+COALESCE(day16,0)+COALESCE(day17,0)+COALESCE(day18,0)+COALESCE(day19,0)+COALESCE(day20,0) +COALESCE(day21,0)+COALESCE(day22,0)+COALESCE(day23,0)+COALESCE(day24,0)+COALESCE(day25,0)+COALESCE(day26,0)+COALESCE(day27,0)+COALESCE(day28,0)+COALESCE(day29,0)+COALESCE(day30,0) +COALESCE(day31,0)) AS hours, 'approved' AS label FROM tasktrack_approval ta LEFT JOIN `user` u ON u.user_id = ta.user_user_id LEFT JOIN project p ON p.project_id = ta.project_project_id WHERE ta.month=? AND ta.year=? AND ta.project_type ='Billable' GROUP BY 1,3 UNION  SELECT p.project_name AS projectName,SUM(tt.hours) AS hours,'logged' AS label FROM tasktrack tt LEFT JOIN `user` u ON u.user_id = tt.user_user_id LEFT JOIN project p ON p.project_id = tt.project_project_id WHERE CAST(tt.`date`  AS DATE) BETWEEN ? AND ? GROUP BY 1,3 ) ems )ems1 GROUP BY 1";
		sql=buffer.toString();
		list = jdbcTemplate.query(sql, new ApprovalTimeTrackReportRowMapper(), new Object[] {month,year,startDate,endDate});	

		return list;
	}
	public List<ExportProjectHourReportModel> GenerateProjectHourReportForExporting (Date fromDate,Date toDate,int monthIndex, int yearIndex){
		String sql ="";
		List<ExportProjectHourReportModel> list = null;

		//sql = " SELECT id,CAST(`date`  AS DATE) AS taskDate,CONCAT(u.first_name,' ',u.last_name) AS resourceName,  `description` as taskDescription,hours , p.project_name as projectName,p.project_id as projectId,  (CASE 	WHEN a.is_billable IS NULL THEN '-' 	WHEN a.is_billable = '1' THEN 'Yes' 	ELSE 'No' END) AS billable  from tasktrack t  LEFT JOIN `user` u ON  u.user_id = t. user_user_id  LEFT JOIN allocation a ON a.project_project_id = t.project_project_id and a.user_user_id = t.user_user_id  LEFT JOIN project p on p.project_id = t.project_project_id  where CAST(`date`  AS DATE) BETWEEN ? AND ?  and t.project_project_id = ? order by TaskDate,ResourceName,hours";
		sql = "SELECT ems1.projectName, ems1.firstName, ems1.lastName, COALESCE(SUM(ems1.approved),0) AS 'approved', SUM(ems1.logged) AS 'logged' FROM ( SELECT ems.projectName,ems.firstName, ems.lastName, CASE WHEN ems.label = 'approved' THEN hours END AS 'approved', CASE WHEN ems.label = 'logged' THEN hours END AS 'logged' FROM ( SELECT p.project_name AS projectName,u.first_name AS firstName, u.last_name AS lastName, SUM((COALESCE(day1,0)+COALESCE(day2,0)+COALESCE(day3,0)+COALESCE(day4,0)+COALESCE(day5,0)+COALESCE(day6,0)+COALESCE(day7,0)+COALESCE(day8,0)+COALESCE(day9,0)+COALESCE(day10,0) +COALESCE(day11,0)+COALESCE(day12,0)+COALESCE(day13,0)+COALESCE(day14,0)+COALESCE(day15,0)+COALESCE(day16,0)+COALESCE(day17,0)+COALESCE(day18,0)+COALESCE(day19,0)+COALESCE(day20,0) +COALESCE(day21,0)+COALESCE(day22,0)+COALESCE(day23,0)+COALESCE(day24,0)+COALESCE(day25,0)+COALESCE(day26,0)+COALESCE(day27,0)+COALESCE(day28,0)+COALESCE(day29,0)+COALESCE(day30,0) +COALESCE(day31,0))) AS hours, 'approved' AS label FROM tasktrack_approval ta LEFT JOIN `user` u ON u.user_id = ta.user_user_id LEFT JOIN project p ON p.project_id = ta.project_project_id WHERE ta.month=? AND ta.year=? AND ta.project_type ='Billable' GROUP BY 1,2,3,5 UNION  SELECT p.project_name AS projectName,u.first_name AS firstName, u.last_name AS lastName,SUM(tt.hours) AS hours,'logged' AS label FROM tasktrack tt LEFT JOIN `user` u ON u.user_id = tt.user_user_id LEFT JOIN project p ON p.project_id = tt.project_project_id WHERE CAST(tt.`date`  AS DATE) >= CAST(?  AS DATE)  AND CAST(tt.`date`  AS DATE)  <=  CAST(?  AS DATE) GROUP BY 1,2,3,5 ) ems )ems1 GROUP BY 1,2,3";
		list = jdbcTemplate.query(sql, new ExportProjectHourReportRowMapper(), new Object[] {monthIndex,yearIndex,fromDate,toDate});	

		return list;
	}
	
	public Object GenerateProjectReportsByRegion(Long projectId, Date fromDate, Date toDate,Long regionIdSelected) {
		// TODO Auto-generated method stub
		String sql ="";
		List<ProjectReportModel> list = null;
		if(projectId == 0) 
		{
			//sql = "SELECT CONCAT(u.first_name,' ',u.last_name) AS users ,  a.is_billable, a.allocated_perce, p.project_name FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id WHERE CAST(a.start_date  AS DATE) >= CAST(?  AS DATE) AND CAST(a.end_date  AS DATE)  <=  CAST(?  AS DATE) order by project_name ";
			sql = "SELECT CONCAT(u.last_name,' ',u.first_name) AS users ,  a.is_billable, a.allocated_perce, p.project_name,u.region_id FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id WHERE (date(?) between date(a.start_date) and date(a.end_date) or date(?) between date(a.start_date) and date(a.end_date))  AND u.region_id = ? order by project_name";
			list = jdbcTemplate.query(sql, new ReportRowMapper(), new Object[] {fromDate,toDate,regionIdSelected});
		}
		else 
		{
			//sql = "   SELECT CONCAT(u.first_name,' ',u.last_name) AS users ,  a.is_billable, a.allocated_perce, p.project_name FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id WHERE p.project_id = ? AND CAST(a.start_date  AS DATE) >= CAST(?  AS DATE) AND CAST(a.end_date  AS DATE)  <=  CAST(?  AS DATE) order by project_name";
			
			//Changed by Haritha 21-10-2019//
			//sql = "SELECT CONCAT(u.last_name,' ',u.first_name) AS users ,  a.is_billable, a.allocated_perce, p.project_name FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id WHERE p.project_id = ? AND ((CAST(a.start_date  AS DATE) >= CAST(?  AS DATE) AND CAST(a.start_date  AS DATE)  <=  CAST(?  AS DATE) ) OR (CAST(a.end_date  AS DATE) >= CAST(?  AS DATE) AND CAST(a.end_date  AS DATE)  <=  CAST(?  AS DATE) ) OR (CAST(a.start_date  AS DATE) >= CAST(?  AS DATE) AND CAST(a.end_date  AS DATE)  <=  CAST(?  AS DATE) ) ) order by project_name";
			//list = jdbcTemplate.query(sql, new ReportRowMapper(), new Object[] {projectId,fromDate,toDate,fromDate,toDate,fromDate,toDate});
			//Changed by Haritha 21-10-2019//
			
			sql = "SELECT CONCAT(u.last_name,' ',u.first_name) AS users ,  a.is_billable, a.allocated_perce, p.project_name , u.region_id FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id WHERE p.project_id = ? AND (date(?) between date(a.start_date) and date(a.end_date) or date(?) between date(a.start_date) and date(a.end_date))  AND u.region_id = ?  order by project_name";
			list = jdbcTemplate.query(sql, new ReportRowMapper(), new Object[] {projectId,fromDate,toDate,regionIdSelected});
		}
		return list;
	}
	public List<BenchProjectReportModel> GenerateBenchProjectReports(Date fromDate, Date toDate, Long regionId) {
		// TODO Auto-generated method stub
		String sql ="";
		List<BenchProjectReportModel> list = null;

		sql = "SELECT CONCAT(u.last_name,' ',u.first_name) AS users ,u.user_id, a.is_billable, a.allocated_perce, p.project_name, d.department_id, d.department_name ,a.start_date,a.end_date FROM allocation a LEFT JOIN `user` u ON u.user_id = a. user_user_id LEFT JOIN project p ON p.project_id = a.project_project_id LEFT JOIN department d ON d.department_id = u.department_department_id WHERE p.project_code = 'BP'  AND  ((CAST(a.start_date  AS DATE) >= CAST(?  AS DATE) AND CAST(a.start_date  AS DATE)  <=  CAST(?  AS DATE)) OR (CAST(a.end_date  AS DATE) >= CAST(? AS DATE) AND CAST(a.end_date  AS DATE)  <=  CAST(?  AS DATE)) OR (CAST(a.start_date  AS DATE) >= CAST(? AS DATE) AND CAST(a.end_date  AS DATE)  <=  CAST(?  AS DATE) ) ) AND u.region_id = ?  order by users";
		list = jdbcTemplate.query(sql, new BenchReportRowMapper(), new Object[] {fromDate,toDate,fromDate,toDate,fromDate,toDate,regionId});	
		
		return list;
	}
	

	public List<FreeAllocationReportModel> getFreeAllocationReportList(Date fromDate, Date toDate,
			Long regionId_selected) {
		String sql ="";
		List<FreeAllocationReportModel> list = null;
		sql = "select u.user_id as userId, concat(u.last_name,' ', u.first_name) as resourceName, coalesce(p.project_name,'') as projectName, coalesce(alc.start_date,null) as startDate , coalesce(alc.end_date,null) as endDate ,u.joining_date joiningDate, u.termination_date terminationDate from `user` u left join ( 	select start_date,end_date,project_project_id,user_user_id     from allocation a 	where 	? between a.start_date and a.end_date or 	? between a.start_date and a.end_date or 	(? <= a.start_date and ?>= a.end_date) 	)alc on alc.user_user_id = u.user_id left join project p on p.project_id = alc.project_project_id where u.department_department_id not in (5,6,7,11) and u.user_name!='admin' and u.user_name!='TIIndia' and coalesce(u.joining_date,?) <= ?  and coalesce(u.termination_date,?)>= ? and u.region_id=? order by 2,3";
		list = jdbcTemplate.query(sql, new FreeAllcoationReportRowMapper(), new Object[] {fromDate,toDate,fromDate,toDate,toDate,toDate,fromDate,fromDate,regionId_selected});	
		return list;
	}
	
}
