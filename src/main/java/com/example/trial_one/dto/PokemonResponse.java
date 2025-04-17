package com.example.trial_one.dto;

import java.util.List;

import lombok.Data;

@Data
public class PokemonResponse {
	private List<PokemonDto> content;
	private int pageNo;
	private int pageSize;
	private long totalElements;
	private int totalPages;
	private boolean last;
}
