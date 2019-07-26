package com.EMS.service;


import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.ExportApprovalReportModel;
import com.EMS.model.ExportProjectHourReportModel;
import com.EMS.model.ExportProjectTaskReportModel;
import com.EMS.model.TaskTrackApproval;

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

@Service
public class ProjectExportServiceImpl implements ProjectExportService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	TimeTrackApprovalJPARepository timeTrackApprovalJPARepository;


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
					(obj.getId(),obj.getProjectName(),obj.getFirstName(),obj.getLastName()
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
		titleCell.setCellValue("PROJECT APPROVAL REPORT");
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
		int cols = dayCount+5;
		String[] headers = new String[cols];
		headers[0] = "User Id";
		headers[1] = "Last Name";
		headers[2] = "First Name";
		headers[3] = "Project Name";
		for(int i=0;i<dayCount;i++) {
			headers[i+4] = colNames.get(i);

		}
		headers[dayCount+4] = "Total Hours";
		List<ExportApprovalReportModel> Listdata = new ArrayList<ExportApprovalReportModel>();

		for(ExportApprovalReportModel obj : data) {
			Listdata.add(new ExportApprovalReportModel
					(obj.getId(),obj.getProjectName(),obj.getFirstName(),obj.getLastName()
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
			int cellcount = 3;
			double totalHour = 0.0;
			if(dayCount>0) {
				cellcount = 4;
				cell = row.createCell(4);
				cell.setCellValue(summary.getDay1());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay1();
			}
			if(dayCount>1) {
				cellcount = 5;
				cell = row.createCell(5);
				cell.setCellValue(summary.getDay2());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay2();
			}
			if(dayCount>2) {
				cellcount = 6;
				cell = row.createCell(6);
				cell.setCellValue(summary.getDay3());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay3();
			}
			if(dayCount>3) {
				cellcount = 7;
				cell = row.createCell(7);
				cell.setCellValue(summary.getDay4());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay4();
			}
			if(dayCount>4) {
				cellcount = 8;
				cell = row.createCell(8);
				cell.setCellValue(summary.getDay5());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay5();
			}
			if(dayCount>5) {
				cellcount = 9;
				cell = row.createCell(9);
				cell.setCellValue(summary.getDay6());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay6();
			}
			if(dayCount>6) {
				cellcount = 10;
				cell = row.createCell(10);
				cell.setCellValue(summary.getDay7());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay7();
			}
			if(dayCount>7) {
				cellcount = 11;
				cell = row.createCell(11);
				cell.setCellValue(summary.getDay8());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay8();
			}
			if(dayCount>8) {
				cellcount = 12;
				cell = row.createCell(12);
				cell.setCellValue(summary.getDay9());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay9();
			}
			if(dayCount>9) {
				cellcount = 13;
				cell = row.createCell(13);
				cell.setCellValue(summary.getDay10());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay10();
			}
			if(dayCount>10) {
				cellcount = 14;
				cell = row.createCell(14);
				cell.setCellValue(summary.getDay11());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay11();
			}
			if(dayCount>11) {
				cellcount = 15;
				cell = row.createCell(15);
				cell.setCellValue(summary.getDay12());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay12();
			}
			if(dayCount>12) {
				cellcount = 16;
				cell = row.createCell(16);
				cell.setCellValue(summary.getDay13());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay13();
			}
			if(dayCount>13) {
				cellcount = 17;
				cell = row.createCell(17);
				cell.setCellValue(summary.getDay14());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay14();
			}
			if(dayCount>14) {
				cellcount = 18;
				cell = row.createCell(18);
				cell.setCellValue(summary.getDay15());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay15();
			}
			if(dayCount>15) {
				cellcount = 19;
				cell = row.createCell(19);
				cell.setCellValue(summary.getDay16());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay16();
			}
			if(dayCount>16) {
				cellcount = 20;
				cell = row.createCell(20);
				cell.setCellValue(summary.getDay17());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay17();
			}
			if(dayCount>17) {
				cellcount = 21;
				cell = row.createCell(21);
				cell.setCellValue(summary.getDay18());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay18();
			}
			if(dayCount>18) {
				cellcount = 22;
				cell = row.createCell(22);
				cell.setCellValue(summary.getDay19());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay19();
			}
			if(dayCount>19) {
				cellcount = 23;
				cell = row.createCell(23);
				cell.setCellValue(summary.getDay20());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay20();
			}
			if(dayCount>20) {
				cellcount = 24;
				cell = row.createCell(24);
				cell.setCellValue(summary.getDay21());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay21();
			}
			if(dayCount>21) {
				cellcount = 25;
				cell = row.createCell(25);
				cell.setCellValue(summary.getDay22());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay22();
			}
			if(dayCount>22) {
				cellcount = 26;
				cell = row.createCell(26);
				cell.setCellValue(summary.getDay23());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay23();
			}
			if(dayCount>23) {
				cellcount = 27;
				cell = row.createCell(27);
				cell.setCellValue(summary.getDay24());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay24();
			}
			if(dayCount>24) {
				cellcount = 28;
				cell = row.createCell(28);
				cell.setCellValue(summary.getDay25());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay25();
			}
			if(dayCount>25) {
				cellcount = 29;
				cell = row.createCell(29);
				cell.setCellValue(summary.getDay26());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay26();
			}
			if(dayCount>26) {
				cellcount = 30;
				cell = row.createCell(30);
				cell.setCellValue(summary.getDay27());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay27();
			}
			if(dayCount>27) {
				cellcount = 31;
				cell = row.createCell(31);
				cell.setCellValue(summary.getDay28());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay28();
			}
			if(dayCount>28) {
				cellcount = 32;
				cell = row.createCell(32);
				cell.setCellValue(summary.getDay29());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay29();
			}
			if(dayCount>29) {
				cellcount = 33;
				cell = row.createCell(33);
				cell.setCellValue(summary.getDay30());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay30();
			}
			if(dayCount>30) {
				cellcount = 34;
				cell = row.createCell(34);
				cell.setCellValue(summary.getDay31());
				cell.setCellStyle(borderedCellStyle);
				totalHour =totalHour+summary.getDay31();
			}

			cell = row.createCell(cellcount+1);
			cell.setCellValue(totalHour);
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
	public void exportBenchReport(Workbook workbook,Sheet sheet,ArrayList<String> colNames,String reportName,Integer monthIndex,Integer yearIndex,String reportType) throws FileNotFoundException {

		String[] headers = new String[4];
		headers[0] = "User Id";
		headers[1] = "Last Name";
		headers[2] = "First Name";
		headers[3] = "Bench Hour";
		int dayCount = colNames.size();
		int weekDays = 0;
		if(reportType == "monthly") {
			 weekDays = countWeekendDays(yearIndex, monthIndex);
		}
		else
		{
			weekDays = countMidWeekendDays(yearIndex, monthIndex);
		}
		int workingDays = dayCount - weekDays;
		int totalHours = workingDays*8;
		double totalWorkingHours = totalHours;
		double benchHour = 0.0;
		List<Object[]> userList = userRepository.getUserList();
		List<Object[]> Listdata = new ArrayList<>();

		for(Object[] item : userList) {
			Long id = ((BigInteger) item[0]).longValue();
			String firstName = (String)item[1];
			String lastName = (String) item[2];
			List<Object[]> loggedData;
			if(reportType == "monthly") {
				loggedData = timeTrackApprovalJPARepository.getTimeTrackApprovalDataByUserId(monthIndex, yearIndex, id);
			}
			else {
				 loggedData = timeTrackApprovalJPARepository.getTimeTrackApprovalDataByUserIdMidMonth(monthIndex, yearIndex, id);

			}
			for(Object[] items : loggedData) {

				if(items[1] != null)
				{
					double userHour = (double)items[1];
					 benchHour = totalWorkingHours-userHour;
					 if(benchHour<0.0) {
						 benchHour = 0.0;
					 }

				}
				else
				{
					 benchHour = totalWorkingHours;

				}

			}
			Listdata.add(new Object[]{id,firstName,lastName,benchHour});

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
		ExportApprovalReportModel totalSummary = new ExportApprovalReportModel();
		for (Object[] summary : Listdata) {
			Row row = sheet.createRow(rowNum++);

			Cell cell = row.createCell(0);
			cell.setCellValue((Long) summary[0]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(1);
			cell.setCellValue((String) summary[2]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(2);
			cell.setCellValue((String) summary[1]);
			cell.setCellStyle(borderedCellStyle);

			cell = row.createCell(3);
			cell.setCellValue((double) summary[3]);
			cell.setCellStyle(borderedCellStyle);


		}

		// Resize all columns to fit the content size
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		//Adding filter menu in column headers
		sheet.setAutoFilter(new CellRangeAddress(2, 13, 1, 4));

	}

	private int countWeekendDays(int year, int month) {
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
	}

}
