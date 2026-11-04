package com.example.airbnb.AirBnB.controllers;

import com.example.airbnb.AirBnB.DTOs.BookingDTO;
import com.example.airbnb.AirBnB.DTOs.BookingRequest;
import com.example.airbnb.AirBnB.DTOs.GuestDTO;
import com.example.airbnb.AirBnB.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping(path = "/init")
    public ResponseEntity<BookingDTO> initialiseBooking(@RequestBody BookingRequest bookingRequest){
        BookingDTO bookingDTO = bookingService.initialiseBooking(bookingRequest);
        return ResponseEntity.ok(bookingDTO);
    }

    @PostMapping(path = "/{bookingId}/add-guest")
    public ResponseEntity<BookingDTO> addGuest(@PathVariable(name = "bookingId")Long Id,
                                               @RequestBody List<GuestDTO> guestDTOList){

        return ResponseEntity.ok(bookingService.addGuest(Id, guestDTOList));

    }

}
