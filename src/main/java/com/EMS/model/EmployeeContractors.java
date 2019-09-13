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
    public String getContractorName() {
        return contractorName;
    }
    public void setContractorName(String category) {
        this.contractorName = contractorName;
    }



}
