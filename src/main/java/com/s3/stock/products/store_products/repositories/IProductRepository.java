package com.s3.stock.products.store_products.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.s3.stock.products.store_products.entitis.Product;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> { 

    List<Product> findByCategoryName(String name);
    List<Product> findByNameContaining(String name);
    Optional<Product> findByName(String name);
    List<Product> findByStockGreaterThanEqual(int stock);
    // List<Product> findByPriceLessThanEqual(double price);
    // List<Product> findByPriceGreaterThanEqual(double price);
    // List<Product> findByPriceBetween(double price1, double price2);
    // List<Product> findByStockBetween(int stock1, int stock2);
    // List<Product> findByCategoryIdAndPriceBetween(Long categoryId, double price1, double price2);
    // List<Product> findByCategoryIdAndStockBetween(Long categoryId, int stock1, int stock2);
    // List<Product> findByCategoryIdAndStockGreaterThanEqual(Long categoryId, int stock);
    // List<Product> findByCategoryIdAndPriceLessThanEqual(Long categoryId, double price);
    // List<Product> findByCategoryIdAndPriceGreaterThanEqual(Long categoryId, double price);
    // List<Product> findByCategoryIdAndStockGreaterThanEqualAndPriceLessThanEqual(Long categoryId, int stock, double price);

}
