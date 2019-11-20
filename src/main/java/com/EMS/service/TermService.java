package com.EMS.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

import com.EMS.model.UserModel;
import com.EMS.model.UserTermination;
import com.EMS.repository.TermRepository;



@Service
public class TermService {
	
	@Autowired
	TermRepository termRepository;
	
	@Autowired
	UserService userservice;

	public Sheet setsheetstyle(Sheet term, String[] termheading) {

		term.createFreezePane(0,6);
		term.addMergedRegion(new CellRangeAddress(0, 4, 0, termheading.length-1));
		for(int i=0;i<termheading.length;i++)
			term.setColumnWidth(i, 22*256);
		term.setDefaultRowHeightInPoints((float) 16.0);
		term.setDisplayGridlines(false);
		return term;
	}

	public Font setnormalfont(Workbook userbook) {
		Font normalFont = userbook.createFont();
		normalFont.setBold(false);
		normalFont.setFontHeightInPoints((short)10);
		normalFont.setColor(IndexedColors.BLACK.getIndex());
		return normalFont;
	}

	public Font setdataheadfont(Workbook userbook) {
		Font headerFont = userbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short)12);
		headerFont.setColor(IndexedColors.BLACK.getIndex());
		
		return headerFont;
	}

	public CellStyle setnormalcellstyle(Workbook userbook, Font termnormalfont) {
		CellStyle normalStyle=userbook.createCellStyle();
		normalStyle.setFont(termnormalfont);
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
		normalStyle.setDataFormat(
			    createHelper.createDataFormat().getFormat("m/d/yy"));
		return normalStyle;
	}
	
	

	public CellStyle setheaderstyle(Workbook userbook, Font termdataheadfont) {
		CellStyle normalStyle=userbook.createCellStyle();
		normalStyle.setFont(termdataheadfont);
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

	public Cell createcell(Row row, int index, CellStyle style) {
		Cell cell0=row.createCell(index);
		cell0.setCellStyle(style);
		return cell0;
	}

	public List<UserTermination> getTermlist(Date startdate,Date enddate){
		List<UserTermination> termlist=new ArrayList<UserTermination>();
		
		try {
			termlist=termRepository.getTerminationList(startdate,enddate);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
			return termlist;
	}

	public CellStyle setnormalcellstyleforint(Workbook userbook, Font termnormalfont) {
		
		CellStyle normalStyle=userbook.createCellStyle();
		normalStyle.setFont(termnormalfont);
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
	
	public Date parsedate(String value) throws Exception{
		Date dateval=new SimpleDateFormat("dd/MM/yyyy").parse(value);
		return dateval;
	}

	public void generateReport(Workbook userbook, String[] termheading,Sheet term,String startdate1,String enddate2) {
		
		try {
			
			Sheet termstyle = setsheetstyle(term,termheading);
			
			Font termnormalfont=setnormalfont(userbook);
			
			Font termdataheadfont=setdataheadfont(userbook);
			
			CellStyle nomalcellstyle=setnormalcellstyle(userbook,termnormalfont);
			
			CellStyle termheadingstyle=userbook.createCellStyle();
			termheadingstyle.cloneStyleFrom(nomalcellstyle);
			termheadingstyle.setFont(termdataheadfont);
			
			CellStyle normalstyleforint=setnormalcellstyleforint(userbook, termnormalfont);
			
			CellStyle termheaderstyle=setheaderstyle(userbook,termdataheadfont);
			
			Row termrow1=term.createRow(0);
			Cell termcell0=createcell(termrow1,0,termheadingstyle);
			termcell0.setCellValue("\n Ti Technology (India) \n Termination Summary \n ");
			
			Row termdataheadrow=term.createRow(5);
			for(int i=0;i<termheading.length;i++) {
				Cell termdatacell=createcell(termdataheadrow, i,termheaderstyle);
				termdatacell.setCellValue(termheading[i]);
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			Date startDate=formatter.parse(startdate1);
			Date endDate=formatter.parse(enddate2);
			
			int termrownum=6;
			
			List<UserTermination> termlist=getTermlist(startDate,endDate);
			//System.out.println("term list:"+termlist.size());
			for(UserTermination termdata:termlist) {
				
				UserModel userdata=userservice.getUserdetailsbyId(termdata.getConsultant().getUserId());
				
				Row datarow=term.createRow(termrownum++);
				Cell datacell0=createcell(datarow, 0, nomalcellstyle);
				Date date1=(Date) termdata.getTerminationDate();
				datacell0.setCellValue(date1);
				Cell datacell1=createcell(datarow, 1, nomalcellstyle);
				datacell1.setCellValue(userdata.getFirstName()+" "+userdata.getLastName());
				Cell datacell2=createcell(datarow, 2, nomalcellstyle);
				datacell2.setCellValue(userdata.getEmploymentType());
				Cell datacell3=createcell(datarow, 3, nomalcellstyle);
				datacell3.setCellValue(userdata.getCppLevel());
				Cell datacell4=createcell(datarow, 4, nomalcellstyle);
				Date date2=(Date) userdata.getJoiningDate();
				datacell4.setCellValue(date2);
				Cell datacell5=createcell(datarow, 5, nomalcellstyle);
				datacell5.setCellValue(termdata.getTermType());
				Cell datacell6=createcell(datarow, 6, nomalcellstyle);
				datacell6.setCellValue(termdata.getTermReason());
				Cell datacell7=createcell(datarow, 7, normalstyleforint);
				
				
				String dateif="DATEDIF(E"+(termrownum)+",A"+(termrownum)+",\"d\")";
				datacell7.setCellFormula(dateif);
				
				Cell datacell8=createcell(datarow, 8, normalstyleforint);
				String service="IF(H"+termrownum+"<365,\"<1 Yr\",IF(AND(MOD(H"+termrownum+",365)=0,INT(H"+termrownum+"/365)=1),CONCATENATE(TEXT(INT(H"+termrownum+"/365),\"0\"),\" yr\"),IF(AND(MOD(H"+termrownum+",365)=0,INT(H"+termrownum+"/365)>1),CONCATENATE(TEXT(INT(H"+termrownum+"/365),\"0\"),\" yrs\"),IF(AND(MOD(H"+termrownum+",365)>0,INT(H"+termrownum+"/365)>0),CONCATENATE(TEXT(INT(H"+termrownum+"/365),\"0\"),\"-\",TEXT(INT(H"+termrownum+"/365)+1,\"0\"),\" yrs\",\"\")))))";
				datacell8.setCellFormula(service);
				
			}
			
			Row termfootrow=term.createRow(termrownum++);
			Cell footercell=createcell(termfootrow, 0, termheaderstyle);
			footercell.setCellValue("Total ");
			Cell footercell1=createcell(termfootrow, 1, termheaderstyle);
			footercell1.setCellFormula("COUNTA(B7:B"+(termrownum-1)+")");
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		

	}

	


}
