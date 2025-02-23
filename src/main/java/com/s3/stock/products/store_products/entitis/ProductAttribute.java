package com.s3.stock.products.store_products.entitis;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


// clase intermediaria entre producto y atributo
@Entity
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "value_attribute_id")
    private ValueAttribut valueAttribute;

    private String value;

    public ProductAttribute() {
    }

    public ProductAttribute(Product product, ValueAttribut valueAttribute, String value) {
        this.product = product;
        this.valueAttribute = valueAttribute;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ValueAttribut getValueAttribute() {
        return valueAttribute;
    }

    public void setValueAttribute(ValueAttribut valueAttribute) {
        this.valueAttribute = valueAttribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    
    

}
