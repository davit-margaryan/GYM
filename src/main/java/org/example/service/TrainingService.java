package org.example.service;


import org.example.dao.TrainingDAO;
import org.example.dto.TrainingRequestDto;
import org.example.exception.InvalidInputException;
import org.example.exception.NotFoundException;
import org.example.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing Training entities.
 */
@Service
public class TrainingService {

    @Autowired
    private TrainingDAO trainingDAO;

    /**
     * Creates and saves a new training record based on the provided TrainingRequestDto.
     *
     * @param trainingRequestDto The data required to create a new training record.
     * @return The newly created Training record.
     * @throws InvalidInputException if any required field is missing or invalid.
     * @throws NotFoundException     if the associated Trainee or Trainer does not exist.
     */
    public Training save(TrainingRequestDto trainingRequestDto) {
        return trainingDAO.save(trainingRequestDto);
    }

    /**
     * Retrieves a training record by its unique ID.
     *
     * @param id The unique ID of the training record to retrieve.
     * @return An Optional containing the training record, if found, or an empty Optional if not found.
     */
    public Optional<Training> findById(UUID id) {
        return trainingDAO.findById(id);
    }

    /**
     * Retrieves all training records.
     *
     * @return A List of all available training records.
     */
    public List<Training> findAll() {
        return trainingDAO.findAll();
    }

    /**
     * Deletes a training record by its unique ID. This method also removes associated trainer, trainee, and training type records.
     *
     * @param id The unique ID of the training record to delete.
     */
    public void delete(UUID id) {
        trainingDAO.delete(id);
    }

    /**
     * Updates an existing training record based on the provided TrainingRequestDto.
     *
     * @param id                 The unique ID of the training record to update.
     * @param trainingRequestDto The data used to update the training record.
     * @return The updated Training record.
     * @throws NotFoundException if the associated Trainee or Trainer does not exist.
     */
    public Training update(UUID id, TrainingRequestDto trainingRequestDto) {
        return trainingDAO.update(id, trainingRequestDto);
    }
}
