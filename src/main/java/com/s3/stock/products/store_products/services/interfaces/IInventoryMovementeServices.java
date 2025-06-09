package com.s3.stock.products.store_products.services.interfaces;

import java.util.List;

import com.s3.stock.products.store_products.entitis.dto.InventoryMovementDto;

public interface IInventoryMovementeServices {
    void addInventoryEntry(Long productId, int quantity, String notes);
    void addInventoryExit(Long productId, int quantity, String notes);
    void addInventoryAdjustment(Long productId, int quantity, String notes);
    int getCurrentStock(Long productId);
    List<InventoryMovementDto> getInventoryMovements(Long productId);
    List<InventoryMovementDto> getAll();
}
