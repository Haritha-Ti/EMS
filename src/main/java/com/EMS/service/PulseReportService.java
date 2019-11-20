package com.EMS.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.AllocationModel;
import com.EMS.model.ClientModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.Technology;
import com.EMS.model.UserModel;

@Service
public class PulseReportService {

	@Autowired
	ProjectAllocationService projectAllocationservice;

	@Autowired
	UserService userservice;

	@Autowired
	ProjectService projectservice;

	public int generateReport(Workbook userbook, String[] pReportHeading, Sheet pulsedata,String startdate,String enddate) {

		Sheet sheetstyle = setsheetStyle(pulsedata, pReportHeading);
		Font normalFont = setnormalfont(userbook);
		CellStyle normalstyle = setnormalstyle(userbook);
		normalstyle.setFont(normalFont);

		CellStyle headerstyle = userbook.createCellStyle();

		Font hFont = setdataheadfont(userbook);
		CellStyle headstyle = setnormalstyle(userbook);
		headstyle.setFont(hFont);

		CellStyle datestyle = setnormaldatestyle(userbook);

		headerstyle.cloneStyleFrom(normalstyle);
		headerstyle.setFont(hFont);

		Row pulserow0 = pulsedata.createRow(0);
		Cell pulsecell0 = createcell(pulserow0, 0, normalstyle);
		pulsecell0.setCellValue("Ti Technology (India)-  Pulse as of "+startdate+" to "+enddate);
		pulsecell0.setCellStyle(headerstyle);

		LocalDate startDate=LocalDate.parse(startdate);
		LocalDate endDate=LocalDate.parse(enddate);
		
		Row pulserow1 = pulsedata.createRow(3);
		for (int i = 0; i < pReportHeading.length; i++) {
			Cell pulseheadcell = createcell(pulserow1, i, headstyle);
			pulseheadcell.setCellValue(pReportHeading[i]);
		}

		List<ProjectModel> projectlist = projectservice.getListofProjects();
		//System.out.println("project :" + projectlist.size());
		int rownum = 4;
		int count=0;
		for (ProjectModel project : projectlist) {
			UserModel projectOwner = userservice.getUserdetailsbyId(project.getProjectOwner().getUserId());

			ClientModel projectClient = new ClientModel();
			if (project.getprojectType() == 0)
				projectClient = projectservice.getClientName(project.getClientName().getClientId());

			List<AllocationModel> allocationlist = projectAllocationservice.getAllocationListonDate(project.getProjectId(),startDate,endDate);
			//System.out.println("allocation list size :" + allocationlist.size());

			count = 0;
			if (!allocationlist.isEmpty()) {
				int startRow=rownum+1;
				
				for (AllocationModel alloc : allocationlist) {
					UserModel user = (UserModel) userservice.getUserdetailsbyId(alloc.getuser().getUserId());

					Row datarow = pulsedata.createRow(rownum++);
					Cell cell0 = createcell(datarow, 0, normalstyle);
					cell0.setCellValue(user.getFirstName() + " " + user.getLastName());

					Cell cell1 = createcell(datarow, 1, datestyle);

					Date date1 = (Date) user.getJoiningDate();
					cell1.setCellValue(date1);

					Cell cell2 = createcell(datarow, 2, normalstyle);
					cell2.setCellValue(user.getEmploymentType());

					Cell cell3 = createcell(datarow, 3, normalstyle);
					cell3.setCellValue(projectClient.getClientName());

					Cell cell4 = createcell(datarow, 4, normalstyle);
					cell4.setCellValue(project.getProjectName());

					Cell cell5 = createcell(datarow, 5, normalstyle);
					cell5.setCellValue(projectOwner.getFirstName() + " " + projectOwner.getLastName());

					Cell cell6 = createcell(datarow, 6, normalstyle);
					cell6.setCellValue(projectClient.getClientCountry());

					Cell cell7 = createcell(datarow, 7, normalstyle);
					cell7.setCellValue(user.getCppLevel());

					Cell cell8 = createcell(datarow, 8, datestyle);
					Date date2 = (Date) project.getStartDate();
					cell8.setCellValue(date2);

					Cell cell9 = createcell(datarow, 9, datestyle);
					Date date3 = (Date) project.getEndDate();
					cell9.setCellValue(date3);

					Cell cell10 = createcell(datarow, 10, normalstyle);
					cell10.setCellValue(project.getisBillable());

					Cell cell11 = createcell(datarow, 11, normalstyle);
					cell11.setCellValue(200);

					Cell cell12 = createcell(datarow, 12, normalstyle);
					cell12.setCellValue(125);

					Cell cell13 = createcell(datarow, 13, normalstyle);
					String formula6="+L"+rownum+"-M"+rownum;
					cell13.setCellFormula(formula6);

					Cell cell14 = createcell(datarow, 14, normalstyle);
					String formula7="IF(N"+rownum+"<0,0,N"+rownum+"/L"+rownum+")";
					cell14.setCellFormula(formula7);

					List<Technology> techList = userservice.getprimarySkills(user.getUserId());
					Cell cell15 = createcell(datarow, 15, normalstyle);

					String skills = null;
					for (Technology obj : techList) {
						if (skills == null) {
							skills = obj.getTechnologyName();
						} else {
							skills = skills + "," + obj.getTechnologyName();
						}
					}
					cell15.setCellValue(skills);

					Cell cell16 = createcell(datarow, 16, normalstyle);
					String formula="+L"+rownum+"/8";
					cell16.setCellFormula(formula);

					Cell cell17 = createcell(datarow, 17, normalstyle);
					cell17.setCellFormula("+Q" + rownum + "/summary!M5");
					count++;
				}
				int endRow=rownum;
				count = rownum + (--count);
				Row datarow = pulsedata.createRow(rownum++);
				datarow.setRowStyle(headstyle);
				Cell cell0 = createcell(datarow, 0, headerstyle);
				cell0.setCellValue("Total");

				pulsedata.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, 1, 1));
				pulsedata.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, 2, 2));
				pulsedata.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, 0, 0));

				Cell cell1 = createcell(datarow, 1, headerstyle);
				String total="COUNTA(A"+startRow+":A"+endRow+")";
				cell1.setCellFormula(total);
				pulsedata.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, 3, 10));
				
				Cell cell11 = createcell(datarow, 11, headerstyle);
				String formula1="SUM(L"+startRow+":L"+endRow+")";
				cell11.setCellFormula(formula1);
				pulsedata.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, 11, 11));

				Cell cell12 = createcell(datarow, 12, headerstyle);
				String formula2="SUM(M"+startRow+":M"+endRow+")";
				cell12.setCellFormula(formula2);
				pulsedata.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, 12, 12));

				Cell cell13 = createcell(datarow, 13, headerstyle);
				String formula3="SUM(N"+startRow+":N"+endRow+")";
				cell13.setCellFormula(formula3);
				pulsedata.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, 13, 13));

				Cell cell14 = createcell(datarow, 14, headerstyle);
				String formula4="IF(L"+rownum+"=0,0,N"+rownum+"/L"+rownum+")";
				cell14.setCellFormula(formula4);

				pulsedata.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, 14, 14));
				pulsedata.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, 15, 15));
				pulsedata.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, 16, 16));
				pulsedata.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, 17, 17));

				datarow = pulsedata.createRow(rownum++);
				Cell cell = createcell(datarow, 0, normalstyle);
			}

		}
		
		rownum=rownum+4;

		Row datarow0 = pulsedata.createRow(rownum++);
		Cell cell6 = createcell(datarow0, 0, headerstyle);
		cell6.setCellValue("Contract");
		Cell cell7 = createcell(datarow0, 1, headerstyle);
		String formula9="COUNTIF(C5:C"+(rownum-1)+",\"contract\")";
		cell7.setCellFormula(formula9);
		
		Row datarow = pulsedata.createRow(rownum++);
		Cell cell0 = createcell(datarow, 0, headerstyle);
		cell0.setCellValue("Term");
		
		Cell cell1 = createcell(datarow, 1, headerstyle);
		String formula10="COUNTIF(C5:C"+(rownum-1)+",\"Term\")";
		cell1.setCellFormula(formula10);

		Row datarow1 = pulsedata.createRow(rownum++);
		Cell cell2 = createcell(datarow1, 0, headerstyle);
		cell2.setCellValue("Payroll");

		Cell cell3 = createcell(datarow1, 1, headerstyle);
		String formula11="COUNTIF(C5:C"+(rownum-1)+",\"payroll\")";
		cell3.setCellFormula(formula11);
		
		Row datarow3= pulsedata.createRow(rownum++);
		Cell cell4 = createcell(datarow3, 0, headerstyle);
		cell4.setCellValue("Total");
		pulsedata.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, 0, 0));
		Cell cell5 = createcell(datarow3, 1, headerstyle);
		String formula12="SUM(B"+(rownum-3)+":B"+(rownum-1)+")";
		cell5.setCellFormula(formula12);
		pulsedata.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, 1, 1));
		
		return rownum;
	}

	public Cell createcell(Row row, int index, CellStyle style) {
		Cell cell0 = row.createCell(index);
		cell0.setCellStyle(style);
		return cell0;
	}

	public Font setnormalfont(Workbook userbook) {
		Font normalFont = userbook.createFont();
		normalFont.setBold(false);
		normalFont.setFontHeightInPoints((short) 10);
		normalFont.setColor(IndexedColors.BLACK.getIndex());
		return normalFont;
	}


	public Font setdataheadfont(Workbook userbook) {
		Font headerFont = userbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setColor(IndexedColors.BLACK.getIndex());

		return headerFont;
	}

	private Sheet setsheetStyle(Sheet pulsedata, String[] pReportHeading) {

		pulsedata.addMergedRegion(new CellRangeAddress(0, 2, 0, pReportHeading.length - 1));
		pulsedata.createFreezePane(3, 4);
		for (int i = 0; i < pReportHeading.length; i++)
			pulsedata.setColumnWidth(i, 23 * 256);
		pulsedata.setDefaultRowHeightInPoints((float) 16.0);
		pulsedata.setDisplayGridlines(false);
		return pulsedata;
	}

	public CellStyle setnormalstyle(Workbook userbook) {
		CellStyle normalStyle = userbook.createCellStyle();
		normalStyle.setWrapText(true);
		normalStyle.setAlignment(HorizontalAlignment.CENTER);

		normalStyle.setBorderLeft(BorderStyle.THIN);
		normalStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		normalStyle.setBorderRight(BorderStyle.THIN);
		normalStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		normalStyle.setBorderTop(BorderStyle.THIN);
		normalStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		normalStyle.setBorderBottom(BorderStyle.THIN);
		normalStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		return normalStyle;
	}

	public CellStyle setnormaldatestyle(Workbook userbook) {
		CellStyle normalStyle = userbook.createCellStyle();
		normalStyle.setWrapText(true);
		normalStyle.setAlignment(HorizontalAlignment.CENTER);

		normalStyle.setBorderLeft(BorderStyle.THIN);
		normalStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		normalStyle.setBorderRight(BorderStyle.THIN);
		normalStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		normalStyle.setBorderTop(BorderStyle.THIN);
		normalStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		normalStyle.setBorderBottom(BorderStyle.THIN);
		normalStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		CreationHelper createHelper = userbook.getCreationHelper();
		normalStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));
		return normalStyle;
	}
	
	

}
