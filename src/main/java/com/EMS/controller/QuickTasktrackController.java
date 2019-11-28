package com.EMS.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import com.EMS.model.*;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.service.QuickTaskTrackService;
import com.EMS.service.UserService;
import com.EMS.utility.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value = { "/tasktrack" })
public class QuickTasktrackController {

	@Autowired
	QuickTaskTrackService quickTasktrackService;

	@Autowired
	UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	// bala
	@PostMapping(value = "/addQuickTimeTrack", headers = "Accept=application/json")
	public JsonNode addQuickTimeTrack(@RequestBody JsonNode taskData, HttpServletResponse status)
			throws JSONException, ParseException {
		ObjectNode dataResponse = objectMapper.createObjectNode();

		try {
			Long uId = taskData.get("uId").asLong();
			Boolean saveFailed = false;

			if (!uId.equals(null)) {

				ArrayNode arrayNode = (ArrayNode) taskData.get("taskDetails");
				UserModel user = userService.getUserDetailsById(uId);
				
				long projectId = Constants.BEACH_PROJECT_ID;
				
				if (!taskData.get("isBeach").asBoolean() ) {
					projectId = taskData.get("projectId").asLong();
				}
				
				ProjectModel projectModel = quickTasktrackService
						.getProjectModelById(taskData.get("projectId").asLong());

				if (!user.equals(null)) {

					for (JsonNode node : arrayNode) {

						double hours = node.get("hours").asDouble();
						Long qTrackId = node.get("qTrackId").asLong();
						if (qTrackId != 0) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							sdf.setTimeZone(TimeZone.getDefault());

							Task taskCategory = quickTasktrackService.getTaskByName(Constants.QUICK_TIME_TRACK_DESC);
							Tasktrack tasktrack = new Tasktrack();
							tasktrack.setTask(taskCategory);
							tasktrack.setProject(projectModel);
							tasktrack.setUser(user);
							tasktrack.setId(qTrackId);
							tasktrack.setHours(node.get("hours").asDouble());
							tasktrack.setDate(sdf.parse(node.get("date").asText()));
							if (quickTasktrackService.updateTaskByName(tasktrack)) {
								dataResponse.put("status", "success");
							} else {
								dataResponse.put("status", "failure");
							}
							continue;
						}

						Tasktrack tasktrack = new Tasktrack();
						tasktrack.setUser(user);

						Task task = quickTasktrackService.getTaskByName(Constants.QUICK_TIME_TRACK_DESC);
						if (task != null)
							tasktrack.setTask(task);
						else {
							saveFailed = true;
							dataResponse.put("message", "Process failed due to invalid task Id");
						}

						tasktrack.setHours(hours);
						// storing projects
						
						if (projectId != 0L) {
							// ProjectModel proj = projectService.findById(projectId);
							if (projectModel != null)
								tasktrack.setProject(projectModel);
							else {
								saveFailed = true;
								dataResponse.put("message", "Process failed due to invalid project Id");
							}
						} else {
							saveFailed = true;
							dataResponse.put("message", "Process failed due to empty project Id");
						}
						// hardcoded description
						tasktrack.setDescription(Constants.QUICK_TIME_TRACK_DESC);
						// getting date
						if (!(node.get("date").asText().isEmpty())) {
							String dateNew = node.get("date").asText();
							TimeZone zone = TimeZone.getTimeZone("MST");
							SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
							outputFormat.setTimeZone(zone);
							Date date1;

							date1 = outputFormat.parse(dateNew);
							if (date1 != null)
								tasktrack.setDate(date1);
							else {
								saveFailed = true;
								dataResponse.put("message", "Process failed due to invalid date ");
							}
						} else {
							saveFailed = true;
							dataResponse.put("message", "Process failed due to empty date value ");
						}
						if (!saveFailed) {
							quickTasktrackService.saveTaskDetails(tasktrack);
							dataResponse.put("message", "success");

						}

					}

				} else {
					saveFailed = true;
					dataResponse.put("message", "Not a valid user Id");
				}
			} else {
				saveFailed = true;
				dataResponse.put("message", "user id is missing");
			}

			if (saveFailed)
				dataResponse.put("status", "Failed");
			else
				dataResponse.put("status", "success");
			dataResponse.put("code", status.getStatus());

		} catch (Exception e) {
			dataResponse.put("status", "failure");
			dataResponse.put("message", "Exception : " + e);
			System.out.println("Exception " + e);
		}

		return dataResponse;
	}

	/**
	 * @author Hashir
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/getQuickTimeTrack")
	public JSONObject getQuickTimeTrack(@RequestBody JsonNode request) throws Exception {
		JSONObject response = new JSONObject();
		try {
			response = quickTasktrackService.getQuickTimeTrack(request);
		}
		catch(Exception ex) {
			response.put("data", ex.getMessage());
			response.put("status", "failed");
		}
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/getBeachTimeTrack")
	public JSONObject getBeachQuickTimeTrack(@RequestBody JsonNode request ) {
				
		JSONObject response = new JSONObject();
		try {
			response = quickTasktrackService.getBeachQuickTimeTrack(request);
		}
		catch(Exception ex) {
			response.put("data", ex.getMessage());
			response.put("status", "failed");
		}
		return response;
		
	}
}
