package com.s3.stock.products.store_products.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.s3.stock.products.store_products.entitis.ProductAttribute;

@Repository
public interface IProductAttribute extends JpaRepository<ProductAttribute, Long> {
    // Optional<ProductAttribute> findByAttributeAndValue(Atribut attribute, String value);

}
