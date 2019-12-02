package com.EMS.repository;

import org.json.simple.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.PayrollModel;

	
public interface PayrollRepository extends JpaRepository<PayrollModel, Long>{
	
	
	@Query("  SELECT P FROM  PayrollModel P  WHERE P.user.userId=?1 ")
	PayrollModel  PayrollByUserId(Long userId);
	
	@Query(" SELECT P.payslipPwd as payslipPwd,P.monthlySalary as monthlySalary,P.hourlySalary as hourlySalary,P.monthlyBusinessAllowance as monthlyBusinessAllowance,"
			+ " P.totalCompensation as totalCompensation,P.accNo as accNo,P.otherIncome as otherIncome,P.salaryIncreaseCommitted as salaryIncreaseCommitted,P.namePA as namePA,P.taxExemptn as taxExemptn,"
			+ " P.sss as sss,P.philHealthNo as philHealthNo,P.pagLbigno as pagLbigno,P.contractTerm as contractTerm,DATE_FORMAT(P.contractStartDt,'%Y-%m-%d') as contractStartDt,DATE_FORMAT(P.contractExpDt,'%Y-%m-%d')  as contractExpDt,P.passportNo as passportNo,"
			+ " P.nationality as nationality,DATE_FORMAT(P.passportExpDt,'%Y-%m-%d') as passportExpDt"
			+ " FROM PayrollModel P where P.user.userId=?1 ")
	JSONObject  getPayrollByUserId(Long userId);


	}
