package com.EMS.service;

import java.util.Date;
import java.util.List;

import com.EMS.model.AllocationModel;
import com.fasterxml.jackson.databind.node.ArrayNode;


public interface ReportService {
	
	public ArrayNode getProjectReportDetails(long projectId,Date fromDate, Date toDate);
	public ArrayNode getBenchProjectReportDetails(long uId,Date fromDate, Date toDate);
	public ArrayNode getBenchProjectReportDetails(Date fromDate, Date toDate);
		
	public List getProjectTaskReportDetails(Date fromDate,Date toDate,Long projectId);
	public List getProjectHourReportDetails(Date fromDate,Date toDate,int monthIndex,int yearIndex);

    int getActualHours(long projectId, Date startDate, Date endDate);
}

