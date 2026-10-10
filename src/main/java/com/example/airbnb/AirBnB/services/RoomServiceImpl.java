package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.RoomDTO;
import com.example.airbnb.AirBnB.entities.Hotels;
import com.example.airbnb.AirBnB.entities.Rooms;
import com.example.airbnb.AirBnB.exceptions.ResourceNotFoundException;
import com.example.airbnb.AirBnB.repositories.HotelRepository;
import com.example.airbnb.AirBnB.repositories.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;

    @Override
    public RoomDTO createRoom(Long hotelId, RoomDTO roomDTO) {
        Hotels hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with Id {} " + hotelId + "does not exist"));
        Rooms roomToBeSaved = modelMapper.map(roomDTO, Rooms.class);
        roomToBeSaved.setHotel(hotel);
        Rooms savedRoom = roomRepository.save(roomToBeSaved);
        if(hotel.isActive()){
            inventoryService.initializeRoomForAYear(savedRoom);
        }
        return modelMapper.map(savedRoom, RoomDTO.class);
    }

    @Override
    public List<RoomDTO> getAllRoomsInHotel(Long hotelId) {
        Hotels hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with Id {} " + hotelId + "does not exist"));
        return hotel.getRooms().stream()
                .map((room -> modelMapper.map(room, RoomDTO.class)))
                .collect(Collectors.toList());
    }

    @Override
    public RoomDTO getRoomById(Long roomId) {
        Rooms room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));
        return modelMapper.map(room, RoomDTO.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        Rooms room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));
        inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);
    }

    @Override
    public RoomDTO updateRoomById(Long hotelId, Long roomId, RoomDTO roomDTO) {
        Hotels hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with Id {} " + hotelId + "does not exist"));
        Rooms room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));
        Rooms newRoom = modelMapper.map(roomDTO, Rooms.class);
        newRoom.setId(roomId);
        Rooms savedRoom = roomRepository.save(newRoom);
        return modelMapper.map(savedRoom, RoomDTO.class);
    }
}
