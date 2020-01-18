package com.EMS.utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
	
	public static List<Date> getDatesBetweenTwo(Date startDate, Date endDate) {
		List<Date> datesInRange = new ArrayList<Date>();
		
		Calendar fromDate = Calendar.getInstance();
		Calendar toDate = Calendar.getInstance();
		
		fromDate.setTime(startDate);
		toDate.setTime(endDate);

		while(fromDate.before(toDate) || fromDate.equals(toDate)) {
			Date date = fromDate.getTime();
			datesInRange.add(date);
			fromDate.add(Calendar.DATE, 1);
		}
		return datesInRange;
	}
}
