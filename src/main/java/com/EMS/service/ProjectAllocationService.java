package com.EMS.service;

import java.util.Date;
import java.util.List;
import com.EMS.model.AllocationModel;
import com.EMS.model.DepartmentModel;
import com.EMS.model.UserModel;



public interface ProjectAllocationService {

	public void save(AllocationModel allocationModel);
	public List<AllocationModel> getList();
	public AllocationModel findDataById(Long id);
	public void remove(Long id);
	public AllocationModel updateData(AllocationModel allocationModel);
	public AllocationModel updatePartially(AllocationModel allocationModel, Long id);
	public List<AllocationModel> getAllocationList(Long projectId);
	public List<DepartmentModel> getDepartmentList();
	public List<UserModel> getUserList();
	public List<AllocationModel> getAllocationLists();
	public Boolean checkIsExist(long userId);
	public List<AllocationModel> getListByUser(long userId);
	public List<AllocationModel> getUsersList(long userId, Date date1, Date date2);
	public Long getAllocId(long parseLong, long parseLong2);
	

}