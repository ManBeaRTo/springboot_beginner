package com.example.trial_one.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.trial_one.dto.PokemonDto;
import com.example.trial_one.dto.PokemonResponse;

public interface PokemonService 
{
	PokemonDto createPokemon(PokemonDto pokemonDto);
	PokemonResponse getAllPokemon(int pageNo, int pageSize);
	PokemonDto getPokemonById(int id);
	PokemonDto updatePokemon(PokemonDto pokemonDto, int id);
	void deletePokemonId(int id);
	PokemonDto updatePokemonWithPicture(int id, PokemonDto pokemonDto, MultipartFile picture, RedirectAttributes redirectAttributes);

	
	List<PokemonDto> getAllPokemon();
	Map<String, Object> getPokemonData();
}