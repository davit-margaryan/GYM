package org.example.dao.impl;


import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dao.TrainingDAO;
import org.example.dto.TrainingRequestDto;
import org.example.exception.InvalidInputException;
import org.example.exception.NotFoundException;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.service.InMemoryStorage;
import org.example.util.UtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Implementation of the TrainingDAO interface for managing Training entities.
 */
@Repository
public class TrainingDAOImpl implements TrainingDAO {
    private static final Logger logger = LoggerFactory.getLogger(TrainingDAOImpl.class);
    private Map<UUID, Training> trainingStorage;
    private UtilService utilService;
    private Map<UUID, Trainee> traineeStorage;
    private Map<UUID, Trainer> trainerStorage;
    private Map<UUID, TrainingType> trainingTypeStorage;
    private TraineeDAO traineeDAO;
    private TrainerDAO trainerDAO;

    @Autowired
    public void setTrainingStorage(InMemoryStorage storage) {
        this.trainingStorage = storage.getTrainingStorage();
    }

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Autowired
    public void setTrainingTypeStorage(InMemoryStorage storage) {
        this.trainingTypeStorage = storage.getTrainingTypeStorage();
    }

    @Autowired
    public void setTrainerStorage(InMemoryStorage storage) {
        this.trainerStorage = storage.getTrainerStorage();
    }

    @Autowired
    public void setTraineeStorage(InMemoryStorage storage) {
        this.traineeStorage = storage.getTraineeStorage();
    }

    @Autowired
    public void setUtilService(UtilService utilService) {
        this.utilService = utilService;
    }

    @Override
    public Training save(TrainingRequestDto trainingRequestDto) {
        if (!areFieldsValid(trainingRequestDto)) {
            throw new InvalidInputException("Missing required fields for creating a training.");
        }
        Training training = new Training();
        training.setId(utilService.generateUniqueKey(trainingStorage));
        training.setDate(trainingRequestDto.getDate());
        training.setDuration(trainingRequestDto.getDuration());
        training.setName(trainingRequestDto.getName());
        TrainingType trainingType = new TrainingType();
        trainingType.setId(utilService.generateUniqueKey(trainingTypeStorage));
        trainingType.setTypeName(trainingRequestDto.getTrainingTypeName());
        training.setTrainingTypeId(trainingType.getId());
        Optional<Trainee> optionalTrainee = traineeDAO.findById(trainingRequestDto.getTraineeId());
        if (optionalTrainee.isEmpty()) {
            throw new NotFoundException("Trainee with " + trainingRequestDto.getTraineeId() + " Not found");
        }
        Optional<Trainer> optionalTrainer = trainerDAO.findById(trainingRequestDto.getTrainerId());
        if (optionalTrainer.isEmpty()) {
            throw new NotFoundException("Trainer with " + trainingRequestDto.getTraineeId() + " Not found");
        }
        training.setTrainerId(trainingRequestDto.getTrainerId());
        training.setTraineeId(trainingRequestDto.getTraineeId());
        trainingStorage.put(training.getId(), training);
        trainingTypeStorage.put(trainingType.getId(), trainingType);
        return training;
    }

    @Override
    public Optional<Training> findById(UUID id) {
        return Optional.ofNullable(trainingStorage.get(id));
    }

    @Override
    public List<Training> findAll() {
        return new ArrayList<>(trainingStorage.values());
    }

    @Override
    public void delete(UUID id) {
        Optional<Training> trainingOptional = findById(id);
        if (trainingOptional.isPresent()) {
            trainingStorage.remove(id);
            trainerStorage.remove(trainingOptional.get().getTrainerId());
            traineeStorage.remove(trainingOptional.get().getTraineeId());
            trainingTypeStorage.remove(trainingOptional.get().getTrainingTypeId());
        } else {
            logger.error("Training not found");
            throw new NotFoundException("Training not found");
        }
    }

    @Override
    public Training update(UUID id, TrainingRequestDto trainingRequestDto) {
        if (!trainingStorage.containsKey(id)) {
            throw new NotFoundException("Training not found with ID: " + id);
        }
        Training training = trainingStorage.get(id);


        if (trainingRequestDto.getName() != null && !trainingRequestDto.getName().isEmpty()) {
            training.setName(trainingRequestDto.getName());
        }

        if (trainingRequestDto.getDate() != null) {
            training.setDate(trainingRequestDto.getDate());
        }

        if (trainingRequestDto.getDuration() != null) {
            training.setDuration(trainingRequestDto.getDuration());
        }

        if (trainingRequestDto.getTrainingTypeName() != null && !trainingRequestDto.getTrainingTypeName().isEmpty()) {
            TrainingType trainingType = trainingTypeStorage.get(training.getTrainingTypeId());
            trainingType.setTypeName(trainingRequestDto.getTrainingTypeName());
            trainingTypeStorage.put(trainingType.getId(), trainingType);
        }

        if (trainingRequestDto.getTraineeId() != null) {
            Optional<Trainee> optionalTrainee = traineeDAO.findById(trainingRequestDto.getTraineeId());
            if (optionalTrainee.isEmpty()) {
                throw new NotFoundException("Trainee with ID: " + trainingRequestDto.getTraineeId() + " not found");
            }
            training.setTraineeId(trainingRequestDto.getTraineeId());
        }

        if (trainingRequestDto.getTrainerId() != null) {
            Optional<Trainer> optionalTrainer = trainerDAO.findById(trainingRequestDto.getTrainerId());
            if (optionalTrainer.isEmpty()) {
                throw new NotFoundException("Trainer with ID: " + trainingRequestDto.getTrainerId() + " not found");
            }
            training.setTrainerId(trainingRequestDto.getTrainerId());
        }
        trainingStorage.put(training.getId(), training);
        return training;
    }


    private boolean areFieldsValid(TrainingRequestDto trainingRequestDto) {
        return trainingRequestDto != null &&
                trainingRequestDto.getTraineeId() != null &&
                trainingRequestDto.getTrainerId() != null &&
                trainingRequestDto.getName() != null &&
                trainingRequestDto.getDate() != null &&
                trainingRequestDto.getDuration() != null &&
                trainingRequestDto.getTrainingTypeName() != null &&
                !trainingRequestDto.getName().isEmpty() &&
                !trainingRequestDto.getTrainingTypeName().isEmpty();
    }

}