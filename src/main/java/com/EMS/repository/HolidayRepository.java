package com.EMS.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EMS.model.HolidayModel;

public interface HolidayRepository extends JpaRepository<HolidayModel, Long>{

	@Query(value = "SELECT Date(holiday.`date`) as `date` ,holiday.holiday_id,holiday.day,holiday.holiday_name,holiday.holiday_type,region.id,region.region_name FROM holiday INNER JOIN region on (region.id = holiday.region_id_id) WHERE  holiday.is_deleted = false",nativeQuery = true)
	List<Object[]> getHolidayLists();

	@Query(value = "SELECT COUNT(date) as `total_holiday`  FROM holiday where holiday_type='National Holiday' and date <=?2 and date >=?1",nativeQuery = true)
	int getNationalHolidayListsByMonth(Date startDate,Date endDate);

	
	
	@Query(value = "SELECT Date(holiday.`date`) as `date` ,holiday.holiday_id,holiday.day, " + 
			" holiday.holiday_name,holiday.holiday_type,region.id,region.region_name " + 
			" FROM holiday " + 
			" INNER JOIN region on (region.id = holiday.region_id_id) " + 
			" WHERE (CASE WHEN :region_Id != 0  " + 
			"       THEN holiday.region_id_id = :region_Id " + 
			"       ELSE holiday.region_id_id != 0 END) AND  (CASE WHEN :monthyear !=  '' " + 
			"        THEN  holiday.date like %:monthyear% " + 
			"        ELSE holiday.date!= 0 END)  ",nativeQuery = true)
	List<Object[]> getHolidayListByRegionId(@Param("region_Id") Long region_Id,@Param("monthyear") String monthyear);

	@Query(value = "SELECT Date(holiday.`date`) as `date` ,holiday.holiday_id,holiday.day,holiday.holiday_name,holiday.holiday_type,region.id,region.region_name FROM holiday INNER JOIN region on (region.id = holiday.region_id_id) where holiday.holiday_id = ?1 ",nativeQuery = true)
	List<Object[]> getHolidayDetails(Long holiday_id);

	@Query(value = "SELECT COUNT(date) as `total_holiday`  FROM holiday where holiday_type='National Holiday' and date <=?2 and date >=?1 and region_id_id = ?3",nativeQuery = true)
	int getNationalHolidayListsByMonthRegion(Date startDate,Date endDate,Long regionId);
	
	@Query(value = "SELECT Date(`date`) as `date` FROM holiday", nativeQuery = true)
	List <Date>  getHolidayDateList();
}
