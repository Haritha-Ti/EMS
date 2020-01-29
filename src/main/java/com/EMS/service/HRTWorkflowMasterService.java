package com.EMS.service;

import java.util.List;

import com.EMS.dto.HRTWorkflowResponseMasterDto;
import com.EMS.model.HRTWorkflowMaster;

public interface HRTWorkflowMasterService {

	List<HRTWorkflowResponseMasterDto> getWorkflowByRegionId(Long regionId);
	
	

}
