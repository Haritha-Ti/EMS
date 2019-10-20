package com.EMS.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cpp_level")
public class CppLevelModel {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	
	private String cpp_level_name;


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getCpp_level_name() {
		return cpp_level_name;
	}


	public void setCpp_level_name(String cpp_level_name) {
		this.cpp_level_name = cpp_level_name;
	}
	
	
	
	
}
