package com.EMS.service;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.EMS.dto.ProjectSubmissionDataDTO;

/**
 * 
 * @author sreejith.j
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestReportServiceImpl {

	@Autowired
	ReportService service;

//	@Test
	public void testGetProjectSubmissionDetails() {
		try {
			System.out.println("Started testing...");
			List<ProjectSubmissionDataDTO> resultArr = service.getProjectSubmissionDetails(10, 2019,1l);

			System.out.println("Completed the operation...");
			System.out.println(resultArr.size());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Test
	public void testGetUsersProjectSubmissionDetails() {
		try {
			System.out.println("Started testing...");
			HashMap<String, Object> resultObj = service.getUsersProjectSubmissionDetails(89l, 2, 112l, 10, 2019,
					"FIRST");

			System.out.println("Completed the operation...");
			System.out.println(resultObj.size());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
