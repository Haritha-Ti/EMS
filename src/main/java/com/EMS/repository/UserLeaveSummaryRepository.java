package com.EMS.repository;

import com.EMS.model.UserLeaveSummary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface UserLeaveSummaryRepository extends JpaRepository<UserLeaveSummary, Long>{

    @Query(value = "select COUNT(*) from user_leave_summary where user_leave_summary_id=?1 ",nativeQuery = true)
    int isExist(long leaveSummaryId);

    @Query(value = "select * from user_leave_summary where user_user_id=?1",nativeQuery = true)
    List<UserLeaveSummary> getUserLeaveSummaryList(long userId);


    @Query(value = "select * from user_leave_summary where user_leave_summary_id=?1",nativeQuery = true)
    UserLeaveSummary getLeaveDetailsById(long userLeaveSummaryId);

    @Query(value = "select COUNT(*) from user_leave_summary where user_user_id=?1 ",nativeQuery = true)
    int isUserExist(long userId);

    @Query(value = "select COUNT(*) from user_leave_summary where user_user_id=?1 and leave_date<=?3 and leave_date>=?2 and leave_type='FD'",nativeQuery = true)
    int getFullDayLeaveDays(long userId, Date startDate, Date endDate);

    @Query(value = "select COUNT(*) from user_leave_summary where user_user_id=?1 and leave_date<=?3 and leave_date>=?2 and leave_type='HD'",nativeQuery = true)
    int getHalfDayLeaveDays(long userId, Date startDate, Date endDate);

    @Query("select u from UserLeaveSummary u where u.leaveDate<=?2 and u.leaveDate>=?1 order by u.user.firstName")
    List<UserLeaveSummary> getUserLeaveListByMonth(Date startDate, Date endDate);
    //@Query(value = "select CONCAT(u.first_name,' ',u.last_name) as name,leave_date,u.contractor_contractor_id from user_leave_summary us inner join user u ON us.user_user_id = u.user_id  where  leave_date<=?2 and leave_date>=?1",nativeQuery = true)
   // List<Object[]> getUserLeaveListByMonth(Date startDate, Date endDate);

    @Query("select u from UserLeaveSummary u where u.leaveDate<=?2 and u.leaveDate>=?1 and u.user.region.id = ?3 order by u.user.firstName")
    List<UserLeaveSummary> getUserLeaveListByMonthRegion(Date startDate, Date endDate,Long regionId);

}
