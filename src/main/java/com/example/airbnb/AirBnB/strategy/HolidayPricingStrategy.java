package com.example.airbnb.AirBnB.strategy;

import com.example.airbnb.AirBnB.entities.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements DynamicPricingStrategy{

    private final DynamicPricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {

        BigDecimal price = wrapped.calculatePrice(inventory);
        boolean isTodayHoliday = true;
        if (isTodayHoliday){
            price = price.multiply(BigDecimal.valueOf(1.25));
        }
        return price;
    }
}
