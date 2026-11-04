package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.BookingDTO;
import com.example.airbnb.AirBnB.DTOs.BookingRequest;
import com.example.airbnb.AirBnB.DTOs.GuestDTO;
import com.example.airbnb.AirBnB.entities.*;
import com.example.airbnb.AirBnB.enums.BookingStatus;
import com.example.airbnb.AirBnB.exceptions.ResourceNotFoundException;
import com.example.airbnb.AirBnB.exceptions.UnAuthorisedException;
import com.example.airbnb.AirBnB.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final BookingRepository bookingsRepository;
    private final GuestRepository guestRepository;
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


        Bookings booking = Bookings.builder()
                .hotels(hotel)
                .rooms(rooms)
                .bookingStatus(BookingStatus.RESERVED)
                .roomsCount(bookingRequest.getRoomsCount())
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .amount(BigDecimal.TEN)
                .build();

        bookingsRepository.save(booking);
        return modelMapper.map(booking, BookingDTO.class);
    }

    @Override
    @Transactional
    public BookingDTO addGuest(Long Id, List<GuestDTO> guestDTOList) {

        Bookings bookings = bookingsRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with Id " + Id + " not found"));

        User user = getCurrentUser();
        if(!user.equals(bookings.getUser())){
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        if(isBookingExpired(bookings)){
            throw new IllegalStateException("Booking with Id{} " + Id + "has already expired");
        }

        if(bookings.getBookingStatus() != BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests");
        }

        for(GuestDTO guestDTO1 : guestDTOList){
            Guest guest = modelMapper.map(guestDTO1, Guest.class);
            guest.setUser(getCurrentUser());
            guestRepository.save(guest);
            bookings.getGuest().add(guest);
        }

        bookings.setBookingStatus(BookingStatus.GUEST_ADDED);
        bookingsRepository.save(bookings);

        return modelMapper.map(bookings, BookingDTO.class);

    }

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean isBookingExpired(Bookings bookings){
        return bookings.getCreatedAt().plusHours(1).isBefore(LocalDateTime.now());
    }

}
