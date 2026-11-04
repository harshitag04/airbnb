package com.example.airbnb.AirBnB.controllers;

import com.example.airbnb.AirBnB.DTOs.HotelPriceDTO;
import com.example.airbnb.AirBnB.DTOs.HotelSearchRequest;
import com.example.airbnb.AirBnB.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/hotels")
public class HotelBrowseController {

    private final InventoryService inventoryService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDTO>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest){

        Page<HotelPriceDTO> page = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);

    }

}
