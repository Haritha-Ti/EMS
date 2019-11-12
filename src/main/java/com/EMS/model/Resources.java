package com.EMS.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;

@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name="Resources")
public class Resources extends Auditable<String>{
	
	
	
	@Id
	@Column(name="resourceId")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long resourceId;
	
	@ManyToOne
	private DepartmentModel department;
	private int resourceCount;
	
	@ManyToOne
	private ProjectModel project;
	
	public ProjectModel getProject() {
		return project;
	}

	public void setProject(ProjectModel project) {
		this.project = project;
	}

	public Resources() {
		
	}
	
	public Resources(long resourceId, int resourceCount) {
		super();
		this.resourceId = resourceId;
		this.resourceCount = resourceCount;
	}

	public DepartmentModel getDepartment() {
		return department;
	}
	
	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	public void setDepartment(DepartmentModel department) {
		this.department = department;
	}
	public int getresourceCount() {
		return resourceCount;
	}
	public void setresourceCount(int resourceCount) {
		this.resourceCount = resourceCount;
	}
	
	
}
