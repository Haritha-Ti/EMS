package com.EMS.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;

@EntityListeners(ModelListener.class)
@Entity
@Audited
@Table(name = "User_payroll")
public class PayrollModel extends Auditable<Long> {
	@Id
	@Column(name = "payroll_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long  payrollId;
	
	@ManyToOne
	@JoinColumn(name = "user_id" ,unique=true )
	private UserModel user;
	
	@Column(name = "payslip_pwd")
	private String payslipPwd;
	
	@Column(name = "monthly_salary")
	private Double  monthlySalary;
	
	@Column(name = "hourly_salary")
	private Double  hourlySalary;
	
	@Column(name = "monthly_business_allowance")
	private Double  monthlyBusinessAllowance;
	
	@Column(name = "total_compensation")
	private Double  totalCompensation;
	
	@Column(name = "accno")
	private String accNo;
	
	@Column(name = "other_income")
	private Double  otherIncome;
	
	@Column(name = "salary_increase_committed")
	private boolean salaryIncreaseCommitted;
	
	@Column(name = "namepa")
	private String namePA;
	
	@Column(name = "tax_exemptn")
	private Double  taxExemptn;
	
	@Column(name = "sss")
	private String sss;
	
	@Column(name = "phil_health_no")
	private String philHealthNo;
	
	@Column(name = "pag_lbig_no")
	private String pagLbigno;
	
	@Column(name = "contract_term")
	private String contractTerm;
	
	@Column(name = "contract_startdt")
	private Date contractStartDt;
	
	@Column(name = "contract_expdt")
	private Date contractExpDt;
	
	@Column(name = "passport_no")
	private String passportNo;
	
	@Column(name = "nationality")
	private String nationality;
	
	@Column(name = "passport_expdt")
	private Date passportExpDt;
	
	public long getPayrollId() {
		return payrollId;
	}

	public void setPayrollId(long payrollId) {
		this.payrollId = payrollId;
	}

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public String getPayslipPwd() {
		return payslipPwd;
	}

	public void setPayslipPwd(String payslipPwd) {
		this.payslipPwd = payslipPwd;
	}

	public Double getMonthlySalary() {
		return monthlySalary;
	}

	public void setMonthlySalary(Double monthlySalary) {
		this.monthlySalary = monthlySalary;
	}

	public Double getHourlySalary() {
		return hourlySalary;
	}

	public void setHourlySalary(Double hourlySalary) {
		this.hourlySalary = hourlySalary;
	}

	public Double getMonthlyBusinessAllowance() {
		return monthlyBusinessAllowance;
	}

	public void setMonthlyBusinessAllowance(Double monthlyBusinessAllowance) {
		this.monthlyBusinessAllowance = monthlyBusinessAllowance;
	}

	public Double getTotalCompensation() {
		return totalCompensation;
	}

	public void setTotalCompensation(Double totalCompensation) {
		this.totalCompensation = totalCompensation;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public Double getOtherIncome() {
		return otherIncome;
	}

	public void setOtherIncome(Double otherIncome) {
		this.otherIncome = otherIncome;
	}

	public boolean isSalaryIncreaseCommitted() {
		return salaryIncreaseCommitted;
	}

	public void setSalaryIncreaseCommitted(boolean salaryIncreaseCommitted) {
		this.salaryIncreaseCommitted = salaryIncreaseCommitted;
	}

	public String getNamePA() {
		return namePA;
	}

	public void setNamePA(String namePA) {
		this.namePA = namePA;
	}

	public Double getTaxExemptn() {
		return taxExemptn;
	}

	public void setTaxExemptn(Double taxExemptn) {
		this.taxExemptn = taxExemptn;
	}

	public String getSss() {
		return sss;
	}

	public void setSss(String sss) {
		this.sss = sss;
	}

	public String getPhilHealthNo() {
		return philHealthNo;
	}

	public void setPhilHealthNo(String philHealthNo) {
		this.philHealthNo = philHealthNo;
	}

	public String getPagLbigno() {
		return pagLbigno;
	}

	public void setPagLbigno(String pagLbigno) {
		this.pagLbigno = pagLbigno;
	}

	public String getContractTerm() {
		return contractTerm;
	}

	public void setContractTerm(String contractTerm) {
		this.contractTerm = contractTerm;
	}

	public Date getContractStartDt() {
		return contractStartDt;
	}

	public void setContractStartDt(Date contractStartDt) {
		this.contractStartDt = contractStartDt;
	}

	public Date getContractExpDt() {
		return contractExpDt;
	}

	public void setContractExpDt(Date contractExpDt) {
		this.contractExpDt = contractExpDt;
	}

	public String getPassportNo() {
		return passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public Date getPassportExpDt() {
		return passportExpDt;
	}

	public void setPassportExpDt(Date passportExpDt) {
		this.passportExpDt = passportExpDt;
	}

	



}
