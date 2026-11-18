package com.example.airbnb.AirBnB.DTOs;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InventoryUpdateRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal surgeFactor;
    private Boolean closed;
}
