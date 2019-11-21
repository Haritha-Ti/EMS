package com.EMS.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cron_date_model")
public class CronDateModel {

	@Id
	@Column(name = "cronId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long cronId;

	private Integer taskTrack;
	private Integer taskTrackUserLevel;
	private Integer apprOneFirstHalf;
	private Integer apprOneSecondHalf;
	private Integer apprTwoFirstHalf;
	private Integer apprTwoSecondHalf;

	public long getCronId() {
		return cronId;
	}

	public void setCronId(long cronId) {
		this.cronId = cronId;
	}

	public Integer getTaskTrack() {
		return taskTrack;
	}

	public void setTaskTrack(Integer taskTrack) {
		this.taskTrack = taskTrack;
	}

	public Integer getTaskTrackUserLevel() {
		return taskTrackUserLevel;
	}

	public void setTaskTrackUserLevel(Integer taskTrackUserLevel) {
		this.taskTrackUserLevel = taskTrackUserLevel;
	}

	public Integer getApprOneFirstHalf() {
		return apprOneFirstHalf;
	}

	public void setApprOneFirstHalf(Integer apprOneFirstHalf) {
		this.apprOneFirstHalf = apprOneFirstHalf;
	}

	public Integer getApprOneSecondHalf() {
		return apprOneSecondHalf;
	}

	public void setApprOneSecondHalf(Integer apprOneSecondHalf) {
		this.apprOneSecondHalf = apprOneSecondHalf;
	}

	public Integer getApprTwoFirstHalf() {
		return apprTwoFirstHalf;
	}

	public void setApprTwoFirstHalf(Integer apprTwoFirstHalf) {
		this.apprTwoFirstHalf = apprTwoFirstHalf;
	}

	public Integer getApprTwoSecondHalf() {
		return apprTwoSecondHalf;
	}

	public void setApprTwoSecondHalf(Integer apprTwoSecondHalf) {
		this.apprTwoSecondHalf = apprTwoSecondHalf;
	}

}
