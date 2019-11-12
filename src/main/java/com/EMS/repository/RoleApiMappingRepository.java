package com.EMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EMS.model.Resources;
import com.EMS.model.RoleApiMapping;

public interface RoleApiMappingRepository extends JpaRepository<RoleApiMapping, Long>{
	
	RoleApiMapping findByMethodName(String methodName);
	
	

}
