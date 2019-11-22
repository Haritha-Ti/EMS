package com.EMS.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cron_time_model")
public class CronTimeModel {

	@Id
	@Column(name = "cronId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long cronId;

	private String taskTrack;
	private String taskTrackUserLevel;
	private String apprOneFirstHalf;
	private String apprOneSecondHalf;
	private String apprTwoFirstHalf;
	private String apprTwoSecondHalf;

	public long getCronId() {
		return cronId;
	}

	public void setCronId(long cronId) {
		this.cronId = cronId;
	}

	public String getTaskTrack() {
		return taskTrack;
	}

	public void setTaskTrack(String taskTrack) {
		this.taskTrack = taskTrack;
	}

	public String getTaskTrackUserLevel() {
		return taskTrackUserLevel;
	}

	public void setTaskTrackUserLevel(String taskTrackUserLevel) {
		this.taskTrackUserLevel = taskTrackUserLevel;
	}

	public String getApprOneFirstHalf() {
		return apprOneFirstHalf;
	}

	public void setApprOneFirstHalf(String apprOneFirstHalf) {
		this.apprOneFirstHalf = apprOneFirstHalf;
	}

	public String getApprOneSecondHalf() {
		return apprOneSecondHalf;
	}

	public void setApprOneSecondHalf(String apprOneSecondHalf) {
		this.apprOneSecondHalf = apprOneSecondHalf;
	}

	public String getApprTwoFirstHalf() {
		return apprTwoFirstHalf;
	}

	public void setApprTwoFirstHalf(String apprTwoFirstHalf) {
		this.apprTwoFirstHalf = apprTwoFirstHalf;
	}

	public String getApprTwoSecondHalf() {
		return apprTwoSecondHalf;
	}

	public void setApprTwoSecondHalf(String apprTwoSecondHalf) {
		this.apprTwoSecondHalf = apprTwoSecondHalf;
	}

}
