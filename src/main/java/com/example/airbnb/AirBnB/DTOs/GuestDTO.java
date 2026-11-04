package com.example.airbnb.AirBnB.DTOs;

import com.example.airbnb.AirBnB.entities.User;
import com.example.airbnb.AirBnB.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
public class GuestDTO {

    private Long Id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}
