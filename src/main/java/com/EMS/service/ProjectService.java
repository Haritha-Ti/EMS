package com.EMS.service;

import java.util.ArrayList;
import java.util.List;

import com.EMS.model.*;

public interface ProjectService {

	
//		method declaration for saving new project record 
		ProjectModel save_project_record(ProjectModel project);

		List<String> getProjectsList();
		
		ProjectModel findById(Long id);


		Long getProjectId(String projectName);
	
//		method declaration for getting contract type details for project creation
		ArrayList<ContractModel> getcontractType();

//		Method invocation for getting department details
		List<DepartmentModel> getdepartment();

		List<Object[]> getNameId();

//		Method for creating project resouce records
		Resources addprojectresouce(Resources resou1);

		ContractModel getContract(long contractId);

//		Method for checking duplicate values in project name
		int duplicationchecking(String getprojectName);

//		Method for getting department details with ID
		DepartmentModel getDepartmentDetails(Long depart);

		List<ProjectModel> getProjectList();
		
		//Get all details of all projects 
		ArrayList<ProjectModel> getListofProjects();

//		UserModel getuser(Long userid);
		
		//method for getting resource list
		List<Resources> getResourceList(long projectId);
		
		//method for getting project details by ID
		ProjectModel getProjectId(long projectId);
		
		//get client name by passing client ID
		ClientModel getClientName(long parseLong);
		//get resource details by ID
		Resources getResourceById(long long1);
		
		//get client master details
		List<ClientModel> getClientList();

		List<String> getclientLocation();

	List<EmployeeContractors> getEmployeeContractorsList();





	
}

