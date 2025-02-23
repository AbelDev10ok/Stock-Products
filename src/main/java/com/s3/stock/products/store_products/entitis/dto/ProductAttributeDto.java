package com.s3.stock.products.store_products.entitis.dto;

public class ProductAttributeDto {

    private String attributeName;
    private String value;

    public ProductAttributeDto() {
    }

    public ProductAttributeDto(String attributeName, String value) {
        this.attributeName = attributeName;
        this.value = value;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    
}
