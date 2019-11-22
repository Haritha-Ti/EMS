package com.EMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.CronDateModel;

public interface CronDateRepository extends JpaRepository<CronDateModel, Long>{

	@Query(value = "select appr_one_second_half from cron_date_model", nativeQuery = true)
	List<Integer> getapproverOneSecondHalfSchedulerDate();

	@Query(value = "select appr_one_first_half from cron_date_model", nativeQuery = true)
	List<Integer> getapproverOneFirstHalfSchedulerDate();

	@Query(value = "select appr_two_second_half from cron_date_model", nativeQuery = true)
	List<Integer> getapproverTwoSecondHalfSchedulerDate();

	@Query(value = "select appr_two_first_half from cron_date_model", nativeQuery = true)
	List<Integer> getapproverTwoFirstHalfSchedulerDate();

	@Query(value = "select task_track_user_level from cron_date_model", nativeQuery = true)
	List<Integer> getUserLevelSchedulerDate();

	@Query(value = "select task_track from cron_date_model", nativeQuery = true)
	List<Integer> getTaskTrackSchedulerDate();

}

