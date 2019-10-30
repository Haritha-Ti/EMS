package com.EMS.utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.springframework.jdbc.core.RowMapper;

import com.EMS.model.ApprovalTimeTrackReportModel;
import com.EMS.model.ExportProjectTaskReportModel;

public class ApprovalTimeTrackReportRowMapper implements RowMapper<ApprovalTimeTrackReportModel> {

	@Override
	public ApprovalTimeTrackReportModel mapRow(ResultSet rs, int rowNum) throws SQLException {

		ApprovalTimeTrackReportModel rpt = new ApprovalTimeTrackReportModel();
		rpt.setProjectName(rs.getString("projectName"));
		rpt.setProjectId(rs.getLong("projectId"));
		rpt.setBillableHours(rs.getString("approved")!=null ? Double.parseDouble(rs.getString("approved")) : 0);
		rpt.setLoggedHours(rs.getString("logged")!=null ? Double.parseDouble(rs.getString("logged")) : 0);
		
		return rpt;
	}
	

}
