package com.example.airbnb.AirBnB.DTOs;

import com.example.airbnb.AirBnB.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequestDTO {
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
}
