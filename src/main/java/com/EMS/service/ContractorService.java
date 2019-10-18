package com.EMS.service;

import java.util.ArrayList;

import com.EMS.model.EmployeeContractors;


public interface ContractorService {

	int duplicationchecking(String contractorName);

	EmployeeContractors save_contractor_record(EmployeeContractors contractors);

	ArrayList<EmployeeContractors> getContractorList();

	EmployeeContractors getContractordata(Long contractId);

}
