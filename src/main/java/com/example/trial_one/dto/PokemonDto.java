package com.example.trial_one.dto;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.trial_one.model.AuditDetails;

import jakarta.persistence.Embedded;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PokemonDto {
	
	private int id;
	private String name;
	private String type;
	private int combat_power;
	private String picturePath;
	private AuditDetails auditDetails;
	private String status;
	private String description;
}
