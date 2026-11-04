package com.example.airbnb.AirBnB.strategy;

import com.example.airbnb.AirBnB.entities.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class OccupancyPricingStrategy implements DynamicPricingStrategy{

    private final DynamicPricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {

        BigDecimal price = wrapped.calculatePrice(inventory);
        double occupancyRate = (double)inventory.getBookedCount() / inventory.getTotalCount();
        if(occupancyRate > 0.8){
            price = price.multiply(BigDecimal.valueOf(1.2));
        }
        return price;
    }
}
