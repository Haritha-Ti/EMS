package com.EMS.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import com.EMS.model.*;
import com.EMS.repository.RoleRepository;
import org.hibernate.mapping.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.service.ProjectService;
import com.EMS.service.RegionFilterService;
import com.EMS.service.RegionService;
import com.EMS.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value = "/project")
public class ProjectController {

	@Autowired
	private ProjectService projectservice;

	@Autowired
	private UserService userservice;

	@Autowired
	private ObjectMapper objectMapper;
	// api for creating new project

	@Autowired
	private RegionService regionService;
	
	@Autowired
	private RegionFilterService regionFilterService;

	@Autowired
	RoleRepository roleRepository;

	@PostMapping("/createProject")
	public JsonNode save_newproject(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) {

		ObjectNode responsedata = objectMapper.createObjectNode();
		int responseflag = 0;
		try {

			// setting values to object from json request
			ProjectModel project = new ProjectModel();

			Long contractId = requestdata.get("contractType").asLong();
			ContractModel contractModel = new ContractModel();
			if ((contractId != null) && (!contractId.equals(" ")))
				contractModel = projectservice.getContract(contractId);
			project.setProjectDetails(requestdata.get("projectDetails").asText());
			project.setprojectType(requestdata.get("projectType").asInt());

			if (project.getprojectType() == 0) { // if the project type is external(value =0)
				Long clientid = requestdata.get("clientId").asLong();
				ClientModel client = new ClientModel();
				if (clientid != 0L) {
					client = projectservice.getClientName(clientid);
					project.setClientName(client);
					project.setClientPointOfContact(requestdata.get("clientPointOfContact").asText());
				}
			}
			project.setParentProjectId(requestdata.get("parentProjectId").asLong());
			//project.setProject_refId(requestdata.get("projectRefId").asText());
			project.setProjectCategory(requestdata.get("projectCategory").asInt());
			project.setProjectName(requestdata.get("projectName").asText());
			project.setisBillable(requestdata.get("isBillable").asInt());
			project.setProjectCode(requestdata.get("projectCode").asText());
			project.setprojectStatus(requestdata.get("projectStatus").asInt());
			project.setisPOC(requestdata.get("isPOC").asInt());
			project.setProjectTier(0);
			UserModel createdBy = userservice.getUserDetailsById(requestdata.get("sessionId").asLong());
			//project.setCreatedBy(createdBy);
			Date createdDate = new Date();
			//project.setCreatedDate(createdDate);
			//project.setModifiedBy(null);
			//project.setModifiedDate(null);
			Long userid = null;

			if(requestdata.get("projectTier").asInt() == 1) {

				userid = requestdata.get("approver_level_1").asLong();

				UserModel pro_owner = new UserModel();

				// method for getting userdetails using ID

				if (userid != null)

					pro_owner = userservice.getUserDetailsById(userid);



				if (pro_owner != null)

					project.setProjectOwner(pro_owner);

				

				project.setProjectTier(1);
				project.setOnsite_lead(null);

			}

			else if (requestdata.get("projectTier").asInt() == 2) {

				

				userid = requestdata.get("approver_level_1").asLong();

				UserModel pro_owner = new UserModel();

				// method for getting userdetails using ID

				if (userid != null)

					pro_owner = userservice.getUserDetailsById(userid);



				if (pro_owner != null)

					project.setProjectOwner(pro_owner);

				

				Long onsite_lead = requestdata.get("approver_level_2").asLong();

				UserModel pro_onsite_lead = new UserModel();

				if (onsite_lead != null) {

					pro_onsite_lead = userservice.getUserDetailsById(onsite_lead);

				}

				if (pro_onsite_lead != null) {

					project.setOnsite_lead(pro_onsite_lead);

				}

				project.setProjectTier(2);

			}
			/*
			 * UserModel pro_onsite_lead = new UserModel(); if (onsite_lead != null) {
			 * pro_onsite_lead = userservice.getUserDetailsById(onsite_lead); } if
			 * (pro_onsite_lead != null) { project.setOnsite_lead(pro_onsite_lead); }
			 * 
			 * // method for getting userdetails using ID if (userid != null) pro_owner =
			 * userservice.getUserDetailsById(userid);
			 * 
			 * if (pro_owner != null) project.setProjectOwner(pro_owner);
			 */

			if (contractModel != null)
				project.setContract(contractModel);

			project.setEstimatedHours(requestdata.get("estimatedHours").asInt());
			String startdate = requestdata.get("startDate").asText();
			String enddate = requestdata.get("endDate").asText();
			String releasingdate = requestdata.get("releasingDate").asText();
          
			// Formatting the dates before storing
			TimeZone zone = TimeZone.getTimeZone("MST");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			formatter.setTimeZone(zone);

			Date date1 = null, date2 = null, releaseDate = null;
			if (!startdate.isEmpty()) {
				date1 = formatter.parse(startdate);
				project.setStartDate(date1);
			}

			if (!enddate.isEmpty()) {
				date2 = formatter.parse(enddate);
				project.setEndDate(date2);

			}

			if (!releasingdate.isEmpty()) {
				releaseDate = formatter.parse(releasingdate);
				project.setReleasingDate(releaseDate);
			}

			if ((project.getProjectDetails() != null) && (project.getProjectDetails().length() > 0)
					&& (!project.getProjectDetails().equals(" ")) && (project.getProjectName() != null)
					&& (!project.getProjectName().equals(" ")) && (project.getProjectName().length() > 0)
					&& (project.getProjectCode() != null) && (!project.getProjectCode().equals(" "))
					&& (project.getProjectCode().length() > 0)) {
				// method invocation for checking duplicate entry for project name
				//int result = projectservice.duplicationchecking(project.getProjectName());
			//	if (result == 0) {
					// Method invocation for creating new project record

					ProjectModel projectmodel = projectservice.save_project_record(project);
					// method invocation for storing resouces of project created

					
					
					  // regions
					ArrayNode arrayNodes = null;
					/*if(requestdata.get("projectRegion") != null)
					{*/ 
						 arrayNodes = (ArrayNode) requestdata.get("projectRegion");
					
					//}
					if (projectmodel != null && arrayNodes.size()!=0) {
						
						for(JsonNode jregion : arrayNodes) {
							ProjectRegion region = new ProjectRegion();
							region.setProject_Id(projectmodel);
							Region regionmodel = regionService.getregion(jregion.asLong());
							region.setRegion_Id(regionmodel);
							projectservice.save_project_region(region);
						}
						
					}
					// regions
					
					
					
					if (projectmodel == null) {
						responseflag = 1;
						responsedata.put("message", "Project record creation failed");
					} else {

						// json array for storing json array from request data
					//	ArrayNode arrayNode = (ArrayNode) requestdata.get("resources");

					/*	if (arrayNode.equals(null)) {
							responseflag = 1;
							responsedata.put("message", "Failed due to project record with empty resource array");
						} else {
							// get totalCount of all jsonObjects

							for (JsonNode node : arrayNode) {

								// setting values on resource object
								Resources resou1 = new Resources();
								if (projectmodel != null)
									resou1.setProject(projectmodel);

								Long depart = node.get("department").asLong();
								DepartmentModel department = new DepartmentModel();

								// method for getting department details
								if (depart != null)
									department = projectservice.getDepartmentDetails(depart);

								if (department != null)
									resou1.setDepartment(department);

								resou1.setresourceCount(node.get("resourceCount").asInt());

								// checking resouce model values before storing
								if ((resou1.getresourceCount() != 0) && (!resou1.getDepartment().equals(null))
										&& (resou1.getProject() != null)) {

									// method invocation for storing resource details
									Resources resourcevalue = projectservice.addprojectresouce(resou1);

									if (resourcevalue == null)
										responseflag = 1;
								} else {
									responseflag = 1;
									responsedata.put("message",
											"Insertion failed due to invalid credientials for project resource");
								}
							}
						}*/

					}

				//} else {
					//responseflag = 1;
					//responsedata.put("message", "Insertion failed due to duplicate entry");
			//	}

			} else {
				responseflag = 1;
				responsedata.put("message", "Insertion failed due to invalid credientials for project");
			}

			// setting values on response json
			if (responseflag == 0) {
				responsedata.put("status", "success");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.put("message", "Record Inserted");
				responsedata.put("payload", "");
			} else {
				responsedata.put("status", "Failed");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.put("payload", "");
			}

		} catch (Exception e) {
			System.out.println("Exception : " + e);
			responsedata.put("status", "Failed");
			responsedata.put("code", httpstatus.getStatus());
			responsedata.put("message", "Exception " + e);
			responsedata.put("payload", "");
		}
		return responsedata;
	}

	// Api for getting project owner details from user table

	@GetMapping(value = "/getAdminFilterData")
	@ResponseBody
	public JsonNode getprojects() {

		// json object for passing response
		ObjectNode responsedata = objectMapper.createObjectNode();

		// Json array for storing filtered data from two tables
		ArrayNode onsitelead_array = objectMapper.createArrayNode();
		ArrayNode userarray = objectMapper.createArrayNode();
		ArrayNode regionarray = objectMapper.createArrayNode();
		ArrayNode timezonearray = objectMapper.createArrayNode();
		ArrayNode contract_array = objectMapper.createArrayNode();
		ArrayNode department_array = objectMapper.createArrayNode();
		ArrayNode project_array = objectMapper.createArrayNode();
		ArrayNode client_array = objectMapper.createArrayNode();
		ArrayNode employeeContractors_array = objectMapper.createArrayNode();
		ArrayNode role_array = objectMapper.createArrayNode();

		// json object for storing records array
		ObjectNode array = objectMapper.createObjectNode();

		try {

			// method invocation for getting client details
			List<ClientModel> clientlist = projectservice.getClientList();
			if (clientlist.isEmpty())
				array.set("clientDetails", client_array);
			else {
				for (ClientModel client : clientlist) {
					ObjectNode clientobj = objectMapper.createObjectNode();
					clientobj.put("clientId", client.getClientId());
					clientobj.put("clientName", client.getClientName());
					client_array.add(clientobj);
				}
				array.set("clientDetails", client_array);
			}

			// method invocation for getting project details
			List<ProjectModel> projectlist = projectservice.getProjectList();
			if (projectlist.isEmpty())
				array.set("projectName", project_array);
			else {
				for (ProjectModel projectdata : projectlist) {
					ObjectNode projectobject = objectMapper.createObjectNode();

					projectobject.put("projectId", projectdata.getProjectId());
					projectobject.put("projectName", projectdata.getProjectName());
					project_array.add(projectobject);
				}
				array.set("projectName", project_array);
			}

			// Method invocation for getting contract type
			ArrayList<ContractModel> contract = projectservice.getcontractType();

			if (contract.isEmpty())
				array.set("contractType", contract_array);
			else {

				// Looping for storing data on json array
				for (ContractModel cont : contract) {

					// json object for storing single record
					ObjectNode contractobject = objectMapper.createObjectNode();

					// adding records to json object
					contractobject.put("contractTypeId", cont.getContractTypeId());
					contractobject.put("contractTypeName", cont.getContractTypeName());
					// adding records object to json array
					contract_array.add(contractobject);
				}

				// storing records array to json object
				array.set("contractType", contract_array);
			}

			// method for getting regions
			ArrayList<Region> region = (ArrayList<Region>) regionService.getlist();

			if (region.isEmpty()) {
				array.set("regions", regionarray);
			}

			else {
				Iterator<Region> itr = region.listIterator();
				while (itr.hasNext()) {
					ObjectNode object = objectMapper.createObjectNode();

					Region regions = itr.next();
					object.put("region_name", regions.getRegion_name());
					object.put("region_code", regions.getRegion_code());
					object.put("region_Id", regions.getId());
					regionarray.add(object);
				}
				array.set("regions", regionarray);
			}

			// method for getting timezones

			ArrayList<TimeZoneModel> zone = regionService.getTimeZones1();

			if (zone.isEmpty()) {
				array.set("timezones", timezonearray);
			}

			else {
				Iterator<TimeZoneModel> itr = zone.listIterator();
				while (itr.hasNext()) {
					ObjectNode object = objectMapper.createObjectNode();

					TimeZoneModel timezone = itr.next();
					object.put("timezone_name", timezone.getTimezone_name());
					object.put("timezone_code", timezone.getTimezone_code());
					object.put("timezone_Id", timezone.getId());
					regionarray.add(object);
				}
				array.set("timezones", timezonearray);
			}

			// Method invocation for getting users with role as owner
			List<UserModel> users_owner = userservice.getprojectOwner();

			if (users_owner.isEmpty())
				array.set("approver_level_1", userarray);
			else {

				Iterator<UserModel> itr = users_owner.listIterator();
				// Looping for storing data on json array
				while (itr.hasNext()) {

					// json object for storing single record
					ObjectNode object = objectMapper.createObjectNode();

					// adding records to json object
					UserModel user = itr.next();
					object.put("firstName", user.getFirstName());
					object.put("id", user.getUserId());
					object.put("lastName", user.getLastName());
					object.put("role", user.getRole().getroleId());
					object.put("status", user.isActive());
					// adding records object to json array
					userarray.add(object);
				}

				// storing records array to json object
				array.set("approver_level_1", userarray);

			}

			// Method invocation for getting users with role as onsite lead
			List<UserModel> onsite_lead = userservice.getOnsiteLead();
			if (onsite_lead.isEmpty())
				array.set("approver_level_2", onsitelead_array);
			else {
				Iterator<UserModel> itr = onsite_lead.listIterator();
				while (itr.hasNext()) {

					// json object for storing single record
					ObjectNode object = objectMapper.createObjectNode();

					// adding records to json object
					UserModel user = itr.next();
					object.put("firstName", user.getFirstName());
					object.put("id", user.getUserId());
					object.put("lastName", user.getLastName());
					object.put("role", user.getRole().getroleId());
					object.put("status", user.isActive());
					// adding records object to json array
					onsitelead_array.add(object);
				}
				array.set("approver_level_2", onsitelead_array);
			}

			// Method invocation for getting users with role as owner
			List<DepartmentModel> department = projectservice.getdepartment();

			if (department.isEmpty())
				array.set("department_resource", department_array);
			else {

				// Looping for storing data on json array
				for (DepartmentModel dept : department) {

					// json object for storing single record
					ObjectNode object = objectMapper.createObjectNode();

					// adding records to json object
					object.put("departmentId", dept.getDepartmentId());
					object.put("department", dept.getdepartmentName());

					// adding records object to json array
					department_array.add(object);
				}

				// storing records array to json object
				array.set("department_resource", department_array);

			}

			// Method invocation for getting employee contractors list
			List<EmployeeContractors> contractorslist = projectservice.getEmployeeContractorsList();

			if (contractorslist.isEmpty())
				array.set("contractorList", employeeContractors_array);
			else {
				for (EmployeeContractors contractors : contractorslist) {
					ObjectNode contractorobj = objectMapper.createObjectNode();
					contractorobj.put("contractorId", contractors.getContractorId());
					contractorobj.put("contractorName", contractors.getContractorName());
					employeeContractors_array.add(contractorobj);
				}
				array.set("contractorList", employeeContractors_array);
			}

			List<RoleModel> rolelist = roleRepository.findAll();
			if (rolelist.isEmpty())
				array.set("contractorList", employeeContractors_array);
			else {
				for (RoleModel role : rolelist) {
					ObjectNode roleobj = objectMapper.createObjectNode();
					roleobj.put("roleId", role.getroleId());
					roleobj.put("roleName", role.getroleName());
					role_array.add(roleobj);

				}
				array.set("roleList", role_array);
			}
			// storing data on response object
			responsedata.put("status", "success");
			responsedata.set("data", array);
			return responsedata;
		} catch (Exception e) {
			System.out.println("Exception " + e);
			return responsedata;
		}

	}

	// api for project list
	@PostMapping(value = "/viewProjects")
	public JsonNode getProjectsList(@RequestBody JsonNode requestdata,HttpServletResponse statusResponse) {
		ObjectNode responsedata = objectMapper.createObjectNode();
		ArrayNode projectArray = objectMapper.createArrayNode();
        Long userId=null;
		try {
			// Getting all projects list to arraylist
			ArrayList<ProjectModel> projectlist=null;
			if (requestdata.get("sessionId") != null && requestdata.get("sessionId").asText() != "") {
				userId = requestdata.get("sessionId").asLong();
			}
			if(userId == null)
			projectlist = projectservice.getListofProjects();
			else
		    projectlist =  (ArrayList<ProjectModel>) regionFilterService.getAllProjectByLoginUserRegion(userId);	
				
			// checking for project arraylist is empty or not
			if (projectlist.isEmpty()) {
				responsedata.put("status", "success");
				responsedata.put("message", "No Records Available");
				responsedata.put("code", statusResponse.getStatus());
				responsedata.put("payload", "");
			} else {

				// loop for getting projectwise details
				for (ProjectModel obj : projectlist) {

					  try{
					// Object declarations
					ObjectNode client = objectMapper.createObjectNode();
					ContractModel contract = null;
					ObjectNode contractobj = objectMapper.createObjectNode();
					String parentproject = projectservice.getProjectName(obj.getParentProjectId());

					// storing projects details in json object
					ObjectNode jsonobj = objectMapper.createObjectNode();
					jsonobj.put("projectId", obj.getProjectId());
					jsonobj.put("projectName", obj.getProjectName());
					jsonobj.put("projectFullName", parentproject + "_" + obj.getProjectName());
					jsonobj.put("isBillable", obj.getisBillable());
					jsonobj.put("projectCode", obj.getProjectCode());
					jsonobj.put("projectType", obj.getprojectType());
					jsonobj.put("projectStatus", obj.getprojectStatus());
					
					//get region list
                    List<ProjectRegion> regions = projectservice.getregionlist(obj.getProjectId());
                    ArrayNode regionsArray = objectMapper.createArrayNode();
                    ArrayList<Integer> regionArraylist = new ArrayList<Integer>();
                    if(regions.isEmpty()) {
                        jsonobj.set("projectRegion", regionsArray);
                    }
                    else {
                        for(ProjectRegion regioneach : regions) {
                            ObjectNode resource = objectMapper.createObjectNode();
                            regionsArray.add((int)regioneach.getRegion_Id().getId());
                        }
                        jsonobj.set("projectRegion", regionsArray);
                    }
                    //
					 if(obj.getReleasingDate()!=null)
					 {
					jsonobj.put("releasingDate", obj.getReleasingDate().toString());
					 }
					if(obj.getClientName() != null)
					{
						client.put("clientId", obj.getClientName().getClientId());
					    client.put("clientName", obj.getClientName().getClientName());
					    jsonobj.set("client", client);
					}
					else {
						client.set("clientId", null);
					    client.set("clientName", null);
					    jsonobj.set("client", client);
						
					}
					
					Long contractId=null;
					
					if(obj.getContract()!=null)
					 contractId = obj.getContract().getContractTypeId() ;

					if (contractId != null) {
						// getting contract details
						contract = projectservice.getContract(contractId);
					}
					// storing contract values in jsonobject
					if (contract == null)
						contractobj = null;
					else {

						contractobj.put("contractTypeId", contract.getContractTypeId());
						contractobj.put("contractTypeName", contract.getContractTypeName());
					}
					jsonobj.set("contractType", contractobj);

					// null checking user ID
					Long userid = obj.getProjectOwner().getUserId();
					UserModel userdata = null;
					if (userid != null) {
						// getting user details
						userdata = userservice.getUserDetailsById(userid);
					}

					ObjectNode userobj = objectMapper.createObjectNode();
					// storing user values in jsonobject
					if (userdata == null)
						userobj = null;
					else {
						userobj.put("firstName", userdata.getFirstName());
						userobj.put("lastName", userdata.getLastName());
						userobj.put("role", userdata.getRole().getroleId());
						userobj.put("userId", userdata.getUserId());
						userobj.put("regionId", userdata.getRegion().getId());

					}
					jsonobj.set("approver_level_1", userobj);

					projectArray.add(jsonobj);
				}
					  catch (Exception e) {
						e.printStackTrace();
					}
			   	
			}
				responsedata.put("status", "success");
				responsedata.put("message", "success");
				responsedata.put("code", statusResponse.getStatus());
				responsedata.set("payload", projectArray);

			}

		} catch (Exception e) {
			System.out.println("Exception : " + e);
			responsedata.put("status", "failed");
			responsedata.put("message", "Exception : " + e);
			responsedata.put("code", statusResponse.getStatus());
		}

		return responsedata;
	}

	@PutMapping(value = "/editProject")
	public JsonNode getprojectData(@RequestBody ObjectNode requestdata, HttpServletResponse httpstatus) {
		ObjectNode responsedata = objectMapper.createObjectNode();
		int responseflag = 0;
		try {

			// setting values to object from json request
			ProjectModel project = projectservice.findById(requestdata.get("projectId").asLong());
			Long contractId = requestdata.get("contractType").asLong();
			ContractModel contractModel = new ContractModel();
			if ((contractId != null) && (!contractId.equals(" ")))
				contractModel = projectservice.getContract(contractId);
			project.setprojectType(requestdata.get("projectType").asInt());

			if (project.getprojectType() == 0) { // if the project type is external(value =0)
				Long clientid = requestdata.get("clientId").asLong();
				ClientModel client = new ClientModel();
				if (clientid != 0L) {
					client = projectservice.getClientName(clientid);
					project.setClientName(client);
					project.setClientPointOfContact(requestdata.get("clientPointOfContact").asText());
				}
			}
			project.setParentProjectId(requestdata.get("parentProjectId").asLong());
			//project.setProject_refId(requestdata.get("projectRefId").asText());
			project.setProjectCategory(requestdata.get("projectCategory").asInt());
			project.setProjectDetails(requestdata.get("projectDetails").asText());
			project.setProjectName(requestdata.get("projectName").asText());
			project.setisBillable(requestdata.get("isBillable").asInt());
			project.setProjectCode(requestdata.get("projectCode").asText());
			project.setprojectStatus(requestdata.get("projectStatus").asInt());
			project.setisPOC(requestdata.get("isPOC").asInt());
			
			UserModel modifiedBy = userservice.getUserDetailsById(requestdata.get("sessionId").asLong());
			//project.setModifiedBy(modifiedBy);
			Date modifiedDate = new Date();
			//project.setModifiedDate(modifiedDate);
			/*
			 * Long userid = requestdata.get("approver_level_1").asLong(); UserModel
			 * pro_owner = new UserModel();
			 * 
			 * // method for getting userdetails using ID if (userid != null) pro_owner =
			 * userservice.getUserDetailsById(userid);
			 * 
			 * if (pro_owner != null) project.setProjectOwner(pro_owner);
			 * 
			 * if (contractModel != null) project.setContract(contractModel);
			 * 
			 * Long approverlevel2 = requestdata.get("approver_level_2").asLong(); UserModel
			 * pro_approver_2 = null;
			 * 
			 * if (approverlevel2 == 0) project.setOnsite_lead(null);
			 * 
			 * if (approverlevel2 != 0) pro_approver_2 =
			 * userservice.getUserDetailsById(approverlevel2);
			 * 
			 * if (pro_approver_2 != null) project.setOnsite_lead(pro_approver_2);
			 */

			
			project.setProjectTier(0);

			

			Long userid = null;

			if(requestdata.get("projectTier").asInt() == 1) {

				userid = requestdata.get("approver_level_1").asLong();

				UserModel pro_owner = new UserModel();

				// method for getting userdetails using ID

				if (userid != null)

					pro_owner = userservice.getUserDetailsById(userid);



				if (pro_owner != null)

					project.setProjectOwner(pro_owner);

				

				project.setProjectTier(1);
				project.setOnsite_lead(null);

			}

			else if (requestdata.get("projectTier").asInt() == 2) {

				

				userid = requestdata.get("approver_level_1").asLong();

				UserModel pro_owner = new UserModel();

				// method for getting userdetails using ID

				if (userid != null)

					pro_owner = userservice.getUserDetailsById(userid);



				if (pro_owner != null)

					project.setProjectOwner(pro_owner);

				

				Long onsite_lead = requestdata.get("approver_level_2").asLong();

				UserModel pro_onsite_lead = new UserModel();

				if (onsite_lead != null) {

					pro_onsite_lead = userservice.getUserDetailsById(onsite_lead);

				}

				if (pro_onsite_lead != null) {

					project.setOnsite_lead(pro_onsite_lead);

				}

				project.setProjectTier(2);

			}
			project.setEstimatedHours(requestdata.get("estimatedHours").asInt());
			String startdate = requestdata.get("startDate").asText();
			String enddate = requestdata.get("endDate").asText();
			String releasingdate = requestdata.get("releasingDate").asText();

			// Formatting the dates before storing
			TimeZone zone = TimeZone.getTimeZone("MST");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			formatter.setTimeZone(zone);
			Date date1 = null, date2 = null, releaseDate = null;

			if (!startdate.isEmpty()) {
				date1 = formatter.parse(startdate);
				project.setStartDate(date1);
			}

			if (!enddate.isEmpty()) {
				date2 = formatter.parse(enddate);
				project.setEndDate(date2);

			}

			if (!releasingdate.isEmpty()) {
				releaseDate = formatter.parse(releasingdate);
				project.setReleasingDate(releaseDate);
			}

			if ((project.getProjectDetails() != null) && (project.getProjectDetails().length() > 0)
					&& (!project.getProjectDetails().equals(" ")) && (project.getProjectName() != null)
					&& (!project.getProjectName().equals(" ")) && (project.getProjectName().length() > 0)
					&& (project.getProjectCode() != null) && (!project.getProjectCode().equals(" "))
					&& (project.getProjectCode().length() > 0)) {
				// method invocation for checking duplicate entry for project name

				int result = projectservice.duplicationchecking(project.getProjectName());
				//if (result <= 1) {
					// Method invocation for creating new project record
					ProjectModel projectmodel = projectservice.save_project_record(project);

					ArrayNode resourcenode = (ArrayNode) requestdata.get("resources");
					ArrayNode regionsjson = (ArrayNode) requestdata.get("projectRegion");
					
					if (projectmodel != null)
					{
						ArrayList<ProjectRegion> regions = projectservice.getRegionsByprojectId(projectmodel.getProjectId());
						
						if(regions.size() > 0) {
						int i = projectservice.deleteProjectRegions(projectmodel.getProjectId());
						System.out.println("-----------i"+i);
						if(i>0) {
							
							for(JsonNode nodes : regionsjson) {
								System.out.println("Regions--------------Id"+nodes.asLong());	
								ProjectRegion regionedits = new ProjectRegion();
								regionedits.setProject_Id(projectmodel);
								Region region1 = regionService.getregion(nodes.asLong());
								regionedits.setRegion_Id(region1);
								projectservice.save_project_region(regionedits);
								
							}
							
						}
						}
						else {
								for(JsonNode nodes : regionsjson) {
								
								ProjectRegion regionedits = new ProjectRegion();
								regionedits.setProject_Id(projectmodel);
								Region region1 = regionService.getregion(nodes.asLong());
								regionedits.setRegion_Id(region1);
								projectservice.save_project_region(regionedits);
								
							}
						}
						
					}
				/*	for (JsonNode node : resourcenode) {
						// setting values on resource object
						Resources resou1 = projectservice.getResourceById(node.get("resourceId").asLong());
						if (projectmodel != null)
							resou1.setProject(projectmodel);

						Long depart = node.get("department").asLong();
						DepartmentModel department = new DepartmentModel();

						// method for getting department details
						if (depart != null)
							department = projectservice.getDepartmentDetails(depart);

						if (department != null)
							resou1.setDepartment(department);

						resou1.setresourceCount(node.get("resourceCount").asInt());

						// checking resouce model values before storing
						if ((resou1.getresourceCount() != 0) && (!resou1.getDepartment().equals(null))
								&& (resou1.getProject() != null)) {

							// method invocation for storing resource details
							Resources resourcevalue = projectservice.addprojectresouce(resou1);

							if (resourcevalue == null)
								responseflag = 1;
						} else
							responseflag = 1;

					}*/
				//} else {
				//	responseflag = 1;
				//}

			} else {
				responseflag = 1;
			}

			// setting values on response json
			if (responseflag == 0) {
				responsedata.put("status", "success");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.put("message", "Record Updated");

			} else {
				responsedata.put("status", "Failed");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.put("message", "Updation failed due to invalid credientials");

			}

		} catch (Exception e) {
			System.out.println("Exception : " + e);
			responsedata.put("status", "Failed");
			responsedata.put("code", httpstatus.getStatus());
			responsedata.put("message", "Exception " + e);

		}

		return responsedata;

	}

	@PutMapping(value = "/getProjectDetails/{projectId}")
	public JsonNode getProjectDetails(@PathVariable("projectId") Long projectId, HttpServletResponse servletresponse) {
		System.out.println("start");
		ObjectNode responseData = objectMapper.createObjectNode();
		ObjectNode response = objectMapper.createObjectNode();
		ArrayNode region = objectMapper.createArrayNode();
		// Object declarations
		ContractModel contract = null;
		ObjectNode contractobj = objectMapper.createObjectNode();
		ObjectNode clientobj = objectMapper.createObjectNode();
		ClientModel clientmodel = null;
		Long clientid = null;

		try {

			// method for getting project details by passing ID
			ProjectModel project = projectservice.getProjectId(projectId);

			if (project.equals(null)) {
				response.put("status", "success");
				response.set("payload", responseData);
				response.put("message", "Data Not Available");
				response.put("code", servletresponse.getStatus());
			} else {

				// assigning values on json object
				responseData.put("projectId", project.getProjectId());
				responseData.put("projectName", project.getProjectName());
				responseData.put("projectDetails", project.getProjectDetails());
				responseData.put("parentProjectId", project.getParentProjectId());
				responseData.put("estimatedHours", project.getEstimatedHours());
				responseData.put("startDate", project.getStartDate().toString());
				responseData.put("endDate", project.getEndDate().toString());
				responseData.put("isBillable", project.getisBillable());
				responseData.put("projectCategory", project.getProjectCategory());
				responseData.put("projectCode", project.getProjectCode());
				responseData.put("projectType", project.getprojectType());
				responseData.put("releasingDate", project.getReleasingDate().toString());
				responseData.put("isPOC", project.getisPOC());
				responseData.put("projectStatus", project.getprojectStatus());
				responseData.put("projectTier", project.getProjectTier());
				//responseData.put("projectRefId", project.getProject_refId());
				System.out.println("pro" + project.getProjectId());
				if (project.getClientName() != null)
					clientid = project.getClientName().getClientId();

				if (clientid != null)
					clientmodel = projectservice.getClientName(clientid);

				if (clientmodel == null)
					clientobj = null;
				else {
					clientobj.put("clientId", clientmodel.getClientId());
					clientobj.put("clientName", clientmodel.getClientName());
				}
				responseData.set("clientName", clientobj);
				responseData.put("clientPointOfContact", project.getClientPointOfContact());
				System.out.println("sec 1");
				// null checking contract type
				Long contractId = project.getContract().getContractTypeId();

				if (contractId != null) {
					// getting contract details
					contract = projectservice.getContract(contractId);
				}
				// storing contract values in jsonobject
				if (contract == null)
					contractobj = null;
				else {

					contractobj.put("contractTypeId", contract.getContractTypeId());
					contractobj.put("contractTypeName", contract.getContractTypeName());
				}
				responseData.set("contractType", contractobj);
				System.out.println("sec 2");
				// null checking user ID
				Long userid = project.getProjectOwner().getUserId();
				UserModel userdata = null;
				if (userid != null) {
					// getting user details
					userdata = userservice.getUserDetailsById(userid);
				}

				ObjectNode userobj = objectMapper.createObjectNode();
				// storing user values in jsonobject
				if (userdata == null)
					userobj = null;
				else {
					userobj.put("firstName", userdata.getFirstName());
					userobj.put("lastName", userdata.getLastName());
					userobj.put("role", userdata.getRole().getroleId());
					userobj.put("userId", userdata.getUserId());

				}
				System.out.println("sec 4");
				responseData.set("approver_level_1", userobj);
				ObjectNode onsite_leads = objectMapper.createObjectNode();
				if (project.getOnsite_lead() != null) {
					Long onsite_lead = project.getOnsite_lead().getUserId();
					UserModel lead_data = null;

					// storing user values in jsonobject
					if (onsite_lead != null) {
						lead_data = userservice.getUserDetailsById(onsite_lead);
					}

					if (lead_data == null)
						onsite_leads = null;
					else {
						onsite_leads.put("firstName", lead_data.getFirstName());
						onsite_leads.put("lastName", lead_data.getLastName());
						onsite_leads.put("role", lead_data.getRole().getroleId());
						onsite_leads.put("userId", lead_data.getUserId());

					}
					System.out.println("sec 4");

				}
				responseData.set("approver_level_2", onsite_leads);
				//get region list
				List<ProjectRegion> regions = projectservice.getregionlist(project.getProjectId());
				ArrayNode regionsArray = objectMapper.createArrayNode();
				ArrayList<Integer> regionArraylist = new ArrayList<Integer>();
				if(regions.isEmpty()) {
					responseData.set("projectRegion", regionsArray);
				}
				else {
					
					 for(ProjectRegion regioneach : regions) { 
						 ObjectNode resource = objectMapper.createObjectNode(); 
						 regionsArray.add((int)regioneach.getRegion_Id().getId());
						 
					 }
					 responseData.set("projectRegion", regionsArray);
				}
				//
				
				
				
				// getting list of resources based on project
				List<Resources> resourcelist = projectservice.getResourceList(project.getProjectId());
				ArrayNode resourceArray = objectMapper.createArrayNode();
				if (resourcelist.isEmpty())
					responseData.set("resource", resourceArray);
				else {

					// resoucewise looping to store data in Json array
					for (Resources resource : resourcelist) {

						// setting resouce details in json object
						ObjectNode resourceobj = objectMapper.createObjectNode();
						resourceobj.put("resourceId", resource.getResourceId());
						resourceobj.put("resourceCount", resource.getresourceCount());

						// getting projectdetails by ID

						ObjectNode projectobj = objectMapper.createObjectNode();
						if (project == null)
							projectobj = null;
						else {
							projectobj.put("projectId", project.getProjectId());
							projectobj.put("projectName", project.getProjectName());
						}
						resourceobj.set("project", projectobj);

						// getting departmentdetails by ID
						DepartmentModel department = projectservice
								.getDepartmentDetails(resource.getDepartment().getDepartmentId());
						ObjectNode departmentobj = objectMapper.createObjectNode();

						if (department == null)
							departmentobj = null;
						else {
							departmentobj.put("departmentId", department.getDepartmentId());
							departmentobj.put("departmentName", department.getdepartmentName());
						}

						resourceobj.set("department", departmentobj);
						// setting resouce json object on resource json array
						resourceArray.add(resourceobj);
					}
					responseData.set("resource", resourceArray);
				}

				response.put("status", "success");
				response.set("payload", responseData);
				response.put("message", "success");
				response.put("code", servletresponse.getStatus());
			}

		} catch (Exception e) {
			System.out.println("Exception " + e);
			response.put("status", "Failed");
			response.put("message", "Exception " + e);
			response.put("code", servletresponse.getStatus());
		}

		return response;
	}

//	Api for getting all parent projects
	@GetMapping(value = "/getAllParentProjects")
	public JsonNode getAllParentProjects(HttpServletResponse statusResponse) {
		ObjectNode responsedata = objectMapper.createObjectNode();
		ArrayNode projectArray = objectMapper.createArrayNode();

		try {
			// Getting all projects list to arraylist
			ArrayList<ProjectModel> projectlist = projectservice.getListofParentProjects();

			// checking for project arraylist is empty or not
			if (projectlist.isEmpty()) {
				responsedata.put("status", "success");
				responsedata.put("message", "No Records Available");
				responsedata.put("code", statusResponse.getStatus());
				responsedata.put("payload", "");
			} else {

				// loop for getting projectwise details
				for (ProjectModel obj : projectlist) {

					// storing projects details in json object
					ObjectNode jsonobj = objectMapper.createObjectNode();
					jsonobj.put("projectId", obj.getProjectId());
					jsonobj.put("projectName", obj.getProjectName());
					jsonobj.put("isBillable", obj.getisBillable());
					jsonobj.put("projectType", obj.getprojectType());
					jsonobj.put("projectStatus", obj.getprojectStatus());
					projectArray.add(jsonobj);
				}
				responsedata.put("status", "success");
				responsedata.put("message", "success");
				responsedata.put("code", statusResponse.getStatus());
				responsedata.set("payload", projectArray);

			}

		} catch (Exception e) {
			System.out.println("Exception : " + e);
			responsedata.put("status", "failed");
			responsedata.put("message", "Exception : " + e);
			responsedata.put("code", statusResponse.getStatus());
		}

		return responsedata;
	}

	@PostMapping("/createParentProject")
	public JsonNode save_newParentproject(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) {

		ObjectNode responsedata = objectMapper.createObjectNode();
		int responseflag = 0;
		try {

			// setting values to object from json request
			ProjectModel project = new ProjectModel();

			project.setParentProjectId(0);
			project.setProject_refId("0");
			project.setProjectCategory(1);
			project.setprojectType(1);
			project.setprojectStatus(1);
			project.setisPOC(0);
			project.setisBillable(0);
			project.setEstimatedHours(0);
			project.setProjectName(requestdata.get("projectName").asText());
			
			if ((project.getProjectName() != null) && (!project.getProjectName().equals(" "))
					&& (project.getProjectName().length() > 0)) {
				// method invocation for checking duplicate entry for project name
				int result1 = projectservice.duplicationchecking(project.getProjectName());
				if (result1 == 0) {
					// Method invocation for creating new project record

					ProjectModel projectmodel = projectservice.save_project_record(project);
					// method invocation for storing resouces of project created

					if (projectmodel == null) {
						responseflag = 1;
						responsedata.put("message", "Project record creation failed");
					}
						
				}
			}
			// setting values on response json
			if (responseflag == 0) {
				responsedata.put("status", "success");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.put("message", "Record Inserted");
				responsedata.put("payload", "");
			} else {
				responsedata.put("status", "Failed");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.put("payload", "");
			}

		} catch (Exception e) {
			System.out.println("Exception : " + e);
			responsedata.put("status", "Failed");
			responsedata.put("code", httpstatus.getStatus());
			responsedata.put("message", "Exception " + e);
			responsedata.put("payload", "");
		}
		return responsedata;
	}
	
	@PostMapping("/editParentProject")
	public JsonNode edit_Parentproject(@RequestBody JsonNode requestdata, HttpServletResponse httpstatus) {
		ObjectNode responsedata = objectMapper.createObjectNode();
		int responseflag = 0;
		
		try {
			ProjectModel project = projectservice.findById(requestdata.get("projectId").asLong());
			project.setProjectName(requestdata.get("projectName").asText());
			ProjectModel projectmodel = projectservice.save_project_record(project);
			
			if (projectmodel == null) {
				responseflag = 1;
				responsedata.put("message", "Project record creation failed");
			}
			
			if (responseflag == 0) {
				responsedata.put("status", "success");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.put("message", "Record Inserted");
				responsedata.put("payload", "");
			} else {
				responsedata.put("status", "Failed");
				responsedata.put("code", httpstatus.getStatus());
				responsedata.put("payload", "");
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			
			responsedata.put("status", "Failed");
			responsedata.put("code", httpstatus.getStatus());
			responsedata.put("message", "Exception " + e);
			responsedata.put("payload", "");
		}
		return responsedata;
	}

	

}
