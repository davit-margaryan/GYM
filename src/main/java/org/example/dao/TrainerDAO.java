package org.example.dao;


import org.example.dto.TrainerRequestDto;
import org.example.model.Trainer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainerDAO {
    Trainer save(TrainerRequestDto trainerRequestDto);

    Optional<Trainer> findById(UUID id);

    List<Trainer> findAll();

    void delete(UUID id);

    Trainer update(UUID id, TrainerRequestDto trainerRequestDto);
}