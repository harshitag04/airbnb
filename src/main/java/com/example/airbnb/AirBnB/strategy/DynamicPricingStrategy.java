package com.example.airbnb.AirBnB.strategy;

import com.example.airbnb.AirBnB.entities.Inventory;

import java.math.BigDecimal;

public interface DynamicPricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);

}
