package com.EMS.service;

import java.util.List;
import com.EMS.model.PageRule;
import com.fasterxml.jackson.databind.node.ArrayNode;


public interface PageRuleService {


	ArrayNode getBlockedPageList(long roleId);



}

