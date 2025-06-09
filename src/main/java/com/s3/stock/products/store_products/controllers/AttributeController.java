package com.s3.stock.products.store_products.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.s3.stock.products.store_products.entitis.dto.AttributeRequest;
import com.s3.stock.products.store_products.services.interfaces.IAttributeServices;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("${api.base.path}/attribute")
public class AttributeController {

    @Autowired
    private IAttributeServices attributeServices;

    @PostMapping("/save")
    public ResponseEntity<?> create(@RequestBody AttributeRequest entity) {
        
        attributeServices.save(entity);
        return ResponseEntity.ok("create");
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(attributeServices.findAll());
    }
    
    

}
