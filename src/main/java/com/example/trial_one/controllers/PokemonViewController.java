package com.example.trial_one.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.trial_one.dto.PokemonDto;
import com.example.trial_one.service.PokemonService;

@Controller
public class PokemonViewController 
{
	@Autowired
	private PokemonService pokemonService;
	
	
	
	@GetMapping("/index")
	public String viewAllPokemon(Model model)
	{
	    List<PokemonDto> pokemons = pokemonService.getAllPokemon();
	    
	    System.out.println("Pokemons list below");
	    for(PokemonDto pokemon : pokemons) 
	    {
	    	System.out.println(pokemon);
	    }
	    model.addAttribute("pokemons", pokemons);
	    return "index";
	}
	
	@GetMapping("/dashboard")
	public String viewDashboard(Model model)
	{
	    List<PokemonDto> pokemons = pokemonService.getAllPokemon();
	    
	    System.out.println("Pokemons list below");
	    for(PokemonDto pokemon : pokemons) 
	    {
	    	System.out.println(pokemon);
	    }
	    model.addAttribute("vehicleRequestList", pokemons);
	    return "dashboard";
	}
	

}