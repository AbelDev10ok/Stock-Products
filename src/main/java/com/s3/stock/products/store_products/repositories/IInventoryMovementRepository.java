package com.s3.stock.products.store_products.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.s3.stock.products.store_products.entitis.InventoryMovement;

@Repository
public interface IInventoryMovementRepository extends JpaRepository<InventoryMovement,Long> {
    List<InventoryMovement> findByProductId(Long id);
}
