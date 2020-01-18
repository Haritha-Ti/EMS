package com.EMS.service;

import com.EMS.model.TaskTrackWeeklyApproval;
import com.EMS.model.TasktrackApprovalSemiMonthly;

public interface TaskTrackApprovalReportService {

	Boolean populateReportRecord(TasktrackApprovalSemiMonthly submissionRecord) throws NullPointerException,Exception;
	
	Boolean populateReportRecord(TaskTrackWeeklyApproval submissionRecord) throws NullPointerException,Exception;
}
