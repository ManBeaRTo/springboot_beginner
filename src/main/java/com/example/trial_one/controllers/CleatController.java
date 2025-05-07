package com.example.trial_one.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.trial_one.dto.CleatDto;
import com.example.trial_one.service.CleatService;

@RestController
@RequestMapping("/trial_one/")
public class CleatController {

	CleatService cleatService;
	
	@Autowired
	CleatController(CleatService cleatService)
	{
		this.cleatService = cleatService;
	}
	
	@PostMapping("cleat/create")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<CleatDto> createCleat(@RequestBody CleatDto cleatDto)
	{
		return new ResponseEntity<>(cleatService.createCleat(cleatDto), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/cleat/{id}/delete")
	public ResponseEntity<String> deleteCleat(@PathVariable("id") int cleatId)
	{
		cleatService.deleteCleatId(cleatId);
		return new ResponseEntity<>("Cleat Delete", HttpStatus.OK);
	}
}
