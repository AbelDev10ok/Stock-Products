package com.s3.stock.products.store_products.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.s3.stock.products.store_products.entitis.Sale;
import com.s3.stock.products.store_products.services.interfaces.ISaleServices;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("${api.base.path}/sales")
public class SaleController {

    @Autowired
    private ISaleServices saleServices;


    @PostMapping("/create")
    public ResponseEntity<?> createSale(@RequestBody Sale sale) {
        return ResponseEntity.ok(saleServices.createSale(sale));
    }

    @GetMapping("/get-all-sales")
    public ResponseEntity<?> getSales() {
        return ResponseEntity.ok(saleServices.getSales());
    }
    

}
