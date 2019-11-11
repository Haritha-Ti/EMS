package com.EMS.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name="Allocation")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AllocationModel extends  Auditable<String> {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long allocId;
	
	@ManyToOne
	private ProjectModel project;
	
	@ManyToOne
	private UserModel user;
	
	private Double allocatedPerce;
//	private Double freeAllocation;
	private Date startDate;
	private Date endDate;
	private Boolean isBillable;
	private boolean active =true;
	
	
	
	
	public long getAllocId() {
		return allocId;
	}
	
	public void setAllocId(long allocId) {
		this.allocId = allocId;
	}
	
	public ProjectModel getproject() {
		return project;
	}
	
	public void setproject(ProjectModel project) {
		this.project = project;
	}
	
	public Double getAllocatedPerce() {
		return allocatedPerce;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public void setAllocatedPerce(Double allocatedPerce) {
		this.allocatedPerce = allocatedPerce;
	}	
	
	public UserModel getuser() {
		return user;
	}
	
	public void setuser(UserModel user) {
		this.user = user;
	}
	
	public Boolean getIsBillable() {
		return isBillable;
	}
	
	public void setIsBillable(Boolean isBillable) {
		this.isBillable = isBillable;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	
	
	
	
	
	
}
