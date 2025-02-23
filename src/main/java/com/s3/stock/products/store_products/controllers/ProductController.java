package com.s3.stock.products.store_products.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.s3.stock.products.store_products.entitis.dto.ProductDto;
import com.s3.stock.products.store_products.services.IProductServices;
import com.s3.stock.products.store_products.util.ValidationDto;

import jakarta.validation.Valid;


@RestController
@RequestMapping("${api.base.path}/products")
public class ProductController {

    @Autowired
    private IProductServices productServices;
    @Autowired
    private	ValidationDto validationDto;

    @GetMapping("/list")
    public ResponseEntity<?> listProducts() {
        return ResponseEntity.ok(productServices.findAll());
    }

    @GetMapping("/find/name")
    public ResponseEntity<?> getProductByName(@RequestParam String name) {
        try {
            return ResponseEntity.ok(productServices.findByName(name));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/find/id/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productServices.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/find/category")
    public ResponseEntity<?> getProductsByCategory(@RequestParam String categoryName) {
        try {
            return ResponseEntity.ok(productServices.findByCategory(categoryName));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/find/containing-name")
    public ResponseEntity<?> getProductByNameContaing(@RequestParam String name) {
        return ResponseEntity.ok(productServices.findByNameContaining(name.toLowerCase()));
    }

    @GetMapping("/find/stock/{stock}")
    public ResponseEntity<?> getProductsByStock(@PathVariable int stock) {
        return ResponseEntity.ok(productServices.findByStockGreaterThanEqual(stock));
    }


    @PutMapping("/increase/product/{id}")
    public ResponseEntity<?> increaseStock(@PathVariable Long id, @RequestParam int quantity) {
        try {
            productServices.increaseStock(id, quantity);
            return ResponseEntity.ok("Stock increased by " + quantity + " for product with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@Valid  @RequestBody ProductDto productDto, BindingResult result) {
        if(result.hasErrors()) {
            return validationDto.validation(result);
        }
        
        try {
            productServices.save(productDto);
            return ResponseEntity.ok("Producto creado exitosamente");
        } catch (IllegalArgumentException ex) {
            // IllegalArgumentException Se lanza cuando se pasa un argumento ilegal o inadecuado a un método. 
            throw new IllegalArgumentException(ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("Error al crear el producto" + ex.getMessage());
        }

    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody ProductDto product, BindingResult result) {
        if(result.hasErrors()) {
            return validationDto.validation(result);
        }
        try {
            productServices.update(product);
            return ResponseEntity.ok("Product updated successfully");
            
        } catch (IllegalArgumentException ex) {
            // IllegalArgumentException Se lanza cuando se pasa un argumento ilegal o inadecuado a un método. 
            throw new IllegalArgumentException(ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("Error al crear el producto " + ex.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productServices.delete(id);
            return ResponseEntity.ok("Product deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
