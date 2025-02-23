package com.s3.stock.products.store_products.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.s3.stock.products.store_products.entitis.Atribut;
import com.s3.stock.products.store_products.entitis.dto.AttributeRequest;
import com.s3.stock.products.store_products.repositories.IAttributRepository;
import com.s3.stock.products.store_products.repositories.ICategoryRepository;


@Service
public class AttributeServices implements IAttributeServices {

    @Autowired
    private IAttributRepository attributRepository;

    @Autowired
    private ICategoryRepository categoryRepository;

    @Override
    public void save(AttributeRequest attribute) {
        Atribut atribut = new Atribut();
        atribut.setName(attribute.getName());
        atribut.getCategorys().add(categoryRepository.findById(attribute.getCategoryId()).get());
        attributRepository.save(atribut);
    }

    @Override
    public void delete(AttributeRequest attributeDto) {
        // TODO Auto-generated method stub
    }

    @Override
    public List<Atribut> findAll() {
        return attributRepository.findAll();
    }

    

}
