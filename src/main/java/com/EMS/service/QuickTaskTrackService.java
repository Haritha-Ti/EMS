package com.EMS.service;


import org.json.simple.JSONObject;

import com.EMS.model.ProjectModel;
import com.EMS.model.Task;
import com.EMS.model.Tasktrack;
import com.fasterxml.jackson.databind.JsonNode;

public interface QuickTaskTrackService {

	ProjectModel getProjectModelById(long projectId);
	
	Task getTaskByName(String taskName);

	Boolean updateTaskByName(Tasktrack task);
	
	Tasktrack saveTaskDetails(Tasktrack task);
	
	JSONObject getQuickTimeTrack(JsonNode request) throws Exception;

}
