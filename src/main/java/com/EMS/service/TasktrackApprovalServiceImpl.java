package com.EMS.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ValueRange;
import java.util.*;
import java.time.YearMonth;


import com.EMS.repository.*;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.EMS.model.AllocationModel;
import com.EMS.model.ProjectModel;
import com.EMS.model.Task;
import com.EMS.model.TaskTrackApproval;
import com.EMS.model.TaskTrackApprovalFinance;
import com.EMS.model.TaskTrackApprovalLevel2;
import com.EMS.model.Tasktrack;
import com.EMS.model.UserModel;
import com.EMS.repository.TaskTrackFinanceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class TasktrackApprovalServiceImpl implements TasktrackApprovalService {

	@Autowired
	TasktrackRepository tasktrackRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	@Autowired
	TimeTrackApprovalRepository timeTrackApprovalRepository;

	@Autowired
	TimeTrackApprovalJPARepository timeTrackApprovalJPARepository;
//	For Task track Model

	@Autowired
	TaskTrackApprovalLevel2Repository timeTrackApprovalLevel2;

	@Autowired
	TaskTrackFinanceRepository taskTrackFinanceRepository;

	@Autowired
	TasktrackApprovalService tasktrackApprovalService;

	@Autowired
	ProjectService projectService;

	@Autowired
	ProjectAllocationRepository projectAllocationRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	private ObjectMapper objectMapper;


	@Override
	public Boolean checkIsUserExists(Long id) {
		Boolean exist = tasktrackRepository.existsByUser(id);
		return exist;
	}

	@Override
	public List<JSONObject> getTimeTrackUserTaskDetails(Long id, Date startDate, Date endDate, List<Object[]> userList,

														List<JSONObject> loggedJsonArray,List<JSONObject> billableJsonArrayLogged,List<JSONObject> timeTrackJSONData, Boolean isExist,Long projectId) {
		List<JSONObject> billableJsonArray;
		List<JSONObject> overTimeArray;
		if (isExist) {
			JSONObject userListObject = new JSONObject();

			userList =getUserListByProject(id, startDate, endDate,projectId);
			//System.out.println("userList  : "+userList);
			loggedJsonArray = new ArrayList<>();

			String name = null;
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0,intday = 0;
			for (int i = 0; i < diffInDays; i++) {
				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DAY_OF_MONTH);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				Double hours = 0.0;
				if (userList != null && userList.size() > 0) {
					JSONObject jsonObject = new JSONObject();
					for (Object[] item : userList) {

						String st = String.valueOf(item[3]);

						if (st.equals(vl)) {
							hours = hours + (Double) item[2];

						}
						name = (String) item[0] + " " + item[1];
					}
					jsonObject.put(vl, hours);
					cal.add(Calendar.DATE, 1);
					loggedJsonArray.add(jsonObject);

				}

				else {
					JSONObject jsonObject = new JSONObject();
					String uName = userService.getUserName(id);
					name = String.valueOf(uName).replace(",", " ");
					jsonObject.put(vl, 0);
					cal.add(Calendar.DATE, 1);
					loggedJsonArray.add(jsonObject);
				}

			}
			userListObject.put("userName", name);
			userListObject.put("userId", id);
			userListObject.put("month", intMonth);
			userListObject.put("logged", loggedJsonArray);
			//System.out.println("logged has data  : "+loggedJsonArray);
			name = null;
			cal.setTime(startDate);
			int monthIndex = (cal.get(Calendar.MONTH) + 1);
			int yearIndex = cal.get(Calendar.YEAR);

			List<TaskTrackApproval> approvalUserList =getUserListForApproval(id,projectId,monthIndex,yearIndex);
			billableJsonArray = new ArrayList<>();
			billableJsonArrayLogged=new ArrayList<>();
			overTimeArray=new ArrayList<>();


			diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			intMonth = 0;
			intday = 0;
			Double hours = 0.0;
			if (approvalUserList != null && approvalUserList.size() > 0) {
				JSONObject jsonObject = new JSONObject();


				for (TaskTrackApproval item : approvalUserList) {
					cal.setTime(startDate);



					for (int i = 0; i < diffInDays; i++)
					{

						intMonth = (cal.get(Calendar.MONTH) + 1);
						intday = cal.get(Calendar.DAY_OF_MONTH);
						String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if(i==0)
							hours=(Double)item.getDay1();
						else if(i==1)
							hours=(Double)item.getDay2();
						else if(i==2)
							hours=(Double)item.getDay3();
						else if(i==3)
							hours=(Double)item.getDay4();
						else if(i==4)
							hours=(Double)item.getDay5();
						else if(i==5)
							hours=(Double)item.getDay6();
						else if(i==6)
							hours=(Double)item.getDay7();
						else if(i==7)
							hours=(Double)item.getDay8();
						else if(i==8)
							hours=(Double)item.getDay9();
						else if(i==9)
							hours=(Double)item.getDay10();
						else if(i==10)
							hours=(Double)item.getDay11();
						else if(i==11)
							hours=(Double)item.getDay12();
						else if(i==12)
							hours=(Double)item.getDay13();
						else if(i==13)
							hours=(Double)item.getDay14();
						else if(i==14)
							hours=(Double)item.getDay15();
						else if(i==15)
							hours=(Double)item.getDay16();
						else if(i==16)
							hours=(Double)item.getDay17();
						else if(i==17)
							hours=(Double)item.getDay18();
						else if(i==18)
							hours=(Double)item.getDay19();
						else if(i==19)
							hours=(Double)item.getDay20();
						else if(i==20)
							hours=(Double)item.getDay21();
						else if(i==21)
							hours=(Double)item.getDay22();
						else if(i==22)
							hours=(Double)item.getDay23();
						else if(i==23)
							hours=(Double)item.getDay24();
						else if(i==24)
							hours=(Double)item.getDay25();
						else if(i==25)
							hours=(Double)item.getDay26();
						else if(i==26)
							hours=(Double)item.getDay27();
						else if(i==27)
							hours=(Double)item.getDay28();
						else if(i==28)
							hours=(Double)item.getDay29();
						else if(i==29)
							hours=(Double)item.getDay30();
						else if(i==30)
							hours=(Double)item.getDay31();

						name = (String) item.getFirstName() + " " + item.getLastName();

						if(item.getProjectType().equals("Billable")) {
							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							billableJsonArrayLogged.add(jsonObject);
						}

						if(item.getProjectType().equals("Overtime"))
						{

							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							overTimeArray.add(jsonObject);


						}

						cal.add(Calendar.DATE, 1);

					}


					/*-------------------------*/

					if(!overTimeArray.isEmpty() && !billableJsonArrayLogged.isEmpty())
					{
						cal.setTime(startDate);
						for (int i = 0; i < diffInDays; i++)
						{
							JSONObject jsonOverTime=new JSONObject();
							JSONObject jsonBillable=new JSONObject();
							JSONObject jsonTotal=new JSONObject();

							Double billable=0.0;
							Double overTime=0.0;


							intMonth = (cal.get(Calendar.MONTH)+1);
							intday = cal.get(Calendar.DAY_OF_MONTH);
							String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
									+ ((intday < 10) ? "0" + intday : "" + intday);

							jsonBillable = billableJsonArrayLogged.get(i);
							if(jsonBillable.get(vl)!=null)
							{
								billable=(Double)jsonBillable.get(vl);
							}


							jsonOverTime = overTimeArray.get(i);
							if(jsonOverTime.get(vl)!=null)
							{
								overTime = (Double) jsonOverTime.get(vl);
							}

							Double totalTime = billable + overTime;
							jsonTotal.put(vl, totalTime);
							billableJsonArray.add(jsonTotal);

							cal.add(Calendar.DATE, 1);
						}
					}
					/*-------------------------*/

				}




			}
			else {
				cal.setTime(startDate);
				for (int i = 0; i < diffInDays; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DAY_OF_MONTH);
					String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					JSONObject jsonObject = new JSONObject();
					jsonObject.put(vl, 0);
					billableJsonArray.add(jsonObject);

					cal.add(Calendar.DATE, 1);

				}
			}
			userListObject.put("billable", billableJsonArray);



			timeTrackJSONData.add(userListObject);

		} else {
			loggedJsonArray = new ArrayList<>();
			billableJsonArray = new ArrayList<>();
			JSONObject userListObject = new JSONObject();

			String uName = userService.getUserName(id);
			String name = String.valueOf(uName).replace(",", " ");

			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0,intday = 0;
			for (int i = 0; i < diffInDays; i++) {
				JSONObject jsonObject = new JSONObject();

				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DAY_OF_MONTH);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				jsonObject.put(vl, 0);
				cal.add(Calendar.DATE, 1);
				loggedJsonArray.add(jsonObject);
				billableJsonArray.add(jsonObject);
			}
			userListObject.put("userName", name);
			userListObject.put("userId", id);
			userListObject.put("month", intMonth);
			userListObject.put("logged", loggedJsonArray);
			userListObject.put("billable", billableJsonArray);
			//System.out.println("logged is empty  : "+loggedJsonArray);
			timeTrackJSONData.add(userListObject);
		}
		return timeTrackJSONData;
	}


	@Override
	public List<JSONObject> getTimeTrackUserTaskDetailsByProject(Long id, Date startDate, Date endDate, List<Object[]> userList,
																 List<JSONObject> loggedJsonArray,List<JSONObject> billableJsonArray,List<JSONObject> nonBillableJsonArray,List<JSONObject> timeTrackJSONData, Boolean isExist,Long projectId) {
		if (isExist) {
			JSONObject userListObject = new JSONObject();

			userList =getUserListByProject(id, startDate, endDate,projectId);

			loggedJsonArray = new ArrayList<>();

			String name = null;
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0,intday = 0;
			for (int i = 0; i < diffInDays; i++) {
				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DAY_OF_MONTH);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				Double hours = 0.0;
				if (userList != null && userList.size() > 0) {
					JSONObject jsonObject = new JSONObject();
					for (Object[] item : userList) {

						String st = String.valueOf(item[3]);

						if (st.equals(vl)) {
							hours = hours + (Double) item[2];

						}
						name = (String) item[0] + " " + item[1];
					}
					jsonObject.put(vl, hours);
					cal.add(Calendar.DATE, 1);
					loggedJsonArray.add(jsonObject);

				}

				else {
					JSONObject jsonObject = new JSONObject();
					String uName = userService.getUserName(id);
					name = String.valueOf(uName).replace(",", " ");
					jsonObject.put(vl, 0);
					cal.add(Calendar.DATE, 1);
					loggedJsonArray.add(jsonObject);
				}

			}
			userListObject.put("userName", name);
			userListObject.put("userId", id);
			userListObject.put("month", intMonth);
			userListObject.put("logged", loggedJsonArray);

			name = null;
			cal.setTime(startDate);
			int monthIndex = (cal.get(Calendar.MONTH) + 1);
			int yearIndex = cal.get(Calendar.YEAR);

			List<TaskTrackApproval> approvalUserList =getUserListForApproval(id,projectId,monthIndex,yearIndex);
			billableJsonArray = new ArrayList<>();
			nonBillableJsonArray = new ArrayList<>();


			diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			intMonth = 0;
			intday = 0;
			Double hours = 0.0;
			if (approvalUserList != null && approvalUserList.size() > 0) {
				JSONObject jsonObject = new JSONObject();

				for (TaskTrackApproval item : approvalUserList) {
					cal.setTime(startDate);

					for (int i = 0; i < diffInDays; i++) {

						intMonth = (cal.get(Calendar.MONTH) + 1);
						intday = cal.get(Calendar.DAY_OF_MONTH);
						String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if(i==0)
							hours=(Double)item.getDay1();
						else if(i==1)
							hours=(Double)item.getDay2();
						else if(i==2)
							hours=(Double)item.getDay3();
						else if(i==3)
							hours=(Double)item.getDay4();
						else if(i==4)
							hours=(Double)item.getDay5();
						else if(i==5)
							hours=(Double)item.getDay6();
						else if(i==6)
							hours=(Double)item.getDay7();
						else if(i==7)
							hours=(Double)item.getDay8();
						else if(i==8)
							hours=(Double)item.getDay9();
						else if(i==9)
							hours=(Double)item.getDay10();
						else if(i==10)
							hours=(Double)item.getDay11();
						else if(i==11)
							hours=(Double)item.getDay12();
						else if(i==12)
							hours=(Double)item.getDay13();
						else if(i==13)
							hours=(Double)item.getDay14();
						else if(i==14)
							hours=(Double)item.getDay15();
						else if(i==15)
							hours=(Double)item.getDay16();
						else if(i==16)
							hours=(Double)item.getDay17();
						else if(i==17)
							hours=(Double)item.getDay18();
						else if(i==18)
							hours=(Double)item.getDay19();
						else if(i==19)
							hours=(Double)item.getDay20();
						else if(i==20)
							hours=(Double)item.getDay21();
						else if(i==21)
							hours=(Double)item.getDay22();
						else if(i==22)
							hours=(Double)item.getDay23();
						else if(i==23)
							hours=(Double)item.getDay24();
						else if(i==24)
							hours=(Double)item.getDay25();
						else if(i==25)
							hours=(Double)item.getDay26();
						else if(i==26)
							hours=(Double)item.getDay27();
						else if(i==27)
							hours=(Double)item.getDay28();
						else if(i==28)
							hours=(Double)item.getDay29();
						else if(i==29)
							hours=(Double)item.getDay30();
						else if(i==30)
							hours=(Double)item.getDay31();

						name = (String) item.getFirstName() + " " + item.getLastName();

						if(item.getProjectType().equals("Billable")) {
							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							billableJsonArray.add(jsonObject);
						}
						if(item.getProjectType().equals("Non-Billable")) {
							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							nonBillableJsonArray.add(jsonObject);
						}

						cal.add(Calendar.DATE, 1);

					}
				}
			}
			else {
				cal.setTime(startDate);
				for (int i = 0; i < diffInDays; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DAY_OF_MONTH);
					String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					JSONObject jsonObject = new JSONObject();
					jsonObject.put(vl, 0);
					billableJsonArray.add(jsonObject);
					nonBillableJsonArray.add(jsonObject);

					cal.add(Calendar.DATE, 1);

				}
			}
			userListObject.put("billable", billableJsonArray);
			userListObject.put("nonbillable", nonBillableJsonArray);



			timeTrackJSONData.add(userListObject);

		} else {
			loggedJsonArray = new ArrayList<>();
			billableJsonArray = new ArrayList<>();
			JSONObject userListObject = new JSONObject();

			String uName = userService.getUserName(id);
			String name = String.valueOf(uName).replace(",", " ");

			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0,intday = 0;
			for (int i = 0; i < diffInDays; i++) {
				JSONObject jsonObject = new JSONObject();

				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DAY_OF_MONTH);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				jsonObject.put(vl, 0);
				cal.add(Calendar.DATE, 1);
				loggedJsonArray.add(jsonObject);
				billableJsonArray.add(jsonObject);
				nonBillableJsonArray.add(jsonObject);
			}
			userListObject.put("userName", name);
			userListObject.put("userId", id);
			userListObject.put("month", intMonth);
			userListObject.put("logged", loggedJsonArray);
			userListObject.put("billable", billableJsonArray);
			userListObject.put("nonbillable", nonBillableJsonArray);

			timeTrackJSONData.add(userListObject);
		}
		return timeTrackJSONData;
	}

	@Override
	public List<JSONObject> getTimeTrackUserProjectTaskDetails(Long projectId,String projectName, Date startDate, Date endDate, List<Object[]> projectList,
															   List<JSONObject> loggedJsonArray,List<JSONObject> billableJsonArray,List<JSONObject> nonBillableJsonArray,List<JSONObject> timeTrackJSONData, Boolean isExist,Long userId) {
		if (isExist) {

			JSONObject userListObject = new JSONObject();

			projectList =getUserListByProject(userId, startDate, endDate,projectId);

			loggedJsonArray = new ArrayList<>();

			String name = null;
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0,intday = 0;
			for (int i = 0; i < diffInDays; i++) {
				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DAY_OF_MONTH);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				Double hours = 0.0;
				if (projectList != null && projectList.size() > 0) {
					JSONObject jsonObject = new JSONObject();
					for (Object[] item : projectList) {

						String st = String.valueOf(item[3]);

						if (st.equals(vl)) {
							hours = hours + (Double) item[2];

						}
						name = (String) item[0] + " " + item[1];
					}
					jsonObject.put(vl, hours);
					cal.add(Calendar.DATE, 1);
					loggedJsonArray.add(jsonObject);

				}

				else {
					JSONObject jsonObject = new JSONObject();
					String uName = userService.getUserName(userId);
					name = String.valueOf(uName).replace(",", " ");
					jsonObject.put(vl, 0);
					cal.add(Calendar.DATE, 1);
					loggedJsonArray.add(jsonObject);
				}

			}
			if(projectName !=null) {
				userListObject.put("projectName", projectName);
			}
			else {
				userListObject.put("userName", name);
			}
			userListObject.put("userId", userId);
			userListObject.put("month", intMonth);
			userListObject.put("logged", loggedJsonArray);

			name = null;
			cal.setTime(startDate);
			int monthIndex = (cal.get(Calendar.MONTH) + 1);
			int yearIndex = cal.get(Calendar.YEAR);

			List<TaskTrackApproval> approvalUserList =getUserListForApproval(userId,projectId,monthIndex,yearIndex);
			billableJsonArray = new ArrayList<>();
			nonBillableJsonArray = new ArrayList<>();


			diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			intMonth = 0;
			intday = 0;
			Double hours = 0.0;
			if (approvalUserList != null && approvalUserList.size() > 0) {
				JSONObject jsonObject = new JSONObject();

				for (TaskTrackApproval item : approvalUserList) {
					cal.setTime(startDate);

					for (int i = 0; i < diffInDays; i++) {

						intMonth = (cal.get(Calendar.MONTH) + 1);
						intday = cal.get(Calendar.DAY_OF_MONTH);
						String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if(i==0)
							hours=(Double)item.getDay1();
						else if(i==1)
							hours=(Double)item.getDay2();
						else if(i==2)
							hours=(Double)item.getDay3();
						else if(i==3)
							hours=(Double)item.getDay4();
						else if(i==4)
							hours=(Double)item.getDay5();
						else if(i==5)
							hours=(Double)item.getDay6();
						else if(i==6)
							hours=(Double)item.getDay7();
						else if(i==7)
							hours=(Double)item.getDay8();
						else if(i==8)
							hours=(Double)item.getDay9();
						else if(i==9)
							hours=(Double)item.getDay10();
						else if(i==10)
							hours=(Double)item.getDay11();
						else if(i==11)
							hours=(Double)item.getDay12();
						else if(i==12)
							hours=(Double)item.getDay13();
						else if(i==13)
							hours=(Double)item.getDay14();
						else if(i==14)
							hours=(Double)item.getDay15();
						else if(i==15)
							hours=(Double)item.getDay16();
						else if(i==16)
							hours=(Double)item.getDay17();
						else if(i==17)
							hours=(Double)item.getDay18();
						else if(i==18)
							hours=(Double)item.getDay19();
						else if(i==19)
							hours=(Double)item.getDay20();
						else if(i==20)
							hours=(Double)item.getDay21();
						else if(i==21)
							hours=(Double)item.getDay22();
						else if(i==22)
							hours=(Double)item.getDay23();
						else if(i==23)
							hours=(Double)item.getDay24();
						else if(i==24)
							hours=(Double)item.getDay25();
						else if(i==25)
							hours=(Double)item.getDay26();
						else if(i==26)
							hours=(Double)item.getDay27();
						else if(i==27)
							hours=(Double)item.getDay28();
						else if(i==28)
							hours=(Double)item.getDay29();
						else if(i==29)
							hours=(Double)item.getDay30();
						else if(i==30)
							hours=(Double)item.getDay31();

						name = (String) item.getFirstName() + " " + item.getLastName();

						if(item.getProjectType().equals("Billable")) {
							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							billableJsonArray.add(jsonObject);
						}
						if(item.getProjectType().equals("Non-Billable")) {
							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							nonBillableJsonArray.add(jsonObject);
						}
						cal.add(Calendar.DATE, 1);

					}
				}
			}
			else {
				cal.setTime(startDate);
				for (int i = 0; i < diffInDays; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DAY_OF_MONTH);
					String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					JSONObject jsonObject = new JSONObject();
					jsonObject.put(vl, 0);
					billableJsonArray.add(jsonObject);
					nonBillableJsonArray.add(jsonObject);
					cal.add(Calendar.DATE, 1);

				}
			}
			userListObject.put("billable", billableJsonArray);
			userListObject.put("nonbillable", nonBillableJsonArray);



			timeTrackJSONData.add(userListObject);

		} else {

			loggedJsonArray = new ArrayList<>();
			billableJsonArray = new ArrayList<>();
			JSONObject userListObject = new JSONObject();

			String uName = userService.getUserName(userId);
			String name = String.valueOf(uName).replace(",", " ");

			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0,intday = 0;
			for (int i = 0; i < diffInDays; i++) {
				JSONObject jsonObject = new JSONObject();

				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DAY_OF_MONTH);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				jsonObject.put(vl, 0);
				cal.add(Calendar.DATE, 1);
				loggedJsonArray.add(jsonObject);
				billableJsonArray.add(jsonObject);
				nonBillableJsonArray.add(jsonObject);
			}
			userListObject.put("userName", name);
			userListObject.put("userId", userId);
			userListObject.put("month", intMonth);
			userListObject.put("logged", loggedJsonArray);
			userListObject.put("billable", billableJsonArray);
			userListObject.put("nonbillable", nonBillableJsonArray);

			timeTrackJSONData.add(userListObject);
		}
		return timeTrackJSONData;
	}

	private List<Object[]> getUserListByProject(Long id, Date startDate, Date endDate, Long projectId) {
		List<Object[]> userTaskList = taskRepository.getUserListByProject(id,startDate,endDate,projectId);
		return userTaskList;
	}

	@Override
	public JSONObject getApprovedUserTaskDetails(Long id, Date startDate, Date endDate, List<TaskTrackApproval> userList,
												 List<JSONObject> jsonArray, List<JSONObject> jsonDataRes1, Boolean isExist,Long projectId) {
		JSONObject userListObject = new JSONObject();

		List<JSONObject> billableArray = new ArrayList<>();
		List<JSONObject> overTimeArray = new ArrayList<>();
		List<JSONObject> nonbillableArray = new ArrayList<>();
		List<JSONObject> beachArray = new ArrayList<>();

		if (isExist) {

			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int monthIndex = (cal.get(Calendar.MONTH) + 1);
			int yearIndex = cal.get(Calendar.YEAR);
			userList =getUserListForApproval(id,projectId,monthIndex,yearIndex);

			jsonArray = new ArrayList<>();

			String name = null;
			Long billableId = null,nonBillableId=null,beachId=null,overtimeId=null,updatedBy=null;

			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0,intday = 0;

			Double hours = 0.0;
			if (userList != null && userList.size() > 0) {
				JSONObject jsonObject = new JSONObject();

				for (TaskTrackApproval item : userList) {
					cal.setTime(startDate);

					for (int i = 0; i < diffInDays; i++) {

						intMonth = (cal.get(Calendar.MONTH) + 1);
						intday = cal.get(Calendar.DAY_OF_MONTH);
						String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if(i==0)
							hours=(Double)item.getDay1();
						else if(i==1)
							hours=(Double)item.getDay2();
						else if(i==2)
							hours=(Double)item.getDay3();
						else if(i==3)
							hours=(Double)item.getDay4();
						else if(i==4)
							hours=(Double)item.getDay5();
						else if(i==5)
							hours=(Double)item.getDay6();
						else if(i==6)
							hours=(Double)item.getDay7();
						else if(i==7)
							hours=(Double)item.getDay8();
						else if(i==8)
							hours=(Double)item.getDay9();
						else if(i==9)
							hours=(Double)item.getDay10();
						else if(i==10)
							hours=(Double)item.getDay11();
						else if(i==11)
							hours=(Double)item.getDay12();
						else if(i==12)
							hours=(Double)item.getDay13();
						else if(i==13)
							hours=(Double)item.getDay14();
						else if(i==14)
							hours=(Double)item.getDay15();
						else if(i==15)
							hours=(Double)item.getDay16();
						else if(i==16)
							hours=(Double)item.getDay17();
						else if(i==17)
							hours=(Double)item.getDay18();
						else if(i==18)
							hours=(Double)item.getDay19();
						else if(i==19)
							hours=(Double)item.getDay20();
						else if(i==20)
							hours=(Double)item.getDay21();
						else if(i==21)
							hours=(Double)item.getDay22();
						else if(i==22)
							hours=(Double)item.getDay23();
						else if(i==23)
							hours=(Double)item.getDay24();
						else if(i==24)
							hours=(Double)item.getDay25();
						else if(i==25)
							hours=(Double)item.getDay26();
						else if(i==26)
							hours=(Double)item.getDay27();
						else if(i==27)
							hours=(Double)item.getDay28();
						else if(i==28)
							hours=(Double)item.getDay29();
						else if(i==29)
							hours=(Double)item.getDay30();
						else if(i==30)
							hours=(Double)item.getDay31();

						name = (String) item.getFirstName() + " " + item.getLastName();
						updatedBy = item.getUpdatedBy();

						if(item.getProjectType().equals("Billable")) {
							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							billableArray.add(jsonObject);
							billableId = item.getId();
						}
						else if(item.getProjectType().equals("Non-Billable")) {
							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							nonbillableArray.add(jsonObject);
							nonBillableId = item.getId();
						}
						else if(item.getProjectType().equals("Beach")) {
							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							beachArray.add(jsonObject);
							beachId = item.getId();
						}
						else if(item.getProjectType().equals("Overtime")) {
							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							overTimeArray.add(jsonObject);
							overtimeId = item.getId();
						}
						cal.add(Calendar.DATE, 1);

					}
				}
			}
			else {
				cal.setTime(startDate);
				for (int i = 0; i < diffInDays; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DAY_OF_MONTH);
					String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					JSONObject jsonObject = new JSONObject();
					jsonObject.put(vl, 0);
					billableArray.add(jsonObject);
					nonbillableArray.add(jsonObject);
					beachArray.add(jsonObject);
					overTimeArray.add(jsonObject);

					cal.add(Calendar.DATE, 1);

				}
			}

			userListObject.put("userName", name);
			userListObject.put("userId", id);
			userListObject.put("month", intMonth);
			userListObject.put("billable", billableArray);;
			userListObject.put("nonBillable", nonbillableArray);
			userListObject.put("beach", beachArray);
			userListObject.put("overtime", overTimeArray);
			userListObject.put("billableId", billableId);
			userListObject.put("nonBillableId", nonBillableId);
			userListObject.put("beachId", beachId);
			userListObject.put("overtimeId", overtimeId);
			userListObject.put("updatedBy", updatedBy);
			jsonDataRes1.add(userListObject);

		}
		else {

			String uName = userService.getUserName(id);
			String name = String.valueOf(uName).replace(",", " ");

			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0,intday = 0;
			for (int i = 0; i < diffInDays; i++) {
				JSONObject jsonObject = new JSONObject();

				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DAY_OF_MONTH);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				jsonObject.put(vl, 0);
				cal.add(Calendar.DATE, 1);
				billableArray.add(jsonObject);
				nonbillableArray.add(jsonObject);
				beachArray.add(jsonObject);
				overTimeArray.add(jsonObject);
			}
			userListObject.put("userName", name);
			userListObject.put("userId", id);
			userListObject.put("month", intMonth);
			userListObject.put("billable", billableArray);;
			userListObject.put("nonBillable", nonbillableArray);
			userListObject.put("beach", beachArray);
			userListObject.put("overtime", overTimeArray);
			userListObject.put("billableId", null);
			userListObject.put("nonBillableId", null);
			userListObject.put("beachId", null);
			userListObject.put("overtimeId", null);
			userListObject.put("updatedBy", null);

		}
		return userListObject;
	}
	public List<TaskTrackApproval> getUserListForApproval(Long id,Long projectId,Integer monthIndex,Integer yearIndex) {

		List<TaskTrackApproval> userList = timeTrackApprovalRepository.getUserListForApproval(id,projectId,monthIndex,yearIndex);

		return userList;
	}

	/*
	 * public List<TaskTrackApprovalLevel2> getUserListForApprovalLevel2(Long
	 * id,Long projectId,Integer monthIndex,Integer yearIndex) {
	 *
	 * List<TaskTrackApprovalLevel2> userList =
	 * timeTrackApprovalRepository.getUserListForApprovalLevel2(id,projectId,
	 * monthIndex,yearIndex);
	 *
	 * return userList; }
	 */
	@Override
	public TaskTrackApproval findById(Long billableId) {
		TaskTrackApproval taskTrackApproval = timeTrackApprovalJPARepository.getOne(billableId);
		return taskTrackApproval;
	}

	@Override
	public TaskTrackApproval updateData(TaskTrackApproval taskTrackApproval) {
		return timeTrackApprovalJPARepository.save(taskTrackApproval);

	}

	@Override
	public TaskTrackApproval save(TaskTrackApproval taskTrackApproval) {
		return timeTrackApprovalJPARepository.save(taskTrackApproval);
	}

	@Override
	public TaskTrackApprovalLevel2 findById2(Long billableId) {
		// TODO Auto-generated method stub
		TaskTrackApprovalLevel2 taskTrackApproval = timeTrackApprovalLevel2.getOne(billableId);
		return taskTrackApproval;
	}

	@Override
	public TaskTrackApprovalLevel2 updateDatas(TaskTrackApprovalLevel2 taskTrackApproval) {
		// TODO Auto-generated method stub
		return timeTrackApprovalLevel2.save(taskTrackApproval);
	}

	@Override
	public TaskTrackApprovalLevel2 saveLevel2(TaskTrackApprovalLevel2 taskTrackApproval) {
		// TODO Auto-generated method stub
		return timeTrackApprovalLevel2.save(taskTrackApproval);
	}

	@Override
	public JSONObject getApproveddatalevel2(Long userId, Date startDate, Date endDate, List<TaskTrackApproval> userList,
											List<JSONObject> jsonArray, List<JSONObject> approvalJSONData, Boolean isExist, Long projectId) {

		List<JSONObject> billableArray = new ArrayList<>();
		List<JSONObject> overTimeArray = new ArrayList<>();
		List<JSONObject> nonbillableArray = new ArrayList<>();
		List<JSONObject> beachArray = new ArrayList<>();
		JSONObject userListObject = new JSONObject();
		List<JSONObject> jsonDataRes1 = new ArrayList<JSONObject>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		int monthIndex = (cal.get(Calendar.MONTH) + 1);
		int yearIndex = cal.get(Calendar.YEAR);
		List<TaskTrackApprovalLevel2> approvedData = timeTrackApprovalLevel2.getApprovedData(userId,monthIndex,yearIndex,projectId);
		jsonArray = new ArrayList<>();

		String name = null;
		Long billableId = null,nonBillableId=null,beachId=null,overtimeId=null,updatedBy=null;

		int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
		int intMonth = 0,intday = 0;

		Double hours = 0.0;
		if (approvedData != null && approvedData.size() > 0) {
			JSONObject jsonObject = new JSONObject();

			for (TaskTrackApprovalLevel2 item : approvedData) {
				cal.setTime(startDate);

				for (int i = 0; i < diffInDays; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DAY_OF_MONTH);
					String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					if(i==0)
						hours=(Double)item.getDay1();
					else if(i==1)
						hours=(Double)item.getDay2();
					else if(i==2)
						hours=(Double)item.getDay3();
					else if(i==3)
						hours=(Double)item.getDay4();
					else if(i==4)
						hours=(Double)item.getDay5();
					else if(i==5)
						hours=(Double)item.getDay6();
					else if(i==6)
						hours=(Double)item.getDay7();
					else if(i==7)
						hours=(Double)item.getDay8();
					else if(i==8)
						hours=(Double)item.getDay9();
					else if(i==9)
						hours=(Double)item.getDay10();
					else if(i==10)
						hours=(Double)item.getDay11();
					else if(i==11)
						hours=(Double)item.getDay12();
					else if(i==12)
						hours=(Double)item.getDay13();
					else if(i==13)
						hours=(Double)item.getDay14();
					else if(i==14)
						hours=(Double)item.getDay15();
					else if(i==15)
						hours=(Double)item.getDay16();
					else if(i==16)
						hours=(Double)item.getDay17();
					else if(i==17)
						hours=(Double)item.getDay18();
					else if(i==18)
						hours=(Double)item.getDay19();
					else if(i==19)
						hours=(Double)item.getDay20();
					else if(i==20)
						hours=(Double)item.getDay21();
					else if(i==21)
						hours=(Double)item.getDay22();
					else if(i==22)
						hours=(Double)item.getDay23();
					else if(i==23)
						hours=(Double)item.getDay24();
					else if(i==24)
						hours=(Double)item.getDay25();
					else if(i==25)
						hours=(Double)item.getDay26();
					else if(i==26)
						hours=(Double)item.getDay27();
					else if(i==27)
						hours=(Double)item.getDay28();
					else if(i==28)
						hours=(Double)item.getDay29();
					else if(i==29)
						hours=(Double)item.getDay30();
					else if(i==30)
						hours=(Double)item.getDay31();

					name = (String) item.getUser().getFirstName() + " " + item.getUser().getLastName();
					updatedBy = item.getUpdatedBy();

					if(item.getProjectType().equals("Billable")) {
						jsonObject = new JSONObject();
						jsonObject.put(vl, hours);
						billableArray.add(jsonObject);
						billableId = item.getId();
					}
					else if(item.getProjectType().equals("Non-Billable")) {
						jsonObject = new JSONObject();
						jsonObject.put(vl, hours);
						nonbillableArray.add(jsonObject);
						nonBillableId = item.getId();
					}
					else if(item.getProjectType().equals("Beach")) {
						jsonObject = new JSONObject();
						jsonObject.put(vl, hours);
						beachArray.add(jsonObject);
						beachId = item.getId();
					}
					else if(item.getProjectType().equals("Overtime")) {
						jsonObject = new JSONObject();
						jsonObject.put(vl, hours);
						overTimeArray.add(jsonObject);
						overtimeId = item.getId();
					}
					cal.add(Calendar.DATE, 1);

				}
			}
		}
		else {
			cal.setTime(startDate);
			for (int i = 0; i < diffInDays; i++) {

				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DAY_OF_MONTH);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				JSONObject jsonObject = new JSONObject();
				jsonObject.put(vl, 0);
				billableArray.add(jsonObject);
				nonbillableArray.add(jsonObject);
				beachArray.add(jsonObject);
				overTimeArray.add(jsonObject);

				cal.add(Calendar.DATE, 1);

			}
		}

		userListObject.put("userName", name);
		userListObject.put("userId", userId);
		userListObject.put("month", intMonth);
		userListObject.put("billable", billableArray);;
		userListObject.put("nonBillable", nonbillableArray);
		userListObject.put("beach", beachArray);
		userListObject.put("overtime", overTimeArray);
		userListObject.put("billableId", billableId);
		userListObject.put("nonBillableId", nonBillableId);
		userListObject.put("beachId", beachId);
		userListObject.put("overtimeId", overtimeId);
		userListObject.put("updatedBy", updatedBy);
		jsonDataRes1.add(userListObject);



		return userListObject;
		//return null;
	}

	@Override
	public void saveLevel3(TaskTrackApprovalFinance taskTrackApproval) {
		// TODO Auto-generated method stub
		taskTrackFinanceRepository.save(taskTrackApproval);
	}

	@Override
	public JSONObject getApproveddatalevel2toFinance(Long userId,Long logUser, int monthIndex, int yearIndex, Long projectId) {
		// TODO Auto-generated method stub

		int flagExist = 0;
		JSONObject userListObject = new JSONObject();
		JSONObject testValidation = new JSONObject();

		testValidation = checkPreviousTimeSheetsareClosed(monthIndex, yearIndex, projectId, logUser);
		String status = null;
		if((boolean) testValidation.get("data")) {

			List<TaskTrackApprovalLevel2> approvedData = timeTrackApprovalLevel2.getApprovedData(userId,monthIndex,yearIndex,projectId);

			List<TaskTrackApprovalFinance> data = taskTrackFinanceRepository.getDatas(userId,monthIndex,yearIndex,projectId);
			if(!data.isEmpty()) {

				flagExist = 1;

			}

			YearMonth yearMonthObject = YearMonth.of(yearIndex, monthIndex);
			int totaldays = yearMonthObject.lengthOfMonth();
			Date fdate = new Date();
			if (approvedData != null && approvedData.size() > 0)
			{

				if(flagExist == 0)
				{
					status="HM";
					for (TaskTrackApprovalLevel2 item : approvedData) {

						TaskTrackApprovalFinance finance = new TaskTrackApprovalFinance();
						TaskTrackApprovalLevel2 level2 = tasktrackApprovalService.findById2(item.getId());
						level2.setForwarded_date(fdate);
						//level2.setStatus("");
						UserModel user = userService.getUserDetailsById(userId);
						ProjectModel project = projectService.getProjectId(projectId);
						finance.setProject(project);
						finance.setProjectType(item.getProjectType());
						finance.setApprover_level2(level2);
						finance.setStatus(status);
						finance.setMonth(monthIndex);
						finance.setYear(yearIndex);
						finance.setDay1(item.getDay1());
						finance.setDay2(item.getDay2());
						finance.setDay3(item.getDay3());
						finance.setDay4(item.getDay4());
						finance.setDay5(item.getDay5());
						finance.setDay6(item.getDay6());
						finance.setDay7(item.getDay7());
						finance.setDay8(item.getDay8());
						finance.setDay9(item.getDay9());
						finance.setDay10(item.getDay10());
						finance.setDay11(item.getDay11());
						finance.setDay12(item.getDay12());
						finance.setDay13(item.getDay13());
						finance.setDay14(item.getDay14());
						finance.setDay15(item.getDay15());
						finance.setUser(user);
						//cal.setTime(startDate);
						taskTrackFinanceRepository.save(finance);
					}
				}
				else {

					status ="FM";
					for(TaskTrackApprovalFinance eachdata : data ) {


						if(eachdata.getStatus().equalsIgnoreCase("HM")) {


							for (TaskTrackApprovalLevel2 item : approvedData) {


								if(eachdata.getProjectType().equalsIgnoreCase("Non-Billable") && item.getProjectType().equalsIgnoreCase("Non-Billable")) {

									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);

									}
								}
								if(eachdata.getProjectType().equalsIgnoreCase("Billable") && item.getProjectType().equalsIgnoreCase("Billable")) {

									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);

									}
								}
								if(eachdata.getProjectType().equalsIgnoreCase("Overtime") && item.getProjectType().equalsIgnoreCase("Overtime")) {

									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);

									}
								}
								if(eachdata.getProjectType().equalsIgnoreCase("Beach") && item.getProjectType().equalsIgnoreCase("Beach")) {

									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);

									}
								}
								taskTrackFinanceRepository.save(eachdata);
							}
						}
					}


				}
			}
			String displyDay=null;
			String mon;
			
			//adding 0 before month if it is less than 10th month
			if(monthIndex<10)
			{
				mon="0"+monthIndex;
			}
			else
			{
				mon=""+monthIndex;
			}
			
			//return 15 if mhalf month
			if(status.equals("HM"))
			{
				
				displyDay=yearIndex+"-"+mon+"-15";
			}
			else if(status.equals("FM")) // return month end if  Full Month
			{
				displyDay=yearIndex+"-"+mon+"-"+totaldays;
			}
			userListObject.put("data", "success");
			userListObject.put("status", "success");
			userListObject.put("message", "forwarded to finance");
			userListObject.put("forwadedDate",displyDay);
			userListObject.put("buttonStatus", status);
		}
		else
		{
			userListObject.put("data", "failed");
			userListObject.put("status", "success");
			userListObject.put("message", testValidation.get("message"));
			userListObject.put("buttonStatus", status);
		}

		return userListObject;
	}

	@Override
	public JSONObject getApproveddatalevel1toFinance(Long userId,Long logUser, int monthIndex, int  yearIndex, Long projectId) {
		// TODO Auto-generated method stub

		JSONObject testValidation = new JSONObject();
		String status = null;
		testValidation = checkPreviousTimeSheetsareClosed(monthIndex, yearIndex, projectId, logUser);
		JSONObject userListObject = new JSONObject();
		if((boolean) testValidation.get("data")) {
			int flagExist = 0;
			List<TaskTrackApproval> approvedData = tasktrackRepository.getApprovedData(userId,monthIndex,yearIndex,projectId);


			List<TaskTrackApprovalFinance> data = taskTrackFinanceRepository.getDatas(userId,monthIndex,yearIndex,projectId);
			if(!data.isEmpty()) {

				flagExist = 1;

			}

			YearMonth yearMonthObject = YearMonth.of(yearIndex, monthIndex);
			int totaldays = yearMonthObject.lengthOfMonth();
			Date fdate = new Date();

			if (approvedData != null && approvedData.size() > 0) {

				if(flagExist == 0)
				{
					status = "HM";

					for (TaskTrackApproval item : approvedData) {
						TaskTrackApprovalFinance finance = new TaskTrackApprovalFinance();
						TaskTrackApproval level1 = tasktrackApprovalService.findById(item.getId());
						//level1.setForwarded_date(yesterday);
						level1.setForwarded_finance(fdate);
						UserModel user = userService.getUserDetailsById(userId);
						ProjectModel project = projectService.getProjectId(projectId);
						finance.setProject(project);
						finance.setProjectType(item.getProjectType());
						finance.setApprover_level1(level1);
						finance.setStatus(status);
						finance.setMonth(monthIndex);
						finance.setYear(yearIndex);
						finance.setDay1(item.getDay1());
						finance.setDay2(item.getDay2());
						finance.setDay3(item.getDay3());
						finance.setDay4(item.getDay4());
						finance.setDay5(item.getDay5());
						finance.setDay6(item.getDay6());
						finance.setDay7(item.getDay7());
						finance.setDay8(item.getDay8());
						finance.setDay9(item.getDay9());
						finance.setDay10(item.getDay10());
						finance.setDay11(item.getDay11());
						finance.setDay12(item.getDay12());
						finance.setDay13(item.getDay13());
						finance.setDay14(item.getDay14());
						finance.setDay15(item.getDay15());
						finance.setUser(user);
						//cal.setTime(startDate);
						taskTrackFinanceRepository.save(finance);
					}
				}
				else {
					status = "FM";

					for(TaskTrackApprovalFinance eachdata : data ) {
						if(eachdata.getStatus().equalsIgnoreCase("HM")) {

							for (TaskTrackApproval item : approvedData) {
								if(eachdata.getProjectType().equalsIgnoreCase("Non-Billable") && item.getProjectType().equalsIgnoreCase("Non-Billable")) {

									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);

									}
								}
								if(eachdata.getProjectType().equalsIgnoreCase("Billable") && item.getProjectType().equalsIgnoreCase("Billable")) {

									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);

									}
								}
								if(eachdata.getProjectType().equalsIgnoreCase("Overtime") && item.getProjectType().equalsIgnoreCase("Overtime")) {

									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);

									}
								}
								if(eachdata.getProjectType().equalsIgnoreCase("Beach") && item.getProjectType().equalsIgnoreCase("Beach")) {

									for (int i = 15; i < totaldays; i++) {

										if (i == 15)
											eachdata.setDay16(item.getDay16());
										else if (i == 16)
											eachdata.setDay17(item.getDay17());
										else if (i == 17)
											eachdata.setDay18(item.getDay18());
										else if (i == 18)
											eachdata.setDay19(item.getDay19());
										else if (i == 19)
											eachdata.setDay20(item.getDay20());
										else if (i == 20)
											eachdata.setDay21(item.getDay21());
										else if (i == 21)
											eachdata.setDay22(item.getDay22());
										else if (i == 22)
											eachdata.setDay23(item.getDay23());
										else if (i == 23)
											eachdata.setDay24(item.getDay24());
										else if (i == 24)
											eachdata.setDay25(item.getDay25());
										else if (i == 25)
											eachdata.setDay26(item.getDay26());
										else if (i == 26)
											eachdata.setDay27(item.getDay27());
										else if (i == 27)
											eachdata.setDay28(item.getDay28());
										else if (i == 28)
											eachdata.setDay29(item.getDay29());
										else if (i == 29)
											eachdata.setDay30(item.getDay30());
										else if (i == 30)
											eachdata.setDay31(item.getDay31());
										eachdata.setStatus(status);

									}
								}
								taskTrackFinanceRepository.save(eachdata);
							}
						}
					}
				}
			}
			
			String displyDay=null;
			String mon;
			
			//adding 0 before month if it is less than 10th month
			if(monthIndex<10)
			{
				mon="0"+monthIndex;
			}
			else
			{
				mon=""+monthIndex;
			}
			
			//return 15 if mhalf month
			if(status.equals("HM"))
			{
				
				displyDay=yearIndex+"-"+mon+"-15";
			}
			else if(status.equals("FM")) // return month end if  Full Month
			{
				displyDay=yearIndex+"-"+mon+"-"+totaldays;
			}

			userListObject.put("data", "success");
			userListObject.put("status", "success");
			userListObject.put("message", "forwarded to finance");
			userListObject.put("forwadedDate",displyDay);
			userListObject.put("buttonStatus", status);
		}
		else 
		{
			userListObject.put("data", "failed");
			userListObject.put("status", "success");
			userListObject.put("message", testValidation.get("message"));
			userListObject.put("buttonStatus", status);
		}

		return userListObject;
	}

	@Override
	public List<JSONObject> getTimeTrackUserTaskDetailsLevel2(Long id, Date startDate, Date endDate,
															  List<Object[]> userList, List<JSONObject> loggedJsonArray, List<JSONObject> billableJsonArrayLogged,
															  List<JSONObject> timeTrackJSONData, Boolean isExist, Long projectId) {

		List<JSONObject> billableJsonArray;
		List<JSONObject> overTimeArray;

		// TODO Auto-generated method stub
		if (isExist) {
			JSONObject userListObject = new JSONObject();

			userList =getUserListByProject(id, startDate, endDate,projectId);

			loggedJsonArray = new ArrayList<>();

			String name = null;
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0,intday = 0;
			for (int i = 0; i < diffInDays; i++) {
				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DAY_OF_MONTH);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				Double hours = 0.0;
				if (userList != null && userList.size() > 0) {
					JSONObject jsonObject = new JSONObject();
					for (Object[] item : userList) {

						String st = String.valueOf(item[3]);

						if (st.equals(vl)) {
							hours = hours + (Double) item[2];

						}
						name = (String) item[0] + " " + item[1];
					}
					jsonObject.put(vl, hours);
					cal.add(Calendar.DATE, 1);
					loggedJsonArray.add(jsonObject);

				}

				else {
					JSONObject jsonObject = new JSONObject();
					String uName = userService.getUserName(id);
					name = String.valueOf(uName).replace(",", " ");
					jsonObject.put(vl, 0);
					cal.add(Calendar.DATE, 1);
					loggedJsonArray.add(jsonObject);
				}

			}
			userListObject.put("userName", name);
			userListObject.put("userId", id);
			userListObject.put("month", intMonth);
			userListObject.put("logged", loggedJsonArray);
			//System.out.println("logged has data  : "+loggedJsonArray);
			name = null;
			cal.setTime(startDate);
			int monthIndex = (cal.get(Calendar.MONTH) + 1);
			int yearIndex = cal.get(Calendar.YEAR);

			List<TaskTrackApprovalLevel2> approvalUserList =getUserListForApprovalLevel2(id,projectId,monthIndex,yearIndex);
			billableJsonArray = new ArrayList<>();
			billableJsonArrayLogged=new ArrayList<>();
			overTimeArray=new ArrayList<>();



			diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			intMonth = 0;
			intday = 0;
			Double hours = 0.0;

			if (approvalUserList != null && approvalUserList.size() > 0) {
				JSONObject jsonObject = new JSONObject();

				for (TaskTrackApprovalLevel2 item : approvalUserList) {
					cal.setTime(startDate);

					for (int i = 0; i < diffInDays; i++) {

						intMonth = (cal.get(Calendar.MONTH) + 1);
						intday = cal.get(Calendar.DAY_OF_MONTH);
						String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
								+ ((intday < 10) ? "0" + intday : "" + intday);

						if(i==0)
							hours=(Double)item.getDay1();
						else if(i==1)
							hours=(Double)item.getDay2();
						else if(i==2)
							hours=(Double)item.getDay3();
						else if(i==3)
							hours=(Double)item.getDay4();
						else if(i==4)
							hours=(Double)item.getDay5();
						else if(i==5)
							hours=(Double)item.getDay6();
						else if(i==6)
							hours=(Double)item.getDay7();
						else if(i==7)
							hours=(Double)item.getDay8();
						else if(i==8)
							hours=(Double)item.getDay9();
						else if(i==9)
							hours=(Double)item.getDay10();
						else if(i==10)
							hours=(Double)item.getDay11();
						else if(i==11)
							hours=(Double)item.getDay12();
						else if(i==12)
							hours=(Double)item.getDay13();
						else if(i==13)
							hours=(Double)item.getDay14();
						else if(i==14)
							hours=(Double)item.getDay15();
						else if(i==15)
							hours=(Double)item.getDay16();
						else if(i==16)
							hours=(Double)item.getDay17();
						else if(i==17)
							hours=(Double)item.getDay18();
						else if(i==18)
							hours=(Double)item.getDay19();
						else if(i==19)
							hours=(Double)item.getDay20();
						else if(i==20)
							hours=(Double)item.getDay21();
						else if(i==21)
							hours=(Double)item.getDay22();
						else if(i==22)
							hours=(Double)item.getDay23();
						else if(i==23)
							hours=(Double)item.getDay24();
						else if(i==24)
							hours=(Double)item.getDay25();
						else if(i==25)
							hours=(Double)item.getDay26();
						else if(i==26)
							hours=(Double)item.getDay27();
						else if(i==27)
							hours=(Double)item.getDay28();
						else if(i==28)
							hours=(Double)item.getDay29();
						else if(i==29)
							hours=(Double)item.getDay30();
						else if(i==30)
							hours=(Double)item.getDay31();

						name = (String) item.getFirstName() + " " + item.getLastName();


						if(item.getProjectType().equals("Billable"))
						{
							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							billableJsonArrayLogged.add(jsonObject);
						}

						if(item.getProjectType().equals("Overtime"))
						{

							jsonObject = new JSONObject();
							jsonObject.put(vl, hours);
							overTimeArray.add(jsonObject);


						}





						cal.add(Calendar.DATE, 1);

					}

					/*-------------------------*/

					if(!overTimeArray.isEmpty() && !billableJsonArrayLogged.isEmpty())
					{
						cal.setTime(startDate);
						for (int i = 0; i < diffInDays; i++)
						{
							JSONObject jsonOverTime=new JSONObject();
							JSONObject jsonBillable=new JSONObject();
							JSONObject jsonTotal=new JSONObject();

							Double billable=0.0;
							Double overTime=0.0;


							intMonth = (cal.get(Calendar.MONTH)+1);
							intday = cal.get(Calendar.DAY_OF_MONTH);
							String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
									+ ((intday < 10) ? "0" + intday : "" + intday);

							jsonBillable = billableJsonArrayLogged.get(i);
							if(jsonBillable.get(vl)!=null)
							{
								billable=(Double)jsonBillable.get(vl);
							}




							jsonOverTime = overTimeArray.get(i);
							if(jsonOverTime.get(vl)!=null)
							{
								overTime = (Double) jsonOverTime.get(vl);
							}

							Double totalTime = billable + overTime;
							jsonTotal.put(vl, totalTime);
							billableJsonArray.add(jsonTotal);

							cal.add(Calendar.DATE, 1);
						}
					}
					/*-------------------------*/
				}
			}
			else {
				cal.setTime(startDate);
				for (int i = 0; i < diffInDays; i++) {

					intMonth = (cal.get(Calendar.MONTH) + 1);
					intday = cal.get(Calendar.DAY_OF_MONTH);
					String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
							+ ((intday < 10) ? "0" + intday : "" + intday);

					JSONObject jsonObject = new JSONObject();
					jsonObject.put(vl, 0);
					billableJsonArray.add(jsonObject);

					cal.add(Calendar.DATE, 1);

				}
			}
			userListObject.put("billable", billableJsonArray);



			timeTrackJSONData.add(userListObject);

		} else {
			loggedJsonArray = new ArrayList<>();
			billableJsonArray = new ArrayList<>();
			JSONObject userListObject = new JSONObject();

			String uName = userService.getUserName(id);
			String name = String.valueOf(uName).replace(",", " ");

			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			int intMonth = 0,intday = 0;
			for (int i = 0; i < diffInDays; i++) {
				JSONObject jsonObject = new JSONObject();

				intMonth = (cal.get(Calendar.MONTH) + 1);
				intday = cal.get(Calendar.DAY_OF_MONTH);
				String vl = cal.get(Calendar.YEAR) + "-" + ((intMonth < 10) ? "0" + intMonth : "" + intMonth) + "-"
						+ ((intday < 10) ? "0" + intday : "" + intday);

				jsonObject.put(vl, 0);
				cal.add(Calendar.DATE, 1);
				loggedJsonArray.add(jsonObject);
				billableJsonArray.add(jsonObject);
			}
			userListObject.put("userName", name);
			userListObject.put("userId", id);
			userListObject.put("month", intMonth);
			userListObject.put("logged", loggedJsonArray);
			userListObject.put("billable", billableJsonArray);
			//System.out.println("logged is empty  : "+loggedJsonArray);
			timeTrackJSONData.add(userListObject);
		}
		return timeTrackJSONData;
	}

	public List<TaskTrackApprovalLevel2> getUserListForApprovalLevel2(Long id,Long projectId,Integer monthIndex,Integer yearIndex) {

		List<TaskTrackApprovalLevel2> userList = timeTrackApprovalLevel2.getUserListForApproval(id,projectId,monthIndex,yearIndex);

		return userList;
	}

	@Override
	public List<Object> getForwardedDate(Long projectId, Long userId, int intMonth,int years) {
		// TODO Auto-generated method stub
		return timeTrackApprovalJPARepository.getForwardedDate(projectId,userId,intMonth,years);
	}

	@Override
	public List<Object> getForwardedDateLevel2(Long projectId, Long userId, int intMonth,int year) {
		// TODO Auto-generated method stub
		return timeTrackApprovalLevel2.getForwardedDateLevel2(projectId,userId,intMonth,year);
	}

	@Override
	public ObjectNode saveLevel2FromLevel1(Long projectId, Long userId,Long logUser, Date startDate, Date endDate) {
		// TODO Auto-generated method stub

		TaskTrackApprovalLevel2 tta2 = new TaskTrackApprovalLevel2();
		Calendar cal = Calendar.getInstance();
		ObjectNode response = objectMapper.createObjectNode();
		ObjectNode ids = objectMapper.createObjectNode();
		cal.setTime(startDate);
		int intMonth = 0,intday = 0;
		intMonth = (cal.get(Calendar.MONTH) + 1);
		int yearIndex = cal.get(Calendar.YEAR);
		intday = cal.get(Calendar.DAY_OF_MONTH);

		Long billable_id = null;
		Long nonbillable_id = null;
		Long beach_id = null;
		Long overtime_id = null;


		Date current_date = new Date();
		Calendar current = Calendar.getInstance();
		current.setTime(current_date);
		int intCurrentMonth = 0;
		intCurrentMonth = (current.get(Calendar.MONTH) + 1);
		//System.out.println("currentMonth ------------------------------------>"+(current.get(Calendar.MONTH) + 1));

		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		int day = 0;
		day = c.get(Calendar.DAY_OF_MONTH);
		int totaldays = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
		//System.out.println("total days"+totaldays);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();

		Date dateobj = new Date();
		String status = "";
		int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;

		if(intCurrentMonth > intMonth ) {

			diffInDays = diffInDays + 1;
			status = "FM";
		}
		else {

			status = "HM";
			endDate = yesterday;
		}

		JSONObject testValidation = new JSONObject();
		testValidation = checkPreviousTimeSheetsareClosed(intMonth, yearIndex, projectId, logUser);
		if((boolean) testValidation.get("data")) {
			List<TaskTrackApproval> approvedData = tasktrackRepository.getApprovedData(userId,intMonth,yearIndex,projectId);
			int fflag = 1;
			//   int eflag = 0;


			if (approvedData.size() > 0) {

				if(!timeTrackApprovalLevel2.getApprovedData(userId, intMonth, yearIndex, projectId).isEmpty())
				{
					fflag=2;
				}


				for (TaskTrackApproval item : approvedData) {
					if(fflag == 1) {
						TaskTrackApprovalLevel2 level2 = new TaskTrackApprovalLevel2();
						TaskTrackApproval level1 = tasktrackApprovalService.findById(item.getId());
						level1.setForwarded_date(endDate);
						level1.setStatus(status);
						//System.out.println("Current_Date"+dateobj);
						level1.setApproved_date(endDate);
						UserModel user = userService.getUserDetailsById(userId);
						ProjectModel project = projectService.getProjectId(projectId);
						level2.setProject(project);
						level2.setProjectType(item.getProjectType());
						level2.setTasktrack_level1_Id(level1);
						level2.setStatus(status);

						//level2.setForwarded_date(yesterday);
						level2.setMonth(intMonth);
						level2.setYear(yearIndex);
						for (int i = 0; i < diffInDays - 1; i++) {
							if(i==0)
								level2.setDay1(item.getDay1());
							else if(i==1)
								level2.setDay2(item.getDay2());
							else if(i==2)
								level2.setDay3(item.getDay3());
							else if(i==3)
								level2.setDay4(item.getDay4());
							else if(i==4)
								level2.setDay5(item.getDay5());
							else if(i==5)
								level2.setDay6(item.getDay6());
							else if(i==6)
								level2.setDay7(item.getDay7());
							else if(i==7)
								level2.setDay8(item.getDay8());
							else if(i==8)
								level2.setDay9(item.getDay9());
							else if(i==9)
								level2.setDay10(item.getDay10());
							else if(i==10)
								level2.setDay11(item.getDay11());
							else if(i==11)
								level2.setDay12(item.getDay12());
							else if(i==12)
								level2.setDay13(item.getDay13());
							else if(i==13)
								level2.setDay14(item.getDay14());
							else if(i==14)
								level2.setDay15(item.getDay15());
							else if(i==15)
								level2.setDay16(item.getDay16());
							else if(i==16)
								level2.setDay17(item.getDay17());
							else if(i==17)
								level2.setDay18(item.getDay18());
							else if(i==18)
								level2.setDay19(item.getDay19());
							else if(i==19)
								level2.setDay20(item.getDay20());
							else if(i==20)
								level2.setDay21(item.getDay21());
							else if(i==21)
								level2.setDay22(item.getDay22());
							else if(i==22)
								level2.setDay23(item.getDay23());
							else if(i==23)
								level2.setDay24(item.getDay24());
							else if(i==24)
								level2.setDay25(item.getDay25());
							else if(i==25)
								level2.setDay26(item.getDay26());
							else if(i==26)
								level2.setDay27(item.getDay27());
							else if(i==27)
								level2.setDay28(item.getDay28());
							else if(i==28)
								level2.setDay29(item.getDay29());
							else if(i==29)
								level2.setDay30(item.getDay30());
							else if(i==30)
								level2.setDay31(item.getDay31());
						}
						level2.setUser(user);
						cal.setTime(startDate);
						tta2 = timeTrackApprovalLevel2.save(level2);
						if(tta2.getProjectType().equalsIgnoreCase("Billable")){
							billable_id = tta2.getId();
						}
						else if(tta2.getProjectType().equalsIgnoreCase("Non-Billable")) {
							nonbillable_id = tta2.getId();
						}

						else if(tta2.getProjectType().equalsIgnoreCase("Beach")) {

							beach_id = tta2.getId();
						}
						else if(tta2.getProjectType().equalsIgnoreCase("Overtime")) {

							overtime_id = tta2.getId();
						}
					}

					else if(fflag == 2) {

						List<TaskTrackApprovalLevel2> level2data = timeTrackApprovalLevel2.getApprovedData(userId, intMonth, yearIndex, projectId);

						if (level2data != null && level2data.size() > 0) {

							for(TaskTrackApprovalLevel2 item1:level2data) {

								Date previous_forwardedDate =  item1.getTasktrack_level1_Id().getForwarded_date();

								Calendar caldays = Calendar.getInstance();
								caldays.setTime(previous_forwardedDate);
								Calendar caldayss = Calendar.getInstance();
								caldays.setTime(endDate);
								int dayf = 0; int daypf = 0;
								daypf = cal.get(Calendar.DAY_OF_MONTH);
								dayf = caldayss.get(Calendar.DAY_OF_MONTH);
								if(item.getProjectType().equalsIgnoreCase("Billable") && item1.getProjectType().equalsIgnoreCase("Billable"))	{
									for (int i = daypf; i < dayf; i++) {
										if(i==1)
											item1.setDay1(item.getDay1());
										else if(i==2)
											item1.setDay2(item.getDay2());
										else if(i==3)
											item1.setDay3(item.getDay3());
										else if(i==4)
											item1.setDay4(item.getDay4());
										else if(i==5)
											item1.setDay5(item.getDay5());
										else if(i==6)
											item1.setDay6(item.getDay6());
										else if(i==7)
											item1.setDay7(item.getDay7());
										else if(i==8)
											item1.setDay8(item.getDay8());
										else if(i==9)
											item1.setDay9(item.getDay9());
										else if(i==10)
											item1.setDay10(item.getDay10());
										else if(i==11)
											item1.setDay11(item.getDay11());
										else if(i==12)
											item1.setDay12(item.getDay12());
										else if(i==13)
											item1.setDay13(item.getDay13());
										else if(i==14)
											item1.setDay14(item.getDay14());
										else if(i==15)
											item1.setDay15(item.getDay15());
										else if(i==16)
											item1.setDay16(item.getDay16());
										else if(i==17)
											item1.setDay17(item.getDay17());
										else if(i==18)
											item1.setDay18(item.getDay18());
										else if(i==19)
											item1.setDay19(item.getDay19());
										else if(i==20)
											item1.setDay20(item.getDay20());
										else if(i==21)
											item1.setDay21(item.getDay21());
										else if(i==22)
											item1.setDay22(item.getDay22());
										else if(i==23)
											item1.setDay23(item.getDay23());
										else if(i==24)
											item1.setDay24(item.getDay24());
										else if(i==25)
											item1.setDay25(item.getDay25());
										else if(i==26)
											item1.setDay26(item.getDay26());
										else if(i==27)
											item1.setDay27(item.getDay27());
										else if(i==28)
											item1.setDay28(item.getDay28());
										else if(i==29)
											item1.setDay29(item.getDay29());
										else if(i==30)
											item1.setDay30(item.getDay30());
										else if(i==31)
											item1.setDay31(item.getDay31());
										tta2 = timeTrackApprovalLevel2.save(item1);
										billable_id = tta2.getId();
									}
								}

								if(item.getProjectType().equalsIgnoreCase("Non-Billable") && item1.getProjectType().equalsIgnoreCase("Non-Billable"))	{
									for (int i = daypf; i < dayf; i++) {
										if(i==1)
											item1.setDay1(item.getDay1());
										else if(i==2)
											item1.setDay2(item.getDay2());
										else if(i==3)
											item1.setDay3(item.getDay3());
										else if(i==4)
											item1.setDay4(item.getDay4());
										else if(i==5)
											item1.setDay5(item.getDay5());
										else if(i==6)
											item1.setDay6(item.getDay6());
										else if(i==7)
											item1.setDay7(item.getDay7());
										else if(i==8)
											item1.setDay8(item.getDay8());
										else if(i==9)
											item1.setDay9(item.getDay9());
										else if(i==10)
											item1.setDay10(item.getDay10());
										else if(i==11)
											item1.setDay11(item.getDay11());
										else if(i==12)
											item1.setDay12(item.getDay12());
										else if(i==13)
											item1.setDay13(item.getDay13());
										else if(i==14)
											item1.setDay14(item.getDay14());
										else if(i==15)
											item1.setDay15(item.getDay15());
										else if(i==16)
											item1.setDay16(item.getDay16());
										else if(i==17)
											item1.setDay17(item.getDay17());
										else if(i==18)
											item1.setDay18(item.getDay18());
										else if(i==19)
											item1.setDay19(item.getDay19());
										else if(i==20)
											item1.setDay20(item.getDay20());
										else if(i==21)
											item1.setDay21(item.getDay21());
										else if(i==22)
											item1.setDay22(item.getDay22());
										else if(i==23)
											item1.setDay23(item.getDay23());
										else if(i==24)
											item1.setDay24(item.getDay24());
										else if(i==25)
											item1.setDay25(item.getDay25());
										else if(i==26)
											item1.setDay26(item.getDay26());
										else if(i==27)
											item1.setDay27(item.getDay27());
										else if(i==28)
											item1.setDay28(item.getDay28());
										else if(i==29)
											item1.setDay29(item.getDay29());
										else if(i==30)
											item1.setDay30(item.getDay30());
										else if(i==31)
											item1.setDay31(item.getDay31());
										tta2 = timeTrackApprovalLevel2.save(item1);
										nonbillable_id = tta2.getId();
									}
								}
								if(item.getProjectType().equalsIgnoreCase("Beach") && item1.getProjectType().equalsIgnoreCase("Beach"))	{
									for (int i = daypf; i < dayf; i++) {
										if(i==1)
											item1.setDay1(item.getDay1());
										else if(i==2)
											item1.setDay2(item.getDay2());
										else if(i==3)
											item1.setDay3(item.getDay3());
										else if(i==4)
											item1.setDay4(item.getDay4());
										else if(i==5)
											item1.setDay5(item.getDay5());
										else if(i==6)
											item1.setDay6(item.getDay6());
										else if(i==7)
											item1.setDay7(item.getDay7());
										else if(i==8)
											item1.setDay8(item.getDay8());
										else if(i==9)
											item1.setDay9(item.getDay9());
										else if(i==10)
											item1.setDay10(item.getDay10());
										else if(i==11)
											item1.setDay11(item.getDay11());
										else if(i==12)
											item1.setDay12(item.getDay12());
										else if(i==13)
											item1.setDay13(item.getDay13());
										else if(i==14)
											item1.setDay14(item.getDay14());
										else if(i==15)
											item1.setDay15(item.getDay15());
										else if(i==16)
											item1.setDay16(item.getDay16());
										else if(i==17)
											item1.setDay17(item.getDay17());
										else if(i==18)
											item1.setDay18(item.getDay18());
										else if(i==19)
											item1.setDay19(item.getDay19());
										else if(i==20)
											item1.setDay20(item.getDay20());
										else if(i==21)
											item1.setDay21(item.getDay21());
										else if(i==22)
											item1.setDay22(item.getDay22());
										else if(i==23)
											item1.setDay23(item.getDay23());
										else if(i==24)
											item1.setDay24(item.getDay24());
										else if(i==25)
											item1.setDay25(item.getDay25());
										else if(i==26)
											item1.setDay26(item.getDay26());
										else if(i==27)
											item1.setDay27(item.getDay27());
										else if(i==28)
											item1.setDay28(item.getDay28());
										else if(i==29)
											item1.setDay29(item.getDay29());
										else if(i==30)
											item1.setDay30(item.getDay30());
										else if(i==31)
											item1.setDay31(item.getDay31());
										tta2 = timeTrackApprovalLevel2.save(item1);
										beach_id = tta2.getId();
									}
								}
								if(item.getProjectType().equalsIgnoreCase("Overtime") && item1.getProjectType().equalsIgnoreCase("Overtime"))	{
									for (int i = daypf; i < dayf; i++) {
										if(i==1)
											item1.setDay1(item.getDay1());
										else if(i==2)
											item1.setDay2(item.getDay2());
										else if(i==3)
											item1.setDay3(item.getDay3());
										else if(i==4)
											item1.setDay4(item.getDay4());
										else if(i==5)
											item1.setDay5(item.getDay5());
										else if(i==6)
											item1.setDay6(item.getDay6());
										else if(i==7)
											item1.setDay7(item.getDay7());
										else if(i==8)
											item1.setDay8(item.getDay8());
										else if(i==9)
											item1.setDay9(item.getDay9());
										else if(i==10)
											item1.setDay10(item.getDay10());
										else if(i==11)
											item1.setDay11(item.getDay11());
										else if(i==12)
											item1.setDay12(item.getDay12());
										else if(i==13)
											item1.setDay13(item.getDay13());
										else if(i==14)
											item1.setDay14(item.getDay14());
										else if(i==15)
											item1.setDay15(item.getDay15());
										else if(i==16)
											item1.setDay16(item.getDay16());
										else if(i==17)
											item1.setDay17(item.getDay17());
										else if(i==18)
											item1.setDay18(item.getDay18());
										else if(i==19)
											item1.setDay19(item.getDay19());
										else if(i==20)
											item1.setDay20(item.getDay20());
										else if(i==21)
											item1.setDay21(item.getDay21());
										else if(i==22)
											item1.setDay22(item.getDay22());
										else if(i==23)
											item1.setDay23(item.getDay23());
										else if(i==24)
											item1.setDay24(item.getDay24());
										else if(i==25)
											item1.setDay25(item.getDay25());
										else if(i==26)
											item1.setDay26(item.getDay26());
										else if(i==27)
											item1.setDay27(item.getDay27());
										else if(i==28)
											item1.setDay28(item.getDay28());
										else if(i==29)
											item1.setDay29(item.getDay29());
										else if(i==30)
											item1.setDay30(item.getDay30());
										else if(i==31)
											item1.setDay31(item.getDay31());

										tta2 = timeTrackApprovalLevel2.save(item1);
										overtime_id = tta2.getId();
										//System.out.println("Overtime"+overtime_id);
									}
								}


							}

						}
					}
				}

			}

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			ids.put("billable_id", billable_id);
			ids.put("nonbillable_id", nonbillable_id);
			ids.put("beach_id", beach_id);
			ids.put("overtime_id", overtime_id);
			response.put("data", "");
			response.put("status", "success");
			response.put("message", "forwarded to level2");
			response.put("forwardedDate",(outputFormat.format(endDate)).toString());
			response.set("ids", ids);
		}
		else {
			response.put("data", "");
			response.put("status", "failed");
			response.put("message", (String)testValidation.get("message"));
		}
		return response;
	}


	public ArrayList<JSONObject>  getFinanceDataByProject(int month, int year, Long projectId) {

		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		ArrayList<JSONObject> resultData = new ArrayList<JSONObject>();
		List<Object[]> financeData = taskTrackFinanceRepository.getFinanceDataByProject(month, year, projectId);
		String intmonth;
		if(month<10){
			intmonth ="0"+month;
		}
		else{
			intmonth =String.valueOf(month);
		}
		for(Object[] item : financeData) {
			JSONObject node = new JSONObject();
			List<JSONObject> billableArray = new ArrayList<>();
			List<JSONObject> userArray = new ArrayList<>();

			node.put("userId",item[0]);
			node.put("firstName",item[1]);
			node.put("lastName",item[2]);
			node.put("status",item[3]);
			for(int i=1;i<=daysInMonth;i++)
			{
				String j;
				if(i<10){
					j ="0"+i;
				}
				else{
					j =String.valueOf(i);
				}
				JSONObject billableNode = new JSONObject();
				billableNode.put(year+"-"+intmonth+"-"+j,item[i+3]);
				billableArray.add(billableNode);
			}
			node.put("billable",billableArray);
			resultData.add(node);
		}

		return resultData;
	}

	public ArrayList<JSONObject>  getFinanceDataByUser(int month, int year, Long userId) {


		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		ArrayList<JSONObject> resultData = new ArrayList<JSONObject>();
		List<Object[]> financeData = taskTrackFinanceRepository.getFinanceDataByUser(month, year, userId);
		String intmonth;
		if(month<10){
			intmonth ="0"+month;
		}
		else{
			intmonth =String.valueOf(month);
		}
		for(Object[] item : financeData) {
			JSONObject node = new JSONObject();
			List<JSONObject> billableArray = new ArrayList<>();
			node.put("projectId",item[0]);
			node.put("projectName",item[1]);
			node.put("status",item[2]);
			for(int i=1;i<=daysInMonth;i++)
			{
				String j;
				if(i<10){
					j ="0"+i;
				}
				else{
					j =String.valueOf(i);
				}
				JSONObject billableNode = new JSONObject();
				billableNode.put(year+"-"+intmonth+"-"+j,item[i+2]);
				billableArray.add(billableNode);
			}
			node.put("billable",billableArray);
			resultData.add(node);
		}

		return resultData;
	}

	public ArrayList<JSONObject>  getFinanceDataByUserAndProject(int month, int year, Long userId, Long projectId) {


		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		ArrayList<JSONObject> resultData = new ArrayList<JSONObject>();
		List<Object[]> financeData = taskTrackFinanceRepository.getFinanceDataByUserAndProject(month, year, userId, projectId);
		String intmonth;
		if(month<10){
			intmonth ="0"+month;
		}
		else{
			intmonth =String.valueOf(month);
		}
		for(Object[] item : financeData) {
			JSONObject node = new JSONObject();
			List<JSONObject> billableArray = new ArrayList<>();
			node.put("projectId",item[0]);
			node.put("projectName",item[1]);
			node.put("userId",item[2]);
			node.put("firstName",item[3]);
			node.put("lastName",item[4]);
			node.put("status",item[5]);
			for(int i=1;i<=daysInMonth;i++)
			{
				String j;
				if(i<10){
					j ="0"+i;
				}
				else{
					j =String.valueOf(i);
				}
				JSONObject billableNode = new JSONObject();
				billableNode.put(year+"-"+intmonth+"-"+j,item[i+5]);
				billableArray.add(billableNode);
			}
			node.put("billable",billableArray);
			resultData.add(node);
		}

		return resultData;
	}


	@Override
	public List<Object[]> getForwardedDates(Long projectId, Long userId, int intMonth, int yearIndex) {
		// TODO Auto-generated method stub
		return timeTrackApprovalJPARepository.getForwardedDates(projectId, userId, intMonth,yearIndex);
	}

	@Override
	public List<TaskTrackApprovalLevel2> getUserIdByProjectAndDateForLevel2(Long projectId, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		int intMonth = 0,intday = 0;
		intMonth = (cal.get(Calendar.MONTH) + 1);
		int yearIndex = cal.get(Calendar.YEAR);
		return timeTrackApprovalLevel2.getUserIdByProjectAndDateForLevel2(projectId,intMonth,yearIndex);
	}

	public JSONObject checkPreviousTimeSheetsareClosed(int month, int year, Long projectId, Long approverId){

		UserModel user =  userRepository.getOne(approverId);
		JSONObject jsonDataRes = new JSONObject();
		String message=null;
		Boolean status=true;

		Long role = user.getRole().getroleId();
		//int prevmonth = month-1;
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		calendar.add(Calendar.MONTH, -1);
		int lastday = calendar.getActualMaximum(Calendar.DATE);
		calendar.set(Calendar.DATE, lastday);
		Date lastDate = calendar.getTime();
		Integer prevMonth = calendar.get(Calendar.MONTH);
		Integer prevMonthYear = calendar.get(Calendar.YEAR);
		Long approverrowcount = null;
		String fromdate = prevMonthYear+"-"+((prevMonth < 10) ? "0" + prevMonth : "" + prevMonth) +"-01";
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
		LocalDate date = LocalDate.parse(fromdate, dateFormat);
		ValueRange range = date.range(ChronoField.DAY_OF_MONTH);
		Long max = range.getMaximum();
		String todate = String.format("%s-%s-%d", prevMonthYear, prevMonth, max);
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate =null;
		Date endDate = null;

		try {
			 startDate = outputFormat.parse(fromdate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			 endDate = outputFormat.parse(todate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int allocated = tasktrackRepository.checkprojectallocated(projectId, startDate, endDate);
		if(allocated>0) {
			int expectedRows = allocated * 4;
			if (role == 2)
			{
				ProjectModel projectData = projectRepository.getProjectDetails(projectId);
				approverrowcount = timeTrackApprovalJPARepository.getCountOfRows(prevMonth, prevMonthYear, projectId);
				if(expectedRows ==approverrowcount)
				{
					if (projectData.getOnsite_lead() == null) {
						if (expectedRows == taskTrackFinanceRepository.getCountOfRowsHM(prevMonth, prevMonthYear, projectId))
						{
							if (expectedRows == taskTrackFinanceRepository.getCountOfRowsFM(prevMonth, prevMonthYear, projectId))
							{
								message = "No pending logs found";
								status = true;

							}
							else
							{
								message = "Pending previous full month logs found";
								status = false;
							}
						}
						else
						{
							message = "Pending previous half month logs found";
							status = false;
						}
					}
					else
					{
						if (expectedRows == timeTrackApprovalLevel2.getCountOfRowsHM(prevMonth, prevMonthYear, projectId))
						{
							if (expectedRows == timeTrackApprovalLevel2.getCountOfRowsFM(prevMonth, prevMonthYear, projectId))
							{
								message = "No pending logs found";
								status = true;
							}
							else
							{
								message = "Pending previous full month logs found";
								status = false;
							}
						}
						else
						{
							message = "Pending previous half month logs found";
							status = false;
						}

					}
				}
				else
				{
					message = "Pending previous month log approval found";
					status = false;
				}
			}
			else if (role == 7)
			{
				approverrowcount = timeTrackApprovalLevel2.getCountOfRowsHM(prevMonth, prevMonthYear, projectId);
				if (expectedRows == approverrowcount)
				{
					if (approverrowcount == taskTrackFinanceRepository.getCountOfRowsHM(prevMonth, prevMonthYear, projectId))
					{
						if (approverrowcount == taskTrackFinanceRepository.getCountOfRowsFM(prevMonth, prevMonthYear, projectId))
						{
							message = "No pending logs found";
							status = true;
						}
						else
						{
							message = "Pending previous full month logs found";
							status = false;
						}
					}
					else
					{
						message = "Pending previous half month logs found";
						status = false;
					}

				}
				else
				{
					message = "Pending previous month log approval found";
					status = false;
				}
			}
		}
		else{
			message = "No pending logs found";
			status = true;

		}

		jsonDataRes.put("data", status);
		jsonDataRes.put("status", "success");
		jsonDataRes.put("message", message);
		jsonDataRes.put("month", prevMonth);
		jsonDataRes.put("year", prevMonthYear);

		return jsonDataRes;
	}

	public JSONObject halfCycleCheck(Long projectId,Long userId,Long approverId,Date endDate) throws ParseException {

		JSONObject jsonDataRes = new JSONObject();
		UserModel user =  userRepository.getOne(approverId);
		String message=null;
		Boolean status=true;
		Long approverrowcount = null;

		Long role = user.getRole().getroleId();
		DateFormat df;
		df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		int day=calendar.get(Calendar.DAY_OF_MONTH);
		int month = (calendar.get(Calendar.MONTH)+1);
		int year = calendar.get(Calendar.YEAR);
		String fromdate = year+"-"+((month < 10) ? "0" + month : "" + month) +"-01";
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startdDate = outputFormat.parse(fromdate);
		List<Tasktrack> logData = tasktrackRepository.getByDate(startdDate,endDate,userId);
		String financeStatus = "";

		if(day>15)
		{
			if(!logData.isEmpty()) {
				if (role == 2) {
						
					ProjectModel projectData = projectRepository.getProjectDetails(projectId);
					approverrowcount = timeTrackApprovalJPARepository.getCountOfRowsByUser(month, year, projectId, userId);
					
					if (projectData.getOnsite_lead() == null) {

						if(approverrowcount==0)
						{
							financeStatus = "";
							message = "Log not approved yet";
							status = false;
						}
						else if (approverrowcount == taskTrackFinanceRepository.getCountOfRowsHMByUser(month, year, projectId, userId))
						{
							
							if(approverrowcount == taskTrackFinanceRepository.getCountOfRowsFMByUser(month, year, projectId, userId))
							{
								financeStatus = "FM";
								message = "No pending logs found";
								status = true;
							}
							else
							{
								financeStatus = "HM";
								message = "Pending full month logs found ";
								status = true;
							}
						}
						else
						{
							financeStatus = "";
							message = "Pending half month logs found";
							status = false;
						}
					}
					else
					{
						if (approverrowcount == timeTrackApprovalLevel2.getCountOfRowsHMByUser(month, year, projectId, userId))
						{
							financeStatus = "";
							message = "No pending logs found";
							status = true;
						}
						else
						{
							financeStatus = "";
							message = "Pending half month logs found";
							status = false;
						}

					}
				}
				else if (role == 7)
				{

					approverrowcount = timeTrackApprovalLevel2.getCountOfRowsHMByUser(month, year, projectId, userId);
					if (approverrowcount == taskTrackFinanceRepository.getCountOfRowsHMByUser(month, year, projectId, userId))
					{
						if(approverrowcount==0)
						{
							financeStatus = "";
							message = "Log not approved yet";
							status = false;
						}
						else if(approverrowcount == taskTrackFinanceRepository.getCountOfRowsFMByUser(month, year, projectId, userId))
						{
							financeStatus = "FM";
							message = "No pending logs found";
							status = true;
						}
						else
						{
							financeStatus = "HM";
							message = "Pending full month logs found ";
							status = true;
						}
					}
					else
					{
						financeStatus = "";
						message = "Pending half month logs found";
						status = false;
					}

				}
			}
			else
			{
				financeStatus = "";
				message = "Pending half month logs found";
				status = false;
			}


		}
		//List<AllocationModel> =

		jsonDataRes.put("data", status);
		jsonDataRes.put("status", "success");
		jsonDataRes.put("message", message);
		jsonDataRes.put("financeStatus", financeStatus);

		return jsonDataRes;

	}


	
}
