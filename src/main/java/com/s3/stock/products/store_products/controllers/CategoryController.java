package com.s3.stock.products.store_products.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.s3.stock.products.store_products.entitis.dto.CategoryRequest;
import com.s3.stock.products.store_products.services.interfaces.ICategoryServices;
import com.s3.stock.products.store_products.util.ValidationDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.base.path}/categories")
public class CategoryController {

    @Autowired
    private ICategoryServices categoryServices;

    @Autowired
    private	ValidationDto validationDto;

    @GetMapping("/all")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(categoryServices.findAll());
    }

    @GetMapping("/find/by-name")
    public ResponseEntity<?> getCategoryByName(@RequestParam String name) { 
        return ResponseEntity.ok(categoryServices.findByName(name));

    }

    @GetMapping("/find/by-id/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryServices.findById(id));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCategory(@Valid @RequestBody CategoryRequest categoryDto, BindingResult result ) {
        if(result.hasErrors()) {
            return validationDto.validation(result);
        }
            categoryServices.delete(categoryDto);
            return ResponseEntity.ok("Category deleted successfully");
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveCategory(@Valid @RequestBody CategoryRequest categoryDto, BindingResult result ) {
        if(result.hasErrors()){
            return validationDto.validation(result);
        }
            return ResponseEntity.ok(categoryServices.save(categoryDto));
    }
}
