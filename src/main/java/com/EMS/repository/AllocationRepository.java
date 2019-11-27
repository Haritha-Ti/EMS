package com.EMS.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EMS.model.AllocationModel;

public interface AllocationRepository extends JpaRepository<AllocationModel, Long> {

	public AllocationModel findOneByProjectProjectIdAndUserUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIsBillableAndActiveOrderByEndDateDesc(
			Long projectId, Long userId, Date startDate, Date endDate, Boolean isBillable, Boolean isActive);
}
