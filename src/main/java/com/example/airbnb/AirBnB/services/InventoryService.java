package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.HotelDTO;
import com.example.airbnb.AirBnB.DTOs.HotelSearchRequest;
import com.example.airbnb.AirBnB.entities.Rooms;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Rooms room);

    void deleteAllInventories(Rooms room);

    Page<HotelDTO> searchHotels(HotelSearchRequest hotelSearchRequest);

}
