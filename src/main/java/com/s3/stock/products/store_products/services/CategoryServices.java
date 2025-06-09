package com.s3.stock.products.store_products.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.action.internal.EntityAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.s3.stock.products.store_products.entitis.Category;
import com.s3.stock.products.store_products.entitis.Product;
import com.s3.stock.products.store_products.entitis.dto.CategoryRequest;
import com.s3.stock.products.store_products.entitis.dto.CategoryResponse;
import com.s3.stock.products.store_products.entitis.dto.ProductAttributeDto;
import com.s3.stock.products.store_products.entitis.dto.ProductResponseDto;
import com.s3.stock.products.store_products.repositories.ICategoryRepository;
import com.s3.stock.products.store_products.services.interfaces.ICategoryServices;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryServices implements ICategoryServices {

    @Autowired
    private ICategoryRepository categoryRepository;


        
    @Transactional
    @Override
    public CategoryResponse save(CategoryRequest categoryDto) {
        Optional<Category> category = categoryRepository.findByName(categoryDto.getName());

        if(category.isPresent()){
            throw new EntityNotFoundException("Category already exist");
        }
        Category newCategory = new Category();
        newCategory.setName(categoryDto.getName());
        categoryRepository.save(newCategory);
        return convertToCategoryDto(newCategory); 
    }

    @Transactional
    @Override
    public void delete(CategoryRequest categoryDto) {
        Optional<Category> category = categoryRepository.findByName(categoryDto.getName());
        if(category.isPresent()){
            categoryRepository.delete(category.get());
        }else{
            throw new EntityNotFoundException("Category not exist");
        }
    }
    
    @Transactional
    @Override
    public void deleteAll() {
        categoryRepository.deleteAll();
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        categoryRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Category not exist"));
        categoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream().map(category->convertToCategoryDto(category)).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Category not exist"));

        return convertToCategoryDto(category);
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryResponse findByName(String name) {
        Category category = categoryRepository.findByName(name).orElseThrow(()-> new EntityNotFoundException("Category not exist"));

        return convertToCategoryDto(category);
    }

    private CategoryResponse convertToCategoryDto(Category category) {
        if (category == null) {
            return null;
        }
        CategoryResponse categoryDto = new CategoryResponse();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setProducts(category.getProducts().stream().map((p)->converToProductDto(p)).toList());
        categoryDto.setSubCategories(category.getSubCategories().stream().map((c)->{
            CategoryRequest categoryRequest = new CategoryRequest();
            categoryRequest.setName(c.getName());
            return categoryRequest;
        }).toList());
        return categoryDto;
    }

    private ProductResponseDto converToProductDto(Product product) {
        ProductResponseDto productDto = new ProductResponseDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());
        productDto.setCategory(product.getCategory().getName());
        productDto.setStock(product.getStock());
        productDto.setBrand(product.getBrand());
        productDto.setModel(product.getModel());
        productDto.setDescription(product.getDescription());
        productDto.setSku(product.getSku());

        List<ProductAttributeDto> productAttributeDtos = product.getProductAttributes().stream()
            .map(attribute -> new ProductAttributeDto(attribute.getValueAttribute().getValue() , attribute.getValueAttribute().getAtribut().getName()))
            .collect(Collectors.toList());
        productDto.setProductAttributes(productAttributeDtos);
        return productDto;
    }
    

}
