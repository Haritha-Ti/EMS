package com.EMS.utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.springframework.jdbc.core.RowMapper;

import com.EMS.model.ExportProjectTaskReportModel;

public class ExportProjectTaskReportRowMapper implements RowMapper<ExportProjectTaskReportModel> {

	@Override
	public ExportProjectTaskReportModel mapRow(ResultSet rs, int rowNum) throws SQLException {

		ExportProjectTaskReportModel rpt = new ExportProjectTaskReportModel();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
		
		rpt.setId(Long.parseLong(rs.getString("id")));
		rpt.setHours(Double.parseDouble(rs.getString("hours")));
		rpt.setProjectId(Long.parseLong(rs.getString("projectId")));
		rpt.setProjectName(rs.getString("projectName"));
		rpt.setResourceName(rs.getString("resourceName"));
		rpt.setTaskDate(simpleDateFormat.format(rs.getDate("taskDate")));
		rpt.setTaskDescription(rs.getString("taskDescription"));
		rpt.setBillable(rs.getString("billable"));
		return rpt;
	}
	

}
