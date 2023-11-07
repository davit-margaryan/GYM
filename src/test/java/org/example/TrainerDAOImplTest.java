package org.example;

import org.example.dao.impl.TrainerDAOImpl;
import org.example.dto.TrainerRequestDto;
import org.example.exception.InvalidInputException;
import org.example.exception.NotFoundException;
import org.example.model.Trainer;
import org.example.model.User;
import org.example.service.InMemoryStorage;
import org.example.util.UtilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerDAOImplTest {

    @InjectMocks
    private TrainerDAOImpl trainerDAO = new TrainerDAOImpl();

    @Mock
    private InMemoryStorage inMemoryStorage;

    @Mock
    private UtilService utilService;

    @Spy
    private Map<UUID, User> userStorage = new HashMap<>();

    @Spy
    private Map<UUID, Trainer> trainerStorage = new HashMap<>();

    private User user;

    private Trainer trainer;

    private TrainerRequestDto trainerRequestDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        inMemoryStorage = mock(InMemoryStorage.class);
        when(inMemoryStorage.getUserStorage()).thenReturn(userStorage);
        when(inMemoryStorage.getTrainerStorage()).thenReturn(trainerStorage);
    }

    @Test
    void testSaveTrainer() {
        try {
            mockUser();
            mockTrainerRequestDto();
        } catch (Exception e) {
            e.printStackTrace();
        }

        when(utilService.isValidName("John")).thenReturn(true);
        when(utilService.isValidName("Doe")).thenReturn(true);
        when(utilService.generateUniqueKey(trainerStorage)).thenReturn(UUID.fromString("d87c669f-3cb0-4d6a-9cca-d2ce64968a8c"));
        when(utilService.generateUniqueKey(userStorage)).thenReturn(UUID.fromString("732200fc-d2f1-45c0-b3dd-fb148cfcc1e5"));
        when(utilService.generateUsername("John", "Doe", userStorage)).thenReturn("JohnDoe");
        when(utilService.generateRandomPassword(10)).thenReturn("randomPassword");

        Trainer savedTrainer = trainerDAO.save(trainerRequestDto);

        assertNotNull(savedTrainer);
        assertEquals(userStorage.get(UUID.fromString("732200fc-d2f1-45c0-b3dd-fb148cfcc1e5")).getFirstName(), user.getFirstName());
        assertEquals(1, userStorage.size());
        assertEquals("Fitness", savedTrainer.getSpecialization());
        verify(utilService).isValidName("John");
        verify(utilService).isValidName("Doe");
        verify(utilService).generateUniqueKey(trainerStorage);
        verify(utilService).generateUniqueKey(userStorage);
        verify(utilService).generateUsername("John", "Doe", userStorage);
        verify(utilService).generateRandomPassword(10);
        verifyNoMoreInteractions(utilService);
    }

    @Test
    void testSaveInvalidLastName() {
        try {
            mockTrainerRequestDto();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        trainerRequestDto.setLastName("");

        assertThrows(InvalidInputException.class, () -> trainerDAO.save(trainerRequestDto));
    }

    @Test
    void testFindById() {
        try {
            mockTrainer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        trainerStorage.put(trainer.getId(), trainer);

        Optional<Trainer> foundTrainer = trainerDAO.findById(trainer.getId());

        assertTrue(foundTrainer.isPresent());
        assertEquals(trainer.getId(), foundTrainer.get().getId());
    }

    @Test
    void testFindByIdTrainerNotFound() {
        UUID nonExistentTrainerId = UUID.randomUUID();

        Optional<Trainer> foundTrainer = trainerDAO.findById(nonExistentTrainerId);

        assertTrue(foundTrainer.isEmpty());
    }

    @Test
    void testFindAll() {
        Trainer trainer1 = new Trainer();
        Trainer trainer2 = new Trainer();
        trainer1.setId(UUID.randomUUID());
        trainer2.setId(UUID.randomUUID());
        trainerStorage.put(trainer1.getId(), trainer1);
        trainerStorage.put(trainer2.getId(), trainer2);

        List<Trainer> trainers = trainerDAO.findAll();

        assertEquals(2, trainers.size());
    }

    @Test
    void testDelete() {
        try {
            mockTrainer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        trainerStorage.put(trainer.getId(), trainer);
        trainerDAO.delete(trainer.getId());

        assertFalse(trainerStorage.containsKey(trainer.getId()));
        assertFalse(userStorage.containsKey(trainer.getUserId()));
    }

    @Test
    void testDeleteTrainerNotFound() {
        UUID nonExistentTrainerId = UUID.randomUUID();

        assertThrows(NotFoundException.class, () -> trainerDAO.delete(nonExistentTrainerId));
    }

    @Test
    void testUpdate() {
        try {
            mockUser();
            mockTrainer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Trainer trainerUnderTest = new Trainer();
        trainerUnderTest.setId(trainer.getId());
        trainerUnderTest.setUserId(user.getId());
        trainerUnderTest.setSpecialization("Fitness");

        trainerStorage.put(trainerUnderTest.getId(), trainerUnderTest);

        doNothing().when(utilService).updateFirstName(any(User.class), anyString());
        doNothing().when(utilService).updateLastName(any(User.class), anyString());
        doNothing().when(utilService).updateUsername(any(User.class), anyString(), anyMap());
        doNothing().when(utilService).updatePassword(any(User.class), anyString());

        when(trainerStorage.get(any(UUID.class))).thenReturn(trainerUnderTest);
        when(userStorage.get(any(UUID.class))).thenReturn(user);

        TrainerRequestDto updatedDto = mock(TrainerRequestDto.class);
        when(updatedDto.getSpecialization()).thenReturn("Yoga");
        when(updatedDto.getFirstName()).thenReturn("Michael");
        when(updatedDto.getLastName()).thenReturn("Brown");
        when(utilService.isValid(anyString())).thenReturn(true);

        Trainer updatedTrainer = trainerDAO.update(trainerUnderTest.getId(), updatedDto);

        assertNotNull(updatedTrainer);

        verify(utilService).updateFirstName(user, "Michael");
        verify(utilService).updateLastName(user, "Brown");
        assertEquals("Yoga", trainerStorage.get(updatedTrainer.getId()).getSpecialization());
    }


    @Test
    void testUpdateTrainerNotFound() {
        UUID nonExistentTrainerId = UUID.randomUUID();
        TrainerRequestDto updatedDto = new TrainerRequestDto();

        assertThrows(NotFoundException.class, () -> trainerDAO.update(nonExistentTrainerId, updatedDto));
    }

    private void mockUser() throws Exception {
        user = PowerMockito.mock(User.class);
        PowerMockito.whenNew(User.class).withNoArguments().thenReturn(user);
        when(user.getId()).thenReturn(UUID.fromString("732200fc-d2f1-45c0-b3dd-fb148cfcc1e5"));
        when(user.getUsername()).thenReturn("Davo");
        when(user.getFirstName()).thenReturn("John");
        when(user.getPassword()).thenReturn("randomPassword");
        when(user.isActive()).thenReturn(true);
        when(user.getLastName()).thenReturn("Doe");
    }

    private void mockTrainer() throws Exception {
        trainer = PowerMockito.mock(Trainer.class);
        PowerMockito.whenNew(Trainer.class).withNoArguments().thenReturn(trainer);
        when(trainer.getUserId()).thenReturn(UUID.fromString("732200fc-d2f1-45c0-b3dd-fb148cfcc1e5"));
        when(trainer.getId()).thenReturn(UUID.fromString("a7c393f4-7a51-11ee-b962-0242ac120002"));
        trainer.setSpecialization("Fitness");
    }

    private void mockTrainerRequestDto() throws Exception {
        trainerRequestDto = PowerMockito.mock(TrainerRequestDto.class);
        PowerMockito.whenNew(TrainerRequestDto.class).withNoArguments().thenReturn(trainerRequestDto);
        when(trainerRequestDto.getFirstName()).thenReturn("John");
        when(trainerRequestDto.getLastName()).thenReturn("Doe");
        when(trainerRequestDto.getSpecialization()).thenReturn("Fitness");
    }
}
