package com.s3.stock.products.store_products.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.s3.stock.products.store_products.entitis.InventoryMovement;
import com.s3.stock.products.store_products.entitis.Product;
import com.s3.stock.products.store_products.entitis.dto.InventoryMovementDto;
import com.s3.stock.products.store_products.repositories.IInventoryMovementRepository;
import com.s3.stock.products.store_products.repositories.IProductRepository;
import com.s3.stock.products.store_products.services.interfaces.IInventoryMovementeServices;
import com.s3.stock.products.store_products.util.MovementType;

import jakarta.persistence.EntityNotFoundException;


@Service
public class InventoryMovementeServices implements IInventoryMovementeServices{

    @Autowired
    private IInventoryMovementRepository iInventoryMovementRepository;

    @Autowired
    private IProductRepository productRepository;

    @Transactional
    @Override
    public void addInventoryAdjustment(Long productId, int quantity, String notes) {
        Product product = productRepository.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        // Crear el movimiento de ajuste
        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setMovement(MovementType.ADJUSTMENT);
        movement.setQuantity(quantity);
        movement.setMovementDate(LocalDateTime.now());
        movement.setNotes(notes);

        // Guardar el movimiento
        iInventoryMovementRepository.save(movement);

        // Actualizar el stock del producto
        product.setStock(product.getStock() + quantity); // Puede ser positivo o negativo
        productRepository.save(product);
    }

    @Transactional
    @Override
    public void addInventoryEntry(Long productId, int quantity, String notes) {

        Product product = productRepository.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    
        // Crear el movimiento de entrada
        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setMovement(MovementType.ENTRY);
        movement.setQuantity(quantity);
        movement.setMovementDate(LocalDateTime.now());
        movement.setNotes(notes);
    
        // Guardar el movimiento
        iInventoryMovementRepository.save(movement);
    
        // Actualizar el stock del producto
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
        
    }

    @Transactional
    @Override
    public void addInventoryExit(Long productId, int quantity, String notes) {
        
        Product product = productRepository.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        // Verificar que haya suficiente stock
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("No hay suficiente stock para realizar la salida");
        }

        // Crear el movimiento de salida
        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setMovement(MovementType.EXIT);
        movement.setQuantity(quantity);
        movement.setMovementDate(LocalDateTime.now());
        movement.setNotes(notes);

        // Guardar el movimiento
        iInventoryMovementRepository.save(movement);

        // Actualizar el stock del producto
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    
        
    }

    @Transactional(readOnly = true)
    @Override
    public List<InventoryMovementDto> getAll() {
        return iInventoryMovementRepository.findAll().stream().map((movement) -> {
            InventoryMovementDto dto = new InventoryMovementDto();
            dto.setId(movement.getId());
            dto.setMovement(movement.getMovement());
            dto.setMovementDate(movement.getMovementDate());
            dto.setNotes(movement.getNotes());
            dto.setQuantity(movement.getQuantity());
            dto.setProductSku(movement.getProduct().getSku());
            dto.setProductName(movement.getProduct().getName());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public int getCurrentStock(Long productId) {
        Product product = productRepository.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        return product.getStock();
    }

    @Transactional(readOnly = true)
    @Override
    public List<InventoryMovementDto> getInventoryMovements(Long productId) {

        productRepository.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));


        return iInventoryMovementRepository.findByProductId(productId).stream().map((movement) -> {
            InventoryMovementDto dto = new InventoryMovementDto();
            dto.setId(movement.getId());
            dto.setMovement(movement.getMovement());
            dto.setMovementDate(movement.getMovementDate());
            dto.setNotes(movement.getNotes());
            dto.setQuantity(movement.getQuantity());
            dto.setProductSku(movement.getProduct().getSku());
            dto.setProductName(movement.getProduct().getName());
            return dto;
        }).collect(Collectors.toList());
    }


    
    
}
