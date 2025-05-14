package com.example.trial_one.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.example.trial_one.dto.NewPokemonDto;
import com.example.trial_one.model.Pokemon;

public interface NewPokemonService {
	Pokemon newCreatePokemon(NewPokemonDto pokemonDto);
	void updatePokemonPicture(int id, String picturePath);
	Pokemon createPokemonWithPicture(NewPokemonDto pokemonDto, MultipartFile picture) throws IOException;

}
