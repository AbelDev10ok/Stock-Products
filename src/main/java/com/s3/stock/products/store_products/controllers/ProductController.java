package com.s3.stock.products.store_products.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.s3.stock.products.store_products.entitis.dto.ProductRequest;
import com.s3.stock.products.store_products.services.IProductServices;

import jakarta.validation.Valid;


@RestController
@RequestMapping("${api.base.path}/products")
public class ProductController {

    @Autowired
    private IProductServices productServices;

    @GetMapping("/list")
    public ResponseEntity<?> listProducts() {
        return ResponseEntity.ok(productServices.findAll());
    }

    @GetMapping("/find/name")
    public ResponseEntity<?> getProductByName(@RequestParam String name) {
        return ResponseEntity.ok(productServices.findByName(name));
    }

    @GetMapping("/find/id/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productServices.findById(id));
    }

    @GetMapping("/find/category")
    public ResponseEntity<?> getProductsByCategory(@RequestParam String categoryName) {
        return ResponseEntity.ok(productServices.findByCategory(categoryName));
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
        productServices.increaseStock(id, quantity);
        return ResponseEntity.ok("Stock increased by " + quantity + " for product with id: " + id);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@Valid  @RequestBody ProductRequest productDto) {
        try {
            productServices.save(productDto);
            return ResponseEntity.ok("Product added successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProduct(@RequestBody ProductRequest product) {
        return ResponseEntity.ok(productServices.save(product));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productServices.delete(id);
        return ResponseEntity.ok("Deleted product with id: " + id);
    }
}
