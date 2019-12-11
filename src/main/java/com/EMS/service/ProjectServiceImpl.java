package com.EMS.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.EMS.model.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.EMS.repository.ClientRepository;
import com.EMS.repository.ContractRepository;
import com.EMS.repository.DepartmentRepository;
import com.EMS.repository.EmployeeContractorsRepository;
import com.EMS.repository.ProjectRegionRepository;
import com.EMS.repository.ProjectRepository;
import com.EMS.repository.ResourceRepository;
import com.EMS.repository.TermRepository;
import com.EMS.repository.UserRepository;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	ProjectRepository project_repositary;

	@Autowired
	UserRepository user_repositary;

	@Autowired
	ResourceRepository resource_repository;

	@Autowired
	ContractRepository contract_repository;

	@Autowired
	DepartmentRepository department_repositary;

	@Autowired
	ClientRepository client_repository;

	@Autowired
	EmployeeContractorsRepository employeeContractorsRepository;
	
	@Autowired
	ProjectRegionRepository projectRegionRepository;
	
	@Autowired
	TermRepository termRepository;
	
	@Override
	public ProjectModel save_project_record(ProjectModel projectmodel) {

		ProjectModel model = project_repositary.save(projectmodel);
		return model;
	}

	@Override
	public ProjectModel findById(Long id) {
		ProjectModel model = project_repositary.getOne(id);
		return model;
	}

	@Override
	public List<String> getProjectsList() {
		List<String> nameList = project_repositary.getProjectName();
		return nameList;
	}

	@Override
	public Long getProjectId(String projectName) {
		Long pid = project_repositary.getProjectId(projectName);
		return pid;
	}

	public List<Object[]> getNameId() {
		List<Object[]> idNameList = project_repositary.getByIdName();
		return idNameList;

	}

	@Override
	public ArrayList<ContractModel> getcontractType() {

		ArrayList<ContractModel> contract = null;
		try {
//			getting contract type records 
			contract = (ArrayList<ContractModel>) contract_repository.findAll();

			return contract;
		} catch (Exception e) {
			System.out.println("Exception : " + e);
			return contract;
		}

	}

	@Override
	public List<DepartmentModel> getdepartment() {
		List<DepartmentModel> department = department_repositary.findAll();
		return department;
	}

	@Override
	public Resources addprojectresouce(Resources resou1) {
		Resources value = resource_repository.save(resou1);
		return value;
	}

	@Override
	public ContractModel getContract(long id) {
		ContractModel contract = contract_repository.getOne(id);
		return contract;
	}


	@Override
	public int duplicationchecking(String getprojectName) {
		int value = project_repositary.findproject(getprojectName);
		return value;
	}

	@Override
	public DepartmentModel getDepartmentDetails(Long depart) {
		DepartmentModel department = department_repositary.getOne(depart);
		return department;
	}

	@Override
	public List<ProjectModel> getProjectList() {
		List<ProjectModel> project = project_repositary.getProjectsOnly();
		return project;
	}

	@Override
	public ArrayList<ProjectModel> getListofProjects() {
		ArrayList<ProjectModel> projectlist=project_repositary.getAllNonParentProjects();
		return projectlist;
	}
	private Sort sortByIdAsc() {
        return new Sort(Sort.Direction.ASC, "projectName");
    }


	@Override
	public List<Resources> getResourceList(long projectId) {

		List<Resources> list=resource_repository.getResourceList(projectId);
		return list;
	}

	@Override
	public ProjectModel getProjectId(long projectId) {
		
		ProjectModel project=new ProjectModel();
		try {
			project=project_repositary.getOne(projectId);
			return project;
		}catch(Exception e) {
			System.out.println("Exception "+e);
			return project;
		}
		
		
	}

	@Override
	public ClientModel getClientName(long id) {

		ClientModel getclient=client_repository.getOne(id);
		return getclient;
	}

	@Override
	public Resources getResourceById(long long1) {
		Resources resource=resource_repository.getOne(long1);
		return resource;
	}

	@Override
	public List<ClientModel> getClientList() {
		List<ClientModel> list=client_repository.getAll();
		return list;
	}

	@Override
	public List<String> getclientLocation() {
		List<String> location=client_repository.getLocation();
		System.out.println("loca :"+location.size());
		return location;
	}

	@Override
	public ProjectModel getProjectDetails(Long projectId) {
		// TODO Auto-generated method stub
		ProjectModel model = project_repositary.getProjectDetails(projectId);
		return model;
	}

	@Override
	public String getProjectName(Long projectId) {
		String projectName = project_repositary.getProjectName(projectId);
		return projectName;
	}

	public List<EmployeeContractors> getEmployeeContractorsList() {
		List<EmployeeContractors> list=employeeContractorsRepository.findAll();
		return list;
	}

	@Override
	public ArrayList<ProjectModel> getListofParentProjects() {
		ArrayList<ProjectModel> projectlist= project_repositary.getparentProjects();
		return projectlist;
	}

	@Override
	public void save_project_region(ProjectRegion region) {
		// TODO Auto-generated method stub
		ProjectRegion region1 = projectRegionRepository.save(region);
		
	}

	@Override
	public List<ProjectRegion> getregionlist(long projectId) {
		// TODO Auto-generated method stub
		List<ProjectRegion> list=projectRegionRepository.getRegionList(projectId);
		return list;
	}

	@Override
	public ArrayList<ProjectRegion> getRegionsByprojectId(long projectId) {
		// TODO Auto-generated method stub
		ArrayList<ProjectRegion> list = (ArrayList<ProjectRegion>) projectRegionRepository.getRegionList(projectId);
		return list;
	}

	@Override
	public ProjectRegion findByIdRegion(long project_region_Id) {
		// TODO Auto-generated method stub
		ProjectRegion region = projectRegionRepository.getOne(project_region_Id);
		return region;
	}

	@Override
	public int deleteProjectRegions(long projectId) {
		// TODO Auto-generated method stub
		int i =  termRepository.deleteByProjectId(projectId);
		return i ;
	}

	//Renjith
	
		public List<ProjectModel>  getProjectListByLevel1(Long userId){
			return project_repositary.getProjectListByLevel1(userId);
		}
		
		public List<ProjectModel>  getProjectListByLevel2(Long userId){
			return project_repositary.getProjectListByLevel2(userId);
		}
		
		public List<ProjectModel>  getAllActiveProjectList(){
			return project_repositary.getAllActiveProjectList();
		}
		//Renjith

		@Override
		public List<ProjectModel> getProjectsBasedOnMonthYearRegion(Long regionId, int month, int year) {
			// TODO Auto-generated method stub
			String startDate = year+"-"+month+"-01"; 
			SimpleDateFormat formatter1=new SimpleDateFormat("yyyy-MM-dd");  
			Date date1 = null;
			try {
				 date1=formatter1.parse(startDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return project_repositary.getProjectsBasedOnMonthYearRegion(regionId,date1);
		}

		@Override
		public List<ProjectModel> getProjectsBasedOnMonthYearRegion(int month, int year) {
			// TODO Auto-generated method stub
			String startDate = year+"-"+month+"-01"; 
			SimpleDateFormat formatter1=new SimpleDateFormat("yyyy-MM-dd");  
			Date date1 = null;
			try {
				 date1=formatter1.parse(startDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return project_repositary.getProjectsBasedOnMonthYearRegion(date1);
		}

		@Override
		public ArrayList<ProjectModel> getProjectsByRegion(Long regionId) {
			// TODO Auto-generated method stub
			return project_repositary.getProjectsByRegion(regionId);
		}

		@Override
		public ArrayList<ProjectModel> getProjectsByRegion() {
			// TODO Auto-generated method stub
			return project_repositary.getProjectsByRegion();
		}

	public ObjectNode getProjectHealthData(Long regionId, String currentDate) throws ParseException {

		//Active Project List
		ArrayNode projectArray = objectMapper.createArrayNode();
		ObjectNode data = objectMapper.createObjectNode();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<ProjectModel> activeProjectList;
		if(regionId!= null) {
			activeProjectList = project_repositary.getAllActiveProjectsByRegion(currentDate, regionId);
		}
		else{
			 activeProjectList = project_repositary.getAllActiveProjectsByDate(currentDate);
		}
		int activeProjectCount = 0;
		activeProjectCount = activeProjectList.size();
		if (activeProjectCount > 0) {
			for (ProjectModel projectData : activeProjectList) {
				ObjectNode projectObj = objectMapper.createObjectNode();
				projectObj.put("projectName", projectData.getProjectName());
				projectObj.put("projectName", projectData.getProjectCode());
				projectObj.put("projectTier", projectData.getProjectTier());
				//get region list
				List<ProjectRegion> regions = getregionlist(projectData.getProjectId());
				ArrayNode regionsArray = objectMapper.createArrayNode();
				ArrayList<String> regionArraylist = new ArrayList<String>();
				if (regions.isEmpty()) {
					projectObj.set("projectRegion", regionsArray);
				} else {
					for (ProjectRegion regioneach : regions) {
						ObjectNode resource = objectMapper.createObjectNode();
						resource.put("regionId",regioneach.getRegion_Id().getId());
						resource.put("regionName",regioneach.getRegion_Id().getId());
						regionsArray.add(resource);
					}
					projectObj.set("projectRegion", regionsArray);
				}
				projectArray.add(projectObj);
			}

		}
		//Active User List
		ArrayNode userArray = objectMapper.createArrayNode();
		List<UserModel> userList;

		if(regionId!= null) {
			userList = user_repositary.getUsersBasedOnMonthYearRegion(regionId,sdf.parse(currentDate));
		}
		else{
			userList = user_repositary.getUsersBasedOnMonthYearRegion(sdf.parse(currentDate));
		}
		int userListCount = 0 ;
		userListCount = userList.size();
		if(userListCount > 0){
			for(UserModel userData : userList){
				ObjectNode userObj = objectMapper.createObjectNode();
				userObj.put("firstName",userData.getFirstName());
				userObj.put("lastName",userData.getLastName());
				userObj.put("cppLevel",userData.getCpplevels().getLevelName());
				userObj.put("regionId",userData.getRegion().getId());
				userObj.put("regionName",userData.getRegion().getRegion_name());
				userArray.add(userObj);
			}
		}
		//New joinies list of last 30 days

		Calendar c = Calendar.getInstance();
		c.setTime(sdf.parse(currentDate));
		c.add(Calendar.DAY_OF_MONTH, -30);
		String startDate = sdf.format(c.getTime());
		ArrayNode newjoinesArray = objectMapper.createArrayNode();
		List<UserModel> newjoinesList;
		if(regionId!= null) {
			 newjoinesList = user_repositary.getNewJoinesListByregion(sdf.parse(startDate), sdf.parse(currentDate), regionId);
		}
		else{
			newjoinesList = user_repositary.getNewJoinesList(sdf.parse(startDate), sdf.parse(currentDate));
		}
		int newjoinesListCount = 0;
		newjoinesListCount = newjoinesList.size();
		if(newjoinesListCount>0){
			for(UserModel newjoinesData : newjoinesList){
				ObjectNode newjoinesObj = objectMapper.createObjectNode();
				newjoinesObj.put("firstName",newjoinesData.getFirstName());
				newjoinesObj.put("lastName",newjoinesData.getLastName());
				newjoinesObj.put("cppLevel",newjoinesData.getCpplevels().getLevelName());
				newjoinesObj.put("joiningDate", sdf.format(newjoinesData.getJoiningDate()));
				newjoinesObj.put("regionId",newjoinesData.getRegion().getId());
				newjoinesObj.put("regionName",newjoinesData.getRegion().getRegion_name());
				newjoinesArray.add(newjoinesObj);
			}
		}
		//Leaved users list of last 30 days
		ArrayNode leavedUsersArray = objectMapper.createArrayNode();
		List<UserModel> leavedUsersList;
		if(regionId!= null) {
			 leavedUsersList = user_repositary.getleavedUsersByregion(sdf.parse(startDate), sdf.parse(currentDate), regionId);
		}
		else{
			leavedUsersList = user_repositary.getleavedUsers(sdf.parse(startDate), sdf.parse(currentDate));
		}
		int leavedUsersListCount = 0;
		leavedUsersListCount = leavedUsersList.size();
		if(leavedUsersListCount>0){
			for(UserModel leavedUsersData : leavedUsersList){
				ObjectNode leavedUsersObj = objectMapper.createObjectNode();
				leavedUsersObj.put("firstName",leavedUsersData.getFirstName());
				leavedUsersObj.put("lastName",leavedUsersData.getLastName());
				leavedUsersObj.put("cppLevel",leavedUsersData.getCpplevels().getLevelName());
				leavedUsersObj.put("terminationDate", sdf.format(leavedUsersData.getTerminationDate()));
				leavedUsersObj.put("regionId",leavedUsersData.getRegion().getId());
				leavedUsersObj.put("regionName",leavedUsersData.getRegion().getRegion_name());
				leavedUsersArray.add(leavedUsersObj);
			}
		}


		data.set("projectData",projectArray);
		data.set("userData",userArray);
		data.set("newJoinesData",newjoinesArray);
		data.set("leavedUsersData",leavedUsersArray);
		return data;
	}

	@Override
	public int duplicationCheckingProjectCode(String projectCode) {
		int value = project_repositary.findprojectbycode(projectCode);
		return value;
	}
}
