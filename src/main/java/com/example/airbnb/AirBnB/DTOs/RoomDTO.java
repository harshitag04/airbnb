package com.example.airbnb.AirBnB.DTOs;

import com.example.airbnb.AirBnB.entities.Hotels;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RoomDTO {

    private Long id;
    private String type;
    private BigDecimal basePrice;
    private Integer totalCount;
    private String[] photos;
    private String[] amenities;
    private Integer capacity;
}
