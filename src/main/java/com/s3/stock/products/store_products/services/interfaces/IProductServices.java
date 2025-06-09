package com.s3.stock.products.store_products.services.interfaces;

import java.io.IOException;
import java.util.List;

import com.s3.stock.products.store_products.entitis.Product;
import com.s3.stock.products.store_products.entitis.dto.ProductResponseDto;

public interface IProductServices {
    public List<ProductResponseDto> findAll();
    public ProductResponseDto findById(Long id);
    public Product getProductfindById(Long id);
    public ProductResponseDto save(ProductResponseDto productdo) throws IOException, IllegalAccessException, Exception;
    public void saveProduct(Product product);
    public void update(ProductResponseDto productDto, Long productId);
    public ProductResponseDto findByName(String name);
    public void delete(Long id);
    public List<ProductResponseDto> findByCategory(String name);
    public List<ProductResponseDto> findByNameContainingAndStock(String name, Integer stock);
    public List<ProductResponseDto> findByStockGreaterThanEqual(int stock);
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
