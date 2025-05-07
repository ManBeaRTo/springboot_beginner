package com.example.trial_one.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.trial_one.dto.NewPokemonDto;
import com.example.trial_one.service.NewPokemonService;

@Controller
public class NewPokemonController implements WebMvcConfigurer
{

	@Autowired
	private NewPokemonService pokemonService;
	
	@GetMapping("add-pokemon-form")
	public String PokemonForm(Model model)
	{
		model.addAttribute("newPokemonDto", new NewPokemonDto());
		return "add-pokemon-form";
	}
	
	@PostMapping("/add-pokemon")
    public String addPokemon(
            NewPokemonDto pokemonDto, // Use PokemonDTO
            RedirectAttributes redirectAttributes) {

        pokemonService.newCreatePokemon(pokemonDto); // Use the service
        redirectAttributes.addFlashAttribute("message", "Pokemon added successfully!");
        return "redirect:/add-pokemon-form";
    }
	
	@GetMapping("/success")
	public String showSuccess(Model model)
	{
		return "success";
	}
}
