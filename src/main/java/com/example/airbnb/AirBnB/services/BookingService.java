package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.BookingDTO;
import com.example.airbnb.AirBnB.DTOs.BookingRequest;
import com.example.airbnb.AirBnB.DTOs.GuestDTO;
import com.example.airbnb.AirBnB.DTOs.HotelReportDTO;
import com.stripe.model.Event;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    BookingDTO initialiseBooking(BookingRequest bookingRequest);

    BookingDTO addGuest(Long Id, List<GuestDTO> guestDTOList);

    String initiatePayment(Long bookingId);

    void capturePayment(Event event);

    void cancelPayment(Long bookingId);

    List<BookingDTO> getAllBookingsByHotelId(Long hotelId);

    HotelReportDTO getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    List<BookingDTO> getMyBookings();
}
