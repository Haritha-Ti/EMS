package com.EMS.utility;

public class Constants {

	public static int EMAIL_TOKEN_EXP_DUR = 10;

//	public static String CONTEXT_PATH = "http://192.168.15.55:4200"; // Local
//	public static String CONTEXT_PATH = "https://pms.titechdev.com"; // Production
	// public static String CONTEXT_PATH =
	// "https://stagingpms.titechdev.com";//Staging

	public static String TASKTRACK_APPROVER_STATUS_OPEN = "OPEN";
	public static String TASKTRACK_APPROVER_STATUS_SUBMIT = "SUBMITTED";
	public static String TASKTRACK_APPROVER_STATUS_LOCK = "LOCKED";
	public static String TASKTRACK_APPROVER_STATUS_REJECT = "REJECTED";
	public static String TASKTRACK_APPROVER_STATUS_CORRECTION = "CORRECTION";
	public static String TASKTRACK_APPROVER_STATUS_CORRECTION_SAVED = "CORRECTION_SAVED";
	public static String TASKTRACK_APPROVER_STATUS_CORRECTED = "CORRECTED";

	public static String TASKTRACK_FINAL_STATUS_OPEN = "OPEN";
	public static String TASKTRACK_FINAL_STATUS_SUBMIT = "SUBMITTED";
	public static String TASKTRACK_FINAL_STATUS_REJECT = "REJECTED";
	public static String TASKTRACK_FINAL_STATUS_CORRECTION = "CORRECTION";
	public static String TASKTRACK_FINAL_STATUS_CORRECTION_SAVED = "CORRECTION_SAVED";
	public static String TASKTRACK_FINAL_STATUS_CORRECTED = "CORRECTED";

	public static String TASKTRACK_CORRECTION_STATUS_OPEN = "OPEN";
	public static String TASKTRACK_CORRECTION_STATUS_CLOSED = "CLOSED";
	public static String TASKTRACK_CORRECTION_FIRST_HALF_CYCLE = "FIRST";
	public static String TASKTRACK_CORRECTION_SECOND_HALF_CYCLE = "SECOND";

}
