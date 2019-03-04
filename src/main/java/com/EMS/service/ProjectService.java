package com.EMS.service;

import java.util.ArrayList;
import java.util.List;

import com.EMS.model.ContractModel;
import com.EMS.model.DepartmentModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.Resources;

public interface ProjectService {

	
//		method declaration for saving new project record 
		ProjectModel save_project_record(ProjectModel project);

		List<String> getProjectsList();
		
		ProjectModel findById(Long id);


		Long getProjectId(String projectName);

//		method declaration for getting user details for project creation
		List<String> getprojectOwner();


	
//		method declaration for getting contract type details for project creation
		ArrayList<ContractModel> getcontractType();

//		Method invocation for getting department details
		List<DepartmentModel> getdepartment();

		List<Object[]> getNameId();

//		Method for creating project resouce records
		Resources addprojectresouce(Resources resou1);

		ContractModel getContract(long contractId);




	
}

