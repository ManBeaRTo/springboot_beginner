package com.example.trial_one.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.trial_one.model.Pokemon;
import com.example.trial_one.repository.PokemonRepository;
import com.example.trial_one.service.PokemonEmbeddingService;

import jakarta.annotation.PostConstruct;

@Service
public class PokemonEmbeddingServiceImpl implements PokemonEmbeddingService {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private EmbeddingClient embeddingClient;

    @Autowired
    private PokemonRepository pokemonRepository;

    @PostConstruct
    public void initializeVectorStore() {
        List<Pokemon> allActivePokemon = pokemonRepository.findAll().stream()
                .filter(pokemon -> "active".equals(pokemon.getStatus()))
                .collect(Collectors.toList());

        List<Document> documents = new ArrayList<>();
        for (Pokemon pokemon : allActivePokemon) {
            documents.add(createDocument(pokemon));
        }

        if (!documents.isEmpty()) {
            vectorStore.add(documents);
            System.out.println("Initialized vector store with " + documents.size() + " Pokémon documents");
        }
    }

    private Document createDocument(Pokemon pokemon) {
        // Combine relevant Pokemon data into embeddable content
        String content = String.format(
                "Name: %s. Type: %s. Description: %s.",
                pokemon.getName(),
                pokemon.getType(),
                pokemon.getDescription() != null ? pokemon.getDescription() : "");

        // Add metadata for retrieval
        return new Document(
                content,
                Map.of("pokemon_id", String.valueOf(pokemon.getId()),
                       "name", pokemon.getName(),
                       "type", pokemon.getType(),
                       "combat_power", String.valueOf(pokemon.getCombat_power())));
    }

    public void addOrUpdatePokemonInVectorStore(Pokemon pokemon) {
        if (!"active".equals(pokemon.getStatus())) {
            // If Pokemon is inactive, remove it from vector store
            deletePokemonFromVectorStore(pokemon.getId());
            return;
        }

        // Create document from Pokemon
        Document document = createDocument(pokemon);
        
        // Remove any existing document for this Pokemon
        vectorStore.delete(List.of(String.valueOf(pokemon.getId())));
        
        // Add the updated document
        vectorStore.add(List.of(document));
        
        System.out.println("Added/Updated Pokemon in vector store: " + pokemon.getName());
    }

    public void deletePokemonFromVectorStore(int pokemonId) {
        // Delete document from vector store
        vectorStore.delete(List.of(String.valueOf(pokemonId)));
        System.out.println("Deleted Pokemon from vector store: ID " + pokemonId);
    }
    
    public List<Map<String, Object>> searchPokemon(String query, int topK) {
        SearchRequest searchRequest = SearchRequest.query(query).withTopK(topK);
        List<Document> results = vectorStore.similaritySearch(searchRequest);
        
        return results.stream()
                .map(Document::getMetadata)
                .collect(Collectors.toList());
    }
}