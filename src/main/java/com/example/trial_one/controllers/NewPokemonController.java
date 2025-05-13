package com.example.trial_one.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.trial_one.dto.NewPokemonDto;
import com.example.trial_one.service.NewPokemonService;

@Controller
public class NewPokemonController{

    @Autowired
    private NewPokemonService pokemonService;
    
    // Define your local upload directory; adjust the path as needed.
    private static final String UPLOAD_DIR = "/home/rambo/Workspace/spring/trial_one/uploads/";
    
    @GetMapping("add-pokemon-form")
    public String PokemonForm(Model model) {
        model.addAttribute("newPokemonDto", new NewPokemonDto());
        return "add-pokemon-form";
    }
    
    @PostMapping("/add-pokemon")
    public String addPokemon(NewPokemonDto pokemonDto, @RequestParam("picture") MultipartFile picture, RedirectAttributes redirectAttributes) {
        try {
            pokemonService.newCreatePokemon(pokemonDto);
            redirectAttributes.addFlashAttribute("message", "Pokemon added successfully!");
            
            // Only proceed with image upload if Pokemon was added successfully
            if (!picture.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = picture.getOriginalFilename();
                File destinationFile = new File(UPLOAD_DIR + fileName);
                picture.transferTo(destinationFile);
                pokemonDto.setPicturePath(fileName);
            }
            return "redirect:/success";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to add Pokemon.");
            return "redirect:/add-pokemon-form";
        }
    }
    
    @GetMapping("/success")
    public String showSuccess(Model model) {
        return "success";
    }
}
