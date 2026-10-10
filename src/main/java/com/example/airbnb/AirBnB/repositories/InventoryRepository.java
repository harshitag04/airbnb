package com.example.airbnb.AirBnB.repositories;

import com.example.airbnb.AirBnB.DTOs.HotelDTO;
import com.example.airbnb.AirBnB.entities.Hotels;
import com.example.airbnb.AirBnB.entities.Inventory;
import com.example.airbnb.AirBnB.entities.Rooms;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    void deleteByRooms(Rooms rooms);

    @Query("""
            SELECT DISTINCT i.hotel
            FROM Inventory i
            WHERE i.city = :city
            AND i.date BETWEEN :startDate AND :endDate
            AND i.closed = false
            AND (i.totalCount - i.bookedCount -i.reservedCount) >= :numberOfRooms
            GROUP BY i.hotel, i.rooms
            HAVING COUNT(i.date) = :daysCount
            """)
    Page<Hotels> findHotelsWithAvailableInventory(String city, LocalDate startDate,
                                                  LocalDate endDate, Integer numberOfRooms,
                                                  Long daysCount, Pageable pageable);

    @Query("""
            SELECT i FROM Inventory i
            WHERE i.rooms.id = :roomId
            AND i.date BETWEEN :checkInDate AND :checkOutDate
            AND i.closed = FALSE
            AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
""")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableRepository(Long roomId, LocalDate checkInDate,
                                                   LocalDate checkOutDate, Integer roomsCount);
}
