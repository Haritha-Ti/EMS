package com.EMS.utility;

import com.EMS.model.TaskTrackApproval;
import com.EMS.model.TaskTrackApprovalFinal;


public class TaskTrackApproverConverter {

	public static TaskTrackApprovalFinal approverToFinalApprover(TaskTrackApproval approval) {
		TaskTrackApprovalFinal finalApprove = new TaskTrackApprovalFinal();
		finalApprove.setId(approval.getId());
		finalApprove.setDay1(approval.getDay1());
		finalApprove.setDay2(approval.getDay2());
		finalApprove.setDay3(approval.getDay3());
		finalApprove.setDay4(approval.getDay4());
		finalApprove.setDay5(approval.getDay5());
		finalApprove.setDay6(approval.getDay6());
		finalApprove.setDay7(approval.getDay7());
		finalApprove.setDay8(approval.getDay8());
		finalApprove.setDay9(approval.getDay9());
		finalApprove.setDay10(approval.getDay10());
		finalApprove.setDay11(approval.getDay11());
		finalApprove.setDay12(approval.getDay12());
		finalApprove.setDay13(approval.getDay13());
		finalApprove.setDay14(approval.getDay14());
		finalApprove.setDay15(approval.getDay15());
		finalApprove.setDay16(approval.getDay16());
		finalApprove.setDay17(approval.getDay17());
		finalApprove.setDay18(approval.getDay18());
		finalApprove.setDay19(approval.getDay19());
		finalApprove.setDay20(approval.getDay20());
		finalApprove.setDay21(approval.getDay21());
		finalApprove.setDay22(approval.getDay22());
		finalApprove.setDay23(approval.getDay23());
		finalApprove.setDay24(approval.getDay24());
		finalApprove.setDay25(approval.getDay25());
		finalApprove.setDay26(approval.getDay26());
		finalApprove.setDay27(approval.getDay27());
		finalApprove.setDay28(approval.getDay28());
		finalApprove.setDay29(approval.getDay29());
		finalApprove.setDay30(approval.getDay30());
		finalApprove.setDay31(approval.getDay31());

		finalApprove.setUser(approval.getUser());
		finalApprove.setProject(approval.getProject());
		finalApprove.setProjectType(approval.getProjectType());
		finalApprove.setYear(approval.getYear());
		finalApprove.setMonth(approval.getMonth());
		finalApprove.setFirstHalfStatus(approval.getFirstHalfStatus());
		finalApprove.setSecondHalfStatus(approval.getSecondHalfStatus());
		//finalApprove.setApprovedDate(approval.getApprovedDate());
		return finalApprove;
	}
	
	public static TaskTrackApproval finalApproverToApprover(TaskTrackApprovalFinal finalApproval) {
		TaskTrackApproval approval = new TaskTrackApproval();
		approval.setId(finalApproval.getId());
		approval.setDay1(finalApproval.getDay1());
		approval.setDay2(finalApproval.getDay2());
		approval.setDay3(finalApproval.getDay3());
		approval.setDay4(finalApproval.getDay4());
		approval.setDay5(finalApproval.getDay5());
		approval.setDay6(finalApproval.getDay6());
		approval.setDay7(finalApproval.getDay7());
		approval.setDay8(finalApproval.getDay8());
		approval.setDay9(finalApproval.getDay9());
		approval.setDay10(finalApproval.getDay10());
		approval.setDay11(finalApproval.getDay11());
		approval.setDay12(finalApproval.getDay12());
		approval.setDay13(finalApproval.getDay13());
		approval.setDay14(finalApproval.getDay14());
		approval.setDay15(finalApproval.getDay15());
		approval.setDay16(finalApproval.getDay16());
		approval.setDay17(finalApproval.getDay17());
		approval.setDay18(finalApproval.getDay18());
		approval.setDay19(finalApproval.getDay19());
		approval.setDay20(finalApproval.getDay20());
		approval.setDay21(finalApproval.getDay21());
		approval.setDay22(finalApproval.getDay22());
		approval.setDay23(finalApproval.getDay23());
		approval.setDay24(finalApproval.getDay24());
		approval.setDay25(finalApproval.getDay25());
		approval.setDay26(finalApproval.getDay26());
		approval.setDay27(finalApproval.getDay27());
		approval.setDay28(finalApproval.getDay28());
		approval.setDay29(finalApproval.getDay29());
		approval.setDay30(finalApproval.getDay30());
		approval.setDay31(finalApproval.getDay31());

		approval.setUser(finalApproval.getUser());
		approval.setProject(finalApproval.getProject());
		approval.setProjectType(finalApproval.getProjectType());
		approval.setYear(finalApproval.getYear());
		approval.setMonth(finalApproval.getMonth());
		approval.setFirstHalfStatus(finalApproval.getFirstHalfStatus());
		approval.setSecondHalfStatus(finalApproval.getSecondHalfStatus());
		//approval.setApprovedDate(finalApproval.getApprovedDate());
		return approval;
	}
}
