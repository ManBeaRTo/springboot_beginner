package com.example.trial_one.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.trial_one.model.Pokemon;

public interface PokemonRepository extends JpaRepository<Pokemon, Integer> 
{

	Page<Pokemon> findAllByOrderByIdDesc(Pageable pageable);
} 
