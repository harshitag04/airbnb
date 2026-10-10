package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.BookingDTO;
import com.example.airbnb.AirBnB.DTOs.BookingRequest;

public interface BookingService {

    BookingDTO initialiseBooking(BookingRequest bookingRequest);

}
