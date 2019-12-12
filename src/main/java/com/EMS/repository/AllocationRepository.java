package com.EMS.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.AllocationModel;

public interface AllocationRepository extends JpaRepository<AllocationModel, Long> {

	public AllocationModel findOneByProjectProjectIdAndUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIsBillableAndActiveOrderByEndDateDesc(
			Long projectId, Long userId, Date endDate, Date startDate, Boolean isBillable, Boolean isActive);

	@Query(value = "select project_project_id as projectId, count(alloc_id) as count from allocation where (?1 between start_date and end_date) group by project_project_id", nativeQuery = true)
	public Object[][] getActiveAllocationCountByProjects(String currentDate);
}
