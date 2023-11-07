package org.example;

import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dao.impl.TrainingDAOImpl;
import org.example.dto.TrainingRequestDto;
import org.example.exception.InvalidInputException;
import org.example.exception.NotFoundException;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.service.InMemoryStorage;
import org.example.util.UtilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingDAOImplTest {

    @InjectMocks
    private TrainingDAOImpl trainingDAO;

    @Mock
    private InMemoryStorage inMemoryStorage;

    @Mock
    private UtilService utilService;

    @Mock
    private TraineeDAO traineeDAO;

    @Mock
    private TrainerDAO trainerDAO;

    @Spy
    private Map<UUID, Training> trainingStorage = new HashMap<>();

    @Spy
    private Map<UUID, Trainee> traineeStorage = new HashMap<>();

    @Spy
    private Map<UUID, Trainer> trainerStorage = new HashMap<>();

    @Spy
    private Map<UUID, TrainingType> trainingTypeStorage = new HashMap<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        inMemoryStorage = mock(InMemoryStorage.class);
        when(inMemoryStorage.getTrainingStorage()).thenReturn(trainingStorage);
        when(inMemoryStorage.getTraineeStorage()).thenReturn(traineeStorage);
        when(inMemoryStorage.getTrainerStorage()).thenReturn(trainerStorage);
        when(inMemoryStorage.getTrainingTypeStorage()).thenReturn(trainingTypeStorage);
    }

    @Test
    void testSave() {
        TrainingRequestDto trainingRequestDto = createTrainingRequestDto();
        Trainee trainee = mockTrainee(UUID.randomUUID());
        Trainer trainer = mockTrainer(UUID.randomUUID());
        when(traineeDAO.findById(trainingRequestDto.getTraineeId())).thenReturn(Optional.of(trainee));
        when(trainerDAO.findById(trainingRequestDto.getTrainerId())).thenReturn(Optional.of(trainer));
        when(utilService.generateUniqueKey(trainingStorage)).thenReturn(UUID.randomUUID());
        when(utilService.generateUniqueKey(trainingTypeStorage)).thenReturn(UUID.randomUUID());

        Training savedTraining = trainingDAO.save(trainingRequestDto);

        assertNotNull(savedTraining);
        assertEquals(trainingRequestDto.getName(), savedTraining.getName());
        assertEquals(trainingRequestDto.getDate(), savedTraining.getDate());
        assertEquals(trainingRequestDto.getDuration(), savedTraining.getDuration());
        assertEquals(trainingRequestDto.getTrainingTypeName(), trainingTypeStorage.get(savedTraining.getTrainingTypeId()).getTypeName());
        assertEquals(trainingRequestDto.getTraineeId(), savedTraining.getTraineeId());
        assertEquals(trainingRequestDto.getTrainerId(), savedTraining.getTrainerId());
    }

    @Test
    void testSaveInvalidFields() {
        TrainingRequestDto trainingRequestDto = new TrainingRequestDto();

        assertThrows(InvalidInputException.class, () -> trainingDAO.save(trainingRequestDto));
    }

    @Test
    void testUpdateTrainingFound() {
        UUID trainingId = UUID.randomUUID();
        TrainingRequestDto updatedDto = createTrainingRequestDto();

        Training training = mockTraining(trainingId);
        trainingStorage.put(trainingId, training);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(UUID.randomUUID());
        trainingTypeStorage.put(trainingType.getId(), trainingType);

        UUID traineeId = UUID.randomUUID();
        Trainee trainee = mockTrainee(traineeId);
        UUID trainerId = UUID.randomUUID();
        Trainer trainer = mockTrainer(trainerId);
        traineeStorage.put(traineeId, trainee);
        trainerStorage.put(trainerId, trainer);

        updatedDto.setTraineeId(traineeId);
        updatedDto.setTrainerId(trainerId);

        training.setTrainingTypeId(trainingType.getId());
        when(trainingTypeStorage.get(training.getTrainingTypeId())).thenReturn(trainingType);
        when(traineeDAO.findById(traineeId)).thenReturn(Optional.of(trainee));
        when(trainerDAO.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(trainingTypeStorage.get(trainingType.getId())).thenReturn(trainingType);

        trainingDAO.update(trainingId, updatedDto);

        verify(training).setName(updatedDto.getName());
        verify(training).setDate(updatedDto.getDate());
    }


    @Test
    void testFindById() {
        UUID trainingId = UUID.randomUUID();
        Training training = mockTraining(trainingId);
        trainingStorage.put(trainingId, training);

        Optional<Training> foundTraining = trainingDAO.findById(trainingId);

        assertTrue(foundTraining.isPresent());
        assertEquals(trainingId, foundTraining.get().getId());
    }

    @Test
    void testFindByIdTrainingNotFound() {
        UUID nonExistentTrainingId = UUID.randomUUID();

        Optional<Training> foundTraining = trainingDAO.findById(nonExistentTrainingId);

        assertTrue(foundTraining.isEmpty());
    }

    @Test
    void testFindAll() {
        Training training1 = mockTraining(UUID.randomUUID());
        Training training2 = mockTraining(UUID.randomUUID());
        trainingStorage.put(UUID.randomUUID(), training1);
        trainingStorage.put(UUID.randomUUID(), training2);

        List<Training> trainings = trainingDAO.findAll();

        assertEquals(2, trainings.size());
    }

    @Test
    void testDelete() {
        UUID trainingId = UUID.randomUUID();
        Training training = mockTraining(trainingId);
        UUID traineeId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();
        training.setTraineeId(traineeId);
        training.setTrainerId(trainerId);
        trainingStorage.put(trainingId, training);
        trainingDAO.delete(trainingId);
        assertFalse(trainingStorage.containsKey(trainingId));
        assertFalse(traineeStorage.containsKey(traineeId));
        assertFalse(trainerStorage.containsKey(trainerId));
    }

    @Test
    void testDeleteTrainingNotFound() {
        UUID nonExistentTrainingId = UUID.randomUUID();

        assertThrows(NotFoundException.class, () -> trainingDAO.delete(nonExistentTrainingId));
    }


    @Test
    void testUpdateTrainingNotFound() {
        UUID nonExistentTrainingId = UUID.randomUUID();
        TrainingRequestDto updatedDto = new TrainingRequestDto();
        assertThrows(NotFoundException.class, () -> trainingDAO.update(nonExistentTrainingId, updatedDto));
    }

    private TrainingRequestDto createTrainingRequestDto() {
        TrainingRequestDto trainingRequestDto = new TrainingRequestDto();
        trainingRequestDto.setName("Training 1");
        trainingRequestDto.setDate(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        trainingRequestDto.setDuration(60);
        trainingRequestDto.setTrainingTypeName("Type 1");
        trainingRequestDto.setTraineeId(UUID.randomUUID());
        trainingRequestDto.setTrainerId(UUID.randomUUID());
        return trainingRequestDto;
    }

    private Trainee mockTrainee(UUID id) {
        Trainee trainee = mock(Trainee.class);
        when(trainee.getId()).thenReturn(id);
        return trainee;
    }

    private Trainer mockTrainer(UUID id) {
        Trainer trainer = mock(Trainer.class);
        when(trainer.getId()).thenReturn(id);
        return trainer;
    }

    private Training mockTraining(UUID id) {
        Training training = mock(Training.class);
        when(training.getId()).thenReturn(id);
        return training;
    }
}
