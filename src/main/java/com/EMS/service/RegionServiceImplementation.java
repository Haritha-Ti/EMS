package com.EMS.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.Region;
import com.EMS.repository.RegionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class RegionServiceImplementation implements RegionService {

	
	@Autowired
	private RegionRepository regionRepository;
	
	@Autowired ObjectMapper objectMapper;
	
	
	@Override
	public ObjectNode saveRegion(Region region) {
		// TODO Auto-generated method stub
		ObjectNode responsedata = objectMapper.createObjectNode();
		
		try {
			Region regions = regionRepository.save(region);
			responsedata.put("status", "success");
			responsedata.put("message", "region saved successfully");
			responsedata.put("payload", "");
		}
		catch(Exception e) {
			responsedata.put("status", "Failed");
			responsedata.put("message", "Exception " + e);
			responsedata.put("payload", "");	
			
		}
		return responsedata;
	}


	@Override
	public ObjectNode EditRegion(Long region_Id, String region_code, String region_name) {
		// TODO Auto-generated method stub
		ObjectNode responsedata = objectMapper.createObjectNode();
		try {
		Region region = regionRepository.getOne(region_Id);
		region.setRegion_code(region_code);
		region.setRegion_name(region_name);
		regionRepository.save(region);
		responsedata.put("status", "success");
		responsedata.put("message", "region updated successfully");
		responsedata.put("payload", "");
		}
		catch(Exception e){
			responsedata.put("status", "Failed");
			responsedata.put("message", "Exception " + e);
			responsedata.put("payload", "");
		}
		
		return responsedata;
	}


	@Override
	public List<Region> getlist() {
		// TODO Auto-generated method stub
		
		
		return regionRepository.getlistofRegions();
	}


	@Override
	public Region getregion(Long region_Id) {
		// TODO Auto-generated method stub
		
		return regionRepository.getOne(region_Id);
	}


	@Override
	public ObjectNode deleteRegion(Long region_Id) {
		// TODO Auto-generated method stub
		ObjectNode responsedata = objectMapper.createObjectNode();
		try {
		Region region = regionRepository.getOne(region_Id);
		region.setDeleted(true);
		regionRepository.save(region);
		responsedata.put("status", "success");
		responsedata.put("message", "region deleted successfully");
		responsedata.put("payload", "");
		}
		catch(Exception e) {
			responsedata.put("status", "Failed");
			responsedata.put("message", "Exception " + e);
			responsedata.put("payload", "");
		}
		return responsedata;
	}

}
