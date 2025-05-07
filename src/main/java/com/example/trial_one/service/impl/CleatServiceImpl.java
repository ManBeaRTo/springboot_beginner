package com.example.trial_one.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.trial_one.dto.CleatDto;
import com.example.trial_one.exceptions.CleatNotFoundException;
import com.example.trial_one.model.Cleat;
import com.example.trial_one.repository.CleatRepository;
import com.example.trial_one.service.CleatService;

@Service
public class CleatServiceImpl implements CleatService {
	private CleatRepository cleatRepository;
	
	@Autowired
	public CleatServiceImpl (CleatRepository cleatRepository)
	{
	this.cleatRepository = cleatRepository;
	}
	
	@Override
	public CleatDto createCleat(CleatDto cleatDto)
	{
		Cleat cleat = new Cleat();
		cleat.setName(cleatDto.getName());
		cleat.setBrand(cleatDto.getBrand());
		
		Cleat newCleat = cleatRepository.save(cleat);
		
		CleatDto cleatResponse = new CleatDto();
		cleatResponse.setId(newCleat.getId());
		cleatResponse.setName(newCleat.getName());
		cleatResponse.setBrand(newCleat.getBrand());
		return cleatResponse;
	}
	
	@Override
	public void deleteCleatId(int id)
	{
		Cleat cleat = cleatRepository.findById(id).orElseThrow(() -> new CleatNotFoundException("Pokemon could not be found"));
		cleatRepository.delete(cleat);
	}
}
