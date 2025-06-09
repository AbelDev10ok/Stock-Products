package com.s3.stock.products.store_products.entitis.dto;

import java.time.LocalDateTime;

import com.s3.stock.products.store_products.util.MovementType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class InventoryMovementDto {

    private Long id;

    @Enumerated(EnumType.STRING)
    private MovementType movement;

    private String productName;

    private String productSku;

    private int quantity; // Cantidad de productos movidos

    private LocalDateTime movementDate; // Fecha del movimiento

    private String notes; // Notas adicionales (opcional) 

    public InventoryMovementDto() {
    }

    public InventoryMovementDto(Long id, MovementType movement, int quantity,
            LocalDateTime movementDate, String notes, String productName, String productSku) {
        this.id = id;
        this.movement = movement;
        this.quantity = quantity;
        this.movementDate = movementDate;
        this.notes = notes;
        this.productName = productName;
        this.productSku = productSku;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MovementType getMovement() {
        return movement;
    }

    public void setMovement(MovementType movement) {
        this.movement = movement;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(LocalDateTime movementDate) {
        this.movementDate = movementDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }


    
    
    
}
