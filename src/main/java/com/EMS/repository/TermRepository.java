package com.EMS.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.EMS.model.Technology;
import com.EMS.model.UserModel;
import com.EMS.model.UserTermination;
import com.EMS.utility.DbConnectionUtility;

@Repository
public class TermRepository extends DbConnectionUtility{

	public List<UserTermination> getTerminationList(Date startDate,Date endDate) {
		String sql = "SELECT * FROM user_termination where date(termination_date) between '2019-01-01' AND '2019-05-01'";

		List<UserTermination> result = jdbcTemplate.query(sql, new RowMapper<UserTermination>() {

			@Override
			public UserTermination mapRow(ResultSet rs, int rowNum) throws SQLException {
				UserTermination tech = new UserTermination();
				tech.setTerminationDate(rs.getDate(4));
				tech.setTermReason(rs.getString(2));
				UserModel user=new UserModel();
				user.setUserId(rs.getLong(5));
				tech.setConsultant(user);
				tech.setTermType(rs.getString(3));
				return tech;
			}
		});

		System.out.println("list :" + result.size());

		return result;
	}

	public int deleteByProjectId(long projectId) {
		// TODO Auto-generated method stub
		
		String str =  "Delete FROM project_region WHERE project_region.project_id_project_id= '"+projectId+"'";
		int result=0;
		try {
		 result = jdbcTemplate.update(str);	
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
