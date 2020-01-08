package com.EMS.service;

import java.io.IOException;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

public interface NewHireEmployeeService {
	
	public String sendMail(String uId, String mailId,String name,String lastName) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException;

}
