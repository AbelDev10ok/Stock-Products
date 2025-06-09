package com.s3.stock.products.store_products.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.s3.stock.products.store_products.entitis.DetailSale;
import com.s3.stock.products.store_products.entitis.Product;
import com.s3.stock.products.store_products.entitis.Sale;
import com.s3.stock.products.store_products.entitis.dto.DetailsResponseDto;
import com.s3.stock.products.store_products.entitis.dto.SaleResponseDto;
import com.s3.stock.products.store_products.repositories.IDetailSaleRepository;
import com.s3.stock.products.store_products.repositories.ISaleRepository;
import com.s3.stock.products.store_products.services.interfaces.IInventoryMovementeServices;
import com.s3.stock.products.store_products.services.interfaces.IProductServices;
import com.s3.stock.products.store_products.services.interfaces.ISaleServices;

@Service
public class SaleServices implements ISaleServices{

    @Autowired
    private ISaleRepository saleRepository;

    @Autowired
    private IDetailSaleRepository detailSaleRepository;

    @Autowired
    private IInventoryMovementeServices inventoryMovementeServices;

    @Autowired
    private IProductServices productServices;

    @Override
    @Transactional
    public SaleResponseDto createSale(Sale venta) {
        // 1. Validar y actualizar el stock de los productos
        for (DetailSale detalle : venta.getDetails()) {
            Product producto = detalle.getProduct();
            int cantidadVendida = detalle.getQuantity();
    
            // Verificar si hay suficiente stock
            int stockActual = inventoryMovementeServices.getCurrentStock(producto.getId());
            if (stockActual < cantidadVendida) {
                throw new RuntimeException("No hay suficiente stock para el producto: " + producto.getName());
            }
    
            // Registrar la salida de inventario
            inventoryMovementeServices.addInventoryExit(producto.getId(), cantidadVendida, "Venta a cliente");
        }
    
        // 2. Calcular el total de la venta
        double total = venta.getDetails().stream()
                .mapToDouble(detalle -> detalle.getQuantity() * detalle.getPriceUnit())
                .sum();
        venta.setTotal(total);
    
        // 3. Guardar la venta y los detalles
        venta.setFecha(LocalDateTime.now());
        Sale ventaGuardada = saleRepository.save(venta);
    
        for (DetailSale detalle : venta.getDetails()) {
            detalle.setSale(ventaGuardada);
            Product producto = productServices.getProductfindById(detalle.getProduct().getId());
            detalle.setProduct(producto);
            detailSaleRepository.save(detalle);
        }
    
        // 4. Obtener la venta con las relaciones cargadas
        Sale ventaCompleta = saleRepository.findByIdWithDetails(ventaGuardada.getId())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        
    
        // 5. Mapear la venta guardada al DTO

        return mapToSaleResponseDTO(ventaCompleta);
    }
    @Override
    public List<SaleResponseDto> getSales() {
        return saleRepository.findAll().stream()
                .map(this::mapToSaleResponseDTO)
                .collect(Collectors.toList());
    }

    private SaleResponseDto mapToSaleResponseDTO(Sale venta) {
    
        List<DetailsResponseDto> detallesDTO = venta.getDetails().stream()
                .map(detalle -> {
                    // Verifica que `product` no sea null
                    if (detalle.getProduct() == null) {
                        throw new RuntimeException("El producto no está cargado en el detalle de la venta");
                    }
                    System.out.println("Producto en detalle: " + detalle.getProduct().getName());
                    System.out.println("Producto en priceUnit: " + detalle.getPriceUnit());
                    System.out.println("Producto en quantity: " + detalle.getQuantity());
                    return new DetailsResponseDto(
                            detalle.getProduct().getName(),
                            detalle.getQuantity(),
                            detalle.getPriceUnit()
                    );
                })
                .collect(Collectors.toList());  // Operación terminal para procesar el Stream
    
        SaleResponseDto saleResponseDto = new SaleResponseDto();
        saleResponseDto.setDetalles(detallesDTO);
        saleResponseDto.setFecha(venta.getFecha());
        saleResponseDto.setId(venta.getId());
        saleResponseDto.setSellerName(venta.getSeller().getEmail());
        saleResponseDto.setTotal(venta.getTotal());
        return saleResponseDto;
    }

}
