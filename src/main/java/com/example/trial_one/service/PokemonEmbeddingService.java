// filepath: /home/rambo/Workspace/spring/trial_one/src/main/java/com/example/trial_one/service/PokemonEmbeddingService.java
package com.example.trial_one.service;

import java.util.List;
import java.util.Map;

import com.example.trial_one.model.Pokemon;

public interface PokemonEmbeddingService {
    void initializeVectorStore();
    void addOrUpdatePokemonInVectorStore(Pokemon pokemon);
    void deletePokemonFromVectorStore(int pokemonId);
    List<Map<String, Object>> searchPokemon(String query, int topK);
}