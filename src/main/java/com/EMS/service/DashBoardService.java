package com.EMS.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface DashBoardService {

	ObjectNode getCountOfResourcesInBenchProject();
	ObjectNode getCountOfResourcesInBenchProject(Long regionId);

}
