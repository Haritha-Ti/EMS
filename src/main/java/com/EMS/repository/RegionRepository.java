package com.EMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.Region;

public interface RegionRepository extends JpaRepository<Region, Long>{

	@Query("SELECT s FROM Region s where s.isDeleted = false")
	List<Region> getlistofRegions();

	/*
	 * @Query("Select s From Region where isDeleted = false") List<Region>
	 * getlist();
	 */

}
