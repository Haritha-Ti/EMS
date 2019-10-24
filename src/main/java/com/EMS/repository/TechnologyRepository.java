package com.EMS.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.Technology;

public interface TechnologyRepository extends JpaRepository<Technology, Long>{

	@Query(value = "select user_technology.experience,technology.technology_id from technology JOIN user_technology ON technology.technology_id = user_technology.technology_technology_id where user_technology.user_user_id = ?1 ",nativeQuery = true)
	List<Object[]> getUserTechnologyList(Long userId);
	
	@Query(value = "SELECT tec.technology_id,tec.technology_name FROM `user_technology` as usrtec JOIN technology as tec ON usrtec.technology_technology_id=tec.technology_id where usrtec.user_user_id=?1",nativeQuery = true)
	List<Object[]> gettechnology(long userId);
	
	@Query(value = "SELECT count(*) FROM technology  WHERE technology_name=?1",nativeQuery = true)
	int findTechnology(String technologyName);

	@Query(value = "SELECT t FROM Technology t order by technologyName")
	List<Technology> getTechnologies();

	@Query(value = "SELECT t FROM Technology t order by technologyName")
	ArrayList<Technology> getAll();

	
	
}
