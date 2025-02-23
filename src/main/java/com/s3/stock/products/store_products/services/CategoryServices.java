package com.s3.stock.products.store_products.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.action.internal.EntityAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.s3.stock.products.store_products.entitis.Category;
import com.s3.stock.products.store_products.entitis.Product;
import com.s3.stock.products.store_products.entitis.dto.CategoryRequest;
import com.s3.stock.products.store_products.entitis.dto.CategoryResponse;
import com.s3.stock.products.store_products.entitis.dto.ProductAttributeDto;
import com.s3.stock.products.store_products.entitis.dto.ProductDto;
import com.s3.stock.products.store_products.repositories.ICategoryRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryServices implements ICategoryServices {

    @Autowired
    private ICategoryRepository categoryRepository;


    @Override
    public void delete(CategoryRequest categoryDto) {
        Optional<Category> category = categoryRepository.findByName(categoryDto.getName());
        if(category.isPresent()){
            categoryRepository.delete(category.get());
        }else{
            throw new EntityNotFoundException("Category not exist");
        }
    }

    @Override
    public void deleteAll() {
        categoryRepository.deleteAll();
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Category not exist"));
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream().map(category->convertToCategoryDto(category)).toList();
    }

    @Override
    public CategoryResponse findById(Long id) {
        categoryRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Category not exist"));

        return convertToCategoryDto(categoryRepository.findById(id).orElse(null));
    }

    @Override
    public CategoryResponse findByName(String name) {
        categoryRepository.findByName(name).orElseThrow(()-> new EntityNotFoundException("Category not exist"));

        return convertToCategoryDto(categoryRepository.findByName(name).orElse(null));
    }

    @Override
    public CategoryResponse save(CategoryRequest categoryDto) {
        categoryRepository.findByName(categoryDto.getName()).orElseThrow(()-> new EntityNotFoundException("Category not exist"));

        Category category = new Category();
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
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

    private ProductDto converToProductDto(Product product) {
        ProductDto productDto = new ProductDto();
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
