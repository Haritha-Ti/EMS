package com.EMS.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Task")
public class TaskModel {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "description")
	private String description;

	@Column(name = "date")
	private Date date;

	@Column(name = "hours")
	private Integer hours;

	@ManyToOne
	private ProjectModel project;

	@ManyToOne
	private UserModel user;

	private String taskName;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ProjectModel getproject() {
		return project;
	}

	public void setproject(ProjectModel project) {
		this.project = project;
	}

	public UserModel getuser() {
		return user;
	}

	public void setuser(UserModel user) {
		this.user = user;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
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

	public Integer getHours() {
		return hours;
	}

	public void setHours(Integer hours) {
		this.hours = hours;
	}
	
	


}
