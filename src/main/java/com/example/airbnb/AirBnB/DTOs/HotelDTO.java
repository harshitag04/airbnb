package com.example.airbnb.AirBnB.DTOs;

import com.example.airbnb.AirBnB.entities.HotelContactInfo;
import lombok.Data;

@Data
public class HotelDTO {

    private Long id;
    private String name;
    private String city;
    private HotelContactInfo contactInfo;
    private String[] photos;
    private String[] amenities;
    private boolean active;

}
