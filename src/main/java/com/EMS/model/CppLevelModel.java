package com.EMS.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;

@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name="cpp_level")
public class CppLevelModel extends Auditable<String>{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long levelId;
	
	
	private String levelName;


	public long getLevelId() {
		return levelId;
	}


	public void setLevelId(long levelId) {
		this.levelId = levelId;
	}


	public String getLevelName() {
		return levelName;
	}


	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}



	
	
	
}
