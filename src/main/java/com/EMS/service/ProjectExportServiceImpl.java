package com.EMS.service;


import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.EMS.model.*;
import com.EMS.repository.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
//import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.EMS.repository.UserRepository;
import com.EMS.repository.TimeTrackApprovalJPARepository;
import com.EMS.repository.TasktrackRepository;
import com.EMS.repository.HolidayRepository;
import com.EMS.repository.UserLeaveSummaryRepository;

@Service
public class ProjectExportServiceImpl implements ProjectExportService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	TimeTrackApprovalJPARepository timeTrackApprovalJPARepository;

	@Autowired
	TasktrackRepository tasktrackRepository;

	@Autowired
	HolidayRepository holidayRepository;

	@Autowired
	UserLeaveSummaryRepository userLeaveSummaryRepository;

	@Autowired
	TaskTrackFinanceRepository taskTrackFinanceRepository;


	@Override
	public void exportProjectTaskReport(List <ExportProjectTaskReportModel> data,HttpServletResponse response) throws FileNotFoundException {

		// TODO Auto-generated method stub
		String[] headers = { "Project Name", "Task Date","Resource Name","Task Category","Task Description", "Hours","Billable"};

		//public static void main(String[] args) throws IOException, InvalidFormatException {
		List<ExportProjectTaskReportModel> Listdata = new ArrayList<ExportProjectTaskReportModel>();

		for(ExportProjectTaskReportModel obj : data) {
			Listdata.add(new ExportProjectTaskReportModel
					(obj.getProjectName(),obj.getResourceName(),obj.getTaskDate()
							,obj.getHours(),obj.getTaskCategory(),obj.getTaskDescription(),obj.getBillable()));

		}

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Project Task Report");
		//Removing grids
		sheet.setDisplayGridlines(false);
		//Freezing columns and rows from scrooling
		sheet.createFreezePane(0,3);

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
		titleCell.setCellValue("PROJECT TASK REPORT");
		titleCell.setCellStyle(titleCellStyle);

		titleRow = sheet.createRow(1);
		titleCell = titleRow.createCell(1);
		titleCell.setCellValue("");

		// Header Cell Style
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.cloneStyleFrom(borderedCellStyle);
		headerCellStyle.setBorderTop(BorderStyle.THICK);
		//headerCellStyle.setFont((org.apache.poi.ss.usermodel.Font) headerFont);

		Row headerRow = sheet.createRow(2);
		int widthInChars = 50;
		sheet.setColumnWidth(4, widthInChars);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with contacts data
		int rowNum = 3;
		ExportProjectTaskReportModel totalSummary = new ExportProjectTaskReportModel();
		for (ExportProjectTaskReportModel summary : Listdata) {
			Row row = sheet.createRow(rowNum++);
			Cell cell = row.createCell(0);
			cell.setCellValue(summary.getProjectName());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(1);
			cell.setCellValue(summary.getTaskDate());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(2);
			cell.setCellValue(summary.getResourceName());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(3);
			cell.setCellValue(summary.getTaskCategory());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(4);
			cell.setCellValue(summary.getTaskDescription());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(5);
			cell.setCellValue(summary.getHours());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(6);
			cell.setCellValue(summary.getBillable());
			cell.setCellStyle(borderedCellStyle);

		}


		// Resize all columns to fit the content size
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		//Adding filter menu in column headers
		sheet.setAutoFilter(new CellRangeAddress(2, 13, 1, 6));


		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
		response.setHeader( "Content-Disposition", "filename=\"" + "ProjectTaskReport.xlsx" + "\"" );

		try {
			workbook.write(response.getOutputStream());
			workbook.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void exportProjectHourReport(List <ExportProjectHourReportModel> data,Workbook workbook,Sheet sheet) throws FileNotFoundException {

		// TODO Auto-generated method stub
		String[] headers = { "Project Name", "First Name","Last Name","Actual Hours","Approved Hours"};

		//public static void main(String[] args) throws IOException, InvalidFormatException {
		List<ExportProjectHourReportModel> Listdata = new ArrayList<ExportProjectHourReportModel>();

		for(ExportProjectHourReportModel obj : data) {
			Listdata.add(new ExportProjectHourReportModel
					(obj.getProjectName(),obj.getFirstName(),obj.getLastName()
							,obj.getApproved(),obj.getLogged()));

		}

		//Removing grids
		sheet.setDisplayGridlines(false);
		//Freezing columns and rows from scrooling
		sheet.createFreezePane(0,3);

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
		titleCell.setCellValue("PROJECT HOUR REPORT");
		titleCell.setCellStyle(titleCellStyle);

		titleRow = sheet.createRow(1);
		titleCell = titleRow.createCell(1);
		titleCell.setCellValue("");

		// Header Cell Style
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.cloneStyleFrom(borderedCellStyle);
		headerCellStyle.setBorderTop(BorderStyle.THICK);

		Row headerRow = sheet.createRow(2);
		int widthInChars = 50;
		sheet.setColumnWidth(4, widthInChars);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with contacts data
		int rowNum = 3;
		ExportProjectHourReportModel totalSummary = new ExportProjectHourReportModel();
		for (ExportProjectHourReportModel summary : Listdata) {
			Row row = sheet.createRow(rowNum++);
			Cell cell = row.createCell(0);
			cell.setCellValue(summary.getProjectName());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(1);
			cell.setCellValue(summary.getFirstName());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(2);
			cell.setCellValue(summary.getLastName());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(3);
			cell.setCellValue(summary.getLogged());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(4);
			cell.setCellValue(summary.getApproved());
			cell.setCellStyle(borderedCellStyle);

		}

		// Resize all columns to fit the content size
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		//Adding filter menu in column headers
		sheet.setAutoFilter(new CellRangeAddress(2, 13, 1, 4));

	}

	@Override
	public void exportApprovalReport(List <ExportApprovalReportModel> data,Workbook workbook,Sheet sheet,ArrayList<String> colNames) throws FileNotFoundException {

		String[] headers = new String[35];
		headers[0] = "User Id";
		headers[1] = "Last Name";
		headers[2] = "First Name";
		headers[3] = "Project Name";
		int dayCount = colNames.size();
		for(int i=0;i<dayCount;i++) {
			headers[i+4] = colNames.get(i);
		}

		List<ExportApprovalReportModel> Listdata = new ArrayList<ExportApprovalReportModel>();

		for(ExportApprovalReportModel obj : data) {
			Listdata.add(new ExportApprovalReportModel
					(obj.getId(),obj.getProjectName(),obj.getFirstName(),obj.getLastName(),obj.getProjectType(),obj.getCppLevel()
							,obj.getDay1(),obj.getDay2(),obj.getDay3(),obj.getDay4(),obj.getDay5(),
							obj.getDay6(),obj.getDay7(),obj.getDay8(),obj.getDay9(),obj.getDay10(),
							obj.getDay11(),obj.getDay12(),obj.getDay13(),obj.getDay14(),obj.getDay15(),
							obj.getDay16(),obj.getDay17(),obj.getDay18(),obj.getDay19(),obj.getDay20(),
							obj.getDay21(),obj.getDay22(),obj.getDay23(),obj.getDay24(),obj.getDay25(),
							obj.getDay26(),obj.getDay27(),obj.getDay28(),obj.getDay29(),obj.getDay30(),
							obj.getDay31()));
		}

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
		titleCell.setCellValue("PROJECT APPROVAL REPORT");
		titleCell.setCellStyle(titleCellStyle);

		titleRow = sheet.createRow(1);
		titleCell = titleRow.createCell(1);
		titleCell.setCellValue("");

		XSSFFont font = (XSSFFont) workbook.createFont();
		font.setFontName("Liberation Sans");
		font.setFontHeightInPoints((short)10);
		font.setBold(true);

		// Header Cell Style
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.cloneStyleFrom(borderedCellStyle);
		headerCellStyle.setBorderTop(BorderStyle.THICK);
		headerCellStyle.setFont(font);

		Row headerRow = sheet.createRow(2);
		int widthInChars = 50;
		sheet.setColumnWidth(4, widthInChars);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with contacts data
		int rowNum = 3;
		ExportApprovalReportModel totalSummary = new ExportApprovalReportModel();
		for (ExportApprovalReportModel summary : Listdata) {
			Row row = sheet.createRow(rowNum++);

			Cell cell = row.createCell(0);
			cell.setCellValue(summary.getId());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(1);
			cell.setCellValue(summary.getLastName());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(2);
			cell.setCellValue(summary.getFirstName());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(3);
			cell.setCellValue(summary.getProjectName());
			cell.setCellStyle(borderedCellStyle);

			if(dayCount>0) {
				cell = row.createCell(4);
				cell.setCellValue(summary.getDay1());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>1) {
				cell = row.createCell(5);
				cell.setCellValue(summary.getDay2());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>2) {
				cell = row.createCell(6);
				cell.setCellValue(summary.getDay3());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>3) {
				cell = row.createCell(7);
				cell.setCellValue(summary.getDay4());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>4) {
				cell = row.createCell(8);
				cell.setCellValue(summary.getDay5());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>5) {
				cell = row.createCell(9);
				cell.setCellValue(summary.getDay6());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>6) {
				cell = row.createCell(10);
				cell.setCellValue(summary.getDay7());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>7) {
				cell = row.createCell(11);
				cell.setCellValue(summary.getDay8());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>8) {
				cell = row.createCell(12);
				cell.setCellValue(summary.getDay9());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>9) {
				cell = row.createCell(13);
				cell.setCellValue(summary.getDay10());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>10) {
				cell = row.createCell(14);
				cell.setCellValue(summary.getDay11());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>11) {
				cell = row.createCell(15);
				cell.setCellValue(summary.getDay12());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>12) {
				cell = row.createCell(16);
				cell.setCellValue(summary.getDay13());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>13) {
				cell = row.createCell(17);
				cell.setCellValue(summary.getDay14());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>14) {
				cell = row.createCell(18);
				cell.setCellValue(summary.getDay15());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>15) {
				cell = row.createCell(19);
				cell.setCellValue(summary.getDay16());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>16) {
				cell = row.createCell(20);
				cell.setCellValue(summary.getDay17());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>17) {
				cell = row.createCell(21);
				cell.setCellValue(summary.getDay18());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>18) {
				cell = row.createCell(22);
				cell.setCellValue(summary.getDay19());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>19) {
				cell = row.createCell(23);
				cell.setCellValue(summary.getDay20());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>20) {
				cell = row.createCell(24);
				cell.setCellValue(summary.getDay21());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>21) {
				cell = row.createCell(25);
				cell.setCellValue(summary.getDay22());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>22) {
				cell = row.createCell(26);
				cell.setCellValue(summary.getDay23());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>23) {
				cell = row.createCell(27);
				cell.setCellValue(summary.getDay24());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>24) {
				cell = row.createCell(28);
				cell.setCellValue(summary.getDay25());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>25) {
				cell = row.createCell(29);
				cell.setCellValue(summary.getDay26());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>26) {
				cell = row.createCell(30);
				cell.setCellValue(summary.getDay27());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>27) {
				cell = row.createCell(31);
				cell.setCellValue(summary.getDay28());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>28) {
				cell = row.createCell(32);
				cell.setCellValue(summary.getDay29());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>29) {
				cell = row.createCell(33);
				cell.setCellValue(summary.getDay30());
				cell.setCellStyle(borderedCellStyle);
			}
			if(dayCount>30) {
				cell = row.createCell(34);
				cell.setCellValue(summary.getDay31());
				cell.setCellStyle(borderedCellStyle);
			}
		}

		// Resize all columns to fit the content size
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		//Adding filter menu in column headers
		sheet.setAutoFilter(new CellRangeAddress(2, 13, 1, 4));

	}


	@Override
	public void exportAllReport(List <ExportApprovalReportModel> data,Workbook workbook,Sheet sheet,ArrayList<String> colNames,String reportName) throws FileNotFoundException {
		int dayCount = colNames.size();
		int cols = dayCount+6;
		String[] headers = new String[cols];
		//headers[0] = "User Id";
		headers[0] = "Last Name";
		headers[1] = "First Name";
		headers[2] = "Project Name";
		headers[3] = "Project Type";
		headers[4] = "Cpp Level";
		for(int i=0;i<dayCount;i++) {
			headers[i+5] = colNames.get(i);

		}
		headers[dayCount+5] = "Total Hours";
		List<ExportApprovalReportModel> Listdata = new ArrayList<ExportApprovalReportModel>();
		for(ExportApprovalReportModel obj : data) {

			Listdata.add(new ExportApprovalReportModel
					(obj.getId(),obj.getProjectName(),obj.getFirstName(),obj.getLastName(),obj.getProjectType(),obj.getCppLevel()
							,obj.getDay1(),obj.getDay2(),obj.getDay3(),obj.getDay4(),obj.getDay5(),
							obj.getDay6(),obj.getDay7(),obj.getDay8(),obj.getDay9(),obj.getDay10(),
							obj.getDay11(),obj.getDay12(),obj.getDay13(),obj.getDay14(),obj.getDay15(),
							obj.getDay16(),obj.getDay17(),obj.getDay18(),obj.getDay19(),obj.getDay20(),
							obj.getDay21(),obj.getDay22(),obj.getDay23(),obj.getDay24(),obj.getDay25(),
							obj.getDay26(),obj.getDay27(),obj.getDay28(),obj.getDay29(),obj.getDay30(),
							obj.getDay31()));
		}

		//Removing grids
		sheet.setDisplayGridlines(false);
		//Freezing columns and rows from scrooling
		sheet.createFreezePane(4,4);

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
		titleCell.setCellValue(reportName);
		titleCell.setCellStyle(titleCellStyle);

		titleRow = sheet.createRow(1);
		titleCell = titleRow.createCell(1);
		titleCell.setCellValue("");

		XSSFFont font = (XSSFFont) workbook.createFont();
		font.setFontName("Liberation Sans");
		font.setFontHeightInPoints((short)10);
		font.setBold(true);


		// Header Cell Style
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.cloneStyleFrom(borderedCellStyle);
		headerCellStyle.setBorderTop(BorderStyle.THICK);
		headerCellStyle.setFont(font);

		Row headerRow = sheet.createRow(2);
		int widthInChars = 50;
		sheet.setColumnWidth(4, widthInChars);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with contacts data
		int rowNum = 3;

		String tmp_pjctName =" ";
		ExportApprovalReportModel totalSummary = new ExportApprovalReportModel();
		for (ExportApprovalReportModel summary : Listdata) {
			if (!tmp_pjctName .equals(" ") && !tmp_pjctName.equals(summary.getProjectName())) {
				Row row1 = sheet.createRow(rowNum++);
				int j;
				for(j=0;j<dayCount+5;j++){
					Cell cell = row1.createCell(j);
					cell.setCellValue("");
					cell.setCellStyle(borderedCellStyle);
				}
			}
			Row row = sheet.createRow(rowNum++);

			/*Cell cell = row.createCell(0);
			cell.setCellValue(summary.getId());
			cell.setCellStyle(borderedCellStyle);*/

			Cell cell = row.createCell(0);
			cell.setCellValue(summary.getLastName());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(1);
			cell.setCellValue(summary.getFirstName());
			cell.setCellStyle(borderedCellStyle);		

			cell = row.createCell(2);
			cell.setCellValue(summary.getProjectName());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(3);
			cell.setCellValue(summary.getProjectType());
			cell.setCellStyle(borderedCellStyle);
			
			cell = row.createCell(4);
			cell.setCellValue(summary.getCppLevel());
			cell.setCellStyle(borderedCellStyle);
			int cellcount = 4;
			double totalHour = 0.0;
			if(dayCount>0) {
				cellcount = 5;
				cell = row.createCell(5);
				cell.setCellValue(summary.getDay1());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay1();
			}
			if(dayCount>1) {
				cellcount = 6;
				cell = row.createCell(6);
				cell.setCellValue(summary.getDay2());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay2();
			}
			if(dayCount>2) {
				cellcount = 7;
				cell = row.createCell(7);
				cell.setCellValue(summary.getDay3());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay3();
			}
			if(dayCount>3) {
				cellcount = 8;
				cell = row.createCell(8);
				cell.setCellValue(summary.getDay4());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay4();
			}
			if(dayCount>4) {
				cellcount = 9;
				cell = row.createCell(9);
				cell.setCellValue(summary.getDay5());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay5();
			}
			if(dayCount>5) {
				cellcount = 10;
				cell = row.createCell(10);
				cell.setCellValue(summary.getDay6());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay6();
			}
			if(dayCount>6) {
				cellcount = 11;
				cell = row.createCell(11);
				cell.setCellValue(summary.getDay7());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay7();
			}
			if(dayCount>7) {
				cellcount = 12;
				cell = row.createCell(12);
				cell.setCellValue(summary.getDay8());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay8();
			}
			if(dayCount>8) {
				cellcount = 13;
				cell = row.createCell(13);
				cell.setCellValue(summary.getDay9());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay9();
			}
			if(dayCount>9) {
				cellcount = 14;
				cell = row.createCell(14);
				cell.setCellValue(summary.getDay10());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay10();
			}
			if(dayCount>10) {
				cellcount = 15;
				cell = row.createCell(15);
				cell.setCellValue(summary.getDay11());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay11();
			}
			if(dayCount>11) {
				cellcount = 16;
				cell = row.createCell(16);
				cell.setCellValue(summary.getDay12());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay12();
			}
			if(dayCount>12) {
				cellcount = 17;
				cell = row.createCell(17);
				cell.setCellValue(summary.getDay13());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay13();
			}
			if(dayCount>13) {
				cellcount = 18;
				cell = row.createCell(18);
				cell.setCellValue(summary.getDay14());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay14();
			}
			if(dayCount>14) {
				cellcount = 19;
				cell = row.createCell(19);
				cell.setCellValue(summary.getDay15());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay15();
			}
			if(dayCount>15) {
				cellcount = 20;
				cell = row.createCell(20);
				cell.setCellValue(summary.getDay16());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay16();
			}
			if(dayCount>16) {
				cellcount = 21;
				cell = row.createCell(21);
				cell.setCellValue(summary.getDay17());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay17();
			}
			if(dayCount>17) {
				cellcount = 22;
				cell = row.createCell(22);
				cell.setCellValue(summary.getDay18());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay18();
			}
			if(dayCount>18) {
				cellcount = 23;
				cell = row.createCell(23);
				cell.setCellValue(summary.getDay19());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay19();
			}
			if(dayCount>19) {
				cellcount = 24;
				cell = row.createCell(24);
				cell.setCellValue(summary.getDay20());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay20();
			}
			if(dayCount>20) {
				cellcount = 25;
				cell = row.createCell(25);
				cell.setCellValue(summary.getDay21());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay21();
			}
			if(dayCount>21) {
				cellcount = 26;
				cell = row.createCell(26);
				cell.setCellValue(summary.getDay22());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay22();
			}
			if(dayCount>22) {
				cellcount = 27;
				cell = row.createCell(27);
				cell.setCellValue(summary.getDay23());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay23();
			}
			if(dayCount>23) {
				cellcount = 28;
				cell = row.createCell(28);
				cell.setCellValue(summary.getDay24());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay24();
			}
			if(dayCount>24) {
				cellcount = 29;
				cell = row.createCell(29);
				cell.setCellValue(summary.getDay25());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay25();
			}
			if(dayCount>25) {
				cellcount = 30;
				cell = row.createCell(30);
				cell.setCellValue(summary.getDay26());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay26();
			}
			if(dayCount>26) {
				cellcount = 31;
				cell = row.createCell(31);
				cell.setCellValue(summary.getDay27());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay27();
			}
			if(dayCount>27) {
				cellcount = 32;
				cell = row.createCell(32);
				cell.setCellValue(summary.getDay28());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay28();
			}
			if(dayCount>28) {
				cellcount = 33;
				cell = row.createCell(33);
				cell.setCellValue(summary.getDay29());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay29();
			}
			if(dayCount>29) {
				cellcount = 34;
				cell = row.createCell(34);
				cell.setCellValue(summary.getDay30());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay30();
			}
			if(dayCount>30) {
				cellcount = 35;
				cell = row.createCell(35);
				cell.setCellValue(summary.getDay31());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay31();
			}
			 tmp_pjctName =summary.getProjectName();
			cell = row.createCell(cellcount+1);
			cell.setCellValue(totalHour);
			cell.setCellStyle(borderedCellStyle);
		}

		// Resize all columns to fit the content size
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		//Adding filter menu in column headers
		sheet.setAutoFilter(new CellRangeAddress(2, rowNum, 0, 4));

	}

	@Override
	public void exportBenchReport(Workbook workbook,Sheet sheet,ArrayList<String> colNames,String reportName,Integer monthIndex,Integer yearIndex,String reportType,Date startDate, Date endDate,int projectType,Long regionId) throws FileNotFoundException {

		String[] headers = new String[4];
		//headers[0] = "User Id";
		headers[0] = "Last Name";
		headers[1] = "First Name";
		headers[2] = "Cpp Level";
		headers[3] = "Bench Hour";
		int dayCount = colNames.size();

		int working_days =0;
		int holidays =0;
		int fullDayLeaveDays =0;
		int halfDayLeaveDays =0;
		double totalWorkingHours =0.0;
		double totalWorkedHours =0.0;
		double leaveHours =0.0;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		c.add(Calendar.DATE, 14);  // number of days to add
		Date end_date = c.getTime();

		double benchHour = 0.0;
		double total_hours =0.0;
		List<Object[]> userList = userRepository.getUserListByRegion(startDate,endDate,regionId);
		List<Object[]> Listdata = new ArrayList<>();

		for(Object[] item : userList) {
			Long id                  = ((BigInteger) item[0]).longValue();
			String firstName         = (String)item[1];
			String lastName          = (String) item[2];
			String cpplevel          = (String) item[5];
			Date joiningDate         = (Date) item[3];
			Date terminationDate     = (Date) item[4];


			List<Object[]> loggedData;

			if(reportType == "monthly") {

				//loggedData = timeTrackApprovalJPARepository.getTimeTrackApprovalDataByUserId(monthIndex, yearIndex, id);
				loggedData = taskTrackFinanceRepository.getTimeTrackApprovalDataByUserId(monthIndex, yearIndex, id,projectType);
				working_days = calculateWorkingDays(startDate,endDate);
				holidays = holidayRepository.getNationalHolidayListsByMonthRegion(startDate,endDate,regionId);
				fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,startDate,endDate);
				halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,startDate,endDate);
				if((startDate.compareTo(joiningDate) < 0)){
					working_days = calculateWorkingDays(joiningDate,endDate);
					holidays = holidayRepository.getNationalHolidayListsByMonthRegion(joiningDate,endDate,regionId);
					fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,joiningDate,endDate);
					halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,joiningDate,endDate);
				}
				if(terminationDate !=null){
					if((terminationDate.compareTo(endDate) < 0)){
						working_days = calculateWorkingDays(startDate,terminationDate);
						holidays = holidayRepository.getNationalHolidayListsByMonthRegion(startDate,terminationDate,regionId);
						fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,startDate,terminationDate);
						halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,startDate,terminationDate);
					}
				}

			}
			else {
				// loggedData = timeTrackApprovalJPARepository.getTimeTrackApprovalDataByUserIdMidMonth(monthIndex, yearIndex, id);
				loggedData = taskTrackFinanceRepository.getTimeTrackApprovalDataByUserIdMidMonth(monthIndex, yearIndex, id,2);
				working_days = calculateWorkingDays(startDate,end_date);
				holidays = holidayRepository.getNationalHolidayListsByMonthRegion(startDate,end_date,regionId);
				fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,startDate,end_date);
				halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,startDate,end_date);
				if((startDate.compareTo(joiningDate) < 0)){
					working_days = calculateWorkingDays(joiningDate,end_date);
					holidays = holidayRepository.getNationalHolidayListsByMonthRegion(joiningDate,end_date,regionId);
					fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,joiningDate,end_date);
					halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,joiningDate,end_date);
				}
				if(terminationDate !=null){
					if((terminationDate.compareTo(end_date) < 0)){
						working_days = calculateWorkingDays(startDate,terminationDate);
						holidays = holidayRepository.getNationalHolidayListsByMonthRegion(startDate,terminationDate,regionId);
						fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,startDate,terminationDate);
						halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,startDate,terminationDate);
					}
				}
			}

			totalWorkingHours = (working_days-holidays)*8;
			leaveHours = (fullDayLeaveDays*8)+(halfDayLeaveDays*4);
			totalWorkedHours = totalWorkingHours -leaveHours;


			for(Object[] items : loggedData) {

				if(items[1] != null)
				{
					double userHour = (double)items[1];
					 benchHour = totalWorkedHours-userHour;
					 if(benchHour<0.0) {
						 benchHour = 0.0;
					 }

				}
				else
				{
					 benchHour = totalWorkedHours;

				}

			}
			Listdata.add(new Object[]{id,firstName,lastName,cpplevel,benchHour});

		}


		//Removing grids
		sheet.setDisplayGridlines(false);
		//Freezing columns and rows from scrooling
		sheet.createFreezePane(0,3);

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
		titleCell.setCellValue(reportName);
		titleCell.setCellStyle(titleCellStyle);

		titleRow = sheet.createRow(1);
		titleCell = titleRow.createCell(1);
		titleCell.setCellValue("");

		XSSFFont font = (XSSFFont) workbook.createFont();
		font.setFontName("Liberation Sans");
		font.setFontHeightInPoints((short)10);
		font.setBold(true);

		// Header Cell Style
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.cloneStyleFrom(borderedCellStyle);
		headerCellStyle.setBorderTop(BorderStyle.THICK);
		headerCellStyle.setFont(font);

		Row headerRow = sheet.createRow(2);
		int widthInChars = 50;
		sheet.setColumnWidth(4, widthInChars);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with contacts data
		int rowNum = 3;
		ExportApprovalReportModel totalSummary = new ExportApprovalReportModel();
		for (Object[] summary : Listdata) {
			Row row = sheet.createRow(rowNum++);

			/*Cell cell = row.createCell(0);
			cell.setCellValue((Long) summary[0]);
			cell.setCellStyle(borderedCellStyle);*/

			Cell cell = row.createCell(0);
			cell.setCellValue((String) summary[2]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(1);
			cell.setCellValue((String) summary[1]);
			cell.setCellStyle(borderedCellStyle);

			
			cell = row.createCell(2);
			cell.setCellValue((String) summary[3]);
			cell.setCellStyle(borderedCellStyle);
			
			cell = row.createCell(3);
			cell.setCellValue((double) summary[4]);
			cell.setCellStyle(borderedCellStyle);


		}

		// Resize all columns to fit the content size
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		//Adding filter menu in column headers
		sheet.setAutoFilter(new CellRangeAddress(2, rowNum, 0, 2));

	}

	@Override
	public void exportSummaryReport(Workbook workbook,Sheet sheet,ArrayList<String> colNames,String reportName,Integer monthIndex,Integer yearIndex,String reportType,Date startDate, Date endDate,int projectType,Long regionId) throws Exception {

		String[] headers = new String[9];
		//headers[0] = "User Id";
		headers[0] = "Last Name";
		headers[1] = "First Name";
		//headers[3] = "Project";
		headers[2] = "Cpp Level";
		headers[3] = "Billable";
		headers[4] = "Non-Billable";
		headers[5] = "Overtime";
		headers[6] = "Beach";
		headers[7] = "Vacation";
		headers[8] = "Total";
		int dayCount = colNames.size();
		//int weekDays = 0;
		int working_days =0;
		int holidays =0;
		int fullDayLeaveDays =0;
		int halfDayLeaveDays =0;
		double totalWorkingHours =0.0;
		double totalWorkedHours =0.0;
		double leaveHours =0.0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		c.add(Calendar.DATE, 14);  // number of days to add
		Date end_date = c.getTime();


		double benchHour         = 0.0;
		double totalHour         = 0.0;
		double billableHour      = 0.0;
		double recbillableHour   = 0.0;
		double nonBillableHour   = 0.0;
		double overtimeHour      = 0.0;

		List<Object[]> userList = userRepository.getUserListByRegion(startDate,endDate,regionId);
		List<Object[]> Listdata = new ArrayList<>();

		for(Object[] item : userList) {
			Long id                  = ((BigInteger) item[0]).longValue();
			String firstName         = (String)item[1];
			String lastName          = (String) item[2];
			Date joiningDate         = (Date) item[3];
			Date terminationDate     = (Date) item[4];
			String cppLevel          = (String) item[5];
			//String projectName = "";
			List<Object[]> loggedData;
			List<Object[]> billable;
			List<Object[]> nonBillable;
			List<Object[]> overtime;

			/*List<AllocationModel> projectList;
			projectList = tasktrackRepository.getProjectNamesByMonth(id,startDate,endDate);
			if (!projectList.isEmpty()) {
				for(AllocationModel projects : projectList) {
					System.out.println(projects.getproject().getProjectName());
					projectName += projects.getproject().getProjectName() + ",";
				}


			}
			else
			{
				projectName = "Bench Project";
			}
			System.out.println("project list "+projectName);*/
			if(reportType == "monthly") {
				//loggedData  = timeTrackApprovalJPARepository.getTimeTrackApprovalDataByUserId(monthIndex, yearIndex, id);
				//billable    = timeTrackApprovalJPARepository.getBillableDataByUserId(monthIndex, yearIndex, id);
				//nonBillable = timeTrackApprovalJPARepository.getNonBillableDataByUserId(monthIndex, yearIndex, id);
				//overtime    = timeTrackApprovalJPARepository.getOvertimeDataByUserId(monthIndex, yearIndex, id);

				loggedData  = taskTrackFinanceRepository.getTimeTrackApprovalDataByUserId(monthIndex, yearIndex, id,projectType);
				billable    = taskTrackFinanceRepository.getBillableDataByUserId(monthIndex, yearIndex, id,projectType);
				nonBillable = taskTrackFinanceRepository.getNonBillableDataByUserId(monthIndex, yearIndex, id,projectType);
				overtime    = taskTrackFinanceRepository.getOvertimeDataByUserId(monthIndex, yearIndex, id,projectType);
				working_days = calculateWorkingDays(startDate,endDate);
				holidays = holidayRepository.getNationalHolidayListsByMonthRegion(startDate,endDate,regionId);
				fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,startDate,endDate);
				halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,startDate,endDate);
				if((startDate.compareTo(joiningDate) < 0)){
					working_days = calculateWorkingDays(joiningDate,endDate);
					holidays = holidayRepository.getNationalHolidayListsByMonthRegion(joiningDate,endDate,regionId);
					fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,joiningDate,endDate);
					halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,joiningDate,endDate);
				}
				if(terminationDate !=null){
					if((terminationDate.compareTo(endDate) < 0)){
						working_days = calculateWorkingDays(startDate,terminationDate);
						holidays = holidayRepository.getNationalHolidayListsByMonthRegion(startDate,terminationDate,regionId);
						fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,startDate,terminationDate);
						halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,startDate,terminationDate);

					}
				}



			}
			else {
				/*loggedData = timeTrackApprovalJPARepository.getTimeTrackApprovalDataByUserIdMidMonth(monthIndex, yearIndex, id);
				billable    = timeTrackApprovalJPARepository.getBillableDataByUserIdMidMonth(monthIndex, yearIndex, id);
				nonBillable = timeTrackApprovalJPARepository.getNonBillableDataByUserIdMidMonth(monthIndex, yearIndex, id);
				overtime    = timeTrackApprovalJPARepository.getOvertimeDataByUserIdMidMonth(monthIndex, yearIndex, id);*/
				loggedData = taskTrackFinanceRepository.getTimeTrackApprovalDataByUserIdMidMonth(monthIndex, yearIndex, id,projectType);
				billable    = taskTrackFinanceRepository.getBillableDataByUserIdMidMonth(monthIndex, yearIndex, id,projectType);
				nonBillable = taskTrackFinanceRepository.getNonBillableDataByUserIdMidMonth(monthIndex, yearIndex, id,projectType);
				overtime    = taskTrackFinanceRepository.getOvertimeDataByUserIdMidMonth(monthIndex, yearIndex, id,projectType);
				working_days = calculateWorkingDays(startDate,end_date);
				holidays = holidayRepository.getNationalHolidayListsByMonthRegion(startDate,end_date,regionId);
				fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,startDate,end_date);
				halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,startDate,end_date);
				if((startDate.compareTo(joiningDate) < 0)){
					working_days = calculateWorkingDays(joiningDate,end_date);
					holidays = holidayRepository.getNationalHolidayListsByMonthRegion(joiningDate,end_date,regionId);
					fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,joiningDate,end_date);
					halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,joiningDate,end_date);
				}
				if(terminationDate !=null){
					if((terminationDate.compareTo(end_date) < 0)){
						working_days = calculateWorkingDays(startDate,terminationDate);
						holidays = holidayRepository.getNationalHolidayListsByMonthRegion(startDate,terminationDate,regionId);
						fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,startDate,terminationDate);
						halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,startDate,terminationDate);

					}
				}
			}

			totalWorkingHours = (working_days-holidays)*8;
			leaveHours = (fullDayLeaveDays*8)+(halfDayLeaveDays*4);
			totalWorkedHours = totalWorkingHours -leaveHours;

			for(Object[] items : loggedData) {

				if(items[1] != null)
				{
					double userHour = (double)items[1];
					benchHour = totalWorkedHours-userHour;
					if(benchHour<0.0) {
						benchHour = 0.0;
					}

				}
				else
				{
					benchHour = totalWorkedHours;

				}



			}
			for(Object[] bill : billable) {
				if(bill[1] != null)
				{
					recbillableHour = (double)bill[1];
					if(recbillableHour > totalWorkedHours)
					{
						billableHour = totalWorkedHours;
					}
					else{

						billableHour = recbillableHour;
					}
				}
				else{
					billableHour = 0.0;
				}
			}

			for(Object[] nonbill : nonBillable) {
				if(nonbill[1] != null)
				{
					nonBillableHour = (double)nonbill[1];
				}
				else{
					nonBillableHour = 0.0;
				}
			}

			for(Object[] over : overtime) {
				if(over[1] != null)
				{
					if(recbillableHour > totalWorkedHours) {
						overtimeHour = (recbillableHour - totalWorkedHours)+(double) over[1];
					}
					else{
						overtimeHour = (double) over[1];
					}
				}
				else{
					overtimeHour = 0.0;
				}
			}


			totalHour = billableHour+nonBillableHour+overtimeHour+benchHour+leaveHours;
			Listdata.add(new Object[]{id,firstName,lastName,cppLevel,billableHour,nonBillableHour,overtimeHour,benchHour,leaveHours,totalHour});

		}


		//Removing grids
		sheet.setDisplayGridlines(false);
		//Freezing columns and rows from scrooling
		sheet.createFreezePane(0,3);

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
		titleCell.setCellValue(reportName);
		titleCell.setCellStyle(titleCellStyle);

		titleRow = sheet.createRow(1);
		titleCell = titleRow.createCell(1);
		titleCell.setCellValue("");

		XSSFFont font = (XSSFFont) workbook.createFont();
		font.setFontName("Liberation Sans");
		font.setFontHeightInPoints((short)10);
		font.setBold(true);

		// Header Cell Style
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.cloneStyleFrom(borderedCellStyle);
		headerCellStyle.setBorderTop(BorderStyle.THICK);
		headerCellStyle.setFont(font);

		Row headerRow = sheet.createRow(2);
		int widthInChars = 50;
		sheet.setColumnWidth(4, widthInChars);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with contacts data
		int rowNum = 3;
		ExportApprovalReportModel totalSummary = new ExportApprovalReportModel();
		for (Object[] summary : Listdata) {
			Row row = sheet.createRow(rowNum++);

			/*Cell cell = row.createCell(0);
			cell.setCellValue((Long) summary[0]);
			cell.setCellStyle(borderedCellStyle);*/

			Cell cell = row.createCell(0);
			cell.setCellValue((String) summary[2]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(1);
			cell.setCellValue((String) summary[1]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(2);
			cell.setCellValue((String) summary[3]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(3);
			cell.setCellValue((double) summary[4]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(4);
			cell.setCellValue((double) summary[5]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(5);
			cell.setCellValue((double) summary[6]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(6);
			cell.setCellValue((double) summary[7]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(7);
			cell.setCellValue((double) summary[8]);
			cell.setCellStyle(borderedCellStyle);
			
			cell = row.createCell(8);
			cell.setCellValue((double) summary[9]);
			cell.setCellStyle(borderedCellStyle);

		}

		// Resize all columns to fit the content size
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		//Adding filter menu in column headers
		sheet.setAutoFilter(new CellRangeAddress(2,rowNum , 0, 2));

	}



	/*private int countWeekendDays(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		// Note that month is 0-based in calendar, bizarrely.
		calendar.set(year, month - 1, 1);
		int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		int count = 0;
		for (int day = 1; day <= daysInMonth; day++) {
			calendar.set(year, month - 1, day);
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			if ((dayOfWeek == Calendar.SUNDAY) ||(dayOfWeek == Calendar.SATURDAY)) {
				count++;
				// Or do whatever you need to with the result.
			}
		}
		return count;
	}

	private int countMidWeekendDays(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		// Note that month is 0-based in calendar, bizarrely.
		calendar.set(year, month - 1, 1);
		int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		int count = 0;
		for (int day = 1; day <= 15; day++) {
			calendar.set(year, month - 1, day);
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			if ((dayOfWeek == Calendar.SUNDAY) ||(dayOfWeek == Calendar.SATURDAY)) {
				count++;
				// Or do whatever you need to with the result.
			}
		}
		return count;
	}*/

	public int calculateWorkingDays(Date startDate,Date endDate){

		int workingDays = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try
		{
			Calendar start = Calendar.getInstance();
			start.setTime(startDate);
			Calendar end = Calendar.getInstance();
			end.setTime(endDate);

			while(!start.after(end))
			{
				int day = start.get(Calendar.DAY_OF_WEEK);
				if ((day != Calendar.SATURDAY) && (day != Calendar.SUNDAY))
					workingDays++;
				start.add(Calendar.DATE, 1);
			}
			//System.out.println(workingDays);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return workingDays;
	}

	@Override
	public void exportLeaveReport(Workbook workbook,Sheet sheet,ArrayList<String> colNames,String reportName,Integer monthIndex,Integer yearIndex,Date startDate, Date endDate) throws ParseException {

		String[] headers = new String[5];
		headers[0] = "Name";
		headers[1] = "Date";
		headers[2] = "Leave Type";
		headers[3] = "Employee Type (FT / SC)";
		headers[4] = "Contractor";
//Removing grids
		sheet.setDisplayGridlines(false);
		//Freezing columns and rows from scrooling
		sheet.createFreezePane(0,3);

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
		titleCell.setCellValue(reportName);
		titleCell.setCellStyle(titleCellStyle);

		titleRow = sheet.createRow(1);
		titleCell = titleRow.createCell(1);
		titleCell.setCellValue("");

		XSSFFont font = (XSSFFont) workbook.createFont();
		font.setFontName("Liberation Sans");
		font.setFontHeightInPoints((short)10);
		font.setBold(true);

		// Header Cell Style
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.cloneStyleFrom(borderedCellStyle);
		headerCellStyle.setBorderTop(BorderStyle.THICK);
		headerCellStyle.setFont(font);

		Row headerRow = sheet.createRow(2);
		int widthInChars = 50;
		sheet.setColumnWidth(4, widthInChars);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with contacts data
		int rowNum = 3;
		ExportApprovalReportModel totalSummary = new ExportApprovalReportModel();
		//List<Object[]> userList = userLeaveSummaryRepository.getUserLeaveListByMonth(startDate,endDate);
		List<UserLeaveSummary> userList = userLeaveSummaryRepository.getUserLeaveListByMonth(startDate,endDate);
		//List<UserLeaveSummary> userList = userLeaveSummaryRepository.getUserLeaveListByMonthRegion(startDate,endDate,regionId);

		List<Object[]> Listdata = new ArrayList<>();
		for(UserLeaveSummary item : userList) {
			String name = item.getUser().getFirstName()+" "+item.getUser().getLastName();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String leaveDate = sdf.format(item.getLeaveDate());
			String leaveType = item.getLeaveType();
			String contractorName = " ";
			String employeeType = "FT";
			if(item.getUser().getContractor()!=null){
				contractorName = item.getUser().getContractor().getContractorName();
				employeeType = "SC";
			}

			Row row = sheet.createRow(rowNum++);

			/*Cell cell = row.createCell(0);
			cell.setCellValue((Long) summary[0]);
			cell.setCellStyle(borderedCellStyle);*/

			Cell cell = row.createCell(0);
			cell.setCellValue(name);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(1);
			cell.setCellValue(leaveDate);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(2);
			cell.setCellValue(leaveType);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(3);
			cell.setCellValue(employeeType);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(4);
			cell.setCellValue(contractorName);
			cell.setCellStyle(borderedCellStyle);

		}

		// Resize all columns to fit the content size
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		//Adding filter menu in column headers
		sheet.setAutoFilter(new CellRangeAddress(2,rowNum , 0, 4));
	}

	public void exportVacationReport(Workbook workrbook, Sheet sheet4, ArrayList<String> colNames, String nameofReport4,
									 int monthIndex, int yearIndex, String reportType, Date startDate, Date endDate,int projectType,Long regionId) {
		// TODO Auto-generated method stub
		String[] headers = new String[4];
		//headers[0] = "User Id";
		headers[0] = "Last Name";
		headers[1] = "First Name";
		headers[2] = "Cpp Level";
		headers[3] = "Vacation Hour";

		int working_days =0;
		int holidays =0;
		int fullDayLeaveDays =0;
		int halfDayLeaveDays =0;
		double totalWorkingHours =0.0;
		double totalWorkedHours =0.0;
		double leaveHours =0.0;
		double total_vacation_hours = 0.0;
		double holidayHours =0.0;
		double totalVacation =0.0;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		c.add(Calendar.DATE, 14);  // number of days to add
		Date end_date = c.getTime();

		double vacationHour = 0.0;
		double total_hours =0.0;
		List<Object[]> userList = userRepository.getUserListByRegion(startDate,endDate,regionId);
		List<Object[]> Listdata = new ArrayList<>();

		for(Object[] item : userList) {
			Long id                  = ((BigInteger) item[0]).longValue();
			String firstName         = (String)item[1];
			String lastName          = (String) item[2];
			String cpplevel          = (String) item[5];
			Date joiningDate         = (Date) item[3];
			Date terminationDate     = (Date) item[4];


			List<Object[]> loggedData;

			if(reportType == "monthly") {

				loggedData = timeTrackApprovalJPARepository.getTimeTrackApprovalDataByUserId(monthIndex, yearIndex, id,projectType);
				working_days = calculateWorkingDays(startDate,endDate);
				holidays = holidayRepository.getNationalHolidayListsByMonthRegion(startDate,endDate,regionId);
				fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,startDate,endDate);
				halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,startDate,endDate);
				if((startDate.compareTo(joiningDate) < 0)){
					working_days = calculateWorkingDays(joiningDate,endDate);
					holidays = holidayRepository.getNationalHolidayListsByMonthRegion(joiningDate,endDate,regionId);
					fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,joiningDate,endDate);
					halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,joiningDate,endDate);
				}
				if(terminationDate !=null){
					if((terminationDate.compareTo(endDate) < 0)){
						working_days = calculateWorkingDays(startDate,terminationDate);
						holidays = holidayRepository.getNationalHolidayListsByMonthRegion(startDate,terminationDate,regionId);
						fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,startDate,terminationDate);
						halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,startDate,terminationDate);
					}
				}

			}
			else {
				loggedData = timeTrackApprovalJPARepository.getTimeTrackApprovalDataByUserIdMidMonth(monthIndex, yearIndex, id);
				working_days = calculateWorkingDays(startDate,end_date);
				holidays = holidayRepository.getNationalHolidayListsByMonthRegion(startDate,end_date,regionId);
				fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,startDate,end_date);
				halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,startDate,end_date);
				if((startDate.compareTo(joiningDate) < 0)){
					working_days = calculateWorkingDays(joiningDate,end_date);
					holidays = holidayRepository.getNationalHolidayListsByMonthRegion(joiningDate,end_date,regionId);
					fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,joiningDate,end_date);
					halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,joiningDate,end_date);
				}
				if(terminationDate !=null){
					if((terminationDate.compareTo(end_date) < 0)){
						working_days = calculateWorkingDays(startDate,terminationDate);
						holidays = holidayRepository.getNationalHolidayListsByMonthRegion(startDate,terminationDate,regionId);
						fullDayLeaveDays = userLeaveSummaryRepository.getFullDayLeaveDays(id,startDate,terminationDate);
						halfDayLeaveDays = userLeaveSummaryRepository.getHalfDayLeaveDays(id,startDate,terminationDate);
					}
				}
			}

			totalWorkingHours = (working_days-holidays)*8;
			leaveHours = (fullDayLeaveDays*8)+(halfDayLeaveDays*4);
			//System.out.println("Holidays----------------------->"+holidays);
			//System.out.println("fulldayleaves----------------------->"+fullDayLeaveDays);
			//totalWorkedHours = totalWorkingHours -leaveHours;
			total_vacation_hours = (fullDayLeaveDays*8)+(halfDayLeaveDays*4);
			totalWorkedHours = totalWorkingHours - total_vacation_hours;
			holidayHours = holidays*8;
			totalVacation = total_vacation_hours + holidayHours;


			/*
			 * for(Object[] items : loggedData) {
			 *
			 * if(items[1] != null) { double userHour = (double)items[1]; vactaionHour =
			 * totalWorkedHours-userHour; if(vactaionHour<0.0) { vactaionHour = 0.0; }
			 *
			 * } else { vactaionHour = totalWorkedHours;
			 *
			 * }
			 *
			 * }
			 */
			Listdata.add(new Object[]{id,firstName,lastName,cpplevel,totalVacation});
			//System.out.println("Listdata------------------------------>"+Listdata.size());
		}
		//Removing grids
		sheet4.setDisplayGridlines(false);
		//Freezing columns and rows from scrooling
		sheet4.createFreezePane(0,3);

		//Bordered Cell Style
		CellStyle borderedCellStyle = workrbook.createCellStyle();
		borderedCellStyle.setBorderLeft(BorderStyle.THIN);
		borderedCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		borderedCellStyle.setBorderRight(BorderStyle.THIN);
		borderedCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		borderedCellStyle.setBorderTop(BorderStyle.THIN);
		borderedCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		borderedCellStyle.setBorderBottom(BorderStyle.THIN);
		borderedCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

		//Title Cell Style
		CellStyle titleCellStyle = workrbook.createCellStyle();
		//titleCellStyle.setFont((org.apache.poi.ss.usermodel.Font) headerFont);

		Row titleRow = sheet4.createRow(0);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue(nameofReport4);
		titleCell.setCellStyle(titleCellStyle);

		titleRow = sheet4.createRow(1);
		titleCell = titleRow.createCell(1);
		titleCell.setCellValue("");

		XSSFFont font = (XSSFFont) workrbook.createFont();
		font.setFontName("Liberation Sans");
		font.setFontHeightInPoints((short)10);
		font.setBold(true);

		// Header Cell Style
		CellStyle headerCellStyle = workrbook.createCellStyle();
		headerCellStyle.cloneStyleFrom(borderedCellStyle);
		headerCellStyle.setBorderTop(BorderStyle.THICK);
		headerCellStyle.setFont(font);

		Row headerRow = sheet4.createRow(2);
		int widthInChars = 50;
		sheet4.setColumnWidth(4, widthInChars);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with contacts data
		int rowNum = 3;
		ExportApprovalReportModel totalSummary = new ExportApprovalReportModel();
		for (Object[] summary : Listdata) {
			Row row = sheet4.createRow(rowNum++);

					/*Cell cell = row.createCell(0);
					cell.setCellValue((Long) summary[0]);
					cell.setCellStyle(borderedCellStyle);*/

			Cell cell = row.createCell(0);
			cell.setCellValue((String) summary[2]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(1);
			cell.setCellValue((String) summary[1]);
			cell.setCellStyle(borderedCellStyle);
			
			cell = row.createCell(2);
			cell.setCellValue((String) summary[3]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(3);
			cell.setCellValue((double) summary[4]);
			cell.setCellStyle(borderedCellStyle);


		}

		// Resize all columns to fit the content size
		for (int i = 0; i < headers.length; i++) {
			sheet4.autoSizeColumn(i);
		}

		//Adding filter menu in column headers
		sheet4.setAutoFilter(new CellRangeAddress(2, rowNum, 0, 2));
	}
	public void exportFinanceDataByProject(Workbook workbook,Sheet sheet,String reportName,Integer month,Integer year,Long projectId,String projectName) throws Exception{

		YearMonth yearMonthObject = YearMonth.of(year, month);
		int dayCount = yearMonthObject.lengthOfMonth();
		int cols = dayCount+3;
		String[] headers = new String[cols];
		headers[0] = "Name";
		headers[1] = "Project Name";


		String intmonth;
		if(month<10){
			intmonth ="0"+month;
		}
		else{
			intmonth =String.valueOf(month);
		}
		if(month<10){
			intmonth ="0"+month;
		}
		for(int i=1;i<=dayCount;i++){
			String j;
			if(i<10){
				j ="0"+i;
			}
			else{
				j =String.valueOf(i);
			}
			headers[i + 1] = year+"-"+intmonth+"-"+j;
		}
		headers[dayCount+2] ="Total Hours";

		List<Object[]> financeData = taskTrackFinanceRepository.getFinanceDataByProject(month, year, projectId);

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
		titleCell.setCellValue(reportName);
		titleCell.setCellStyle(titleCellStyle);

		titleRow = sheet.createRow(1);
		titleCell = titleRow.createCell(1);
		titleCell.setCellValue("");

		XSSFFont font = (XSSFFont) workbook.createFont();
		font.setFontName("Liberation Sans");
		font.setFontHeightInPoints((short)10);
		font.setBold(true);


		// Header Cell Style
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.cloneStyleFrom(borderedCellStyle);
		headerCellStyle.setBorderTop(BorderStyle.THICK);
		headerCellStyle.setFont(font);

		Row headerRow = sheet.createRow(2);
		int widthInChars = 50;
		sheet.setColumnWidth(4, widthInChars);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with contacts data
		int rowNum = 3;

		for (Object[] summary : financeData) {
			double totalHour =0.0;
			Row row = sheet.createRow(rowNum++);

			Cell cell = row.createCell(0);
			cell.setCellValue((String) summary[1]+" "+(String) summary[2]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(1);
			cell.setCellValue(projectName);
			cell.setCellStyle(borderedCellStyle);

			for(int d=1;d<=dayCount;d++) {
				cell = row.createCell(d+1);
				cell.setCellValue((double)summary[d+3]);
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+(double)summary[d+3];
			}
			cell = row.createCell(dayCount+2);
			cell.setCellValue(totalHour);
			cell.setCellStyle(borderedCellStyle);

		}


		// Resize all columns to fit the content size
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		//Adding filter menu in column headers
		sheet.setAutoFilter(new CellRangeAddress(2, rowNum, 0, 1));


	}
    //Renjith
	public void exportFinanceDataByProjectSet(Workbook workbook,Sheet sheet,String reportName,Integer month,Integer year,Set<Long> prjSet) throws Exception{

		YearMonth yearMonthObject = YearMonth.of(year, month);
		int dayCount = yearMonthObject.lengthOfMonth();
		int cols = dayCount+3;
		String[] headers = new String[cols];
		headers[0] = "Name";
		headers[1] = "Project Name";


		String intmonth;
		if(month<10){
			intmonth ="0"+month;
		}
		else{
			intmonth =String.valueOf(month);
		}
		if(month<10){
			intmonth ="0"+month;
		}
		for(int i=1;i<=dayCount;i++){
			String j;
			if(i<10){
				j ="0"+i;
			}
			else{
				j =String.valueOf(i);
			}
			headers[i + 1] = year+"-"+intmonth+"-"+j;
		}
		headers[dayCount+2] ="Total Hours";

		List<Object[]> financeData = taskTrackFinanceRepository.getFinanceDataByProjectSet(month, year, prjSet);

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
		titleCell.setCellValue(reportName);
		titleCell.setCellStyle(titleCellStyle);

		titleRow = sheet.createRow(1);
		titleCell = titleRow.createCell(1);
		titleCell.setCellValue("");

		XSSFFont font = (XSSFFont) workbook.createFont();
		font.setFontName("Liberation Sans");
		font.setFontHeightInPoints((short)10);
		font.setBold(true);


		// Header Cell Style
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.cloneStyleFrom(borderedCellStyle);
		headerCellStyle.setBorderTop(BorderStyle.THICK);
		headerCellStyle.setFont(font);

		Row headerRow = sheet.createRow(2);
		int widthInChars = 50;
		sheet.setColumnWidth(4, widthInChars);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with contacts data
		int rowNum = 3;

		for (Object[] summary : financeData) {
			double totalHour =0.0;
			Row row = sheet.createRow(rowNum++);

			Cell cell = row.createCell(0);
			cell.setCellValue((String) summary[1]+" "+(String) summary[2]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(1);
			cell.setCellValue(summary[0]+"");
			cell.setCellStyle(borderedCellStyle);

			for(int d=1;d<=dayCount;d++) {
				cell = row.createCell(d+1);
				cell.setCellValue((double)summary[d+3]);
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+(double)summary[d+3];
			}
			cell = row.createCell(dayCount+2);
			cell.setCellValue(totalHour);
			cell.setCellStyle(borderedCellStyle);

		}


		// Resize all columns to fit the content size
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		//Adding filter menu in column headers
		sheet.setAutoFilter(new CellRangeAddress(2, rowNum, 0, 1));


	}

	//Renjith
	public void exportFinanceDataByUser(Workbook workbook,Sheet sheet,String reportName,Integer month,Integer year,Long userId,String userName) throws Exception{

		YearMonth yearMonthObject = YearMonth.of(year, month);
		int dayCount = yearMonthObject.lengthOfMonth();
		int cols = dayCount+3;
		String[] headers = new String[cols];
		headers[0] = "Name";
		headers[1] = "Project Name";

		String intmonth;
		if(month<10){
			intmonth ="0"+month;
		}
		else{
			intmonth =String.valueOf(month);
		}
		if(month<10){
			intmonth ="0"+month;
		}
		for(int i=1;i<=dayCount;i++){
			String j;
			if(i<10){
				j ="0"+i;
			}
			else{
				j =String.valueOf(i);
			}
			headers[i + 1] = year+"-"+intmonth+"-"+j;
		}
		headers[dayCount+2]="Total Hours";
		List<Object[]> financeData = taskTrackFinanceRepository.getFinanceDataByUser(month, year, userId);

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
		titleCell.setCellValue(reportName);
		titleCell.setCellStyle(titleCellStyle);

		titleRow = sheet.createRow(1);
		titleCell = titleRow.createCell(1);
		titleCell.setCellValue("");

		XSSFFont font = (XSSFFont) workbook.createFont();
		font.setFontName("Liberation Sans");
		font.setFontHeightInPoints((short)10);
		font.setBold(true);


		// Header Cell Style
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.cloneStyleFrom(borderedCellStyle);
		headerCellStyle.setBorderTop(BorderStyle.THICK);
		headerCellStyle.setFont(font);

		Row headerRow = sheet.createRow(2);
		int widthInChars = 50;
		sheet.setColumnWidth(4, widthInChars);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with contacts data
		int rowNum = 3;

		for (Object[] summary : financeData) {

			double totalHour =0.0;
			Row row = sheet.createRow(rowNum++);

			Cell cell = row.createCell(0);
			cell.setCellValue(userName);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(1);
			cell.setCellValue((String) summary[1]);
			cell.setCellStyle(borderedCellStyle);

			for(int d=1;d<=dayCount;d++) {
				cell = row.createCell(d+1);
				cell.setCellValue((double)summary[d+2]);
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+(double)summary[d+2];
			}
			cell = row.createCell(dayCount+2);
			cell.setCellValue(totalHour);
			cell.setCellStyle(borderedCellStyle);


		}


		// Resize all columns to fit the content size
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		//Adding filter menu in column headers
		sheet.setAutoFilter(new CellRangeAddress(2, rowNum, 0, 1));
	}

	public void exportFinanceDataByUserAndProject(Workbook workbook,Sheet sheet,String reportName,Integer month,Integer year,Long userId,Long projectId) throws Exception{
		YearMonth yearMonthObject = YearMonth.of(year, month);
		int dayCount = yearMonthObject.lengthOfMonth();
		int cols = dayCount+3;
		String[] headers = new String[cols];
		headers[0] = "Name";
		headers[1] = "Project Name";

		String intmonth;
		if(month<10){
			intmonth ="0"+month;
		}
		else{
			intmonth =String.valueOf(month);
		}
		if(month<10){
			intmonth ="0"+month;
		}
		for(int i=1;i<=dayCount;i++){
			String j;
			if(i<10){
				j ="0"+i;
			}
			else{
				j =String.valueOf(i);
			}
			headers[i + 1] = year+"-"+intmonth+"-"+j;
		}
		headers[dayCount+2] = "Total Hours";
		List<Object[]> financeData = taskTrackFinanceRepository.getFinanceDataByUserAndProject(month, year, userId,projectId);

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
		titleCell.setCellValue(reportName);
		titleCell.setCellStyle(titleCellStyle);

		titleRow = sheet.createRow(1);
		titleCell = titleRow.createCell(1);
		titleCell.setCellValue("");

		XSSFFont font = (XSSFFont) workbook.createFont();
		font.setFontName("Liberation Sans");
		font.setFontHeightInPoints((short)10);
		font.setBold(true);


		// Header Cell Style
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.cloneStyleFrom(borderedCellStyle);
		headerCellStyle.setBorderTop(BorderStyle.THICK);
		headerCellStyle.setFont(font);

		Row headerRow = sheet.createRow(2);
		int widthInChars = 50;
		sheet.setColumnWidth(4, widthInChars);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with contacts data
		int rowNum = 3;

		for (Object[] summary : financeData) {

			double totalHour =0.0;
			Row row = sheet.createRow(rowNum++);

			Cell cell = row.createCell(0);
			cell.setCellValue((String)summary[3]+" "+(String)summary[4]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(1);
			cell.setCellValue((String) summary[1]);
			cell.setCellStyle(borderedCellStyle);

			for(int d=1;d<=dayCount;d++) {
				cell = row.createCell(d+1);
				cell.setCellValue((double)summary[d+5]);
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+(double)summary[d+5];
			}
			cell = row.createCell(dayCount+2);
			cell.setCellValue(totalHour);
			cell.setCellStyle(borderedCellStyle);


		}


		// Resize all columns to fit the content size
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		//Adding filter menu in column headers
		sheet.setAutoFilter(new CellRangeAddress(2, rowNum, 0, 1));

	}

	@Override
	public void exportFinanceDataByMonthAndYear(Workbook workrbook, Sheet sheet, String nameofReport, int month,
			int year) {
		// TODO Auto-generated method stub
		YearMonth yearMonthObject = YearMonth.of(year, month);
		
		int dayCount = yearMonthObject.lengthOfMonth();
		int cols = dayCount+3;
		String[] headers = new String[cols];
		headers[0] = "Name";
		headers[1] = "Project Name";

		String intmonth;
		if(month<10){
			intmonth ="0"+month;
		}
		else{
			intmonth =String.valueOf(month);
		}
		if(month<10){
			intmonth ="0"+month;
		}
		for(int i=1;i<=dayCount;i++){
			String j;
			if(i<10){
				j ="0"+i;
			}
			else{
				j =String.valueOf(i);
			}
			headers[i + 1] = year+"-"+intmonth+"-"+j;
		}
		headers[dayCount+2] = "Total Hours";
		List<Object[]> financeData = taskTrackFinanceRepository.getFinanceDataByMonthYear(month, year);

		
		
		//Removing grids
		sheet.setDisplayGridlines(false);
		//Freezing columns and rows from scrooling
		sheet.createFreezePane(3,3);

		//Bordered Cell Style
		CellStyle borderedCellStyle = workrbook.createCellStyle();
		borderedCellStyle.setBorderLeft(BorderStyle.THIN);
		borderedCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		borderedCellStyle.setBorderRight(BorderStyle.THIN);
		borderedCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		borderedCellStyle.setBorderTop(BorderStyle.THIN);
		borderedCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		borderedCellStyle.setBorderBottom(BorderStyle.THIN);
		borderedCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

		//Title Cell Style
		CellStyle titleCellStyle = workrbook.createCellStyle();
		//titleCellStyle.setFont((org.apache.poi.ss.usermodel.Font) headerFont);

		Row titleRow = sheet.createRow(0);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue(nameofReport);
		titleCell.setCellStyle(titleCellStyle);

		titleRow = sheet.createRow(1);
		titleCell = titleRow.createCell(1);
		titleCell.setCellValue("");

		XSSFFont font = (XSSFFont) workrbook.createFont();
		font.setFontName("Liberation Sans");
		font.setFontHeightInPoints((short)10);
		font.setBold(true);


		// Header Cell Style
		CellStyle headerCellStyle = workrbook.createCellStyle();
		headerCellStyle.cloneStyleFrom(borderedCellStyle);
		headerCellStyle.setBorderTop(BorderStyle.THICK);
		headerCellStyle.setFont(font);

		Row headerRow = sheet.createRow(2);
		int widthInChars = 50;
		sheet.setColumnWidth(4, widthInChars);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with contacts data
		int rowNum = 3;
		
		for (Object[] summary : financeData) {
			
			double totalHour =0.0;
			Row row = sheet.createRow(rowNum++);

			Cell cell = row.createCell(0);
			cell.setCellValue((String)summary[1]+" "+(String)summary[2]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(1);
			cell.setCellValue(summary[4].toString());
			cell.setCellStyle(borderedCellStyle);

			for(int d=1;d<=dayCount;d++) {
				
				cell = row.createCell(d+1);
				cell.setCellValue((double)summary[d+4]);
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+(double)summary[d+4];
			}
			cell = row.createCell(dayCount+2);
			cell.setCellValue(totalHour);
			cell.setCellStyle(borderedCellStyle);
			

		}

		
		// Resize all columns to fit the content size
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		//Adding filter menu in column headers
		sheet.setAutoFilter(new CellRangeAddress(2, rowNum, 0, 1));
	}

	@Override
	public void exportBenchReport(Workbook workrbook, Sheet sheet, String nameofReport,
			List<BenchProjectReportModel> benchProjectReport) {
		// TODO Auto-generated method stub
		
		String[] headers = new String[3];
		headers[0] = "Name";
		headers[1] = "Department";
		headers[2] = "Bench Time";
		
		//Removing grids
		sheet.setDisplayGridlines(false);
		//Freezing columns and rows from scrooling
		sheet.createFreezePane(3,3);

		//Bordered Cell Style
		CellStyle borderedCellStyle = workrbook.createCellStyle();
		borderedCellStyle.setBorderLeft(BorderStyle.THIN);
		borderedCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		borderedCellStyle.setBorderRight(BorderStyle.THIN);
		borderedCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		borderedCellStyle.setBorderTop(BorderStyle.THIN);
		borderedCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		borderedCellStyle.setBorderBottom(BorderStyle.THIN);
		borderedCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

		//Title Cell Style
		CellStyle titleCellStyle = workrbook.createCellStyle();
		//titleCellStyle.setFont((org.apache.poi.ss.usermodel.Font) headerFont);

		Row titleRow = sheet.createRow(0);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue(nameofReport);
		titleCell.setCellStyle(titleCellStyle);

		titleRow = sheet.createRow(1);
		titleCell = titleRow.createCell(1);
		titleCell.setCellValue("");

		XSSFFont font = (XSSFFont) workrbook.createFont();
		font.setFontName("Liberation Sans");
		font.setFontHeightInPoints((short)10);
		font.setBold(true);


		// Header Cell Style
		CellStyle headerCellStyle = workrbook.createCellStyle();
		headerCellStyle.cloneStyleFrom(borderedCellStyle);
		headerCellStyle.setBorderTop(BorderStyle.THICK);
		headerCellStyle.setFont(font);

		Row headerRow = sheet.createRow(2);
		int widthInChars = 50;
		sheet.setColumnWidth(4, widthInChars);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with contacts data
		int rowNum = 3;
		
		for (BenchProjectReportModel summary : benchProjectReport) {
			
			
			Row row = sheet.createRow(rowNum++);

			Cell cell = row.createCell(0);
			cell.setCellValue(summary.getUserName());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(1);
			cell.setCellValue(summary.getDepartmentName());
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(2);
			cell.setCellValue(summary.getAllocatedPerce());
			cell.setCellStyle(borderedCellStyle);

		}

		
		// Resize all columns to fit the content size
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		//Adding filter menu in column headers
		sheet.setAutoFilter(new CellRangeAddress(2, rowNum, 0, 1));
		
	}

}

