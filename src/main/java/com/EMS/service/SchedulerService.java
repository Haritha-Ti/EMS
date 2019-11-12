package com.EMS.service;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.EMS.dto.MailDomainDto;
import com.EMS.model.CronModel;
import com.EMS.repository.CronRepository;
import com.EMS.repository.HolidayRepository;
import com.EMS.repository.TasktrackRepository;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Component
public class SchedulerService {

	@Autowired
	private CronRepository cronRepo;

	@Autowired
	private TasktrackRepository taskRepo;

	@Autowired
	private HolidayRepository holidayRepo;
	
	@Autowired
	private EmailNotificationService emailNotificationService;
	
	@Autowired
    private Configuration freemarkerConfig;


	@SuppressWarnings({ "unchecked", "rawtypes", "unused", "unlikely-arg-type" })
	@Scheduled(cron = "0 35 16 * * *")
	public void create() throws Exception {

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
		
		List <Date>  holidays =  new ArrayList();
		holidays = holidayRepo.getHolidayDateList();

		List<CronModel> dates = cronRepo.getCronDates();
		if(dates != null && dates.size() > 0) {

		for (CronModel item : dates) {
			croneDate = formatter.parse(item.getCronDate().toString());
			if (croneDate.equals(date)) {
				System.out.println("Scheduler started " + date);
				LocalDate toDate = now.minusDays(1).toLocalDate();
				LocalDate fromDate = now.minusMonths(1).minusDays(toDate.getDayOfMonth()).toLocalDate();
				List<Object[]> trackTaskList = taskRepo.getTrackTaskList(fromDate, toDate);
				

				for (Object[] obj : trackTaskList) {
					String userName = obj[5].toString();
					String projectName = obj[1].toString();
					String taskDate = (obj[0] == null) ? null : obj[0].toString();
					Integer userIsActive = (Integer) obj[6];
					String email = (obj[3] == null)?null:obj[3].toString();
					String projectEndDate = (obj[4] == null)?null:obj[4].toString();
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
						}
						else
							projectMap.put(projectName, null);
						userMap.put(userName, projectMap);
					}
					
					
					//User IsActive
					if(!userActiveMap.containsKey(userName)) {
						userActiveMap.put(userName, userIsActive);
						userEmailMap.put(userName, email);	
					}
					if(!projectActiveMap.containsKey(projectName)) {
						projectActiveMap.put(projectName, projectEndDate);
					}
					if(!userFullNameMap.containsKey(userName)) {
						userFullNameMap.put(userName, fullName);
					}
	
				}
								
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");				
				Iterator it = userMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();

					Map projectMap = new HashMap<String, ArrayList<String>>();
					projectMap = (Map) entry.getValue();

					String userName = entry.getKey().toString();
					List<String> projectList = (List<String>) projectMap.keySet().stream().collect(Collectors.toList());

					for (String projectName : projectList) {
						List<LocalDate> dateList = (List<LocalDate>) projectMap.get(projectName);
						StringBuilder sb = new StringBuilder();
						LocalDate startDate = fromDate;
											
						if(Integer.parseInt(userActiveMap.get(userName).toString()) == 1) {
							
						while (!startDate.isAfter(toDate)) {
							
							if((sdf.parse(projectActiveMap.get(projectName).toString()).after(sdf.parse(startDate.toString()))) || 
									(sdf.parse(projectActiveMap.get(projectName).toString()).equals((sdf.parse(startDate.toString()))))) {
								if ((dateList==null || !dateList.contains(startDate.toString()))
										&& !holidays.contains(sdf.parse(startDate.toString()))
										&& !startDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) 
										&& !startDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
									
										if(sb.length() != 0) 
											sb.append(", ");
										sb.append(startDate.format(DateTimeFormatter.ofPattern("MMM d")));	
								}	
							}

							startDate = startDate.plusDays(1);

						}
						}else {
							continue;
						}
						
						if(sb.length() != 0) {
							MailDomainDto mailDomainDto = new MailDomainDto();
							mailDomainDto.setSubject("RCG Time Sheet- Please Submit ASAP ");
							
							StringBuilder mailBody = new StringBuilder("Hi "+ userFullNameMap.get(userName) +", ");
							mailBody.append("<br/><br/>Project: "+ projectName);
							mailBody.append("<br/><br/>Your timtrack is pending for the following days: <br/>"+sb);

							Template t = freemarkerConfig.getTemplate("email_template.ftl");
					        String html = (FreeMarkerTemplateUtils.processTemplateIntoString(t, mailDomainDto)).replace("MAIL_BODY", mailBody).replace("Title", "Please submit your time sheet !");

							mailDomainDto.setMailBody(html);
							mailDomainDto.setTo(userEmailMap.get(userName).toString());
						    String token = UUID.randomUUID().toString();
							String msg = emailNotificationService.sendMail(token, mailDomainDto);
						}	
					}

				}

			}
		}
		}
	}
}
