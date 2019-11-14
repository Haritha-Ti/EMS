package com.EMS.repository;

import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.TaskTrackApproval;

public interface AuditRepository extends JpaRepository<TaskTrackApproval,Long>{
@Query(value="SELECT u.user_name,p.project_name,t.revtype,DATE_FORMAT(t.trx_date, '%d/%m/%Y'),t.user_in_action,DATE_FORMAT(t.approved_date, '%d/%m/%Y'),t.day1,t.day2,t.day3,t.day4,t.day5,t.day6,t.day7,t.day8,\r\n" + 
		"t.day9,t.day10,t.day11,t.day12,t.day13,t.day14,t.day15,t.day16,t.day17,t.day18,t.day19,t.day20,\r\n" + 
		"t.day21,t.day22,t.day23,t.day24,t.day25,t.day26,t.day27,t.day28,t.day29,t.day30,t.day31,t.first_half_status,t.`month`,\r\n" + 
		"t.project_type,t.second_half_status,t.`year` FROM tasktrack_approval_aud t\r\n" + 
		"join `user` u on u.user_id=t.user_user_id\r\n" + 
		"join `project` p on p.project_id=t.project_project_id\r\n" + 
		"where t.project_project_id=:projectId and t.user_user_id=:userId and STR_TO_DATE(t.trx_date, '%d/%m/%Y')>=:fromDate and STR_TO_DATE(t.trx_date, '%d/%m/%Y')<=:toDate\r\n" + 
		"order by 4 asc ;",nativeQuery=true)
	public List<JSONObject> getAuditDataByUserId(Long projectId,Long userId, Date fromDate, Date toDate) ;

@Query(value="SELECT u.user_name,p.project_name,t.revtype,DATE_FORMAT(t.trx_date, '%d/%m/%Y'),t.user_in_action,DATE_FORMAT(t.approved_date, '%d/%m/%Y'),t.day1,t.day2,t.day3,t.day4,t.day5,t.day6,t.day7,t.day8,\r\n" + 
		"t.day9,t.day10,t.day11,t.day12,t.day13,t.day14,t.day15,t.day16,t.day17,t.day18,t.day19,t.day20,\r\n" + 
		"t.day21,t.day22,t.day23,t.day24,t.day25,t.day26,t.day27,t.day28,t.day29,t.day30,t.day31,t.first_half_status,t.`month`,\r\n" + 
		"t.project_type,t.second_half_status,t.`year` FROM tasktrack_approval_final_aud t\r\n" + 
		"join `user` u on u.user_id=t.user_user_id\r\n" + 
		"join `project` p on p.project_id=t.project_project_id\r\n" + 
		"where t.project_project_id=:projectId and t.user_user_id=:userId and STR_TO_DATE(t.trx_date, '%d/%m/%Y')>=:fromDate and STR_TO_DATE(t.trx_date, '%d/%m/%Y')<=:toDate\r\n" + 
		"order by 4 asc ;",nativeQuery=true)
public List<JSONObject> getAuditDataByUserIdForFinal(Long projectId,Long userId, Date fromDate, Date toDate);

@Query(value=" select u.user_id,u.rev,u.revtype,DATE_FORMAT(STR_TO_DATE(u.trx_date, '%m/%d/%Y %h:%i%p'),'%Y-%m-%d'),u.user_in_action,u.recruiter,u.active,\r\n" + 
		" u.blood_group,u.contact,u.cpp_level,DATE_FORMAT(u.dob, '%d/%m/%Y') as dob,u.email,u.emp_category,u.emp_id,u.employment_type,u.first_name,\r\n" + 
		" u.last_name,u.qualification,u.referred_by,u.termination_date,u.user_name,u.contractor_contractor_id,\r\n" + 
		" u.cpplevels_id,u.department_department_id,u.region_id,u.role_role_id,u.timezone_id from user_aud u where u.user_id=?1",nativeQuery=true)
List<JSONObject> getAuditUserDetailsById(Long userId);

@Query(value=" select u.user_id,u.rev,u.revtype,DATE_FORMAT(STR_TO_DATE(u.trx_date, '%m/%d/%Y %h:%i%p'),'%Y-%m-%d'),u.user_in_action,u.recruiter,u.active,\r\n" + 
		" u.blood_group,u.contact,u.cpp_level,DATE_FORMAT(u.dob, '%d/%m/%Y') as dob,u.email,u.emp_category,u.emp_id,u.employment_type,u.first_name,\r\n" + 
		" u.last_name,u.qualification,u.referred_by,u.termination_date,u.user_name,u.contractor_contractor_id,\r\n" + 
		" u.cpplevels_id,u.department_department_id,u.region_id,u.role_role_id,u.timezone_id from user_aud u where u.user_id=?1\r\n" + 
		" and DATE_FORMAT(STR_TO_DATE(u.trx_date, '%m/%d/%Y %h:%i%p'),'%Y-%m-%d')>=?2 and DATE_FORMAT(STR_TO_DATE(u.trx_date, '%m/%d/%Y %h:%i%p'),'%Y-%m-%d')<=?3",nativeQuery=true)
List<JSONObject> getAuditUserDetailsByDateRange(Long userId, Date fromDate, Date toDate);

@Query(value=" SELECT  aud.revtype,DATE_FORMAT(STR_TO_DATE(aud.trx_date, '%m/%d/%Y %h:%i%p'),'%d-%m-%Y %h:%i %p') as trx_date,aud.user_in_action ,aud.client_point_of_contact  , DATE_FORMAT(aud.end_date, '%d/%m/%Y') as end_date ,aud.estimated_hours ,aud.is_billable ,aud.ispoc  , "
		+ " pp.project_name as parent_project,aud.project_category ,aud.project_code ,aud.project_details ,aud.project_name ,aud.project_status ,aud.project_tier ,aud.project_type ,aud.project_ref_id , DATE_FORMAT(aud.releasing_date , '%d/%m/%Y') as releasing_date , DATE_FORMAT(aud.start_date, '%d/%m/%Y') as start_date ,c.client_name ,ct.contract_type_name  ,concat(olu.first_name,' ',olu.last_name) as onsite_lead , concat(pwu.first_name,' ',pwu.last_name) as project_owner"
		+ " FROM project_aud aud"
		+ " Left join project pp on aud.parent_project_id=pp.project_id"
		+ " left join user olu on  aud.onsite_lead_user_id=olu.user_id"
		+ " left join user pwu on  aud.project_owner_user_id =pwu.user_id"
		+ " left join  contract_type ct on aud.contract_contract_type_id=ct.contract_type_id"
		+ " left join client c on aud.client_name_client_id=c.client_id"
		+ " where aud.project_id=?1 order by trx_date " ,nativeQuery=true)

public List<JSONObject> getProjectAuditDataByProjectId(Long projectId);


@Query(value=" SELECT  aud.revtype,DATE_FORMAT(STR_TO_DATE(aud.trx_date, '%m/%d/%Y %h:%i%p'),'%d-%m-%Y %h:%i %p') as trx_date,aud.user_in_action ,aud.client_point_of_contact  , DATE_FORMAT(aud.end_date, '%d/%m/%Y') as end_date ,aud.estimated_hours ,aud.is_billable ,aud.ispoc  , "
		+ " pp.project_name as parent_project,aud.project_category ,aud.project_code ,aud.project_details ,aud.project_name ,aud.project_status ,aud.project_tier ,aud.project_type ,aud.project_ref_id , DATE_FORMAT(aud.releasing_date , '%d/%m/%Y') as releasing_date , DATE_FORMAT(aud.start_date, '%d/%m/%Y') as start_date ,c.client_name ,ct.contract_type_name  ,concat(olu.first_name,' ',olu.last_name) as onsite_lead , concat(pwu.first_name,' ',pwu.last_name) as project_owner"
		+ " FROM project_aud aud"
		+ " Left join project pp on aud.parent_project_id=pp.project_id"
		+ " left join user olu on  aud.onsite_lead_user_id=olu.user_id"
		+ " left join user pwu on  aud.project_owner_user_id =pwu.user_id"
		+ " left join  contract_type ct on aud.contract_contract_type_id=ct.contract_type_id"
		+ " left join client c on aud.client_name_client_id=c.client_id"
		+ " where aud.project_id=?1  and  DATE_FORMAT(STR_TO_DATE(aud.trx_date, '%m/%d/%Y %h:%i%p'),'%Y-%m-%d')>=?2  and DATE_FORMAT(STR_TO_DATE(aud.trx_date, '%m/%d/%Y %h:%i%p'),'%Y-%m-%d')<=?3"
		+ " order by trx_date " ,nativeQuery=true)

public List<JSONObject> getProjectAuditDataByProjectIdAndDateRange(Long projectId,Date startDate,Date endDate);

}