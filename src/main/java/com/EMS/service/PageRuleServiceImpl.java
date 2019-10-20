package com.EMS.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.PageRule;

import com.EMS.repository.PageRuleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Service
public class PageRuleServiceImpl implements PageRuleService {
		
	@Autowired
	PageRuleRepository pageRuleRepository;
	
	@Autowired
	ObjectMapper objectMapper;


	@Override
	public ArrayNode getBlockedPageList(long roleId) {
		ObjectNode node = objectMapper.createObjectNode();
		ArrayNode allowedpages = objectMapper.createArrayNode();
		//parents only
		List<PageRule> allowedList = pageRuleRepository.getBlockedList(roleId);
		
		for(PageRule rules : allowedList) {
			List<PageRule> childs = pageRuleRepository.getChildsParent(rules.getId());
			System.out.println("childs--------------->"+childs.size());
			ObjectNode eachpage = objectMapper.createObjectNode();
			ArrayNode allowedchilds = objectMapper.createArrayNode();
			for(PageRule child : childs) {
				ObjectNode nodes = objectMapper.createObjectNode();
				nodes.put("page_id", child.getId());
				nodes.put("key", child.getPageKey());
				nodes.put("path", child.getPath());
				nodes.put("icon",child.getIcon());
				nodes.put("label", child.getLabel());
				nodes.put("level", child.getLevel_Id());
				allowedchilds.add(nodes);
			}
            eachpage.set("childs", allowedchilds);
			eachpage.put("level", rules.getLevel_Id());
			eachpage.put("key", rules.getPageKey());
			eachpage.put("path", rules.getPath());
			eachpage.put("icon",rules.getIcon());
			eachpage.put("label", rules.getLabel());
			eachpage.put("page_id", rules.getId());
			allowedpages.add(eachpage);
			
			
		}
		
		return allowedpages;
	}




	

}
