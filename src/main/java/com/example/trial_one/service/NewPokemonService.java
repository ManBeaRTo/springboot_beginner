package com.example.trial_one.service;

import com.example.trial_one.dto.NewPokemonDto;
import com.example.trial_one.model.Pokemon;

public interface NewPokemonService {
	Pokemon newCreatePokemon(NewPokemonDto pokemonDto);
}
