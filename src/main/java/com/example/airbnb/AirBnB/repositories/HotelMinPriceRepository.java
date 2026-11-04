package com.example.airbnb.AirBnB.repositories;

import com.example.airbnb.AirBnB.DTOs.HotelPriceDTO;
import com.example.airbnb.AirBnB.entities.HotelMinPrice;
import com.example.airbnb.AirBnB.entities.Hotels;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice, Long> {

    @Query("""
            SELECT new com.example.airbnb.AirBnB.DTOs.HotelPriceDTO(i.hotels, AVG(i.price))
            FROM HotelMinPrice i
            WHERE i.hotels.city = :city
            AND i.date BETWEEN :startDate AND :endDate
            AND i.hotels.active = true
            GROUP BY i.hotels
            """)
    Page<HotelPriceDTO> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("numberOfRooms") Integer numberOfRooms,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );

    Optional<HotelMinPrice> findByHotelsAndDate(Hotels hotel, LocalDate date);

}
