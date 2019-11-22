package com.EMS.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.EMS.model.ExportApprovalReportModel;

public class ApprovalReportRowMapper implements RowMapper<ExportApprovalReportModel> {

	@Override
	public ExportApprovalReportModel mapRow(ResultSet rs, int rowNum) throws SQLException {

		ExportApprovalReportModel rpt = new ExportApprovalReportModel();
		rpt.setId(rs.getString("id")!=null ? Double.parseDouble(rs.getString("id")) : null);
		rpt.setFirstName(rs.getString("firstName"));
		rpt.setLastName(rs.getString("lastName"));
		rpt.setProjectName(rs.getString("projectName"));
		rpt.setProjectCode(rs.getString("projectCode"));
		String projectType;
		long projecttype = Long.parseLong(rs.getString("projectType"));
		if(projecttype == 1)
		{
			projectType = "IC";
		}
		else
		{
			projectType = "Non IC";
		}
		rpt.setProjectType(projectType);
		rpt.setCppLevel(rs.getString("levelName"));
		rpt.setDay1(rs.getString("day1")!=null ? Double.parseDouble(rs.getString("day1")) : 0);
		rpt.setDay2(rs.getString("day2")!=null ? Double.parseDouble(rs.getString("day2")) : 0);
		rpt.setDay3(rs.getString("day3")!=null ? Double.parseDouble(rs.getString("day3")) : 0);
		rpt.setDay4(rs.getString("day4")!=null ? Double.parseDouble(rs.getString("day4")) : 0);
		rpt.setDay5(rs.getString("day5")!=null ? Double.parseDouble(rs.getString("day5")) : 0);
		rpt.setDay6(rs.getString("day6")!=null ? Double.parseDouble(rs.getString("day6")) : 0);
		rpt.setDay7(rs.getString("day7")!=null ? Double.parseDouble(rs.getString("day7")) : 0);
		rpt.setDay8(rs.getString("day8")!=null ? Double.parseDouble(rs.getString("day8")) : 0);
		rpt.setDay9(rs.getString("day9")!=null ? Double.parseDouble(rs.getString("day9")) : 0);
		rpt.setDay10(rs.getString("day10")!=null ? Double.parseDouble(rs.getString("day10")) : 0);
		rpt.setDay11(rs.getString("day11")!=null ? Double.parseDouble(rs.getString("day11")) : 0);
		rpt.setDay12(rs.getString("day12")!=null ? Double.parseDouble(rs.getString("day12")) : 0);
		rpt.setDay13(rs.getString("day13")!=null ? Double.parseDouble(rs.getString("day13")) : 0);
		rpt.setDay14(rs.getString("day14")!=null ? Double.parseDouble(rs.getString("day14")) : 0);
		rpt.setDay15(rs.getString("day15")!=null ? Double.parseDouble(rs.getString("day15")) : 0);
		rpt.setDay16(rs.getString("day16")!=null ? Double.parseDouble(rs.getString("day16")) : 0);
		rpt.setDay17(rs.getString("day17")!=null ? Double.parseDouble(rs.getString("day17")) : 0);
		rpt.setDay18(rs.getString("day18")!=null ? Double.parseDouble(rs.getString("day18")) : 0);
		rpt.setDay19(rs.getString("day19")!=null ? Double.parseDouble(rs.getString("day19")) : 0);
		rpt.setDay20(rs.getString("day20")!=null ? Double.parseDouble(rs.getString("day20")) : 0);
		rpt.setDay21(rs.getString("day21")!=null ? Double.parseDouble(rs.getString("day21")) : 0);
		rpt.setDay22(rs.getString("day22")!=null ? Double.parseDouble(rs.getString("day22")) : 0);
		rpt.setDay23(rs.getString("day23")!=null ? Double.parseDouble(rs.getString("day23")) : 0);
		rpt.setDay24(rs.getString("day24")!=null ? Double.parseDouble(rs.getString("day24")) : 0);
		rpt.setDay25(rs.getString("day25")!=null ? Double.parseDouble(rs.getString("day25")) : 0);
		rpt.setDay26(rs.getString("day26")!=null ? Double.parseDouble(rs.getString("day26")) : 0);
		rpt.setDay27(rs.getString("day27")!=null ? Double.parseDouble(rs.getString("day27")) : 0);
		rpt.setDay28(rs.getString("day28")!=null ? Double.parseDouble(rs.getString("day28")) : 0);
		rpt.setDay29(rs.getString("day29")!=null ? Double.parseDouble(rs.getString("day29")) : 0);
		rpt.setDay30(rs.getString("day30")!=null ? Double.parseDouble(rs.getString("day30")) : 0);
		rpt.setDay31(rs.getString("day31")!=null ? Double.parseDouble(rs.getString("day31")) : 0);
		
		return rpt;
	}
	

}
