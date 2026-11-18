package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.DTOs.HotelPriceDTO;
import com.example.airbnb.AirBnB.DTOs.HotelSearchRequest;
import com.example.airbnb.AirBnB.DTOs.InventoryDTO;
import com.example.airbnb.AirBnB.DTOs.InventoryUpdateRequestDTO;
import com.example.airbnb.AirBnB.entities.Inventory;
import com.example.airbnb.AirBnB.entities.Rooms;
import com.example.airbnb.AirBnB.entities.User;
import com.example.airbnb.AirBnB.exceptions.ResourceNotFoundException;
import com.example.airbnb.AirBnB.repositories.HotelMinPriceRepository;
import com.example.airbnb.AirBnB.repositories.InventoryRepository;
import com.example.airbnb.AirBnB.repositories.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.airbnb.utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService{

    private final ModelMapper modelMapper;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;

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
                    .reservedCount(0)
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
    public Page<HotelPriceDTO> searchHotels(HotelSearchRequest hotelSearchRequest) {

        Long daysCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate()) + 1;

        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());

        Page<HotelPriceDTO> hotelsPage = hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getNumberOfRooms(), daysCount, pageable);

        return hotelsPage;
    }

    @Override
    public List<InventoryDTO> getAllInventoriesByRoom(Long roomId) {
        log.info("Getting All inventory by room for room with id: {}", roomId);
        Rooms room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: "+roomId));

        User user = getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())) throw new AccessDeniedException("You are not the owner of room with id: "+roomId);

        List<Inventory> inventoryList = inventoryRepository.findByRoomsOrderByDate(room);

        return inventoryList.stream()
                .map((invt) -> modelMapper.map(invt, InventoryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, InventoryUpdateRequestDTO inventoryUpdateRequestDTO) {
        log.info("Updating All inventory by room for room with id {} between date range: {} - {}", roomId,
                inventoryUpdateRequestDTO.getStartDate(), inventoryUpdateRequestDTO.getEndDate());
        Rooms room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: "+roomId));

        User user = getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())) throw new AccessDeniedException("You are not the owner of room with id: "+roomId);

        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId, inventoryUpdateRequestDTO.getStartDate(),
                inventoryUpdateRequestDTO.getEndDate());

        inventoryRepository.updateInventory(roomId, inventoryUpdateRequestDTO.getStartDate(),
                inventoryUpdateRequestDTO.getEndDate(), inventoryUpdateRequestDTO.getSurgeFactor(),
                inventoryUpdateRequestDTO.getClosed());

    }
}
