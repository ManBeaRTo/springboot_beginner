package com.example.trial_one.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.example.trial_one.dto.NewPokemonDto;
import com.example.trial_one.model.Pokemon;
import com.example.trial_one.repository.PokemonRepository;
import com.example.trial_one.service.NewPokemonService;

@Service
public class NewPokemonServiceImpl implements NewPokemonService {
	@Autowired
	private PokemonRepository pokemonRepository;

	private static final String UPLOAD_DIR = "/home/rambo/Workspace/spring/trial_one/uploads/";

	@Override
	public Pokemon newCreatePokemon(NewPokemonDto pokemonDto) {
		Pokemon pokemon = new Pokemon();
		pokemon.setName(pokemonDto.getName());
		pokemon.setType(pokemonDto.getType());
		pokemon.setCombat_power(pokemonDto.getCombat_power());
		pokemon.setPicturePath(pokemonDto.getPicturePath());
		pokemon.setDescription(pokemonDto.getDescription());
		pokemon.setStatus("active");

		pokemonRepository.save(pokemon);
		return pokemon;
	}

	@Override
	public void updatePokemonPicture(int id, String picturePath) {
		Pokemon pokemon = pokemonRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Pokemon not found"));
		
		// Extract filename components
		String fileNameWithoutExt = picturePath.contains(".") ? 
			picturePath.substring(0, picturePath.lastIndexOf('.')) : picturePath;
		String extension = picturePath.contains(".") ? 
			picturePath.substring(picturePath.lastIndexOf('.')) : "";
		
		// Append pokemon's name and combat power to create a unique filename
		String finalFileName = fileNameWithoutExt + "_" + pokemon.getName() + "_" + pokemon.getCombat_power() + extension;
		
		// Update the Pokemon entity with the new filename
		pokemon.setPicturePath(finalFileName);
		pokemonRepository.save(pokemon);
	}

	@Override
	public Pokemon createPokemonWithPicture(NewPokemonDto pokemonDto, MultipartFile picture) throws IOException {
		// First create and save the Pokemon
		Pokemon savedPokemon = newCreatePokemon(pokemonDto);
		
		// Handle image upload if there is a picture
		if (savedPokemon != null && picture != null && !picture.isEmpty()) {
			Path uploadPath = Paths.get(UPLOAD_DIR);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			
			String fileName = picture.getOriginalFilename();
			String extension = fileName.contains(".") ? 
				fileName.substring(fileName.lastIndexOf('.')) : "";
				
			// Create the unique filename
			String finalFileName = savedPokemon.getName() + "_" + savedPokemon.getCombat_power() + extension;
			
			// Use the final filename for the actual file
			File destinationFile = new File(UPLOAD_DIR + finalFileName);
			picture.transferTo(destinationFile);
			
			// Update the Pokemon with the picture path
			savedPokemon.setPicturePath(finalFileName);
			pokemonRepository.save(savedPokemon);
		}
		
		return savedPokemon;
	}
}
