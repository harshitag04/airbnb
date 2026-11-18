package com.example.airbnb.AirBnB.repositories;

import com.example.airbnb.AirBnB.entities.Hotels;
import com.example.airbnb.AirBnB.entities.Inventory;
import com.example.airbnb.AirBnB.entities.Rooms;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    List<Inventory> findByHotelAndDateBetween(Hotels hotels, LocalDate startDate, LocalDate endDate);


    @Query("""
SELECT i FROM Inventory i
WHERE i.rooms.id = :roomId
AND i.date BETWEEN :startDate AND :endDate
AND (i.totalCount - i.bookedCount) >= :numberOfRooms
AND i.closed = false
""")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory>findAndLockReservedInventory(@Param("roomId") Long roomId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 @Param("numberOfRooms") int numberOfRooms);

    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount + :numberOfRooms
                WHERE i.rooms.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount - i.reservedCount) >= :numberOfRooms
                  AND i.closed = false
            """)
    void initBooking(@Param("roomId") Long roomId,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate,
                     @Param("numberOfRooms") int numberOfRooms);


    @Modifying
    @Query("""
UPDATE Inventory i
SET i.reservedCount = i.reservedCount - :numberOfRooms,
i.bookedCount = i.bookedCount + :numberOfRooms
WHERE i.rooms.id = :roomId
AND i.date BETWEEN :startDate AND :endDate
AND i.totalCount - i.bookedCount >= :numberOfRooms
AND i.reservedCount >= :numberOfRooms
AND i.closed = false
""")
    void confirmBooking(@Param("roomId") Long roomId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("numberOfRooms") int numberOfRooms);


    @Modifying
    @Query("""
UPDATE Inventory i
                SET i.bookedCount = i.bookedCount - :numberOfRooms
                WHERE i.rooms.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                  AND i.closed = false
""")
    void cancelBooking(@Param("roomId") Long roomId,
                       @Param("startDate") LocalDate startDate,
                       @Param("endDate") LocalDate endDate,
                       @Param("numberOfRooms") int numberOfRooms);

    @Modifying
    @Query("""
UPDATE Inventory i
                SET i.surgeFactor = :surgeFactor,
                i.closed = :closed
                WHERE i.rooms.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND i.closed = false
""")
    void updateInventory(Long roomId, LocalDate startDate, LocalDate endDate, BigDecimal surgeFactor, Boolean closed);


    @Query("""
                SELECT i
                FROM Inventory i
                WHERE i.rooms.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> getInventoryAndLockBeforeUpdate(Long roomId, LocalDate startDate, LocalDate endDate);

    List<Inventory> findByRoomsOrderByDate(Rooms room);
}
