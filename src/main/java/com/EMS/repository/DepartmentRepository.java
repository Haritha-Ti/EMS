package com.EMS.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.EMS.model.DepartmentModel;


@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentModel, Long> {

	@Query("SELECT s FROM DepartmentModel s order by departmentName ASC")
	List<DepartmentModel> findDeptName();

	

		
	
}



































































