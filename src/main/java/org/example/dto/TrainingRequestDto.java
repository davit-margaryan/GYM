package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingRequestDto {

    private UUID id;
    private UUID traineeId;
    private UUID trainerId;
    private String name;
    private LocalDate date;
    private Number duration;
    private String trainingTypeName;

}
