package com.s3.stock.products.store_products.services.interfaces;

import java.util.List;

import com.s3.stock.products.store_products.entitis.Sale;
import com.s3.stock.products.store_products.entitis.dto.SaleResponseDto;

public interface ISaleServices {
    SaleResponseDto createSale(Sale venta);
    List<SaleResponseDto> getSales();
}
