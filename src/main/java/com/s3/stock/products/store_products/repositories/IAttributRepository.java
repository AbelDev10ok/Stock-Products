package com.s3.stock.products.store_products.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.s3.stock.products.store_products.entitis.Atribut;

@Repository
public interface IAttributRepository extends JpaRepository<Atribut, Long> {

    Optional<Atribut> findByName(String name);
}
