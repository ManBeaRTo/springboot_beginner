package com.example.trial_one.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.trial_one.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> 
{
	List<Review> findByPokemonId(int pokemonId); 
}
