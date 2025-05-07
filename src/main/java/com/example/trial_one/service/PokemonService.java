package com.example.trial_one.service;

import java.util.List;
import java.util.Map;

import com.example.trial_one.dto.PokemonDto;
import com.example.trial_one.dto.PokemonResponse;

public interface PokemonService 
{
	PokemonDto createPokemon(PokemonDto pokemonDto);
	PokemonResponse getAllPokemon(int pageNo, int pageSize);
	PokemonDto getPokemonById(int id);
	PokemonDto updatePokemon(PokemonDto pokemonDto, int id);
	void deletePokemonId(int id);
	
	List<PokemonDto> getAllPokemon();
	Map<String, Object> getPokemonData();
}