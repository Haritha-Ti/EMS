package com.EMS.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.EMS.model.BenchProjectReportModel;
import com.EMS.model.ProjectReportModel;
import com.EMS.model.Technology;
import com.EMS.model.UserModel;
import com.EMS.model.UserTechnology;
import com.EMS.utility.BenchReportRowMapper;
import com.EMS.utility.DbConnectionUtility;
import com.EMS.utility.ReportRowMapper;

import ch.qos.logback.classic.db.names.DBNameResolver;

@Repository
public class UserTechnologyRepository extends DbConnectionUtility{

//	@Modifying
//	@Transactional
//	@Query(value ="delete from EMS.user_technology where EMS.user_technology.user_user_id = 124 ",nativeQuery = true)
//	@Query(value = "delete from UserTechnology u where u.user.userId = 125")
//	UserTechnology deleteByUserId(long userId);
	
	
	public int  deleteByUserId (long userId){
		String sql ="delete from user_technology where user_user_id='"+userId+"'";
		int result=0;
		try {
		 result = jdbcTemplate.update(sql);	
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int  save (UserTechnology user){
		String sql ="INSERT INTO user_technology (experience,technology_technology_id,user_user_id,skill_level,comment)VALUES('"+user.getExperience()+"','"+user.getTechnology().getTechnologyId()+"','"+user.getUser().getUserId()+"','"+user.getSkill_level()+"','"+user.getComment()+"')";
		int result=0;
		try {
		 result = jdbcTemplate.update(sql);	
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Boolean checkExistanceOfUserId(Long userId) {
		String sql="SELECT COUNT(*) FROM user_technology WHERE user_user_id=?";
		int result = 0;
		result = jdbcTemplate.queryForObject(sql, new Object[] { userId }, Integer.class);
		
		if(result>0)
			return true;
		else
			return false;
		
	}

	public List<Technology> getPrimarySkills(long userId) {
		
		String sql="SELECT user_technology.technology_technology_id,technology.technology_name FROM user_technology INNER JOIN technology ON user_technology.technology_technology_id=technology.technology_id where user_technology.user_user_id='"+userId+"'";
		
		List<Technology> result = jdbcTemplate.query(sql, new RowMapper<Technology>(){

			@Override
			public Technology mapRow(ResultSet rs, int rowNum) throws SQLException {
				Technology tech=new Technology();
				tech.setTechnologyName(rs.getString(2));
				return tech;
			}});
		
		return result;
	}

	

}
