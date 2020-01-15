package com.EMS.utility;

public class Constants {

	public static int EMAIL_TOKEN_EXP_DUR = 10;

//	public static String CONTEXT_PATH = "http://192.168.15.55:4200"; // Local
//	public static String CONTEXT_PATH = "https://pms.titechdev.com"; // Production
	// public static String CONTEXT_PATH =
	// "https://stagingpms.titechdev.com";//Staging

	public static final String TASKTRACK_APPROVER_STATUS_OPEN = "OPEN";
	public static final String TASKTRACK_APPROVER_STATUS_SUBMIT = "SUBMITTED";
	public static final String TASKTRACK_APPROVER_STATUS_LOCK = "LOCKED";
	public static final String TASKTRACK_APPROVER_STATUS_CORRECTION = "CORRECTION";
	public static final String TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED = "CORRECTION_SAVED";
	public static final String TASKTRACK_APPROVER_STATUS_CORRECTED = "CORRECTION_SUBMITTED";
	public static final String TASKTRACK_APPROVER_STATUS_REJECTION = "REJECTED";
	public static final String TASKTRACK_APPROVER_STATUS_REJECTION_SAVED = "REJECTION_SAVED";
	public static final String TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED = "REJECTION_SUBMITTED";

	public static final String TASKTRACK_FINAL_STATUS_OPEN = "OPEN";
	public static final String TASKTRACK_FINAL_STATUS_SUBMIT = "SUBMITTED";
	public static final String TASKTRACK_FINAL_STATUS_CORRECTION = "CORRECTION";
	public static final String TASKTRACK_FINAL_STATUS_CORRECTION_SAVED = "CORRECTION_SAVED";

	public static final String TASKTRACK_CORRECTION_STATUS_OPEN = "OPEN";
	public static final String TASKTRACK_CORRECTION_STATUS_CLOSED = "CLOSED";

	public static final String TASKTRACK_CORRECTION_TYPE_CORRECTION = "CORRECTION";
	public static final String TASKTRACK_CORRECTION_TYPE_RECORRECTION = "RECORRECTION";

	public static final String TASKTRACK_REJECTION_STATUS_OPEN = "OPEN";
	public static final String TASKTRACK_REJECTION_STATUS_CLOSED = "CLOSED";
	public static final String TASKTRACK_REJECTION_FIRST_HALF_CYCLE = "FIRST";
	public static final String TASKTRACK_REJECTION_SECOND_HALF_CYCLE = "SECOND";

	public static final String TASKTRACK_PROJECT_TYPE_BILLABLE = "billable";
	public static final String TASKTRACK_PROJECT_TYPE_NON_BILLABLE = "nonBillable";
	public static final String TASKTRACK_PROJECT_TYPE_OVERTIME = "overtime";
	public static final String TASKTRACK_PROJECT_TYPE_BEACH = "beach";

	public static final String NOT_SUBMITTED = "NOT_SUBMITTED";

	public static final long BEACH_PROJECT_ID = 35l;

	public static final String QUICK_TIME_TRACK_DESC = "Quick Time Track";

	public static final String HALF_DAY_LEAVE = "HD";

	public static final Integer DEFAULT_DECIMAL_PRECISION = 2;

	// Sunday-1, Monday-2, Tuesday-3, Wednesday-4, Thursday-5, Friday-6, Saturday-7
	public static final Integer WEEK_START_DAY = 1;
	
	public static final String TASKTRACK_USER_STATUS_SUBMIT = "SUBMITTED";
	public static final String TASKTRACK_USER_STATUS_SAVED = "SAVED";
	public static final String TASKTRACK_USER_STATUS_REJECTION = "REJECTED";
	

	public static final Double roundToDefaultPrecision(Double value) {

		Double multiplicationConstant = Math.pow(10, Constants.DEFAULT_DECIMAL_PRECISION);
		value = Math.round(value * multiplicationConstant) / multiplicationConstant;

		return value;
	}

	public class ProjectWorkflow {
		public static final int SEMI_MONTHLY_WITHOUT_DAILY_TASK = 1;
		public static final int SEMI_MONTHLY_WITH_DAILY_TASK = 2;
		public static final int WEEKLY_WITHOUT_DAILY_TASK = 3;
		public static final int WEEKLY_WITH_DAILY_TASK = 4;
	}
	
	public class TaskTrackWeeklyApproval {
		public static final String TASKTRACK_WEEKLY_APPROVER_STATUS_SUBMIT = "SUBMITTED";
		public static final String TASKTRACK_WEEKLY_APPROVER_STATUS_SAVED = "SAVED";
		public static final String TASKTRACK_WEEKLY_APPROVER_STATUS_OPEN = "OPEN";
		public static final String TASKTRACK_WEEKLY_APPROVER_STATUS_REJECTED = "REJECTED";
		public static final String TASKTRACK_WEEKLY_APPROVER_STATUS_APPROVED = "APPROVED";
	}
	
	public class TaskTrackSemiMonthlyApproval {
		public static final String TASKTRACK_SEMI_MONTHLY_APPROVER_STATUS_APPROVED = "APPROVED";
	}

	public static final int WARN_CODE = 501;
	public static final int ERROR_CODE = 500;
	public static final int SUCCESS_CODE = 200;
	public static final String SUCCESS = "Success";
	public static final String OK = "OK";
	public static final String ERROR = "Error";
}
