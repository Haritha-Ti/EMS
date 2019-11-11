package com.EMS.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.CronModel;

public interface CronRepository extends JpaRepository<CronModel, Long>{

	@Query(value="Select * from cron_model",nativeQuery=true)
	List<CronModel> getCronDates();

	
}
