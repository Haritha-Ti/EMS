package com.EMS.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.TaskTrackApproval;
import com.EMS.model.TaskTrackApprovalFinal;
import com.EMS.repository.AuditRepository;

@Service
public class AuditServiceImpl implements AuditService {
@Autowired
AuditRepository auditRepository;
@Autowired
ProjectAllocationService projectAllocationService;
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAuditByUserId(Long projectId,Long userId, Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		JSONObject json=new JSONObject();
		List<JSONObject> taskTrackApproval=new ArrayList<JSONObject>();
		List<JSONObject> rowsNode=new ArrayList<JSONObject>();
		taskTrackApproval=auditRepository.getAuditDataByUserId(projectId,userId,fromDate,toDate);
		if (taskTrackApproval.isEmpty() ||taskTrackApproval == null)
			return null;
		JSONArray columns =new JSONArray();		
		columns.add("User Name");
		columns.add("Project Name");
		columns.add("Transaction Type");
		columns.add("Transaction Date");
		columns.add("Users In Action");
		columns.add("Approved Date");
		columns.add("Day1");
		columns.add("Day2");
		columns.add("Day3");
		columns.add("Day4");
		columns.add("Day5");
		columns.add("Day6");
		columns.add("Day7");
		columns.add("Day8");
		columns.add("Day9");
		columns.add("Day10");
		columns.add("Day11");
		columns.add("Day12");
		columns.add("Day13");
		columns.add("Day14");
		columns.add("Day15");
		columns.add("Day16");
		columns.add("Day17");
		columns.add("Day18");
		columns.add("Day19");
		columns.add("Day20");	
		columns.add("Day21");
		columns.add("Day22");
		columns.add("Day23");
		columns.add("Day23");
		columns.add("Day25");
		columns.add("Day26");
		columns.add("Day27");
		columns.add("Day28");
		columns.add("Day29");
		columns.add("Day30");
		columns.add("Day31");
		columns.add("First Half Status");
		columns.add("Month");
		columns.add("Project Type");
		columns.add("Second Half Status");
		columns.add("Year");
		
		for (JSONObject data : taskTrackApproval) {
			JSONObject dataNode = new JSONObject();
			dataNode.put("User Name", data.get("user_name"));
			dataNode.put("Project Name", data.get("project_name"));
			dataNode.put("Transaction Type", data.get("revtype"));
			dataNode.put("Transaction Date", data.get("trx_date"));
			dataNode.put("User In Action", data.get("user_in_action"));
			dataNode.put("Approved Date", data.get("DATE_FORMAT(t.approved_date, '%d/%m/%Y')"));
			dataNode.put("Day1", data.get("day1"));
			dataNode.put("Day2", data.get("day2"));
			dataNode.put("Day3", data.get("day3"));
			dataNode.put("Day4", data.get("day4"));
			dataNode.put("Day5", data.get("day5"));
			dataNode.put("Day6", data.get("day6"));
			dataNode.put("Day7", data.get("day7"));
			dataNode.put("Day8", data.get("day8"));
			dataNode.put("Day9", data.get("day9"));
			dataNode.put("Day10", data.get("day10"));
			dataNode.put("Day11", data.get("day11"));
			dataNode.put("Day12", data.get("day12"));
			dataNode.put("Day13", data.get("day13"));
			dataNode.put("Day14", data.get("day14"));
			dataNode.put("Day15", data.get("day15"));
			dataNode.put("Day16", data.get("day16"));
			dataNode.put("Day17", data.get("day17"));
			dataNode.put("Day18", data.get("day18"));
			dataNode.put("Day19", data.get("day19"));
			dataNode.put("Day20", data.get("day20"));
			dataNode.put("Day21", data.get("day21"));
			dataNode.put("Day22", data.get("day22"));
			dataNode.put("Day23", data.get("day23"));
			dataNode.put("Day24", data.get("day24"));
			dataNode.put("Day25", data.get("day25"));
			dataNode.put("Day26", data.get("day26"));
			dataNode.put("Day27", data.get("day27"));
			dataNode.put("Day28", data.get("day28"));
			dataNode.put("Day29", data.get("day29"));
			dataNode.put("Day30", data.get("day30"));
			dataNode.put("Day31", data.get("day31"));
			dataNode.put("First Half Status", data.get("first_half_status"));
			dataNode.put("Month", data.get("month"));
			dataNode.put("Project Type", data.get("project_type"));
			dataNode.put("Second Half Status", data.get("second_half_status"));
			dataNode.put("Year", data.get("year"));
			rowsNode.add(dataNode);
	}
		json.put("Columns",columns);
		json.put("Rows",rowsNode);
		return json;
		}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAuditByUserIdForFinal(Long projectId,Long userId, Date fromDate, Date toDate) {
		JSONObject json=new JSONObject();
		List<JSONObject> taskTrackApprovalFinal=new ArrayList<JSONObject>();
		List<JSONObject> rowsNode=new ArrayList<JSONObject>();
		taskTrackApprovalFinal=auditRepository.getAuditDataByUserIdForFinal(projectId,userId,fromDate,toDate);
		if (taskTrackApprovalFinal.isEmpty() || taskTrackApprovalFinal == null)
			return null;
		JSONArray columns =new JSONArray();		
		columns.add("User Name");
		columns.add("Project Name");
		columns.add("Transaction Type");
		columns.add("Users In Action");
		columns.add("Transaction Date");
		columns.add("Approved Date");
		columns.add("Day1");
		columns.add("Day2");
		columns.add("Day3");
		columns.add("Day4");
		columns.add("Day5");
		columns.add("Day6");
		columns.add("Day7");
		columns.add("Day8");
		columns.add("Day9");
		columns.add("Day10");
		columns.add("Day11");
		columns.add("Day12");
		columns.add("Day13");
		columns.add("Day14");
		columns.add("Day15");
		columns.add("Day16");
		columns.add("Day17");
		columns.add("Day18");
		columns.add("Day19");
		columns.add("Day20");	
		columns.add("Day21");
		columns.add("Day22");
		columns.add("Day23");
		columns.add("Day23");
		columns.add("Day25");
		columns.add("Day26");
		columns.add("Day27");
		columns.add("Day28");
		columns.add("Day29");
		columns.add("Day30");
		columns.add("Day31");
		columns.add("First Half Status");
		columns.add("Month");
		columns.add("Project Type");
		columns.add("Second Half Status");
		columns.add("Year");
		
		for (JSONObject data : taskTrackApprovalFinal) {
			JSONObject dataNode = new JSONObject();
			dataNode.put("User Name", data.get("user_name"));
			dataNode.put("Project Name", data.get("project_name"));
			dataNode.put("Transaction Type", data.get("revtype"));
			dataNode.put("Transaction Date", data.get("trx_date"));
			dataNode.put("User In Action", data.get("user_in_action"));
			dataNode.put("Approved Date", data.get("DATE_FORMAT(t.approved_date, '%d/%m/%Y')"));
			dataNode.put("Day1", data.get("day1"));
			dataNode.put("Day2", data.get("day2"));
			dataNode.put("Day3", data.get("day3"));
			dataNode.put("Day4", data.get("day4"));
			dataNode.put("Day5", data.get("day5"));
			dataNode.put("Day6", data.get("day6"));
			dataNode.put("Day7", data.get("day7"));
			dataNode.put("Day8", data.get("day8"));
			dataNode.put("Day9", data.get("day9"));
			dataNode.put("Day10", data.get("day10"));
			dataNode.put("Day11", data.get("day11"));
			dataNode.put("Day12", data.get("day12"));
			dataNode.put("Day13", data.get("day13"));
			dataNode.put("Day14", data.get("day14"));
			dataNode.put("Day15", data.get("day15"));
			dataNode.put("Day16", data.get("day16"));
			dataNode.put("Day17", data.get("day17"));
			dataNode.put("Day18", data.get("day18"));
			dataNode.put("Day19", data.get("day19"));
			dataNode.put("Day20", data.get("day20"));
			dataNode.put("Day21", data.get("day21"));
			dataNode.put("Day22", data.get("day22"));
			dataNode.put("Day23", data.get("day23"));
			dataNode.put("Day24", data.get("day24"));
			dataNode.put("Day25", data.get("day25"));
			dataNode.put("Day26", data.get("day26"));
			dataNode.put("Day27", data.get("day27"));
			dataNode.put("Day28", data.get("day28"));
			dataNode.put("Day29", data.get("day29"));
			dataNode.put("Day30", data.get("day30"));
			dataNode.put("Day31", data.get("day31"));
			dataNode.put("First Half Status", data.get("first_half_status"));
			dataNode.put("Month", data.get("month"));
			dataNode.put("Project Type", data.get("project_type"));
			dataNode.put("Second Half Status", data.get("second_half_status"));
			dataNode.put("Year", data.get("year"));
			rowsNode.add(dataNode);
	}
		json.put("Columns",columns);
		json.put("Rows",rowsNode);
		return json;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAuditUserDetailsById(Long userId) {
		List<JSONObject> userDetails=auditRepository.getAuditUserDetailsById(userId);
		if (userDetails.isEmpty() || userDetails == null)
			return null;
		
		JSONObject json=new JSONObject();
		List<JSONObject> rowsNode=new ArrayList<JSONObject>();
		JSONArray columns =new JSONArray();		
		columns.add("Transaction Type");
		columns.add("Transcaction Date");
		columns.add("User In Action");
		columns.add("Recruiter");
		columns.add("Active");
		columns.add("Blood Group");
		columns.add("Contact");
		columns.add("Level Name");
		columns.add("DateOfBirth");
		columns.add("Email");
		columns.add("Employee Categroy");
		columns.add("Employee Id");
		columns.add("Employment Type");
		columns.add("FirstName");
		columns.add("LastName");
		columns.add("Qualification");
		columns.add("Referred By");
		columns.add("Termination Date");
		columns.add("User Name");
		columns.add("Contractor Name");
		columns.add("CppLevels Id");
		columns.add("Department Name");
		columns.add("Region Name");
		columns.add("Role Name");
		columns.add("Timezone Name");	
		for (JSONObject data : userDetails) {
			JSONObject dataNode = new JSONObject();
			dataNode.put("Transaction Type", data.get("revtype"));
			dataNode.put("Transcaction Date", data.get("trx_date"));
			dataNode.put("User In Action", data.get("user_in_action"));
			dataNode.put("Recruiter", data.get("recruiter"));
			dataNode.put("Active", data.get("active"));
			dataNode.put("Blood Group", data.get("blood_group"));
			dataNode.put("Contact", data.get("contact"));
			dataNode.put("Level Name", data.get("level_name"));
			dataNode.put("DateOfBirth", data.get("dob"));
			dataNode.put("Email", data.get("email"));
			dataNode.put("Employee Categroy", data.get("emp_category"));
			dataNode.put("Employee Id", data.get("emp_id"));
			dataNode.put("Employment Type", data.get("employment_type"));
			dataNode.put("First Name", data.get("first_name"));
			dataNode.put("Last Name", data.get("last_name"));
			dataNode.put("Qualification", data.get("qualification"));
			dataNode.put("Referred By", data.get("referred_by"));
			dataNode.put("Termination Date", data.get("termination_date"));
			dataNode.put("User Name", data.get("user_name"));
			dataNode.put("Contractor Name", data.get("contractor_name"));
			dataNode.put("CppLevels Id", data.get("cpplevels_id"));
			dataNode.put("Department Name", data.get("department_name"));
			dataNode.put("Region Name", data.get("region_name"));
			dataNode.put("Role Name", data.get("role_name"));
			dataNode.put("Timezone Name", data.get("timezone_name"));
			rowsNode.add(dataNode);
	}
		json.put("Columns",columns);
		json.put("Rows",rowsNode);
	
		return json;
	
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAuditUserDetailsByDateRange(Long userId, Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		List<JSONObject> userDetails= auditRepository.getAuditUserDetailsByDateRange(userId,fromDate,toDate);
		if (userDetails.isEmpty() || userDetails == null)
			return null;	
		JSONObject json=new JSONObject();
		List<JSONObject> rowsNode=new ArrayList<JSONObject>();
		JSONArray columns =new JSONArray();	
		columns.add("Transaction Type");
		columns.add("Transcaction Date");
		columns.add("User In Action");
		columns.add("Recruiter");
		columns.add("Active");
		columns.add("Blood Group");
		columns.add("Contact");
		columns.add("Level Name");
		columns.add("DateOfBirth");
		columns.add("Email");
		columns.add("Employee Categroy");
		columns.add("Employee Id");
		columns.add("Employment Type");
		columns.add("FirstName");
		columns.add("LastName");
		columns.add("Qualification");
		columns.add("Referred By");
		columns.add("Termination Date");
		columns.add("User Name");
		columns.add("Contractor Name");
		columns.add("CppLevels Id");
		columns.add("Department Name");
		columns.add("Region Name");
		columns.add("Role Name");
		columns.add("Timezone Name");
		for (JSONObject data : userDetails) {
			JSONObject dataNode = new JSONObject();
			dataNode.put("Transaction Type", data.get("revtype"));
			dataNode.put("Transcaction Date", data.get("trx_date"));
			dataNode.put("User In Action", data.get("user_in_action"));
			dataNode.put("Recruiter", data.get("recruiter"));
			dataNode.put("Active", data.get("active"));
			dataNode.put("Blood Group", data.get("blood_group"));
			dataNode.put("Contact", data.get("contact"));
			dataNode.put("Level Name", data.get("level_name"));
			dataNode.put("DateOfBirth", data.get("dob"));
			dataNode.put("Email", data.get("email"));
			dataNode.put("Employee Categroy", data.get("emp_category"));
			dataNode.put("Employee Id", data.get("emp_id"));
			dataNode.put("Employment Type", data.get("employment_type"));
			dataNode.put("First Name", data.get("first_name"));
			dataNode.put("Last Name", data.get("last_name"));
			dataNode.put("Qualification", data.get("qualification"));
			dataNode.put("Referred By", data.get("referred_by"));
			dataNode.put("Termination Date", data.get("termination_date"));
			dataNode.put("User Name", data.get("user_name"));
			dataNode.put("Contractor Name", data.get("contractor_name"));
			dataNode.put("CppLevels Id", data.get("cpplevels_id"));
			dataNode.put("Department Name", data.get("department_name"));
			dataNode.put("Region Name", data.get("region_name"));
			dataNode.put("Role Name", data.get("role_name"));
			dataNode.put("Timezone Name", data.get("timezone_name"));
			rowsNode.add(dataNode);
	}
		json.put("Columns",columns);
		json.put("Rows",rowsNode);
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getProjectAuditDataByProjectId(Long projectId) {

		List<JSONObject> projectAuditData = auditRepository.getProjectAuditDataByProjectId(projectId);
		if (projectAuditData.isEmpty() || projectAuditData == null)
			return null;
		JSONObject node = new JSONObject();
		List<JSONObject> rowsNode = new ArrayList<JSONObject>();
		JSONArray     columns =new JSONArray();
		
		columns.add("Transaction Type");
		columns.add("User In Action");
		columns.add("Transaction Type");
		columns.add("Project");
		columns.add("Project Category");
		columns.add("Project Code");
		columns.add("Project Details");
		columns.add("Project Status");
		columns.add("Project Tier");
		columns.add("Project Type");
		columns.add("Project Refence ID");
		columns.add("Start Date");
		columns.add("End Date");
		columns.add("Releasing Date");
		columns.add("Parent Project");
		columns.add("Contract Type Name");
		columns.add("Project Owner");
		columns.add("Client");
		columns.add("Onsite Lead");
		columns.add("Client Point Of Contact");
		columns.add("Estimated Hours");
		columns.add("Billable");
		columns.add("Ispoc");
		
		
		for (JSONObject data : projectAuditData) {

			JSONObject dataNode = new JSONObject();
			dataNode.put("Transaction Type", data.get("revtype"));
			dataNode.put("User In Action", data.get("user_in_action"));
			dataNode.put("Transaction Date", data.get("trx_date"));
			dataNode.put("Project", data.get("project_name"));
			dataNode.put("Project Category", data.get("project_category"));
			dataNode.put("Project Code", data.get("project_code"));
			dataNode.put("Project Details", data.get("project_details"));
			dataNode.put("Project Status", data.get("project_status"));
			dataNode.put("Project Tier", data.get("project_tier"));
			dataNode.put("Project Type", data.get("project_type"));
			dataNode.put("Project Refence ID", data.get("project_ref_id"));
			dataNode.put("Start Date", data.get("start_date"));
			dataNode.put("End Date", data.get("end_date"));
			dataNode.put("Releasing Date", data.get("releasing_date"));
			dataNode.put("Parent Project", data.get("parent_project"));
			dataNode.put("Contract Type Name", data.get("contract_type_name"));
			dataNode.put("Project Owner", data.get("project_owner"));
			dataNode.put("Client", data.get("client_name"));
			dataNode.put("Onsite Lead", data.get("onsite_lead"));
			dataNode.put("Client Point Of Contact", data.get("client_point_of_contact"));
			dataNode.put("Estimated Hours", data.get("estimated_hours"));
			dataNode.put("Billable", data.get("is_billable"));
			dataNode.put("Ispoc", data.get("ispoc"));
			rowsNode.add(dataNode);

		}

		node.put("Columns", columns);
		node.put("Rows", rowsNode);
		return node;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getProjectAuditDataByProjectIdAndDateRange(Long projectId, Date startDate, Date endDate) {

		List<JSONObject> projectAuditData = auditRepository.getProjectAuditDataByProjectIdAndDateRange(projectId,
				startDate, endDate);
		if (projectAuditData.isEmpty() || projectAuditData == null)
			return null;
		JSONObject node = new JSONObject();
		List<JSONObject> rowsNode = new ArrayList<JSONObject>();
		JSONArray     columns =new JSONArray();

		columns.add("Transaction Type");
		columns.add("User In Action");
		columns.add("Transaction Type");
		columns.add("Project");
		columns.add("Project Category");
		columns.add("Project Code");
		columns.add("Project Details");
		columns.add("Project Status");
		columns.add("Project Tier");
		columns.add("Project Type");
		columns.add("Project Refence ID");
		columns.add("Start Date");
		columns.add("End Date");
		columns.add("Releasing Date");
		columns.add("Parent Project");
		columns.add("Contract Type Name");
		columns.add("Project Owner");
		columns.add("Client");
		columns.add("Onsite Lead");
		columns.add("Client Point Of Contact");
		columns.add("Estimated Hours");
		columns.add("Billable");
		columns.add("Ispoc");

		for (JSONObject data : projectAuditData) {

			JSONObject dataNode = new JSONObject();
			dataNode.put("Transaction Type", data.get("revtype"));
			dataNode.put("User In Action", data.get("user_in_action"));
			dataNode.put("Transaction Date", data.get("trx_date"));
			dataNode.put("Project", data.get("project_name"));
			dataNode.put("Project Category", data.get("project_category"));
			dataNode.put("Project Code", data.get("project_code"));
			dataNode.put("Project Details", data.get("project_details"));
			dataNode.put("Project Status", data.get("project_status"));
			dataNode.put("Project Tier", data.get("project_tier"));
			dataNode.put("Project Type", data.get("project_type"));
			dataNode.put("Project Refence ID", data.get("project_ref_id"));
			dataNode.put("Start Date", data.get("start_date"));
			dataNode.put("End Date", data.get("end_date"));
			dataNode.put("Releasing Date", data.get("releasing_date"));
			dataNode.put("Parent Project", data.get("parent_project"));
			dataNode.put("Contract Type Name", data.get("contract_type_name"));
			dataNode.put("Project Owner", data.get("project_owner"));
			dataNode.put("Client", data.get("client_name"));
			dataNode.put("Onsite Lead", data.get("onsite_lead"));
			dataNode.put("Client Point Of Contact", data.get("client_point_of_contact"));
			dataNode.put("Estimated Hours", data.get("estimated_hours"));
			dataNode.put("Billable", data.get("is_billable"));
			dataNode.put("Ispoc", data.get("ispoc"));
			rowsNode.add(dataNode);

		}

		node.put("Columns", columns);
		node.put("Rows", rowsNode);
		return node;

	}

}
