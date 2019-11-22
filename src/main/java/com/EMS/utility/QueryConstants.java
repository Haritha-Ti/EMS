package com.EMS.utility;

public class QueryConstants {

	public static final String FINANCE_APPROVED_HOURS_REPORT_QUERY = "select p.project_id as projectId,p.project_name as projectName,p.project_tier as projectTier, c.client_name as clientName, u.user_id as userId, concat(u.first_name,' ', u.last_name) as userName, r.region_name as region, "
			+ "ap1Usr.user_id as approver1Id, concat(ap1Usr.first_name,' ', ap1Usr.last_name) as approver1Name, "
			+ "(ta.day1 +ta.day2 +ta.day3 +ta.day4 +ta.day5 +ta.day6 +ta.day7 +ta.day8 +ta.day9 +ta.day10 +ta.day11 +ta.day12 +ta.day13 +ta.day14 +ta.day15) as approver1FirstHours,ta.project_type as projectType1, ta.first_half_status as status1, "
			+ "ap2Usr.user_id as approver2Id, concat(ap2Usr.first_name,' ', ap2Usr.last_name) as approver2Name, "
			+ "(tf.day1 +tf.day2 +tf.day3 +tf.day4 +tf.day5 +tf.day6 +tf.day7 +tf.day8 +tf.day9 +tf.day10 +tf.day11 +tf.day12 +tf.day13 +tf.day14 +tf.day15) as approver2Hours,tf.project_type as projectType2, tf.first_half_status as status2, "
			+ "(ta.day16 +ta.day17 +ta.day18 +ta.day19 +ta.day20 +ta.day21 +ta.day22 +ta.day23 +ta.day24 +ta.day25 +ta.day26 +ta.day27 +ta.day28 +ta.day29 +ta.day30 +ta.day31) as approver1SecondHours, ta.second_half_status as appr1StatusSecond, "
			+ "(tf.day16 +tf.day17 +tf.day18 +tf.day19 +tf.day20 +tf.day21 +tf.day22 +tf.day23 +tf.day24 +tf.day25 +tf.day26 +tf.day27 +tf.day28 +tf.day29 +tf.day30 +tf.day31) as approver2SecondHours, tf.second_half_status as appr2StatusSecond "
			+ "from project p " + "left join user ap1Usr on ap1Usr.user_id= p.project_owner_user_id "
			+ "left join user ap2Usr on ap2Usr.user_id= p.onsite_lead_user_id "
			+ "left join client c on c.client_id= p.client_name_client_id "
			+ "left join allocation a on a.project_project_id= p.project_id and (month(a.start_date)<=?1 and year(a.start_date)<=?2 and month(a.end_date)>=?1 and year(a.end_date)>=?2) "
			+ "left join user u on u.user_id=a.user_user_id " + "left join region r on r.id= u.region_id "
			+ "left join tasktrack_approval ta on ta.project_project_id = p.project_id and ta.user_user_id= u.user_id and ta.month=?1 and ta.year=?2 "
			+ "left join tasktrack_approval_final tf on tf.project_project_id = p.project_id and tf.user_user_id= u.user_id and tf.month=?1 and tf.year=?2 "
			+ "where (month(p.start_date)<=?1 and year(p.start_date)<=?2 and month(p.end_date)>=?1 and year(p.end_date)>=?2) order by p.project_name asc, userName";


}
