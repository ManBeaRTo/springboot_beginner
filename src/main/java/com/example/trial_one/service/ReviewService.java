package com.example.trial_one.service;

import java.util.List;

import com.example.trial_one.dto.ReviewDto;

public interface ReviewService 
{
	ReviewDto createReview(int pokemonId, ReviewDto reviewDto);
	public ReviewDto getReviewById(int reviewId, int pokemonId);
	public List<ReviewDto> getReviewsByPokemonId(int pokemonId);
	public ReviewDto updateReview(int pokemonId, int reviewId, ReviewDto reviewDto);
	void deleteReview(int pokemonId, int reviewId);
}
