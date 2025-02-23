package com.s3.stock.products.store_products.entitis.dto;

public class AttributeRequest {
    private String name;
    private Long categoryId;

    public AttributeRequest() {
    }

    public AttributeRequest(String name, Long categoryId) {
        this.name = name;
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    

}
