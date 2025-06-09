package com.s3.stock.products.store_products.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.s3.stock.products.store_products.entitis.Provider;
import com.s3.stock.products.store_products.services.interfaces.IProviderServices;

@RestController
@RequestMapping("${api.base.path}/provider")
public class ProviderController {

    @Autowired
    private IProviderServices providerService;

    @PostMapping("/create")
    public ResponseEntity<?> createProvider(@RequestBody Provider provider) {
        providerService.saveProvider(provider);
        return ResponseEntity.ok("create");
    }

    @GetMapping("/list")
    public ResponseEntity<?> listProviders(){
        return ResponseEntity.ok(providerService.getProviders());
    }

    @DeleteMapping("/delete/{providerId}")
    public ResponseEntity<?> deleteProvider(@PathVariable Long providerId){
        providerService.deleteProvider(providerId);
        return ResponseEntity.ok("delete");
    }   

}
