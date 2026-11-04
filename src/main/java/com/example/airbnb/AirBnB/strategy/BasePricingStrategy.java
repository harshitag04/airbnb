package com.example.airbnb.AirBnB.strategy;

import com.example.airbnb.AirBnB.entities.Inventory;

import java.math.BigDecimal;

public class BasePricingStrategy implements DynamicPricingStrategy {

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRooms().getBasePrice();
    }
}
