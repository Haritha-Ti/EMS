package com.EMS.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cron_model")
public class CronModel {
	
	@Id
	@Column(name="cronId")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long cronId;
	
	private Date cronDate;
	
	public long getCronId() {
		return cronId;
	}

	public void setCronId(long cronId) {
		this.cronId = cronId;
	}

	public Date getCronDate() {
		return cronDate;
	}

	public void setCronDate(Date cronDate) {
		this.cronDate = cronDate;
	}

	

	
	

}
