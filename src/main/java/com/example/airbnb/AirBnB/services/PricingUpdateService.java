package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.entities.HotelMinPrice;
import com.example.airbnb.AirBnB.entities.Hotels;
import com.example.airbnb.AirBnB.entities.Inventory;
import com.example.airbnb.AirBnB.repositories.HotelMinPriceRepository;
import com.example.airbnb.AirBnB.repositories.HotelRepository;
import com.example.airbnb.AirBnB.repositories.InventoryRepository;
import com.example.airbnb.AirBnB.strategy.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PricingUpdateService {

    private final InventoryRepository inventoryRepository;
    private final HotelRepository hotelRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;

    @Scheduled(cron = "0 */5 * * * *")
    public void updatePrices(){
        int page = 0;
        int batchSize = 100;

        while(true){
            Page<Hotels> hotels = hotelRepository.findAll(PageRequest.of(page, batchSize));
            if(hotels.isEmpty()){
                break;
            }
            hotels.getContent().forEach(this::updateHotelPrices);
            page++;
        }
    }

    public void updateHotelPrices(Hotels hotels) {

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotels, startDate, endDate);

        updateInventoryPrices(inventoryList);

        updateHotelMinPrice(hotels, inventoryList, startDate, endDate);

    }

    private void updateHotelMinPrice(Hotels hotels, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {

        Map<LocalDate, BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e->e.getValue().orElse(BigDecimal.ZERO)));

        // Prepare HotelPrice entities in bulk
        List<HotelMinPrice> hotelPrices = new ArrayList<>();

        dailyMinPrices.forEach((date, price) -> {
            HotelMinPrice hotelPrice = hotelMinPriceRepository.findByHotelsAndDate(hotels, date)
                    .orElse(new HotelMinPrice(hotels, date));
            hotelPrice.setPrice(price);
            hotelPrices.add(hotelPrice);
        });

        // Save all HotelPrice entities in bulk
        hotelMinPriceRepository.saveAll(hotelPrices);
    }

    private void updateInventoryPrices(List<Inventory> inventoryList) {

        inventoryList.forEach((inventory) -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });
        inventoryRepository.saveAll(inventoryList);
    }

}
