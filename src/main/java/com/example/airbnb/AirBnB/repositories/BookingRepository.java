package com.example.airbnb.AirBnB.repositories;

import com.example.airbnb.AirBnB.entities.Bookings;
import com.example.airbnb.AirBnB.entities.Hotels;
import com.example.airbnb.AirBnB.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Bookings, Long> {
    Optional<Bookings> findByPaymentSessionId(String sessionId);

    List<Bookings> findByHotels(Hotels hotel);

    List<Bookings> findByHotelsAndCreatedAtBetween(Hotels hotel, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Bookings> findByUser(User user);
}
