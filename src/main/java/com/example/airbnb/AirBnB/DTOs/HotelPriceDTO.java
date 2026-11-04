package com.example.airbnb.AirBnB.DTOs;

import com.example.airbnb.AirBnB.entities.Hotels;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HotelPriceDTO {

    private Hotels hotels;
    private Double price;

    // The query looks specifically for this constructor signature
    public HotelPriceDTO(Hotels hotel, Double price) {
        this.hotels = hotel;
        this.price = price;
    }
}
