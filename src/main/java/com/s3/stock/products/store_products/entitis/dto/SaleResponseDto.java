package com.s3.stock.products.store_products.entitis.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SaleResponseDto {
    private Long id;
    private LocalDateTime fecha;
    private List<DetailsResponseDto> detalles;
    private String sellerName;
    private Double total;



    public SaleResponseDto() {
    }   

    public SaleResponseDto(Long id, LocalDateTime fecha, List<DetailsResponseDto> detalles, String sellerName, Double total) {
        this.id = id;
        this.fecha = fecha;
        this.detalles = detalles;
        this.sellerName = sellerName;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public List<DetailsResponseDto> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetailsResponseDto> detalles) {
        this.detalles = detalles;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
    
}
