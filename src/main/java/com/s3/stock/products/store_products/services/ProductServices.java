package com.s3.stock.products.store_products.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.s3.stock.products.store_products.entitis.Category;
import com.s3.stock.products.store_products.entitis.Product;
import com.s3.stock.products.store_products.entitis.dto.ProductRequest;
import com.s3.stock.products.store_products.repositories.ICategoryRepository;
import com.s3.stock.products.store_products.repositories.IProductRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductServices implements IProductServices{

    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private ICategoryRepository categoryRepository;


    @Transactional
    @Override
    public Product save(ProductRequest productDto) {
    Category category = categoryRepository.findByName(productDto.getCategory())
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    

    Product existingProduct = productRepository.findByName(productDto.getName()).orElse(null);

    // Podira hacer que si la categoria no existe, se cree una nueva, pero no se si es necesario ya que la creo desde la base de datos.

    Product product = new Product();
    product.setName(productDto.getName());
    product.setDescription(productDto.getDescription());
    product.setPrice(productDto.getPrice());
    product.setStock(productDto.getStock());
    product.setCategory(category);

        if (existingProduct != null) {
            // Update existing product
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setStock(product.getStock());
            existingProduct.setCategory(category);
            // Update other fields as needed
            return productRepository.save(existingProduct);
        } else {
            // Save new product
            return productRepository.save(product);
        }
    }
    
    @Transactional
    @Override
    public void delete(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            throw new EntityNotFoundException("No se pudo encontrar el producto");
        }
        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo eliminar el producto");
        }
        
    }
    
    @Transactional(readOnly = true)
    @Override
    public ProductRequest findById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            throw new EntityNotFoundException("No se pudo encontrar el producto");
        }
        return convertToDto(product.get());
    }

    @Transactional(readOnly = true)
    @Override
    public ProductRequest findByName(String name) {
        Optional<Product> product = productRepository.findByName(name);
        if (!product.isPresent()) {
            throw new EntityNotFoundException("No se pudo encontrar el producto");
        }
        return convertToDto(product.get());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductRequest> findAll() {
        return productRepository.findAll()
            .stream()
            .map(this::convertToDto)
            .toList();
    }

    @Transactional
    @Override
    public void increaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductRequest> findByCategory(String name) {
        return productRepository.findByCategory_Name(name).stream().map( product -> {
            return convertToDto(product);
        }).toList();
    }


    @Transactional(readOnly = true)
    @Override
    public List<ProductRequest> findByNameContaining(String name) {
        return productRepository.findByNameContaining(name).stream().map(product -> {
            return convertToDto(product);
        }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductRequest> findByStockGreaterThanEqual(int stock) {
        return productRepository.findByStockGreaterThanEqual(stock).stream().map(product ->convertToDto(product)).toList();
    }

    private ProductRequest convertToDto(Product product) {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName(product.getName());
        productRequest.setDescription(product.getDescription());
        productRequest.setPrice(product.getPrice());
        productRequest.setCategory(product.getCategory().getName());
        productRequest.setStock(product.getStock());
        productRequest.setId(product.getId());
        return productRequest;
    }

}
