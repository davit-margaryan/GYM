package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeRequestDto {

    private String firstName;
    private String lastName;
    private String address;
    private String username;
    private String password;

}
