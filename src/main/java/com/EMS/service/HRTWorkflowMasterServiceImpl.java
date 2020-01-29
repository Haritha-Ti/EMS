package com.EMS.service;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.dto.HRTWorkflowResponseMasterDto;
import com.EMS.model.HRTWorkflowMaster;
import com.EMS.repository.HRTWorkflowMasterRepository;

@Service
public class HRTWorkflowMasterServiceImpl implements HRTWorkflowMasterService {
	
	@Autowired
	HRTWorkflowMasterRepository HRTWorkflowMasterRepository;

	@Override
	public List<HRTWorkflowResponseMasterDto> getWorkflowByRegionId(Long regionId) {
		List<HRTWorkflowMaster> masterList = new ArrayList<HRTWorkflowMaster>();
		List<HRTWorkflowResponseMasterDto> masterFinalList = new ArrayList<HRTWorkflowResponseMasterDto>();
		masterList = HRTWorkflowMasterRepository.findByRegionId(regionId);
		masterList.forEach(HRTmaster -> {
			ModelMapper HRTMmasterMapper = new ModelMapper();
			HRTWorkflowResponseMasterDto HRTMasterIndividual = new HRTWorkflowResponseMasterDto();
			HRTMasterIndividual.setId(HRTmaster.getId());
			HRTMasterIndividual.setStage(HRTmaster.getStage());
			HRTMasterIndividual.setStatus(HRTmaster.getStatus());
			HRTMasterIndividual.setRegion(HRTmaster.getRegion() !=null?HRTmaster.getRegion().getRegion_name():null);
			HRTMasterIndividual.setUserName(HRTmaster.getUser() !=null?HRTmaster.getUser().getUsername():null);
			masterFinalList.add(HRTMasterIndividual);
		});
		return masterFinalList;
	}

}
