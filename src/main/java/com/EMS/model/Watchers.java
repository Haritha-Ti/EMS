package com.EMS.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ManyToAny;

public class Watchers extends Auditable<Long>  {

	@Id
	@Column
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long watcherId;
	
	@ManyToOne	
	private UserModel watcher;
	
	@ManyToOne
	private AllocationModel allocationId;

	public long getWatcherId() {
		return watcherId;
	}

	public void setWatcherId(long watcherId) {
		this.watcherId = watcherId;
	}

	public UserModel getWatcher() {
		return watcher;
	}

	public void setWatcher(UserModel watcher) {
		this.watcher = watcher;
	}

	public AllocationModel getAllocationId() {
		return allocationId;
	}

	public void setAllocationId(AllocationModel allocationId) {
		this.allocationId = allocationId;
	}
	
	
	
	
}
