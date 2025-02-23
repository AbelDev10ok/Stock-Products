package com.s3.stock.products.store_products.entitis.dto;

import java.util.List;

public class CategoryResponse {
    private Long id;
    private String name;
    private List<ProductDto> products;
    List<CategoryRequest> subCategories;

    public CategoryResponse() {
    }

    public CategoryResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }

    public List<CategoryRequest> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<CategoryRequest> subCategories) {
        this.subCategories = subCategories;
    }
    
}
