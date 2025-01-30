package com.s3.stock.products.store_products.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.s3.stock.products.store_products.entitis.Category;
import com.s3.stock.products.store_products.entitis.Product;
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

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@Valid  @RequestBody ProductRequest productDto) {
        try {
            productServices.save(productDto);
            return ResponseEntity.ok(productDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // @PutMapping("/update")
    // public ResponseEntity<?> updateProduct(Product product) {
    //     return ResponseEntity.ok(productServices.save(product));
    // }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProduct(Long id) {
        return ResponseEntity.ok("Product deleted");
    }
}
