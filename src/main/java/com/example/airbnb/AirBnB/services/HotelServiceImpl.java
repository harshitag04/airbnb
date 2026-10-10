package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.HotelDTO;
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

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;

    @Override
    public HotelDTO createHotel(HotelDTO hotelDTO) {
        Hotels hotelToBeSaved = modelMapper.map(hotelDTO, Hotels.class);
        hotelToBeSaved.setActive(false);
        Hotels savedHotel = hotelRepository.save(hotelToBeSaved);
        return modelMapper.map(savedHotel, HotelDTO.class);
    }

    @Override
    public List<HotelDTO> getHotels() {
        return List.of((HotelDTO) hotelRepository.findAll());
    }

    @Override
    public HotelDTO getHotelById(Long Id) {
        Hotels hotel = hotelRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with Id {} " + Id + "does not exist"));
        return modelMapper.map(hotel, HotelDTO.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long Id) {
        Hotels exist = hotelRepository
                .findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+ Id));
        for(Rooms rooms: exist.getRooms()){
            inventoryService.deleteAllInventories(rooms);
            roomRepository.deleteById(rooms.getId());
        }
        hotelRepository.deleteById(Id);
    }

    @Override
    public HotelDTO updateHotelById(HotelDTO hotelDTO, Long Id) {
        Hotels exist = hotelRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with Id {} " + Id + "does not exist"));
        modelMapper.map(hotelDTO, exist);
        exist.setId(Id);
        Hotels updatedHotel = hotelRepository.save(exist);
        return modelMapper.map(updatedHotel, HotelDTO.class);
    }

    @Override
    @Transactional
    public void activateHotelById(Long Id) {
        Hotels exist = hotelRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with Id {} " + Id + "does not exist"));
        exist.setActive(true);
        hotelRepository.save(exist);
        for(Rooms rooms: exist.getRooms()){
            inventoryService.initializeRoomForAYear(rooms);
        }
    }
}
