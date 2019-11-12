package com.EMS.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Past;

import org.hibernate.annotations.GeneratorType;
import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;


@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name="userTechnology")
public class UserTechnology  extends Auditable<String> {
	
	@Id
	@Column(name="userTechnologyId")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long userTechnologyId;
	
	@ManyToOne
	private UserModel user;
	
	@ManyToOne
	private Technology technology;
	private Double experience;
	
	public UserTechnology() {
		
	}
	public UserTechnology(long userTechnologyId,double experience) {
		super();
		this.userTechnologyId = userTechnologyId;
		this.experience = experience;
	}
	public long getUserTechnologyId() {
		return userTechnologyId;
	}
	public void setUserTechnologyId(long userTechnologyId) {
		this.userTechnologyId = userTechnologyId;
	}
	public UserModel getUser() {
		return user;
	}
	public void setUser(UserModel user) {
		this.user = user;
	}
	public Technology getTechnology() {
		return technology;
	}
	public void setTechnology(Technology technology) {
		this.technology = technology;
	}
	public Double getExperience() {
		return experience;
	}
	public void setExperience(Double experience) {
		this.experience = experience;
	}
	
	
	
	
	
}
