package com.example.trial_one.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.trial_one.dto.PokemonDto;
import com.example.trial_one.dto.PokemonResponse;
import com.example.trial_one.exceptions.GlobalExceptionHandler;
import com.example.trial_one.service.PokemonService;

@Controller
@RequestMapping("/api/")
public class PokemonController {

    private static final String UPLOAD_DIR = "/home/rambo/Workspace/spring/trial_one/uploads/";

    private final GlobalExceptionHandler globalExceptionHandler;
    private PokemonService pokemonService;

    @Autowired
    public PokemonController(PokemonService pokemonService, GlobalExceptionHandler globalExceptionHandler) {
        this.pokemonService = pokemonService;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @GetMapping("pokemon")
    public ResponseEntity<PokemonResponse> getPokemons(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return new ResponseEntity<>(pokemonService.getAllPokemon(pageNo, pageSize), HttpStatus.OK);
    }

    @GetMapping("pokemon/{id}")
    public ResponseEntity<PokemonDto> pokemonDetail(@PathVariable int id) {
        return ResponseEntity.ok(pokemonService.getPokemonById(id));
    }

    @PostMapping("pokemon/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PokemonDto> createPokemon(@RequestBody PokemonDto pokemonDto) {
        return new ResponseEntity<>(pokemonService.createPokemon(pokemonDto), HttpStatus.CREATED);
    }

    // New GET mapping to show the edit form for a Pokemon
    @GetMapping("pokemon/{id}/edit")
    public String showEditForm(@PathVariable int id, Model model) {
        PokemonDto pokemon = pokemonService.getPokemonById(id);
        model.addAttribute("pokemon", pokemon);
        // make sure this view exists as edit-pokemon-form.html or similar
        return "pokemon-edit";
    }

    @PostMapping("pokemon/{id}/edit")
    public String updatePokemon(@PathVariable int id, @RequestParam("picture") MultipartFile picture,
            @ModelAttribute("pokemon") PokemonDto pokemonDto, RedirectAttributes redirectAttributes) {
        
        PokemonDto updatedPokemon = pokemonService.updatePokemonWithPicture(id, pokemonDto, picture, redirectAttributes);
        
        if (updatedPokemon == null) {
            // If the service returns null, there was an error with the file upload
            return "redirect:/add-pokemon-form";
        }
        
        return "redirect:/api/pokemon-table";
    }

    @DeleteMapping("pokemon/{id}/delete")
    public ResponseEntity<String> deletePokemon(@PathVariable int id) {
        pokemonService.deletePokemonId(id);
        return new ResponseEntity<>("Pokemon Deleted", HttpStatus.OK);
    }

    @GetMapping("pokemon/{id}/confirm-delete")
    public String confirmDelete(@PathVariable int id, Model model) {
        PokemonDto pokemon = pokemonService.getPokemonById(id);
        model.addAttribute("pokemon", pokemon);
        return "delete-confirmation";
    }

    @GetMapping("pokemon/{id}/delete-confirmed")
    public String deleteConfirmed(@PathVariable int id) {
        pokemonService.deletePokemonId(id);
        return "redirect:/api/pokemon-table";
    }

    @GetMapping("pokemon-table")
    public String showTable(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PokemonResponse pokemonResponse = pokemonService.getAllPokemon(page, size);
        List<PokemonDto> pokemonDtoList = pokemonResponse.getContent();

        int totalPokemons = (int) pokemonResponse.getTotalElements();
        String strongestPokemonName = pokemonService.getAllPokemon().stream()
                .max((p1, p2) -> Integer.compare(p1.getCombat_power(), p2.getCombat_power()))
                .map(PokemonDto::getName)
                .orElse("N/A");
        String weakestPokemonName = pokemonService.getAllPokemon().stream()
                .min((p1, p2) -> Integer.compare(p1.getCombat_power(), p2.getCombat_power()))
                .map(PokemonDto::getName)
                .orElse("N/A");

        model.addAttribute("pokemonList", pokemonDtoList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pokemonResponse.getTotalPages());
        model.addAttribute("totalItems", pokemonResponse.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPokemons", totalPokemons);
        model.addAttribute("strongestPokemonName", strongestPokemonName);
        model.addAttribute("weakestPokemonName", weakestPokemonName);

        return "pokemon-table";
    }

    @GetMapping("pokemon-view/{id}")
    public String showView(@PathVariable int id, Model model) {
        PokemonDto pokemon = pokemonService.getPokemonById(id);
        model.addAttribute("pokemon", pokemon);
        return "pokemon-view";
    }
}
