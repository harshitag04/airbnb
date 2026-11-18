package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.GuestDTO;

import java.util.List;

public interface GuestService {

    List<GuestDTO> getAllGuests();

    void updateGuest(Long guestId, GuestDTO guestDto);

    void deleteGuest(Long guestId);

    GuestDTO addNewGuest(GuestDTO guestDto);

}
