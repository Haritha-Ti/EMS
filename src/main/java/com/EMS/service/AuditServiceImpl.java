package com.EMS.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
		columns.add("User In Action");
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
			dataNode.put("Approved Date", data.get("approved_date"));
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
		columns.add("User In Action");
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
			dataNode.put("Approved Date", data.get("approved_date"));
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
		columns.add("Transaction Date");
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
		//columns.add("CppLevels Id");
		columns.add("Department Name");
		columns.add("Region Name");
		columns.add("Role Name");
		columns.add("Timezone Name");	
		for (JSONObject data : userDetails) {
			JSONObject dataNode = new JSONObject();
			dataNode.put("Transaction Type", data.get("revtype"));
			dataNode.put("Transaction Date", data.get("trx_date"));
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
			dataNode.put("FirstName", data.get("first_name"));
			dataNode.put("LastName", data.get("last_name"));
			dataNode.put("Qualification", data.get("qualification"));
			dataNode.put("Referred By", data.get("referred_by"));
			dataNode.put("Termination Date", data.get("termination_date"));
			dataNode.put("User Name", data.get("user_name"));
			dataNode.put("Contractor Name", data.get("contractor_name"));
			//dataNode.put("CppLevels Id", data.get("cpplevels_id"));
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
		columns.add("Transaction Date");
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
		//columns.add("CppLevels Id");
		columns.add("Department Name");
		columns.add("Region Name");
		columns.add("Role Name");
		columns.add("Timezone Name");
		for (JSONObject data : userDetails) {
			JSONObject dataNode = new JSONObject();
			dataNode.put("Transaction Type", data.get("revtype"));
			dataNode.put("Transaction Date", data.get("trx_date"));
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
			dataNode.put("FirstName", data.get("first_name"));
			dataNode.put("LastName", data.get("last_name"));
			dataNode.put("Qualification", data.get("qualification"));
			dataNode.put("Referred By", data.get("referred_by"));
			dataNode.put("Termination Date", data.get("termination_date"));
			dataNode.put("User Name", data.get("user_name"));
			dataNode.put("Contractor Name", data.get("contractor_name"));
			//dataNode.put("CppLevels Id", data.get("cpplevels_id"));
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
		columns.add("Transaction Date");
		columns.add("Project");
		columns.add("Project Category");
		columns.add("Project Code");
		columns.add("Project Details");
		columns.add("Project Status");
		columns.add("Project Tier");
		columns.add("Project Type");
		//columns.add("Project Refence ID");
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
		//columns.add("Project Poc");
		
		
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
			//dataNode.put("Project Refence ID", data.get("project_ref_id"));
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
			//dataNode.put("Project Poc", data.get("ispoc"));
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
		columns.add("Transaction Date");
		columns.add("Project");
		columns.add("Project Category");
		columns.add("Project Code");
		columns.add("Project Details");
		columns.add("Project Status");
		columns.add("Project Tier");
		columns.add("Project Type");
		//columns.add("Project Refence ID");
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
		//columns.add("Project Poc");

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
			//dataNode.put("Project Refence ID", data.get("project_ref_id"));
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
			//dataNode.put("Project Poc", data.get("ispoc"));
			rowsNode.add(dataNode);

		}

		node.put("Columns", columns);
		node.put("Rows", rowsNode);
		return node;

	}
	
	
	@SuppressWarnings("unchecked")
	public void getProjectAuditeReport(Long projectId, Date startDate, Date endDate,Workbook workbook,Sheet sheet) {
		
		List<JSONObject>  projectAuditData=null;
		if(startDate==null && endDate==null )
			projectAuditData=auditRepository.getProjectAuditDataByProjectId(projectId);
		else
		    projectAuditData = auditRepository.getProjectAuditDataByProjectIdAndDateRange(projectId,
				startDate, endDate);
		
		JSONArray     columns =new JSONArray();

		
		String[] headers = new String[21];
		headers[0]="Transaction Type";
		headers[1]="User In Action";
		headers[2]="Transaction Date";
		headers[3]="Project";
		headers[4]="Project Category";
		headers[5]="Project Code";
		headers[6]="Project Details";
		headers[7]="Project Status";
		headers[8]="Project Tier";
		headers[9]="Project Type";
		//headers[10]="Project Refence ID";
		headers[10]="Start Date";
		headers[11]="End Date";
		headers[12]="Releasing Date";
		headers[13]="Parent Project";
		headers[14]="Contract Type Name";
		headers[15]="Project Owner";
		headers[16]="Client";
		headers[17]="Onsite Lead";
		headers[18]="Client Point Of Contact";
		headers[19]="Estimated Hours";
		headers[20]="Billable";
		//headers[22]="Project Poc";
		//Removing grids
				sheet.setDisplayGridlines(false);
				//Freezing columns and rows from scrooling
				//sheet.createFreezePane(3,3);

				//Bordered Cell Style
				CellStyle borderedCellStyle = workbook.createCellStyle();
				borderedCellStyle.setBorderLeft(BorderStyle.THIN);
				borderedCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				borderedCellStyle.setBorderRight(BorderStyle.THIN);
				borderedCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				borderedCellStyle.setBorderTop(BorderStyle.THIN);
				borderedCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				borderedCellStyle.setBorderBottom(BorderStyle.THIN);
				borderedCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

				//Title Cell Style
				CellStyle titleCellStyle = workbook.createCellStyle();
				//titleCellStyle.setFont((org.apache.poi.ss.usermodel.Font) headerFont);

				Row titleRow = sheet.createRow(0);
				Cell titleCell = titleRow.createCell(0);
				titleCell.setCellValue(" Project Audit Report");
				titleCell.setCellStyle(titleCellStyle);

				/*titleRow = sheet.createRow(1);
				titleCell = titleRow.createCell(1);
				titleCell.setCellValue("");*/

				XSSFFont font = (XSSFFont) workbook.createFont();
				font.setFontName("Liberation Sans");
				font.setFontHeightInPoints((short)10);
				font.setBold(true);


				// Header Cell Style
				CellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.cloneStyleFrom(borderedCellStyle);
				headerCellStyle.setBorderTop(BorderStyle.THICK);
				headerCellStyle.setFont(font);

				Row headerRow = sheet.createRow(1);
				int widthInChars = 50;
				sheet.setColumnWidth(4, widthInChars);
				for (int i = 0; i < headers.length; i++) {
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(headers[i]);
					cell.setCellStyle(headerCellStyle);
				}

				// Create Other rows and cells with contacts data
				int rowNum = 2;
				Row prevRow = null;
				CellStyle style = workbook.createCellStyle();
				style.setFillForegroundColor(IndexedColors.RED.getIndex());
			    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				for (JSONObject data : projectAuditData) {

					double totalHour =0.0;
					
					
					Row row = sheet.createRow(rowNum++);
                   
					Cell cell = row.createCell(0);
					cell.setCellValue((String)data.get("revtype"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(0).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					

					cell = row.createCell(1);
					cell.setCellValue((String) data.get("user_in_action"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(1).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(2);
					cell.setCellValue((String) data.get("trx_date"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(2).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(3);
					cell.setCellValue((String) data.get("project_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(3).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(4);
					cell.setCellValue((String) data.get("project_category"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(4).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(5);
					cell.setCellValue((String) data.get("project_code"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(5).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(6);
					cell.setCellValue((String) data.get("project_details"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(6).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(7);
					cell.setCellValue((String)data.get("project_status"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(7).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);

					cell = row.createCell(8);
					cell.setCellValue((String) data.get("project_tier"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(8).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(9);
					cell.setCellValue((String) data.get("project_type"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(9).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					/*cell = row.createCell(10);
					cell.setCellValue((String) data.get("project_ref_id"));
					cell.setCellStyle(borderedCellStyle);*/
					
					cell = row.createCell(10);
					cell.setCellValue((String) data.get("start_date"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(10).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(11);
					cell.setCellValue((String) data.get("end_date"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(11).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(12);
					cell.setCellValue((String) data.get("releasing_date"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(12).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					
				    cell = row.createCell(13);
					cell.setCellValue((String)data.get("parent_project"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(13).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);

					cell = row.createCell(14);
					cell.setCellValue((String) data.get("contract_type_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(14).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(15);
					cell.setCellValue((String) data.get("project_owner"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(15).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(16);
					cell.setCellValue((String) data.get("client_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(16).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(17);
					cell.setCellValue((String) data.get("onsite_lead"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(17).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(18);
					cell.setCellValue((String) data.get("client_point_of_contact"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(18).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(19);
					cell.setCellValue((Integer) data.get("estimated_hours"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(19).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(20);
					cell.setCellValue((String) data.get("is_billable"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(20).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					/*cell = row.createCell(22);
					cell.setCellValue((String) data.get("ispoc"));
					cell.setCellStyle(borderedCellStyle);*/

					prevRow=row;


				}
				// Resize all columns to fit the content size
				for (int i = 0; i < headers.length; i++) {
					sheet.autoSizeColumn(i);
				}
		
				//sheet.setAutoFilter(new CellRangeAddress(2, 3, 0, 1));
	}
	
	
	
	@SuppressWarnings("unchecked")
	public void getUserAuditDataReport(Long userId, Date fromDate, Date toDate,Workbook workbook,Sheet sheet) {
		
		List<JSONObject>  projectAuditData=null;
		if(fromDate==null && toDate==null )
			projectAuditData=auditRepository.getAuditUserDetailsById(userId);
		else
		    projectAuditData = auditRepository.getAuditUserDetailsByDateRange(userId,fromDate,toDate);
		
		JSONArray     columns =new JSONArray();

		
		String[] headers = new String[25];
		headers[0]="Transaction Type";
		headers[1]="Transaction Date";
		headers[2]="User In Action";
		headers[3]="Recruiter";
		headers[4]="Active";
		headers[5]="Blood Group";
		headers[6]="Contact";
		headers[7]="Level Name";
		headers[8]="DateOfBirth";
		headers[9]="Email";
		headers[10]="Employee Categroy";
		headers[11]="Employee Id";
		headers[12]="Employment Type";
		headers[13]="FirstName";
		headers[14]="LastName";
		headers[15]="Qualification";
		headers[16]="Referred By";
		headers[17]="Termination Date";
		headers[18]="User Name";
		headers[19]="Contractor Name";
		headers[20]="Department Name";
		headers[21]="Region Name";
		headers[22]="Role Name";
		headers[23]="Timezone Name"; 
		//Removing grids
				sheet.setDisplayGridlines(false);
				//Freezing columns and rows from scrooling
				//sheet.createFreezePane(3,3);

				//Bordered Cell Style
				CellStyle borderedCellStyle = workbook.createCellStyle();
				borderedCellStyle.setBorderLeft(BorderStyle.THIN);
				borderedCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				borderedCellStyle.setBorderRight(BorderStyle.THIN);
				borderedCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				borderedCellStyle.setBorderTop(BorderStyle.THIN);
				borderedCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				borderedCellStyle.setBorderBottom(BorderStyle.THIN);
				borderedCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

				//Title Cell Style
				CellStyle titleCellStyle = workbook.createCellStyle();
				//titleCellStyle.setFont((org.apache.poi.ss.usermodel.Font) headerFont);

				Row titleRow = sheet.createRow(0);
				Cell titleCell = titleRow.createCell(0);
				titleCell.setCellValue(" User Audit Report");
				titleCell.setCellStyle(titleCellStyle);

				/*titleRow = sheet.createRow(1);
				titleCell = titleRow.createCell(1);
				titleCell.setCellValue("");*/

				XSSFFont font = (XSSFFont) workbook.createFont();
				font.setFontName("Liberation Sans");
				font.setFontHeightInPoints((short)10);
				font.setBold(true);


				// Header Cell Style
				CellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.cloneStyleFrom(borderedCellStyle);
				headerCellStyle.setBorderTop(BorderStyle.THICK);
				headerCellStyle.setFont(font);

				Row headerRow = sheet.createRow(1);
				int widthInChars = 50;
				sheet.setColumnWidth(4, widthInChars);
				for (int i = 0; i < headers.length; i++) {
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(headers[i]);
					cell.setCellStyle(headerCellStyle);
				}

				// Create Other rows and cells with contacts data
				int rowNum = 2;
                Row prevRow=null;
                CellStyle style = workbook.createCellStyle();
				style.setFillForegroundColor(IndexedColors.RED.getIndex());
			    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				for (JSONObject data : projectAuditData) {

					double totalHour =0.0;
					Row row = sheet.createRow(rowNum++);

					Cell cell = row.createCell(0);
					cell.setCellValue((String)data.get("revtype"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(0).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);

					cell = row.createCell(1);
					cell.setCellValue((String) data.get("trx_date"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(1).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(2);
					cell.setCellValue((String) data.get("user_in_action"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(2).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(3);
					cell.setCellValue((String) data.get("recruiter"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(3).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(4);
					cell.setCellValue((boolean) data.get("active"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(4).getBooleanCellValue()==cell.getBooleanCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(5);
					cell.setCellValue((String) data.get("blood_group"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(5).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(6);
					cell.setCellValue(data.get("contact")+"");
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(6).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(7);
					cell.setCellValue((String)data.get("level_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(7).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);

					cell = row.createCell(8);
					cell.setCellValue((String) data.get("dob"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(8).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(9);
					cell.setCellValue((String) data.get("email"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(9).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(10);
					cell.setCellValue((String) data.get("emp_category"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(10).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(11);
					cell.setCellValue(data.get("emp_id")+"");
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(11).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(12);
					cell.setCellValue((String) data.get("employment_type"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(12).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(13);
					cell.setCellValue((String) data.get("first_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(13).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					
				    cell = row.createCell(14);
					cell.setCellValue((String)data.get("last_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(14).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);

					cell = row.createCell(15);
					cell.setCellValue((String) data.get("qualification"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(15).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(16);
					cell.setCellValue((String) data.get("referred_by"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(16).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(17);
					cell.setCellValue((String) data.get("termination_date"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(17).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(18);
					cell.setCellValue((String) data.get("user_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(18).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(19);
					cell.setCellValue((String) data.get("contractor_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(19).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					/*cell = row.createCell(20);
					cell.setCellValue(data.get("cpplevels_id")+"");
					cell.setCellStyle(borderedCellStyle);*/
					
					cell = row.createCell(20);
					cell.setCellValue((String) data.get("department_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(20).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(21);
					cell.setCellValue((String) data.get("region_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(21).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(22);
					cell.setCellValue((String) data.get("role_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(22).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(23);
					cell.setCellValue((String) data.get("timezone_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(23).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);

					prevRow=row;


				}
				// Resize all columns to fit the content size
				for (int i = 0; i < headers.length; i++) {
					sheet.autoSizeColumn(i);
				}
		
				//sheet.setAutoFilter(new CellRangeAddress(2, 3, 0, 1));
	}

	
	@SuppressWarnings("unchecked")
	public void getAuditDataReport(Long userId, Long projectId ,Date fromDate, Date toDate,Workbook workbook,Sheet sheet) {
		
		List<JSONObject>  AuditData=null;
		
		     AuditData=auditRepository.getAuditDataByUserId(projectId,userId,fromDate,toDate);
		
		
		JSONArray     columns =new JSONArray();

		
		String[] headers = new String[42];
		headers[0]="User Name";
		headers[1]="Project Name";
		headers[2]="Transaction Type";
		headers[3]="Transaction Date";
		headers[4]="Users In Action";
		headers[5]="Approved Date";
		headers[6]="Day1";
		headers[7]="Day2";
		headers[8]="Day3";
		headers[9]="Day4";
		headers[10]="Day5";
		headers[11]="Day6";
		headers[12]="Day7";
		headers[13]="Day8";
		headers[14]="Day9";
		headers[15]="Day10";
		headers[16]="Day11";
		headers[17]="Day12";
		headers[18]="Day13";
		headers[19]="Day14";
		headers[20]="Day15";
		headers[21]="Day16";
		headers[22]="Day17";
		headers[23]="Day18"; 
		headers[24]="Day19"; 
		headers[25]="Day20"; 
		headers[26]="Day21"; 
		headers[27]="Day22"; 
		headers[28]="Day23"; 
		headers[29]="Day24"; 
		headers[30]="Day25"; 
		headers[31]="Day26"; 
		headers[32]="Day27"; 
		headers[33]="Day28";
		headers[34]="Day29";
		headers[35]="Day30";
		headers[36]="Day31";
		headers[37]="First Half Status";
		headers[38]="Month";
		headers[39]="Project Type";
		headers[40]="Second Half Status";
		headers[41]="Year";
		//Removing grids
				sheet.setDisplayGridlines(false);
				//Freezing columns and rows from scrooling
				sheet.createFreezePane(3,3);

				//Bordered Cell Style
				CellStyle borderedCellStyle = workbook.createCellStyle();
				borderedCellStyle.setBorderLeft(BorderStyle.THIN);
				borderedCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				borderedCellStyle.setBorderRight(BorderStyle.THIN);
				borderedCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				borderedCellStyle.setBorderTop(BorderStyle.THIN);
				borderedCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				borderedCellStyle.setBorderBottom(BorderStyle.THIN);
				borderedCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

				//Title Cell Style
				CellStyle titleCellStyle = workbook.createCellStyle();
				//titleCellStyle.setFont((org.apache.poi.ss.usermodel.Font) headerFont);

				Row titleRow = sheet.createRow(0);
				Cell titleCell = titleRow.createCell(0);
				titleCell.setCellValue("Audit Report");
				titleCell.setCellStyle(titleCellStyle);

				/*titleRow = sheet.createRow(1);
				titleCell = titleRow.createCell(1);
				titleCell.setCellValue("");*/

				XSSFFont font = (XSSFFont) workbook.createFont();
				font.setFontName("Liberation Sans");
				font.setFontHeightInPoints((short)10);
				font.setBold(true);


				// Header Cell Style
				CellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.cloneStyleFrom(borderedCellStyle);
				headerCellStyle.setBorderTop(BorderStyle.THICK);
				headerCellStyle.setFont(font);

				Row headerRow = sheet.createRow(1);
				int widthInChars = 50;
				sheet.setColumnWidth(4, widthInChars);
				for (int i = 0; i < headers.length; i++) {
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(headers[i]);
					cell.setCellStyle(headerCellStyle);
				}

				// Create Other rows and cells with contacts data
				int rowNum = 2;
				Row prevRow=null;
                CellStyle style = workbook.createCellStyle();
				style.setFillForegroundColor(IndexedColors.RED.getIndex());
			    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				for (JSONObject data : AuditData) {

					double totalHour =0.0;
					Row row = sheet.createRow(rowNum++);

					Cell cell = row.createCell(0);
					cell.setCellValue((String)data.get("user_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(0).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);

					cell = row.createCell(1);
					cell.setCellValue((String) data.get("project_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(1).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(2);
					cell.setCellValue((String) data.get("revtype"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(2).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(3);
					cell.setCellValue((String) data.get("trx_date"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(3).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(4);
					cell.setCellValue((String) data.get("user_in_action"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(4).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(5);
					cell.setCellValue((String) data.get("approved_date"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(5).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(6);
					cell.setCellValue((Double)data.get("day1"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(6).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(7);
					cell.setCellValue((Double)data.get("day2"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(7).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);

					cell = row.createCell(8);
					cell.setCellValue((Double) data.get("day3"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(8).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(9);
					cell.setCellValue((Double) data.get("day4"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(9).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(10);
					cell.setCellValue((Double) data.get("day5"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(10).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(11);
					cell.setCellValue((Double)data.get("day6"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(11).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(12);
					cell.setCellValue((Double) data.get("day7"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(12).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(13);
					cell.setCellValue((Double) data.get("day8"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(13).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					
				    cell = row.createCell(14);
					cell.setCellValue((Double)data.get("day9"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(14).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);

					cell = row.createCell(15);
					cell.setCellValue((Double) data.get("day10"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(15).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(16);
					cell.setCellValue((Double) data.get("day11"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(16).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(17);
					cell.setCellValue((Double) data.get("day12"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(17).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(18);
					cell.setCellValue((Double) data.get("day13"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(18).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(19);
					cell.setCellValue((Double) data.get("day14"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(19).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					/*cell = row.createCell(20);
					cell.setCellValue(data.get("cpplevels_id")+"");
					cell.setCellStyle(borderedCellStyle);*/
					
					cell = row.createCell(20);
					cell.setCellValue((Double) data.get("day15"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(20).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(21);
					cell.setCellValue((Double) data.get("day16"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(21).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(22);
					cell.setCellValue((Double) data.get("day17"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(22).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(23);
					cell.setCellValue((Double) data.get("day18"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(23).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(24);
					cell.setCellValue((Double) data.get("day19"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(24).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(25);
					cell.setCellValue((Double) data.get("day20"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(25).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(26);
					cell.setCellValue((Double) data.get("day21"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(26).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(27);
					cell.setCellValue((Double) data.get("day22"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(27).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(28);
					cell.setCellValue((Double) data.get("day23"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(28).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(29);
					cell.setCellValue((Double) data.get("day24"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(29).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(30);
					cell.setCellValue((Double) data.get("day25"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(30).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(31);
					cell.setCellValue((Double) data.get("day26"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(31).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(32);
					cell.setCellValue((Double) data.get("day27"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(32).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(33);
					cell.setCellValue((Double) data.get("day28"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(33).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(34);
					cell.setCellValue((Double) data.get("day29"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(34).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(35);
					cell.setCellValue((Double) data.get("day30"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(35).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(36);
					cell.setCellValue((Double) data.get("day31"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(36).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(37);
					cell.setCellValue((String) data.get("first_half_status"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(37).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(38);
					cell.setCellValue((Integer) data.get("month"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(38).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(39);
					cell.setCellValue((String) data.get("project_type"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(39).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(40);
					cell.setCellValue((String) data.get("second_half_status"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(40).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(41);
					cell.setCellValue((Integer) data.get("year"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(41).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);

					
					prevRow=row;


				}
				// Resize all columns to fit the content size
				for (int i = 0; i < headers.length; i++) {
					sheet.autoSizeColumn(i);
				}
		
				//sheet.setAutoFilter(new CellRangeAddress(2, 3, 0, 1));
	}
	
	
	@SuppressWarnings("unchecked")
	public void getAuditDataFinalReport(Long userId, Long projectId ,Date fromDate, Date toDate,Workbook workbook,Sheet sheet) {
		
		List<JSONObject>  AuditData=null;
		
		     AuditData=auditRepository.getAuditDataByUserIdForFinal(projectId,userId,fromDate,toDate);
		
		
		JSONArray     columns =new JSONArray();

		
		String[] headers = new String[42];
		headers[0]="User Name";
		headers[1]="Project Name";
		headers[2]="Transaction Type";
		headers[3]="Transaction Date";
		headers[4]="Users In Action";
		headers[5]="Approved Date";
		headers[6]="Day1";
		headers[7]="Day2";
		headers[8]="Day3";
		headers[9]="Day4";
		headers[10]="Day5";
		headers[11]="Day6";
		headers[12]="Day7";
		headers[13]="Day8";
		headers[14]="Day9";
		headers[15]="Day10";
		headers[16]="Day11";
		headers[17]="Day12";
		headers[18]="Day13";
		headers[19]="Day14";
		headers[20]="Day15";
		headers[21]="Day16";
		headers[22]="Day17";
		headers[23]="Day18"; 
		headers[24]="Day19"; 
		headers[25]="Day20"; 
		headers[26]="Day21"; 
		headers[27]="Day22"; 
		headers[28]="Day23"; 
		headers[29]="Day24"; 
		headers[30]="Day25"; 
		headers[31]="Day26"; 
		headers[32]="Day27"; 
		headers[33]="Day28";
		headers[34]="Day29";
		headers[35]="Day30";
		headers[36]="Day31";
		headers[37]="First Half Status";
		headers[38]="Month";
		headers[39]="Project Type";
		headers[40]="Second Half Status";
		headers[41]="Year";
		//Removing grids
				sheet.setDisplayGridlines(false);
				//Freezing columns and rows from scrooling
				//sheet.createFreezePane(3,3);

				//Bordered Cell Style
				CellStyle borderedCellStyle = workbook.createCellStyle();
				borderedCellStyle.setBorderLeft(BorderStyle.THIN);
				borderedCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				borderedCellStyle.setBorderRight(BorderStyle.THIN);
				borderedCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				borderedCellStyle.setBorderTop(BorderStyle.THIN);
				borderedCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				borderedCellStyle.setBorderBottom(BorderStyle.THIN);
				borderedCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

				//Title Cell Style
				CellStyle titleCellStyle = workbook.createCellStyle();
				//titleCellStyle.setFont((org.apache.poi.ss.usermodel.Font) headerFont);

				Row titleRow = sheet.createRow(0);
				Cell titleCell = titleRow.createCell(0);
				titleCell.setCellValue("Audit Final Report ");
				titleCell.setCellStyle(titleCellStyle);

				/*titleRow = sheet.createRow(1);
				titleCell = titleRow.createCell(1);
				titleCell.setCellValue("");*/

				XSSFFont font = (XSSFFont) workbook.createFont();
				font.setFontName("Liberation Sans");
				font.setFontHeightInPoints((short)10);
				font.setBold(true);


				// Header Cell Style
				CellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.cloneStyleFrom(borderedCellStyle);
				headerCellStyle.setBorderTop(BorderStyle.THICK);
				headerCellStyle.setFont(font);

				Row headerRow = sheet.createRow(1);
				int widthInChars = 50;
				sheet.setColumnWidth(4, widthInChars);
				for (int i = 0; i < headers.length; i++) {
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(headers[i]);
					cell.setCellStyle(headerCellStyle);
				}

				// Create Other rows and cells with contacts data
				int rowNum = 2;
				Row prevRow=null;
                CellStyle style = workbook.createCellStyle();
				style.setFillForegroundColor(IndexedColors.RED.getIndex());
			    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

				for (JSONObject data : AuditData) {

					double totalHour =0.0;
					Row row = sheet.createRow(rowNum++);

					Cell cell = row.createCell(0);
					cell.setCellValue((String)data.get("user_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(0).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);

					cell = row.createCell(1);
					cell.setCellValue((String) data.get("project_name"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(1).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(2);
					cell.setCellValue((String) data.get("revtype"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(2).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(3);
					cell.setCellValue((String) data.get("trx_date"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(3).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(4);
					cell.setCellValue((String) data.get("user_in_action"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(4).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(5);
					cell.setCellValue((String) data.get("approved_date"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(5).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(6);
					cell.setCellValue((Double)data.get("day1"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(6).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(7);
					cell.setCellValue((Double)data.get("day2"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(7).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);

					cell = row.createCell(8);
					cell.setCellValue((Double) data.get("day3"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(8).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(9);
					cell.setCellValue((Double) data.get("day4"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(9).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(10);
					cell.setCellValue((Double) data.get("day5"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(10).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(11);
					cell.setCellValue((Double)data.get("day6"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(11).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(12);
					cell.setCellValue((Double) data.get("day7"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(12).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(13);
					cell.setCellValue((Double) data.get("day8"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(13).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					
				    cell = row.createCell(14);
					cell.setCellValue((Double)data.get("day9"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(14).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);

					cell = row.createCell(15);
					cell.setCellValue((Double) data.get("day10"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(15).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(16);
					cell.setCellValue((Double) data.get("day11"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(16).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(17);
					cell.setCellValue((Double) data.get("day12"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(17).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(18);
					cell.setCellValue((Double) data.get("day13"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(18).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(19);
					cell.setCellValue((Double) data.get("day14"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(19).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					/*cell = row.createCell(20);
					cell.setCellValue(data.get("cpplevels_id")+"");
					cell.setCellStyle(borderedCellStyle);*/
					
					cell = row.createCell(20);
					cell.setCellValue((Double) data.get("day15"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(20).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(21);
					cell.setCellValue((Double) data.get("day16"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(21).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(22);
					cell.setCellValue((Double) data.get("day17"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(22).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(23);
					cell.setCellValue((Double) data.get("day18"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(23).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(24);
					cell.setCellValue((Double) data.get("day19"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(24).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(25);
					cell.setCellValue((Double) data.get("day20"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(25).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(26);
					cell.setCellValue((Double) data.get("day21"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(26).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(27);
					cell.setCellValue((Double) data.get("day22"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(27).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(28);
					cell.setCellValue((Double) data.get("day23"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(28).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(29);
					cell.setCellValue((Double) data.get("day24"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(29).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(30);
					cell.setCellValue((Double) data.get("day25"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(30).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(31);
					cell.setCellValue((Double) data.get("day26"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(31).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(32);
					cell.setCellValue((Double) data.get("day27"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(32).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(33);
					cell.setCellValue((Double) data.get("day28"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(33).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(34);
					cell.setCellValue((Double) data.get("day29"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(34).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(35);
					cell.setCellValue((Double) data.get("day30"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(35).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(36);
					cell.setCellValue((Double) data.get("day31"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(36).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(37);
					cell.setCellValue((String) data.get("first_half_status"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(37).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(38);
					cell.setCellValue((Integer) data.get("month"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(38).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(39);
					cell.setCellValue((String) data.get("project_type"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(39).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(40);
					cell.setCellValue((String) data.get("second_half_status"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(40).getStringCellValue()==cell.getStringCellValue())) )
					    cell.setCellStyle(style);
					
					cell = row.createCell(41);
					cell.setCellValue((Integer) data.get("year"));
					cell.setCellStyle(borderedCellStyle);
					if(prevRow!=null &&  (!(prevRow.getCell(41).getNumericCellValue()==cell.getNumericCellValue())) )
					    cell.setCellStyle(style);

					
					prevRow=row;


				}
				// Resize all columns to fit the content size
				for (int i = 0; i < headers.length; i++) {
					sheet.autoSizeColumn(i);
				}
		
				//sheet.setAutoFilter(new CellRangeAddress(2, 3, 0, 1));
	}




}
