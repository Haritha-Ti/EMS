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
public class UserTechnology  extends Auditable<Long> {
	
	@Id
	@Column(name="userTechnologyId")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long userTechnologyId;
	
	@ManyToOne
	private UserModel user;
	
	@ManyToOne
	private Technology technology;
	private Double experience;
	private String comment;
	private int skill_level;
	
	
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getSkill_level() {
		return skill_level;
	}
	public void setSkill_level(int skill_level) {
		this.skill_level = skill_level;
	}
	public UserTechnology() {
		
	}
	
	public UserTechnology(long userTechnologyId, UserModel user, Technology technology, Double experience,
			String comment, int skill_level) {
		super();
		this.userTechnologyId = userTechnologyId;
		this.user = user;
		this.technology = technology;
		this.experience = experience;
		this.comment = comment;
		this.skill_level = skill_level;
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
