package com.EMS.utility;

import org.joda.time.DateTimeConstants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
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

	// Nisha
	public  static JSONArray findWeeks(int curMonth, int curYear) {
		int month = curMonth;
		int year = curYear;
		int dayOfWeek = DateTimeConstants.SUNDAY;
		String weekRange;
		JSONArray weeksArray = new JSONArray();
		org.joda.time.LocalDate firstOfMonth = new org.joda.time.LocalDate(year, month, 1);
		org.joda.time.LocalDate firstOfNextMonth = firstOfMonth.plusMonths(1);
		org.joda.time.LocalDate firstDateInGrid = firstOfMonth.withDayOfWeek(dayOfWeek);
		if (firstDateInGrid.isAfter(firstOfMonth)) { // If getting the next start of week instead of desired week's
			// start, adjust backwards.
			firstDateInGrid = firstDateInGrid.minusWeeks(1);
		}

		org.joda.time.LocalDate weekStart = firstDateInGrid;
		org.joda.time.LocalDate weekStop = null;
		int weekNumber = 0;

		do {
			JSONObject weekDataResponse = new JSONObject();

			weekNumber = weekNumber + 1;
			weekStop = weekStart.plusDays(6);
			// System.out.println( weekNumber + " week: " + weekStart + " --- " + weekStop
			// ); // 1 week: 03-30-2014 --- 04-05-2014
			weekRange = weekStart + " - " + weekStop;

			weekDataResponse.put("startDate", weekStart.toDateTimeAtStartOfDay().toDate());
			weekDataResponse.put("endDate", weekStop.toDateTimeAtStartOfDay().toDate());
			weeksArray.add(weekDataResponse);
			weekStart = weekStop.plusDays(1);

		} while (weekStop.isBefore(firstOfNextMonth) && weekStart.isBefore(firstOfNextMonth));

		return weeksArray;
	}

	public static final SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

	public static final SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static final SimpleDateFormat dateFormat3 = new SimpleDateFormat("dd MMM");

	public static final SimpleDateFormat dateFormat4 = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
}
