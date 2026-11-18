package com.example.airbnb.AirBnB.controllers;

import com.example.airbnb.AirBnB.DTOs.BookingDTO;
import com.example.airbnb.AirBnB.DTOs.HotelDTO;
import com.example.airbnb.AirBnB.DTOs.HotelReportDTO;
import com.example.airbnb.AirBnB.services.BookingService;
import com.example.airbnb.AirBnB.services.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<HotelDTO> createNewHotel(@RequestBody HotelDTO hotelDto) {
        log.info("Attempting to create a new hotel with name: "+hotelDto.getName());
        HotelDTO hotel = hotelService.createHotel(hotelDto);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDTO> getHotelById(@PathVariable Long hotelId) {
        HotelDTO hotelDto = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotelDto);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDTO> updateHotelById(@PathVariable Long hotelId, @RequestBody HotelDTO hotelDto) {
        HotelDTO hotel = hotelService.updateHotelById(hotelDto, hotelId);
        return ResponseEntity.ok(hotel);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable Long hotelId) {
        hotelService.deleteHotelById(hotelId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{hotelId}")
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelId) {
        hotelService.activateHotelById(hotelId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<HotelDTO>> getAllHotels(){
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping(path = "/{hotelId}/bookings")
    public ResponseEntity<List<BookingDTO>> getAllBookingsByHotelId(@PathVariable Long hotelId){
        return ResponseEntity.ok(bookingService.getAllBookingsByHotelId(hotelId));
    }

    @GetMapping(path = "/{hotelId}/reports")
    public ResponseEntity<HotelReportDTO> getHotelReport(@PathVariable Long hotelId,
                                                         @RequestParam(required = false) LocalDate startDate,
                                                         @RequestParam(required = false) LocalDate endDate){
        if(startDate == null) startDate = LocalDate.now().minusMonths(1);
        if(endDate == null) endDate = LocalDate.now();

        return ResponseEntity.ok(bookingService.getHotelReport(hotelId, startDate, endDate));
    }
}
