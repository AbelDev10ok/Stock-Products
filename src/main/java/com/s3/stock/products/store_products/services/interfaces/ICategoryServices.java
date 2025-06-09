package com.s3.stock.products.store_products.services.interfaces;

import java.util.List;

import com.s3.stock.products.store_products.entitis.dto.CategoryRequest;
import com.s3.stock.products.store_products.entitis.dto.CategoryResponse;

public interface ICategoryServices {
    CategoryResponse save(CategoryRequest category);
    void delete(CategoryRequest categoryDto) ;
    CategoryResponse findById(Long id);
    CategoryResponse findByName(String name);
    List<CategoryResponse> findAll();
    void deleteById(Long id);
    void deleteAll();
    
}
