package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Training {

    private UUID id;
    private UUID traineeId;
    private UUID trainerId;
    private UUID trainingTypeId;
    private String name;
    private LocalDate date;
    private Number duration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Training training = (Training) o;
        return Objects.equals(id, training.id) && Objects.equals(traineeId, training.traineeId) && Objects.equals(trainerId, training.trainerId) && Objects.equals(trainingTypeId, training.trainingTypeId) && Objects.equals(name, training.name) && Objects.equals(date, training.date) && Objects.equals(duration, training.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, traineeId, trainerId, trainingTypeId, name, date, duration);
    }
}
