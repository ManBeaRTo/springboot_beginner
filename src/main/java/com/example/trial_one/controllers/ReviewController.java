package com.example.trial_one.controllers;

import com.example.trial_one.dto.ReviewDto;
import com.example.trial_one.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trial_one/")
public class ReviewController {

    private ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/pokemon/{pokemonId}/reviews")
    public ResponseEntity<ReviewDto> createReview(@PathVariable(value = "pokemonId") int pokemonId, @RequestBody ReviewDto reviewDto) {
        return new ResponseEntity<>(reviewService.createReview(pokemonId, reviewDto), HttpStatus.CREATED);
    }

    @GetMapping("/pokemon/{pokemonId}/reviews")
    public List<ReviewDto> getReviewsByPokemonId(@PathVariable(value = "pokemonId") int pokemonId) {
        return reviewService.getReviewsByPokemonId(pokemonId);
    }

    @GetMapping("/pokemon/{pokemonId}/reviews/{id}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable(value = "pokemonId") int pokemonId, @PathVariable(value = "id") int reviewId) {
        ReviewDto reviewDto = reviewService.getReviewById(pokemonId, reviewId);
        return new ResponseEntity<>(reviewDto, HttpStatus.OK);
    }

    @PutMapping("/pokemon/{pokemonId}/reviews/{id}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable(value = "pokemonId") int pokemonId, @PathVariable(value = "id") int reviewId,
                                                  @RequestBody ReviewDto reviewDto) {
        ReviewDto updatedReview = reviewService.updateReview(pokemonId, reviewId, reviewDto);
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }

    @DeleteMapping("/pokemon/{pokemonId}/reviews/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable(value = "pokemonId") int pokemonId, @PathVariable(value = "id") int reviewId) {
        reviewService.deleteReview(pokemonId, reviewId);
        return new ResponseEntity<>("Review deleted successfully", HttpStatus.OK);
    }
}