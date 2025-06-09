package com.s3.stock.products.store_products.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.s3.stock.products.store_products.entitis.DetailSale;

@Repository
public interface IDetailSaleRepository extends JpaRepository<DetailSale, Long> {

}
