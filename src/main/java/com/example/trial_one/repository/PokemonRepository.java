package com.example.trial_one.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.trial_one.model.Pokemon;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Integer> {
    
    // Find by status with pagination and ordering
    Page<Pokemon> findByStatusOrderByIdDesc(String status, Pageable pageable);
    
    // Find by status or null status (for active Pokemon) with pagination and ordering
    Page<Pokemon> findByStatusOrStatusIsNullOrderByIdDesc(String status, Pageable pageable);
    
    // Find by status without pagination
    List<Pokemon> findByStatus(String status);
    
    // Find by status or null status (for active Pokemon) without pagination
    List<Pokemon> findByStatusOrStatusIsNull(String status);
    
    // Default method for backward compatibility
    Page<Pokemon> findAllByOrderByIdDesc(Pageable pageable);
}
