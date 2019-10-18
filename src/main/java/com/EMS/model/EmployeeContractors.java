package com.EMS.model;

import javax.persistence.*;


@Entity
@Table(name = "employeeContractors")
public class EmployeeContractors {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long contractorId;
    private String contractorName;
	
    
    public long getContractorId() {
		return contractorId;
	}
	public void setContractorId(long contractorId) {
		this.contractorId = contractorId;
	}
	public String getContractorName() {
		return contractorName;
	}
	public void setContractorName(String contractorName) {
		this.contractorName = contractorName;
	}
	public EmployeeContractors() {
		super();
		// TODO Auto-generated constructor stub
	}
	public EmployeeContractors(long contractorId, String contractorName) {
		super();
		this.contractorId = contractorId;
		this.contractorName = contractorName;
	}

  


}
