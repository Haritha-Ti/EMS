package com.EMS.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.EMS.model.ProjectReportModel;

public class ReportRowMapper implements RowMapper<ProjectReportModel> {

	@Override
	public ProjectReportModel mapRow(ResultSet rs, int rowNum) throws SQLException {

		ProjectReportModel rpt = new ProjectReportModel();
		
		rpt.setUserName(rs.getString("users"));
		rpt.setAllocatedPerce(rs.getString("allocated_perce"));
		rpt.setIsBillable(rs.getInt("is_billable"));
		rpt.setProjectName(rs.getString("project_name"));
		rpt.setRegionId(rs.getInt("region_id"));
		return rpt;
	}
	

}
