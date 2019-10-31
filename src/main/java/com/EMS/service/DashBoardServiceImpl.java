package com.EMS.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.AllocationModel;
import com.EMS.model.Technology;
import com.EMS.repository.ProjectAllocationRepository;
import com.EMS.repository.ProjectRepository;
import com.EMS.repository.TechnologyRepository;
import com.EMS.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class DashBoardServiceImpl implements DashBoardService {

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	ProjectAllocationRepository projectAllocation;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TechnologyRepository technologyRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public ObjectNode getCountOfResourcesInBenchProject() {

		ObjectNode jsonData = objectMapper.createObjectNode();
		ObjectNode technologyData = objectMapper.createObjectNode();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String datestring = formatter.format(date);
		System.out.println("datee :" + datestring);
//	    For getting the count of active projects
		int activeProjects = projectRepository.getActiveProjects(datestring);
		System.out.println("Active projects count : " + activeProjects);
		jsonData.put("projectCount", activeProjects);

//		For getting the count of active users
		int activeUsers = userRepository.getAllActiveUsers(datestring);
		System.out.println("active users count :" + activeUsers);
		jsonData.put("usersCount", activeUsers);

		
//		For getting count of available bench resources
		List<AllocationModel> allocationList = projectAllocation.getBenchResources(datestring);
		
		if (!allocationList.isEmpty()) {
			int benchUsersCount = allocationList.size();
			System.out.println("bench count :" + benchUsersCount);
			jsonData.put("benchUsersCount", benchUsersCount);

			Map<String, Integer> technologyCount = new HashMap<String, Integer>();
			for (AllocationModel allocationData : allocationList) {
				List<Object[]> tec = technologyRepository.gettechnology(allocationData.getuser().getUserId());

				for (Object[] technology : tec) {
					String value = technology[1].toString();
					if (technologyCount.containsKey(technology[1])) {
						int count = technologyCount.get(technology[1]);
						count++;
						technologyCount.put(value, count);
					} else
						technologyCount.put(value, 1);
				}
			}

			for (Map.Entry<String, Integer> entry : technologyCount.entrySet()) {

				technologyData.put(entry.getKey(), entry.getValue());
			}
			
		}else
			jsonData.put("benchUsersCount", 0);

			jsonData.set("technology count", technologyData);

		return jsonData;
	}
	
	@Override
	public ObjectNode getCountOfResourcesInBenchProject(Long regionId) {

		ObjectNode jsonData = objectMapper.createObjectNode();
		ObjectNode technologyData = objectMapper.createObjectNode();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String datestring = formatter.format(date);
		System.out.println("datee :" + datestring);
//	    For getting the count of active projects
		int activeProjects = projectRepository.getActiveProjectsCountByRegion(datestring, regionId);
		System.out.println("Active projects count : " + activeProjects);
		jsonData.put("projectCount", activeProjects);

//		For getting the count of active users
		int activeUsers = userRepository.getAllActiveUsersByRegion(regionId);
		System.out.println("active users count :" + activeUsers);
		jsonData.put("usersCount", activeUsers);

		
//		For getting count of available bench resources
		List<AllocationModel> allocationList = projectAllocation.getBenchResourcesRegionId(datestring,regionId);
		
		if (!allocationList.isEmpty()) {
			int benchUsersCount = allocationList.size();
			System.out.println("bench count :" + benchUsersCount);
			jsonData.put("benchUsersCount", benchUsersCount);

			Map<String, Integer> technologyCount = new HashMap<String, Integer>();
			for (AllocationModel allocationData : allocationList) {
				List<Object[]> tec = technologyRepository.gettechnology(allocationData.getuser().getUserId());

				for (Object[] technology : tec) {
					String value = technology[1].toString();
					if (technologyCount.containsKey(technology[1])) {
						int count = technologyCount.get(technology[1]);
						count++;
						technologyCount.put(value, count);
					} else
						technologyCount.put(value, 1);
				}
			}

			for (Map.Entry<String, Integer> entry : technologyCount.entrySet()) {

				technologyData.put(entry.getKey(), entry.getValue());
			}
			
		}else
			jsonData.put("benchUsersCount", 0);

			jsonData.set("technology count", technologyData);

		return jsonData;
	}

}
