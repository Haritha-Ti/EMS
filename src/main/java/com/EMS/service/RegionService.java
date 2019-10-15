package com.EMS.service;

import java.util.List;

import com.EMS.model.HolidayModel;
import com.EMS.model.Region;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface RegionService {

	ObjectNode saveRegion(Region region);

	ObjectNode EditRegion(Long region_Id, String region_code, String region_name);

	List<Region> getlist();

	Region getregion(Long region_Id);

	ObjectNode deleteRegion(Long region_Id);

	

}
