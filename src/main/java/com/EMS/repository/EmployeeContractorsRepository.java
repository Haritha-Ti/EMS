package com.EMS.repository;

import com.EMS.model.EmployeeContractors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeContractorsRepository extends JpaRepository<EmployeeContractors, Long> {

	@Query(value = "SELECT count(*) FROM employee_contractors  WHERE contractor_name=?1", nativeQuery = true)
	int findContractor(String contractorName);

}
