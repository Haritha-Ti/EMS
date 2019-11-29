package com.EMS.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.EMS.model.FreeAllocationReportModel;


public class FreeAllcoationReportRowMapper implements RowMapper<FreeAllocationReportModel> {

	@Override
	public FreeAllocationReportModel mapRow(ResultSet rs, int rowNum) throws SQLException {

		FreeAllocationReportModel rpt = new FreeAllocationReportModel();
		try {
		rpt.setUserId(Long.parseLong(rs.getString("userId")));
		rpt.setResourceName(rs.getString("resourceName"));
		rpt.setProjectName(rs.getString("projectName"));
		rpt.setStartDate(rs.getDate("startDate"));
		rpt.setEndDate(rs.getDate("endDate"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return rpt;
	}
	

}
