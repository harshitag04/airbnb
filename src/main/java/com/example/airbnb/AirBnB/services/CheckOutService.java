package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.entities.Bookings;

public interface CheckOutService {

    String getCheckOutSession(Bookings bookings, String successUrl, String failureUrl);
}
