package com.EMS.utility;

public class QueryConstants {

	public static final String FINANCE_APPROVED_HOURS_REPORT_QUERY = "(select p.project_id as projectId,p.project_name as projectName,p.project_tier as projectTier, c.client_name as clientName, u.user_id as userId, concat(u.last_name,' ', u.first_name) as userName, r.region_name as region, "
			+ "ap1Usr.user_id as approver1Id, concat(ap1Usr.last_name,' ', ap1Usr.first_name) as approver1Name, "
			+ "(COALESCE(ta.day1,0) +COALESCE(ta.day2,0) +COALESCE(ta.day3,0) +COALESCE(ta.day4,0) +COALESCE(ta.day5,0) +COALESCE(ta.day6,0) +COALESCE(ta.day7,0) +COALESCE(ta.day8,0) +COALESCE(ta.day9,0) +COALESCE(ta.day10,0) +COALESCE(ta.day11,0) +COALESCE(ta.day12,0) +COALESCE(ta.day13,0) +COALESCE(ta.day14,0) +COALESCE(ta.day15,0)) as approver1FirstHours,ta.project_type as projectType1, ta.first_half_status as status1, "
			+ "ap2Usr.user_id as approver2Id, concat(ap2Usr.last_name,' ', ap2Usr.first_name) as approver2Name, "
			+ "null as approver2Hours,null as projectType2, null as status2, "
			+ "(COALESCE(ta.day16,0) +COALESCE(ta.day17,0) +COALESCE(ta.day18,0) +COALESCE(ta.day19,0) +COALESCE(ta.day20,0) +COALESCE(ta.day21,0) +COALESCE(ta.day22,0) +COALESCE(ta.day23,0) +COALESCE(ta.day24,0) +COALESCE(ta.day25,0) +COALESCE(ta.day26,0) +COALESCE(ta.day27,0) +COALESCE(ta.day28,0) +COALESCE(ta.day29,0) +COALESCE(ta.day30,0) +COALESCE(ta.day31,0)) as approver1SecondHours, ta.second_half_status as appr1StatusSecond, "
			+ "null as approver2SecondHours, null as appr2StatusSecond " + "from project p "
			+ "left join user ap1Usr on ap1Usr.user_id= p.project_owner_user_id "
			+ "left join user ap2Usr on ap2Usr.user_id= p.onsite_lead_user_id "
			+ "left join client c on c.client_id= p.client_name_client_id "
			+ "join allocation a on a.project_project_id= p.project_id and (?1 between a.start_date and a.end_date or last_day(?1) between a.start_date and a.end_date) "
			+ "join user u on u.user_id=a.user_user_id " + "left join region r on r.id= u.region_id "
			+ "left join tasktrack_approval ta on ta.project_project_id = p.project_id and ta.user_user_id= u.user_id and ta.month=month(?1) and ta.year=year(?1)) "
			+ "union "
			+ "(select p.project_id as projectId,p.project_name as projectName,p.project_tier as projectTier, c.client_name as clientName, u.user_id as userId, concat(u.last_name,' ', u.first_name) as userName, r.region_name as region, "
			+ "ap1Usr.user_id as approver1Id, concat(ap1Usr.last_name,' ', ap1Usr.first_name) as approver1Name, "
			+ "null as approver1FirstHours,null as projectType1, null as status1, "
			+ "ap2Usr.user_id as approver2Id, concat(ap2Usr.last_name,' ', ap2Usr.first_name) as approver2Name, "
			+ "(COALESCE(tf.day1,0) +COALESCE(tf.day2,0) +COALESCE(tf.day3,0) +COALESCE(tf.day4,0) +COALESCE(tf.day5,0) +COALESCE(tf.day6,0) +COALESCE(tf.day7,0) +COALESCE(tf.day8,0) +COALESCE(tf.day9,0) +COALESCE(tf.day10,0) +COALESCE(tf.day11,0) +COALESCE(tf.day12,0) +COALESCE(tf.day13,0) +COALESCE(tf.day14,0) +COALESCE(tf.day15,0)) as approver2Hours,tf.project_type as projectType2, tf.first_half_status as status2, "
			+ "null as approver1SecondHours, null as appr1StatusSecond, "
			+ "(COALESCE(tf.day16,0) +COALESCE(tf.day17,0) +COALESCE(tf.day18,0) +COALESCE(tf.day19,0) +COALESCE(tf.day20,0) +COALESCE(tf.day21,0) +COALESCE(tf.day22,0) +COALESCE(tf.day23,0) +COALESCE(tf.day24,0) +COALESCE(tf.day25,0) +COALESCE(tf.day26,0) +COALESCE(tf.day27,0) +COALESCE(tf.day28,0) +COALESCE(tf.day29,0) +COALESCE(tf.day30,0) +COALESCE(tf.day31,0)) as approver2SecondHours, tf.second_half_status as appr2StatusSecond "
			+ "from project p " + "left join user ap1Usr on ap1Usr.user_id= p.project_owner_user_id "
			+ "left join user ap2Usr on ap2Usr.user_id= p.onsite_lead_user_id "
			+ "left join client c on c.client_id= p.client_name_client_id "
			+ "join allocation a on a.project_project_id= p.project_id and (?1 between a.start_date and a.end_date or last_day(?1) between a.start_date and a.end_date) "
			+ "join user u on u.user_id=a.user_user_id " + "left join region r on r.id= u.region_id "
			+ "left join tasktrack_approval_final tf on tf.project_project_id = p.project_id and tf.user_user_id= u.user_id and tf.month=month(?1) and tf.year=year(?1)) order by projectId asc, userName";

}
