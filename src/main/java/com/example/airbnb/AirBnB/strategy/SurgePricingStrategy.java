package com.example.airbnb.AirBnB.strategy;

import com.example.airbnb.AirBnB.entities.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class SurgePricingStrategy implements DynamicPricingStrategy{

    private final DynamicPricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {

        BigDecimal price = wrapped.calculatePrice(inventory);
        return price.multiply(inventory.getSurgeFactor());
    }
}
