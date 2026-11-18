package com.example.airbnb.AirBnB.strategy;

import com.example.airbnb.AirBnB.entities.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingService {

    public BigDecimal calculateDynamicPricing(Inventory inventory){

        DynamicPricingStrategy dynamicPricingStrategy = new BasePricingStrategy();

        DynamicPricingStrategy dynamicPricingStrategy1 = new OccupancyPricingStrategy(dynamicPricingStrategy);
        DynamicPricingStrategy dynamicPricingStrategy2 = new UrgencyPricingStrategy(dynamicPricingStrategy1);
        DynamicPricingStrategy dynamicPricingStrategy3 = new HolidayPricingStrategy(dynamicPricingStrategy2);
        DynamicPricingStrategy dynamicPricingStrategy4 = new SurgePricingStrategy(dynamicPricingStrategy3);

        return dynamicPricingStrategy4.calculatePrice(inventory);
    }

    //    Return the sum of price of this inventory list
    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList) {
        return inventoryList.stream()
                .map(this::calculateDynamicPricing)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
