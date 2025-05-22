package com.example.trial_one.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
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

@Service
public class PokemonServiceImpl implements PokemonService {
	private PokemonRepository pokemonRepository;
	private static final String UPLOAD_DIR = "/home/rambo/Workspace/spring/trial_one/uploads/";

	@Autowired
	private PokemonEmbeddingServiceImpl pokemonEmbeddingService;

	@Override
	public PokemonDto updatePokemonWithPicture(int id, PokemonDto pokemonDto, MultipartFile picture,
			RedirectAttributes redirectAttributes) {
		// Get the current Pokemon to find its existing picture path
		PokemonDto existingPokemon = getPokemonById(id);
		String oldPicturePath = existingPokemon.getPicturePath();

		// If no new picture is uploaded, keep the old picture path
		if (picture == null || picture.isEmpty()) {
			pokemonDto.setPicturePath(oldPicturePath);
			return updatePokemon(pokemonDto, id);
		}

		// First update the Pokemon details
		PokemonDto updatedPokemon = updatePokemon(pokemonDto, id);

		// Process the new picture
		try {
			Path uploadPath = Paths.get(UPLOAD_DIR);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			String fileName = picture.getOriginalFilename();

			// Create unique filename with pokemon details
			String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";
			String finalFileName = pokemonDto.getName() + "_" + pokemonDto.getCombat_power() + extension;

			// Check if the new filename is the same as the old one
			boolean sameFileName = oldPicturePath != null && oldPicturePath.equals(finalFileName);
			File destinationFile;

			if (sameFileName) {
				// Use a temporary filename to avoid conflicts
				String tempFileName = finalFileName + ".temp";
				destinationFile = new File(UPLOAD_DIR + tempFileName);

				// Upload to temporary file
				picture.transferTo(destinationFile);

				// Delete the old file
				File oldFile = new File(UPLOAD_DIR + oldPicturePath);
				if (oldFile.exists()) {
					if (oldFile.delete()) {
						System.out.println("Old image file deleted successfully: " + oldPicturePath);
					} else {
						System.err.println("Failed to delete old image file: " + oldPicturePath);
					}
				}

				// Rename temp file to final filename
				File finalFile = new File(UPLOAD_DIR + finalFileName);
				if (destinationFile.renameTo(finalFile)) {
					System.out.println("Renamed temp file to final filename: " + finalFileName);
				} else {
					System.err.println("Failed to rename temp file. Using temp filename instead.");
					finalFileName = tempFileName; // Use the temp filename if rename fails
				}
			} else {
				// Normal case - different filenames
				destinationFile = new File(UPLOAD_DIR + finalFileName);

				// Upload the new file
				picture.transferTo(destinationFile);

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
			}

			// Update the picture path in database
			updatedPokemon.setPicturePath(finalFileName);
			updatedPokemon = updatePokemon(updatedPokemon, id);

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "Could not upload file.");
			return updatedPokemon;
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

		// Update vector store
		pokemonEmbeddingService.addOrUpdatePokemonInVectorStore(newPokemon);

		PokemonDto pokemonResponse = new PokemonDto();
		pokemonResponse.setId(newPokemon.getId());
		pokemonResponse.setName(newPokemon.getName());
		pokemonResponse.setType(newPokemon.getType());
		pokemonResponse.setPicturePath(newPokemon.getPicturePath());
		pokemonResponse.setDescription(newPokemon.getDescription());

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
		pokemon.setDescription(pokemonDto.getDescription());

		Pokemon updatedPokemon = pokemonRepository.save(pokemon);

		// Update vector store
		pokemonEmbeddingService.addOrUpdatePokemonInVectorStore(updatedPokemon);

		return mapToDto(updatedPokemon);
	}

	@Override
	public PokemonResponse getAllPokemon(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Pokemon> pokemons = pokemonRepository.findAllByOrderByIdDesc(pageable);
		List<Pokemon> listOfPokemon = pokemons.getContent();
		List<PokemonDto> content = listOfPokemon.stream()
				.filter(pokemon -> "active".equals(pokemon.getStatus()))
				.map(p -> mapToDto(p))
				.collect(Collectors.toList());

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
		pokemonDto.setAuditDetails(pokemon.getAuditDetails());
		pokemonDto.setDescription(pokemon.getDescription());
		return pokemonDto;
	}

	private Pokemon mapToEntiry(PokemonDto pokemonDto) {
		Pokemon pokemon = new Pokemon();
		pokemon.setName(pokemonDto.getName());
		pokemon.setType(pokemonDto.getType());
		pokemon.setCombat_power(pokemonDto.getCombat_power());
		pokemon.setPicturePath(pokemonDto.getPicturePath());
		pokemon.setAuditDetails(pokemonDto.getAuditDetails());
		pokemon.setDescription(pokemonDto.getDescription());
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

		// Implement soft delete by changing status
		pokemon.setStatus("inactive");

		// Handle the associated image file if it exists
		if (pokemon.getPicturePath() != null && !pokemon.getPicturePath().isEmpty()) {
			try {
				// Create delete directory if it doesn't exist
				Path deleteDir = Paths.get(UPLOAD_DIR, "deleted");
				if (!Files.exists(deleteDir)) {
					Files.createDirectories(deleteDir);
				}

				// Original file
				Path originalPath = Paths.get(UPLOAD_DIR, pokemon.getPicturePath());
				File originalFile = originalPath.toFile();

				if (originalFile.exists()) {
					// Create path for file in the deleted folder
					Path destPath = Paths.get(UPLOAD_DIR, "deleted", pokemon.getPicturePath());
					File destFile = destPath.toFile();

					// If a file with the same name already exists in the deleted folder,
					// add a timestamp to make it unique
					if (destFile.exists()) {
						String fileName = pokemon.getPicturePath();
						String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";
						String nameWithoutExt = fileName.contains(".")
								? fileName.substring(0, fileName.lastIndexOf('.'))
								: fileName;

						// Add timestamp to create unique name
						String uniqueName = nameWithoutExt + "_" + System.currentTimeMillis() + extension;
						destPath = Paths.get(UPLOAD_DIR, "deleted", uniqueName);
						destFile = destPath.toFile();
					}

					// Move file to deleted folder
					boolean moved = originalFile.renameTo(destFile);

					if (moved) {
						System.out.println("File moved successfully to: " + destFile.getPath());
						// Update the path in the database to point to the new location
						pokemon.setPicturePath("deleted/" + destFile.getName());
					} else {
						System.err.println("Failed to move file to deleted folder. Check permissions.");
					}
				} else {
					System.out.println("Image file not found: " + originalPath);
				}
			} catch (Exception e) {
				System.err.println("Error moving image file: " + e.getMessage());
				e.printStackTrace();
			}
		}

		// Save the updated Pokemon with inactive status and updated picture path
		pokemonRepository.save(pokemon);

		 // Update vector store (remove from embeddings since it's inactive)
		pokemonEmbeddingService.deletePokemonFromVectorStore(id);

		// Log the soft delete operation
		System.out.println("Pokemon ID " + id + " soft deleted (marked as inactive)");
	}

	@Override
	public List<PokemonDto> getAllPokemon() {
		return pokemonRepository.findAll().stream()
				.filter(pokemon -> "active".equals(pokemon.getStatus()))
				.map(this::mapToDto)
				.collect(Collectors.toList());
	}

	@Override
	public Map<String, Object> getPokemonData() {
		Map<String, Object> result = new HashMap<>();
		List<PokemonDto> allPokemon = getAllPokemon();

		result.put("all", allPokemon.size());
		return result;
	}

	@Override
	public Long getAllInactivePokemon() {
		return pokemonRepository.findAll().stream()
				.filter(pokemon -> "inactive".equals(pokemon.getStatus()))
				.count();
	}	
}