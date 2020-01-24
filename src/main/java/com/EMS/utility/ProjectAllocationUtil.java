package com.EMS.utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.EMS.model.AllocationModel;
import com.EMS.repository.AllocationRepository;


public class ProjectAllocationUtil {
	

	//List all allocated dates for a given start and end date from request
	public static List<String> findAllocatedDates(AllocationRepository allocationRepository, Date startDate, Date endDate, SimpleDateFormat sdf, Long userId,
			Long projectId) {
		List<AllocationModel> userProjAllocations = allocationRepository.findByUserUserIdAndProjectProjectId(userId,
				projectId);
		
		List<String> projectDateList = new ArrayList<String>();

		for (AllocationModel al : userProjAllocations) {
			Date allocStartDate = al.getStartDate();
			Date allocEndDate = al.getEndDate();

			Calendar fromDate = Calendar.getInstance();
			Calendar toDate = Calendar.getInstance();

			if (allocStartDate.before(startDate)) {
				fromDate.setTime(startDate);
			} else {
				fromDate.setTime(allocStartDate);
			}

			if (allocEndDate.before(endDate)) {
				toDate.setTime(allocEndDate);
			} else {
				toDate.setTime(endDate);
			}

			while (fromDate.before(toDate) || fromDate.equals(toDate)) {
				Date result = fromDate.getTime();
				String date = sdf.format(result);
				projectDateList.add(date);
				fromDate.add(Calendar.DATE, 1);
			}
		}
		return projectDateList;
	}
}
