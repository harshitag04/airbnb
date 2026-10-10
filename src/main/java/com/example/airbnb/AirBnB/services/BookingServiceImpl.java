package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.BookingDTO;
import com.example.airbnb.AirBnB.DTOs.BookingRequest;
import com.example.airbnb.AirBnB.entities.*;
import com.example.airbnb.AirBnB.enums.BookingStatus;
import com.example.airbnb.AirBnB.exceptions.ResourceNotFoundException;
import com.example.airbnb.AirBnB.repositories.BookingRepository;
import com.example.airbnb.AirBnB.repositories.HotelRepository;
import com.example.airbnb.AirBnB.repositories.InventoryRepository;
import com.example.airbnb.AirBnB.repositories.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final BookingRepository bookingsRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public BookingDTO initialiseBooking(BookingRequest bookingRequest) {

        Hotels hotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with Id {} " + bookingRequest.getHotelId()));

        Rooms rooms = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with Id {} " + bookingRequest.getRoomId()));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableRepository(bookingRequest.getRoomId(),
                bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(),
                bookingRequest.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) + 1;

        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Room is not available anymore");
        }

        for(Inventory inventory : inventoryList){
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }

        inventoryRepository.saveAll(inventoryList);

        User user = User.builder()
                .id(1L)
                .build();

        Bookings booking = Bookings.builder()
                .hotels(hotel)
                .rooms(rooms)
                .bookingStatus(BookingStatus.RESERVED)
                .roomsCount(bookingRequest.getRoomsCount())
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(user)
                .amount(BigDecimal.TEN)
                .build();

        bookingsRepository.save(booking);
        return modelMapper.map(booking, BookingDTO.class);
    }
}
