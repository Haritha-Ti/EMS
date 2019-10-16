package com.EMS.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.EMS.model.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface AttendanceService {

	public List<Object[]> getHolidayList();


	public List<Object[]> getUserLeaveList(Long userId, LocalDate firstDayOfYear, LocalDate lastDayOfYear);

	public List<Object[]> getWeeklyLeavelist(Date startdate1,Date enddate1);

	public List<LeaveModel> getYearlyLeavelist(long userId, Date startDate1, Date endDate1);

	public void saveLeaveMarking(LeaveModel leaveModel);

	public ObjectNode getLeavebalanceData(Long userId, int quarter, int year);

	public Boolean checkLeave(LeaveModel leaveModel);

	public void setLeaveBalance(LeaveBalanceModel leavebal);

	public List<Long> getAllUserId();

	public Boolean checkLeaveBalance(LeaveBalanceModel lBalance);

	public void deleteBalance(LeaveBalanceModel lBalance);

	public LeaveBalanceModel getUserLeaveBalance(int i, int year, long userId);

	public List<LeaveModel> getLeavelist(Date startDate1, Date endDate1);

	public LeaveModel Leavedetails(long leaveId);

	public List<Object[]> getUserLeaveListByLeaveType(Long userId, String type, LocalDate firstDayOfYear,
			LocalDate lastDayOfYear);

	public LeaveModel getLeaveDetails(long leaveId);


	public void deleteLeaveMarking(long leaveId);


	public List<LeaveModel> getLeavelist(Date startDate1, Date endDate1, String leaveType);

	public long addUserLeaveSummary(UserLeaveSummary userLeaveSummary);

	public Boolean isExist(Long leaveSummaryId);

	public List<UserLeaveSummary> getUserLeaveSummaryList(long userId);

	public UserLeaveSummary getLeaveDetailsById(long userLeaveSummaryId);

	public void removeUserLeaveSummary(UserLeaveSummary userLeaveData);

	public Boolean isUserExist(Long userId);


	public List<Object[]> getHolidayListByRegionId(Long region_Id, String month, String year);


	public List<Object[]> getHolidayDetails(Long holiday_id);

}
