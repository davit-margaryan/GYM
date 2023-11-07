package org.example.dao;


import org.example.dto.TrainingRequestDto;
import org.example.model.Training;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainingDAO {
    Training save(TrainingRequestDto trainingRequestDto);

    Optional<Training> findById(UUID id);

    List<Training> findAll();

    void delete(UUID id);

    Training update(UUID id, TrainingRequestDto trainingRequestDto);
}