package com.EMS.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.model.HolidayModel;
import com.EMS.model.Region;
import com.EMS.service.RegionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/region")
public class RegionController {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private RegionService regionservice;

	
	// add region
	
		@PostMapping(value = "/addRegion")
		public ObjectNode addRegion(@RequestBody JSONObject requestdata,HttpServletResponse httpstatus) throws JSONException {
			
			ObjectNode jsonDataRes = objectMapper.createObjectNode();
			String region_name = null;
			String region_code = null ;
			Long region_Id = null;
			System.out.println("Here");
			
	       if(requestdata.get("region_name") != null && requestdata.get("region_name") != "") {
				
	    	   region_name = (String) requestdata.get("region_name");
			}

	       if(requestdata.get("region_code") != null && requestdata.get("region_code") != "") {
				
	    	   region_code = (String) requestdata.get("region_code");
			}
			
	       if(requestdata.get("region_Id") != null && requestdata.get("region_Id") != "") {
				
	    	   region_Id = Long.valueOf( requestdata.get("region_Id").toString());
			}
	       
	       if(region_Id == null) {
	       Region region = new Region();
	       region.setRegion_code(region_code);
	       region.setRegion_name(region_name);
	       region.setDeleted(false);
	       jsonDataRes = regionservice.saveRegion(region);
	       }
	       else {
	    	   jsonDataRes = regionservice.EditRegion(region_Id,region_code,region_name);
	    	   
	       }
	       
	       
			return jsonDataRes;
		}
		// get region list
		
		@GetMapping("/getRegionList")
		public ObjectNode getRegionList(HttpServletResponse httpstatus) {
			
			
			ObjectNode node = objectMapper.createObjectNode();
			ArrayNode userarray=objectMapper.createArrayNode();
			
			try {
			List<Region> regions = regionservice.getlist();
			
			for(Region r : regions) {
				
				ObjectNode n = objectMapper.createObjectNode();
				n.put("region_name", r.getRegion_name());
				n.put("region_code", r.getRegion_code());
				n.put("region_Id", r.getId());
				userarray.add(n);
			}
			node.put("status", "success");
			node.set("data", userarray);
			}
			catch(Exception e) {
				node.put("status", "failed ");
				node.set("data", null);
			}
			return node;
			
		}
		
		
		@GetMapping("/getRegionList/{region_Id}")
		public ObjectNode getRegion(@PathVariable("region_Id") Long region_Id,HttpServletResponse httpstatus) {
			
			ObjectNode node = objectMapper.createObjectNode();
			ObjectNode data = objectMapper.createObjectNode();
			
			Region region = regionservice.getregion(region_Id);
			
			data.put("region_name", region.getRegion_name());
			data.put("region_code", region.getRegion_code());
			data.put("region_Id", region.getId());
			
			node.set("data", data);
			return node;
		}
		
		
		
		@PutMapping("/deleteregion")
		public ObjectNode deleteRegion(HttpServletResponse httpstatus,@RequestBody JSONObject requestdata) {
			
			ObjectNode node = objectMapper.createObjectNode();
			
			Long region_Id = null;
			 if(requestdata.get("region_Id") != null && requestdata.get("region_Id") != "") {
					
		    	   region_Id = Long.valueOf( requestdata.get("region_Id").toString());
				}
			
			node = regionservice.deleteRegion(region_Id);
			
			
			return node;
		}

		@PostMapping(value = "/addHoliday")
		public ObjectNode addHoliday(@RequestBody JSONObject requestdata,HttpServletResponse httpstatus) throws JSONException {
			
			ObjectNode jsonDataRes = objectMapper.createObjectNode();
			String holiday_name = null;
			String holiday_type = null ;
			Date holiday_date = null;
			Long holiday_id = null;
			String holiday_day = null;
			Long region_id = null;
			
			System.out.println("Here");
			
	       if(requestdata.get("holiday_name") != null && requestdata.get("holiday_name") != "") {
				
	    	   holiday_name = (String) requestdata.get("holiday_name");
			}

	       if(requestdata.get("holiday_type") != null && requestdata.get("holiday_type") != "") {
				
	    	   holiday_type = (String) requestdata.get("holiday_type");
			}
			
	       if(requestdata.get("holiday_id") != null && requestdata.get("holiday_id") != "") {
				
	    	   holiday_id = Long.valueOf( requestdata.get("holiday_id").toString());
			}
	       

	       if(requestdata.get("region_id") != null && requestdata.get("region_id") != "") {
				
	    	   region_id = Long.valueOf( requestdata.get("region_id").toString());
			}
	       
	       String date1 = (String) requestdata.get("holiday_date");
			

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			if (!date1.isEmpty()) {
				try {
					holiday_date = outputFormat.parse(date1);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
		        System.out.println(simpleDateformat.format(holiday_date));
		        holiday_day =  simpleDateformat.format(holiday_date);
			}
	       
	       
	       if(holiday_id == null) {
	       
	    	   HolidayModel holiday = new HolidayModel();
	    	   holiday.setDate(holiday_date);
	    	   holiday.setHolidayType(holiday_type);
	    	   holiday.setDay(holiday_day);
	    	   holiday.setHolidayName(holiday_name);
	    	 Region region =  regionservice.getregion(region_id);
	    	   holiday.setRegion_id(region);
	       jsonDataRes = regionservice.saveHoliday(holiday);
	       }
	       else {
	    	   
	    	   HolidayModel holiday = new HolidayModel();
	    	   holiday.setDate(holiday_date);
	    	   holiday.setHolidayType(holiday_type);
	    	   holiday.setDay(holiday_day);
	    	   holiday.setHolidayName(holiday_name);
	    	 Region region =  regionservice.getregion(region_id);
	    	   holiday.setRegion_id(region);
	    	   holiday.setHolidayId(holiday_id);
	    	   jsonDataRes = regionservice.EditHoliday(holiday);
	    	   
	       }
	       
	       
			return jsonDataRes;
		}
		
		
		@PutMapping("/deleteHoliday")
		public ObjectNode deleteHoliday(HttpServletResponse httpstatus,@RequestBody JSONObject requestdata) {
			
			ObjectNode node = objectMapper.createObjectNode();
			
			Long holiday_Id = null;
			 if(requestdata.get("holiday_Id") != null && requestdata.get("holiday_Id") != "") {
					
				 holiday_Id = Long.valueOf( requestdata.get("holiday_Id").toString());
				}
			
			node = regionservice.deleteHoliday(holiday_Id);
			
			
			return node;
		}
		
		
		@GetMapping("/getTimeZoneList")
		public ObjectNode getTimeZoneList(HttpServletResponse httpstatus) {
			
			
			ObjectNode node = objectMapper.createObjectNode();
			ArrayNode userarray=objectMapper.createArrayNode();
			
			try {
			List<Object[]> timezones = regionservice.getTimeZones();
			
			for(Object[] r : timezones) {
				
				ObjectNode n = objectMapper.createObjectNode();
				n.put("timezone_name", r[1].toString());
				n.put("timezone_code", r[0].toString());
				n.put("timezone_Id", r[2].toString());
				userarray.add(n);
			}
			node.put("status", "success");
			node.set("data", userarray);
			}
			catch(Exception e) {
				node.put("status", "failed ");
				node.set("data", null);
			}
			return node;
			
		}
}
