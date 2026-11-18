package com.example.airbnb.AirBnB.services;

import com.example.airbnb.AirBnB.entities.Bookings;
import com.example.airbnb.AirBnB.entities.User;
import com.example.airbnb.AirBnB.repositories.BookingRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CheckOutServiceImpl implements CheckOutService{

    private final BookingRepository bookingRepository;

    @Override
    public String getCheckOutSession(Bookings booking, String successUrl, String failureUrl) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {

            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .build();

            Customer customer = Customer.create(customerCreateParams);

            SessionCreateParams sessionCreateParams = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("inr")
                                                    .setUnitAmount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(booking.getHotels().getName() + ":" + booking.getRooms().getType())
                                                            .setDescription("Booking ID: " + booking.getId())
                                                            .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(sessionCreateParams);

            booking.setPaymentSessionId(session.getId());
            bookingRepository.save(booking);

            return session.getUrl();

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }
}
