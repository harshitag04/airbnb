package com.example.airbnb.AirBnB.DTOs;

import com.example.airbnb.AirBnB.entities.*;
import com.example.airbnb.AirBnB.enums.BookingStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDTO {

    private Long Id;
    private BookingStatus bookingStatus;
    private Integer roomsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Set<Guest> guest;

}
