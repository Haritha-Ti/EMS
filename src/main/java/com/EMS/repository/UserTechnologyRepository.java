package com.EMS.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
public class UserTechnologyRepository extends DbConnectionUtility {

//	@Modifying
//	@Transactional
//	@Query(value ="delete from EMS.user_technology where EMS.user_technology.user_user_id = 124 ",nativeQuery = true)
//	@Query(value = "delete from UserTechnology u where u.user.userId = 125")
//	UserTechnology deleteByUserId(long userId);

	public int deleteByUserId(long userId) {
		String sql = "delete from user_technology where user_user_id='" + userId + "'";
		int result = 0;
		try {
			result = jdbcTemplate.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int save(UserTechnology user) {
		String sql = "INSERT INTO user_technology (experience,technology_technology_id,user_user_id,skill_level,comment)VALUES('"
				+ user.getExperience() + "','" + user.getTechnology().getTechnologyId() + "','"
				+ user.getUser().getUserId() + "','" + user.getSkill_level() + "','" + user.getComment() + "')";
		int result = 0;
		try {
			result = jdbcTemplate.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Boolean checkExistanceOfUserId(Long userId) {
		String sql = "SELECT COUNT(*) FROM user_technology WHERE user_user_id=?";
		int result = 0;
		result = jdbcTemplate.queryForObject(sql, new Object[] { userId }, Integer.class);

		if (result > 0)
			return true;
		else
			return false;

	}

	public List<Technology> getPrimarySkills(long userId) {

		String sql = "SELECT user_technology.technology_technology_id,technology.technology_name FROM user_technology INNER JOIN technology ON user_technology.technology_technology_id=technology.technology_id where user_technology.user_user_id='"
				+ userId + "'";

		List<Technology> result = jdbcTemplate.query(sql, new RowMapper<Technology>() {

			@Override
			public Technology mapRow(ResultSet rs, int rowNum) throws SQLException {
				Technology tech = new Technology();
				tech.setTechnologyName(rs.getString(2));
				return tech;
			}
		});

		return result;
	}

	public JSONObject getSkillsByUserId(Long userId) {
		String sql = "SELECT  ut.user_technology_id,t.technology_id,t.technology_name,ut.experience,ut.skill_level FROM user_technology ut join technology t on t.technology_id=ut.technology_technology_id where user_user_id='"
				+ userId + "'";
		JSONObject skillsObject = new JSONObject();

		JSONArray primaryskill = new JSONArray();
		JSONArray seconSkill = new JSONArray();
		List<JSONObject> result = jdbcTemplate.query(sql, new RowMapper<JSONObject>() {

			@Override
			public JSONObject mapRow(ResultSet rs, int rowNum) throws SQLException {
				JSONObject obj = new JSONObject();
				obj.put("userTechnologyId", rs.getLong(1));
				obj.put("technologyName", rs.getString(3));
				obj.put("experience", rs.getDouble(4));
				obj.put("skillLevel", rs.getInt(5));
				obj.put("technologyId", rs.getLong(2));

				if (rs.getInt(5) == 1)
					primaryskill.add(obj);
				else
					seconSkill.add(obj);

				return obj;

			}
		});

		skillsObject.put("primarySkills", primaryskill);
		skillsObject.put("secondarySkills", seconSkill);

		return skillsObject;
//		return result;
	}



	public UserTechnology getUsertechnology(Long userTechId) {
		String sql="SELECT * FROM user_technology where user_technology_id='"+userTechId+"'";
		
		List<UserTechnology> result = jdbcTemplate.query(sql, new RowMapper<UserTechnology>() {

			@Override
			public UserTechnology mapRow(ResultSet rs, int rowNum) throws SQLException {
				UserTechnology userTech=new UserTechnology();
				userTech.setUserTechnologyId(rs.getLong("user_technology_id"));
				userTech.setComment(rs.getString("comment"));
				userTech.setExperience(rs.getDouble("experience"));
				userTech.setSkill_level(rs.getInt("skill_level"));
				Technology tech=new Technology();
				tech.setTechnologyId(rs.getLong("technology_technology_id"));
				userTech.setTechnology(tech);
				UserModel user=new UserModel();
				user.setUserId(rs.getLong("user_user_id"));
				userTech.setUser(user);
				
				return userTech;

			}
		});
		
		if(result.isEmpty()) 
			return null;
		else
			return result.get(0);	
	}

	public int deleteUserTechnology(Long userTechnologyId) {
		String sql="delete FROM user_technology where user_technology_id='"+userTechnologyId+"'";
		
		int result = 0;
		try {
			result = jdbcTemplate.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	public int updateusertechnology(UserTechnology userTechnology) {
		String sql = "update user_technology set experience='"+userTechnology.getExperience()+"',technology_technology_id='"+userTechnology.getTechnology().getTechnologyId()+"',user_user_id='"+userTechnology.getUser().getUserId()+"',comment='"+userTechnology.getComment()+"',skill_level='"+userTechnology.getSkill_level()+"' where user_technology_id='"+userTechnology.getUserTechnologyId()+"'";
		int result = 0;
		try {
			result = jdbcTemplate.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
