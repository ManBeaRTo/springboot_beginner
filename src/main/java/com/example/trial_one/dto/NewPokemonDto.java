package com.example.trial_one.dto;

import lombok.Data;

@Data
public class NewPokemonDto {
	private String name;
	private String type;
	private int combat_power;
	private String picturePath;
	private String status;
	private String description;
}
