package com.EMS.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.EMS.dto.ProjectSubmissionDataDTO;
import com.EMS.model.AllocationModel;
import com.EMS.repository.HolidayRepository;
import com.EMS.repository.ProjectAllocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.ApprovalTimeTrackReportModel;
import com.EMS.model.BenchProjectReportModel;
import com.EMS.repository.ProjectReportsRepository;
import com.EMS.repository.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
public class ReportServiceImpl implements ReportService {

	@Autowired
	ProjectReportsRepository projectReportsRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	ProjectAllocationRepository projectAllocationRepository;

	@Autowired
	ProjectExportService projectExportService;

	@Autowired
	HolidayRepository holidayRepository;

	public ArrayNode getProjectReportDetails(long projectId, Date fromDate, Date toDate) {
		ArrayNode array = objectMapper.createArrayNode();
		try {

			array = objectMapper.convertValue(
					projectReportsRepository.GenerateProjectReports(projectId, fromDate, toDate), ArrayNode.class);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return array;
	}

	public ArrayNode getBenchProjectReportDetails(long uId, Date fromDate, Date toDate) {
		ArrayNode array = objectMapper.createArrayNode();
		try {
			array = objectMapper.convertValue(
					projectReportsRepository.GenerateBenchProjectReports(uId, fromDate, toDate), ArrayNode.class);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return array;
	}

	public ArrayNode getBenchProjectReportDetails(Date fromDate, Date toDate) {
		ArrayNode array = objectMapper.createArrayNode();
		try {
			array = objectMapper.convertValue(projectReportsRepository.GenerateBenchProjectReports(fromDate, toDate),
					ArrayNode.class);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return array;
	}

	public List getProjectTaskReportDetails(Date fromDate, Date toDate, Long projectId) {
		List list = new ArrayList();
		try {
			list = projectReportsRepository.GenerateProjectTaskReportForExporting(fromDate, toDate, projectId);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return list;
	}

	public List<ApprovalTimeTrackReportModel> getApprovalStatusReport(Date startDate, Date endDate,
			int startDateOfMonth, int endDateOfMonth, int month, int year) {
		List<ApprovalTimeTrackReportModel> array = null;
		try {
			array = projectReportsRepository.getApprovalStatusReportDetails(startDate, endDate, startDateOfMonth,
					endDateOfMonth, month, year);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return array;
	}

	public List getProjectHourReportDetails(Date fromDate, Date toDate, int monthIndex, int yearIndex) {
		List list = new ArrayList();
		try {
			list = projectReportsRepository.GenerateProjectHourReportForExporting(fromDate, toDate, monthIndex,
					yearIndex);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return list;
	}

	public List<Object[]> getAllocationDetailsTechWise(long techId, Date fromDate, Date toDate) throws Exception {

		List<Object[]> result = projectRepository.getAllocationDetailsTechWise(techId, fromDate, toDate);

		return result;
	}

	public List<Object[]> getAllocatedProjectByUserId(long userId, Date fromDate, Date toDate) throws Exception {

		List<Object[]> result = projectRepository.getAllocatedProjectByUserId(userId, fromDate, toDate);

		return result;
	}

	public int getActualHours(long projectId, Date startDate, Date endDate) {
		List<AllocationModel> userdata = projectAllocationRepository.getUserDataByProjectAndDate(projectId, startDate,
				endDate);
		int userCount = userdata.size();
		int workingDays = 0;
		int totalWorkingHours = 0;
		int holidays = 0;
		int actualHours = 0;
		/*
		 * System.out.println("userCount--------"+userCount);
		 * System.out.println("projectId--------"+projectId);
		 */
		for (AllocationModel data : userdata) {
			Date curStartDate = startDate;
			Date curEndDate = endDate;
			if (startDate.compareTo(data.getStartDate()) < 0) {
				curStartDate = data.getStartDate();
			}
			if (endDate.compareTo(data.getEndDate()) > 0) {
				curEndDate = data.getEndDate();
			}
			workingDays = projectExportService.calculateWorkingDays(curStartDate, curEndDate);
			holidays = holidayRepository.getNationalHolidayListsByMonthRegion(curStartDate, curEndDate,
					data.getuser().getRegion().getId());
			totalWorkingHours = (workingDays - holidays) * 8;
			actualHours = actualHours + totalWorkingHours;
			/*
			 * System.out.println("user--------"+data.getuser().getFirstName());
			 * System.out.println("curStartDate--------"+curStartDate);
			 * System.out.println("curEndDate--------"+curEndDate);
			 * System.out.println("workingDays--------"+workingDays);
			 * System.out.println("holidays--------"+holidays);
			 * System.out.println("totalWorkingHours--------"+totalWorkingHours);
			 */

		}

		/*
		 * System.out.println("actualHours--------"+actualHours);
		 * System.out.println("****************************");
		 * System.out.println("****************************");
		 */
		return actualHours;

	}

	public ArrayNode getProjectReportDetailsByRegions(Long projectId, Date fromDate, Date toDate,
			Long regionIdSelected) {
		// TODO Auto-generated method stub
		ArrayNode array = objectMapper.createArrayNode();
		try {

			array = objectMapper.convertValue(projectReportsRepository.GenerateProjectReportsByRegion(projectId,
					fromDate, toDate, regionIdSelected), ArrayNode.class);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return array;

	}

	public List<BenchProjectReportModel> getBenchProjectReportDetailsReport(Long userId, Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		List<BenchProjectReportModel> data = projectReportsRepository.GenerateBenchProjectReports(userId, fromDate,
				toDate);
		return data;
	}

	public List<BenchProjectReportModel> getBenchProjectReportDetailsReport(Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		List<BenchProjectReportModel> data = projectReportsRepository.GenerateBenchProjectReports(fromDate, toDate);
		return data;
	}

	public List<BenchProjectReportModel> getBenchProjectReportDetailsReport(Long userId, Date fromDate, Date toDate,
			Long regionId) {
		// TODO Auto-generated method stub
		List<BenchProjectReportModel> data = projectReportsRepository.GenerateBenchProjectReports(fromDate, toDate,
				regionId);
		return data;
	}

	public ArrayNode getBenchProjectReportDetailsReports(Long getuId, Date fromDate, Date toDate, Long regionId) {
		// TODO Auto-generated method stub
		ArrayNode array = objectMapper.createArrayNode();
		try {
			array = objectMapper.convertValue(
					projectReportsRepository.GenerateBenchProjectReports(fromDate, toDate, regionId), ArrayNode.class);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return array;
	}

	public List<Object[]> getAllocationDetailsTechWiseRegionwise(Long techId, Date fromDate, Date toDate, Long regionId)
			throws Exception {
		List<Object[]> result = projectRepository.getAllocationDetailsTechWiseRegionwise(techId, fromDate, toDate,
				regionId);

		return result;
	}

	@Override
	public List<ProjectSubmissionDataDTO> getProjectSubmissionDetails(Integer month, Integer year, String session)
			throws Exception {
		List<ProjectSubmissionDataDTO> submittedDataList = new ArrayList<ProjectSubmissionDataDTO>();
		
		return submittedDataList;
	}

}
