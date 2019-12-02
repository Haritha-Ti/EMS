package com.EMS.model;

import java.util.Date;

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
@Table(name = "Performance_Mangement")
public class PerformanceMangementModel extends Auditable<Long> {
	@Id
	@Column(name = "Performance_MangementId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long Performance_MangementId;

	@Column(name = "type")
	private String type;

	@Column(name = "appraisal_date")
	private Date appraisal_date;

	@Column(name = "rating")
	private String rating;

	@ManyToOne
	@JoinColumn(name = "userId" , unique=true)
	private UserModel user;

	public long getPerformance_MangementId() {
		return Performance_MangementId;
	}

	public void setPerformance_MangementId(long performance_MangementId) {
		Performance_MangementId = performance_MangementId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getAppraisal_date() {
		return appraisal_date;
	}

	public void setAppraisal_date(Date appraisal_date) {
		this.appraisal_date = appraisal_date;
	}

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}
	
	

}
