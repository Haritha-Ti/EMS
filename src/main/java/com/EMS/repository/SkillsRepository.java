package com.EMS.repository;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.SkillsModel;

public interface SkillsRepository extends JpaRepository<SkillsModel,Long>{
	
	
	
	@Query(" SELECT s.primarySkills as primaryskills,s.secondarySkills as secondaryskills,s.otherSkills as otherskills FROM SkillsModel s  WHERE userId.userId=?1")
	List<JSONObject>  getSkillsByUserId(Long userID);
	
	@Query(" SELECT s FROM SkillsModel s  WHERE userId.userId=?1  ")
	List<SkillsModel>  getAllSkillsByUserId(Long userID);
	

}
