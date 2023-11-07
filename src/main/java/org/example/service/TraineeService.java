package org.example.service;


import org.example.dao.TraineeDAO;
import org.example.dto.TraineeRequestDto;
import org.example.exception.InvalidInputException;
import org.example.exception.NotFoundException;
import org.example.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing Trainee entities.
 */
@Service
public class TraineeService {

    @Autowired
    private TraineeDAO traineeDAO;

    /**
     * Save a new Trainee entity based on the provided TraineeRequestDto.
     *
     * @param traineeRequestDto The TraineeRequestDto containing the trainee's information.
     * @return Created Trainee entity.
     * @throws InvalidInputException When the first name or last name is invalid.
     */
    public Trainee save(TraineeRequestDto traineeRequestDto) {
        return traineeDAO.save(traineeRequestDto);
    }

    /**
     * Find a Trainee by its unique identifier (UUID).
     *
     * @param id The unique identifier of the Trainee.
     * @return An Optional containing the Trainee if found, or an empty Optional if not found.
     */
    public Optional<Trainee> findById(UUID id) {
        return traineeDAO.findById(id);
    }

    /**
     * Retrieve a list of all Trainees.
     *
     * @return A list of all Trainees in the data storage.
     */
    public List<Trainee> findAll() {
        return traineeDAO.findAll();
    }

    /**
     * Delete a Trainee by its unique identifier (UUID).
     *
     * @param id The unique identifier of the Trainee to be deleted.
     */
    public void delete(UUID id) {
        traineeDAO.delete(id);
    }

    /**
     * Update an existing Trainee entity with information from a TraineeRequestDto.
     *
     * @param id                The unique identifier of the Trainee to be updated.
     * @param traineeRequestDto The TraineeRequestDto containing the updated information.
     * @return The updated Trainee entity.
     * @throws NotFoundException When the Trainee with the specified ID is not found.
     */
    public Trainee update(UUID id, TraineeRequestDto traineeRequestDto) {
        return traineeDAO.update(id, traineeRequestDto);
    }

}
