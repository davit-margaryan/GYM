package org.example.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JSONData {

    private String firstName;
    private String lastName;
    private String address;
    private String role;
    private String specialization;

}
