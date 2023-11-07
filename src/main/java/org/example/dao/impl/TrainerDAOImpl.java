package org.example.dao.impl;


import org.example.dao.TrainerDAO;
import org.example.dto.TrainerRequestDto;
import org.example.exception.InvalidInputException;
import org.example.exception.NotFoundException;
import org.example.model.Trainer;
import org.example.model.User;
import org.example.service.InMemoryStorage;
import org.example.util.UtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Implementation of the TrainerDAO interface for managing Trainer entities.
 */
@Repository
public class TrainerDAOImpl implements TrainerDAO {
    private static final Logger logger = LoggerFactory.getLogger(TrainerDAOImpl.class);
    private Map<UUID, Trainer> trainerStorage;
    private Map<UUID, User> userStorage;
    private UtilService utilService;

    @Autowired
    public void setTrainerStorage(InMemoryStorage storage) {
        this.trainerStorage = storage.getTrainerStorage();
    }

    @Autowired
    public void setUtilService(UtilService utilService) {
        this.utilService = utilService;
    }

    @Autowired
    public void setUserStorage(InMemoryStorage inMemoryStorage) {
        this.userStorage = inMemoryStorage.getUserStorage();
    }

    @Override
    public Trainer save(TrainerRequestDto trainerRequestDto) {
        Trainer trainer = new Trainer();
        User user = new User();
        if (!utilService.isValidName(trainerRequestDto.getFirstName()) || !utilService.isValidName(trainerRequestDto.getLastName())) {
            logger.error("Invalid firstname or lastname ");
            throw new InvalidInputException("Invalid firstname or lastname");
        }
        trainer.setId(utilService.generateUniqueKey(trainerStorage));
        trainer.setUserId(saveUser(user, trainerRequestDto));
        trainer.setSpecialization(trainerRequestDto.getSpecialization());
        trainerStorage.put(trainer.getId(), trainer);
        logger.info("Trainer successfully created");
        return trainer;
    }

    @Override
    public Optional<Trainer> findById(UUID id) {
        return Optional.ofNullable(trainerStorage.get(id));
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(trainerStorage.values());
    }

    @Override
    public void delete(UUID id) {
        Optional<Trainer> trainerOptional = findById(id);
        if (trainerOptional.isPresent()) {
            userStorage.remove(trainerOptional.get().getUserId());
            trainerStorage.remove(id);
        } else {
            logger.error("Trainer not found");
            throw new NotFoundException("Trainer not found");
        }
    }

    @Override
    public Trainer update(UUID id, TrainerRequestDto trainerRequestDto) {
        if (!trainerStorage.containsKey(id)) {
            throw new NotFoundException("Trainer not found with ID: " + id);
        }
        Trainer trainer = trainerStorage.get(id);
        UUID userId = trainer.getUserId();
        User user = userStorage.get(userId);
        utilService.updateFirstName(user, trainerRequestDto.getFirstName());
        utilService.updateLastName(user, trainerRequestDto.getLastName());
        utilService.updateUsername(user, trainerRequestDto.getUsername(), userStorage);
        utilService.updatePassword(user, trainerRequestDto.getPassword());
        if (utilService.isValid(trainerRequestDto.getSpecialization())) {
            trainer.setSpecialization(trainerRequestDto.getSpecialization());
        }
        trainerStorage.put(id, trainer);
        userStorage.put(userId, user);
        logger.info("Trainer successfully updated");
        return trainer;
    }

    private UUID saveUser(User user, TrainerRequestDto trainerRequestDto) {
        user.setId(utilService.generateUniqueKey(userStorage));
        user.setFirstName(trainerRequestDto.getFirstName());
        user.setLastName(trainerRequestDto.getLastName());
        user.setUsername(utilService.generateUsername(user.getFirstName(), user.getLastName(), userStorage));
        user.setPassword(utilService.generateRandomPassword(10));
        user.setActive(true);
        userStorage.put(user.getId(), user);
        return user.getId();
    }

}