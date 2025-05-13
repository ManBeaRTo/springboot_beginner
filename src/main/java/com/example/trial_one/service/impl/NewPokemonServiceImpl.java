package com.example.trial_one.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.trial_one.dto.NewPokemonDto;
import com.example.trial_one.model.Pokemon;
import com.example.trial_one.repository.PokemonRepository;
import com.example.trial_one.service.NewPokemonService;

@Service
public class NewPokemonServiceImpl implements NewPokemonService {
	@Autowired
	private PokemonRepository pokemonRepository;
	
	@Override
	public Pokemon newCreatePokemon(NewPokemonDto pokemonDto)
	{
		Pokemon pokemon = new Pokemon();
		pokemon.setName(pokemonDto.getName());
		pokemon.setType(pokemonDto.getType());
		pokemon.setCombat_power(pokemonDto.getCombat_power());
		pokemon. setPicturePath(pokemonDto.getPicturePath());
		pokemonRepository.save(pokemon);
		return pokemon;
	}
}
