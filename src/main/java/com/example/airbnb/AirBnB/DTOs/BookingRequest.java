package com.example.airbnb.AirBnB.DTOs;

import com.example.airbnb.AirBnB.entities.Rooms;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {

    private Long hotelId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer roomsCount;

}
