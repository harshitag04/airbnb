package com.example.airbnb.AirBnB.repositories;

import com.example.airbnb.AirBnB.entities.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Bookings, Long> {
}
