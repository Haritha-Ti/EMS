package com.EMS.service;

import java.util.ArrayList;

import com.EMS.model.Technology;

public interface TechnologyService {

	int duplicationchecking(String technologyName);

	Technology save_technology_record(Technology technology);

	Technology getTechnologydata(long techId);

	ArrayList<Technology> getTechnologyList();

}
