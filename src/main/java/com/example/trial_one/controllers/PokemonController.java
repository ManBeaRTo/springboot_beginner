package com.example.trial_one.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.trial_one.dto.PokemonDto;
import com.example.trial_one.dto.PokemonResponse;
import com.example.trial_one.exceptions.GlobalExceptionHandler;
import com.example.trial_one.service.PokemonService;

@Controller
@RequestMapping("/api/")
public class PokemonController {

    private final GlobalExceptionHandler globalExceptionHandler;

	private PokemonService pokemonService;
	
	@Autowired
	public PokemonController(PokemonService pokemonService, GlobalExceptionHandler globalExceptionHandler) {
		super();
		this.pokemonService = pokemonService;
this.globalExceptionHandler = globalExceptionHandler;
	}

	@GetMapping("pokemon")
	public ResponseEntity<PokemonResponse> getPokemons(
			@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize)
	{
		return new ResponseEntity<>(pokemonService.getAllPokemon(pageNo, pageSize), HttpStatus.OK);
	}
	
	@GetMapping("pokemon/{id}")
	public ResponseEntity<PokemonDto> pokemonDetail(@PathVariable int id)
	{
		return ResponseEntity.ok(pokemonService.getPokemonById(id));
	} 
	
	@PostMapping("pokemon/create")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<PokemonDto> createPokemon(@RequestBody PokemonDto pokemonDto)
	{
		return new ResponseEntity<>(pokemonService.createPokemon(pokemonDto), HttpStatus.CREATED);
	}
	
	@PutMapping("pokemon/{id}/update")
	public ResponseEntity<PokemonDto> updatePokemon(@RequestBody PokemonDto pokemonDto, @PathVariable("id") int pokemonId)
	{
		PokemonDto response = pokemonService.updatePokemon(pokemonDto, pokemonId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@DeleteMapping("pokemon/{id}/delete")
	public ResponseEntity<String> deletePokemon(@PathVariable("id") int pokemonId)
	{
		pokemonService.deletePokemonId(pokemonId);
		return new ResponseEntity<>("Pokemon Deleted", HttpStatus.OK);
	}

	@GetMapping("pokemon-table")
	public String showTable(
	    Model model,
	    @RequestParam(defaultValue = "0") int page,
	    @RequestParam(defaultValue = "8")int size) {
	    
	    // Get paginated results
	    Pageable pageable = PageRequest.of(page, size);
	    PokemonResponse pokemonResponse = pokemonService.getAllPokemon(page, size);
	    List<PokemonDto> pokemonDtoList = pokemonResponse.getContent();
	    
	    // Calculate stats
	    int totalPokemons = (int) pokemonResponse.getTotalElements();
	    
	    // Strongest pokemon
	    String strongestPokemonName = pokemonService.getAllPokemon().stream()
	        .max((p1, p2) -> Integer.compare(p1.getCombat_power(), p2.getCombat_power()))
	        .map(PokemonDto::getName)
	        .orElse("N/A");
	    
	    // Weakest Pokemon
	    String weakestPokemonName = pokemonService.getAllPokemon().stream()
	        .min((p1, p2) -> Integer.compare(p1.getCombat_power(), p2.getCombat_power()))
	        .map(PokemonDto::getName)
	        .orElse("N/A");
	    
	    // Add pagination attributes
	    model.addAttribute("pokemonList", pokemonDtoList);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", pokemonResponse.getTotalPages());
	    model.addAttribute("totalItems", pokemonResponse.getTotalElements());
		model.addAttribute("pageSize", size);
	    
	    // Add stats attributes
				model.addAttribute("totalPokemons", totalPokemons);
		model.addAttribute("strongestPokemonName", strongestPokemonName);
		model.addAttribute("weakestPokemonName", weakestPokemonName);

		return "pokemon-table";
	}
	
	@GetMapping("pokemon-view/{id}")
	public String showView(@PathVariable int id, Model model)
	{
		PokemonDto pokemon = pokemonService.getPokemonById(id);
		model.addAttribute("pokemon", pokemon);
		
		return "pokemon-view";
	}
	
}
