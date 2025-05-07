package com.example.trial_one.service;

import com.example.trial_one.dto.CleatDto;

public interface CleatService {
		
	CleatDto createCleat(CleatDto cleatDto);
	void deleteCleatId(int id);

}
