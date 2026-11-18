package com.example.airbnb.AirBnB.controllers;

import com.example.airbnb.AirBnB.DTOs.InventoryDTO;
import com.example.airbnb.AirBnB.DTOs.InventoryUpdateRequestDTO;
import com.example.airbnb.AirBnB.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping(path = "/rooms/{roomId}")
    public ResponseEntity<List<InventoryDTO>> getAllInventoriesByRoom(@PathVariable Long roomId){
        return ResponseEntity.ok(inventoryService.getAllInventoriesByRoom(roomId));
    }

    @PatchMapping(path = "/rooms/{roomId}")
    public ResponseEntity<Void> updateInventory(@PathVariable Long roomId,
                                                @RequestBody InventoryUpdateRequestDTO inventoryUpdateRequestDTO){

        inventoryService.updateInventory(roomId, inventoryUpdateRequestDTO);
        return ResponseEntity.noContent().build();
    }

}
