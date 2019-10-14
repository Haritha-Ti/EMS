package com.EMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EMS.model.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
	
	
	

}
