package com.EMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.PageRule;

public interface PageRuleRepository extends JpaRepository<PageRule, Long> {

	@Query(value = "select rule from PageRule rule where rule.roleId = ?1 and rule.level_Id = 0   order by sort ASC ")
	List<PageRule> getBlockedList(Long roleId);

	@Query(value = "select rule from PageRule rule where rule.parent_Id = ?1  order by sort ASC")
	List<PageRule> getChildsParent(long parent_Id);

}
