package com.EMS.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.AllocationModel;

public interface AllocationRepository extends JpaRepository<AllocationModel, Long> {

	public AllocationModel findOneByProjectProjectIdAndUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIsBillableOrderByEndDateDesc(
			Long projectId, Long userId, Date endDate, Date startDate, Boolean isBillable);

	@Query(value = "select project_project_id as projectId, count(alloc_id) as count from allocation where (?1 between start_date and end_date) group by project_project_id", nativeQuery = true)
	public Object[][] getActiveAllocationCountByProjects(String currentDate);

	public List<AllocationModel> findByProjectProjectIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
			Long projectId, Date endDate, Date startDate);

	public List<AllocationModel> findByUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long userId,
			Date endDate, Date startDate);
	
	List<AllocationModel> findByUserUserIdAndProjectProjectId(Long userId, Long projectId);
}
