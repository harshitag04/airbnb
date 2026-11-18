package com.example.airbnb.AirBnB.controllers;

import com.example.airbnb.AirBnB.DTOs.RoomDTO;
import com.example.airbnb.AirBnB.repositories.RoomRepository;
import com.example.airbnb.AirBnB.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
public class RoomAdminController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDTO> createNewRoom(@PathVariable Long hotelId,
                                                 @RequestBody RoomDTO roomDto) {
        RoomDTO room = roomService.createRoom(hotelId, roomDto);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRoomsInHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getAllRoomsInHotel(hotelId));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<RoomDTO> deleteRoomById(@PathVariable Long roomId) {
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDTO> updateRoomById(@PathVariable Long hotelId, @PathVariable Long roomId,
                                                  @RequestBody RoomDTO roomDTO){
        return ResponseEntity.ok(roomService.updateRoomById(hotelId, roomId, roomDTO));
    }
}
