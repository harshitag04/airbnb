package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.*;
import com.example.airbnb.AirBnB.entities.Rooms;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {

    void initializeRoomForAYear(Rooms room);

    void deleteAllInventories(Rooms room);

    Page<HotelPriceDTO> searchHotels(HotelSearchRequest hotelSearchRequest);

    List<InventoryDTO> getAllInventoriesByRoom(Long roomId);

    void updateInventory(Long roomId, InventoryUpdateRequestDTO inventoryUpdateRequestDTO);
}
