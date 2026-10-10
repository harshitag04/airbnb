package com.example.airbnb.AirBnB.repositories;

import com.example.airbnb.AirBnB.entities.Hotels;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotels, Long> {

}
