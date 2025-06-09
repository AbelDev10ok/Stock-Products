package com.s3.stock.products.store_products.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.s3.stock.products.store_products.entitis.Sale;

@Repository
public interface ISaleRepository extends JpaRepository<Sale, Long> {
    @Query("SELECT s FROM Sale s " +
           "JOIN FETCH s.seller u " +
           "JOIN FETCH s.details dt " +
           "JOIN FETCH dt.product p " +
           "WHERE s.id = :id")
    Optional<Sale> findByIdWithDetails(@Param("id") Long id);
}
