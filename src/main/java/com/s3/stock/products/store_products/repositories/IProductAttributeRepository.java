package com.s3.stock.products.store_products.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.s3.stock.products.store_products.entitis.ProductAttribute;



@Repository
public interface IProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
    void deleteByProductIdAndValueAttributeAtributId(Long id,Long attributId);
}
