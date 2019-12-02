package com.EMS.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.model.EmploymentDetailsModel;
import com.EMS.model.PayrollModel;
import com.EMS.model.PerformanceMangementModel;
import com.EMS.model.SkillsModel;
import com.EMS.model.UserModel;
import com.EMS.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value = "/user")
public class UserController {

	@Autowired
	UserService userService;

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/addSkills")
	public JSONObject addSkills(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {

		JSONObject response = new JSONObject();
		SkillsModel skill = new SkillsModel();
		Long userId = null;
		try {
			userId = requestdata.get("userId").asLong();
			if (userId == null || userId == 0L) {

				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			UserModel user = userService.getUserDetailsById(requestdata.get("userId").asLong());
			if (user == null) {
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}

			skill.setPrimarySkills(requestdata.get("primaryskill").asText().trim());
			skill.setSecondarySkills(requestdata.get("secondaryskill").asText().trim());
			skill.setOtherSkills(requestdata.get("otherskill").asText().trim());
			skill.setUserId(user);
			if (userService.addSkills(skill) != null) {
				response.put("Status", "Sucess");
				response.put("Code", 201);
				response.put("Message", "Skills Added");
			} else {
				response.put("Status", "Failure");
				response.put("Code", 400);
				response.put("Message", "Skills Addition failed !");
			}

		} catch (Exception e) {
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@PutMapping(value = "/editSkills")
	public JSONObject editSkills(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {

		JSONObject response = new JSONObject();
		SkillsModel skill = new SkillsModel();
		Long userId = null;
		Long skillId = null;
		try {
			userId = requestdata.get("userId").asLong();
			if (userId == null || userId == 0L) {

				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			UserModel user = userService.getUserDetailsById(requestdata.get("userId").asLong());
			if (user == null) {
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			skillId = userService.getSkillModelByUserId(userId).getSkiilsId();
			if (skillId == null || skillId == 0L) {
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			skill.setPrimarySkills(requestdata.get("primaryskill").asText().trim());
			skill.setSecondarySkills(requestdata.get("secondaryskill").asText().trim());
			skill.setOtherSkills(requestdata.get("otherskill").asText().trim());
			skill.setSkiilsId(skillId);
			skill.setUserId(user);
			if (userService.updateSkills(skill) != null) {
				response.put("Status", "Sucess");
				response.put("Code", 201);
				response.put("Message", "Skills Updated");
			} else {
				response.put("Status", "Failure");
				response.put("Code", 400);
				response.put("Message", "Skills Updation failed !");
			}

		} catch (Exception e) {
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
		}
		return response;
	}

	@SuppressWarnings({ "unchecked", "unused", "null" })
	@PostMapping(value = "/getSkills")
	public JSONObject getSkills(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {

		JSONObject response = null;
		Long userId = null;
		try {

			userId = requestdata.get("userId").asLong();
			if (userId == null || userId == 0L) {
				response = new JSONObject();
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			response = userService.getSkillsByUserId(userId);

			if (response != null) {
				response.put("Status", "Sucess");
				response.put("Code", 200);

			} else {
				response = new JSONObject();
				response.put("Status", "Sucess");
				response.put("Code", 204);
				response.put("Message", "No Content");
			}

		} catch (Exception e) {
			response = new JSONObject();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/addAppraisal")
	public JSONObject addAppraisal(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {

		JSONObject response = new JSONObject();
		PerformanceMangementModel model = new PerformanceMangementModel();
		Long userId = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		try {
			userId = requestdata.get("userId").asLong();
			if (userId == null || userId == 0L) {

				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			UserModel user = userService.getUserDetailsById(requestdata.get("userId").asLong());
			if (user == null) {
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}

			model.setType(requestdata.get("type").asText().trim());
			model.setAppraisal_date(org.springframework.util.StringUtils.isEmpty(requestdata.get("appraisal_date").asText())? null : dateFormat.parse(requestdata.get("appraisal_date").asText().trim()));
			model.setRating(requestdata.get("rating").asText().trim());
			model.setUser(user);
			if (userService.addAppraisal(model) != null) {
				response.put("Status", "Sucess");
				response.put("Code", 201);
				response.put("Message", "Appraisal Added");
			} else {
				response.put("Status", "Failure");
				response.put("Code", 400);
				response.put("Message", "Appraisal Addition failed !");
			}

		} catch (Exception e) {
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	@PutMapping(value = "/editAppraisal")
	public JSONObject editAppraisal(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {

		JSONObject response = new JSONObject();
		PerformanceMangementModel model = new PerformanceMangementModel();
		Long userId = null;
		Long performance_MgntId = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		try {
			userId = requestdata.get("userId").asLong();
			if (userId == null || userId == 0L) {

				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			UserModel user = userService.getUserDetailsById(requestdata.get("userId").asLong());
			if (user == null) {
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			performance_MgntId = userService.getPerformanceManagementModelByUserId(userId).getPerformance_MangementId();
			if (performance_MgntId == null || performance_MgntId == 0L) {
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			model.setType(requestdata.get("type").asText().trim());
			model.setAppraisal_date(org.springframework.util.StringUtils.isEmpty(requestdata.get("appraisal_date").asText())? null : dateFormat.parse(requestdata.get("appraisal_date").asText().trim()));
			model.setRating(requestdata.get("rating").asText().trim());
			model.setPerformance_MangementId(performance_MgntId);
			model.setUser(user);
			if (userService.updateAppraisal(model) != null) {
				response.put("Status", "Sucess");
				response.put("Code", 201);
				response.put("Message", "Appraisal Updated");
			} else {
				response.put("Status", "Failure");
				response.put("Code", 400);
				response.put("Message", "Appraisal Updation failed !");
			}

		} catch (Exception e) {
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
		}
		return response;

	}
	
	
	@SuppressWarnings({ "unchecked", "unused", "null" })
	@PostMapping(value = "/getAppraisalDetails")
	public JSONObject getAppraisalDetails(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {

		JSONObject response = null;
		Long userId = null;
		try {

			userId = requestdata.get("userId").asLong();
			if (userId == null || userId == 0L) {
				response = new JSONObject();
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			response = userService.getAppraisalDetailsByUserId(userId);

			if (response != null) {
				response.put("Status", "Sucess");
				response.put("Code", 200);

			} else {
				response = new JSONObject();
				response.put("Status", "Sucess");
				response.put("Code", 204);
				response.put("Message", "No Content");
			}

		} catch (Exception e) {
			response = new JSONObject();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
		}
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/addEmploymentDetails")
	public JSONObject addEmploymentDetails(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {

		JSONObject response = new JSONObject();
		EmploymentDetailsModel model = new EmploymentDetailsModel();
		Long userId = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		try {
			userId = requestdata.get("userId").asLong();
			if (userId == null || userId == 0L) {

				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			UserModel user = userService.getUserDetailsById(requestdata.get("userId").asLong());
			if (user == null) {
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
           
			model.setContractStartdate(org.springframework.util.StringUtils.isEmpty(requestdata.get("contract_startdate").asText())? null : dateFormat.parse(requestdata.get("contract_startdate").asText().trim()));
			model.setContractEnddate(org.springframework.util.StringUtils.isEmpty(requestdata.get("contract_enddate").asText())? null : dateFormat.parse(requestdata.get("contract_enddate").asText().trim()));
			model.setRegularizationDate(org.springframework.util.StringUtils.isEmpty(requestdata.get("regularization_date").asText())? null : dateFormat.parse(requestdata.get("regularization_date").asText().trim()));
			model.setNewCppLevel(requestdata.get("newcpplevel").asText().trim());
			model.setNewCppLevelEffDate(org.springframework.util.StringUtils.isEmpty(requestdata.get("newcpplevel_eff_date").asText())? null : dateFormat.parse(requestdata.get("newcpplevel_eff_date").asText().trim()));
			model.setVisaStatus(requestdata.get("visastatus").asText().trim());
			model.setVisaType(requestdata.get("visatype").asText().trim());
			model.setVisaExpDate(org.springframework.util.StringUtils.isEmpty(requestdata.get("visa_expdate").asText())? null : dateFormat.parse(requestdata.get("visa_expdate").asText().trim()));
			model.setSource(requestdata.get("source").asText().trim());
			model.setReferredby(requestdata.get("referredby").asText().trim());
			model.setTermtype(requestdata.get("termtype").asText().trim());
			model.setLastWorkDate(org.springframework.util.StringUtils.isEmpty(requestdata.get("lastwork_date").asText())? null : dateFormat.parse(requestdata.get("lastwork_date").asText().trim()));
			model.setPayThruDate(org.springframework.util.StringUtils.isEmpty(requestdata.get("paythru_date").asText())? null : dateFormat.parse(requestdata.get("paythru_date").asText().trim()));
			model.setTerminationDate(org.springframework.util.StringUtils.isEmpty(requestdata.get("termination_date").asText())? null : dateFormat.parse(requestdata.get("termination_date").asText().trim()));
			model.setTerminationReason(requestdata.get("termination_reason").asText().trim());
			model.setUser(user);
	
			if (userService.addEmploymentDetails(model) != null) {
				response.put("Status", "Sucess");
				response.put("Code", 201);
				response.put("Message", " Employment Details Added");
			} else {
				response.put("Status", "Failure");
				response.put("Code", 400);
				response.put("Message", " Employment Details Addition failed !");
			}

		} catch (Exception e) {
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
		}
		return response;

	}
	
	
	@SuppressWarnings("unchecked")
	@PutMapping(value = "/editEmploymentDetails")
	public JSONObject editEmploymentDetails(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {

		JSONObject response = new JSONObject();
		EmploymentDetailsModel model = new EmploymentDetailsModel();
		Long userId = null;
		Long employmentDetailsId = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		try {
			userId = requestdata.get("userId").asLong();
			if (userId == null || userId == 0L) {

				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			UserModel user = userService.getUserDetailsById(requestdata.get("userId").asLong());
			if (user == null) {
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			employmentDetailsId = userService.getEmploymentDetailsModelByUserId(userId).getEmploymentDetailsId();
			if (employmentDetailsId == null || employmentDetailsId == 0L) {
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			model.setContractStartdate(org.springframework.util.StringUtils.isEmpty(requestdata.get("contract_startdate").asText())? null : dateFormat.parse(requestdata.get("contract_startdate").asText().trim()));
			model.setContractEnddate(org.springframework.util.StringUtils.isEmpty(requestdata.get("contract_enddate").asText())? null : dateFormat.parse(requestdata.get("contract_enddate").asText().trim()));
			model.setRegularizationDate(org.springframework.util.StringUtils.isEmpty(requestdata.get("regularization_date").asText())? null : dateFormat.parse(requestdata.get("regularization_date").asText().trim()));
			model.setNewCppLevel(requestdata.get("newcpplevel").asText().trim());
			model.setNewCppLevelEffDate(org.springframework.util.StringUtils.isEmpty(requestdata.get("newcpplevel_eff_date").asText())? null : dateFormat.parse(requestdata.get("newcpplevel_eff_date").asText().trim()));
			model.setVisaStatus(requestdata.get("visastatus").asText().trim());
			model.setVisaType(requestdata.get("visatype").asText().trim());
			model.setVisaExpDate(org.springframework.util.StringUtils.isEmpty(requestdata.get("visa_expdate").asText())? null : dateFormat.parse(requestdata.get("visa_expdate").asText().trim()));
			model.setSource(requestdata.get("source").asText().trim());
			model.setReferredby(requestdata.get("referredby").asText().trim());
			model.setTermtype(requestdata.get("termtype").asText().trim());
			model.setLastWorkDate(org.springframework.util.StringUtils.isEmpty(requestdata.get("lastwork_date").asText())? null : dateFormat.parse(requestdata.get("lastwork_date").asText().trim()));
			model.setPayThruDate(org.springframework.util.StringUtils.isEmpty(requestdata.get("paythru_date").asText())? null : dateFormat.parse(requestdata.get("paythru_date").asText().trim()));
			model.setTerminationDate(org.springframework.util.StringUtils.isEmpty(requestdata.get("termination_date").asText())? null : dateFormat.parse(requestdata.get("termination_date").asText().trim()));
			model.setTerminationReason(requestdata.get("termination_reason").asText().trim());
			model.setEmploymentDetailsId(employmentDetailsId);
			model.setUser(user);
			if (userService.updateEmploymentDetailsModel(model) != null) {
				response.put("Status", "Sucess");
				response.put("Code", 201);
				response.put("Message", " Employment Details Updated");
			} else {
				response.put("Status", "Failure");
				response.put("Code", 400);
				response.put("Message", " Employment Details Updation failed !");
			}

		} catch (Exception e) {
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
		}
		return response;

	}
	
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/getEmploymentDetails")
	public JSONObject getEmploymentDetails(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {

		JSONObject response = null;
		Long userId = null;
		try {

			userId = requestdata.get("userId").asLong();
			if (userId == null || userId == 0L) {
				response = new JSONObject();
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			response = userService.getEmploymentDetailsByUserId(userId);

			if (response != null) {
				response.put("Status", "Sucess");
				response.put("Code", 200);

			} else {
				response = new JSONObject();
				response.put("Status", "Sucess");
				response.put("Code", 204);
				response.put("Message", "No Content");
			}

		} catch (Exception e) {
			response = new JSONObject();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
		}
		return response;
	}
	
	
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/addPayroll")
	public JSONObject addPayroll(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {

		JSONObject response = new JSONObject();
		PayrollModel model = new PayrollModel();
		Long userId = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		try {
			userId = requestdata.get("userId").asLong();
			if (userId == null || userId == 0L) {

				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			UserModel user = userService.getUserDetailsById(requestdata.get("userId").asLong());
			if (user == null) {
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
              model.setPayslipPwd(requestdata.get("payslipPwd").asText().trim());
			  model.setMonthlySalary(requestdata.get("monthlySalary").asDouble());
			  model.setHourlySalary(requestdata.get("hourlySalary").asDouble());
			  model.setMonthlyBusinessAllowance(requestdata.get("monthlyBusinessAllowance").asDouble());
			  model.setTotalCompensation(requestdata.get("totalCompensation").asDouble());
			  model.setAccNo(requestdata.get("accNo").asText());
			  model.setOtherIncome(requestdata.get("otherIncome").asDouble());
			  model.setSalaryIncreaseCommitted(requestdata.get("salaryIncreaseCommitted").asBoolean());
			  model.setNamePA(requestdata.get("namePA").asText());
			  model.setTaxExemptn(requestdata.get("taxExemptn").asDouble());
			  model.setSss(requestdata.get("sss").asText());
			  model.setPhilHealthNo(requestdata.get("philHealthNo").asText());
			  model.setPagLbigno(requestdata.get("pagLbigno").asText());
			  model.setContractTerm(requestdata.get("contractTerm").asText());
			  model.setContractStartDt(org.springframework.util.StringUtils.isEmpty(requestdata.get("contractStartDt").asText())? null : dateFormat.parse(requestdata.get("contractStartDt").asText()));
			  model.setContractExpDt(org.springframework.util.StringUtils.isEmpty(requestdata.get("contractExpDt").asText())? null : dateFormat.parse(requestdata.get("contractExpDt").asText()));
			  model.setPassportNo(requestdata.get("passportNo").asText());
			  model.setNationality(requestdata.get("nationality").asText());
			  model.setPassportExpDt(org.springframework.util.StringUtils.isEmpty(requestdata.get("passportExpdt").asText())? null :  dateFormat.parse(requestdata.get("passportExpdt").asText()));
			  model.setUser(user);
	
			if (userService.addPayroll(model) != null) {
				response.put("Status", "Sucess");
				response.put("Code", 201);
				response.put("Message", "Payroll Added");
			} else {
				response.put("Status", "Failure");
				response.put("Code", 400);
				response.put("Message", "Payroll Addition failed !");
			}

		} catch (Exception e) {
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
		}
		return response;

	}
	
	
	@SuppressWarnings("unchecked")
	@PutMapping(value = "/editPayroll")
	public JSONObject editPayroll(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {

		JSONObject response = new JSONObject();
		PayrollModel model = new PayrollModel();
		Long userId = null;
		Long payrollId = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		try {
			userId = requestdata.get("userId").asLong();
			if (userId == null || userId == 0L) {

				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			UserModel user = userService.PayrollByUserId(userId).getUser();
			if (user == null) {
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			payrollId =  userService.PayrollByUserId(userId).getPayrollId();
			if (payrollId == null || payrollId == 0L) {
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			  model.setPayslipPwd(requestdata.get("payslipPwd").asText().trim());
			  model.setMonthlySalary(requestdata.get("monthlySalary").asDouble());
			  model.setHourlySalary(requestdata.get("hourlySalary").asDouble());
			  model.setMonthlyBusinessAllowance(requestdata.get("monthlyBusinessAllowance").asDouble());
			  model.setTotalCompensation(requestdata.get("totalCompensation").asDouble());
			  model.setAccNo(requestdata.get("accNo").asText());
			  model.setOtherIncome(requestdata.get("otherIncome").asDouble());
			  model.setSalaryIncreaseCommitted(requestdata.get("salaryIncreaseCommitted").asBoolean());
			  model.setNamePA(requestdata.get("namePA").asText());
			  model.setTaxExemptn(requestdata.get("taxExemptn").asDouble());
			  model.setSss(requestdata.get("sss").asText());
			  model.setPhilHealthNo(requestdata.get("philHealthNo").asText());
			  model.setPagLbigno(requestdata.get("pagLbigno").asText());
			  model.setContractTerm(requestdata.get("contractTerm").asText());
			  model.setContractStartDt(org.springframework.util.StringUtils.isEmpty(requestdata.get("contractStartDt").asText())? null : dateFormat.parse(requestdata.get("contractStartDt").asText()));
			  model.setContractExpDt(org.springframework.util.StringUtils.isEmpty(requestdata.get("contractExpDt").asText())? null : dateFormat.parse(requestdata.get("contractExpDt").asText()));
			  model.setPassportNo(requestdata.get("passportNo").asText());
			  model.setNationality(requestdata.get("nationality").asText());
			  model.setPassportExpDt(org.springframework.util.StringUtils.isEmpty(requestdata.get("passportExpdt").asText())? null :  dateFormat.parse(requestdata.get("passportExpdt").asText()));
			  model.setPayrollId(payrollId);
			  model.setUser(user);
			if (userService.updatePayroll(model) != null) {
				response.put("Status", "Sucess");
				response.put("Code", 201);
				response.put("Message", "Payroll Updated");
			} else {
				response.put("Status", "Failure");
				response.put("Code", 400);
				response.put("Message", "Payroll Updation failed !");
			}

		} catch (Exception e) {
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
		}
		return response;

	}
	
	
	
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/getPayrollDetails")
	public JSONObject getPayrollDetails(@RequestBody ObjectNode requestdata, HttpServletResponse servletresponse) {

		JSONObject response = null;
		Long userId = null;
		try {

			userId = requestdata.get("userId").asLong();
			if (userId == null || userId == 0L) {
				response = new JSONObject();
				response.put("Status", "failure");
				response.put("Code", 422);
				response.put("Message", "Invalid Input");
				return response;
			}
			response = userService.getPayrollByUserId(userId);

			if (response != null) {
				response.put("Status", "Sucess");
				response.put("Code", 200);

			} else {
				response = new JSONObject();
				response.put("Status", "Sucess");
				response.put("Code", 204);
				response.put("Message", "No Content");
			}

		} catch (Exception e) {
			response = new JSONObject();
			response.put("Status", "failure");
			response.put("Code", 500);
			response.put("Message", e.getMessage());
		}
		return response;
	}


}
