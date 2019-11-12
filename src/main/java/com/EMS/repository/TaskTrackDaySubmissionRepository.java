package com.EMS.repository;

import com.EMS.model.TaskTrackDaySubmissionModel;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TaskTrackDaySubmissionRepository extends JpaRepository<TaskTrackDaySubmissionModel,Integer> {
	
	Optional<TaskTrackDaySubmissionModel> findByMonth(int month);
	
	TaskTrackDaySubmissionModel findOneByMonth(Integer month);
}
