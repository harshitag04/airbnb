package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.BookingDTO;
import com.example.airbnb.AirBnB.DTOs.BookingRequest;
import com.example.airbnb.AirBnB.DTOs.GuestDTO;

import java.util.List;

public interface BookingService {

    BookingDTO initialiseBooking(BookingRequest bookingRequest);

    BookingDTO addGuest(Long Id, List<GuestDTO> guestDTOList);
}
