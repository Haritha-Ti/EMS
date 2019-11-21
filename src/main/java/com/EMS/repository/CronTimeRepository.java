package com.EMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.CronTimeModel;

public interface CronTimeRepository extends JpaRepository<CronTimeModel, Long>{
	
	@Query(value="Select appr_one_second_half from cron_time_model",nativeQuery=true)
	String getCronDates1();

	@Query(value="Select appr_one_first_half from cron_time_model",nativeQuery=true)
	String getCronDatesForApproverOneFirstHalf();

	@Query(value="Select appr_two_first_half from cron_time_model",nativeQuery=true)
	String getCronDatesForApproverTwoFirstHalf();

	@Query(value="Select appr_two_second_half from cron_time_model",nativeQuery=true)
	String getCronDatesForApproverTwoSecondHalf();

	@Query(value="Select task_track_user_level from cron_time_model",nativeQuery=true)
	String getCronTaskTrackSchedulerAtUserlevel();

	@Query(value="Select task_track from cron_time_model",nativeQuery=true)
	String getCronCreateTaskTrack();

}
