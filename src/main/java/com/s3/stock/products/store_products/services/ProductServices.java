package com.s3.stock.products.store_products.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public Product save(ProductRequest productDto) {
    // Check if a product with the same name already exists
    Product existingProduct = productRepository.findByName(productDto.getName()).orElse(null);

    // Convert category string to Category object
    // Podira hacer que si la categoria no existe, se cree una nueva, pero no se si es necesario ya que la creo desde la base de datos.
    Category category = categoryRepository.findByName(productDto.getCategory())
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

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
    
    @Override
    public Product findById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            throw new EntityNotFoundException("No se pudo encontrar el producto");
        }
        return product.get();
    }

    @Override
    public Product findByName(String name) {
        Optional<Product> product = productRepository.findByName(name);
        if (!product.isPresent()) {
            throw new EntityNotFoundException("No se pudo encontrar el producto");
        }
        return product.get();
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public void increaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    @Override
    public List<Product> findByCategoryName(String name) {
        return productRepository.findByCategoryName(name);
    }


    @Override
    public List<Product> findByNameContaining(String name) {
        return productRepository.findByNameContaining(name);
    }

    @Override
    public List<Product> findByStockGreaterThanEqual(int stock) {
        return productRepository.findByStockGreaterThanEqual(stock);
    }

}
