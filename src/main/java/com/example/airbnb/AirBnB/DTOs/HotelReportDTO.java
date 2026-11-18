package com.example.airbnb.AirBnB.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class HotelReportDTO {

    private Long bookingCount;
    private BigDecimal totalRevenue;
    private BigDecimal avgRevenue;

}
