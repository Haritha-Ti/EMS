package com.EMS.model;

import java.util.Date;

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
@Table(name = "holiday")
public class HolidayModel extends Auditable<Long> {

	@Id
	@Column(name = "holidayId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long holidayId;
	
	private Date date;
	private String day;
	private String holidayName;
	private String holidayType;
	@ManyToOne
	private Region region_id;
	
	private boolean isDeleted;
	
	
	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public long getHolidayId() {
		return holidayId;
	}
	
	public void setHolidayId(long holidayId) {
		this.holidayId = holidayId;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getDay() {
		return day;
	}
	
	public void setDay(String day) {
		this.day = day;
	}
	
	public String getHolidayName() {
		return holidayName;
	}
	
	public void setHolidayName(String holidayName) {
		this.holidayName = holidayName;
	}
	
	public String getHolidayType() {
		return holidayType;
	}
	
	public void setHolidayType(String holidayType) {
		this.holidayType = holidayType;
	}

	public Region getRegion_id() {
		return region_id;
	}

	public void setRegion_id(Region region_id) {
		this.region_id = region_id;
	}
	
	
}
