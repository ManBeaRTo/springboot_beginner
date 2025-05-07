package com.example.trial_one.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.trial_one.dto.PokemonDto;
import com.example.trial_one.service.PokemonService;

@Controller
public class VehicleViewController 
{
	@Autowired
	PokemonService pokemonService;
	@GetMapping("/pricing")
	public String View(Model modal)
	{
		List<PokemonDto> pokemonList = pokemonService.getAllPokemon();
		modal.addAttribute("pokemons", pokemonList);
		
		Map<String, Object>pokemonData = pokemonService.getPokemonData();
		modal.addAttribute("pokemonData", pokemonData);
		
		return "pricing";
	}
	
	@PostMapping("/pokemon/{id}/delete")
	public String deletePokemon(@PathVariable("id") int pokemonId)
	{
		pokemonService.deletePokemonId(pokemonId);
		return "redirect:/pricing";
	}
}
