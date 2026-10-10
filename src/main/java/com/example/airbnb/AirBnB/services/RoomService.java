package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.RoomDTO;

import java.util.List;

public interface RoomService {

    RoomDTO createRoom(Long hotelId, RoomDTO roomDTO);

    List<RoomDTO> getAllRoomsInHotel(Long hotelId);

    RoomDTO getRoomById(Long roomId);

    void deleteRoomById(Long roomId);

    RoomDTO updateRoomById(Long hotelId, Long roomId, RoomDTO roomDTO);
}
