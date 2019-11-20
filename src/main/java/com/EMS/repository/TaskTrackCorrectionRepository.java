package com.EMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.EMS.model.TaskTrackCorrection;
import com.EMS.utility.Constants;

@Repository
public interface TaskTrackCorrectionRepository extends JpaRepository<TaskTrackCorrection, Long> {

	@Query("SELECT correction FROM TaskTrackCorrection correction "
			+ "where correction.user.userId = ?1 and correction.project.projectId = ?2 "
			+ "and correction.month = ?3 and correction.year = ?4 and correction.day >= ?5 and correction.day <= ?6 and correction.status = '"
			+ Constants.TASKTRACK_CORRECTION_STATUS_OPEN + "'")
	List<TaskTrackCorrection> findCorrectionDays(Long userId, Long projectId, Integer month, Integer year,
			Integer firstDay, Integer lastDay);

	@Query("SELECT COUNT(*) FROM TaskTrackCorrection correction "
			+ "where correction.user.userId = ?1 and correction.project.projectId = ?2 "
			+ "and correction.month = ?3 and correction.year = ?4 and correction.day = ?5 and status='"+Constants.TASKTRACK_CORRECTION_STATUS_OPEN+"' ")
	int checkExist(Long userId, Long projectId, Integer month, Integer year, Integer day);
	//Nisha
	@Query("SELECT correction FROM TaskTrackCorrection correction "
			+ "where correction.user.userId = ?1 and correction.project.projectId = ?2 "
			+ "and correction.month = ?3 and correction.year = ?4 and correction.day = ?5 and status !='"+Constants.TASKTRACK_CORRECTION_STATUS_CLOSED+"'")
	List<TaskTrackCorrection> getTaskCorrectData(Long userId, Long projectId, Integer month, Integer year, Integer day);

}
