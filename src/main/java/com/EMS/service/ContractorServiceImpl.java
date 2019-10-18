package com.EMS.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.EmployeeContractors;
import com.EMS.repository.EmployeeContractorsRepository;

@Service
public class ContractorServiceImpl implements ContractorService{

	@Autowired
	EmployeeContractorsRepository contractorRepository;

	@Override
	public int duplicationchecking(String contractorName) {
		int value = contractorRepository.findContractor(contractorName);
		return value;
	}

	@Override
	public EmployeeContractors save_contractor_record(EmployeeContractors contractors) {
		EmployeeContractors emp_contractors=contractorRepository.save(contractors);
		return emp_contractors;
	}

	@Override
	public ArrayList<EmployeeContractors> getContractorList() {
		ArrayList<EmployeeContractors> list=(ArrayList<EmployeeContractors>) contractorRepository.findAll();
		return list;
	}

	@Override
	public EmployeeContractors getContractordata(Long contractId) {
		EmployeeContractors contractdata=contractorRepository.getOne(contractId);
		return contractdata;
	}
}
