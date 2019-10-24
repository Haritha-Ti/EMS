package com.EMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.Region;

public interface RegionRepository extends JpaRepository<Region, Long>{

	@Query("SELECT s FROM Region s where s.isDeleted = false order by s.region_name")
	List<Region> getlistofRegions();

	@Query(value = "SELECT timezone_code,timezone_name,id FROM timezone order by timezone_name",nativeQuery = true)
	List<Object[]> getListofTimezones();

	/*
	 * @Query("Select s From Region where isDeleted = false") List<Region>
	 * getlist();
	 */

}
