package com.s3.stock.products.store_products.entitis.dto;

public class DetailsResponseDto {
    private String productName;
    private int quantity;
    private double priceUnit;

    public DetailsResponseDto() {
    }   


    public DetailsResponseDto(String productName, int quantity, double priceUnit) {
        this.productName = productName;
        this.quantity = quantity;
        this.priceUnit = priceUnit;
    }


    public String getProductName() {
        return productName;
    }


    public void setProductName(String productName) {
        this.productName = productName;
    }


    public int getQuantity() {
        return quantity;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public double getPriceUnit() {
        return priceUnit;
    }


    public void setPriceUnit(double priceUnit) {
        this.priceUnit = priceUnit;
    }

    
}
