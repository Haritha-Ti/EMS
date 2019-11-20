package com.EMS.service;

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
import org.apache.poi.ss.util.RegionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SummaryService {

	@Autowired
	ProjectService projectservice;

	public void generateReport(Workbook userbook, String[] summaryHeading, Sheet summary, String startdate,
			String enddate, String exchangeRateName, int exchangeRate, int endrow) {

		int summaryHdrLen = summaryHeading.length * 2 + 2;
		Sheet sheetstyle = setsheetStyle(summary, summaryHdrLen);
		Font normalFont = setnormalfont(userbook);
		CellStyle normalstyle = setnormalstyle(userbook);
		normalstyle.setFont(normalFont);

		CellStyle headerstyle = userbook.createCellStyle();

		Font hFont = setdataheadfont(userbook);
		CellStyle headstyle = setnormalstyle(userbook);
		headstyle.setFont(hFont);

		CellStyle noborderstyle = setdatestylenoborder(userbook);
		noborderstyle.setFont(normalFont);
		headerstyle.cloneStyleFrom(normalstyle);
		headerstyle.setFont(hFont);

		Row summaryTitleRow = summary.createRow(0);
		Cell summaryTitleCell = createcell(summaryTitleRow, 0, headstyle);
		summaryTitleCell.setCellValue("Ti Technology (India)-  Pulse as of " + startdate + " to " + enddate);

		Row exchangeRateRow = summary.createRow(4);
		Cell exchNameCell = createcell(exchangeRateRow, summaryHdrLen - 2, normalstyle);
		exchNameCell.setCellValue(exchangeRateName);
		Cell exchNameCell1 = createcell(exchangeRateRow, summaryHdrLen, normalstyle);
		exchNameCell1.setCellValue(exchangeRate);

		int count = 0;
		Row summaryrow1 = summary.createRow(6);
		for (int i = 0; i < summaryHdrLen + 1; i++) {

			if (count < summaryHeading.length) {

				if (i > 3 && (i % 2 == 0)) {
					Cell summaryheadcell = createcell(summaryrow1, i, headstyle);
					summaryheadcell.setCellValue(summaryHeading[count]);
					
					if(i<summaryHdrLen)
						summary.addMergedRegion(new CellRangeAddress(6, 6, i, i + 1));
					count++;

				} else if (i > 2 && (i % 2 == 1)) {
					summary.setColumnWidth(i, 4 * 256);
					Cell summaryheadcell = createcell(summaryrow1, i, headstyle);
				} else if (i == 0) {
					summary.setColumnWidth(i, 4 * 256);
					Cell summaryheadcell = createcell(summaryrow1, i, headstyle);
				} else {
					summary.setColumnWidth(i, 22 * 256);
					Cell summaryheadcell = createcell(summaryrow1, i, headstyle);
				}
			}

		}
		summary.addMergedRegion(new CellRangeAddress(6, 6, 1, 3));
		int rownum = 8;
		Row summRow = summary.createRow(rownum);
		Cell summCell = createcell(summRow, 1, normalstyle);
		summCell.setCellValue("Billable");

		rownum += 2;

		List<String> location = projectservice.getclientLocation();
		//System.out.println("location list:" + location.size());
		int startrow = 0, lastrow = 0,benchrow=0,totalrow=0;
		
		startrow = rownum+2;
		for (String item : location) {

			rownum += 2;
			
			Row summRow1 = summary.createRow(rownum);
			Cell summCell1 = createcell(summRow1, 2, noborderstyle);
			summCell1.setCellValue(item);

			summCell = createcell(summRow1, 4, noborderstyle);
			summCell.setCellFormula(
					"SUMIF('PulseReport'!G6:G" + endrow + ",C" + (rownum + 1) + ",'PulseReport'!L6:L" + endrow + ")");

			summCell = createcell(summRow1, 6, noborderstyle);
			summCell.setCellFormula(
					"SUMIF('PulseReport'!G6:G" + endrow + ",C" + (rownum + 1) + ",'PulseReport'!M6:M" + endrow + ")");

			summCell = createcell(summRow1, 8, noborderstyle);
			summCell.setCellFormula(
					"SUMIF('PulseReport'!G6:G" + endrow + ",C" + (rownum + 1) + ",'PulseReport'!N6:N" + endrow + ")");

			summCell = createcell(summRow1, 10, noborderstyle);
			summCell.setCellFormula("IF(E" + (rownum + 1) + ">0,I" + (rownum + 1) + "/E" + (rownum + 1) + ",0)");

			summCell = createcell(summRow1, 12, noborderstyle);
			summCell.setCellFormula("COUNTIF('PulseReport'!G6:G" + endrow + ",C" + (rownum + 1) + ")");

		}
		lastrow = rownum+1;
		rownum += 3;
		
		totalrow=rownum+1;
		summRow = summary.createRow(rownum++);
		summCell = createcell(summRow, 2, noborderstyle);
		summCell.setCellValue("Total");

		summCell = createcell(summRow, 4, noborderstyle);
		summCell.setCellFormula("SUM(E"+startrow+":E"+lastrow+")");

		summCell = createcell(summRow, 6, noborderstyle);
		summCell.setCellFormula("SUM(G"+startrow+":G"+lastrow+")");

		summCell = createcell(summRow, 8, noborderstyle);
		summCell.setCellFormula("SUM(I"+startrow+":I"+lastrow+")");

		summCell = createcell(summRow, 10, noborderstyle);
		summCell.setCellFormula("I" + (rownum + 1) + "/E" + (rownum + 1));

		summCell = createcell(summRow, 12, noborderstyle);
		summCell.setCellFormula("SUM(M"+startrow+":M"+lastrow+")");
		
		rownum += 2;
		benchrow=rownum+1;
		summRow = summary.createRow(rownum++);

		summCell =  createcell(summRow, 1, noborderstyle);
		summCell.setCellValue("Bench");
		
		summCell = createcell(summRow, 4, noborderstyle);
		summCell.setCellFormula("SUMIF('PulseReport'!D4:D" + endrow + ",\"NCR\",'PulseReport'!L4:L" + endrow + ")");
	


		summCell = createcell(summRow, 6, noborderstyle);
		summCell.setCellFormula(
				"SUMIF('PulseReport'!D4:D" + endrow + ",\"NCR\",'PulseReport'!M4:M" + endrow + ")");

		summCell = createcell(summRow, 8, noborderstyle);
		summCell.setCellFormula(
				"SUMPRODUCT(--('PulseReport'!D4:D" + endrow + "=\"NCR\"),'PulseReport'!N4:N" + endrow + ")");

		summCell = createcell(summRow, 10, noborderstyle);
		summCell.setCellFormula("IF(E" + rownum + ">0,I" + rownum + "/E" + rownum + ",0)");

		summCell = createcell(summRow, 12, noborderstyle);
		summCell.setCellFormula("COUNTIF('PulseReport'!D4:D" + endrow + ",\"NCR\")");
		
		
		rownum += 2;
		summRow = summary.createRow(rownum++);

		summCell = createcell(summRow, 4, noborderstyle);
		summCell.setCellFormula("+E"+totalrow+"+E"+benchrow+"");

		summCell = createcell(summRow, 6, noborderstyle);
		summCell.setCellFormula("+G"+totalrow+"+G"+benchrow+"");

		summCell = createcell(summRow, 8, noborderstyle);
		summCell.setCellFormula("+I"+totalrow+"+I"+benchrow+"");

		summCell = createcell(summRow, 10, noborderstyle);
		summCell.setCellFormula("IF(E" + rownum + ">0,I" + rownum + "/E" + rownum + ",0)");
		
		summCell = createcell(summRow, 12, noborderstyle);
		summCell.setCellFormula("+M"+totalrow+"+M"+benchrow+"");
		
		CellRangeAddress summaryTableRange = new CellRangeAddress(6, rownum, 1, summaryHdrLen);
		RegionUtil.setBorderTop(BorderStyle.THICK, summaryTableRange, summary);
		RegionUtil.setBorderBottom(BorderStyle.THICK, summaryTableRange, summary);
		RegionUtil.setBorderLeft(BorderStyle.THICK, summaryTableRange, summary);
		RegionUtil.setBorderRight(BorderStyle.THICK, summaryTableRange, summary);
		

	}

	private CellStyle setdatestylenoborder(Workbook userbook) {
		CellStyle normalStyle = userbook.createCellStyle();
		normalStyle.setWrapText(true);
		normalStyle.setAlignment(HorizontalAlignment.RIGHT);

		return normalStyle;
	}

	private Font setdataheadfont(Workbook userbook) {
		Font headerFont = userbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setColor(IndexedColors.BLACK.getIndex());

		return headerFont;
	}

	private CellStyle setnormalstyle(Workbook userbook) {
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

	private Font setnormalfont(Workbook userbook) {
		Font normalFont = userbook.createFont();
		normalFont.setBold(true);
		normalFont.setFontHeightInPoints((short) 10);
		normalFont.setColor(IndexedColors.BLACK.getIndex());
		return normalFont;
	}

	private Sheet setsheetStyle(Sheet summary, int summaryHeading) {
		summary.addMergedRegion(new CellRangeAddress(0, 2, 0, summaryHeading));
		for (int i = 0; i < summaryHeading + 1; i++)
			summary.setColumnWidth(i, 23 * 256);
		summary.setDefaultRowHeightInPoints((float) 16.0);
//		summary.setDisplayGridlines(false);
		return summary;
	}

	public Cell createcell(Row row, int index, CellStyle style) {
		Cell cell0 = row.createCell(index);
		cell0.setCellStyle(style);
		return cell0;
	}

}
