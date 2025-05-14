package com.example.trial_one.controllers;

import java.io.IOException;

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
import com.example.trial_one.model.Pokemon;
import com.example.trial_one.utils.FileCheckUtils;

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
    public String addPokemon(NewPokemonDto pokemonDto, @RequestParam("picture") MultipartFile picture, 
                             RedirectAttributes redirectAttributes) {
        try {
            // Verify file type using the utility class
            if (!picture.isEmpty()) {
                if (!FileCheckUtils.isImageFile(picture)) {
                    redirectAttributes.addFlashAttribute("warning_message", 
                        "Invalid file type. Only JPEG, JPG, and PNG files are allowed.");
                    return "redirect:/add-pokemon-form";
                }
                
                // For extra security, also check file signature
                try {
                    if (!FileCheckUtils.isValidImageFile(picture)) {
                        redirectAttributes.addFlashAttribute("warning_message", 
                            "Invalid image content. The file does not appear to be a valid image.");
                        return "redirect:/add-pokemon-form";
                    }
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("warning_message", 
                        "Could not verify file contents: " + e.getMessage());
                    return "redirect:/add-pokemon-form";
                }
            }
            
            Pokemon savedPokemon = pokemonService.createPokemonWithPicture(pokemonDto, picture);
            
            redirectAttributes.addFlashAttribute("message", "Pokemon added successfully!");
            return "redirect:/success";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("pictureMessage", 
                "Pokemon saved, but picture upload failed: " + e.getMessage());
            return "redirect:/success";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("warning_message", "Failed to add Pokemon: " + e.getMessage());
            return "redirect:/add-pokemon-form";
        }
    }
    
    @GetMapping("/success")
    public String showSuccess(Model model) {
        return "success";
    }
}
