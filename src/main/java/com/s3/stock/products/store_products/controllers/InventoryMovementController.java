package com.s3.stock.products.store_products.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.s3.stock.products.store_products.services.interfaces.IInventoryMovementeServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("${api.base.path}/inventory")
public class InventoryMovementController {

    @Autowired
    private IInventoryMovementeServices inventoryMovementeServices;

    @GetMapping("/all")
    public ResponseEntity<?> getAllInventory() {
        return ResponseEntity.ok().body(inventoryMovementeServices.getAll());        
    }

    @GetMapping("/current-product/{product_id}")
    public ResponseEntity<?> getCurrentStock(@PathVariable Long product_id) {
        return ResponseEntity.ok().body(inventoryMovementeServices.getCurrentStock(product_id));
    }
    

    @PostMapping("/add-entry/{productId}")
    public ResponseEntity<?> addInventoryEntry(@PathVariable Long productId, @RequestParam int quantity, @RequestParam String note) {
        inventoryMovementeServices.addInventoryEntry(productId, quantity, note);
        return ResponseEntity.ok().body("ok");
    }
    
    @PostMapping("/add-exit/{productId}")
    public ResponseEntity<?> addInventoryExit(@PathVariable Long productId, @RequestParam int quantity, @RequestParam String note) {
        inventoryMovementeServices.addInventoryExit(productId, quantity, note);
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/add-adjustment/{productId}")
    public ResponseEntity<?> addInventoryAdjustment(@PathVariable Long productId, @RequestParam int quantity, @RequestParam String note) {
        inventoryMovementeServices.addInventoryAdjustment(productId, quantity, note);
        return ResponseEntity.ok().body("ok");
    }
    
}
