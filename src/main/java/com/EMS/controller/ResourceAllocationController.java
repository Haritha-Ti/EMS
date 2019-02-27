package com.EMS.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.model.Alloc;
import com.EMS.model.DepartmentModel;
import com.EMS.model.UserModel;
import com.EMS.service.ProjectService;
import com.EMS.service.ResourceAllocationService;

@RestController
@RequestMapping(value = "/resource")
public class ResourceAllocationController {

	@Autowired
	ResourceAllocationService resourceAllocation;

	@Autowired
	ProjectService projectService;

	@PostMapping(value = "/add")
	public void getString(@RequestBody Alloc resourceAllocationModel) {
		resourceAllocation.save(resourceAllocationModel);

	}

	@GetMapping(value = "/findData")
	public List<Alloc> getDetails() {
		List<Alloc> list = resourceAllocation.getList();
		return list;
	}

	@GetMapping(value = "/getData/{id}")
	public ResponseEntity<Alloc> getData(@PathVariable("id") Long id) {
		Alloc alloc = resourceAllocation.findDataBy(id);
		return new ResponseEntity<Alloc>(alloc, HttpStatus.OK);

	}

	@DeleteMapping(value = "/deleteData/{id}")
	public ResponseEntity<Alloc> deleteData(@PathVariable("id") Long id) {
		resourceAllocation.remove(id);
		return new ResponseEntity<Alloc>(HttpStatus.OK);

	}

//	 @PutMapping(value = "/update", headers="Accept=application/json")
//	 public ResponseEntity<String> updateData(@RequestBody Alloc currentAlloc ){
//		 Alloc alloc = resourceAllocation.findDataBy(currentAlloc.getId());
//		 if(alloc == null) {
//			 return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
//		 }
//		 resourceAllocation.updateData(currentAlloc);
//		return new ResponseEntity<String>(HttpStatus.OK);
//		 
//	 }
//	 
	@PatchMapping(value = "/partialUpdate/{id}")
	public ResponseEntity<Alloc> partialupdate(@RequestBody Alloc alloc, @PathVariable("id") Long id) {
		Alloc allocs = resourceAllocation.findDataBy(id);
		if (allocs == null) {
			return new ResponseEntity<Alloc>(HttpStatus.NOT_FOUND);
		}
		Alloc alloc1 = resourceAllocation.updatePartially(alloc, id);
		return new ResponseEntity<Alloc>(alloc1, HttpStatus.OK);
	}
	
	
	
	

	// To get department name list

	@GetMapping(value = "/getUsers")
	public List getUsernameList() {
		List<UserModel> userList = resourceAllocation.getUserList();
		return userList;

	}

	// To get department name list

	@GetMapping(value = "/getDepartment")
	public List getDepartmentnameList() {
		List<DepartmentModel> departmentList = resourceAllocation.getDepartmentList();
		return departmentList;

	}

	// To get the allocation list based on project name

	@GetMapping(value = "/allocationList/{projectId}")
	public List<Alloc> getAllocationLists(@PathVariable("projectId") Long projectId) {
//		 Long projectId = projectService.getProjectId(projectName);
		List<Alloc> alloc = resourceAllocation.getAllocationList(projectId);
//		 List<Alloc> alloc = resourceAllocation.getList();
		return alloc;

	}

	// To update resource allocation data
	@PutMapping(value = "/update")
	public ResponseEntity<Alloc> updateData(@RequestBody Alloc newAlloc) {
		Long id = newAlloc.getId();
		try {
			Alloc oldAlloc = resourceAllocation.findDataBy(id);
			if (oldAlloc == null)
				return new ResponseEntity<Alloc>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			System.out.println(e);
		}
		Alloc alloc = resourceAllocation.updateData(newAlloc);
		return new ResponseEntity<Alloc>(alloc, HttpStatus.OK);
	}

}