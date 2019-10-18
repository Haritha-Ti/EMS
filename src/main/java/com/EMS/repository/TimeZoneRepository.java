package com.EMS.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.TimeZoneModel;

public interface TimeZoneRepository extends JpaRepository<TimeZoneModel, Long>{

	@Query("SELECT s FROM TimeZoneModel s")
	ArrayList<TimeZoneModel> getTimeZones1();

}
