package com.example.trial_one.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        try {
            // Adding a try-catch block to deal with any potential issues
            try {
                // Use a non-empty query that's generic enough to match all documents
                SearchRequest allDocsRequest = SearchRequest.query("pokemon").withTopK(1000);
                List<Document> existingDocs = vectorStore.similaritySearch(allDocsRequest);
                
                if (!existingDocs.isEmpty()) {
                    List<String> docIds = existingDocs.stream()
                        .map(Document::getId)
                        .collect(Collectors.toList());
                    
                    System.out.println("Found " + docIds.size() + " documents to delete");
                    
                    // Delete in batches to avoid overwhelming the database
                    for (String docId : docIds) {
                        try {
                            vectorStore.delete(List.of(docId));
                        } catch (Exception e) {
                            System.out.println("Error deleting document with ID " + docId + ": " + e.getMessage());
                        }
                    }
                    
                    System.out.println("Cleared existing vector store: " + existingDocs.size() + " documents");
                }
            } catch (Exception e) {
                System.out.println("Error finding documents for deletion: " + e.getMessage());
                // Continue with adding new documents anyway
            }
            
            // Continue with adding new documents...
            List<Pokemon> allActivePokemon = pokemonRepository.findAll().stream()
                    .filter(pokemon -> "active".equals(pokemon.getStatus()))
                    .collect(Collectors.toList());
                    
            System.out.println("Adding " + allActivePokemon.size() + " Pokemon to vector store");
            
            List<Document> documents = new ArrayList<>();
            for (Pokemon pokemon : allActivePokemon) {
                documents.add(createDocument(pokemon));
            }

            if (!documents.isEmpty()) {
                vectorStore.add(documents);
                System.out.println("Initialized vector store with " + documents.size() + " Pokémon documents");
            }
        } catch (Exception e) {
            System.out.println("Error initializing vector store: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Document createDocument(Pokemon pokemon) {
        // Combine relevant Pokemon data into embeddable content
        String content = String.format(
                "Name: %s. Type: %s. Description: %s.",
                pokemon.getName(),
                pokemon.getType(),
                pokemon.getDescription() != null ? pokemon.getDescription() : "");

        // Generate a stable UUID from Pokemon ID
        String documentId = UUID.nameUUIDFromBytes(("pokemon-" + pokemon.getId()).getBytes()).toString();
        
        // Add metadata for retrieval
        return new Document(
                documentId, // Use the UUID string as document ID
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
        String documentId = UUID.nameUUIDFromBytes(("pokemon-" + pokemon.getId()).getBytes()).toString();
        try {
            vectorStore.delete(List.of(documentId));
        } catch (Exception e) {
            // Just log the error and continue - the document might not exist yet
            System.out.println("No existing document found for ID: " + pokemon.getId());
        }
        
        // Add the updated document
        vectorStore.add(List.of(document));
        
        System.out.println("Added/Updated Pokemon in vector store: " + pokemon.getName());
    }

    public void deletePokemonFromVectorStore(int pokemonId) {
        // Generate same UUID as when creating the document
        String documentId = UUID.nameUUIDFromBytes(("pokemon-" + pokemonId).getBytes()).toString();
        
        try {
            // Delete document from vector store
            vectorStore.delete(List.of(documentId));
            System.out.println("Deleted Pokemon from vector store: ID " + pokemonId);
        } catch (Exception e) {
            System.err.println("Error deleting from vector store: " + e.getMessage());
        }
    }
    
    public List<Map<String, Object>> searchPokemon(String query, int topK) {
        SearchRequest searchRequest = SearchRequest.query(query).withTopK(topK);
        List<Document> results = vectorStore.similaritySearch(searchRequest);
        
        return results.stream()
                .map(Document::getMetadata)
                .collect(Collectors.toList());
    }
}