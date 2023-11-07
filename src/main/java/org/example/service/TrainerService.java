package org.example.service;


import org.example.dao.TrainerDAO;
import org.example.dto.TrainerRequestDto;
import org.example.exception.InvalidInputException;
import org.example.exception.NotFoundException;
import org.example.model.Trainer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing Trainer entities.
 */
@Service
public class TrainerService {

    private TrainerDAO trainerDAO;

    /**
     * Save a new Trainer entity based on the provided TrainerRequestDto.
     *
     * @param trainerRequestDto The TrainerRequestDto containing the trainer's information.
     * @return Created Trainer entity.
     * @throws InvalidInputException When the first name, last name, or specialization is invalid.
     */
    public Trainer save(TrainerRequestDto trainerRequestDto) {
        return trainerDAO.save(trainerRequestDto);
    }

    /**
     * Find a Trainer by its unique identifier (UUID).
     *
     * @param id The unique identifier of the Trainer.
     * @return An Optional containing the Trainer if found, or an empty Optional if not found.
     */
    public Optional<Trainer> findById(UUID id) {
        return trainerDAO.findById(id);
    }

    /**
     * Retrieve a list of all Trainers.
     *
     * @return A list of all Trainers in the data storage.
     */
    public List<Trainer> findAll() {
        return trainerDAO.findAll();
    }

    /**
     * Delete a Trainer by its unique identifier (UUID).
     *
     * @param id The unique identifier of the Trainer to be deleted.
     */
    public void delete(UUID id) {
        trainerDAO.delete(id);
    }

    /**
     * Update an existing Trainer entity with information from a TrainerRequestDto.
     *
     * @param id                The unique identifier of the Trainer to be updated.
     * @param trainerRequestDto The TrainerRequestDto containing the updated information.
     * @return The updated Trainer entity.
     * @throws NotFoundException When the Trainer with the specified ID is not found.
     */
    public Trainer update(UUID id, TrainerRequestDto trainerRequestDto) {
        return trainerDAO.update(id, trainerRequestDto);
    }

}
