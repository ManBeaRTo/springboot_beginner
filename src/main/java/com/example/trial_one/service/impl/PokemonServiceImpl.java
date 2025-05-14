package com.example.trial_one.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.trial_one.dto.PokemonDto;
import com.example.trial_one.dto.PokemonResponse;
import com.example.trial_one.exceptions.PokemonNotFoundException;
import com.example.trial_one.model.Pokemon;
import com.example.trial_one.repository.PokemonRepository;
import com.example.trial_one.service.PokemonService;

import java.nio.file.Path;

@Service
public class PokemonServiceImpl implements PokemonService {
	private PokemonRepository pokemonRepository;
	private static final String UPLOAD_DIR = "/home/rambo/Workspace/spring/trial_one/uploads/";
	
	@Override
	public PokemonDto updatePokemonWithPicture(int id, PokemonDto pokemonDto, MultipartFile picture, RedirectAttributes redirectAttributes) {
	    // Get the current Pokemon to find its existing picture path
	    PokemonDto existingPokemon = getPokemonById(id);
	    String oldPicturePath = existingPokemon.getPicturePath();
	    
	    // First update the Pokemon details
	    PokemonDto updatedPokemon = updatePokemon(pokemonDto, id);
	    
	    // Only process picture if a new one was uploaded
	    if (!picture.isEmpty()) {
	        try {
	            Path uploadPath = Paths.get(UPLOAD_DIR);
	            if (!Files.exists(uploadPath)) {
	                Files.createDirectories(uploadPath);
	            }
	            
	            String fileName = picture.getOriginalFilename();
	            
	            // Create unique filename with pokemon details
	            String extension = fileName.contains(".") ? 
	                fileName.substring(fileName.lastIndexOf('.')) : "";
	            String finalFileName = pokemonDto.getName() + "_" + pokemonDto.getCombat_power() + extension;
	            
	            File destinationFile = new File(UPLOAD_DIR + finalFileName);
	            
	            // Upload the new file
	            picture.transferTo(destinationFile);
	            
	            // Update the picture path in database
	            updatedPokemon.setPicturePath(finalFileName);
	            updatedPokemon = updatePokemon(updatedPokemon, id);
	            
	            // Delete the old picture file if it exists
	            if (oldPicturePath != null && !oldPicturePath.isEmpty()) {
	                File oldFile = new File(UPLOAD_DIR + oldPicturePath);
	                if (oldFile.exists()) {
	                    if (oldFile.delete()) {
	                        System.out.println("Old image file deleted successfully: " + oldPicturePath);
	                    } else {
	                        System.err.println("Failed to delete old image file: " + oldPicturePath);
	                    }
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            redirectAttributes.addFlashAttribute("message", "Could not upload file.");
	            return updatedPokemon;
	        }
	    }
	    
	    return updatedPokemon;
	}
	
	@Autowired
	public PokemonServiceImpl(PokemonRepository pokemonRepository) {
		this.pokemonRepository = pokemonRepository;
	}

	@Override
	public PokemonDto createPokemon(PokemonDto pokemonDto) {

		Pokemon pokemon = new Pokemon();
		pokemon.setName(pokemonDto.getName());
		pokemon.setType(pokemonDto.getType());
		pokemon.setType(pokemonDto.getPicturePath());

		Pokemon newPokemon = pokemonRepository.save(pokemon);

		PokemonDto pokemonResponse = new PokemonDto();
		pokemonResponse.setId(newPokemon.getId());
		pokemonResponse.setName(newPokemon.getName());
		pokemonResponse.setType(newPokemon.getType());
		pokemonResponse.setPicturePath(newPokemon.getPicturePath());
		return pokemonResponse;
	}

	@Override
	public PokemonDto updatePokemon(PokemonDto pokemonDto, int id) {
		Pokemon pokemon = pokemonRepository.findById(id)
				.orElseThrow(() -> new PokemonNotFoundException("Pokemon could not be updated"));

		pokemon.setName(pokemonDto.getName());
		pokemon.setType(pokemonDto.getType());
		pokemon.setCombat_power(pokemonDto.getCombat_power());
		pokemon.setPicturePath(pokemonDto.getPicturePath());

		Pokemon updatedPokemon = pokemonRepository.save(pokemon);
		return mapToDto(updatedPokemon);
	}

	@Override
	public PokemonResponse getAllPokemon(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Pokemon> pokemons = pokemonRepository.findAllByOrderByIdDesc(pageable);
		List<Pokemon> listOfPokemon = pokemons.getContent();
		List<PokemonDto> content = listOfPokemon.stream().map(p -> mapToDto(p)).collect(Collectors.toList());

		PokemonResponse pokemonResponse = new PokemonResponse();
		pokemonResponse.setContent(content);
		pokemonResponse.setPageNo(pokemons.getNumber());
		pokemonResponse.setPageSize(pokemons.getSize());
		pokemonResponse.setTotalElements(pokemons.getTotalElements());
		pokemonResponse.setTotalPages(pokemons.getTotalPages());
		pokemonResponse.setLast(pokemons.isLast());

		return pokemonResponse;
	}

	private PokemonDto mapToDto(Pokemon pokemon) {
		PokemonDto pokemonDto = new PokemonDto();
		pokemonDto.setId(pokemon.getId());
		pokemonDto.setName(pokemon.getName());
		pokemonDto.setType(pokemon.getType());
		pokemonDto.setCombat_power(pokemon.getCombat_power());
		pokemonDto.setPicturePath(pokemon.getPicturePath());
		return pokemonDto;
	}

	private Pokemon mapToEntiry(PokemonDto pokemonDto) {
		Pokemon pokemon = new Pokemon();
		pokemon.setName(pokemonDto.getName());
		pokemon.setType(pokemonDto.getType());
		pokemon.setCombat_power(pokemonDto.getCombat_power());
		pokemon.setPicturePath(pokemonDto.getPicturePath());
		return pokemon;
	}

	@Override
	public PokemonDto getPokemonById(int id) {
		Pokemon pokemon = pokemonRepository.findById(id)
				.orElseThrow(() -> new PokemonNotFoundException("Pokemon could not be found"));
		return mapToDto(pokemon);
	}

	@Override
	public void deletePokemonId(int id) {
		Pokemon pokemon = pokemonRepository.findById(id)
				.orElseThrow(() -> new PokemonNotFoundException("Pokemon could not be found"));
		
		// Delete the associated image file if it exists
		if (pokemon.getPicturePath() != null && !pokemon.getPicturePath().isEmpty()) {
			try {
				File imageFile = new File(UPLOAD_DIR + pokemon.getPicturePath());
				if (imageFile.exists()) {
					imageFile.delete();
				}
			} catch (Exception e) {
				// Log the error but continue with deletion
				System.err.println("Error deleting image file: " + e.getMessage());
			}
		}
		
		pokemonRepository.delete(pokemon);
	}

	@Override
	public List<PokemonDto> getAllPokemon() {
		List<Pokemon> pokemons = pokemonRepository.findAll();
		return pokemonRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
	}

	@Override
	public Map<String, Object> getPokemonData() {
		Map<String, Object> result = new HashMap<>();
		List<PokemonDto> allPokemon = getAllPokemon();

		result.put("all", allPokemon.size());
		return result;
	}
}
