package com.EMS.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.TaskTrackWeeklyApproval;
import com.EMS.model.TasktrackApprovalReporting;
import com.EMS.model.TasktrackApprovalSemiMonthly;
import com.EMS.repository.TaskTrackApprovalReportRepository;
import com.EMS.utility.DateUtil;

@Service
public class TaskTrackApprovalReportServiceImpl implements TaskTrackApprovalReportService{

	@Autowired
	TaskTrackApprovalReportRepository reportRepository;
	
	@Override
	public Boolean populateReportRecord(TasktrackApprovalSemiMonthly record) throws NullPointerException, Exception {
		TasktrackApprovalReporting report = reportRepository.
				findMonthReport(record.getUser().getUserId(), record.getProject().getProjectId(), record.getMonth(), record.getYear());
		if(report == null) {
			report = new TasktrackApprovalReporting();
		}
		report.setDay1(record.getDay1());
		report.setDay2(record.getDay2());
		report.setDay3(record.getDay3());
		report.setDay4(record.getDay4());
		report.setDay5(record.getDay5());
		report.setDay6(record.getDay6());
		report.setDay7(record.getDay7());
		report.setDay8(record.getDay8());
		report.setDay9(record.getDay9());
		report.setDay10(record.getDay10());
		report.setDay11(record.getDay11());
		report.setDay12(record.getDay12());
		report.setDay13(record.getDay13());
		report.setDay14(record.getDay14());
		report.setDay15(record.getDay15());
		report.setDay16(record.getDay16());
		report.setDay17(record.getDay17());
		report.setDay18(record.getDay18());
		report.setDay19(record.getDay19());
		report.setDay20(record.getDay20());
		report.setDay21(record.getDay21());
		report.setDay22(record.getDay22());
		report.setDay23(record.getDay23());
		report.setDay24(record.getDay24());
		report.setDay25(record.getDay25());
		report.setDay26(record.getDay26());
		report.setDay27(record.getDay27());
		report.setDay28(record.getDay28());
		report.setDay29(record.getDay29());
		report.setDay30(record.getDay30());
		report.setDay31(record.getDay31());

		report.setMonth(record.getMonth());
		report.setYear(record.getYear());
		report.setProject(record.getProject());
		report.setUser(record.getUser());
			
		reportRepository.save(report);
		return Boolean.TRUE;
	}

	@Override
	public Boolean populateReportRecord(TaskTrackWeeklyApproval record)throws NullPointerException, Exception {
	
		Calendar calendar = Calendar.getInstance();
		
		Integer month1 = null;
		Integer year1 = null;
		
		Integer month2 = null;
		Integer year2= null;
		
		Map<Integer, Map<Integer,Double>> monthMap = new HashMap<Integer, Map<Integer,Double>>();
		
		List<Date> dates = DateUtil.getDatesBetweenTwo(record.getStartDate(), record.getEndDate());
		int indx = 1;
		for(Date date : dates) {
			calendar.setTime(date);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int month = calendar.get(Calendar.MONTH) + 1;
			int year = calendar.get(Calendar.YEAR);
			
			Double hour = null;
			switch(indx) {
				case 1:
					hour = record.getDay1();
					break;
				case 2:
					hour = record.getDay2();
					break;
				case 3:
					hour = record.getDay3();
					break;
				case 4:
					hour = record.getDay4();
					break;
				case 5:
					hour = record.getDay5();
					break;
				case 6:
					hour = record.getDay6();
					break;
				case 7:
					hour = record.getDay7();
					break;
			}
			indx++;
			Map<Integer,Double> dayMap = null;
			if(monthMap.containsKey(month)) {
				dayMap = monthMap.get(month);
				dayMap.put(day, hour);
			}
			else {
				dayMap = new HashMap<Integer, Double>();
				dayMap.put(day, hour);
				monthMap.put(month, dayMap);
			}
			if(month1 == null){
				month1 = month;
				year1 = year;
			}
			else if(month2 == null && month1 != month){
				month2 = month;
				year2 = year;
			}
		}
		
		List<TasktrackApprovalReporting> reportList = new ArrayList<TasktrackApprovalReporting>();
		if(month2 != null && year2 != null) {
			reportList = reportRepository.findWeekIntersectingMonthResport(record.getUser().getUserId(), record.getProject().getProjectId(), 
					month1, year1, month2, year2);
		}
		else {
			reportList.add(reportRepository.findMonthReport(record.getUser().getUserId(), record.getProject().getProjectId(), month1, year1));
		}
		
		
		if(reportList.size() == 0) {
			for(Map.Entry<Integer, Map<Integer,Double>> map : monthMap.entrySet()) {
				TasktrackApprovalReporting tasktrackApprovalReporting = new TasktrackApprovalReporting();
				tasktrackApprovalReporting.setMonth(map.getKey());
				tasktrackApprovalReporting.setYear(map.getKey() == month1 ? year1 : year2);
				tasktrackApprovalReporting.setProject(record.getProject());
				tasktrackApprovalReporting.setUser(record.getUser());
				reportList.add(tasktrackApprovalReporting);
			}
		}
		for(TasktrackApprovalReporting report : reportList) {
			if(monthMap.containsKey(report.getMonth())) {
				for(Map.Entry<Integer, Double> dayMap : monthMap.get(report.getMonth()).entrySet()) {
					Double hour = dayMap.getValue();
					switch(dayMap.getKey()) {
						case 1:
							report.setDay1(hour);
							break;
						case 2:
							report.setDay2(hour);
							break;
						case 3:
							report.setDay3(hour);
							break;
						case 4:
							report.setDay4(hour);
							break;
						case 5:
							report.setDay5(hour);
							break;
						case 6:
							report.setDay6(hour);
							break;
						case 7:
							report.setDay7(hour);
							break;
						case 8:
							report.setDay8(hour);
							break;
						case 9:
							report.setDay9(hour);
							break;
						case 10:
							report.setDay10(hour);
							break;
						case 11:
							report.setDay11(hour);
							break;
						case 12:
							report.setDay12(hour);
							break;
						case 13:
							report.setDay13(hour);
							break;
						case 14:
							report.setDay14(hour);
							break;
						case 15:
							report.setDay15(hour);
							break;
						case 16:
							report.setDay16(hour);
							break;
						case 17:
							report.setDay17(hour);
							break;
						case 18:
							report.setDay18(hour);
							break;
						case 19:
							report.setDay19(hour);
							break;
						case 20:
							report.setDay20(hour);
							break;
						case 21:
							report.setDay21(hour);
							break;
						case 22:
							report.setDay22(hour);
							break;
						case 23:
							report.setDay23(hour);
							break;
						case 24:
							report.setDay24(hour);
							break;
						case 25:
							report.setDay25(hour);
							break;
						case 26:
							report.setDay26(hour);
							break;
						case 27:
							report.setDay27(hour);
							break;
						case 28:
							report.setDay28(hour);
							break;
						case 29:
							report.setDay29(hour);
							break;
						case 30:
							report.setDay30(hour);
							break;
						case 31:
							report.setDay31(hour);
							break;
					}
				}
			}
		}
		reportRepository.saveAll(reportList);
		return Boolean.TRUE;
	}
}
