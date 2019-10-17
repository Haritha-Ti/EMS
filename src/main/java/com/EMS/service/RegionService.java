package com.EMS.service;

import java.util.ArrayList;
import java.util.List;

import com.EMS.model.HolidayModel;
import com.EMS.model.Region;
import com.EMS.model.TimeZoneModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface RegionService {

	ObjectNode saveRegion(Region region);

	ObjectNode EditRegion(Long region_Id, String region_code, String region_name);

	List<Region> getlist();

	Region getregion(Long region_Id);

	ObjectNode deleteRegion(Long region_Id);

	ObjectNode saveHoliday(HolidayModel holiday);

	ObjectNode EditHoliday(HolidayModel holiday);

	ObjectNode deleteHoliday(Long holiday_Id);

	List<Object[]> getTimeZones();

	TimeZoneModel getZone(Long timezoneId);

	ArrayList<TimeZoneModel> getTimeZones1();

	

}
