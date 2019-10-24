package com.EMS.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.Technology;
import com.EMS.repository.TechnologyRepository;

@Service
public class TechnologyServiceImpl implements TechnologyService{

	@Autowired
	TechnologyRepository technologyRepository;
	
	@Override
	public int duplicationchecking(String technologyName) {
		int value = technologyRepository.findTechnology(technologyName);
		return value;
	}

	@Override
	public Technology save_technology_record(Technology technology) {
		Technology tech=technologyRepository.save(technology);
		return tech;
	}

	@Override
	public Technology getTechnologydata(long techId) {
		Technology techData=technologyRepository.getOne(techId);
		return techData;
	}

	@Override
	public ArrayList<Technology> getTechnologyList() {
		ArrayList<Technology> list=(ArrayList<Technology>) technologyRepository.getAll();
		return list;
	}

}
