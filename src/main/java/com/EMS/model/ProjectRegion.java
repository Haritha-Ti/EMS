package com.EMS.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="project_region")
public class ProjectRegion {

	@Id
	@Column(name="project_region_Id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long project_region_Id;
	
	@ManyToOne
	private ProjectModel project_Id;
	
	@ManyToOne
	private Region region_Id;

	public long getProject_region_Id() {
		return project_region_Id;
	}

	public void setProject_region_Id(long project_region_Id) {
		this.project_region_Id = project_region_Id;
	}

	public ProjectModel getProject_Id() {
		return project_Id;
	}

	public void setProject_Id(ProjectModel project_Id) {
		this.project_Id = project_Id;
	}

	public Region getRegion_Id() {
		return region_Id;
	}

	public void setRegion_Id(Region region_Id) {
		this.region_Id = region_Id;
	}

	
	
	
	
}
