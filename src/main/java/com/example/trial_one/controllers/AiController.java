package com.example.trial_one.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.trial_one.dto.PokemonDto;
import com.example.trial_one.repository.PokemonRepository;
import com.example.trial_one.service.PokemonService;
import com.example.trial_one.service.impl.PokemonEmbeddingServiceImpl;

@Controller
@RequestMapping("/ai")
public class AiController {
    
    @Autowired
    private PokemonEmbeddingServiceImpl pokemonEmbeddingService;
    
    @Autowired
    private PokemonService pokemonService;
    
    @GetMapping("/search")
    public String showSearchPage(Model model) {
        model.addAttribute("pokemonCount", pokemonService.getAllPokemon().size());
        return "ai_search";
    }
    
    @GetMapping("/search-results")
    public String searchPokemon(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK,
            Model model) {
        
        // Perform semantic search
        List<Map<String, Object>> searchResults = pokemonEmbeddingService.searchPokemon(query, topK);
        
        // Convert the search results to Pokemon objects
        List<PokemonDto> pokemonResults = new ArrayList<>();
        
        for (Map<String, Object> result : searchResults) {
            String pokemonIdStr = (String) result.get("pokemon_id");
            if (pokemonIdStr != null) {
                try {
                    int pokemonId = Integer.parseInt(pokemonIdStr);
                    PokemonDto pokemon = pokemonService.getPokemonById(pokemonId);
                    pokemonResults.add(pokemon);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid Pokemon ID format: " + pokemonIdStr);
                } catch (Exception e) {
                    System.err.println("Error fetching Pokemon with ID " + pokemonIdStr + ": " + e.getMessage());
                }
            }
        }
        
        // Add results to model
        model.addAttribute("query", query);
        model.addAttribute("pokemonResults", pokemonResults);
        model.addAttribute("resultCount", pokemonResults.size());
        
        return "ai_search"; // Return the same page with results
    }
    
    // Add a hook to integrate with Pokemon CRUD operations
    @GetMapping("/rebuild-index")
    @ResponseBody
    public String rebuildVectorStore() {
        pokemonEmbeddingService.initializeVectorStore();
        return "Vector store rebuilt successfully!";
    }
}