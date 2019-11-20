package com.EMS.listener;

import javax.persistence.EntityManager;
import javax.persistence.PreRemove;

import com.EMS.model.Auditable;
import com.EMS.service.BeanUtil;

public class ModelListener {
	
	@PreRemove
	public void preRemove(Auditable<Long> modelObj){
		EntityManager entityManager = BeanUtil.getBean(EntityManager.class);
		modelObj.setUser_in_action(null);
		entityManager.merge(modelObj);
		entityManager.flush();
	}

}//Renjith
