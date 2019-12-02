package com.EMS.repository;

import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.TaskTrackApproval;

public interface AuditRepository extends JpaRepository<TaskTrackApproval,Long>{
@Query(value="SELECT u.user_name,p.project_name,"
		+ "  case  when t.revtype=0 then 'Created'  when t.revtype=1 then 'Updated'  when t.revtype=2 then 'Deleted' else null end as revtype , "
		+ " DATE_FORMAT(t.trx_date,'%d-%m-%Y %H:%i:%s') as trx_date ,concat(uia.first_name,' ',uia.last_name) as user_in_action,\r\n" + 
		"DATE_FORMAT(t.approved_date, '%d/%m/%Y') as approved_date,coalesce(t.day1,0) as day1 ,coalesce(t.day2,0) as day2,coalesce(t.day3,0) as day3,coalesce(t.day4,0) as day4,coalesce(t.day5,0) as day5 ,coalesce(t.day6,0) as day6,coalesce(t.day7,0) as day7,coalesce(t.day8,0) as day8,\r\n" + 
		"coalesce(t.day9,0) as day9 ,coalesce(t.day10,0) as day10,coalesce(t.day11,0) as day11,coalesce(t.day12,0) as day12,coalesce(t.day13,0) as day13,coalesce(t.day14,0) as day14,coalesce(t.day15,0) as day15,coalesce(t.day16,0) as day16,coalesce(t.day17,0) as day17,coalesce(t.day18,0) as day18 ,coalesce(t.day19,0) as day19 ,coalesce(t.day20,0) as day20,\r\n" + 
		"coalesce(t.day21,0) as day21,coalesce(t.day22,0) as day22,coalesce(t.day23,0) as day23 ,coalesce(t.day24,0) as day24 ,coalesce(t.day25,0) as day25,coalesce(t.day26,0) as day26,coalesce(t.day27,0) as day27,coalesce(t.day28,0) as day28,coalesce(t.day29,0) as day29,coalesce(t.day30,0) as day30,coalesce(t.day31,0) as day31 ,t.first_half_status,t.`month`,\r\n" + 
		"t.project_type,t.second_half_status,t.`year` FROM tasktrack_approval_aud t\r\n" + 
		"join `user` u on u.user_id=t.user_user_id\r\n" + 
		"join `project` p on p.project_id=t.project_project_id\r\n" + 
		" Left join  user uia on t.user_in_action=uia.user_id " +
		" where t.user_user_id=?2 and t.project_project_id=?1 "
		+ " and DATE_FORMAT(t.trx_date,'%Y-%m-%d') >?3 -  INTERVAL 1 DAY and  DATE_FORMAT(t.trx_date,'%Y-%m-%d') <?4   \r\n" 
		+ 
		" order by trx_date ",nativeQuery=true)
	public List<JSONObject> getAuditDataByUserId(Long projectId,Long userId, Date fromDate, Date toDate) ;

@Query(value="SELECT u.user_name,p.project_name,"
		+ "  case  when t.revtype=0 then 'Created'  when t.revtype=1 then 'Updated'  when t.revtype=2 then 'Deleted' else null end as revtype , "
		+ " DATE_FORMAT(t.trx_date,'%d-%m-%Y %H:%i:%s') as trx_date ,concat(uia.first_name,' ',uia.last_name) as user_in_action,\r\n" + 
		"DATE_FORMAT(t.approved_date, '%d/%m/%Y') as approved_date,coalesce(t.day1,0) as day1 ,coalesce(t.day2,0) as day2,coalesce(t.day3,0) as day3,coalesce(t.day4,0) as day4,coalesce(t.day5,0) as day5 ,coalesce(t.day6,0) as day6,coalesce(t.day7,0) as day7,coalesce(t.day8,0) as day8,\r\n" + 
		"coalesce(t.day9,0) as day9 ,coalesce(t.day10,0) as day10,coalesce(t.day11,0) as day11,coalesce(t.day12,0) as day12,coalesce(t.day13,0) as day13,coalesce(t.day14,0) as day14,coalesce(t.day15,0) as day15,coalesce(t.day16,0) as day16,coalesce(t.day17,0) as day17,coalesce(t.day18,0) as day18 ,coalesce(t.day19,0) as day19 ,coalesce(t.day20,0) as day20,\r\n" + 
		"coalesce(t.day21,0) as day21,coalesce(t.day22,0) as day22,coalesce(t.day23,0) as day23 ,coalesce(t.day24,0) as day24 ,coalesce(t.day25,0) as day25,coalesce(t.day26,0) as day26,coalesce(t.day27,0) as day27,coalesce(t.day28,0) as day28,coalesce(t.day29,0) as day29,coalesce(t.day30,0) as day30,coalesce(t.day31,0) as day31 ,t.first_half_status,t.`month`,\r\n" + 
		"t.project_type,t.second_half_status,t.`year` FROM tasktrack_approval_final_aud t\r\n" + 
		"join `user` u on u.user_id=t.user_user_id\r\n" + 
		"join `project` p on p.project_id=t.project_project_id\r\n" + 
		" Left join  user uia on t.user_in_action=uia.user_id " +
		" where t.user_user_id=?2 and t.project_project_id=?1 "
		+ " and DATE_FORMAT(t.trx_date,'%Y-%m-%d') >?3 - INTERVAL 1 DAY and  DATE_FORMAT(t.trx_date,'%Y-%m-%d') <?4   \r\n" 
		+ 
		" order by trx_date ",nativeQuery=true)
public List<JSONObject> getAuditDataByUserIdForFinal(Long projectId,Long userId, Date fromDate, Date toDate);

@Query(value="SELECT case  when u.revtype=0 then 'Created'  when u.revtype=1 then 'Updated'  when u.revtype=2 then 'Deleted' else null end as revtype ,  "
		+ " DATE_FORMAT(u.trx_date,'%d-%m-%Y %H:%i:%s') as trx_date,concat(us.first_name,' ',us.last_name) as user_in_action,u.recruiter,u.active,\r\n" + 
		"u.blood_group,u.contact,u.cpplevels_id,DATE_FORMAT(u.dob, '%Y-%m-%d') as dob,u.email,u.emp_category,\r\n" + 
		"u.emp_id,u.employment_type,u.first_name,u.last_name,u.qualification,u.referred_by,\r\n" + 
		"DATE_FORMAT(u.termination_date,'%Y-%m-%d') as termination_date,u.user_name,ec.contractor_name,cl.level_name,d.department_name,re.region_name,r.role_name,tz.timezone_name from user_aud u\r\n" + 
		"Left join  `user` us on u.user_in_action=us.user_id\r\n" + 
		"Left join role r on u.role_role_id=r.role_id\r\n" + 
		"Left join employee_contractors ec on u.contractor_contractor_id=ec.contractor_id\r\n" + 
		"Left join region re on u.region_id=re.id\r\n" + 
		"Left join timezone tz on u.timezone_id=tz.id\r\n" + 
		"Left join department d on u.department_department_id=d.department_id\r\n" + 
		"Left join cpp_level cl on u.cpplevels_id=cl.id\r\n" + 
		"where u.user_id=?1  order by trx_date ",nativeQuery=true)
List<JSONObject> getAuditUserDetailsById(Long userId);

@Query(value="SELECT  case  when u.revtype=0 then 'Created'  when u.revtype=1 then 'Updated'  when u.revtype=2 then 'Deleted' else null end as revtype , "
		+ " DATE_FORMAT(u.trx_date,'%d-%m-%Y %H:%i:%s') as trx_date,concat(us.first_name,' ',us.last_name) as user_in_action,u.recruiter,u.active,\r\n" + 
		"u.blood_group,u.contact,u.cpplevels_id,DATE_FORMAT(u.dob, '%Y-%m-%d') as dob,u.email,u.emp_category,\r\n" + 
		"u.emp_id,u.employment_type,u.first_name,u.last_name,u.qualification,u.referred_by,\r\n" + 
		"DATE_FORMAT(u.termination_date,'%Y-%m-%d') as termination_date,u.user_name,ec.contractor_name,cl.level_name,d.department_name,re.region_name,r.role_name,tz.timezone_name from user_aud u\r\n" + 
		"Left join  `user` us on u.user_in_action=us.user_id\r\n" + 
		"Left join role r on u.role_role_id=r.role_id\r\n" + 
		"Left join employee_contractors ec on u.contractor_contractor_id=ec.contractor_id\r\n" + 
		"Left join region re on u.region_id=re.id\r\n" + 
		"Left join timezone tz on u.timezone_id=tz.id\r\n" + 
		"Left join department d on u.department_department_id=d.department_id\r\n" + 
		"Left join cpp_level cl on u.cpplevels_id=cl.id\r\n" + 
		"where u.user_id=?1 \r\n" + 
		"and DATE_FORMAT(u.trx_date,'%Y-%m-%d')>?2 - INTERVAL 1 DAY  \r\n" + 
		"and DATE_FORMAT(u.trx_date,'%Y-%m-%d')<?3   order by trx_date ",nativeQuery=true)
List<JSONObject> getAuditUserDetailsByDateRange(Long userId, Date fromDate, Date toDate);

@Query(value=" SELECT  case  when aud.revtype=0 then 'Created'  when aud.revtype=1 then 'Updated'  when aud.revtype=2 then 'Deleted' else null end as revtype ,"
		+ " DATE_FORMAT(aud.trx_date,'%d-%m-%Y %H:%i:%s') as trx_date,concat(uia.first_name,' ',uia.last_name) as user_in_action ,aud.client_point_of_contact  , DATE_FORMAT(aud.end_date, '%d/%m/%Y') as end_date ,aud.estimated_hours ,"
		+ " case when aud.is_billable=1  then 'Billable' when  aud.is_billable=0 then 'Non-Billable' else null end as is_billable ,"
		+ " case when  aud.project_type=1 then 'Ic' when aud.project_type=0 then 'Non-Ic' else null end as project_type   , "
		+ " pp.project_name as parent_project,"
		+ " case when aud.project_category=1 then 'Development' when aud.project_category=2 then 'Bench' when aud.project_category=3 then 'HR'  when aud.project_category=4 then 'Business Development' else null end  as project_category  ,"
		+ " aud.project_code ,aud.project_details ,aud.project_name ,"
		+ " case when aud.project_status=0 then 'Inactive' when aud.project_status=1 then 'Active' else null end as project_status  ,"
		+ " case  when aud.project_tier=1 then 'Tier 1'  when aud.project_tier=2 then 'Tier 2'   else null end as project_tier , "
		+ " DATE_FORMAT(aud.releasing_date , '%d/%m/%Y') as releasing_date , DATE_FORMAT(aud.start_date, '%d/%m/%Y') as start_date ,c.client_name ,ct.contract_type_name  ,concat(olu.first_name,' ',olu.last_name) as onsite_lead , concat(pwu.first_name,' ',pwu.last_name) as project_owner"
		+ " FROM project_aud aud"
		+ " Left join project pp on aud.parent_project_id=pp.project_id"
		+ " Left join user olu on  aud.onsite_lead_user_id=olu.user_id"
		+ " Left join user pwu on  aud.project_owner_user_id =pwu.user_id"
		+ " Left join  contract_type ct on aud.contract_contract_type_id=ct.contract_type_id"
		+ " Left join client c on aud.client_name_client_id=c.client_id"
		+ " Left join  user uia on aud.user_in_action=uia.user_id"
		+ " where aud.project_id=?1 order by trx_date " ,nativeQuery=true)

public List<JSONObject> getProjectAuditDataByProjectId(Long projectId);


@Query(value=" SELECT  case  when aud.revtype=0 then 'Created'  when aud.revtype=1 then 'Updated'  when aud.revtype=2 then 'Deleted' else null end as revtype  ,"
		+ " DATE_FORMAT(aud.trx_date,'%d-%m-%Y %H:%i:%s') as trx_date,concat(uia.first_name,' ',uia.last_name) as user_in_action ,aud.client_point_of_contact  , DATE_FORMAT(aud.end_date, '%d/%m/%Y') as end_date ,aud.estimated_hours ,"
		+ "  case when aud.is_billable=1  then 'Billable' when  aud.is_billable=0 then 'Non-Billable' else null end as is_billable  , "
		+ "  case when  aud.project_type=1 then 'Ic' when aud.project_type=0 then 'Non-Ic' else null end as project_type , "
		+ " pp.project_name as parent_project,"
		+ " case when aud.project_category=1 then 'Development' when aud.project_category=2 then 'Bench' when aud.project_category=3 then 'HR'  when aud.project_category=4 then 'Business Development' else null end as project_category  ,"
		+ " aud.project_code ,aud.project_details ,aud.project_name ,"
		+ " case when aud.project_status=0 then 'Inactive' when aud.project_status=1 then 'Active' else null end as project_status , "
		+ " case  when aud.project_tier=1 then 'Tier 1'  when aud.project_tier=2 then 'Tier 2'   else null end as project_tier ,"
		+ " DATE_FORMAT(aud.releasing_date , '%d/%m/%Y') as releasing_date , DATE_FORMAT(aud.start_date, '%d/%m/%Y') as start_date ,c.client_name ,ct.contract_type_name  ,concat(olu.first_name,' ',olu.last_name) as onsite_lead , concat(pwu.first_name,' ',pwu.last_name) as project_owner"
		+ " FROM project_aud aud"
		+ " Left join project pp on aud.parent_project_id=pp.project_id"
		+ " Left join user olu on  aud.onsite_lead_user_id=olu.user_id"
		+ " Left join user pwu on  aud.project_owner_user_id =pwu.user_id"
		+ " Left join  contract_type ct on aud.contract_contract_type_id=ct.contract_type_id"
		+ " Left join client c on aud.client_name_client_id=c.client_id"
		+ " Left join  user uia on aud.user_in_action=uia.user_id"
		+ " where aud.project_id=?1  and  DATE_FORMAT(aud.trx_date,'%Y-%m-%d') >?2 - INTERVAL 1 DAY    and DATE_FORMAT(aud.trx_date,'%Y-%m-%d') <?3    "
		+ " order by trx_date " ,nativeQuery=true)

public List<JSONObject> getProjectAuditDataByProjectIdAndDateRange(Long projectId,Date startDate,Date endDate);

}
