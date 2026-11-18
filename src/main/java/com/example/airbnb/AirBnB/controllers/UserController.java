package com.example.airbnb.AirBnB.controllers;

import com.example.airbnb.AirBnB.DTOs.BookingDTO;
import com.example.airbnb.AirBnB.DTOs.GuestDTO;
import com.example.airbnb.AirBnB.DTOs.ProfileUpdateRequestDTO;
import com.example.airbnb.AirBnB.DTOs.UserDTO;
import com.example.airbnb.AirBnB.services.BookingService;
import com.example.airbnb.AirBnB.services.GuestService;
import com.example.airbnb.AirBnB.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;
    private final GuestService guestService;

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequestDTO profileUpdateRequestDTO) {
        userService.updateProfile(profileUpdateRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/myBookings")
    public ResponseEntity<List<BookingDTO>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @GetMapping("/guests")
    public ResponseEntity<List<GuestDTO>> getAllGuests() {
        return ResponseEntity.ok(guestService.getAllGuests());
    }

    @PostMapping("/guests")
    public ResponseEntity<GuestDTO> addNewGuest(@RequestBody GuestDTO guestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.addNewGuest(guestDTO));
    }

    @PutMapping("guests/{guestId}")
    public ResponseEntity<Void> updateGuest(@PathVariable Long guestId, @RequestBody GuestDTO guestDTO) {
        guestService.updateGuest(guestId, guestDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("guests/{guestId}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId) {
        guestService.deleteGuest(guestId);
        return ResponseEntity.noContent().build();
    }

}
