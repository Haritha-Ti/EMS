package com.EMS.service;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.EMS.dto.MailDomainDto;
import com.EMS.dto.PreviousMonthMailDto;
import com.EMS.repository.CronDateRepository;
import com.EMS.repository.HolidayRepository;
import com.EMS.repository.TasktrackRepository;
import com.EMS.utility.Constants;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Component
public class SchedulerService {

	

	@Autowired
	private TasktrackRepository taskRepo;

	@Autowired
	private HolidayRepository holidayRepo;
	
	@Autowired
	private EmailNotificationService emailNotificationService;
	
	@Autowired
    private Configuration freemarkerConfig;
	
	@Autowired
	private CronDateRepository cronDateRepository;


	@SuppressWarnings({ "unchecked", "rawtypes", "unused", "unlikely-arg-type" })
	//@Scheduled(cron = "0 35 16 * * *")
	@Scheduled(cron = "#{getCronCreateTaskTrack}")
	public void createTaskTrack() throws Exception {



		Date date = null, croneDate = null;
		LocalDateTime now = LocalDateTime.now();
		TimeZone zone = TimeZone.getTimeZone("MST");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		formatter.setTimeZone(zone);
		date = formatter.parse(now.toString());

		Map userMap = new HashMap<String, Map<String, ArrayList<String>>>();
		Map userActiveMap = new HashMap<String, Integer>();
		Map projectActiveMap = new HashMap<String, String>();
		Map userEmailMap = new HashMap<String, String>();
		Map userFullNameMap = new HashMap<String, String>();

		List<Date> holidays = new ArrayList();
		holidays = holidayRepo.getHolidayDateList();

		List<Integer> dates = cronDateRepository.getTaskTrackSchedulerDate();
		if (dates != null && dates.size() > 0) {

			for (Integer item : dates) {
//				croneDate = formatter.parse(item.getCronDate().toString());
				int year = LocalDate.now().getYear();
				int currentMonth = LocalDate.now().getMonthValue();
				String cronDate = year + "-" + currentMonth + "-" + item;
				if (formatter.parse(cronDate).equals(date)) {
					System.out.println("Scheduler started " + date);
					LocalDate toDate = now.minusDays(1).toLocalDate();
					LocalDate fromDate = now.minusMonths(1).minusDays(toDate.getDayOfMonth()).toLocalDate();
					List<Object[]> trackTaskList = taskRepo.getTrackTaskList(fromDate, toDate);
					
					
					for (Object[] obj : trackTaskList) {
						String userName = obj[5].toString();
						String projectName = obj[1].toString();
						String taskDate = (obj[0] == null) ? null : obj[0].toString();
						Integer userIsActive = (Integer) obj[6];
						String email = (obj[3] == null) ? null : obj[3].toString();
						String projectEndDate = (obj[4] == null) ? null : obj[4].toString();
						String fullName = obj[2].toString();

						Map projectMap = new HashMap<String, ArrayList<String>>();
						if (userMap.containsKey(userName)) {
							projectMap = (Map) userMap.get(userName);
							if (projectMap.containsKey(projectName)) {
								ArrayList<String> dateList = (ArrayList<String>) projectMap.get(projectName);
								dateList.add(taskDate);
								projectMap.put(projectName, dateList);
							} else {
								projectMap.put(projectName, new ArrayList<>(Arrays.asList(taskDate)));
								userMap.put(userName, projectMap);
							}
						} else {
							if (taskDate != null) {
								projectMap.put(projectName, new ArrayList<>(Arrays.asList(taskDate)));
							} else
								projectMap.put(projectName, null);
							userMap.put(userName, projectMap);
						}

						// User IsActive
						if (!userActiveMap.containsKey(userName)) {
							userActiveMap.put(userName, userIsActive);
							userEmailMap.put(userName, email);
						}
						if (!projectActiveMap.containsKey(projectName)) {
							projectActiveMap.put(projectName, projectEndDate);
						}
						if (!userFullNameMap.containsKey(userName)) {
							userFullNameMap.put(userName, fullName);
						}

					}

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

					Iterator it = userMap.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();

						Map projectMap = new HashMap<String, ArrayList<String>>();
						projectMap = (Map) entry.getValue();

						String userName = entry.getKey().toString();
						List<String> projectList = (List<String>) projectMap.keySet().stream()
								.collect(Collectors.toList());

						for (String projectName : projectList) {
							System.out.println(projectName);
							
							Map allocationDateMap = new HashMap<String, ArrayList<JSONArray>>();

							List<Object[]> allocationDateList = taskRepo.getAllocationDateList(fromDate,projectName);
							if(allocationDateList != null && allocationDateList.size() > 0) {
								
								for(Object[] allocationItem : allocationDateList) {
									String username = allocationItem[2].toString();
									String startDate = allocationItem[0].toString();
									String endDate  = allocationItem[1].toString();
									
									ArrayList<JSONArray> dateList = new ArrayList<JSONArray>();
											
									if(allocationDateMap.containsKey(username)) {
										dateList =  (ArrayList<JSONArray>) allocationDateMap.get(username);
									    JSONArray arrayItem = new JSONArray();
									    arrayItem.add(startDate);
									    arrayItem.add(endDate);
									    dateList.add(arrayItem);
									    allocationDateMap.put(username, dateList);
//									    
									} 
									else {
										JSONArray arrayItem = new JSONArray();
									    arrayItem.add(startDate);
									    arrayItem.add(endDate);
									    dateList.add(arrayItem);
									    allocationDateMap.put(username, dateList);
									}
									
								}
								
								
								
								List<LocalDate> dateList = (List<LocalDate>) projectMap.get(projectName);
								StringBuilder sb = new StringBuilder();
								LocalDate startDate = fromDate;

								if (Integer.parseInt(userActiveMap.get(userName).toString()) == 1) {

									while (!startDate.isAfter(toDate)) {
										ArrayList<JSONArray> allocationDateArray = (ArrayList<JSONArray>) allocationDateMap.get(userName);

										if(allocationDateArray != null && allocationDateArray.size() > 0) {
											for (ArrayList<String> dateItem : allocationDateArray) {
										        LocalDate date1 = LocalDate.parse(dateItem.get(0).toString(), format);
										        LocalDate date2 = LocalDate.parse(dateItem.get(1).toString(), format);
												if((startDate.isAfter(date1) || startDate.isEqual(date1)) && (startDate.isBefore(date2) || startDate.isEqual(date2))) {
													
													
													if ((sdf.parse(projectActiveMap.get(projectName).toString())
															.after(sdf.parse(startDate.toString())))
															|| (sdf.parse(projectActiveMap.get(projectName).toString())
																	.equals((sdf.parse(startDate.toString()))))) {
														if ((dateList == null || !dateList.contains(startDate.toString()))
																&& !holidays.contains(sdf.parse(startDate.toString()))
																&& !startDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)
																&& !startDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {

															if (sb.length() != 0)
																sb.append(", ");
															sb.append(startDate.format(DateTimeFormatter.ofPattern("MMM d")));
														}
													}

												}
											}
										}

										startDate = startDate.plusDays(1);

									}
								} else {
									continue;
								}

								if (sb.length() != 0) {
									MailDomainDto mailDomainDto = new MailDomainDto();
									mailDomainDto.setSubject("RCG Time Sheet- Please Submit ASAP ");

									StringBuilder mailBody = new StringBuilder(
											"Hi " + userFullNameMap.get(userName) + ", ");
									mailBody.append("<br/><br/>Project: " + projectName);
									mailBody.append(
											"<br/><br/>Your timetrack is pending for the following days: <br/>" + sb);

									Template t = freemarkerConfig.getTemplate("email_template.ftl");
									String html = (FreeMarkerTemplateUtils.processTemplateIntoString(t, mailDomainDto))
											.replace("MAIL_BODY", mailBody)
											.replace("Title", "Please submit your time sheet !");

									mailDomainDto.setMailBody(html);
									mailDomainDto.setTo(userEmailMap.get(userName).toString());
									String token = UUID.randomUUID().toString();
									String msg = emailNotificationService.sendMail(token, mailDomainDto, false);
								}
								
								
								
							}
	
							
							
						}

					}

				}
			}
		}
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes", "unused", "unlikely-arg-type" })
//	//@Scheduled(cron = "* * * * * *")
	@Scheduled(cron = "#{getCronTaskTrackSchedulerAtUserlevel}")
	public void taskTrackSchedulerAtUserlevel() throws Exception {

		Date date = null, croneDate = null;
		LocalDateTime now = LocalDateTime.now();
		TimeZone zone = TimeZone.getTimeZone("MST");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		formatter.setTimeZone(zone);
		date = formatter.parse(now.toString());

		List<Date> holidays = new ArrayList();
		holidays = holidayRepo.getHolidayDateList();

		Map userDateListMap = new HashMap<String, ArrayList<String>>();
		Map userEmailMap = new HashMap<String, String>();
		Map userFullNameMap = new HashMap<String, String>();

//		List<TaskTrackCronModel> dates = cronRepo.getCronDates();
		List<Integer> dates = cronDateRepository.getUserLevelSchedulerDate();

		if (dates != null && dates.size() > 0) {
			for (Integer item : dates) {
//				croneDate = formatter.parse(item.getCronDate().toString());

				int year = LocalDate.now().getYear();
				int currentMonth = LocalDate.now().getMonthValue();
				String cronDate = year + "-" + currentMonth + "-" + item;
				if (formatter.parse(cronDate).equals(date)) {

					LocalDate toDate = now.minusDays(1).toLocalDate();
					LocalDate fromDate = now.minusMonths(1).minusDays(toDate.getDayOfMonth()).toLocalDate();
					List<Object[]> userTaskTrackList = taskRepo.getUserTaskTrackList(fromDate, toDate);

					if (userTaskTrackList != null && userTaskTrackList.size() > 0) {
						for (Object[] userTaskItem : userTaskTrackList) {
							String userName = (userTaskItem[3] == null) ? null : userTaskItem[3].toString();
							String email = (userTaskItem[4] == null) ? null : userTaskItem[4].toString();
							String fullName = (userTaskItem[5] == null) ? null : userTaskItem[5].toString();
							String taskDate = (userTaskItem[1] == null) ? null : userTaskItem[1].toString();

							if (!userEmailMap.containsKey(userName))
								userEmailMap.put(userName, email);
							if (!userFullNameMap.containsKey(userName))
								userFullNameMap.put(userName, fullName);
							if (!userDateListMap.containsKey(userName)) {
								ArrayList<String> dateList = new ArrayList<String>();
								dateList.add(taskDate);
								userDateListMap.put(userName, dateList);
							} else {
								ArrayList<String> dateList = (ArrayList<String>) userDateListMap.get(userName);
								dateList.add(taskDate);
								userDateListMap.put(userName, dateList);
							}

						}

					}

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Iterator it = userDateListMap.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();
						List<LocalDate> dateList = (List<LocalDate>) userDateListMap.get(entry.getKey());

						StringBuilder sb = new StringBuilder();
						LocalDate startDate = fromDate;

						while (!startDate.isAfter(toDate)) {
							if ((dateList == null || !dateList.contains(startDate.toString()))
									&& !holidays.contains(sdf.parse(startDate.toString()))
									&& !startDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)
									&& !startDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {

								if (sb.length() != 0)
									sb.append(", ");
								sb.append(startDate.format(DateTimeFormatter.ofPattern("MMM d")));
							}
							startDate = startDate.plusDays(1);
						}

						if (sb.length() != 0) {
							MailDomainDto mailDomainDto = new MailDomainDto();
							mailDomainDto.setSubject("RCG Time Sheet- Please Submit ASAP ");

							StringBuilder mailBody = new StringBuilder(
									"Hi " + userFullNameMap.get(entry.getKey()) + ", ");
							mailBody.append("<br/><br/>Your timetrack is pending for the following days: <br/>" + sb);
							mailBody.append("<br/><br/>If there is no project exist,choose beach project.");
							Template t = freemarkerConfig.getTemplate("email_template.ftl");
							String html = (FreeMarkerTemplateUtils.processTemplateIntoString(t, mailDomainDto))
									.replace("MAIL_BODY", mailBody).replace("Title", "Please submit your time sheet !");

							mailDomainDto.setMailBody(html);
							mailDomainDto.setTo(userEmailMap.get(entry.getKey()).toString());
							String token = UUID.randomUUID().toString();
							String msg = emailNotificationService.sendMail(token, mailDomainDto, false);
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "unused", "unlikely-arg-type" })
//	//@Scheduled(cron = "* * * * * *")
	@Scheduled(cron = "#{getCronForApproverTwoFirstHalf}")
	public void approverTwoFirstHalfScheduler() throws Exception {

		Date date = null, croneDate = null;
		LocalDateTime now = LocalDateTime.now();
		TimeZone zone = TimeZone.getTimeZone("MST");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		formatter.setTimeZone(zone);
		date = formatter.parse(now.toString());
		System.out.println("Starting ApproverTwoFirstHalfScheduler....");
		Map approverMap = new HashMap<String, Map<String, ArrayList<String>>>();
		Map approverMailMap = new HashMap<String, String>();
		Map approverFullNameMap = new HashMap<String, String>();

//		List<TaskTrackCronModel> dates = cronRepo.getCronDates();
		List<Integer> dates = cronDateRepository.getapproverTwoFirstHalfSchedulerDate();

		if (dates != null && dates.size() > 0) {
			for (Integer item : dates) {
//				croneDate = formatter.parse(item.getCronDate().toString());

				int year = LocalDate.now().getYear();
				int currentMonth = LocalDate.now().getMonthValue();
				String cronDate = year + "-" + currentMonth + "-" + item;
				if (formatter.parse(cronDate).equals(date)) {
					Integer month = now.getMonthValue();

					Map<String, PreviousMonthMailDto> previousmonthdata = getApproverOnePreviousMonthData(
							now.getMonthValue() - 1, 1, 2);

					List<Object[]> approverTwoDataList = taskRepo.getApproverTwoFirstHalfInfo(month);

					if (approverTwoDataList != null && approverTwoDataList.size() > 0) {
						for (Object[] approverTwoDataItem : approverTwoDataList) {
							String approverName = (approverTwoDataItem[1] == null) ? null
									: approverTwoDataItem[1].toString();
							String projectName = (approverTwoDataItem[3] == null) ? null
									: approverTwoDataItem[3].toString();
							String approverMail = (approverTwoDataItem[2] == null) ? null
									: approverTwoDataItem[2].toString();
							String employeeName = (approverTwoDataItem[4] == null) ? null
									: approverTwoDataItem[4].toString();
							String approverFullName = (approverTwoDataItem[5] == null) ? null
									: approverTwoDataItem[5].toString();
							String status = (approverTwoDataItem[0] == null) ? null : approverTwoDataItem[0].toString();
							Map<String, ArrayList<String>> approverProjectMap = new HashMap<String, ArrayList<String>>();

							if ((!status.equals(Constants.TASKTRACK_APPROVER_STATUS_SUBMIT))
									&& (!status.equals(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED))
									&& (!status.equals(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED))
									&& (!status.equals(Constants.TASKTRACK_APPROVER_STATUS_LOCK))) {
								if (!approverMailMap.containsKey(approverName))
									approverMailMap.put(approverName, approverMail);
								if (!approverFullNameMap.containsKey(approverName))
									approverFullNameMap.put(approverName, approverFullName);
								if (approverMap.containsKey(approverName)) {
									approverProjectMap = (Map<String, ArrayList<String>>) approverMap.get(approverName);
									if (approverProjectMap.containsKey(projectName)) {
										ArrayList<String> employeeList = (ArrayList<String>) approverProjectMap
												.get(projectName);
										employeeList.add(employeeName);
										approverProjectMap.put(projectName, employeeList);

									} else {
										approverProjectMap.put(projectName,
												new ArrayList<>(Arrays.asList(employeeName)));
									}
								} else {
									approverProjectMap.put(projectName, new ArrayList<>(Arrays.asList(employeeName)));
								}
								approverMap.put(approverName, approverProjectMap);
							}

						}
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Iterator it = approverMap.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();

						Map projectMap = new HashMap<String, ArrayList<String>>();
						projectMap = (Map) entry.getValue();

						String userName = entry.getKey().toString();
						List<String> projectList = (List<String>) projectMap.keySet().stream()
								.collect(Collectors.toList());

						if (projectList != null && projectList.size() > 0) {
							for (String projectName : projectList) {
								ArrayList<String> userList = (ArrayList<String>) projectMap.get(projectName);
								StringBuilder sb = new StringBuilder();
								if (userList != null && userList.size() > 0) {

									for (String user : userList) {

										sb.append(" * " + user + "<br/>");
									}

									MailDomainDto mailDomainDto = new MailDomainDto();
									mailDomainDto.setSubject("Approve & Submit Time Sheet");

									StringBuilder mailBody = new StringBuilder(
											"Hi " + approverFullNameMap.get(userName) + ", ");

									String preData = "";
									if (previousmonthdata.containsKey(userName)) {
										PreviousMonthMailDto previousData = previousmonthdata.get(userName);

										if (previousData.getMailContent().containsKey(projectName)) {

											preData = previousData.getMailContent().get(projectName).toString();
//											mailBody.append(preData);
											previousData.getMailContent().remove(projectName);
											previousmonthdata.put(userName, previousData);
										}

									}

									String monthname = new DateFormatSymbols().getMonths()[month-1];
									mailBody.append("<br/><br/>Project Name : " + projectName);
									mailBody.append(preData);
									mailBody.append("<br/><br/><b>" + monthname
											+ "</b> First half task track approval is pending for the following users: <br/>"
											+ sb);

									Template t = freemarkerConfig.getTemplate("email_template.ftl");
									String html = (FreeMarkerTemplateUtils.processTemplateIntoString(t, mailDomainDto))
											.replace("MAIL_BODY", mailBody)
											.replace("Title", "Approve & Submit Time Sheet !");

									mailDomainDto.setMailBody(html);
									mailDomainDto.setTo(approverMailMap.get(userName).toString());
									String token = UUID.randomUUID().toString();
									String msg = emailNotificationService.sendMail(token, mailDomainDto, false);
								}
							}
						}
					}

					Iterator itrator = previousmonthdata.entrySet().iterator();
					while (itrator.hasNext()) {
						Map.Entry entry = (Map.Entry) itrator.next();

						String userName = entry.getKey().toString();

						if (previousmonthdata.containsKey(userName)) {

							PreviousMonthMailDto previousProjects = previousmonthdata.get(userName);
							if (previousProjects != null) {

								String approverMail = previousProjects.getEmail();
								String approverFullname = previousProjects.getApproverFullName();

								MailDomainDto mailDomainDto = new MailDomainDto();
								mailDomainDto.setSubject("Approve & Submit Time Sheet");

								StringBuilder mailBody = new StringBuilder(
										"Hi " + approverFullNameMap.get(userName) + ", ");

								Map<String, StringBuilder> previousProjectsList = previousProjects.getMailContent();

								if (previousProjectsList.size() > 0) {

									Iterator previousprojectitrator = previousProjectsList.entrySet().iterator();
									while (previousprojectitrator.hasNext()) {
										Map.Entry projectentry = (Map.Entry) previousprojectitrator.next();

										String projectname = projectentry.getKey().toString();
										String projectContent = projectentry.getValue().toString();

										mailBody = new StringBuilder("Hi " + approverFullname + ", ");
										mailBody.append("<br/><br/>Project Name : " + projectname);

										mailBody.append(projectContent);

										Template t = freemarkerConfig.getTemplate("email_template.ftl");
										String html = (FreeMarkerTemplateUtils.processTemplateIntoString(t,
												mailDomainDto)).replace("MAIL_BODY", mailBody).replace("Title",
														"Approve & Submit Time Sheet !");

										mailDomainDto.setMailBody(html);
										mailDomainDto.setTo(approverMail);
										String token = UUID.randomUUID().toString();
										String msg = emailNotificationService.sendMail(token, mailDomainDto, false);
									}

								}

							}

						}
					}
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
//	//@Scheduled(cron = "* * * * * *")
	@Scheduled(cron = "#{getCronForApproverTwoSecondHalf}")
	public void approverTwoSecondHalfScheduler() throws Exception {

		Date date = null, croneDate = null;
		LocalDateTime now = LocalDateTime.now();
		TimeZone zone = TimeZone.getTimeZone("MST");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		formatter.setTimeZone(zone);
		date = formatter.parse(now.toString());
		System.out.println("Starting ApproverTwoSecondHalfScheduler...");
		Map approverMap = new HashMap<String, Map<String, ArrayList<String>>>();
		Map approverMailMap = new HashMap<String, String>();
		Map approverFullNameMap = new HashMap<String, String>();

//		List<TaskTrackCronModel> dates = cronRepo.getCronDates();
		List<Integer> dates = cronDateRepository.getapproverTwoSecondHalfSchedulerDate();

		if (dates != null && dates.size() > 0) {
			for (Integer item : dates) {
//				croneDate = formatter.parse(item.getCronDate().toString());

				int year = LocalDate.now().getYear();
				int currentMonth = LocalDate.now().getMonthValue();
				String cronDate = year + "-" + currentMonth + "-" + item;
				if (formatter.parse(cronDate).equals(date)) {
					Integer month = now.getMonthValue();

					Map<String, PreviousMonthMailDto> previousmonthdata = getApproverOnePreviousMonthData(
							now.getMonthValue() - 1, 2, 2);

					List<Object[]> approverTwoDataList = taskRepo.getApproverTwoSecondHalfInfo(month);

					if (approverTwoDataList != null && approverTwoDataList.size() > 0) {
						for (Object[] approverTwoDataItem : approverTwoDataList) {
							String approverName = (approverTwoDataItem[1] == null) ? null
									: approverTwoDataItem[1].toString();
							String projectName = (approverTwoDataItem[3] == null) ? null
									: approverTwoDataItem[3].toString();
							String approverMail = (approverTwoDataItem[2] == null) ? null
									: approverTwoDataItem[2].toString();
							String employeeName = (approverTwoDataItem[4] == null) ? null
									: approverTwoDataItem[4].toString();
							String approverFullName = (approverTwoDataItem[5] == null) ? null
									: approverTwoDataItem[5].toString();
							String status = (approverTwoDataItem[0] == null) ? null : approverTwoDataItem[0].toString();
							Map<String, ArrayList<String>> approverProjectMap = new HashMap<String, ArrayList<String>>();

							if ((!status.equals(Constants.TASKTRACK_FINAL_STATUS_SUBMIT))
									&& (!status.equals(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED))
									&& (!status.equals(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED))
									&& (!status.equals(Constants.TASKTRACK_APPROVER_STATUS_LOCK))) {
								if (!approverMailMap.containsKey(approverName))
									approverMailMap.put(approverName, approverMail);
								if (!approverFullNameMap.containsKey(approverName))
									approverFullNameMap.put(approverName, approverFullName);
								if (approverMap.containsKey(approverName)) {
									approverProjectMap = (Map<String, ArrayList<String>>) approverMap.get(approverName);
									if (approverProjectMap.containsKey(projectName)) {
										ArrayList<String> employeeList = (ArrayList<String>) approverProjectMap
												.get(projectName);
										employeeList.add(employeeName);
										approverProjectMap.put(projectName, employeeList);

									} else {
										approverProjectMap.put(projectName,
												new ArrayList<>(Arrays.asList(employeeName)));
									}
								} else {
									approverProjectMap.put(projectName, new ArrayList<>(Arrays.asList(employeeName)));
								}
								approverMap.put(approverName, approverProjectMap);
							}

						}
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Iterator it = approverMap.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();

						Map projectMap = new HashMap<String, ArrayList<String>>();
						projectMap = (Map) entry.getValue();

						String userName = entry.getKey().toString();
						List<String> projectList = (List<String>) projectMap.keySet().stream()
								.collect(Collectors.toList());

						if (projectList != null && projectList.size() > 0) {
							for (String projectName : projectList) {
								ArrayList<String> userList = (ArrayList<String>) projectMap.get(projectName);
								StringBuilder sb = new StringBuilder();
								if (userList != null && userList.size() > 0) {

									for (String user : userList) {
//										if (sb.length() != 0)
//											sb.append(", ");
										sb.append(" * " + user + "<br/>");
									}

									MailDomainDto mailDomainDto = new MailDomainDto();
									mailDomainDto.setSubject("Approve & Submit Time Sheet");

									StringBuilder mailBody = new StringBuilder(
											"Hi " + approverFullNameMap.get(userName) + ", ");

									String preData = "";
									if (previousmonthdata.containsKey(userName)) {
										PreviousMonthMailDto previousData = previousmonthdata.get(userName);

										if (previousData.getMailContent().containsKey(projectName)) {

											preData = previousData.getMailContent().get(projectName).toString();
//											mailBody.append(preData);
											previousData.getMailContent().remove(projectName);
											previousmonthdata.put(userName, previousData);
										}

									}

									String monthname = new DateFormatSymbols().getMonths()[month-1];
									mailBody.append("<br/><br/>Project Name : " + projectName);
									mailBody.append(preData);
									mailBody.append("<br/><br/><b>" + monthname
											+ "</b> Second half task track approval is pending for the following users: <br/>"
											+ sb);

									Template t = freemarkerConfig.getTemplate("email_template.ftl");
									String html = (FreeMarkerTemplateUtils.processTemplateIntoString(t, mailDomainDto))
											.replace("MAIL_BODY", mailBody)
											.replace("Title", "Approve & Submit Time Sheet !");

									mailDomainDto.setMailBody(html);
									mailDomainDto.setTo(approverMailMap.get(userName).toString());
									String token = UUID.randomUUID().toString();
									String msg = emailNotificationService.sendMail(token, mailDomainDto, false);
								}
							}
						}
					}

					Iterator itrator = previousmonthdata.entrySet().iterator();
					while (itrator.hasNext()) {
						Map.Entry entry = (Map.Entry) itrator.next();

						String userName = entry.getKey().toString();

						if (previousmonthdata.containsKey(userName)) {

							PreviousMonthMailDto previousProjects = previousmonthdata.get(userName);
							if (previousProjects != null) {

								String approverMail = previousProjects.getEmail();
								String approverFullname = previousProjects.getApproverFullName();

								MailDomainDto mailDomainDto = new MailDomainDto();
								mailDomainDto.setSubject("Approve & Submit Time Sheet");

								StringBuilder mailBody = new StringBuilder(
										"Hi " + approverFullNameMap.get(userName) + ", ");

								Map<String, StringBuilder> previousProjectsList = previousProjects.getMailContent();

								if (previousProjectsList.size() > 0) {

									Iterator previousprojectitrator = previousProjectsList.entrySet().iterator();
									while (previousprojectitrator.hasNext()) {
										Map.Entry projectentry = (Map.Entry) previousprojectitrator.next();

										String projectname = projectentry.getKey().toString();
										String projectContent = projectentry.getValue().toString();

										mailBody = new StringBuilder("Hi " + approverFullname + ", ");
										mailBody.append("<br/><br/>Project Name : " + projectname);

										mailBody.append(projectContent);

										Template t = freemarkerConfig.getTemplate("email_template.ftl");
										String html = (FreeMarkerTemplateUtils.processTemplateIntoString(t,
												mailDomainDto)).replace("MAIL_BODY", mailBody).replace("Title",
														"Approve & Submit Time Sheet !");

										mailDomainDto.setMailBody(html);
										mailDomainDto.setTo(approverMail);
										String token = UUID.randomUUID().toString();
										String msg = emailNotificationService.sendMail(token, mailDomainDto, false);
									}

								}

							}

						}
					}

				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused", "null" })
//	//@Scheduled(cron = "* * * * * *")
	@Scheduled(cron = "#{getCronForApproverOneFirstHalf}")
	public void approverOneFirstHalfScheduler() throws Exception {

		Date date = null, croneDate = null;
		LocalDateTime now = LocalDateTime.now();
		TimeZone zone = TimeZone.getTimeZone("MST");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		formatter.setTimeZone(zone);
		date = formatter.parse(now.toString());
		System.out.println("Starting ApproverOneFirstHalfScheduler...");
		Map approverMap = new HashMap<String, Map<String, ArrayList<String>>>();
		Map approverMailMap = new HashMap<String, String>();
		Map approverFullNameMap = new HashMap<String, String>();

//		List<TaskTrackCronModel> dates = cronRepo.getCronDates();
		List<Integer> dates = cronDateRepository.getapproverOneFirstHalfSchedulerDate();

		if (dates != null && dates.size() > 0) {
			for (Integer item : dates) {
//				croneDate = formatter.parse(item.getCronDate().toString());

				int year = LocalDate.now().getYear();
				int currentMonth = LocalDate.now().getMonthValue();
				String cronDate = year + "-" + currentMonth + "-" + item;
				System.out.println(formatter.parse(cronDate));
				if (formatter.parse(cronDate).equals(date)) {
					Integer month = now.getMonthValue();

					Map<String, PreviousMonthMailDto> previousmonthdata = getApproverOnePreviousMonthData(
							now.getMonthValue() - 1, 1, 1);

					List<Object[]> approverOneDataList = taskRepo.getApproverOneFirstHalfInfo(month);

					if (approverOneDataList != null && approverOneDataList.size() > 0) {
						for (Object[] approverOneDataItem : approverOneDataList) {
							String approverName = (approverOneDataItem[1] == null) ? null
									: approverOneDataItem[1].toString();
							String projectName = (approverOneDataItem[3] == null) ? null
									: approverOneDataItem[3].toString();
							String approverMail = (approverOneDataItem[2] == null) ? null
									: approverOneDataItem[2].toString();
							String employeeName = (approverOneDataItem[4] == null) ? null
									: approverOneDataItem[4].toString();
							String approverFullName = (approverOneDataItem[5] == null) ? null
									: approverOneDataItem[5].toString();
							String status = (approverOneDataItem[0] == null) ? null : approverOneDataItem[0].toString();
							Map<String, ArrayList<String>> approverProjectMap = new HashMap<String, ArrayList<String>>();

							if ((!status.equals(Constants.TASKTRACK_FINAL_STATUS_SUBMIT))
									&& (!status.equals(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED))
									&& (!status.equals(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED))
									&& (!status.equals(Constants.TASKTRACK_APPROVER_STATUS_LOCK))) {
								if (!approverMailMap.containsKey(approverName))
									approverMailMap.put(approverName, approverMail);
								if (!approverFullNameMap.containsKey(approverName))
									approverFullNameMap.put(approverName, approverFullName);
								if (approverMap.containsKey(approverName)) {
									approverProjectMap = (Map<String, ArrayList<String>>) approverMap.get(approverName);
									if (approverProjectMap.containsKey(projectName)) {
										ArrayList<String> employeeList = (ArrayList<String>) approverProjectMap
												.get(projectName);
										employeeList.add(employeeName);
										approverProjectMap.put(projectName, employeeList);

									} else {
										approverProjectMap.put(projectName,
												new ArrayList<>(Arrays.asList(employeeName)));
									}
								} else {
									approverProjectMap.put(projectName, new ArrayList<>(Arrays.asList(employeeName)));
								}
								approverMap.put(approverName, approverProjectMap);
							}

						}
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Iterator it = approverMap.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();

						Map projectMap = new HashMap<String, ArrayList<String>>();
						projectMap = (Map) entry.getValue();

						String userName = entry.getKey().toString();
						List<String> projectList = (List<String>) projectMap.keySet().stream()
								.collect(Collectors.toList());

						if (projectList != null && projectList.size() > 0) {
							for (String projectName : projectList) {
								ArrayList<String> userList = (ArrayList<String>) projectMap.get(projectName);
								StringBuilder sb = new StringBuilder();
								if (userList != null && userList.size() > 0) {

									for (String user : userList) {
//										if (sb.length() != 0)
//											sb.append(", ");
										sb.append(" * " + user + "<br/>");
									}

									MailDomainDto mailDomainDto = new MailDomainDto();
									mailDomainDto.setSubject("Approve & Submit Time Sheet");

									StringBuilder mailBody = new StringBuilder(
											"Hi " + approverFullNameMap.get(userName) + ", ");

									String preData = "";
									if (previousmonthdata.containsKey(userName)) {
										PreviousMonthMailDto previousData = previousmonthdata.get(userName);

										if (previousData.getMailContent().containsKey(projectName)) {

											preData = previousData.getMailContent().get(projectName).toString();
//											mailBody.append(preData);
											previousData.getMailContent().remove(projectName);
											previousmonthdata.put(userName, previousData);
										}

									}

									String monthname = new DateFormatSymbols().getMonths()[month-1];
									mailBody.append("<br/><br/>Project Name : " + projectName);
									mailBody.append(preData);
									mailBody.append("<br/><br/><b>" + monthname
											+ "</b> First half task track approval is pending for the following users: <br/>"
											+ sb);

									Template t = freemarkerConfig.getTemplate("email_template.ftl");
									String html = (FreeMarkerTemplateUtils.processTemplateIntoString(t, mailDomainDto))
											.replace("MAIL_BODY", mailBody)
											.replace("Title", "Approve & Submit Time Sheet !");

									mailDomainDto.setMailBody(html);
									mailDomainDto.setTo(approverMailMap.get(userName).toString());
									String token = UUID.randomUUID().toString();
									String msg = emailNotificationService.sendMail(token, mailDomainDto, false);
								}
							}
						}
					}

					Iterator itrator = previousmonthdata.entrySet().iterator();
					while (itrator.hasNext()) {
						Map.Entry entry = (Map.Entry) itrator.next();

						String userName = entry.getKey().toString();

						if (previousmonthdata.containsKey(userName)) {

							PreviousMonthMailDto previousProjects = previousmonthdata.get(userName);
							if (previousProjects != null) {

								String approverMail = previousProjects.getEmail();
								String approverFullname = previousProjects.getApproverFullName();

								MailDomainDto mailDomainDto = new MailDomainDto();
								mailDomainDto.setSubject("Approve & Submit Time Sheet");

								StringBuilder mailBody = new StringBuilder(
										"Hi " + approverFullNameMap.get(userName) + ", ");

								Map<String, StringBuilder> previousProjectsList = previousProjects.getMailContent();

								if (previousProjectsList.size() > 0) {

									Iterator previousprojectitrator = previousProjectsList.entrySet().iterator();
									while (previousprojectitrator.hasNext()) {
										Map.Entry projectentry = (Map.Entry) previousprojectitrator.next();

										String projectname = projectentry.getKey().toString();
										String projectContent = projectentry.getValue().toString();

										mailBody = new StringBuilder("Hi " + approverFullname + ", ");
										mailBody.append("<br/><br/>Project Name : " + projectname);

										mailBody.append(projectContent);

										Template t = freemarkerConfig.getTemplate("email_template.ftl");
										String html = (FreeMarkerTemplateUtils.processTemplateIntoString(t,
												mailDomainDto)).replace("MAIL_BODY", mailBody).replace("Title",
														"Approve & Submit Time Sheet !");

										mailDomainDto.setMailBody(html);
										mailDomainDto.setTo(approverMail);
										String token = UUID.randomUUID().toString();
										String msg = emailNotificationService.sendMail(token, mailDomainDto, false);
									}

								}

							}

						}
					}

				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	@Scheduled(cron = "#{getCronForApproverOneSecondHalf}")
	public void approverOneSecondHalfScheduler() throws Exception {

		Date date = null, croneDate = null;
		LocalDateTime now = LocalDateTime.now();
		TimeZone zone = TimeZone.getTimeZone("MST");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		formatter.setTimeZone(zone);
		date = formatter.parse(now.toString());
		System.out.println("Starting ApproverOneSecondHalfScheduler...");
		Map approverMap = new HashMap<String, Map<String, ArrayList<String>>>();
		Map approverMailMap = new HashMap<String, String>();
		Map approverFullNameMap = new HashMap<String, String>();

//		List<TaskTrackCronModel> dates = cronRepo.getCronDates();
		List<Integer> dates = cronDateRepository.getapproverOneSecondHalfSchedulerDate();

		if (dates != null && dates.size() > 0) {
			for (Integer item : dates) {
//				croneDate = formatter.parse(item.getCronDate().toString());

				int year = LocalDate.now().getYear();
				int currentMonth = LocalDate.now().getMonthValue();
				String cronDate = year + "-" + currentMonth + "-" + item;
				System.out.println(formatter.parse(cronDate));

				if (formatter.parse(cronDate).equals(date)) {
//				if (croneDate.equals(date)) {

					Integer month = now.getMonthValue();

					Map<String, PreviousMonthMailDto> previousmonthdata = getApproverOnePreviousMonthData(
							now.getMonthValue() - 1, 2, 1);

					List<Object[]> approveroneDataList = taskRepo.getApproverOneSecondHalfInfo(month);

					if (approveroneDataList != null && approveroneDataList.size() > 0) {
						for (Object[] approverOneDataItem : approveroneDataList) {
							String approverName = (approverOneDataItem[1] == null) ? null
									: approverOneDataItem[1].toString();
							String projectName = (approverOneDataItem[3] == null) ? null
									: approverOneDataItem[3].toString();
							String approverMail = (approverOneDataItem[2] == null) ? null
									: approverOneDataItem[2].toString();
							String employeeName = (approverOneDataItem[4] == null) ? null
									: approverOneDataItem[4].toString();
							String approverFullName = (approverOneDataItem[5] == null) ? null
									: approverOneDataItem[5].toString();
							String status = (approverOneDataItem[0] == null) ? null : approverOneDataItem[0].toString();
							Map<String, ArrayList<String>> approverProjectMap = new HashMap<String, ArrayList<String>>();

							if ((!status.equals(Constants.TASKTRACK_FINAL_STATUS_SUBMIT))
									&& (!status.equals(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED))
									&& (!status.equals(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED))
									&& (!status.equals(Constants.TASKTRACK_APPROVER_STATUS_LOCK))) {
								if (!approverMailMap.containsKey(approverName))
									approverMailMap.put(approverName, approverMail);
								if (!approverFullNameMap.containsKey(approverName))
									approverFullNameMap.put(approverName, approverFullName);
								if (approverMap.containsKey(approverName)) {
									approverProjectMap = (Map<String, ArrayList<String>>) approverMap.get(approverName);
									if (approverProjectMap.containsKey(projectName)) {
										ArrayList<String> employeeList = (ArrayList<String>) approverProjectMap
												.get(projectName);
										employeeList.add(employeeName);
										approverProjectMap.put(projectName, employeeList);

									} else {
										approverProjectMap.put(projectName,
												new ArrayList<>(Arrays.asList(employeeName)));
									}
								} else {
									approverProjectMap.put(projectName, new ArrayList<>(Arrays.asList(employeeName)));
								}
								approverMap.put(approverName, approverProjectMap);
							}

						}
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Iterator it = approverMap.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();

						Map projectMap = new HashMap<String, ArrayList<String>>();
						projectMap = (Map) entry.getValue();

						String userName = entry.getKey().toString();

						List<String> projectList = (List<String>) projectMap.keySet().stream()
								.collect(Collectors.toList());

						if (projectList != null && projectList.size() > 0) {
							for (String projectName : projectList) {
								ArrayList<String> userList = (ArrayList<String>) projectMap.get(projectName);
								StringBuilder sb = new StringBuilder();
								if (userList != null && userList.size() > 0) {

									for (String user : userList) {
//										if (sb.length() != 0)
//											sb.append(", ");
										sb.append(" * " + user + "<br/>");
									}

									MailDomainDto mailDomainDto = new MailDomainDto();
									mailDomainDto.setSubject("Approve & Submit Time Sheet");

									StringBuilder mailBody = new StringBuilder(
											"Hi " + approverFullNameMap.get(userName) + ", ");
									String preData = "";
									if (previousmonthdata.containsKey(userName)) {
										PreviousMonthMailDto previousData = previousmonthdata.get(userName);

										if (previousData.getMailContent().containsKey(projectName)) {

//											mailBody = new StringBuilder(
//													"Hi " + previousData.getApproverFullName() + ", ");

											preData = previousData.getMailContent().get(projectName).toString();
//											mailBody.append(preData);
											previousData.getMailContent().remove(projectName);
											previousmonthdata.put(userName, previousData);
										}

									}

									String monthname = new DateFormatSymbols().getMonths()[month-1];
									mailBody.append("<br/><br/>Project Name : " + projectName);
									mailBody.append(preData);
									mailBody.append("<br/><br/><b>" + monthname
											+ "</b> Second half task track approval is pending for the following users: <br/>"
											+ sb);

									Template t = freemarkerConfig.getTemplate("email_template.ftl");
									String html = (FreeMarkerTemplateUtils.processTemplateIntoString(t, mailDomainDto))
											.replace("MAIL_BODY", mailBody)
											.replace("Title", "Approve & Submit Time Sheet !");

									mailDomainDto.setMailBody(html);
									mailDomainDto.setTo(approverMailMap.get(userName).toString());
									String token = UUID.randomUUID().toString();
									String msg = emailNotificationService.sendMail(token, mailDomainDto, false);
								}
							}
						}

					}
					Iterator itrator = previousmonthdata.entrySet().iterator();
					while (itrator.hasNext()) {
						Map.Entry entry = (Map.Entry) itrator.next();

						String userName = entry.getKey().toString();

						if (previousmonthdata.containsKey(userName)) {

							PreviousMonthMailDto previousProjects = previousmonthdata.get(userName);
							if (previousProjects != null) {

								String approverMail = previousProjects.getEmail();
								String approverFullname = previousProjects.getApproverFullName();

								MailDomainDto mailDomainDto = new MailDomainDto();
								mailDomainDto.setSubject("Approve & Submit Time Sheet");

								StringBuilder mailBody = new StringBuilder(
										"Hi " + approverFullNameMap.get(userName) + ", ");

								Map<String, StringBuilder> previousProjectsList = previousProjects.getMailContent();

								if (previousProjectsList.size() > 0) {

									Iterator previousprojectitrator = previousProjectsList.entrySet().iterator();
									while (previousprojectitrator.hasNext()) {
										Map.Entry projectentry = (Map.Entry) previousprojectitrator.next();

										String projectname = projectentry.getKey().toString();
										String projectContent = projectentry.getValue().toString();

										mailBody = new StringBuilder("Hi " + approverFullname + ", ");
										mailBody.append("<br/><br/>Project Name : " + projectname);

										mailBody.append(projectContent);

										Template t = freemarkerConfig.getTemplate("email_template.ftl");
										String html = (FreeMarkerTemplateUtils.processTemplateIntoString(t,
												mailDomainDto)).replace("MAIL_BODY", mailBody).replace("Title",
														"Approve & Submit Time Sheet !");

										mailDomainDto.setMailBody(html);
										mailDomainDto.setTo(approverMail);
										String token = UUID.randomUUID().toString();
										String msg = emailNotificationService.sendMail(token, mailDomainDto, false);
									}

								}

							}

						}
					}
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, PreviousMonthMailDto> getApproverOnePreviousMonthData(int month, int indexKey,
			int approverKey) {

		List<Object[]> approveroneDataList = new ArrayList<Object[]>();
		List<Object[]> currentmonthFirstHalfData = new ArrayList<Object[]>();

		if (approverKey == 1) {
			approveroneDataList = taskRepo.getApproverOnePreviousMonthInfo(month);

		}

		if (approverKey == 2) {
			approveroneDataList = taskRepo.getApproverTwoPreviousMonthData(month);
		}

		if ((indexKey == 2) && (approverKey == 1)) {
			currentmonthFirstHalfData = taskRepo.getApproverOneFirstHalfInfo(month + 1);
		}

		if ((indexKey == 2) && (approverKey == 2)) {
			currentmonthFirstHalfData = taskRepo.getApproverTwoFirstHalfInfo(month + 1);
		}

		Map currentMonthDataMap = new HashMap<String, Map<String, ArrayList<String>>>();
		Map approverFirstHalfMap = new HashMap<String, Map<String, ArrayList<String>>>();
		Map approverSecondHalfMap = new HashMap<String, Map<String, ArrayList<String>>>();
		Map approverMailMap = new HashMap<String, String>();
		Map approverFullNameMap = new HashMap<String, String>();
		Map previousMonthDataMap = new HashMap<String, PreviousMonthMailDto>();

		Map mailContent = new HashMap<String, StringBuilder>();
		////

		/*
		 * @Author haritha
		 */

		if (currentmonthFirstHalfData != null && currentmonthFirstHalfData.size() > 0) {
			for (Object[] currentMonthDataItem : currentmonthFirstHalfData) {
				String approverName = (currentMonthDataItem[1] == null) ? null : currentMonthDataItem[1].toString();
				String projectName = (currentMonthDataItem[3] == null) ? null : currentMonthDataItem[3].toString();
				String approverMail = (currentMonthDataItem[2] == null) ? null : currentMonthDataItem[2].toString();
				String employeeName = (currentMonthDataItem[4] == null) ? null : currentMonthDataItem[4].toString();
				String approverFullName = (currentMonthDataItem[5] == null) ? null : currentMonthDataItem[5].toString();
				String firstHalfStatus = (currentMonthDataItem[0] == null) ? null : currentMonthDataItem[0].toString();
				String projecttier = null;

				if (approverKey == 1) {
					projecttier = (currentMonthDataItem[6] == null) ? null : currentMonthDataItem[6].toString();
				}

				Map<String, ArrayList<String>> approverFirstHalfProjectMap = new HashMap<String, ArrayList<String>>();

				if ((!firstHalfStatus.equals(Constants.TASKTRACK_FINAL_STATUS_SUBMIT))
						&& (!firstHalfStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED))
						&& (!firstHalfStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED))
						&& (!firstHalfStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_LOCK))) {
					if (!approverMailMap.containsKey(approverName))
						approverMailMap.put(approverName, approverMail);
					if (!approverFullNameMap.containsKey(approverName))
						approverFullNameMap.put(approverName, approverFullName);
					if (currentMonthDataMap.containsKey(approverName)) {
						approverFirstHalfProjectMap = (Map<String, ArrayList<String>>) currentMonthDataMap
								.get(approverName);
						if (approverFirstHalfProjectMap.containsKey(projectName)) {
							ArrayList<String> employeeList = (ArrayList<String>) approverFirstHalfProjectMap
									.get(projectName);
							employeeList.add(employeeName);
							approverFirstHalfProjectMap.put(projectName, employeeList);

						} else {
							approverFirstHalfProjectMap.put(projectName, new ArrayList<>(Arrays.asList(employeeName)));
						}
					} else {
						approverFirstHalfProjectMap.put(projectName, new ArrayList<>(Arrays.asList(employeeName)));
					}
					currentMonthDataMap.put(approverName, approverFirstHalfProjectMap);
				}

			}
		}

		if (approveroneDataList != null && approveroneDataList.size() > 0) {
			for (Object[] approverOneDataItem : approveroneDataList) {
				String approverName = (approverOneDataItem[2] == null) ? null : approverOneDataItem[2].toString();
				String projectName = (approverOneDataItem[4] == null) ? null : approverOneDataItem[4].toString();
				String approverMail = (approverOneDataItem[3] == null) ? null : approverOneDataItem[3].toString();
				String employeeName = (approverOneDataItem[5] == null) ? null : approverOneDataItem[5].toString();
				String approverFullName = (approverOneDataItem[6] == null) ? null : approverOneDataItem[6].toString();
				String firstHalfStatus = (approverOneDataItem[0] == null) ? null : approverOneDataItem[0].toString();
				String secondHalfStatus = (approverOneDataItem[1] == null) ? null : approverOneDataItem[1].toString();

				Map<String, ArrayList<String>> approverFirstHalfProjectMap = new HashMap<String, ArrayList<String>>();
				Map<String, ArrayList<String>> approverSecondHalfProjectMap = new HashMap<String, ArrayList<String>>();

				if ((!firstHalfStatus.equals(Constants.TASKTRACK_FINAL_STATUS_SUBMIT))
						&& (!firstHalfStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED))
						&& (!firstHalfStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED))
						&& (!firstHalfStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_LOCK))) {
					if (!approverMailMap.containsKey(approverName))
						approverMailMap.put(approverName, approverMail);
					if (!approverFullNameMap.containsKey(approverName))
						approverFullNameMap.put(approverName, approverFullName);
					if (approverFirstHalfMap.containsKey(approverName)) {
						approverFirstHalfProjectMap = (Map<String, ArrayList<String>>) approverFirstHalfMap
								.get(approverName);
						if (approverFirstHalfProjectMap.containsKey(projectName)) {
							ArrayList<String> employeeList = (ArrayList<String>) approverFirstHalfProjectMap
									.get(projectName);
							employeeList.add(employeeName);
							approverFirstHalfProjectMap.put(projectName, employeeList);

						} else {
							approverFirstHalfProjectMap.put(projectName, new ArrayList<>(Arrays.asList(employeeName)));
						}
					} else {
						approverFirstHalfProjectMap.put(projectName, new ArrayList<>(Arrays.asList(employeeName)));
					}
					approverFirstHalfMap.put(approverName, approverFirstHalfProjectMap);
				}

				if ((!secondHalfStatus.equals(Constants.TASKTRACK_FINAL_STATUS_SUBMIT))
						&& (!secondHalfStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_REJECTION_SUBMITTED))
						&& (!secondHalfStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_CORRECTED))
						&& (!secondHalfStatus.equals(Constants.TASKTRACK_APPROVER_STATUS_LOCK))) {
					if (!approverMailMap.containsKey(approverName))
						approverMailMap.put(approverName, approverMail);
					if (!approverFullNameMap.containsKey(approverName))
						approverFullNameMap.put(approverName, approverFullName);
					if (approverSecondHalfMap.containsKey(approverName)) {
						approverSecondHalfProjectMap = (Map<String, ArrayList<String>>) approverSecondHalfMap
								.get(approverName);
						if (approverSecondHalfProjectMap.containsKey(projectName)) {
							ArrayList<String> employeeList = (ArrayList<String>) approverSecondHalfProjectMap
									.get(projectName);
							employeeList.add(employeeName);
							approverSecondHalfProjectMap.put(projectName, employeeList);

						} else {
							approverSecondHalfProjectMap.put(projectName, new ArrayList<>(Arrays.asList(employeeName)));
						}
					} else {
						approverSecondHalfProjectMap.put(projectName, new ArrayList<>(Arrays.asList(employeeName)));
					}
					approverSecondHalfMap.put(approverName, approverSecondHalfProjectMap);
				}

			}
		}

		Iterator it = approverFirstHalfMap.entrySet().iterator();
		while (it.hasNext()) {
//			StringBuilder previousMonthData = new StringBuilder();
			Map.Entry entry = (Map.Entry) it.next();

			Map projectMap = new HashMap<String, ArrayList<String>>();
			projectMap = (Map) entry.getValue();

			String userName = entry.getKey().toString();

			List<String> projectList = (List<String>) projectMap.keySet().stream().collect(Collectors.toList());

			if (projectList != null && projectList.size() > 0) {
				for (String projectName : projectList) {
					ArrayList<String> userList = (ArrayList<String>) projectMap.get(projectName);
					StringBuilder previousMonthData = new StringBuilder();

					StringBuilder sb = new StringBuilder();
					if (userList != null && userList.size() > 0) {

						for (String user : userList) {
//							if (sb.length() != 0)
//								sb.append(", ");
							sb.append(" * " + user + "<br/>");
						}

						String monthname = new DateFormatSymbols().getMonths()[month - 1];
//						previousMonthData.append("<br/><br/>Project Name : " + projectName);
						previousMonthData.append("<br/><br/><b>" + monthname
								+ "</b> First half task track approval is pending for the following users: <br/>"
								+ sb);

						if (!mailContent.containsKey(projectName)) {
							mailContent.put(projectName, previousMonthData);
						}

//						if(previousMonthDataMap.containsKey(userName)){
//							Map<String, StringBuilder> preProjMap = (Map<String, StringBuilder>) previousMonthDataMap.get(userName);
//							if(!preProjMap.containsKey(projectName)) {
//								
//							}
//							
////							previousMonthDataMap.put(userName, content);
//						}
//						else {
//							previousMonthDataMap.put(userName, mailContent);
//						}
//							 
					}
				}
			}
			PreviousMonthMailDto mailDto = new PreviousMonthMailDto();
			mailDto.setEmail(approverMailMap.get(userName).toString());
			mailDto.setApproverFullName(approverFullNameMap.get(userName).toString());
			mailDto.setMailContent(mailContent);
			previousMonthDataMap.put(userName, mailDto);

//			if(previousMonthDataMap.containsKey(userName)){
//				StringBuilder content = (StringBuilder) previousMonthDataMap.get(userName);
//				content.append(previousMonthData);
//				previousMonthDataMap.put(userName, content);
//			}
//			else {
//				previousMonthDataMap.put(userName, previousMonthData);
//			}

		}

		Iterator itr = approverSecondHalfMap.entrySet().iterator();
		while (itr.hasNext()) {

			Map.Entry entry = (Map.Entry) itr.next();
			Map projectMap = new HashMap<String, ArrayList<String>>();
			projectMap = (Map) entry.getValue();

			String userName = entry.getKey().toString();

			List<String> projectList = (List<String>) projectMap.keySet().stream().collect(Collectors.toList());

			if (projectList != null && projectList.size() > 0) {
				for (String projectName : projectList) {

					StringBuilder previousMonthData = new StringBuilder();
					ArrayList<String> userList = (ArrayList<String>) projectMap.get(projectName);
					StringBuilder sb = new StringBuilder();
					if (userList != null && userList.size() > 0) {

						for (String user : userList) {
//							if (sb.length() != 0)
//								sb.append(", ");
							sb.append("* " + user + "<br/>");
						}

						String monthname = new DateFormatSymbols().getMonths()[month - 1];
						if (!mailContent.containsKey(projectName)) {
//							previousMonthData.append("<br/><br/>Project Name : " + projectName);
							previousMonthData.append("<br/><br/><b>" + monthname
									+ "</b> Second half task track approval is pending for the following users: <br/>"
									+ sb);
							mailContent.put(projectName, previousMonthData);
						} else {
							String value = mailContent.get(projectName).toString();
							previousMonthData.append(value + "<br/><br/><b>" + monthname
									+ "</b> Second half task track approval is pending for the following users: <br/>"
									+ sb);
							mailContent.put(projectName, previousMonthData);
						}

					}
				}
			}

			PreviousMonthMailDto mailDto = new PreviousMonthMailDto();
			mailDto.setEmail(approverMailMap.get(userName).toString());
			mailDto.setApproverFullName(approverFullNameMap.get(userName).toString());
			mailDto.setMailContent(mailContent);
			previousMonthDataMap.put(userName, mailDto);

//			if(previousMonthDataMap.containsKey(userName)){
//				StringBuilder content = (StringBuilder) previousMonthDataMap.get(userName);
//				content.append(previousMonthData);
//				previousMonthDataMap.put(userName, content);
//			}
//			else {
//				previousMonthDataMap.put(userName, previousMonthData);
//			}

		}

		Iterator currentMonthStatusIterator = currentMonthDataMap.entrySet().iterator();
		while (currentMonthStatusIterator.hasNext()) {
//			StringBuilder previousMonthData = new StringBuilder();
			Map.Entry entry = (Map.Entry) currentMonthStatusIterator.next();

			Map projectMap = new HashMap<String, ArrayList<String>>();
			projectMap = (Map) entry.getValue();

			String userName = entry.getKey().toString();

			List<String> projectList = (List<String>) projectMap.keySet().stream().collect(Collectors.toList());

			if (projectList != null && projectList.size() > 0) {
				for (String projectName : projectList) {
					ArrayList<String> userList = (ArrayList<String>) projectMap.get(projectName);
					StringBuilder currentMonthData = new StringBuilder();

					StringBuilder sb = new StringBuilder();
					if (userList != null && userList.size() > 0) {

						for (String user : userList) {
//							if (sb.length() != 0)
//								sb.append(", ");
							sb.append(" * " + user + "<br/>");
						}

						String monthname = new DateFormatSymbols().getMonths()[month];
						if (!mailContent.containsKey(projectName)) {
//							previousMonthData.append("<br/><br/>Project Name : " + projectName);
							currentMonthData.append("<br/><br/><b>" + monthname
									+ "</b> First half task track approval is pending for the following users: <br/>"
									+ sb);
							mailContent.put(projectName, currentMonthData);
						} else {
							String value = mailContent.get(projectName).toString();
							currentMonthData.append(value + "<br/><br/><b>" + monthname
									+ "</b> First half task track approval is pending for the following users: <br/>"
									+ sb);
							mailContent.put(projectName, currentMonthData);
						}

					}
				}
			}
			PreviousMonthMailDto mailDto = new PreviousMonthMailDto();
			mailDto.setEmail(approverMailMap.get(userName).toString());
			mailDto.setApproverFullName(approverFullNameMap.get(userName).toString());
			mailDto.setMailContent(mailContent);
			previousMonthDataMap.put(userName, mailDto);

		}

		return previousMonthDataMap;
	}
}
