package com.example.airbnb.AirBnB.controllers;

import com.example.airbnb.AirBnB.services.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/webhook")
@Slf4j
public class WebHookController {

    private final BookingService bookingService;

    @Value("${stripe.secret.webhook}")
    private String webhookSecretKey;

    @PostMapping(path = "/payment")
    public ResponseEntity<Void> capturePayment(@RequestBody String payLoad, @RequestHeader("Stripe-Signature") String sigHeader){
        log.info("Inside controller capturePayments");
        try {
            System.out.print("Inside controller");
            Event event = Webhook.constructEvent(payLoad, sigHeader, webhookSecretKey);

            bookingService.capturePayment(event);

            return ResponseEntity.noContent().build();

        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
    }

}
