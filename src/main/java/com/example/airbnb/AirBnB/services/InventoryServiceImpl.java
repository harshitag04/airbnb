package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.HotelDTO;
import com.example.airbnb.AirBnB.DTOs.HotelSearchRequest;
import com.example.airbnb.AirBnB.entities.Hotels;
import com.example.airbnb.AirBnB.entities.Inventory;
import com.example.airbnb.AirBnB.entities.Rooms;
import com.example.airbnb.AirBnB.repositories.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{

    private final ModelMapper modelMapper;
    private final InventoryRepository inventoryRepository;

    @Override
    public void initializeRoomForAYear(Rooms room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for (; !today.isAfter(endDate); today=today.plusDays(1)) {
            Inventory inventory = Inventory.builder()
                    .bookedCount(0)
                    .date(today)
                    .surgeFactor(BigDecimal.ONE)
                    .price(room.getBasePrice())
                    .totalCount(room.getTotalCount())
                    .city(room.getHotel().getCity())
                    .hotel(room.getHotel())
                    .rooms(modelMapper.map(room, Rooms.class))
                    .bookedCount(0)
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);
        }
    }

    @Override
    public void deleteAllInventories(Rooms room) {
        LocalDate today = LocalDate.now();
        inventoryRepository.deleteByRooms(room);
    }

    @Override
    public Page<HotelDTO> searchHotels(HotelSearchRequest hotelSearchRequest) {

        Long daysCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate()) + 1;

        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());

        Page<Hotels> hotelsPage = inventoryRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getNumberOfRooms(), daysCount, pageable);

        return hotelsPage.map((element) -> modelMapper.map(element, HotelDTO.class));
    }
}
