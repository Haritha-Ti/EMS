package com.EMS.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.EMS.dto.ProjectSubmissionDataDTO;
import com.fasterxml.jackson.databind.node.ArrayNode;

public interface ReportService {

	public ArrayNode getProjectReportDetails(long projectId, Date fromDate, Date toDate);

	public ArrayNode getBenchProjectReportDetails(long uId, Date fromDate, Date toDate);

	public ArrayNode getBenchProjectReportDetails(Date fromDate, Date toDate);

	public List getProjectTaskReportDetails(Date fromDate, Date toDate, Long projectId);

	public List getProjectHourReportDetails(Date fromDate, Date toDate, int monthIndex, int yearIndex);

	int getActualHours(long projectId, Date startDate, Date endDate);

	public List<Object[]> getAllocationDetailsTechWiseRegionwise(Long techId, Date fromDate, Date toDate, Long regionId)
			throws Exception;

	public List<ProjectSubmissionDataDTO> getProjectSubmissionDetails(Integer month, Integer year, Long regionId)
			throws Exception;

	public HashMap<String, Object> getUsersProjectSubmissionDetails(Long projectid, Integer projectTyre, Long userId,
			Integer month, Integer year, String session) throws Exception;

	public JSONObject getFreeAllocationReport(Long regionId_selected, Date fromDate, Date toDate);

	List<HashMap<String, Object>> getRegionLeaves(Integer region, Integer month, Integer year) throws Exception;

	public List<Map<String, Object>> getSubmissionDetailsForFinanceFullReport(Long projectid, Integer projectTyre,
			Long userId, Integer month, Integer year, String session) throws Exception;
}
