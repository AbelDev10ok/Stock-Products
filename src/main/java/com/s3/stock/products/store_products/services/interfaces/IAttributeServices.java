package com.s3.stock.products.store_products.services.interfaces;

import java.util.List;

import com.s3.stock.products.store_products.entitis.Atribut;
import com.s3.stock.products.store_products.entitis.dto.AttributeRequest;

public interface IAttributeServices {
    void save(AttributeRequest attribute);
    void delete(AttributeRequest attributeDto) ;
    List<Atribut> findAll();
}
