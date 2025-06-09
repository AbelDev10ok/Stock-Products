package com.s3.stock.products.store_products.controllers;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s3.stock.products.store_products.entitis.dto.ProductAttributeDto;
import com.s3.stock.products.store_products.entitis.dto.ProductResponseDto;
import com.s3.stock.products.store_products.services.interfaces.IProductServices;
import com.s3.stock.products.store_products.entitis.dto.ProductRequestDto;
import com.s3.stock.products.store_products.entitis.dto.ProductRequestUpdateDto;
import com.s3.stock.products.store_products.util.ValidationDto;

import jakarta.validation.Valid;


@RestController
@RequestMapping("${api.base.path}/products")
public class ProductController {

    @Autowired
    private IProductServices productServices;
    @Autowired
    private	ValidationDto validationDto;

    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> listProducts() {
        return ResponseEntity.ok(productServices.findAll());
    }

    @GetMapping("/find/name")
    public ResponseEntity<?> getProductByName(@RequestParam String name) {
            return ResponseEntity.ok(productServices.findByName(name));
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

    @GetMapping("/find")
    public ResponseEntity<?> getProductByNameAndStock(
        @RequestParam(required = false) String name, 
        @RequestParam(required = false) Integer stock) {
        return ResponseEntity.ok(productServices.findByNameContainingAndStock(name, stock));
    }

    // @GetMapping("/find/stock/{stock}")
    // public ResponseEntity<?> getProductsByStock(@PathVariable int stock) {
    //     return ResponseEntity.ok(productServices.findByStockGreaterThanEqual(stock));
    // }


    @PutMapping("/increase/product/{id}")
    public ResponseEntity<?> increaseStock(@PathVariable Long id, @RequestParam int quantity) {
        try {
            productServices.increaseStock(id, quantity);
            return ResponseEntity.ok("Stock increased by " + quantity + " for product with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(
            @Valid @ModelAttribute ProductRequestDto productDto,
            @RequestParam("productAttributes") String productAttributesJson,
            BindingResult result) {

            // Deserializo el productAttributes
            ObjectMapper objectMapper = new ObjectMapper();
            ProductResponseDto product = new ProductResponseDto();
            try {
                List<ProductAttributeDto> productAttributes = objectMapper.readValue( productAttributesJson,
                    new TypeReference<List<ProductAttributeDto>>() {}
                );
                product.setProductAttributes(productAttributes);
                product.setBrand(productDto.getBrand());
                product.setCategory(productDto.getCategory());
                product.setModel(productDto.getModel());
                product.setPrice(productDto.getPrice());
                product.setStock(productDto.getStock());
                product.setSku(productDto.getSku());
                product.setImage(productDto.getImage());
                product.setName(productDto.getName());
                product.setDescription(productDto.getDescription());
                product.setProvider(productDto.getProvider());
                
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Invalid JSON format for productAttributes");
            }

            if (result.hasErrors()) {
                return validationDto.validation(result);
            }

            try {
                ProductResponseDto productCreate = productServices.save(product);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("message", "Product created successfully", "product", productCreate));
            } 
            catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(ex.getMessage());
            } catch (Exception ex) {
                // String message = extractErrorMessage.extractErrorMessage(ex.getMessage()); //o el mensaje que quieras enviar

                throw new RuntimeException("Error al crear el producto: " + ex.getMessage());
            }
    }

    @PutMapping(value = "/update/{product_id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public ResponseEntity<?> updateProduct(
        @Valid @ModelAttribute ProductRequestUpdateDto productDto,
        @RequestParam("productAttributes") String productAttributesJson,
        @PathVariable("product_id") Long productId,

        BindingResult result 
        ) {
            // Deserializar productAttributes
            ObjectMapper objectMapper = new ObjectMapper();
            ProductResponseDto product = new ProductResponseDto();

            try {
                List<ProductAttributeDto> productAttributes = objectMapper.readValue( productAttributesJson,
                    new TypeReference<List<ProductAttributeDto>>() {}
                );
                product.setProductAttributes(productAttributes);
                product.setBrand(productDto.getBrand());
                product.setCategory(productDto.getCategory());
                product.setModel(productDto.getModel());
                product.setPrice(productDto.getPrice());
                product.setSku(productDto.getSku());
                product.setImage(productDto.getImage());
                product.setName(productDto.getName());
                product.setDescription(productDto.getDescription());
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Invalid JSON format for productAttributes");
            }
            

        if(result.hasErrors()) {
            return validationDto.validation(result);
        }
        try {
            productServices.update(product,productId);
        return ResponseEntity.ok(Map.of("message", "Product updated successfully"));            
        } catch (IllegalArgumentException ex) {
            // IllegalArgumentException Se lanza cuando se pasa un argumento ilegal o inadecuado a un método. 
            throw new IllegalArgumentException(ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("Error al crear el producto " + ex.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
            productServices.delete(id);
            return ResponseEntity.ok("Product deleted");

    }
}
