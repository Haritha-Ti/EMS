package com.EMS.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.LeaveBalanceModel;
import com.EMS.model.LeaveModel;
import com.EMS.model.UserLeaveSummary;
import com.EMS.repository.HolidayRepository;
import com.EMS.repository.LeaveBalanceRepository;
import com.EMS.repository.LeaveRepository;
import com.EMS.repository.UserLeaveSummaryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class AttendanceServiceImpl implements AttendanceService {

	@Autowired
	HolidayRepository holidayRepository;

	@Autowired
	LeaveRepository leaveRepository;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	LeaveBalanceRepository leaveBalanceRepository;

	@Autowired
	UserLeaveSummaryRepository userLeaveSummaryRepository;

	@Override
	public List<Object[]> getHolidayList() {
		List<Object[]> list = holidayRepository.getHolidayLists();
		return list;
	}


	@Override
	public List<Object[]> getUserLeaveList(Long userId, LocalDate firstDayOfYear, LocalDate lastDayOfYear) {
		List<Object[]> leaveList = leaveRepository.getUserLeaveList(userId, firstDayOfYear, lastDayOfYear);
		return leaveList;
	}


	@Override
	public List<Object[]> getWeeklyLeavelist(Date startdate1,Date enddate1) {
		List<Object[]> list=leaveRepository.getweeklyleavelist(startdate1,enddate1);
		//System.out.println("startdate :"+startdate1+" enddate: "+enddate1+" list :"+list.size());
		return list;
	}

	@Override
	public List<LeaveModel> getYearlyLeavelist(long userId, Date startDate1, Date endDate1) {
		
		List<LeaveModel> list=leaveRepository.getyearlyleavelist(userId,startDate1,endDate1);
		return list;
	}

	
	@Override
	public void saveLeaveMarking(LeaveModel leaveModel) {
		leaveRepository.save(leaveModel);
		
	}

	
	@Override
	public ObjectNode getLeavebalanceData(Long userId, int quarter, int year) {
		ObjectNode leaveBalanceNode = objectMapper.createObjectNode();

		 	Double clBalance = 0.0;
		    Double slBalance = 0.0;
		    Double elBalance = 0.0;

			    
//				for (int i = 1; i <= quarter; i++) {

					LeaveBalanceModel leaveBalanceOld = leaveBalanceRepository.getLeaveBalance(userId, quarter,
							year);
					if(leaveBalanceOld != null) {
						if (leaveBalanceOld.getClBalance() > 0) {
							clBalance+= leaveBalanceOld.getClBalance();
													
						}
						if (leaveBalanceOld.getSlBalance() > 0) {
							slBalance+= leaveBalanceOld.getSlBalance();
						}
						if (leaveBalanceOld.getElBalance() > 0) {
							elBalance+= leaveBalanceOld.getElBalance();
						}
						leaveBalanceNode.put("balanceId", leaveBalanceOld.getLeaveBalanceId());
					}
					
//				}


			leaveBalanceNode.put("CL", clBalance);
			leaveBalanceNode.put("SL", slBalance);
			leaveBalanceNode.put("EL", elBalance);
			

		return leaveBalanceNode;
	}

	@Override
	public Boolean checkLeave(LeaveModel leaveModel) {
		int result=leaveRepository.checkuser(leaveModel.getUser().getUserId(),leaveModel.getLeaveFrom(),leaveModel.getLeaveTo()); 
		//System.out.println("check count:"+result);
		if(result==0)
			return true;
		else
			return false;
	}

	@Override
	public void setLeaveBalance(LeaveBalanceModel leavebal) {
		leaveBalanceRepository.save(leavebal);
		
	}

	@Override
	public List<Long> getAllUserId() {
		List<Long> list=leaveBalanceRepository.getUserId();
		return list;
	}


	@Override
	public List<LeaveModel> getLeavelist(Date startDate1, Date endDate1) {
		List<LeaveModel> list=leaveRepository.getyearlyleavelist(startDate1,endDate1);
		return list;
	}

	@Override
	public LeaveModel Leavedetails(long leaveId) {
		LeaveModel leavedata=leaveRepository.getOne(leaveId);
		return leavedata;
	}

	@Override
	public List<Object[]> getUserLeaveListByLeaveType(Long userId, String type, LocalDate firstDayOfYear,
			LocalDate lastDayOfYear) {
		List<Object[]> leaveList = null;
		leaveList = leaveRepository.getUsersLeaveLeaveListByType(userId,firstDayOfYear,lastDayOfYear,type);
		return leaveList;
	}


	@Override
	public Boolean checkLeaveBalance(LeaveBalanceModel lBalance) {
		int result=leaveBalanceRepository.checkBalance(lBalance.getUser().getUserId(),lBalance.getQuarter(),lBalance.getYear()); 
		//System.out.println("check count:"+result);
		if(result==0)
			return true;
		else
			return false;
	}


	@Override
	public void deleteBalance(LeaveBalanceModel lBalance) {

		try {
			leaveBalanceRepository.deleteleaveBalance(lBalance.getQuarter(),lBalance.getYear(),lBalance.getUser().getUserId());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public LeaveBalanceModel getUserLeaveBalance(int quarter, int year, long userId) {
		LeaveBalanceModel list=leaveBalanceRepository.getuserLeaveBalance(year,quarter,userId);
		return list;
	}


	@Override
	public LeaveModel getLeaveDetails(long leaveId) {
		LeaveModel leave=leaveRepository.getOne(leaveId);
		return leave;
	}


	@Override
	public void deleteLeaveMarking(long leaveId) {
		try {
			leaveRepository.deleteleaveMarking(leaveId);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public List<LeaveModel> getLeavelist(Date startDate1, Date endDate1, String leaveType) {
		List<LeaveModel> list=leaveRepository.getleavelist(startDate1,endDate1,leaveType);
		return list;
	}

	@Override
	public long addUserLeaveSummary(UserLeaveSummary userLeaveSummary) {
		UserLeaveSummary userLeave = userLeaveSummaryRepository.save(userLeaveSummary);
		return userLeave.getLeaveSummaryId();

	}

	@Override
	public Boolean isExist(Long leaveSummaryId) {
		int result = 0;
		result = userLeaveSummaryRepository.isExist(leaveSummaryId);

		if(result>0)
			return true;
		else
			return false;
	}

	@Override
	public List<UserLeaveSummary> getUserLeaveSummaryList(long userId){
		List<UserLeaveSummary> userLeaveSummary = userLeaveSummaryRepository.getUserLeaveSummaryList(userId);
		return userLeaveSummary;
	}

	@Override
	public UserLeaveSummary getLeaveDetailsById(long userLeaveSummaryId){
		UserLeaveSummary userLeaveSummary = userLeaveSummaryRepository.getLeaveDetailsById(userLeaveSummaryId);
		return userLeaveSummary;
	}

	@Override
	public void removeUserLeaveSummary(UserLeaveSummary userLeaveData){
		userLeaveSummaryRepository.delete(userLeaveData);

	}

	@Override
	public Boolean isUserExist(Long userId){
		int result = 0;
		result = userLeaveSummaryRepository.isUserExist(userId);

		if(result>0)
			return true;
		else
			return false;
	}


	@Override
	public List<Object[]> getHolidayListByRegionId(Long region_Id, String month, String year) {
		// TODO Auto-generated method stub
	
		String monthyear = "";
				
		if(month != null && year != null && month != "" && year != "") {
			monthyear = year+"-"+month;
		}
		if(year != "" && month == "" || year != null && month == null ) {
			
			monthyear = year+"-";
		}
		System.out.println(monthyear+"-------------------->");
		List<Object[]> list = holidayRepository.getHolidayListByRegionId(region_Id,monthyear);
		return list;
	}


	@Override
	public List<Object[]> getHolidayDetails(Long holiday_id) {
		// TODO Auto-generated method stub
		List<Object[]> list = holidayRepository.getHolidayDetails(holiday_id);
		return list;
	}
}
