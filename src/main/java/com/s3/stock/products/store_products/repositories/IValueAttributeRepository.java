package com.s3.stock.products.store_products.repositories;

import java.lang.foreign.Linker.Option;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.s3.stock.products.store_products.entitis.ValueAttribut;


@Repository
public interface IValueAttributeRepository  extends JpaRepository<ValueAttribut, Long> {    
    Optional<ValueAttribut> findById(Long id);
    Optional<ValueAttribut> findByValueAndAtributName(String value, String name);

}
