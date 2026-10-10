package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.HotelDTO;

import java.util.List;

public interface HotelService {

    HotelDTO createHotel(HotelDTO hotels);

    List<HotelDTO> getHotels();

    HotelDTO getHotelById(Long Id);

    void deleteHotelById(Long Id);

    HotelDTO updateHotelById(HotelDTO hotels, Long Id);

    void activateHotelById(Long Id);

}
