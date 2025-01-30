package com.s3.stock.products.store_products.services;

import java.util.List;

import com.s3.stock.products.store_products.entitis.Product;
import com.s3.stock.products.store_products.entitis.dto.ProductRequest;

public interface IProductServices {
    public List<Product> findAll();
    public Product findById(Long id);
    public Product save(ProductRequest product);
    public Product findByName(String name);
    public void delete(Long id);
    public List<Product> findByCategoryName(String name);
    public List<Product> findByNameContaining(String name);
    public List<Product> findByStockGreaterThanEqual(int stock);
    public void increaseStock(Long productId, int quantity);
    // public List<Product> findByPriceLessThanEqual(double price);
    // public List<Product> findByPriceGreaterThanEqual(double price);
    // public List<Product> findByPriceBetween(double price1, double price2);
    // public List<Product> findByStockBetween(int stock1, int stock2);
    // public List<Product> findByCategoryIdAndPriceBetween(Long categoryId, double price1, double price2);
    // public List<Product> findByCategoryIdAndStockBetween(Long categoryId, int stock1, int stock2);
    // public List<Product> findByCategoryIdAndStockGreaterThanEqual(Long categoryId, int stock);
    // public List<Product> findByCategoryIdAndPriceLessThanEqual(Long categoryId, double price);
    // public List<Product> findByCategoryIdAndPriceGreaterThanEqual(Long categoryId, double price);
    // public List<Product> findByCategoryIdAndStockGreaterThanEqualAndPriceLessThanEqual(Long categoryId, int stock, double price);    
}
