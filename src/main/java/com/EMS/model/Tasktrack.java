package com.EMS.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "Tasktrack")
public class Tasktrack implements Comparable<Tasktrack> {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "description")
	@JsonProperty("taskSummary")
	private String description;

	@Column(name = "date")
	private Date date;

	@Column(name = "hours")
	private Double hours;

	@ManyToOne
	private ProjectModel project;

	@ManyToOne
	private UserModel user;

	@ManyToOne
	private Task task;

	@Transient
	private long projectId;
	
	@Transient
	private long taskTypeId;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getHours() {
		return hours;
	}

	public void setHours(Double hours) {
		this.hours = hours;
	}

	public ProjectModel getProject() {
		return project;
	}

	public void setProject(ProjectModel project) {
		this.project = project;
	}

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	
	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public long getTaskTypeId() {
		return taskTypeId;
	}

	public void setTaskTypeId(long taskTypeId) {
		this.taskTypeId = taskTypeId;
	}

	@Override
	public int hashCode() {
		return this.getDate().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Tasktrack)) {
			return false;
		} else {
			Tasktrack tasktrack = (Tasktrack) obj;
			if (tasktrack.getDate().compareTo(this.getDate()) == 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(Tasktrack obj) {
		return this.getDate().compareTo(obj.getDate());
	}
}
