package com.EMS.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.dto.ProjectSubmissionDataDTO;
import com.EMS.dto.UsersEntryApprovalDetailsDTO;
import com.EMS.model.AllocationModel;
import com.EMS.model.ApprovalTimeTrackReportModel;
import com.EMS.model.BenchProjectReportModel;
import com.EMS.model.FreeAllocationReportModel;
import com.EMS.model.TaskTrackApproval;
import com.EMS.model.TaskTrackApprovalFinal;
import com.EMS.model.Tasktrack;
import com.EMS.model.UserLeaveSummary;
import com.EMS.repository.HolidayRepository;
import com.EMS.repository.LeaveRepository;
import com.EMS.repository.ProjectAllocationRepository;
import com.EMS.repository.ProjectReportsRepository;
import com.EMS.repository.ProjectRepository;
import com.EMS.repository.TaskTrackFinalJPARepository;
import com.EMS.repository.TasktrackRepository;
import com.EMS.repository.TimeTrackApprovalJPARepository;
import com.EMS.repository.UserLeaveSummaryRepository;
import com.EMS.utility.Constants;
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

	@Autowired
	TasktrackRepository tasktrackRepository;

	@Autowired
	TimeTrackApprovalJPARepository tasktrackApprovalRepository;

	@Autowired
	TaskTrackFinalJPARepository tasktrackFinalRepository;

	@Autowired
	UserLeaveSummaryRepository userLeaveSummaryRepository;

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

	/**
	 * @author sreejith.j
	 */
	@Override
	public List<ProjectSubmissionDataDTO> getProjectSubmissionDetails(Integer month, Integer year, Long regionId)
			throws Exception {
		Object[][] resultList = null;
		List<ProjectSubmissionDataDTO> submissionDataList = new ArrayList<ProjectSubmissionDataDTO>();

		resultList = projectRepository.getProjectWiseApprovalReportForFirstHalf(year + "-" + month + "-" + "01",
				regionId);

		HashMap<Long, ProjectSubmissionDataDTO> projectSubmissionMap = new HashMap<Long, ProjectSubmissionDataDTO>();
		HashMap<Long, HashMap<Integer, UsersEntryApprovalDetailsDTO>> projectMap = new HashMap<Long, HashMap<Integer, UsersEntryApprovalDetailsDTO>>();
		ProjectSubmissionDataDTO submissionObj = new ProjectSubmissionDataDTO();
		HashMap<Integer, UsersEntryApprovalDetailsDTO> approver1List = new HashMap<Integer, UsersEntryApprovalDetailsDTO>();
		for (Object[] resultObj : resultList) {
			Long projectId = Long.parseLong(
					(String.valueOf(resultObj[0]) == null || String.valueOf(resultObj[0]).equalsIgnoreCase("null"))
							? "0"
							: String.valueOf(resultObj[0]));
			String projectName = (String.valueOf(resultObj[1]) == null
					|| String.valueOf(resultObj[1]).equalsIgnoreCase("null")) ? null : String.valueOf(resultObj[1]);
			Integer projectTier = Integer.parseInt(
					(String.valueOf(resultObj[2]) == null || String.valueOf(resultObj[2]).equalsIgnoreCase("null"))
							? "0"
							: String.valueOf(resultObj[2]));
			String clientName = (String.valueOf(resultObj[3]) == null
					|| String.valueOf(resultObj[3]).equalsIgnoreCase("null")) ? null : String.valueOf(resultObj[3]);
			Integer userId = Integer.parseInt(
					(String.valueOf(resultObj[4]) == null || String.valueOf(resultObj[4]).equalsIgnoreCase("null"))
							? "0"
							: String.valueOf(resultObj[4]));
			String userName = (String.valueOf(resultObj[5]) == null
					|| String.valueOf(resultObj[5]).equalsIgnoreCase("null")) ? null : String.valueOf(resultObj[5]);
			String region = (String.valueOf(resultObj[6]) == null
					|| String.valueOf(resultObj[6]).equalsIgnoreCase("null")) ? null : String.valueOf(resultObj[6]);
			Integer approver1Id = Integer.parseInt(
					(String.valueOf(resultObj[7]) == null || String.valueOf(resultObj[7]).equalsIgnoreCase("null"))
							? "0"
							: String.valueOf(resultObj[7]));
			String approver1Name = (String.valueOf(resultObj[8]) == null
					|| String.valueOf(resultObj[8]).equalsIgnoreCase("null")) ? null : String.valueOf(resultObj[8]);
			Double approver1FirstHalfHours = Double.parseDouble(
					(String.valueOf(resultObj[9]) == null || String.valueOf(resultObj[9]).equalsIgnoreCase("null"))
							? "0"
							: String.valueOf(resultObj[9]));
			String projectType1 = (String.valueOf(resultObj[10]) == null
					|| String.valueOf(resultObj[10]).equalsIgnoreCase("null")) ? null : String.valueOf(resultObj[10]);
			String appr1FirstHalfStatus = (String.valueOf(resultObj[11]) == null
					|| String.valueOf(resultObj[11]).equalsIgnoreCase("null"))
							? Constants.TASKTRACK_APPROVER_STATUS_OPEN
							: String.valueOf(resultObj[11]);
			Integer approver2Id = Integer.parseInt(
					(String.valueOf(resultObj[12]) == null || String.valueOf(resultObj[12]).equalsIgnoreCase("null"))
							? "0"
							: String.valueOf(resultObj[12]));
			String approver2Name = (String.valueOf(resultObj[13]) == null
					|| String.valueOf(resultObj[13]).equalsIgnoreCase("null")) ? null : String.valueOf(resultObj[13]);
			Double approver2FirstHalfHours = Double.parseDouble(
					(String.valueOf(resultObj[14]) == null || String.valueOf(resultObj[14]).equalsIgnoreCase("null"))
							? "0"
							: String.valueOf(resultObj[14]));
			String projectType2 = (String.valueOf(resultObj[15]) == null
					|| String.valueOf(resultObj[15]).equalsIgnoreCase("null")) ? null : String.valueOf(resultObj[15]);
			String appr2FirstHalfStatus = (String.valueOf(resultObj[16]) == null
					|| String.valueOf(resultObj[16]).equalsIgnoreCase("null")) ? Constants.TASKTRACK_FINAL_STATUS_OPEN
							: String.valueOf(resultObj[16]);
			Double approver1SecondHalfHours = Double.parseDouble(
					(String.valueOf(resultObj[17]) == null || String.valueOf(resultObj[17]).equalsIgnoreCase("null"))
							? "0"
							: String.valueOf(resultObj[17]));
			String appr1SecondHalfStatus = (String.valueOf(resultObj[18]) == null
					|| String.valueOf(resultObj[18]).equalsIgnoreCase("null"))
							? Constants.TASKTRACK_APPROVER_STATUS_OPEN
							: String.valueOf(resultObj[18]);
			Double approver2SecondHalfHours = Double.parseDouble(
					(String.valueOf(resultObj[19]) == null || String.valueOf(resultObj[19]).equalsIgnoreCase("null"))
							? "0"
							: String.valueOf(resultObj[19]));
			String appr2SecondHalfStatus = (String.valueOf(resultObj[20]) == null
					|| String.valueOf(resultObj[20]).equalsIgnoreCase("null")) ? Constants.TASKTRACK_FINAL_STATUS_OPEN
							: String.valueOf(resultObj[20]);

			if (projectId != 0) {
				if (!(projectMap.containsKey(projectId))) {
					HashMap<String, Object> approver1 = new HashMap<String, Object>();
					HashMap<String, Object> approver2 = new HashMap<String, Object>();
					if (approver1Id != 0) {
						approver1.put("id", approver1Id);
						approver1.put("name", approver1Name);
					} else {
						approver1 = null;
					}
					if (approver2Id != 0) {
						approver2.put("id", approver2Id);
						approver2.put("name", approver2Name);
					} else {
						approver2 = null;
					}
					submissionObj = new ProjectSubmissionDataDTO(projectId, projectName, projectTier, clientName,
							approver1, approver2);
					projectSubmissionMap.put(projectId, submissionObj);
				}
				if (userId != 0) {
					if (projectMap.containsKey(projectId)) {
						approver1List = projectMap.get(projectId);
						if (approver1List.containsKey(userId)) {
							UsersEntryApprovalDetailsDTO obj = approver1List.get(userId);
							if (projectType1 != null && (projectType1
									.equalsIgnoreCase(Constants.TASKTRACK_PROJECT_TYPE_BILLABLE)
									|| projectType1.equalsIgnoreCase(Constants.TASKTRACK_PROJECT_TYPE_OVERTIME))) {
								obj.setAppr1FirstHalfTotalHours(
										obj.getAppr1FirstHalfTotalHours() + approver1FirstHalfHours);
								obj.setAppr1SecondHalfTotalHours(
										obj.getAppr1SecondHalfTotalHours() + approver1SecondHalfHours);
							}
							if (projectType2 != null && (projectType2
									.equalsIgnoreCase(Constants.TASKTRACK_PROJECT_TYPE_BILLABLE)
									|| projectType2.equalsIgnoreCase(Constants.TASKTRACK_PROJECT_TYPE_OVERTIME))) {
								obj.setAppr2FirstHalfTotalHours(
										obj.getAppr2FirstHalfTotalHours() + approver2FirstHalfHours);
								obj.setAppr2SecondHalfTotalHours(
										obj.getAppr2SecondHalfTotalHours() + approver2SecondHalfHours);
							}
						} else {
							Double ap1FHHours = 0.0;
							Double ap1SHHours = 0.0;
							Double ap2FHHours = 0.0;
							Double ap2SHHours = 0.0;
							if (projectType1 != null && (projectType1
									.equalsIgnoreCase(Constants.TASKTRACK_PROJECT_TYPE_BILLABLE)
									|| projectType1.equalsIgnoreCase(Constants.TASKTRACK_PROJECT_TYPE_OVERTIME))) {
								ap1FHHours = approver1FirstHalfHours;
								ap1SHHours = approver1SecondHalfHours;
							}
							if (projectType2 != null && (projectType2
									.equalsIgnoreCase(Constants.TASKTRACK_PROJECT_TYPE_BILLABLE)
									|| projectType2.equalsIgnoreCase(Constants.TASKTRACK_PROJECT_TYPE_OVERTIME))) {
								ap2FHHours = approver2FirstHalfHours;
								ap2SHHours = approver2SecondHalfHours;
							}
							approver1List.put(userId,
									new UsersEntryApprovalDetailsDTO(userId, userName, region, ap1FHHours, ap1SHHours,
											appr1FirstHalfStatus, appr1SecondHalfStatus, ap2FHHours, ap2SHHours,
											appr2FirstHalfStatus, appr2SecondHalfStatus));
						}
					} else {
						approver1List = new HashMap<Integer, UsersEntryApprovalDetailsDTO>();
						Double ap1FHHours = 0.0;
						Double ap1SHHours = 0.0;
						Double ap2FHHours = 0.0;
						Double ap2SHHours = 0.0;
						if (projectType1 != null
								&& (projectType1.equalsIgnoreCase(Constants.TASKTRACK_PROJECT_TYPE_BILLABLE)
										|| projectType1.equalsIgnoreCase(Constants.TASKTRACK_PROJECT_TYPE_OVERTIME))) {
							ap1FHHours = approver1FirstHalfHours;
							ap1SHHours = approver1SecondHalfHours;
						}
						if (projectType2 != null
								&& (projectType2.equalsIgnoreCase(Constants.TASKTRACK_PROJECT_TYPE_BILLABLE)
										|| projectType2.equalsIgnoreCase(Constants.TASKTRACK_PROJECT_TYPE_OVERTIME))) {
							ap2FHHours = approver2FirstHalfHours;
							ap2SHHours = approver2SecondHalfHours;
						}
						approver1List.put(userId,
								new UsersEntryApprovalDetailsDTO(userId, userName, region, ap1FHHours, ap1SHHours,
										appr1FirstHalfStatus, appr1SecondHalfStatus, ap2FHHours, ap2SHHours,
										appr2FirstHalfStatus, appr2SecondHalfStatus));
						projectMap.put(projectId, approver1List);
					}
				}

			}
		}
		for (Long key : projectSubmissionMap.keySet()) {
			ProjectSubmissionDataDTO submissionDataObj = projectSubmissionMap.get(key);
			List<UsersEntryApprovalDetailsDTO> usersList = new ArrayList<UsersEntryApprovalDetailsDTO>();
			if (projectMap.get(key) != null) {
				for (Integer usersKey : projectMap.get(key).keySet()) {
					usersList.add(projectMap.get(key).get(usersKey));
				}
			}
			submissionDataObj.setUsers(usersList);
			submissionDataList.add(submissionDataObj);
		}
		return submissionDataList;
	}

	/**
	 * @author sreejith.j
	 */
	@Override
	public HashMap<String, Object> getUsersProjectSubmissionDetails(Long projectId, Integer projectTyre, Long userId,
			Integer month, Integer year, String session) throws Exception {
		HashMap<String, Object> response = new HashMap<String, Object>();
		int startDay = 1, endDay = 15;
		Calendar startCal = Calendar.getInstance();
		SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
		startCal.setTime(frmt.parse(year + "-" + month + "-" + 1));
		Date startDate = startCal.getTime();
		Calendar endCal = Calendar.getInstance();
		endCal = (Calendar) startCal.clone();

		if (!session.equalsIgnoreCase("FIRST")) {
			startDay = 16;
			endDay = startCal.getActualMaximum(Calendar.DATE);
		}
		endCal.setTime(frmt.parse(year + "-" + month + "-" + endDay));
		Date endDate = endCal.getTime();
		Map<String, Double> timeTrackEntriesMap = new LinkedHashMap<String, Double>();
		Map<String, String> vacationEntriesMap = new LinkedHashMap<String, String>();

		Map<String, LinkedHashMap<String, Double>> timeTrackApprover1EntriesMap = new LinkedHashMap<String, LinkedHashMap<String, Double>>();
		LinkedHashMap<String, Double> billableApprover1EntriesMap = new LinkedHashMap<String, Double>();
		LinkedHashMap<String, Double> nonBillableApprover1EntriesMap = new LinkedHashMap<String, Double>();
		LinkedHashMap<String, Double> overtimeApprover1EntriesMap = new LinkedHashMap<String, Double>();
		LinkedHashMap<String, Double> beachApprover1EntriesMap = new LinkedHashMap<String, Double>();
		timeTrackApprover1EntriesMap.put(Constants.TASKTRACK_PROJECT_TYPE_BILLABLE, billableApprover1EntriesMap);
		timeTrackApprover1EntriesMap.put(Constants.TASKTRACK_PROJECT_TYPE_NON_BILLABLE, nonBillableApprover1EntriesMap);
		timeTrackApprover1EntriesMap.put(Constants.TASKTRACK_PROJECT_TYPE_OVERTIME, overtimeApprover1EntriesMap);
		timeTrackApprover1EntriesMap.put(Constants.TASKTRACK_PROJECT_TYPE_BEACH, beachApprover1EntriesMap);

		Map<String, LinkedHashMap<String, Double>> timeTrackApprover2EntriesMap = new LinkedHashMap<String, LinkedHashMap<String, Double>>();
		LinkedHashMap<String, Double> billableApprover2EntriesMap = new LinkedHashMap<String, Double>();
		LinkedHashMap<String, Double> nonBillableApprover2EntriesMap = new LinkedHashMap<String, Double>();
		LinkedHashMap<String, Double> overtimeApprover2EntriesMap = new LinkedHashMap<String, Double>();
		LinkedHashMap<String, Double> beachApprover2EntriesMap = new LinkedHashMap<String, Double>();
		timeTrackApprover2EntriesMap.put(Constants.TASKTRACK_PROJECT_TYPE_BILLABLE, billableApprover2EntriesMap);
		timeTrackApprover2EntriesMap.put(Constants.TASKTRACK_PROJECT_TYPE_NON_BILLABLE, nonBillableApprover2EntriesMap);
		timeTrackApprover2EntriesMap.put(Constants.TASKTRACK_PROJECT_TYPE_OVERTIME, overtimeApprover2EntriesMap);
		timeTrackApprover2EntriesMap.put(Constants.TASKTRACK_PROJECT_TYPE_BEACH, beachApprover2EntriesMap);
		response.put("timetrack", timeTrackEntriesMap);
		response.put("vacation", vacationEntriesMap);
		response.put("approver2Timetrack", timeTrackApprover2EntriesMap);

		List<TaskTrackApproval> appr1TimeTrackEntriesObj = new ArrayList<TaskTrackApproval>();

		List<Object[]> timeTrackEntriesList = tasktrackRepository.getTaskTrackProjectHoursForUser(userId, projectId,
				month, year, startDay, endDay);
		for (int idx = startDay; idx <= endDay; idx++) {
			String date = year + "-" + (month < 10 ? "0" : "") + month + "-" + (idx < 10 ? "0" : "") + idx;
			timeTrackEntriesMap.put(date, 0.0);
			billableApprover1EntriesMap.put(date, 0.0);
			nonBillableApprover1EntriesMap.put(date, 0.0);
			overtimeApprover1EntriesMap.put(date, 0.0);
			beachApprover1EntriesMap.put(date, 0.0);
			billableApprover2EntriesMap.put(date, 0.0);
			nonBillableApprover2EntriesMap.put(date, 0.0);
			overtimeApprover2EntriesMap.put(date, 0.0);
			beachApprover2EntriesMap.put(date, 0.0);
		}
		for (Object[] obj : timeTrackEntriesList) {
			timeTrackEntriesMap.put(String.valueOf(obj[0]),
					timeTrackEntriesMap.get(String.valueOf(obj[0])) + Double.valueOf(String.valueOf(obj[1])));
		}

		List<UserLeaveSummary> userLeaves = userLeaveSummaryRepository.getLeaveListForUserByDateRange(userId, startDate,
				endDate);
		for (UserLeaveSummary leaveObj : userLeaves) {
			vacationEntriesMap.put(frmt.format(leaveObj.getLeaveDate()), leaveObj.getLeaveType());
		}

		if (projectTyre == 2) {
			response.put("approver1Timetrack", timeTrackApprover1EntriesMap);
			appr1TimeTrackEntriesObj = tasktrackApprovalRepository
					.findByUserUserIdAndProjectProjectIdAndMonthAndYear(userId, projectId, month, year);
			for (TaskTrackApproval taskTrackApprovalObj : appr1TimeTrackEntriesObj) {
				LinkedHashMap<String, Double> mapObj = new LinkedHashMap<>();
				if (taskTrackApprovalObj.getProjectType().equalsIgnoreCase("Billable")) {
					mapObj = timeTrackApprover1EntriesMap.get(Constants.TASKTRACK_PROJECT_TYPE_BILLABLE);
				} else if (taskTrackApprovalObj.getProjectType().equalsIgnoreCase("Non-Billable")) {
					mapObj = timeTrackApprover1EntriesMap.get(Constants.TASKTRACK_PROJECT_TYPE_NON_BILLABLE);
				} else if (taskTrackApprovalObj.getProjectType().equalsIgnoreCase("Overtime")) {
					mapObj = timeTrackApprover1EntriesMap.get(Constants.TASKTRACK_PROJECT_TYPE_OVERTIME);
				} else if (taskTrackApprovalObj.getProjectType().equalsIgnoreCase("Beach")) {
					mapObj = timeTrackApprover1EntriesMap.get(Constants.TASKTRACK_PROJECT_TYPE_BEACH);
				}
				for (String key : mapObj.keySet()) {
					String day = key.substring(8);
					switch (day) {
					case "01":
						mapObj.put(key, taskTrackApprovalObj.getDay1());
						break;
					case "02":
						mapObj.put(key, taskTrackApprovalObj.getDay2());
						break;
					case "03":
						mapObj.put(key, taskTrackApprovalObj.getDay3());
						break;
					case "04":
						mapObj.put(key, taskTrackApprovalObj.getDay4());
						break;
					case "05":
						mapObj.put(key, taskTrackApprovalObj.getDay5());
						break;
					case "06":
						mapObj.put(key, taskTrackApprovalObj.getDay6());
						break;
					case "07":
						mapObj.put(key, taskTrackApprovalObj.getDay7());
						break;
					case "08":
						mapObj.put(key, taskTrackApprovalObj.getDay8());
						break;
					case "09":
						mapObj.put(key, taskTrackApprovalObj.getDay9());
						break;
					case "10":
						mapObj.put(key, taskTrackApprovalObj.getDay10());
						break;
					case "11":
						mapObj.put(key, taskTrackApprovalObj.getDay11());
						break;
					case "12":
						mapObj.put(key, taskTrackApprovalObj.getDay12());
						break;
					case "13":
						mapObj.put(key, taskTrackApprovalObj.getDay13());
						break;
					case "14":
						mapObj.put(key, taskTrackApprovalObj.getDay14());
						break;
					case "15":
						mapObj.put(key, taskTrackApprovalObj.getDay15());
						break;
					case "16":
						mapObj.put(key, taskTrackApprovalObj.getDay16());
						break;
					case "17":
						mapObj.put(key, taskTrackApprovalObj.getDay17());
						break;
					case "18":
						mapObj.put(key, taskTrackApprovalObj.getDay18());
						break;
					case "19":
						mapObj.put(key, taskTrackApprovalObj.getDay19());
						break;
					case "20":
						mapObj.put(key, taskTrackApprovalObj.getDay20());
						break;
					case "21":
						mapObj.put(key, taskTrackApprovalObj.getDay21());
						break;
					case "22":
						mapObj.put(key, taskTrackApprovalObj.getDay22());
						break;
					case "23":
						mapObj.put(key, taskTrackApprovalObj.getDay23());
						break;
					case "24":
						mapObj.put(key, taskTrackApprovalObj.getDay24());
						break;
					case "25":
						mapObj.put(key, taskTrackApprovalObj.getDay25());
						break;
					case "26":
						mapObj.put(key, taskTrackApprovalObj.getDay26());
						break;
					case "27":
						mapObj.put(key, taskTrackApprovalObj.getDay27());
						break;
					case "28":
						mapObj.put(key, taskTrackApprovalObj.getDay28());
						break;
					case "29":
						mapObj.put(key, taskTrackApprovalObj.getDay29());
						break;
					case "30":
						mapObj.put(key, taskTrackApprovalObj.getDay30());
						break;
					case "31":
						mapObj.put(key, taskTrackApprovalObj.getDay31());
						break;
					default:
						break;
					}
				}
			}
		}

		List<TaskTrackApprovalFinal> appr2TimeTrackEntriesObj = tasktrackFinalRepository
				.findByUserUserIdAndProjectProjectIdAndMonthAndYear(userId, projectId, month, year);

		for (TaskTrackApprovalFinal taskTrackApprovalFinalObj : appr2TimeTrackEntriesObj) {
			LinkedHashMap<String, Double> mapObj = new LinkedHashMap<>();
			if (taskTrackApprovalFinalObj.getProjectType().equalsIgnoreCase("Billable")) {
				mapObj = timeTrackApprover2EntriesMap.get(Constants.TASKTRACK_PROJECT_TYPE_BILLABLE);
			} else if (taskTrackApprovalFinalObj.getProjectType().equalsIgnoreCase("Non-Billable")) {
				mapObj = timeTrackApprover2EntriesMap.get(Constants.TASKTRACK_PROJECT_TYPE_NON_BILLABLE);
			} else if (taskTrackApprovalFinalObj.getProjectType().equalsIgnoreCase("Overtime")) {
				mapObj = timeTrackApprover2EntriesMap.get(Constants.TASKTRACK_PROJECT_TYPE_OVERTIME);
			} else if (taskTrackApprovalFinalObj.getProjectType().equalsIgnoreCase("Beach")) {
				mapObj = timeTrackApprover2EntriesMap.get(Constants.TASKTRACK_PROJECT_TYPE_BEACH);
			}
			for (String key : mapObj.keySet()) {
				String day = key.substring(8);
				switch (day) {
				case "01":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay1());
					break;
				case "02":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay2());
					break;
				case "03":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay3());
					break;
				case "04":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay4());
					break;
				case "05":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay5());
					break;
				case "06":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay6());
					break;
				case "07":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay7());
					break;
				case "08":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay8());
					break;
				case "09":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay9());
					break;
				case "10":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay10());
					break;
				case "11":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay11());
					break;
				case "12":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay12());
					break;
				case "13":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay13());
					break;
				case "14":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay14());
					break;
				case "15":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay15());
					break;
				case "16":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay16());
					break;
				case "17":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay17());
					break;
				case "18":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay18());
					break;
				case "19":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay19());
					break;
				case "20":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay20());
					break;
				case "21":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay21());
					break;
				case "22":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay22());
					break;
				case "23":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay23());
					break;
				case "24":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay24());
					break;
				case "25":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay25());
					break;
				case "26":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay26());
					break;
				case "27":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay27());
					break;
				case "28":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay28());
					break;
				case "29":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay29());
					break;
				case "30":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay30());
					break;
				case "31":
					mapObj.put(key, taskTrackApprovalFinalObj.getDay31());
					break;
				default:
					break;
				}
			}
		}
		return response;
	}

	public JSONObject getFreeAllocationReport(Long regionId_selected, Date fromDate, Date toDate) {
		SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
		List<FreeAllocationReportModel> list = null;
		JSONObject response = new JSONObject();
		try {
			list = projectReportsRepository.getFreeAllocationReportList(fromDate, toDate, regionId_selected);
			LinkedHashMap<Long, String> fullyAllocatedResources = new LinkedHashMap<Long, String>();
			LinkedHashMap<String, LinkedHashMap<String, JSONArray>> userMap = new LinkedHashMap<String, LinkedHashMap<String, JSONArray>>();
			JSONArray userData = new JSONArray();
			JSONArray projectList = new JSONArray();
			LinkedHashMap<String, JSONArray> dateObj = new LinkedHashMap<String, JSONArray>();
			boolean bench = false;
			for (FreeAllocationReportModel obj : list) {
				long userId = obj.getUserId();
				String resourceName = obj.getResourceName();
				String projectName = obj.getProjectName();
				projectList = new JSONArray();
				bench = false;
				// Stop iteration for users those who already allocated for whole month;
				if (fullyAllocatedResources.containsKey(userId))
					continue;
				Date alcStartDate = obj.getStartDate();
				Date alcEndDate = obj.getEndDate();
				Date joiningDate = obj.getJoiningDate();
				Date terminationDate = obj.getTerminationDate();
				// Add users to list those who already allocated for whole month
				if (alcStartDate != null
						&& (alcStartDate.before(fromDate) || alcStartDate.equals(frmt.parse(frmt.format(fromDate))))
						&& alcEndDate != null
						&& (alcEndDate.after(toDate) || alcEndDate.equals(frmt.parse(frmt.format(toDate))))) {
					fullyAllocatedResources.put(userId, resourceName);
					continue;
				}
				// Non allocated resource, those who does not have any project
				if (projectName.equals("") || alcStartDate == null || alcEndDate == null) {
					bench = true;
				}
				// Iterate over through dates
				Calendar calender = Calendar.getInstance();
				calender.setTime(fromDate);
				int month = (calender.get(Calendar.MONTH) + 1);
				int year = calender.get(Calendar.YEAR);
				calender.setTime(toDate);
				int lastDay = calender.get(Calendar.DATE);
				for (int day = 1; day <= lastDay; day++) {
					String date = year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day;
					Date loopDate = frmt.parse(date);
					if (joiningDate != null && (joiningDate.after(loopDate) || joiningDate.equals(loopDate))) {
						projectList = new JSONArray();
						projectList.add(null);
						dateObj.put(date, projectList);
					} else if (terminationDate != null && terminationDate.before(loopDate)) {
						projectList = new JSONArray();
						projectList.add(null);
						dateObj.put(date, projectList);
					} else if (bench) {
						projectList = new JSONArray();
						projectList.add("Free Allocation");
						dateObj.put(date, projectList);
					} else if ((loopDate.after(alcStartDate) || loopDate.equals(alcStartDate))
							&& (loopDate.before(alcEndDate) || loopDate.equals(alcEndDate))) {
						if (userMap.containsKey(resourceName)) {
							dateObj = userMap.get(resourceName);
							if (dateObj.containsKey(date)) {
								projectList = dateObj.get(date);
								if (!projectList.contains(projectName)) {
									projectList.remove("Free Allocation");
									projectList.add(projectName);
								}
							} else {
								projectList = new JSONArray();
								projectList.add(projectName);
								dateObj.put(date, projectList);
							}
						} else {
							projectList = new JSONArray();
							projectList.add(projectName);
							dateObj.put(date, projectList);
						}
					} else {
						if (userMap.containsKey(resourceName)) {
							dateObj = userMap.get(resourceName);
							if (dateObj.containsKey(date)) {
								projectList = dateObj.get(date);
								if (projectList.size() <= 0)
									projectList.add("Free Allocation");
							} else {
								projectList = new JSONArray();
								projectList.add(projectName);
								dateObj.put(date, projectList);
							}
						} else {
							projectList = new JSONArray();
							projectList.add("Free Allocation");
							dateObj.put(date, projectList);
						}
					}
				}
				userMap.put(resourceName, dateObj);
				dateObj = new LinkedHashMap<String, JSONArray>();
				userData.add(userMap);
			}
			JSONArray userDetails = new JSONArray();
			LinkedHashMap<String, Integer> freeResourceCount = new LinkedHashMap<String, Integer>();
			// Iterate through Map to find bench resource count
			projectList = new JSONArray();
			Integer count = 0;
			Map singleUser = new HashMap();
			for (Map.Entry<String, LinkedHashMap<String, JSONArray>> userEntry : userMap.entrySet()) {
				singleUser = new HashMap();
				singleUser.put("userName", userEntry.getKey());
				singleUser.put("dateList", userEntry.getValue());
				userDetails.add(singleUser);
				LinkedHashMap<String, JSONArray> dateObject = userEntry.getValue();
				for (Map.Entry<String, JSONArray> dateEntry : dateObject.entrySet()) {
					String date = dateEntry.getKey();
					projectList = dateEntry.getValue();
					if (projectList.contains("Free Allocation")) {
						if (freeResourceCount.get(date) != null) {
							count = freeResourceCount.get(date);
							count++;
							freeResourceCount.put(date, count);
						} else
							freeResourceCount.put(date, 1);
					}
				}
			}
			response.put("userInfo", userDetails);
			response.put("freeResourceCount", freeResourceCount);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return response;
	}

	/**
	 * @author sreejith.j
	 */
	@Override
	public List<HashMap<String, Object>> getRegionLeaves(Integer region, Integer month, Integer year) throws Exception {
		List<HashMap<String, Object>> leaveResponse = new ArrayList<HashMap<String, Object>>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar monthStartDate = Calendar.getInstance();
		monthStartDate.setTime(formatter.parse(year + "-" + month + "-01"));
		Calendar monthEndDate = Calendar.getInstance();
		monthEndDate
				.setTime(formatter.parse(year + "-" + month + "-" + monthStartDate.getActualMaximum(Calendar.DATE)));

		List<UserLeaveSummary> leaveSummaryList = userLeaveSummaryRepository
				.getUserLeaveListByMonthRegion(monthStartDate.getTime(), monthEndDate.getTime(), Long.valueOf(region));
		HashMap<Long, HashMap<String, Object>> userHashMap = new HashMap<Long, HashMap<String, Object>>();
		HashMap<String, Object> userObj = new HashMap<String, Object>();
		HashMap<Integer, Object> leavesObj = new HashMap<Integer, Object>();
		for (UserLeaveSummary obj : leaveSummaryList) {
			if (userHashMap.containsKey(obj.getUser().getEmpId())) {
				userObj = userHashMap.get(obj.getUser().getEmpId());
			} else {
				userObj = new HashMap<String, Object>();
				String name = obj.getUser().getLastName() + " " + obj.getUser().getFirstName();
				userObj.put("name", name.trim());
				userObj.put("empId", obj.getUser().getEmpId());
				userObj.put("totalLeaves", 0);
				leavesObj = new HashMap<Integer, Object>();
				userObj.put("leaves", leavesObj);
				userHashMap.put(obj.getUser().getEmpId(), userObj);
				leaveResponse.add(userHashMap.get(obj.getUser().getEmpId()));
			}
			leavesObj = (HashMap<Integer, Object>) userObj.get("leaves");
			Calendar leaveDate = Calendar.getInstance();
			leaveDate.setTime(obj.getLeaveDate());
			Double leaveAmt = obj.getLeaveType().equalsIgnoreCase(Constants.HALF_DAY_LEAVE) ? 0.5 : 1;
			leavesObj.put(leaveDate.get(Calendar.DATE), leaveAmt);
			userObj.put("totalLeaves", Double.parseDouble(userObj.get("totalLeaves").toString()) + leaveAmt);
		}
		return leaveResponse;
	}
}
