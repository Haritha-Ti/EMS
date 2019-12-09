package com.EMS.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.EMS.listener.ModelListener;

@Audited
@EntityListeners(ModelListener.class)
@Entity
@Table(name = "User_Skills")
public class SkillsModel extends Auditable<Long> {
	@Id
	@Column(name = "skiilsId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long skiilsId;

	@ManyToOne
	@JoinColumn(name = "userId" , unique=true)
	private UserModel userId;

	@Column(name = "primary_skills")
	private String primarySkills;
	@Column(name = "secondary_skills")
	private String secondarySkills;
	@Column(name = "other_skills")
	private String otherSkills;

	public long getSkiilsId() {
		return skiilsId;
	}

	public void setSkiilsId(long skiilsId) {
		this.skiilsId = skiilsId;
	}

	public UserModel getUserId() {
		return userId;
	}

	public void setUserId(UserModel userId) {
		this.userId = userId;
	}

	public String getPrimarySkills() {
		return primarySkills;
	}

	public void setPrimarySkills(String primarySkills) {
		this.primarySkills = primarySkills;
	}

	public String getSecondarySkills() {
		return secondarySkills;
	}

	public void setSecondarySkills(String secondarySkills) {
		this.secondarySkills = secondarySkills;
	}

	public String getOtherSkills() {
		return otherSkills;
	}

	public void setOtherSkills(String otherSkills) {
		this.otherSkills = otherSkills;
	}

}
